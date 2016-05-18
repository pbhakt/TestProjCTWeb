package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import play.Logger;

import com.clicktable.comparator.TableWaitlistSort;
import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.HistoricalTatDao;
import com.clicktable.dao.intf.QueueDao;
import com.clicktable.dao.intf.QuickSearchDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.TableShuffleDao;
import com.clicktable.dao.intf.TatDao;
import com.clicktable.dao.intf.WaitlistDao;
import com.clicktable.model.OperationalHours;
import com.clicktable.model.Reservation;
import com.clicktable.model.Shift;
import com.clicktable.model.Table;
import com.clicktable.model.TableWaitingTime;
import com.clicktable.model.WaitlistResult;
import com.clicktable.repository.TatRepo;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.service.intf.AllTablesWaitlistService;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ReservationService;
import com.clicktable.service.intf.TableShuffleService;
import com.clicktable.service.intf.WaitlistService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.ValidationError;

@Component
public class AllTablesWaitlistServiceImpl  implements AllTablesWaitlistService
{
	@Autowired
	AuthorizationService authService;
	
	@Autowired
	RestaurantValidator restaurantValidator;
	
	@Autowired
	QuickSearchDao quickSearchDao;
	
	@Autowired
	RestaurantDao restDao;
	
	@Autowired
	TatRepo tatRepo;
	
	@Autowired
	TatDao tatDao;
	
	@Autowired
	HistoricalTatDao histDao;
	
	@Autowired
	WaitlistDao waitlistDao;
	
	@Autowired
	QueueDao queueDao;
	
	@Autowired
	ReservationService resvService;
	
	@Autowired
	CustomerDao guestDao;
	
	@Autowired
	TableShuffleDao shuffleDao;
	
	@Autowired
	WaitlistService waitlistService;
	
	@Autowired
	TableShuffleService shuffleService;

	
	@Override
	public BaseResponse getAllTablesWaitlistResult(Map<String, Object> params, String token)
	{

		BaseResponse getResponse = new BaseResponse();
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		Map <String, Object> responseMap = new HashMap<>();

		if(!params.containsKey(Constants.TAT))
		{
			errorList.add(new ValidationError(Constants.TAT, UtilityMethods.getErrorMsg(ErrorCodes.TAT_REQUIRED), ErrorCodes.TAT_REQUIRED));
			
		}
		
		if(!errorList.isEmpty())
		{
			getResponse = new ErrorResponse(ResponseCodes.WAITLIST_RECORD_FETCH_FAILURE, errorList);
			return getResponse;
		}

		Long inputTat =  Long.valueOf(params.get(Constants.TAT).toString())*60*1000;

		responseMap = waitlistService.getTableWaitingList(params, token, errorList);

		if(!errorList.isEmpty())
		{
			getResponse = new ErrorResponse(ResponseCodes.WAITLIST_RECORD_FETCH_FAILURE, errorList);
			return getResponse;
		}

		Map<String,List<Reservation>> resvList = (Map<String, List<Reservation>>) responseMap.get("reservationList");
		Map<Integer,WaitlistResult> tblWaitMap = new HashMap<>();
		tblWaitMap = (Map<Integer, WaitlistResult>) responseMap.get("waitTimeResult");

		List<TableWaitingTime> availableTables = new ArrayList<>();
		//List<Table> tableList = entry.getValue();
		List<TableWaitingTime> tblWaitList = new ArrayList<>();
		Map<String, Table> tableMapCheck = new HashMap<>();

		for(Map.Entry<Integer, WaitlistResult> entry : tblWaitMap.entrySet())
		{
			WaitlistResult waitlist = entry.getValue();
			for(TableWaitingTime tblWaitTime : waitlist.getTblWaitlist())
			{
				if(!tableMapCheck.containsKey(tblWaitTime.getTableGuid()))
				{
					tableMapCheck.put(tblWaitTime.getTableGuid(), tblWaitTime.getTable());
					tblWaitList.add(tblWaitTime);
				}
			}
		}


		Collections.sort(tblWaitList, new TableWaitlistSort());
		for(TableWaitingTime tblWait : tblWaitList)
		{
			if(tblWait.getWaitTime() < 0 && tblWait.getAvailableTime().getTime() != 0)
			{
				tblWait.setAvailableTime(new Date());
			}
		}

		Long endTimeForTables = tblWaitList.get(tblWaitList.size() - 1).getAvailableTime().getTime() + inputTat;


		for(TableWaitingTime tblWaitTime : tblWaitList)
		{
			List<Reservation> reservationList = resvList.get(tblWaitTime.getTable().getGuid());
			Boolean isAvailable = true;
			Long availableTime = tblWaitTime.getAvailableTime().getTime();
			if(reservationList != null)
			{
				for(Reservation resv : reservationList)
				{
					if(resv.getEstStartTime().getTime() < endTimeForTables && resv.getEstStartTime().getTime() >= availableTime){
						isAvailable = false;
						break;
					}
				}
			}
			if(isAvailable){
				availableTables.add(tblWaitTime);
			}
		}

		getResponse = new GetResponse(ResponseCodes.WAITLIST_RECORD_FETCH_SUCCESFULLY,availableTables);

		return getResponse;
	}	


	private List<Shift> getShiftsForDay(OperationalHours op_hrs,String currentDay) 
	{
	    List<Shift> shiftList = new ArrayList<>();
	    if(currentDay.equalsIgnoreCase(Constants.SUNDAY))
	    {
		Logger.debug("Day is Sunday");
		shiftList = op_hrs.getSunday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.MONDAY))
	    {
		Logger.debug("Day is Monday");
		shiftList = op_hrs.getMonday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.TUESDAY))
	    {
		Logger.debug("Day is TUESDAY");
		shiftList = op_hrs.getTuesday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.WEDNESDAY))
	    {
		Logger.debug("Day is WEDNESDAY");
		shiftList = op_hrs.getWednesday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.THURSDAY))
	    {
		Logger.debug("Day is THURSDAY");
		shiftList = op_hrs.getThursday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.FRIDAY))
	    {
		Logger.debug("Day is FRIDAY");
		shiftList = op_hrs.getFriday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.SATURDAY))
	    {
		Logger.debug("Day is SATURDAY");
		shiftList = op_hrs.getSaturday();
	    }
	    
	    return shiftList;
	}
}
