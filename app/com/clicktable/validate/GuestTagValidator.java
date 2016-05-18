package com.clicktable.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ch.qos.logback.classic.Logger;

import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.GuestTagDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.GuestTagModel;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Tag;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

/**
 * @author p.vishwakarma
 *
 */
@org.springframework.stereotype.Service
public class GuestTagValidator extends EntityValidator<Tag> {

	public static final Logger log = (Logger) LoggerFactory.getLogger(GuestTagValidator.class);

	@Autowired
	RestaurantDao restDao;

	@Autowired
	CustomerDao guestDao;

	@Autowired
	GuestTagDao guestTagDao;


	public List<ValidationError> validateGuestTagsByGuestOnAdd(
			GuestTagModel guestTagModel, List<ValidationError> listOfError) {

		if (guestTagModel.getGuestGuid() == null
				|| guestTagModel.getGuestGuid().equals("")) {
			listOfError.add(new ValidationError(Constants.GUEST_GUID,
					UtilityMethods.getErrorMsg(ErrorCodes.GUEST_GUID_REQUIRED),
					ErrorCodes.GUEST_GUID_REQUIRED));

		}

		if (listOfError.isEmpty()
				&& (guestTagModel.getTagGuid() == null || guestTagModel
						.getTagGuid().equals(""))) {
			listOfError.add(new ValidationError(Constants.TAG_GUID_LABEL,
					UtilityMethods.getErrorMsg(ErrorCodes.TAG_GUID_REQUIRED),
					ErrorCodes.TAG_GUID_REQUIRED));

		}

		if (listOfError.isEmpty()) {
			// By Guest : All Validations
			Map<String, Object> guestParams = new HashMap<String, Object>();
			guestParams.put(Constants.GUID, guestTagModel.getGuestGuid());
			guestParams.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			GuestProfile guestProf = guestDao.findGuest(guestParams);
			if (guestProf == null
					|| !guestProf.getStatus().equals(Constants.ACTIVE_STATUS)) { // never
																					// match
																					// !guest_prof.getStatus().equals(Constants.ACTIVE_STATUS)
																					// condition
				listOfError.add(new ValidationError(Constants.GUID,
						UtilityMethods
								.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID),
						ErrorCodes.INVALID_GUEST_GUID));
			} else {

				if (guestTagModel.getTagGuid() != null) {

					Map<String, Object> guestTagParams = new HashMap<String, Object>();
					guestTagParams.put(Constants.GUID,
							guestTagModel.getTagGuid());
					guestTagParams.put(Constants.STATUS,
							Constants.ACTIVE_STATUS);
					int guestTagListSize = guestTagDao.findByFields(Tag.class,
							guestTagParams).size();
					if (guestTagListSize != 1) {
						listOfError
								.add(new ValidationError(
										Constants.GUID,
										UtilityMethods
												.getErrorMsg(ErrorCodes.INVALID_TAG_GUID),
										ErrorCodes.INVALID_TAG_GUID));
					} else {

						Map<String, Object> guestTagParams1 = new HashMap<String, Object>();
						guestTagParams1.put(Constants.GUEST_GUID,
								guestTagModel.getGuestGuid());
						guestTagParams1.put(Constants.GUID,
								guestTagModel.getTagGuid());

						int count1 = guestTagDao.findByFields(Tag.class,
								guestTagParams1).size();
						if (count1 != 0) {
							// already exists
							listOfError.add(new ValidationError(
									Constants.TAG_GUID_LABEL, UtilityMethods
											.getErrorMsg(ErrorCodes.TAG_EXIST),
									ErrorCodes.TAG_EXIST));

						} else {

							Map<String, Object> guestTagParams2 = new HashMap<String, Object>();
							guestTagParams2.put(Constants.GUEST_GUID,
									guestTagModel.getGuestGuid());
							guestTagParams2.put(Constants.REST_GUID,
									guestTagModel.getRestaurantGuid());

							int tag_list_guest_rest_count = guestTagDao
									.findByFields(Tag.class, guestTagParams2)
									.size();
							if (tag_list_guest_rest_count >= 5) {
								// Restaurant can add max 5 tags to
								// a
								// guest
								listOfError
										.add(new ValidationError(
												Constants.GUID,
												UtilityMethods
														.getErrorMsg(ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_GUEST),
												ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_GUEST));

							}
						}

					}
				} else if (guestTagModel.getTagName() != null) {

					Map<String, Object> guestTagParams = new HashMap<String, Object>();
					guestTagParams.put(Constants.GUEST_GUID,
							guestTagModel.getGuestGuid());
					guestTagParams.put(Constants.TAG_NAME,
							guestTagModel.getTagName());

					int restTagListCount = guestTagDao.findByFields(Tag.class,
							guestTagParams).size();
					if (restTagListCount > 0) {
						// Tag with same name already exists!
						listOfError.add(new ValidationError(
								Constants.TAG_GUID_LABEL, UtilityMethods
										.getErrorMsg(ErrorCodes.TAG_EXIST_WITH_GUEST),
								ErrorCodes.TAG_EXIST_WITH_GUEST));

					}

					Map<String, Object> guestTagParams1 = new HashMap<String, Object>();
					guestTagParams1.put(Constants.GUEST_GUID,
							guestTagModel.getGuestGuid());

					int tag_list_guest_count = guestTagDao.findByFields(
							Tag.class, guestTagParams1).size();
					if (tag_list_guest_count >= 5) {
						// Restaurant can add max 5 tags to a
						// guest
						listOfError
								.add(new ValidationError(
										Constants.GUID,
										UtilityMethods
												.getErrorMsg(ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_GUEST),
										ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_GUEST));

					}

				}
			}
		}
		return listOfError;
	}

	public List<ValidationError> validateGuestTagsByRestaurantOnAdd(
			GuestTagModel guestTagModel, List<ValidationError> listOfError) {

		// Map<String, Object> params
		if (guestTagModel.getGuestGuid() == null
				|| guestTagModel.getGuestGuid().equals("")) {
			listOfError.add(new ValidationError(Constants.GUEST_GUID,
					UtilityMethods.getErrorMsg(ErrorCodes.GUEST_GUID_REQUIRED),
					ErrorCodes.GUEST_GUID_REQUIRED));

		}

		if (listOfError.isEmpty()
				&& (guestTagModel.getTagGuid() == null || guestTagModel
						.getTagGuid().equals(""))) {
			if (guestTagModel.getTagName() == null
					|| guestTagModel.getTagName().equals(""))
				listOfError.add(new ValidationError(Constants.TAG_GUID_LABEL,
						UtilityMethods
								.getErrorMsg(ErrorCodes.TAG_GUID_REQUIRED),
						ErrorCodes.TAG_GUID_REQUIRED));

		}

		if (listOfError.isEmpty()
				&& (guestTagModel.getTagName() == null || guestTagModel
						.getTagName().equals(""))) {
			if (guestTagModel.getTagGuid() == null
					|| guestTagModel.getTagGuid().equals(""))
				listOfError.add(new ValidationError(Constants.TAG_NAME,
						UtilityMethods
								.getErrorMsg(ErrorCodes.TAG_NAME_REQUIRED),
						ErrorCodes.TAG_NAME_REQUIRED));

		}

		if (listOfError.isEmpty()) {
			// By Restaurant : All Validations
			Restaurant res = restDao.findRestaurantByGuid(guestTagModel
					.getRestaurantGuid());
			if (res == null) {
				listOfError.add(new ValidationError(Constants.REST_GUID,
						UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REST_ID),
						ErrorCodes.INVALID_REST_ID));
			} else {

				Map<String, Object> params1 = new HashMap<String, Object>();
				params1.put(Constants.GUID, guestTagModel.getGuestGuid());
				params1.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				GuestProfile guest_prof = guestDao.findGuest(params1);
				if (guest_prof == null) { // NO NEED
					listOfError
							.add(new ValidationError(
									Constants.GUID,
									UtilityMethods
											.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID),
									ErrorCodes.INVALID_GUEST_GUID));
				} else {
					// no need of if condtion
					boolean isGuestForRest = guestDao.isGuestForRest(
							guestTagModel.getGuestGuid(),
							guestTagModel.getRestaurantGuid());
					if (!isGuestForRest) {
						// list of error ...Not guest of restaurant.
						listOfError
								.add(new ValidationError(
										Constants.GUEST_GUID,
										UtilityMethods
												.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID),
										ErrorCodes.INVALID_GUEST_GUID));
					} else {
						if (guestTagModel.getTagGuid() != null) {
							Map<String, Object> guestTagParams = new HashMap<String, Object>();
							guestTagParams.put(Constants.GUID,
									guestTagModel.getTagGuid());
							guestTagParams.put(Constants.STATUS,
									Constants.ACTIVE_STATUS);
							int guestTagListSize = guestTagDao.findByFields(
									Tag.class, guestTagParams).size();
							if (guestTagListSize != 1) {
								listOfError
										.add(new ValidationError(
												Constants.GUID,
												UtilityMethods
														.getErrorMsg(ErrorCodes.INVALID_TAG_GUID),
												ErrorCodes.INVALID_TAG_GUID));
							} else {

								Map<String, Object> guestTagParams1 = new HashMap<String, Object>();
								guestTagParams1.put(Constants.GUID,
										guestTagModel.getTagGuid());
								guestTagParams1.put(Constants.STATUS,
										Constants.ACTIVE_STATUS);
								guestTagParams1.put(Constants.REST_GUID,
										guestTagModel.getRestaurantGuid());
								int count = guestTagDao.findByFields(Tag.class,
										guestTagParams1).size();

								if (count != 1) {
									// Not a valid tag guid
									listOfError
											.add(new ValidationError(
													Constants.GUID,
													UtilityMethods
															.getErrorMsg(ErrorCodes.INVALID_TAG_GUID),
													ErrorCodes.INVALID_TAG_GUID));

								} else {

									Map<String, Object> guestTagParams2 = new HashMap<String, Object>();
									guestTagParams2.put(Constants.GUID,
											guestTagModel.getTagGuid());
									guestTagParams2.put(Constants.STATUS,
											Constants.ACTIVE_STATUS);
									guestTagParams2.put(Constants.GUEST_GUID,
											guestTagModel.getGuestGuid());
									int count1 = guestTagDao.findByFields(
											Tag.class, guestTagParams2).size();

									if (count1 != 0) {
										// already exists
										listOfError
												.add(new ValidationError(
														Constants.TAG_GUID_LABEL,
														UtilityMethods
																.getErrorMsg(ErrorCodes.TAG_EXIST),
														ErrorCodes.TAG_EXIST));

									}
								}

							}
						} else if (guestTagModel.getTagName() != null) {

							Map<String, Object> guestTagParams1 = new HashMap<String, Object>();
							guestTagParams1.put(Constants.TAG_NAME,
									guestTagModel.getTagName());
							guestTagParams1.put(Constants.STATUS,
									Constants.ACTIVE_STATUS);
							guestTagParams1.put(Constants.REST_GUID,
									guestTagModel.getRestaurantGuid());
							int restTagListCount = guestTagDao.findByFields(
									Tag.class, guestTagParams1).size();

							if (restTagListCount > 0) {
								// Tag with same name already exists!
								listOfError
										.add(new ValidationError(
												Constants.TAG_GUID_LABEL,
												UtilityMethods
														.getErrorMsg(ErrorCodes.TAG_EXIST_IN_RESTAURANT),
												ErrorCodes.TAG_EXIST_IN_RESTAURANT));

							} else {

								Map<String, Object> guestTagParams3 = new HashMap<String, Object>();
								guestTagParams3.put(Constants.STATUS,
										Constants.ACTIVE_STATUS);
								guestTagParams3.put(Constants.GUEST_GUID,
										guestTagModel.getGuestGuid());
								guestTagParams3.put(Constants.REST_GUID,
										guestTagModel.getRestaurantGuid());
								guestTagParams3.put(Constants.TYPE,
										Constants.TAG_PREFERENCES);
								int tagListGuestRestCount = guestTagDao
										.findByFields(Tag.class,
												guestTagParams3).size();

								if (tagListGuestRestCount >= 5) {
									// Restaurant can add max 5 tags to a
									// guest
									listOfError
											.add(new ValidationError(
													Constants.GUID,
													UtilityMethods
															.getErrorMsg(ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_REST),
													ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_REST));

								}
							}

						}

						if (listOfError.isEmpty()) {

							Map<String, Object> guestTagParams3 = new HashMap<String, Object>();
							guestTagParams3.put(Constants.STATUS,
									Constants.ACTIVE_STATUS);
							guestTagParams3.put(Constants.GUEST_GUID,
									guestTagModel.getGuestGuid());
							guestTagParams3.put(Constants.REST_GUID,
									guestTagModel.getRestaurantGuid());
							guestTagParams3.put(Constants.TYPE,
									Constants.TAG_PREFERENCES);
							int tagListGuestRestCount = guestTagDao
									.findByFields(Tag.class, guestTagParams3)
									.size();
							if (tagListGuestRestCount >= 5) {
								// Restaurant can add max 5 tags to
								// a
								// guest
								listOfError
										.add(new ValidationError(
												Constants.GUID,
												UtilityMethods
														.getErrorMsg(ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_REST),
												ErrorCodes.MORE_THAN_FIVE_PREFERENCES_BY_REST));

							}
						}
					}
				}

			}
		}
		return listOfError;
	}

	public List<ValidationError> validateGuestTagsByGuestOnRemove(
			GuestTagModel guestTagModel, List<ValidationError> listOfError) {

		if (guestTagModel.getGuestGuid() == null) {
			listOfError.add(new ValidationError(Constants.GUEST_GUID,
					UtilityMethods.getErrorMsg(ErrorCodes.GUEST_GUID_REQUIRED),
					ErrorCodes.GUEST_GUID_REQUIRED));

		}

		if (listOfError.isEmpty() && guestTagModel.getTagGuid() == null) {
			listOfError.add(new ValidationError(Constants.TAG_GUID_LABEL,
					UtilityMethods.getErrorMsg(ErrorCodes.TAG_GUID_REQUIRED),
					ErrorCodes.TAG_GUID_REQUIRED));

		}

		// By Guest : All Validations
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put(Constants.GUID, guestTagModel.getGuestGuid());
		params1.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		GuestProfile guest_prof = guestDao.findGuest(params1);	//camelCase
		if (guest_prof == null || !guest_prof.getStatus().equals(Constants.ACTIVE_STATUS)) {  // never match !guest_prof.getStatus().equals(Constants.ACTIVE_STATUS) condition
			listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID), ErrorCodes.INVALID_GUEST_GUID));
		} else {


			Map<String, Object> guestTagParams = new HashMap<String, Object>();
			guestTagParams.put(Constants.GUID, guestTagModel.getTagGuid());
			guestTagParams.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			int guestTagListSize = guestTagDao.findByFields(Tag.class,
					guestTagParams).size();
			if (guestTagListSize != 1) {
				listOfError
						.add(new ValidationError(Constants.GUID, UtilityMethods
								.getErrorMsg(ErrorCodes.INVALID_TAG_GUID),
								ErrorCodes.INVALID_TAG_GUID));
			} else {


				Map<String, Object> guestTagParams1 = new HashMap<String, Object>();
				guestTagParams1.put(Constants.GUEST_GUID,
						guestTagModel.getGuestGuid());
				guestTagParams1.put(Constants.GUID, guestTagModel.getTagGuid());



				int count1 = guestTagDao.findByFields(Tag.class,
						guestTagParams1).size();
				if (count1 != 1) {
					// already exists
					listOfError.add(new ValidationError(
							Constants.TAG_GUID_LABEL, UtilityMethods
									.getErrorMsg(ErrorCodes.INVALID_TAG_GUID),
							ErrorCodes.INVALID_TAG_GUID));

				}

			}

		}

		return listOfError;
	}

	public List<ValidationError> validateGuestTagsByRestaurantOnRemove(
			GuestTagModel guestTagModel, List<ValidationError> listOfError) {

		if (guestTagModel.getGuestGuid() == null) {
			listOfError.add(new ValidationError(Constants.GUEST_GUID,
					UtilityMethods.getErrorMsg(ErrorCodes.GUEST_GUID_REQUIRED),
					ErrorCodes.GUEST_GUID_REQUIRED));

		}

		if (listOfError.isEmpty() && guestTagModel.getTagGuid() == null) {
			listOfError.add(new ValidationError(Constants.TAG_GUID_LABEL,
					UtilityMethods.getErrorMsg(ErrorCodes.TAG_GUID_REQUIRED),
					ErrorCodes.TAG_GUID_REQUIRED));

		}
		if (listOfError.isEmpty()) {
			// By Restaurant : All Validations

			Restaurant res = restDao.findRestaurantByGuid(guestTagModel
					.getRestaurantGuid());
			if (res == null) {
				listOfError.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REST_ID), ErrorCodes.INVALID_REST_ID));
			} else {
				Map<String, Object> params1 = new HashMap<String, Object>();
				params1.put(Constants.GUID, guestTagModel.getGuestGuid());
				params1.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				GuestProfile guest_prof = guestDao.findGuest(params1);
				if (guest_prof == null) { // NO NEED 
					listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID), ErrorCodes.INVALID_GUEST_GUID));
				} else {
					boolean isGuestForRest = guestDao.isGuestForRest(
							guestTagModel.getGuestGuid(),
							guestTagModel.getRestaurantGuid());
					if (!isGuestForRest) {
						// list of error ...Not guest of restaurant.
						listOfError.add(new ValidationError(Constants.GUEST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID), ErrorCodes.INVALID_GUEST_GUID));
					} else {
						Map<String, Object> params2 = new HashMap<String, Object>();
						params2.put(Constants.GUID, guestTagModel.getTagGuid());
						params2.put(Constants.STATUS, Constants.ACTIVE_STATUS);

						List<Tag> guest_tag_list = guestTagDao.findByFields(
								Tag.class, params2);
						int guest_tag_list_size = guest_tag_list.size();
						if (guest_tag_list_size != 1) {
							listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_TAG_GUID), ErrorCodes.INVALID_TAG_GUID));
						} else {


							// Rastaurant Staff can not remove tags other then
							// preferences.
							if (!guest_tag_list.get(0).getType()
									.equals(Constants.TAG_PREFERENCES)) {
								listOfError
										.add(new ValidationError(
												Constants.GUID,
												UtilityMethods
														.getErrorMsg(ErrorCodes.INVALID_TAG_TYPE_TO_REMOVE),
												ErrorCodes.INVALID_TAG_TYPE_TO_REMOVE));
							} else {

								Map<String, Object> params3 = new HashMap<String, Object>();
								params3.put(Constants.GUID,
										guestTagModel.getTagGuid());
								params3.put(Constants.REST_GUID,
										guestTagModel.getRestaurantGuid());

								int count = guestTagDao.findByFields(Tag.class,
										params3).size();
								if (count != 1) {
									// Not a valid tag guid
									listOfError
											.add(new ValidationError(
													Constants.TAG_GUID_LABEL,
													UtilityMethods
															.getErrorMsg(ErrorCodes.INVALID_TAG_GUID),
													ErrorCodes.INVALID_TAG_GUID));

								} else {
									Map<String, Object> params4 = new HashMap<String, Object>();
									params4.put(Constants.GUID,
											guestTagModel.getTagGuid());
									params4.put(Constants.GUEST_GUID,
											guestTagModel.getGuestGuid());
									params4.put(Constants.REST_GUID,
											guestTagModel.getRestaurantGuid());
									int count1 = guestTagDao.findByFields(
											Tag.class, params4).size();
									if (count1 != 1) {
										// already exists
										listOfError
												.add(new ValidationError(
														Constants.TAG_GUID_LABEL,
														UtilityMethods
																.getErrorMsg(ErrorCodes.INVALID_TAG_GUID),
														ErrorCodes.INVALID_TAG_GUID));

									}
								}
							}
						}

					}
				}

			}
		}
		return listOfError;
	}

	public Map<String, List<String>> validateEventandOffersGuestTagsByRestaurantOnAdd(
			String guest_guid, Map<String, Object> params,
			List<String> tagNameList, List<ValidationError> listOfError) {
		Map<String, List<String>> return_map = new HashMap<String, List<String>>();

		List<String> restTagGuidListOfName = new ArrayList<String>();

		List<String> new_tag_name_list = new ArrayList<String>();
		List<String> existing_rest_tagGuid_list = new ArrayList<String>();
		List<String> existing_rest_and_Guest_tagGuid_list = new ArrayList<String>();
		List<String> existing_rest_no_guest_tagGuid_list = new ArrayList<String>();
		// By Restaurant : All Validations
		Restaurant res = restDao.findRestaurantByGuid(params.get(
				Constants.REST_GUID).toString());
		if (res == null) {
			listOfError.add(new ValidationError(Constants.REST_GUID,
					UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REST_ID),
					ErrorCodes.INVALID_REST_ID));
		} else {
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put(Constants.GUID, guest_guid);
			params1.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			GuestProfile guest_prof = guestDao.findGuest(params1);
			if (guest_prof == null) {
				listOfError.add(new ValidationError(Constants.GUID,
						UtilityMethods
								.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID),
						ErrorCodes.INVALID_GUEST_GUID));
			} else {
				boolean isGuestForRest = guestDao.isGuestForRest(guest_guid,
						params.get(Constants.REST_GUID).toString());
				if (!isGuestForRest) {
					// list of error ...Not guest of restaurant.
					listOfError
							.add(new ValidationError(
									Constants.GUEST_GUID,
									UtilityMethods
											.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID),
									ErrorCodes.INVALID_GUEST_GUID));
				} else {
					String tagNameCollection = "";
					if (tagNameList.size() > 0) {
						for (String tagName : tagNameList) {
							tagNameCollection = tagNameCollection + ","
									+ tagName;
						}
					}
					
					if(!tagNameCollection.equals("")){
						tagNameCollection  = tagNameCollection.substring(1);
					}
					
					Map<String, Object> guestTagParams1 = new HashMap<String, Object>();
					guestTagParams1.put(Constants.TAG_NAME, tagNameCollection);
					guestTagParams1.put(Constants.STATUS,
							Constants.ACTIVE_STATUS);
					guestTagParams1.put(Constants.REST_GUID,
							params.get(Constants.REST_GUID).toString());
					List<Tag> restTagList = guestTagDao.findByFields(Tag.class,
							guestTagParams1);
					if (restTagList.size()>0) {
						for (Tag restTag : restTagList) {
							for (String tagName : tagNameList) {
								if (restTag.getName().equals(tagName)) {
									existing_rest_tagGuid_list.add(restTag
											.getGuid());
									restTagGuidListOfName
											.add(tagName);
									// break;
								} else {
									new_tag_name_list.add(tagName);
									// break;
								}
							}

						}
						Set<String> hs = new HashSet<>();
						hs.addAll(new_tag_name_list);
						new_tag_name_list.clear();
						new_tag_name_list.addAll(hs);
						new_tag_name_list.removeAll(restTagGuidListOfName);
					}else{
						new_tag_name_list.addAll(tagNameList);
					}
					
					for (String tag_Guid : existing_rest_tagGuid_list) {

						Map<String, Object> guestTagParams2 = new HashMap<String, Object>();
						guestTagParams2.put(Constants.TAG_GUID, tag_Guid);
						guestTagParams2.put(Constants.STATUS,
								Constants.ACTIVE_STATUS);
						guestTagParams2.put(Constants.REST_GUID,
								params.get(Constants.REST_GUID).toString());
						guestTagParams2.put(Constants.GUEST_GUID, guest_guid);

						if (guestTagDao
								.findByFields(Tag.class, guestTagParams2)
								.size() != 1) {
							existing_rest_no_guest_tagGuid_list.add(tag_Guid);
						} else {
							existing_rest_and_Guest_tagGuid_list.add(tag_Guid);
						}
					}

				}
			}

		}

		return_map.put("tagGuids", existing_rest_no_guest_tagGuid_list);
		return_map.put("tagNames", new_tag_name_list);
		return_map
				.put("existingTagGuids", existing_rest_and_Guest_tagGuid_list);
		return return_map;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.TAG_GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_TAG_ID;
	}
}
