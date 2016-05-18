package com.clicktable.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import play.Logger;
import play.i18n.Messages;

import com.clicktable.config.StormpathConfig;
import com.clicktable.dao.intf.AccountValuesDao;
import com.clicktable.dao.intf.AddressDao;
import com.clicktable.dao.intf.AttributeDao;
import com.clicktable.dao.intf.CalculatedTatDao;
import com.clicktable.dao.intf.DayOfWeekDao;
import com.clicktable.dao.intf.HistoricalTatDao;
import com.clicktable.dao.intf.NumberOfCoversDao;
import com.clicktable.dao.intf.ParentAccountDao;
import com.clicktable.dao.intf.QueueDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.SectionDao;
import com.clicktable.dao.intf.StaffDao;
import com.clicktable.dao.intf.StaffInfoDao;
import com.clicktable.model.AccountIdUnique;
import com.clicktable.model.BlackOutHours;
import com.clicktable.model.CalculatedTat;
import com.clicktable.model.DayOfWeek;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.HistoricalTat;
import com.clicktable.model.HistoricalTatResult;
import com.clicktable.model.NumberOfCovers;
import com.clicktable.model.Onboarding;
import com.clicktable.model.OperationalHours;
import com.clicktable.model.ParentAccount;
import com.clicktable.model.Queue;
import com.clicktable.model.Reservation;
import com.clicktable.model.RestSystemConfigModel;
import com.clicktable.model.Restaurant;
import com.clicktable.model.RestaurantAddress;
import com.clicktable.model.RestaurantContactInfo;
import com.clicktable.model.RestaurantContactInfoAdmin;
import com.clicktable.model.RestaurantGeneralInfo;
import com.clicktable.model.Section;
import com.clicktable.model.Staff;
import com.clicktable.model.StaffInfo;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CustomerLoginService;
import com.clicktable.service.intf.RestaurantService;
import com.clicktable.service.intf.StaffService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.SectionValidator;
import com.clicktable.validate.StaffValidator;
import com.clicktable.validate.ValidationError;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.client.Client;

@org.springframework.stereotype.Service
public class RestaurantServiceImpl implements RestaurantService {

	@Autowired
	RestaurantDao restDao;

	@Autowired
	SectionDao sectionDao;

	@Autowired
	StaffDao staffDao;
	
	@Autowired
	StaffInfoDao staffInfoDao;

	@Autowired
	AddressDao addressDao;

	@Autowired
	ParentAccountDao accountDao;

	@Autowired
	AttributeDao attributeDao;

	@Autowired
	HistoricalTatDao histDao;

	@Autowired
	DayOfWeekDao dayDao;

	@Autowired
	NumberOfCoversDao coversDao;

	@Autowired
	CalculatedTatDao calTatDao;

	@Autowired
	SectionValidator sectionValidator;

	@Autowired
	RestaurantValidator validateRestObject;

	@Autowired
	StaffValidator validateStaffObject;

	@Autowired
	StaffService staffService;

	@Autowired
	AuthorizationService authorizationService;

	@Autowired
	QueueDao queueDao;

	@Autowired
	CustomerLoginService guestService;

	@Autowired
	AccountValuesDao accValuesDao;
	
	
	@Autowired
	ReservationDao resvDao;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse addRestaurant(Restaurant rest, Staff staff, String token) {

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);

		String restId = UtilityMethods.generateCtId();
		rest.setGuid(restId);
		staff.setGuid(UtilityMethods.generateCtId());
		staff.setRestaurantGuid(restId);
		staff.setLanguageCode(rest.getLanguageCode());

		rest.setStatus(Constants.ACTIVE_STATUS);
		staff.setStatus(Constants.ACTIVE_STATUS);
		staff.setRoleId(Constants.ADMIN_ROLE_ID);

		String accountId = "", parentAccountId = "";

		List<ValidationError> listOfErrorForRestaurant = validateRestObject.validateRestaurantOnAdd(rest);

		BaseResponse response;

