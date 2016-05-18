package com.clicktable.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.LanguageService;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class LanguageController extends Controller {

	@Autowired
	LanguageService languageService;

	@Autowired
	AuthorizationService authService;

	



	public Result getLanguage() {

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		BaseResponse response = languageService.getLanguage(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	

}
