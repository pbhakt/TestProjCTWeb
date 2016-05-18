package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.ApplicationDetails;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface AppDetailsService {

	BaseResponse getAppDetails(Map<String, Object> params);

	BaseResponse addAppDetails(ApplicationDetails appDetails, String token);

	
}
