package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Building;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.BuildingService;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class BuildingController extends Controller {

	@Autowired
	BuildingService buildingService;
	
	@Autowired
	AuthorizationService authService;
	
	public Result getBuilding() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		BaseResponse response = buildingService.getBuilding(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result addBuilding() {
		Building building = Json.fromJson(request().body().asJson(), Building.class);
		BaseResponse response = buildingService.addBuilding(building, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result deleteBuilding() {
		JsonNode json = request().body().asJson();
		Building building = Json.fromJson(json, Building.class);
		BaseResponse response = buildingService.deleteBuilding(building, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateBuilding() {
		JsonNode json =request().body().asJson();
		Building building = Json.fromJson(json, Building.class);
		BaseResponse response = buildingService.updateBuildingRequest(building, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
}