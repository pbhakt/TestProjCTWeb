package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Table;

@org.springframework.stereotype.Service
public interface QuickSearchDao 
{

    List<Table> getAllTables(Map<String, Object> params);

    Map<String, Table> getTablesHavingReservation(long startDate, long endDate, String restID,Map<String, Object> params, Map <String, Table> allTables, List<String> tableGuidList);
    
    /*Map<String, Table> getCalendarEvents(String restGuid,long startTime,long endTime,  Map<String,Table> allTables);*/

    Boolean getHoliday(String restGuid, long startTime);

    //List<Reservation> getReservations(long currentTime, long nextDayShiftStartTime, String restID, Map<String, Object> params);


	/*List<QuickSearchReservation> getAllTables(Map<String, Object> params);

	List<QuickSearchTablesWithCover> searchTablesWithCovers(Map<String, Object> params);

	List<QuickSearchReservation> getAllTablesNew(Map<String, Object> params);*/

}
