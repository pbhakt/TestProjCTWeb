package com.clicktable.controllers;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AddressService;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class AddressController extends Controller {

	@Autowired
	AddressService addressService;
	
	@Autowired
	AuthorizationService authService;
		
	
	public Result addMasterAddressData() {
		Map<String, Object> params = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = addressService.addAddress(params);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
}
