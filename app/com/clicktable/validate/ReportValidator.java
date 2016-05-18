package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import play.Logger;
import play.i18n.Messages;

import com.clicktable.model.UserInfoModel;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class ReportValidator {

	public void validateFileFormat(String fileFormat, List<ValidationError> errorList){
		if ((!UtilityMethods.getEnumValues(Constants.REPORTS, Constants.FILE_FORMAT).contains(fileFormat))) {
			errorList.add(new ValidationError(Constants.FILE_FORMAT, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_FILE_FORMAT), ErrorCodes.INVALID_FILE_FORMAT));
		}
	}
	
	
	public Map<String, Object> validateParams(Map<String, Object> paramMap, List<ValidationError> errorList, UserInfoModel userInfo) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (paramMap.containsKey(Constants.START_DATE)) {
			
			if (paramMap.containsKey(Constants.END_DATE)) {
				DateTime startDate = getValidDate(paramMap.get(Constants.START_DATE).toString(),Constants.START_DATE,errorList);
				DateTime endDate = getValidDate(paramMap.get(Constants.END_DATE).toString(),Constants.END_DATE, errorList);
				if(errorList.isEmpty()){
					if(endDate.isBefore(startDate)){
							errorList.add(new ValidationError(Constants.END_DATE, UtilityMethods.getErrorMsg(com.clicktable.util.ErrorCodes.INVALID_END_DATE), com.clicktable.util.ErrorCodes.INVALID_END_DATE));
					}
					params.put(Constants.START_DATE, paramMap.get(Constants.START_DATE));
					params.put(Constants.END_DATE, paramMap.get(Constants.END_DATE));
				}
			}else {
					errorList.add(new ValidationError(Constants.END_DATE, Messages.get(ErrorCodes.REQUIRED, Constants.END_DATE), ErrorCodes.REQUIRED));
				
			}
			
		} else {
			
			errorList.add(new ValidationError(Constants.START_DATE, Messages.get(ErrorCodes.REQUIRED, Constants.START_DATE), ErrorCodes.REQUIRED));
			
		}
		
		if(paramMap.containsKey(Constants.FILE_FORMAT)){
			Logger.debug("Constants.FILE_FORMAT"+Constants.FILE_FORMAT);
			if ((!UtilityMethods.getEnumValues(Constants.REPORTS, Constants.FILE_FORMAT).contains(paramMap.get(Constants.FILE_FORMAT)))) {
				errorList.add(new ValidationError(Constants.FILE_FORMAT, UtilityMethods.getErrorMsg(com.clicktable.util.ErrorCodes.INVALID_FILE_FORMAT), com.clicktable.util.ErrorCodes.INVALID_FILE_FORMAT));
			}else{
				params.put(Constants.FILE_FORMAT, paramMap.get(Constants.FILE_FORMAT));
			}
		}else{
			errorList.add(new ValidationError(Constants.FILE_FORMAT, UtilityMethods.getErrorMsg(ErrorCodes.FILE_FORMAT_REQUIRED), ErrorCodes.FILE_FORMAT_REQUIRED));
		}
		if (userInfo.getRoleId() != 1l) {
			params.put(Constants.REST_GUID, userInfo.getRestGuid());
		} else if (paramMap.containsKey(Constants.REST_GUID)) {
			params.put(Constants.REST_GUID, paramMap.get(Constants.REST_GUID));
		} else {
			errorList.add(new ValidationError(Constants.REST_GUID, Messages.get(ErrorCodes.REQUIRED, Constants.REST_GUID), ErrorCodes.REQUIRED));
		}
		return params;
	}
	
	private DateTime getValidDate(String dateStr,String fieldName, List<ValidationError> errorList) {
		DateTime date = null;
		try {
			date = DateTime.parse(dateStr);
		} catch (Exception e) {
			errorList.add(new ValidationError(fieldName, e.getMessage(), com.clicktable.util.ErrorCodes.INVALID_DATE_FORMAT));
		}
		return date;
	}
	
}
