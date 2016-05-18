package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Note;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.NoteService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class NoteController extends Controller {
	@Autowired
	NoteService noteService;

	@Autowired
	AuthorizationService authService;

	public Result addNote() {
		Note note = new Note();
		JsonNode json=request().body().asJson();

		String description = json.get(Constants.NOTE).asText();
		if(!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			note.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		note.setNote(description);

		BaseResponse response = noteService.addNote(note, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result getNotes() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = noteService.getNotes(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = response.formatDateToJson();
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result deleteNotes() {
		JsonNode json=request().body().asJson();
		String guid = json.get(Constants.GUID).asText();
		
		BaseResponse response = noteService.deleteNotes(guid, request().getHeader(ACCESS_TOKEN));
		JsonNode result = response.formatDateToJson();
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
