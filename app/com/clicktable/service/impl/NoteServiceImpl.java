package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.dao.intf.NoteDao;
import com.clicktable.model.Note;
import com.clicktable.model.Table;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.NoteService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.NoteValidator;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class NoteServiceImpl implements NoteService {

	@Autowired
	NoteDao noteDao;

	@Autowired
	NoteValidator validateNoteObject;

	@Autowired
	RestaurantValidator restaurantValidator;
	/*
	 * @Autowired RestaurantHasTableDao restaurantHasTableDao;
	 */

	@Autowired
	AuthorizationService authorizationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse addNote(Note note, String token) {
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		Logger.debug("restaurant of logged in user : "+userInfo.getRestGuid());
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		BaseResponse response;
		if (!(userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && !userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)){
		note.setRestaurantGuid(userInfo.getRestGuid());
		}
		listOfError.addAll(validateNoteObject.validateNoteOnAdd(note));
		restaurantValidator.validateRestaurant(note.getRestaurantGuid(), userInfo, listOfError);
		if (listOfError.isEmpty()) {
			String guid = noteDao.addRestaurantNote(note, note.getRestaurantGuid());
			if (guid == null) {
				listOfError.add(new ValidationError(Constants.REST_ID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REST_ID),ErrorCodes.INVALID_REST_ID));
				response = new ErrorResponse(ResponseCodes.NOTE_ADDED_FAILURE, listOfError);
			} else {
				response = new PostResponse<Table>(ResponseCodes.NOTE_ADDED_SUCCESFULLY, guid);
			}
		} else
			response = new ErrorResponse(ResponseCodes.NOTE_ADDED_FAILURE, listOfError);
		return response;
	}

	@Override
	public BaseResponse getNotes(Map<String, Object> params, String token) {
		BaseResponse getResponse;
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		if (!(userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)))
			params.put(Constants.REST_ID, userInfo.getRestGuid());
		Map<String, Object> qryParamMap = validateNoteObject.validateFinderParams(params, Note.class);
		List<Note> notes = noteDao.findByFields(Note.class, qryParamMap);
		getResponse = new GetResponse<Note>(ResponseCodes.NOTES_FETCH_SUCCESFULLY, notes);
		return getResponse;
	}

	@Override
	public BaseResponse deleteNotes(String guid,
			String token) {
		// TODO Auto-generated method stub
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Map<String,Object> params=new HashMap<String,Object>();
		params.put(Constants.GUID, guid);
		BaseResponse getResponse=null;
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) && !userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID))
			params.put(Constants.REST_ID, userInfo.getRestGuid());
		
		restaurantValidator.validateRestaurant(params.get(Constants.REST_ID).toString(), userInfo, listOfError);
		if(listOfError.isEmpty()){
			if(null!=params.get(Constants.GUID)){
				    String notes = noteDao.deleteNote(params);
				    getResponse =  new PostResponse<Note>(ResponseCodes.NOTE_DEL_SUCCESFULLY,notes);
		   		}
		    }
		else{		
			getResponse =new PostResponse<Note>(ResponseCodes.NOTE_DEL_FAILURE, params.get(Constants.GUID).toString());	
		    }
		return getResponse;
		
	}
	
	
	

}
