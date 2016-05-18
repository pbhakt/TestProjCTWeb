package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.api.libs.Crypto;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Http.Cookies;
import play.mvc.Result;

import com.clicktable.model.Staff;
import com.clicktable.model.UserInfoModel;
import com.clicktable.model.UserToken;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.StaffService;
import com.clicktable.service.intf.UserTokenService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;
import com.clicktable.response.LoginResponse;

@org.springframework.stereotype.Controller
public class StaffController extends Controller {

	@Autowired
	StaffService staffService;
	
	@Autowired
	UserTokenService userTokenService;
	
	@Autowired
	AuthorizationService authService;
	/**
	 * Controller to add staff
	 * 
	 * @return
	 */
	//@Transactional
	public Result addStaff() {
		
		JsonNode json = request().body().asJson();
		
		Staff staff = Json.fromJson(json, Staff.class);
		staff.setGuid(UtilityMethods.generateCtId());
		BaseResponse response = staffService.addStaffMember(staff, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	/**
	 * Controller to get list of staff members
	 * 
	 * @return
	 */
	public Result getStaff() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.debug("rest guid is "+userInfo.getRestGuid());
		if(userInfo.getRestGuid() !=null && (!userInfo.getRestGuid().isEmpty()))
		{
		stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		BaseResponse response = staffService.getStaffMembers(stringParamMap);
		JsonNode result = response.formatDateToJson();
		Logger.debug(Json.stringify(result));
		return ok(result);
		
	}

	/**
	 * Controller to update staff member
	 * 
	 * @return
	 */
	//@Transactional
	public Result updateStaff() {
		JsonNode json = request().body().asJson();
		Staff staff = Json.fromJson(json, Staff.class);
		
		BaseResponse response = staffService.updateStaffMember(staff, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);

	}

	/**
	 * Controller to change login password of staff member
	 * 
	 * @return
	 */
	//@Transactional
	public Result changePassword() {
		JsonNode json = request().body().asJson();
		// get username,old password and new password from the json
		String userName = json.get(Constants.USERNAME).asText().toString();
		String oldPassword = json.get(Constants.OLD_PASSWORD).asText().toString();
		String newPassword = json.get(Constants.NEW_PASSWORD).asText().toString();
		BaseResponse response = staffService.changePassword(userName, oldPassword, newPassword);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	/**
	 * Controller for login of staff
	 * 
	 * @return
	 */
	//@Transactional
	public Result staffLogin() {

		Logger.debug("In staff login controller");
		JsonNode json = request().body().asJson();

		BaseResponse response = null;
		String userName ="";
		Iterator<Cookie> i = request().cookies().iterator();

		Cookie cookie = null;
		while(i.hasNext())
		{
			Cookie cookie1 = i.next();
			
			String key = cookie1.name();
			if(key.equals("UserName"))
			{
				cookie = cookie1;
			}
		
		}

		if(cookie != null && (!json.hasNonNull(Constants.USERNAME))  && (!json.hasNonNull(Constants.PASSWORD)))
		{
			String userNameStr = cookie.value();
			userName = Crypto.decryptAES(userNameStr);
			response = staffService.staffLoginWithCookies(userName);
		}
		else
		{
			// get username and password from the json
			userName = new String(Base64.getDecoder().decode(json.get(Constants.USERNAME).asText().toString()));
			String password = new String(Base64.getDecoder().decode(json.get(Constants.PASSWORD).asText().toString()));
			Logger.debug("username is "+userName+" password is "+password);
			response = staffService.staffLogin(userName, password);

		}

		if(response.getResponseStatus() && ((LoginResponse)response).getToken() != null){
			/*response().setCookie(Constants.ACCESS_TOKEN, ((LoginResponse)response).getToken());*/
			 response().setCookie("UserName",Crypto.encryptAES(userName));		
			//((LoginResponse)response).setRefreshToken(null);

			((LoginResponse)response).setToken(Crypto.encryptAES(((LoginResponse)response).getToken()));
		}	
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));

		return ok(result);

	}
	
    
	
	//@Transactional
		public Result verificationOTP() {
			JsonNode json = request().body().asJson();
			// get username and password from the json
			String guid = json.get(Constants.GUID).asText().toString();
			String token = json.get(Constants.TOKEN).asText().toString();
			BaseResponse response = staffService.staffVerification(guid, token,false);
			if(response.getResponseStatus() && ((LoginResponse)response).getToken() != null){
				/*response().setCookie(Constants.ACCESS_TOKEN, ((LoginResponse)response).getToken());*/
				 response().setCookie("UserName",Crypto.encryptAES(((LoginResponse)response).getUserInfo().getEmail()));		
				//((LoginResponse)response).setRefreshToken(null);

				((LoginResponse)response).setToken(Crypto.encryptAES(((LoginResponse)response).getToken()));
			}	
			JsonNode result = Json.toJson(response);
			Logger.debug(Json.stringify(result));
			return ok(result);

		}

		//@Transactional
	 public Result resendOTP() {
					JsonNode json = request().body().asJson();
					String guid = null;
					if(json.has(Constants.GUID))
						guid = json.get(Constants.GUID).asText().toString();
					BaseResponse response = staffService.staffResendOTP(guid);
					JsonNode result = Json.toJson(response);
					Logger.debug(Json.stringify(result));
					return ok(result);

		}

	/**
	 * Controller for forgot password.
	 * 
	 * @return
	 */
	//@Transactional
	public Result forgotPassword() {
		JsonNode json =request().body().asJson();
		// get email id of staff member from the json
		String email = json.get(Constants.EMAIL).asText().toString();
		BaseResponse response = staffService.forgotPassword(email);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	/**
	 * Controller for reset password.
	 * 
	 * @return
	 */
	//@Transactional
	public Result resetPassword() {
	     
	       
		JsonNode json = request().body().asJson();
		Logger.debug("json in controller is "+json);
		// get email id of staff member from the json
		String sptoken = "", password = "";
		if(json.get(Constants.SP_TOKEN) != null)
		{
		sptoken = json.get(Constants.SP_TOKEN).asText().toString();
		}
		if(json.get(Constants.PASSWORD) != null)
		{
		password = json.get(Constants.PASSWORD).asText().toString();
		}
		BaseResponse response = staffService.resetPassword(sptoken,password);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result logOut() {
		String token = request().getHeader(ACCESS_TOKEN);
		BaseResponse response = staffService.logOut(token);
		
		if(response.getResponseStatus()){

			Iterator<Cookie> i = request().cookies().iterator();

			if(i.hasNext())
			{
				response().discardCookie("UserName");
			}


		}	
		
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	/**
	 * Controller to delete staff member
	 * 
	 * @return
	 */
	//@Transactional
	public Result deleteStaff() {
		JsonNode json = request().body().asJson();
		Staff staff = Json.fromJson(json, Staff.class);
		
		BaseResponse response = staffService.deleteStaffMember(staff.getGuid(), request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);

	}
	
	public Result addStaffToken() {
		JsonNode json = request().body().asJson();
		UserToken user_token = Json.fromJson(json, UserToken.class);
		BaseResponse response = userTokenService.add_UserToken(user_token);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	public Result getRefreshToken() {/*
		JsonNode result=null ;
		Iterator<Cookie> i=request().cookies().iterator();
		String userToken=null;
		String refreshToken=null;
		while(i.hasNext()){
			Cookie cookie=i.next();
			if(null!=cookie && cookie.name().equalsIgnoreCase("UserName")){
				userToken=cookie.value();
			}
			if(null!=cookie && cookie.name().equalsIgnoreCase("RefreshToken")){
				refreshToken=cookie.value();
			}
		}
			
		MultivaluedMap<String,String> formData=new MultivaluedMapImpl();
		
		formData.add("grant_type", "refresh_token");
		formData.add("refresh_token",refreshToken);				
		Oauth oauth=staffService.getOauthTokens(formData);
		if(null!=oauth && !oauth.getCode().equalsIgnoreCase(Constants.OAUTH_REFRESH_TOKEN_EXPIRED)){


		Map<String,String> map=new HashMap<String,String>();
		Long ttl=Calendar.getInstance().getTimeInMillis()+ Long.valueOf(oauth.getExpires_in())*1000;
		map.put("TTL", String.valueOf(ttl));
		map.put("TokenType",oauth.getToken_type());
		map.put("Authorization", oauth.getAccess_token());		
		
		Cache.remove(userToken);
		
		response().setCookie("RefreshToken", oauth.getRefresh_token());
		response().setCookie("UserName", userToken);
		Cache.set(userToken, map, (Integer.parseInt(oauth.getExpires_in())+Constants.CACHE_LIFE_LIVE));
		oauth.setRefresh_token(null);
		result =Json.toJson(oauth);
		
		
		}else{
			BaseResponse response=new BaseResponse();
			response.setResponseCode(ErrorCodes.REFRESH_TOKEN_EXPIRED);
			response.setResponseMessage(Constants.REFRESH_TOKEN_EXPIRED);
			result=Json.toJson(response);
			return forbidden(result);
			 
		}
		
		
		Logger.debug(Json.stringify(result));
		
		return ok(result);
<<<<<<< HEAD
	}
	
	
=======
	*/
		return null;
		}

	   
	public Result patchStatusStaff() {
		JsonNode json = request().body().asJson();
		Staff staff = Json.fromJson(json, Staff.class);
		BaseResponse response = staffService.updateStatusStaffMember(staff, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);

	}
	
	
	public Result logOutAllUsers() {
		String token = request().getHeader(ACCESS_TOKEN);
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = staffService.logOutAllUsers(token, stringParamMap);
		
		if(response.getResponseStatus()){

			Iterator<Cookie> i = request().cookies().iterator();

			if(i.hasNext())
			{
				response().discardCookie("UserName");
			}


		}	
		
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	public Result setStaffInfo() {
		String token = request().getHeader(ACCESS_TOKEN);
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = staffService.setStaffInfo(token, stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	
	
	
}
