package com.clicktable.validate;

import java.util.List;

import com.clicktable.model.Language;
import com.clicktable.util.ErrorCodes;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class LanguageValidator extends EntityValidator<Language> 
{
	
	
	
	

	public List<ValidationError> validateLanguageOnAdd(Language language) {
		List<ValidationError> errorList = validateOnAdd(language);
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, language.getStatus(), language.getLanguageCode());

		
		return errorList;
	}

	/**
	 * validations on language at the time of updation
	 * 
	 * @param language
	 * @return
	 */

	public List<ValidationError> validateLanguageOnUpdate(Language language) {
		List<ValidationError> errorList = validateLanguageOnAdd(language);
		return errorList;
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
