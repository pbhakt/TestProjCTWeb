package com.clicktable.service.impl;

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

import play.Logger;

import com.clicktable.comparator.TableWaitlistSort;
import com.clicktable.dao.intf.QueueDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.TableShuffleDao;
import com.clicktable.dao.intf.TatDao;
import com.clicktable.dao.intf.WaitlistDao;
import com.clicktable.model.Reservation;
import com.clicktable.model.Shift;
import com.clicktable.model.Table;
import com.clicktable.model.TableWaitingTime;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ReservationService;
import com.clicktable.service.intf.TableShuffleService;
import com.clicktable.service.intf.WaitlistService;
import com.clicktable.util.Constants;
import com.clicktable.util.ResponseCodes;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.ValidationError;

@Component
public class TableShuffleServiceImpl implements TableShuffleService {
	@Autowired
	AuthorizationService authService;

	@Autowired
	RestaurantValidator restaurantValidator;

	@Autowired
	ReservationService reservationService;

	@Autowired
	TableShuffleDao shuffleDao;

	@Autowired
	RestaurantDao restDao;

	@Autowired
	ReservationDao resvDao;

	@Autowired
	TatDao tatDao;

	@Autowired
	WaitlistService waitlistService;

	@Autowired
	WaitlistDao waitlistDao;

	@Autowired
	QueueDao queueDao;

/*	Map<String,Table> allTablesWithGuid;	
	Map<Integer,List<Table>> allTablesWithCovers;
	*/
	

/*	@Override
	public void getAllTables(String restaurantId, List<ValidationError> errorList){
		List<Object> allTablesList = waitlistDao.getAllTables(restaurantId);
		if(((Map<Integer, List<Table>>) allTablesList.get(0)).isEmpty())
		{
			errorList.add(new ValidationError(Constants.TABLE_LABEL, UtilityMethods.getErrorMsg(ErrorCodes.TABLE_NOT_AVAILABLE),ErrorCodes.TABLE_NOT_AVAILABLE));
		}
		if(((Map<Integer, List<Table>>) allTablesList.get(1)).isEmpty())
		{
			errorList.add(new ValidationError(Constants.TABLE_LABEL, UtilityMethods.getErrorMsg(ErrorCodes.TABLE_NOT_AVAILABLE),ErrorCodes.TABLE_NOT_AVAILABLE));
		}
		setAllTablesWithCovers((Map<Integer, List<Table>>) allTablesList.get(0));
		setAllTablesWithGuid((Map<String, Table>) allTablesList.get(1));
		
		
	}*/
	
/*	
	public Map<String, Table> getAllTablesWithGuid() {
		return allTablesWithGuid;
	}

	
	public void setAllTablesWithGuid(Map<String, Table> allTablesWithGuid) {
		this.allTablesWithGuid = allTablesWithGuid;
	}

	@Override
	public Map<Integer, List<Table>> getAllTablesWithCovers() {
		return allTablesWithCovers;
	}

	public void setAllTablesWithCovers(Map<Integer, List<Table>> allTablesWithCovers) {
		this.allTablesWithCovers = allTablesWithCovers;
	}*/

	@Override
	//@Transactional    /* Enhancement : Shuffle table method is made Transactional  ( 2015-10-26)*/
	public BaseResponse shuffleTables(Map<String, Object> params, String token) {
		
		Map<String, List<Reservation>> blockListMap = new HashMap<String, List<Reservation>>();
		BaseResponse getResponse = null;
		getResponse = shuffleTableAPI(params, token, blockListMap);
		return getResponse;
	}
	
