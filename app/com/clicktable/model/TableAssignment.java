package com.clicktable.model;

import java.io.Serializable;

import play.data.validation.Constraints.Required;

import com.clicktable.util.ErrorCodes;

/**
 * 
 * @author p.singh
 * 
 */


public class TableAssignment implements Serializable
{
    
        /**
	 * 
	 */
	private static final long serialVersionUID = -2209203550118292401L;
		@Required(message=ErrorCodes.TBL_ASSIGNMENT_TBL_GUID_REQUIRED)
	private String tableGuid;
        @Required(message=ErrorCodes.TBL_ASSIGNMENT_START_TIME_REQUIRED)
        private String startTime;
        @Required(message=ErrorCodes.TBL_ASSIGNMENT_END_TIME_REQUIRED)
        private String endTime;
        @Required(message=ErrorCodes.TBL_ASSIGNMENT_DATE_REQUIRED)
        private String date;
        private Integer maxCovers;
	@Required(message=ErrorCodes.TBL_ASSIGNMENT_SERVER_GUID_REQUIRED)
    	private String serverGuid;
	@Required(message=ErrorCodes.TBL_ASSIGNMENT_REST_GUID_REQUIRED)
	private String restaurantGuid;
	
	
	
	
	public String getTableGuid() {
	    return tableGuid;
	}
	public void setTableGuid(String tableGuid) {
		this.tableGuid =tableGuid == null ?null: tableGuid.trim();

	}
	public String getStartTime() {
	    return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime == null ?null: startTime.trim();
	}
	public String getEndTime() {
	    return endTime;
	}
	public void setEndTime(String endTime) {

		this.endTime = endTime == null ?null: endTime.trim();
	}
	public String getDate() {
	    return date;
	}
	public void setDate(String date) {
		this.date = date == null ?null: date.trim();
	}
	public String getServerGuid() {
	    return serverGuid;
	}
	public void setServerGuid(String serverGuid) {
		this.serverGuid = serverGuid == null ?null: serverGuid.trim();
	}
	public String getRestaurantGuid() {
	    return restaurantGuid;
	}
	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid == null ?null: restaurantGuid.trim();

	}
	public Integer getMaxCovers() {
	    return maxCovers;
	}
	public void setMaxCovers(Integer maxCovers) {
	    this.maxCovers = maxCovers;
	}
	
	
	
   	

}