		if (listOfErrorForRestaurant.isEmpty()) {
			// set account and parent account id
			accountId = rest.getCountryCode() + getAccountShortId(userInfo);
			parentAccountId = "P-" + accountId;

			rest.setAccountId(accountId);
			rest.setParentAccountId(parentAccountId);

			Restaurant restaurant = restDao.create(rest);
			// System.out.println("Restaurant created with id "+restaurant.getId());
			staff.setRestaurantGuid(restaurant.getGuid());
			List<ValidationError> listOfErrorForStaff = validateStaffObject.validateStaffOnAdd(staff);
			if (listOfErrorForStaff.isEmpty()) {
				// add staff to stormpath within try catch so that if exception
				// arises in adding user to storm path
				// restaurant is deleted from database and response for failure
				// is
				// send
				try {
					staff.setHref(staffService.addStaffToStormPath(staff));
					Staff newStaff = staffDao.create(staff);

					Logger.debug("staff created");
					// if restaurant guid is not null then create relationship
					// of restaurant and staff
					if ((staff.getRestaurantGuid() != null) && (!staff.getRestaurantGuid().equals(""))) {
						Long id = staffDao.addRestaurantStaff(rest.getGuid(), newStaff);
						Logger.debug("relationship id is " + id);
					}
					
					
					
					StaffInfo staffInfo = new StaffInfo();
					staffInfo.setIs_otp_require(staff.isIs_otp_require());
					staffInfo.setStaffGuid(newStaff.getGuid());
					try{
						staffInfo = staffInfoDao.addStaffInfo(staffInfo);
					}catch(Exception e){
						Logger.debug("Exception is----------------" + e);
						
					}
							
					
					if(staffInfo == null || staffInfo.getGuid() ==null)
					{
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();	
						Client client = StormpathConfig.getInstance().getClient();
						client.getResource(Constants.STORMPATH_HREF_PATH_STRING + newStaff.getHref(), Account.class).delete();

						listOfErrorForStaff.add(new ValidationError(Constants.STAFF_INFO, UtilityMethods.getErrorMsg(ErrorCodes.STAFF_INFO_CREATION_FAILURE), ErrorCodes.STAFF_INFO_CREATION_FAILURE));
						response = new ErrorResponse(ResponseCodes.STAFF_ADDED_FAILURE, listOfErrorForStaff);
						return response;
					}
					
					

					// if relationship is to be created with creation of staff
					// admin

					/*
					 * response = staffService.addStaffMember(staff, token);
					 * 
					 * 
					 * Logger.debug("response instanceof ErrorResponse==========="
					 * +response.getResponseMessage());
					 * 
					 * if(!response.getResponseStatus()) { throw new
					 * Exception(); }
					 */

					// create relationship of address with rest
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(Constants.ADDRESS_LINE_1, restaurant.getAddressLine1());
					params.put(Constants.ADDRESS_LINE_2, restaurant.getAddressLine2());
					params.put(Constants.CITY, restaurant.getCity());
					params.put(Constants.COUNTRY, restaurant.getCountryCode());
					params.put(Constants.STATE, restaurant.getState());
					params.put(Constants.LOCALITY, restaurant.getLocality());
					if (restaurant.getBuilding() != null) {
						params.put(Constants.BUILDING, restaurant.getBuilding());
					}
					params.put(Constants.ZIPCODE, restaurant.getZipcode());

					List<RestaurantAddress> addressList = addressDao.findByFields(RestaurantAddress.class, params);
					Logger.debug("address list is " + addressList);
					RestaurantAddress address;
					if (addressList.size() > 0) {
						address = addressList.get(0);
					} else {
						address = new RestaurantAddress(restaurant);
						address.setGuid(UtilityMethods.generateCtId());
						addressDao.create(address);
						Logger.debug("address created");
					}

					/*
					 * HasAddress relationModel =
					 * address.addRelationTag(restaurant, address);
					 * Logger.debug("Before creating " + relationModel.getId());
					 * restDao.saveRelationModel(relationModel);
					 * Logger.debug("relationship created " +
					 * relationModel.getId());
					 */

					Long id = restDao.addRestaurantAddress(rest, address);
					Logger.debug("address relationship id is " + id);

					ParentAccount account = new ParentAccount(restaurant);
					account.setGuid(UtilityMethods.generateCtId());

					accountDao.create(account);
					Logger.debug("Parent account created");

					id = restDao.addRestaurantAccount(restaurant, account);
					
					Logger.debug("account relationship id is " + id);
					
					List<Queue> queueList = new ArrayList<>();
					Queue queue;
					for (int i = 0; i < 9; i++) {
						queue = new Queue();
						queue.setInfoOnCreate(userInfo);
						queue = queueDao.create(queue);
						queueList.add(queue);
					}

					Boolean queueAdded = queueDao.addQueue(queueList, restaurant);

					Logger.debug("Queue added :::::::::::::::::::::::::::::::::::::::" + queueAdded);

					response = new PostResponse<Restaurant>(ResponseCodes.RESTAURANT_ADDED_SUCCESFULLY, restaurant.getGuid());
					//throw new Exception();
				} catch (Exception e) {
					// code to delete restaurant
					restDao.delete(Restaurant.class, restaurant.getId());
					Logger.debug("restaurant deleted");
					listOfErrorForRestaurant.add(new ValidationError(Constants.STORMPATH_MODULE, e.getMessage()));
					response = new ErrorResponse(ResponseCodes.RESTAURANT_ADDED_FAILURE, listOfErrorForRestaurant);
				}

			} else {
				// code to delete restaurant
				restDao.delete(Restaurant.class, restaurant.getId());
				Logger.debug("restaurant deleted");
				response = new ErrorResponse(ResponseCodes.RESTAURANT_ADDED_FAILURE, listOfErrorForStaff);
			}

		} else {

			response = new ErrorResponse(ResponseCodes.RESTAURANT_ADDED_FAILURE, listOfErrorForRestaurant);
		}

		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public BaseResponse getRestaurants(Map<String, Object> params) {
		BaseResponse getResponse;
		Map<String, Object> qryParamMap = validateRestObject.validateFinderParams(params, Restaurant.class);
		List<Restaurant> restList = restDao.findByFields(Restaurant.class, qryParamMap);
		getResponse = new GetResponse<Restaurant>(ResponseCodes.RESTAURANT_RECORD_FETCH_SUCCESFULLY, restList);
		return getResponse;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse updateRestaurant(Restaurant rest, String token) {
		BaseResponse response;

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		rest.setUpdatedBy(userInfo.getGuid());

		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		if (rest.getGuid() == null)
			listOfError.add(validateRestObject.createError(Constants.GUID, ErrorCodes.REST_ID_REQUIRED));
		else {
			// /if role is admin then check for restaurant(admin can change
			// details of his own restaurant)
			if (userInfo.getRoleId().equals(Constants.ADMIN_ROLE_ID) && (!rest.getGuid().equals(userInfo.getRestGuid()))) {
				listOfError.add(validateRestObject.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
				response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
				return response;
			}

			Restaurant restaurant = restDao.find(rest.getGuid());
			if (restaurant == null)
				listOfError.add(validateRestObject.createError(Constants.GUID, ErrorCodes.INVALID_REST_ID));
			else {
				rest.copyExistingValues(restaurant);
				listOfError.addAll(validateRestObject.validateRestaurantOnUpdate(rest));
			}
		}

		if (listOfError.isEmpty()) {
			Restaurant restaurant = restDao.update(rest);
			response = new UpdateResponse<Restaurant>(ResponseCodes.RESTAURANT_UPDATED_SUCCESFULLY, restaurant.getGuid());
		} else {
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
		}
		return response;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse updateRestaurantGeneralInfo(RestaurantGeneralInfo restGeneralInfo, String token) {
		BaseResponse response;
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		Restaurant restaurant = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		if (null != restGeneralInfo.getGuid()) {

			restaurant = restDao.find(restGeneralInfo.getGuid());
			if (null == restaurant) {
				listOfError.add(validateRestObject.createError(Constants.GUID, ErrorCodes.INVALID_REST_ID));
			} else {

				if ((userInfo.getRoleId().equals(Constants.ADMIN_ROLE_ID)) && (!restaurant.getGuid().equals(userInfo.getRestGuid()))) {
					listOfError.add(validateRestObject.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
					response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
					return response;
				}
				listOfError.addAll(validateRestObject.validateRestaurantGeneralInfoOnUpdate(restGeneralInfo));
			}
			restGeneralInfo.setUpdatedBy(userInfo.getGuid());

		} else {
			listOfError.add(validateRestObject.createError(Constants.GUID, ErrorCodes.REST_ID_REQUIRED));
		}

		if (listOfError.isEmpty()) {
			Map<String, Object> objectAsMap = UtilityMethods.entityConversionToMap(restGeneralInfo);
			String rest = restDao.updateRestaurantGeneralInfo(restGeneralInfo, objectAsMap);
			response = new UpdateResponse<Restaurant>(ResponseCodes.RESTAURANT_UPDATED_SUCCESFULLY, rest);
		} else {
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
		}
		return response;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse updateContactInfo(RestaurantContactInfo contactInfo, String token) {
		BaseResponse response;

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		contactInfo.setUpdatedBy(userInfo.getGuid());

		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		if (contactInfo.getGuid() == null)
			listOfError.add(validateRestObject.createError(Constants.GUID, ErrorCodes.REST_ID_REQUIRED));
		else {
			// /if role is admin then check for restaurant(admin can change
			// details of his own restaurant)
			if ((userInfo.getRoleId().equals(Constants.ADMIN_ROLE_ID)) && (!contactInfo.getGuid().equals(userInfo.getRestGuid()))) {
				listOfError.add(validateRestObject.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
				response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
				return response;
			}

			Restaurant restaurant = restDao.find(contactInfo.getGuid());
			if (restaurant == null)
				listOfError.add(validateRestObject.createError(Constants.GUID, ErrorCodes.INVALID_REST_ID));
			else {
				contactInfo.copyExistingValues(restaurant);
				listOfError.addAll(validateRestObject.validateRestaurantContactInfo(contactInfo));
			}
		}

		if (listOfError.isEmpty()) {
			Restaurant restaurant = restDao.updateContactInfo(contactInfo);
			response = new UpdateResponse<Restaurant>(ResponseCodes.RESTAURANT_UPDATED_SUCCESFULLY, restaurant.getGuid());
		} else {
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
		}
		return response;

	}

	@Override
	public BaseResponse addRestaurant(Onboarding onboard, String token) {
		Restaurant restaurant = new Restaurant(onboard);
		Staff staff = new Staff(onboard);
		restaurant.setStatus(Constants.ACTIVE_STATUS);
		staff.setStatus(Constants.ACTIVE_STATUS);
		return addRestaurant(restaurant, staff, token);
	}

	/**
	 * Method to add attributes Permitted to admin and ct_admin
	 */
	@Override
	@Transactional
	public BaseResponse addAttributes(String restGuid, String attrGuid, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if ((restGuid == null) || restGuid.equals(""))
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
		if ((attrGuid == null) || attrGuid.equals(""))
			listOfError.add(validateRestObject.createError(Constants.ATTR_GUID, ErrorCodes.ATTR_ID_REQUIRED));

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		Logger.debug("role id is " + userInfo.getRoleId() + " rest guid is " + userInfo.getRestGuid());
		// /if role is admin then check for restaurant(admin can change details
		// of his own restaurant)
		if ((userInfo.getRoleId().equals(Constants.ADMIN_ROLE_ID)) && (!restGuid.equals(userInfo.getRestGuid()))) {
			listOfError.add(validateRestObject.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
			return response;
		}

		Restaurant restaurant = restDao.find(restGuid);
		if (restaurant == null)
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.INVALID_REST_ID));

		String[] attrGuidArr = attrGuid.split(",");

		if (listOfError.isEmpty()) {
			boolean created = attributeDao.addRestaurantAttributes(restGuid, attrGuidArr);
			Logger.debug("query result" + created);
			response = new UpdateResponse<Restaurant>(ResponseCodes.RESTAURANT_ATTRIBUTES_UPDATED_SUCCESFULLY, restaurant.getGuid());
		} else {
			response = new ErrorResponse(ResponseCodes.RESTAURANT_ATTRIBUTES_UPDATION_FAILURE, listOfError);
		}

		return response;

	}

	/**
	 * Method to add system config(tat and waitlist time etc) Permitted to admin
	 * and ct_admin
	 */
	@Override
	@Transactional
	public BaseResponse addSystemConfig(RestSystemConfigModel rest, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);

		// Rest guid is missing
		if ((rest.getRestaurantGuid() == null) || (rest.getRestaurantGuid().equals(""))) {
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
			return response;
		}

		// /if role is admin then check for restaurant(admin can change details
		// of his own restaurant)
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID) && !userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) && (!rest.getRestaurantGuid().equals(userInfo.getRestGuid()))) {
			listOfError.add(validateRestObject.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
			return response;

		}

		listOfError.addAll(validateRestObject.validateRestaurantSystemConfigModelOnAdd(rest));
		if (!listOfError.isEmpty()) {
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
			return response;
		}

		boolean created = restDao.addSystemConfig(rest, token);
		Logger.debug("query result" + created);

		HistoricalTat hist = restDao.findHistoricalTatForRest(rest.getRestaurantGuid());
		Logger.debug("Historical tat is " + hist);
		if (hist == null) {
			Logger.debug("Updating historical tat");
			addHistoricalTatData(rest);
		} else {
			updateHistoricalTatData(rest);
		}

		if (created) {
			response = new UpdateResponse<Restaurant>(ResponseCodes.RESTAURANT_UPDATED_SUCCESFULLY, rest.getRestaurantGuid());
		} else {
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
		}

		return response;

	}

	/**
	 * Method to get system config(tat and waitlist time etc)
	 */
	@Override
	@Transactional(readOnly = true)
	public BaseResponse getSystemConfig(Map<String, Object> params) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if ((params.get(Constants.REST_GUID) == null) || params.get(Constants.REST_GUID).equals(""))
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
		if (listOfError.isEmpty()) {
			Iterator<Map<String, Object>> itr = restDao.getSystemConfig(params);
			Integer reserveReleaseTime = 0, bufferOpenTime = 0, dinningSlotInterval = 0, waitlistReleaseTime = 0, reserveOverlapTime = 0, barMaxTime = 0;
			Boolean bar = false;
			String otpMobile = "";
			String forcedShiftEndTime = "";
			RestSystemConfigModel rest = new RestSystemConfigModel();
			System.out.println(itr.hasNext());
			while (itr.hasNext()) {
				Set<Entry<String, Object>> entrySet = itr.next().entrySet();
				Logger.debug("entry set is " + entrySet);
				String name = "";
				Integer value = 0;
				Integer familyTat = 0;
				for (Map.Entry<String, Object> entry : entrySet) {
					Logger.debug("entry is " + entry);
					if (entry.getKey().contains(Constants.NAME)) {
						name = (String) entry.getValue();
					}

					if (entry.getKey().contains(Constants.VALUE)) {
						value = (Integer) entry.getValue();
					}
					
					if (entry.getKey().contains(Constants.FAMILY_TAT)) {
						familyTat = (Integer) entry.getValue();
					}

					if (entry.getKey().contains(Constants.RESERVE_RELEASE_TIME)) {
						Logger.debug("result is " + entry.getKey() + " value====" + entry.getValue());
						if (entry.getValue() != null) {
							reserveReleaseTime = Integer.parseInt(entry.getValue().toString());
						}
					}
					if (entry.getKey().contains(Constants.RESERVE_OVERLAP_TIME)) {
						Logger.debug("result is " + entry.getKey() + " value====" + entry.getValue());
						if (entry.getValue() != null) {
							reserveOverlapTime = Integer.parseInt((String) entry.getValue().toString());
						}
					}
					if (entry.getKey().contains(Constants.WAITLIST_RELEASE_TIME)) {
						Logger.debug("result is " + entry.getKey() + " value====" + entry.getValue());
						if (entry.getValue() != null) {
							waitlistReleaseTime = Integer.parseInt((String) entry.getValue().toString());
						}
					}
					if (entry.getKey().contains(Constants.DINING_SLOT_INTERVAL)) {
						Logger.debug("result is " + entry.getKey() + " value====" + entry.getValue());
						if (entry.getValue() != null) {
							dinningSlotInterval = Integer.parseInt((String) entry.getValue().toString());
						}
					}
					if (entry.getKey().contains(Constants.OTP_MOBILE)) {
						Logger.debug("result is " + entry.getKey() + " value====" + entry.getValue());
						otpMobile = (String) entry.getValue();
					}
					if (entry.getKey().contains(Constants.FORCED_SHIFT_END_TIME)) {
						Logger.debug("result is " + entry.getKey() + " value====" + entry.getValue());
						forcedShiftEndTime = (String) entry.getValue();
					}
					if (entry.getKey().contains(Constants.BUFFER_OPEN_TIME)) {
						Logger.debug("result is " + entry.getKey() + " value====" + entry.getValue());
						if (entry.getValue() != null) {
							bufferOpenTime = Integer.parseInt((String) entry.getValue().toString());
						}
					}

					if (entry.getKey().contains(Constants.BAR)) {
						Logger.debug("result is " + entry.getKey() + " value====" + entry.getValue());
						if (entry.getKey().contains(Constants.BAR_MAX_TIME_DB)) {
							Logger.debug("result is " + entry.getKey() + " value====" + entry.getValue());
							if (entry.getValue() != null) {
								barMaxTime = (Integer) entry.getValue();
							}
						}else 
						if (entry.getValue() != null) {
							bar = (Boolean) entry.getValue();
						}
					}
					
				}

				switch (name) {
				case Constants.TAT_WD_12:
					rest.setTat_wd_12(value);
					rest.setFamily_tat_wd_12(familyTat);
					break;
				case Constants.TAT_WE_12:
					rest.setTat_we_12(value);
					rest.setFamily_tat_we_12(familyTat);
					break;
				case Constants.TAT_WD_34:
					rest.setTat_wd_34(value);
					rest.setFamily_tat_wd_34(familyTat);
					break;
				case Constants.TAT_WE_34:
					rest.setTat_we_34(value);
					rest.setFamily_tat_we_34(familyTat);
					break;
				case Constants.TAT_WD_56:
					rest.setTat_wd_56(value);
					rest.setFamily_tat_wd_56(familyTat);
					break;
				case Constants.TAT_WE_56:
					rest.setTat_we_56(value);
					rest.setFamily_tat_we_56(familyTat);
					break;
				case Constants.TAT_WD_78:
					rest.setTat_wd_78(value);
					rest.setFamily_tat_wd_78(familyTat);
					break;
				case Constants.TAT_WE_78:
					rest.setTat_we_78(value);
					rest.setFamily_tat_we_78(familyTat);
					break;
				case Constants.TAT_WD_8P:
					rest.setTat_wd_8P(value);
					rest.setFamily_tat_wd_8P(familyTat);
					break;
				case Constants.TAT_WE_8P:
					rest.setTat_we_8P(value);
					rest.setFamily_tat_we_8P(familyTat);
					break;
				default:
					break;
				}
			}
			rest.setDiningSlotInterval(dinningSlotInterval);
			rest.setReserveOverlapTime(reserveOverlapTime);
			rest.setReserveReleaseTime(reserveReleaseTime);
			rest.setWaitlistReleaseTime(waitlistReleaseTime);
			rest.setBufferOpenTime(bufferOpenTime);
			rest.setBar(bar);
			rest.setBarMaxTime(barMaxTime);
			if (forcedShiftEndTime != null && forcedShiftEndTime.trim().length() > 0) {
				Date dt = new Date(Long.parseLong(forcedShiftEndTime));
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_FORMAT_CONFIGURATION);
				String time1 = sdf.format(dt);
				rest.setForcedShiftEndTime(time1);
			} else {
				rest.setForcedShiftEndTime(null);
			}
			rest.setOtpMobile(otpMobile);
			rest.setRestaurantGuid(params.get(Constants.REST_GUID).toString());

			List<RestSystemConfigModel> restTatList = new ArrayList<>();
			restTatList.add(rest);
			response = new GetResponse<>(ResponseCodes.RESTAURANT_SYSTEM_CONFIGURATION_FETCH_SUCCESFULLY, restTatList);
		} else {
			response = new ErrorResponse(ResponseCodes.RESTAURANT_SYSTEM_CONFIGURATION_FETCH_FAILURE, listOfError);
		}
		return response;

	}

