package com.clicktable.service.impl;

import com.clicktable.dao.intf.*;
import com.clicktable.model.*;
import com.clicktable.model.Queue;
import com.clicktable.response.*;
import com.clicktable.service.intf.*;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.*;
import com.csvreader.CsvWriter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/*import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;*/
//import com.pubnub.api.*;

//import org.json.*;

@Component
public class ReservationServiceImpl implements ReservationService {

	@Autowired
	ReservationDao reservationDao;

	@Autowired
	RestaurantDao restaurantDao;

	@Autowired
	CustomerDao guestDao;

	

	@Autowired
	SectionDao sectionDao;

	@Autowired
	ReservationValidator reservationValidator;

	@Autowired
	CustomerValidator guestValidator;

	@Autowired
	TableValidator tableValidator;

	@Autowired
	RestaurantValidator restValidator;
	@Autowired
	ConversationValidator conversationValidator;

	@Autowired
	GuestReservationRelationDao guestRelationDao;

	@Autowired
	TableReservationRelationDao tableRelationDao;

	@Autowired
	AuthorizationService authService;

	@Autowired
	CalenderEventDao calEventDao;
	
	@Autowired
	BarEntryService barEntryService;
	
	@Autowired
	BarEntryDao barEntryDao;

	//@Autowired
	//TagServiceDao tagDao;
	/*@Autowired
	GuestHasTagsDao assignTagDao;*/
	
	@Autowired
	GuestTagDao assignTagDao;

	@Autowired
	HistoricalTatDao tatDao;

	@Autowired
	TableShuffleService shuffleService;

	@Autowired
	QueueDao queueDao;

	@Autowired
	CurrentValuesDao currentValuesDao;

	/*@Autowired
	GuestHasTagsService assignTagService;*/
	
	@Autowired
	GuestTagsService assignTagService;

	@Autowired
	TableDao tableDao;
	
	@Autowired
	WaitlistService waitlistService;
	
	@Autowired
	TableShuffleDao shuffleDao;
	
	@Autowired
	BarEntryValidator barEntryValidator;
	

	@Autowired
	ConversationService conversationService;

	private Map<String,Lock> patchLockMap = new ConcurrentHashMap<>();
	private static Logger.ALogger log = Logger.of(ReservationServiceImpl.class);

