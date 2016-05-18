package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.model.ApplicationDetails;
import com.clicktable.model.State;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AppDetailsService;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.StateService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

@org.springframework.stereotype.Controller
public class AppDetailsController extends Controller {

	@Autowired
	AppDetailsService appDetailsService;
	
	@Autowired
	AuthorizationService authService;
	
	public Result getAppDetails() {

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		BaseResponse response = appDetailsService.getAppDetails(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result addAppDetails() {
		ApplicationDetails appDetails = Json.fromJson(request().body().asJson(), ApplicationDetails.class);
		BaseResponse response = appDetailsService.addAppDetails(appDetails, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	

}
