package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.GuestBookService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class GuestBookController extends Controller {

	/*
	 * @Autowired Neo4jTemplate neo4jTemplate;
	 */

	@Autowired
	GuestBookService guestBookService;
	@Autowired
	AuthorizationService authService;

	public Result getGuestBook() {
		String restGuid = null;
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());

		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.debug("rest guid is " + userInfo.getRestGuid());
		if ((userInfo.getRestGuid() != null) && (!userInfo.getRestGuid().equals(""))) {
			restGuid = userInfo.getRestGuid();
			stringParamMap.put(Constants.REST_GUID, restGuid);
			stringParamMap.put(Constants.ROLE_ID, userInfo.getRoleId());
		}
		//stringParamMap.put(Constants.GUID, customerGuid);

		BaseResponse response = guestBookService.getGuestBookData(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
