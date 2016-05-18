package com.clicktable.service.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Country;
import com.clicktable.response.BaseResponse;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface CountryService {
	
	/**
	 * Add new Country
	 * @param countries	Country to be added
	 * @return
	 */
	BaseResponse addCountry(List<Country> countries,String token);
	
	/**
	 * Get countrys based on parameters
	 * @param params	search parameters
	 * @return
	 */
	BaseResponse getCountry(Map<String,Object> params);

	BaseResponse deleteCountry(Country country, String token);

	BaseResponse updateCountryRequest(Country country, String token);

	
	/**
	 * Update Country data
	 * @param country
	 * @return
	 */
	//BaseResponse updateCountry(Country country, String token);

	
}
