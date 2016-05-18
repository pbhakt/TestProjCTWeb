package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Reservation;
import com.clicktable.model.Table;

@org.springframework.stereotype.Service
public interface TableShuffleDao 
{

 
    //Map<String, Object> getReservationForBlockTables(Map<String, Object> params);
    Map<String, List<Reservation>> getBlockedTables(Map<String, Object> queryMap, Map<String, List<Reservation>> resvList);
	//String getBestFitTable(Map<String,Object> params, Map<String, List<Reservation>> custTabList);
	Map<String, Table> getAllTables(Map<String, Object> params);
	//Map<String, List<Reservation>> getBestFitForWaitlist(Map<String, Object> params,Map<String, List<Reservation>> blockListMap);
	List<Reservation> getTablesHavingReservation(Map<String, Object> params);


}
