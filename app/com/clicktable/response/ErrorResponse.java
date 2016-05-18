package com.clicktable.response;

import java.util.List;

import play.Logger;
import play.i18n.Messages;

import com.clicktable.validate.ValidationError;

public class ErrorResponse extends BaseResponse 
{
	List<ValidationError> errorList;
	
	

	public ErrorResponse( String responseCode, List<ValidationError> errorList)
	{
	        Logger.debug("creating error response " + responseCode);
		this.setResponseStatus(false);
		this.setResponseCode(responseCode);		
		this.setResponseMessage(Messages.get(responseCode));
		this.errorList = errorList;
	}

	public List<ValidationError> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<ValidationError> errorList) {
		this.errorList = errorList;
	}	

}
