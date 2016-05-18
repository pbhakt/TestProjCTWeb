package com.clicktable.service.intf;

import java.io.InputStream;
import java.util.Map;

import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface AddressService {

	BaseResponse addAddress(Map<String, Object> params);

	//BaseResponse addAddress(File file);

	BaseResponse addAddress(InputStream file);

	
}
