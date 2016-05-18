package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.City;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface CityService {

	BaseResponse getCity(Map<String, Object> params);

	BaseResponse addCity(City city, String token);

	BaseResponse deleteCity(City city, String header);

	BaseResponse updateCityRequest(City city, String token);
	
}
