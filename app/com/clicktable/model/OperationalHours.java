package com.clicktable.model;

import java.util.Collections;
import java.util.List;

import com.clicktable.util.Constants;

public class OperationalHours {

	private List<Shift> sunday;
	private List<Shift> monday;
	private List<Shift> tuesday;
	private List<Shift> wednesday;
	private List<Shift> thursday;
	private List<Shift> friday;
	private List<Shift> saturday;
	
	private Integer diningSlot=Integer.valueOf(0);
	private String restGuid;
	

	public List<Shift> getSunday() {		
		return sunday;
	}

	public void setSunday(List<Shift> sunday) {
		Collections.sort(sunday);
		this.sunday = sunday;
	}

	public List<Shift> getMonday() {
		return monday;
	}

	public void setMonday(List<Shift> monday) {
		Collections.sort(monday);
		this.monday = monday;
	}

	public List<Shift> getTuesday() {
		return tuesday;
	}

	public void setTuesday(List<Shift> tuesday) {
		Collections.sort(tuesday);
		this.tuesday = tuesday;
	}

	public List<Shift> getWednesday() {
		return wednesday;
	}

	public void setWednesday(List<Shift> wednesday) {
		Collections.sort(wednesday);
		this.wednesday = wednesday;
	}

	public List<Shift> getThursday() {
		return thursday;
	}

	public void setThursday(List<Shift> thursday) {
		Collections.sort(thursday);
		this.thursday = thursday;
	}

	public List<Shift> getFriday() {
		return friday;
	}

	public void setFriday(List<Shift> friday) {
		Collections.sort(friday);
		this.friday = friday;
	}

	public List<Shift> getSaturday() {
		return saturday;
	}

	public void setSaturday(List<Shift> saturday) {
		Collections.sort(saturday);
		this.saturday = saturday;
	}

	public Integer getDiningSlot() {
		return diningSlot;
	}

	public void setDiningSlot(Integer diningSlot) {
		this.diningSlot = diningSlot;
	}

	public String getRestGuid() {
		return restGuid;
	}

	public void setRestGuid(String restGuid) {
		this.restGuid = restGuid;
	}

	public void setDiningSlotsForShift()
    {      
		setDiningSlotsForShift(sunday,Constants.SUNDAY);
		setDiningSlotsForShift(monday,Constants.MONDAY);
		setDiningSlotsForShift(tuesday,Constants.TUESDAY);
		setDiningSlotsForShift(wednesday,Constants.WEDNESDAY);
		setDiningSlotsForShift(thursday,Constants.THURSDAY);
		setDiningSlotsForShift(friday,Constants.FRIDAY);
		setDiningSlotsForShift(saturday,Constants.SATURDAY);
    }
	private void setDiningSlotsForShift(List<Shift> day,String day_name)
	{
		if(null!=day){
		for (Shift shift : day) {
			shift.setDiningSlot(this.diningSlot);
			shift.setDay(day_name);
		}
	}
	}


}
