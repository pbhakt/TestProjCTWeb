package com.clicktable.validate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.CuisineDao;
import com.clicktable.model.Cuisine;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;

@org.springframework.stereotype.Service
public class CuisineValidator extends EntityValidator<Cuisine> {

	@Autowired
	CuisineDao cuisineDao;
	/**
	 * {@inheritDoc}
	 */
	public List<ValidationError> validateCuisineOnAdd(Cuisine cuisine) {
		List<ValidationError> errorList = validateOnAdd(cuisine);		
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, cuisine.getStatus(), cuisine.getLanguageCode());
		return errorList;
	}

	public Cuisine validateCuisineOnUpdate(Cuisine cuisine, List<ValidationError> listOfError) {

		Cuisine getCuisine = validateGuid(cuisine.getGuid(), listOfError);
		if (listOfError.isEmpty()) {
			if (cuisineDao.otherCuisineWithSameNameExists(cuisine.getName(), getCuisine.getId())) {
				listOfError.add(createError(Constants.CUISINE_MODULE, ErrorCodes.DUPLICATE_CUISINE_NAME));
			}
			cuisine.copyExistingValues(getCuisine);
			listOfError.addAll(validateCuisineOnAdd(cuisine));
		}

		return cuisine;
	}

	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.CUISINE_ID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_CUISINE_ID;
	}
}
