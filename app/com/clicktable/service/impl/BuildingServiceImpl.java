package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.clicktable.dao.intf.BuildingDao;
import com.clicktable.model.Building;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.BuildingService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.BuildingValidator;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class BuildingServiceImpl implements BuildingService {

	
	@Autowired
	BuildingDao buildingDao;

	@Autowired
	AuthorizationService authService;
	
	@Autowired
	BuildingValidator buildingValidator;
	
	@Override
	@Transactional(readOnly =true)
	public BaseResponse getBuilding(Map<String, Object> params) {
		BaseResponse getResponse;
		if(!params.containsKey(Constants.STATUS))
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Building> buildingList = buildingDao.findBuildings(params);
		getResponse =  new GetResponse<Building>(ResponseCodes.BUILDING_RECORD_FETCH_SUCCESFULLY, buildingList);
		return getResponse;
	}

	@Override
	@Transactional
	public BaseResponse addBuilding(Building building, String header) {
		BaseResponse response;
		building.setGuid(UtilityMethods.generateCtId());
		building.setStatus(Constants.ACTIVE_STATUS);
		List<ValidationError> listOfError = buildingValidator.validateBuildingOnAdd(building);
		if(listOfError.isEmpty()){
			String buildingGuid = buildingDao.addBuilding(building);
			response = new PostResponse<Building>(
					ResponseCodes.BUILDING_ADDED_SUCCESFULLY, buildingGuid);
			return response;
		}
		
			return new ErrorResponse(ResponseCodes.BUILDING_ADDITION_FAILURE, listOfError);
	}

	@Override
	public BaseResponse deleteBuilding(Building building, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Building existing = buildingValidator.validateBuilding(building, listOfError);
		if(listOfError.isEmpty())
		{
			Building buildingToUpdate = existing;
			buildingToUpdate.setStatus(Constants.DELETED_STATUS);
			listOfError.addAll(CustomValidations.validateStatusAndLanguageCode(listOfError, building.getStatus(), null));
			if(listOfError.isEmpty())
			{
				Building updated=buildingDao.update(buildingToUpdate);
				response = new UpdateResponse<>(ResponseCodes.BUILDING_DELETED_SUCCESFULLY, updated.getGuid());
				return response;
			}
		}
		return new ErrorResponse(ResponseCodes.BUILDING_DELETION_FAILURE, listOfError);
	}

	@Override
	public BaseResponse updateBuildingRequest(Building building, String token) {
		BaseResponse response = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		if (building.getGuid() == null) {
			listOfError.add(buildingValidator.createError(Constants.GUID, ErrorCodes.GUID_REQUIRED));
		} else {
			Building existing = buildingDao.find(building.getGuid());
			if (existing == null) {
				listOfError.add(buildingValidator.createError(Constants.GUID, ErrorCodes.INVALID_BUILDING_ID));
			} else {
				
				if(!building.getRegionName().equals(existing.getRegionName())){
					listOfError.add(buildingValidator.createError(Constants.REGION_NAME, ErrorCodes.REGION_NAME_NOT_EDITABLE));
				}
				if(!building.getLocalityName().equals(existing.getLocalityName())){
					listOfError.add(buildingValidator.createError(Constants.LOCALITY_NAME, ErrorCodes.LOCALITY_NAME_NOT_EDITABLE));
				}
				if(!building.getName().equals(existing.getName()))
					existing.setName(building.getName());
				if(listOfError.isEmpty()){
				building  = new Building(existing);
				listOfError = (buildingValidator.validateBuildingOnUpdate(building, listOfError));
				}
			}
			
		}
		
		if(listOfError.isEmpty())
		{
			Building updated=buildingDao.update(building);
			response = new UpdateResponse<>(ResponseCodes.BUILDING_UPDATED_SUCCESFULLY, updated.getGuid());
			return response;
		}
	return new ErrorResponse(ResponseCodes.BUILDING_UPDATION_FAILURE, listOfError);
	}


}
