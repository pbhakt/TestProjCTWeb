package com.clicktable.service.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Attribute;
import com.clicktable.response.BaseResponse;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface AttributeService {
	
	/**
	 * Add new Attribute
	 * @param attribute	Attribute to be added
	 * @return
	 */
	//BaseResponse addAttribute(Attribute attribute,String token);
	
	/**
	 * Get attributes based on parameters
	 * @param params	search parameters
	 * @return
	 */
	BaseResponse getAttributes(Map<String,Object> params);
	
	/**
	 * Update Attribute data
	 * @param attribute
	 * @return
	 */
	//BaseResponse updateAttribute(Attribute attribute, String token);
	
	
	
	
	public BaseResponse addCountryAttributes(String countryGuid, String attrGuid, String token);

	BaseResponse addAttributes(List<Attribute> attributeList);

	BaseResponse addAttribute(Attribute attribute);

	
}
