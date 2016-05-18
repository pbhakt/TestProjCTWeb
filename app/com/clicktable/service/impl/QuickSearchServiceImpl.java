package com.clicktable.service.impl;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.clicktable.dao.intf.QueueDao;
import com.clicktable.dao.intf.QuickSearchDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.TableShuffleDao;
import com.clicktable.dao.intf.TatDao;
import com.clicktable.dao.intf.WaitlistDao;
import com.clicktable.model.OperationalHours;
import com.clicktable.model.QuickSearchReservationNew;
import com.clicktable.model.Reservation;
import com.clicktable.model.Shift;
import com.clicktable.model.Table;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.QuickSearchService;
import com.clicktable.service.intf.WaitlistService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.ValidationError;

@Component
public class QuickSearchServiceImpl  implements QuickSearchService
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
	TatDao tatDao;
	
	@Autowired
	WaitlistService waitlistService;
	
	@Autowired
	WaitlistDao waitlistDao;
	
	@Autowired
	TableShuffleDao shuffleDao;
	
	@Autowired
	TableShuffleServiceImpl shuffleService;
	
	@Autowired
	QueueDao queueDao;
	

	
	//new methods for quick search
	
	@Override
	public BaseResponse getQuickSearchResult(Map<String, Object> params, String token) 
	{
		// TODO Auto-generated method stub
		List<ValidationError> errorList = new ArrayList<ValidationError>();

		BaseResponse getResponse = null;

		long tat = 0l;
		Long bufferTime = 60*60*1000L;

		SimpleDateFormat timestampFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
		dateFormat.setTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE));
		timestampFormat.setTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE));
		timeFormat.setTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE));

		/* Validating Restaurant ID  */
		UserInfoModel userInfo = authService.getUserInfoByToken(token);

		String restaurantId = waitlistService.isValidRest(userInfo, params, errorList);


		List<Object> allTablesMap = waitlistDao.getAllTables(restaurantId);
		Map<String,Table> allTablesWithGuid = (Map<String, Table>) allTablesMap.get(1);
		Map<Integer, List<Table>> allTablesWithCovers = (Map<Integer, List<Table>>) allTablesMap.get(0);
		List<Table> allApplicableTables = (List<Table>) allTablesMap.get(2);

		if(!errorList.isEmpty())
		{

			getResponse =new ErrorResponse(ResponseCodes.QUICK_SEARCH_RECORD_FETCH_FAILURE, errorList);
			return getResponse;

		}

		String currentDateStr = params.get(Constants.DATE) != null ? (String) params.get(Constants.DATE) : dateFormat.format(new Date());
		String timeStr = params.get(Constants.TIME) != null ? params.get(Constants.TIME).toString() : timeFormat.format(new Date());



		if(null != params && !params.containsKey(Constants.ALL_TABLES))
		{
			errorList.add(new ValidationError(Constants.ALL_TABLES, UtilityMethods.getErrorMsg(ErrorCodes.MISSING_ALL_TABLES),ErrorCodes.MISSING_ALL_TABLES));
		}
		else
		{
			if(!Boolean.valueOf(params.get(Constants.ALL_TABLES).toString())){
				if((null!= params) && (!params.containsKey(Constants.COVERS)))
				{
					errorList.add(new ValidationError(Constants.COVERS, UtilityMethods.getErrorMsg(ErrorCodes.COVERS_REQUIRED),ErrorCodes.COVERS_REQUIRED));
					getResponse =new ErrorResponse(ResponseCodes.QUICK_SEARCH_RECORD_FETCH_FAILURE, errorList);
					return getResponse;
				}
				if(null != params && !params.containsKey(Constants.TAT))
				{
					tat = this.getTatValue(restaurantId, currentDateStr, Integer.parseInt(params.get(Constants.COVERS).toString()), errorList);
					params.put(Constants.TAT, tat);
				}
				allApplicableTables = allTablesWithCovers.get(Integer.valueOf(params.get(Constants.COVERS).toString()));

			}else{
				if(null != params && !params.containsKey(Constants.TAT))
				{
					if((null!= params) && (!params.containsKey(Constants.COVERS)))
					{
						errorList.add(new ValidationError(Constants.COVERS, UtilityMethods.getErrorMsg(ErrorCodes.COVERS_REQUIRED),ErrorCodes.COVERS_REQUIRED));
						getResponse =new ErrorResponse(ResponseCodes.QUICK_SEARCH_RECORD_FETCH_FAILURE, errorList);
						return getResponse;
					}
					tat = this.getTatValue(restaurantId, currentDateStr, Integer.parseInt(params.get(Constants.COVERS).toString()), errorList);
					params.put(Constants.TAT, tat);
				} 

			}
		}

		if(!errorList.isEmpty())
		{
			getResponse =new ErrorResponse(ResponseCodes.QUICK_SEARCH_RECORD_FETCH_FAILURE, errorList);
			return getResponse;
		}

		//do time specific tasks
		Long currentDateTime = 0l,  startTime = 0l ;
		String dateTimeStr = currentDateStr + " " + timeStr;
		try 
		{
			currentDateTime =  dateFormat.parse(currentDateStr).getTime();
			startTime = timestampFormat.parse(dateTimeStr).getTime();
		} 
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Long overlapTime = 0*60*1000l; 

		Integer noOfSlots = -1;

		if(params.containsKey("noOfSlots"))
		{
			noOfSlots = Integer.valueOf(params.get("noOfSlots").toString());
		}

		QuickSearchReservationNew quickSearch;
		List<QuickSearchReservationNew> quickSearchList = new ArrayList<>();
		Long endTime, statusTime;
		int allocateCount = 0,availCount = 0 ,blockCount = 0;
		List<Map<String,String>> allocatedGuids, blockedGuids, availableGuids;

		/*fetch applicable current shift*/
		Map<String, Object> shiftTime = new HashMap<>();

		if(params.containsKey(Constants.DAY_NAME) &&  params.get(Constants.DAY_NAME).toString().equalsIgnoreCase("tomorrow"))
		{
			Date tomorrowDate = new Date(new Date().getTime() + 24*60*60*1000);
			shiftTime = waitlistService.getApplicableShifts( restaurantId, errorList, tomorrowDate,true);

		}else{
			shiftTime = waitlistService.getApplicableShifts( restaurantId, errorList, new Date(startTime),false);
		}


		if(!errorList.isEmpty())
		{
			getResponse = new ErrorResponse(ResponseCodes.QUICK_SEARCH_RECORD_FETCH_FAILURE, errorList);
			return getResponse;
		}

		//get dining slots
		List<Long> diningSlotList = new ArrayList<>();
		List<Shift> shiftList = new ArrayList<>();
		startTime = (Long) shiftTime.get("startTime");
		currentDateTime = (Long) shiftTime.get("currentDateTime");
		Long currentDayShiftEndTime = (Long) shiftTime.get("currentDayShiftEndTime");
		shiftList = (List<Shift>) shiftTime.get("shiftList");


		Boolean isHoliday = quickSearchDao.getHoliday(restaurantId, currentDateTime);

		if(isHoliday)
		{
			errorList.add(new ValidationError(Constants.TIME, UtilityMethods.getErrorMsg(ErrorCodes.TODAY_IS_HOLIDAY),ErrorCodes.TODAY_IS_HOLIDAY));
			getResponse =new ErrorResponse(ResponseCodes.QUICK_SEARCH_RECORD_FETCH_FAILURE, errorList);
			return getResponse;
		}

		tat = Long.parseLong(params.get(Constants.TAT).toString());

		List<Long> diningSlots = new ArrayList<>();
		List<Long> outOfOpHrsList = new ArrayList<>();

		for(Shift shift : shiftList)
		{
			diningSlotList.addAll(shift.getDiningSlotsWithDateTime(currentDateTime));
		}

		Collections.sort(diningSlotList);

		int index = diningSlotList.indexOf(startTime);
		if(index == -1){
			for(Long time : diningSlotList)
			{
				if(time > startTime){
					index = diningSlotList.indexOf(time);
					break;
				}
			}
		}



		if(index == -1)
		{
			errorList.add(new ValidationError(Constants.OPERATIONAL_HOURS, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_OPERATIONAL_HOURS),ErrorCodes.INVALID_OPERATIONAL_HOURS));
		}

		if(!errorList.isEmpty())
		{
			getResponse =new ErrorResponse(ResponseCodes.QUICK_SEARCH_RECORD_FETCH_FAILURE, errorList);
			return getResponse;
		}

		int lastIndex = diningSlotList.size() - 1;
		int startIndex = index;



		if(index >= 2)
		{
			startIndex = index - 2 ;
		}

		int outOfOpHrStartIndex = lastIndex;

		//TODO : the slots should always be more than the current Time;

		//TODO : to manage the gap between shifts 

		/* get all blocked tables*/
		Map<String,Object> blockResvParam = new HashMap<>();
		blockResvParam.put(Constants.REST_GUID, restaurantId);
		blockResvParam.put("currentDateTime", currentDateTime);
		blockResvParam.put("nextDateTime", (currentDateTime + 24*60*60*1000));
		blockResvParam.put("currentShiftEndTime", (currentDayShiftEndTime + 60*60*1000L));
		blockResvParam.put("currentTime", startTime);

		Map<String, List<Reservation>> blockListMap = new HashMap<String, List<Reservation>>();
		blockListMap = shuffleDao.getBlockedTables(blockResvParam, blockListMap);
		blockResvParam = null;

		/* get all the reservations*/
		Map<String, List<Reservation>> reservationListMap = new HashMap<String, List<Reservation>>();
		Map<String,Object> resvParam = new HashMap<>();	
		resvParam.put(Constants.START_TIME, startTime);
		resvParam.put("currentShiftEnd", (currentDayShiftEndTime + 60*60*1000L));
		resvParam.put("restaurantId", restaurantId);
		List<Reservation> allReservationList = shuffleDao.getTablesHavingReservation(resvParam);
		resvParam = null;

		shuffleService.addAllResvToBlockMap(allReservationList, reservationListMap);

		/* get all the Walkin*/
		Map<String,Object> queueMap = new HashMap<>();
		queueMap.put(Constants.REST_ID, restaurantId);
		queueMap.put("includeOutOfOpHrResv", false);
		List<Reservation> queuedResv = queueDao.getQueuedReservation(queueMap);


		shuffleService.addAllResvToBlockMap(queuedResv, reservationListMap);

		Reservation resv = new Reservation();

		Integer count = 0;

		Map<String,String> resultObjMap ;

		for(; startIndex <= lastIndex ; startIndex++)
		{

			startTime = diningSlotList.get(startIndex);
			if(new Date().getTime() > startTime)
			{
				continue;
			}

			count++;
			if(noOfSlots != -1 && count > noOfSlots)
			{
				break;
			}


			statusTime = startTime;
			endTime = (startTime + (tat*60*1000) - overlapTime);
			if(endTime > currentDayShiftEndTime){
				outOfOpHrStartIndex = startIndex;
				break;

			}
			allocateCount=0; availCount=0; blockCount=0;
			allocatedGuids = new ArrayList<>();
			availableGuids = new ArrayList<>();
			blockedGuids = new ArrayList<>();


			resv.setEstStartTime(new Date(startTime));
			resv.setEstEndTime(new Date(endTime));
			if(allApplicableTables != null)
			{
				for(Table t : allApplicableTables){
					StringBuilder blockReason = new StringBuilder();
					Map<String,String> blockReasonMap = new HashMap<>();
					Boolean checkForBlockedResv = shuffleService.checkForBlockedReservationQuickSearch(resv, reservationListMap, t.getGuid(),blockReasonMap);
					if(checkForBlockedResv){
						allocateCount++;
						resultObjMap = new HashMap<>();
						resultObjMap.put(t.getGuid(), "");
						allocatedGuids.add(resultObjMap);
					}else{
						blockReasonMap = new HashMap<>();
						blockReasonMap.put("reason", "");
						checkForBlockedResv = shuffleService.checkForBlockedReservationQuickSearch(resv, blockListMap, t.getGuid(),blockReasonMap);
						if(checkForBlockedResv){
							blockCount++;
							resultObjMap = new HashMap<>();
							resultObjMap.put(t.getGuid(), blockReasonMap.get("reason"));
							blockedGuids.add(resultObjMap);
						}else{
							availCount++;
							resultObjMap = new HashMap<>();
							resultObjMap.put(t.getGuid(), "");
							availableGuids.add(resultObjMap);
						}
					}
				}
			}


			quickSearch = new QuickSearchReservationNew();
			quickSearch.setTime(statusTime);
			quickSearch.setAllocatedTable(allocatedGuids);
			quickSearch.setAllocateTable(allocateCount);
			quickSearch.setAvailableTable(availCount);
			quickSearch.setAvailableTableGuid(availableGuids);
			quickSearch.setBlockedTableGuid(blockedGuids);
			quickSearch.setBlockTable(blockCount);
			quickSearch.setIsWithinOperationalHours(true);

			quickSearchList.add(quickSearch);

		}


		outOfOpHrsList = diningSlotList.subList(outOfOpHrStartIndex, lastIndex+1);
		Collections.sort(outOfOpHrsList);

		for(Long time : outOfOpHrsList)
		{
			count++;
			if(noOfSlots != -1 && count > noOfSlots)
			{
				break;
			}

			quickSearch = new QuickSearchReservationNew();
			quickSearch.setAllocatedTable(new ArrayList<Map<String,String>>());
			quickSearch.setAllocateTable(0);
			quickSearch.setAvailableTable(0);
			quickSearch.setTime(time);
			quickSearch.setAvailableTableGuid(new ArrayList<Map<String,String>>());
			quickSearch.setBlockedTableGuid(new ArrayList<Map<String,String>>());
			quickSearch.setBlockTable(0);
			quickSearch.setIsWithinOperationalHours(false);
			quickSearchList.add(quickSearch);
		}

		getResponse = new GetResponse<QuickSearchReservationNew>(ResponseCodes.QUICK_SEARCH_RECORD_FETCH_SUCCESFULLY,quickSearchList);
		return getResponse;
	}
	
	


	//private method to find tat 
	private long getTatValue(String rest_guid, String currentDate, int num_covers, List<ValidationError> listOfError) 
	{
		long tat_value = 0L;
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE));
		try
		{
			Calendar calc = Calendar.getInstance();
			calc.setTime(sdf.parse(currentDate));
			String dayNames[] = new DateFormatSymbols().getWeekdays();
			String value = Constants.WEEKDAY;
			if (dayNames[calc.get(Calendar.DAY_OF_WEEK)].equalsIgnoreCase(Constants.FRIDAY) || dayNames[calc.get(Calendar.DAY_OF_WEEK)].equalsIgnoreCase(Constants.SATURDAY) || dayNames[calc.get(Calendar.DAY_OF_WEEK)].equalsIgnoreCase(Constants.SUNDAY)) 
			{
				value= Constants.WEEKEND;
			}


			tat_value = tatDao.get_tat_value(rest_guid,num_covers, value);

		} 
		catch (Exception e) 
		{

			listOfError.add(new ValidationError(Constants.TAT, UtilityMethods.getErrorMsg(ErrorCodes.TAT_VALUE_UNDEFINED) + " " + num_covers, ErrorCodes.TAT_VALUE_UNDEFINED));
		}

		return tat_value;
	}
	
	
	
	private List<Long> getDinigSlotForDay(OperationalHours op_hrs,String currentDay)
	{
	    List <Long> slotList = new ArrayList<>();
	    List<Shift> shiftList = new ArrayList<>();
	    if(currentDay.equalsIgnoreCase(Constants.SUNDAY))
	    {
		//Logger.debug("Day is Sunday");
		shiftList = op_hrs.getSunday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.MONDAY))
	    {
		//Logger.debug("Day is Monday");
		shiftList = op_hrs.getMonday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.TUESDAY))
	    {
		//Logger.debug("Day is TUESDAY");
		shiftList = op_hrs.getTuesday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.WEDNESDAY))
	    {
		//Logger.debug("Day is WEDNESDAY");
		shiftList = op_hrs.getWednesday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.THURSDAY))
	    {
		//Logger.debug("Day is THURSDAY");
		shiftList = op_hrs.getThursday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.FRIDAY))
	    {
		//Logger.debug("Day is FRIDAY");
		shiftList = op_hrs.getFriday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.SATURDAY))
	    {
		//Logger.debug("Day is SATURDAY");
		shiftList = op_hrs.getSaturday();
	    }
	    
	    //Logger.debug("Shift list is " + shiftList);
	    
	    for(Shift shift : shiftList)
	    {
		slotList.addAll(shift.getDiningSlots());
	    }
	    
	    //Logger.debug("Slot list before sorting is " + slotList);
	    Collections.sort(slotList);
	    //Logger.debug("Slot list after sorting is " + slotList);
	    return slotList;
	}
	
	
	


	private List<Shift> getShiftsForDay(OperationalHours op_hrs,String currentDay) 
	{
	    List<Shift> shiftList = new ArrayList<>();
	    if(currentDay.equalsIgnoreCase(Constants.SUNDAY))
	    {
		//Logger.debug("Day is Sunday");
		shiftList = op_hrs.getSunday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.MONDAY))
	    {
		//Logger.debug("Day is Monday");
		shiftList = op_hrs.getMonday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.TUESDAY))
	    {
		//Logger.debug("Day is TUESDAY");
		shiftList = op_hrs.getTuesday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.WEDNESDAY))
	    {
		//Logger.debug("Day is WEDNESDAY");
		shiftList = op_hrs.getWednesday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.THURSDAY))
	    {
		//Logger.debug("Day is THURSDAY");
		shiftList = op_hrs.getThursday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.FRIDAY))
	    {
		//Logger.debug("Day is FRIDAY");
		shiftList = op_hrs.getFriday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.SATURDAY))
	    {
		//Logger.debug("Day is SATURDAY");
		shiftList = op_hrs.getSaturday();
	    }
	    
	    return shiftList;
	}

	

}
