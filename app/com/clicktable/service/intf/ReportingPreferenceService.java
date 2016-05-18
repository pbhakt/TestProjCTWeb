package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.ReportingPreference;
import com.clicktable.response.BaseResponse;




@org.springframework.stereotype.Service
public interface ReportingPreferenceService {
	
	/**
	 * Get conversations based on parameters
	 * @param params	search parameters
	 * @return
	 */
	BaseResponse getReportingPreferences(Map<String,Object> params);
	
	BaseResponse updateReportingPreferences(ReportingPreference preference, String token);

	BaseResponse addReportingPreferences(ReportingPreference prefrence, String token);

	
}
