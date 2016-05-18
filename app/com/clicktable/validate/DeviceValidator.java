package com.clicktable.validate;

import java.util.List;

import com.clicktable.model.Device;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class DeviceValidator extends EntityValidator<Device> 
{	

	public List<ValidationError> validateDeviceOnAdd(Device device) {
		List<ValidationError> errorList = validateOnAdd(device);
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, device.getStatus(), device.getLanguageCode());
		
		if ((device.getType() != null) && (!UtilityMethods.getEnumValues(Constants.DEVICE_MODULE, Constants.DEVICE_TYPE).contains(device.getType()))) 
	        {

			errorList = CustomValidations.populateErrorList(errorList, Constants.DEVICE_TYPE, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_DEVICE_TYPE),ErrorCodes.INVALID_DEVICE_TYPE);
		}

		
		return errorList;
	}

	/**
	 * validations on device at the time of updation
	 * 
	 * @param device
	 * @return
	 */

	public List<ValidationError> validateDeviceOnUpdate(Device device) {
		List<ValidationError> errorList = validateDeviceOnAdd(device);
		return errorList;
	}
	
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.DEVICE_GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_DEVICE_GUID;
	}
	     
	
	


}
