package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Attribute;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.service.intf.AttributeService;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class AttributeController extends Controller {

	@Autowired
	AttributeService attributeService;

	@Autowired
	AuthorizationService authService;

	public Result addAttribute() 
	{
		Attribute attribute = Json.fromJson(request().body().asJson(), Attribute.class);
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		attribute.setInfoOnCreate(userInfo);
		BaseResponse response = attributeService.addAttribute(attribute);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);		
	}
	
	public Result addAttributes() 
	{
		BaseResponse response = new BaseResponse();
		JsonNode json = request().body().asJson();
		JsonNode attributes = json.findValue("attribute");		
		List<Attribute> attributeList = new ArrayList<Attribute>();
		
		if (attributes.isArray()) {
			for (JsonNode attrJson : attributes) {
				Attribute attribute =  Json.fromJson(attrJson, Attribute.class);
				attribute.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
				attributeList.add(attribute);
			}
		}
		
		//Attribute attribute = Json.fromJson(request().body().asJson(), Attribute.class);
		PostResponse<Attribute> res = (PostResponse<Attribute>)attributeService.addAttributes(attributeList);
		List<String> list_of_attr = Arrays.asList((String[])res.getGuid());
		if(list_of_attr.isEmpty()){
			response.createResponse( "Attributes Addition Failure!", false);
		}else{
			response.createResponse("Attributes Added Successfully!", true);
		}
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);		
	}


	public Result getAttributes() {

		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		Logger.debug(stringParamMap.toString());
		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)))
		{
			stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
		}

		BaseResponse response = attributeService.getAttributes(stringParamMap);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	/**
	 * Method to make relationships of country and attributes
	 * @return
	 */
	public Result addCountryAttributes() {
		JsonNode json = request().body().asJson();
		Logger.debug("json in controller is "+json);
		String  countryGuid = json.get(Constants.COUNTRY_GUID).asText();
		String  attrGuid = json.get(Constants.ATTR_GUID).asText();
		
		Logger.debug("countryGuid is "+countryGuid+" attrGuid is "+attrGuid);
		
		
		BaseResponse response = attributeService.addCountryAttributes(countryGuid, attrGuid, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
