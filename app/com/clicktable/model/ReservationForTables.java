package com.clicktable.model;



import java.util.Date;
import java.util.List;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;


public class ReservationForTables {

	
	
	private String reservationGuid;
	public List<String> tableGuid ;
	private String reservationStatus;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date estStartTime;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date estEndTime;
	private Long availableAfter;
	private Long requestTime;
	
	
	
	public String getReservationGuid() {
	    return reservationGuid;
	}
	public void setReservationGuid(String reservationGuid) {
	    this.reservationGuid = reservationGuid;
	}
	
	
	
	public List<String> getTableGuid() {
	    return tableGuid;
	}
	public void setTableGuid(List<String> tableGuid) {
	    this.tableGuid = tableGuid;
	}
	public String getReservationStatus() {
	    return reservationStatus;
	}
	public void setReservationStatus(String reservationStatus) {
	    this.reservationStatus = reservationStatus;
	}

	public Date getEstStartTime() {
		return estStartTime == null ? null : (Date) estStartTime.clone();
	}

	public void setEstStartTime(Date estStartTime) {
		this.estStartTime = estStartTime == null ? null : (Date) estStartTime.clone();
	}

	public Date getEstEndTime() {
		return estEndTime == null ? null : (Date) estEndTime.clone();
	}

	public void setEstEndTime(Date estEndTime) {
		this.estEndTime = estEndTime == null ? null : (Date) estEndTime.clone();
	}

	public Long getAvailableAfter() {
	    return availableAfter;
	}
	public void setAvailableAfter(Long availableAfter) {
	    this.availableAfter = availableAfter;
	}
	public Long getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(Long requestTime) {
		this.requestTime = requestTime;
	}
	
	
	
	
	

	
	
	
}
