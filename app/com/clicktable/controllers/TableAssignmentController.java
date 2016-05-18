package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.TableAssignment;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.TableAssignmentService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
/**
 * 
 * @author p.singh
 *
 */
public class TableAssignmentController extends Controller {
	@Autowired
	TableAssignmentService assignTableService;

	@Autowired
	AuthorizationService authService;

	// @Transactional
	public Result assignTable() {
		TableAssignment tableAssignment = Json.fromJson(request().body().asJson(), TableAssignment.class);
		BaseResponse response = assignTableService.assignTable(tableAssignment, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result unassignTable() {
		TableAssignment tableAssignment = Json.fromJson(request().body().asJson(), TableAssignment.class);
		BaseResponse response = assignTableService.unassignTable(tableAssignment, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result getTableAssignment(String serverGuid) {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.debug("rest guid is " + userInfo.getRestGuid());
		if(userInfo.getRestGuid() !=null && (!userInfo.getRestGuid().isEmpty())){
			stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		stringParamMap.put(Constants.SERVER_GUID, serverGuid);
		BaseResponse response = assignTableService.getTableAssignment(stringParamMap);
		JsonNode result = response.formatDateToJson();
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
