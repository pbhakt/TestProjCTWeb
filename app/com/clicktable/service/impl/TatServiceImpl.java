package com.clicktable.service.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.dao.intf.TatDao;
import com.clicktable.model.Tat;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.TatService;
import com.clicktable.util.Constants;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.TatValidator;
import com.clicktable.validate.ValidationError;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class TatServiceImpl implements TatService {

	@Autowired
	TatDao tatDao;
	
	@Autowired
	TatValidator validateTatObject;

	@Autowired
	AuthorizationService authorizationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
        public BaseResponse addTat(Tat tat, String token)
	{

	        tat = new Tat(tat.getName());
		tat.setGuid(UtilityMethods.generateCtId());
		tat.setStatus(Constants.ACTIVE_STATUS);
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		tat.setInfoOnCreate(userInfo);
		
		List<ValidationError> listOfErrorForTat = validateTatObject.validateTatOnAdd(tat);
		
		//right now we are using two methods one to check whether name already exists and other
		//to create tat.but later on we will use only a single query for the same
		
		//check for active name of tat
		validateTatObject.validateAgainstExisting(tat, listOfErrorForTat);
		/*Map<String,Object> params = new HashMap<String, Object>();
		params.put(Constants.NAME, tat.getName());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Tat> tatList = tatDao.findByFields(Tat.class, params);
		*/
		BaseResponse response=null;
		
		if (listOfErrorForTat.isEmpty())
		{
		  /*  //if name already exists then send error message
		   if(tatList.size()>0)
		   {
		       Logger.debug("tat name already exists");
		       ValidationError error = new ValidationError(Constants.NAME,UtilityMethods.getErrorMsg(ErrorCodes.TAT_NAME_ALREADY_EXISTS),ErrorCodes.TAT_NAME_ALREADY_EXISTS);
		       listOfErrorForTat.add(error);
		       response = new ErrorResponse(ResponseCodes.TAT_ADDED_FAILURE,listOfErrorForTat);
				
		   }
		   //if name doesn't exist than create a new one
		   else
		   {*/
		    Logger.debug("creating new tat node");
		    Tat newTat = tatDao.create(tat);
		    response = new PostResponse<Tat>(ResponseCodes.TAT_ADDED_SUCCESFULLY,newTat.getGuid());
		  // }
		
		} 
		else 
		{
		    response = new ErrorResponse(ResponseCodes.TAT_ADDED_FAILURE,listOfErrorForTat);
		}

		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly =true)
	public BaseResponse getTats(Map<String, Object> params)
	{
		BaseResponse getResponse; 
		Map<String, Object> qryParamMap = validateTatObject.validateFinderParams(params, Tat.class);
		List<Tat> tatList = tatDao.findByFields(Tat.class, qryParamMap);
		getResponse= new GetResponse<Tat>(ResponseCodes.TAT_RECORD_FETCH_SUCCESFULLY, tatList);
		return getResponse;

	}

	
	@Override
	@Transactional
	public BaseResponse addTats(List<Tat> tats)
	{
		BaseResponse response; 
		List<ValidationError> listOfError = new ArrayList<>();		
		for(Tat tat:tats){			
			//tat.setInfoOnCreate(userInfo);
			listOfError.addAll(validateTatObject.validateTatOnAdd(tat));
			if(listOfError.isEmpty())
				validateTatObject.validateAgainstExisting(tat,listOfError);
		}		
		if(listOfError.isEmpty()){
			List<Tat> addedTat = tatDao.createMultiple(tats);
			Object[] guid_array = validateTatObject.getGuids(addedTat).toArray();
			response = new PostResponse<>(ResponseCodes.TAT_ADDED_SUCCESFULLY, guid_array);
			return response;
		}
		response = new ErrorResponse(ResponseCodes.TAT_ADDED_FAILURE,listOfError);
		return response;

	}

	

}
