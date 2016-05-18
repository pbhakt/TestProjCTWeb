package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Region;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.RegionService;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class RegionController extends Controller {

	
	@Autowired
	RegionService regionService;
	
	@Autowired
	AuthorizationService authService;
	
	
	public Result getRegion() {

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		BaseResponse response = regionService.getRegion(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result addRegion() {
		Region region = Json.fromJson(request().body().asJson(), Region.class);
		BaseResponse response = regionService.addRegion(region, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result deleteRegion() {
		JsonNode json = request().body().asJson();
		Region region = Json.fromJson(json, Region.class);
		BaseResponse response = regionService.deleteRegion(region, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateRegion() {
		JsonNode json =request().body().asJson();
		Region region = Json.fromJson(json, Region.class);
		BaseResponse response = regionService.updateRegionRequest(region, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
}

