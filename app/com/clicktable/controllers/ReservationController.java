package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Reservation;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.SupportResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ReservationService;
import com.clicktable.service.intf.TableShuffleService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@org.springframework.stereotype.Controller
public class ReservationController extends Controller {

	@Autowired
	ReservationService reservationService;

	@Autowired
	AuthorizationService authService;
	
	@Autowired
	TableShuffleService shuffleService;

	public Result addReservation() {
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
		String token = request().getHeader(ACCESS_TOKEN);
		if (!(request().hasHeader(Constants.MODE) && request().getHeader(Constants.MODE).equals(Constants.APP)))
			reservation.setInfoOnCreate(authService.getUserInfoByToken(token));
		BaseResponse response = reservationService.createReservation(reservation, token, true);
		
		if(response instanceof PostResponse && reservation.getBookingMode().equals(Constants.ONLINE_STATUS))
		{
			Map<String, Object> resvParams = new HashMap<>();
			resvParams.put(Constants.REST_ID, reservation.getRestaurantGuid());
			resvParams.put("ShuffleReservation", false);
			shuffleService.shuffleTables(resvParams, token);
			
		}
		
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		Logger.info("**************RESERVATION CONTROLLER END ADD RESERVATION**************   " + (Calendar.getInstance().getTimeInMillis() - start));
		return ok(result);
	}
	
	
	
	public Result deleteReservation() {
		/*JsonNode json = request().body().asJson();
		Reservation reservation = Json.fromJson(json, Reservation.class);*/
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = reservationService.deleteReservation(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	
	
	

	public Result getReservation() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = reservationService.getReservation(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result updateReservation() {
		JsonNode json = request().body().asJson();
		Reservation reservation = Json.fromJson(json, Reservation.class);
		String userId = authService.getLoggedInUser(request().getHeader(ACCESS_TOKEN));
		reservation.setUpdatedBy(userId);
		BaseResponse response = new BaseResponse();   // reservationService.updateReservation(reservation, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result patchReservation() {
		JsonNode json = request().body().asJson();
		Reservation reservation = Json.fromJson(json, Reservation.class);
		String userId = authService.getLoggedInUser(request().getHeader(ACCESS_TOKEN));
		reservation.setUpdatedBy(userId);
		BaseResponse response = reservationService.patchReservation(reservation, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	/**
	 * Return Reservation stats for Dashboard display
	 * 
	 * @return
	 */
	public Result getReservationStats() {
		Map<String, Object> params = UtilityMethods.convertQueryStringToMap(request().queryString());

		UserInfoModel userInfo = authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN));
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID))) {
			params.put(Constants.REST_GUID, userInfo.getRestGuid());
		}

		BaseResponse response = reservationService.getReservationStats(params);
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result getQueueReservation() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = reservationService.getQueueReservation(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result convertReservationToWaitlist() {

		JsonNode json = request().body().asJson();
		Map<String, Object> params = new HashMap<String, Object>();
		String addToWaitlistGuid = json.get("addToWaitlistGuid").asText().toString();
		String seatWaitistGuid = json.get("seatWaitistGuid").asText().toString();
		String finishedReservationGuid = json.get("finishedReservationGuid").asText().toString();
		String cancelledReservationGuid = json.get("cancelledReservationGuid").asText().toString();
		String restaurantGuid = json.get(Constants.REST_GUID).asText().toString();
		String seatWithReduceTat = json.get("seatWithReduceTat").asText().toString();
		String reducedEndTime = json.get("reducedEndTime").asText().toString();
		ArrayNode tableGuidArr = null;
		if ((json.get("tableGuid") != null) && (json.get("tableGuid").isArray())) {
			tableGuidArr = (ArrayNode) json.get("tableGuid");
		}
		List<String> tableGuid = new ArrayList<String>();

		if (tableGuidArr != null) {
			Iterator itr = tableGuidArr.iterator();
			JsonNode node;
			while (itr.hasNext()) {
				node = (JsonNode) itr.next();
				tableGuid.add(node.asText());
			}

		}

		params.put("addToWaitlistGuid", addToWaitlistGuid);
		params.put("seatWaitistGuid", seatWaitistGuid);
		params.put("finishedReservationGuid", finishedReservationGuid);
		params.put("cancelledReservationGuid", cancelledReservationGuid);
		params.put(Constants.REST_GUID, restaurantGuid);
		params.put("reducedEndTime", reducedEndTime);
		params.put("seatWithReduceTat", seatWithReduceTat);
		params.put("tableGuid", tableGuid);
		
		if(json.get("guid") != null)
			params.put("guid", json.get("guid").asText().toString());
		if(json.get("requestTime") != null)
			params.put("requestTime", json.get("requestTime").asText().toString());
		if(json.get("isUnknown") != null)
			params.put("isUnknown", json.get("isUnknown").asText().toString());
		if(json.get("numCovers") != null)
			params.put("numCovers", json.get("numCovers").asText().toString());
		if(json.get("tat") != null)
			params.put("tat", json.get("tat").asText().toString());
		
		BaseResponse response = reservationService.convertReservationToWaitlist(params, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	public Result getReservationWithRespectToGuid() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		//stringParamMap.put(Constants.GUID, reservationGuid);

		BaseResponse response = reservationService.getReservationWithRespectToGuid(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}

	
	
	

	
	public Result addToQueue() {
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
		String token = request().getHeader(ACCESS_TOKEN);
		reservation.setInfoOnCreate(authService.getUserInfoByToken(token));
		BaseResponse response = reservationService.directlyAddToQueue(reservation, token);
		
		if(response instanceof PostResponse && reservation.getBookingMode().equals(Constants.ONLINE_STATUS))
		{
			Map<String, Object> resvParams = new HashMap<>();
			resvParams.put(Constants.REST_ID, reservation.getRestaurantGuid());
			resvParams.put("ShuffleReservation", false);
			shuffleService.shuffleTables(resvParams, token);
			
		}
		
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		Logger.info("**************RESERVATION CONTROLLER END ADD RESERVATION**************   " + (Calendar.getInstance().getTimeInMillis() - start));
		return ok(result);
	}
	
	
	public Result updateReservationViaSchedular() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		
		BaseResponse response = reservationService.updateReservationViaSchedular();
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}


}
