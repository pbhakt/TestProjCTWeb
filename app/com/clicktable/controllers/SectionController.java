package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Section;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.SectionService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class SectionController extends Controller {

	@Autowired
	SectionService sectionService;

	@Autowired
	AuthorizationService authService;

	//@Transactional
	public Result addSection() 
	{
		JsonNode json = request().body().asJson();
		Section section = Json.fromJson(json, Section.class);
		section.setGuid(UtilityMethods.generateCtId());
		BaseResponse response = sectionService.addSection(section, request().getHeader(ACCESS_TOKEN));
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
		String  sectionGuid = json.get(Constants.GUID).asText();
		String  restGuid = null==json.get(Constants.REST_GUID)?"":json.get(Constants.REST_GUID).asText();
		BaseResponse response = sectionService.deleteSection(sectionGuid,restGuid, request().getHeader(ACCESS_TOKEN));
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
		BaseResponse response = sectionService.getSections(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateSection() {
		JsonNode json = request().body().asJson();
		Section section = Json.fromJson(json, Section.class);
		BaseResponse response = sectionService.updateRestaurant(section, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	


	/*public Result getSections() {

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		//UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		if((userInfo.getRoleId() != Constants.CT_ADMIN_ROLE_ID) && (userInfo.getRoleId() != Constants.CUSTOMER_ROLE_ID))
		{
		stringParamMap.put(Constants.GUID, userInfo.getRestGuid());
		}

		BaseResponse response = sectionService.getSections(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}*/

	/*//@Transactional
	public Result updateSection() {
		JsonNode json = request().body().asJson();
		Restaurant rest = Json.fromJson(json, Restaurant.class);
		BaseResponse response = restService.updateRestaurant(rest, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}*/

}
