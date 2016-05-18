package com.clicktable.model;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;


public class CustomReservation implements Serializable
{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4454194615585573294L;
	private String restaurantGuid;
	private String guid;
	private List<String> tableGuid =new ArrayList<String>();
	private String reservationStatus;

	/*@GraphProperty(propertyName = "customer_remark")
	private String customerRemark;*/
	
	/*@GraphProperty(propertyName = "resv_notes")
	private String resvNotes;*/
	//private String bookingMode;
	//private String bookedBy; 
	//private String bookedById;
	private Integer numCovers;
	
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date reservationTime= new Timestamp(new Date().getTime());;
	
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date estStartTime;

	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date estEndTime;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date actStartTime;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date actEndTime;
	//private String cancelledById;
	/*@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date cancelTime;*/
	//private String reasonToCancel;
	//private String cancelledBy;
	
	
	public CustomReservation()
	{
	    super();
	}
	
	
	public CustomReservation(Reservation res)
	{
	    this.setActEndTime(res.getActEndTime());
	    this.setActStartTime(res.getActStartTime());
	    //this.setBookedBy(res.getBookedBy());
	    //this.setBookedById(res.getBookedById());
	    //this.setBookingMode(res.getBookingMode());
	    //this.setCancelledBy(res.getCancelledBy());
	    //this.setCancelledById(res.getCancelledById());
	    //this.setCancelTime(res.getCancelTime());
	    //this.setCustomerRemark(res.getCustomerRemark());
	    this.setEstEndTime(res.getEstEndTime());
	    this.setEstStartTime(res.getEstStartTime());
	    this.setGuid(res.getGuid());
	    this.setTableGuid(res.getTableGuid());
	    //this.setResvNotes(res.getResvNotes());
	    this.setRestaurantGuid(res.getRestaurantGuid());
	    this.setReservationTime(res.getReservationTime());
	    this.setReservationStatus(res.getReservationStatus());
	    //this.setReasonToCancel(res.getReasonToCancel());
	    this.setNumCovers(res.getNumCovers());
	    
	}
	


	/*public String getCustomerRemark() {
		return customerRemark;
	}

	public void setCustomerRemark(String customerRemark) {
		this.customerRemark = customerRemark;
	}*/

	public Integer getNumCovers() {
		return numCovers;
	}

	public void setNumCovers(Integer numCovers) {
		this.numCovers = numCovers;
	}
	
	public String getRestaurantGuid() {
		return restaurantGuid;
	}

	public void setRestaurantGuid(String restGuid) {
		this.restaurantGuid = restGuid;
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

	/*public String getBookedById() {
		return bookedById;
	}

	public void setBookedById(String bookedById) {
		this.bookedById = bookedById;
	}*/

	public Date getReservationTime() {
		return reservationTime == null ? null : (Date) reservationTime.clone();
	}

	public void setReservationTime(Date reservationTime) {
		this.reservationTime = reservationTime == null ? null : (Date) reservationTime.clone();
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

	public Date getActStartTime() {
		return actStartTime == null ? null : (Date) actStartTime.clone();
	}

	public void setActStartTime(Date actStartTime) {
		this.actStartTime = actStartTime == null ? null : (Date) actStartTime.clone();
	}

	public Date getActEndTime() {
		return actEndTime == null ? null : (Date) actEndTime.clone();
	}

	public void setActEndTime(Date actEndTime) {
		this.actEndTime = actEndTime == null ? null : (Date) actEndTime.clone();
	}

/*	public String getCancelledById() {
		return cancelledById;
	}

	public void setCancelledById(String cancelledById) {
		this.cancelledById = cancelledById;
	}

	public Date getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}


	
	public String getReasonToCancel() {
		return reasonToCancel;
	}

	public void setReasonToCancel(String reasonToCancel) {
		this.reasonToCancel = reasonToCancel;
	}*/

	/*public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	

	public String getBookingMode() {
		return bookingMode;
	}

	public void setBookingMode(String bookingMode) {
		this.bookingMode = bookingMode;
	}

	public String getBookedBy() {
		return bookedBy;
	}

	public void setBookedBy(String bookedBy) {
		this.bookedBy = bookedBy;
	}*/
	


	/**
	 * @return the resvNotes
	 */
	/*public String getResvNotes() {
		return resvNotes;
	}*/

	/**
	 * @param resvNotes the resvNotes to set
	 */
	/*public void setResvNotes(String resvNotes) {
		this.resvNotes = resvNotes;
	}*/

	public String getGuid() {
	    return guid;
	}

	public void setGuid(String guid) {
	    this.guid = guid;
	}

	
	
	

}
