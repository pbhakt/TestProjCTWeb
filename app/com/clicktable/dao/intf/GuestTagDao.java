/**
 * 
 */
package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.GuestTagModel;
import com.clicktable.model.Tag;

/**
 * @author p.vishwakarma
 *
 */
@org.springframework.stereotype.Service
public interface GuestTagDao  extends GenericDao<Tag> {

	String addNewTagWithGuest_ByGuest(Tag new_tag, String guest_guid);

	String addNewTagWithGuest_ByRestaurant(Tag new_tag, String guest_guid,
			String restGuid);

	String removeGuestTagOfCustomer_ByGuest(Map<String, Object> params);

	String removeGuestTagOfCustomer_ByRestaurant(Map<String, Object> params);

	String addExistingTagWithGuest(GuestTagModel guestTagModel);

	void addNewEventandOfferGuestTagWithGuest_ByRestaurant(
			List<Tag> tagslist, String guestGuid, String restGuid);

	void addExistingEventandOfferGuestTagWithGuest_ByRestaurant(
			List<String> existing_tag_guid_list, String guestGuid,
			String restGuid);

	void addExistingRestandGuestEventandOfferGuestTagWithGuest_ByRestaurant(
			List<String> existing_tag_guid_list, String guestGuid,
			String restGuid);

	void mergingTag(List<Tag> existing_tag_guid_list);

	List<Tag> getTagMergingTest(List<String> existing_tag_guid_list);

	void cleanup();
}
