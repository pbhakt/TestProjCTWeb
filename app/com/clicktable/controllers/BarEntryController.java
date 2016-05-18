package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.BarEntry;
import com.clicktable.model.Reservation;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.BarEntryService;
import com.clicktable.service.intf.TableShuffleService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class BarEntryController extends Controller {

	@Autowired
	BarEntryService barEntryService;

	@Autowired
	AuthorizationService authService;
	
	@Autowired
	TableShuffleService shuffleService;

	public Result addBarEntry() {
		String token = request().getHeader(ACCESS_TOKEN);
		BarEntry barEntry = Json.fromJson(request().body().asJson(), BarEntry.class);
		if (!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			barEntry.setInfoOnCreate(authService.getUserInfoByToken(token));
		barEntry.setStatus(Constants.CREATED);

		BaseResponse response = barEntryService.addBarEntry(barEntry);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result updateBarEntry() {
		String token = request().getHeader(ACCESS_TOKEN);
		Map<String,Object> barEntryMap = Json.fromJson(request().body().asJson(), Map.class);
		BaseResponse response = barEntryService.updateBarEntry(barEntryMap, token);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
		
	}

	public Result getBarEntry() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is " + userInfo.getRoleId());
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID))) {
			stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			stringParamMap.put(Constants.GUEST_GUID, userInfo.getGuid());
		}
		BaseResponse response = barEntryService.getBarEntry(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result moveToRestaurant() {
		String token = request().getHeader(ACCESS_TOKEN);
		JsonNode json = request().body().asJson();
		Map<String,Object> dataMap = Json.fromJson(json, Map.class);

		BaseResponse response = barEntryService.moveToRestaurant(dataMap, token);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result moveFromWaitlist() {
		String token = request().getHeader(ACCESS_TOKEN);
		JsonNode json = request().body().asJson();
		Reservation waiting = Json.fromJson(json, Reservation.class);
		BaseResponse response = barEntryService.moveFromWaitlist(waiting, token);
		
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		Map<String, Object> shuffleParams = new HashMap<>();
		shuffleParams.put(Constants.REST_ID, userInfo.getRestGuid());
		shuffleService.shuffleTables(shuffleParams, token);
		
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
