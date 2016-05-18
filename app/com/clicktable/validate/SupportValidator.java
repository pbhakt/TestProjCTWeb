package com.clicktable.validate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import play.Logger;

import com.clicktable.model.ComplaintRequest;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class SupportValidator extends EntityValidator<ComplaintRequest> {

	public List<ValidationError> validateComplaintRequestOnAdd(ComplaintRequest complaintReq) {
		List<ValidationError> errorList = validateOnAdd(complaintReq);
		if (complaintReq.getAttachmentIds().size() > 8) {
			errorList.add(new ValidationError(Constants.ATTACHMENT_IDS, UtilityMethods.getErrorMsg(ErrorCodes.ATTACHMENT_MAX), ErrorCodes.ATTACHMENT_MAX));
		}
		if (!UtilityMethods.getEnumValues(Constants.TICKET, Constants.ISSUE_TYPE).contains(complaintReq.getIssueType()))
			errorList.add(new ValidationError(Constants.ISSUE_TYPE, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_ISSUE_TYPE), ErrorCodes.INVALID_ISSUE_TYPE));
		return errorList;
	}

	public List<ValidationError> validateAttachment(File file, String fileName) {

		List<ValidationError> errorList = new ArrayList<ValidationError>();

		if (file == null)
			errorList.add(new ValidationError(Constants.ATTACHMENT, UtilityMethods.getErrorMsg(ErrorCodes.ATTACHMENT_REQUIRED), ErrorCodes.INVALID_ATTACHMENT));
		else {
			try {
				if (ImageIO.read(file) == null) {
					errorList.add(new ValidationError(Constants.ATTACHMENT, UtilityMethods.getErrorMsg(ErrorCodes.ATTACHMENT_REQUIRED), ErrorCodes.INVALID_ATTACHMENT));
				} else {
					double megabytes = (((double)file.length()) / (1024 * 1024));
					if (megabytes > 0.99) {
						errorList.add(new ValidationError(Constants.ATTACHMENT, UtilityMethods.getErrorMsg(ErrorCodes.FILE_SIZE), ErrorCodes.FILE_SIZE));
						System.out.println("............done..........");
					}else if (!UtilityMethods.getEnumValues(Constants.ATTACHMENT, Constants.FILE_EXT).contains(fileName.split("[.]")[1].toUpperCase()))
						errorList.add(new ValidationError(Constants.ATTACHMENT, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_FILE_NAME), ErrorCodes.INVALID_FILE_NAME));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Logger.debug("Exception is ------------ " + e.getLocalizedMessage());
				
				errorList.add(new ValidationError(Constants.ATTACHMENT, UtilityMethods.getErrorMsg(ErrorCodes.ATTACHMENT_REQUIRED), ErrorCodes.INVALID_ATTACHMENT));
			}

		}
		return errorList;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_GUID;
	}

}
