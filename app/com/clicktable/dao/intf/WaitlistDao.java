package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Reservation;
import com.clicktable.model.Table;

@org.springframework.stereotype.Service
public interface WaitlistDao 
{

   
    //Map<String, List<Reservation>> getReservations(long currentTime, long nextDayShiftStartTime, String restID, );

    List<Object> getAllTables(String restaurantId);

    /*Map<String, List> getCalendarEvents(String restaurantId, long startTime);*/

    Map<String, List<Reservation>> getReservations(Map<String, Object> params);

    Map<Integer, Long> getActualTat(Map<String, Object> params);

    List<Table> getAllTablesList(Map<String, Object> params);

    Map<String, List<Reservation>> getReservationsExceptProvidedOne(long currentTime, long currentDayShiftStartTime,long nextDayShiftStartTime, String restID,Map<String, Object> params, String resvGuid);
}
