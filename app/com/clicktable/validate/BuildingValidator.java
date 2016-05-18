package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.BuildingDao;
import com.clicktable.dao.intf.LocalityDao;
import com.clicktable.dao.intf.RegionDao;
import com.clicktable.model.Building;
import com.clicktable.model.Locality;
import com.clicktable.model.Region;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class BuildingValidator extends EntityValidator<Building> {

	@Autowired
	BuildingDao buildingDao;
	
	@Autowired
	RegionDao regionDao;
	
	@Autowired
	LocalityDao localityDao;
	
	public List<ValidationError> validateBuildingOnAdd(Building building) {
		List<ValidationError> errorList = validateOnAdd(building);
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		param.put(Constants.NAME,building.getRegionName());
		List<Region> cityList = regionDao.findByFields(Region.class, param);
		if (cityList.isEmpty()) {
			errorList.add(new ValidationError(Constants.REGION_NAME, UtilityMethods.getErrorMsg(ErrorCodes.REGION_WITH_NAME_NOT_EXISTS), ErrorCodes.REGION_WITH_NAME_NOT_EXISTS));
		}
		
		if (errorList.isEmpty()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			params.put(Constants.NAME, building.getLocalityName());
			params.put(Constants.REGION_NAME, building.getRegionName());
			List<Locality> localityList = localityDao.findByFields(Locality.class, params);
			System.out.println("::::::::stateList::::::" + localityList);
			if (!localityList.isEmpty()) {
				Map<String, Object> params1 = new HashMap<String, Object>();
				params1.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				params1.put(Constants.NAME, building.getName());
				params1.put(Constants.LOCALITY_NAME, building.getLocalityName());
				params1.put(Constants.REGION_NAME, building.getRegionName());
				List<Building> buildingList1 = buildingDao.findByFields(Building.class,
						params1);
				System.out.println("::::::::stateList1::::::" + buildingList1);
				if (!buildingList1.isEmpty()) {
					errorList.add(new ValidationError(Constants.NAME, UtilityMethods.getErrorMsg(ErrorCodes.BUILDING_ALREADY_EXISTS), ErrorCodes.BUILDING_ALREADY_EXISTS));
				}
			} else {
				errorList.add(new ValidationError(Constants.LOCALITY_NAME, UtilityMethods.getErrorMsg(ErrorCodes.LOCALITY_WITH_NAME_NOT_EXISTS), ErrorCodes.LOCALITY_WITH_NAME_NOT_EXISTS));
			}
		}
		
		
		return errorList;
	}

	public Building validateBuilding(Building building,
			List<ValidationError> listOfError) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, building.getGuid());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Building> existingList = buildingDao.findByFields(Building.class, params);
		Building existing = null;
		if(existingList.size()==1){
			existing = existingList.get(0);
		}else{
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_BUILDING_ID), ErrorCodes.INVALID_BUILDING_ID));
		}
		return existing;
	}

	public List<ValidationError> validateBuildingOnUpdate(Building building,
			List<ValidationError> listOfError) {
		listOfError = validateBuildingOnAdd(building);
		return listOfError;
	}
	
	@Override
	public String getMissingGuidErrorCode() {
		return ErrorCodes.BUILDING_GUID_REQUIRED;
	}

	@Override
	public String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_BUILDING_ID;
	}

	
}
