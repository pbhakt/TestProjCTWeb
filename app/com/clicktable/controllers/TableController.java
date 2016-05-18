package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Table;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.TableService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class TableController extends Controller {
	@Autowired
	TableService tableService;

	@Autowired
	AuthorizationService authService;

	public Result addTable() {
		Table table = Json.fromJson(request().body().asJson(), Table.class);
		if(!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			table.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		table.setStatus(Constants.ACTIVE_STATUS);
		BaseResponse response = tableService.addTable(table, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	//@Transactional
	public Result getTables() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = tableService.getTables(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = response.formatDateToJson();
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	
	
	public Result getBlockedTables()
	{
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = tableService.getBlockedTables(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = response.formatDateToJson();
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	

	//@Transactional
	public Result updateTable() {
		Table table = Json.fromJson(request().body().asJson(), Table.class);
		System.out.println(Json.toJson(table));
		String userId = authService.getLoggedInUser(request().getHeader(ACCESS_TOKEN));
		table.setUpdatedBy(userId);
		BaseResponse response = tableService.updateTable(table, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	public Result patchTable() 
	{
		JsonNode json =request().body().asJson();
		Table table = Json.fromJson(json, Table.class);
		String userId = authService.getLoggedInUser(request().getHeader(ACCESS_TOKEN));
		table.setUpdatedBy(userId);
		BaseResponse response = tableService.patchTable(table, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	public Result getCurrentTableStatus() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = tableService.getCurrentTableStatus(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = response.formatDateToJson();
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
