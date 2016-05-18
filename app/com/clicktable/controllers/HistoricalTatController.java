package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.HistoricalTat;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.HistoricalTatService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class HistoricalTatController extends Controller {

	@Autowired
	HistoricalTatService historicalTatService;

	@Autowired
	AuthorizationService authService;

	//@Transactional
	public Result addHistoricalTat() 
	{
		JsonNode json = request().body().asJson();
		HistoricalTat historicalTat = Json.fromJson(json, HistoricalTat.class);
		historicalTat.setGuid(UtilityMethods.generateCtId());
		BaseResponse response = historicalTatService.addHistoricalTat(historicalTat, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
		
	}



	public Result getHistoricalTats() 
	{

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.debug("========user info is====================="+userInfo);
		Logger.debug("role is=========="+userInfo.getRoleId()+" rest guid is==============="+userInfo.getRestGuid());
		
		if((userInfo.getRestGuid() != null) && (!userInfo.getRestGuid().equals("")))
		{
		stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}

		BaseResponse response = historicalTatService.getHistoricalTats(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	//@Transactional
	public Result updateHistoricalTat() 
	{
		JsonNode json = request().body().asJson();
		HistoricalTat historicalTat = Json.fromJson(json, HistoricalTat.class);
		BaseResponse response = historicalTatService.updateHistoricalTat(historicalTat, request().getHeader(ACCESS_TOKEN));
		Logger.debug("response in controller is ====="+response);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	/**
	 * Method to delete historicalTat node and relationships of restaurant and historicalTat
	 * @return
	 */
	public Result deleteHistoricalTat() {
		JsonNode json = request().body().asJson();
		Logger.debug("json in controller is "+json);
		String  historicalTatGuid = json.get(Constants.DEVICE_GUID).asText();
		Logger.debug(" historicalTatGuid is "+historicalTatGuid);
		
		
		BaseResponse response = historicalTatService.deleteHistoricalTat(historicalTatGuid, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
