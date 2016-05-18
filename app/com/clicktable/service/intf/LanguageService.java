package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.response.BaseResponse;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface LanguageService {
	
	/**
	 * Add new Language
	 * @param language	Language to be added
	 * @return
	 */
	//BaseResponse addLanguage(Language language,String token);
	
	/**
	 * Get languages based on parameters
	 * @param params	search parameters
	 * @return
	 */
	BaseResponse getLanguage(Map<String,Object> params);
	
	/**
	 * Update Language data
	 * @param language
	 * @return
	 */
	//BaseResponse updateLanguage(Language language, String token);

	
}
