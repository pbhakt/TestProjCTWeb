package com.clicktable.service.impl;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.TableAssignmentDao;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Server;
import com.clicktable.model.TableAssignment;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.TableAssignmentService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.TableAssignmentValidator;
import com.clicktable.validate.ValidationError;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class TableAssignmentServiceImpl implements TableAssignmentService {

	
	

	@Autowired
	TableAssignmentValidator assignmentValidator;
	
	@Autowired
	RestaurantDao restDao;
	
	@Autowired
	TableAssignmentDao assignmentDao;

	
	@Autowired
	AuthorizationService authorizationService;

	
	
	
	
	/**
	 * Method to assign table to a server
	 * 
	 */
	@Override
	@Transactional
	public BaseResponse assignTable(TableAssignment tableAssign, String token)
	{
	    /**
	     * TO DO:   validations
	     */
	    
	    
	    BaseResponse response;
	    List<ValidationError> listOfError = new ArrayList<ValidationError>();
             
             UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
             ///if role is admin then check for restaurant(admin can change details of his own restaurant)
             if((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && (!tableAssign.getRestaurantGuid().equals(userInfo.getRestGuid())))
		{
        	 listOfError.add(assignmentValidator.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
        	 response = new ErrorResponse(ResponseCodes.TABLE_ASSIGNMENT_FAILURE,listOfError);
        	 return response;
		}
             
            /* Restaurant restaurant = restDao.findRestaurantByGuid(tableAssign.getRestaurantGuid());
		if (restaurant == null)
			listOfError.add(UtilityMethods.createError(Constants.REST_GUID, ErrorCodes.INVALID_REST_ID));*/
             
             
             	listOfError.addAll(assignmentValidator.validateOnAdd(tableAssign));
             	
             	Logger.debug("start time is=========="+tableAssign.getStartTime()+" end time is========"+tableAssign.getEndTime()+" date is ========="+tableAssign.getDate());
		
		
		
             if (listOfError.isEmpty()) 
		{
        	 
        	 
        	 String[] tableGuidArr = tableAssign.getTableGuid().split(",");
 		
 		Iterator<Map<String, Object>> itr = assignmentDao.getTableServerAndRest(tableAssign, tableGuidArr);
 		int i = 0;
 		
 		String tableGuid = "", restaurantGuid = "", serverGuid = "";
 		
 		Logger.debug("itr is "+itr);
 		
 		while(itr.hasNext())
 		{
 		    Map<String, Object> map = itr.next();
 		    tableGuid = tableGuid + map.get(Constants.TABLE_GUID).toString() + ",";
 		    restaurantGuid = map.get(Constants.REST_GUID).toString();
 		    serverGuid = map.get(Constants.SERVER_GUID).toString();
 		    i++;
 		}
 		
 		Logger.debug("rest is "+restaurantGuid+" tables are "+tableGuid+" server is "+serverGuid+" i is "+i);
 		
 		if(i < tableGuidArr.length)
 		{
 		    Logger.debug( "" + i + " i < tableGuidArr.length " + tableGuidArr.length);
 		    ValidationError error = new ValidationError(Constants.TABLE_ASSIGNMENT_DETAILS,UtilityMethods.getErrorMsg(ErrorCodes.INVALID_TABLE_ASSIGNMENT_DETAILS), ErrorCodes.INVALID_TABLE_ASSIGNMENT_DETAILS);
 		    listOfError.add(error);
 		    response = new ErrorResponse(ResponseCodes.TABLE_ASSIGNMENT_FAILURE,listOfError);
 		    return response;  
 		}
 		else
 		{
 		   /* if(!tableGuid.equals(tableAssign.getTableGuid()))
 		    {
 			Logger.debug(tableGuid + " !tableGuid.equals(tableAssign.getTableGuid()) " + tableAssign.getTableGuid());
 			ValidationError error = new ValidationError("table assignment details", "There is some error in table guid");
 			listOfError.add(error);
 		    }*/
 		    
 		    
 		    listOfError = new ArrayList<>();
 		    
 		    if(!serverGuid.equals(tableAssign.getServerGuid()))
 		    {
 			Logger.debug("!serverGuid.equals(tableAssign.getServerGuid())");
 			ValidationError error = new ValidationError(Constants.SERVER_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_SERVER_GUID),ErrorCodes.INVALID_SERVER_GUID);
 			listOfError.add(error);
 		    }
 		    
 		    if(!restaurantGuid.equals(tableAssign.getRestaurantGuid()))
 		    {
 			Logger.debug("!restaurantGuid.equals(tableAssign.getRestaurantGuid())");
 			ValidationError error = new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_RESTAURANT_GUID),ErrorCodes.INVALID_RESTAURANT_GUID);
 			listOfError.add(error);
 		    }
 		    
 		    if(listOfError.size() > 0)
 		    {
 			response = new ErrorResponse(ResponseCodes.TABLE_ASSIGNMENT_FAILURE,listOfError);
 	 		return response; 
 		    }
 		}
 		
        	 
        	 
        	 
        	  
        	  Logger.debug("no error");
		  boolean created = assignmentDao.assignTablesToServer(tableAssign, tableGuidArr);
        	  Logger.debug("query result"+created);
        	  if(created)
        	  {
        	  response = new UpdateResponse<Restaurant>(ResponseCodes.TABLES_ASSIGNED_SUCCESFULLY, tableAssign.getServerGuid());
        	  }
        	  else
        	  {
        	   response = new ErrorResponse(ResponseCodes.TABLE_ASSIGNMENT_FAILURE,listOfError); 
        	  }
		} 
             else 
             	{
        	  response = new ErrorResponse(ResponseCodes.TABLE_ASSIGNMENT_FAILURE,listOfError);
		}
		
		return response;
	    
	}
	
	
	
	
	/**
	 * Method to unassign table to a server
	 * 
	 */
	@Override
	@Transactional
	public BaseResponse unassignTable(TableAssignment tableAssign, String token)
	{
	    /**
	     * TO DO:   validations
	     */
	    
	    
	    BaseResponse response;
	    List<ValidationError> listOfError = new ArrayList<ValidationError>();
             
             UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
             ///if role is admin then check for restaurant(admin can change details of his own restaurant)
             if((userInfo.getRoleId().equals(Constants.ADMIN_ROLE_ID)) && (!tableAssign.getRestaurantGuid().equals(userInfo.getRestGuid())))
		{
        	 listOfError.add(assignmentValidator.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
        	 response = new ErrorResponse(ResponseCodes.TABLE_UNASSIGNMENT_FAILURE,listOfError);
        	 return response;
		}
             
            /* Restaurant restaurant = restDao.find( restGuid);
		if (restaurant == null)
			listOfError.add(UtilityMethods.createError(Constants.REST_GUID, ErrorCodes.INVALID_REST_ID));*/
             
             
             	//listOfError.addAll(assignmentValidator.validateOnAdd(tableAssign));
             	
             	Logger.debug("start time is=========="+tableAssign.getStartTime()+" end time is========"+tableAssign.getEndTime()+" date is ========="+tableAssign.getDate());
		
		String[] tableGuidArr = tableAssign.getTableGuid().split(",");
		
             if (listOfError.isEmpty()) 
		{
		  boolean created = assignmentDao.unassignTablesToServer(tableAssign, tableGuidArr);
        	  Logger.debug("query result"+created);
        	  if(created)
        	  {
        	  response = new UpdateResponse<Restaurant>(ResponseCodes.TABLES_UNASSIGNED_SUCCESFULLY, tableAssign.getServerGuid());
        	  }
        	  else
        	  {
        	   response = new ErrorResponse(ResponseCodes.TABLE_UNASSIGNMENT_FAILURE,listOfError); 
        	  }
		} 
             else 
             	{
        	  response = new ErrorResponse(ResponseCodes.TABLE_UNASSIGNMENT_FAILURE,listOfError);
		}
		
		return response;
	    
	}

	/**
	 * Method to get table assignment for a particular server
	 */
	@Override
	public BaseResponse getTableAssignment(Map<String,Object> params)
	{
	    
	   /**
	    * TO DO:  Validations
	    */
	    
	    
	    BaseResponse response = null; 
	    	 List<ValidationError> listOfError = new ArrayList<ValidationError>();
	             /*if ((params.get(Constants.REST_GUID) == null) || params.get(Constants.REST_GUID).equals(""))
				listOfError.add(UtilityMethods.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED))*/
	    	if(listOfError.isEmpty())
	    	{
		   Iterator<Map<String, Object>> itr = assignmentDao.getTableAssignment(params);
		   List<Server> serverList = new ArrayList<Server>();
	           List<TableAssignment> assignmentList = new ArrayList<TableAssignment>();
	           TableAssignment assignTable;
	           SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
       	    	   SimpleDateFormat timestampFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
       	    	   String serverGuid="", serverName = "",colorCode = "",createdBy="",updatedBy="",restaurantGuid="";
       	           Date createdDate = null;
       	           Date updatedDate = null;
       	    	   Server server = new Server();
       	    	       if(itr.hasNext())
       	    	       {
	        	while(itr.hasNext())
	        	{
	        	    assignTable = new TableAssignment();
	        	    Set<Entry<String, Object>> entrySet = itr.next().entrySet();
	        	    Logger.debug("entry set is "+entrySet);
	        	    
	        	    for(Map.Entry<String, Object> entry  : entrySet)
	        	    {
	        		Logger.debug("entry is "+entry);
	        		Logger.debug("entry.getKey().contains(Constants.UPDATED_DATE)=========="+entry.getKey().contains(Constants.UPDATED_DATE));
	        		if(entry.getKey().equals(Constants.TABLE_GUID))
	        		{
	        		    assignTable.setTableGuid(entry.getValue().toString());
	        		}
	        		
	        		if(entry.getKey().equals(Constants.MAX_COVERS))
	        		{
	        		    assignTable.setMaxCovers(Integer.valueOf(entry.getValue().toString()));
	        		}
	        		
	        		if(entry.getKey().equals(Constants.SERVER_GUID))
	        		{
	        		    serverGuid = entry.getValue().toString();
	        		    assignTable.setServerGuid(serverGuid);
	        		}
			
	        		if(entry.getKey().equals(Constants.START_TIME))
	        		{
	        		    String startTimeStr = timestampFormat.format(new Date(Long.parseLong( entry.getValue().toString())));
	        		    Logger.debug("start time is "+startTimeStr);
	        		    assignTable.setStartTime(startTimeStr);
	        		}
	        		
	        		if(entry.getKey().equals(Constants.END_TIME))
	        		{
	        		    String endTimeStr = timestampFormat.format(new Date(Long.valueOf( entry.getValue().toString())));
	        		    Logger.debug("end time is "+endTimeStr);
	        		    assignTable.setEndTime(endTimeStr);
	        		}
	        		
	        		if(entry.getKey().equals(Constants.DATE))
	        		{
	        		    Logger.debug("getting date"+entry.getValue().toString());
	        		    String dateStr = dateFormat.format(new Date(Long.valueOf( entry.getValue().toString())));
	        		    Logger.debug("date is "+dateStr);
	        		    assignTable.setDate(dateStr);
	        		}
	        		
	        		if(entry.getKey().equals(Constants.NAME))
	        		{
	        		    Logger.debug("name is "+entry.getValue().toString());
	        		    serverName = entry.getValue().toString();
	        		}
	        		
	        		if(entry.getKey().equals(Constants.COLOR_CODE))
	        		{
	        		    Logger.debug("color code  is "+entry.getValue().toString());
	        		    colorCode = entry.getValue().toString();
	        		}
	        		
	        		if(entry.getKey().equals(Constants.CREATED_DATE))
	        		{
	        		    Logger.debug("created date  is "+entry.getValue());
	        		    Timestamp date = new Timestamp(Long.valueOf(entry.getValue().toString()));
	        		    Logger.debug("created date in date format is "+date);
	        		    createdDate = (Date)date;
	        		}
	        		
	        		if(entry.getKey().equals(Constants.UPDATED_DATE))
	        		{
	        		    Logger.debug("getting updated date==============");
	        		    Logger.debug("getting updated date==============");
	        		    Logger.debug("updated date  is "+entry.getValue());
	        		    Timestamp date = new Timestamp(Long.valueOf( entry.getValue().toString()));
	        		    Logger.debug("updated date in date format is "+date);
	        		    updatedDate = (Date)date;
	        		}
	        		
	        		if(entry.getKey().equals(Constants.CREATED_BY))
	        		{
	        		    Logger.debug("created by  is "+entry.getValue().toString());
	        		    createdBy = entry.getValue().toString();
	        		}
	        		
	        		if(entry.getKey().equals(Constants.UPDATED_BY))
	        		{
	        		    Logger.debug("updated by  is "+entry.getValue().toString());
	        		    updatedBy = entry.getValue().toString();
	        		}
	        		
	        		
	        		if(entry.getKey().equals(Constants.REST_GUID))
	        		{
	        		    Logger.debug("rest guid  is "+entry.getValue().toString());
	        		    restaurantGuid = entry.getValue().toString();
	        		    assignTable.setRestaurantGuid(restaurantGuid);
	        		}
			    }
	        	    
	        	   assignmentList.add(assignTable);
		    
	        	}
	        	server.setGuid(serverGuid);
	        	server.setName(serverName);
	        	server.setColorCode(colorCode);
	        	server.setCreatedBy(createdBy);
	        	server.setUpdatedBy(updatedBy);
	        	server.setCreatedDate(createdDate);
	        	server.setUpdatedDate(updatedDate);
	        	server.setRestaurantGuid(restaurantGuid);
	        	server.setAssignedTables(assignmentList);
	        	
	        	serverList.add(server);
       	    	      }
	        	if(serverList.size()==0){
	        		response= new GetResponse<Server>(ResponseCodes.TABLE_ASSIGNMENT_FETCH_FAILURE_EMPTY_LIST,serverList);
	        	}else{
	        	response= new GetResponse<Server>(ResponseCodes.TABLE_ASSIGNMENT_FETCH_SUCCESFULLY,serverList);
	        	}
	    	}
	    	else
	    	{
	    	    response = new ErrorResponse(ResponseCodes.TABLE_ASSIGNMENT_FETCH_FAILURE, listOfError);
	    	}
		return response;
	    
	}

}
