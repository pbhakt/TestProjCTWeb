package com.clicktable.response;

import java.util.List;

import play.i18n.Messages;

import com.clicktable.validate.ValidationError;

public class UpdateResponse<T> extends BaseResponse {

	private List<ValidationError> errors;

	private String guid;
	private Object entityObject;
	
	
	public UpdateResponse(String responseCode,  String guid) {
		this.setResponseStatus(true);
		this.setResponseCode(responseCode);		
		this.setResponseMessage(Messages.get(responseCode));
		this.guid = guid;
	}
	
	public UpdateResponse(String responseCode,  Object entityObject) {
		this.setResponseStatus(true);
		this.setResponseCode(responseCode);		
		this.setResponseMessage(Messages.get(responseCode));
		this.entityObject = entityObject;
	}

	public List<ValidationError> getErrors() {
		return errors;
	}

	public void setErrors(List<ValidationError> errors) {
		this.errors = errors;
	}

	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Object getEntityObject() {
		return entityObject;
	}

	public void setEntityObject(Object entityObject) {
		this.entityObject = entityObject;
	}
	
	
	
	

	
}
