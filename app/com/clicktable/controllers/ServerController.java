package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Server;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ServerService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class ServerController extends Controller {

	@Autowired
	ServerService serverService;

	@Autowired
	AuthorizationService authService;

	//@Transactional
	public Result addServer() 
	{
		JsonNode json = request().body().asJson();
		Server server = Json.fromJson(json, Server.class);
		if(!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			server.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = serverService.addServer(server, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
		
	}



	public Result getServers() 
	{

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.debug("========user info is====================="+userInfo);
		Logger.debug("role is=========="+userInfo.getRoleId()+" rest guid is==============="+userInfo.getRestGuid());
		
		if((userInfo.getRestGuid() != null) && (!userInfo.getRestGuid().equals("")))
		{
		stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}

		BaseResponse response = serverService.getServers(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	public Result getRestaurantServers() 
	{

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.debug("========user info is====================="+userInfo);
		Logger.debug("role is=========="+userInfo.getRoleId()+" rest guid is==============="+userInfo.getRestGuid());
		
		if((userInfo.getRestGuid() != null) && (!userInfo.getRestGuid().equals("")))
		{
		stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}

		BaseResponse response = serverService.getRestaurantServers(stringParamMap ,request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	//@Transactional
	public Result updateServer() 
	{
		JsonNode json = request().body().asJson();
		Server server = Json.fromJson(json, Server.class);
		BaseResponse response = serverService.updateServer(server, request().getHeader(ACCESS_TOKEN));
		Logger.debug("response in controller is ====="+response);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	/**
	 * Method to delete server node and relationships of restaurant and server
	 * @return
	 */
	public Result deleteServer() {
		JsonNode json =request().body().asJson();
		Logger.debug("json in controller is "+json);
		String  serverGuid = json.get("serverGuid").asText();
		Logger.debug(" serverGuid is "+serverGuid);
		
		
		BaseResponse response = serverService.deleteServer(serverGuid, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
