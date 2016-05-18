package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.libs.Json;

import com.clicktable.dao.intf.CountryDao;
import com.clicktable.model.Country;
import com.clicktable.model.Restaurant;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CountryService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CountryValidator;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.ValidationError;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class CountryServiceImpl implements CountryService {

	@Autowired
	CountryDao countryDao;

	@Autowired
	CountryValidator validateCountryObject;

	@Autowired
	AuthorizationService authorizationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse addCountry(List<Country> countries, String token) {
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		List<ValidationError> listOfErrorForCountry = new ArrayList<ValidationError>();
		for (Country country : countries) {
			country.setStatus(Constants.ACTIVE_STATUS);
			country.setInfoOnCreate(userInfo);
			listOfErrorForCountry=(validateCountryObject.validateCountryOnAdd(country,listOfErrorForCountry));
		}

		BaseResponse response = null;
		if (listOfErrorForCountry.isEmpty()) {
			List<String> guids = new ArrayList<String>();
			for (Country c : countries) {
					Country createdCountry = countryDao.create(c);
					guids.add(createdCountry.getGuid());
			}
			if (guids.isEmpty())
				response = new ErrorResponse(ResponseCodes.COUNTRY_ADDED_FAILURE, listOfErrorForCountry);
			else
				response = new PostResponse<Restaurant>(ResponseCodes.COUNTRY_ADDED_SUCCESFULLY, Json.stringify(Json.toJson(guids)));
		} else {
			response = new ErrorResponse(ResponseCodes.COUNTRY_ADDED_FAILURE, listOfErrorForCountry);
		}

		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public BaseResponse getCountry(Map<String, Object> params) {
		BaseResponse getResponse;
		if(!params.containsKey(Constants.STATUS))
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		Map<String, Object> qryParamMap = validateCountryObject.validateFinderParams(params, Country.class);
		List<Country> countryList = countryDao.findByFields(Country.class, qryParamMap);
		getResponse = new GetResponse<Country>(ResponseCodes.COUNTRY_RECORD_FETCH_SUCCESFULLY, countryList);
		return getResponse;

	}

	@Override
	public BaseResponse deleteCountry(Country country, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Country existing = validateCountryObject.validateCountry(country, listOfError);
		if(listOfError.isEmpty())
		{
			Country countryToUpdate = existing;
			countryToUpdate.setStatus(Constants.DELETED_STATUS);
			listOfError.addAll(CustomValidations.validateStatusAndLanguageCode(listOfError, country.getStatus(), existing.getLanguageCode()));
			if(listOfError.isEmpty())
			{
				Country updated=countryDao.update(countryToUpdate);
				
				response = new UpdateResponse<>(ResponseCodes.COUNTRY_DELETED_SUCCESFULLY, updated.getGuid());
				return response;

			}
		}
		return new ErrorResponse(ResponseCodes.COUNTRY_DELETION_FAILURE, listOfError);
	}

	@Override
	public BaseResponse updateCountryRequest(Country country, String token) {
		BaseResponse response = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Country existing = null;
		if (country.getGuid() == null) {
			listOfError.add(validateCountryObject.createError(Constants.GUID, ErrorCodes.GUID_REQUIRED));
		} else {
			existing = countryDao.find(country.getGuid());
			if (existing == null) {
				listOfError.add(validateCountryObject.createError(Constants.GUID, ErrorCodes.INVALID_COUNTRY_ID));
			} else {
				if(country.getName().equals(existing.getName()) && country.getCountryCode().equals(existing.getCountryCode())){
					listOfError.add(new ValidationError(Constants.COUNTRY_NAME, UtilityMethods.getErrorMsg(ErrorCodes.COUNTRY_NAME_ALREADY_EXISTS), ErrorCodes.COUNTRY_NAME_ALREADY_EXISTS));
					listOfError.add(new ValidationError(Constants.COUNTRY_CODE, UtilityMethods.getErrorMsg(ErrorCodes.COUNTRY_WITH_CODE_EXISTS), ErrorCodes.COUNTRY_WITH_CODE_EXISTS));
					
				}else{
					if(listOfError.isEmpty() && !country.getName().equals(existing.getName())){
						existing.setName(country.getName());
						Map<String, Object> params2 = new HashMap<String, Object>();
						params2.put(Constants.STATUS, Constants.ACTIVE_STATUS);
						params2.put(Constants.NAME, country.getName());
						List<Country> countryNameList = countryDao.findByFields(Country.class, params2);
						if(countryNameList.size()==1){
							listOfError.add(new ValidationError(Constants.COUNTRY_NAME, UtilityMethods.getErrorMsg(ErrorCodes.COUNTRY_NAME_ALREADY_EXISTS), ErrorCodes.COUNTRY_NAME_ALREADY_EXISTS));
						}
					}
					if(listOfError.isEmpty() && !country.getCountryCode().equals(existing.getCountryCode())){
						existing.setCountryCode(country.getCountryCode());
						Map<String, Object> params = new HashMap<String, Object>();
						params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
						params.put(Constants.COUNTRY_CODE, country.getCountryCode());
						List<Country> countryCodeList = countryDao.findByFields(Country.class, params);
						if(countryCodeList.size()>0){
							listOfError.add(new ValidationError(Constants.COUNTRY_CODE, UtilityMethods.getErrorMsg(ErrorCodes.COUNTRY_WITH_CODE_EXISTS), ErrorCodes.COUNTRY_WITH_CODE_EXISTS));
						}
					}
				}
			}
			
		}
		
		if(listOfError.isEmpty())
		{
			Country updated=countryDao.updateCountry(existing, countryDao.find(country.getGuid()));
			response = new UpdateResponse<>(ResponseCodes.COUNTRY_UPDATED_SUCCESFULLY, updated.getGuid());
			return response;

		}
	return new ErrorResponse(ResponseCodes.COUNTRY_UPDATION_FAILURE, listOfError);
	}

}
