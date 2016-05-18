package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.CityDao;
import com.clicktable.dao.intf.StateDao;
import com.clicktable.model.City;
import com.clicktable.model.State;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class CityValidator extends EntityValidator<City> {

	@Autowired
	CityDao cityDao;
	
	@Autowired
	StateDao stateDao;
	
	public List<ValidationError> validateCityOnAdd(City city) {
		List<ValidationError> errorList = validateOnAdd(city);
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		param.put(Constants.STATE_CODE,city.getStateCode());
		List<State> stateList = stateDao.findByFields(State.class, param);
		if (stateList.isEmpty()) {
			errorList.add(new ValidationError(Constants.STATE_CODE, UtilityMethods.getErrorMsg(ErrorCodes.STATE_WITH_CODE_NOT_EXIST), ErrorCodes.STATE_WITH_CODE_NOT_EXIST));
		}
		
		if (errorList.isEmpty()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			params.put(Constants.NAME, city.getName());
			params.put(Constants.STATE_CODE, city.getStateCode());
			List<City> cityList = cityDao.findByFields(City.class, params);
			if (!cityList.isEmpty()) {
				errorList.add(new ValidationError(Constants.NAME, UtilityMethods.getErrorMsg(ErrorCodes.CITY_ALREADY_EXISTS), ErrorCodes.CITY_ALREADY_EXISTS));
			}
			}
		
		return errorList;
	}

	public City validateCity(City city, List<ValidationError> listOfError) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, city.getGuid());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<City> existingList = cityDao.findByFields(City.class, params);
		City existing = null;
		if(existingList.size()==1){
			existing = existingList.get(0);
			if(cityDao.hasChildRelationships(existing)){
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.CANT_DELETE_CITY), ErrorCodes.CANT_DELETE_CITY));	
			}
		}else{
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_CITY_GUID), ErrorCodes.INVALID_CITY_GUID));
		}
		return existing;
	}

	public List<ValidationError> validateCityOnUpdate(City city, List<ValidationError> listOfError) {
		listOfError = validateCityOnAdd(city);
		return listOfError;
	}

	@Override
	public String getMissingGuidErrorCode() {
		return ErrorCodes.CITY_GUID_REQUIRED;
	}

	@Override
	public String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_CITY_GUID;
	}

	
}
