package com.clicktable.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;

import com.clicktable.dao.intf.UserTokenDao;
import com.clicktable.model.Staff;
import com.clicktable.model.UserToken;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class StaffValidator extends EntityValidator<Staff> {

	/**
	 * validations on staff at the time of addition
	 * 
	 * @param staff
	 * @return
	 */
	
	@Autowired
	UserTokenDao userTokenDao;
	
	public List<ValidationError> validateStaffOnAdd(Staff staff) 
	{
	        Logger.debug("validating staff");
	        List<ValidationError> errorList = validateOnAdd(staff);
		
		//validate status and language code
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, staff.getStatus(), staff.getLanguageCode());
		
		//restaurant guid can be null for ct admin
		if((!staff.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && ((staff.getRestaurantGuid() == null) || (staff.getRestaurantGuid().equals(""))))
		{
		    Logger.debug("validating restaurant guid ");
		    errorList = CustomValidations.populateErrorList(errorList, Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.REST_ID_REQUIRED),ErrorCodes.REST_ID_REQUIRED);
		    Logger.debug("error list is "+errorList);
		}
		
		if(null!=staff.getMobileNo() && !UtilityMethods.isValidNumericNumber(staff.getMobileNo()))
		{
		    Logger.debug("validating mobile no ");
		    errorList = CustomValidations.populateErrorList(errorList, Constants.MOBILE, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_MOBILE_NO),ErrorCodes.INVALID_MOBILE_NO);
		    Logger.debug("error list is "+errorList);
        }
		
		//allowed values for staff role id are 1,2,3,4 and 5
		if((staff.getRoleId()!=null) && ((!UtilityMethods.isValidNumericNumber(staff.getRoleId().toString())) || (staff.getRoleId() > Constants.SERVER_ROLE_ID) || (staff.getRoleId() < Constants.CT_ADMIN_ROLE_ID)))
		{
		    Logger.debug("validating role ");
		    errorList = CustomValidations.populateErrorList(errorList, Constants.ROLE_ID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_ROLE_ID),ErrorCodes.INVALID_ROLE_ID);
		}


		Logger.debug("error list is "+errorList);
		return errorList;
	}

	/**
	 * validations on staff at the time of updation
	 * 
	 * @param staff
	 * @return
	 */
	public List<ValidationError> validateStaffOnUpdate(Staff staff) 
	{
		List<ValidationError> errorList = validateStaffOnAdd(staff);
		return errorList;
	}
	
	
	
	
	
	/**
	 * validations on staff(new staff can be changed or updated by staff member of that restaurant for which staff is being created or updated or by ct admin)
	 * 
	 * @param staff
	 * @return
	 */
	public List<ValidationError> validateStaffForRestaurant(String restGuid, String restGuidForLoggedInStaff) 
	{
	        Logger.debug("validating staff for restaurant");
	        List<ValidationError> errorList = new ArrayList<ValidationError>();
	        if(!restGuid.equals(restGuidForLoggedInStaff))
		    {
			errorList.add(createError(Constants.REST_ID, ErrorCodes.NO_ACCESS_TO_CREATE_OR_UPDATE_STAFF_OF_OTHER_REST));
			
		    }
		return errorList;
	}

	public List<ValidationError> validateUserToken(UserToken user_token) {
		List<ValidationError> listOfError = new ArrayList<>();
		Map<String, Object> params=new HashMap<String,Object>();
		params.put("token",user_token.getToken());
		params.put("user_id",user_token.getUserId());
		List<UserToken> token_list = userTokenDao.findByFields(params);
		if(token_list.size()>0)
			listOfError.add(createError(Constants.TOKEN, ErrorCodes.TOKEN_ALREADY_EXISTS));
		return listOfError;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.STAFF_ID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_STAFF_ID;
	}

	public List<ValidationError> validateStaffOnPatchUpdate(
			Staff staff) {
		//validate status
		return validateEnumValues(staff, Constants.COMMON_MODULE);
	}

}
