
package com.clicktable.validate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;

import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.Restaurant;
import com.clicktable.repository.GuestProfileRepo;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class CustomerValidator extends EntityValidator<GuestProfile> {

	@Autowired
	CustomerDao guestDao;

	@Autowired
	GuestProfileRepo guestRepo;

	/**
	 * {@inheritDoc}
	 */
	public List<ValidationError> validateCustomerOnAdd(GuestProfile customer) {

		List<ValidationError> errorList = validateOnAdd(customer);
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, customer.getStatus(), customer.getLanguageCode());
		if ((customer.getIsVip() != null) && (customer.getIsVip())) {
			if ((customer.getReason() == null) || (customer.getReason().equals(""))) {
				Logger.debug("vip reason required");
				errorList = CustomValidations.populateErrorList(errorList, Constants.VIP_REASON, UtilityMethods.getErrorMsg(ErrorCodes.VIP_REASON_REQUIRED), ErrorCodes.VIP_REASON_REQUIRED);
			}

			if (((customer.getReason() != null) && (!customer.getReason().equals("")))
					&& (!UtilityMethods.getEnumValues(Constants.CUSTOMER_MODULE, Constants.VIP_REASON).contains(customer.getReason()))) {
				errorList = CustomValidations.populateErrorList(errorList, Constants.VIP_REASON, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_VIP_REASON), ErrorCodes.INVALID_VIP_REASON);
			}

		}

		if ((customer.getGender() != null) && (!customer.getGender().equals("")) && (!UtilityMethods.getEnumValues(Constants.CUSTOMER_MODULE, Constants.GENDER).contains(customer.getGender()))) {

			errorList = CustomValidations.populateErrorList(errorList, Constants.GENDER, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GENDER), ErrorCodes.INVALID_GENDER);
		}

		if ((customer.getMobile() == null) || (customer.getMobile().equals(""))) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.MOBILE, UtilityMethods.getErrorMsg(ErrorCodes.CUST_MOBILE_REQUIRED), ErrorCodes.CUST_MOBILE_REQUIRED);
		}

		if ((customer.getMobile() != null) && (!UtilityMethods.isValidNumericNumber(customer.getMobile()))) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.MOBILE, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_MOBILE_NO), ErrorCodes.INVALID_MOBILE_NO);
		}

		if ((customer.getMobile() == "0000000000") && (!customer.isDummy())) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.MOBILE, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_MOBILE_NO), ErrorCodes.INVALID_MOBILE_NO);
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		SimpleDateFormat extFormat = new SimpleDateFormat(Constants.EXT_TIMESTAMP_FORMAT);

		Date currentDate = new Date();

		// if dob is not null then check for valid date and dob is not after
		// current date
		if (customer.getDob() != null) {
			Logger.debug("dob is " + new Date(customer.getDob().getTime()).toString());
			try {
				Logger.debug("date format after parse is " + dateFormat.format(extFormat.parse(customer.getDob().toString())));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Logger.debug("formatted date is " + dateFormat.format(new Date(customer.getDob().getTime())).toString());
			Logger.debug("time is " + customer.getDob().getTime() + " dob is " + customer.getDob());
			Logger.debug("day is " + customer.getDob().getDate() + " mmonth is " + customer.getDob().getMonth() + " year is " + customer.getDob().getYear());
			if ((!UtilityMethods.isValidDate(dateFormat.format(new Date(customer.getDob().getTime())).toString(), dateFormat))) {
				Logger.debug("dob is " + dateFormat.format(customer.getDob()));
				errorList = CustomValidations.populateErrorList(errorList, Constants.DOB, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_DATE_FORMAT), ErrorCodes.INVALID_DATE_FORMAT);
			}

			if (customer.getDob().after(currentDate)) {
				Logger.debug("dob cannot be after current date");
				errorList = CustomValidations.populateErrorList(errorList, Constants.DOB, UtilityMethods.getErrorMsg(ErrorCodes.DOB_AFTER_CURRENT_DATE), ErrorCodes.DOB_AFTER_CURRENT_DATE);
			}

		}

		// if anniversary is not null then check for valid date and anniversary
		// is not after current date
		if (customer.getAnniversary() != null) {
			if ((!UtilityMethods.isValidDate(dateFormat.format(new Date(customer.getAnniversary().getTime())).toString(), dateFormat))) {
				Logger.debug("anniversary is " + dateFormat.format(customer.getAnniversary()));
				errorList = CustomValidations.populateErrorList(errorList, Constants.ANNIVERSARY, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_DATE_FORMAT), ErrorCodes.INVALID_DATE_FORMAT);
			}

			if (customer.getAnniversary().after(currentDate)) {
				Logger.debug("anniversary cannot be after current date");
				errorList = CustomValidations.populateErrorList(errorList, Constants.ANNIVERSARY, UtilityMethods.getErrorMsg(ErrorCodes.ANNIVERSARY_AFTER_CURRENT_DATE),
						ErrorCodes.ANNIVERSARY_AFTER_CURRENT_DATE);
			}
		}

		// if dob and anniversary both are not null then check whether dob is
		// before anniversary
		if ((customer.getAnniversary() != null) && (customer.getDob() != null) && (customer.getDob().after(customer.getAnniversary()))) {
			Logger.debug("dob cannot be after anniversary");
			errorList = CustomValidations.populateErrorList(errorList, Constants.ANNIVERSARY, UtilityMethods.getErrorMsg(ErrorCodes.DOB_AFTER_ANNIVERSARY), ErrorCodes.DOB_AFTER_ANNIVERSARY);
		}

		return errorList;

	}
	
	
	public List<ValidationError> validateCSVCustomerOnAdd(GuestProfile customer) {
		List<ValidationError> errorList = validateOnAdd(customer);
		//errorList = CustomValidations.validateStatusAndLanguageCode(errorList, customer.getStatus(), customer.getLanguageCode());
		if ((customer.getIsVip() != null) && (customer.getIsVip())) {
			if ((customer.getReason() == null) || (customer.getReason().equals(""))) {
				Logger.debug("vip reason required");
				errorList = CustomValidations.populateErrorList(errorList, Constants.VIP_REASON, UtilityMethods.getErrorMsg(ErrorCodes.VIP_REASON_REQUIRED), ErrorCodes.VIP_REASON_REQUIRED);
			}

			if (((customer.getReason() != null) && (!customer.getReason().equals("")))
					&& (!UtilityMethods.getEnumValues(Constants.CUSTOMER_MODULE, Constants.VIP_REASON).contains(customer.getReason()))) {
				errorList = CustomValidations.populateErrorList(errorList, Constants.VIP_REASON, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_VIP_REASON), ErrorCodes.INVALID_VIP_REASON);
			}

		}

		if ((customer.getGender() != null) && (!customer.getGender().equals("")) && (!UtilityMethods.getEnumValues(Constants.CUSTOMER_MODULE, Constants.GENDER).contains(customer.getGender()))) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.GENDER, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GENDER), ErrorCodes.INVALID_GENDER);
		}

		if ((customer.getMobile() == null) || (customer.getMobile().equals(""))) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.MOBILE, UtilityMethods.getErrorMsg(ErrorCodes.CUST_MOBILE_REQUIRED), ErrorCodes.CUST_MOBILE_REQUIRED);
		}

		if ((customer.getMobile() != null) && (!UtilityMethods.isValidNumericNumber(customer.getMobile()))) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.MOBILE, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_MOBILE_NO), ErrorCodes.INVALID_MOBILE_NO);
		}

		if ((customer.getMobile() == "0000000000") && (!customer.isDummy())) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.MOBILE, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_MOBILE_NO), ErrorCodes.INVALID_MOBILE_NO);
		}
		return errorList;

	}
	
	
	

	/**
	 * {@inheritDoc}
	 */
	public List<ValidationError> validateCustomerOnUpdate(GuestProfile customer) {
		List<ValidationError> errorList = validateCustomerOnAdd(customer);
		return errorList;
	}


	public GuestProfile validateGuest(String guestGuid, List<ValidationError> listOfError) {
		GuestProfile guest = null;
		if (guestGuid == null)
			listOfError.add(new ValidationError(Constants.GUEST_ID, UtilityMethods.getErrorMsg(ErrorCodes.CUST_ID_REQUIRED), ErrorCodes.CUST_ID_REQUIRED));
		if (listOfError.isEmpty()) {
			guest = guestRepo.findByguid(guestGuid);
			if (guest == null)
				listOfError.add(new ValidationError(Constants.GUEST_ID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_CUST_ID) + " " + guestGuid, ErrorCodes.INVALID_CUST_ID));
		}
		return guest;
	}

	public Restaurant validateGuestOfRestaurant(String guestGuid, String restGuidForLoggedInUser, List<ValidationError> listOfError) {
		boolean matched = false;
		Restaurant restaurant = null;
		List<Restaurant> restList = guestRepo.getRestByGuestguid(guestGuid);
		for (Restaurant rest : restList) {
			if (rest.getGuid().equals(restGuidForLoggedInUser)) {
				matched = true;
				restaurant = rest;
				break;
			}
		}
		if (!matched) {
			listOfError.add(new ValidationError(Constants.GUEST_ID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID), ErrorCodes.INVALID_GUEST_GUID));
		}
		return restaurant;

	}

	/**
	 * validations on staff(new staff can be changed or updated by staff member
	 * of that restaurant for which staff is being created or updated or by ct
	 * admin)
	 * 
	 * @param staff
	 * @return
	 */
	public List<ValidationError> validateCustomerForRestaurant(GuestProfile customer, String restGuidForLoggedInStaff) {
		Logger.debug("validating customer for restaurant");
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		Logger.debug("customer.getRestGuid() =" + customer.getRestGuid());
		if (!customer.getRestGuid().equals(restGuidForLoggedInStaff)) {
			errorList.add(createError(Constants.RESTGUID, ErrorCodes.NO_ACCESS_TO_CREATE_OR_UPDATE_STAFF_OF_OTHER_REST));
			Logger.debug("rest guid from customer=" + customer.getRestGuid() + " is not equal to rest guid for staff=" + restGuidForLoggedInStaff);
		}
		return errorList;
	}

	public List<ValidationError> validateCSVHeaders(List<String> headers) {
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		GuestProfile dummy = new GuestProfile();// dummy object
		dummy.setCreatedBy(UtilityMethods.generateCtId());
		dummy.setUpdatedBy(UtilityMethods.generateCtId());
		dummy.setGuid(UtilityMethods.generateCtId());
		dummy.setIsVip(false);
		dummy.setRestGuid(UtilityMethods.generateCtId());
		List<String> requiredFields = requiredFields(dummy);

		if (!headers.containsAll(requiredFields)) {
			String errorMsg = "CSV must contains these feilds :" + requiredFields;
			System.out.println(errorMsg);
			listOfError.add(new ValidationError(Constants.HEADERS, errorMsg, ErrorCodes.HEADERS_MISSING));
		}
		return listOfError;
	}

	public GuestProfile getDummyGuest() {
		Map<String, Object> params = new HashMap<String, Object>();
		GuestProfile guestProfile = null;
		params.put(Constants.FIRSTNAME, Constants.DUMMY_FIRSTNAME );
		//params.put(Constants.LASTNAME, Constants.DUMMY_LASTNAME);
		params.put(Constants.EMAIL_ID, Constants.DUMMY_EMAIL);
		params.put(Constants.MOBILE, Constants.DUMMY_MOBILE);

		List<GuestProfile> guestList = guestDao.findByFields(GuestProfile.class, params);
		if (!guestList.isEmpty()) {
			guestProfile = guestList.get(0);
		}
		return guestProfile;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.CUST_ID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_CUST_ID;
	}
	
	public List<ValidationError> validateFileFormat(String fileFormat, List<ValidationError> errorList){
		if ((!UtilityMethods.getEnumValues(Constants.REPORTS, Constants.FILE_FORMAT).contains(fileFormat))) {
			errorList.add(new ValidationError(Constants.FILE_FORMAT, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_FILE_FORMAT), ErrorCodes.INVALID_FILE_FORMAT));
		}
		return errorList;
	}
	
}
