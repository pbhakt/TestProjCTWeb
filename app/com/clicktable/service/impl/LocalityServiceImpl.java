package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.clicktable.dao.intf.LocalityDao;
import com.clicktable.model.Locality;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.LocalityService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.LocalityValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class LocalityServiceImpl implements LocalityService {

	
	@Autowired
	LocalityDao localityDao;

	@Autowired
	AuthorizationService authService;
	
	@Autowired
	LocalityValidator localityValidator;
	

	@Override
	@Transactional
	public BaseResponse addLocality(Locality locality, String header) {
		BaseResponse response;
		locality.setGuid(UtilityMethods.generateCtId());
		locality.setStatus(Constants.ACTIVE_STATUS);
		List<ValidationError> listOfError = localityValidator.validateLocalityOnAdd(locality);
		if(listOfError.isEmpty()){
			String localityGuid = localityDao.addLocality(locality);
			response = new PostResponse<Locality>(
					ResponseCodes.LOCALITY_ADDED_SUCCESFULLY, localityGuid);
			return response;
		}
		
			return new ErrorResponse(ResponseCodes.LOCALITY_ADDITION_FAILURE, listOfError);
	}


	@Override
	public BaseResponse getLocalities(Map<String, Object> stringParamMap) {
		BaseResponse getResponse; 
		if(!stringParamMap.containsKey(Constants.STATUS))
		stringParamMap.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Locality> localityList = localityDao.getLocalities(stringParamMap);
		getResponse =  new GetResponse<Locality>(ResponseCodes.LOCALITY_RECORD_FETCH_SUCCESFULLY, localityList);
		return getResponse;
	}


	@Override
	public BaseResponse deleteLocality(Locality locality, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Locality existing = localityValidator.validateLocality(locality, listOfError);
		if(listOfError.isEmpty())
		{
			Locality localityToUpdate = existing;
			localityToUpdate.setStatus(Constants.DELETED_STATUS);
			listOfError.addAll(CustomValidations.validateStatusAndLanguageCode(listOfError, locality.getStatus(), null));
			if(listOfError.isEmpty())
			{
				Locality updated=localityDao.update(localityToUpdate);
				
				response = new UpdateResponse<>(ResponseCodes.LOCALITY_DELETED_SUCCESFULLY, updated.getGuid());
				return response;

			}
		}
		return new ErrorResponse(ResponseCodes.LOCALITY_DELETION_FAILURE, listOfError);
	}


	@Override
	public BaseResponse updateLocalityRequest(Locality locality, String token) {
		BaseResponse response = null;
		Locality existing= null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		if (locality.getGuid() == null) {
			listOfError.add(localityValidator.createError(Constants.GUID, ErrorCodes.GUID_REQUIRED));
		} else {
			existing = localityDao.find(locality.getGuid());
			if (existing == null) {
				listOfError.add(localityValidator.createError(Constants.GUID, ErrorCodes.INVALID_LOCALITY_ID));
			} else {
				if(!locality.getCityName().equals(existing.getCityName())){
					listOfError.add(localityValidator.createError(Constants.CITY_NAME, ErrorCodes.CITY_NAME_NOT_EDITABLE));
				}
				if(!locality.getRegionName().equals(existing.getRegionName())){
					listOfError.add(localityValidator.createError(Constants.REGION_NAME, ErrorCodes.REGION_NAME_NOT_EDITABLE));
				}
				if(!locality.getName().equals(existing.getName()))
					existing.setName(locality.getName());
				if(listOfError.isEmpty()){
				locality  = new Locality(existing);
				listOfError = (localityValidator.validateLocalityOnUpdate(locality, listOfError));
				}
			}
			
		}
		
		if(listOfError.isEmpty())
		{
			Locality updated=localityDao.updateLocality(locality, localityDao.find(locality.getGuid()));
			response = new UpdateResponse<>(ResponseCodes.LOCALITY_UPDATED_SUCCESFULLY, updated.getGuid());
			return response;
		}
	return new ErrorResponse(ResponseCodes.LOCALITY_UPDATION_FAILURE, listOfError);
	}

}
