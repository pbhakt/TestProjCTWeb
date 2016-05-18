package com.clicktable.model;

import java.util.List;
import java.util.Map;

public class QuickSearchReservationNew {	


	private Long time;

	private int availableTable;	
	private int blockTable;
	private Integer allocateTable;
	private List<Map<String,String>> allocatedTable;
	private List<Map<String,String>> availableTableGuid;
	private List<Map<String,String>> blockedTableGuid;
	private Boolean isWithinOperationalHours;
	/**
	 * @return the time
	 */

	/**
	 * @return the availableTable
	 */
	public int getAvailableTable() {
		return availableTable;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	/**
	 * @param availableTable the availableTable to set
	 */
	public void setAvailableTable(int availableTable) {
		this.availableTable = availableTable;
	}
	/**
	 * @return the blockTable
	 */
	public int getBlockTable() {
		return blockTable;
	}
	/**
	 * @param blockTable the blockTable to set
	 */
	public void setBlockTable(int blockTable) {
		this.blockTable = blockTable;
	}
	/**
	 * @return the availableTableGuid
	 */
	/*	public List<String> getAvailableTableGuid() {
	return availableTableGuid;
}
	 *//**
	 * @param availableTableGuid the availableTableGuid to set
	 *//*
public void setAvailableTableGuid(List<String> availableTableGuid) {
	this.availableTableGuid = availableTableGuid;
}
	  *//**
	  * @return the blockedTableGuid
	  *//*
public List<String> getBlockedTableGuid() {
	return blockedTableGuid;
}
	   *//**
	   * @param blockedTableGuid the blockedTableGuid to set
	   *//*
public void setBlockedTableGuid(List<String> blockedTableGuid) {
	this.blockedTableGuid = blockedTableGuid;
}*/

	/**
	 * @param systemTimeFormat the systemTimeFormat to set
	 */
	public Integer getAllocateTable() {
		return allocateTable;
	}
	public void setAllocateTable(Integer allocateTable) {
		this.allocateTable = allocateTable;
	}
	/*	public List<String> getAllocatedTable() {
    return allocatedTable;
}
public void setAllocatedTable(List<String> allocatedTable) {
    this.allocatedTable = allocatedTable;
}*/
	public Boolean getIsWithinOperationalHours() {
		return isWithinOperationalHours;
	}
	public void setIsWithinOperationalHours(Boolean isWithinOperationalHours) {
		this.isWithinOperationalHours = isWithinOperationalHours;
	}
	public List<Map<String, String>> getAvailableTableGuid() {
		return availableTableGuid;
	}
	public void setAvailableTableGuid(List<Map<String, String>> availableTableGuid) {
		this.availableTableGuid = availableTableGuid;
	}
	public List<Map<String, String>> getBlockedTableGuid() {
		return blockedTableGuid;
	}
	public void setBlockedTableGuid(List<Map<String, String>> blockedTableGuid) {
		this.blockedTableGuid = blockedTableGuid;
	}
	public List<Map<String, String>> getAllocatedTable() {
		return allocatedTable;
	}
	public void setAllocatedTable(List<Map<String, String>> allocatedTable) {
		this.allocatedTable = allocatedTable;
	}



}

