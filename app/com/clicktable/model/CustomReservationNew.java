package com.clicktable.model;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;


public class CustomReservationNew  {

	
    	private String guid;
	private String restaurantGuid;
	private String shortId;
	private String guestGuid;
	public List<String> tableGuid =new ArrayList<String>();
	private String reservationStatus;
	private String customerRemark;
	private String bookingMode;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date reservationTime= new Timestamp(new Date().getTime());
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date estStartTime;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date estEndTime;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date actStartTime;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date actEndTime;
	private String cancelledById;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date cancelTime;
	private String reasonToCancel;
	private String cancelledBy;
	private String tat;
	private String quotedTime;
	private String reservationNote;
	private String estWaitingTime;
	private List<ReservationHistory> history;
	private String  guest_firstName;
	private String  guest_lastName;
	private String  guest_mobile;
	private String  guest_email;	
	private String isVIP ;
	private String total_guest_visit;
	
	private String conversation="";
	
	public CustomReservationNew()
	{
	    super();
	}

	public CustomReservationNew(Long estStartTime, Long estEndTime, Long actStartTime, Long actEndTime, String tat) 
	{
	    if(actEndTime != null)
	    {
	    this.actEndTime = new Date(actEndTime);
	    }
	    if(actStartTime != null)
	    {
	    this.actStartTime = new Date(actStartTime);
	    }
	    this.estEndTime = new Date(estEndTime);
	    this.estStartTime = new Date(estStartTime);
	    this.tat = tat;
	}
	
	
	public CustomReservationNew(Reservation resv) 
	{
	    
	    this.actEndTime = resv.getActEndTime();
	    this.actStartTime = resv.getActStartTime();
	    this.bookingMode = resv.getBookingMode();
	    this.cancelledBy = resv.getCancelledBy();
	    this.cancelledById = resv.getCancelledById();
	    this.cancelTime = resv.getCancelTime();
	    this.conversation = resv.getConversation();
	    this.customerRemark = resv.getCustomerRemark();
	    this.estEndTime = resv.getEstEndTime();
	    this.estStartTime = resv.getEstStartTime();
	    this.estWaitingTime = resv.getEstWaitingTime();
	    this.guest_email = null;
	    this.guest_firstName = null;
	    this.guest_lastName = null;
	    this.guest_mobile = null;
	    this.guestGuid = resv.getGuestGuid();
	    this.guid = resv.getGuid();
	    this.history = resv.getHistory();
	    this.isVIP = null;
	    this.quotedTime = resv.getQuotedTime();
	    this.reasonToCancel = resv.getReasonToCancel();
	    this.reservationNote = resv.getReservationNote();
	    this.reservationStatus = resv.getReservationStatus();
	    this.reservationTime = resv.getReservationTime();
	    this.restaurantGuid = resv.getRestaurantGuid();
	    this.shortId = resv.getShortId();
	    this.tableGuid = resv.getTableGuid();
	    this.tat = resv.getTat();
	    this.total_guest_visit = resv.getTotal_guest_visit();
	    
	}

	public String getCustomerRemark() {
		return customerRemark;
	}

	public void setCustomerRemark(String customerRemark) {
		this.customerRemark = customerRemark;
	}

	
	public String getShortId() {
		return shortId;
	}

	public void setShortId(String shortId) {
		this.shortId = shortId;
	}
	
	public String getRestaurantGuid() {
		return restaurantGuid;
	}

	public void setRestaurantGuid(String restGuid) {
		this.restaurantGuid = restGuid;
	}

	public String getGuestGuid() {
		return guestGuid;
	}

	public void setGuestGuid(String guestGuid) {
		this.guestGuid = guestGuid;
	}

	public List<String> getTableGuid() {
		return processTableGuid(tableGuid);
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

	public String getCancelledById() {
		return cancelledById;
	}

	public void setCancelledById(String cancelledById) {
		this.cancelledById = cancelledById;
	}

	public Date getCancelTime() {
		return cancelTime == null ? null : (Date) cancelTime.clone();
	}

	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime == null ? null : (Date) cancelTime.clone();
	}


	
	public String getReasonToCancel() {
		return reasonToCancel;
	}

