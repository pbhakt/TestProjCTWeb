package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.BlackOutHours;
import com.clicktable.model.OperationalHours;
import com.clicktable.model.RestSystemConfigModel;
import com.clicktable.model.Restaurant;
import com.clicktable.model.RestaurantContactInfo;
import com.clicktable.model.RestaurantContactInfoAdmin;
import com.clicktable.model.RestaurantGeneralInfo;
//import com.clicktable.model.RestaurantGeneralInfo;
import com.clicktable.model.Staff;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.RestaurantService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class RestaurantController extends Controller {

	@Autowired
	RestaurantService restService;

	@Autowired
	AuthorizationService authService;

	//@Transactional
	public Result addRestaurant() {
		JsonNode json = request().body().asJson();
		JsonNode restJson = json.get("restaurant");
		Restaurant rest = Json.fromJson(restJson, Restaurant.class);

		JsonNode staffJson = json.get("staff");
		Staff staff = Json.fromJson(staffJson, Staff.class);
		
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		rest.setInfoOnCreate(userInfo);
		staff.setInfoOnCreate(userInfo);

		BaseResponse response = restService.addRestaurant(rest, staff,request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}



	public Result getRestaurants() {

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is "+userInfo.getRoleId());
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals( Constants.CUSTOMER_ROLE_ID)))
		{
		stringParamMap.put(Constants.GUID, userInfo.getRestGuid());
		}

		Logger.debug("param map is "+stringParamMap);
		BaseResponse response = restService.getRestaurants(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	//@Transactional
	public Result updateRestaurant() 
	{
		JsonNode json =request().body().asJson();
		Restaurant rest = Json.fromJson(json, Restaurant.class);
		BaseResponse response = restService.updateRestaurant(rest, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	

	//@Transactional
		public Result updateRestaurantGeneralInfo() 
		{
			JsonNode json =request().body().asJson();
			RestaurantGeneralInfo rest = Json.fromJson(json, RestaurantGeneralInfo.class);
			BaseResponse response = restService.updateRestaurantGeneralInfo(rest, request().getHeader(ACCESS_TOKEN));
			JsonNode result = Json.toJson(response);
			Logger.debug(Json.stringify(result));
			return ok(result);
		}

	public Result updateContactInfo() 
	{
		JsonNode json =request().body().asJson();
		RestaurantContactInfo rest = Json.fromJson(json, RestaurantContactInfo.class);
		BaseResponse response = restService.updateContactInfo(rest, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	

	
	
	
	/**
	 * Method to make relationships of restaurant and attributes
	 * @return
	 */
	public Result addAttributes() {
		JsonNode json =request().body().asJson();
		Logger.debug("json in controller is "+json);
		
		String  restGuid = "";
		if(json.get(Constants.REST_GUID) != null)
		restGuid = json.get(Constants.REST_GUID).asText();
		
		
		String  attrGuid = json.get(Constants.ATTR_GUID).asText();
		
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)))
		{
			restGuid =  userInfo.getRestGuid();
		}
		
		
		Logger.debug("restguid is "+restGuid+" attrGuid is "+attrGuid);
		
		
		BaseResponse response = restService.addAttributes(restGuid, attrGuid, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	
	
	/**
	 * Method to make relationships of restaurant and tat
	 * @return
	 */
	public Result addSystemConfig() 
	{
		JsonNode json =request().body().asJson();
		Logger.debug("json in controller is "+json);
		
		RestSystemConfigModel restTat = Json.fromJson(json, RestSystemConfigModel.class);
		Logger.debug("rest tat is "+restTat);
		BaseResponse response = restService.addSystemConfig(restTat, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	/**
	 * Method to get tat info for a restaurant
	 * @return
	 */
	public Result getSystemConfig() {

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is "+userInfo.getRoleId());
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals( Constants.CUSTOMER_ROLE_ID)))
		{
		stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		
		BaseResponse response = restService.getSystemConfig(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	

	/**
	 * Method to make relationships of restaurant and section
	 * @return
	 */
	public Result addSection() {
		JsonNode json =request().body().asJson();
		Logger.debug("json in controller is "+json);
		String  restGuid = json.get(Constants.REST_GUID).asText();
		String  sectionGuid = json.get(Constants.SECTION_GUID).asText();
		
		Logger.debug("restguid is "+restGuid+" sectionGuid is "+sectionGuid);
		
		
		BaseResponse response = restService.addSection(restGuid, sectionGuid, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	/**
	 * Method to delete relationships of restaurant and section
	 * @return
	 */
	public Result deleteSection() {
		JsonNode json =request().body().asJson();
		Logger.debug("json in controller is "+json);
		String  restGuid = json.get(Constants.REST_GUID).asText();
		String  sectionGuid = json.get(Constants.SECTION_GUID).asText();
		
		Logger.debug("restguid is "+restGuid+" sectionGuid is "+sectionGuid);
		
		
		BaseResponse response = restService.deleteSection(restGuid, sectionGuid, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	/**
	 * Method to make relationships of restaurant and section
	 * @return
	 */
	public Result getSection() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is "+userInfo.getRoleId());
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals( Constants.CUSTOMER_ROLE_ID)))
		{
		stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}

		Logger.debug("param map is "+stringParamMap);
		BaseResponse response = restService.getRestaurantsSection(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	/**
	 * Method to make relationships of restaurant and section
	 * @return
	 */
	public Result getHistoricalTat() 
	{
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));

		if((userInfo != null) && (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals( Constants.CUSTOMER_ROLE_ID)))
		{
		stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}

		Logger.debug("param map is "+stringParamMap);
		BaseResponse response = restService.getHistoricalTat(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	/**
	 * Method to create operational Hours of the restaurant 
	 * @return
	 */
	public Result addOperationalHours() 
	{
		

		OperationalHours ophr = Json.fromJson(request().body().asJson(), OperationalHours.class);
		ophr.setDiningSlotsForShift();
		Logger.debug("OperationalHours json in controller is ::::::::::::::::::::::"+request().body().asJson());
		
		BaseResponse response = restService.addOperationalHours(ophr,request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result getOperationalHours() 
	{
    	Map<String, Object> params = UtilityMethods.convertQueryStringToMap(request().queryString());
    	params.put(Constants.TOKEN, request().getHeader(ACCESS_TOKEN));    	
    	Logger.debug(params.toString());
		BaseResponse response = restService.getOperationalHours(params);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	/**
	 * Method to create operational Hours of the Reservation 
	 * @return
	 */
	public Result addBlackOutHour() 
	{
		

		BlackOutHours ophr = Json.fromJson(request().body().asJson(), BlackOutHours.class);
		Logger.debug("OperationalHours json in controller is ::::::::::::::::::::::"+request().body().asJson());		
		BaseResponse response = restService.addBlackOutHours(ophr,request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result getBlackOutHour() 
	{
    	Map<String, Object> params = UtilityMethods.convertQueryStringToMap(request().queryString());
    	params.put(Constants.TOKEN, request().getHeader(ACCESS_TOKEN));    	
    	Logger.debug(params.toString());
		BaseResponse response = restService.getBlackOutHours(params);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	
	/**
	 * Method to delete relationships of restaurant with reservation,event and guests
	 * @return
	 */
	public Result cleanRestaurantData() 
	{
	    	Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = restService.cleanRestaurantData(stringParamMap , request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	public Result getWeather() 
	{
		BaseResponse response = restService.getRestaurantWeather(request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateContactInfoByCtAdmin() 
	{
		JsonNode json =request().body().asJson();
		RestaurantContactInfoAdmin rest = Json.fromJson(json, RestaurantContactInfoAdmin.class);
		BaseResponse response = restService.updateContactInfoCtAdmin(rest, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	

	public Result statusUpdateRestaurant() 
	{
		JsonNode json =request().body().asJson();
		Restaurant rest = Json.fromJson(json, Restaurant.class);
		BaseResponse response = restService.statusUpdateRestaurant(rest, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result reactivateRestaurant() 
	{
		JsonNode json =request().body().asJson();
		Staff staff = Json.fromJson(json, Staff.class);
		BaseResponse response = restService.reactivateRestaurant(staff, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	
}
