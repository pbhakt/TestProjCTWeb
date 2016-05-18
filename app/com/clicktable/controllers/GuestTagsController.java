package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.GuestTagModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.GuestTagsService;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class GuestTagsController extends Controller {

	@Autowired
	GuestTagsService guestTagsService;
	@Autowired
	AuthorizationService authService;
	
	/*Add Guest Tag with relationship */
	public Result addGuestTag() {
		GuestTagModel guestTagModel = Json.fromJson(request().body().asJson(), GuestTagModel.class);
		BaseResponse response = guestTagsService.addGuestTag(guestTagModel, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
   
	/*Fetch Guest Tag */
	public Result getGuestTag() {
		Map<String, Object> params = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = guestTagsService.getTag(params, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	/*Remove Guest Tag */
	public Result removeGuestTag() {
		GuestTagModel guestTagModel = Json.fromJson(request().body().asJson(), GuestTagModel.class);
		BaseResponse response = guestTagsService.removeGuestTag(guestTagModel, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	/*Merging Tag */
	public Result mergeTag() {
		BaseResponse response = guestTagsService.mergeTag(request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
}
