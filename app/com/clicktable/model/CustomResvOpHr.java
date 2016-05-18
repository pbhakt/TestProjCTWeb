package com.clicktable.model;

import java.util.Date;
import java.util.List;


public class CustomResvOpHr {

private String guid;
private List<String> tableGuid;
private Date estStartTime;
private Date estEndTime;
private String reservationStatus;
private Integer numCovers;

private String guest_firstName;
private String guest_mobile;
private String guest_isd_code;
private String isVIP;
private String reason;


public CustomResvOpHr(Reservation resv) {
	// TODO Auto-generated constructor stub
	
	this.setGuid(resv.getGuid());
	this.setTableGuid(resv.getTableGuid());
	this.setEstStartTime(resv.getEstStartTime());
	this.setEstEndTime(resv.getEstEndTime());
	this.setReservationStatus(resv.getReservationStatus());
	this.setNumCovers(resv.getNumCovers());
	this.setGuest_firstName(resv.getGuest_firstName());
	this.setGuest_isd_code(resv.getGuest_isd_code());
	this.setGuest_mobile(resv.getGuest_mobile());
	this.setIsVIP(resv.getIsVIP());
	this.setReason(resv.getReason());
	
	
}
public Date getEstStartTime() {
	return estStartTime;
}
public void setEstStartTime(Date estStartTime) {
	this.estStartTime = estStartTime;
}
public Date getEstEndTime() {
	return estEndTime;
}
public void setEstEndTime(Date estEndTime) {
	this.estEndTime = estEndTime;
}
public String getReservationStatus() {
	return reservationStatus;
}
public void setReservationStatus(String reservationStatus) {
	this.reservationStatus = reservationStatus;
}
public String getGuid() {
	return guid;
}
public void setGuid(String guid) {
	this.guid = guid;
}
public List<String> getTableGuid() {
	return tableGuid;
}
public void setTableGuid(List<String> tableGuid) {
	this.tableGuid = tableGuid;
}
public Integer getNumCovers() {
	return numCovers;
}
public void setNumCovers(Integer numCovers) {
	this.numCovers = numCovers;
}
public String getGuest_firstName() {
	return guest_firstName;
}
public void setGuest_firstName(String guest_firstName) {
	this.guest_firstName = guest_firstName;
}
public String getGuest_mobile() {
	return guest_mobile;
}
public void setGuest_mobile(String guest_mobile) {
	this.guest_mobile = guest_mobile;
}
public String getGuest_isd_code() {
	return guest_isd_code;
}
public void setGuest_isd_code(String guest_isd_code) {
	this.guest_isd_code = guest_isd_code;
}
public String getIsVIP() {
	return isVIP;
}
public void setIsVIP(String isVIP) {
	this.isVIP = isVIP;
}
public String getReason() {
	return reason;
}
public void setReason(String reason) {
	this.reason = reason;
}






}
