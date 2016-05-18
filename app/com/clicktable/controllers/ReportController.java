package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.SupportResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ConversationService;
import com.clicktable.service.intf.EventService;
import com.clicktable.service.intf.ReportService;
import com.clicktable.service.intf.ReservationService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class ReportController extends Controller{

	@Autowired
	ReportService reportService;
	
	@Autowired
	ReservationService reservationService;
	
	@Autowired
	EventService eventService;
	
	@Autowired
	ConversationService conversationService;
	
	@Autowired
	AuthorizationService authService;
	
	public Result getCustomersReport() {
		String str = request().getHeader(ACCESS_TOKEN);
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = reportService.getCustomersReport(request().getHeader(ACCESS_TOKEN), stringParamMap);
		if (response.getResponseStatus()) {
			response().setContentType("application/x-download");
			response().setHeader("Content-disposition", "attachment; filename=" + Constants.GUEST_CSV_EXPORT_FILE_NAME);
			SupportResponse<File> fileResponse = (SupportResponse<File>) response;
			Logger.debug("fileResponse>>"+fileResponse.getResponseCode());
			Logger.debug("getObject>>");
			fileResponse.getObj().deleteOnExit();
			return ok(fileResponse.getObj());
		} else {
			JsonNode result = Json.toJson(response);
			Logger.debug(Json.stringify(result));
			return ok(result);
		}
	}
	
	public Result getReservationsReport() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = reportService.getReservationsReport(request().getHeader(ACCESS_TOKEN), stringParamMap);
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
	
	public Result getCalenderEventsReport()
	{
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is "+userInfo.getRoleId());
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals( Constants.CUSTOMER_ROLE_ID))){
			stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		BaseResponse response =  reportService.getCalenderEventsReport(stringParamMap,  request().getHeader(ACCESS_TOKEN));
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
		BaseResponse response =  reportService.getCalenderEventAttendenceReport(stringParamMap,  request().getHeader(ACCESS_TOKEN));
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
		
	}
	
	public Promise<Result> getConversationReport() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		stringParamMap.put("url", request().path());
		Promise<BaseResponse> promiseResponse = reportService.getEventPromotionReport(request().getHeader(ACCESS_TOKEN), stringParamMap);
		return promiseResponse.map(response->{
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
		});
	}
	
}
