package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.Building;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface BuildingService {

	BaseResponse getBuilding(Map<String, Object> params);

	BaseResponse addBuilding(Building building, String header);

	BaseResponse deleteBuilding(Building building, String token);

	BaseResponse updateBuildingRequest(Building building, String token);
	
}
