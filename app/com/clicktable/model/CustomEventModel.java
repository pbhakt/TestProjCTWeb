package com.clicktable.model;

public class CustomEventModel extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomEventModel(Event e) {
	
		this.setAllday(e.isAllday());
		this.setBlockingArea(e.getBlockingArea());
		this.setBlockingType(e.getBlockingType());
		this.setCategory(e.getCategory());
		this.setCreatedBy(e.getCreatedBy());
		this.setCreatedDate(e.getCreatedDate());
		this.setDateOfMonth(e.getDateOfMonth());
		this.setDayOfTheWeek(e.getDayOfTheWeek());
		this.setEndDate(e.getEndDate());
		this.setEndTime(e.getEndTime());
		this.setEventDescription(e.getEventDescription());
		this.setGuid(e.getGuid());
		this.setWeekOfMonth(e.getWeekOfMonth());
		this.setUpdatedDate(e.getUpdatedDate());
		this.setUpdatedBy(e.getUpdatedBy());
		this.setType(e.getType());
		this.setSubCategory(e.getSubCategory());
		this.setStatus(e.getStatus());
		this.setStartTime(e.getStartTime());
		this.setStartDate(e.getStartDate());
		this.setRestaurantGuid(e.getRestaurantGuid());
		this.setRecurring(e.isRecurring());
		this.setRecurrenceType(e.getRecurrenceType());
		this.setRecurrenceEndDate(e.getRecurrenceEndDate());
		this.setRecurOn(e.getRecurOn());
		this.setRecurEvery(e.getRecurEvery());
		this.setRecurEndType(e.getRecurEndType());
		this.setNumOfRecurrence(e.getNumOfRecurrence());
		this.setName(e.getName());
		this.setLanguageCode(e.getLanguageCode());
		this.setIsDraft(e.getIsDraft());
		this.setId(e.getId());
		
	}


	private int totalNumOfCovers;
	private int totalNumOfTables;

	public int getTotalNumOfCovers() {
		return totalNumOfCovers;
	}

	public void setTotalNumOfCovers(int totalNumOfCovers) {
		this.totalNumOfCovers = totalNumOfCovers;
	}

	public int getTotalNumOfTables() {
		return totalNumOfTables;
	}

	public void setTotalNumOfTables(int totalNumOfTables) {
		this.totalNumOfTables = totalNumOfTables;
	}

	
}
