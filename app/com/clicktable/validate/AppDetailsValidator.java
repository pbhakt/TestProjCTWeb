package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.AppDetailsDao;
import com.clicktable.dao.intf.CountryDao;
import com.clicktable.dao.intf.StateDao;
import com.clicktable.model.ApplicationDetails;
import com.clicktable.model.Country;
import com.clicktable.model.Section;
import com.clicktable.model.State;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class AppDetailsValidator extends EntityValidator<ApplicationDetails> {

	@Autowired
	AppDetailsDao apppDetailsDao;
	
	public List<ValidationError> validateAppDetailsOnAdd(ApplicationDetails appDetails, List<ValidationError> listOfError) {
		listOfError.addAll(validateOnAdd(appDetails));
		
		if (!UtilityMethods.getEnumValues(Constants.APPLICATION_DETAILS, Constants.APP_NAME).contains(appDetails.getAppName())) {
			listOfError = CustomValidations.populateErrorList(listOfError, Constants.APP_NAME, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_APPLICATION_NAME), ErrorCodes.INVALID_APPLICATION_NAME);
		}
		
		if (!UtilityMethods.getEnumValues(Constants.APPLICATION_DETAILS, Constants.PLATFORM).contains(appDetails.getPlatform())) {
			listOfError = CustomValidations.populateErrorList(listOfError, Constants.PLATFORM, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_PLATFORM), ErrorCodes.INVALID_PLATFORM);
		}
	
		return listOfError;
	}


	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.STATE_GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_STATE_GUID;
	}
	
}
