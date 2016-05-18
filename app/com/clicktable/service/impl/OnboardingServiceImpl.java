package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.OnboardingDao;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.Onboarding;
import com.clicktable.repository.GuestProfileRepo;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.CustomerLoginService;
import com.clicktable.service.intf.NotificationService;
import com.clicktable.service.intf.OnboardingService;
import com.clicktable.service.intf.RestaurantService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.OnboardingValidator;
import com.clicktable.validate.ValidationError;

@Service
public class OnboardingServiceImpl implements OnboardingService {

	@Autowired
	OnboardingValidator onboardValidator;

	@Autowired
	CustomerDao customerDao;

	@Autowired
	CustomerLoginService customerService;

	@Autowired
	NotificationService notification;
	
	@Autowired
	OnboardingDao onboardDao;

	@Autowired
	GuestProfileRepo guestRepo;

	@Autowired
	RestaurantService restaurantService;

	@Override
	@Transactional
	public BaseResponse addOnboardingRequest(Onboarding onboard, String token) {
		BaseResponse response = null;
		if (onboard.getCreatedBy() == null) {
			// TODO change to guest login
			onboard.setCreatedBy(onboard.getGuid());
			onboard.setUpdatedBy(onboard.getGuid());
		}
		
		List<ValidationError> listOfError = onboardValidator.validateRequestOnAdd(onboard);
		if (!onboard.getReadConditions()) {
			listOfError = CustomValidations.populateErrorList(listOfError, Constants.READ_CONDITIONS, UtilityMethods.getErrorMsg(ErrorCodes.READ_TERMS_AND_CONDITIONS),
					ErrorCodes.READ_TERMS_AND_CONDITIONS);
		}

		if (listOfError.isEmpty()) {
			Onboarding newOnboarding = onboardDao.create(onboard);
			//auto-revert of signup
			ArrayList<String> to = new ArrayList<String>();
			to.add(newOnboarding.getEmail());
			ArrayList<String> tags = new ArrayList<String>();
			tags.add(Constants.ONBOARDING_MODULE);
			Map<String, String> templateContent = new java.util.HashMap<String, String>();
			templateContent.put(Constants.FIRST_NAME, newOnboarding.getFirstName());
			String templateName = Constants.SIGN_UP_MANDRILL_TEMPLATE_NAME;
			notification.sendEmail(to, tags, templateName, templateContent);
			//send notification to info
			ArrayList<String> notificationto = new ArrayList<String>();
			notificationto.add(UtilityMethods.getConfString(Constants.SUPPORT_USERNAME));
			templateName= Constants.SIGN_UP_NOTIFICATION_MANDRILL_TEMPLATE_NAME;
			templateContent.put(Constants.RESTAURANT_NAME, newOnboarding.getRestaurantName()+","+newOnboarding.getCity());
			notification.sendEmail(notificationto, tags, templateName, templateContent);
			response = new PostResponse<Onboarding>(ResponseCodes.ONBOARD_REQUEST_SUCCESFUL, newOnboarding.getGuid());
		} else {
			response = new ErrorResponse(ResponseCodes.ONBOARD_REQUEST_ADD_FAILURE, listOfError);
		}

		return response;
	}

