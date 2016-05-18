package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.CityDao;
import com.clicktable.dao.intf.RegionDao;
import com.clicktable.dao.intf.StateDao;
import com.clicktable.model.City;
import com.clicktable.model.Region;
import com.clicktable.model.State;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class RegionValidator extends EntityValidator<Region> {

	@Autowired
	StateDao stateDao;
	
	@Autowired
	CityDao cityDao;
	
	@Autowired
	RegionDao regionDao;
	
	public List<ValidationError> validateRegionOnAdd(Region region) {
		List<ValidationError> errorList = validateOnAdd(region);
		
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		param.put(Constants.STATE_CODE,region.getStateCode());
		List<State> stateList = stateDao.findByFields(State.class, param);
		if (stateList.isEmpty()) {
			errorList.add(new ValidationError(Constants.STATE_CODE, UtilityMethods.getErrorMsg(ErrorCodes.STATE_WITH_CODE_NOT_EXIST), ErrorCodes.STATE_WITH_CODE_NOT_EXIST));
		}
		
		if (errorList.isEmpty()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			params.put(Constants.NAME, region.getCityName());
			params.put(Constants.STATE_CODE, region.getStateCode());
			List<City> cityList = cityDao.findByFields(City.class, params);
			if (!cityList.isEmpty()) {
				Map<String, Object> params1 = new HashMap<String, Object>();
				params1.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				params1.put(Constants.NAME, region.getName());
				params1.put(Constants.CITY_NAME, region.getCityName());
				params1.put(Constants.STATE_CODE, region.getStateCode());
				List<Region> regionList1 = regionDao.findByFields(Region.class,
						params1);
				if (!regionList1.isEmpty()) {
					errorList.add(new ValidationError(Constants.NAME, UtilityMethods.getErrorMsg(ErrorCodes.REGION_ALREADY_EXISTS), ErrorCodes.REGION_ALREADY_EXISTS));
				}
			} else {
				errorList.add(new ValidationError(Constants.CITY_NAME, UtilityMethods.getErrorMsg(ErrorCodes.CITY_WITH_NAME_NOT_EXISTS), ErrorCodes.CITY_WITH_NAME_NOT_EXISTS));
			}
		}
		
		return errorList;
	}

	public Region validateRegion(Region region, List<ValidationError> listOfError) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, region.getGuid());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Region> existingList = regionDao.findByFields(Region.class, params);
		Region existing = null;
		if(existingList.size()==1){
			existing = existingList.get(0);
			if(regionDao.hasChildRelationships(existing)){
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.CANT_DELETE_REGION), ErrorCodes.CANT_DELETE_REGION));	
			}
		}else{
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REGION_ID), ErrorCodes.INVALID_REGION_ID));
		}
		return existing;
	}

	public List<ValidationError> validateRegionOnUpdate(Region region,
			List<ValidationError> listOfError) {
		listOfError = validateRegionOnAdd(region);
		return listOfError;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.REGION_GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_REGION_ID;
	}
	
}
