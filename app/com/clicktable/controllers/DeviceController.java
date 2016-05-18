package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Device;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.DeviceService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class DeviceController extends Controller {

	@Autowired
	DeviceService deviceService;

	@Autowired
	AuthorizationService authService;

	public Result addDevice() 
	{
		JsonNode json = request().body().asJson();
		Device device = Json.fromJson(json, Device.class);
		device.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = deviceService.addDevice(device, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);		
	}

	public Result getDevices() 
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

		BaseResponse response = deviceService.getDevices(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result updateDevice() 
	{
		JsonNode json = request().body().asJson();
		Device device = Json.fromJson(json, Device.class);
		BaseResponse response = deviceService.updateDevice(device, request().getHeader(ACCESS_TOKEN));
		Logger.debug("response in controller is ====="+response);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
		
	/**
	 * Method to delete device node and relationships of restaurant and device
	 * @return
	 */
	public Result deleteDevice() {
		JsonNode json = request().body().asJson();
		Logger.debug("json in controller is "+json);
		String  deviceGuid = json.get(Constants.DEVICE_GUID).asText();
		Logger.debug(" deviceGuid is "+deviceGuid);
				
		BaseResponse response = deviceService.deleteDevice(deviceGuid, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
