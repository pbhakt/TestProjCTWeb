package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CustomSettingService;
import com.clicktable.service.intf.RestaurantService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

@org.springframework.stereotype.Controller
public class CustomSettingController extends Controller {

	
	
	@Autowired
	CustomSettingService customSettingService;

	@Autowired
	AuthorizationService authService;
	
	public Result getSettings() {

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is "+userInfo.getRoleId());
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals( Constants.CUSTOMER_ROLE_ID)))
		{
		stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		
		BaseResponse response = customSettingService.getCustomSetting(stringParamMap);
		
		JsonNode result = Json.toJson(response);
		//Logger.debug(result.toString());
		return ok(result);
	}
	
}
