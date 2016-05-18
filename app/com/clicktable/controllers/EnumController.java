package com.clicktable.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.response.BaseResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class EnumController extends Controller {

	public Result getAllEnums() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		Map<String, String> enumMap = UtilityMethods.getAllEnums();
		Logger.debug("enum map is " + enumMap);
		List<Map<String, String>> responseList = new ArrayList<Map<String, String>>();
		responseList.add(enumMap);
		BaseResponse response = new GetResponse<>(ResponseCodes.ENUM_DATA_FETCH_SUCCESFULLY, responseList);

		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
}
