package com.clicktable.response;

import java.util.List;

import play.i18n.Messages;

public class GetResponse<T> extends BaseResponse 
{
	
	public GetResponse(String responseCode, List<T> resultlist) {
		this.setResponseStatus(true);
		this.setResponseCode(responseCode);		
		this.setResponseMessage(Messages.get(responseCode));
		this.setList(resultlist);
	}
	
	public GetResponse() {
		super();
	}

	private List<T> list;

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
}
