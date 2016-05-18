package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.Region;
import com.clicktable.response.BaseResponse;

public interface RegionService {

	BaseResponse getRegion(Map<String, Object> params);

	BaseResponse addRegion(Region region, String header);

	BaseResponse deleteRegion(Region region, String token);

	BaseResponse updateRegionRequest(Region region, String header);
	
}
