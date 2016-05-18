package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.CorporateOffers;
import com.clicktable.model.Corporation;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CorporationService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class CorporationController extends Controller {

	@Autowired
	CorporationService corporationService;

	@Autowired
	AuthorizationService authService;

	public Result getCorporation() {

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		BaseResponse response = corporationService.getCorporation(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result addCorporation() {
		Corporation corporation = Json.fromJson(request().body().asJson(), Corporation.class);
		BaseResponse response = corporationService.addCorporation(corporation, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	/*
	 * public Result deleteCorporation() { JsonNode json =
	 * request().body().asJson(); Corporation corporation = Json.fromJson(json,
	 * Corporation.class); BaseResponse response =
	 * corporationService.deleteCorporation(corporation,
	 * request().getHeader(ACCESS_TOKEN)); JsonNode result =
	 * Json.toJson(response); Logger.debug(Json.stringify(result)); return
	 * ok(result); }
	 */

	public Result updateCorporation() {
		JsonNode json = request().body().asJson();
		Corporation corporation = Json.fromJson(json, Corporation.class);
		BaseResponse response = corporationService.updateCorporation(corporation, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	// Corporate Offers Controller

	public Result getCorporateOffers() {
		String token = request().getHeader(ACCESS_TOKEN);
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = corporationService.getCorporateOffers(stringParamMap, token);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result addCorporateOffers() {
		String token = request().getHeader(ACCESS_TOKEN);
		
		Map<String, Object> params = UtilityMethods.convertQueryStringToMap(request().queryString());	//no need to get query params in POST request
		
		CorporateOffers corporate_offers = Json.fromJson(request().body().asJson(), CorporateOffers.class);
		if (!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			corporate_offers.setInfoOnCreate(authService.getUserInfoByToken(token));
		
		BaseResponse response = corporationService.addCorporateOffers(corporate_offers, params, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result updateCorporateOffers() {
		JsonNode json = request().body().asJson();
		Map<String, Object> params = UtilityMethods.convertQueryStringToMap(request().queryString());  //no need 
		CorporateOffers corporate_offers = Json.fromJson(json, CorporateOffers.class);
		BaseResponse response = corporationService.updateCorporateOffers(corporate_offers, params, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	/*public Result updateCorporateOffersStatus() {
		JsonNode json = request().body().asJson();
		String status = json.get("status").toString();
		BaseResponse response = corporationService.update
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}*/

}