	public void setReasonToCancel(String reasonToCancel) {
		this.reasonToCancel = reasonToCancel;
	}

	public String getCancelledBy() {
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

	
	/**
	 * @return the history
	 */
	public List<ReservationHistory> getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(List<ReservationHistory> history) {
		this.history = history;
	}

	
	/**
	 * @return the guest_firstName
	 */
	public String getGuest_firstName() {
		return guest_firstName;
	}

	/**
	 * @param guest_firstName the guest_firstName to set
	 */
	public void setGuest_firstName(String guest_firstName) {
		this.guest_firstName = guest_firstName;
	}

	/**
	 * @return the guest_lastName
	 */
	public String getGuest_lastName() {
		return guest_lastName;
	}

	/**
	 * @param guest_lastName the guest_lastName to set
	 */
	public void setGuest_lastName(String guest_lastName) {
		this.guest_lastName = guest_lastName;
	}

	/**
	 * @return the guest_mobile
	 */
	public String getGuest_mobile() {
		return guest_mobile;
	}

	/**
	 * @param guest_mobile the guest_mobile to set
	 */
	public void setGuest_mobile(String guest_mobile) {
		this.guest_mobile = guest_mobile;
	}

	

	/**
	 * @return the tat
	 */
	public String getTat() {
		return tat;
	}

	/**
	 * @param tat the tat to set
	 */
	public void setTat(String tat) {
		this.tat = tat;
	}

	/**
	 * @return the guest_email
	 */
	public String getGuest_email() {
		return guest_email;
	}

	/**
	 * @param guest_email the guest_email to set
	 */
	public void setGuest_email(String guest_email) {
		this.guest_email = guest_email;
	}


	/**
	 * @return the isVIP
	 */
	public String getIsVIP() {
		return isVIP;
	}

	/**
	 * @param isVIP the isVIP to set
	 */
	public void setIsVIP(String isVIP) {
		this.isVIP = isVIP;
	}

	/**
	 * @return the total_guest_visit
	 */
	public String getTotal_guest_visit() {
		return total_guest_visit;
	}

	/**
	 * @param total_guest_visit the total_guest_visit to set
	 */
	public void setTotal_guest_visit(String total_guest_visit) {
		this.total_guest_visit = total_guest_visit;
	}

	/**
	 * @return the quotedTime
	 */
	public String getQuotedTime() {
		return quotedTime;
	}

	/**
	 * @param quotedTime the quotedTime to set
	 */
	public void setQuotedTime(String quotedTime) {
		this.quotedTime = quotedTime;
	}

	


	/**
	 * @return the reservationNote
	 */
	public String getReservationNote() {
		return reservationNote;
	}

	/**
	 * @param reservationNote the reservationNote to set
	 */
	public void setReservationNote(String reservationNote) {
		this.reservationNote = reservationNote;
	}

	public String getConversation() {
	    return conversation;
	}

	public void setConversation(String conversation) {
	    this.conversation = conversation;
	}
	


	/**
	 * @return the estWaitingTime
	 */
	public String getEstWaitingTime() {
		return estWaitingTime;
	}

	/**
	 * @param estWaitingTime the estWaitingTime to set
	 */
	public void setEstWaitingTime(String estWaitingTime) {
		this.estWaitingTime = estWaitingTime;
	}

	private List<String> processTableGuid(List<String> guids)
	{
		List<String> list = new ArrayList<String>();
		for (String str : guids) {
			list.add(  str.replaceAll("\\[", "").replaceAll("\\]", ""));
		}
		return list;
	}

	public String getGuid() {
	    return guid;
	}

	public void setGuid(String guid) {
	    this.guid = guid;
	}

	
	
	
	

}
