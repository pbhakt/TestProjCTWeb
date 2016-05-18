package com.clicktable.validate;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.Logger;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

public class CustomValidations {
	// public boolean result;
	public Boolean isValidPageNo(Map<String, Object> params) {
		Boolean validPageNo = true;
		Integer pageNo = 0;
		try {

			pageNo = Integer.parseInt(params.get(Constants.PAGE_NO).toString());

			if (pageNo < 0) {
				validPageNo = false;
			}
		} catch (Exception e) {
			Logger.debug("Exception is ------------ " + e.getLocalizedMessage());
			
			validPageNo = false;
		}
		return validPageNo;
	}

	// private method to validate a string for a pattern
	private static boolean isValidPattern(String str, String pattern) {
		Pattern patternObj = Pattern.compile(pattern);
		Matcher matcher = patternObj.matcher(str);
		return matcher.matches();
	}

	// method to vaidate integer without decimals
	public static boolean isValidInteger(String str) {
		String pattern = "^[0-9]+$";
		return isValidPattern(str, pattern);
	}

	// method to vaidate number integer or double or long or float
	public static boolean isValidNumber(String str) {
		String pattern = "^[0-9]*(\\.)?[0-9]+$";
		return isValidPattern(str, pattern);
	}

	// method to check valid url
	public static boolean isValidUrl(String url) {
		String urlPattern = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		return isValidPattern(url, urlPattern);
	}

	/*
	 * //not complete public boolean isValidWebsite(String url) { String
	 * urlPattern =
	 * "/^(http(s?):\\/\\/)?(www\\.)+[a-zA-Z0-9\\.\\-\\_]+(\\.[a-zA-Z]{2,3})+(\\/[a-zA-Z0-9\\_\\-\\s\\.\\/\\?\\%\\#\\&\\=]*)?$/"
	 * ; return isValidPattern(url, urlPattern); //return false; }
	 */

	// method to check valid email
	public static boolean isValidEmail(String email) {
		String pattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		return isValidPattern(email, pattern);
		// return false;
	}

	// method to check valid Zip Code
	public static boolean isValidZipCode(String zipCode) {
		String pattern = "^\\d{5}((-|\\s)?\\d{4})?$";
		return isValidPattern(zipCode, pattern);
	}

	// method to check valid phone number
	public static boolean isValidPhoneNumber(String phoneNo) {
		// validate phone numbers of format "1234567890"
		if (phoneNo.matches("\\d{10}"))
			return true;
		// validate phone numbers of format "+919324832534"
		else if (phoneNo.matches("\\+\\d{12}"))
			return true;
		// validating phone number with -, . or spaces
		else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}"))
			return true;
		// validating phone number with extension length from 3 to 5
		else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))
			return true;
		// validating phone number where area code is in braces ()
		else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}"))
			return true;
		// return false if nothing matches the input
		else
			return false;

	}

	// method to populate errorlist

	public static List<ValidationError> populateErrorList(List<ValidationError> errorList, String fieldName, String errorMessage) 
	{
		ValidationError error = new ValidationError(fieldName, errorMessage);
		errorList.add(error);
		return errorList;
	}
	
	
	// method to populate errorlist new method with error code

	public static List<ValidationError> populateErrorList(List<ValidationError> errorList, String fieldName, String errorMessage, String errorCode)
	{
		ValidationError error = new ValidationError(fieldName, errorMessage, errorCode);
		errorList.add(error);
		return errorList;
	}

	// method to validate status and language code

	public static List<ValidationError> validateStatusAndLanguageCode(List<ValidationError> errorList, String status, String language_cd) {
		if ((!UtilityMethods.getEnumValues(Constants.COMMON_MODULE, Constants.STATUS).contains(status)) && (status != null)) {

			errorList = CustomValidations.populateErrorList(errorList, Constants.STATUS, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_STATUS),ErrorCodes.INVALID_STATUS);
		}

		if (!UtilityMethods.getEnumValues(Constants.COMMON_MODULE, Constants.LANG_CD).contains(language_cd) && (language_cd != null)) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.LANG_CD, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_LANG_CODE),ErrorCodes.INVALID_LANG_CODE);
		}

		return errorList;
	}

	public static List<ValidationError> validateLanguageCode(List<ValidationError> errorList, String language_cd) {
		if (!UtilityMethods.getEnumValues(Constants.COMMON_MODULE, Constants.LANG_CD).contains(language_cd) && (language_cd != null)) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.LANG_CD, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_LANG_CODE),ErrorCodes.INVALID_LANG_CODE);
		}
		return errorList;
	}

}
