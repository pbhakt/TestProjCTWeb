package com.clicktable.model;

import java.util.List;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ComplaintRequest{
	@Required(message=ErrorCodes.SUBJECT_REQUIRED_SUPPORT)
	@MaxLength(message=ErrorCodes.SUBJECT_MAXLENGTH_SUPPORT,value=200)
	private String subject;
	@Required(message=ErrorCodes.ACCOUNT_ID_REQUIRED_SUPPORT)
	private String accountId;
	@Required(message=ErrorCodes.RESTAURANT_NAME_REQUIRED_SUPPORT)
	private String restaurantName;
	@Email(message=ErrorCodes.INVALID_USERNAME_SUPPORT)
	@Required(message=ErrorCodes.USERNAME_REQUIRED_SUPPORT)
	private String username;
	@MaxLength(message=ErrorCodes.DEVICE_MAXLENGTH_SUPPORT,value=50)
	@Required(message=ErrorCodes.DEVICE_REQUIRED_SUPPORT)
	private String device;
	@MaxLength(message=ErrorCodes.OS_MAXLENGTH_SUPPORT,value=50)
	@Required(message=ErrorCodes.OS_REQUIRED_SUPPORT)
	private String os;
	@Required(message=ErrorCodes.ISSUE_TYPE_REQUIRED_SUPPORT)
	private String issueType;
	@MaxLength(message=ErrorCodes.DESCRIPTION_MAXLENGTH_SUPPORT,value=1000)
	@Required(message=ErrorCodes.DESCRIPTION_REQUIRED_SUPPORT)
	private String description;
	
	private List<Long> attachmentIds;
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getRestaurantName() {
		return restaurantName;
	}
	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getIssueType() {
		return issueType;
	}
	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Long> getAttachmentIds() {
		return attachmentIds;
	}
	public void setAttachmentIds(List<Long> attachmentIds) {
		this.attachmentIds = attachmentIds;
	}
	

}
