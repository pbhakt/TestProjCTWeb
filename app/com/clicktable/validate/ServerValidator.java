package com.clicktable.validate;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.clicktable.model.Server;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class ServerValidator extends EntityValidator<Server> 
{
   

	
	@Override
	public Map<String, Object> validateFinderParams(Map<String, Object> params, Class type) 
	{
	    Map<String, Object> validParamMap;
	    validParamMap = super.validateFinderParams(params, Server.class);
	    for (Entry<String, Object> entry : params.entrySet()) 
	    {
		if (entry.getKey().equals(Constants.TABLE_GUID))
		    {
		    validParamMap.put(entry.getKey(), (String) entry.getValue());
		    }
	    }
	  return validParamMap;
	    
	}
	
	
	
	

	public List<ValidationError> validateServerOnAdd(Server server) {
		List<ValidationError> errorList = validateOnAdd(server);
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, server.getStatus(), server.getLanguageCode());

		return errorList;
	}

	/**
	 * validations on server at the time of updation
	 * 
	 * @param server
	 * @return
	 */

	public List<ValidationError> validateServerOnUpdate(Server server) {
		List<ValidationError> errorList = validateServerOnAdd(server);
		return errorList;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.SERVER_GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_SERVER_GUID;
	}
	

}

