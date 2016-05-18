/**
 * 
 */
package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.dao.intf.GuestHasTagsDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.model.Restaurant;
import com.clicktable.model.TagModelOld;
import com.clicktable.model.UserInfoModel;
import com.clicktable.relationshipModel.GuestTagPreferencesRelationshipModel;
import com.clicktable.repository.TagPreferencesRepo;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.GuestHasTagsService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CustomerValidator;
import com.clicktable.validate.GuestHasTagsValidator;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.ValidationError;

/**
 * @author a.thakur
 *
 */
@org.springframework.stereotype.Service
public class GuestHasTagsServiceImpl implements GuestHasTagsService {

	@Autowired
	GuestHasTagsDao guestHasTagsDao;

	@Autowired
	TagPreferencesRepo tag_repo;

	@Autowired
	RestaurantDao restaurantDao;

	@Autowired
	AuthorizationService authService;

	@Autowired
	GuestHasTagsValidator guestHasTagsValidator;

	@Autowired
	CustomerValidator guestValidator;
	
	@Autowired
	RestaurantValidator restValidator;

	private BaseResponse base_response;
	private List<ValidationError> listOfError;
	private ValidationError error;

	@Override
	@Transactional
	public BaseResponse addGuestProfileTag(String guestguid, TagModelOld tag,
			String token) {
		UserInfoModel user = authService.getUserInfoByToken(token);
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		List<TagModelOld> tagList = new ArrayList<TagModelOld>();
		Map<String, Object> params = new java.util.HashMap<>();
		Map<String, Object> map_object = new java.util.HashMap<>();
		int preferenceGuestCount = 0;
		int preferenceRestaurantCount = 0;
		TagModelOld tagModel = null;
		params.put(Constants.GUEST_GUID, guestguid);
		params.put(Constants.ACTIVE_STATUS, "ACTIVE");

		if (user.getRoleId().equals(Constants.STAFF_ROLE_ID)|| user.getRoleId().equals(Constants.MANAGER_ROLE_ID)|| user.getRoleId().equals(Constants.ADMIN_ROLE_ID)) {
			params.put(Constants.REST_GUID, user.getRestGuid());
			tag.setAddedBy("RESTAURANT");
			map_object = guestHasTagsDao.validateRestWithTagCount(params);
		} else if (user.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			tag.setAddedBy("GUEST");
			map_object = guestHasTagsDao.validateGuestWithTagCount(params);
		}

		/* Validate Customer | Rest ID | Get Tags w.r.t to REST GUEST RELATIONSHIP */

		if (!user.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)
				&& null == map_object.get(Constants.REST_NODE)) {
			/* Restaurant is Missing */
			error = new ValidationError(Constants.REST_GUID,UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REST_ID),	ErrorCodes.INVALID_REST_ID);
			listOfError.add(error);
		} else if (null == map_object.get(Constants.GUEST_NODE)) {
			/* Guest Is missing */
			error = new ValidationError(Constants.GUEST_GUID,UtilityMethods.getErrorMsg(ErrorCodes.INVALID_CUST_ID),ErrorCodes.INVALID_CUST_ID);
			listOfError.add(error);
		}

		/* Retrieving Tag Model for Staff | Consumer( Guest(S) */

		List<TagModelOld> tagModelList = new ArrayList<TagModelOld>();
		if (user.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			tagModelList = (null == map_object.get(Constants.GUEST_TAG_MODEL)) ? null	: (List<TagModelOld>) map_object.get(Constants.GUEST_TAG_MODEL);
		} else if (user.getRoleId().equals(Constants.STAFF_ROLE_ID)	|| user.getRoleId().equals(Constants.MANAGER_ROLE_ID)	|| user.getRoleId().equals(Constants.ADMIN_ROLE_ID)) {
			tagModelList = (null == map_object.get(Constants.REST_TAG_MODEL)) ? null	: (List<TagModelOld>) map_object.get(Constants.REST_TAG_MODEL);
		}
       
		/* Maintaining Tag Model for Staff | Consumer( Guest(S) */
		
		if (null != tagModelList) {
			/* Maintaining Uniqueness Tag across each Restaurant(s) */
				for (TagModelOld restTagging : tagModelList) {
					if (restTagging.getName().equalsIgnoreCase(tag.getName())) {
						tagModel = restTagging;
						tagModel.setExist(true);
						if (user.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
							preferenceGuestCount++;
						} else if (user.getRoleId().equals(Constants.STAFF_ROLE_ID)	|| user.getRoleId().equals(Constants.MANAGER_ROLE_ID)	|| user.getRoleId().equals(Constants.ADMIN_ROLE_ID)) {
							preferenceRestaurantCount++;
						}
					} else {
						tagModel.setInfoOnCreate(authService
								.getUserInfoByToken(token));
						if (user.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
							tagModel = new TagModelOld(tag.getName(),tag.getAddedBy());
							preferenceGuestCount++;
						} else if (user.getRoleId().equals(Constants.STAFF_ROLE_ID)	|| user.getRoleId().equals(	Constants.MANAGER_ROLE_ID)	|| user.getRoleId().equals(Constants.ADMIN_ROLE_ID)) {
							tagModel = new TagModelOld(tag.getName(),tag.getAddedBy());
							preferenceRestaurantCount++;
						}
	
					}
					tagList.add(tagModel);
				}
			
			
		} else {
				/* No need to count preferenceRestaurantCount as it always new one */
				if (user.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
					tagModel = new TagModelOld(tag.getName(),
							tag.getAddedBy());
				} else if (user.getRoleId().equals(Constants.STAFF_ROLE_ID)	|| user.getRoleId().equals(Constants.MANAGER_ROLE_ID)	|| user.getRoleId().equals(Constants.ADMIN_ROLE_ID)) {
					tagModel = new TagModelOld(tag.getName(),tag.getAddedBy());
				}
				tagModel.setInfoOnCreate(authService.getUserInfoByToken(token));
				tagList.add(tagModel);
		}

		/* Validationg for Max 5 Tag for Each Rest */

		if (preferenceRestaurantCount > 5) {
			error = new ValidationError(
					Constants.TAG_MODULE,
					UtilityMethods
							.getErrorMsg(ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_REST),
					ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_REST);
			listOfError.add(error);
		}
		
		if(preferenceGuestCount > 5)
		{
			error = new ValidationError(Constants.TAG_MODULE,
					UtilityMethods.getErrorMsg(ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_GUEST),ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_GUEST);
			listOfError.add(error);
		}

		if (listOfError.isEmpty()) {

			base_response = new BaseResponse();
			String tagGuid = guestHasTagsDao.addGuestTagRelationship(guestguid,
					user.getRestGuid(), tagList,user.getRoleId());
			Logger.debug("tag guid is------------------------------------------------------------"
					+ tagGuid);
			base_response = new PostResponse<GuestTagPreferencesRelationshipModel>(
					ResponseCodes.GUEST_TAG_UPDATED_REL_SUCCESS, tagGuid);
		} else {
			base_response = new ErrorResponse(
					ResponseCodes.GUEST_TAG_UPDATED_REL_FAILED, listOfError);
		}
		return base_response;

	}

	/* Not in use as it will be imported from Graph DB */
	@Override
	@Transactional
	public BaseResponse addTagRestaurant(List<TagModelOld> tag_model, String token) {
		// TODO Auto-generated method stub
		List<TagModelOld> tagList = new ArrayList<>();
		listOfError = new ArrayList<ValidationError>();
		

		if (listOfError.isEmpty()) {
			/* Validate Tag Enum Values */
			listOfError = guestHasTagsValidator.validateEnum(tagList);
			if (listOfError.isEmpty()) {
				base_response = new BaseResponse();
				List<String> list = guestHasTagsDao.addTags(tagList);

				base_response = new PostResponse<GuestTagPreferencesRelationshipModel>(
						ResponseCodes.TAG_ADD_REL_SUCCESS, list.toArray());
			} else {
				base_response = new ErrorResponse(
						ResponseCodes.TAG_ADD_REL_FAILED, listOfError);
			}
		} else {
			base_response = new ErrorResponse(ResponseCodes.TAG_ADD_REL_FAILED,
					listOfError);
		}

		return base_response;
	}

	@Override
	@Transactional(readOnly = true)
	public BaseResponse getGuestHasTag(Map<String, Object> params, String token) {

		BaseResponse getResponse;
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
			if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
				params.put(Constants.GUEST_GUID, userInfo.getGuid());
			} else {
				params.put(Constants.REST_GUID, userInfo.getRestGuid());
			}
			
		}

		Map<String, Object> qryParamMap = guestHasTagsValidator
				.validateFinderParams(params, TagModelOld.class);

		Logger.debug("query param map is " + qryParamMap);

		List<TagModelOld> guestTag = guestHasTagsDao.findByFields(TagModelOld.class,
				qryParamMap);
		if (guestTag.size() > 0) {
			getResponse = new GetResponse<TagModelOld>(
					ResponseCodes.GUEST_TAG_FETCH_REL_SUCCESS, guestTag);
		} else {
			getResponse = new GetResponse<TagModelOld>(
					ResponseCodes.GUEST_TAG_FETCH_REL_FAILED, guestTag);
		}
		return getResponse;
	}

	@Override
	@Transactional
	public BaseResponse removeGuestHasTag(String guid, List<TagModelOld> tag,
			String rest_guid) {

		List<TagModelOld> tagList = new ArrayList<>();
		listOfError = new ArrayList<ValidationError>();
		/*
		 * Validating Guest GUID
		 */
		boolean customer_exist = guestHasTagsValidator
				.validateGuestProfileGUID(guid);
		if (customer_exist) {
			Restaurant rest_exist = restValidator.validateGuid(rest_guid, listOfError);
			if (null != rest_exist) {
				/*
				 * Validating Tag GUID
				 */
				if (null != tag && tag.size() > 0) {
					for (TagModelOld tagObject : tag) {
						if (null != tagObject.getGuid()
								&& tagObject.getGuid().trim().length() > 0) {
							/* Get Tag Model from Object */
							TagModelOld tagModel = tag_repo.getTag(tagObject.getGuid());
							if (null != tagModel) {
								tagModel.setExist(true);
								tagList.add(tagModel);
							} else {
								error = new ValidationError(
										Constants.TAG_GUID,
										UtilityMethods
												.getErrorMsg(ErrorCodes.INVALID_TAG_ID),
										ErrorCodes.INVALID_TAG_ID);
								listOfError.add(error);
								break;
							}
						}
					}
				} else {
					error = new ValidationError(Constants.TAG_MODEL,
							UtilityMethods
									.getErrorMsg(ErrorCodes.INVALID_TAG_MODEL),
							ErrorCodes.INVALID_TAG_MODEL);
					listOfError.add(error);
				}
			} else {
				error = new ValidationError(Constants.REST_GUID,
						UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REST_ID),
						ErrorCodes.INVALID_REST_ID);
				listOfError.add(error);

			}
		} else {
			error = new ValidationError(Constants.GUEST_GUID,
					UtilityMethods.getErrorMsg(ErrorCodes.INVALID_CUST_ID),
					ErrorCodes.INVALID_CUST_ID);
			listOfError.add(error);
		}

		if (listOfError.isEmpty()) {
			base_response = new BaseResponse();
			guestHasTagsDao.removeGuestHasTagRelationship(guid, tagList,
					rest_guid);
			base_response = new PostResponse<GuestTagPreferencesRelationshipModel>(
					ResponseCodes.GUEST_TAG_DELETED_REL_SUCCESS, guid);
		} else {
			base_response = new ErrorResponse(
					ResponseCodes.GUEST_TAG_DELETED_REL_FAILED, listOfError);
		}

		return base_response;
	}

	@Override
	@Transactional
	public BaseResponse removeTag(List<TagModelOld> tag) {

		List<TagModelOld> tagList = new ArrayList<>();
		listOfError = new ArrayList<ValidationError>();

		/*
		 * Validating Tag GUID
		 */
		if (null != tag && tag.size() > 0) {
			for (TagModelOld tagObject : tag) {
				if (null != tagObject.getGuid()
						&& tagObject.getGuid().trim().length() > 0) {
					/* Get Tag Model from Object */
					TagModelOld tagModel = tag_repo.getTag(tagObject.getGuid());
					if (null != tagModel) {
						tagModel.setExist(true);
						tagList.add(tagModel);
					} else {
						error = new ValidationError(
								Constants.TAG_GUID,
								UtilityMethods
										.getErrorMsg(ErrorCodes.INVALID_TAG_ID),
								ErrorCodes.INVALID_TAG_ID);
						listOfError.add(error);
						break;
					}
				}
			}
		} else {
			error = new ValidationError(Constants.TAG_MODEL,
					UtilityMethods.getErrorMsg(ErrorCodes.INVALID_TAG_MODEL),
					ErrorCodes.INVALID_TAG_MODEL);
			listOfError.add(error);
		}

		if (listOfError.isEmpty()) {
			base_response = new BaseResponse();
			List<String> list = guestHasTagsDao.removeTag(tagList);
			base_response = new PostResponse<GuestTagPreferencesRelationshipModel>(
					ResponseCodes.TAG_DELETED_REL_SUCCESS, list.toArray());
		} else {
			base_response = new ErrorResponse(
					ResponseCodes.TAG_DELETED_REL_FAILED, listOfError);
		}

		return base_response;
	}

	@Override
	public BaseResponse getTag(Map<String, Object> stringParamMap, String header) {
		BaseResponse getResponse;
		UserInfoModel userInfo = authService.getUserInfoByToken(header);
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
			if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
				stringParamMap.put(Constants.GUEST_GUID, userInfo.getGuid());
			} else {
				stringParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());
			}
		}

		Map<String, Object> qryParamMap = guestHasTagsValidator
				.validateFinderParams(stringParamMap, TagModelOld.class);

		List<TagModelOld> guestTag = guestHasTagsDao.getTag(TagModelOld.class, qryParamMap);
		if (guestTag.size() > 0) {
			getResponse = new GetResponse<TagModelOld>(ResponseCodes.TAG_FETCH_SUCCESS,
					guestTag);
		} else {
			getResponse = new GetResponse<TagModelOld>(ResponseCodes.TAG_FETCH_FAILED,
					guestTag);
		}
		return getResponse;
	}

	/*@Override
	public BaseResponse addGuestProfileEventTag(String guid, String rest_guid,
			List<TagModelOld> tag, String token) {

		List<TagModelOld> tagList = new ArrayList<>();
		listOfError = new ArrayList<ValidationError>();
		
		 * Validating Guest GUID
		 
		boolean customer_exist = guestHasTagsValidator
				.validateGuestProfileGUID(guid);
		if (customer_exist) {
			Restaurant rest_exist = restValidator.validateGuid(rest_guid, listOfError);
			if (null != rest_exist) {
				
				 * Validating Tag GUID
				 
				if (null != tag && tag.size() > 0) {
					for (TagModelOld tagObject : tag) {
						if (null != tagObject.getGuid()
								&& tagObject.getGuid().trim().length() > 0) {
							 Get Tag Model from Object 
							TagModelOld tagModel = tag_repo.getTag(tagObject.getGuid());
							if (null != tagModel) {
								tagModel.setExist(true);
								tagList.add(tagModel);
							} else {
								error = new ValidationError(
										Constants.TAG_GUID,
										UtilityMethods
												.getErrorMsg(ErrorCodes.INVALID_TAG_ID),
										ErrorCodes.INVALID_TAG_ID);
								listOfError.add(error);
								break;
							}
						} else {
							if ((null != tagObject.getName() && !tagObject
									.getName().trim().equalsIgnoreCase(""))) {
								
								 * call to Tag Repo to find Tag from Master
								 * Table
								 
								TagModelOld tagModel = tag_repo
										.checkTagNameWithTypeExist(
												tagObject.getName(),
												tagObject.getAddedBy());
								if (null == tagModel) {
									tagModel.setInfoOnCreate(authService.getUserInfoByToken(token));
								} else {
									tagModel.setExist(true);
								}
								tagList.add(tagModel);
							} else {
								error = new ValidationError(
										Constants.TAG_MODEL,
										UtilityMethods
												.getErrorMsg(ErrorCodes.INVALID_TAG_MODEL),
										ErrorCodes.INVALID_TAG_MODEL);
								listOfError.add(error);
								break;
							}
						}
					}
				} else {
					error = new ValidationError(Constants.TAG_MODEL,
							UtilityMethods
									.getErrorMsg(ErrorCodes.INVALID_TAG_MODEL),
							ErrorCodes.INVALID_TAG_MODEL);
					listOfError.add(error);
				}
			} else {
				error = new ValidationError(Constants.REST_GUID,
						UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REST_ID),
						ErrorCodes.INVALID_REST_ID);
				listOfError.add(error);

			}
		} else {
			error = new ValidationError(Constants.GUEST_GUID,
					UtilityMethods.getErrorMsg(ErrorCodes.INVALID_CUST_ID),
					ErrorCodes.INVALID_CUST_ID);
			listOfError.add(error);
		}

		if (listOfError.isEmpty()) {
			 Validate Tag Enum Values 
			listOfError = guestHasTagsValidator.validateEnum(tagList);
			if (listOfError.isEmpty()) {
				base_response = new BaseResponse();
				String tagGuid = guestHasTagsDao.addGuestEventTagRelationship(
						guid, rest_guid, tagList);
				Logger.debug("tag guid is------------------------------------------------------------"
						+ tagGuid);
				base_response = new PostResponse<GuestTagPreferencesRelationshipModel>(
						ResponseCodes.GUEST_TAG_UPDATED_REL_SUCCESS, tagGuid);
			} else {
				base_response = new ErrorResponse(
						ResponseCodes.GUEST_TAG_UPDATED_REL_FAILED, listOfError);
			}
		} else {
			base_response = new ErrorResponse(
					ResponseCodes.GUEST_TAG_UPDATED_REL_FAILED, listOfError);
		}

		return base_response;
	}*/
}
