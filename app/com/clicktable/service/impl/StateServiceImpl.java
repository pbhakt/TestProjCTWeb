package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.clicktable.dao.intf.StateDao;
import com.clicktable.model.State;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.StateService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.StateValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class StateServiceImpl implements StateService {

	@Autowired
	StateDao stateDao;

	@Autowired
	AuthorizationService authService;
	
	@Autowired
	StateValidator stateValidator;
	
	@Override
	@Transactional(readOnly = true)
	public BaseResponse getState(Map<String, Object> params) {
		BaseResponse getResponse;
		if(!params.containsKey(Constants.STATUS))
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<State> stateList = stateDao.findStates(params);
		getResponse = new GetResponse<State>(
				ResponseCodes.STATE_RECORD_FETCH_SUCCESFULLY, stateList);
		return getResponse;
	}

	@Override
	@Transactional
	public BaseResponse addState(State state, String token) {
		BaseResponse response;
		state.setGuid(UtilityMethods.generateCtId());
		state.setStatus(Constants.ACTIVE_STATUS);
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		listOfError = stateValidator.validateStateOnAdd(state, listOfError);
		if(listOfError.isEmpty()){
			String stateGuid = stateDao.addState(state);
			response = new PostResponse<State>(
					ResponseCodes.STATE_ADDED_SUCCESFULLY, stateGuid);
			return response;
		}
		
			return new ErrorResponse(ResponseCodes.STATE_ADDITION_FAILURE, listOfError);
	}

	@Override
	public BaseResponse deleteState(State state, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		State existing = stateValidator.validateState(state, listOfError);
		if(listOfError.isEmpty())
		{
			State stateToUpdate = existing;
			stateToUpdate.setStatus(Constants.DELETED_STATUS);
			listOfError.addAll(CustomValidations.validateStatusAndLanguageCode(listOfError, state.getStatus(), null));
			if(listOfError.isEmpty())
			{
				State updated=stateDao.update(stateToUpdate);
				
				response = new UpdateResponse<>(ResponseCodes.STATE_DELETED_SUCCESFULLY, updated.getGuid());
				return response;

			}
		}
		return new ErrorResponse(ResponseCodes.STATE_DELETION_FAILURE, listOfError);
	}

	@Override
	public BaseResponse updateStateRequest(State state, String header) {
		BaseResponse response = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		State existing = null, existing1 = null;
		if (state.getGuid() == null) {
			listOfError.add(stateValidator.createError(Constants.GUID, ErrorCodes.GUID_REQUIRED));
		} else {
			existing = existing1 = stateDao.find(state.getGuid());
			if (existing == null) {
				listOfError.add(stateValidator.createError(Constants.GUID, ErrorCodes.INVALID_STATE_GUID));
			} else {
				if(!state.getCountryCode().equals(existing.getCountryCode())){
					listOfError.add(stateValidator.createError(Constants.COUNTRY_CODE, ErrorCodes.COUNTRY_CODE_NOT_EDITABLE));
				}
				
				if(state.getName().equals(existing.getName()) && state.getStateCode().equals(existing.getStateCode())){
					listOfError.add(new ValidationError(Constants.STATE_NAME, UtilityMethods.getErrorMsg(ErrorCodes.STATE_ALREADY_EXISTS), ErrorCodes.STATE_ALREADY_EXISTS));
					listOfError.add(new ValidationError(Constants.STATE_CODE, UtilityMethods.getErrorMsg(ErrorCodes.STATE_WITH_CODE_EXISTS), ErrorCodes.STATE_WITH_CODE_EXISTS));
					
				}else{
					if(listOfError.isEmpty() && !state.getName().equals(existing.getName())){
						existing.setName(state.getName());
						Map<String, Object> params2 = new HashMap<String, Object>();
						params2.put(Constants.STATUS, Constants.ACTIVE_STATUS);
						params2.put(Constants.NAME, state.getName());
						List<State> stateNameList = stateDao.findByFields(State.class, params2);
						if(stateNameList.size()==1){
							listOfError.add(new ValidationError(Constants.STATE_NAME, UtilityMethods.getErrorMsg(ErrorCodes.STATE_ALREADY_EXISTS), ErrorCodes.STATE_ALREADY_EXISTS));
						}
					}
					if(listOfError.isEmpty() && !state.getStateCode().equals(existing.getStateCode())){
						existing.setStateCode(state.getStateCode());
						Map<String, Object> params = new HashMap<String, Object>();
						params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
						params.put(Constants.STATE_CODE, state.getStateCode());
						List<State> stateCodeList = stateDao.findByFields(State.class, params);

						if(stateCodeList.size()>0){
							listOfError.add(new ValidationError(Constants.STATE_CODE, UtilityMethods.getErrorMsg(ErrorCodes.STATE_WITH_CODE_EXISTS), ErrorCodes.STATE_WITH_CODE_EXISTS));
						}
					}
				}
				if(listOfError.isEmpty()){
					state  = new State(existing);
				}
			}
			
		}
		
		if(listOfError.isEmpty())
		{
			State updated=stateDao.updateState(state, existing1);
			response = new UpdateResponse<>(ResponseCodes.STATE_UPDATED_SUCCESFULLY, updated.getGuid());
			return response;
		}
	return new ErrorResponse(ResponseCodes.STATE_UPDATION_FAILURE, listOfError);
	}

}
