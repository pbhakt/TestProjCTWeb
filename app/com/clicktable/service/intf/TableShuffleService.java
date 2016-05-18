package com.clicktable.service.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Reservation;
import com.clicktable.model.Table;
import com.clicktable.response.BaseResponse;
@org.springframework.stereotype.Service
public interface TableShuffleService 
{
    BaseResponse shuffleTables(Map<String, Object> stringParamMap, String header);

	
	//BaseResponse CheckForResvShuffle(Map<String, Object> params, String restaurantId);


	void addAllResvToBlockMap(List<Reservation> allReservationList,
			Map<String, List<Reservation>> blockListMap);


	Boolean checkForBlockedReservation(Reservation resv,
			Map<String, List<Reservation>> blockListMap, String tableGuid);


	Boolean shuffleTablesMethod(Reservation existingReservation, String reservationStatus);


	List<Reservation> cloneReservationList(List<Reservation> resvList);


	BaseResponse shuffleTableAPI(Map<String, Object> params, String token,
			Map<String, List<Reservation>> blockListMap);


	//Map<Integer, List<Table>> getAllTablesWithCovers();


	//void getAllTables(String restaurantId, List<ValidationError> errorList);


	Reservation allocateTableToWalkin(String tableGuid, Reservation resv,
			Long start_time, Long bestWaitTime, Long currentTblWaitTime,
			Map<String, Table> allTablesWithGuid);


	Boolean checkForBlockedReservationQuickSearch(Reservation resv,
			Map<String, List<Reservation>> blockListMap, String tableGuid,
			Map<String,String> blockReasonMap);
	
}