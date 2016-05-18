package com.clicktable.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.clicktable.dao.intf.LanguageDao;
import com.clicktable.model.Language;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.LanguageService;
import com.clicktable.util.ResponseCodes;
import com.clicktable.validate.LanguageValidator;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class LanguageServiceImpl implements LanguageService {

	@Autowired
	LanguageDao languageDao;

	@Autowired
	LanguageValidator validateLanguageObject;

	@Autowired
	AuthorizationService authorizationService;

        /**
	 * {@inheritDoc}
	 */
	/*
	@Override
	@Transactional
        public BaseResponse addLanguage(Language language, String token)
	{

		language.setGuid(UtilityMethods.generateCtId());
		language.setStatus(Constants.ACTIVE_STATUS);
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		language.setInfoOnCreate(userInfo);
		
		List<ValidationError> listOfErrorForLanguage = new ArrayList<>();
		listOfErrorForLanguage = validateLanguageObject.validateLanguageOnAdd(language);
		
		Map<String,Object> params = new HashMap<String, Object>();
		params.put(Constants.NAME, language.getName());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Language> languageList = languageDao.findByFields(Language.class, params);
		BaseResponse response=null;
		
		if (listOfErrorForLanguage.isEmpty())
		{
		    if(languageList.size()>0)
			{
			    ValidationError error = new ValidationError(Constants.NAME,UtilityMethods.getErrorMsg(ErrorCodes.COUNTRY_NAME_ALREADY_EXISTS));
			   listOfErrorForLanguage.add(error);
			    response = new ErrorResponse(ResponseCodes.COUNTRY_ADDED_FAILURE,listOfErrorForLanguage);
				
			}
		    else
		    {
		    languageDao.create(language);
		    // System.out.println("Language created with id "+language.getId());
		    response = new PostResponse<Restaurant>(ResponseCodes.COUNTRY_ADDED_SUCCESFULLY,language.getGuid());
		    }
		
		} 
		else 
		{
		    response = new ErrorResponse(ResponseCodes.COUNTRY_ADDED_FAILURE,listOfErrorForLanguage);
		}

		return response;
	}*/

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly =true)
	public BaseResponse getLanguage(Map<String, Object> params)
	{
		BaseResponse getResponse; 
		Map<String, Object> qryParamMap = validateLanguageObject.validateFinderParams(params, Language.class);
		List<Language> languageList = languageDao.findByFields(Language.class, qryParamMap);
		getResponse= new GetResponse<Language>(ResponseCodes.LANGUAGE_RECORD_FETCH_SUCCESFULLY, languageList);
		return getResponse;

	}


	

}
