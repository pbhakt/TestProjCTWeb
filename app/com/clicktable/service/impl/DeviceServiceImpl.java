package com.clicktable.service.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.config.Neo4jConfig;
import com.clicktable.dao.intf.DeviceDao;
import com.clicktable.model.Device;
import com.clicktable.model.Restaurant;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.DeviceService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.DeviceValidator;
import com.clicktable.validate.ValidationError;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	DeviceDao deviceDao;
	
	@Autowired
	Neo4jConfig neo4jConf;
	
	

	@Autowired
	DeviceValidator deviceValidator;

	@Autowired
	AuthorizationService authorizationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse addDevice(Device device, String token)
	{

		Logger.debug("in service imlpl");
		BaseResponse response=null;
		String restaurantGuid = "";
		restaurantGuid = device.getRestaurantGuid();
		Logger.debug("rest guid is "+restaurantGuid);
		device.setStatus(Constants.ACTIVE_STATUS);

		List<ValidationError> listOfErrorForDevice = deviceValidator.validateDeviceOnAdd(device);

		Map<String,Object> params = new HashMap<String, Object>();
		params.put(Constants.DEVICE_ID, device.getDeviceId());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		Logger.debug("finding device");
		List<Device> deviceList = deviceDao.findByFields(Device.class, params);
		Logger.debug("device list is "+deviceList);

		if (listOfErrorForDevice.isEmpty())
		{
			if(deviceList.size()>0)
			{
				ValidationError error = new ValidationError(Constants.DEVICE_ID,UtilityMethods.getErrorMsg(ErrorCodes.DEVICE_ID_ALREADY_EXISTS),ErrorCodes.DEVICE_ID_ALREADY_EXISTS);
				listOfErrorForDevice.add(error);
				response = new ErrorResponse(ResponseCodes.DEVICE_ADDED_FAILURE,listOfErrorForDevice);

			}
			else
			{

				deviceDao.create(device);
				Logger.debug("Device created.guid is "+device.getGuid());

				Long relId = deviceDao.addRestaurantDevice(device, restaurantGuid);
				Logger.debug("relationship created. Id is --------"+relId+" guid is "+device.getGuid());

				response = new PostResponse<Device>(ResponseCodes.DEVICE_ADDED_SUCCESFULLY,device.getGuid());
			}

		} 
		else 
		{
			response = new ErrorResponse(ResponseCodes.DEVICE_ADDED_FAILURE,listOfErrorForDevice);
		}

		Logger.debug("response is "+response);


		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly =true)
	public BaseResponse getDevices(Map<String, Object> params)
	{
		BaseResponse getResponse = null; 
		Map<String, Object> qryParamMap = deviceValidator.validateFinderParams(params, Device.class);
		List<Device> deviceList = deviceDao.findByFields(Device.class, qryParamMap);
		getResponse= new GetResponse<Device>(ResponseCodes.DEVICE_RECORD_FETCH_SUCCESFULLY, deviceList);
		return getResponse;

	}

	

	@Override
	public BaseResponse updateDevice(Device device, String token) 
	{
	    BaseResponse response = null;
		
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		device.setUpdatedBy(userInfo.getGuid());
		
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
	    
		    Logger.debug("finding device");
		       
			Device oldDevice = deviceValidator.validateGuid( device.getGuid(), listOfError);
			Logger.debug("old device is "+oldDevice);
			if(listOfError.isEmpty()){
				device.copyExistingValues(oldDevice);
				listOfError.addAll(deviceValidator.validateDeviceOnUpdate(device));
			}

		

		if (listOfError.isEmpty()) 
		{
			Device newDevice = deviceDao.update(device);
			response = new UpdateResponse<Device>(ResponseCodes.DEVICE_UPDATED_SUCCESFULLY, newDevice.getGuid());
		} else {
			response = new ErrorResponse(ResponseCodes.DEVICE_UPDATION_FAILURE,listOfError);
		}

		return response;
	}


	
	
	/**
	 * Method to delete device
	 */
	@Override
	@Transactional
	public BaseResponse deleteDevice(String deviceGuid, String token)
	{
	    BaseResponse response;
	  //  UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
	    List<ValidationError> listOfError = new ArrayList<ValidationError>();             
        Device device = deviceValidator.validateGuid(deviceGuid, listOfError);
		 //if user is not ct admin then check if server being created and logged in staff member both belongs to same restaurant
		/*else if(!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))
		    listOfError.addAll( validateStaffObject.validateStaffForRestaurant(device.getRestaurantGuid(), userInfo.getRestGuid()));*/
		
		if (listOfError.isEmpty()) 
		{
		  boolean deleted = deviceDao.deleteDevice(deviceGuid);
        	  Logger.debug("query result"+deleted);
        	  response = new UpdateResponse<Restaurant>(ResponseCodes.DEVICE_DELETED_SUCCESFULLY, deviceGuid);
		} 
             else 
             	{
        	  response = new ErrorResponse(ResponseCodes.DEVICE_DELETED_FAILURE,listOfError);
		}
		
		return response;
	    
	}
	
	
	
	

}
