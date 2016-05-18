package com.clicktable.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.clicktable.dao.intf.UserTokenDao;
import com.clicktable.model.UserToken;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.service.intf.UserTokenService;
import com.clicktable.util.Constants;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.StaffValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class UserTokenServiceImpl implements UserTokenService {

	@Autowired
	UserTokenDao userTokenDao;
	
	@Autowired
	StaffValidator staffValidator;
	
	@Override
	@Transactional
	public void addUserToken(UserToken userToken) {
		userTokenDao.create(userToken);

	}

	@Override
	@Transactional(readOnly=true)
	public boolean tokenExists(String token) {
		boolean exists = false;
		Map<String, Object> params = new java.util.HashMap<String, Object>();
		params.put(Constants.TOKEN, token);
		List<UserToken> userTokenList = userTokenDao.findByFields(params);
		if (userTokenList.size() > 0) {
			exists = true;
		}
		return exists;
	}

	@Override
	@Transactional
	public void deleteToken(String token) 
	{
	        Map<String, Object> params = new java.util.HashMap<String, Object>();
		params.put(Constants.TOKEN, token);
		List<UserToken> userTokenList = userTokenDao.findByFields(params);
		if (userTokenList.size() > 0) 
		{
			UserToken userToken = userTokenList.get(0);
			userTokenDao.deleteToken(userToken);
		}
	    
	}
	
	@Transactional(readOnly=true)
	public UserToken findToken(String token)
	{
	        UserToken userToken = null;
	        Map<String, Object> params = new java.util.HashMap<String, Object>();
		params.put(Constants.TOKEN, token);
		List<UserToken> userTokenList = userTokenDao.findByFields(params);
		if (userTokenList.size() > 0) 
		{
		  userToken = userTokenList.get(0);
		}
		
		return userToken;
	}

	@Transactional(readOnly = true)
	@Override
	public BaseResponse add_UserToken(UserToken user_token) {
		BaseResponse response = new BaseResponse();
		List<ValidationError> listOfError = staffValidator
				.validateUserToken(user_token);
		if (listOfError.isEmpty()) {
			user_token.setGuid(UtilityMethods.generateCtId());
			String token_guid = userTokenDao.addUserToken(user_token);
			if (token_guid == null) {
				response.createResponse(
						ResponseCodes.USER_TOKEN_ADDED_FAILURE, false);
			} else {
				response = new PostResponse<UserToken>(
						ResponseCodes.USER_TOKEN_ADDED_SUCCESSFULLY, token_guid);
			}
		} else {
			response = new ErrorResponse(
					ResponseCodes.USER_TOKEN_ADDED_FAILURE, listOfError);
		}
		return response;
	}
}
