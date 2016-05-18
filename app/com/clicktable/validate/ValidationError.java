package com.clicktable.validate;

import com.clicktable.util.UtilityMethods;


public class ValidationError {
	
	private String fieldName;
	private String errorCode;
	private String info;
	private Object errorMessage;
		
	public ValidationError(String fieldName, Object errorMessage) {
		this.fieldName = fieldName;
		this.errorMessage = errorMessage;
	}
	
	public ValidationError(String fieldName, String errorCode) {
		this(fieldName, UtilityMethods.getErrorMsg(errorCode), errorCode);
	}
	
	
	public ValidationError(String fieldName, Object errorMessage, String errorCode) 
	{
		this(fieldName,errorMessage);
		this.errorCode = errorCode;
		
	}
	public ValidationError(String fieldName, Object errorMessage, String errorCode,String info) 
	{
		this(fieldName,errorMessage,errorCode);
		this.info = info;
		
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Object getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(Object errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
	    return errorCode;
	}

	public void setErrorCode(String errorCode) {
	    this.errorCode = errorCode;
	}

	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	
	
	

}