	/**
	 * Method to create relationship of restaurant and section Permitted to
	 * admin and ct_admin
	 */
	@Override
	@Transactional
	public BaseResponse addSection(String restGuid, String sectionGuid, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if ((restGuid == null) || restGuid.equals(""))
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
		if ((sectionGuid == null) || sectionGuid.equals(""))
			listOfError.add(validateRestObject.createError(Constants.SECTION_GUID, ErrorCodes.SECTION_ID_REQUIRED));

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		// /if role is admin then check for restaurant(admin can change details
		// of his own restaurant)
		if ((userInfo.getRoleId().equals(Constants.ADMIN_ROLE_ID)) && (!restGuid.equals(userInfo.getRestGuid()))) {
			listOfError.add(validateRestObject.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
			return response;
		}

		Restaurant restaurant = restDao.find(restGuid);
		if (restaurant == null)
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.INVALID_REST_ID));

		if (listOfError.isEmpty()) {
			boolean created = restDao.addRestaurantSection(restGuid, sectionGuid);
			Logger.debug("query result" + created);
			response = new UpdateResponse<Restaurant>(ResponseCodes.SECTION_ADDED_SUCCESFULLY, sectionGuid);
		} else {
			response = new ErrorResponse(ResponseCodes.SECTION_ADDED_FAILURE, listOfError);
		}

