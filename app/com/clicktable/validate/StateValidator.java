package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.CountryDao;
import com.clicktable.dao.intf.StateDao;
import com.clicktable.model.Country;
import com.clicktable.model.State;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class StateValidator extends EntityValidator<State> {

	@Autowired
	StateDao stateDao;
	@Autowired
	CountryDao countryDao;
	
	public List<ValidationError> validateStateOnAdd(State state, List<ValidationError> listOfError) {
		listOfError.addAll(validateOnAdd(state));
	
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		param.put(Constants.COUNTRY_CODE,state.getCountryCode());
		List<Country> countryList = countryDao.findByFields(Country.class, param);
		if (countryList.isEmpty()) {
			listOfError.add(new ValidationError(Constants.COUNTRY_CODE, UtilityMethods.getErrorMsg(ErrorCodes.COUNTRY_WITH_CODE_NOT_EXISTS), ErrorCodes.COUNTRY_WITH_CODE_NOT_EXISTS));
		}
		
		if (listOfError.isEmpty()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			params.put(Constants.STATE_CODE, state.getStateCode());
			List<State> stateCodeList = stateDao.findByFields(State.class, params);
			if (stateCodeList.isEmpty()) {
				
				Map<String, Object> params2 = new HashMap<String, Object>();
				params2.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				params2.put(Constants.NAME, state.getName());
				List<State> stateNameList = stateDao.findByFields(State.class, params2);
				
				if (stateNameList.isEmpty()) {
				
				Map<String, Object> params1 = new HashMap<String, Object>();
				params1.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				params1.put(Constants.STATE_CODE, state.getStateCode());
				params1.put(Constants.NAME, state.getName());
				params1.put(Constants.COUNTRY_CODE, state.getCountryCode());
				List<State> stateList1 = stateDao.findByFields(State.class,
						params1);
				if (!stateList1.isEmpty()) {
					listOfError.add(new ValidationError(Constants.STATE_NAME, UtilityMethods.getErrorMsg(ErrorCodes.STATE_ALREADY_EXISTS), ErrorCodes.STATE_ALREADY_EXISTS));
				}
				}else{
					listOfError.add(new ValidationError(Constants.STATE_NAME, UtilityMethods.getErrorMsg(ErrorCodes.STATE_ALREADY_EXISTS), ErrorCodes.STATE_ALREADY_EXISTS));
				}
			} else {
				listOfError.add(new ValidationError(Constants.STATE_CODE, UtilityMethods.getErrorMsg(ErrorCodes.STATE_WITH_CODE_EXISTS), ErrorCodes.STATE_WITH_CODE_EXISTS));
			}
		}
		return listOfError;
	}

	public State validateState(State state, List<ValidationError> listOfError) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, state.getGuid());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<State> existingList = stateDao.findByFields(State.class, params);
		State existing = null;
		if(existingList.size()==1){
			existing = existingList.get(0);
			if(stateDao.hasChildRelationships(existing)){
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.CANT_DELETE_STATE), ErrorCodes.CANT_DELETE_STATE));	
			}
		}else{
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_STATE_GUID), ErrorCodes.INVALID_STATE_GUID));
		}
		return existing;
	}

	public List<ValidationError> validateStateOnUpdate(State state, List<ValidationError> listOfError) {
		
		//listOfError = validateStateOnAdd(state, listOfError);
		
		return listOfError;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.STATE_GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_STATE_GUID;
	}
	
}
