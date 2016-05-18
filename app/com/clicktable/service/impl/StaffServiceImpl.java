package com.clicktable.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import play.Logger;
import play.api.libs.Crypto;
import play.cache.Cache;
import play.i18n.Messages;
import play.libs.Json;

import com.clicktable.config.StormpathConfig;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.StaffDao;
import com.clicktable.dao.intf.StaffInfoDao;
import com.clicktable.model.Oauth;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Staff;
import com.clicktable.model.StaffInfo;
import com.clicktable.model.UserInfoModel;
import com.clicktable.repository.RestaurantRepo;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.LoginResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.SMSResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.NotificationService;
import com.clicktable.service.intf.StaffService;
import com.clicktable.service.intf.UserTokenService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.StaffValidator;
import com.clicktable.validate.ValidationError;
import com.firebase.security.token.TokenOptions;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.directory.Directories;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.resource.ResourceException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * 
 * @author p.singh
 * 
 */

@org.springframework.stereotype.Service
public class StaffServiceImpl implements StaffService {

	@Autowired
	StaffDao staffDao;

	@Autowired
	RestaurantDao restDao;

	@Autowired
	RestaurantRepo restRepo;

	@Autowired
	UserTokenService userTokenService;

	@Autowired
	StaffValidator validateStaffObject;

	@Autowired
	AuthorizationService authorizationService;

	@Autowired
	NotificationService notification;
	
	@Autowired
	RestaurantValidator restValidator;
	
	@Autowired
	StaffInfoDao staffInfoDao;
	
	//private Oauth oauth;

	/**
	 * Service Method to add staff to storm path.whenever a staff member is
	 * created this method is called
	 */
	@Override
	public String addStaffToStormPath(Staff staff) {
		// TODO Auto-generated method stub

		String href = "";

		DirectoryList dirList = StormpathConfig.getInstance().getTenant().getDirectories(Directories.where(Directories.name().eqIgnoreCase(Constants.STAFF_DIRECTORY)));
		Client client = StormpathConfig.getInstance().getClient();
		Directory directory = null;
		for (Directory dir : dirList) {
			directory = dir;
		}
		com.stormpath.sdk.application.Application application = StormpathConfig.getInstance().getApplication();

		Account account = client.instantiate(Account.class);

		// Set the account properties
		account.setGivenName(staff.getFirstName());
		account.setSurname(staff.getLastName());
		account.setEmail(staff.getEmail());

		// use the generatePassword method of UtilityMethods class to generate
		// random password
		String password = UtilityMethods.generatePassword();
		account.setPassword(password);

		// Create the account using the directory object
		// Account createdAccount = application.createAccount(account);
		Account createdAccount = directory.createAccount(account);
		href = createdAccount.getHref();
		String[] subStr = href.split("/");
		// whenever a new staff is added to storm path a password reset email is
		// send to staff member to change the login password
		application.sendPasswordResetEmail(staff.getEmail());

		return subStr[subStr.length - 1];

	}

	/**
	 * Service Method to authenticate staff in stormpath
	 * 
	 * @return 1,if user is authenticated successfully 2,if user is not
	 *         authenticated due to invalid username 3,if user is not
	 *         authenticated due to wrong password
	 */
	public Integer authenticateStaff(String userName, String password) {
		Integer isAuthenticated = 0;

		try {

			DirectoryList dirList = StormpathConfig.getInstance().getTenant().getDirectories(Directories.where(Directories.name().eqIgnoreCase(Constants.STAFF_DIRECTORY)));
			Directory directory = null;
			for (Directory dir : dirList) {
				directory = dir;
			}

			UsernamePasswordRequest authenticationRequest = new UsernamePasswordRequest(userName, password);
			com.stormpath.sdk.application.Application application = StormpathConfig.getInstance().getApplication();

			AuthenticationResult result = application.authenticateAccount(authenticationRequest);

			// AccountList accountList = directory.getAccounts();

			Directory dir = result.getAccount().getDirectory();

			/*
			 * Account account = result.getAccount(); for (Account acc :
			 * accountList) { if(acc.getEmail().equals(account.getEmail())) {
			 * isAuthenticated = Constants.SUCCESSFULLY_AUTHENTICATED; return
			 * isAuthenticated; }
			 * 
			 * }
			 */

			Logger.debug("dir name is " + dir.getName() + " directory name is " + directory.getName() + " isauthenticated======" + isAuthenticated);
			if (dir.getName().equals(directory.getName())) {
				isAuthenticated = Constants.SUCCESSFULLY_AUTHENTICATED;
			} else {
				isAuthenticated = Constants.INVALID_ACCOUNT;
			}

			Logger.debug("isAuthenticated===============" + isAuthenticated);
		} catch (ResourceException ex) {
			String loginFailureMsg = ex.getDeveloperMessage();
			Logger.debug("login failure message is " + loginFailureMsg);
			if (loginFailureMsg.contains(Constants.INVALID_USERNAME_MSG)) {
				isAuthenticated = Constants.INVALID_USERNAME;
			}
			if (loginFailureMsg.contains(Constants.WRONG_PASSWORD_MSG)) {
				isAuthenticated = Constants.WRONG_PASSWORD;
			}

			// System.out.println(ex.getDeveloperMessage());

		}

		return isAuthenticated;
	}

