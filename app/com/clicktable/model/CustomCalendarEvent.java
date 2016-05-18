package com.clicktable.model;

import org.springframework.beans.BeanUtils;

public class CustomCalendarEvent extends CalenderEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Boolean recurring = false;
	private String recurrenceType;
	
	public CustomCalendarEvent(CalenderEvent calEvent) {
		BeanUtils.copyProperties(calEvent, this);		
	}

	

	public Boolean isRecurring() {
		return recurring;
	}

	public void setRecurring(Boolean recurring) {
		this.recurring = recurring;
	}

	public String getRecurrenceType() {
		return recurrenceType;
	}

	public void setRecurrenceType(String recurrenceType) {
		this.recurrenceType = recurrenceType;
	}
}
