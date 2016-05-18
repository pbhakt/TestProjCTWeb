package com.clicktable.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.ReportingPreferenceDao;
import com.clicktable.model.ReportingPreference;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ReportingPreferenceService;
import com.clicktable.service.intf.ReservationService;
import com.clicktable.service.intf.RestaurantService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.ReportingValidator;
import com.clicktable.validate.ReservationValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class ReportingPreferenceServiceImpl implements ReportingPreferenceService {

	@Autowired
	ReportingPreferenceDao reportingPreferenceDao;

	@Autowired
	ReservationService reservationService;

	@Autowired
	ReportingValidator reportingValidator;

	@Autowired
	AuthorizationService authService;

	@Autowired
	ReservationValidator reservationValidator;

	@Autowired
	RestaurantService restaurantService;

	@Override
	public BaseResponse getReportingPreferences(Map<String, Object> params) {
		BaseResponse response;
		Map<String, Object> finderParams = reportingValidator.validateFinderParams(params);
		List<ReportingPreference> prefrences = reportingPreferenceDao.findByCustomeFields(ReportingPreference.class, finderParams);
		response = new GetResponse<ReportingPreference>(ResponseCodes.PREFERENCE_FETCHED_SUCCESSFULLY, prefrences);
		return response;

	}

	@Override
	public BaseResponse addReportingPreferences(ReportingPreference prefrence, String token) {
		BaseResponse response;
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		List<ValidationError> listOfError = reportingValidator.validateReportingPrefernceOnCreate(prefrence, userInfo);
		if (listOfError.isEmpty()) {
			String guid = reportingPreferenceDao.addPrefernce(prefrence);
			response = new PostResponse<ReportingPreference>(ResponseCodes.PREFERENCE_CREATED_SUCCESSFULLY, guid);
		} else {
			response = new ErrorResponse(ResponseCodes.PREFERENCE_CREATION_FAILURE, listOfError);
		}
		return response;
	}

	@Override
	public BaseResponse updateReportingPreferences(ReportingPreference prefrence, String token) {
		BaseResponse response;
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		prefrence.setInfoOnUpdate(userInfo);
		List<ValidationError> listOfError = reportingValidator.validateReportingPrefernceOnUpdate(prefrence, userInfo);
		if (listOfError.isEmpty()) {
			HashMap<String, Object> finderParams = new HashMap<String, Object>();
			finderParams.put(Constants.GUID, prefrence.getGuid());
			List<ReportingPreference> preferences = reportingPreferenceDao.findByCustomeFields(ReportingPreference.class, finderParams);
			if (preferences.isEmpty()) {
				// errorList = CustomValidations.populateErrorList(errorList,
				// Constants.REPORTING_PREFERENCE_LABEL,
				// UtilityMethods.getErrorMsg(ErrorCodes.REPORTING_PREFERENCE_EXISTS),
				// ErrorCodes.REPORTING_PREFERENCE_EXISTS);
				listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GUID) + " " + prefrence.getGuid(),
						ErrorCodes.INVALID_GUID));
				response = new ErrorResponse(ResponseCodes.PREFERENCE_UPDATION_FAILURE, listOfError);
			} else {
				ReportingPreference existing = preferences.get(0);
				prefrence.copyExistingValues(existing);
				ReportingPreference preference = reportingPreferenceDao.update(prefrence);
				response = new UpdateResponse<ReportingPreference>(ResponseCodes.PREFERENCE_UPDATED_SUCCESSFULLY, preference.getGuid());
			}
		} else {
			response = new ErrorResponse(ResponseCodes.PREFERENCE_UPDATION_FAILURE, listOfError);
		}
		return response;
	}

}
