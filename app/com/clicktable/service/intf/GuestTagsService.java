package com.clicktable.service.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.GuestTagModel;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface GuestTagsService {


	public BaseResponse getTag(Map<String, Object> stringParamMap, String header);

	public BaseResponse addGuestTag(GuestTagModel guestTagModel, String token);

	public BaseResponse removeGuestTag(GuestTagModel guestTagModel, String token);

	public void addGuestProfileEventTag(String guestGuid, List<String> tagNameList,
			String token);

	public BaseResponse mergeTag(String header);


}
