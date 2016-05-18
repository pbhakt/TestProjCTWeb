package com.clicktable.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.CountryDao;
import com.clicktable.model.Country;
//import com.clicktable.repository.CountryRepo;
//import com.clicktable.dao.intf.CountryDao;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

/**
 * 
 * @author p.singh
 *
 */



@org.springframework.stereotype.Service
public class CountryValidator extends EntityValidator<Country> 
{
   
	@Autowired
	CountryDao countryDao;

	
	/*@Autowired
	CountryRepo restRepo*/;	
	

	public List<ValidationError> validateCountryOnAdd(Country country, List<ValidationError> listOfErrorForCountry) {
		listOfErrorForCountry.addAll(validateOnAdd(country));
		listOfErrorForCountry.addAll(CustomValidations.validateStatusAndLanguageCode(listOfErrorForCountry, country.getStatus(), country.getLanguageCode()));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		params.put(Constants.NAME,country.getName());
		List<Country> countryList = countryDao.findByFields(Country.class, params);
		if (countryList.isEmpty()) {
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			//params1.put(Constants.NAME,country.getName());
			params1.put(Constants.COUNTRY_CODE,country.getCountryCode());
			List<Country> countryList1 = countryDao.findByFields(Country.class, params1);
			if (!countryList1.isEmpty()) {
				listOfErrorForCountry.add(new ValidationError(Constants.COUNTRY_CODE, UtilityMethods.getErrorMsg(ErrorCodes.COUNTRY_WITH_CODE_EXISTS), ErrorCodes.COUNTRY_WITH_CODE_EXISTS));
			}else{
				Map<String, Object> params2 = new HashMap<String, Object>();
				params2.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				params2.put(Constants.NAME,country.getName());
				params2.put(Constants.COUNTRY_CODE,country.getCountryCode());
				List<Country> countryList2 = countryDao.findByFields(Country.class, params2);
				if (!countryList2.isEmpty()) {
					listOfErrorForCountry.add(new ValidationError(Constants.COUNTRY_CODE, UtilityMethods.getErrorMsg(ErrorCodes.COUNTRY_WITH_CODE_EXISTS), ErrorCodes.COUNTRY_WITH_CODE_EXISTS));
				}
			}
		}else{
			listOfErrorForCountry.add(new ValidationError(Constants.NAME, UtilityMethods.getErrorMsg(ErrorCodes.COUNTRY_NAME_ALREADY_EXISTS), ErrorCodes.COUNTRY_NAME_ALREADY_EXISTS));
		}
		return listOfErrorForCountry;
	}

	/**
	 * validations on country at the time of updation
	 * 
	 * @param country
	 * @return
	 */

	public List<ValidationError> validateCountryOnUpdate(Country country) {
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		errorList.addAll(validateOnAdd(country));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		params.put(Constants.GUID, country.getGuid());
		List<Country> countryList = countryDao.findByFields(Country.class,
				params);
		if (!countryList.isEmpty() && countryList.size()==1) {
			Country cntry = countryList.get(0);
			if(cntry.getName().equals(country.getName()) && !cntry.getCountryCode().equals(country.getCountryCode())){
				Map<String, Object> params1 = new HashMap<String, Object>();
				params1.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				params1.put(Constants.COUNTRY_CODE, country.getCountryCode());
				List<Country> countryList1 = countryDao.findByFields(Country.class,
						params1);
				if (!countryList1.isEmpty()) {
					errorList.add(new ValidationError(Constants.COUNTRY_CODE, UtilityMethods
							.getErrorMsg(ErrorCodes.COUNTRY_WITH_CODE_EXISTS),
							ErrorCodes.COUNTRY_WITH_CODE_EXISTS));
				}
			}else if(!cntry.getName().equals(country.getName()) && cntry.getCountryCode().equals(country.getCountryCode())){

				Map<String, Object> params2 = new HashMap<String, Object>();
				params2.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				params2.put(Constants.NAME, country.getName());
				List<Country> countryList2 = countryDao.findByFields(Country.class,
						params2);
				if (!countryList2.isEmpty()) {
					errorList.add(new ValidationError(Constants.COUNTRY_CODE, UtilityMethods
							.getErrorMsg(ErrorCodes.COUNTRY_NAME_ALREADY_EXISTS),
							ErrorCodes.COUNTRY_NAME_ALREADY_EXISTS));
				}
			
			}
			
			
		}else{
			errorList.add(new ValidationError(Constants.GUID, UtilityMethods
					.getErrorMsg(ErrorCodes.INVALID_COUNTRY_ID),
					ErrorCodes.INVALID_COUNTRY_ID));
		}
		return errorList;
	}





	public Country validateCountry(Country country, List<ValidationError> listOfError) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, country.getGuid());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Country> existingList = countryDao.findByFields(Country.class, params);
		Country existing = null;
		if(existingList.size()==1){
			existing = existingList.get(0);
			if(countryDao.hasChildRelationships(existing)){
			listOfError.add(new ValidationError(Constants.GUID, ErrorCodes.CANT_DELETE_COUNTRY, ErrorCodes.CANT_DELETE_COUNTRY));	
			}
		}else{
			listOfError.add(new ValidationError(Constants.GUID, ErrorCodes.INVALID_COUNTRY_ID, ErrorCodes.INVALID_COUNTRY_ID));
		}
		return existing;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.COUNTRY_ID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_COUNTRY_ID;
	}

}
