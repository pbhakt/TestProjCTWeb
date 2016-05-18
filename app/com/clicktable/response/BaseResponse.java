package com.clicktable.response;

import java.text.SimpleDateFormat;

import play.Logger;
import play.i18n.Messages;
import play.libs.Json;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseResponse {

	private String responseCode;
	private String responseMessage;
	private Boolean responseStatus;
	private Object object;

	
	
	public BaseResponse() {
		super();		
	}

	public BaseResponse(String responseCode, boolean status, Object object) {
		if (null != responseCode) {
			this.setResponseCode(responseCode);
			this.setResponseMessage(Messages.get(responseCode));
			this.setResponseStatus(status);
			this.setObject(object);
		}
		
	}

	public void createResponse(String message, boolean status, Object... obj) {
		if (null != message) {
			this.setResponseCode(message);
			this.setResponseMessage(Messages.get(message, obj));
			this.setResponseStatus(status);
		}
	}
	
	public JsonNode formatDateToJson() {
		ObjectMapper ow = new ObjectMapper();
		ow.writer().with(new SimpleDateFormat(Constants.TIMESTAMP_FORMAT));
		Logger.info("within date format json");

		String json = "";
		try {
			json = ow.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Json.parse(json);
	}
	
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Boolean getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(Boolean responseStatus) {
		this.responseStatus = responseStatus;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}
