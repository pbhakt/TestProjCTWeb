package com.clicktable.model;

import java.util.List;

import play.Logger;

public class CustomOperationalHour {

	private List<CustomShift> sunday;
	private List<CustomShift> monday;
	private List<CustomShift> tuesday;
	private List<CustomShift> wednesday;
	private List<CustomShift> thursday;
	private List<CustomShift> friday;
	private List<CustomShift> saturday;
	
	public List<CustomShift> getSunday() {
		return sunday;
	}
	public void setSunday(List<CustomShift> sunday) {
		this.sunday = sunday;
	}
	public List<CustomShift> getMonday() {
		return monday;
	}
	public void setMonday(List<CustomShift> monday) {
		this.monday = monday;
	}
	public List<CustomShift> getTuesday() {
		return tuesday;
	}
	public void setTuesday(List<CustomShift> tuesday) {
		this.tuesday = tuesday;
	}
	public List<CustomShift> getWednesday() {
		return wednesday;
	}
	public void setWednesday(List<CustomShift> wednesday) {
		this.wednesday = wednesday;
	}
	public List<CustomShift> getThursday() {
		return thursday;
	}
	public void setThursday(List<CustomShift> thursday) {
		this.thursday = thursday;
	}
	public List<CustomShift> getFriday() {
		return friday;
	}
	public void setFriday(List<CustomShift> friday) {
		this.friday = friday;
	}
	public List<CustomShift> getSaturday() {
		return saturday;
	}
	public void setSaturday(List<CustomShift> saturday) {
		this.saturday = saturday;
	}
	
	public class CustomShift{

		private String shiftName;
		private long startTimeInMillis;
		private long endTimeInMillis;
		private Integer diningSlot;
		
		public String getShiftName() {
			return shiftName;
		}
		public void setShiftName(String shiftName) {
			this.shiftName = shiftName;
		}
		
		public long getStartTimeInMillis() {
			return startTimeInMillis;
		}
		public void setStartTimeInMillis(long startTimeInMillis) {
			this.startTimeInMillis = startTimeInMillis;
		}
		
		public long getEndTimeInMillis() {
			return endTimeInMillis;
		}
		public void setEndTimeInMillis(long endTimeInMillis) {
			this.endTimeInMillis = endTimeInMillis;
		}
			
		public Integer getDiningSlot() {
			return diningSlot;
		}
		public void setDiningSlot(Integer diningSlot) {
			this.diningSlot = diningSlot;
		}
		public void setEndTimeInMillis2(long endTimeInMillis) {
		    Logger.debug("end time in millis set");
			this.endTimeInMillis = endTimeInMillis;
		}
		
		public long getEndTimeInMillis2() {
			return this.endTimeInMillis;
		}
	}
}
