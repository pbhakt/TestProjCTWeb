/**
 * 
 */
package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.TagModelOld;

/**
 * @author a.thakur
 *
 */
@org.springframework.stereotype.Service
public interface GuestHasTagsDao  extends GenericDao<TagModelOld> {

	public List<String> addTags(List<TagModelOld> tag_model);

	public List<String> removeGuestHasTagRelationship(String guid, List<TagModelOld> tags,
			String rest_guid);

	public List<TagModelOld> getTagsForGuest(Map<String, Object> params);

	public String addGuestTagRelationship(String guestProfile_guid,
			String rest_guid, List<TagModelOld> tag,long roleId);
	/*Need to Remove*/
	public String addGuestTagRelationship(String guestProfile_guid,
			String rest_guid, List<TagModelOld> tag);
	public String addGuestEventTagRelationship(String guid, String rest_guid,
			List<TagModelOld> tagList);

	public List<String> removeTag(List<TagModelOld> tagList);

	public List<TagModelOld> getTag(Class<TagModelOld> class1, Map<String, Object> qryParamMap);


	public Map<String, Object> validateRestWithTagCount(
			Map<String, Object> params);

	Map<String, Object> validateGuestWithTagCount(Map<String, Object> params);

}
