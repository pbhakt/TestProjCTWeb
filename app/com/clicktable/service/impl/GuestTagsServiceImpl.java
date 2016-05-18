/**
 * 
 */
package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;

import com.clicktable.dao.intf.GuestTagDao;
import com.clicktable.model.GuestTagModel;
import com.clicktable.model.Tag;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.GuestTagsService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.GuestTagValidator;
import com.clicktable.validate.ValidationError;
import com.google.common.collect.Lists;

/**
 * @author p.vishwakarma
 *
 */
@org.springframework.stereotype.Service
public class GuestTagsServiceImpl implements GuestTagsService {

	@Autowired
	AuthorizationService authService;

	@Autowired
	GuestTagValidator guestTagValidator;

	@Autowired
	GuestTagDao guestTagDao;

	@Override
	public BaseResponse removeGuestTag(GuestTagModel guestTagModel, String token) {
	    Map<String, Object> params = new HashMap<String, Object>();
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		String guid = null;
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			listOfError = (guestTagValidator.validateGuestTagsByGuestOnRemove(
					guestTagModel, listOfError));
			if (listOfError.isEmpty()) {
				guid = guestTagDao.removeGuestTagOfCustomer_ByGuest(params);
			}
		} else {

			if (userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
				if (!params.containsKey(Constants.REST_GUID)) {
					listOfError.add(new ValidationError(Constants.REST_GUID,
							ErrorCodes.REST_ID_REQUIRED,
							ErrorCodes.REST_ID_REQUIRED));
				}
			} else {

				guestTagModel.setRestaurantGuid(userInfo.getRestGuid());
			}
			if (listOfError.isEmpty())
				listOfError = guestTagValidator
						.validateGuestTagsByRestaurantOnRemove(guestTagModel,
								listOfError);
			if (listOfError.isEmpty()) {
				
				Map<String, Object> params1 = new HashMap<String, Object>();
				params1.put(Constants.TAG_GUID, guestTagModel.getTagGuid());
				params1.put(Constants.GUEST_GUID, guestTagModel.getGuestGuid());
				params1.put(Constants.REST_GUID, guestTagModel.getRestaurantGuid());
				guid = guestTagDao
						.removeGuestTagOfCustomer_ByRestaurant(params1);

			}

		}

		if (listOfError.isEmpty()) {
			return new PostResponse<Tag>(
					ResponseCodes.GUEST_TAG_REMOVED_SUCCESSFULLY, guid);
		}

