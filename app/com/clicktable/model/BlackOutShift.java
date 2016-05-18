package com.clicktable.model;

import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BlackOutShift extends Entity{

	private static final long serialVersionUID = -1029048878805229612L;
	//private String startTime;
	private long startTimeInMillis;
	//private String endTime;
	private long endTimeInMillis;
	private String day;

	public BlackOutShift() {
		// TODO Auto-generated constructor stub
	}

	public String getStartTime() {
		int mins = (int) (this.startTimeInMillis / 1000 / 60);
		return getTimeInHHMM(mins);
	}
	
	public void setStartTime(String startTime) {
		// Logger.debug("Start time setted");
		//this.startTime = startTime;
		int mins = getTimeInMins(startTime);
		long milisec = mins * 60 * 1000L;
		setStartTimeInMillis(milisec);
	}

	public String getEndTime() {
		int mins = (int) (this.endTimeInMillis / 1000 / 60);
		return getTimeInHHMM(mins);
	}

	public void setEndTime(String endTime) {
		// Logger.debug("End time setted");
		//this.endTime = endTime;
		int mins = getTimeInMins(endTime);
		long milisec = mins * 60 * 1000L;
		setEndTimeInMillis(milisec);
	}

	private int getTimeInMins(String time) {
		String[] splittedTime = time.split(":");
		String timeHr = splittedTime[0];
		String timeMin = splittedTime[1];
		int hrInMin = Integer.parseInt(timeHr) * 60;
		int minInMin = Integer.parseInt(timeMin.trim());

		return hrInMin + minInMin;
	}

	private String getTimeInHHMM(int mins) {
		StringBuilder  time = new StringBuilder();
		if(mins/60<=9){
			time.append('0');
			}
		time.append(mins/60).append(':').append(mins%60);
		return time.toString();
	}

	/**
	 * @return the startTimeInMillis
	 */
	public long getStartTimeInMillis() {
		return startTimeInMillis;
	}

	/**
	 * @param startTimeInMillis
	 *            the startTimeInMillis to set
	 */
	public void setStartTimeInMillis(long startTimeInMillis) {
		this.startTimeInMillis = startTimeInMillis;
	}

	/**
	 * @return the endTimeInMillis
	 */
	public long getEndTimeInMillis() {
		int mins = (int) (this.endTimeInMillis / 1000 / 60);
		setEndTime(getTimeInHHMM(mins));
		return endTimeInMillis;
	}

	/**
	 * @param endTimeInMillis
	 *            the endTimeInMillis to set
	 */
	public void setEndTimeInMillis(long endTimeInMillis) {
		// Logger.debug("end time in millis set");
		this.endTimeInMillis = endTimeInMillis;
	}

	


	/**
	 * @return the day
	 */
	public String getDay() {
		return day;
	}

	/**
	 * @param day the day to set
	 */
	public void setDay(String day) {
		this.day = day;
	}

	
}