	@Override
	@Transactional
	public BaseResponse createReservation(Reservation reservation, String token, Boolean sendSms) {

		Lock lock = getLock(reservation.getRestaurantGuid());
		lock.lock();

		try
		{
			
		/*	System.out.println("process starts");
			try{
				Thread.sleep(5*1000L);
			}catch(Exception e){
				
			}
			System.out.println("process ends");*/
			
			
			BaseResponse response;
			List<Table> tableList = new ArrayList<Table>();
			reservation.setReservationStatus("CREATED");
			GuestProfile guest = null;
			Restaurant rest = null;
			String guestName = null;
			String restName = null;
			String restLocality = null;
			String restRegion = null;
			String guestMobile = null;
			String restCity = null;

			UserInfoModel userInfo = authService.getUserInfoByToken(token);
			reservation.setShortId(getReservationShortId(userInfo));

			List<ValidationError> listOfError = new ArrayList<>();

			if (!(reservation.getRestaurantGuid().equals(userInfo.getRestGuid()))){
				listOfError.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.REST_FOR_STAFF_NOT_VALID), ErrorCodes.REST_FOR_STAFF_NOT_VALID));
			}

			/* Add Dummy Guest In case of Unknow Guest */

			if (reservation.getIsUnknown() && reservation.getBookingMode().equalsIgnoreCase(Constants.WALKIN_STATUS)) {
				guest = guestValidator.getDummyGuest();
				if (null == guest) {
					listOfError.add(new ValidationError(Constants.DUMMY_GUEST_ID, UtilityMethods.getErrorMsg(ErrorCodes.DUMMY_GUEST_ID_NOT_FOUND), ErrorCodes.DUMMY_GUEST_ID_NOT_FOUND));
				} else {
					reservation.setGuestGuid(guest.getGuid());

				}
			}

			if(!listOfError.isEmpty()){
				return new ErrorResponse(ResponseCodes.RESERVATION_ADDED_FAILURE, listOfError);
			}

			if(reservation.getEstStartTime().getTime() == new Date(0).getTime())
			{
				if(reservation.getTableGuid() == null || reservation.getTableGuid().size() == 1){
					reservation.setTableGuid(new ArrayList<>());
				}
				reservation.setQueued(true);
				Reservation resv = reservationDao.addReservation(reservation, guest, tableList);
				Boolean addedToQueue = addToQueue(reservation);
				Reservation [] resvArr = new Reservation[1];
				resvArr[0] = resv;
				response = new PostResponse<Reservation>(ResponseCodes.RESERVATION_ADDED_SUCCESFULLY, resvArr);
				return response;
			}

			listOfError = reservationValidator.validateReservationOnCreate(reservation, Constants.ADD);

			/* Validating Guest | Table | Restaurant | Covers */
			Map<String, Object> map = reservationValidator.validateRestGuestTable(reservation, listOfError);

			if(!listOfError.isEmpty()){
				return new ErrorResponse(ResponseCodes.RESERVATION_ADDED_FAILURE, listOfError);
			}

			if (null != map){
				guestName = map.get(Constants.NAME).toString();
				restName = map.get(Constants.RESTAURANT_NAME) != null ? map.get(Constants.RESTAURANT_NAME).toString() : null;
				restLocality = map.get(Constants.LOCALITY) != null ? map.get(Constants.LOCALITY).toString() : null;
				restCity = map.get(Constants.CITY) != null ? map.get(Constants.CITY).toString() : null;
				restRegion = map.get( Constants.REGION) != null ? map.get( Constants.REGION).toString() : null;
				guestMobile = map.get(Constants.MOBILE) != null ? map.get(Constants.MOBILE).toString() : null;
			}

			reservationValidator.validateTat(listOfError, reservation);
			if(!listOfError.isEmpty()){
				return new ErrorResponse(ResponseCodes.RESERVATION_ADDED_FAILURE, listOfError);
			}

			Boolean isValidTable = shuffleService.shuffleTablesMethod(reservation, null);

			if(!isValidTable)
				reservationValidator.validateReservationTimeSlot( reservation, listOfError);

			if (!listOfError.isEmpty()) {
				return new ErrorResponse(ResponseCodes.RESERVATION_ADDED_FAILURE, listOfError);
			}


			//String reservation_guid = reservationDao.addReservation(reservation, guest, tableList);

			Reservation resv = reservationDao.addReservation(reservation, guest, tableList);
			Reservation [] resvArr = new Reservation[1];
			resvArr[0] = resv;

			if(reservation.getBookingMode().equals(Constants.WALKIN_STATUS))
			{
				Boolean addedToQueue = addToQueue(reservation);
			}

			/*Added this method to shuffle the waitlist if the reservation is created on the same table as walkin*/

			/*Map<String, Object> resvParams = new HashMap<>();
		resvParams.put(Constants.REST_ID, reservation.getRestaurantGuid());
		shuffleService.shuffleTables(resvParams, token);*/

			response = new PostResponse<Reservation>(ResponseCodes.RESERVATION_ADDED_SUCCESFULLY, resvArr);

			//in case of lite app sendSms comes as false from controller so not to end sms in that case

			if(sendSms == null || sendSms)
			{
				if (response.getResponseCode().equalsIgnoreCase("9002") && !reservation.getIsUnknown() && null != restName && reservation.getBookingMode().equalsIgnoreCase(Constants.ONLINE_STATUS)) {
					try {
						SimpleDateFormat dateformat = new SimpleDateFormat("EEE, d MMM yyyy");
						SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
						Object params[] = { guestName, restName + (null != restLocality ? ", " + restLocality : "") + (null != restRegion ? ", " + restRegion : ""), reservation.getNumCovers(),
								dateformat.format(new Date(reservation.getEstStartTime().getTime())), timeformat.format(new Date(reservation.getEstStartTime().getTime())) };
						String sms_message = UtilityMethods.sendSMSFormat(params, Constants.SMS_CREATED_MSG);
						// Update Guest Conversation
						GuestConversation conversation = new GuestConversation(reservation, sms_message);
						conversation.setGuid(UtilityMethods.generateCtId());
						conversation.setCreatedBy(reservation.getCreatedBy());
						conversation.setUpdatedBy(reservation.getUpdatedBy());
						conversationService.addConversationAndMsg(conversation, false);

					} catch (Exception e) {
						log.warn("Exception in service", e);
					}
				} else if (response.getResponseCode().equalsIgnoreCase("9002") && !reservation.getIsUnknown() && null != restName && reservation.getBookingMode().equalsIgnoreCase(Constants.WALKIN_STATUS)) {
					final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
					for (StackTraceElement element : stackTrace) {
						try {
							if (element.getMethodName().equalsIgnoreCase(Constants.WAITLIST_METHOD) && element.getClassName().equalsIgnoreCase(Constants.WAITLIST_PCKG)) {
								SimpleDateFormat dateformat = new SimpleDateFormat("EEE, d MMM yyyy");
								SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
								SimpleDateFormat minFormat = new SimpleDateFormat("mm");
								int waitTime = (int) ((reservation.getEstStartTime().getTime() - Calendar.getInstance().getTimeInMillis()) / (60 * 1000));
								if (waitTime < 0) {
									waitTime = 0;
								}


								Object params[] = { guestName, restName + (null != restLocality ? ", " + restLocality : "") + (null != restRegion ? ", " + restRegion : ""), reservation.getNumCovers(),
										dateformat.format(new Date(Calendar.getInstance().getTimeInMillis())), timeformat.format(new Date(Calendar.getInstance().getTimeInMillis())), waitTime };


								String sms_message = UtilityMethods.sendSMSFormat(params, Constants.SMS_WAITLIST_MSG);
								// Update Guest Conversation
								GuestConversation conversation = new GuestConversation(reservation, sms_message);
								conversation.setGuid(UtilityMethods.generateCtId());
								conversation.setCreatedBy(reservation.getCreatedBy());
								conversation.setUpdatedBy(reservation.getUpdatedBy());
								conversationService.addConversationAndMsg(conversation, false);
							}
						} catch (Exception e) {
							log.warn("Exception in service", e);
						}

					}
					// com.clicktable.controllers.WaitlistController addToWaitlist
				}
			}
			/*Pubnub pubnub = new Pubnub( 
				"pub-c-273527c7-f738-45f4-9eed-8f7abc3c721b", "sub-c-37aecff6-69da-11e5-b6e2-0619f8945a4f");*/

			/*	Pubnub pubnub = new Pubnub( 
				"pub-c-ef56ac61-d434-4b74-9bfa-e276793230e0", "sub-c-4e567498-ef63-11e5-ab43-02ee2ddab7fe");



		Callback callback = new Callback() {
			  public void successCallback(String channel, Object response) {
			    System.out.println(response.toString());
			  }
			  public void errorCallback(String channel, PubnubError error) {
			    System.out.println(error.toString());
			  }
			};

			JsonNode result = Json.toJson(reservation);

			 ObjectMapper mapper = new ObjectMapper();
	         JSONObject resvObject = null;
	         try {
				resvObject=new JSONObject(mapper.writeValueAsString(reservation));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				log.warn("Exception in service", e);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				log.warn("Exception in service", e);
			}
			pubnub.publish("my_channel", resvObject , callback);*/

			return response;
		}
		finally
		{

			lock.unlock();
			//patchLockMap.remove(reservation.getRestaurantGuid());
			/*System.out.println("map size new :" + patchLockMap.size());*/
		}

	}

	
	@Override
	@Transactional(readOnly = true)
	public BaseResponse getReservation(Map<String, Object> params, String token) {
		BaseResponse getResponse = null;
		List<Reservation> reservations = null;
		boolean showAllDay = false;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
			if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
				params.put(Constants.GUEST_GUID, userInfo.getGuid());
			} else {
				params.put(Constants.REST_GUID, userInfo.getRestGuid());
			}
		}

		/*
		 * Validating Restaurant GUID in case of CT_ADMIN_ROLE_ID Or
		 * CUSTOMER_ROLE_ID
		 */

		if (!params.containsKey(Constants.REST_GUID)) {

			listOfError.add(reservationValidator.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
			return new ErrorResponse(ResponseCodes.RESERVATION_RECORD_FETCH_FAILURE, listOfError);
		}
		if (params.containsKey(Constants.SHOW_ALL_DAY)) {
			reservationValidator.showAlldayReservation(params);
			showAllDay = true;
		}
		Map<String, Object> qryParamMap = reservationValidator.validateFinderParams(params, Reservation.class);

		Logger.debug("query param map is ---------------------------------------" + qryParamMap);
		
		
		/*fetch applicable current shift*/
		Map<String, Object> shiftTime = waitlistService.getApplicableShifts( params.get(Constants.REST_GUID).toString(), listOfError,new Date(),false);

		if(!listOfError.isEmpty())
		{
			getResponse = new ErrorResponse(ResponseCodes.RESERVATION_RECORD_FETCH_FAILURE, listOfError);
			return getResponse;
		}


		Long currentShiftStart = (Long) shiftTime.get("currentDayShiftStartTime");
		Long currentShiftEnd = (Long) shiftTime.get("currentDayShiftEndTime");
	
		if (showAllDay) {
			
			qryParamMap.put("currentShiftEnd", currentShiftEnd);
			qryParamMap.put("currentShiftStart", currentShiftStart);
			reservations = reservationDao.findByCustomeFields(Reservation.class, qryParamMap);
			
			/*System.out.println(params);*/
			
			getResponse = new GetResponse<Reservation>(ResponseCodes.RESERVATION_RECORD_FETCH_SUCCESFULLY, reservations);
		} else {
			reservations = reservationDao.findByFields(Reservation.class, qryParamMap);
			/*System.out.println(params+"<<<<<<<<<<");*/
			getResponse = new GetResponse<Reservation>(ResponseCodes.RESERVATION_RECORD_FETCH_SUCCESFULLY, reservations);
		}
		return getResponse;
	}


	
	


	private Lock createLock(){
		return new ReentrantLock();
	}
	
	private Lock getLock(String id)
	{
		synchronized (patchLockMap) {
			Lock lock = patchLockMap.get(id);
			if(lock == null)
			{
				lock = createLock();
				patchLockMap.put(id, lock);
			}
			
			return lock;
		}
	}
	
	
	@Override
	//@Transactional
	public BaseResponse patchReservation(Reservation reservation, String token) {

		
		Lock lock = getLock(reservation.getGuid());
		lock.lock();

		try
		{
			

			List<ValidationError> listOfError = new ArrayList<ValidationError>();
			BaseResponse response;
			String updatereservation_guid = null;
			SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);



			Long newDateTime = new Date().getTime();
			try {
				newDateTime = sdf.parse(sdf.format(new Date())).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				log.warn("Exception in service", e);
			}

			/* Validating Reservation GUID */
			Reservation existing = reservationValidator.validateGuid(reservation.getGuid(), listOfError);

			



			/*reservation with these satuses can't be patchedd */
			if(!listOfError.isEmpty()){
				response = new ErrorResponse(ResponseCodes.RESERVATION_UPDATED_FAILURE, listOfError);
				return response;
			}

			if (existing.getReservationStatus().equalsIgnoreCase(Constants.FINISHED) || existing.getReservationStatus().equalsIgnoreCase(Constants.CANCELLED)
					|| existing.getReservationStatus().equalsIgnoreCase(Constants.NO_SHOW_STATUS)) {
				listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.NO_ALLOW_TO_UPDATE_THIS_RESERVATION), ErrorCodes.NO_ALLOW_TO_UPDATE_THIS_RESERVATION));
			}

			if(!listOfError.isEmpty()){
				response = new ErrorResponse(ResponseCodes.RESERVATION_UPDATED_FAILURE, listOfError);
				return response;
			}

			/*code to manage reservation status changes*/
			UserInfoModel userInfo = authService.getUserInfoByToken(token);
			Map<String, Object> params = new HashMap<>();
			params.put(Constants.REST_ID, reservation.getRestaurantGuid());

			boolean validateTimeSlot = false;
			boolean editReservation = false;
			if(reservation.getTat() != null)
			{
				existing.setTat(reservation.getTat());
				validateTimeSlot = true;
				editReservation = true;
			}

			if(reservation.getEstStartTime() != null)
			{
				existing.setEstStartTime(reservation.getEstStartTime());
				existing.setEstEndTime(new Date(reservation.getEstStartTime().getTime() + Long.valueOf(existing.getTat())*60*1000));
				validateTimeSlot = true;
				editReservation = true;
			}

			if(reservation.getEstEndTime() != null)
			{
				existing.setEstEndTime(reservation.getEstEndTime());
				validateTimeSlot = true;
				editReservation = true;
			}

			if(reservation.getNumCovers() != null)
			{
				existing.setNumCovers(reservation.getNumCovers());
				validateTimeSlot = true;
				editReservation = true;
			}
			if (reservation.getTableGuid() != null && reservation.getTableGuid().size() > 0){
				existing.setTableGuid(reservation.getTableGuid());
				validateTimeSlot = true;
				if(!existing.getReservationStatus().equals(Constants.SEATED) && reservation.getReservationStatus() != null && reservation.getReservationStatus().equals(Constants.SEATED)){
					validateTimeSlot = false;
				}
			}

			if(reservation.getSource() != null)
			{
				existing.setSource(reservation.getSource());
			}


			if(reservation.getOfferId() != null && !reservation.getOfferId().equals(""))
			{
				listOfError = reservationValidator.validateOfferId(reservation, listOfError);
				existing.setOfferId(reservation.getOfferId());
				existing.setOfferName(reservation.getOfferName());
			}
			else if(reservation.getOfferId() != null && reservation.getOfferId().equals(""))
			{
				existing.setOfferId(null);
				existing.setOfferName(null);
			}


			Boolean isValidTable = false;
			if(validateTimeSlot){
				//isValidTable = reservationValidator.validateReservationTimeSlot( existing, listOfError);
				Reservation existResv = new Reservation();
				try {
					existResv = (Reservation) existing.clone();
				} catch (CloneNotSupportedException e) {
					log.warn("Exception in service", e);
				}

				String reservationStatus = (existing.getReservationStatus().equals(Constants.SEATED)) ? existing.getReservationStatus() : null;
				isValidTable = shuffleService.shuffleTablesMethod(existResv, reservationStatus);

				BaseResponse resvTableResponse = new BaseResponse();
				if(!isValidTable){
					resvTableResponse  = getReservationsForTables(existing);
				}

				if (resvTableResponse instanceof GetResponse) {
					List resultList = ((GetResponse) resvTableResponse).getList();
					if (resultList.size() > 0) {
						response = new ErrorResponse(ResponseCodes.RESERVATION_UPDATION_FAILURE_DUE_TO_SEATED, resultList);
						return response;

					}
				}

			}

			if (!listOfError.isEmpty() || (!isValidTable && validateTimeSlot)) {
				return new ErrorResponse(ResponseCodes.RESERVATION_UPDATED_FAILURE, listOfError);
			}

			String existingReservationStatus = existing.getReservationStatus();
			if (null != reservation.getReservationStatus() && !existingReservationStatus.equalsIgnoreCase(reservation.getReservationStatus())) {
				String reservationStatus = reservation.getReservationStatus();
				if (reservationStatus.equalsIgnoreCase(Constants.FINISHED)){
					existing.setActEndTime(new Date(newDateTime));
					existing.setReservationStatus(reservationStatus);
					
				
					//TODO :  check the error response from template.saveOnly
					updatereservation_guid = reservationDao.update(existing).getGuid();

					String reservation_guid_1 = reservationDao.updateReservation(existing);

					if ((reservation.getToBypass() == null) || (!reservation.getToBypass())) {
						Runnable runnableTask = () -> {
							shuffleService.shuffleTables(params, token);
						};
						new Thread(runnableTask).start();
					}
					sendSMStoCustomer(existing);
					response = new UpdateResponse<Reservation>(ResponseCodes.RESERVATION_UPDATED_SUCCESFULLY, updatereservation_guid);
					
					return response;

				}else if(reservationStatus.equalsIgnoreCase(Constants.CANCELLED)){
					existing.setCancelledBy(userInfo.getUserType());
					existing.setCancelledById(userInfo.getGuid());
					existing.setCancelTime(new Date(newDateTime));
					existing.setReservationStatus(reservationStatus);
					updatereservation_guid = reservationDao.update(existing).getGuid();
					if (existing.getBookingMode().equals(Constants.WALKIN_STATUS)) {
						removeResvFromQueue(existing);
					}

					String reservation_guid_1 = reservationDao.updateReservation(existing);

					if ((reservation.getToBypass() == null) || (!reservation.getToBypass())) {
						Runnable runnableTask = () -> {
							shuffleService.shuffleTables(params, token);
						};
						new Thread(runnableTask).start();
					}
					sendSMStoCustomer(existing);
					response = new UpdateResponse<Reservation>(ResponseCodes.RESERVATION_UPDATED_SUCCESFULLY, updatereservation_guid);
					return response;

				}else if (reservationStatus.equalsIgnoreCase(Constants.NO_SHOW_STATUS)){
					existing.setReservationStatus(reservationStatus);
					Reservation resv = reservationDao.update(existing);
					updatereservation_guid = resv.getGuid();

					if (existing.getBookingMode().equals(Constants.WALKIN_STATUS)) {
						removeResvFromQueue(existing);
					}

					String reservation_guid_1 = reservationDao.updateReservation(existing);

					if ((reservation.getToBypass() == null) || (!reservation.getToBypass())) {
						Runnable runnableTask = () -> {
							shuffleService.shuffleTables(params, token);
						};
						new Thread(runnableTask).start();
					}
					sendSMStoCustomer(existing);
					response = new UpdateResponse<Reservation>(ResponseCodes.RESERVATION_UPDATED_SUCCESFULLY, updatereservation_guid);
					return response;
				}else if(reservation.getReservationStatus().equalsIgnoreCase(Constants.SEATED) 
						&& !existing.getReservationStatus().equalsIgnoreCase(Constants.SEATED))
				{
					if ((existing.getBookingMode().equals(Constants.ONLINE_STATUS)) && (((Long) (existing.getEstStartTime().getTime() - new Date().getTime())) > 30 * 60 * 1000L)) {
						ValidationError error = new ValidationError(Constants.EST_START_TIME, UtilityMethods.getErrorMsg(ErrorCodes.RESERVATION_CANNOT_BE_SEATED_BEFORE_30_MIN),
								ErrorCodes.RESERVATION_CANNOT_BE_SEATED_BEFORE_30_MIN, "");
						listOfError.add(error);
						response = new ErrorResponse(ResponseCodes.RESERVATION_UPDATED_FAILURE, listOfError);
						return response;

					}

					if(reservation.getTableGuid().size() > 0){
						existing.setTableGuid(reservation.getTableGuid());
					}

					// Next 6 lines are added as during patch est start time of reservation get changed in shuffleTablesMethod
					Reservation existResv = new Reservation();
					try {
						existResv = (Reservation) existing.clone();
					} catch (CloneNotSupportedException e) {
						log.warn("Exception in service", e);
					}


					/**/
					Boolean isShufflePossible = shuffleService.shuffleTablesMethod(existResv, reservationStatus);

					BaseResponse resvTableResponse = new BaseResponse();
					if(!isShufflePossible){
						resvTableResponse = getReservationsForTables(existing);
					}

					if (resvTableResponse instanceof GetResponse) {
						List resultList = ((GetResponse) resvTableResponse).getList();
						if (resultList.size() > 0) {
							response = new ErrorResponse(ResponseCodes.RESERVATION_UPDATION_FAILURE_DUE_TO_SEATED, resultList);
							return response;

						}
					}

					// TODO : Method needs optimization
					if(!existing.getIsUnknown()){
						tagGuestWithOngoingEvent(existing, token);
					}
					if (existing.getBookingMode().equalsIgnoreCase(Constants.ONLINE_STATUS)) {
						existing.setActStartTime(new Date(newDateTime));

					} else if (existing.getBookingMode().equalsIgnoreCase(Constants.WALKIN_STATUS)) {
						if(reservation.getEstStartTime() == null) {
							existing.setEstStartTime(new Date(newDateTime));
						}else{
							existing.setEstStartTime(reservation.getEstStartTime());
						}
						if(reservation.getEstEndTime() == null) {
							existing.setEstEndTime(new Date(newDateTime + Long.parseLong(existing.getTat()) * 60 * 1000));
						}else{
							existing.setEstEndTime(reservation.getEstEndTime());
						}

						existing.setActStartTime(existing.getEstStartTime());
					}

					existing.setReservationStatus(reservationStatus);
					updatereservation_guid = reservationDao.update(existing).getGuid();

					if (existing.getBookingMode().equals(Constants.WALKIN_STATUS)) {
						removeResvFromQueue(existing);
					}

					String reservation_guid_1 = reservationDao.updateReservation(existing);

					Long seatedTime = guestDao.addFirstSeatedTime(existing.getRestaurantGuid(), existing.getGuestGuid(), newDateTime);
					Logger.debug("Guest first seated at =======================================================" + seatedTime);

					if ((reservation.getToBypass() == null) || (!reservation.getToBypass())) {
						Runnable runnableTask = () -> {
							shuffleService.shuffleTables(params, token);
						};
						new Thread(runnableTask).start();
					}

					response = new UpdateResponse<Reservation>(ResponseCodes.RESERVATION_UPDATED_SUCCESFULLY, updatereservation_guid);

					return response;

				}
				existing.setReservationStatus(reservationStatus);
			}

			if(reservation.getPreferredTable() != null)
			{
				existing.setPreferredTable(reservation.getPreferredTable());
			}

			if(reservation.getReservationNote() != null)
			{
				existing.setReservationNote(reservation.getReservationNote());
			}



			if(reservation.getBookingMode() != null && reservation.getBookingMode().equals(Constants.WALKIN_STATUS) 
					&& existing.getBookingMode().equals(Constants.ONLINE_STATUS)){
				
				
				
				if(existing.getReservationStatus().equals(Constants.SEATED))
				{
					List resvList = new ArrayList<>();
					resvList.add(existing);
					response = new ErrorResponse(ResponseCodes.RESERVATION_UPDATION_FAILURE_DUE_TO_SEATED, resvList);
					return response;
				}
				
				existing.setBookingMode(Constants.WALKIN_STATUS);
				existing.setCreatedDate(new Date());
				existing.setQueued(true);
				addToQueue(existing);
			}

			updatereservation_guid = reservationDao.update(existing).getGuid();

			String reservation_guid_1 = reservationDao.updateReservation(existing);

			/*Send SMS on dit reservation*/
			if(editReservation && existing.getBookingMode().equals(Constants.ONLINE_STATUS)){
				sendSMStoCustomer(existing);
			}

			if ((reservation.getToBypass() == null) || (!reservation.getToBypass())) {
				Runnable runnableTask = () -> {
					shuffleService.shuffleTables(params, token);
				};
				new Thread(runnableTask).start();
			}
			//sendSMStoCustomer(reservation);
			response = new UpdateResponse<Reservation>(ResponseCodes.RESERVATION_UPDATED_SUCCESFULLY, updatereservation_guid);
			
			
			
			return response;
		}
		finally
		{
			lock.unlock();
			patchLockMap.remove(reservation.getGuid());
			/*System.out.println("map size new :" + patchLockMap.size());*/
		}

	}
	
	
	public void removeResvFromQueue(Reservation resv)
	{
		Queue queue = queueDao.getQueueForReservation(resv);
		if (queue != null) {
			int count = queue.getCount();
			if (count > 0) {
				queue.setCount(count - 1);
				queueDao.updateAllProperties(queue);
			}
			queueDao.deleteQueueReservation(resv);

		}
	}
	
	public void sendSMStoCustomer(Reservation reservation){
		
		SimpleDateFormat dateformat = new SimpleDateFormat("EEE, d MMM yyyy");
		SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
		
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put(Constants.REST_GUID, reservation.getRestaurantGuid());
		paramMap.put(Constants.GUID, reservation.getGuestGuid());
		List<GuestProfile> guestList = guestDao.getRestaurantGuest(GuestProfile.class, paramMap);
		
		String guestName = "";
		if(guestList != null && guestList.size()>0)
		{
			guestName = guestList.get(0).getFirstName();
		}
			
			
		Restaurant restaurant = restaurantDao.find(reservation.getRestaurantGuid());
		Object sms_params[] = {
				guestName,
				restaurant.getName() + (null != restaurant.getLocality() ? ", " + restaurant.getLocality() : "")
						+ (null != restaurant.getRegion() ? ", " + restaurant.getRegion() : ""), reservation.getNumCovers(),new Object(),new Object()
						};
		String sms_message = UtilityMethods.sendSMSFormat(sms_params, Constants.SMS_RESERVATION_RELEASED);
		sms_params[3] = "";
		sms_params[4] = "";
		
		if(reservation.getReservationStatus().equalsIgnoreCase(Constants.CANCELLED)){
			sms_params[3] = dateformat.format(reservation.getCancelTime().getTime());
			//sms_params[4] = timeformat.format(reservation.getCancelTime().getTime()); As per Varun Handa , Cancelled Time should  not be used in SMS message but estStartTime of reservation would be used 
			sms_params[4] = timeformat.format(reservation.getEstStartTime().getTime());
			sms_message = UtilityMethods.sendSMSFormat(sms_params, Constants.SMS_RESERVATION_CANCELLED);
		}else if(reservation.getReservationStatus().equalsIgnoreCase(Constants.NO_SHOW_STATUS))
		{
			sms_params[3] = dateformat.format(Calendar.getInstance().getTime());
			sms_params[4] = timeformat.format(Calendar.getInstance().getTime());
			sms_message = UtilityMethods.sendSMSFormat(sms_params, Constants.SMS_RESERVATION_NO_SHOW);
		}else if(reservation.getReservationStatus().equalsIgnoreCase(Constants.FINISHED))
		{
			sms_params[3] = dateformat.format(Calendar.getInstance().getTime());
			sms_params[4] = timeformat.format(Calendar.getInstance().getTime());
			sms_message = UtilityMethods.sendSMSFormat(sms_params, Constants.SMS_RESERVATION_RELEASED);
		}else{
			sms_params[3] = dateformat.format(new Date(reservation.getEstStartTime().getTime()));
			sms_params[4] = timeformat.format(new Date(reservation.getEstStartTime().getTime()));
			sms_message = UtilityMethods.sendSMSFormat(sms_params, Constants.SMS_CREATED_MSG);
		}
		
		// Update Guest Conversation 
		GuestConversation conversation = new GuestConversation(reservation, sms_message);
		conversation.setGuid(UtilityMethods.generateCtId());
		conversation.setCreatedBy(reservation.getUpdatedBy());
		conversation.setUpdatedBy(reservation.getUpdatedBy());
		Logger.debug("Adding coversation and message...........................");
		conversationService.addConversationAndMsg(conversation, false);

	
	}

	private void tagGuestWithOngoingEvent(Reservation reservation, String token) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put(Constants.START_TIME_BEFORE, new DateTime().toDate());
         params.put(Constants.END_TIME_AFTER, new DateTime().toDate());
         params.put(Constants.REST_GUID, reservation.getRestaurantGuid());
         List<String> eventType = new ArrayList<String>();
         eventType.add(Constants.EVENT);
         eventType.add(Constants.OFFER);
         params.put(Constants.TYPE, eventType);
         List<CalenderEvent> eventList = calEventDao.findByFields(CalenderEvent.class, params);

         List<String> tagNameList = new ArrayList<String>();
         for (CalenderEvent event : eventList) {
                tagNameList.add(event.getCategory());

         }
                
                Set<String> hs = new HashSet<>();
                hs.addAll(tagNameList);
                tagNameList.clear();
                tagNameList.addAll(hs);
                assignTagService.addGuestProfileEventTag(reservation.getGuestGuid(), tagNameList, token);


	}

	private Map<String, Object> copyMapValues(Map<String, Object> existingReservationMap, Map<String, Object> newReservationMap) {
		// TODO Auto-generated method stub

		for (Entry<String, Object> entry : existingReservationMap.entrySet()) {
			if (!newReservationMap.containsKey(entry.getKey())) {
				newReservationMap.put(entry.getKey(), entry.getValue());
			}
		}
		return newReservationMap;

	}

	private Map<String, Object> customeUpdateMap(Map<String, Object> objectAsMap, Reservation reservation) {

		/*
		 * Conversion need to retain the state of Date parameter after
		 * UtilityMethods .entityConversionToMap(reservation) Convert Entity
		 * Object to Map
		 */

		if (null != objectAsMap.get("estStartTime")) {
			objectAsMap.put("estStartTime", UtilityMethods.parseCustomeDate(reservation.getEstStartTime().toString(), Constants.EXT_TIMESTAMP_FORMAT, Constants.TIMESTAMP_FORMAT));
		}
		if (null != objectAsMap.get("estEndTime")) {
			objectAsMap.put("estEndTime", UtilityMethods.parseCustomeDate(reservation.getEstEndTime().toString(), Constants.EXT_TIMESTAMP_FORMAT, Constants.TIMESTAMP_FORMAT));
		}
		if (null != objectAsMap.get("actEndTime")) {
			objectAsMap.put("actEndTime", UtilityMethods.parseCustomeDate(reservation.getActEndTime().toString(), Constants.EXT_TIMESTAMP_FORMAT, Constants.TIMESTAMP_FORMAT));
		}
		if (null != objectAsMap.get("actStartTime")) {
			objectAsMap.put("actStartTime", UtilityMethods.parseCustomeDate(reservation.getActStartTime().toString(), Constants.EXT_TIMESTAMP_FORMAT, Constants.TIMESTAMP_FORMAT));
		}
		if (null != objectAsMap.get("tableGuid")) {
			objectAsMap.put("tableGuid", reservation.getTableGuid().toString());
		}

		return objectAsMap;

	}

	private Reservation copyExistingValue(Reservation reservation, Reservation existing, boolean isValidationESTTimeRequired) {
		// TODO Auto-generated method stub
		/*
		 * Add Common Reservation Values which can't be alter at time of
		 * Modification
		 */
		reservation.setReservationTime(existing.getReservationTime());
		reservation.setCreatedDate(existing.getCreatedDate());
		reservation.setBookingMode(existing.getBookingMode());
		if (null == reservation.getReservationStatus()) {
			reservation.setReservationStatus(existing.getReservationStatus());
		}
		if (null == reservation.getEstEndTime()) {
			reservation.setEstEndTime(existing.getEstEndTime());
			isValidationESTTimeRequired = false;
		}
		if (null == reservation.getEstStartTime()) {
			reservation.setEstStartTime(existing.getEstStartTime());
			isValidationESTTimeRequired = false;
		}
		if (null == reservation.getActStartTime()) {
			reservation.setActStartTime(existing.getActStartTime());
		}
		if (null == reservation.getActEndTime()) {
			reservation.setActEndTime(existing.getActEndTime());
		}
		// if(null==reservation.getRestaurantGuid()){
		reservation.setRestaurantGuid(existing.getRestaurantGuid());
		// }
		// if(null==reservation.getGuestGuid()){
		reservation.setGuestGuid(existing.getGuestGuid());
		// }
		if (null == reservation.getCreatedBy()) {
			reservation.setCreatedBy(existing.getGuestGuid());
		}
		if (null == reservation.getBookedBy()) {
			reservation.setBookedBy(existing.getBookedBy());
		}
		if (null == reservation.getNumCovers()) {
			reservation.setNumCovers(existing.getNumCovers());
		}

		if (null == reservation.getBookedById()) {
			reservation.setBookedById(existing.getBookedById());
		}
		
		if (null == reservation.getUpdatedBy()) {
			reservation.setCreatedBy(existing.getUpdatedBy());
		}
		if (null == reservation.getReservationNote()) {
			reservation.setReservationNote(existing.getReservationNote());
		}
		if (null == reservation.getTat()) {
			reservation.setTat(existing.getTat());
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Long time = reservation.getEstStartTime().getTime() + Long.parseLong(reservation.getTat().trim()) * 60 * 1000;
			sdf.format(new Date(time));
			try {
				reservation.setEstEndTime(sdf.parse(sdf.format(new Date(time))));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				log.warn("Exception in service", e);
			}

		}

		Logger.debug("in copy existing values table guid is==================================================== " + reservation.getTableGuid());
		if (null == reservation.getTableGuid() || reservation.getTableGuid().size() == 0) {
			reservation.setTableGuid(null);
			List<String> guid = new ArrayList<String>();
			Logger.debug("existing table guids are " + existing.getTableGuid());
			for (String guids : existing.getTableGuid()) {
				Logger.debug("guid is " + guids);
				String converguids[] = UtilityMethods.replaceSpecialCharacter(guids);
				Logger.debug("cover guids are " + Arrays.toString(converguids));
				guid.add(converguids[0]);
			}

			Logger.debug("guid is " + guid);
			reservation.tableGuid = guid;

		}

		return reservation;
	}

	@Override
	public BaseResponse getReservationStats(Map<String, Object> params) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		params = reservationValidator.validateGetStatsParams(params, listOfError);
		if (listOfError.isEmpty()) {
			Map<String, Object> distributionMap = new HashMap<String, Object>();
			params.put(Constants.RESERVATION_STATUS, Constants.FINISHED);
			distributionMap.put(Constants.MODE_DISTRIBUTION, reservationDao.getReservationModeCounts(params));
			params.put("queued", true);
			params.put(Constants.RESERVATION_STATUS, Arrays.asList(new String[] { Constants.FINISHED, Constants.NO_SHOW_STATUS, Constants.CANCELLED, Constants.SEATED }));
			distributionMap.put(Constants.QUEUE_DISTRIBUTION, reservationDao.getWaitlistStatusCounts(params));
			params.remove("queued");

			params.put(Constants.RESERVATION_STATUS, Arrays.asList(new String[] { Constants.FINISHED, Constants.NO_SHOW_STATUS, Constants.CANCELLED }));
			params.put("bookingMode", Constants.ONLINE_STATUS);
			distributionMap.put(Constants.STATUS_DISTRIBUTION, reservationDao.getReservationStatusCounts(params));
			params.remove("bookingMode");

			Map<Integer, Object> tatDistributionMap = new HashMap<Integer, Object>();
			initializeMap(tatDistributionMap);
			Map<Integer, Object> waitDistributionMap = new HashMap<Integer, Object>();
			initializeMap(waitDistributionMap);
			Map<Object, Integer> coversDistributionMap = new HashMap<Object, Integer>();
			// initializeCoversMap(coversDistributionMap, params);

			params.put(Constants.RESERVATION_STATUS, Constants.FINISHED);
			distributionMap.put(Constants.GUEST_DISTRIBUTION, reservationDao.getReservationGuestCounts(params));
			params.put(Constants.DASHBOARD, true);
			List<Reservation> reservations = reservationDao.findByFields(Reservation.class, params);
/*System.out.println(params);*/

			for (Reservation reservation : reservations) {
				/*System.out.println(reservation.getGuid()+"<<getGuid|"+reservation.getReservationStatus()+"<<getSTATUS|"+reservation.getGuestGuid()+"<<getGuid|");*/
				updateTatAndWaitTimeDistribution(tatDistributionMap, waitDistributionMap, reservation);
				updateCoversDistribution((String) params.get(Constants.CALENDER_TYPE), coversDistributionMap, reservation);
			}

			for (int i = 1; i < 10; i++) {
				int[] tatArr = (int[]) tatDistributionMap.get(i);
				if (tatArr[1] != 0)
					tatDistributionMap.put(i, (int) tatArr[0] / (tatArr[1] * 60 * 1000));
				else
					tatDistributionMap.put(i, 0);

				int[] waitArr = (int[]) waitDistributionMap.get(i);
				if (waitArr[1] != 0)
					waitDistributionMap.put(i, (int) waitArr[0] / (waitArr[1] * 60 * 1000));
				else
					waitDistributionMap.put(i, 0);
			}

			distributionMap.put(Constants.TAT_DISTRIBUTION, tatDistributionMap);
			distributionMap.put(Constants.WAIT_DISTRIBUTION, waitDistributionMap);
			cumulateCoversDistribution(coversDistributionMap, params);
			distributionMap.put(Constants.COVER_DISTRIBUTION, coversDistributionMap);
			ArrayList<Object> list = new ArrayList<Object>();
			list.add(distributionMap);
			response = new GetResponse<Object>(ResponseCodes.DASHBOARD_STATS_FETCHED_SUCCESSFULLY, list);
			return response;
		}

		return new ErrorResponse(ResponseCodes.DASHBOARD_STATS_FETCHED_FAILURE, listOfError);
	}

	private void cumulateCoversDistribution(Map<Object, Integer> coversDistributionMap, Map<String, Object> params) {
		int start = 0;
		int end = 0;
		int max = 0;

		DateTime startDate = (DateTime) params.get(Constants.EST_END_AFTER);
		DateTime endDate = (DateTime) params.get(Constants.EST_START_BEFORE);
		String calendarType = (String) params.get(Constants.CALENDER_TYPE);

		switch (calendarType) {
		case Constants.DAYS:
			start = startDate.getHourOfDay();
			end = endDate.getHourOfDay();
			max = startDate.hourOfDay().withMaximumValue().getHourOfDay();
			break;
		case Constants.WEEK:
			start = startDate.getDayOfWeek();
			end = endDate.getDayOfWeek();
			max = startDate.dayOfWeek().withMaximumValue().getDayOfWeek();
			break;
		case Constants.MONTH:
			start = startDate.getDayOfMonth();
			end = endDate.getDayOfMonth();
			max = startDate.dayOfMonth().withMaximumValue().getDayOfMonth();
			break;
		case Constants.YEAR:
			start = startDate.getMonthOfYear();
			end = endDate.getMonthOfYear();
			max = startDate.monthOfYear().withMaximumValue().getMonthOfYear();
			break;
		}

		int sum = 0;

		for (int i = start; i <= (end > start ? end : max + end); i++) {
			if (coversDistributionMap.containsKey(i > max ? i - max : i))
				sum = sum + coversDistributionMap.get(i > max ? i - max : i);
			coversDistributionMap.put(i > max ? i - max : i, sum);
		}

	}

	private void updateTatAndWaitTimeDistribution(Map<Integer, Object> tatDistributionMap, Map<Integer, Object> waitDistributionMap, Reservation reservation) {
		Integer numCovers = reservation.getNumCovers() < 10 ? reservation.getNumCovers() : 9;
		DateTime actStartTime;
		DateTime actEndTime;

		if (reservation.getActStartTime() != null)
			actStartTime = new DateTime(reservation.getActStartTime().getTime());
		else
			actStartTime = new DateTime(reservation.getEstStartTime().getTime());
		if (reservation.getActEndTime() != null)
			actEndTime = new DateTime(reservation.getActEndTime().getTime());
		else
			actEndTime = new DateTime(reservation.getEstEndTime().getTime());

		int[] arr = (int[]) tatDistributionMap.get(numCovers);
		arr[0] = arr[0] + (int) (actEndTime.getMillis() - actStartTime.getMillis());
		arr[1] += 1;
		tatDistributionMap.put(numCovers, arr);

		if (reservation.getBookingMode().equals(Constants.WALKIN_STATUS)) {
			DateTime arriveTime = new DateTime(reservation.getCreatedDate().getTime());
			arr = (int[]) waitDistributionMap.get(numCovers);
			arr[0] = arr[0] + (int) (actStartTime.getMillis() - arriveTime.getMillis());
			arr[1] += 1;
			waitDistributionMap.put(numCovers, arr);
		}

	}

	private void updateCoversDistribution(String calendarType, Map<Object, Integer> coversDistributionMap, Reservation reservation) {

		DateTime actStartTime;
		DateTime actEndTime;

		if (reservation.getActStartTime() != null)
			actStartTime = new DateTime(reservation.getActStartTime().getTime());
		else
			actStartTime = new DateTime(reservation.getEstStartTime().getTime());
		if (reservation.getActEndTime() != null)
			actEndTime = new DateTime(reservation.getActEndTime().getTime());
		else
			actEndTime = new DateTime(reservation.getEstEndTime().getTime());

		switch (calendarType) {
		case Constants.DAYS:
			addToCoversMap(coversDistributionMap, actStartTime.getHourOfDay(), actEndTime.getHourOfDay(), reservation.getNumCovers());
			break;
		case Constants.WEEK:
			addToCoversMap(coversDistributionMap, actStartTime.getDayOfWeek(), actEndTime.getDayOfWeek(), reservation.getNumCovers());
			break;
		case Constants.MONTH:
			addToCoversMap(coversDistributionMap, actStartTime.getDayOfMonth(), actEndTime.getDayOfMonth(), reservation.getNumCovers());
			break;
		case Constants.YEAR:
			addToCoversMap(coversDistributionMap, actStartTime.getMonthOfYear(), actEndTime.getMonthOfYear(), reservation.getNumCovers());
			break;
		}

	}

	private void initializeCoversMap(Map<Object, Integer> coversDistributionMap, Map<String, Object> params) {
		DateTime startDate = (DateTime) params.get(Constants.ACT_END_AFTER);
		DateTime endDate = (DateTime) params.get(Constants.ACT_START_BEFORE);
		String calendarType = (String) params.get(Constants.CALENDER_TYPE);
		do {
			switch (calendarType) {
			case Constants.DAYS:
				// for(int i=0;i<=23;i++)
				coversDistributionMap.put(startDate.getHourOfDay(), 0);
				startDate = startDate.plusHours(1);
				break;
			case Constants.WEEK:
				coversDistributionMap.put(startDate.getDayOfWeek(), 0);
				startDate = startDate.plusDays(1);
				break;
			case Constants.MONTH:
				coversDistributionMap.put(startDate.getDayOfMonth(), 0);
				startDate = startDate.plusWeeks(1);
				break;
			case Constants.YEAR:
				coversDistributionMap.put(startDate.getMonthOfYear(), 0);
				startDate = startDate.plusMonths(1);
				break;
			}
		} while (!startDate.isAfter(endDate));
		/*
		 * switch(params){ case Constants.DAYS: for(int i=0;i<=23;i++)
		 * coversDistributionMap.put(i, 0); break; case Constants.WEEK: for(int
		 * i=1;i<=7;i++) coversDistributionMap.put(i, 0); break; case
		 * Constants.MONTH: for(int i=1;i<=31;i++) coversDistributionMap.put(i,
		 * 0); break; case Constants.YEAR: for(int i=1;i<=12;i++)
		 * coversDistributionMap.put(i, 0); break; }
		 */

	}

	private void addToCoversMap(Map<Object, Integer> coversDistributionMap, int startHour, int endHour, Integer numCovers) {
		if (!coversDistributionMap.containsKey(startHour))
			coversDistributionMap.put(startHour, 0);
		coversDistributionMap.put(startHour, coversDistributionMap.get(startHour) + numCovers);
		if (startHour != endHour) {
			if (!coversDistributionMap.containsKey(endHour))
				coversDistributionMap.put(endHour, 0);
			coversDistributionMap.put(endHour, coversDistributionMap.get(endHour) + numCovers);
		}
	}

	private void initializeMap(Map<Integer, Object> distributionMap) {
		for (int i = 1; i < 10; i++)
			distributionMap.put(i, new int[2]);
	}

	@Override
	@Transactional(readOnly = true)
	public BaseResponse getQueueReservation(Map<String, Object> params, String token) {
		BaseResponse getResponse;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID) && !userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			params.put(Constants.REST_GUID, userInfo.getRestGuid());
		}

		/*
		 * Validating Restaurant GUID in case of CT_ADMIN_ROLE_ID Or
		 * CUSTOMER_ROLE_ID
		 */

		if (!params.containsKey(Constants.REST_GUID)) {

			listOfError.add(reservationValidator.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
			return new ErrorResponse(ResponseCodes.RESERVATION_RECORD_FETCH_FAILURE, listOfError);
		}

		if (!params.containsKey(Constants.COVERS)) {

			listOfError.add(reservationValidator.createError(Constants.COVERS, ErrorCodes.COVERS_REQUIRED));
			return new ErrorResponse(ResponseCodes.RESERVATION_RECORD_FETCH_FAILURE, listOfError);
		}

		List<Reservation> reservations = reservationDao.getQueueReservation(params);
		getResponse = new GetResponse<Reservation>(ResponseCodes.RESERVATION_RECORD_FETCH_SUCCESFULLY, reservations);
		return getResponse;
	}

	@Override
	public BaseResponse getReservationsForTables(Reservation reservation) {
		
		BaseResponse getResponse;
		
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		Date current_date = new Date();
		try {
			current_date = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.warn("Exception in service", e);
		}
		
		List<ValidationError> errorList = new ArrayList<>();
		Map<String, Object> shiftTime = waitlistService.getApplicableShifts( reservation.getRestaurantGuid(), errorList,current_date,false);
	
		Long startTime = (Long) shiftTime.get("startTime");
		Long currentShiftEnd = (Long) shiftTime.get("currentDayShiftEndTime");
		
		/* get all the reservations with block event*/
		Map<String,Object> resvParam = new HashMap<>();	
		resvParam.put(Constants.START_TIME, startTime);
		resvParam.put("currentShiftEnd", (currentShiftEnd + 60*60*1000L));
		resvParam.put("restaurantId", reservation.getRestaurantGuid());
		
		List<Reservation> allReservationList = shuffleDao.getTablesHavingReservation(resvParam);
		
		for(Reservation r : allReservationList){
			if(r.getGuid().equals(reservation.getGuid())){
				allReservationList.remove(r);
				break;
			}
		}
		
		Map <String, List<Reservation>> tableResvMap = new HashMap<>();
		shuffleService.addAllResvToBlockMap(allReservationList, tableResvMap); 		
		
		

		Long currentTime = current_date.getTime();
		if (reservation.getBookingMode().equals(Constants.ONLINE_STATUS)) {
			if (currentTime < reservation.getEstStartTime().getTime()) {
			} else {
				currentTime = reservation.getEstStartTime().getTime();
			}
		} else {/*
			currentTime = new Date().getTime();

		*/}
		
		
		List<ReservationForTables> resvForTblList = new ArrayList<>();
		Map<String,ReservationForTables> resvMap = new HashMap<>();
		Map<String,ReservationForTables> allocatedMap = new HashMap<>();
		Map<String,ReservationForTables> seatedMap = new HashMap<>();
		ReservationForTables resvTable = null;
		
		for(String tableForResv : reservation.getTableGuid())
		{
			List<Reservation> resvList = tableResvMap.get(tableForResv);
			
			if(resvList != null)
			{
				for(Reservation resvFromList : resvList)
				{
					resvTable = new ReservationForTables();
					resvTable.setEstEndTime(resvFromList.getEstEndTime());
					resvTable.setEstStartTime(resvFromList.getEstStartTime());
					resvTable.setReservationGuid(resvFromList.getGuid());
					resvTable.setTableGuid(resvFromList.getTableGuid());
					resvTable.setRequestTime(current_date.getTime());
					
					Long resvEstEndTime = 0L;
					/*to handle the case where the user is trying to seat an OOH walkin where est_end_time is null*/
					if(reservation.getEstEndTime() == null){
						try {
							resvEstEndTime = sdf.parse(sdf.format(new Date(currentTime + Long.valueOf(reservation.getTat())*60*1000))).getTime();
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							log.warn("Exception in service", e);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							log.warn("Exception in service", e);
						}
					}else{
						resvEstEndTime = reservation.getEstEndTime().getTime();
					}
					
			
					
					if(resvFromList.getReservationStatus().equals(Constants.SEATED)){
						resvTable.setReservationStatus(Constants.SEATED);
						resvTable.setAvailableAfter(null);
						seatedMap.put(resvFromList.getGuid(),resvTable);
					}else if(resvFromList.getEstStartTime().getTime() >= currentTime && resvEstEndTime > resvFromList.getEstStartTime().getTime()){
						resvTable.setReservationStatus(Constants.RESERVED);
						resvTable.setAvailableAfter(resvFromList.getEstEndTime().getTime() - (current_date.getTime()));
						resvMap.put(resvFromList.getGuid(),resvTable);
					}else if(resvFromList.getEstStartTime().getTime() <= currentTime && resvFromList.getEstEndTime().getTime() > currentTime){
						resvTable.setReservationStatus(Constants.ALLOCATED);
						resvTable.setAvailableAfter(resvFromList.getEstEndTime().getTime() - (current_date.getTime()));
						allocatedMap.put(resvFromList.getGuid(),resvTable);
					}else{
						continue;
					}
					
					
				}
			}
		}
		
		for(Map.Entry<String, ReservationForTables> entry : seatedMap.entrySet())
		{
			resvForTblList.add(entry.getValue());
		}
		
		for(Map.Entry<String, ReservationForTables> entry : allocatedMap.entrySet())
		{
			resvForTblList.add(entry.getValue());
		}
		
		for(Map.Entry<String, ReservationForTables> entry : resvMap.entrySet())
		{
			resvForTblList.add(entry.getValue());
		}
		
		
		getResponse = new GetResponse<ReservationForTables>(ResponseCodes.RESERVATION_RECORD_FETCH_SUCCESFULLY, resvForTblList);
		return getResponse;
	}

	String getReservationShortId(UserInfoModel userInfo) {
		List<CurrentValues> values = currentValuesDao.findAll(CurrentValues.class);
		long id = 0;
		CurrentValues value;
		if (values.isEmpty()) {
			value = new CurrentValues();
			value.setInfoOnCreate(userInfo);
			int minId = 0;
			for (int i = 1; i < Constants.MIN_ID_LENGTH; i++) {
				minId += Math.pow(Constants.BASE, i);
			}
			value.setReservationId(minId);
			currentValuesDao.create(value);
			id = value.getReservationId();
		} else if (values.size() == 1) {
			value = values.get(0);
			id = values.get(0).getReservationId();
			value.setReservationId(id + 1);
			currentValuesDao.update(value);
		}
		return DateTime.now().getYearOfCentury() + UtilityMethods.convertToBase62(id);

	}

	@Override
	public BaseResponse convertReservationToWaitlist(Map<String, Object> params, String token) {
		BaseResponse response = new BaseResponse();
		String addToWaitlistGuid = "", cancelledReservationGuid = "", finishedReservationGuid = "";

		Map<String, Object> shuffleMap = new HashMap<>();

		shuffleMap.put(Constants.REST_ID, params.get(Constants.REST_GUID));

		if (params.get("addToWaitlistGuid") != null && !params.get("addToWaitlistGuid").equals("null")) {
			addToWaitlistGuid = (String) params.get("addToWaitlistGuid");
			String[] guidArr = addToWaitlistGuid.split(",");
			for (String guid : guidArr) {
				response = addToWaitlist(guid, token);
				
				if(response instanceof ErrorResponse){
					return response;
				}
				
			}
		}
		
		if ((params.get("finishedReservationGuid") != null) && (!params.get("finishedReservationGuid").equals("null"))) {
			finishedReservationGuid = (String) params.get("finishedReservationGuid");

			String[] guidArr = finishedReservationGuid.split(",");
			for (String guid : guidArr) {
				response = finishReservation(guid, token);
			}
		}

		if ((params.get("cancelledReservationGuid") != null) && (!params.get("cancelledReservationGuid").equals("null"))) {

			cancelledReservationGuid = (String) params.get("cancelledReservationGuid");
			String[] guidArr = cancelledReservationGuid.split(",");
			for (String guid : guidArr) {
				response = cancelReservation(guid, token);
			}

		}


		if ((params.get("seatWithReduceTat") != null) && (params.get("reducedEndTime") != null) && (!params.get("reducedEndTime").equals("null")) && (!params.get("seatWithReduceTat").equals("null"))) {
			response = seatWaitlistWithReducedTat((String) params.get("seatWithReduceTat"), (String) params.get("reducedEndTime"), token, (List<String>) params.get("tableGuid"));
		}
		
		if ((params.get("seatWaitistGuid") != null) && (!params.get("seatWaitistGuid").equals("null"))) {
			response = seatWaitlist((String) params.get("seatWaitistGuid"), token, (List<String>) params.get("tableGuid"));
		}
		
		if((params.get("seatWithReduceTat") == null || params.get("seatWithReduceTat").equals("null")) && (params.get("reducedEndTime") != null && !params.get("reducedEndTime").equals("null"))){
			if(params.get("guid") != null && !params.get("guid").equals("null"))
			{
				Long currentTime = Long.valueOf(params.get("requestTime").toString());
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
				Date endTime = null;
				try {
					endTime = sdf.parse((String) params.get("reducedEndTime"));
				} catch (ParseException e) {
					log.warn("Exception in service", e);
				}
				Integer tat = 0;
				if(endTime != null){
					//tat = (int) ((endTime.getTime() - Long.valueOf(params.get("requestTime").toString())) / (1000 * 60));
					tat = (int) ((endTime.getTime() - currentTime) / (1000 * 60));
					//tat = endTime.getTime() - Long.valueOf(params.get("requestTime").toString());
				}

				Map<String,Object> dataMap = new HashMap<>();
				dataMap.put("guid", params.get("guid"));
				dataMap.put("isUnknown", params.get("isUnknown"));
				dataMap.put("numCovers",  params.get("numCovers"));
				dataMap.put("tableGuid", params.get("tableGuid"));
				dataMap.put("tat", tat);
				dataMap.put("requestTime", currentTime);
				List<ValidationError> listOfError = new ArrayList<>();
				Reservation reservation = barEntryValidator.validateMoveToRestaurantData(dataMap, listOfError );

				if (!listOfError.isEmpty()) {
					response = new ErrorResponse(ResponseCodes.BARENTRY_UPDATION_FAILURE, listOfError);
					return response;
				}


				Map<String, Object> barEntryMap = new HashMap<String, Object>();

				UserInfoModel userInfo = authService.getUserInfoByToken(token);
				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(TimeZone.getTimeZone("IST"));
				barEntryMap.put(Constants.GUID, dataMap.get(Constants.GUID));
				barEntryMap.put(Constants.STATUS, Constants.FINISHED);
				barEntryMap.put(Constants.UPDATED_DATE,	cal.getTimeInMillis());
				barEntryMap.put(Constants.END_TIME, cal.getTimeInMillis());
				barEntryMap.put(Constants.UPDATED_BY, userInfo.getGuid());

				BarEntry updatedBarEntry = barEntryDao.update(barEntryMap);

				reservation.setBookingMode(Constants.WALKIN_STATUS);
				reservation.setReservationStatus(Constants.SEATED);
				reservation.setRestaurantGuid(userInfo.getRestGuid());
				reservation.setIsUnknown(false);
				reservation.setGuestGuid(updatedBarEntry.getGuestGuid());
				reservation.setInfoOnCreate(userInfo);

				response = createReservation(reservation, token, true);

				if (response.getResponseCode().equals(
						ResponseCodes.RESERVATION_ADDED_SUCCESFULLY)) {	
					response = seatWaitlistWithReducedTat(reservation.getGuid(), (String)params.get("reducedEndTime"), token, reservation.getTableGuid());
				}
			}
		}
		
		else if((params.get("seatWaitistGuid") == null || params.get("seatWaitistGuid").equals("null"))){

			if(params.get("guid") != null && !params.get("guid").equals("null"))
			{
				Map<String,Object> dataMap = new HashMap<>();
				dataMap.put("guid", params.get("guid"));
				dataMap.put("isUnknown", params.get("isUnknown"));
				dataMap.put("numCovers",  params.get("numCovers"));
				dataMap.put("tableGuid", params.get("tableGuid"));
				dataMap.put("tat", params.get("tat"));
				dataMap.put("requestTime", params.get("requestTime"));
				List<ValidationError> listOfError = new ArrayList<>();
				Reservation reservation = barEntryValidator.validateMoveToRestaurantData(dataMap, listOfError );

				if (!listOfError.isEmpty()) {
					response = new ErrorResponse(ResponseCodes.BARENTRY_UPDATION_FAILURE, listOfError);
					return response;
				}


				Map<String, Object> barEntryMap = new HashMap<String, Object>();

				UserInfoModel userInfo = authService.getUserInfoByToken(token);
				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(TimeZone.getTimeZone("IST"));
				barEntryMap.put(Constants.GUID, dataMap.get(Constants.GUID));
				barEntryMap.put(Constants.STATUS, Constants.FINISHED);
				barEntryMap.put(Constants.UPDATED_DATE,	cal.getTimeInMillis());
				barEntryMap.put(Constants.END_TIME, cal.getTimeInMillis());
				barEntryMap.put(Constants.UPDATED_BY, userInfo.getGuid());

				BarEntry updatedBarEntry = barEntryDao.update(barEntryMap);

				reservation.setBookingMode(Constants.WALKIN_STATUS);
				reservation.setReservationStatus(Constants.SEATED);
				reservation.setRestaurantGuid(userInfo.getRestGuid());
				reservation.setIsUnknown(false);
				reservation.setGuestGuid(updatedBarEntry.getGuestGuid());
				reservation.setInfoOnCreate(userInfo);

				response = createReservation(reservation, token, true);

				if (response.getResponseCode().equals(
						ResponseCodes.RESERVATION_ADDED_SUCCESFULLY)) {	
					response = seatWaitlist(reservation.getGuid(), token, reservation.getTableGuid());
				}

			}
		}
		
		

		Runnable runnableTask = () -> {
			shuffleService.shuffleTables(shuffleMap, token);
		};
		new Thread(runnableTask).start();

		return response;
	}

	private BaseResponse finishReservation(String finishedReservationGuid, String token) {
		Reservation resv = new Reservation();
		resv.setGuid(finishedReservationGuid);
		resv.setReservationStatus(Constants.FINISHED);
		resv.setToBypass(true);
		BaseResponse response = patchReservation(resv, token);
		return response;

	}

	private BaseResponse seatWaitlist(String seatWaitistGuid, String token, List<String> tableGuid) {
		Reservation resv = new Reservation();
		resv.setReservationStatus(Constants.SEATED);
		resv.setGuid(seatWaitistGuid);
		resv.setToBypass(true);
		if ((tableGuid != null) && (tableGuid.size() > 0)) {
			resv.setTableGuid(tableGuid);
		}
		BaseResponse response = patchReservation(resv, token);
		return response;
	}

	private BaseResponse addToWaitlist(String addToWaitlistGuid, String token) {
		Reservation resv = new Reservation();
		resv.setBookingMode(Constants.WALKIN_STATUS);
		resv.setGuid(addToWaitlistGuid);
		resv.setTableGuid(new ArrayList<>());
		BaseResponse response = patchReservation(resv, token);
		return response;
	}

	private BaseResponse cancelReservation(String cancelledReservationGuid, String token) {
		Reservation resv = new Reservation();
		resv.setReservationStatus(Constants.CANCELLED);
		resv.setGuid(cancelledReservationGuid);
		resv.setToBypass(true);
		BaseResponse response = patchReservation(resv, token);
		return response;
	}

	private BaseResponse seatWaitlistWithReducedTat(String seatWithReduceTat, String reducedEndTime, String token, List<String> tableGuid) {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		Date endTime = null;
		try {
			endTime = sdf.parse(reducedEndTime);
		} catch (ParseException e) {
			log.warn("Exception in service", e);
		}
		Date startTime = new Date();
		int tat = (int) ((endTime.getTime() - startTime.getTime()) / (1000 * 60));
		
		Reservation resv = new Reservation();
		resv.setGuid(seatWithReduceTat);
		resv.setTat(Integer.toString(tat));
		resv.setEstStartTime(startTime);
		resv.setEstEndTime(endTime);
		resv.setToBypass(true);
		if ((tableGuid != null) && (tableGuid.size() > 0)) {
			resv.setTableGuid(tableGuid);
		}
		resv.setReservationStatus(Constants.SEATED);
		BaseResponse response = patchReservation(resv, token);
		return response;
	}

	@Override
	public BaseResponse getReservationWithRespectToGuid(Map<String, Object> params, String token) {
		BaseResponse getResponse;
		List<ValidationError> listOfError = new ArrayList<>();
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		Logger.debug("User info is------------------------------"+userInfo);
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
			if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
				params.put(Constants.GUEST_GUID, userInfo.getGuid());
			} else {
				params.put(Constants.REST_GUID, userInfo.getRestGuid());
			}
		}

		if (!params.containsKey(Constants.REST_GUID)) {

			listOfError.add(reservationValidator.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
			return new ErrorResponse(ResponseCodes.RESERVATION_RECORD_FETCH_FAILURE, listOfError);
		}

		Reservation reservation = reservationDao.getReservationWithRespectToGuid(params);
		List<Reservation> resvList = new ArrayList<>();
		if (reservation != null) {
			resvList.add(reservation);
		}
		getResponse = new GetResponse<Reservation>(ResponseCodes.RESERVATION_RECORD_FETCH_SUCCESFULLY, resvList);
		return getResponse;
	}

	@Override
	public BaseResponse getReservationsCSV(String token, Map<String, Object> stringParamMap) {

		BaseResponse response;
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		Map<String, Object> params = reservationValidator.validateParamsForReport(stringParamMap, errorList, userInfo);
		if (errorList.isEmpty()) {
			restValidator.validateGuid(params.get(Constants.REST_GUID).toString(), errorList);
		}

		if (errorList.isEmpty()) {
			File dir = new File("exportCSV");
			dir.mkdir();
			String outputFile = "exportCSV/" + params.get(Constants.REST_GUID).toString() + "(reservation details)-" + UtilityMethods.timestamp() + ".csv";
			try {
				CsvWriter writer = new CsvWriter(new FileWriter(outputFile, true), ',');
				Map<String, Object> tableParams = new HashMap<String, Object>();
				tableParams.put(Constants.REST_ID, params.get(Constants.REST_GUID).toString());
				List<Table> tables = tableDao.findAllTables(Table.class, tableParams);
				Map<String, Table> tableGuidMap = new HashMap<String, Table>();
				tables.forEach(x -> tableGuidMap.put(x.getGuid(), x));
				Map<Reservation, GuestProfile> reservationData = reservationDao.getReservationDetailsOnDate(params);
				if (reservationData.isEmpty()) {
					errorList.add(reservationValidator.createError(Constants.RESERVATION_LABEL, ErrorCodes.RESERVATION_NOT_FOUND));
					return new ErrorResponse(ResponseCodes.RESERVATION_CSV_FETCH_FAILURE, errorList);
				}
				writer.write("Name");
				writer.write("Contact Number");
				writer.write("VIP");
				writer.write("No. of Covers");
				writer.write("Start Time");
				writer.write("End Time");
				writer.write("Table");
				writer.write("TAT");
				writer.write("Status");
				writer.write("Note");
				writer.endRecord();
				reservationData.keySet().forEach(resv -> {
					try {
						String name = reservationData.get(resv).getFirstName();
						/*if (reservationData.get(resv).getLastName() != null) {
							name = name + " " + reservationData.get(resv).getLastName();
						}*/
						writer.write(name);
						writer.write(reservationData.get(resv).getMobile());
						if (reservationData.get(resv).getIsVip())
							writer.write(reservationData.get(resv).getReason());
						else
							writer.write("");
						writer.write(resv.getNumCovers().toString());
						writer.write(resv.getEstStartTime() + "");
						writer.write(resv.getEstEndTime() + "");
						String tableNames = resv.getTableGuid().stream().map(x -> tableGuidMap.get(x).getName()).collect(Collectors.joining(","));
						writer.write(tableNames);
						writer.write(resv.getTat());
						writer.write(resv.getReservationStatus());
						writer.write(resv.getReservationNote());
						writer.endRecord();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						log.warn("Exception in service", e);
					}

				});
				writer.close();
			} catch (Exception v) {
				System.err.println(v);
				return new ErrorResponse(ResponseCodes.RESERVATION_CSV_FETCH_FAILURE, errorList);
			}
			response = new SupportResponse<File>(ResponseCodes.RESERVATION_CSV_FETCH_SUCCESFULLY, new File(outputFile));
			return response;
		} else {
			/*System.out.println("listerror");*/
			return new ErrorResponse(ResponseCodes.RESERVATION_CSV_FETCH_FAILURE, errorList);
		}
		// return new File(outputFile);

	}
	
	
	
	public Boolean addToQueue(Reservation reservation)
	{
		Boolean created = false;

		Map<Integer,Queue> queueMap = queueDao.getQueue(reservation.getRestaurantGuid());
		Integer resvCovers = reservation.getNumCovers();
		if(resvCovers >8)
		{
			resvCovers = 9;
		}
		///update queue
		Queue queue = queueMap.get(resvCovers);
		if(queue != null)
		{
			int count = queue.getCount();
			queue.setCount(count+1);
			Queue newQueue = queueDao.updateAllProperties(queue);
			created = queueDao.updateQueueData(newQueue,reservation);
		}
	
		return created;
		
		
	}
	
	
	@Override
	public Boolean deleteWaitlist(String guid , String token)
	{
		Boolean deleted = false;
		
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		
		/*List<ValidationError> listOfError = new ArrayList<>();

		if (!(reservation.getRestaurantGuid().equals(userInfo.getRestGuid()))){
			listOfError.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.REST_FOR_STAFF_NOT_VALID), ErrorCodes.REST_FOR_STAFF_NOT_VALID));
		}*/
		
		Reservation resv = new Reservation();
		resv.setGuid(guid);
		resv.setRestaurantGuid(userInfo.getRestGuid());
		//queueDao.deleteQueueReservation(resv);
		removeResvFromQueue(resv);
		reservationDao.deleteWaitlistData(guid);
		deleted = true;
		
		
		return deleted;
		
	}
	
	
	
	
	
	
	@Override
	@Transactional
	public BaseResponse directlyAddToQueue(Reservation reservation, String token) {
		BaseResponse response;
		List<Table> tableList = new ArrayList<Table>();
		reservation.setReservationStatus("CREATED");
		GuestProfile guest = null;
		String guestName = null;
		String restName = null;
		String restLocality = null;
		String restRegion = null;
		String guestMobile = null;
		String restCity = null;

		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		reservation.setShortId(getReservationShortId(userInfo));
		
		List<ValidationError> listOfError = new ArrayList<>();

		if (!(reservation.getRestaurantGuid().equals(userInfo.getRestGuid()))){
			listOfError.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.REST_FOR_STAFF_NOT_VALID), ErrorCodes.REST_FOR_STAFF_NOT_VALID));
		}
		
		/* Add Dummy Guest In case of Unknow Guest */

		if (reservation.getIsUnknown() && reservation.getBookingMode().equalsIgnoreCase(Constants.WALKIN_STATUS)) {
			guest = guestValidator.getDummyGuest();
			if (null == guest) {
				listOfError.add(new ValidationError(Constants.DUMMY_GUEST_ID, UtilityMethods.getErrorMsg(ErrorCodes.DUMMY_GUEST_ID_NOT_FOUND), ErrorCodes.DUMMY_GUEST_ID_NOT_FOUND));
			} else {
				reservation.setGuestGuid(guest.getGuid());

			}
		}

		if(!listOfError.isEmpty()){
			return new ErrorResponse(ResponseCodes.RESERVATION_ADDED_FAILURE, listOfError);
		}
		
		if(reservation.getEstStartTime().getTime() == new Date(0).getTime())
		{
			if(reservation.getTableGuid() == null || reservation.getTableGuid().size() == 1){
				reservation.setTableGuid(new ArrayList<>());
			}
			reservation.setQueued(true);
			Reservation resv = reservationDao.addReservation(reservation, guest, tableList);
			Boolean addedToQueue = addToQueue(reservation);
			Reservation [] resvArr = new Reservation[1];
			resvArr[0] = resv;
			response = new PostResponse<Reservation>(ResponseCodes.RESERVATION_ADDED_SUCCESFULLY, resvArr);
			return response;
		}
		
		listOfError = reservationValidator.validateReservationOnCreate(reservation, Constants.ADD);

		/* Validating Guest | Table | Restaurant | Covers */
		Map<String, Object> map = reservationValidator.validateRestGuestTable(reservation, listOfError);

		if(!listOfError.isEmpty()){
			return new ErrorResponse(ResponseCodes.RESERVATION_ADDED_FAILURE, listOfError);
		}

		if (null != map){
			guestName = map.get(Constants.NAME).toString();
			restName = map.get(Constants.RESTAURANT_NAME) != null ? map.get(Constants.RESTAURANT_NAME).toString() : null;
			restLocality = map.get(Constants.LOCALITY) != null ? map.get(Constants.LOCALITY).toString() : null;
			restCity = map.get(Constants.CITY) != null ? map.get(Constants.CITY).toString() : null;
			guestMobile = map.get(Constants.MOBILE) != null ? map.get(Constants.MOBILE).toString() : null;
		}

		reservationValidator.validateTat(listOfError, reservation);
		if(!listOfError.isEmpty()){
			return new ErrorResponse(ResponseCodes.RESERVATION_ADDED_FAILURE, listOfError);
		}
		
		Boolean isValidTable = shuffleService.shuffleTablesMethod(reservation, null);
		
		if(!isValidTable)
			reservationValidator.validateReservationTimeSlot( reservation, listOfError);
		
		if (!listOfError.isEmpty()) {
			return new ErrorResponse(ResponseCodes.RESERVATION_ADDED_FAILURE, listOfError);
		}
		
		
		Reservation resv = reservationDao.addReservation(reservation, guest, tableList);
		Reservation [] resvArr = new Reservation[1];
		resvArr[0] = resv;
		
		if(reservation.getBookingMode().equals(Constants.WALKIN_STATUS))
		{
			Boolean addedToQueue = addToQueue(reservation);
		}
		
		response = new PostResponse<Reservation>(ResponseCodes.RESERVATION_ADDED_SUCCESFULLY, resvArr);
		
		if (response.getResponseCode().equalsIgnoreCase("9002") && !reservation.getIsUnknown() && null != restName && reservation.getBookingMode().equalsIgnoreCase(Constants.ONLINE_STATUS)) {
			try {
				SimpleDateFormat dateformat = new SimpleDateFormat("EEE, d MMM yyyy");
				SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
				Object params[] = { guestName, restName + (null != restLocality ? ", " + restLocality : "") + (null != restCity ? ", " + restCity : ""), reservation.getNumCovers(),
						dateformat.format(new Date(reservation.getEstStartTime().getTime())), timeformat.format(new Date(reservation.getEstStartTime().getTime())) };
				String sms_message = UtilityMethods.sendSMSFormat(params, Constants.SMS_CREATED_MSG);
				// Update Guest Conversation
				GuestConversation conversation = new GuestConversation(reservation, sms_message);
				conversation.setGuid(UtilityMethods.generateCtId());
				conversation.setCreatedBy(reservation.getCreatedBy());
				conversation.setUpdatedBy(reservation.getUpdatedBy());
				conversationService.addConversationAndMsg(conversation, false);

			} catch (Exception e) {
				log.warn("Exception in service", e);
			}
		} else if (response.getResponseCode().equalsIgnoreCase("9002") && !reservation.getIsUnknown() && null != restName && reservation.getBookingMode().equalsIgnoreCase(Constants.WALKIN_STATUS)) {
			final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			for (StackTraceElement element : stackTrace) {
				try {
					if (element.getMethodName().equalsIgnoreCase(Constants.WAITLIST_METHOD) && element.getClassName().equalsIgnoreCase(Constants.WAITLIST_PCKG)) {
						SimpleDateFormat dateformat = new SimpleDateFormat("EEE, d MMM yyyy");
						SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
						SimpleDateFormat minFormat = new SimpleDateFormat("mm");
						int waitTime = (int) ((reservation.getEstStartTime().getTime() - Calendar.getInstance().getTimeInMillis()) / (60 * 1000));
						if (waitTime < 0) {
							waitTime = 0;
						}


						Object params[] = { guestName, restName + (null != restLocality ? ", " + restLocality : "") + (null != restCity ? ", " + restCity : ""), reservation.getNumCovers(),
								dateformat.format(new Date(Calendar.getInstance().getTimeInMillis())), timeformat.format(new Date(Calendar.getInstance().getTimeInMillis())), waitTime };

						String sms_message = UtilityMethods.sendSMSFormat(params, Constants.SMS_WAITLIST_MSG);
						// Update Guest Conversation
						GuestConversation conversation = new GuestConversation(reservation, sms_message);
						conversation.setGuid(UtilityMethods.generateCtId());
						conversation.setCreatedBy(reservation.getCreatedBy());
						conversation.setUpdatedBy(reservation.getUpdatedBy());
						conversationService.addConversationAndMsg(conversation, false);
					}
				} catch (Exception e) {
					log.warn("Exception in service", e);
				}

			}
		}
	
		return response;

	}


	@Override
	@Transactional
	public BaseResponse deleteReservation(Map<String,Object> params, String token) {

		BaseResponse response = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		
		Reservation reservation = new Reservation();
		if(params.containsKey(Constants.GUID))
			reservation.setGuid((String) params.get(Constants.GUID));

		reservation = reservationValidator.validateGuid(reservation.getGuid(),
				listOfError);

		if(!listOfError.isEmpty())
		{
			/*listOfError.add(new ValidationError(Constants.GUID, UtilityMethods
					.getErrorMsg(ErrorCodes.INVALID_WAITLIST_GUID),
					ErrorCodes.INVALID_WAITLIST_GUID));*/
			response = new ErrorResponse(
					ResponseCodes.WAITLIST_REMOVE_FAILURE, listOfError);
			return response;
		}

		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		if (!(reservation.getRestaurantGuid().equals(userInfo.getRestGuid()))){
			listOfError.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.REST_FOR_STAFF_NOT_VALID), ErrorCodes.REST_FOR_STAFF_NOT_VALID));
			response = new ErrorResponse(ResponseCodes.WAITLIST_REMOVE_FAILURE, listOfError);
			return response;
		}


		Boolean deleted = deleteWaitlist(reservation.getGuid(), token);
		if (deleted) {

			response = new PostResponse<Reservation>(ResponseCodes.WAITLIST_REMOVED_SUCCESFULLY, reservation.getGuid());

		} else{
			response = new ErrorResponse(
					ResponseCodes.WAITLIST_REMOVE_FAILURE, listOfError);
		}

		return response;

	}


	@Override
	public BaseResponse updateReservationViaSchedular() {
	
		BaseResponse getResponse;
		queueDao.deleteAllQueueReservationBySchedular();
		reservationDao.updateReservationWithShifEndCypherViaSchedular();
		List<Reservation> resvList = new ArrayList<>();
		
		getResponse = new GetResponse<Reservation>(ResponseCodes.RESERVATION_UPDATED_SUCCESFULLY, resvList);
		return getResponse;
	
	}
	
	
	

	
}