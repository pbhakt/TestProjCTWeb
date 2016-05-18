package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.libs.Json;

import com.clicktable.dao.intf.CuisineDao;
import com.clicktable.model.Cuisine;
import com.clicktable.model.Restaurant;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CuisineService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CuisineValidator;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class CuisineServiceImpl implements CuisineService {

	@Autowired
	CuisineDao cuisineDao;

	@Autowired
	CuisineValidator validateCuisineObject;

	@Autowired
	AuthorizationService authService;
	
	@Autowired
	RestaurantValidator restValidator;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse addCuisine(Cuisine cuisine) {
		List<ValidationError> listOfError = validateCuisineObject.validateCuisineOnAdd(cuisine);
		if (listOfError.isEmpty())
			if (cuisineDao.cuisineWithNameExists(cuisine.getName()))
				listOfError = CustomValidations.populateErrorList(listOfError, Constants.CUISINE_MODULE, UtilityMethods.getErrorMsg(ErrorCodes.DUPLICATE_CUISINE_NAME),ErrorCodes.DUPLICATE_CUISINE_NAME);
		BaseResponse response;
		if (listOfError.isEmpty()) {
			Cuisine newCuisine = cuisineDao.create(cuisine);
			response = new PostResponse<Cuisine>(ResponseCodes.CUISINE_ADDED_SUCCESFULLY, newCuisine.getGuid());
		} else
			response = new ErrorResponse(ResponseCodes.CUISINE_ADDED_FAILURE, listOfError);
		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse updateCuisine(Cuisine cuisine) {
		List<ValidationError> listOfError = validateCuisineObject.validateCuisineOnAdd(cuisine);
		cuisine = validateCuisineObject.validateCuisineOnUpdate(cuisine, listOfError);
		BaseResponse response;
		if (listOfError.isEmpty()) {
			Cuisine updatedCuisine = cuisineDao.update(cuisine);
			response = new UpdateResponse<Cuisine>(ResponseCodes.CUISINE_UPDATED_SUCCESFULLY, updatedCuisine.getGuid());
		} else
			response = new ErrorResponse(ResponseCodes.CUISINE_UPDATED_FAILURE, listOfError);

		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BaseResponse getCuisines(Map<String, Object> params) {
		BaseResponse getResponse;
		Map<String, Object> qryParamMap = validateCuisineObject.validateFinderParams(params, Cuisine.class);
		List<Cuisine> cuisnes = cuisineDao.findByFields(Cuisine.class, qryParamMap);
		getResponse = new GetResponse<Cuisine>(ResponseCodes.CUISINE_RECORD_FETCH_SUCCESFULLY, cuisnes);
		return getResponse;
	}

	@Override
	@Transactional
	public BaseResponse addCuisineRelationship(Set<String> cuisineGuid, String rest_guid, String token) {
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		BaseResponse response;
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		Restaurant restaurant = restValidator.validateRestaurantInNeo4j(rest_guid, userInfo, listOfError);
		if (listOfError.isEmpty()) {
			cuisineDao.addCuisineRelationship(cuisineGuid, restaurant);
			response = new PostResponse<Cuisine>(ResponseCodes.CUISINE_ADDED_SUCCESFULLY, Json.stringify(Json.toJson(cuisineGuid.toArray())));
		} else
			response = new ErrorResponse(ResponseCodes.CUISINE_UPDATED_FAILURE, listOfError);

		return response;

	}

	@Override
	@Transactional
	public BaseResponse removeCuisineRelationship(String rest_guid, String[] cuisineGuid, String token) {
		// TODO Auto-generated method stub

		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		BaseResponse response;
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		restValidator.validateRestaurant(rest_guid, userInfo, listOfError);
		if (listOfError.isEmpty()) {
			cuisineDao.removeHasCuisineRelationship(rest_guid, cuisineGuid);
			response = new PostResponse<Cuisine>(ResponseCodes.CUISINE_REMOVED_SUCCESFULLY, cuisineGuid);
		} else
			response = new ErrorResponse(ResponseCodes.CUISINE_REMOVED_FAILURE, listOfError);

		return response;

	}

	@Override
	public BaseResponse getCuisinesRelationship(Map<String, Object> params, String token) {
		BaseResponse getResponse;
		boolean isAdmin = true;
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		if (!(userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))) {
			isAdmin = false;
			params.put(Constants.REST_ID, userInfo.getRestGuid());
		}
		Map<String, Object> qryParamMap = validateCuisineObject.validateFinderParams(params, Cuisine.class);
		List<Cuisine> cuisnes = cuisineDao.findByFields(qryParamMap, userInfo.getRestGuid(), isAdmin);
		getResponse = new GetResponse<Cuisine>(ResponseCodes.CUISINE_RECORD_FETCH_SUCCESFULLY, cuisnes);
		return getResponse;
	}

}
