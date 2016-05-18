package com.clicktable.model;

import java.util.Date;
import java.util.List;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * 
 * @author p.singh
 * @company Clicktable Technologies LLP
 */

public class TableWaitingTime 
{

 
  private Table table;
  private Long waitTime;
  private String tableGuid;
  private List<String> resvSourceList;
  
  public List<String> getResvSourceList() {
	return resvSourceList;
}
public void setResvSourceList(List<String> resvSourceList) {
	this.resvSourceList = resvSourceList;
}
@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT ,timezone=Constants.TIMEZONE)
  private Date availableTime ;

  public Long getWaitTime() {
    return waitTime;
}
public void setWaitTime(Long waitTime) {
    this.waitTime = waitTime;
}
public Table getTable() {
    return table;
}
public void setTable(Table table) {
    this.table = table;
}
public Date getAvailableTime() {
    return availableTime == null ? null : (Date) availableTime.clone();
}
public void setAvailableTime(Date availableTime) {
    this.availableTime = availableTime == null ? null : (Date) availableTime.clone();
}
public String getTableGuid() {
    return tableGuid;
}
public void setTableGuid(String tableGuid) {
    this.tableGuid = tableGuid;
}
  
  
  

	

}
