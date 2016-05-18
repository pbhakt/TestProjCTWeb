package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.dao.intf.QueueDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.RestaurantHasTableDao;
import com.clicktable.dao.intf.TableDao;
import com.clicktable.dao.intf.TableShuffleDao;
import com.clicktable.dao.intf.WaitlistDao;
import com.clicktable.model.CustomTable;
import com.clicktable.model.Reservation;
import com.clicktable.model.Table;
import com.clicktable.model.UserInfoModel;
import com.clicktable.repository.ReservationRepo;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.TableService;
import com.clicktable.service.intf.TableShuffleService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.TableValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class TableServiceImpl implements TableService {

	@Autowired
	TableDao tableDao;

	@Autowired
	TableValidator tableValidator;

	@Autowired
	RestaurantValidator restaurantValidator;

	@Autowired
	RestaurantHasTableDao restaurantHasTableDao;

	@Autowired
	RestaurantDao restDao;
	
	@Autowired
	ReservationRepo resvRepo;

	@Autowired
	AuthorizationService authorizationService;
	
	@Autowired
	WaitlistDao waitlistDao;
	
	@Autowired
	TableShuffleDao shuffleDao;
	
	@Autowired
	TableShuffleService shuffleService;
	
	
	@Autowired
	QueueDao queueDao;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse addTable(Table table, String token) {
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);

		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		BaseResponse response;
		listOfError.addAll(tableValidator.validateTableOnAdd(table));
		tableValidator.validateTableForRestaurant(table, userInfo, listOfError);
		
		  if((table.getSectionId() != null) && (!table.getSectionId().equals("")))
		  { tableValidator.validateTableForSection(table, listOfError); }
		 
		if (listOfError.isEmpty()) {
			/* Call TableDaoImpl to set Relationship b/w table */
			String table_saved_guid = tableDao.addRestaurantTable(table);
			if (table_saved_guid == null) {
				listOfError.add(new ValidationError(Constants.REST_ID + "/" + Constants.SECTION_ID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REST_ID) + "/"
						+ UtilityMethods.getErrorMsg(ErrorCodes.INVALID_SECTION_ID)));
				response = new ErrorResponse(ResponseCodes.TABLE_ADDED_FAILURE, listOfError);
			} else {
				response = new PostResponse<Table>(ResponseCodes.TABLE_ADDED_SUCCESFULLY, table_saved_guid);
			}

		} else
			response = new ErrorResponse(ResponseCodes.TABLE_ADDED_FAILURE, listOfError);

		return response;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse updateTable(Table table, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		/* Validating Restaurant | Table | UserInfo */
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		// Table existingTable = tableValidator.validateGuid(table.getGuid(),
		// listOfError);
		if (listOfError.isEmpty()) {
			// table.copyExistingValues(existingTable);
			listOfError.addAll(tableValidator.validateTableOnUpdate(table));
			if (listOfError.isEmpty()) {
				tableValidator.validateTableForRestaurantOnUpdate(table, userInfo, listOfError);
				// restaurant =
				// restaurantValidator.validateGuid(existingTable.getRestId(),
				// listOfError);
			}

			if ((table.getSectionId() != null) && (!table.getSectionId().equals(""))) {
				tableValidator.validateTableForSection(table, listOfError);
			}
			// Table getTable = tableDao.find(Table.class, table.getGuid());
			tableValidator.validateTableForRestaurantOnUpdate(table, userInfo, listOfError);
			/* Finding Restaurant */
			if (listOfError.isEmpty()) {
				/* Boolean toDeleteRelWithSection =(table.getSection() == null); */

				String updatedTable_guid = tableDao.updateRestaurantTable(table);
				response = new UpdateResponse<Table>(ResponseCodes.TABLE_UPDATED_SUCCESFULLY, updatedTable_guid);
			} else {
				response = new ErrorResponse(ResponseCodes.TABLE_UPDATED_FAILURE, listOfError);
			}
		} else {
			response = new ErrorResponse(ResponseCodes.TABLE_UPDATED_FAILURE, listOfError);
		}
		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public BaseResponse getTables(Map<String, Object> params, String token) {
		BaseResponse getResponse;
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)))
			params.put(Constants.REST_ID, userInfo.getRestGuid());
		if (!params.containsKey(Constants.STATUS) && !params.containsKey(Constants.GUID)) {
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
     	}
		
		
		Map<String, Object> qryParamMap = tableValidator
				.validateFinderParams(params);
		//System.out.println("-----QueryParam---" + qryParamMap);
		List<Table> final_table_list = new ArrayList<Table>();
		List<Table> tables = tableDao.findByFields(Table.class, qryParamMap);
		/*Map<String, Table> tableMap = new HashMap<String, Table>();
		for (Table table : tables) {
			tableMap.put(table.getGuid(), table);
		}*/
		//Map<String, String> customTbl = tableDao.getCustomTable(tableMap);
		for (Table table : tables) {
			CustomTable ct = new CustomTable(table);
			/*if (customTbl.get(table.getGuid()) != null) {
				String server_guid = (String) customTbl.get(table.getGuid());
				ct.setServerGuid(server_guid);
			}else{
				ct.setServerGuid(null);
			}*/
			final_table_list.add(ct);
		}

		getResponse = new GetResponse<Table>(ResponseCodes.TABLE_RECORD_FETCH_SUCCESFULLY, final_table_list);
		return getResponse;
	}
	
	
	/*public BaseResponse getTables(Map<String, Object> params, String token) {
		BaseResponse getResponse;
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)))
			params.put(Constants.REST_ID, userInfo.getRestGuid());
		if (!params.containsKey(Constants.STATUS) && !params.containsKey(Constants.GUID)) {
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
     	}
		
		Map<String, Object> qryParamMap = tableValidator
				.validateFinderParams(params);
		//System.out.println("-----QueryParam---" + qryParamMap);
		List<Table> final_table_list = new ArrayList<Table>();
		List<Table> tables = tableDao.findByFields(Table.class, qryParamMap);
		Map<String, Table> tableMap = new HashMap<String, Table>();
		for (Table table : tables) {
			tableMap.put(table.getGuid(), table);
		}
		//Map<String, String> customTbl = tableDao.getCustomTable(tableMap);
		for (Table table : tables) {
			CustomTable ct = new CustomTable(table);
			if (customTbl.get(table.getGuid()) != null) {
				String server_guid = (String) customTbl.get(table.getGuid());
				ct.setServerGuid(server_guid);
			}else{
				ct.setServerGuid(null);
			}
			final_table_list.add(ct);
		}

		getResponse = new GetResponse<Table>(ResponseCodes.TABLE_RECORD_FETCH_SUCCESFULLY, final_table_list);
		return getResponse;
	}*/

	@Override
	public boolean getTablesAvailability(List<String> tableGuids, Date start, Date end) {
		return true;
		// TODO Auto-generated method stub

	}
	
	
	@Override
	public BaseResponse patchTable(Table table, String token)
	{
	    BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Table existing = tableValidator.validateGuid(table.getGuid(), listOfError);
		if(listOfError.isEmpty())
		{
			Table tableToUpdate = existing;
			tableToUpdate.setStatus(table.getStatus());
			UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
			tableToUpdate.setInfoOnUpdate(userInfo);
			Logger.debug("table name is "+tableToUpdate.getName());
			
			listOfError.addAll(tableValidator.validateTableOnUpdate(tableToUpdate));
			
			 if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)))
				if (!(tableToUpdate.getRestId().equals(userInfo.getRestGuid())))
					listOfError.add(tableValidator.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_CREATE_OR_UPDATE_TABLE_OF_OTHER_REST)); 
			 
			if(table.getStatus().equals(Constants.DELETED_STATUS))
			{
			  List<Reservation> reservations = resvRepo.get_reservations_for_table_new(table.getGuid());
			  if(reservations.size() > 0 )
			  {
			      String errorMsg = UtilityMethods.getErrorMsg(ErrorCodes.RESERVATION_EXIST_FOR_TABLE) + " Reservation guids are : ";
			      for(Reservation resv : reservations)
			      {
				  errorMsg += (resv.getGuid() + " , ");
			      }
			       
			      errorMsg = errorMsg.substring(0, errorMsg.length() - 1);
			      
			      listOfError.add(new ValidationError(Constants.GUID, errorMsg, ErrorCodes.RESERVATION_EXIST_FOR_TABLE));
			  }
				
				tableValidator.deleteTable(tableToUpdate, listOfError);
			}
			
			Logger.debug("updating table");
			if(listOfError.isEmpty())
			{
			    Logger.debug("updating table " + tableToUpdate.getRestId());
			    Table updated=tableDao.update(tableToUpdate);
			    Logger.debug("table updated  ...............status set to "+updated.getStatus() +" table status is "+table.getStatus());
			    //if status is DELETED then delete all relationships of table with section and restaurant
			     if(table.getStatus().equals(Constants.DELETED_STATUS))
			    {
				 Logger.debug("Deleting table relationship");
				int deleted=tableDao.deleteTable(updated);
				Logger.debug("table relationships deleted=========================================="+deleted);
				
			    }
			     
			     response = new UpdateResponse<>(ResponseCodes.TABLE_UPDATED_SUCCESFULLY, updated.getGuid());
			     return response;
					
			}
		}
		return new ErrorResponse(ResponseCodes.TABLE_UPDATED_FAILURE, listOfError);
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public BaseResponse getBlockedTables(Map<String, Object> params, String token) 
	{
		BaseResponse getResponse;
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)))
			params.put(Constants.REST_GUID, userInfo.getRestGuid());
		params.put(Constants.START_TIME, (new Date().getTime() - 330*60*1000L ));
		List<String> blockedTables = tableDao.getBlockedTables(params);
		

		getResponse = new GetResponse<String>(ResponseCodes.TABLE_RECORD_FETCH_SUCCESFULLY, blockedTables);
		return getResponse;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public BaseResponse getCurrentTableStatus(Map<String, Object> params, String token) {
		BaseResponse getResponse;
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)))
			params.put(Constants.REST_ID, userInfo.getRestGuid());
		if (!params.containsKey(Constants.STATUS) && !params.containsKey(Constants.GUID)) {
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
     	}
		
		
		//Map<String, Object> qryParamMap = tableValidator.validateFinderParams(params);
		
		List<Table> final_table_list = new ArrayList<Table>();
		
		List<Object> allTablesList = waitlistDao.getAllTables(params.get(Constants.REST_ID).toString());
		Map<String, Table> tableWithGuid = (Map<String, Table>) allTablesList.get(1);
		
		
		Map<String,Object> resvParam = new HashMap<>();	
		resvParam.put(Constants.START_TIME, new Date().getTime());
		resvParam.put("restaurantId", params.get(Constants.REST_ID).toString());
		
		List<Reservation> allReservationList = shuffleDao.getTablesHavingReservation(resvParam);
		resvParam = null;
		
		Map<String, List<Reservation>> resvListMap = new HashMap<String, List<Reservation>>();
		shuffleService.addAllResvToBlockMap(allReservationList, resvListMap);
		
		Map<String,Object> queueParam = new HashMap<>();
		queueParam.put(Constants.REST_ID, params.get(Constants.REST_ID).toString());
		queueParam.put("currentTimeFilter", new Date().getTime());
		
		List<Reservation> queuedResv = queueDao.getQueuedReservation(queueParam);
		shuffleService.addAllResvToBlockMap(queuedResv, resvListMap);
		queueParam = null;
		
		for(Map.Entry<String, List<Reservation>> entry : resvListMap.entrySet()){
			Boolean isSeated = false;
			Reservation reservation = null;
			for(Reservation r : entry.getValue()){
				if(r.getReservationStatus().equals(Constants.SEATED)){
					isSeated = true;
					reservation = r;
					break;
				}else{
					reservation = r;
				}
			}
			if(isSeated){
				Table t = tableWithGuid.get(entry.getKey()); 
				t.setTableStatus(Constants.SEATED);
				t.setReservationGuid(reservation.getGuid());
				t.setReservation_EndTime(reservation.getEstEndTime());
				t.setReservation_StartTime(reservation.getEstStartTime());
				t.setSeated_time(reservation.getActStartTime());
				t.setTat(reservation.getTat());
			}else{
				Table t = tableWithGuid.get(entry.getKey()); 
				t.setTableStatus(Constants.ALLOCATED);
				t.setReservationGuid(reservation.getGuid());
				t.setReservation_EndTime(reservation.getEstEndTime());
				t.setReservation_StartTime(reservation.getEstStartTime());
				t.setTat(reservation.getTat());
			}
		}
		
		Map<String,Object> blockResvParam = new HashMap<>();
		blockResvParam.put(Constants.REST_GUID, params.get(Constants.REST_ID).toString());
		blockResvParam.put("currentTime", new Date().getTime());
		
		Map<String, List<Reservation>> blockListMap = new HashMap<String, List<Reservation>>();
		blockListMap = shuffleDao.getBlockedTables(blockResvParam, blockListMap);
		blockResvParam = null;
		
		for(Map.Entry<String, List<Reservation>> entry: blockListMap.entrySet()){
			String tableStatus = tableWithGuid.get(entry.getKey()).getTableStatus();
			List<Reservation> resvList = entry.getValue();
			String reasonForBlock = "";
			if(resvList.size() > 0)
			{
				reasonForBlock = (resvList.get(0).getReasonToCancel() == null) ? "" : resvList.get(0).getReasonToCancel();
			}
			if(tableStatus.equals(Constants.AVAILABLE)){
				tableWithGuid.get(entry.getKey()).setTableStatus(Constants.BLOCKED);
				tableWithGuid.get(entry.getKey()).setReasonForVip(reasonForBlock);
			}else{
				tableStatus = tableStatus + "," + Constants.BLOCKED;
				tableWithGuid.get(entry.getKey()).setTableStatus(tableStatus);
				tableWithGuid.get(entry.getKey()).setReasonForVip(reasonForBlock);
			}
		}
		
		for(Map.Entry<String, Table> entry : tableWithGuid.entrySet())
		{
			final_table_list.add(entry.getValue());
		}

		getResponse = new GetResponse<Table>(ResponseCodes.TABLE_RECORD_FETCH_SUCCESFULLY, final_table_list);
		return getResponse;
	}

}
