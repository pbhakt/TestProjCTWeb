package com.clicktable.service.intf;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.Onboarding;
import com.clicktable.response.BaseResponse;

@Service
public interface OnboardingService {

	BaseResponse addOnboardingRequest(Onboarding onboard, String token);
	
	BaseResponse updateOnboardingRequest(Onboarding onboard,String token);
	
	BaseResponse getOnboardingRequests(Map<String,Object> params);

	BaseResponse onboardingVerification(Map<String, String> params, String token);

	BaseResponse resendCode(String onboardGuid);

	
}
