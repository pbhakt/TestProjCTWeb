package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;

import com.clicktable.dao.intf.TatDao;
import com.clicktable.model.Tat;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class TatValidator extends EntityValidator<Tat> 
{
   
	@Autowired
	TatDao tatDao;
	
	/*@Autowired
	TatRepo restRepo*/;
	
	
	
	@Override
	public Map<String, Object> validateFinderParams(Map<String, Object> params, Class type) 
	{
	    Map<String, Object> validParamMap;
	    validParamMap = super.validateFinderParams(params, Tat.class);
	    
	    if(params.containsKey(Constants.REST_GUID))
	    {
	    	validParamMap.put(Constants.REST_GUID, params.get(Constants.REST_GUID));
	    }
	    if(params.containsKey(Constants.COUNTRY_GUID))
	    {
	    	validParamMap.put(Constants.COUNTRY_GUID, params.get(Constants.COUNTRY_GUID));
	    }
	    return validParamMap;
	    
	}
	
	
	
	

	public List<ValidationError> validateTatOnAdd(Tat tat) {
		List<ValidationError> errorList = validateOnAdd(tat);
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, tat.getStatus(), tat.getLanguageCode());

		
		return errorList;
	}

	/**
	 * validations on tat at the time of updation
	 * 
	 * @param tat
	 * @return
	 */

	public List<ValidationError> validateTatOnUpdate(Tat tat) {
		List<ValidationError> errorList = validateTatOnAdd(tat);
		return errorList;
	}





	public void validateAgainstExisting(Tat tat, List<ValidationError> listOfError) {
		//check for active name of tat
		Map<String,Object> params = new HashMap<String, Object>();
		params.put(Constants.NAME, tat.getName());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Tat> tatList = tatDao.findByFields(Tat.class, params);
					
		//if name already exists then send error message
		if(tatList.size()>0){
		    Logger.debug("tat name already exists");
		    ValidationError error = new ValidationError(Constants.NAME,UtilityMethods.getErrorMsg(ErrorCodes.TAT_NAME_ALREADY_EXISTS),ErrorCodes.TAT_NAME_ALREADY_EXISTS);
		    listOfError.add(error);		
		}
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.TAT_ID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_TAT_ID;
	}

}
