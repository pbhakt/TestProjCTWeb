package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.CorporateOffersDao;
import com.clicktable.dao.intf.CorporationDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.StateDao;
import com.clicktable.model.CorporateOffers;
import com.clicktable.model.Corporation;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class CorporationValidator extends EntityValidator<Corporation> {

	@Autowired
	CorporationDao corporationDao;

	@Autowired
	CorporateOffersDao corporateoffersDao;
	
	@Autowired
	RestaurantDao restDao;
	
	@Autowired
	RestaurantValidator restValidator;

	@Autowired
	StateDao stateDao;

	public List<ValidationError> validateCorporationOnAdd(Corporation corporation) {
		List<ValidationError> errorList = validateOnAdd(corporation);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		params.put(Constants.WEBSITE, corporation.getWebsite());
		List<Corporation> corporationList = corporationDao.findByFields(Corporation.class, params);
		if (!corporationList.isEmpty()) {
			errorList.add(new ValidationError(Constants.WEBSITE, UtilityMethods.getErrorMsg(ErrorCodes.CORPORATION_WITH_SAME_WEBSITE_ALREADY_EXISTS),
					ErrorCodes.CORPORATION_WITH_SAME_WEBSITE_ALREADY_EXISTS));
		}

		return errorList;
	}

	public Corporation validateCorporation(Corporation corporation, List<ValidationError> listOfError) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, corporation.getGuid());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Corporation> existingList = corporationDao.findByFields(Corporation.class, params);
		Corporation existing = null;

		return existing;
	}

	public List<ValidationError> validateCorporationOnUpdate(Corporation corporation, List<ValidationError> listOfError) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		params.put(Constants.WEBSITE, corporation.getWebsite());
		List<Corporation> corporationList = corporationDao.findByFields(Corporation.class, params);
		if (!(corporationList.size() > 1) && !(corporationList.get(0).getGuid().equals(corporation.getGuid()))) {
			listOfError.add(new ValidationError(Constants.WEBSITE, UtilityMethods.getErrorMsg(ErrorCodes.CORPORATION_WITH_SAME_WEBSITE_ALREADY_EXISTS),
					ErrorCodes.CORPORATION_WITH_SAME_WEBSITE_ALREADY_EXISTS));
		}
		return listOfError;
	}

	public List<ValidationError> validateCorporateOffersOnAdd(CorporateOffers corporate_offers) {
		List<ValidationError> errorList = validateOnAdd(corporate_offers);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		params.put(Constants.NAME, corporate_offers.getName());
		params.put(Constants.REST_GUID, corporate_offers.getRestaurantGuid());
		List<CorporateOffers> corporateoffersList = corporateoffersDao.findByFields(CorporateOffers.class, params);
		if (!corporateoffersList.isEmpty()) {
			errorList.add(new ValidationError(Constants.NAME, UtilityMethods.getErrorMsg(ErrorCodes.CORPORATE_OFFERS_WITH_SAME_NAME_ALREADY_EXISTS),
					ErrorCodes.CORPORATE_OFFERS_WITH_SAME_NAME_ALREADY_EXISTS));
		}
		return errorList;
	}

	public List<ValidationError> validateCorporateOffersOnUpdate(CorporateOffers corporate_offers, List<ValidationError> listOfError) {
		listOfError.addAll(validateOnAdd(corporate_offers));

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, corporate_offers.getGuid());
		List<CorporateOffers> existingList = corporateoffersDao.findCorporateOffers(params);
		if (existingList.size() != 1) {// it should must be !existingList.isEmpty() 
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_CORPORATE_OFFERS_GUID),
							ErrorCodes.INVALID_CORPORATE_OFFERS_GUID));
		}

		if (listOfError.isEmpty()) {
			listOfError.addAll(validateEnumValues(corporate_offers,
					Constants.COMMON_MODULE));
		}

		if (listOfError.isEmpty()) {
			CorporateOffers corp_offer = existingList.get(0);
			if (!corp_offer.getName().equals(corporate_offers.getName())) {
				Map<String, Object> params2 = new HashMap<String, Object>();
				params2.put(Constants.NAME, corporate_offers.getName());
				params2.put(Constants.REST_GUID, corporate_offers.getRestaurantGuid());
				List<CorporateOffers> existingList2 = corporateoffersDao
						.findCorporateOffers(params2);
				if (existingList2.size() > 0) {// it should must be !existingList.isEmpty()
					listOfError
							.add(new ValidationError(
									Constants.NAME,
									UtilityMethods
											.getErrorMsg(ErrorCodes.CORPORATE_OFFERS_WITH_SAME_NAME_ALREADY_EXISTS),
									ErrorCodes.CORPORATE_OFFERS_WITH_SAME_NAME_ALREADY_EXISTS));
				}
			}
		}
	
		
		return listOfError;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_CORPORATION_GUID;
	}

}
