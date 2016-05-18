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

import com.clicktable.model.TagModelOld;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.GuestHasTagsService;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;


@org.springframework.stereotype.Controller
public class GuestHasTagsController extends Controller {


	@Autowired
	GuestHasTagsService guestHasTagsService;
	@Autowired
	AuthorizationService authService;

	/*Add Guest Tag with relationship */
	public Result addGuestTag() {
		String guest_guid = null;
		/*String rest_guid = null;*/
		TagModelOld tag = null;
		List<TagModelOld> listTag = new ArrayList<TagModelOld>();
		JsonNode json = request().body().asJson();
		guest_guid = json.findValue("guestGuid").textValue();
		//rest_guid = json.findValue("restGuid").textValue();
		JsonNode tags = json.findValue("tag");
		if (tags.isArray()) {
			for (JsonNode tagjson : tags) {
				tag = Json.fromJson(tagjson, TagModelOld.class);
				listTag.add(tag);
			}

		}
		BaseResponse response = guestHasTagsService.addGuestProfileTag(guest_guid,
				 listTag.get(0), request().getHeader(ACCESS_TOKEN));

		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
    /*Add Tag to Master Table */
	public Result addTag() {
		JsonNode json = request().body().asJson();
		JsonNode tags = json.findValue("tag");
		TagModelOld tag = null;
		List<TagModelOld> listTag = new ArrayList<TagModelOld>();
		if (tags.isArray()) {
			for (JsonNode tagjson : tags) {
				tag = Json.fromJson(tagjson, TagModelOld.class);
				listTag.add(tag);
			}
		}
		BaseResponse response = guestHasTagsService.addTagRestaurant(listTag,
				request().getHeader(ACCESS_TOKEN));

		JsonNode result = Json.toJson(response);

		return ok(result);
	}

	public Result getGuestTag() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = guestHasTagsService.getGuestHasTag(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result getTag() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = guestHasTagsService.getTag(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result removeGuestTag() {

		JsonNode json = request().body().asJson();
		String guid = json.findValue("guestGuid").textValue();
		String rest_guid = json.findValue("restGuid").textValue();
		JsonNode tags = json.findValue("tag");
		TagModelOld tag = null;
		List<TagModelOld> listTag = new ArrayList<TagModelOld>();
		if (tags.isArray()) {
			for (JsonNode tagjson : tags) {
				tag = Json.fromJson(tagjson, TagModelOld.class);
				listTag.add(tag);
			}
		}

		/*
		 * Call to "GuestProfileTagRestaurantService.java" to validate Tag GUID
		 * and Guest GUID
		 */
		BaseResponse response = guestHasTagsService.removeGuestHasTag(guid, listTag, rest_guid);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result removeTag() {

		JsonNode json = request().body().asJson();
		JsonNode tags = json.findValue("tag");
		TagModelOld tag = null;
		List<TagModelOld> listTag = new ArrayList<TagModelOld>();
		if (tags.isArray()) {
			for (JsonNode tagjson : tags) {
				tag = Json.fromJson(tagjson, TagModelOld.class);
				listTag.add(tag);
			}
		}

		/*
		 * Call to "GuestProfileTagRestaurantService.java" to validate Tag GUID
		 * and Guest GUID
		 */
		BaseResponse response = guestHasTagsService.removeTag(listTag);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
