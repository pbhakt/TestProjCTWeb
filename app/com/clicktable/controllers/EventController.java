
package com.clicktable.controllers;


import static com.clicktable.util.Constants.ACCESS_TOKEN;
import static com.clicktable.util.Constants.APP;
import static com.clicktable.util.Constants.MODE;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Event;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.DeleteResponse;
import com.clicktable.response.SupportResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.EventService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class EventController extends Controller {
	
	@Autowired
	EventService eventService;
	
	@Autowired
	AuthorizationService authService;
	
	public Result addEvent() {
		
		Event event = Json.fromJson(request().body().asJson(), Event.class);
		BaseResponse response;

		if(!(request().hasHeader(MODE) && request().getHeader(MODE).equals(APP)))
			event.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		response =  eventService.addEvent(event, request().getHeader(ACCESS_TOKEN));
		String token = request().getHeader(ACCESS_TOKEN);
			
			//code to shuffle on event creation
			UserInfoModel userInfo = authService.getUserInfoByToken(token);
			if(userInfo.getRestGuid() != null)
			{
				event.setRestaurantGuid(userInfo.getRestGuid());
				eventService.shuffleTable(event, token);
			}

					
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateEvent() {
		Event event = Json.fromJson(request().body().asJson(), Event.class);
		String token = request().getHeader(ACCESS_TOKEN);
		event.setInfoOnUpdate(authService.getUserInfoByToken(token));	
		BaseResponse response =  eventService.updateEvent(event, token);
		UserInfoModel userInfo = authService.getUserInfoByToken(token);

		if(response instanceof UpdateResponse)
		{
			Event updatedEvent = (Event) ((UpdateResponse<Event>) response).getEntityObject();
			if(userInfo.getRestGuid() != null)
			{
				eventService.shuffleTable(updatedEvent, token);
			}
		}


		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result getEvents(){

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		//Logger.info("role id is "+userInfo.getRoleId());
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals( Constants.CUSTOMER_ROLE_ID)))
		{
		stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		BaseResponse response =  eventService.getEvents(stringParamMap);
		JsonNode result = Json.toJson(response);
		//Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result getCalenderEvents()
	{
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is "+userInfo.getRoleId());
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals( Constants.CUSTOMER_ROLE_ID))){
			stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		BaseResponse response =  eventService.getCalenderEvents(stringParamMap,  request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	/*public Result getCalenderEventsReport()
	{
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is "+userInfo.getRoleId());
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals( Constants.CUSTOMER_ROLE_ID))){
			stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		BaseResponse response =  eventService.getCalenderEventsReport(stringParamMap,  request().getHeader(ACCESS_TOKEN));
		if (response.getResponseStatus()) {
			response().setContentType("application/x-download");
			response().setHeader("Content-disposition", "attachment; filename=" + Constants.RESERVATION_CSV_EXPORT_FILE_NAME);
			SupportResponse<File> fileResponse = (SupportResponse<File>) response;
			Logger.debug("fileResponse>>"+fileResponse.getResponseCode());
			Logger.debug("getObject>>");
			fileResponse.getObj().deleteOnExit();
			return ok(fileResponse.getObj());
		} 
			JsonNode result = Json.toJson(response);
			Logger.debug(Json.stringify(result));
			return ok(result);
		
	}
	
	public Result getCalenderEventsAttendenceReport()
	{
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is "+userInfo.getRoleId());
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals( Constants.CUSTOMER_ROLE_ID))){
			System.out.println("...........5............");
			stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		Logger.debug("?>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>156");
		BaseResponse response =  eventService.getCalenderEventAttendenceReport(stringParamMap,  request().getHeader(ACCESS_TOKEN));
		Logger.debug("?>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>158");
		if (response.getResponseStatus()) {
			response().setContentType("application/x-download");
			response().setHeader("Content-disposition", "attachment; filename=" + Constants.RESERVATION_CSV_EXPORT_FILE_NAME);
			SupportResponse<File> fileResponse = (SupportResponse<File>) response;
			Logger.debug("fileResponse>>"+fileResponse.getResponseCode());
			Logger.debug("getObject>>");
			fileResponse.getObj().deleteOnExit();
			return ok(fileResponse.getObj());
		} 
			JsonNode result = Json.toJson(response);
			Logger.debug(Json.stringify(result));
			return ok(result);
		
	}*/
	
	/*public Result patchEvent() {
		JsonNode json = request().body().asJson();
		Event event = Json.fromJson(json, Event.class);
		String token = request().getHeader(ACCESS_TOKEN);
		String userId = authService.getLoggedInUser(token);
		event.setUpdatedBy(userId);
		BaseResponse response = eventService.patchEvent(event, request().getHeader(ACCESS_TOKEN));
		
		if(response instanceof UpdateResponse)
		{
			Event updatedEvent = (Event) ((UpdateResponse<Event>) response).getEntityObject();
			UserInfoModel userInfo = authService.getUserInfoByToken(token);
			if(userInfo.getRestGuid() != null)
			{
				eventService.shuffleTable(updatedEvent, token);
			}
		}
		
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}*/
	
	public Result deleteCalendarEvent(){
		JsonNode json = request().body().asJson();
		CalenderEvent calEvent = Json.fromJson(json, CalenderEvent.class);
		calEvent.setInfoOnUpdate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = eventService.deleteCalendarEvent(calEvent, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result deleteEvent(){
		JsonNode json = request().body().asJson();
		Event event = Json.fromJson(json, Event.class);
		String token = request().getHeader(ACCESS_TOKEN);
		String userId = authService.getLoggedInUser(token);
		event.setUpdatedBy(userId);
		BaseResponse response = eventService.deleteEvent(event, token);
		UserInfoModel userInfo = authService.getUserInfoByToken(token);

		
		if(response instanceof DeleteResponse)
		{
			Event deletedEvent = (Event) ((DeleteResponse) response).getId();
			if(userInfo.getRestGuid() != null)
			{
				eventService.shuffleTable(deletedEvent, token);
			}
		}
		
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateCalendarEvent(){
		JsonNode json = request().body().asJson();
		CalenderEvent calEvent = Json.fromJson(json, CalenderEvent.class);
		calEvent.setInfoOnUpdate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = eventService.updateCalendarEvent(calEvent, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);

	}
	
	
}

