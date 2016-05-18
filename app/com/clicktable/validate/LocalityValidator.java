package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.CityDao;
import com.clicktable.dao.intf.LocalityDao;
import com.clicktable.dao.intf.RegionDao;
import com.clicktable.model.City;
import com.clicktable.model.Locality;
import com.clicktable.model.Region;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class LocalityValidator extends EntityValidator<Locality> {

	
	@Autowired
	LocalityDao localityDao;
	
	@Autowired
	RegionDao regionDao;
	
	@Autowired
	CityDao cityDao;
	
	public List<ValidationError> validateLocalityOnAdd(Locality locality) {
		List<ValidationError> errorList = validateOnAdd(locality);
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		param.put(Constants.NAME,locality.getCityName());
		List<City> cityList = cityDao.findByFields(City.class, param);
		if (cityList.isEmpty()) {
			errorList.add(new ValidationError(Constants.CITY_NAME, UtilityMethods.getErrorMsg(ErrorCodes.CITY_WITH_NAME_NOT_EXISTS), ErrorCodes.CITY_WITH_NAME_NOT_EXISTS));
		}
		
		if (errorList.isEmpty()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			params.put(Constants.NAME, locality.getRegionName());
			params.put(Constants.CITY_NAME, locality.getCityName());
			List<Region> regionList = regionDao.findByFields(Region.class, params);
			if (!regionList.isEmpty()) {
				Map<String, Object> params1 = new HashMap<String, Object>();
				params1.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				params1.put(Constants.NAME, locality.getName());
				params1.put(Constants.CITY_NAME, locality.getCityName());
				params1.put(Constants.REGION_NAME, locality.getRegionName());
				List<Locality> localityList1 = localityDao.findByFields(Locality.class,
						params1);
				if (!localityList1.isEmpty()) {
					errorList.add(new ValidationError(Constants.NAME, UtilityMethods.getErrorMsg(ErrorCodes.LOCALITY_ALREADY_EXISTS), ErrorCodes.LOCALITY_ALREADY_EXISTS));
				}
			} else {
				errorList.add(new ValidationError(Constants.REGION_NAME, UtilityMethods.getErrorMsg(ErrorCodes.REGION_WITH_NAME_NOT_EXISTS), ErrorCodes.REGION_WITH_NAME_NOT_EXISTS));
			}
		}
		
		return errorList;
	}

	public Locality validateLocality(Locality locality,
			List<ValidationError> listOfError) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, locality.getGuid());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Locality> existingList = localityDao.findByFields(Locality.class, params);
		Locality existing = null;
		if(existingList.size()==1){
			existing = existingList.get(0);
			if(localityDao.hasChildRelationships(existing)){
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.CANT_DELETE_LOCALITY), ErrorCodes.CANT_DELETE_LOCALITY));	
			}
		}else{
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_LOCALITY_ID), ErrorCodes.INVALID_LOCALITY_ID));
		}
		return existing;
	}

	public List<ValidationError> validateLocalityOnUpdate(Locality locality,
			List<ValidationError> listOfError) {
		listOfError = validateLocalityOnAdd(locality);
		return listOfError;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.LOCALITY_GUID;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_LOCALITY_ID;
	}
	
}
