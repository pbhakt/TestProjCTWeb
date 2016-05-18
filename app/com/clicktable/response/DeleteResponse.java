package com.clicktable.response;

import play.i18n.Messages;

public class DeleteResponse extends BaseResponse {

	public DeleteResponse(String responseCode, Object id) {
		this.setResponseStatus(true);
		this.setResponseCode(responseCode);
		this.setResponseMessage(Messages.get(responseCode));
		this.setId(id);
	}

	public DeleteResponse() {
		super();
	}

	private Object id;

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

}
