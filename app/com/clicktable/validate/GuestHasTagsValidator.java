/**
 * 
 */
package com.clicktable.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;

import ch.qos.logback.classic.Logger;

import com.clicktable.model.TagModelOld;
import com.clicktable.repository.GuestProfileRepo;
import com.clicktable.repository.TagPreferencesRepo;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;

/**
 * @author a.thakur
 *
 */
@org.springframework.stereotype.Service
public class GuestHasTagsValidator extends EntityValidator<TagModelOld> {

	public static final Logger log = (Logger) LoggerFactory.getLogger(GuestHasTagsValidator.class);

	@Autowired
	GuestProfileRepo guest_profile_tag_repo;

	@Autowired
	TagPreferencesRepo tag_repo;

	@Autowired
	Neo4jOperations template;

	public boolean validateGuestProfileGUID(String guestProfile_guid) {
		boolean guest_profile_exist = false;

		int count_row = guest_profile_tag_repo.count_row(guestProfile_guid);

		log.info("Validating Guest Profile GUID " + count_row);
		if (count_row != 0) {
			guest_profile_exist = true;
		}
		return guest_profile_exist;

	}

	public boolean validateTags(String tag_guid[]) {
		// TODO Auto-generated method stub
		boolean tag_profile_exist = false;
		for (int i = 0; i < tag_guid.length; i++) {

			int count_row = tag_repo.count_row(tag_guid[i]);

			log.info("Validating Tag Profile GUID " + count_row);
			if (count_row != 0) {
				tag_profile_exist = true;
			}
		}
		return tag_profile_exist;
	}

	public boolean validateTag(String tag_guid) {
		// TODO Auto-generated method stub
		boolean tag_profile_exist = false;

		int count_row = tag_repo.count_row(tag_guid);

		log.info("Validating Tag Profile GUID " + count_row);
		if (count_row != 0) {
			tag_profile_exist = true;

		}
		return tag_profile_exist;
	}

	public boolean validateRelationship(String relationship, String guest_guid) {
		// TODO Auto-generated method stub
		boolean guest_has_tag_rel_exist = false;

		int count_row = guest_profile_tag_repo.count_guest_has_tag_exist(guest_guid);

		log.info("Validating GUEST_HAS_RELATIONSHIP Exist " + count_row);
		if (count_row != 0) {
			guest_has_tag_rel_exist = true;

		}
		return guest_has_tag_rel_exist;

	}

	public Map<String, Object> validateFinderParams(Map<String, Object> params, Class type) {
		Map<String, Object> validParamMap = new HashMap<String, Object>();
		for (Entry<String, Object> entry : params.entrySet()) {

			if (null != entry.getKey() && !params.containsKey("CLASS")) {
				if (entry.getKey().equals(Constants.GUEST_GUID)) {
					if (null != entry.getValue())
						validParamMap.put(Constants.GUEST_GUID, entry.getValue());
				} else if (entry.getKey().equals(Constants.REST_GUID)) {
					if (null != entry.getValue())
						validParamMap.put(Constants.REST_GUID, entry.getValue());
				} else if (entry.getKey().equals(Constants.TAG_GUIDS)) {
					if (null != entry.getValue())
						validParamMap.put(Constants.TAG_GUIDS, entry.getValue());
				} /*else {
					validParamMap.putAll(super.validateFinderParams(params, type));
				}*/
			}

		}
		validParamMap.putAll(super.validateFinderParams(params, type));
		return validParamMap;

	}

	public List<ValidationError> validateEnum(List<TagModelOld> tag) {
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		for (TagModelOld tagModel : tag) {
			errorList.addAll(validateEnumValues(tagModel, Constants.TAG_MODULE));
		}
		return errorList;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.TAG_GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_TAG_GUID;
	}
}