	/**
	 * Service Method to change login password of staff member
	 * 
	 * @param userName
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	@Override
	public BaseResponse changePassword(String userName, String oldPassword, String newPassword) {
		List<ValidationError> listOfErrorForStaff = new ArrayList<ValidationError>();
		// response object is instantiated on the basi of response type whether
		// it is a successful response or error response
		BaseResponse response = new BaseResponse();

		try {

			DirectoryList dirList = StormpathConfig.getInstance().getTenant().getDirectories(Directories.where(Directories.name().eqIgnoreCase(Constants.STAFF_DIRECTORY)));
			Directory directory = null;
			for (Directory dir : dirList) {
				directory = dir;
			}
			// authenticate user on the basis of username and old password
			UsernamePasswordRequest authenticationRequest = new UsernamePasswordRequest(userName, oldPassword);
			com.stormpath.sdk.application.Application application = StormpathConfig.getInstance().getApplication();
			AuthenticationResult result = application.authenticateAccount(authenticationRequest);
			Directory dir = result.getAccount().getDirectory();
			if (dir.getName().equals(directory.getName())) {
				// if authenticated successfully then change the password
				result.getAccount().setPassword(newPassword).save();
				response.createResponse(ResponseCodes.PASSWORD_CHANGED_SUCCESFULLY, true);
			} else {
				response.createResponse(ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_ACCOUNT, false);

			}

		} catch (ResourceException ex) {

			String loginFailureMsg = ex.getDeveloperMessage();
			Logger.debug("login failure message is " + loginFailureMsg);
			if (loginFailureMsg.contains(Constants.INVALID_USERNAME_MSG)) {
				listOfErrorForStaff.add(new ValidationError(Constants.USERNAME, Messages.get(ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_USERNAME, new Object()),
						ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_USERNAME));

			}
			if (loginFailureMsg.contains(Constants.WRONG_PASSWORD_MSG)) {
				listOfErrorForStaff.add(new ValidationError(Constants.OLD_PASSWORD, Messages.get(ResponseCodes.SOCIAL_LOGIN_FAILURE_WRONG_PASSWORD, new Object()),
						ResponseCodes.SOCIAL_LOGIN_FAILURE_WRONG_PASSWORD));

			}

			// if authentication failed error response is send
			response = new ErrorResponse(ResponseCodes.PASSWORD_CHANGE_FAILURE, listOfErrorForStaff);
		}

		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse addStaffMember(Staff staff, String token) {
		BaseResponse response;

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);

		if (staff.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
			staff.setCreatedBy(staff.getGuid());
			staff.setUpdatedBy(staff.getGuid());
		} else {
			staff.setCreatedBy(userInfo.getGuid());
			staff.setUpdatedBy(userInfo.getGuid());
		}
		staff.setStatus(Constants.ACTIVE_STATUS);
		Boolean isOTPRequire = staff.isIs_otp_require();
		staff.setIs_otp_require(null);

		List<ValidationError> listOfErrorForStaff = validateStaffObject.validateStaffOnAdd(staff);

		if (!listOfErrorForStaff.isEmpty()) 
		{
			response = new ErrorResponse(ResponseCodes.STAFF_ADDED_FAILURE, listOfErrorForStaff);
			return response;
		}

		Restaurant rest = null;
		if ((staff.getRestaurantGuid() != null) && (!staff.getRestaurantGuid().equals(""))) {

			rest = restValidator.validateGuid(staff.getRestaurantGuid(), listOfErrorForStaff);
			Logger.debug("finding restaurant " + rest);
			if (rest == null) {
				Logger.debug("list of error is " + listOfErrorForStaff);
				response = new ErrorResponse(ResponseCodes.STAFF_ADDED_FAILURE, listOfErrorForStaff);
				return response;
			}

		}

		// if user is not ct admin then check if staff member being created
		// and logged in staff member both belongs to same restaurant
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
			listOfErrorForStaff = validateStaffObject.validateStaffForRestaurant(staff.getRestaurantGuid(), userInfo.getRestGuid());
			if (!listOfErrorForStaff.isEmpty()) {
				response = new ErrorResponse(ResponseCodes.STAFF_ADDED_FAILURE, listOfErrorForStaff);
				return response;
			}
		}

		Logger.debug("list of error is empty");
		// If no validation error then add staff to stormpath within try
		// catch so that if exception
		// arises in adding user to storm path
		// response for failure is send
		try {

			staff.setHref(addStaffToStormPath(staff));
			Logger.debug("staff added to stormpath");

			/* Generation FireBase Token */
		/*	try {
				Map<String, Object> authPayload = new HashMap<String, Object>();
				authPayload.put("uid", UtilityMethods.generateCtId());
				authPayload.put("token", Constants.FIREBASE_TOKEN_NAME);

				TokenOptions tokenOptions = new TokenOptions();
				tokenOptions.setAdmin(true);

				TokenGenerator tokenGenerator = new TokenGenerator(Constants.FIREBASE_TOKEN);
				//staff.setFirebase_token(tokenGenerator.createToken(authPayload, tokenOptions));
			} catch (Exception e) {
				e.printStackTrace();
			}
*/
			/*
			 * Create relationship of staff with restaurant
			 */
			Staff newStaff = null;
			try{
				newStaff = staffDao.create(staff);
			}catch(Exception e){
			}
					
			Logger.debug("staff created");

			
			if(newStaff == null || newStaff.getRestaurantGuid() == null || newStaff.getGuid() == null)
			{
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();	
				Client client = StormpathConfig.getInstance().getClient();
				client.getResource(Constants.STORMPATH_HREF_PATH_STRING + newStaff.getHref(), Account.class).delete();

				listOfErrorForStaff.add(new ValidationError(Constants.STAFF_INFO, UtilityMethods.getErrorMsg(ErrorCodes.STAFF_INFO_CREATION_FAILURE), ErrorCodes.STAFF_INFO_CREATION_FAILURE));
				response = new ErrorResponse(ResponseCodes.STAFF_ADDED_FAILURE, listOfErrorForStaff);
				return response;
			}
			
//			if ((staff.getRestaurantGuid() != null) && (!staff.getRestaurantGuid().equals(""))) {
			Long id = staffDao.addRestaurantStaff(rest.getGuid(), newStaff);
			Logger.debug("relationship id is " + id);
//			}
			
			StaffInfo staffInfo = new StaffInfo();
			staffInfo.setIs_otp_require(isOTPRequire);
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
			
