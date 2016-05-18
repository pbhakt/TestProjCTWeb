package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.HistoricalTat;
import com.clicktable.response.BaseResponse;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface HistoricalTatService {
	
	/**
	 * Add new HistoricalTat
	 * @param historicalTat	HistoricalTat to be added
	 * @return
	 */
	BaseResponse addHistoricalTat(HistoricalTat historicalTat,String token);
	
	/**
	 * Get historicalTats based on parameters
	 * @param params	search parameters
	 * @return
	 */
	BaseResponse getHistoricalTats(Map<String,Object> params);
	
	/**
	 * Update HistoricalTat data
	 * @param historicalTat
	 * @return
	 */
	BaseResponse updateHistoricalTat(HistoricalTat historicalTat, String token);
	
	/**
	 * Delete HistoricalTat
	 * @param historicalTatGuid
	 * @param token
	 * @return
	 */
	
	public BaseResponse deleteHistoricalTat(String historicalTatGuid, String token);
	
	
	
	
	
	
}
