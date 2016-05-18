package com.clicktable.model;

import java.util.List;

public class CustomBlackOutHours {
	
	private List<CustomBlackOutShift> sunday;
	private List<CustomBlackOutShift> monday;
	private List<CustomBlackOutShift> tuesday;
	private List<CustomBlackOutShift> wednesday;
	private List<CustomBlackOutShift> thursday;
	private List<CustomBlackOutShift> friday;
	private List<CustomBlackOutShift> saturday;
	
	
	public List<CustomBlackOutShift> getSunday() {
		return sunday;
	}


	public void setSunday(List<CustomBlackOutShift> sunday) {
		this.sunday = sunday;
	}


	public List<CustomBlackOutShift> getMonday() {
		return monday;
	}


	public void setMonday(List<CustomBlackOutShift> monday) {
		this.monday = monday;
	}


	public List<CustomBlackOutShift> getTuesday() {
		return tuesday;
	}


	public void setTuesday(List<CustomBlackOutShift> tuesday) {
		this.tuesday = tuesday;
	}


	public List<CustomBlackOutShift> getWednesday() {
		return wednesday;
	}


	public void setWednesday(List<CustomBlackOutShift> wednesday) {
		this.wednesday = wednesday;
	}


	public List<CustomBlackOutShift> getThursday() {
		return thursday;
	}


	public void setThursday(List<CustomBlackOutShift> thursday) {
		this.thursday = thursday;
	}


	public List<CustomBlackOutShift> getFriday() {
		return friday;
	}


	public void setFriday(List<CustomBlackOutShift> friday) {
		this.friday = friday;
	}


	public List<CustomBlackOutShift> getSaturday() {
		return saturday;
	}


	public void setSaturday(List<CustomBlackOutShift> saturday) {
		this.saturday = saturday;
	}


	public class CustomBlackOutShift{
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
		private long startTimeInMillis;
		private long endTimeInMillis;
	}
	
}
