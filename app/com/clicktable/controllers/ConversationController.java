package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.EventPromotion;
import com.clicktable.model.GuestConversation;
import com.clicktable.model.Miscall;
import com.clicktable.model.Template;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ConversationService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class ConversationController extends Controller {

	@Autowired
	ConversationService conversationService;

	@Autowired
	AuthorizationService authService;
	
	public Result addEventPromotion() {
		EventPromotion eventPromotion = Json.fromJson(request().body().asJson(),EventPromotion.class);
		if(!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			eventPromotion.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		eventPromotion.setStatus(Constants.ACTIVE_STATUS);
		BaseResponse response = conversationService.addEventPromotion(eventPromotion, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result getGuestCount() {
		EventPromotion eventPromotion = Json.fromJson(request().body().asJson(), EventPromotion.class);
		BaseResponse response = conversationService.getEventPromotionGuestCount(eventPromotion, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result getEventPromotionFilteredGuest() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		EventPromotion eventPromotion = Json.fromJson(request().body().asJson(), EventPromotion.class);
		BaseResponse response = conversationService.getEventPromotionGuestInfo(eventPromotion, request().getHeader(ACCESS_TOKEN),stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	public Result getEventPromotion() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is " + userInfo.getRoleId());
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID))) {
			stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			stringParamMap.put(Constants.GUEST_GUID, userInfo.getGuid());
		}
		BaseResponse response = conversationService.getEventPromotion(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	/*public Promise<Result> genrateReport() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		stringParamMap.put("url", request().path());
		Promise<BaseResponse> promiseResponse = conversationService.getEventPromotionReport(request().getHeader(ACCESS_TOKEN), stringParamMap);
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
	}*/
	
	
	
	public Result addTemplate() {
		Template template = Json.fromJson(request().body().asJson(), Template.class);
		if(!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			template.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = conversationService.addTemplate(template, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	public Result getTemplate() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		Logger.info("role id is " + userInfo.getRoleId());
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID))) {
			stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			stringParamMap.put(Constants.GUEST_GUID, userInfo.getGuid());
		}
		BaseResponse response = conversationService.getTemplate(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	
	public Result addGuestConversation() {
		/*Map<String, Object> params = UtilityMethods.convertQueryStringToMap(request().queryString());
		SMS sms = Json.fromJson(Json.toJson(params), SMS.class);
		sms.setGuid(UtilityMethods.generateCtId());
		conversationService.addGuestResponse(sms);*/
		return ok();
	}

	public Result addRestaurantConversation() {
		GuestConversation conversation = Json.fromJson(request().body().asJson(), GuestConversation.class);
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		if(!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			conversation.setInfoOnCreate(userInfo);
		conversation.setSmsStatus(Constants.WAITING);
		conversation.setOrigin(Constants.CONVERSATION_ENUM_VALUE);
		conversation.setStatus(Constants.ACTIVE_STATUS);
		conversation.setSentBy(Constants.RESTAURANT_ENUM);
		conversation.setOriginGuid(userInfo.getGuid());
		BaseResponse response = conversationService.addConversation(conversation, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Promise<Result> getConversation() {
		
		Map<String, Object> stringParamMap = UtilityMethods
				.convertQueryStringToMap(request().queryString());

		String token = request().getHeader(ACCESS_TOKEN);
		UserInfoModel userInfo = authService.getUserInfoByToken(token);

		if ((userInfo.getRoleId().equals(Constants.ADMIN_ROLE_ID))
				|| (userInfo.getRoleId().equals(Constants.MANAGER_ROLE_ID))) {

			stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		} else if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))
			return Promise.promise(() -> ok("Can not access"));

		WSRequestHolder wsHolder = WS.url(UtilityMethods
				.getConfString(Constants.CT_SUPPORT_URL) + request().path());
		stringParamMap.forEach((key, value) -> {
			wsHolder.setQueryParameter(key, value.toString());
		});
		return wsHolder.get().map(resp -> ok(resp.asJson()));
	}
	
	
	public Promise<Result> delivery() {
		Map<String, Object> stringParamMap = UtilityMethods
				.convertQueryStringToMap(request().queryString());
		WSRequestHolder wsHolder = WS.url(UtilityMethods
				.getConfString(Constants.CT_SUPPORT_URL) + request().path());
		stringParamMap.forEach((key, value) -> {
			wsHolder.setQueryParameter(key, value.toString());
		});
		return wsHolder.get().map(resp -> ok(resp.asJson()));
	}
	
	public Result misscall() {
		Map<String, Object> params = UtilityMethods.convertQueryStringToMap(request().queryString());
		Miscall miscall = Json.fromJson(Json.toJson(params), Miscall.class);
		miscall.setGuid(UtilityMethods.generateCtId());
		conversationService.addGuestResponse(miscall);
		return ok();
	}
	
	public Result eventPromotion()
	{
		
		
		return ok();
	}

}
