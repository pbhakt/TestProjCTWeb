package com.clicktable.validate;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.libs.Json;

import com.clicktable.dao.intf.OnboardingDao;
import com.clicktable.model.Onboarding;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@Service
public class OnboardingValidator extends EntityValidator<Onboarding> {
	@Autowired
	OnboardingDao onboardingDao;

	/**
	 * {@inheritDoc}
	 */
	public List<ValidationError> validateRequestOnAdd(Onboarding onboard) {
		List<ValidationError> errorList = validateOnAdd(onboard);
		errorList.forEach(x->System.out.println(x));
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, onboard.getStatus(), onboard.getLanguageCode());
		List<Onboarding> objectsByName = onboardingDao.listOfDuplicateOnboarding(onboard.getRestaurantName(), onboard.getEmail());
		System.out.println(Json.toJson(objectsByName));
		
		if (!objectsByName.isEmpty()) {
			for (Onboarding onboarding : objectsByName) {	
				
				if (onboarding.getEmail().toLowerCase().equalsIgnoreCase(onboard.getEmail().toLowerCase())) {
					System.out.println("onboarding.getEmail().equalsIgnoreCase(onboard.getEmail())==="+onboarding.getEmail().equalsIgnoreCase(onboard.getEmail()));
					errorList = CustomValidations.populateErrorList(errorList, Constants.EMAIL, UtilityMethods.getErrorMsg(ErrorCodes.EMAIL_ALREADY_EXIST),ErrorCodes.EMAIL_ALREADY_EXIST);
				}
				/*if (onboarding.getRestaurantName().equals(onboard.getRestaurantName())) {
					errorList = CustomValidations.populateErrorList(errorList, Constants.RESTAURANT_NAME, UtilityMethods.getErrorMsg(ErrorCodes.RESTAURANT_NAME_ALREADY_EXSIT),ErrorCodes.RESTAURANT_NAME_ALREADY_EXSIT);
				}*/
			}
		}
		errorList.addAll(validateEnumValues(onboard, Constants.ONBOARDING_MODULE));
		return errorList;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ValidationError> validateRequestOnUpdate(Onboarding onboard) {
		List<ValidationError> errorList = validateOnAdd(onboard);
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, onboard.getStatus(), onboard.getLanguageCode());
		List<Onboarding> objectsByName = onboardingDao.listOfOtherDuplicateOnboarding(onboard.getRestaurantName(), onboard.getEmail(), onboard.getId());
		if (!objectsByName.isEmpty()) {
			for (Onboarding onboarding : objectsByName) {
				if (onboarding.getEmail().equals(onboard.getEmail()))
					errorList = CustomValidations.populateErrorList(errorList, Constants.EMAIL, UtilityMethods.getErrorMsg(ErrorCodes.EMAIL_ALREADY_EXIST),ErrorCodes.EMAIL_ALREADY_EXIST);
				if (onboarding.getRestaurantName().equals(onboard.getRestaurantName()))
					errorList = CustomValidations.populateErrorList(errorList, Constants.RESTAURANT_NAME, UtilityMethods.getErrorMsg(ErrorCodes.RESTAURANT_NAME_ALREADY_EXSIT),ErrorCodes.RESTAURANT_NAME_ALREADY_EXSIT);
			}
		}

		if (!UtilityMethods.getEnumValues(Constants.ONBOARDING_MODULE, Constants.REQUEST_STATUS).contains(onboard.getRequestStatus()) && onboard.getRequestStatus() != null) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.REQUEST_STATUS, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REQUEST_STATUS),ErrorCodes.INVALID_REQUEST_STATUS);
		}
		return errorList;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.ONBOARD_ID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_ONBOARD_ID;
	}
}
