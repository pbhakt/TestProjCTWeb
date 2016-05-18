package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Locality;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.LocalityService;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class LocalityController extends Controller {

	@Autowired
	LocalityService localityService;
	
	@Autowired
	AuthorizationService authService;
	
	public Result getLocality() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		BaseResponse response = localityService.getLocalities(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result addLocality() {
		Locality locality = Json.fromJson(request().body().asJson(), Locality.class);
		BaseResponse response = localityService.addLocality(locality, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result deleteLocality() {
		JsonNode json = request().body().asJson();
		Locality locality = Json.fromJson(json, Locality.class);
		BaseResponse response = localityService.deleteLocality(locality, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateLocality() {
		JsonNode json =request().body().asJson();
		Locality locality = Json.fromJson(json, Locality.class);
		BaseResponse response = localityService.updateLocalityRequest(locality, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
