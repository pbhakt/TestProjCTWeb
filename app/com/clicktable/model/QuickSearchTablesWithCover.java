package com.clicktable.model;

import java.util.List;

public class QuickSearchTablesWithCover {
	
	private int param;
	
	private String availableCount;
	private String allocateTable;
	private String availableTable;
	private String blockTable;
	private String Est_Waiting_Time;
	private List<Table> freeTable;
	/**
	 * @return the param
	 */
	public int getParam() {
		return param;
	}
	/**
	 * @param param the param to set
	 */
	public void setParam(int param) {
		this.param = param;
	}
	/**
	 * @return the availableCount
	 */
	public String getAvailableCount() {
		return availableCount;
	}
	/**
	 * @param availableCount the availableCount to set
	 */
	public void setAvailableCount(String availableCount) {
		this.availableCount = availableCount;
	}
	/**
	 * @return the allocateTable
	 */
	public String getAllocateTable() {
		return allocateTable;
	}
	/**
	 * @param allocateTable the allocateTable to set
	 */
	public void setAllocateTable(String allocateTable) {
		this.allocateTable = allocateTable;
	}
	/**
	 * @return the availableTable
	 */
	public String getAvailableTable() {
		return availableTable;
	}
	/**
	 * @param availableTable the availableTable to set
	 */
	public void setAvailableTable(String availableTable) {
		this.availableTable = availableTable;
	}
	/**
	 * @return the blockTable
	 */
	public String getBlockTable() {
		return blockTable;
	}
	/**
	 * @param blockTable the blockTable to set
	 */
	public void setBlockTable(String blockTable) {
		this.blockTable = blockTable;
	}
	/**
	 * @return the est_Waiting_Time
	 */
	public String getEst_Waiting_Time() {
		return Est_Waiting_Time;
	}
	/**
	 * @param est_Waiting_Time the est_Waiting_Time to set
	 */
	public void setEst_Waiting_Time(String est_Waiting_Time) {
		Est_Waiting_Time = est_Waiting_Time;
	}
	
	/**
	 * @return the freeTable
	 */
	public List<Table> getFreeTable() {
		return freeTable;
	}
	/**
	 * @param freeTable the freeTable to set
	 */
	public void setFreeTable(List<Table> freeTable) {
		this.freeTable = freeTable;
	}
	
	
	
	
}
