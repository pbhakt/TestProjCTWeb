package com.clicktable.service.impl;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.config.Neo4jConfig;
import com.clicktable.dao.intf.HistoricalTatDao;
import com.clicktable.model.HistoricalTat;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.HistoricalTatService;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class HistoricalTatServiceImpl implements HistoricalTatService {

	@Autowired
	HistoricalTatDao historicalTatDao;
	
	@Autowired
	Neo4jConfig neo4jConf;
	
	

	//@Autowired
	//HistoricalTatValidator historicalTatValidator;

	@Autowired
	AuthorizationService authorizationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
        public BaseResponse addHistoricalTat(HistoricalTat historicalTat, String token)
	{
	    
	       //Transaction tx = neo4jConf.getGraphDatabaseService().beginTx();
	        Logger.debug("in service imlpl");
	        BaseResponse response=null;
		/* String restaurantGuid = "";
	        restaurantGuid = historicalTat.getRestaurantGuid();
	        Logger.debug("rest guid is "+restaurantGuid);

		historicalTat.setStatus(Constants.ACTIVE_STATUS);
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		historicalTat.setInfoOnCreate(userInfo);
		
		List<ValidationError> listOfErrorForHistoricalTat = new ArrayList<>();
		listOfErrorForHistoricalTat = historicalTatValidator.validateHistoricalTatOnAdd(historicalTat);
		
		try
		{
		
		    
		Map<String,Object> params = new HashMap<String, Object>();
		params.put(Constants.DEVICE_ID, historicalTat.getHistoricalTatId());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		Logger.debug("finding historicalTat");
		List<HistoricalTat> historicalTatList = historicalTatDao.findByFields(HistoricalTat.class, params);
		Logger.debug("historicalTat list is "+historicalTatList);
		
		if (listOfErrorForHistoricalTat.isEmpty())
		{
		    if(historicalTatList.size()>0)
			{
			    ValidationError error = new ValidationError(Constants.DEVICE_ID,UtilityMethods.getErrorMsg(ErrorCodes.DEVICE_ID_ALREADY_EXISTS),ErrorCodes.DEVICE_ID_ALREADY_EXISTS);
			    listOfErrorForHistoricalTat.add(error);
			    response = new ErrorResponse(ResponseCodes.DEVICE_ADDED_FAILURE,listOfErrorForHistoricalTat);
				
			}
		    else
		    {
			
		      historicalTatDao.create(historicalTat);
		     Logger.debug("HistoricalTat created.guid is "+historicalTat.getGuid());
		    
		    Long relId = historicalTatDao.addRestaurantHistoricalTat(historicalTat, restaurantGuid);
		    Logger.debug("relationship created. Id is --------"+relId+" guid is "+historicalTat.getGuid());

		   response = new PostResponse<HistoricalTat>(ResponseCodes.DEVICE_ADDED_SUCCESFULLY,historicalTat.getGuid());
		    }
		
		} 
		else 
		{
		    response = new ErrorResponse(ResponseCodes.DEVICE_ADDED_FAILURE,listOfErrorForHistoricalTat);
		}
		
		Logger.debug("response is "+response);
		}
		catch(DeadlockDetectedException dde)
		{
		    Logger.debug("deadlock occured"+dde.getMessage());
		    tx.close();
		    addHistoricalTat(historicalTat, token);
		    
		}
		finally
		{
		    Logger.debug("finally of add");
		    tx.success();
		}*/

		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly =true)
	public BaseResponse getHistoricalTats(Map<String, Object> params)
	{
		BaseResponse getResponse = null; 
		/*Transaction tx = neo4jConf.getGraphDatabaseService().beginTx();
		try
		{
		Map<String, Object> qryParamMap = historicalTatValidator.validateFinderParams(params, HistoricalTat.class);
		List<HistoricalTat> historicalTatList = historicalTatDao.findByFields(HistoricalTat.class, qryParamMap);
		getResponse= new GetResponse<HistoricalTat>(ResponseCodes.DEVICE_RECORD_FETCH_SUCCESFULLY, historicalTatList);
		}
		catch(DeadlockDetectedException dde)
		{
		  Logger.debug("deadlock in get "+dde.getMessage());
		  tx.close();
		  getHistoricalTats(params);
		}
		finally
		{
		    Logger.debug("finally of get");
		    tx.success();
		}*/
		return getResponse;

	}

	

	@Override
	public BaseResponse updateHistoricalTat(HistoricalTat historicalTat, String token) 
	{
	    BaseResponse response = null;
	    
	  /*  Transaction tx = neo4jConf.getGraphDatabaseService().beginTx();
	    
	    try
	    {
		
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		historicalTat.setUpdatedBy(userInfo.getGuid());
		
		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		if (historicalTat.getGuid() == null)
			listOfError.add(UtilityMethods.createError(Constants.GUID, ErrorCodes.DEVICE_GUID_REQUIRED));
		else 
		{
		    ///if role is admin then check for restaurant(admin can change details of his own restaurant)
		    if((userInfo.getRoleId() == Constants.ADMIN_ROLE_ID) && (!rest.getGuid().equals(userInfo.getRestGuid())))
			{
			    listOfError.add(UtilityMethods.createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
			    response = new ErrorResponse(ResponseCodes.RESTAURANT_UPDATION_FAILURE,listOfError);
			    return response;
			}
		    
		       Logger.debug("finding historicalTat");
		       
			HistoricalTat oldHistoricalTat = historicalTatDao.findHistoricalTatByGuid( historicalTat.getGuid());
			Logger.debug("old historicalTat is "+oldHistoricalTat);
			if (oldHistoricalTat == null)
				listOfError.add(UtilityMethods.createError(Constants.GUID, ErrorCodes.INVALID_DEVICE_GUID));
			else 
			{
				historicalTat.copyExistingValues(oldHistoricalTat);
				listOfError.addAll(historicalTatValidator.validateHistoricalTatOnUpdate(historicalTat));
			}
		}
		

		if (listOfError.isEmpty()) 
		{
			HistoricalTat newHistoricalTat = historicalTatDao.update(historicalTat);
			response = new UpdateResponse<HistoricalTat>(ResponseCodes.DEVICE_UPDATED_SUCCESFULLY, newHistoricalTat.getGuid());
		} else {
			response = new ErrorResponse(ResponseCodes.DEVICE_UPDATION_FAILURE,listOfError);
		}
	    }
	    catch(DeadlockDetectedException dde)
	    {
		Logger.debug("deadlock in update "+dde.getMessage());
		tx.close();
		updateHistoricalTat(historicalTat, token);
	    }
	    finally
	    {
		Logger.debug("finally of update");
		tx.success();
	    }*/
		return response;
	}


	
	
	/**
	 * Method to delete historicalTat
	 */
	@Override
	@Transactional
	public BaseResponse deleteHistoricalTat(String historicalTatGuid, String token)
	{
	    BaseResponse response = null;
	 /* //  UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
	    List<ValidationError> listOfError = new ArrayList<ValidationError>();
             if ((historicalTatGuid == null) || historicalTatGuid.equals(""))
			listOfError.add(UtilityMethods.createError(Constants.DEVICE_GUID, ErrorCodes.DEVICE_GUID_REQUIRED));
             
             HistoricalTat historicalTat = historicalTatDao.findHistoricalTatByGuid(historicalTatGuid);
		if (historicalTat == null)
			listOfError.add(UtilityMethods.createError(Constants.DEVICE_GUID, ErrorCodes.INVALID_DEVICE_GUID));
		 //if user is not ct admin then check if server being created and logged in staff member both belongs to same restaurant
		else if(!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))
		    listOfError.addAll( validateStaffObject.validateStaffForRestaurant(historicalTat.getRestaurantGuid(), userInfo.getRestGuid()));
		
		if (listOfError.isEmpty()) 
		{
		  boolean deleted = historicalTatDao.deleteHistoricalTat(historicalTatGuid);
        	  Logger.debug("query result"+deleted);
        	  response = new UpdateResponse<Restaurant>(ResponseCodes.DEVICE_DELETED_SUCCESFULLY, historicalTatGuid);
		} 
             else 
             	{
        	  response = new ErrorResponse(ResponseCodes.DEVICE_DELETED_FAILURE,listOfError);
		}*/
		
		return response;
	    
	}
	
	
	
	

}
