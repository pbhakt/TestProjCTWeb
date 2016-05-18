package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.BarEntry;
import com.clicktable.model.Reservation;
import com.clicktable.response.BaseResponse;




@org.springframework.stereotype.Service
public interface BarEntryService {
	
	/**
	 * Get conversations based on parameters
	 * @param params	search parameters
	 * @return
	 */
	BaseResponse getBarEntry(Map<String,Object> params);

	BaseResponse addBarEntry(BarEntry barEntry);
	
	BaseResponse moveFromWaitlist(Reservation waiting, String token);

	BaseResponse moveToRestaurant(Map<String, Object> barEntryMap,  String token);

	BaseResponse updateBarEntry(Map<String, Object> barEntryMap, String token);


}
