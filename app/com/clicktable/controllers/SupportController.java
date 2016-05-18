package com.clicktable.controllers;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.zendesk.client.v2.model.Attachment;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.ComplaintRequest;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.SupportService;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class SupportController extends Controller {

	@Autowired
	SupportService supportService;

	public Result submitTicket() {
		ComplaintRequest complaint = Json.fromJson(request().body().asJson(), ComplaintRequest.class);
		BaseResponse response = supportService.submitTicket(complaint);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result uploadAttachment(String fileName) {
		File file = request().body().asRaw().asFile();
		BaseResponse response = supportService.addAttachment(file, fileName);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result deleteAttachment(Long id) {
		Attachment attachment = new Attachment();
		attachment.setId(id);
		BaseResponse response = supportService.deleteAttachment(attachment);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result getToken() {
		BaseResponse response = supportService.getToken();
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}