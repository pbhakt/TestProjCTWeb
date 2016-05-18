package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Calendar;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Reservation;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AllTablesWaitlistService;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ReservationService;
import com.clicktable.service.intf.WaitlistService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Controller
public class WaitlistController extends Controller {

	@Autowired
	WaitlistService waitlistService;
	
	@Autowired
	AllTablesWaitlistService allTablesWaitlistService;

	@Autowired
	AuthorizationService authService;
	
	@Autowired
	ReservationService reservationService;

	public Result getWaitlist() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = waitlistService.getWaitlistResult(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		// Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result getWaitlistForMobile() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = waitlistService.getWaitlistResultForMobile(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		// Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	

	public Result addToWaitlist() {

		long start = System.currentTimeMillis();
		Logger.info("**************RESERVATION CONTROLLER ADD RESERVATION**************   " + (Calendar.getInstance().getTimeInMillis() - start));
		JsonNode json = request().body().asJson();

		Reservation reservation = Json.fromJson(json, Reservation.class);
		if (null != json.get("isUnknown")) {
			if (json.get("isUnknown").asText().toString().equalsIgnoreCase("true")) {
				reservation.setIsUnknown(true);
			} else {
				reservation.setIsUnknown(false);
			}
		} else {
			reservation.setIsUnknown(false);
		}
		System.out.println(reservation.getEstStartTime());
		if (!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			reservation.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		
		reservation.setQueued(true);
		Boolean sendSms = true;
		BaseResponse response = reservationService.createReservation(reservation, request().getHeader(ACCESS_TOKEN),sendSms);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		Logger.info("**************RESERVATION CONTROLLER END ADD RESERVATION**************   " + (Calendar.getInstance().getTimeInMillis() - start));
		return ok(result);

	}
	
	
	
	
	public Result addToWaitlistForMobile() {

		long start = System.currentTimeMillis();
		Logger.info("**************RESERVATION CONTROLLER ADD RESERVATION**************   " + (Calendar.getInstance().getTimeInMillis() - start));
		JsonNode json = request().body().asJson();

		Reservation reservation = Json.fromJson(json, Reservation.class);
		if (null != json.get("isUnknown")) {
			if (json.get("isUnknown").asText().toString().equalsIgnoreCase("true")) {
				reservation.setIsUnknown(true);
			} else {
				reservation.setIsUnknown(false);
			}
		} else {
			reservation.setIsUnknown(false);
		}
		System.out.println(reservation.getEstStartTime());
		if (!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			reservation.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		
		reservation.setQueued(true);
		Boolean sendSms = false;
		BaseResponse response = reservationService.createReservation(reservation, request().getHeader(ACCESS_TOKEN), sendSms);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		Logger.info("**************RESERVATION CONTROLLER END ADD RESERVATION**************   " + (Calendar.getInstance().getTimeInMillis() - start));
		return ok(result);

	}
	
	
	
	public Result getWaitlistForAllTables() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = allTablesWaitlistService.getAllTablesWaitlistResult(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		// Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
	
	public Result getWaitlistForCovers() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = null ;//waitlistService.getWaitlistForCovers(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		// Logger.debug(Json.stringify(result));
		return ok(result);
	}

}
