package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.State;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface StateService {

	BaseResponse getState(Map<String, Object> params);

	BaseResponse addState(State state, String token);

	BaseResponse deleteState(State state, String token);

	BaseResponse updateStateRequest(State state, String header);
	
}
