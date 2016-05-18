package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.ReportingPreference;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.GuestHasTagsService;
import com.clicktable.service.intf.ReportingPreferenceService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;


@org.springframework.stereotype.Controller
public class ReportingController extends Controller {

	@Autowired
	ReportingPreferenceService preferenceService;

	@Autowired
	GuestHasTagsService guestHasTagService;

	@Autowired
	AuthorizationService authService;

	
	public Result addPreference() {
		String token = request().getHeader(ACCESS_TOKEN);
		ReportingPreference preference = Json.fromJson(request().body().asJson(), ReportingPreference.class);
		if (!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			preference.setInfoOnCreate(authService.getUserInfoByToken(token));
		//barEntry.setStatus(Constants.CREATED);
		BaseResponse response = preferenceService.addReportingPreferences(preference, token);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result getPreferences() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug("query string map is " + stringParamMap);
		BaseResponse response = preferenceService.getReportingPreferences(stringParamMap);
		return ok(Json.toJson(response));
	}

	public Result updatePreference() {
		BaseResponse response;
		String token = request().getHeader(ACCESS_TOKEN);

		ReportingPreference preference = Json.fromJson(request().body().asJson(), ReportingPreference.class);
		if (preference == null) {
			response = new ErrorResponse("Json Parsing Error", null);
			JsonNode result = Json.toJson(response);
			Logger.debug(Json.stringify(result));
			return ok(result);
		}
		response = preferenceService.updateReportingPreferences(preference, token);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	 public Result deletePreference() {
		 /*System.out.println("/////////////////////"+request().body().asJson());
		 	GuestProfile customer = Json.fromJson(request().body().asJson(), GuestProfile.class);
		 	System.out.println("............................");
			BaseResponse response = loginService.deleteCustomer(customer, request().getHeader(ACCESS_TOKEN));
			
			JsonNode result = Json.toJson(response);
			
			Logger.debug(Json.stringify(result));
			return ok(result);*/
			return null;
		}
	

	
}
