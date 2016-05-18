package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.clicktable.dao.intf.CityDao;
import com.clicktable.model.City;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CityService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CityValidator;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class CityServiceImpl implements CityService {

	
	@Autowired
	CityDao cityDao;

	@Autowired
	AuthorizationService authService;
	
	@Autowired
	CityValidator cityValidator;
	
	
	@Override
	@Transactional(readOnly =true)
	public BaseResponse getCity(Map<String, Object> params) {
		BaseResponse getResponse; 
		if(!params.containsKey(Constants.STATUS))
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<City> cityList = cityDao.findCities(params);
		getResponse =  new GetResponse<City>(ResponseCodes.CITY_RECORD_FETCH_SUCCESFULLY, cityList);
		return getResponse;
	}

	@Override
	@Transactional
	public BaseResponse addCity(City city, String token) {
		BaseResponse response;
		city.setGuid(UtilityMethods.generateCtId());
		city.setStatus(Constants.ACTIVE_STATUS);
		List<ValidationError> listOfError = cityValidator.validateCityOnAdd(city);
		if(listOfError.isEmpty()){
			String cityGuid = cityDao.addCity(city);
			response = new PostResponse<City>(
					ResponseCodes.CITY_ADDED_SUCCESFULLY, cityGuid);
			return response;
		}
		
			return new ErrorResponse(ResponseCodes.CITY_ADDITION_FAILURE, listOfError);
	}

	@Override
	public BaseResponse deleteCity(City city, String header) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		City existing = cityValidator.validateCity(city, listOfError);
		if(listOfError.isEmpty())
		{
			City cityToUpdate = existing;
			cityToUpdate.setStatus(Constants.DELETED_STATUS);
			listOfError.addAll(CustomValidations.validateStatusAndLanguageCode(listOfError, city.getStatus(), null));
			if(listOfError.isEmpty())
			{
				City updated=cityDao.update(cityToUpdate);
				
				response = new UpdateResponse<>(ResponseCodes.CITY_DELETED_SUCCESFULLY, updated.getGuid());
				return response;

			}
		}
		return new ErrorResponse(ResponseCodes.CITY_DELETION_FAILURE, listOfError);
	}

	@Override
	public BaseResponse updateCityRequest(City city, String token) {
		BaseResponse response = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		City existing = null;
		if (city.getGuid() == null) {
			listOfError.add(cityValidator.createError(Constants.GUID, ErrorCodes.GUID_REQUIRED));
		} else {
			existing = cityDao.find(city.getGuid());
			if (existing == null) {
				listOfError.add(cityValidator.createError(Constants.GUID, ErrorCodes.INVALID_CITY_GUID));
			} else {
				if(!city.getStateCode().equals(existing.getStateCode())){
					listOfError.add(cityValidator.createError(Constants.STATE_CODE, ErrorCodes.STATE_CODE_NOT_EDITABLE));
				}
				if(!city.getName().equals(existing.getName()))
					existing.setName(city.getName());
				if(listOfError.isEmpty()){
				city  = new City(existing);
				listOfError = (cityValidator.validateCityOnUpdate(city, listOfError));
				}
			}
			
		}
		
		if(listOfError.isEmpty())
		{
			City updated=cityDao.updateCity(city, cityDao.find(city.getGuid()));
			response = new UpdateResponse<>(ResponseCodes.CITY_UPDATED_SUCCESFULLY, updated.getGuid());
			return response;
		}
	return new ErrorResponse(ResponseCodes.CITY_UPDATION_FAILURE, listOfError);
	}

}
