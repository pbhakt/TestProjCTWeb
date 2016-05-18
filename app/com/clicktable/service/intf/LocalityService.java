package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.Locality;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface LocalityService {

	BaseResponse addLocality(Locality locality, String header);

	BaseResponse getLocalities(Map<String, Object> stringParamMap);

	BaseResponse deleteLocality(Locality locality, String token);

	BaseResponse updateLocalityRequest(Locality locality, String token);
	
}
