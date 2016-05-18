package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Onboarding;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.OnboardingService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class OnboardingController extends Controller {

	@Autowired
	OnboardingService onboardingService;

	@Autowired
	AuthorizationService authService;

	//@Transactional
	public Result addOnboardingRequest() {
		Onboarding onboard = Json.fromJson(request().body().asJson(), Onboarding.class);
		onboard.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = onboardingService.addOnboardingRequest(onboard,request().getHeader(ACCESS_TOKEN));

		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	//@Transactional
	public Result getOnboardingRequests() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = onboardingService.getOnboardingRequests(stringParamMap);
		JsonNode result = response.formatDateToJson();
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	//@Transactional
	public Result updateOnboardingRequest() {
		JsonNode json =request().body().asJson();
		Onboarding onboard = Json.fromJson(json, Onboarding.class);
		String userId = authService.getLoggedInUser(request().getHeader(ACCESS_TOKEN));
		onboard.setUpdatedBy(userId);
		BaseResponse response = onboardingService.updateOnboardingRequest(onboard,request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	/**
	 * Method for code verification
	 * @return
	 */
	
	public Result onboardingVerification() 
	{
		JsonNode json =request().body().asJson();
		
		String onboardGuid = json.get(Constants.GUID).asText().toString();
		String code = json.get(Constants.VERIFICATION_CODE).asText().toString();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.GUID, onboardGuid);
		params.put(Constants.VERIFICATION_CODE, code);
		
		String userId = authService.getLoggedInUser(request().getHeader(ACCESS_TOKEN));
		BaseResponse response = onboardingService.onboardingVerification(params, userId);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	/**
	 * Method to resend verification code
	 * @return
	 */
	
	public Result resendCode()
	{
		JsonNode json =request().body().asJson();
		String onboardGuid ="";
		if(json.get(Constants.GUID) != null)
		{
		onboardGuid = json.get(Constants.GUID).asText().toString();
		}
		BaseResponse response = onboardingService.resendCode(onboardGuid);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
}
