package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.clicktable.dao.intf.RegionDao;
import com.clicktable.model.Region;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.RegionService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.RegionValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class RegionServiceImpl implements RegionService {

	
	@Autowired
	RegionDao regionDao;

	@Autowired
	AuthorizationService authService;
	
	@Autowired
	RegionValidator regionValidator;
	
	@Override
	@Transactional(readOnly =true)
	public BaseResponse getRegion(Map<String, Object> params) {
		BaseResponse getResponse; 
		if(!params.containsKey(Constants.STATUS))
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Region> regionList = regionDao.findRegions(params);
		getResponse =  new GetResponse<Region>(ResponseCodes.REGION_RECORD_FETCH_SUCCESFULLY, regionList);
		return getResponse;
	}

	@Override
	@Transactional
	public BaseResponse addRegion(Region region, String header) {
		BaseResponse response;
		region.setGuid(UtilityMethods.generateCtId());
		region.setStatus(Constants.ACTIVE_STATUS);
		List<ValidationError> listOfError = regionValidator.validateRegionOnAdd(region);
		if(listOfError.isEmpty()){
			String regionGuid = regionDao.addRegion(region);
			response = new PostResponse<Region>(
					ResponseCodes.REGION_ADDED_SUCCESFULLY, regionGuid);
			return response;
		}
		
			return new ErrorResponse(ResponseCodes.REGION_ADDITION_FAILURE, listOfError);
	}

	@Override
	public BaseResponse deleteRegion(Region region, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Region existing = regionValidator.validateRegion(region, listOfError);
		if(listOfError.isEmpty())
		{
			Region regionToUpdate = existing;
			regionToUpdate.setStatus(Constants.DELETED_STATUS);
			listOfError.addAll(CustomValidations.validateStatusAndLanguageCode(listOfError, region.getStatus(), null));
			if(listOfError.isEmpty())
			{
				Region updated=regionDao.update(regionToUpdate);
				
				response = new UpdateResponse<>(ResponseCodes.REGION_DELETED_SUCCESFULLY, updated.getGuid());
				return response;

			}
		}
		return new ErrorResponse(ResponseCodes.REGION_DELETION_FAILURE, listOfError);
	}

	@Override
	public BaseResponse updateRegionRequest(Region region, String header) {
		BaseResponse response = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Region existing = null;
		if (region.getGuid() == null) {
			listOfError.add(regionValidator.createError(Constants.GUID, ErrorCodes.GUID_REQUIRED));
		} else {
			existing = regionDao.find(region.getGuid());
			if (existing == null) {
				listOfError.add(regionValidator.createError(Constants.GUID, ErrorCodes.INVALID_REGION_ID));
			} else {
				if(!region.getStateCode().equals(existing.getStateCode())){
					listOfError.add(regionValidator.createError(Constants.STATE_CODE, ErrorCodes.STATE_CODE_NOT_EDITABLE));
				}
				if(!region.getCityName().equals(existing.getCityName())){
					listOfError.add(regionValidator.createError(Constants.CITY_NAME, ErrorCodes.CITY_NAME_NOT_EDITABLE));
				}
				if(!region.getName().equals(existing.getName()))
					existing.setName(region.getName());
				if(listOfError.isEmpty()){
				region  = new Region(existing);
				listOfError = (regionValidator.validateRegionOnUpdate(region, listOfError));
				}
			}
			
		}
		
		if(listOfError.isEmpty())
		{
			Region updated=regionDao.updateRegion(region, regionDao.find(region.getGuid()));
			response = new UpdateResponse<>(ResponseCodes.REGION_UPDATED_SUCCESFULLY, updated.getGuid());
			return response;
		}
	return new ErrorResponse(ResponseCodes.REGION_UPDATION_FAILURE, listOfError);
	}

}
