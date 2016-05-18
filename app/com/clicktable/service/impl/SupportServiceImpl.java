package com.clicktable.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.zendesk.client.v2.Zendesk;
import org.zendesk.client.v2.model.Attachment;
import org.zendesk.client.v2.model.Comment;
import org.zendesk.client.v2.model.CustomFieldValue;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.Ticket.Requester;

import com.clicktable.model.ComplaintRequest;
import com.clicktable.model.SupportCredentials;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.DeleteResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.SupportResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.SupportService;
import com.clicktable.util.Constants;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.SupportValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class SupportServiceImpl implements SupportService {

	static Zendesk zd = new Zendesk.Builder(UtilityMethods.getConfString(Constants.SUPPORT_URL)).setUsername(UtilityMethods.getConfString(Constants.SUPPORT_USERNAME))
			.setPassword(UtilityMethods.getConfString(Constants.SUPPORT_PASSWORD)).build();

	@Autowired
	SupportValidator supportValidator;

	@Autowired
	AuthorizationService authService;

	@Override
	public BaseResponse submitTicket(ComplaintRequest complaintReq) {
		BaseResponse response;
		List<ValidationError> listOfError = supportValidator.validateComplaintRequestOnAdd(complaintReq);
		if (listOfError.isEmpty()) {
			Requester requester = new Requester(complaintReq.getUsername());
			requester.setName(complaintReq.getRestaurantName());
			Ticket ticket = new Ticket();
			ticket.setRequester(requester);
			ticket.setSubject(complaintReq.getSubject());
			Comment comment = new Comment(complaintReq.getDescription());
			List<Attachment> attachments = new ArrayList<Attachment>();
			complaintReq.getAttachmentIds().forEach(x -> {
				Attachment attachment = new Attachment();
				attachment.setId(x);
				attachments.add(attachment);
			});
			comment.setAttachments(attachments);
			ticket.setComment(comment);
			List<CustomFieldValue> customFields = new ArrayList<CustomFieldValue>();
			customFields.add(new CustomFieldValue(Constants.SUPPORT_ACCOUNT_ID, complaintReq.getAccountId()));
			customFields.add(new CustomFieldValue(Constants.SUPPORT_RESTAURANT_NAME, complaintReq.getRestaurantName()));
			customFields.add(new CustomFieldValue(Constants.SUPPORT_DEVICE, complaintReq.getDevice()));
			customFields.add(new CustomFieldValue(Constants.SUPPORT_OS, complaintReq.getOs()));
			customFields.add(new CustomFieldValue(Constants.SUPPORT_ISSUE_TYPE, complaintReq.getIssueType()));
			ticket.setCustomFields(customFields);
			Ticket uploadedTicket = zd.createTicket(ticket);
			response = new SupportResponse<Ticket>(ResponseCodes.TICKET_SUCCESS, uploadedTicket);
		} else {
			response = new ErrorResponse(ResponseCodes.TICKET_FAILURE, listOfError);
		}
		return response;
	}

	@Override
	public BaseResponse addAttachment(File file, String fileName) {
		BaseResponse response;
		List<ValidationError> listOfError = supportValidator.validateAttachment(file, fileName);
		if (listOfError.isEmpty()) {

			// byte[] contents = new byte[(int) file.length()];
			byte[] contents = new byte[(int) file.length()];
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				fis.read(contents);
				fis.close();
			} catch (Exception e) { // TODOAuto-generated catch block
				e.printStackTrace();
			}
			Attachment.Upload upload = zd.createUpload(fileName, contents);
			response = new SupportResponse<Attachment.Upload>(ResponseCodes.ATTACHMENT_SUCCESS, upload);
		} else {
			return new ErrorResponse(ResponseCodes.ATTACHMENT_FAILURE, listOfError);
		}
		return response;
	}

	@Override
	public BaseResponse deleteAttachment(Attachment attachment) {
		BaseResponse response;
		zd.deleteAttachment(attachment);
		response = new DeleteResponse(ResponseCodes.ATTACHMENT_DELETED_SUCCESFULLY, attachment.getId());
		return response;
	}

	@Override
	public BaseResponse getToken(){
		BaseResponse response;
		SupportCredentials token =  new SupportCredentials();
		token.setUsername(UtilityMethods.getConfString(Constants.SUPPORT_USERNAME));
		token.setToken(UtilityMethods.getConfString(Constants.SUPPORT_TOKEN));
		response = new SupportResponse<SupportCredentials>(ResponseCodes.TOKEN_SUCCESS, token);
		return response;
	}

}
