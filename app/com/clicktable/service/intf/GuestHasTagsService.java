package com.clicktable.service.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.TagModelOld;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface GuestHasTagsService {

	public BaseResponse addTagRestaurant(List<TagModelOld> listTag, String token);

	public BaseResponse removeGuestHasTag(String guid, List<TagModelOld> tags,
			String rest_guid);

	public BaseResponse removeTag(List<TagModelOld> tags);

	public BaseResponse addGuestProfileTag(String guid, TagModelOld tag, String header);

	public BaseResponse getGuestHasTag(Map<String, Object> stringParamMap,
			String header);

	public BaseResponse getTag(Map<String, Object> stringParamMap, String header);

	//BaseResponse addGuestProfileEventTag(String guid, String rest_guid,
		//	List<TagModelOld> tag, String token);

}