	@Override
	public BaseResponse shuffleTableAPI(Map<String, Object> params, String token, 
			Map<String,List<Reservation>> blockListMap) {
		// TODO Auto-generated method stub

		List<ValidationError> errorList = new ArrayList<ValidationError>();
		BaseResponse getResponse = null;

		/* Validating Restaurant ID  */
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		String restaurantId =  waitlistService.isValidRest(userInfo, params, errorList);

		if(!errorList.isEmpty())
		{
			getResponse = new ErrorResponse(ResponseCodes.TABLE_SHUFFLE_FAILURE, errorList);
			return getResponse;
		}

		/*fetch applicable current shift*/
		Map<String, Object> shiftTime = waitlistService.getApplicableShifts( restaurantId, errorList,new Date(),false);

		if(!errorList.isEmpty())
		{
			getResponse = new ErrorResponse(ResponseCodes.TABLE_SHUFFLE_FAILURE, errorList);
			return getResponse;
		}
		
		//Long ongoingShiftStart = 0L;
		Long ongoingShiftEnd = 0L;
				
		

		/*fetch all tables with guid as key and covers as key*/
		

		Long startTime = (Long) shiftTime.get("startTime");
		Long currentDateTime = (Long) shiftTime.get("currentDateTime");
		//Long currentShiftStart = (Long) shiftTime.get("currentDayShiftStartTime");
		Long currentShiftEnd = (Long) shiftTime.get("currentDayShiftEndTime");
		List<Shift> shiftList = (List<Shift>) shiftTime.get("shiftList");
		
		for(Shift shift : shiftList){
			Boolean diffShiftStartAndCurrent = (shift.getStartTimeInMillis() - startTime <= 0);
			Boolean diffShiftEndAndCurrent = (shift.getEndTimeInMillis2() - startTime > 0);
			
			if(diffShiftStartAndCurrent && diffShiftEndAndCurrent){
				//ongoingShiftStart = shift.getStartTimeInMillis();
				ongoingShiftEnd = shift.getEndTimeInMillis2();
				break;
			}
		}
		
		/*get all tables*/
		
		//getAllTables(restaurantId,errorList);
		List<Object> allTablesList = waitlistDao.getAllTables(restaurantId);
		Map<Integer,List<Table>> allTablesWithCovers = (Map<Integer, List<Table>>) allTablesList.get(0);
		Map<String,Table> allTablesWithGuid = (Map<String, Table>) allTablesList.get(1);
		
		
		if(!errorList.isEmpty())
		{
			getResponse = new ErrorResponse(ResponseCodes.TABLE_SHUFFLE_FAILURE, errorList);
			return getResponse;
		}

		/* get all blocked tables*/
		Map<String,Object> blockResvParam = new HashMap<>();
		blockResvParam.put(Constants.REST_GUID, restaurantId);
		blockResvParam.put("currentDateTime", currentDateTime);
		blockResvParam.put("nextDateTime", (currentDateTime + 24*60*60*1000));
		blockResvParam.put("currentShiftEndTime", (currentShiftEnd + 60*60*1000L));
		blockResvParam.put("currentTime", startTime);

		//Map<String, List<Reservation>> blockListMap = new HashMap<String, List<Reservation>>();
		blockListMap = shuffleDao.getBlockedTables(blockResvParam, blockListMap);
		blockResvParam = null;

		/* get all the reservations with block event*/
		Map<String,Object> resvParam = new HashMap<>();	
		resvParam.put(Constants.START_TIME, startTime);
		resvParam.put("currentShiftEnd", ongoingShiftEnd);
		resvParam.put("restaurantId", restaurantId);
		List<Reservation> allReservationList = shuffleDao.getTablesHavingReservation(resvParam);
		resvParam = null;

		List<Reservation> reservationList = createBlockMapResvList(allReservationList, blockListMap, null);
		allReservationList = null;
		
		

		Boolean shuffleReservations = (params.get("ShuffleReservation") != null && !Boolean.valueOf(params.get("ShuffleReservation").toString())) ?
				false : true;
		for (Reservation resv : reservationList) {
			if(!shuffleReservations){
				addToBlockMap(resv, blockListMap);
				continue;
			}
			
			
			

			List<Table> applicableTables = allTablesWithCovers.get(resv.getNumCovers()) == null ? new ArrayList<>() 
					: allTablesWithCovers.get(resv.getNumCovers());
			
			
			for(Table t : applicableTables){
				Boolean canReserveTable = true;
				String tableGuid = t.getGuid();
				if(blockListMap.containsKey(tableGuid)){
					Boolean resvCheck = checkForBlockedReservation(resv, blockListMap, tableGuid);
					if(resvCheck){
						canReserveTable = false;
						continue;
					}

					if(canReserveTable){
						allocateTableToResv(t, resv, blockListMap, allTablesWithGuid);
						List<Reservation> resList = blockListMap.get(resv.getTableGuid().get(0)) == null ? new ArrayList<>() :
							blockListMap.get(resv.getTableGuid().get(0));
						resList.add(resv);
						blockListMap.put(resv.getTableGuid().get(0), resList);
						break;
					}

				}else{
					allocateTableToResv(t, resv, blockListMap, allTablesWithGuid);
					List<Reservation> resList = blockListMap.get(resv.getTableGuid().get(0)) == null ? new ArrayList<>() :
						blockListMap.get(resv.getTableGuid().get(0));
					resList.add(resv);
					blockListMap.put(resv.getTableGuid().get(0), resList);
					break;
				}

			}
		}

		
		if(shuffleReservations)
			resvDao.updateReservationForShuffle(reservationList);
		
		
		/* to add the no table reservations to blocklist map*/
		
		for (Reservation resv : reservationList) {
			if(!allTablesWithCovers.containsKey(resv.getTableGuid().get(0))){
				List<Reservation> resList = blockListMap.get(resv.getTableGuid().get(0)) == null ? new ArrayList<>() :
					blockListMap.get(resv.getTableGuid().get(0));
				resList.add(resv);
				blockListMap.put(resv.getTableGuid().get(0), resList);
			}
		}

		Map<String,Object> queueMap = new HashMap<>();
		queueMap.put(Constants.REST_ID, restaurantId);
		List<Reservation> queuedResv = queueDao.getQueuedReservation(queueMap);
		List<Reservation> walkinList = new ArrayList<>();

		for(Reservation resv : queuedResv)
		{
			walkinList.add(resv);

		}

		List<Reservation> updateWalkinList = new ArrayList<>();

		for (Reservation resv : walkinList) {

			Long inputTat = Long.valueOf(resv.getTat()) * 60 * 1000;

			Map<String,Object> waitParams = new HashMap<>();
			waitParams.put("tatToUse", inputTat);
			waitParams.put("historicalTat", inputTat);
			waitParams.put("actualTat", inputTat);
			waitParams.put("inputTat", inputTat);
			waitParams.put("startTime", startTime);

			List<TableWaitingTime> tblWaitList = new ArrayList<>();			
			List<Table> allApplicableTables = allTablesWithCovers.get(resv.getNumCovers()) == null ? new ArrayList<>() : allTablesWithCovers.get(resv.getNumCovers());


			if(resv.getTableGuid().size() > 1){
				Long waitTime = 0L;
				for(String tblGuid : resv.getTableGuid()){
					List<Reservation> resvList = blockListMap.get(tblGuid) == null ? 
							new ArrayList<>() : cloneReservationList(blockListMap.get(tblGuid));
					TableWaitingTime tblWait = waitlistService.getTableWaitingTime(null, resvList, waitParams,tblGuid);
					waitTime = (tblWait.getWaitTime() > waitTime) ? tblWait.getWaitTime() : waitTime;
				}
				
				Reservation clonedResv = new Reservation();
				try {
					clonedResv = (Reservation) resv.clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				clonedResv.setEstStartTime(new Date(startTime + waitTime));
				clonedResv.setEstEndTime(new Date(startTime + waitTime + Long.valueOf(resv.getTat())*60*1000));
				
				Boolean isBlock = false;
				for(String tblGuid : clonedResv.getTableGuid()){
					isBlock = checkForBlockedReservation(clonedResv, blockListMap, tblGuid);
					if(isBlock){
						break;
					}
				}
				if(isBlock){
					addToBlockMap(resv, blockListMap);
					continue;
				}
				
				if(startTime + waitTime > ongoingShiftEnd)
				{
					resv.setEstStartTime(new Date(0));
					resv.setEstEndTime(null);
					updateWalkinList.add(resv);
					continue;
				}
				
				resv.setEstStartTime(new Date(startTime + waitTime));
				resv.setEstEndTime(new Date(startTime + waitTime + Long.valueOf(resv.getTat())*60*1000));
				addToBlockMap(resv, blockListMap);
				updateWalkinList.add(resv);
				continue;
			}
			
			
			if(allApplicableTables.size() == 0)
			{
				resv.setEstStartTime(new Date(0));
				resv.setEstEndTime(null);
				if(resv.getPreferredTable() == null)
				{
					resv.setTableGuid(new ArrayList<>());
				}
				updateWalkinList.add(resv);
				continue;
			}
			
			for (Table t : allApplicableTables) {
				String tableGuid = t.getGuid();
				List<Reservation> resvList = blockListMap.get(t.getGuid()) == null ? 
						new ArrayList<>() : cloneReservationList(blockListMap.get(t.getGuid()));
						
						TableWaitingTime tblWait = waitlistService.getTableWaitingTime(null, resvList, waitParams,tableGuid);
						tblWaitList.add(tblWait);
						if(tblWait.getWaitTime() == 0){
							break;
						}
			}
			
			

			Collections.sort(tblWaitList, new TableWaitlistSort());
			int getIndex = 0;

			if (tblWaitList.get(0).getWaitTime() < 0) {
				for (int i = 1; i < tblWaitList.size(); i++) {
					if (tblWaitList.get(i).getWaitTime() == 0) {
						getIndex = i;
						break;
					}
				}
			}

			Long start_time = 0l;
			Long bestWaitTime = tblWaitList.get(getIndex).getWaitTime();

			/*if((bestWaitTime + new Date().getTime()) > ongoingShiftEnd)
			{
				resv.setEstStartTime(new Date(0));
				resv.setEstEndTime(null);
				if(resv.getPreferredTable() == null)
				{
					resv.setTableGuid(new ArrayList<>());
				}
				updateWalkinList.add(resv);
				continue;
			}*/

			if (bestWaitTime < 0) {
				start_time = new Date().getTime();
			} else {
				start_time = tblWaitList.get(getIndex).getAvailableTime().getTime();
			}

			String tableGuid = tblWaitList.get(getIndex).getTableGuid();
			Long currentTblWaitTime = bestWaitTime;

			if(resv.getTableGuid().size() > 0){
				String currentTableGuid = resv.getTableGuid().get(0);
				List<Reservation> existingResvList = blockListMap.get(currentTableGuid) == null ? 
						new ArrayList<>() : cloneReservationList(blockListMap.get(currentTableGuid));
						TableWaitingTime currentWLTableWaitTimeObj = waitlistService.getTableWaitingTime(null, existingResvList, waitParams, currentTableGuid);
						currentTblWaitTime = currentWLTableWaitTimeObj.getWaitTime();
			}else{
				List<String> tableGuidList = new ArrayList<>();
				tableGuidList.add(tableGuid);
				resv.setTableGuid(tableGuidList);
			}			
			
			Reservation updatedResv = allocateTableToWalkin(tableGuid, resv, start_time, bestWaitTime, currentTblWaitTime, allTablesWithGuid);
			
			//if waitlist goes out of operation hours than set est start time =0 on that table
			if(updatedResv.getEstStartTime().getTime() > ongoingShiftEnd)
			{
				resv.setEstStartTime(new Date(0));
				resv.setEstEndTime(null);
				if(resv.getPreferredTable() == null)
				{
					resv.setTableGuid(new ArrayList<>());
				}
				updateWalkinList.add(resv);
				continue;
			}
			
			tableGuid = updatedResv.getTableGuid().get(0);
			List<Reservation> walkinListAdd = new ArrayList<>();
			if(blockListMap.containsKey(tableGuid)){
				walkinListAdd = blockListMap.get(tableGuid);
			}
			walkinListAdd.add(updatedResv);
			blockListMap.put(tableGuid, walkinListAdd);
			updateWalkinList.add(resv);
		}
		

		resvDao.updateWalkinsForShuffle(updateWalkinList);
		getResponse = new GetResponse(ResponseCodes.TABLE_SHUFFLED_SUCCESFULLY, null);
		return getResponse;
	}
	
	

	@Override
	public List<Reservation> cloneReservationList(List<Reservation> resvList){

		List<Reservation> clonedReservationList = new ArrayList<>();
		for(Reservation r : resvList){
			try {
				Reservation clonedResv = (Reservation) r.clone();
				clonedReservationList.add(clonedResv);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return clonedReservationList;
	}


	public Reservation allocateTableToResv(Table t, Reservation resv, Map <String, List<Reservation>> blockListMap , Map<String,Table> allTablesWithGuid){

		Logger.debug("Parameters in allocate tabe to resv are------  resv.tableguid===" + resv.getTableGuid() + " table is====" + t.getGuid() );
			
		if(null == allTablesWithGuid){
			Logger.error("Parameters in allocate tabe to resv are------  resv.tableguid===" + resv.getTableGuid() + " table is====" + t.getGuid() );
			Logger.error("allTablesWithGuid parameter not initialized ");
			Logger.error("allTablesWithGuid is =================================================" + allTablesWithGuid);
			//getAllTables(resv.getRestaurantGuid(), new ArrayList<>());
			
		}
		
		if(null == allTablesWithGuid.get(resv.getTableGuid().get(0))){
			Logger.error("Parameters in allocate tabe to resv are------  resv.tableguid===" + resv.getTableGuid() + " table is====" + t.getGuid() );
			Logger.error("allTablesWithGuid.get(resv.getTableGuid().get(0)) is null ");
			Logger.error("allTablesWithGuid is =================================================" + allTablesWithGuid);
			
		}
		
		Integer resvTableMinCovers = allTablesWithGuid.get(resv.getTableGuid().get(0)).getMinCovers();
		Integer resvTableMaxCovers = allTablesWithGuid.get(resv.getTableGuid().get(0)).getMaxCovers();
		String tableGuid = t.getGuid();
		if(t.getMaxCovers() == resvTableMaxCovers && t.getMinCovers() == resvTableMinCovers){
			Boolean isBlocked = checkForBlockedReservation(resv, blockListMap, resv.getTableGuid().get(0));
			if(!isBlocked){
				tableGuid = resv.getTableGuid().get(0);
				resv.setIsUpdated(false);
			}
		}
		List<String> tblList = new ArrayList<>();
		tblList.add(tableGuid);
		resv.setTableGuid(tblList); 
		return resv;
	}

	@Override
	public Reservation allocateTableToWalkin(String tableGuid, Reservation resv, Long start_time, Long bestWaitTime, Long currentTblWaitTime, Map<String,Table> allTablesWithGuid)
	{
		SimpleDateFormat timestampFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		timestampFormat.setTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE));
		
		Table t = allTablesWithGuid.get(tableGuid);
		Table currentTable = allTablesWithGuid.get(resv.getTableGuid().get(0));
		Integer currentTblMaxCovers = currentTable.getMaxCovers();
		Integer currentTblMinCovers = currentTable.getMinCovers();
		
		if(resv.getPreferredTable() != null){
			start_time = new Date().getTime() + currentTblWaitTime;
			tableGuid = currentTable.getGuid();
		}else if((t.getMaxCovers().equals(currentTblMaxCovers)) && (t.getMinCovers().equals(currentTblMinCovers)) && (bestWaitTime.equals(currentTblWaitTime))){
			tableGuid = currentTable.getGuid();
		}

		try{
			start_time = timestampFormat.parse(timestampFormat.format(new Date(start_time))).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> tableGuidList = new ArrayList<>();
		tableGuidList.add(tableGuid);
		resv.setTableGuid(tableGuidList);
		resv.setEstStartTime(new Date(start_time));
		resv.setEstEndTime(new Date(start_time + Long.valueOf(resv.getTat())*60*1000));

		return resv;
	}



	/*@Override
	public BaseResponse CheckForResvShuffle(Map<String, Object> params, String restaurantId) {
		// TODO Auto-generated method stub
		
		Boolean isShuffled = true;
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		BaseResponse getResponse = new GetResponse<>();

		String resvGuid =  (String) params.get("reservationGuid");
		String toBeSeatedTables = (String) params.get("tableGuid");

		Setting up timezone and other time field 
		SimpleDateFormat timestampFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		timestampFormat.setTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE));

		fetch applicable current shift
		Map<String, Object> shiftTime = waitlistService.getApplicableShifts( restaurantId, errorList, new Date(), false);

		if(!errorList.isEmpty())
		{
			getResponse = new ErrorResponse("shift", errorList);
			return getResponse;
		}

		Long startTime = (Long) shiftTime.get("startTime");
		Long currentDateTime = (Long) shiftTime.get("currentDateTime");
		Long currentShiftStart = (Long) shiftTime.get("currentDayShiftStartTime");
		Long currentShiftEnd = (Long) shiftTime.get("currentDayShiftEndTime");

		List<Object> allTables = waitlistDao.getAllTables(restaurantId);
		Map<Integer,List<Table>> allTablesWithCovers = (Map<Integer, List<Table>>) allTables.get(0);
		Map<String,Table> allTablesWithGuid = (Map<String, Table>) allTables.get(1);
		
		
//		getAllTables(restaurantId, errorList);
		
		if(!errorList.isEmpty())
		{
			getResponse = new ErrorResponse(ResponseCodes.RESERVATION_RECORD_FETCH_FAILURE, errorList);
			return getResponse;
		}

		List<String> tableGuidList = new ArrayList<>();
		Integer totalCovers = 0;

		for(String tableGuid : toBeSeatedTables.split(","))
		{
			tableGuidList.add(tableGuid);
			totalCovers = totalCovers + allTablesWithGuid.get(tableGuid).getMaxCovers();
		}

		Map<String,Object> resvParam = new HashMap<>();
		resvParam.put(Constants.REST_GUID, restaurantId);
		resvParam.put("currentDateTime", currentDateTime);
		resvParam.put("nextDateTime", (currentDateTime + 24*60*60*1000));
		resvParam.put("currentShiftEndTime", (currentShiftEnd + 60*60*1000L));
		resvParam.put("currentTime", startTime);
	
		params.put(Constants.START_TIME, startTime);
		params.put("currentShiftEnd", (currentShiftEnd + 60*60*1000L));
		params.put("restaurantId", restaurantId);

		//Map<String,List<Reservation>> resvMap = shuffleDao.getTablesHavingReservation(params);
		Map<String,List<Reservation>> resvMap = null; 
		List<Reservation> reservationList = resvMap.get("Reservation");
		List<Reservation> seatedList = resvMap.get("Seated");

		Integer seatedIndex = -1;
		for(Reservation r :reservationList){
			if(r.getGuid().equals(resvGuid)){
				if(r.getNumCovers() > totalCovers){
					errorList.add(new ValidationError("num covers", "more than max capacity of table"));
					getResponse = new ErrorResponse("1234", errorList);
					return getResponse;
				}
				Reservation resv = new Reservation();
				try {
					resv = (Reservation) r.clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				resv.setTableGuid(tableGuidList);
				seatedList.add(resv);
				seatedIndex = seatedList.indexOf(resv);
				reservationList.remove(resv);
				break;
			}
		}

		Get a list of all the blocked tables

		Map<String, List<Reservation>> blockListMap = new HashMap<String, List<Reservation>>();
		blockListMap = shuffleDao.getBlockedTables(resvParam, blockListMap);

		for(Reservation r : seatedList){
			for(String tableGuid : r.getTableGuid()){
				List <Reservation> seatedTable = new ArrayList<>();
				if(blockListMap.containsKey(tableGuid)){
					seatedTable = blockListMap.get(tableGuid);
				}
				seatedTable.add(r);
				blockListMap.put(tableGuid, seatedTable);
			}
		}


		for (Reservation resv : reservationList) {

			List<Table> applicableTables = allTablesWithCovers.get(resv.getNumCovers());
			List<Table> finalApplicableTables = applicableTables;
			String preferredSection = resv.getPrefferedSection();
			if(preferredSection != null)
			{
				finalApplicableTables = new ArrayList<>();
				for(Table t : applicableTables)
				{
					if(preferredSection.equals(t.getSectionId()))
					{
						finalApplicableTables.add(t);
					}
				}
			}

			Long resvStartTime = resv.getEstStartTime().getTime();
			Long resvEndTime = resv.getEstEndTime().getTime();
			
			
			if(null == allTablesWithGuid){
				Logger.error("Parameters in allocate tabe to resv are------  resv.tableguid===" + resv.getTableGuid()  );
				Logger.error("allTablesWithGuid parameter not initialized ");
				Logger.error("allTablesWithGuid is =================================================" + allTablesWithGuid);
				//getAllTables(resv.getRestaurantGuid(), new ArrayList<>());
				
			}
			
			if(null == allTablesWithGuid.get(resv.getTableGuid().get(0))){
				Logger.error("Parameters in allocate tabe to resv are------  resv.tableguid===" + resv.getTableGuid() );
				Logger.error("allTablesWithGuid.get(resv.getTableGuid().get(0)) is null ");
				Logger.error("allTablesWithGuid is =================================================" + allTablesWithGuid);
			}

			Integer resvTableMinCovers = allTablesWithGuid.get(resv.getTableGuid().get(0)).getMinCovers();
			Integer resvTableMaxCovers = allTablesWithGuid.get(resv.getTableGuid().get(0)).getMaxCovers();
			Boolean isShufflePossible = true;
			for(Table t : finalApplicableTables){
				isShufflePossible = true;
				Boolean canReserveTable = true;
				String tableGuid = t.getGuid();
				if(blockListMap.containsKey(t.getGuid())){
					List<Reservation> resList = blockListMap.get(t.getGuid());
					for(Reservation r : resList){
						Boolean resvCheck = checkForBlockedReservation(r, blockListMap, null);
						if(resvCheck){
							canReserveTable = false;
							isShufflePossible = false;
							break;
						}
					}
					if(canReserveTable){
						if(t.getMaxCovers() == resvTableMaxCovers  && t.getMinCovers() == resvTableMinCovers){
							tableGuid = resv.getTableGuid().get(0);
							resv.setIsUpdated(false);
						}

						List<Reservation> resvList = new ArrayList<>();
						if(blockListMap.containsKey(tableGuid))
						{
							resvList = blockListMap.get(tableGuid);
						}

						List<String> tblList = new ArrayList<>();
						tblList.add(tableGuid);
						resv.setTableGuid(tblList); 
						resvList.add(resv);
						blockListMap.put(tableGuid, resvList);
						break;
					}

				}else{
					if(t.getMaxCovers() == resvTableMaxCovers  && t.getMinCovers() == resvTableMinCovers){
						tableGuid = resv.getTableGuid().get(0);
						canReserveTable = true;
						if(blockListMap.containsKey(tableGuid))
						{
							for(Reservation r : blockListMap.get(tableGuid)){
								if((r.getEstStartTime().getTime() >= resvStartTime && r.getEstStartTime().getTime() < resvEndTime)
										|| (r.getEstEndTime().getTime() > resvStartTime && r.getEstEndTime().getTime() <= resvEndTime)
										|| (r.getEstStartTime().getTime() < resvStartTime && r.getEstEndTime().getTime() > resvEndTime)){
									canReserveTable = false;
									//isShufflePossible = false;
									break;
								}
							}
						}
						if(canReserveTable){
							resv.setIsUpdated(false);
						}else{
							tableGuid = t.getGuid();
							List<String> tableList = new ArrayList<>();
							tableList.add(tableGuid);
							resv.setTableGuid(tableList);
							resv.setIsUpdated(true);
						}

					}
					List<String> tblList = new ArrayList<>();
					tblList.add(tableGuid);
					List<Reservation> resvList = new ArrayList<>();
					resv.setTableGuid(tblList);
					resvList.add(resv);
					blockListMap.put(tableGuid, resvList);
					break;
				}

			}
			if(!isShufflePossible){
				errorList.add(new ValidationError("table", "No table available"));
				getResponse = new ErrorResponse("0000000", errorList);
				return getResponse;
			}
		}


		if(seatedIndex != -1){
			Logger.debug("start index is----------------------"+seatedIndex);
			reservationList.add(seatedList.get(seatedIndex));
		}


		for(Reservation r : reservationList)
		{
			r.setGuest_firstName(allTablesWithGuid.get(r.getTableGuid().get(0)).getName());
		}

		Logger.debug("reservationList------------->>>" + reservationList);

		getResponse = new GetResponse<Reservation>(ResponseCodes.RESERVATION_RECORD_FETCH_SUCCESFULLY, reservationList);
		//resvDao.updateReservationForShuffle(reservationList);

		//Shuffle Walkins

		//DAO to get all the queued objects.
		//iterate the result set on the basis of walkin logic


			Map<String,Object> queueMap = new HashMap<>();
		queueMap.put(Constants.REST_ID, restaurantId);

		List<Reservation> queuedResv = queueDao.getQueuedReservation(queueMap);
		List<Reservation> walkinList = new ArrayList<>();

		for(Reservation resv : queuedResv)
		{
			if(resv.getTableGuid().size() > 1)
			{
				for(String tableGuid : resv.getTableGuid())
				{
					List <Reservation> multipleTabResv = new ArrayList<>();
					if(blockListMap.containsKey(tableGuid))
					{
						multipleTabResv = blockListMap.get(tableGuid);
					}
					multipleTabResv.add(resv);
					blockListMap.put(tableGuid, multipleTabResv);
				}

			}
			else
			{
				walkinList.add(resv);
			}

		}

		Logger.debug("Walkin list is-------------------------------" + walkinList);


		Long walkinItrTime = new Date().getTime();
		for (Reservation resv : walkinList) {

			Long inputTat = Long.valueOf(resv.getTat()) * 60 * 1000;

			Map<String,Object> waitParams = new HashMap<>();
			waitParams.put("tatToUse", inputTat);
			waitParams.put("historicalTat", inputTat);
			waitParams.put("actualTat", inputTat);
			waitParams.put("inputTat", inputTat);
			waitParams.put("startTime", startTime);

			List<TableWaitingTime> tblWaitList = new ArrayList<>();

			Map<String, List<Reservation>> tableResvMap = new LinkedHashMap<>();

			List<Table> allApplicableTables = allTables.get(resv.getNumCovers());
			String preferredSection = resv.getPrefferedSection();

			for(Table t : allApplicableTables){
				if(preferredSection != null)
				{
					if(preferredSection.equals(t.getSectionId()))
					{
						tableResvMap.put(t.getGuid(), blockListMap.get(t.getGuid()));
					}
				}else
				{
					tableResvMap.put(t.getGuid(), blockListMap.get(t.getGuid()));
				}
			}

			for (Map.Entry<String, List<Reservation>> entry : tableResvMap.entrySet()) {
				String tableGuid = entry.getKey();
				//Long tatToUse = 0L;
				List<Reservation> resvList = entry.getValue();
				TableWaitingTime tblWait = waitlistService.getTableWaitingTime(null, resvList, waitParams,tableGuid);
				tblWaitList.add(tblWait);
				if(tblWait.getWaitTime() == 0){
					break;
				}
			}

			Collections.sort(tblWaitList, new TableWaitlistSort());

			int getIndex = 0;

			if (tblWaitList.get(0).getWaitTime() < 0) {

				for (int i = 1; i < tblWaitList.size(); i++) {
					if (tblWaitList.get(i).getWaitTime() == 0) {
						getIndex = i;
						break;
					}
				}

			}

			Long start_time = 0l;

			Long bestWaitTime = tblWaitList.get(getIndex).getWaitTime();
			if (bestWaitTime < 0) {
				start_time = new Date().getTime();

			} else {
				start_time = tblWaitList.get(getIndex).getAvailableTime().getTime();
			}

			String tableGuid = tblWaitList.get(getIndex).getTableGuid();
			Integer tableMaxCovers = tableMap.get(tableGuid).getMaxCovers();
			Integer tableMinCovers = tableMap.get(tableGuid).getMinCovers();

			Current Waitlist Table 

			String currentTableGuid = resv.getTableGuid().get(0);
			Integer currentTblMaxCovers = tableMap.get(currentTableGuid).getMaxCovers();
			Integer currentTblMinCovers = tableMap.get(currentTableGuid).getMinCovers();

			List<Reservation> newReservationList = new ArrayList<>();
			List<Reservation> existingResvList = tableResvMap.get(currentTableGuid) == null ? new ArrayList<>() : tableResvMap.get(currentTableGuid); 

			for(Reservation r : existingResvList){
				try {
					Reservation clonedResv = (Reservation) r.clone();
					newReservationList.add(clonedResv);
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}


			TableWaitingTime currentWLTableWaitTimeObj = waitlistService.getTableWaitingTime(null, newReservationList, waitParams, currentTableGuid);
			Long currentTblWaitTime = currentWLTableWaitTimeObj.getWaitTime();

			if(tableMaxCovers == currentTblMaxCovers && tableMinCovers == currentTblMinCovers && bestWaitTime == currentTblWaitTime){
				tableGuid = currentTableGuid;
			}else if(resv.getPreferredTable() != null){
				start_time = currentTblWaitTime;
				tableGuid = currentTableGuid;
			}


		 * Code added to change the available time for waitlist with preffered table as true.


			if(resv.getPreferredTable() != null){
				List<Reservation> resvList = tableResvMap.get(resv.getTableGuid().get(0));
				List<Reservation> newResList = new ArrayList<>();

				for(Reservation r : resvList){
					try {
						Reservation clonedResv = (Reservation) r.clone();
						newResList.add(clonedResv);
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				TableWaitingTime tblWait = waitlistService.getWaitlistTimeForTable(new HashMap<>(), newResList, resv);
				start_time = tblWait.getAvailableTime().getTime();
				tableGuid = resv.getTableGuid().get(0);
			}

			try{
				start_time = timestampFormat.parse(timestampFormat.format(new Date(start_time))).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			List<String> tableGuidList = new ArrayList<>();
			tableGuidList.add(tableGuid);
			resv.setTableGuid(tableGuidList);
			resv.setEstStartTime(new Date(start_time));
			resv.setEstEndTime(new Date(start_time + inputTat));

			List<Reservation> walkinListAdd = new ArrayList<>();

			if(blockListMap.containsKey(tableGuid)){
				walkinListAdd = blockListMap.get(tableGuid);
	}


			walkinListAdd.add(resv);
			blockListMap.put(tableGuid, walkinListAdd);
	}

		Logger.debug("Waiting time for walkin list is--------------------------------------------" + (new Date().getTime() - walkinItrTime));

		resvDao.updateWalkinsForShuffle(walkinList);
		 

		return getResponse;
	}
*/

	private List<Reservation> createBlockMapResvList(List<Reservation> allReservationList, Map<String, List<Reservation>> blockListMap, 
			Map<String, List<Reservation>> returnMap) {
		List<Reservation> tablesToBeShuffled = new ArrayList<>();
		Boolean returnMapNull = (returnMap == null);
		
		for(Reservation reservation : allReservationList){
			if(reservation.getReservationStatus().equals("SEATED") || reservation.getTableGuid().size() > 1 || 
					(reservation.getPreferredTable() != null && reservation.getBookingMode().equals(Constants.ONLINE_STATUS))){
				
				addToBlockMap(reservation, (returnMapNull ? blockListMap : returnMap));
			}else if(reservation.getTableGuid().size() == 1 && reservation.getBookingMode().equals(Constants.ONLINE_STATUS)){
				if(blockListMap.containsKey(reservation.getTableGuid().get(0))){
					Boolean isBlock = checkForBlockedReservation(reservation, blockListMap, null);
					if(isBlock){
						addToBlockMap(reservation, (returnMapNull ? blockListMap : returnMap));
					}else{
						tablesToBeShuffled.add(reservation);
					}
				}else{
					tablesToBeShuffled.add(reservation);
				}
			}
		}
		return tablesToBeShuffled;
	}
	
	private List<Reservation> createBlockMapResvListForNoTables(List<Reservation> allReservationList, Map<String, List<Reservation>> blockListMap, 
			Map<String, List<Reservation>> returnMap, Map<Integer,List<Table>> allTablesWithCovers) {
		
		
		List<Reservation> tablesToBeShuffled = new ArrayList<>();
		Boolean returnMapNull = (returnMap == null);
		
	
		for(Reservation reservation : allReservationList){
			 if(reservation.getTableGuid().size() == 1 && reservation.getBookingMode().equals(Constants.ONLINE_STATUS)){		 
				 if(!allTablesWithCovers.containsKey(reservation.getNumCovers())){
					 addToBlockMap(reservation, (returnMapNull ? blockListMap : returnMap));
				 }else{
					 tablesToBeShuffled.add(reservation);
				 }
			}else{
				tablesToBeShuffled.add(reservation);
			}
		}
		return tablesToBeShuffled;
	}
	
	
	
	
	
	/*private List<Reservation> createBlockMapResvList2(List<Reservation> allReservationList, 
			Map<String, List<Reservation>> blockListMap, Map<String, List<Reservation>> returnMap) {
		List<Reservation> tablesToBeShuffled = new ArrayList<>();
		for(Reservation reservation : allReservationList){
			if(reservation.getReservationStatus().equals("SEATED") || reservation.getTableGuid().size() > 1 || 
					(reservation.getPreferredTable() != null && reservation.getBookingMode().equals(Constants.ONLINE_STATUS))){
				addToBlockMap(reservation, returnMap);
			}else if(reservation.getTableGuid().size() == 1 && reservation.getBookingMode().equals(Constants.ONLINE_STATUS)){
				if(blockListMap.containsKey(reservation.getTableGuid().get(0))){
					Boolean isBlock = checkForBlockedReservation(reservation, blockListMap, null);
					if(isBlock){
						addToBlockMap(reservation, returnMap);
					}else{
						tablesToBeShuffled.add(reservation);
					}
				}else{
					tablesToBeShuffled.add(reservation);
				}
			}
		}
		return tablesToBeShuffled;
	}*/

	@Override
	public void addAllResvToBlockMap(List<Reservation> allReservationList, Map<String, List<Reservation>> blockListMap) {
		for(Reservation reservation : allReservationList){
			addToBlockMap(reservation, blockListMap);
		}
	}



	private void addToBlockMap(Reservation resv, Map<String, List<Reservation>> blockListMap) {

		for(String tableGuid : resv.getTableGuid()){
			List<Reservation> reservationListForKey = new ArrayList<>();
			if(blockListMap.containsKey(tableGuid)){
				reservationListForKey = blockListMap.get(tableGuid);
			}
			reservationListForKey.add(resv);
			blockListMap.put(tableGuid, reservationListForKey);
		}
	}

	@Override
	public Boolean checkForBlockedReservation(Reservation resv, Map<String, List<Reservation>> blockListMap, String tableGuid) {
		List<Reservation> blockedResv = tableGuid == null ? blockListMap.get(resv.getTableGuid().get(0)) : blockListMap.get(tableGuid);
		blockedResv = blockedResv == null ? new ArrayList<>() : blockedResv;

		//Long resvEstStartTime = resv.getEstStartTime().getTime();
		
		for(Reservation block : blockedResv){
			//Long blockEstStartTime = block.getEstEndTime().getTime();
			
			if((resv.getEstStartTime().getTime() <= block.getEstStartTime().getTime() && resv.getEstEndTime().getTime() > block.getEstStartTime().getTime())
					|| (resv.getEstStartTime().getTime() < block.getEstEndTime().getTime() && resv.getEstEndTime().getTime() >= block.getEstEndTime().getTime())
					|| (resv.getEstStartTime().getTime() >= block.getEstStartTime().getTime() && resv.getEstEndTime().getTime() <= block.getEstEndTime().getTime())){

				return true;
			}
		}
		return false;
	}
	
	@Override
	public Boolean shuffleTablesMethod(Reservation existingReservation, String reservationStatus)
	{
		// TODO Auto-generated method stub

		List<ValidationError> errorList = new ArrayList<ValidationError>();
		//BaseResponse getResponse = null;
		String restaurantId =  existingReservation.getRestaurantGuid();
		
		/* new Date() was replaced resvDate to check the availability for edit/create reservation for future date*/
		/*Date resvDate = existingReservation.getEstStartTime() == null && reservationStatus.equals(Constants.SEATED)
				? new Date() : existingReservation.getEstStartTime();*/
		
		Date resvDate = reservationStatus == null ? existingReservation.getEstStartTime() : new Date();
		Map<String, Object> shiftTime = waitlistService.getApplicableShifts( restaurantId, errorList, resvDate, false);

		if(!errorList.isEmpty())
		{
			//getResponse = new ErrorResponse(ResponseCodes.TABLE_SHUFFLE_FAILURE, errorList);
			return false;
		}

		/*fetch all tables with guid as key and covers as key*/
		List<Object> allTablesList = waitlistDao.getAllTables(restaurantId);
		Map<Integer,List<Table>> allTablesWithCovers = (Map<Integer, List<Table>>) allTablesList.get(0);
		Map<String,Table> allTablesWithGuid = (Map<String, Table>) allTablesList.get(1);
//		getAllTables(restaurantId, errorList);
	
		if(!errorList.isEmpty())
		{
			//getResponse = new ErrorResponse(ResponseCodes.TABLE_SHUFFLE_FAILURE, errorList);
			return false;
		}

		Long startTime = (Long) shiftTime.get("startTime");
		Long currentDateTime = (Long) shiftTime.get("currentDateTime");
		//Long currentShiftStart = (Long) shiftTime.get("currentDayShiftStartTime");
		Long currentShiftEnd = (Long) shiftTime.get("currentDayShiftEndTime");

		/* get all blocked tables*/
		Map<String,Object> blockResvParam = new HashMap<>();
		blockResvParam.put(Constants.REST_GUID, restaurantId);
		blockResvParam.put("currentDateTime", currentDateTime);
		blockResvParam.put("nextDateTime", (currentDateTime + 24*60*60*1000));
		blockResvParam.put("currentShiftEndTime", (currentShiftEnd + 60*60*1000L));
		blockResvParam.put("currentTime", startTime);

		Map<String, List<Reservation>> blockListMap = new HashMap<String, List<Reservation>>();
		/*HashMap<String, List<Reservation>> OnlyBlockTablesMap = new HashMap<String, List<Reservation>>();*/
		blockListMap = shuffleDao.getBlockedTables(blockResvParam, blockListMap);
		/*OnlyBlockTablesMap = (HashMap<String, List<Reservation>>) blockListMap.clone();*/
		blockResvParam = null;

		/* get all the reservations with block event*/
		Map<String,Object> resvParam = new HashMap<>();	
		resvParam.put(Constants.START_TIME, startTime);
		resvParam.put("currentShiftEnd", (currentShiftEnd + 60*60*1000L));
		resvParam.put("restaurantId", restaurantId);
		List<Reservation> allReservationList = shuffleDao.getTablesHavingReservation(resvParam);
		resvParam = null;
	
		for(Reservation r : allReservationList){
			if(r.getGuid().equals(existingReservation.getGuid())){
				allReservationList.remove(r);
				break;
			}
		}
		
		Map<String, List<Reservation>> returnMap = new HashMap<String, List<Reservation>>();
		
		List<Reservation> reservationListForShuffle = createBlockMapResvList(allReservationList, blockListMap, returnMap);
		List<Reservation> reservationList = createBlockMapResvListForNoTables(reservationListForShuffle, blockListMap, returnMap, allTablesWithCovers);
		

		allReservationList = null;
		//Boolean isReservationInBlockMap = false;
		
		
		
		if(existingReservation.getBookingMode().equals(Constants.WALKIN_STATUS) && 
				!existingReservation.getReservationStatus().equals(Constants.SEATED) && reservationStatus != null){
			Long sTime = Calendar.getInstance().getTimeInMillis();
			existingReservation.setEstStartTime(new Date(sTime));
			existingReservation.setEstEndTime(new Date(sTime + Long.valueOf(existingReservation.getTat())*60*1000));
		}
		
		if(existingReservation.getBookingMode().equals(Constants.ONLINE_STATUS) && 
				!existingReservation.getReservationStatus().equals(Constants.SEATED) && reservationStatus != null && 
				reservationStatus.equals(Constants.SEATED)){
			Long sTime = Calendar.getInstance().getTimeInMillis();
			if(existingReservation.getEstStartTime().getTime() > sTime){
				existingReservation.setEstStartTime(new Date(sTime));
			}
			//existingReservation.setEstEndTime(new Date(sTime + Long.valueOf(existingReservation.getTat())*60*1000));
		}
		
		
		/*This case is required to handlke when the table is overdue*/
		if(reservationStatus != null && reservationStatus.equalsIgnoreCase(Constants.SEATED)){
			for(String t : existingReservation.getTableGuid()){
				if(returnMap.containsKey(t)){
					List<Reservation> resvList = returnMap.get(t);
					for(Reservation r : resvList){
						if(r.getReservationStatus().equals(Constants.SEATED)){
							return false;
						}
					}
				}
			}
		}
		
		for(String t : existingReservation.getTableGuid()){
			Boolean checkBlockMap = checkForBlockedReservation(existingReservation, returnMap, t);
			if(checkBlockMap){
				return false;
			}
		}
		
		//TODO : List of error!
		
		addToBlockMap(existingReservation, returnMap);
		
		for(Reservation r : reservationList){
			if(r.getGuid().equals(existingReservation.getGuid())){
				reservationList.remove(r);
				break;
			}
		}
		
		for(Map.Entry<String, List<Reservation>> entry : returnMap.entrySet()){
			List<Reservation> resvList = new ArrayList<>();
			if(blockListMap.containsKey(entry.getKey()))
			{
				resvList = blockListMap.get(entry.getKey());
			}
			
			resvList.addAll(entry.getValue());
			blockListMap.put(entry.getKey(), resvList);
		}
		
		
		for (Reservation resv : reservationList) {

			List<Table> applicableTables = allTablesWithCovers.get(resv.getNumCovers()) == null ? new ArrayList<>() : 
				allTablesWithCovers.get(resv.getNumCovers());
			Boolean canShuffleTable = false;
			
			for(Table t : applicableTables){
				Boolean canReserveTable = true;
				String tableGuid = t.getGuid();
				if(blockListMap.containsKey(tableGuid)){
					//List<Reservation> resList = blockListMap.get(tableGuid);
					Boolean resvCheck = checkForBlockedReservation(resv, blockListMap, tableGuid);
					if(resvCheck){
						canReserveTable = false;
						continue;
					}

					if(canReserveTable){
						canShuffleTable = true;
						allocateTableToResv(t, resv, blockListMap, allTablesWithGuid);
						List<Reservation> resList = blockListMap.get(resv.getTableGuid().get(0)) == null ? new ArrayList<>() :
							blockListMap.get(resv.getTableGuid().get(0));
						resList.add(resv);
						blockListMap.put(resv.getTableGuid().get(0), resList);
						break;
					}

				}else{
					canShuffleTable = true;
					allocateTableToResv(t, resv, blockListMap, allTablesWithGuid);
					List<Reservation> resList = blockListMap.get(resv.getTableGuid().get(0)) == null ? new ArrayList<>() :
						blockListMap.get(resv.getTableGuid().get(0));
					resList.add(resv);
					blockListMap.put(resv.getTableGuid().get(0), resList);
					break;
				}

			}
			if(!canShuffleTable){
				return false;
			}
		}
		resvDao.updateReservationForShuffle(reservationList);
		return true;
	}
	
	
	@Override
	public Boolean checkForBlockedReservationQuickSearch(Reservation resv, Map<String, List<Reservation>> blockListMap, String tableGuid,Map<String,String> blockReasonMap ) {
		List<Reservation> blockedResv = tableGuid == null ? blockListMap.get(resv.getTableGuid().get(0)) : blockListMap.get(tableGuid);
		blockedResv = blockedResv == null ? new ArrayList<>() : blockedResv;

		//Long resvEstStartTime = resv.getEstStartTime().getTime();
		
		for(Reservation block : blockedResv){
			//Long blockEstStartTime = block.getEstEndTime().getTime();
			
			if((resv.getEstStartTime().getTime() <= block.getEstStartTime().getTime() && resv.getEstEndTime().getTime() > block.getEstStartTime().getTime())
					|| (resv.getEstStartTime().getTime() < block.getEstEndTime().getTime() && resv.getEstEndTime().getTime() >= block.getEstEndTime().getTime())
					|| (resv.getEstStartTime().getTime() >= block.getEstStartTime().getTime() && resv.getEstEndTime().getTime() <= block.getEstEndTime().getTime())){

				blockReasonMap.put("reason", block.getReasonToCancel());
				return true;
			}
		}
		return false;
	}
}
