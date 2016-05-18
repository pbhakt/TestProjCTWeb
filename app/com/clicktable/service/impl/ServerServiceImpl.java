package com.clicktable.service.impl;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.ServerDao;
import com.clicktable.model.CustomServer;
import com.clicktable.model.Reservation;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Server;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ServerService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.ServerValidator;
import com.clicktable.validate.StaffValidator;
import com.clicktable.validate.ValidationError;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class ServerServiceImpl implements ServerService {

	@Autowired
	ServerDao serverDao;
	
	

	@Autowired
	ServerValidator validateServerObject;
	
	@Autowired
	StaffValidator validateStaffObject;

	@Autowired
	AuthorizationService authorizationService;
	
	@Autowired
	ReservationDao resvDao;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
        public BaseResponse addServer(Server server, String token)
	{

	    	BaseResponse response=null;
		
		server.setStatus(Constants.ACTIVE_STATUS);
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		
		Server newServer;
		List<ValidationError> listOfErrorForServer = validateServerObject.validateServerOnAdd(server);
		
		
		if (listOfErrorForServer.isEmpty())
		{
		    	//if user is not ct admin then check if server being created and logged in staff member both belongs to same restaurant
			if(!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))
			   {
				listOfErrorForServer = validateStaffObject.validateStaffForRestaurant(server.getRestaurantGuid(), userInfo.getRestGuid());
				if(!listOfErrorForServer.isEmpty())
				   {
				    response = new ErrorResponse(ResponseCodes.SERVER_ADDED_FAILURE,listOfErrorForServer);
				    return response;
				   }
			   }
			
			//check whether any active server exists for same restaurant with same color code
			Server existingServerForColor = serverDao.checkForColorCode(server);
			 if(existingServerForColor != null)
			 {
			     ValidationError error = new ValidationError(Constants.COLOR_CODE,UtilityMethods.getErrorMsg(ErrorCodes.COLOR_CODE_ALREADY_EXISTS),ErrorCodes.COLOR_CODE_ALREADY_EXISTS);
			     listOfErrorForServer.add(error);
			     response = new ErrorResponse(ResponseCodes.SERVER_ADDED_FAILURE,listOfErrorForServer); 
			     return response;
			 }
			
		        Map<String,Object> params = new HashMap<String, Object>();
			params.put(Constants.SERVER_ID, server.getServerId());
			params.put(Constants.REST_GUID, server.getRestaurantGuid());
			//params.put(Constants.SHIFT_STATUS, Constants.ACTIVE_STATUS);
			Logger.debug("params are >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>." + params);
			List<Server> serverList = serverDao.findByFields(Server.class, params);
			Logger.debug("Server list is .............................."+serverList);
		    //if any server exist for this rest with this server id then check for name 	
		    if(serverList.size() > 0)
		    {
			Server existingServer = serverList.get(0);
			Logger.debug("server name from server is "+server.getName()+" server name from existing server is "+existingServer.getName());
			Logger.debug("!existingServer.getName().equals(server.getName())------------------------------------------ " + (!existingServer.getName().equals(server.getName())));
			//if name for new server and existing server is not same then show error message that server with same server id already exists
			if(!existingServer.getName().equals(server.getName()))
			{
			ValidationError error = new ValidationError(Constants.SERVER_ID,UtilityMethods.getErrorMsg(ErrorCodes.SERVER_ID_ALREADY_EXISTS),ErrorCodes.SERVER_ID_ALREADY_EXISTS);
			listOfErrorForServer.add(error);
			response = new ErrorResponse(ResponseCodes.SERVER_ADDED_FAILURE,listOfErrorForServer);
			return response;
			}
			//if name for new and existing server is same then make existing server active
			else
			{
			    Logger.debug("!existingServer.getColorCode().equals(server.getColorCode()):::::::::::::::::::::::::" + (!existingServer.getColorCode().equals(server.getColorCode())));
			   
			    //update color code for server
			    existingServer.setInfoOnUpdate(userInfo);
			    existingServer.setColorCode(server.getColorCode());
			    existingServer.setStatus(Constants.ACTIVE_STATUS);
			    newServer = serverDao.update(existingServer);
			}
				
		    }
		    else
		    {
		    newServer = serverDao.create(server);
		    serverDao.addRestaurantServer(newServer);
		    }
		    
		    response = new PostResponse<Restaurant>(ResponseCodes.SERVER_ADDED_SUCCESFULLY,newServer.getGuid());
		    // System.out.println("Server created with id "+server.getId());
		    
		
		} 
		else 
		{
		    response = new ErrorResponse(ResponseCodes.SERVER_ADDED_FAILURE,listOfErrorForServer);
		}

		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly =true)
	public BaseResponse getServers(Map<String, Object> params)
	{
		BaseResponse getResponse; 
		Map<String, Object> qryParamMap = validateServerObject.validateFinderParams(params, Server.class);
		if(!qryParamMap.containsKey(Constants.STATUS))
		{
		    qryParamMap.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		}
		List<Server> serverList = serverDao.findByFields(Server.class, qryParamMap);
		getResponse= new GetResponse<Server>(ResponseCodes.SERVER_RECORD_FETCH_SUCCESFULLY, serverList);
		return getResponse;

	}
	
	
	
	

