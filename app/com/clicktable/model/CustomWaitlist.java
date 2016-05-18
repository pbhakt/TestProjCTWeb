package com.clicktable.model;


/**
 * 
 * @author p.singh
 * @company Clicktable Technologies LLP
 */

public class CustomWaitlist 
{
  private Reservation resv;
  private Table table;
  private Long estStartTime;
  private Long estEndTime;
  private Long gap;

  private String tableGuid;
  private String resvSource;
 
public CustomWaitlist(Reservation reservation, Table tbl) 
{
    this.resv = reservation;
    this.table = tbl;
    this.estStartTime = reservation.getEstStartTime().getTime();
    this.estEndTime = reservation.getEstEndTime().getTime();
}

public CustomWaitlist(Reservation reservation, String tbl) 
{
    this.resv = reservation;
    //this.tableGuid = tbl;
    this.estStartTime = reservation.getEstStartTime().getTime();
    this.estEndTime = reservation.getEstEndTime().getTime();
}

public Reservation getResv() {
    return resv;
}
public void setResv(Reservation resv) {
    this.resv = resv;
}
public Table getTable() {
    return table;
}
public void setTable(Table table) {
    this.table = table;
}
public Long getEstStartTime() {
    return estStartTime;
}
public void setEstStartTime(Long estStartTime) {
    this.estStartTime = estStartTime;
}
public Long getEstEndTime() {
    return estEndTime;
}
public void setEstEndTime(Long estEndTime) {
    this.estEndTime = estEndTime;
}
public Long getGap() {
    return gap;
}
public void setGap(Long gap) {
    this.gap = gap;
}

public String getTableGuid() {
	return tableGuid;
}

public void setTableGuid(String tableGuid) {
	this.tableGuid = tableGuid;
}

public String getResvSource() {
	return resvSource;
}

public void setResvSource(String resvSource) {
	this.resvSource = resvSource;
}


  
  
  
	
	

}
