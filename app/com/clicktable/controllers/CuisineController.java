package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Cuisine;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CuisineService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class CuisineController extends Controller {

	@Autowired
	CuisineService cuisineService;

	@Autowired
	AuthorizationService authService;

	public Result addCuisine() {
		Cuisine cuisine = Json.fromJson(request().body().asJson(), Cuisine.class);
		cuisine.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		cuisine.setStatus(Constants.ACTIVE_STATUS);
		BaseResponse response = cuisineService.addCuisine(cuisine);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result updateCuisine() {
		Cuisine cuisine = Json.fromJson(request().body().asJson(), Cuisine.class);
		// set Logged in user detail
		cuisine.setInfoOnUpdate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = cuisineService.updateCuisine(cuisine);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result getCuisines() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = cuisineService.getCuisines(stringParamMap);
		JsonNode result = response.formatDateToJson();
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result getRestaurantCuisines() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = cuisineService.getCuisinesRelationship(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = response.formatDateToJson();
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result addRestaurantCuisines() {
		JsonNode json =request().body().asJson();
		Set<String> cuisineGuids = new HashSet<String>();
		json.withArray("cuisineGuid").forEach(cuisineGuid -> cuisineGuids.add(cuisineGuid.asText()));
		String rest_guid = json.get("restGuid").textValue();
		BaseResponse response = cuisineService.addCuisineRelationship(cuisineGuids, rest_guid, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result removeRestaurantCuisines() {
		String rest_guid = null;
		JsonNode json = request().body().asJson();
		String cuisineGuid[] = UtilityMethods.replaceSpecialCharacter(json.findValues("cuisineGuid").toString());
		rest_guid = json.findValue("restGuid").textValue();
		BaseResponse response = cuisineService.removeCuisineRelationship(rest_guid, cuisineGuid, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);

	}

}