/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse updateServer(Server server, String token)
	{
		BaseResponse response = null;
		
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		server.setUpdatedBy(userInfo.getGuid());
		
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Server existingServer = null;

		if (server.getGuid() == null)
			listOfError.add(validateServerObject.createError(Constants.GUID, ErrorCodes.SERVER_ID_REQUIRED));
		else 
		{
		      //if user is not ct admin then check if server being created and logged in staff member both belongs to same restaurant
			if(!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))
			   {
				listOfError = validateStaffObject.validateStaffForRestaurant(server.getRestaurantGuid(), userInfo.getRestGuid());
				if(!listOfError.isEmpty())
				   {
				    response = new ErrorResponse(ResponseCodes.SERVER_UPDATION_FAILURE,listOfError);
				    return response;
				   }
			   }
		    
			existingServer = serverDao.find(server.getGuid());
			if (existingServer == null)
				listOfError.add(validateServerObject.createError(Constants.GUID, ErrorCodes.INVALID_SERVER_ID));
			else 
			{
				server.copyExistingValues(existingServer);
				server.setStatus(Constants.ACTIVE_STATUS);
				listOfError.addAll(validateServerObject.validateServerOnUpdate(server));
			}
		}
		

		if (listOfError.isEmpty()) 
		{
		    //if previous color code and new color code is not same then check whether the same color already exists for this restaurant
		    if(!server.getColorCode().equals(existingServer.getColorCode()))
		    {
			//check whether any active server exists for same restaurant with same color code
			Server existingServerForColor = serverDao.checkForColorCode(server);
			 if(existingServerForColor != null)
			 {
			     ValidationError error = new ValidationError(Constants.COLOR_CODE,UtilityMethods.getErrorMsg(ErrorCodes.COLOR_CODE_ALREADY_EXISTS),ErrorCodes.COLOR_CODE_ALREADY_EXISTS);
			     listOfError.add(error);
			     response = new ErrorResponse(ResponseCodes.SERVER_UPDATION_FAILURE,listOfError); 
			     return response;
			 }
		        
		        
		    }
		    
			Logger.debug("Going to update server");
		     serverDao.update(server);
		     response = new UpdateResponse<Server>(ResponseCodes.SERVER_UPDATED_SUCCESFULLY, server.getGuid());
		     Logger.debug("Response after updation is =========="+response);
		    
		} 
		else 
		{
			response = new ErrorResponse(ResponseCodes.SERVER_UPDATION_FAILURE,listOfError);
		}
		Logger.debug("Returning response"+response);
		return response;

	}
	
	
	
	
	/**
	 * Method to delete server
	 */
	@Override
	@Transactional
	public BaseResponse deleteServer(String serverGuid, String token)
	{
	    BaseResponse response;
	    UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
	    List<ValidationError> listOfError = new ArrayList<ValidationError>();
             if ((serverGuid == null) || serverGuid.equals(""))
			listOfError.add(validateServerObject.createError(Constants.SERVER_GUID, ErrorCodes.SERVER_ID_REQUIRED));
             
             Server server = serverDao.find(serverGuid);
		if (server == null)
			listOfError.add(validateServerObject.createError(Constants.SERVER_GUID, ErrorCodes.INVALID_SERVER_ID));
		 //if user is not ct admin then check if server being created and logged in staff member both belongs to same restaurant
		else if(!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))
		    listOfError.addAll( validateStaffObject.validateStaffForRestaurant(server.getRestaurantGuid(), userInfo.getRestGuid()));
		
		if (listOfError.isEmpty()) 
		{
		   boolean tablesUnassigned = serverDao.unassignAllTablesForServer(serverGuid);
		   Logger.debug("tables unassigned "+tablesUnassigned);
		  boolean created = serverDao.deleteServer(serverGuid);
        	  Logger.debug("query result"+created);
        	  response = new UpdateResponse<Restaurant>(ResponseCodes.SERVER_DELETED_SUCCESFULLY, serverGuid);
		} 
             else 
             	{
        	  response = new ErrorResponse(ResponseCodes.SERVER_DELETED_FAILURE,listOfError);
		}
		
		return response;
	    
	}

	@Override
	public BaseResponse getRestaurantServers(Map<String, Object> params, String token)
	{
	    	BaseResponse getResponse; 
		Map<String, Object> qryParamMap = validateServerObject.validateFinderParams(params, Server.class);
		
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
	             if ((qryParamMap != null) && (!qryParamMap.containsKey(Constants.REST_GUID)))
				listOfError.add(validateServerObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
	             
	             if (listOfError.isEmpty()) 
			{
	        	 String startDate = "";
	        	 String endDate = "";
	        	 Long start_dt = 0L;
	        	 Long end_dt = 0L;
	        	 
				   
	        
	        	 try 
	        	 {
	        	 SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
	        	 SimpleDateFormat timestampFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
	        	 String currentDate = dateFormat.format(new Date());
	        	 Logger.debug("current date is "+currentDate);
	        	  startDate = currentDate+" 00:00:00";
	        	  start_dt = timestampFormat.parse(startDate).getTime();
	        	  endDate = timestampFormat.format(new Date(start_dt + 24*60*60*1000));
	        	  Logger.debug("end date is "+endDate);
	        	  end_dt = new Date(start_dt + 24*60*60*1000).getTime();
		        	 
	        	 } 
	        	 catch (ParseException e) 
	        	 {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			 }
	        	 
	        	 qryParamMap.put(Constants.STATUS, Constants.ACTIVE_STATUS);
	        	 List<Server> serverList = serverDao.findByFields(Server.class, qryParamMap);
	        	 Logger.debug("Active servers list is "+serverList);
	        	 Map<String,Object> resParamMap ;
	        	 List<CustomServer> customServerList = new ArrayList<>();
	        	 CustomServer customServer;
	                 		 
		        	
	        	 for(Server server : serverList)
	        	 {
	        	     Logger.debug("server guid is "+server.getGuid());
	        	     resParamMap = new HashMap<>();
	        	     resParamMap.put(Constants.EST_START_AFTER, start_dt);
	        	     resParamMap.put(Constants.EST_END_BEFORE, end_dt);
	        	     resParamMap.put(Constants.RESERVATION_STATUS, Constants.FINISHED+","+Constants.SEATED);
	        	     List<Reservation> reservationList = resvDao.findByFields(Reservation.class, resParamMap);
	        	     Logger.debug("Reservation list is ------------------------------"+reservationList);
	        	     int totalSeatedTables=0 , totalFinishedTables=0 , totalSeatedCovers=0 , totalFinishedCovers =0;
	        	     customServer = new CustomServer(server);
	        	     
	        	     for(Reservation reservation : reservationList)
	        	     {
	        		 Logger.debug("reservation status is "+reservation.getReservationStatus()+" server guid is "+server.getGuid()+" reservation server guid is "+reservation.getServerGuids());
	        		 
	        		 if((reservation.getServerGuids() != null) && reservation.getServerGuids().contains(server.getGuid()))
	        		 {
	        		     if(reservation.getReservationStatus().equals(Constants.SEATED))
	        		     {
	        			 totalSeatedTables += reservation.getTableGuid().size();
	        			 totalSeatedCovers += reservation.getNumCovers();
	        		     }
	        		     
	        		     if(reservation.getReservationStatus().equals(Constants.FINISHED))
	        		     {
	        			 totalFinishedTables += reservation.getTableGuid().size();
	        			 totalFinishedCovers += reservation.getNumCovers();
	        		     }
	        		 }
	        		 
	        	     }
	        	     
	        	     customServer.setTotalFinishedCovers(totalFinishedCovers);
	        	     customServer.setTotalFinishedTables(totalFinishedTables);
	        	     customServer.setTotalSeatedCovers(totalSeatedCovers);
	        	     customServer.setTotalSeatedTables(totalSeatedTables);
	        	     
	        	     customServerList.add(customServer);
	        	     
	        	     
	        	 }
	 		getResponse= new GetResponse<CustomServer>(ResponseCodes.SERVER_RECORD_FETCH_SUCCESFULLY, customServerList);
			} 
	             else 
	             	{
	        	  getResponse = new ErrorResponse(ResponseCodes.SERVER_RECORD_FETCH_FAILURE,listOfError);
			}
		return getResponse;
	}
	
	
	
	

}

