package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.AttributeDao;
import com.clicktable.model.Attribute;
//import com.clicktable.repository.AttributeRepo;
import com.clicktable.util.Constants;
//import com.clicktable.dao.intf.AttributeDao;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

/**
 * 
 * @author p.singh
 *
 */


@org.springframework.stereotype.Service
public class AttributeValidator extends EntityValidator<Attribute> 
{
   

	
	/*@Autowired
	AttributeRepo restRepo*/;
	
	@Autowired
	AttributeDao attributeDao;
	
	@Override
	public Map<String, Object> validateFinderParams(Map<String, Object> params, Class type) 
	{
	    Map<String, Object> validParamMap;
	    validParamMap = super.validateFinderParams(params, Attribute.class);
	    
		for (Entry<String, Object> entry : params.entrySet()) 
		{
		if (entry.getKey().equals(Constants.START_WITH) || entry.getKey().equals(Constants.REST_GUID) || entry.getKey().equals(Constants.COUNTRY_GUID))
			validParamMap.put(entry.getKey(), (String) entry.getValue());
		}
	    
	    return validParamMap;
	    
	}
	
	
	
	

	public List<ValidationError> validateAttributeOnAdd(Attribute attribute) {
		List<ValidationError> errorList = validateOnAdd(attribute);
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, attribute.getStatus(), attribute.getLanguageCode());
		
		
		return errorList;
	}

	/**
	 * validations on attribute at the time of updation
	 * 
	 * @param attribute
	 * @return
	 */

	public List<ValidationError> validateAttributeOnUpdate(Attribute attribute) {
		List<ValidationError> errorList = validateAttributeOnAdd(attribute);
		return errorList;
	}
	
	public List<ValidationError> validateAgainstExisting(Attribute attribute, List<ValidationError> errorList) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put(Constants.NAME, attribute.getName());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Attribute> attributeList = attributeDao.findByFields(Attribute.class, params);
		
		if (errorList.isEmpty())
		{
		    if(attributeList.size()>0)
			{
			    ValidationError error = new ValidationError(Constants.NAME,UtilityMethods.getErrorMsg(ErrorCodes.ATTRIBUTE_NAME_ALREADY_EXISTS),ErrorCodes.ATTRIBUTE_NAME_ALREADY_EXISTS);
			    errorList.add(error);			   
				
			}
		}
		return errorList;
	}





	@Override
	public String getMissingGuidErrorCode() {
		return ErrorCodes.ATTR_ID_REQUIRED;
	}

	@Override
	public String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_ATTRIBUTE_ID;
	}


}
