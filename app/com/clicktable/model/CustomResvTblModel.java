package com.clicktable.model;

import java.util.Date;
import java.util.List;


public class CustomResvTblModel {

private String resvGuid;
private List<String> tableGuidList;
private String tableGuid;
private Date estStartTime;
private Date estEndTime;
private String reservationStatus;
public String getResvGuid() {
	return resvGuid;
}
public void setResvGuid(String resvGuid) {
	this.resvGuid = resvGuid;
}
public List<String> getTableGuidList() {
	return tableGuidList;
}
public void setTableGuidList(List<String> tableGuidList) {
	this.tableGuidList = tableGuidList;
}
public String getTableGuid() {
	return tableGuid;
}
public void setTableGuid(String tableGuid) {
	this.tableGuid = tableGuid;
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






}
