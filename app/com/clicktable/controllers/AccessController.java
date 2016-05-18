package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.GuestProfile;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.SupportResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CustomerLoginService;
import com.clicktable.service.intf.GuestHasTagsService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.twilio.sdk.LookupsClient;
import com.twilio.sdk.resource.instance.lookups.PhoneNumber;


/**
 */
@org.springframework.stereotype.Controller
public class AccessController extends Controller {

	@Autowired
	CustomerLoginService loginService;

	@Autowired
	GuestHasTagsService guestHasTagService;

	@Autowired
	AuthorizationService authService;

	/**
	 * Method authenticateGoogle.
	 * @param accessToken String
	 * @param socialID String
	 * @return Result
	 */
	public Result authenticateGoogle(String accessToken, String socialID) {
		System.out.println(" Google Calling ");
		Logger.debug("in google login controller");
		BaseResponse response = loginService.loginWithGoogle(accessToken,socialID);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	/**
	 * Method authenticateFacebook.
	 * @param accessToken String
	 * @param socialID String
	 * @return Result
	 */
	public Result authenticateFacebook(String accessToken, String socialID) {
		BaseResponse response = loginService.loginWithFacebook(accessToken,socialID);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	/**
	 * Method addCustomer.
	 * @return Result
	 */
	public Result addCustomer() {
		GuestProfile customer = Json.fromJson(request().body().asJson(), GuestProfile.class);
		/*if (!(request().hasHeader(MODE) && request().getHeader(MODE).equals(APP)))
			customer.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));*/
		Logger.debug("dob is " + customer.getDob());
		// Logger.debug("last login is "+customer.getLastLogin());
		BaseResponse response = loginService.addCustomer(customer, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	/**
	 * Method addCustomersFromCSV.
	 * @param restaurantGuid String represents the guid of restaurant for which guests are uploading 
	 * @return Result  
	 */
	public Result addCustomersFromCSV(String restaurantGuid) {
		File file= null;
			if(request().body().asRaw()!=null)
				file = request().body().asRaw().asFile();
		BaseResponse response = loginService.addCustomersFromCSV(file, request().getHeader(ACCESS_TOKEN), restaurantGuid);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	/*public Result getCustomersReport(String fileFormat,String restGuid) {
		//System.out.println("AccessController.getCustomersReport()"+fileFormat+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+restGuid);
		BaseResponse response = loginService.getCustomersReport(request().getHeader(ACCESS_TOKEN), restGuid,fileFormat);
		if (response.getResponseStatus()) {
			response().setContentType("application/x-download");
			response().setHeader("Content-disposition", "attachment; filename=" + Constants.GUEST_CSV_EXPORT_FILE_NAME);
			SupportResponse<File> fileResponse = (SupportResponse<File>) response;
			Logger.debug("fileResponse>>"+fileResponse.getResponseCode());
			Logger.debug("getObject>>");
			fileResponse.getObj().deleteOnExit();
			return ok(fileResponse.getObj());
		} else {
			JsonNode result = Json.toJson(response);
			Logger.debug(Json.stringify(result));
			return ok(result);
		}
	}*/

	/**
	 * Method getCustomers.
	 * @return Result
	 */
	public Result getCustomers() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug("query string map is " + stringParamMap);
		BaseResponse response = loginService.getCustomers(stringParamMap, request().getHeader(ACCESS_TOKEN));
		return ok(Json.toJson(response));

	}

	/**
	 * Method getCustomersCount.
	 * @return Result
	 */
	public Result getCustomersCount() {

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug("query string map is " + stringParamMap);

		BaseResponse response = loginService.getCustomers(stringParamMap, request().getHeader(ACCESS_TOKEN));
		return ok(Json.toJson(response));

	}

	/**
	 * Method updateCustomer.
	 * @return Result
	 */
	public Result updateCustomer() {
		BaseResponse response;
		GuestProfile customer = Json.fromJson(request().body().asJson(), GuestProfile.class);	
		if (customer == null) {
			response = new ErrorResponse("Json Parsing Error", null);
			JsonNode result = Json.toJson(response);
			Logger.debug(Json.stringify(result));
			return ok(result);
		}
		//GuestProfile guest = Json.fromJson(customer, GuestProfile.class);

		/*List<Tag> listTag = new ArrayList<Tag>();
		Tag tag=null;
		JsonNode tags = json.findValue("tag");
		if (tags.isArray()) {
			for (JsonNode tagjson : tags) {
				tag = Json.fromJson(tagjson, Tag.class);
				listTag.add(tag);
			}

		}*/		
		response = loginService.updateProfile(customer, request().getHeader(ACCESS_TOKEN), null);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	/**
	 * Method getCurrentServerTime.
	 * @return Result
	 */
	public Result getCurrentServerTime() {

		Map<String, String> currdate = new HashMap<String, String>();
		Calendar cldr = Calendar.getInstance();
		Date dt = cldr.getTime();
		currdate.put("timeinmilli", cldr.getTimeInMillis() + "");
		currdate.put("timezone", new SimpleDateFormat("z").format(dt));
		currdate.put("dateformat", Constants.TIMESTAMP_FORMAT);
		currdate.put("time", new SimpleDateFormat(Constants.TIMESTAMP_FORMAT).format(dt));
		return ok(Json.toJson(currdate));

	}

	
	/**
	 * Method verifyPhoneNumber.
	 * @return Result
	 */
	public Result verifyPhoneNumber() {

		JsonNode json = request().body().asJson();
		String mobile = json.get(Constants.MOBILE).asText().toString();
		String ACCOUNT_SID = UtilityMethods.getConfString("twilio.account_sid");
		String AUTH_TOKEN = UtilityMethods.getConfString("twilio.auth_token");

		boolean status = true;

		try {
			LookupsClient client = new LookupsClient(ACCOUNT_SID, AUTH_TOKEN);

			PhoneNumber number = client.getPhoneNumber(mobile);
			number.getCountryCode();
			//System.out.println(number.getCountryCode());
		   // System.out.println(number.getFormattedNumber());
		} catch (Exception tre) {
			System.out.println(tre.getLocalizedMessage());

			if (tre.getLocalizedMessage().contains("com.twilio.sdk.TwilioRestException: The requested resource /PhoneNumbers"))
			{
				Logger.debug("Not a valid number");
				status = false;
			}
		}

		Map<String, Boolean> res = new HashMap<String, Boolean>();
		res.put("is_valid", status);

		return ok(Json.toJson(res));

	}
	
	/**
	 * Method verifyEmail.
	 * @return Result
	 */
	public Result verifyEmail() {

		JsonNode json = request().body().asJson();
		String email = json.get(Constants.EMAIL).asText().toString();

		com.sun.jersey.api.client.Client client = new com.sun.jersey.api.client.Client();
		client.addFilter(new HTTPBasicAuthFilter("api_key",
				UtilityMethods.getConfString("mailgun.api_key")));
		WebResource webResource = client.resource("https://api.mailgun.net/v3"
				+ "/address/validate");
		MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
		queryParams.add("address", email);
		String str = webResource.queryParams(queryParams)
				.get(ClientResponse.class).getEntity(String.class);

		Map<String, Boolean> res = new HashMap<String, Boolean>();
		res.put("is_valid", Json.parse(str).get("is_valid").asBoolean());
		
		return ok(Json.toJson(res));
	}
	
	

	/**
	 * Method updateConsumerProfile.
	 * @return Result
	 */
	public Result updateConsumerProfile() {
		GuestProfile customer = Json.fromJson(request().body().asJson(), GuestProfile.class);		
		customer.setCreatedBy(customer.getGuid());
		customer.setUpdatedBy(customer.getGuid());
		BaseResponse response = loginService.updateConsumerProfile(customer);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	

	/**
	 * Method sendOTP.
	 * @return Result
	 */
	public Result sendOTP() {
		GuestProfile customer = Json.fromJson(request().body().asJson(), GuestProfile.class);
		customer.setCreatedBy(customer.getGuid());
		customer.setUpdatedBy(customer.getGuid());
		BaseResponse response = loginService.sendOTP(customer, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	/**
	 * Method verificationOTP.
	 * @return Result
	 */
	public Result verificationOTP() {
		JsonNode json = request().body().asJson();		
		String guid = json.get(Constants.GUID).asText().toString();
		String otp_token = json.get(Constants.TOKEN).asText().toString();
		BaseResponse response = loginService.customerVerification(guid,otp_token,request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);

	}
	
	//@Transactional
	 /**
	  * Method resendOTP.
	  * @return Result
	  */
	 public Result resendOTP() {
					JsonNode json = request().body().asJson();
					String guid = null;
					if(json.has(Constants.GUID))
						guid = json.get(Constants.GUID).asText().toString();
					BaseResponse response = loginService.customerResendOTP(guid);
					JsonNode result = Json.toJson(response);
					Logger.debug(Json.stringify(result));
					return ok(result);

		}
	 
	
	
	 /**
	  * Method deleteCustomer.
	  * @return Result
	  */
	 public Result deleteCustomer() {
		 System.out.println("/////////////////////"+request().body().asJson());
		 	GuestProfile customer = Json.fromJson(request().body().asJson(), GuestProfile.class);
		 	System.out.println("............................");
			BaseResponse response = loginService.deleteCustomer(customer, request().getHeader(ACCESS_TOKEN));
			
			JsonNode result = Json.toJson(response);
			
			Logger.debug(Json.stringify(result));
			return ok(result);
		}
	 
	
	

	
}
