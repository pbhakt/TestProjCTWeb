package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.dao.intf.AppDetailsDao;
import com.clicktable.dao.intf.StateDao;
import com.clicktable.model.ApplicationDetails;
import com.clicktable.model.Country;
import com.clicktable.model.State;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AppDetailsService;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.StateService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.AppDetailsValidator;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.StateValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class AppDetailsServiceImpl implements AppDetailsService {

	@Autowired
	AppDetailsDao appDetailsDao;

	@Autowired
	AuthorizationService authService;
	
	@Autowired
	AppDetailsValidator appDetailsValidator;
	
	@Override
	@Transactional(readOnly = true)
	public BaseResponse getAppDetails(Map<String, Object> params) {
		BaseResponse getResponse;
		if(!params.containsKey(Constants.STATUS))
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		
		List<ApplicationDetails> appDetailsList = appDetailsDao.findApplicationDetails(params);
		getResponse = new GetResponse<ApplicationDetails>(
				ResponseCodes.APPLICATION_DETAILS_RECORD_FETCH_SUCCESFULLY, appDetailsList);
		return getResponse;
	}

	@Override
	@Transactional
	public BaseResponse addAppDetails(ApplicationDetails appDetails, String token) {
		BaseResponse response;
		//state.setGuid(UtilityMethods.generateCtId());
		//state.setStatus(Constants.ACTIVE_STATUS);
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		listOfError = appDetailsValidator.validateAppDetailsOnAdd(appDetails, listOfError);
		
		if(!listOfError.isEmpty()){
			return new ErrorResponse(ResponseCodes.APPLICATION_DETAILS_ADDITION_FAILURE, listOfError);
		}
		
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Constants.APP_NAME, appDetails.getAppName());
		param.put(Constants.PLATFORM, appDetails.getPlatform());
		List<ApplicationDetails> appDetailsList = appDetailsDao.findApplicationDetails(param);
		
		List<String> oldVersionsList = new ArrayList<>();
		
		String savedAppName = null;
		if(appDetailsList.size() == 0)
		{
			appDetails.setOldVersions(oldVersionsList);
			savedAppName = appDetailsDao.create(appDetails).getAppName();
		}
		else
		{
			ApplicationDetails appDetailsFromDb = appDetailsList.get(0);
			oldVersionsList = appDetailsFromDb.getOldVersions();
			if(oldVersionsList == null || oldVersionsList.size() == 0)
			{
				oldVersionsList = new ArrayList<>();
			}
			
			oldVersionsList.add(appDetailsFromDb.getBuildVersion());
			appDetailsFromDb.setBuildVersion(appDetails.getBuildVersion());
			appDetailsFromDb.setForceUpdate(appDetails.isForceUpdate());
			appDetailsFromDb.setOldVersions(oldVersionsList);
			appDetailsFromDb.setStatus(appDetails.getStatus());
			
			savedAppName = appDetailsDao.addApplicationDetails(appDetailsFromDb);
			
		}
		
			response = new PostResponse<State>(
					ResponseCodes.APPLICATION_DETAILS_ADDED_SUCCESFULLY, savedAppName);
			return response;
		
	}



}
