package com.clicktable.model;

public class CustomTable extends Table {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomTable(){}
	
	public CustomTable(Table table){
		this.setBlockGuid(table.getBlockGuid());
		this.setCreatedBy(table.getCreatedBy());
		this.setCreatedDate(table.getCreatedDate());
		this.setCurrentServerTime(table.getCurrentServerTime());
		this.setGuid(table.getGuid());
		this.setId(table.getId());
		this.setLanguageCode(table.getLanguageCode());
		this.setMaxCovers(table.getMaxCovers());
		this.setMinCovers(table.getMinCovers());
		this.setName(table.getName());
		this.setReservation_EndTime(table.getReservation_EndTime());
		this.setUpdatedDate(table.getUpdatedDate());
		this.setUpdatedBy(table.getUpdatedBy());
		this.setType(table.getType());
		this.setTableStatus(table.getTableStatus());
		this.setStatus(table.getStatus());
		this.setSectionId(table.getSectionId());
		this.setSeated_time(table.getSeated_time());
		this.setRestId(table.getRestId());
		this.setReservationGuid(table.getReservationGuid());
		this.setReservation_StartTime(table.getReservation_StartTime());
		this.setTat(table.getTat());
	}
	
	private String serverGuid;

	public String getServerGuid() {
		return serverGuid;
	}
	public void setServerGuid(String serverGuid) {
		this.serverGuid = serverGuid;
	}
	
	
}
