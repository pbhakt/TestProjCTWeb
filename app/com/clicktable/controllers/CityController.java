package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.City;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CityService;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class CityController extends Controller {

	@Autowired
	CityService cityService;
	
	@Autowired
	AuthorizationService authService;
	
	public Result getCity() {
		
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		BaseResponse response = cityService.getCity(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}	
	
	public Result addCity() {
		City city = Json.fromJson(request().body().asJson(), City.class);
		BaseResponse response = cityService.addCity(city, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result deleteCity() {
		JsonNode json = request().body().asJson();
		City city = Json.fromJson(json, City.class);
		BaseResponse response = cityService.deleteCity(city, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateCity() {
		JsonNode json =request().body().asJson();
		City city = Json.fromJson(json, City.class);
		BaseResponse response = cityService.updateCityRequest(city, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
}
