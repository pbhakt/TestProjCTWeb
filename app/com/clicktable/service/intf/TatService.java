package com.clicktable.service.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Tat;
import com.clicktable.response.BaseResponse;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface TatService {
	
	/**
	 * Add new Tat
	 * @param tat	Tat to be added
	 * @return
	 */
	BaseResponse addTat(Tat tat,String token);
	
	/**
	 * Get tats based on parameters
	 * @param params	search parameters
	 * @return
	 */
	BaseResponse getTats(Map<String,Object> params);

	BaseResponse addTats(List<Tat> tats);
	
	/**
	 * Update Tat data
	 * @param tat
	 * @return
	 */
	//BaseResponse updateTat(Tat tat, String token);
	
	

	
}
