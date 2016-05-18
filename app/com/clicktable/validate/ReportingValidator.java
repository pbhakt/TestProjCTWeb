package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.ReportingPreferenceDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.model.BarEntry;
import com.clicktable.model.ReportingPreference;
import com.clicktable.model.UserInfoModel;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;


@Service
public class ReportingValidator extends EntityValidator<ReportingPreference> {

	@Autowired
	ReportingPreferenceDao preferenceDao;

	@Autowired
	CustomerDao customerDao;

	@Autowired
	ReservationDao resvDao;

	@Autowired
	RestaurantValidator restValidator;

	public List<ValidationError> validateReportingPrefernceOnCreate(ReportingPreference prefrence,UserInfoModel userInfo) {
		List<ValidationError> errorList = validateOnAdd(prefrence);
		HashMap<String, Object> finderParams = new HashMap<String,Object>();
		finderParams.put(Constants.REST_GUID, prefrence.getRestaurantGuid());
		List<ReportingPreference> preferences = preferenceDao.findByCustomeFields(ReportingPreference.class, finderParams);
		if(!preferences.isEmpty()){
			errorList = CustomValidations.populateErrorList(errorList, Constants.REPORTING_PREFERENCE_LABEL, UtilityMethods.getErrorMsg(ErrorCodes.REPORTING_PREFERENCE_EXISTS), ErrorCodes.REPORTING_PREFERENCE_EXISTS);
		}
		if(errorList.isEmpty()){
			restValidator.validateRestaurantInNeo4j(prefrence.getRestaurantGuid(), userInfo, errorList);
		}
		return errorList;
	}

	public List<ValidationError> validateReportingPrefernceOnUpdate(ReportingPreference prefrence,UserInfoModel userInfo) {
		List<ValidationError> errorList = validateOnAdd(prefrence);
		
		System.out.println(errorList.size());
		System.out.println(errorList);
		//validateGuid(prefrence.getGuid(), errorList);
		return errorList;
	}

	
	public Map<String, Object> validateFinderParams(Map<String, Object> params) {
		Map<String, Object> validParamMap = new HashMap<String, Object>();
		validParamMap.putAll(super.validateFinderParams(params, BarEntry.class));
		return validParamMap;
	}

	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_GUID;
	}



	
	
	
}
