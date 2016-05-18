package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.CorporateOffers;
import com.clicktable.model.Corporation;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface CorporationService {

	BaseResponse getCorporation(Map<String, Object> params);

	BaseResponse addCorporation(Corporation corporation, String token);

	//BaseResponse deleteCorporation(Corporation corporation, String token);

	BaseResponse updateCorporation(Corporation corporation, String token);

	BaseResponse getCorporateOffers(Map<String, Object> params, String token);

	BaseResponse addCorporateOffers(CorporateOffers corporate_offers,
			Map<String, Object> params, String token);

	BaseResponse updateCorporateOffers(CorporateOffers corporate_offers,
			Map<String, Object> params, String token);
	
}
