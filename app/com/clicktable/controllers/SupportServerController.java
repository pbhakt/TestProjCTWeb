/**
 * 
 */
package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.UserInfoModel;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * @author j.yadav
 *
 */
@org.springframework.stereotype.Controller
public class SupportServerController extends Controller {

	@Autowired
	AuthorizationService authService;

	public Promise<Result> callSupport(String all) {

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
		if ("application/json".equals(request().getHeader(CONTENT_TYPE))) {
			return wsHolder.get().map(resp -> ok(resp.asJson()));
		} else {
			return wsHolder.get().map(resp -> {
				if("application/x-download".equals(resp.getHeader("Content-Type"))){
					String fileName =  (String) stringParamMap.get("guid");
					response().setContentType("application/x-download");
					response().setHeader("Content-disposition", "attachment; filename=" + fileName+".Pdf");
					return ok(resp.getBodyAsStream());
				}
						return ok(resp.asJson());
				});
		}
	}
	
	public Promise<Result> masterData(String all) {

		JsonNode json = request().body().asJson();

		String token = request().getHeader(ACCESS_TOKEN);
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		
		if (userInfo == null || !userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))
			return Promise.promise(() -> ok("Can not access"));
		else {
			((ObjectNode) json).put("createdBy", userInfo.getGuid());
			((ObjectNode) json).put("updatedBy", userInfo.getGuid());
			Promise<WSResponse> wsResponse = WS.url(
					UtilityMethods.getConfString(Constants.CT_SUPPORT_URL)
							+ request().path()).post(json);
			return wsResponse.map(resp -> ok(resp.asJson()));
		}
	}
	
	public Promise<Result> updateMasterData(String all) {

		JsonNode json = request().body().asJson();

		String token = request().getHeader(ACCESS_TOKEN);
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		
		DateTime date = new DateTime();
		if (userInfo == null || !userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))
			return Promise.promise(() -> ok("Can not access"));
		else {
			((ObjectNode) json).put("createdBy", userInfo.getGuid());
			((ObjectNode) json).put("updatedBy", userInfo.getGuid());
			((ObjectNode) json).put("updatedDate", date.getMillis());
			
			Promise<WSResponse> wsResponse = WS.url(
					UtilityMethods.getConfString(Constants.CT_SUPPORT_URL)
							+ request().path()).put(json);
			return wsResponse.map(resp -> ok(resp.asJson()));
		}
	}

}
