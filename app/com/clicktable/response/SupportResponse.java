package com.clicktable.response;

import play.i18n.Messages;

public class SupportResponse<T> extends BaseResponse {

	public SupportResponse(String responseCode, T object) {
		this.setResponseStatus(true);
		this.setResponseCode(responseCode);
		this.setResponseMessage(Messages.get(responseCode));
		this.setObj(object);
	}

	public SupportResponse() {
		super();
	}

	private T obj;

	public T getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}
	
	

}
