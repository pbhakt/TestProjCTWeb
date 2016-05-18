package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.Device;
import com.clicktable.response.BaseResponse;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface DeviceService {
	
	/**
	 * Add new Device
	 * @param device	Device to be added
	 * @return
	 */
	BaseResponse addDevice(Device device,String token);
	
	/**
	 * Get devices based on parameters
	 * @param params	search parameters
	 * @return
	 */
	BaseResponse getDevices(Map<String,Object> params);
	
	/**
	 * Update Device data
	 * @param device
	 * @return
	 */
	BaseResponse updateDevice(Device device, String token);
	
	/**
	 * Delete Device
	 * @param deviceGuid
	 * @param token
	 * @return
	 */
	
	public BaseResponse deleteDevice(String deviceGuid, String token);
	
	
	
	
	
	
}
