package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.Section;
import com.clicktable.response.BaseResponse;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface SectionService {
	
	BaseResponse addSection(Section section,String token);	

	BaseResponse deleteSection(String sectionGuid,String rest_ID, String header);

	BaseResponse updateRestaurant(Section section, String header);

	BaseResponse getSections(Map<String, Object> stringParamMap);

	
}
