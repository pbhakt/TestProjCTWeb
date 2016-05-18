package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.Reservation;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface ReservationService {

	/** Create Reservation
	 * @param reservation
	 * @param token 
	 * @param sendSms 
	 * @return
	 */
	BaseResponse createReservation(Reservation reservation, String token, Boolean sendSms);
	
	/**
	 * Get Reservations based on parameters
	 * @param params
	 * @param token 
	 * @return
	 */
	BaseResponse getReservation(Map<String,Object> params, String token);
	
	/**
	 * Update reservation details
	 * @param reservation
	 * @return
	 */
	//BaseResponse updateReservation(Reservation reservation, String token);

	BaseResponse getReservationStats(Map<String, Object> params);

	BaseResponse getQueueReservation(Map<String, Object> params,String token);

	/*BaseResponse getReservationsForTables(
		Map<String, Object> stringParamMap, String header);*/

	BaseResponse getReservationsForTables(Reservation reservation);

	BaseResponse convertReservationToWaitlist(Map<String, Object> params,String header);

	BaseResponse getReservationWithRespectToGuid(
		Map<String, Object> stringParamMap, String header);

	BaseResponse patchReservation(Reservation reservation, String token);

	BaseResponse getReservationsCSV(String token, Map<String,Object>queryMap);
	
	Boolean deleteWaitlist(String guid, String token);

	BaseResponse directlyAddToQueue(Reservation reservation, String token);

	
	BaseResponse deleteReservation(Map<String, Object> params, String token);

	BaseResponse updateReservationViaSchedular();

	
	
	
}