		return response;

	}

	/**
	 * Method to delete section Permitted to admin and ct_admin
	 */
	@Override
	@Transactional
	public BaseResponse deleteSection(String restGuid, String sectionGuid, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if ((restGuid == null) || restGuid.equals(""))
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
		if ((sectionGuid == null) || sectionGuid.equals(""))
			listOfError.add(validateRestObject.createError(Constants.SECTION_GUID, ErrorCodes.SECTION_ID_REQUIRED));

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		// /if role is admin then check for restaurant(admin can change details
		// of his own restaurant)
		if ((userInfo.getRoleId().equals(Constants.ADMIN_ROLE_ID)) && (!restGuid.equals(userInfo.getRestGuid()))) {
			listOfError.add(validateRestObject.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
			return response;
		}

		Restaurant restaurant = restDao.find(restGuid);
		if (restaurant == null)
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.INVALID_REST_ID));

		if (listOfError.isEmpty()) {
			boolean created = restDao.deleteRestaurantSection(restGuid, sectionGuid);
			Logger.debug("query result" + created);
			response = new UpdateResponse<Restaurant>(ResponseCodes.SECTION_DELETED_SUCCESFULLY, sectionGuid);
		} else {
			response = new ErrorResponse(ResponseCodes.SECTION_DELETED_FAILURE, listOfError);
		}

		return response;

	}

	@Override
	public BaseResponse getRestaurantsSection(Map<String, Object> stringParamMap) {
		// TODO Auto-generated method stub
		BaseResponse getResponse;
		Map<String, Object> qryParamMap = sectionValidator.validateFinderParams(stringParamMap, Section.class);
		List<Section> restList = sectionDao.findByFields(Section.class, qryParamMap);
		getResponse = new GetResponse<Section>(ResponseCodes.SECTION_RECORD_FETCH_SUCCESFULLY, restList);
		return getResponse;
	}

	/**
	 * Method to add Operational Hours for a Restaurant Permitted to admin and
	 * ct_admin
	 */

	@Transactional
	public BaseResponse addOperationalHours(OperationalHours ophr, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		// if access token is not of CT-ADMIN, get the RestaurantId using token
		// and set in ophr.
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID) && !userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			ophr.setRestGuid(userInfo.getRestGuid());
		}

		if (null == ophr.getRestGuid()) {
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
		}

		if (listOfError.isEmpty() && (ophr.getDiningSlot() == 0 || ophr.getDiningSlot() < 0)) {
			if (ophr.getDiningSlot() == 0)
				listOfError.add(validateRestObject.createError(Constants.DINING_SLOT_INTERVAL, ErrorCodes.DINING_SLOT_INTERVAL_MISSING));
			if (ophr.getDiningSlot() < 0)
				listOfError.add(validateRestObject.createError(Constants.INVALID_DINING_SLOT_INTERVAL, ErrorCodes.INVALID_DINING_SLOT_INTERVAL));
		}
		// validate the operational Hours (Need to work on it )
		Map params=new HashMap<String,Object>();
		params.put(Constants.REST_GUID, ophr.getRestGuid());
		OperationalHours existing_op_hr = restDao.getOperationalHours(params);
		if (listOfError.isEmpty()) {
			
			  Calendar c1 = Calendar.getInstance();			  
			  Calendar c2 = Calendar.getInstance();
			  c2.add(Calendar.MONTH, 2);
			  c2.add(Calendar.DATE, 1);
			  
			  Long startTime = c1.getTimeInMillis();
			  Long endTime = c2.getTimeInMillis();
			  
			  SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT); 
			  
			  try {
				endTime = sdf.parse(sdf.format(new java.util.Date(c2.getTimeInMillis()))).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			  
			  Map<String,Object> paramMap = new HashMap<>();
			  paramMap.put("startTime", startTime);
			  paramMap.put("endTime", endTime);
			  paramMap.put(Constants.REST_GUID, ophr.getRestGuid());
			  
			  List<Reservation> resvList = resvDao.getReservationsForTime(paramMap);
			  List<String> resvGuidList = new ArrayList<>();
			  
			  List<Map<String, Long>> resultList = validateRestObject.validateOperationalHours(ophr,existing_op_hr,listOfError);
		if(!resultList.isEmpty())
			{
				
				for(Reservation resv : resvList)
				{
					for(Map<String,Long> map : resultList)
					{
						if(resv.getEstStartTime().getTime() >= map.get("estStartTime") &&  resv.getEstStartTime().getTime() < map.get("estEndTime"))
						{
							resvGuidList.add(resv.getGuid());
						}
					}

				}
				
				
				
				if(!resvGuidList.isEmpty())
				{
					paramMap.put("resvGuidList", resvGuidList);
					List reservationList = resvDao.getReservationDetailsByGuid(paramMap);
					if(reservationList.size() > 0)
					{
						response = new ErrorResponse(ResponseCodes.OPHR_UPDATION_FAILURE_DUE_TO_RESV, reservationList);
						return response;
					}
					
				}
				
				/*for(Entry<String,List<Reservation>> entry : resultMap.entrySet() )
				{
					String day = entry.getKey();
					List resvList = entry.getValue();
					if(resvList.size() > 0)
					{
						response = new ErrorResponse(ResponseCodes.OPHR_UPDATION_FAILURE_DUE_TO_RESV, resvList);
						response.setResponseMessage(Messages.get(ResponseCodes.OPHR_UPDATION_FAILURE_DUE_TO_RESV, day));
						return response;
					}
				}*/
			}
		
			

			if (listOfError.isEmpty()) {
				boolean created = restDao.addOperationalHours(ophr);
				Logger.debug("Operational Hours Addition Status ::::::: " + created);
				if (created) {
					response = new UpdateResponse<Restaurant>(ResponseCodes.OPERATIONAL_HOUR_RECORD_ADDED_SUCCESS, ophr.getRestGuid());
				} else {
					response = new ErrorResponse(ResponseCodes.OPERATIONAL_HOUR_RECORD_ADDED_FAILURE, listOfError);
				}

			} else {
				response = new ErrorResponse(ResponseCodes.OPERATIONAL_HOUR_RECORD_ADDED_FAILURE, listOfError);
			}
		} else {
			response = new ErrorResponse(ResponseCodes.OPERATIONAL_HOUR_RECORD_ADDED_FAILURE, listOfError);
		}

		return response;

	}

	// private method to add historical tat data for rest
	private void addHistoricalTatData(RestSystemConfigModel rest) {
		// add historicaltat
		HistoricalTat histTat = new HistoricalTat(rest.getRestaurantGuid());
		histTat = histDao.create(histTat);

		// create hist tat and rest relationship
		Long id = histDao.addRestaurantHistoricalTat(histTat, rest.getRestaurantGuid());
		Logger.debug("Relationship of hist tat and rest created.id is " + id);

		// create day of week and create relationship of day of week and hist
		// tat
		List<DayOfWeek> dayList = new ArrayList<>();
		StringBuilder dayOfWeekGuids = new StringBuilder();
		for (int i = 0; i < 7; i++) {

			DayOfWeek day = new DayOfWeek(i);
			day = dayDao.create(day);
			dayList.add(day);
			Logger.debug("day created guid is " + day.getGuid() + " id is " + day.getId());
			dayOfWeekGuids.append(day.getGuid() + ",");

		}

		String dayOfWeekGuidsStr = null;
		if (dayOfWeekGuids.length() > 0) {
			dayOfWeekGuidsStr = dayOfWeekGuids.substring(0, dayOfWeekGuids.length() - 1);
		}

		Logger.debug("day of week guids are " + dayOfWeekGuids);

		dayDao.addDayOfWeek(histTat, dayOfWeekGuidsStr);

		// get all tats for restaurant
		Map<String, Object> param = new HashMap<>();
		param.put(Constants.REST_GUID, rest.getRestaurantGuid());
		List<RestSystemConfigModel> tatList = ((GetResponse) getSystemConfig(param)).getList();

		RestSystemConfigModel config = new RestSystemConfigModel();
		if (tatList.size() > 0) {
			config = tatList.get(0);
		}

		// create number of covers nodes and add relationship with day of week
		// with tat values
		List<NumberOfCovers> coversList = new ArrayList<>();
		NumberOfCovers cover = null;
		int tatValue = 0;
		CalculatedTat calTat;
		List<CalculatedTat> calTatList;
		for (DayOfWeek day : dayList) {

			for (int j = 0; j < Constants.MAX_ALLOWED_COVERS; j++) {
				calTatList = new ArrayList<>();
				cover = new NumberOfCovers(j + 1);
				cover = coversDao.create(cover);
				coversList.add(cover);

				tatValue = getTatValue(j, day, config);

				// add calculated tat for cover and create relationship with
				// covers
				calTat = new CalculatedTat(tatValue);
				calTat = calTatDao.create(calTat);
				calTatList.add(calTat);

				calTat = new CalculatedTat(tatValue);
				calTat = calTatDao.create(calTat);
				calTatList.add(calTat);

				// add cover with day
				id = coversDao.addForNumberOfCovers(cover, day, tatValue);
				Logger.debug("Relatonship of covers and day of week created ... id is " + id);

				// add cover with calculated tat
				calTatDao.addTatValue(cover, calTatList, tatValue);

			}
		}

		Logger.debug("covers list is " + coversList + " Cover List size is " + coversList.size());

	}

	@Override
	@Transactional(readOnly = true)
	public BaseResponse getOperationalHours(Map<String, Object> params) {
		// TODO Auto-generated method stub
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(params.get(Constants.TOKEN).toString());
		if (null != userInfo && !userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID) && !userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			params.put(Constants.REST_GUID, userInfo.getRestGuid());
		} 

		/*
		 * Validating Restaurant GUID in case of CT_ADMIN_ROLE_ID Or
		 * CUSTOMER_ROLE_ID
		 */

		if (!params.containsKey(Constants.REST_GUID)) {

			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
		}

		if (listOfError.isEmpty()) {
			OperationalHours op_hr = restDao.getOperationalHours(params);
			Logger.debug("Operational Hours retrieving ::::::: ");
			List<OperationalHours> list = new ArrayList<OperationalHours>();
			list.add(op_hr);
			if (!list.isEmpty()) {
				response = new GetResponse<OperationalHours>(ResponseCodes.OPERATIONAL_HOUR_RECORD_FETCH_SUCCESS, list);
			} else {
				response = new ErrorResponse(ResponseCodes.OPERATIONAL_HOUR_RECORD_FETCH_FAILURE, listOfError);
			}

		} else {
			response = new ErrorResponse(ResponseCodes.OPERATIONAL_HOUR_RECORD_FETCH_FAILURE, listOfError);
		}

		return response;
	}

	@Override
	public BaseResponse getHistoricalTat(Map<String, Object> params) {
		BaseResponse response;

		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("IST"));

		SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
		timeFormat.setTimeZone(TimeZone.getTimeZone("IST"));

		Date date = new Date();

		try {

			if ((params != null) && (params.containsKey(Constants.DATE))) {

				date = sdf.parse(params.get(Constants.DATE).toString());

			}

			Date time = timeFormat.parse(timeFormat.format(new Date()));

			if ((params != null) && (params.containsKey(Constants.TIME))) {

				time = timeFormat.parse(params.get(Constants.TIME).toString());

			}

			Logger.debug("Time from param is " + time);
			Long shiftChangeTime = 0L;
			shiftChangeTime = timeFormat.parse(Constants.SHIFT_CHANGE_TIME).getTime();
			Logger.debug("shift change time  is " + shiftChangeTime + " time is " + time.getTime());

			if (params != null) {
				if (time.getTime() > shiftChangeTime) {
					params.put(Constants.SHIFT, "DINNER");
				} else {
					params.put(Constants.SHIFT, "LUNCH");
				}

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if ((params != null) && (!params.containsKey(Constants.REST_GUID))) {
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
		}

		if ((params != null) && (!params.containsKey(Constants.COVERS))) {
			listOfError.add(validateRestObject.createError(Constants.COVERS, ErrorCodes.COVERS_REQUIRED));
		}

		if (listOfError.isEmpty()) {
			Calendar calc = Calendar.getInstance();
			calc.setTimeInMillis(date.getTime());
			String dayNames[] = new DateFormatSymbols().getWeekdays();

			Logger.debug("Day of week is " + dayNames[calc.get(Calendar.DAY_OF_WEEK)]);
			params.put(Constants.DAY_OF_THE_WEEK, dayNames[calc.get(Calendar.DAY_OF_WEEK)].toUpperCase());

			List<HistoricalTatResult> tatValueList = histDao.getHistoricalTat(params);

			Logger.debug("Tat value in rest service is " + tatValueList);
			response = new GetResponse<>(ResponseCodes.HISTORICAL_TAT_VALUE_FETCH_SUCCESFULLY, tatValueList);
		} else {
			response = new ErrorResponse(ResponseCodes.HISTORICAL_TAT_VALUE_FETCH_FAILURE, listOfError);
		}

		return response;
	}

	@Override
	public BaseResponse addBlackOutHours(BlackOutHours ophr, String header) {
		// TODO Auto-generated method stub
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		// if access token is not of CT-ADMIN, get the RestaurantId using token
		// and set in ophr.
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(header);
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID) && !userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			ophr.setRestGuid(userInfo.getRestGuid());
		}

		if (null == ophr.getRestGuid()) {
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
		}

		if (listOfError.isEmpty()) {
			listOfError = validateRestObject.validateBlackOutOperationalHours(ophr, listOfError);

			if (listOfError.isEmpty()) {
				boolean created = restDao.addBlackOutHours(ophr);
				Logger.debug("Operational Hours Addition Status ::::::: " + created);
				if (created) {
					response = new UpdateResponse<Restaurant>(ResponseCodes.BLACK_OUT_OPERATIONAL_HOUR_RECORD_ADDED_SUCCESS, ophr.getRestGuid());
				} else {
					response = new ErrorResponse(ResponseCodes.BLACK_OUT_OPERATIONAL_HOUR_RECORD_ADDED_FAILURE, listOfError);
				}

			} else {
				response = new ErrorResponse(ResponseCodes.BLACK_OUT_OPERATIONAL_HOUR_RECORD_ADDED_FAILURE, listOfError);
			}
		} else {
			response = new ErrorResponse(ResponseCodes.BLACK_OUT_OPERATIONAL_HOUR_RECORD_ADDED_FAILURE, listOfError);
		}
		return response;
	}

	@Override
	public BaseResponse getBlackOutHours(Map<String, Object> params) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(params.get(Constants.TOKEN).toString());
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID) && !userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			params.put(Constants.REST_GUID, userInfo.getRestGuid());
		}

		/*
		 * Validating Restaurant GUID in case of CT_ADMIN_ROLE_ID Or
		 * CUSTOMER_ROLE_ID
		 */

		if (!params.containsKey(Constants.REST_GUID)) {

			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
		}

		if (listOfError.isEmpty()) {
			BlackOutHours op_hr = restDao.getBlackOutHours(params);
			List<BlackOutHours> list = new ArrayList<BlackOutHours>();
			list.add(op_hr);
			if (!list.isEmpty()) {
				response = new GetResponse<BlackOutHours>(ResponseCodes.BLACK_OUT_OPERATIONAL_HOUR_RECORD_FETCH_SUCCESS, list);
			} else {
				response = new ErrorResponse(ResponseCodes.BLACK_OUT_OPERATIONAL_HOUR_RECORD_FETCH_FAILURE, listOfError);
			}

		} else {
			response = new ErrorResponse(ResponseCodes.BLACK_OUT_OPERATIONAL_HOUR_RECORD_FETCH_FAILURE, listOfError);
		}

		return response;

	}

	@Override
	public BaseResponse cleanRestaurantData(Map<String, Object> stringParamMap, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		String restGuid = (String) stringParamMap.get(Constants.REST_GUID);
		if ((restGuid == null) || restGuid.equals(""))
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));

		Restaurant restaurant = restDao.find(restGuid);
		if (restaurant == null)
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.INVALID_REST_ID));

		if (listOfError.isEmpty()) {
			boolean created = restDao.deleteRestaurantData(restGuid);
			String guestGuids = createGuests(restGuid, token);
			Logger.debug("Guest guids are  ----------" + guestGuids);
			Logger.debug("query result" + created);
			response = new UpdateResponse<Restaurant>(ResponseCodes.RESTAURANT_DATA_DELETED_SUCCESFULLY, guestGuids);
		} else {
			response = new ErrorResponse(ResponseCodes.RESTAURANT_DATA_DELETION_FAILURE, listOfError);
		}

		return response;

	}

	private String createGuests(String restGuid, String token) {
		String guestGuids = "";
		Object[] objArr;
		List guestMapList = getGuestMapList();
		BaseResponse response;

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);

		Map guestMap = new HashMap<>();
		GuestProfile guest;

		for (int i = 0; i < 9; i++) {
			guestMap = (Map) guestMapList.get(i);
			guest = new GuestProfile();
			guest.setDummy(false);
			guest.setEmailId((String) guestMap.get("emailId"));
			guest.setFirstName((String) guestMap.get("firstName"));
			guest.setGender((String) guestMap.get("gender"));
			guest.setIsVip((Boolean) guestMap.get("isVip"));
			//guest.setLastName((String) guestMap.get("lastName"));
			guest.setMobile((String) guestMap.get("mobile"));
			guest.setReason((String) guestMap.get("reason"));
			guest.setRestGuid(restGuid);
			guest.setInfoOnCreate(userInfo);
			guest.setStatus(Constants.ACTIVE_STATUS);

			response = guestService.addCustomer(guest, token);

			Logger.debug("Guest add response is " + response);

			if (response instanceof PostResponse) {
				objArr = ((PostResponse) response).getGuid();
				for (int j = 0; j < objArr.length; j++) {
					guestGuids = guestGuids + objArr[j] + ",";
				}

			}

		}

		if (guestGuids.contains(",")) {
			guestGuids = guestGuids.substring(0, guestGuids.length() - 1);
		}

		return guestGuids;

	}

	private List<HashMap<String, Object>> getGuestMapList() {
		List guestMapList = new ArrayList<>();

		// guest 1
		Map guestMap = new HashMap<>();
		guestMap.put("firstName", "Pavan");
		guestMap.put("lastName", "Thakur");
		guestMap.put("mobile", "2238424130");
		guestMapList.add(guestMap);

		// guest 2
		guestMap = new HashMap<>();
		guestMap.put("firstName", "Rohan");
		guestMap.put("lastName", "Gandhi");
		guestMap.put("mobile", "2238424131");
		guestMapList.add(guestMap);

		// guest 3
		guestMap = new HashMap<>();
		guestMap.put("firstName", "Ankur");
		guestMap.put("lastName", "Jain");
		guestMap.put("mobile", "2238424139");
		guestMapList.add(guestMap);

		// guest 4
		guestMap = new HashMap<>();
		guestMap.put("firstName", "Pawan");
		guestMap.put("lastName", "Nigam");
		guestMap.put("mobile", "2238424133");
		guestMapList.add(guestMap);

		// guest 5
		guestMap = new HashMap<>();
		guestMap.put("firstName", "Abhishek");
		guestMap.put("lastName", "Rana");
		guestMap.put("mobile", "2238424134");
		guestMapList.add(guestMap);

		// guest 6
		guestMap = new HashMap<>();
		guestMap.put("firstName", "Vikas");
		guestMap.put("lastName", "Suriyal");
		guestMap.put("mobile", "2238424135");
		guestMapList.add(guestMap);

		// guest 7
		guestMap = new HashMap<>();
		guestMap.put("firstName", "Sonu");
		guestMap.put("lastName", "Rathi");
		guestMap.put("mobile", "2238424136");
		guestMapList.add(guestMap);

		// guest 8
		guestMap = new HashMap<>();
		guestMap.put("firstName", "Ankit");
		guestMap.put("lastName", "Sharma");
		guestMap.put("mobile", "2238424137");
		guestMapList.add(guestMap);

		// guest 9
		guestMap = new HashMap<>();
		guestMap.put("firstName", "Sheel");
		guestMap.put("lastName", "Jain");
		guestMap.put("mobile", "2238424138");
		guestMapList.add(guestMap);

		Map guestMapToUpdate = new HashMap<>();

		for (int i = 0; i < 9; i++) {
			guestMapToUpdate = (Map) guestMapList.get(i);
			if (i == 0 || i == 4 || i == 7) {
				guestMapToUpdate.put("emailId", null);
				guestMapToUpdate.put("gender", "MALE");
				guestMapToUpdate.put("isVip", true);
				guestMapToUpdate.put("reason", "INVESTOR");
			} else {
				guestMapToUpdate.put("emailId", null);
				guestMapToUpdate.put("gender", "MALE");
				guestMapToUpdate.put("isVip", false);
				guestMapToUpdate.put("reason", null);
			}
		}

		return guestMapList;
	}

	private void updateHistoricalTatData(RestSystemConfigModel rest) {

		Map<String, Object> params = new HashMap<>();
		params.put(Constants.REST_GUID, rest.getRestaurantGuid());
		// create day of week and create relationship of day of week and hist
		// tat
		List<DayOfWeek> dayList = new ArrayList<DayOfWeek>();
		dayList = dayDao.findByFields(DayOfWeek.class, params);
		List<RestSystemConfigModel> tatList = ((GetResponse) getSystemConfig(params)).getList();

		RestSystemConfigModel config = new RestSystemConfigModel();
		if (tatList.size() > 0) {
			config = tatList.get(0);
		}

		// create number of covers nodes and add relationship with day of week
		// with tat values
		List<NumberOfCovers> coversList;
		int tatValue = 0;
		List<CalculatedTat> calTatList;
		Map<String, Object> coverQueryParams;
		Map<String, Object> calTatQueryParams;
		for (DayOfWeek day : dayList) {
			coverQueryParams = new HashMap<>();
			coverQueryParams.put(Constants.GUID, day.getGuid());
			coversList = coversDao.findByFields(NumberOfCovers.class, coverQueryParams);

			for (NumberOfCovers cover : coversList) {
				int j = cover.getCovers() - 1;
				tatValue = getTatValue(j, day, config);
				calTatQueryParams = new HashMap<>();
				calTatQueryParams.put(Constants.GUID, cover.getGuid());
				calTatList = calTatDao.findByFields(CalculatedTat.class, calTatQueryParams);

				for (CalculatedTat calTat : calTatList) {
					calTat.setValue(tatValue);
					calTatDao.update(calTat);
				}
			}
		}

	}

	String getAccountShortId(UserInfoModel userInfo) {
		List<AccountIdUnique> values = accValuesDao.findAll(AccountIdUnique.class);
		long id;
		AccountIdUnique value = null;
		if (values.isEmpty()) {
			value = new AccountIdUnique();
			value.setInfoOnCreate(userInfo);
			value.setAccountId(Constants.MIN_ACCOUNT_ID);
			accValuesDao.create(value);
			id = value.getAccountId();

		} else if (values.size() == 1) {
			value = values.get(0);
			id = values.get(0).getAccountId();
			value.setAccountId(id + 1);
			accValuesDao.update(value);
		}
		return String.valueOf(value.getAccountId());
	}

	@Override
	@Transactional(readOnly = true)
	public BaseResponse getRestaurantWeather(String token) {
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		BaseResponse response = null;
		String restGuid = "";

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (userInfo == null) {

			listOfError.add(validateRestObject.createError(Constants.TOKEN, ErrorCodes.INVALID_ACCESS_TOKEN));
			response = new ErrorResponse(ResponseCodes.WEATHER_FETCH_FAILURE, listOfError);
			return response;

		} else {
			restGuid = userInfo.getRestGuid();
		}

		if (restGuid != null && !restGuid.equals("")) {

			parameters.put(Constants.GUID, restGuid);
			List<Restaurant> restList = restDao.findByFields(Restaurant.class, parameters);

			if ((restList != null) && (restList.size() == 1)) {

				try {

					Restaurant rest = restList.get(0);
					String lat = rest.getLatitude();
					String lang = rest.getLongitude();

					if (lat != null && lang != null) {
						String appid = UtilityMethods.getConfString(Constants.OPENWEATHER_APPID);

						HttpClient client = new DefaultHttpClient();

						HttpGet request = new HttpGet(Constants.OPEN_WEATHER_URL + "?APPID=" + appid + "&lat=" + lat + "&lon=" + lang);
						HttpResponse res = client.execute(request);

						BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
						String line = "";
						line = rd.readLine();
						if (line != null) {

							ObjectMapper mapper = new ObjectMapper();
							JsonNode actualObj = mapper.readTree(line);

							List<JsonNode> list = new ArrayList<JsonNode>();
							list.add(actualObj);
							response = new GetResponse<JsonNode>(ResponseCodes.WEATHER_FETCH_SUCCESSFULLY, list);
						} else {
							listOfError.add(validateRestObject.createError(Constants.INVALID, ResponseCodes.NOT_FOUND));
							response = new ErrorResponse(ResponseCodes.WEATHER_FETCH_FAILURE, listOfError);
							return response;
						}

					} else {
						listOfError.add(validateRestObject.createError(Constants.LAT_LONG, ErrorCodes.LAT_LONG_NOT_CONFIGURED));
						response = new ErrorResponse(ResponseCodes.WEATHER_FETCH_FAILURE, listOfError);
						return response;
					}

				} catch (ClientProtocolException e) {
					Logger.debug("Exception is ------------ " + e.getLocalizedMessage());
					listOfError.add(validateRestObject.createError(Constants.INVALID, ErrorCodes.NOT_FOUND));
					response = new ErrorResponse(ResponseCodes.WEATHER_FETCH_FAILURE, listOfError);
					return response;
				} catch (IOException e) {
					Logger.debug("Exception is ------------ " + e.getLocalizedMessage());
					listOfError.add(validateRestObject.createError(Constants.INVALID, ErrorCodes.NOT_FOUND));
					response = new ErrorResponse(ResponseCodes.WEATHER_FETCH_FAILURE, listOfError);
					return response;
				}

			} else {
				listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.INVALID_RESTAURANT_GUID));
				response = new ErrorResponse(ResponseCodes.WEATHER_FETCH_FAILURE, listOfError);
				return response;
			}

		} else {
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.INVALID_RESTAURANT_GUID));
			response = new ErrorResponse(ResponseCodes.WEATHER_FETCH_FAILURE, listOfError);
			return response;
		}

		return response;
	}

	@Override
	public BaseResponse updateContactInfoCtAdmin(RestaurantContactInfoAdmin contactInfo, String token) {
		BaseResponse response;

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);

		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		if (contactInfo.getGuid() == null)
			listOfError.add(validateRestObject.createError(Constants.GUID, ErrorCodes.REST_ID_REQUIRED));
		else {
			// /if role is admin then check for restaurant(admin can change
			// details of his own restaurant)
			if ((userInfo.getRoleId().equals(Constants.ADMIN_ROLE_ID)) && (!contactInfo.getGuid().equals(userInfo.getRestGuid()))) {
				listOfError.add(validateRestObject.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
				response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
				return response;
			}

			Restaurant restaurant = restDao.find(contactInfo.getGuid());
			if (restaurant == null)
				listOfError.add(validateRestObject.createError(Constants.GUID, ErrorCodes.INVALID_REST_ID));
			else {
				contactInfo.copyExistingValues(restaurant);
				listOfError.addAll(validateRestObject.validateRestaurantContactInfoAdmin(contactInfo));
			}
		}

		if (listOfError.isEmpty()) {
			Restaurant restaurant = restDao.updateContactInfoAdmin(contactInfo);
			response = new UpdateResponse<Restaurant>(ResponseCodes.RESTAURANT_UPDATED_SUCCESFULLY, restaurant.getGuid());
		} else {
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
		}
		return response;

	}

	private int getTatValue(int j, DayOfWeek day, RestSystemConfigModel config) {
		int tatValue = 0;
		if (day.getDayType().equals(Constants.WEEKEND)) {
			if (j == 0 || j == 1) {
				tatValue = config.getTat_we_12();
			}
			if (j == 2 || j == 3) {
				tatValue = config.getTat_we_34();
			}
			if (j == 4 || j == 5) {
				tatValue = config.getTat_we_56();
			}
			if (j == 6 || j == 7) {
				tatValue = config.getTat_we_78();
			}
			if (j == 8) {
				tatValue = config.getTat_we_8P();
			}

		} else {
			if (j == 0 || j == 1) {
				tatValue = config.getTat_wd_12();
			}
			if (j == 2 || j == 3) {
				tatValue = config.getTat_wd_34();
			}
			if (j == 4 || j == 5) {
				tatValue = config.getTat_wd_56();
			}
			if (j == 6 || j == 7) {
				tatValue = config.getTat_wd_78();
			}
			if (j == 8) {
				tatValue = config.getTat_wd_8P();
			}

		}

		return tatValue;
	}


	@Override
	public BaseResponse statusUpdateRestaurant(Restaurant rest, String token) {
		BaseResponse response;
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if (rest.getGuid() == null) {
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods
					.getErrorMsg(ErrorCodes.INVALID_RESTAURANT_GUID),
					ErrorCodes.INVALID_RESTAURANT_GUID));
			return new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, rest.getGuid());
		List<Restaurant> restaurantList = restDao.findByFields(Restaurant.class, params);
		if(restaurantList.size()!=1){
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods
					.getErrorMsg(ErrorCodes.INVALID_RESTAURANT_GUID),
					ErrorCodes.INVALID_RESTAURANT_GUID));
			return new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
		}
		Restaurant restaurant = restaurantList.get(0);
		if(listOfError.isEmpty()){
			rest.setLanguageCode(restaurant.getLanguageCode());
		listOfError.addAll(validateRestObject.validateRestaurantOnPatchUpdate(rest));
		}
		if(listOfError.isEmpty()){
			if(rest.getStatus().equals(Constants.INACTIVE_STATUS)){
				if(restaurant.getStatus().equals(Constants.INACTIVE_STATUS)){
					listOfError.add(new ValidationError(Constants.STATUS, UtilityMethods
							.getErrorMsg(ErrorCodes.RESTAURANT_ALREADY_INACTIVE),
							ErrorCodes.RESTAURANT_ALREADY_INACTIVE));
					return new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
				}else if(restaurant.getStatus().equals(Constants.DELETED_STATUS)){
					listOfError.add(new ValidationError(Constants.STATUS, UtilityMethods
							.getErrorMsg(ErrorCodes.DELETED_RESTAURANT_CAN_NOT_BE_DEACTIVATED),
							ErrorCodes.DELETED_RESTAURANT_CAN_NOT_BE_DEACTIVATED));
					return new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
				}else {
					restaurant.setStatus(Constants.INACTIVE_STATUS);
					//********Set staff of Restaurant INACTIVE (relationship)*******************
					restDao.setInactiveAllActiveStaff(rest.getGuid());
					restDao.update(restaurant);
				}
			}else if(rest.getStatus().equals(Constants.DELETED_STATUS)){
				listOfError.add(new ValidationError(Constants.STATUS, UtilityMethods
						.getErrorMsg(ErrorCodes.DELETED_RESTAURANT_CAN_NOT_BE_DELETED),
						ErrorCodes.DELETED_RESTAURANT_CAN_NOT_BE_DELETED));
				return new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
			}else if(rest.getStatus().equals(Constants.ACTIVE_STATUS)){
				
				listOfError.add(new ValidationError(Constants.STATUS, UtilityMethods
						.getErrorMsg(ErrorCodes.INVALID_STATUS),
						ErrorCodes.INVALID_STATUS));
				return new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
			
			}
			response = new UpdateResponse<Restaurant>(ResponseCodes.RESTAURANT_UPDATED_SUCCESFULLY, restaurant.getGuid());
			
		}else{
			//Deactivation Failure....
			response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
			
		}
		
		return response;
	}


	@Override
	public BaseResponse reactivateRestaurant(Staff staff, String token) {
		BaseResponse response=null;
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		
		
		if (userInfo!=null) {
			staff.setGuid(UtilityMethods.generateCtId());
			staff.setCreatedBy(userInfo.getGuid());
			staff.setUpdatedBy(userInfo.getGuid());
		}else{
			listOfError.add(new ValidationError(Constants.ACCESS_TOKEN, UtilityMethods
					.getErrorMsg(ErrorCodes.INVALID_ACCESS_TOKEN),
					ErrorCodes.INVALID_ACCESS_TOKEN));
			return new ErrorResponse(ResponseCodes.RESTAURANT_ACTIVATION_FAILURE, listOfError);
		}
		listOfError.addAll(validateRestObject.validateRestaurantOnReactivation(staff));
		
		if(listOfError.isEmpty()){
			response = staffService.addStaffMember(staff, token);
			if(response.getResponseStatus()){
				Restaurant rest = restDao.findRestaurantByGuid(staff.getRestaurantGuid());
				rest.setStatus(Constants.ACTIVE_STATUS);
				Restaurant restaurant = restDao.update(rest);
				response = new UpdateResponse<Restaurant>(ResponseCodes.RESTAURANT_ACTIVATED_SUCCESFULLY, restaurant.getGuid());
				
			}else{
				response.setResponseCode(ResponseCodes.RESTAURANT_UPDATION_FAILURE);
				response.setResponseMessage(Messages.get(ResponseCodes.RESTAURANT_UPDATION_FAILURE));
				return response;
			}
		}else{
			return new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE, listOfError);
		
		}
		return response;
	}

}
