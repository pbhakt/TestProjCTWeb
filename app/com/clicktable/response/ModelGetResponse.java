package com.clicktable.response;

import play.i18n.Messages;

public class ModelGetResponse<T> extends BaseResponse {
	
	public ModelGetResponse(String responseCode, T data , Boolean status) {
		this.setResponseStatus(status);
		this.setResponseCode(responseCode);		
		this.setResponseMessage(Messages.get(responseCode));
		this.setData(data);
	}
	


	public ModelGetResponse() {
		super();
	}



	private T data;



	public T getData() {
	    return data;
	}



	public void setData(T data) {
	    this.data = data;
	}

	
}
