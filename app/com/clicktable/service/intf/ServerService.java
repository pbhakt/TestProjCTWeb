package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.Server;
import com.clicktable.response.BaseResponse;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface ServerService {
	
	/**
	 * Add new Server
	 * @param server	Server to be added
	 * @return
	 */
	BaseResponse addServer(Server server, String token);
	
	/**
	 * Get servers based on parameters
	 * @param params	search parameters
	 * @return
	 */
	BaseResponse getServers(Map<String,Object> params);
	
	/**
	 * Update Server data
	 * @param server
	 * @return
	 */
	BaseResponse updateServer(Server server, String token);
	
	
	BaseResponse deleteServer(String serverGuid, String token);

	BaseResponse getRestaurantServers(Map<String, Object> params, String token);

	
}
