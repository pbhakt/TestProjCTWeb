package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.clicktable.dao.intf.CorporateOffersDao;
import com.clicktable.dao.intf.CorporationDao;
import com.clicktable.model.CorporateOffers;
import com.clicktable.model.Corporation;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CorporationService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CorporationValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class CorporationServiceImpl implements CorporationService {

	@Autowired
	CorporationDao corporationDao;

	@Autowired
	CorporateOffersDao corporateoffersDao;

	@Autowired
	AuthorizationService authService;

	@Autowired
	CorporationValidator corporationValidator;

	@Override
	@Transactional(readOnly = true)
	public BaseResponse getCorporation(Map<String, Object> params) {
		BaseResponse getResponse;
		if (!params.containsKey(Constants.STATUS)) // default must be to get all
													// nodes with any status
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		// what in case of multiple status?
		List<Corporation> corporationList = corporationDao.findCorporations(params);
		getResponse = new GetResponse<Corporation>(ResponseCodes.CORPORATION_RECORD_FETCH_SUCCESFULLY, corporationList);
		return getResponse;
	}

	@Override
	@Transactional
	public BaseResponse addCorporation(Corporation corporation, String token) {

		BaseResponse response;
		corporation.setGuid(UtilityMethods.generateCtId());
		corporation.setStatus(Constants.ACTIVE_STATUS);
		List<ValidationError> listOfError = corporationValidator.validateCorporationOnAdd(corporation);
		if (listOfError.isEmpty()) {

			/*
			 * Corporation corp = corporationDao.create(corporation); String
			 * corporationGuid = corp.getGuid();
			 */

			String corporationGuid = corporationDao.addCorporation(corporation);
			response = new PostResponse<Corporation>(ResponseCodes.CORPORATION_ADDED_SUCCESFULLY, corporationGuid);
			return response;
		}

		return new ErrorResponse(ResponseCodes.CORPORATION_ADDITION_FAILURE, listOfError);
	}

	/*
	 * @Override public BaseResponse deleteCorporation(Corporation corporation,
	 * String header) { BaseResponse response; List<ValidationError> listOfError
	 * = new ArrayList<ValidationError>(); Corporation existing =
	 * corporationValidator.validateCorporation(corporation, listOfError);
	 * if(listOfError.isEmpty()) { Corporation corporationToUpdate = existing;
	 * corporationToUpdate.setStatus(Constants.DELETED_STATUS);
	 * listOfError.addAll
	 * (CustomValidations.validateStatusAndLanguageCode(listOfError,
	 * corporation.getStatus(), null)); if(listOfError.isEmpty()) { Corporation
	 * updated=corporationDao.update(corporationToUpdate);
	 * 
	 * response = new
	 * UpdateResponse<>(ResponseCodes.CORPORATION_DELETED_SUCCESFULLY,
	 * updated.getGuid()); return response;
	 * 
	 * } } return new ErrorResponse(ResponseCodes.CORPORATION_DELETION_FAILURE,
	 * listOfError); }
	 */

	@Override
	public BaseResponse updateCorporation(Corporation corporation, String token) {
		BaseResponse response = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Corporation existing = null;
		if (corporation.getGuid() == null) {
			listOfError.add(corporationValidator.createError(Constants.GUID, ErrorCodes.GUID_REQUIRED));
		} else {
			existing = corporationDao.find(corporation.getGuid());
			if (existing == null) {
				listOfError.add(corporationValidator.createError(Constants.GUID, ErrorCodes.INVALID_CORPORATION_GUID));
			} else {
				if (!corporation.getName().equals(existing.getName()))
					existing.setName(corporation.getName());
				if (!corporation.getWebsite().equals(existing.getWebsite()))
					existing.setWebsite(corporation.getWebsite());
				if (listOfError.isEmpty()) {
					corporation = new Corporation(existing);
					listOfError = (corporationValidator.validateCorporationOnUpdate(corporation, listOfError));
				}
			}

		}

		if (listOfError.isEmpty()) {
			Corporation updated = corporationDao.updateCorporation(corporation, corporationDao.find(corporation.getGuid()));
			response = new UpdateResponse<>(ResponseCodes.CORPORATION_UPDATED_SUCCESFULLY, updated.getGuid());
			return response;
		}
		return new ErrorResponse(ResponseCodes.CORPORATION_UPDATION_FAILURE, listOfError);
	}

	@Override
	public BaseResponse getCorporateOffers(Map<String, Object> params, String token) {
		BaseResponse getResponse;
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
			params.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		List<CorporateOffers> corporateOffersList = corporateoffersDao.findCorporateOffers(params);
		getResponse = new GetResponse<CorporateOffers>(ResponseCodes.CORPORATE_OFFERS_RECORD_FETCH_SUCCESFULLY, corporateOffersList);
		return getResponse;
	}

	@Override
	public BaseResponse addCorporateOffers(CorporateOffers corporate_offers, Map<String, Object> params, String token) {
		BaseResponse response;
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		corporate_offers.setGuid(UtilityMethods.generateCtId());
		corporate_offers.setStatus(Constants.ACTIVE_STATUS);
		listOfError.addAll(corporationValidator.validateCorporateOffersOnAdd(corporate_offers));
		if (listOfError.isEmpty()) {
			String corporationGuid = corporateoffersDao.addCorporateOffers(corporate_offers);
			response = new PostResponse<Corporation>(ResponseCodes.CORPORATE_OFFERS_ADDED_SUCCESFULLY, corporationGuid);
			return response;
		}

		return new ErrorResponse(ResponseCodes.CORPORATE_OFFERS_ADDITION_FAILURE, listOfError);
	}

	@Override
	public BaseResponse updateCorporateOffers(CorporateOffers corporate_offers, Map<String, Object> params, String token) {
		BaseResponse response = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		UserInfoModel userInfo = authService.getUserInfoByToken(token);

		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID) && (!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID))) { // no-need
			if (!corporate_offers.getRestaurantGuid().equals(
					userInfo.getRestGuid())) {
				listOfError.add(corporationValidator.createError(
						Constants.REST_GUID, ErrorCodes.INVALID_REST_ID));
				return new ErrorResponse(
						ResponseCodes.CORPORATE_OFFERS_ADDITION_FAILURE,
						listOfError);
			}

		}

		if (listOfError.isEmpty()) {
			listOfError = corporationValidator.validateCorporateOffersOnUpdate(
					corporate_offers, listOfError);
		}

		if (listOfError.isEmpty()) {

			Map<String, Object> params1 = new HashMap<String, Object>();// no-need
			params1.put(Constants.GUID, corporate_offers.getGuid());// no-need
			// no-need corporateoffersDao.findCorporateOffers(params1).get(0)
			CorporateOffers updated = corporateoffersDao.updateCorporateOffers(corporate_offers, corporateoffersDao.findCorporateOffers(params1).get(0));
			response = new UpdateResponse<>(ResponseCodes.CORPORATE_OFFERS_UPDATED_SUCCESFULLY, updated.getGuid());
			return response;
		}
		return new ErrorResponse(ResponseCodes.CORPORATE_OFFERS_UPDATION_FAILURE, listOfError);
	}

}
