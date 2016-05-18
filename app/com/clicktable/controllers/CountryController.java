package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Country;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CountryService;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class CountryController extends Controller {

	@Autowired
	CountryService countryService;

	@Autowired
	AuthorizationService authService;

	public Result addCountry() {
		JsonNode json = request().body().asJson();
		List<Country> countries = new ArrayList<Country>();
		json.forEach(x -> countries.add(Json.fromJson(x, Country.class)));
		BaseResponse response = countryService.addCountry(countries, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);

	}

	public Result getCountry() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		BaseResponse response = countryService.getCountry(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result deleteCountry() {
		JsonNode json = request().body().asJson();
		Country country = Json.fromJson(json, Country.class);
		BaseResponse response = countryService.deleteCountry(country, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateCountry() {
		JsonNode json =request().body().asJson();
		Country country = Json.fromJson(json, Country.class);
		//String userId = authService.getLoggedInUser(request().getHeader(ACCESS_TOKEN));
		BaseResponse response = countryService.updateCountryRequest(country, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
}
