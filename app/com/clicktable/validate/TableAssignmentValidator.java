package com.clicktable.validate;


import java.util.List;

import com.clicktable.model.TableAssignment;
import com.clicktable.util.ErrorCodes;

@org.springframework.stereotype.Service
public class TableAssignmentValidator extends EntityValidator<TableAssignment> {
	
	

	public List<ValidationError> validateTableAssignmentOnAdd(TableAssignment tableAssign) 
	{
		List<ValidationError> errorList = validateOnAdd(tableAssign);
		/*errorList = CustomValidations.validateStatusAndLanguageCode(errorList, table.getStatus(), table.getLanguageCode());
		errorList.addAll(validateEnumValues(table, Constants.TABLE_MODULE));
		if (table.getMinCovers() != null && table.getMaxCovers() != null)
			if (table.getMinCovers() > table.getMaxCovers()) {
				errorList = CustomValidations.populateErrorList(errorList, Constants.MIN_COVERS, UtilityMethods.getErrorMsg(ErrorCodes.MIN_GREATER_THEN_MAX));
			}*/
		return errorList;
	}

	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.TBL_ASSIGNMENT_TBL_GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_GUID;
	}


}
