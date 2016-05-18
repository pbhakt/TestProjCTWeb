package com.clicktable.model;

import java.util.List;

public class BlackOutHours {

	private List<BlackOutShift> sunday;
	private List<BlackOutShift> monday;
	private List<BlackOutShift> tuesday;
	private List<BlackOutShift> wednesday;
	private List<BlackOutShift> thursday;
	private List<BlackOutShift> friday;
	private List<BlackOutShift> saturday;

	private String restGuid;

	public String getRestGuid() {
		return restGuid;
	}

	public void setRestGuid(String restGuid) {
		this.restGuid = restGuid;
	}

	/**
	 * @return the sunday
	 */
	public List<BlackOutShift> getSunday() {
		return sunday;
	}

	/**
	 * @param sunday
	 *            the sunday to set
	 */
	public void setSunday(List<BlackOutShift> sunday) {
		this.sunday = sunday;
	}

	/**
	 * @return the monday
	 */
	public List<BlackOutShift> getMonday() {
		return monday;
	}

	/**
	 * @param monday
	 *            the monday to set
	 */
	public void setMonday(List<BlackOutShift> monday) {
		this.monday = monday;
	}

	/**
	 * @return the tuesday
	 */
	public List<BlackOutShift> getTuesday() {
		return tuesday;
	}

	/**
	 * @param tuesday
	 *            the tuesday to set
	 */
	public void setTuesday(List<BlackOutShift> tuesday) {
		this.tuesday = tuesday;
	}

	/**
	 * @return the wednesday
	 */
	public List<BlackOutShift> getWednesday() {
		return wednesday;
	}

	/**
	 * @param wednesday
	 *            the wednesday to set
	 */
	public void setWednesday(List<BlackOutShift> wednesday) {
		this.wednesday = wednesday;
	}

	/**
	 * @return the thursday
	 */
	public List<BlackOutShift> getThursday() {
		return thursday;
	}

	/**
	 * @param thursday
	 *            the thursday to set
	 */
	public void setThursday(List<BlackOutShift> thursday) {
		this.thursday = thursday;
	}

	/**
	 * @return the friday
	 */
	public List<BlackOutShift> getFriday() {
		return friday;
	}

	/**
	 * @param friday
	 *            the friday to set
	 */
	public void setFriday(List<BlackOutShift> friday) {
		this.friday = friday;
	}

	/**
	 * @return the saturday
	 */
	public List<BlackOutShift> getSaturday() {
		return saturday;
	}

	/**
	 * @param saturday
	 *            the saturday to set
	 */
	public void setSaturday(List<BlackOutShift> saturday) {
		this.saturday = saturday;
	}

}
