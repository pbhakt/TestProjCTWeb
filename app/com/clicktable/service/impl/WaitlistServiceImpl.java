package com.clicktable.service.impl;

import java.text.DateFormatSymbols;
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

import play.Logger;

import com.clicktable.comparator.ReservationSort;
import com.clicktable.comparator.TableWaitlistSort;
import com.clicktable.comparator.TableWaitlistSortMobile;
import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.HistoricalTatDao;
import com.clicktable.dao.intf.QueueDao;
import com.clicktable.dao.intf.QuickSearchDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.TableShuffleDao;
import com.clicktable.dao.intf.TatDao;
import com.clicktable.dao.intf.WaitlistDao;
import com.clicktable.model.CustomWaitlist;
import com.clicktable.model.OperationalHours;
import com.clicktable.model.Queue;
import com.clicktable.model.Reservation;
import com.clicktable.model.Shift;
import com.clicktable.model.Table;
import com.clicktable.model.TableWaitingTime;
import com.clicktable.model.TableWaitingTimeMobile;
import com.clicktable.model.UserInfoModel;
import com.clicktable.model.WaitlistResult;
import com.clicktable.model.WaitlistResultMobile;
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
public class WaitlistServiceImpl  implements WaitlistService
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
	AllTablesWaitlistService allTableService;
	
	@Autowired
	TableShuffleDao shuffleDao;
	
	@Autowired
	TableShuffleService shuffleService;
	
	//new methods for quick search
	Map<String , Object> resultMap = new HashMap<>();
	
	public Map<String, Object> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}


	@Override
	public BaseResponse getWaitlistResult(Map<String, Object> params, String token)
	{
	
		BaseResponse getResponse = new BaseResponse();
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		Map <String, Object> responseMap = new HashMap<>();
		
		responseMap = getTableWaitingList(params, token, errorList);
		
		if(!errorList.isEmpty())
		{
			getResponse = new ErrorResponse(ResponseCodes.WAITLIST_RECORD_FETCH_FAILURE, errorList);
			return getResponse;
		}
		
		List quickSearchList = new ArrayList<>();
		quickSearchList.add(responseMap.get("waitTimeResult"));
		getResponse = new GetResponse(ResponseCodes.WAITLIST_RECORD_FETCH_SUCCESFULLY,quickSearchList);
		
		return getResponse;
	}
		
	
	@Override
	public Map<String,Object> getTableWaitingList(Map<String,Object> params, String token, List<ValidationError> errorList)
	{
		
		Map <String, Object> responseMap = new HashMap<>();
		
		Map<String, List<Reservation>> blockListMap = new HashMap<String, List<Reservation>>();
		BaseResponse response = shuffleService.shuffleTableAPI(params, token, blockListMap);
		
		if(response instanceof ErrorResponse)
		{
			errorList.addAll(((ErrorResponse)response).getErrorList());
		}
		
		
		
		/* Validating Restaurant ID  */
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		String restaurantId = isValidRest(userInfo, params, errorList);
		
		if(!errorList.isEmpty())
		{
			Logger.debug("Errors in shuffle table api-------" + errorList);
			return responseMap;
		}
		
		//Map<Integer,List<Table>> allTablesWithCovers = shuffleService.getAllTablesWithCovers();
		
		List<Object> allTablesList = waitlistDao.getAllTables(restaurantId);
		Map<Integer,List<Table>> allTablesWithCovers = (Map<Integer, List<Table>>) allTablesList.get(0);
		
		// TODO 
		//To add functionality to return for a particular number of coversj
		
		Long inputTat = null;
		if(params.containsKey(Constants.TAT) && (params.get(Constants.TAT)!=null))
		{
			inputTat =  Long.valueOf(params.get(Constants.TAT).toString())*60*1000;
		}
		
		
		/*fetch applicable current shift*/
		if(resultMap.isEmpty()){
			resultMap = getApplicableShifts( restaurantId, errorList, new Date(),false); 
		}
		
		if(!errorList.isEmpty())
		{
			return responseMap;
		}
		
		
		Long startTime = (Long) resultMap.get("startTime");
		Long currentDateTime = (Long) resultMap.get("currentDateTime");
		String dayType = (String) resultMap.get("dayType");
		List<Shift> shiftList = (List<Shift>) resultMap.get("shiftList");
		
		Long ongoingShiftStart = 0L;
		Long ongoingShiftEnd = 0L;
				
		for(Shift shift : shiftList){
			Boolean diffShiftStartAndCurrent = (shift.getStartTimeInMillis() - startTime <= 0);
			Boolean diffShiftEndAndCurrent = (shift.getEndTimeInMillis2() - startTime > 0);
			
			if(diffShiftStartAndCurrent && diffShiftEndAndCurrent){
				ongoingShiftStart = shift.getStartTimeInMillis();
				ongoingShiftEnd = shift.getEndTimeInMillis2();
				break;
			}
		}

		if(ongoingShiftStart == 0 && ongoingShiftEnd == 0){
			//TODO List of error
		}
		
		/*check Holiday */
		Boolean isHoliday = quickSearchDao.getHoliday(restaurantId, currentDateTime);
		if(isHoliday)
		{
			errorList.add(new ValidationError(Constants.TIME, UtilityMethods.getErrorMsg(ErrorCodes.TODAY_IS_HOLIDAY),ErrorCodes.TODAY_IS_HOLIDAY));
			return responseMap;
		}
	
		
		/*get actual tat data*/
		
		Map<String,Object> actTatParams = new HashMap<>();
		actTatParams.put(Constants.REST_GUID, restaurantId);
		actTatParams.put(Constants.DAY_NAME, dayType);
		HashMap<Integer,Long> actualTatMap = (HashMap<Integer, Long>) waitlistDao.getActualTat(actTatParams);
		HashMap<Integer,Long> tatMap = (HashMap<Integer, Long>) actualTatMap.clone();

		Logger.debug("Actual tat map is " + actualTatMap);

		//Long overlapTime = 0*60*1000l;    //default overlap time 10 minutes
		
		Map<Integer,Queue> queueMap = queueDao.getQueue(restaurantId);
		Long historicalTat = 0l, actualTat = 0l;
		int cover = 0;
		Long inputParamMap = inputTat;
		Map<Integer,WaitlistResult> tblWaitMap = new HashMap<>();
		
		if(params.containsKey(Constants.COVERS) && (params.get(Constants.COVERS)!=null))
		{
			Integer inputCovers =  Integer.valueOf(params.get(Constants.COVERS).toString());
			List<Table> tablesForCovers = allTablesWithCovers.get(inputCovers);
			
			if(tablesForCovers == null || tablesForCovers.size() < 1)
			{
				errorList.add(new ValidationError(Constants.COVERS, UtilityMethods.getErrorMsg(ErrorCodes.NO_TABLE_AVAILABE),ErrorCodes.NO_TABLE_AVAILABE));
				return responseMap;
			}
			
			allTablesWithCovers = new HashMap<>();
			allTablesWithCovers.put(inputCovers, tablesForCovers);
		}
		
		
		for(Map.Entry<Integer, List<Table>> entry : allTablesWithCovers.entrySet())
		{
			cover = entry.getKey();
			int coverToUse = cover;
			
			coverToUse = cover > 8 ? 9 : cover;
			historicalTat = tatMap.get(coverToUse) != null ? (Long) tatMap.get(coverToUse) : historicalTat;
			actualTat = actualTatMap.get(coverToUse) != null ? actualTatMap.get(coverToUse) : actualTat;
			inputTat = inputParamMap == null ? historicalTat : inputTat;

			long tatToUse = historicalTat;
			List<Table> tableList = entry.getValue();
			List<TableWaitingTime> tblWaitList = new ArrayList<>();
			WaitlistResult waitlistResult = new WaitlistResult();
			if(queueMap.get(coverToUse) != null)
			{
				waitlistResult.setQueueCount(queueMap.get(coverToUse).getCount());
			}
			else
			{
				waitlistResult.setQueueCount(0);  
			}

			Map<String,Object> waitParams = new HashMap<>();
			waitParams.put("tatToUse", tatToUse);
			waitParams.put("historicalTat", historicalTat);
			waitParams.put("actualTat", actualTat);
			waitParams.put("inputTat", inputTat);
			waitParams.put("startTime", startTime);
			
			/*Map<String, Object> waitlistResultMap = getTableWaitingList(params, token, waitParams, errorList, tableList);*/
			for(Table t : tableList)
			{
				List<Reservation> reservationList = blockListMap.get(t.getGuid()) == null ? new ArrayList<>() :
					blockListMap.get(t.getGuid()); 
				List<Reservation> newReservationList = new ArrayList<>();
				for(Reservation r : reservationList){
					try {
						Reservation clonedResv = (Reservation) r.clone();
						newReservationList.add(clonedResv);
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				TableWaitingTime tblWait = getTableWaitingTime(t,newReservationList,waitParams,t.getGuid());
				tblWaitList.add(tblWait);
			}

			Collections.sort(tblWaitList,new TableWaitlistSort());
			for(TableWaitingTime tblWait : tblWaitList)
			{
				if(tblWait.getWaitTime() < 0)
				{
					tblWait.setAvailableTime(new Date());
				}else if (tblWait.getAvailableTime().getTime() > ongoingShiftEnd){
					tblWait.setWaitTime(-1L);
					tblWait.setAvailableTime(new Date(0));
				}
			}
			waitlistResult.setTblWaitlist(tblWaitList);
			tblWaitMap.put(cover, waitlistResult);
		}
		
		responseMap.put("waitTimeResult", tblWaitMap);
		responseMap.put("reservationList", blockListMap);
		
		return responseMap;
}
	

	private List<Shift> getShiftsForDay(OperationalHours op_hrs,String currentDay) 
	{
	    List<Shift> shiftList = new ArrayList<>();
	    if(currentDay.equalsIgnoreCase(Constants.SUNDAY))
	    {
		shiftList = op_hrs.getSunday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.MONDAY))
	    {
		shiftList = op_hrs.getMonday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.TUESDAY))
	    {
		shiftList = op_hrs.getTuesday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.WEDNESDAY))
	    {
		shiftList = op_hrs.getWednesday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.THURSDAY))
	    {
		shiftList = op_hrs.getThursday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.FRIDAY))
	    {
		shiftList = op_hrs.getFriday();
	    }
	    if(currentDay.equalsIgnoreCase(Constants.SATURDAY))
	    {
		shiftList = op_hrs.getSaturday();
	    }
	    
	    return shiftList;
	}
	
	
	
	@Override
	public String isValidRest(UserInfoModel userInfo, Map<String,Object> params, List<ValidationError> errorList)
	{
		String restaurantId = "";
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) 
		{
		    if (!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) 
		    {
		    	params.put(Constants.REST_ID, userInfo.getRestGuid());
		    }
		}
	
		if(null != params && params.containsKey(Constants.REST_ID))
		{
			 restaurantId = (String) params.get(Constants.REST_ID);
			 restaurantValidator.validateGuid(restaurantId, errorList);
		}
		else
		{
			errorList.add(new ValidationError(Constants.REST_ID, UtilityMethods.getErrorMsg(ErrorCodes.REST_ID_REQUIRED), ErrorCodes.REST_ID_REQUIRED));
		}
		
		return restaurantId;
	}


	
	
