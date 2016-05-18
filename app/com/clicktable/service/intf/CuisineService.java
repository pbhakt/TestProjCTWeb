package com.clicktable.service.intf;

import java.util.Map;
import java.util.Set;

import com.clicktable.model.Cuisine;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface CuisineService {

	
	BaseResponse updateCuisine(Cuisine cuisine);
	

	BaseResponse getCuisines(Map<String, Object> params);

	BaseResponse removeCuisineRelationship(String rest_guid, String[] cuisineGuid, String token);

	BaseResponse addCuisine(Cuisine cuisine);

	BaseResponse getCuisinesRelationship(Map<String, Object> stringParamMap, String token);

	BaseResponse addCuisineRelationship(Set<String> cuisineGuid, String rest_guid, String token);


}



