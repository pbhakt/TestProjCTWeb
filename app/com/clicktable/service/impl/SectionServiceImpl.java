package com.clicktable.service.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.dao.intf.SectionDao;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Section;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.SectionService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.validate.SectionValidator;
import com.clicktable.validate.ValidationError;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class SectionServiceImpl implements SectionService {

	@Autowired
	SectionDao sectionDao;

	@Autowired
	SectionValidator validateSectionObject;

	@Autowired
	AuthorizationService authorizationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
        public BaseResponse addSection(Section section, String token)
	{

		//section.setGuid(UtilityMethods.generateCtId());
		
		
		section.setStatus(Constants.ACTIVE_STATUS);
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		section.setInfoOnCreate(userInfo);
		
		List<ValidationError> listOfErrorForSection  = validateSectionObject.validateSectionOnAdd(section);
		
		BaseResponse response=null;
		
		/* Getting Restaurant GUID from Token */
		
		if (null!=userInfo && !userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID) && !userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			section.setRestID(userInfo.getRestGuid());
		}
		
		if(null==section.getRestID()){
			listOfErrorForSection.add(validateSectionObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));        	
		}
		
		if (listOfErrorForSection.isEmpty())
		{
		listOfErrorForSection=validateSectionObject.checkSection(section, Constants.ADD);
		}
		
		if (listOfErrorForSection.isEmpty())
		{
			
		   String guid=sectionDao.addRestaurantSection(section);
		   response = new PostResponse<Restaurant>(ResponseCodes.SECTION_ADDED_SUCCESFULLY,guid);
		     
		
		} 
		else 
		{
		    response = new ErrorResponse(ResponseCodes.SECTION_ADDED_FAILURE,listOfErrorForSection);
		}

		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly =true)
	public BaseResponse getSections(Map<String, Object> params)
	{
		BaseResponse getResponse; 
		Map<String, Object> qryParamMap = validateSectionObject.validateFinderParams(params, Section.class);
		if(!qryParamMap.containsKey(Constants.STATUS)){
			qryParamMap.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		}
		
		List<Section> sectionList = sectionDao.findByFields(Section.class, qryParamMap);
		getResponse= new GetResponse<Section>(ResponseCodes.SECTION_RECORD_FETCH_SUCCESFULLY, sectionList);
		return getResponse;

		
		
	}

	@Override
	public BaseResponse deleteSection(String sectionGuid,String restID, String token) {

		BaseResponse response;
		Section section=null;
		
		
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if ((sectionGuid == null) || sectionGuid.equals("")) {
			listOfError.add(validateSectionObject.createError(Constants.SECTION_GUID,
					ErrorCodes.SECTION_ID_REQUIRED));
			   response = new ErrorResponse(ResponseCodes.SECTION_DELETED_FAILURE, listOfError);
		} else {
			section = sectionDao.find(sectionGuid);
			if(null==section){
				listOfError.add(validateSectionObject.createError(Constants.SECTION_GUID, ErrorCodes.INVALID_SECTION_ID)); 
				response = new ErrorResponse(ResponseCodes.SECTION_DELETED_FAILURE, listOfError);
			}
		}
		
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		if (null!=userInfo && !userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID) && !userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			if(!userInfo.getRestGuid().equalsIgnoreCase(section.getRestID())){
				listOfError.add(validateSectionObject.createError(Constants.ACCESS_DENIED, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST)); 
				response = new ErrorResponse(ResponseCodes.SECTION_DELETED_FAILURE, listOfError);
			}
		}else if(null!=userInfo && userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)){
			if ((restID == null) || restID.equals("")){
				listOfError.add(validateSectionObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
				response = new ErrorResponse(ResponseCodes.SECTION_DELETED_FAILURE, listOfError);
			}
			
			else{
				if(!restID.equalsIgnoreCase(section.getRestID())){
					listOfError.add(validateSectionObject.createError(Constants.ACCESS_DENIED, ErrorCodes.NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST));
					response = new ErrorResponse(ResponseCodes.SECTION_DELETED_FAILURE, listOfError);
				}
			}
			
		}else if(null==userInfo){ 
			listOfError.add(validateSectionObject.createError(Constants.INVALID_ACCESS_TOKEN, ErrorCodes.ACCESS_TOKEN_MISSING)); 
			response = new ErrorResponse(ResponseCodes.SECTION_DELETED_FAILURE, listOfError);
		}
		
		/*Validating Section and tables before deleted */
		if(listOfError.isEmpty()){
			validateSectionObject.deleteSection(section, listOfError);
		}

		if (listOfError.isEmpty()) {
			boolean created = sectionDao.deleteRestaurantSection(section);
			Logger.debug("query result" + created);
			response = new UpdateResponse<Restaurant>(ResponseCodes.SECTION_DELETED_SUCCESFULLY, sectionGuid);
		} else {
			response = new ErrorResponse(ResponseCodes.SECTION_DELETED_FAILURE, listOfError);
		}

		return response;

	
	}

	

	@Override
	public BaseResponse updateRestaurant(Section section, String token) {
		BaseResponse response;
		
		
		Section check_section = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if ((null==section.getGuid()) || ("").equalsIgnoreCase(section.getGuid())) {
			listOfError.add(validateSectionObject.createError(Constants.SECTION_GUID,
					ErrorCodes.SECTION_ID_REQUIRED));
			   response = new ErrorResponse(ResponseCodes.SECTION_DELETED_FAILURE, listOfError);
			   return response;
		} else {
			check_section = sectionDao.find(section.getGuid());
			if(null==check_section){
				listOfError.add(validateSectionObject.createError(Constants.SECTION_GUID, ErrorCodes.INVALID_SECTION_ID)); 
				response = new ErrorResponse(ResponseCodes.SECTION_UPDATION_FAILURE, listOfError);
			return response;
			}
		}
		
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		if (null!=userInfo && !userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID) && !userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			  section.setRestID(userInfo.getRestGuid());
		}else if(null!=userInfo && userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)){
			if (null==section.getRestID()){
				listOfError.add(validateSectionObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
				response = new ErrorResponse(ResponseCodes.SECTION_UPDATION_FAILURE, listOfError);
			}
			
			
		}else if(null==userInfo){ 
			listOfError.add(validateSectionObject.createError(Constants.INVALID_ACCESS_TOKEN, ErrorCodes.ACCESS_TOKEN_MISSING)); 
			response = new ErrorResponse(ResponseCodes.SECTION_UPDATION_FAILURE, listOfError);
		}
		
		if (listOfError.isEmpty() && !check_section.getName().equals(section.getName()))
		{
			listOfError=validateSectionObject.checkSection(section, Constants.UPDATE);
		}
		
		
		if (listOfError.isEmpty()) {
			sectionDao.updateRestaurantSection(section);
			response = new UpdateResponse<Restaurant>(ResponseCodes.SECTION_UPDATED_SUCCESFULLY, section.getGuid());
		} else {
			response = new ErrorResponse(ResponseCodes.SECTION_UPDATION_FAILURE, listOfError);
		}

		return response;
	}


	

}
