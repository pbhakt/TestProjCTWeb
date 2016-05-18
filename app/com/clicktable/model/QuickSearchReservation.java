package com.clicktable.model;

import java.util.List;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;

public class QuickSearchReservation {
	
	
	
	@JsonFormat(pattern = Constants.TIME_FORMATTING)
	private String time;
	@JsonFormat(pattern = Constants.TIME_FORMAT)
	private String systemTimeFormat;
	
	private int availableTable;	
	private int blockTable;
	//private String allocateTable;
	//private List<Table> allocatedTable;
	private List<String> availableTableGuid;
	private List<String> blockedTableGuid;
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * @return the availableTable
	 */
	public int getAvailableTable() {
		return availableTable;
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
	public List<String> getAvailableTableGuid() {
		return availableTableGuid;
	}
	/**
	 * @param availableTableGuid the availableTableGuid to set
	 */
	public void setAvailableTableGuid(List<String> availableTableGuid) {
		this.availableTableGuid = availableTableGuid;
	}
	/**
	 * @return the blockedTableGuid
	 */
	public List<String> getBlockedTableGuid() {
		return blockedTableGuid;
	}
	/**
	 * @param blockedTableGuid the blockedTableGuid to set
	 */
	public void setBlockedTableGuid(List<String> blockedTableGuid) {
		this.blockedTableGuid = blockedTableGuid;
	}
	/**
	 * @return the systemTimeFormat
	 */
	public String getSystemTimeFormat() {
		return systemTimeFormat;
	}
	/**
	 * @param systemTimeFormat the systemTimeFormat to set
	 */
	public void setSystemTimeFormat(String systemTimeFormat) {
		this.systemTimeFormat = systemTimeFormat;
	}
	
	

}