	@Override
	@Transactional
	public BaseResponse updateOnboardingRequest(Onboarding onboard, String token) {
		BaseResponse response = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		if (onboard.getGuid() == null) {
			listOfError.add(onboardValidator.createError(Constants.GUID, ErrorCodes.ONBOARD_ID_REQUIRED));
		} else {
			Onboarding existing = onboardDao.find(onboard.getGuid());
			if (existing == null) {
				listOfError.add(onboardValidator.createError(Constants.GUID, ErrorCodes.INVALID_ONBOARD_ID));
			} else {
				onboard.copyExistingValues(existing);
				listOfError.addAll(onboardValidator.validateRequestOnUpdate(onboard));

				Onboarding newOnboarding = null;
				if (listOfError.isEmpty()) {
					if (!onboard.getReadConditions()) {
						listOfError = CustomValidations.populateErrorList(listOfError, Constants.READ_CONDITIONS, UtilityMethods.getErrorMsg(ErrorCodes.READ_TERMS_AND_CONDITIONS),
								ErrorCodes.READ_TERMS_AND_CONDITIONS);
						response = new ErrorResponse(ResponseCodes.ONBOARD_REQUEST_UPDATE_FAILURE, listOfError);
					} else if (!(existing.getRequestStatus().equals(Constants.REJECTED)) && (onboard.getRequestStatus().equals(Constants.REJECTED))) {
						if (onboard.getReasonToReject() == null || onboard.getReasonToReject().equals("")) {
							listOfError = CustomValidations.populateErrorList(listOfError, Constants.REASON_TO_REJECT, UtilityMethods.getErrorMsg(ErrorCodes.REASON_TO_REJECT_REQUIRED),
									ErrorCodes.REASON_TO_REJECT_REQUIRED);
							response = new ErrorResponse(ResponseCodes.ONBOARD_REQUEST_UPDATE_FAILURE, listOfError);
						}
					}
					if (response == null) {
						newOnboarding = onboardDao.update(onboard);
						/* Add Dummy Guest to Onboard Request */
						if (!(existing.getRequestStatus().equals(Constants.APPROVED)) && (onboard.getRequestStatus().equals(Constants.APPROVED))) { // Check
							ArrayList<String> to = new ArrayList<String>();
							to.add(newOnboarding.getEmail());
							ArrayList<String> tags = new ArrayList<String>();
							tags.add(Constants.ONBOARDING_MODULE);
							tags.add(Constants.APPROVED);
							Map<String, String> templateContent = new java.util.HashMap<String, String>();
							templateContent.put(Constants.FIRST_NAME, newOnboarding.getFirstName());
							String templateName = Constants.SIGN_UP_APPROVED_MANDRILL_TEMPLATE_NAME;
							notification.sendEmail(to, tags, templateName, templateContent);							
							System.out.println("OnboardingServiceImpl.updateOnboardingRequest()----126");
							if (onboard.getReasonToReject() != null)
								onboard.setReasonToReject(null);
							response = restaurantService.addRestaurant(onboard, token);
							System.out.println("OnboardingServiceImpl.updateOnboardingRequest()----130");
							if (response != null && response.getResponseStatus()) {
								System.out.println("OnboardingServiceImpl.updateOnboardingRequest()----132");
								Object[] rest_Guid = ((PostResponse) response).getGuid();
								onboard.setRestGuid(rest_Guid[0].toString());
								newOnboarding = onboardDao.update(onboard);
								/* Adding Dummy guest with Restaurant */

								GuestProfile guest = new GuestProfile();
								guest.setFirstName(Constants.DUMMY_FIRSTNAME);
								//guest.setLastName(Constants.DUMMY_LASTNAME);
								guest.setEmailId(Constants.DUMMY_EMAIL);
								guest.setMobile(Constants.DUMMY_MOBILE);
								guest.setIsVip(Constants.DUMMY_IsVIP);
								guest.setGender(Constants.DUMMY_GENDER);
								guest.setGuestType(Constants.DUMMY_FIRSTNAME);
								Object[] restGuid=((PostResponse)response).getGuid();
								guest.setRestGuid(restGuid[0].toString());
								guest.setGuid(UtilityMethods.generateCtId());
								guest.setDummy(true);
								customerService.addCustomer(guest, token);

								System.out.println("Restaurant Name :-------" + guest.getRestGuid());

							}
						} else if (!(existing.getRequestStatus().equals(Constants.REJECTED)) && (onboard.getRequestStatus().equals(Constants.REJECTED))) { // Check
							ArrayList<String> to = new ArrayList<String>();
							to.add(newOnboarding.getEmail());
							ArrayList<String> tags = new ArrayList<String>();
							tags.add(Constants.ONBOARDING_MODULE);
							tags.add(Constants.REJECTED);
							Map<String, String> templateContent = new java.util.HashMap<String, String>();
							templateContent.put(Constants.FIRST_NAME, newOnboarding.getFirstName());
							templateContent.put(Constants.REASON_TO_REJECT, newOnboarding.getReasonToReject());
							String templateName = Constants.SIGN_UP_REJECT_MANDRILL_TEMPLATE_NAME;
							notification.sendEmail(to, tags, templateName, templateContent);
						}
						response = new UpdateResponse<Onboarding>(ResponseCodes.ONBOARD_REQUEST_UPDATE_SUCCESFUL, newOnboarding.getGuid());
					}
					return response;
				}
			}
		}
		response = new ErrorResponse(ResponseCodes.ONBOARD_REQUEST_UPDATE_FAILURE, listOfError);
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public BaseResponse getOnboardingRequests(Map<String, Object> params) {
		BaseResponse getResponse;// = new GetResponse<Onboarding>();
		Map<String, Object> qryParamMap = onboardValidator.validateFinderParams(params, Onboarding.class);
		List<Onboarding> onboardList = onboardDao.findByFields(Onboarding.class, qryParamMap);
		getResponse = new GetResponse<Onboarding>(ResponseCodes.ONBOARD_RECORD_FETCH_SUCCESFULLY, onboardList);
		return getResponse;
	}

	@Override
	@Transactional
	public BaseResponse onboardingVerification(Map<String, String> params, String userId) {
		BaseResponse response = null;
		String onboardGuid = params.get(Constants.GUID);
		String code = params.get(Constants.VERIFICATION_CODE);
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if (onboardGuid == null) {
			listOfError.add(onboardValidator.createError(Constants.GUID, ErrorCodes.ONBOARD_ID_REQUIRED));
			response = new ErrorResponse(ResponseCodes.CODE_VERIFICATION_FAILURE, listOfError);
			return response;
		} else {
			Onboarding existing = onboardDao.find(onboardGuid);
			if (existing == null) {
				listOfError.add(onboardValidator.createError(Constants.GUID, ErrorCodes.INVALID_ONBOARD_ID));
				response = new ErrorResponse(ResponseCodes.CODE_VERIFICATION_FAILURE, listOfError);
				return response;
			} else {
				listOfError = new ArrayList<>();
				if ((code == null) || ( (code.trim().equals("")))) {
					listOfError.add(onboardValidator.createError(Constants.VERIFICATION_CODE, ErrorCodes.VERIFICATION_CODE_REQUIRED));
				}
				Onboarding newOnboarding = null;
				if (listOfError.isEmpty()) {

					// verify code
					Boolean isVerified = UtilityMethods.verifyCode(code);

					Logger.debug("isVerified===== " + isVerified);
					existing.setIsVerified(isVerified);
					// existing.setVerificationCode(code);
					existing.setUpdatedBy(userId);

					newOnboarding = onboardDao.update(existing);
					Logger.debug("onboarding updated " + newOnboarding.getGuid());

					if (isVerified) {
						response = new PostResponse<Onboarding>(ResponseCodes.CODE_VERIFIED_SUCCESFULLY, "");
					} else {
						response = new PostResponse<Onboarding>(ResponseCodes.CODE_VERIFICATION_FAILURE, "");
					}

				} else {
					response = new ErrorResponse(ResponseCodes.CODE_VERIFICATION_FAILURE, listOfError);
				}
			}
		}
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public BaseResponse resendCode(String onboardGuid) {
		BaseResponse response = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if (onboardGuid == null) {
			listOfError.add(onboardValidator.createError(Constants.GUID, ErrorCodes.ONBOARD_ID_REQUIRED));
			response = new ErrorResponse(ResponseCodes.CODE_RESEND_FAILURE, listOfError);
			return response;
		} else {
			Onboarding existing = onboardDao.find(onboardGuid);
			if (existing == null) {
				listOfError.add(onboardValidator.createError(Constants.GUID, ErrorCodes.INVALID_ONBOARD_ID));
				response = new ErrorResponse(ResponseCodes.CODE_RESEND_FAILURE, listOfError);
				return response;
			} else {
				Boolean resendCode = UtilityMethods.resendCode();
				if (resendCode) {
					response = new PostResponse<Onboarding>(ResponseCodes.CODE_RESEND_SUCCESFULLY, "");
				} else {
					response = new ErrorResponse(ResponseCodes.CODE_RESEND_FAILURE, listOfError);
				}
			}
		}
		return response;
	}

}
