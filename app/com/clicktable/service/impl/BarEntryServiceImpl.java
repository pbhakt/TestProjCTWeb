package com.clicktable.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.clicktable.dao.intf.BarEntryDao;
//import com.clicktable.dao.intf.ConversationDao;
import com.clicktable.model.BarEntry;
import com.clicktable.model.GuestConversation;
import com.clicktable.model.Reservation;
import com.clicktable.model.RestSystemConfigModel;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.BarEntryService;
import com.clicktable.service.intf.ConversationService;
import com.clicktable.service.intf.ReservationService;
import com.clicktable.service.intf.RestaurantService;
import com.clicktable.service.intf.TableShuffleService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.BarEntryValidator;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.ReservationValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class BarEntryServiceImpl implements BarEntryService {

	@Autowired
	BarEntryDao barEntryDao;

	@Autowired
	ReservationService reservationService;

	@Autowired
	BarEntryValidator barEntryValidator;

	@Autowired
	AuthorizationService authService;

	@Autowired
	ReservationValidator reservationValidator;

	@Autowired
	RestaurantService restaurantService;
	
	@Autowired
	TableShuffleService shuffleService;
	
	
	@Autowired
	ConversationService conversationService;
	

	@Override
	public BaseResponse getBarEntry(Map<String, Object> params) {
		BaseResponse response;

		Map<String, Object> finderParams = barEntryValidator
				.validateFinderParams(params, BarEntry.class);
		List<BarEntry> barEntries = barEntryDao.findByCustomeFields(
				BarEntry.class, finderParams);
		response = new GetResponse<>(
				ResponseCodes.BARENTRY_FETCHED_SUCCESSFULLY, barEntries);

		return response;
	}

	@Override
	@Transactional
	public BaseResponse addBarEntry(BarEntry barEntry) {
		BaseResponse response;
		String guestName = null;
		String restName =  null;
		String restLocality = null;
		String restCity = null;
		String guestMobile = null;
		
		List<ValidationError> listOfError = barEntryValidator
				.validateBarEntryOnCreate(barEntry);
		if (listOfError.isEmpty()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.REST_GUID, barEntry.getRestaurantGuid());
			GetResponse<RestSystemConfigModel> configResponse = (GetResponse<RestSystemConfigModel>) restaurantService
					.getSystemConfig(params);
			if (configResponse
					.getResponseCode()
					.equals(ResponseCodes.RESTAURANT_SYSTEM_CONFIGURATION_FETCH_SUCCESFULLY)
					&& !configResponse.getList().isEmpty()) {
				RestSystemConfigModel config = configResponse.getList().get(0);
				if (!config.getBar())
					listOfError = CustomValidations
							.populateErrorList(
									listOfError,
									Constants.REST_GUID,
									UtilityMethods
											.getErrorMsg(ErrorCodes.RESTAURANT_NOT_CONFIGURED_FOR_BAR),
									ErrorCodes.RESTAURANT_NOT_CONFIGURED_FOR_BAR);
				else {
					if (config.getBarMaxTime() > 0)
						barEntry.setEndTime(new DateTime(barEntry
								.getStartTime().getTime()).plusMinutes(
								config.getBarMaxTime()).toDate());
					else
						barEntry.setEndTime(new DateTime(barEntry
								.getStartTime().getTime()).plusMinutes(
								Constants.DEFAULT_BAR_MAX_TIME).toDate());
				}
			}
		}
		if (listOfError.isEmpty()) {
			
			
			String guid = barEntryDao.addBarEntry(barEntry);
			
			Map<String, Object> map = barEntryDao.getGuestAndRestDataFromBarEntry(barEntry);
			

			if (null != map){
				guestName = map.get(Constants.NAME).toString();
				restName = map.get(Constants.RESTAURANT_NAME) != null ? map.get(Constants.RESTAURANT_NAME).toString() : null;
				restLocality = map.get(Constants.LOCALITY) != null ? map.get(Constants.LOCALITY).toString() : null;
				restCity = map.get(Constants.CITY) != null ? map.get(Constants.CITY).toString() : null;
				guestMobile = map.get(Constants.MOBILE) != null ? map.get(Constants.MOBILE).toString() : null;
			}
			
			
			try {
				SimpleDateFormat dateformat = new SimpleDateFormat("EEE, d MMM yyyy");
				SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
				Object params[] = { guestName, restName + (null != restLocality ? ", " + restLocality : "") + (null != restCity ? ", " + restCity : ""), barEntry.getNumCovers(),
						dateformat.format(new Date(barEntry.getStartTime().getTime())), timeformat.format(new Date(barEntry.getStartTime().getTime())) };
				String sms_message = UtilityMethods.sendSMSFormat(params, Constants.SMS_ADDED_TO_BAR_MSG);
				// Update Guest Conversation
				GuestConversation conversation = new GuestConversation(barEntry, sms_message);
				conversation.setGuid(UtilityMethods.generateCtId());
				conversation.setCreatedBy(barEntry.getCreatedBy());
				conversation.setUpdatedBy(barEntry.getUpdatedBy());
				conversationService.addConversationAndMsg(conversation, false);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
			response = new PostResponse<BarEntry>(
					ResponseCodes.BARENTRY_CREATED_SUCCESSFULLY, guid);
		} else {
			response = new ErrorResponse(
					ResponseCodes.BARENTRY_CREATION_FAILURE, listOfError);
		}
		return response;
	}

	protected static Map<String, Object> filterBarEntryMap(
			Map<String, Object> inputMap) {
		Map<String, Object> outPutMap = new HashMap<String, Object>();
		List<String> c = new ArrayList<String>();
		c.add(Constants.GUID);
		c.add(Constants.STATUS);
		c.add(Constants.NUMCOVERS);
		c.add(Constants.NOTE);
		Set<String> keys = inputMap.keySet();
		keys.retainAll(c);
		keys.forEach(k -> {
			System.out.println(inputMap.get(k));
			if (inputMap.get(k) != null) {
				outPutMap.put(k, inputMap.get(k));
			}
		});
		return outPutMap;
	}

	@Override
	@Transactional
	public BaseResponse updateBarEntry(Map<String, Object> barEntryMap,
			String token) {
		BaseResponse response;
		/*Integer x = 1/0;*/
		Map<String, Object> filteredBarEntryMap = filterBarEntryMap(barEntryMap);
		List<ValidationError> listOfError = barEntryValidator
				.validateBarEntryOnUpdate(filteredBarEntryMap);

		if (listOfError.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("IST"));
			UserInfoModel userInfo = authService.getUserInfoByToken(token);
			if(userInfo !=null )
				filteredBarEntryMap.put(Constants.UPDATED_BY, userInfo.getGuid());
			if (barEntryMap.containsKey(Constants.STATUS)
					&& barEntryMap.get(Constants.STATUS).toString()
							.equals(Constants.FINISHED))
				filteredBarEntryMap.put(Constants.END_TIME,
						cal.getTimeInMillis());
			filteredBarEntryMap.put(Constants.UPDATED_DATE,
					cal.getTimeInMillis());
			BarEntry barEntry = barEntryDao.update(filteredBarEntryMap);
			response = new UpdateResponse<BarEntry>(
					ResponseCodes.BARENTRY_UPDATED_SUCCESSFULLY,
					barEntry.getGuid());
		} else {
			response = new ErrorResponse(
					ResponseCodes.BARENTRY_UPDATION_FAILURE, listOfError);
		}
		return response;
	}

	@Override
	@Transactional
	public BaseResponse moveToRestaurant(Map<String, Object> dataMap,
			String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Reservation reservation = barEntryValidator.validateMoveToRestaurantData(dataMap, listOfError);

		if (!listOfError.isEmpty()) {
			response = new ErrorResponse(ResponseCodes.BARENTRY_UPDATION_FAILURE, listOfError);
			return response;
		}

		
		reservation.setBookingMode(Constants.WALKIN_STATUS);
/*		reservation.setEstStartTime(new Date());*/
		reservation.setReservationStatus(Constants.SEATED);
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		reservation.setRestaurantGuid(userInfo.getRestGuid());
		reservation.setIsUnknown(false);

		reservation.setEstEndTime(new Date(reservation.getEstStartTime().getTime() + Long.valueOf(reservation.getTat())*60*1000));
		
		Boolean isShufflePossible = shuffleService.shuffleTablesMethod(reservation, Constants.SEATED);

		BaseResponse resvTableResponse = new BaseResponse();
		if(!isShufflePossible){
			resvTableResponse = reservationService.getReservationsForTables(reservation);
		}

		if (resvTableResponse instanceof GetResponse) {
			List resultList = ((GetResponse) resvTableResponse).getList();
			if (resultList.size() > 0) {
				response = new ErrorResponse(ResponseCodes.RESERVATION_UPDATION_FAILURE_DUE_TO_SEATED, resultList);
				return response;

			}
		}

		response = logicForMoveToRestaurant(reservation,dataMap, token);
		
		return response;
	}
	
	
	private BaseResponse logicForMoveToRestaurant( Reservation reservation,Map<String,Object> dataMap, String token)
	{
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		
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

		reservation.setInfoOnCreate(authService.getUserInfoByToken(token));
		reservation.setGuestGuid(updatedBarEntry.getGuestGuid());
	
		response = reservationService.createReservation(reservation, token,true);
		
		
		if(response instanceof ErrorResponse)
		{
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();	
			return response;
		}
		
		if (response.getResponseCode().equals(
				ResponseCodes.RESERVATION_ADDED_SUCCESFULLY)) {	
			System.out.println("In patch reservation");
			Reservation patchReservation = new Reservation();
			patchReservation.setGuid(reservation.getGuid());
			patchReservation.setReservationStatus(Constants.SEATED);
			response = reservationService.patchReservation(patchReservation, token);
		}

		if(!response.getResponseStatus())
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();	

		return response;


	}

	@Override
	@Transactional
	public BaseResponse moveFromWaitlist(Reservation waiting, String token) {
		BaseResponse response = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		waiting = reservationValidator.validateGuid(waiting.getGuid(),
				listOfError);
		if (listOfError.isEmpty() && waiting.isQueued()) {
			//waiting.setReservationStatus(Constants.MOVED_TO_BAR);
			//BaseResponse patchResponse = reservationService.patchReservation(
				//	waiting, token);
			Boolean deleted = reservationService.deleteWaitlist(waiting.getGuid(), token);
			if (deleted) {
				BarEntry barEntry = new BarEntry();
				barEntry.setInfoOnCreate(authService.getUserInfoByToken(token));
				barEntry.setStatus(Constants.CREATED);
				barEntry.setGuestGuid(waiting.getGuestGuid());
				barEntry.setNumCovers(waiting.getNumCovers());
				barEntry.setRestaurantGuid(waiting.getRestaurantGuid());
				response = addBarEntry(barEntry);
				if(!response.getResponseStatus()){
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				}
			} else{
				response = new ErrorResponse(
						ResponseCodes.WAITLIST_REMOVE_FAILURE, listOfError);
			}
		} else {
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods
					.getErrorMsg(ErrorCodes.INVALID_WAITLIST_GUID),
					ErrorCodes.INVALID_WAITLIST_GUID));
			response = new ErrorResponse(
					ResponseCodes.BARENTRY_CREATION_FAILURE, listOfError);
		}
		
		return response;
	}
	
	
	

	

}