/*	public Map<String, List<Reservation>> getAllBlockAndResevForTable(){
		
	}*/
	
	@Override
	public Map<String,Object> getApplicableShifts( String restaurantId,List<ValidationError> errorList, Date dateObject, Boolean tomorrow)
	{
		SimpleDateFormat timestampFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
		dateFormat.setTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE));
		timestampFormat.setTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE));
		timeFormat.setTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE));
		
		
		String currentDateStr = dateFormat.format(dateObject);
		Long startTime = dateObject.getTime();
		Long currentDateTime =  UtilityMethods.getDateFromString(currentDateStr, dateFormat).getTime();
			

		//get name of this day and previous day
		Calendar calc = Calendar.getInstance();
		calc.setTimeInMillis(startTime);
		String dayNames[] = new DateFormatSymbols().getWeekdays();
		int dayCount = calc.get(Calendar.DAY_OF_WEEK);
		String currentDay = dayNames[calc.get(Calendar.DAY_OF_WEEK)];
		calc.set(Calendar.DAY_OF_MONTH, calc.get(Calendar.DAY_OF_MONTH)-1);
		String previousDay = dayNames[calc.get(Calendar.DAY_OF_WEEK)];
		
		Map<String,Object> opHrParams = new HashMap<String, Object>();
		opHrParams.put(Constants.REST_GUID, restaurantId);
		OperationalHours op_hrs = restDao.getOperationalHours(opHrParams);
		//get shifts
		List<Shift> shiftList = new ArrayList<>();
		List<Shift> currentDateShifts = getShiftsForDay(op_hrs,currentDay);
		
		if(currentDateShifts == null)
		{
			errorList.add(new ValidationError(Constants.MASTER_DATA, UtilityMethods.getErrorMsg(ErrorCodes.MASTER_DATA_IS_MISSING),ErrorCodes.MASTER_DATA_IS_MISSING));
			return null;
			
		}
		
		Long currentDayShiftStartTime = 0L;
		Long currentDayShiftEndTime = 0L;

		for(Shift shift : currentDateShifts)
		{
			currentDayShiftStartTime = currentDayShiftStartTime == 0L ? shift.getStartTimeInMillis() : 
				(currentDayShiftStartTime < shift.getStartTimeInMillis() ? currentDayShiftStartTime : shift.getStartTimeInMillis()); 

			currentDayShiftEndTime = currentDayShiftEndTime == 0 ? shift.getEndTimeInMillis2() :
				(currentDayShiftEndTime < shift.getEndTimeInMillis2() ? shift.getEndTimeInMillis2() : currentDayShiftEndTime);

			shift.setStartTimeInMillis(shift.getStartTimeInMillis() + currentDateTime);
			shift.setEndTimeInMillis2(shift.getEndTimeInMillis2() + currentDateTime);
			shiftList.add(shift);
		}
		
		currentDayShiftStartTime = currentDayShiftStartTime + currentDateTime;
		currentDayShiftEndTime = currentDayShiftEndTime + currentDateTime;
		
		if(tomorrow){
			startTime = currentDayShiftStartTime;
		}
		
		Long prevDayShiftStartTime = 0L;
		Long prevDayShiftEndTime = 0L;


		if(currentDayShiftStartTime > startTime){
			shiftList = new ArrayList<>(); 
			List<Shift> previousDateShifts = getShiftsForDay(op_hrs,previousDay);
			if(previousDateShifts == null)
			{
				errorList.add(new ValidationError(Constants.MASTER_DATA, UtilityMethods.getErrorMsg(ErrorCodes.MASTER_DATA_IS_MISSING),ErrorCodes.MASTER_DATA_IS_MISSING));
				return null;
			}
			for(Shift shift : previousDateShifts)
			{
				prevDayShiftStartTime = prevDayShiftStartTime == 0L ? shift.getStartTimeInMillis() : 
					(prevDayShiftStartTime < shift.getStartTimeInMillis() ? prevDayShiftStartTime : shift.getStartTimeInMillis()); 

				prevDayShiftEndTime = prevDayShiftEndTime == 0 ? shift.getEndTimeInMillis2() :
					(prevDayShiftEndTime < shift.getEndTimeInMillis2() ? shift.getEndTimeInMillis2() : prevDayShiftEndTime);
				shift.setStartTimeInMillis(shift.getStartTimeInMillis() + currentDateTime - 24*60*60*1000);
				shift.setEndTimeInMillis2(shift.getEndTimeInMillis2() + currentDateTime - 24*60*60*1000);
				shiftList.add(shift);
			}
			
			currentDateTime = currentDateTime - 24*60*60*1000;
			currentDayShiftStartTime = prevDayShiftStartTime + currentDateTime;
			currentDayShiftEndTime = prevDayShiftEndTime + currentDateTime;
			currentDay = previousDay;
			if(dayCount == 1 || dayCount == 0){
				dayCount = 7;
			}else{
				dayCount = dayCount - 1;
			}
		}
		
		String dayType = Constants.WEEKDAY;
		if((dayCount == 6) || (dayCount == 7 ))
		{
			dayType = Constants.WEEKEND;
		}
		
		Long shiftChangeTime = UtilityMethods.getDateFromString(Constants.SHIFT_CHANGE_TIME, timeFormat).getTime();

		if((startTime - currentDateTime - 330*60*1000) > shiftChangeTime)
		{
			resultMap.put(Constants.SHIFT,"DINNER");	
		}
		else
		{
			resultMap.put(Constants.SHIFT,"LUNCH");
		}

		
		
		resultMap.put("startTime", startTime);
		resultMap.put("currentDateTime", currentDateTime);
		resultMap.put("currentDayShiftStartTime", currentDayShiftStartTime);
		resultMap.put("currentDayShiftEndTime", currentDayShiftEndTime);
		resultMap.put("currentDay", currentDay);
		resultMap.put("dayType", dayType);
		resultMap.put("shiftList", shiftList);
		
		
		return resultMap;
		
	}

	
	@Override
	public TableWaitingTime getTableWaitingTime(Table t, List<Reservation> reservationList, Map<String,Object> waitParams, String tableGuid)
	{
		TableWaitingTime tblWait = new TableWaitingTime();
		if(t != null)
			tblWait.setTable(t);
		if(tableGuid != null)
			tblWait.setTableGuid(tableGuid);
		List<String> resvSourceList = new ArrayList<>();

		Long tatToUse = (Long) waitParams.get("tatToUse");
		Long actualTat = (Long) waitParams.get("actualTat");
		Long historicalTat = (Long) waitParams.get("historicalTat");
		Long inputTat = (Long) waitParams.get("inputTat");
		Long startTime = (Long) waitParams.get("startTime");

		if(reservationList == null)
		{
			tblWait.setWaitTime(0L);
			tblWait.setAvailableTime(new Date());
			resvSourceList.add("");

		}else if(reservationList.size() == 0){
			tblWait.setWaitTime(0L);
			tblWait.setAvailableTime(new Date());
			resvSourceList.add("");

		}
		else if(reservationList.size() == 1)
		{
			long resvTat = Long.valueOf(reservationList.get(0).getTat())*60*1000;
			tatToUse = resvTat == actualTat ? historicalTat : resvTat;

			if(reservationList.get(0).getActStartTime() != null && reservationList.get(0).getActEndTime() == null)
			{
				Long actualEndTime = reservationList.get(0).getActStartTime().getTime() + tatToUse;
				actualEndTime = (actualEndTime > reservationList.get(0).getEstEndTime().getTime()) ? actualEndTime : 
					reservationList.get(0).getEstEndTime().getTime();

				tblWait.setWaitTime(actualEndTime - startTime);
				tblWait.setAvailableTime(new Date(actualEndTime));
			}
			else if(reservationList.get(0).getEstStartTime().getTime() > (startTime + inputTat))
			{
				long x = 0l;
				tblWait.setWaitTime(x);
				tblWait.setAvailableTime(new Date());
			}
			else if(reservationList.get(0).getEstStartTime().getTime() < startTime && reservationList.get(0).getEstEndTime().getTime() > startTime)
			{
				if(reservationList.get(0).getBlockResv().equals("BLOCK")){
					tblWait.setWaitTime(reservationList.get(0).getEstEndTime().getTime() - startTime);
					tblWait.setAvailableTime(reservationList.get(0).getEstEndTime());
				}else{
					tblWait.setWaitTime(tatToUse);
					tblWait.setAvailableTime(new Date(startTime + tatToUse));
				}
			}
			else
			{
				tblWait.setWaitTime(reservationList.get(0).getEstStartTime().getTime() + tatToUse - startTime);
				tblWait.setAvailableTime(new Date(reservationList.get(0).getEstStartTime().getTime() + tatToUse));
			}
			resvSourceList.add(reservationList.get(0).getBlockResv());
		}
		else
		{
			Collections.sort(reservationList, new ReservationSort());
			List<CustomWaitlist> custWaitList = new ArrayList<>();
			int count = 0;
			long prevResvTat = 0L;
			long currResvTat;


			for(Reservation r : reservationList)
			{
				currResvTat = Long.valueOf(r.getTat())*60*1000;
				tatToUse = currResvTat == actualTat ? historicalTat : currResvTat;
				long x = 0L;
				CustomWaitlist custWait = new CustomWaitlist(r, t);
				custWait.setResvSource(r.getBlockResv());
				if(count == 0)
				{
					if(reservationList.get(0).getEstStartTime().getTime() > startTime){
						x = reservationList.get(0).getEstStartTime().getTime() - startTime;
					}

					if(x >= inputTat)
					{
						custWait.setGap(-1l);
						custWaitList.add(custWait);
						break;
					}
					else 
					{
						custWait.setGap(-1l);
						prevResvTat = currResvTat;
						count++;
					}
				}
				else
				{

					if(reservationList.get(count -1).getActStartTime() != null && reservationList.get(count -1).getActEndTime() == null)
					{
						Long actualEndTime = reservationList.get(count -1).getActStartTime().getTime() + prevResvTat;
						actualEndTime = Math.max(actualEndTime, reservationList.get(count -1).getEstEndTime().getTime());
						x = r.getEstStartTime().getTime() - Math.max(actualEndTime, startTime);
	
					}
					else if(reservationList.get(count -1).getEstStartTime().getTime() < startTime && reservationList.get(count -1).getEstEndTime().getTime() > startTime )
					{						
						
						Long reservationTat  = 0L;
						if(reservationList.get(count-1).getBlockResv().equals("BLOCK")){
							reservationTat = reservationList.get(count-1).getEstEndTime().getTime();
						}else{
							reservationTat = Math.max((startTime + prevResvTat), reservationList.get(count-1).getEstEndTime().getTime());
						}
						 
						x = r.getEstStartTime().getTime() - reservationTat;
					}
					else
					{
						x = r.getEstStartTime().getTime() - reservationList.get(count-1).getEstEndTime().getTime();
					}

					if(x >= inputTat)
					{
						custWaitList.get(count-1).setGap(x);
						break;
					}
					else 
					{
						custWaitList.get(count-1).setGap(x);
						if(custWaitList.get(count-1).getEstEndTime() > custWait.getEstEndTime()){
							custWait.setEstEndTime(custWaitList.get(count-1).getEstEndTime());
							r.setEstEndTime(new Date(custWaitList.get(count-1).getEstEndTime()));
						}
						custWait.setGap(-1l);
						count++;
						prevResvTat = currResvTat;
					}

				}
				custWaitList.add(custWait);
			}

			if(custWaitList.get(0).getEstStartTime() > (startTime + inputTat))
			{
				tblWait.setWaitTime(0L);
				tblWait.setAvailableTime(new Date());
				resvSourceList.add("");

			}else if (custWaitList.size() == 1 &&  custWaitList.get(0).getResv().getActStartTime() != null && 
					custWaitList.get(0).getResv().getActEndTime() == null){

				Long actualEndTime = custWaitList.get(0).getResv().getActStartTime().getTime() + prevResvTat;
				actualEndTime = (actualEndTime > custWaitList.get(0).getResv().getEstEndTime().getTime()) ? actualEndTime :
					custWaitList.get(0).getResv().getEstEndTime().getTime(); 

				if(actualEndTime - startTime > 0){
					tblWait.setWaitTime(actualEndTime - startTime);
					tblWait.setAvailableTime(new Date(actualEndTime));
				}else{
					tblWait.setWaitTime(actualEndTime - startTime);
					tblWait.setAvailableTime(new Date());
				}
				resvSourceList.add(custWaitList.get(0).getResvSource());

			}else if (custWaitList.size() == 1 && custWaitList.get(0).getEstStartTime() < startTime && custWaitList.get(0).getEstEndTime() > startTime){

				//Long waitTime = Integer.parseInt(custWaitList.get(0).getResv().getTat())*60*1000 == actualTat ? historicalTat : Integer.parseInt(custWaitList.get(0).getResv().getTat())*60*1000;

				Long waitTime;
				if(custWaitList.get(0).getResv().getBlockResv().equals("BLOCK"))
				{
					waitTime = custWaitList.get(0).getResv().getEstEndTime().getTime() - startTime;
				}
				else
				{
					waitTime = Integer.parseInt(custWaitList.get(0).getResv().getTat())*60*1000 == actualTat ? historicalTat : Integer.parseInt(custWaitList.get(0).getResv().getTat())*60*1000;
				}
				tblWait.setWaitTime(waitTime);
				tblWait.setAvailableTime(new Date(startTime + waitTime));
				resvSourceList.add(custWaitList.get(0).getResvSource());
			}
			else
			{
				Long waitTime = custWaitList.get(custWaitList.size()-1).getEstEndTime() - startTime;
				Long resvTat = Long.valueOf(custWaitList.get(custWaitList.size()-1).getResv().getTat())*60*1000;
				Long realWaitTime;

				if(custWaitList.get(custWaitList.size()-1).getResv().getBlockResv().equals("BLOCK") || custWaitList.get(custWaitList.size()-1).getResv().getReservationStatus().equals(Constants.SEATED)){
					realWaitTime = waitTime;
				}else{
					realWaitTime = (waitTime > resvTat) ? waitTime : resvTat;
				}
				tblWait.setWaitTime(realWaitTime);
				tblWait.setAvailableTime(new Date(custWaitList.get(custWaitList.size()-1).getEstEndTime()));
				for(CustomWaitlist customWait : custWaitList)
				{
					resvSourceList.add(customWait.getResvSource());

				}
			}
		}

		tblWait.setResvSourceList(resvSourceList);
		return tblWait;

	}

	@Override
	public BaseResponse getWaitlistResultForMobile(
			Map<String, Object> stringParamMap, String token) {
	
		BaseResponse getResponse = new BaseResponse();
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		Map <String, Object> responseMap = new HashMap<>();
		
		responseMap = getTableWaitingListForMobile(stringParamMap, token, errorList);
		
		if(!errorList.isEmpty())
		{
			getResponse = new ErrorResponse(ResponseCodes.WAITLIST_RECORD_FETCH_FAILURE, errorList);
			return getResponse;
		}
		
		List quickSearchList = new ArrayList<>();
		quickSearchList.add(responseMap.get("waitTimeResult"));
		getResponse = new GetResponse(ResponseCodes.WAITLIST_RECORD_FETCH_SUCCESFULLY,quickSearchList);
		
		return getResponse;
	}

	private Map<String, Object> getTableWaitingListForMobile(Map<String, Object> params, String token,List<ValidationError> errorList) {
		
		Map <String, Object> responseMap = new HashMap<>();
		
		Map<String, List<Reservation>> blockListMap = new HashMap<String, List<Reservation>>();
		BaseResponse response = shuffleService.shuffleTableAPI(params, token, blockListMap);
		
		if(response instanceof ErrorResponse)
		{
			errorList.addAll(((ErrorResponse)response).getErrorList());
		}
		
		
		
		/* Validating Restaurant ID  */
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		String restaurantId = isValidRest(userInfo, params, errorList);
		
		if(!errorList.isEmpty())
		{
			Logger.debug("Errors in shuffle table api-------" + errorList);
			return responseMap;
		}
		
		//Map<Integer,List<Table>> allTablesWithCovers = shuffleService.getAllTablesWithCovers();
		
		List<Object> allTablesList = waitlistDao.getAllTables(restaurantId);
		Map<Integer,List<Table>> allTablesWithCovers = (Map<Integer, List<Table>>) allTablesList.get(0);
		
		// TODO 
		//To add functionality to return for a particular number of coversj
		
		Long inputTat = null;
		if(params.containsKey(Constants.TAT) && (params.get(Constants.TAT)!=null))
		{
			inputTat =  Long.valueOf(params.get(Constants.TAT).toString())*60*1000;
		}
		
		
		/*fetch applicable current shift*/
		if(resultMap.isEmpty()){
			resultMap = getApplicableShifts( restaurantId, errorList, new Date(),false); 
		}
		
		if(!errorList.isEmpty())
		{
			return responseMap;
		}
		
		
		Long startTime = (Long) resultMap.get("startTime");
		Long currentDateTime = (Long) resultMap.get("currentDateTime");
		String dayType = (String) resultMap.get("dayType");
		List<Shift> shiftList = (List<Shift>) resultMap.get("shiftList");
		
		Long ongoingShiftStart = 0L;
		Long ongoingShiftEnd = 0L;
				
		for(Shift shift : shiftList){
			Boolean diffShiftStartAndCurrent = (shift.getStartTimeInMillis() - startTime <= 0);
			Boolean diffShiftEndAndCurrent = (shift.getEndTimeInMillis2() - startTime > 0);
			
			if(diffShiftStartAndCurrent && diffShiftEndAndCurrent){
				ongoingShiftStart = shift.getStartTimeInMillis();
				ongoingShiftEnd = shift.getEndTimeInMillis2();
				break;
			}
		}

		if(ongoingShiftStart == 0 && ongoingShiftEnd == 0){
			//TODO List of error
		}
		
		/*check Holiday */
		Boolean isHoliday = quickSearchDao.getHoliday(restaurantId, currentDateTime);
		if(isHoliday)
		{
			errorList.add(new ValidationError(Constants.TIME, UtilityMethods.getErrorMsg(ErrorCodes.TODAY_IS_HOLIDAY),ErrorCodes.TODAY_IS_HOLIDAY));
			return responseMap;
		}
	
		
		/*get actual tat data*/
		
		Map<String,Object> actTatParams = new HashMap<>();
		actTatParams.put(Constants.REST_GUID, restaurantId);
		actTatParams.put(Constants.DAY_NAME, dayType);
		HashMap<Integer,Long> actualTatMap = (HashMap<Integer, Long>) waitlistDao.getActualTat(actTatParams);
		HashMap<Integer,Long> tatMap = (HashMap<Integer, Long>) actualTatMap.clone();

		Logger.debug("Actual tat map is " + actualTatMap);

		Long overlapTime = 0*60*1000l;    //default overlap time 10 minutes
		
		Map<Integer,Queue> queueMap = queueDao.getQueue(restaurantId);
		Long historicalTat = 0l, actualTat = 0l;
		int cover = 0;
		Long inputParamMap = inputTat;
		Map<Integer,WaitlistResultMobile> tblWaitMap = new HashMap<>();
		
		if(params.containsKey(Constants.COVERS) && (params.get(Constants.COVERS)!=null))
		{
			Integer inputCovers =  Integer.valueOf(params.get(Constants.COVERS).toString());
			List<Table> tablesForCovers = allTablesWithCovers.get(inputCovers);
			
			if(tablesForCovers == null || tablesForCovers.size() < 1)
			{
				errorList.add(new ValidationError(Constants.COVERS, UtilityMethods.getErrorMsg(ErrorCodes.NO_TABLE_AVAILABE),ErrorCodes.NO_TABLE_AVAILABE));
				return responseMap;
			}
			
			allTablesWithCovers = new HashMap<>();
			allTablesWithCovers.put(inputCovers, tablesForCovers);
		}
		
		
		for(Map.Entry<Integer, List<Table>> entry : allTablesWithCovers.entrySet())
		{
			cover = entry.getKey();
			int coverToUse = cover;
			
			coverToUse = cover > 8 ? 9 : cover;
			historicalTat = tatMap.get(coverToUse) != null ? (Long) tatMap.get(coverToUse) : historicalTat;
			actualTat = actualTatMap.get(coverToUse) != null ? actualTatMap.get(coverToUse) : actualTat;
			inputTat = inputParamMap == null ? historicalTat : inputTat;

			long tatToUse = historicalTat;
			List<Table> tableList = entry.getValue();
			List<TableWaitingTimeMobile> tblWaitList = new ArrayList<>();
			WaitlistResultMobile waitlistResult = new WaitlistResultMobile();
			if(queueMap.get(coverToUse) != null)
			{
				waitlistResult.setQueueCount(queueMap.get(coverToUse).getCount());
			}
			else
			{
				waitlistResult.setQueueCount(0);  
			}

			Map<String,Object> waitParams = new HashMap<>();
			waitParams.put("tatToUse", tatToUse);
			waitParams.put("historicalTat", historicalTat);
			waitParams.put("actualTat", actualTat);
			waitParams.put("inputTat", inputTat);
			waitParams.put("startTime", startTime);
			
			/*Map<String, Object> waitlistResultMap = getTableWaitingList(params, token, waitParams, errorList, tableList);*/
			for(Table t : tableList)
			{
				List<Reservation> reservationList = blockListMap.get(t.getGuid()) == null ? new ArrayList<>() :
					blockListMap.get(t.getGuid()); 
				List<Reservation> newReservationList = new ArrayList<>();
				for(Reservation r : reservationList){
					try {
						Reservation clonedResv = (Reservation) r.clone();
						newReservationList.add(clonedResv);
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				TableWaitingTimeMobile tblWait = getTableWaitingTimeForMobile(t,newReservationList,waitParams,t.getGuid());
				tblWaitList.add(tblWait);
			}

			Collections.sort(tblWaitList,new TableWaitlistSortMobile());
			for(TableWaitingTimeMobile tblWait : tblWaitList)
			{
				if(tblWait.getWaitTime() < 0)
				{
					tblWait.setAvailableTime(new Date());
				}else if (tblWait.getAvailableTime().getTime() > ongoingShiftEnd){
					tblWait.setWaitTime(-1L);
					tblWait.setAvailableTime(new Date(0));
				}
			}
			waitlistResult.setTblWaitlist(tblWaitList);
			tblWaitMap.put(cover, waitlistResult);
		}
		
		responseMap.put("waitTimeResult", tblWaitMap);
		responseMap.put("reservationList", blockListMap);
		
		return responseMap;
}
	
	
	
	private TableWaitingTimeMobile getTableWaitingTimeForMobile(Table t, List<Reservation> reservationList, Map<String,Object> waitParams, String tableGuid)
	{
		TableWaitingTimeMobile tblWait = new TableWaitingTimeMobile();
		
		tblWait.setTableGuid(tableGuid);
		List<String> resvSourceList = new ArrayList<>();

		Long tatToUse = (Long) waitParams.get("tatToUse");
		Long actualTat = (Long) waitParams.get("actualTat");
		Long historicalTat = (Long) waitParams.get("historicalTat");
		Long inputTat = (Long) waitParams.get("inputTat");
		Long startTime = (Long) waitParams.get("startTime");

		if(reservationList == null)
		{
			tblWait.setWaitTime(0L);
			tblWait.setAvailableTime(new Date());
			resvSourceList.add("");

		}else if(reservationList.size() == 0){
			tblWait.setWaitTime(0L);
			tblWait.setAvailableTime(new Date());
			resvSourceList.add("");

		}
		else if(reservationList.size() == 1)
		{
			long resvTat = Long.valueOf(reservationList.get(0).getTat())*60*1000;
			tatToUse = resvTat == actualTat ? historicalTat : resvTat;

			if(reservationList.get(0).getActStartTime() != null && reservationList.get(0).getActEndTime() == null)
			{
				Long actualEndTime = reservationList.get(0).getActStartTime().getTime() + tatToUse;
				actualEndTime = (actualEndTime > reservationList.get(0).getEstEndTime().getTime()) ? actualEndTime : 
					reservationList.get(0).getEstEndTime().getTime();

				tblWait.setWaitTime(actualEndTime - startTime);
				tblWait.setAvailableTime(new Date(actualEndTime));
			}
			else if(reservationList.get(0).getEstStartTime().getTime() > (startTime + inputTat))
			{
				long x = 0l;
				tblWait.setWaitTime(x);
				tblWait.setAvailableTime(new Date());
			}
			else if(reservationList.get(0).getEstStartTime().getTime() < startTime && reservationList.get(0).getEstEndTime().getTime() > startTime)
			{
				if(reservationList.get(0).getBlockResv().equals("BLOCK")){
					tblWait.setWaitTime(reservationList.get(0).getEstEndTime().getTime() - startTime);
					tblWait.setAvailableTime(reservationList.get(0).getEstEndTime());
				}else{
					tblWait.setWaitTime(tatToUse);
					tblWait.setAvailableTime(new Date(startTime + tatToUse));
				}
			}
			else
			{
				tblWait.setWaitTime(reservationList.get(0).getEstStartTime().getTime() + tatToUse - startTime);
				tblWait.setAvailableTime(new Date(reservationList.get(0).getEstStartTime().getTime() + tatToUse));
			}
			resvSourceList.add(reservationList.get(0).getBlockResv());
		}
		else
		{
			Collections.sort(reservationList, new ReservationSort());
			List<CustomWaitlist> custWaitList = new ArrayList<>();
			int count = 0;
			long prevResvTat = 0L;
			long currResvTat;


			for(Reservation r : reservationList)
			{
				currResvTat = Long.valueOf(r.getTat())*60*1000;
				tatToUse = currResvTat == actualTat ? historicalTat : currResvTat;
				long x = 0L;
				CustomWaitlist custWait = new CustomWaitlist(r, t);
				custWait.setResvSource(r.getBlockResv());
				if(count == 0)
				{
					if(reservationList.get(0).getEstStartTime().getTime() > startTime){
						x = reservationList.get(0).getEstStartTime().getTime() - startTime;
					}

					if(x >= inputTat)
					{
						custWait.setGap(-1l);
						custWaitList.add(custWait);
						break;
					}
					else 
					{
						custWait.setGap(-1l);
						prevResvTat = currResvTat;
						count++;
					}
				}
				else
				{

					if(reservationList.get(count -1).getActStartTime() != null && reservationList.get(count -1).getActEndTime() == null)
					{
						Long actualEndTime = reservationList.get(count -1).getActStartTime().getTime() + prevResvTat;
						actualEndTime = Math.max(actualEndTime, reservationList.get(count -1).getEstEndTime().getTime());
						x = r.getEstStartTime().getTime() - Math.max(actualEndTime, startTime);
	
					}
					else if(reservationList.get(count -1).getEstStartTime().getTime() < startTime && reservationList.get(count -1).getEstEndTime().getTime() > startTime )
					{						
						
						Long reservationTat  = 0L;
						if(reservationList.get(count-1).getBlockResv().equals("BLOCK")){
							reservationTat = reservationList.get(count-1).getEstEndTime().getTime();
						}else{
							reservationTat = Math.max((startTime + prevResvTat), reservationList.get(count-1).getEstEndTime().getTime());
						}
						 
						x = r.getEstStartTime().getTime() - reservationTat;
					}
					else
					{
						x = r.getEstStartTime().getTime() - reservationList.get(count-1).getEstEndTime().getTime();
					}

					if(x >= inputTat)
					{
						custWaitList.get(count-1).setGap(x);
						break;
					}
					else 
					{
						custWaitList.get(count-1).setGap(x);
						if(custWaitList.get(count-1).getEstEndTime() > custWait.getEstEndTime()){
							custWait.setEstEndTime(custWaitList.get(count-1).getEstEndTime());
							r.setEstEndTime(new Date(custWaitList.get(count-1).getEstEndTime()));
						}
						custWait.setGap(-1l);
						count++;
						prevResvTat = currResvTat;
					}

				}
				custWaitList.add(custWait);
			}

			if(custWaitList.get(0).getEstStartTime() > (startTime + inputTat))
			{
				tblWait.setWaitTime(0L);
				tblWait.setAvailableTime(new Date());
				resvSourceList.add("");

			}else if (custWaitList.size() == 1 &&  custWaitList.get(0).getResv().getActStartTime() != null && 
					custWaitList.get(0).getResv().getActEndTime() == null){

				Long actualEndTime = custWaitList.get(0).getResv().getActStartTime().getTime() + prevResvTat;
				actualEndTime = (actualEndTime > custWaitList.get(0).getResv().getEstEndTime().getTime()) ? actualEndTime :
					custWaitList.get(0).getResv().getEstEndTime().getTime(); 

				if(actualEndTime - startTime > 0){
					tblWait.setWaitTime(actualEndTime - startTime);
					tblWait.setAvailableTime(new Date(actualEndTime));
				}else{
					tblWait.setWaitTime(actualEndTime - startTime);
					tblWait.setAvailableTime(new Date());
				}
				resvSourceList.add(custWaitList.get(0).getResvSource());

			}else if (custWaitList.size() == 1 && custWaitList.get(0).getEstStartTime() < startTime && custWaitList.get(0).getEstEndTime() > startTime){

				//Long waitTime = Integer.parseInt(custWaitList.get(0).getResv().getTat())*60*1000 == actualTat ? historicalTat : Integer.parseInt(custWaitList.get(0).getResv().getTat())*60*1000;

				Long waitTime;
				if(custWaitList.get(0).getResv().getBlockResv().equals("BLOCK"))
				{
					waitTime = custWaitList.get(0).getResv().getEstEndTime().getTime() - startTime;
				}
				else
				{
					waitTime = Integer.parseInt(custWaitList.get(0).getResv().getTat())*60*1000 == actualTat ? historicalTat : Integer.parseInt(custWaitList.get(0).getResv().getTat())*60*1000;
				}
				tblWait.setWaitTime(waitTime);
				tblWait.setAvailableTime(new Date(startTime + waitTime));
				resvSourceList.add(custWaitList.get(0).getResvSource());
			}
			else
			{
				Long waitTime = custWaitList.get(custWaitList.size()-1).getEstEndTime() - startTime;
				Long resvTat = Long.valueOf(custWaitList.get(custWaitList.size()-1).getResv().getTat())*60*1000;
				Long realWaitTime;

				if(custWaitList.get(custWaitList.size()-1).getResv().getBlockResv().equals("BLOCK") || custWaitList.get(custWaitList.size()-1).getResv().getReservationStatus().equals(Constants.SEATED)){
					realWaitTime = waitTime;
				}else{
					realWaitTime = (waitTime > resvTat) ? waitTime : resvTat;
				}
				tblWait.setWaitTime(realWaitTime);
				tblWait.setAvailableTime(new Date(custWaitList.get(custWaitList.size()-1).getEstEndTime()));
				for(CustomWaitlist customWait : custWaitList)
				{
					resvSourceList.add(customWait.getResvSource());

				}
			}
		}

		//tblWait.setResvSourceList(resvSourceList);
		return tblWait;

	}

	


}