		return new ErrorResponse(ResponseCodes.GUEST_TAG_REMOVE_FAILURE,
				listOfError);

	}

	@Override
	public BaseResponse getTag(Map<String, Object> params, String token) {
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		List<Tag> result = new ArrayList<Tag>();
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			// Fetch by customer

			if(!params.containsKey(Constants.GUEST_GUID))
			params.put(Constants.GUEST_GUID, userInfo.getGuid());
			if(!params.containsKey(Constants.TYPE))
			params.put(Constants.TYPE, Constants.TAG_PREFERENCES);
			result = guestTagDao.findByFields(Tag.class, params);
		} else {
			if (userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
				if (!params.containsKey(Constants.REST_GUID)) {
					listOfError.add(new ValidationError(Constants.REST_GUID,
							ErrorCodes.REST_ID_REQUIRED,
							ErrorCodes.REST_ID_REQUIRED));
				}
			} else {
				if(!params.containsKey(Constants.REST_GUID))
				params.put(Constants.REST_GUID, userInfo.getRestGuid());
				if(!params.containsKey(Constants.TYPE))
				params.put(Constants.TYPE, Constants.TAG_PREFERENCES);
			}
			if (listOfError.isEmpty()) {
				// Fetch by restaurant
				result = guestTagDao.findByFields(Tag.class, params);
			}

		}

		if (listOfError.isEmpty()) {
			return new GetResponse<Tag>(
					ResponseCodes.GUEST_TAG_FETCHED_SUCCESSFULLY, result);
		}

		return new ErrorResponse(ResponseCodes.GUEST_TAG_FETCH_FAILURE,
				listOfError);

	}

	@Override
	public BaseResponse addGuestTag(GuestTagModel guestTagModel, String token) {

		String guid = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			listOfError = (guestTagValidator.validateGuestTagsByGuestOnAdd(guestTagModel, listOfError));
			if (listOfError.isEmpty()) {
				if (guestTagModel.getTagGuid() != null) {
					guid = guestTagDao.addExistingTagWithGuest(guestTagModel);
				} else {
					Tag tag = new Tag();
					tag.setInfoOnCreate(userInfo);
					tag.setName(guestTagModel.getTagName());
					tag.setAddedBy(Constants.GUEST_ADDED);// GUEST
					tag.setType(Constants.TAG_PREFERENCES);
					
					guid = guestTagDao.addNewTagWithGuest_ByGuest(tag,
							guestTagModel.getGuestGuid());
				}
			}
		} else {
			if (userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
				if ((guestTagModel.getRestaurantGuid()!= null) || (!guestTagModel.getRestaurantGuid().equals(""))) {
					listOfError.add(new ValidationError(Constants.REST_GUID,
							ErrorCodes.REST_ID_REQUIRED,
							ErrorCodes.REST_ID_REQUIRED));
				}
			} else {
				guestTagModel.setRestaurantGuid(userInfo.getRestGuid());
			}
			if (listOfError.isEmpty())
				listOfError = guestTagValidator
						.validateGuestTagsByRestaurantOnAdd(guestTagModel, listOfError);
			if (listOfError.isEmpty()) {
				if (guestTagModel.getTagGuid() != null) {
					guid = guestTagDao.addExistingTagWithGuest(guestTagModel);// duplicate already in line 120
				} else {
					Tag new_tag = new Tag();
					new_tag.setInfoOnCreate(userInfo);
					new_tag.setName(guestTagModel.getTagName());
					new_tag.setAddedBy(Constants.RESTAURANT_ADDED);// RESTAURANT

					new_tag.setType(Constants.TAG_PREFERENCES);

					guid = guestTagDao.addNewTagWithGuest_ByRestaurant(new_tag,
							guestTagModel.getGuestGuid(), userInfo.getRestGuid());
				}
			}
		}

		if (listOfError.isEmpty()) {
			return new PostResponse<Tag>(
					ResponseCodes.GUEST_TAG_ADDED_SUCCESSFULLY, guid);
		}

		return new ErrorResponse(ResponseCodes.GUEST_TAG_ADDITION_FAILURE,
				listOfError);
	}

	@Override
	public void addGuestProfileEventTag(String guestGuid,
			List<String> tagNameList, String token) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		//List<String> tagNameList = new ArrayList<String>();
		List<String> existingTagGuidList = new ArrayList<String>();
		List<String> existingRestNoguestTagGuidList = new ArrayList<String>();
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		UserInfoModel userInfo = authService.getUserInfoByToken(token);

		if (userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)
				|| userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			listOfError.add(new ValidationError(Constants.ACCESS_TOKEN,
					ErrorCodes.INVALID_ACCESS_TOKEN,
					ErrorCodes.INVALID_ACCESS_TOKEN));
		} else {
			params.put(Constants.REST_GUID, userInfo.getRestGuid());
		}
		if (listOfError.isEmpty()) {
			map = guestTagValidator
					.validateEventandOffersGuestTagsByRestaurantOnAdd(guestGuid, params, tagNameList, listOfError);
			tagNameList = map.get("tagNames");
			existingRestNoguestTagGuidList = map.get("tagGuids");
			existingTagGuidList = map.get("existingTagGuids");

		}
		if (listOfError.isEmpty() && !tagNameList.isEmpty()) {
			
			List<String> eventCategoryList = UtilityMethods.getEnumValues(Constants.EVENT_MODULE, Constants.EVENTCATEGORY);
			List<String> offerCategoryList = UtilityMethods.getEnumValues(Constants.EVENT_MODULE, Constants.OFFERCATEGORY);
			
			List<Tag> tagslist = new ArrayList<Tag>();
			for (String tagName : tagNameList) {
				Tag new_tag= null;
				if(eventCategoryList.contains(tagName)){
				new_tag = new Tag(tagName,
						Constants.RESTAURANT_ADDED, Constants.EVENT);
				}else if (offerCategoryList.contains(tagName)){
				new_tag = new Tag(tagName,
							Constants.RESTAURANT_ADDED, Constants.OFFER);
				}
				if(new_tag != null)
				{
				new_tag.setInfoOnCreate(userInfo);
				tagslist.add(new_tag);
				}
			}
			guestTagDao.addNewEventandOfferGuestTagWithGuest_ByRestaurant(
					tagslist, guestGuid, userInfo.getRestGuid());

		}
		if (listOfError.isEmpty()
				&& !existingRestNoguestTagGuidList.isEmpty()) {
			guestTagDao.addExistingEventandOfferGuestTagWithGuest_ByRestaurant(
					existingRestNoguestTagGuidList, guestGuid,
					userInfo.getRestGuid());

		}
		if (listOfError.isEmpty() && !existingTagGuidList.isEmpty()) {
			guestTagDao
					.addExistingRestandGuestEventandOfferGuestTagWithGuest_ByRestaurant(
							existingTagGuidList, guestGuid,
							userInfo.getRestGuid());

		}
	}
	
	@Override
	public BaseResponse mergeTag(String header) {
		// TODO Auto-generated method stub
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		UserInfoModel userInfo = authService.getUserInfoByToken(header);
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)){
			listOfError.add(new ValidationError(Constants.ACCESS_DENIED,
					ErrorCodes.INVALID_ACCESS_TOKEN,
					ErrorCodes.INVALID_ACCESS_TOKEN));
		}
		
		if(listOfError.isEmpty()){
		try{
		    long startTime=Calendar.getInstance().getTimeInMillis();
			Logger.info(" Meging WORKING (MergingTag)  =========== ");
			List<String> existing_tag_guid_list =new ArrayList<String>();
		   /* existing_tag_guid_list.add("bbbcdc17-951a-49c0-aeda-f4c646c07d56");			   
		    existing_tag_guid_list.add("d740bccf-145e-4a7e-b962-7a3e0cf7f97f");
		    existing_tag_guid_list.add("1a5173c1-7402-4385-a154-808291d34d9b");
		    existing_tag_guid_list.add("d960fbd7-47c4-4663-8dae-e0b61597616f");
		    existing_tag_guid_list.add("47a9adfb-0faf-4899-9a40-9ec087420831");
		    existing_tag_guid_list.add("6f170c8d-5829-4151-8216-a03588c63dfe");
		    existing_tag_guid_list.add("86d3c922-a277-4177-8a70-2df0aa172c1b");
		    existing_tag_guid_list.add("4988284c-7ee9-4511-abba-595b87bb21d7");
		    existing_tag_guid_list.add("16982258-5352-4343-93c2-de3a6ffcf585");
		    existing_tag_guid_list.add("56877c79-2d66-4738-97de-681cdf2c9b3a");
		    existing_tag_guid_list.add("72dbf196-65d0-4c63-a57b-3d702784b8d9");
		    existing_tag_guid_list.add("12527b24-b486-4b12-9854-7f104dcf4808");
		    existing_tag_guid_list.add("860cb8a3-ef7b-46de-b941-16ca12554f44");
		    existing_tag_guid_list.add("f0fc3ac6-17d5-4b67-9564-33b9b1b5473d");
		    existing_tag_guid_list.add("b8709494-c3a6-4bfe-ada7-ad94111868c8");
		    existing_tag_guid_list.add("dabf9b16-d536-4fa9-960f-ea683a01f938");
		    existing_tag_guid_list.add("b247897a-7094-4cf3-a997-c09ead0c631f");*/
			List<Tag> listTag=guestTagDao.getTagMergingTest(existing_tag_guid_list);
			
		    Lists.partition(listTag, 10).forEach(mergingList -> {
		    	guestTagDao.mergingTag(mergingList);
			});
		    guestTagDao.cleanup();
		    Logger.info(" Meging End (MergingTag)  =========== "+(Calendar.getInstance().getTimeInMillis()-startTime));
		    return new GetResponse<Tag>(
					ResponseCodes.TAG_MERGED_SUCCESSFULLY,listTag);
			
			}catch(Exception e){
				Logger.info("Error in merging Tags");
			    e.printStackTrace();
			}
		}

				 
		return new ErrorResponse(ResponseCodes.TAG_MERGED_FAILURE,
				listOfError);
	}

}
