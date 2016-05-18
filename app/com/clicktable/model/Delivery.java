package com.clicktable.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Delivery{

	
	private String externalId;
	private String deliveredTS;
	private String status;
	private String phoneNo;
	private String cause;

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId.trim();
	}

	public String getDeliveredTS() {
		return deliveredTS;
	}

	public void setDeliveredTS(String deliveredTS) {
		this.deliveredTS = deliveredTS;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause.toUpperCase();
	}

}
