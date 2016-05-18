package com.clicktable.service.intf;

import java.io.File;

import org.zendesk.client.v2.model.Attachment;

import com.clicktable.model.ComplaintRequest;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface SupportService {

	BaseResponse submitTicket(ComplaintRequest complaintReq);

	BaseResponse deleteAttachment(Attachment attachment);

	BaseResponse addAttachment(File file, String fileName);

	BaseResponse getToken();


	
}
