package com.clicktable.service.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.dao.intf.AttributeDao;
import com.clicktable.dao.intf.CountryDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.model.Attribute;
import com.clicktable.model.Country;
import com.clicktable.model.CustomAttributeModel;
import com.clicktable.model.Restaurant;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.service.intf.AttributeService;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.validate.AttributeValidator;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.ValidationError;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class AttributeServiceImpl implements AttributeService {

	@Autowired
	AttributeDao attributeDao;
	
	@Autowired
	CountryDao countryDao;
	
	@Autowired
	RestaurantDao restDao;
	
	@Autowired
	RestaurantValidator restValidator;

	@Autowired
	AttributeValidator validateAttributeObject;

	@Autowired
	AuthorizationService authorizationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
        public BaseResponse addAttribute(Attribute attribute)
	{
		
		attribute.setStatus(Constants.ACTIVE_STATUS);
		
		
		List<ValidationError> listOfErrorForAttribute = validateAttributeObject.validateAttributeOnAdd(attribute);
		
		listOfErrorForAttribute = validateAttributeObject.validateAgainstExisting(attribute, listOfErrorForAttribute);
		
		BaseResponse response=null;
		
		if (listOfErrorForAttribute.isEmpty())
		{
		    attributeDao.create(attribute);
		    // System.out.println("Attribute created with id "+attribute.getId());
		    response = new PostResponse<Attribute>(ResponseCodes.ATTRIBUTE_ADDED_SUCCESFULLY,attribute.getGuid());
		  
		} 
		else 
		{
		    response = new ErrorResponse(ResponseCodes.ATTRIBUTE_ADDED_FAILURE,listOfErrorForAttribute);
		}

		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly =true)
	public BaseResponse getAttributes(Map<String, Object> params)
	{
		BaseResponse getResponse; 
		List<CustomAttributeModel> customAttributeList = new ArrayList<>();
		Map<String, Object> qryParamMap = validateAttributeObject.validateFinderParams(params, Attribute.class);
		String restGuid = "";
		Restaurant rest = null;
		/*if((params.get(Constants.REST_GUID) == null) || (params.get(Constants.REST_GUID).equals("")))
		{
		    ValidationError error = new ValidationError(Constants.REST_GUID,UtilityMethods.getErrorMsg(ErrorCodes.REST_ID_REQUIRED),ErrorCodes.REST_ID_REQUIRED);
		    errorList.add(error);
		    getResponse = new ErrorResponse(ResponseCodes.ATTRIBUTE_RECORD_FETCH_FAILURE,errorList);
		    return getResponse;
			
		}*/
		if(params.get(Constants.REST_GUID) != null && !params.get(Constants.REST_GUID).equals("")){
		restGuid = params.get(Constants.REST_GUID).toString();
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		rest = restValidator.validateGuid(restGuid, listOfError); 
		}
		/*if(rest == null)
		{
		    return (new GetResponse<CustomAttributeModel>(ResponseCodes.ATTRIBUTE_RECORD_FETCH_SUCCESFULLY, customAttributeList));
		}
		else
		{*/
		List<Attribute> restAttributeList = attributeDao.findByFields(Attribute.class, qryParamMap);
		Logger.debug("rest attribute list size is " + restAttributeList.size());
		Logger.debug("query param map is "+qryParamMap);
		
		qryParamMap.remove(Constants.REST_GUID);
		if(rest != null)
		qryParamMap.put(Constants.COUNTRY_CODE, rest.getCountryCode());
		Logger.debug("query param map is "+qryParamMap);
		List<Attribute> allAttributeList = attributeDao.findByFields(Attribute.class, qryParamMap);
		Logger.debug("all attribute list size is " + allAttributeList.size());
		
		
		CustomAttributeModel custAttr ;
		for(Attribute attr : allAttributeList)
		{
		    custAttr = new CustomAttributeModel(attr);
		    
		    if(restAttributeList.contains(attr))
		    {
			custAttr.setRestHasAttr(true);
		    }
		    else
		    {
			custAttr.setRestHasAttr(false);
		    }
		    
		    customAttributeList.add(custAttr);
		}
		
	//	}
		Logger.debug("customAttributeList size is " + customAttributeList.size());
		getResponse= new GetResponse<CustomAttributeModel>(ResponseCodes.ATTRIBUTE_RECORD_FETCH_SUCCESFULLY, customAttributeList);
		return getResponse;

	}


	
	
	
	
	
	/**
	 * Method to add attributes
	 */
	@Transactional
	public BaseResponse addCountryAttributes(String countryGuid, String attrGuid, String token)
	{
	    BaseResponse response;
	    List<ValidationError> listOfError = new ArrayList<ValidationError>();
             if ((countryGuid == null) || countryGuid.equals(""))
			listOfError.add(validateAttributeObject.createError(Constants.COUNTRY_GUID, ErrorCodes.COUNTRY_ID_REQUIRED));
             if ((attrGuid == null) || attrGuid.equals(""))
			listOfError.add(validateAttributeObject.createError(Constants.ATTR_GUID, ErrorCodes.ATTR_ID_REQUIRED));
             
             Country country = countryDao.find(countryGuid);
		if (country == null)
			listOfError.add(validateAttributeObject.createError(Constants.COUNTRY_GUID, ErrorCodes.INVALID_COUNTRY_ID));
		
		
		/*List<Attribute> attrList = new ArrayList<>();
		
		Logger.debug("attrlist is "+attrList);*/
		
             
             if (listOfError.isEmpty()) 
		{
               String[] attrGuidArr = attrGuid.split(",");
        	   boolean created = attributeDao.addCountryAttributes(countryGuid, attrGuidArr);
        	   Logger.debug("query result"+created);
        	   response = new PostResponse<Attribute>(ResponseCodes.RELATIONSHIP_WITH_COUNTRY_CREATED_SUCCESFULLY,"");
		} 
             else 
             	{
			response = new ErrorResponse(ResponseCodes.RELATIONSHIP_WITH_COUNTRY_CREATION_FAILURE,listOfError);
		}
		
		return response;
	    
	}

	@Override
	public BaseResponse addAttributes(List<Attribute> attributeList) {
		List<Attribute> attr_list = new ArrayList<Attribute>();
		List<ValidationError> listOfErrorForAttribute = new ArrayList<>();
		for(Attribute attribute:attributeList){
			listOfErrorForAttribute = validateAttributeObject.validateAttributeOnAdd(attribute);
			validateAttributeObject.validateAgainstExisting(attribute, listOfErrorForAttribute);
		}
		if(listOfErrorForAttribute.isEmpty())
		attr_list = attributeDao.createMultiple(attributeList);
		List<String> guids = new ArrayList<String>();
		attr_list.forEach((s)->guids.add(s.getGuid()));
		PostResponse<Attribute> response = new PostResponse<Attribute>(ResponseCodes.ATTRIBUTE_ADDED_SUCCESFULLY, guids.toString());
		return response;
	}

}
