package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface CustomSettingService {
	
	BaseResponse getCustomSetting(Map<String, Object> params);
	
	

}
