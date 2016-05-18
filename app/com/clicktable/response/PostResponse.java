package com.clicktable.response;

import java.util.ArrayList;
import java.util.List;

import play.i18n.Messages;


public class PostResponse<T> extends BaseResponse {
	
	
	private Object guid[];
	private List<String> guidList=new ArrayList<String>();

	public PostResponse(String responseCode, String guid ) {
		this.setResponseStatus(true);
		this.setResponseCode(responseCode);		
		this.setResponseMessage(Messages.get(responseCode));
		guidList.add(guid);
		this.guid =guidList.toArray() ;
	}
	public PostResponse(String responseCode, final Object guid_array[] ) {
		this.setResponseStatus(true);
		this.setResponseCode(responseCode);		
		this.setResponseMessage(Messages.get(responseCode));
		this.guid = guid_array == null ? null : (Object[]) guid_array.clone();
	}



	public PostResponse() {
		super();
	}
	/**
	 * @return the guid_array
	 */
	public Object[] getGuid() {
		return guid == null ? null : (Object[]) guid.clone();
	}
	/**
	 * @param guid_array the guid_array to set
	 */
	public void setGuid(Object[] guid) {
		this.guid = guid== null ? null : (Object[]) guid.clone();
	}
	
}
