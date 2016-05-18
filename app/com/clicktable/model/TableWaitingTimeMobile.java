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

public class TableWaitingTimeMobile 
{

 
  private Long waitTime;
  private String tableGuid;
 

@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT ,timezone=Constants.TIMEZONE)
  private Date availableTime ;

  public Long getWaitTime() {
    return waitTime;
}
public void setWaitTime(Long waitTime) {
    this.waitTime = waitTime;
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