			response = new PostResponse<Staff>(ResponseCodes.STAFF_ADDED_SUCCESFULLY, newStaff.getGuid());
		} catch (ResourceException e) {
			if (e.getDeveloperMessage().contains("Account with that email already exists")) {
				listOfErrorForStaff.add(new ValidationError(Constants.EMAIL, UtilityMethods.getErrorMsg(ErrorCodes.EMAIL_ACCOUNT_ALREADY_EXISTS), ErrorCodes.EMAIL_ACCOUNT_ALREADY_EXISTS));
			}
			else if (e.getDeveloperMessage().contains("Account email address is in an invalid format")) {
				listOfErrorForStaff.add(new ValidationError(Constants.EMAIL, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_EMAIL_FORMAT), ErrorCodes.INVALID_EMAIL_FORMAT));
			}
			else {
				listOfErrorForStaff.add(new ValidationError(Constants.STORMPATH_MODULE, e.getMessage()));
			}
			response = new ErrorResponse(ResponseCodes.STAFF_ADDED_FAILURE, listOfErrorForStaff);
		}

		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse updateStaffMember(Staff staff, String token) {
		BaseResponse response = null; 

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);

		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.GUID, staff.getGuid());
		paramMap.put("include_inactive", true);
		Map<String,Object> resultMap = staffDao.findActiveStaffWithActiveRest(paramMap);

		Staff staffObj = (Staff) resultMap.get("staff");
		Restaurant rest = (Restaurant) resultMap.get("rest");
		StaffInfo staffInfo = (StaffInfo) resultMap.get("staffInfo");

		if (staffObj == null || staffInfo == null) {
			listOfError.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_STAFF_ID), ErrorCodes.INVALID_STAFF_ID));
			response = new ErrorResponse(ResponseCodes.STAFF_UPDATION_FAILURE, listOfError);
			return response;
		}

		if(rest == null)
		{
			listOfError.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.RESTAURANT_NOT_ACTIVE), ErrorCodes.INVALID_RESTAURANT_GUID));
			response = new ErrorResponse(ResponseCodes.STAFF_UPDATION_FAILURE, listOfError);
			return response;
		}
		
		if(userInfo.getRoleId().equals(Constants.MANAGER_ROLE_ID) && staffObj.getRoleId().equals(Constants.ADMIN_ROLE_ID))
		{
			listOfError.add(new ValidationError(Constants.ACCESS_DENIED, UtilityMethods.getErrorMsg(ErrorCodes.MANAGER_CAN_CREATE_OR_UPDATE_ONLY_STAFF_OR_SERVER), ErrorCodes.MANAGER_CAN_CREATE_OR_UPDATE_ONLY_STAFF_OR_SERVER));
			response = new ErrorResponse(ResponseCodes.STAFF_UPDATION_FAILURE, listOfError);
			return response;
		}
		

		staffObj.setUpdatedBy(userInfo.getGuid());
		if(staff.getRoleId() != null)
		{
			staffObj.setRoleId(staff.getRoleId());
		}
		if(staff.getFirstName() != null)
		{
			staffObj.setFirstName(staff.getFirstName());
		}
		if(staff.getLastName() != null)
		{
			staffObj.setLastName(staff.getLastName());
		}
		if(staff.getMobileNo() != null)
		{
			staffObj.setMobileNo(staff.getMobileNo());
		}
		if(staff.isIs_otp_require() != null)
		{
			staffObj.setIs_otp_require(null);
			staffInfo.setIs_otp_require(staff.isIs_otp_require());
			staffInfoDao.updateAllProperties(staffInfo);
		}

		if(staff.getStatus() != null && staff.getStatus().equals(Constants.INACTIVE_STATUS) && 
				staffObj.getStatus().equals(Constants.ACTIVE_STATUS)){

			staffObj.setStatus(Constants.INACTIVE_STATUS);
			String staffToken = authorizationService.getTokenForStaff(staffObj.getGuid());
			if(staffToken != null)
			{
				logOut(Crypto.encryptAES(staffToken));
			}

		}else if(staff.getStatus() != null && staff.getStatus().equals(Constants.ACTIVE_STATUS)){
			staffObj.setStatus(Constants.ACTIVE_STATUS);
		}

		staffObj = staffDao.updateStaff(staffObj);

		if (staffObj == null || staffInfo == null) {
			response = new ErrorResponse(ResponseCodes.STAFF_UPDATION_FAILURE, listOfError);
			return response;
		}
		response = new UpdateResponse<Staff>(ResponseCodes.STAFF_UPDATED_SUCCESFULLY, staffObj.getGuid());
		return response;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public BaseResponse getStaffMembers(Map<String, Object> params) {
		BaseResponse getResponse;
		Map<String, Object> qryParamMap = validateStaffObject.validateFinderParams(params, Staff.class);
		List<Staff> staffList = staffDao.findByFields(Staff.class, qryParamMap);
		getResponse = new GetResponse<Staff>(ResponseCodes.STAFF_RECORD_FETCH_SUCCESFULLY, staffList);
		return getResponse;
	}

	/**
	 * Service method for Resend OTP to staff member
	 */
	@Override
	public BaseResponse staffResendOTP(String guid) {

		// TODO Auto-generated method stub
		SMSResponse sms_response = new SMSResponse();
		BaseResponse response = new LoginResponse();
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.GUID, guid);
		//List<Staff> staffList = staffDao.findByFields(Staff.class, paramMap);
		Map<String,Object> resultMap = staffDao.findActiveStaffWithActiveRest(paramMap);

		Staff staff = (Staff) resultMap.get("staff");
		Restaurant restaurant = (Restaurant) resultMap.get("rest");
		StaffInfo staffInfo = (StaffInfo) resultMap.get("staffInfo");

		if(staff ==  null || staffInfo == null)
		{
			listOfError.add(new ValidationError(Constants.SMS_NOT_SENT, ErrorCodes.SMS_NOT_DELIVERED, ErrorCodes.SMS_NOT_DELIVERED));
			return new ErrorResponse(ResponseCodes.CODE_RESEND_FAILURE, listOfError);

		}
		if (staffInfo.isIs_otp_require()) {
			if (null == restaurant.getOtpMobile()) {
				listOfError.add(new ValidationError(Constants.REST_OTP_MOBILE, ErrorCodes.REST_OTP_MOBILE, ErrorCodes.REST_OTP_MOBILE));
				return new ErrorResponse(ResponseCodes.REST_OTP_MOBILE, listOfError);
			}
			/* Sending Verification Code */
			List<String> list = new ArrayList<String>();
			list.add(restaurant.getOtpMobile());
			/* Generate Message */
			int otp_token = UtilityMethods.generateOTP();
			sms_response = new SMSResponse();
			sms_response = notification.sendSMS(list, Constants.SMS_MESSAGE + otp_token, false).get(0);
			if (null != sms_response && sms_response.getSmsStatus().equalsIgnoreCase(ResponseCodes.SMS_SENT)) {
				staffInfo.setOtp_generated_time(new Date().getTime());
				staffInfo.setOtpToken(String.valueOf(otp_token));
				staffInfoDao.updateAllProperties(staffInfo);
				((LoginResponse) response).setSmsResponse(sms_response);
				((LoginResponse) response).setOtpRequire(staffInfo.isIs_otp_require());
				((LoginResponse) response).setStaff_guid(staff.getGuid());
				response.createResponse( ResponseCodes.SMS_SENT, true);
			} else {
				listOfError.add(new ValidationError(Constants.SMS_NOT_SENT, ErrorCodes.SMS_NOT_DELIVERED, ErrorCodes.SMS_NOT_DELIVERED));
				return new ErrorResponse(ResponseCodes.CODE_RESEND_FAILURE, listOfError);
			}

		}
		return response;
	}

	/**
	 * Service method for login of staff member
	 */
	@Override
	public BaseResponse staffLogin(String userName, String password) {

		BaseResponse response = new LoginResponse();
		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		Integer isAuthenticated = authenticateStaff(userName, password);
	
		//Logger.debug("isAuthenticated("+isAuthenticated+") == Constants.SUCCESSFULLY_AUTHENTICATED("+Constants.SUCCESSFULLY_AUTHENTICATED+")============================================"+(isAuthenticated == Constants.SUCCESSFULLY_AUTHENTICATED));
		if (isAuthenticated == Constants.SUCCESSFULLY_AUTHENTICATED) 
		{
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put(Constants.EMAIL, userName);
			//List<Staff> staffList = staffDao.findByFields(Staff.class, paramMap);
			
			Map<String,Object> resultMap = null;
			
			if(userName.equals(UtilityMethods.getConfString("sysadmin.user")) || userName.equals(UtilityMethods.getConfString("ctadmin.user"))){
				response = adminVerification(userName);
				return response;
			}else{
				resultMap = staffDao.findActiveStaffWithActiveRest(paramMap);
			}
			
			Staff staff = (Staff) resultMap.get("staff");
			Restaurant rest = (Restaurant) resultMap.get("rest");
			StaffInfo staffInfo = (StaffInfo) resultMap.get("staffInfo");
			//Staff staff;

			if (staff == null || staffInfo == null) {
				response.createResponse( ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_ACCOUNT, false);
				return response;

			}

			if(rest == null)
			{
				listOfError.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.RESTAURANT_NOT_ACTIVE), ErrorCodes.INVALID_RESTAURANT_GUID));
				return new ErrorResponse(ResponseCodes.STAFF_LOGIN_FAILURE, listOfError);
			}

			if(staffInfo.isIs_otp_require() != null && staffInfo.isIs_otp_require())
			{
				if(null==rest.getOtpMobile())
				{
					listOfError.add(new ValidationError(Constants.REST_OTP_MOBILE, ErrorCodes.REST_OTP_MOBILE, ErrorCodes.REST_OTP_MOBILE));
					return new ErrorResponse(ResponseCodes.REST_OTP_MOBILE, listOfError);
				}
				
				/* Sending Verification Code */
				List<String> list = new ArrayList<String>();
				list.add(rest.getOtpMobile());

				/* Generate Message */
				int otp_token = UtilityMethods.generateOTP();
				SMSResponse sms_response = new SMSResponse();
				Object param[] = { staff.getFirstName()+" "+staff.getLastName(), String.valueOf(otp_token) };
				String sms_messge = UtilityMethods.sendSMSFormat(param, Constants.SMS_LOGIN_OTP_MSG);
				sms_response = notification.sendSMS(list, sms_messge, false).get(0);
				if (((SMSResponse) sms_response).getSmsStatus().equalsIgnoreCase(ResponseCodes.SMS_SENT)) {
					staffInfo.setOtp_generated_time((new Date().getTime()));
					staffInfo.setOtpToken(String.valueOf(otp_token));
					staffInfoDao.updateAllProperties(staffInfo);
					response = new LoginResponse();
					((LoginResponse) response).setOtpRequire(staffInfo.isIs_otp_require());
					((LoginResponse) response).setStaff_guid(staff.getGuid());
					((LoginResponse) response).setSmsResponse(sms_response);
				} else {
					listOfError.add(new ValidationError(Constants.SMS_NOT_SENT, ErrorCodes.SMS_NOT_DELIVERED, ErrorCodes.SMS_NOT_DELIVERED));
					return new ErrorResponse(ResponseCodes.STAFF_ADDED_FAILURE, listOfError);
				}

				response.createResponse(  ResponseCodes.SMS_SENT, true);
				//}
			} else {
				/* If Staff is not eligible or sending OTP */
				//staffDao.update(staff);
				response = staffVerification(staff.getGuid(), null, true);
			}



		} else {

			if (isAuthenticated == Constants.INVALID_USERNAME) {
				response.createResponse(ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_USERNAME, false);
			} else if (isAuthenticated == Constants.WRONG_PASSWORD) {
				response.createResponse(ResponseCodes.SOCIAL_LOGIN_FAILURE_WRONG_PASSWORD, false);
			} else if (isAuthenticated == Constants.INVALID_ACCOUNT) {
				response.createResponse(ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_ACCOUNT, false);
			}

		}
		
		
		return response;
		
	}

	/* Staff Verification */

	/**
	 * Service method for login of staff member
	 */
	@Override
	public BaseResponse staffVerification(String guid, String token, boolean isFlag) {
		BaseResponse loginResponse = new LoginResponse();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.GUID, guid);
		Map<String,Object> resultMap = staffDao.findActiveStaffWithActiveRest(paramMap);
		
		Staff staff = (Staff) resultMap.get("staff");
		Restaurant rest = (Restaurant) resultMap.get("rest");
		StaffInfo staffInfo = (StaffInfo) resultMap.get("staffInfo");
		
		
		if (null != token && !isFlag && null != staff && null != staffInfo) {

			Calendar cal = Calendar.getInstance();
			long currentTime = cal.getTimeInMillis();
			if (null != staffInfo.getOtp_generated_time() && (currentTime - staffInfo.getOtp_generated_time()) < Constants.OTP_VALIDITY * 60 * 1000 && (staffInfo.getOtpToken().equalsIgnoreCase(token))) {
				loginResponse.setResponseCode(ResponseCodes.SMS_TOKEN_VERIFIED);
				loginResponse.setResponseMessage("SMS Tken Verified !");
				loginResponse.setResponseStatus(Boolean.valueOf(true));
				staffInfo.setOtpToken(null);
				staffInfo.setOtp_generated_time(null);
				isFlag = Boolean.TRUE;
			} 
		}

		if (null != staff && isFlag) {
			//Logger.debug("staff list size is ==========================================================" + staffList.size());

			staffInfo.setCurrentLoginTime(new Date().getTime());

			UserInfoModel userInfo = new UserInfoModel(staff);
			loginResponse = new LoginResponse(userInfo);
			String generatedToken = UtilityMethods.generateToken(Constants.STAFF + userInfo.getGuid() + userInfo.getRoleId());
			
			//generatedToken = Crypto.encryptAES(generatedToken);
			
			((LoginResponse) loginResponse).setToken(generatedToken);
			
			staffInfo.setToken(generatedToken);
			staffInfoDao.updateAllProperties(staffInfo);

			Calendar cldr = Calendar.getInstance();
			Date dt = cldr.getTime();
			((LoginResponse) loginResponse).setTimeinmilli(cldr.getTimeInMillis() + "");
			((LoginResponse) loginResponse).setTime(new SimpleDateFormat(Constants.TIMESTAMP_FORMAT).format(dt));
			((LoginResponse) loginResponse).setTimezone(new SimpleDateFormat("z").format(dt));
			((LoginResponse) loginResponse).setDateformat(Constants.TIMESTAMP_FORMAT);
			((LoginResponse) loginResponse).setRest(rest);

			
			Cache.set(generatedToken, userInfo,Constants.TTLForCache);
			
			if (null != token) {
				loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_SUCCESS_VALID_OTP, true);
			} else {
				loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_SUCCESS, true);
			}

		}
		else {
			if (!isFlag) {
				loginResponse.createResponse(ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_OTP, false);

			} else {
				loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_USER, false);

			}
		}
		return loginResponse;
	}

	/**
	 * Method for forgot password
	 * 
	 * @return
	 */
	@Override
	public BaseResponse forgotPassword(String email) {
		BaseResponse response = new BaseResponse();
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if (CustomValidations.isValidEmail(email)) {
			try {
				com.stormpath.sdk.application.Application application = StormpathConfig.getInstance().getApplication();
				// send password reset mail to the specified email id
				application.sendPasswordResetEmail(email);
				response.createResponse(ResponseCodes.EMAIL_SENT_SUCCESSFULLY, true);
			} catch (ResourceException e) {
				Logger.debug("Exception is ------------ " + e.getLocalizedMessage());
				listOfError.add(validateStaffObject.createError(Constants.EMAIL, ErrorCodes.EMAIL_ACCOUNT_NOT_EXIST));
				response = new ErrorResponse(ResponseCodes.EMAIL_SENT_FAILURE, listOfError);
			}
		} else {
			listOfError.add(validateStaffObject.createError(Constants.EMAIL, ErrorCodes.INVALID_EMAIL_FORMAT));
			response = new ErrorResponse(ResponseCodes.EMAIL_SENT_FAILURE, listOfError);

		}
		return response;
	}

	/**
	 * Method for reset password
	 * 
	 * @return
	 */
	@Override
	public BaseResponse resetPassword(String sptoken, String password) {
		BaseResponse response = new BaseResponse();
		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		Logger.debug("error list " + listOfError);
		try {

			com.stormpath.sdk.application.Application application = StormpathConfig.getInstance().getApplication();

			// Logger.debug("application is "+application);

			Logger.debug("token is " + sptoken + " password is " + password);

			/*
			 * Account account =
			 * application.verifyPasswordResetToken("$"+sptoken);
			 * Logger.debug("sptoken verified account is "+account);
			 */
			Account account = application.resetPassword(sptoken, password);

			Logger.debug("account is " + account);

			response.createResponse( ResponseCodes.PASSWORD_CHANGED_SUCCESFULLY, true);
		} catch (IllegalArgumentException ie) {
			Logger.debug("illegal argument exception is " + ie.getLocalizedMessage());
			listOfError.add(new ValidationError(Constants.SP_TOKEN, ie.getLocalizedMessage()));
			response = new ErrorResponse(ResponseCodes.PASSWORD_CHANGE_FAILURE, listOfError);
		} catch (ResourceException e) {
			// listOfError.add(validateStaffObject.createError(Constants.EMAIL,
			// ErrorCodes.EMAIL_ACCOUNT_NOT_EXIST));
			Logger.debug("exception is " + e.getDeveloperMessage());
			Logger.debug("code is " + e.getCode() + " cause is " + e.getCause() + " more info is " + e.getMoreInfo() + " status is " + e.getStatus() + " stormpath error is " + e.getStormpathError());
			if (e.getDeveloperMessage().contains("Password") || e.getDeveloperMessage().contains(Constants.PASSWORD) || e.getDeveloperMessage().contains(Constants.PASSWORD.toUpperCase())) {
				listOfError.add(new ValidationError(Constants.PASSWORD, e.getDeveloperMessage()));
				response = new ErrorResponse(ResponseCodes.PASSWORD_CHANGE_FAILURE, listOfError);
			}
			if (e.getDeveloperMessage().contains("The requested resource does not exist")) {
				listOfError.add(new ValidationError(Constants.SP_TOKEN, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_SP_TOKEN), ErrorCodes.INVALID_SP_TOKEN));
				response = new ErrorResponse(ResponseCodes.PASSWORD_CHANGE_FAILURE, listOfError);
			}
		}

		return response;
	}

	@Override
	public BaseResponse logOut(String token) {
		if(token != null)
		{
		token = Crypto.decryptAES(token);
		}
		
		
		BaseResponse response = new BaseResponse();
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if (token == null) {
			listOfError.add(new ValidationError(Constants.ACCESS_TOKEN, UtilityMethods.getErrorMsg(ErrorCodes.ACCESS_TOKEN_MISSING), ErrorCodes.ACCESS_TOKEN_MISSING));
			response = new ErrorResponse(ResponseCodes.LOG_OUT_FAILURE, listOfError);
		} else {
			
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put(Constants.TOKEN, token);
			Map<String,Object> resultMap = staffDao.findActiveStaffWithActiveRest(paramMap);
			
			StaffInfo staffInfo = (StaffInfo) resultMap.get("staffInfo");
			
			if(staffInfo == null){
				response = adminLogout(token, listOfError);
				return response;
			}
			
			List<String> loginHistory = (staffInfo.getLoginHistory() == null) ? new ArrayList<>() : staffInfo.getLoginHistory();
			List<String> logoutHistory = (staffInfo.getLogoutHistory() == null) ? new ArrayList<>() : staffInfo.getLogoutHistory();
			Long currentLoginTime = staffInfo.getCurrentLoginTime();
			
			if(loginHistory.size() == 10){
				loginHistory.remove(0);
			}
			
			if(logoutHistory.size() == 10){
				logoutHistory.remove(0);
			}
			
			loginHistory.add(currentLoginTime.toString());
			logoutHistory.add(String.valueOf(new Date().getTime()));
			staffInfo.setCurrentLoginTime(null);
			staffInfo.setLoginHistory(loginHistory);
			staffInfo.setLogoutHistory(logoutHistory);
			staffInfo.setToken(null);
			
			boolean removed = authorizationService.removeSession(token);
			if (removed) {
				staffInfoDao.updateAllProperties(staffInfo);
				
				response.createResponse( ResponseCodes.LOGGED_OUT_SUCCESSFULLY, true);
			} else {
				listOfError.add(new ValidationError(Constants.LOG_OUT, UtilityMethods.getErrorMsg(ErrorCodes.LOG_OUT_FAILURE), ErrorCodes.LOG_OUT_FAILURE));
				response = new ErrorResponse(ResponseCodes.LOG_OUT_FAILURE, listOfError);
			}
		}
		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse deleteStaffMember(String staffGuid, String token) {
		BaseResponse response;
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		if (staffGuid == null || staffGuid.equals(""))
		{
			listOfError.add(validateStaffObject.createError(Constants.GUID, ErrorCodes.STAFF_ID_REQUIRED));
			response = new ErrorResponse(ResponseCodes.STAFF_DELETION_FAILURE, listOfError);
			return response;
		}
		
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.GUID, staffGuid);
		paramMap.put("include_inactive", true);
		Map<String,Object> resultMap = staffDao.findActiveStaffWithActiveRest(paramMap);
		
		Staff staffObj = (Staff) resultMap.get("staff");
		Restaurant rest = (Restaurant) resultMap.get("rest");
		//StaffInfo staffInfo = (StaffInfo) resultMap.get("staffInfo");
		
		if (staffObj == null)
		{
			listOfError.add(validateStaffObject.createError(Constants.GUID, ErrorCodes.INVALID_STAFF_ID));
			response = new ErrorResponse(ResponseCodes.STAFF_DELETION_FAILURE, listOfError);
			return response;
		}

		staffObj.setUpdatedBy(userInfo.getGuid());
		staffObj.setStatus(Constants.DELETED_STATUS);
		Logger.debug("list of error is " + listOfError);


		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {

			if(!rest.getGuid().equals(userInfo.getRestGuid()))
			{
				listOfError.add(new ValidationError(Constants.REST_ID, UtilityMethods.getErrorMsg(ErrorCodes.NO_ACCESS_TO_CREATE_OR_UPDATE_STAFF_OF_OTHER_REST), ErrorCodes.NO_ACCESS_TO_CREATE_OR_UPDATE_STAFF_OF_OTHER_REST));
				response = new ErrorResponse(ResponseCodes.STAFF_DELETION_FAILURE, listOfError);
				return response;
			}

		}


		if (staffObj.getStatus().equals(Constants.DELETED_STATUS)) {

			if(userInfo.getGuid().equals(staffObj.getGuid()))
			{
				listOfError.add(validateStaffObject.createError(Constants.ACCESS_DENIED,
						ErrorCodes.USER_CANNOT_DELETE_HIS_OWN_ACCOUNT));
				response = new ErrorResponse(ResponseCodes.STAFF_UPDATION_FAILURE,listOfError);
				return response;

			}

			try
			{
			Client client = StormpathConfig.getInstance().getClient();
			client.getResource(Constants.STORMPATH_HREF_PATH_STRING + staffObj.getHref(), Account.class).delete();	
			}
			catch (ResourceException ex) {
				String loginFailureMsg = ex.getDeveloperMessage();
				Logger.debug("login failure message is " + loginFailureMsg);
				if (loginFailureMsg.contains("The requested resource does not exist")) {
					Logger.debug("User already deleted from stormpath");
				}
				
				if (loginFailureMsg.contains("The resource has been updated by another user causing a version conflict")) {
					Logger.debug("Version Conflict");
					
					listOfError.add(validateStaffObject.createError(Constants.STORMPATH_ERROR,
							ErrorCodes.VERSION_CONFLICT));
					response = new ErrorResponse(ResponseCodes.STAFF_UPDATION_FAILURE,listOfError);
					return response;
				}
				
				// System.out.println(ex.getDeveloperMessage());

			}
			
			
		}
		
		String staffToken = authorizationService.getTokenForStaff(staffObj.getGuid());
		if(staffToken != null)
		{
			logOut(staffToken);
		}
		
		Staff updatedStaffObj = staffDao.updateStaff(staffObj);
		response = new UpdateResponse<Staff>(ResponseCodes.STAFF_DELETED_SUCCESFULLY, updatedStaffObj.getGuid());
		return response;
	}

	@Override
	public BaseResponse addStaffMember(Staff staff) {

		BaseResponse response;
		staff.setStatus(Constants.ACTIVE_STATUS);
		List<ValidationError> listOfErrorForStaff = validateStaffObject.validateStaffOnAdd(staff);

		if (listOfErrorForStaff.isEmpty()) {
			Restaurant rest = null;
			if ((staff.getRestaurantGuid() != null) && (!staff.getRestaurantGuid().equals(""))) {
				rest = restValidator.validateGuid(staff.getRestaurantGuid(), listOfErrorForStaff);
				Logger.debug("finding restaurant " + rest);
				if (rest == null) {					
					Logger.debug("list of error is " + listOfErrorForStaff);
					response = new ErrorResponse(ResponseCodes.STAFF_ADDED_FAILURE, listOfErrorForStaff);
					return response;
				}

			}

			// if user is not ct admin then check if staff member being created
			// and logged in staff member both belongs to same restaurant
			/*
			 * if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
			 * listOfErrorForStaff = validateStaffObject
			 * .validateStaffForRestaurant(staff.getRestaurantGuid(),
			 * userInfo.getRestGuid()); if (!listOfErrorForStaff.isEmpty()) {
			 * response = new ErrorResponse( ResponseCodes.STAFF_ADDED_FAILURE,
			 * listOfErrorForStaff); return response; } }
			 */

			Logger.debug("list of error is empty");
			// If no validation error then add staff to stormpath within try
			// catch so that if exception
			// arises in adding user to storm path
			// response for failure is send
			try {

				staff.setHref(addStaffToStormPath(staff));
				Logger.debug("staff added to stormpath");

				/* Generation FireBase Token */
				try {
					Map<String, Object> authPayload = new HashMap<String, Object>();
					authPayload.put("uid", UtilityMethods.generateCtId());
					authPayload.put("token", Constants.FIREBASE_TOKEN_NAME);

					TokenOptions tokenOptions = new TokenOptions();
					tokenOptions.setAdmin(true);

					//TokenGenerator tokenGenerator = new TokenGenerator(Constants.FIREBASE_TOKEN);
					/*staff.setFirebase_token(tokenGenerator.createToken(authPayload, tokenOptions));*/
				} catch (Exception e) {
					e.printStackTrace();
				}

				/*
				 * Create relationship of staff with restaurant
				 */
				Staff newStaff = staffDao.create(staff);
				Logger.debug("staff created");
				// if restaurant guid is not null then create relationship of
				// restaurant and staff
				if ((staff.getRestaurantGuid() != null) && (!staff.getRestaurantGuid().equals(""))) {
					Long id = staffDao.addRestaurantStaff(rest.getGuid(), newStaff);
					Logger.debug("relationship id is " + id);
				}

				response = new PostResponse<Staff>(ResponseCodes.STAFF_ADDED_SUCCESFULLY, newStaff.getGuid());
			} catch (ResourceException e) {
				if (e.getDeveloperMessage().contains("Account with that email already exists")) {
					listOfErrorForStaff.add(new ValidationError(Constants.EMAIL, UtilityMethods.getErrorMsg(ErrorCodes.EMAIL_ACCOUNT_ALREADY_EXISTS), ErrorCodes.EMAIL_ACCOUNT_ALREADY_EXISTS));
				} else {
					listOfErrorForStaff.add(new ValidationError(Constants.STORMPATH_MODULE, e.getMessage()));
				}
				response = new ErrorResponse(ResponseCodes.STAFF_ADDED_FAILURE, listOfErrorForStaff);
			}

		} else {
			// if validation error then send error response
			response = new ErrorResponse(ResponseCodes.STAFF_ADDED_FAILURE, listOfErrorForStaff);
		}

		return response;

	}
	
	@Override
	public Oauth getOauthTokens(MultivaluedMap<String, String> formData) {
		Oauth oauth_model = null;
		try {
			String urlString = Constants.STORMPATH_HREF_APP_STRING + UtilityMethods.getConfigForModule(Constants.STROMPATH_APPLICATION_KEY).getString(Constants.STROMPATH_APPLICATION_ID) + Constants.OAUTH_TOKEN_PATH;

			String authrizationHeader = UtilityMethods.getConfigForModule(Constants.STROMPATH_APPLICATION_KEY).getString(Constants.STROMPATH_ID) + ":"
					+ UtilityMethods.getConfigForModule(Constants.STROMPATH_APPLICATION_KEY).getString(Constants.STROMPATH_SECRET);
			String encodeAuthrizationHeader = "Basic " + Base64.encodeBase64String(authrizationHeader.getBytes("UTF-8"));

			com.sun.jersey.api.client.Client restClient = com.sun.jersey.api.client.Client.create();
			WebResource webResource = restClient.resource(urlString);
			ClientResponse resp = webResource.header("Authorization", encodeAuthrizationHeader).type("application/x-www-form-urlencoded").post(ClientResponse.class, formData);
			oauth_model = Json.fromJson(Json.parse(resp.getEntity(String.class)), Oauth.class);
			if (resp.getStatus() != 200) {
				System.err.println("Unable to connect to the server");
			} else {

				// String response= resp.getEntity(String.class);
				oauth_model.setCode(Constants.SERVER_OK);
				oauth_model.setStatus(Constants.SERVER_OK);
				System.out.println("----- +++++ Refresh Token " + oauth_model.getAccess_token());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return oauth_model;
	}

	@Override
	public BaseResponse updateStatusStaffMember(Staff staff, String token) {
		BaseResponse response;

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);

		// staff.setUpdatedBy(userInfo.getGuid());

		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Staff staffObj = null;
		if (staff.getGuid() == null)
		{
			listOfError.add(validateStaffObject.createError(Constants.GUID,ErrorCodes.STAFF_ID_REQUIRED));
			response = new ErrorResponse(ResponseCodes.STAFF_UPDATION_FAILURE, listOfError);
			return response;
		}

		staffObj = staffDao.find(staff.getGuid());

		if (staffObj == null)
			listOfError.add(validateStaffObject.createError(Constants.GUID,ErrorCodes.INVALID_STAFF_ID));
		else {
			if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
				listOfError = validateStaffObject.validateStaffForRestaurant(staffObj.getRestaurantGuid(),userInfo.getRestGuid());
				if (!listOfError.isEmpty()) {
					response = new ErrorResponse(ResponseCodes.STAFF_UPDATION_FAILURE,listOfError);
					return response;
				}
			}

			staffObj.setUpdatedBy(userInfo.getGuid());

			Logger.debug("list of error is " + listOfError);
			listOfError.addAll(validateStaffObject.validateStaffOnPatchUpdate(staff));
			Logger.debug("staff validated");

		}

		if (!listOfError.isEmpty()) {
			response = new ErrorResponse(ResponseCodes.STAFF_UPDATION_FAILURE,listOfError);
			return response;

		}

		if (staffObj.getStatus().equals(Constants.DELETED_STATUS)) {
			listOfError.add(validateStaffObject.createError(Constants.ACCESS_DENIED,ErrorCodes.USER_ALREADY_DELETED));
			response = new ErrorResponse(ResponseCodes.STAFF_UPDATION_FAILURE, listOfError);
			return response;
		}

		if (staff.getStatus().equals(Constants.DELETED_STATUS)) {
			if (userInfo.getGuid().equals(staff.getGuid())) {
				listOfError.add(validateStaffObject.createError(Constants.ACCESS_DENIED,ErrorCodes.USER_CANNOT_DELETE_HIS_OWN_ACCOUNT));
				response = new ErrorResponse(ResponseCodes.STAFF_UPDATION_FAILURE,listOfError);
				return response;

			}
			Client client = StormpathConfig.getInstance().getClient();
			client.getResource(Constants.STORMPATH_HREF_PATH_STRING + staff.getHref(), Account.class).delete();
			String staffToken = authorizationService.getTokenForStaff(staffObj.getGuid());
			if(staffToken != null)
			{
				logOut(staffToken);
			}
			Logger.debug("staff member deleted");
		} else if (staff.getStatus().equals(Constants.INACTIVE_STATUS)) {

			String staffToken = authorizationService.getTokenForStaff(staffObj.getGuid());
			if(staffToken != null)
			{
				logOut(staffToken);
			}

		}

		staffObj.setStatus(staff.getStatus());

		Staff resultStaff = staffDao.updateStaff(staffObj);
		response = new UpdateResponse<Staff>(ResponseCodes.STAFF_UPDATED_SUCCESFULLY,resultStaff.getGuid());

		return response;
	}
	
	
	
	
	@Transactional
	@Override
	public BaseResponse logOutAllUsers(String token,Map<String,Object> params)
	{

		if(token != null)
		{
			token = Crypto.decryptAES(token);
		}
		BaseResponse response = new BaseResponse();
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if (token == null) {
			listOfError.add(new ValidationError(Constants.ACCESS_TOKEN, UtilityMethods.getErrorMsg(ErrorCodes.ACCESS_TOKEN_MISSING), ErrorCodes.ACCESS_TOKEN_MISSING));
			response = new ErrorResponse(ResponseCodes.LOG_OUT_FAILURE, listOfError);
			return response;
		} 

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.TOKEN, token);
		Map<String,Object> resultMap = staffDao.findActiveStaffWithActiveRest(paramMap);

		Staff staff = (Staff) resultMap.get("staff");
		Restaurant rest = (Restaurant) resultMap.get("rest");
		//StaffInfo staffInfo = (StaffInfo) resultMap.get("staffInfo");

		if(!staff.getRoleId().equals(Constants.ADMIN_ROLE_ID))
		{
			listOfError.add(validateStaffObject.createError(Constants.ACCESS_DENIED ,ErrorCodes.ONLY_ADMIN_HAS_ACCESS));
			response = new ErrorResponse(ResponseCodes.LOG_OUT_FAILURE,listOfError);
			return response;
		}

		paramMap = new HashMap<>();
		paramMap.put(Constants.REST_GUID, rest.getGuid());
		if(params != null && params.containsKey(Constants.STAFF_GUID))
		{
			paramMap.put(Constants.STAFF_GUID, params.get(Constants.STAFF_GUID));
		}
		List<StaffInfo> staffInfoList = staffDao.getLogOutUsersList(paramMap);
		List<StaffInfo> logoutList = new ArrayList<>();
		List<String> tokenList = new ArrayList<>();
		for(StaffInfo info : staffInfoList)
		{
			if(!info.getStaffGuid().equals(staff.getGuid()))
			{
				List<String> loginHistory = (info.getLoginHistory() == null) ? new ArrayList<>() : info.getLoginHistory();
				List<String> logoutHistory = (info.getLogoutHistory() == null) ? new ArrayList<>() : info.getLogoutHistory();
				Long currentLoginTime = info.getCurrentLoginTime();

				if(loginHistory.size() == 10){
					loginHistory.remove(0);
				}

				if(logoutHistory.size() == 10){
					logoutHistory.remove(0);
				}

				loginHistory.add(currentLoginTime.toString());
				logoutHistory.add(String.valueOf(new Date().getTime()));
				info.setCurrentLoginTime(null);
				info.setLoginHistory(loginHistory);
				info.setLogoutHistory(logoutHistory);
				tokenList.add(info.getToken());
				info.setToken(null);
				logoutList.add(info);

			}
		}
		try{
			for(StaffInfo sInfo : logoutList){
				staffInfoDao.updateAllProperties(sInfo);
				/*Cache.remove(sInfo.getToken());*/
			}
		}catch(Exception e){
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			response.createResponse( ResponseCodes.LOG_OUT_FAILURE, false);
			return response;
		}

		for(String accessToken : tokenList){
			Cache.remove(accessToken);
		}	

		response.createResponse( ResponseCodes.LOGGED_OUT_SUCCESSFULLY, true);

		return response;

	}

	@Override
	public BaseResponse setStaffInfo(String token,Map<String, Object> stringParamMap) 
	{
		// TODO Auto-generated method stub

		BaseResponse response;

		List<Staff> staffList = staffDao.findAll(Staff.class);
		List<Map<String,Object>> mapList = new ArrayList<>();

		for(Staff staff : staffList)
		{
			Map<String,Object> staffMap = new HashMap<>();
			staffMap.put("otp_require", staff.isIs_otp_require());
			staffMap.put("staff_guid", staff.getGuid());
			staffMap.put("guid", UtilityMethods.generateCtId());
			mapList.add(staffMap);

		}

		List<ValidationError> errorList = new ArrayList<>();

		for(Map<String, Object> map : mapList)
		{
			staffInfoDao.setStaffInfoList(map,errorList);
		}

		response = new GetResponse<>("11111", errorList);
		return response;

	}

	
	
	
	private BaseResponse adminVerification(String userName) {
		BaseResponse loginResponse = new LoginResponse();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.EMAIL, userName);
		List<Staff> staffList = staffDao.findByFields(Staff.class, paramMap);
		if(staffList == null ||  staffList.size() == 0)
		{
			loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_ACCOUNT, false);
			return loginResponse;
		}

		Staff staff = staffList.get(0);

		Map<String,Object> staffInfoMap = new HashMap<>();
		staffInfoMap.put("staffGuid", staffList.get(0).getGuid());

		List<StaffInfo> staffInfoList =  staffInfoDao.findByFields(StaffInfo.class, staffInfoMap);

		if(staffInfoList == null || staffInfoList.size() == 0)
		{
			loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_ACCOUNT, false);
			return loginResponse;
		}

		UserInfoModel userInfo = new UserInfoModel(staff);
		loginResponse = new LoginResponse(userInfo);
		String generatedToken = UtilityMethods.generateToken(Constants.STAFF + userInfo.getGuid() + userInfo.getRoleId());
		
		//generatedToken = Crypto.encryptAES(generatedToken);

		StaffInfo staffInfo = staffInfoList.get(0);
		staffInfo.setCurrentLoginTime(new Date().getTime());
		staffInfo.setToken(generatedToken);
		staffInfoDao.updateAllProperties(staffInfo);

		((LoginResponse) loginResponse).setToken(generatedToken);
		((LoginResponse) loginResponse).setRest(null);
		
		
		Calendar cldr = Calendar.getInstance();
		Date dt = cldr.getTime();
		((LoginResponse) loginResponse).setTimeinmilli(cldr.getTimeInMillis() + "");
		((LoginResponse) loginResponse).setTime(new SimpleDateFormat(Constants.TIMESTAMP_FORMAT).format(dt));
		((LoginResponse) loginResponse).setTimezone(new SimpleDateFormat("z").format(dt));
		((LoginResponse) loginResponse).setDateformat(Constants.TIMESTAMP_FORMAT);
		
		Cache.set(generatedToken, userInfo);
		loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_SUCCESS, true);
		return loginResponse;
	
	
	}
	
	
	private BaseResponse adminLogout(String token, List<ValidationError> listOfError) {
		BaseResponse loginResponse = new LoginResponse();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.TOKEN, token);

		List<StaffInfo> staffInfoList =  staffInfoDao.findByFields(StaffInfo.class, paramMap);

		if(staffInfoList == null || staffInfoList.size() == 0)
		{
			listOfError.add(new ValidationError(Constants.LOG_OUT, UtilityMethods.getErrorMsg(ErrorCodes.LOG_OUT_FAILURE), ErrorCodes.LOG_OUT_FAILURE));
			loginResponse = new ErrorResponse(ResponseCodes.LOG_OUT_FAILURE, listOfError);
			return loginResponse;
		}
		
		StaffInfo staffInfo = staffInfoList.get(0);

		List<String> loginHistory = (staffInfo.getLoginHistory() == null) ? new ArrayList<>() : staffInfo.getLoginHistory();
		List<String> logoutHistory = (staffInfo.getLogoutHistory() == null) ? new ArrayList<>() : staffInfo.getLogoutHistory();
		Long currentLoginTime = staffInfo.getCurrentLoginTime();
		
		if(loginHistory.size() == 10){
			loginHistory.remove(0);
		}
		
		if(logoutHistory.size() == 10){
			logoutHistory.remove(0);
		}
		
		loginHistory.add(currentLoginTime.toString());
		logoutHistory.add(String.valueOf(new Date().getTime()));
		staffInfo.setCurrentLoginTime(null);
		staffInfo.setLoginHistory(loginHistory);
		staffInfo.setLogoutHistory(logoutHistory);
		staffInfo.setToken(null);
		
		boolean removed = authorizationService.removeSession(token);
		if (removed) {
			staffInfoDao.updateAllProperties(staffInfo);
			loginResponse.createResponse( ResponseCodes.LOGGED_OUT_SUCCESSFULLY, true);
		} else {
			listOfError.add(new ValidationError(Constants.LOG_OUT, UtilityMethods.getErrorMsg(ErrorCodes.LOG_OUT_FAILURE), ErrorCodes.LOG_OUT_FAILURE));
			loginResponse = new ErrorResponse(ResponseCodes.LOG_OUT_FAILURE, listOfError);
		}
		return loginResponse;
	}
	
	
	
	
	/**
	 * Service method for login of staff member
	 */
	@Override
	public BaseResponse staffLoginWithCookies(String userName) {

		BaseResponse response = new LoginResponse();
		List<ValidationError> listOfError = new ArrayList<ValidationError>();


		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.EMAIL, userName);
		//List<Staff> staffList = staffDao.findByFields(Staff.class, paramMap);

		Map<String,Object> resultMap = null;

		if(userName.equals(UtilityMethods.getConfString("sysadmin.user")) || userName.equals(UtilityMethods.getConfString("ctadmin.user"))){
			response = adminVerificationWithCookies(userName);
			return response;
		}else{
			resultMap = staffDao.findActiveStaffWithActiveRest(paramMap);
		}

		Staff staff = (Staff) resultMap.get("staff");
		Restaurant rest = (Restaurant) resultMap.get("rest");
		StaffInfo staffInfo = (StaffInfo) resultMap.get("staffInfo");
		//Staff staff;

		if (staff == null || staffInfo == null) {
			response.createResponse( ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_ACCOUNT, false);
			return response;

		}

		if(rest == null)
		{
			listOfError.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.RESTAURANT_NOT_ACTIVE), ErrorCodes.INVALID_RESTAURANT_GUID));
			return new ErrorResponse(ResponseCodes.STAFF_LOGIN_FAILURE, listOfError);
		}


		response = staffVerificationWithCookies(staff , rest);

		return response;

	}
	
	
	
	private BaseResponse adminVerificationWithCookies(String userName) {
		BaseResponse loginResponse = new LoginResponse();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.EMAIL, userName);
		List<Staff> staffList = staffDao.findByFields(Staff.class, paramMap);
		if(staffList == null ||  staffList.size() == 0)
		{
			loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_FAILURE_USER_LOGGED_OUT, false);
			return loginResponse;
		}

		Staff staff = staffList.get(0);

		Map<String,Object> staffInfoMap = new HashMap<>();
		staffInfoMap.put("staffGuid", staffList.get(0).getGuid());

		List<StaffInfo> staffInfoList =  staffInfoDao.findByFields(StaffInfo.class, staffInfoMap);

		if(staffInfoList == null || staffInfoList.size() == 0)
		{
			loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_ACCOUNT, false);
			return loginResponse;
		}


		String generatedToken = UtilityMethods.generateToken(Constants.STAFF + staff.getGuid() + staff.getRoleId());
		//generatedToken = Crypto.encryptAES(generatedToken);
		
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(generatedToken);

		if(userInfo == null)
		{
			loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_ACCOUNT, false);
			return loginResponse;

		}

		loginResponse = new LoginResponse(userInfo);

		/*StaffInfo staffInfo = staffInfoList.get(0);
		staffInfo.setCurrentLoginTime(new Date().getTime());
		staffInfo.setToken(generatedToken);
		staffInfoDao.updateAllProperties(staffInfo);*/

		((LoginResponse) loginResponse).setToken(generatedToken);
		((LoginResponse) loginResponse).setRest(null);


		Calendar cldr = Calendar.getInstance();
		Date dt = cldr.getTime();
		((LoginResponse) loginResponse).setTimeinmilli(cldr.getTimeInMillis() + "");
		((LoginResponse) loginResponse).setTime(new SimpleDateFormat(Constants.TIMESTAMP_FORMAT).format(dt));
		((LoginResponse) loginResponse).setTimezone(new SimpleDateFormat("z").format(dt));
		((LoginResponse) loginResponse).setDateformat(Constants.TIMESTAMP_FORMAT);

		Cache.set(generatedToken, userInfo);
		loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_SUCCESS, true);
		return loginResponse;


	}
	
	
	
	
	/* Staff Verification with cookies*/

	private BaseResponse staffVerificationWithCookies(Staff staff, Restaurant rest) {
		BaseResponse loginResponse = new LoginResponse();

		/*Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.GUID, guid);
		Map<String,Object> resultMap = staffDao.findActiveStaffWithActiveRest(paramMap);

		Staff staff = (Staff) resultMap.get("staff");
		Restaurant rest = (Restaurant) resultMap.get("rest");
		StaffInfo staffInfo = (StaffInfo) resultMap.get("staffInfo");*/

		/*	
		if (null != token && !isFlag && null != staff && null != staffInfo) {

			Calendar cal = Calendar.getInstance();
			long currentTime = cal.getTimeInMillis();
			if (null != staffInfo.getOtp_generated_time() && (currentTime - staffInfo.getOtp_generated_time()) < Constants.OTP_VALIDITY * 60 * 1000 && (staffInfo.getOtpToken().equalsIgnoreCase(token))) {
				loginResponse.setResponseCode(ResponseCodes.SMS_TOKEN_VERIFIED);
				loginResponse.setResponseMessage("SMS Tken Verified !");
				loginResponse.setResponseStatus(Boolean.valueOf(true));
				staffInfo.setOtpToken(null);
				staffInfo.setOtp_generated_time(null);
				isFlag = Boolean.TRUE;
			} 
		}*/

		//if (null != staff && isFlag) {
		//Logger.debug("staff list size is ==========================================================" + staffList.size());

		//staffInfo.setCurrentLoginTime(new Date().getTime());

		//UserInfoModel userInfo = new UserInfoModel(staff);
		//loginResponse = new LoginResponse(userInfo);
		String generatedToken = UtilityMethods.generateToken(Constants.STAFF + staff.getGuid() + staff.getRoleId());
		
		generatedToken = Crypto.encryptAES(generatedToken);

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(generatedToken);

		if(userInfo == null)
		{
			loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_FAILURE_USER_LOGGED_OUT, false);
			return loginResponse;
		}

		loginResponse = new LoginResponse(userInfo);
		((LoginResponse) loginResponse).setToken(generatedToken);

		/*staffInfo.setToken(generatedToken);
			staffInfoDao.updateAllProperties(staffInfo);*/

		Calendar cldr = Calendar.getInstance();
		Date dt = cldr.getTime();
		((LoginResponse) loginResponse).setTimeinmilli(cldr.getTimeInMillis() + "");
		((LoginResponse) loginResponse).setTime(new SimpleDateFormat(Constants.TIMESTAMP_FORMAT).format(dt));
		((LoginResponse) loginResponse).setTimezone(new SimpleDateFormat("z").format(dt));
		((LoginResponse) loginResponse).setDateformat(Constants.TIMESTAMP_FORMAT);
		((LoginResponse) loginResponse).setRest(rest);
		loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_SUCCESS, true);


		Cache.set(generatedToken, userInfo,Constants.TTLForCache);

		/*if (null != token) {
				loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_SUCCESS_VALID_OTP, true);
			} else {
				loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_SUCCESS, true);
			}*/

		/*	}
		else {
			if (!isFlag) {
				loginResponse.createResponse(ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_OTP, false);

			} else {
				loginResponse.createResponse( ResponseCodes.SOCIAL_LOGIN_FAILURE_INVALID_USER, false);

			}
		}*/
		return loginResponse;
	}
	
	
	
}
