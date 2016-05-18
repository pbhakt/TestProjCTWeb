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
import com.clicktable.service.intf.TableShuffleService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * @author p.singh
 * 
 */

@org.springframework.stereotype.Controller
public class TableShuffleController extends Controller 
{

	@Autowired
	TableShuffleService shuffleService;
	
	@Autowired
	AuthorizationService authService;
	
	public Result shuffleTables() 
	{
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());		
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is "+userInfo.getRoleId());
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals( Constants.CUSTOMER_ROLE_ID)))
		{
		stringParamMap.put(Constants.REST_ID, userInfo.getRestGuid());
		}
		
		BaseResponse response = shuffleService.shuffleTables(stringParamMap, request().getHeader(ACCESS_TOKEN));  
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	/*public Result seatReservation() 
	{
		BaseResponse response = new BaseResponse();
		
		Map<String, Object> stringParamMap = new HashMap<String, Object>();
		
		JsonNode json = request().body().asJson();
		// get username,old password and new password from the json
		String reservationGuid = json.get("reservationGuid").asText().toString();
		String tableGuid = json.get("tableGuid").asText().toString();
		String restGuid = json.get("restGuid").asText().toString();
		
		stringParamMap.put("reservationGuid", reservationGuid);
		stringParamMap.put("tableGuid", tableGuid);
		 response = shuffleService.CheckForResvShuffle(stringParamMap, restGuid);  
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}*/


	
}
