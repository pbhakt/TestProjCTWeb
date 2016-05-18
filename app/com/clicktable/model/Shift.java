package com.clicktable.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Shift extends Entity implements Comparable<Shift> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String shiftName;
	private String startTime;
	private long startTimeInMillis;
	private String endTime;
	private long endTimeInMillis;
	private String day;
	private Integer diningSlot;
	private boolean all_day;

	
	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	public String getStartTime() {
		int mins=(int) (this.startTimeInMillis/1000/60);
		return getTimeInHHMM(mins);
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
		int mins=getTimeInMins(startTime);
		long milisec=mins*60*1000L;
		setStartTimeInMillis(milisec);
	}

	public String getEndTime() {
		int mins=(int) (this.endTimeInMillis/1000/60);
		return getTimeInHHMM(mins);
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
		int mins=getTimeInMins(endTime);
		long milisec=mins*60*1000L;
		setEndTimeInMillis(milisec);
	}

	public List<Long> getDiningSlots() {
		return createDiningSlots();
	}
	
	public List<Long> getDiningSlotsWithDateTime(Long currentDateTime) {

		List<Long> list = new ArrayList<Long>();
		int srtTime = getTimeInMins(this.startTime);
		int enTime = getTimeInMins(this.endTime);

		if(this.diningSlot <= 0)
			return list;

		list.add(new Long (srtTime*60*1000 + currentDateTime));
		while (true)
		{

			if((srtTime+this.diningSlot)<= enTime )
			{
				srtTime = srtTime+this.diningSlot;
				list.add(new Long (srtTime*60*1000 + currentDateTime));

			}
			else break;
		}

		return list;

	}

	/*public void setDiningSlots(List<String> diningSlots) {
		this.diningSlots = diningSlots;
	}*/

	private List<Long> createDiningSlots()
	{
		List<Long> list = new ArrayList<Long>();
		int srtTime = getTimeInMins(this.startTime);
		int enTime = getTimeInMins(this.endTime);
	
		if(this.diningSlot <= 0)
			return list;
	
		list.add(Long.valueOf(srtTime*60*1000L));
		while (true)
		{
			
			if((srtTime+this.diningSlot)<= enTime )
			{
				srtTime = srtTime+this.diningSlot;
				list.add(Long.valueOf(srtTime*60*1000L));
				
			}
			else break;
		}
		
		return list;
	}
	
	
	private int getTimeInMins(String time)
	{
		String[] splittedTime = time.split(":");
		String timeHr = splittedTime[0];
		String timeMin = splittedTime[1];
		int hrInMin = Integer.parseInt(timeHr)*60;
		int minInMin = Integer.parseInt(timeMin.trim());
		
		return hrInMin+minInMin;
	}
	
	private String getTimeInHHMM(int mins)
	{
		StringBuilder  time = new StringBuilder();
		if(mins/60<=9){
			time.append('0');
			}
		time.append(mins/60).append(':').append(mins%60);
		return time.toString();
	}

	public int getDiningSlot() {
		return diningSlot;
	}

	public void setDiningSlot(int diningSlot) {
		this.diningSlot = diningSlot;
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
	/**
	 * @return the startTimeInMillis
	 */
	public long getStartTimeInMillis() {
		return startTimeInMillis;
	}

	/**
	 * @param startTimeInMillis the startTimeInMillis to set
	 */
	public void setStartTimeInMillis(long startTimeInMillis) {
		this.startTimeInMillis = startTimeInMillis;
	}

	/**
	 * @return the endTimeInMillis
	 */
	public long getEndTimeInMillis() {
		int mins=(int) (this.endTimeInMillis/1000/60);
		setEndTime( getTimeInHHMM(mins));
		return endTimeInMillis;
	}

	/**
	 * @param endTimeInMillis the endTimeInMillis to set
	 */
	public void setEndTimeInMillis(long endTimeInMillis) {
	    //Logger.debug("end time in millis set");
		this.endTimeInMillis = endTimeInMillis;
	}
	
	public void setEndTimeInMillis2(long endTimeInMillis) {
		this.endTimeInMillis = endTimeInMillis;
	}
	
	public long getEndTimeInMillis2() {
		return this.endTimeInMillis;
	}

	
	@Override
	public int compareTo(Shift o) {
		// TODO Auto-generated method stub
		if(this.startTimeInMillis<o.getStartTimeInMillis()){
			return -1;
		}else if(this.startTimeInMillis>o.getStartTimeInMillis()){
			return 1;
		}else{
			return 0;
		}
       
	}

	/**
	 * @return the all_day
	 */
	public boolean isAll_day() {
		return all_day;
	}

	/**
	 * @param all_day the all_day to set
	 */
	public void setAll_day(boolean all_day) {
		this.all_day = all_day;
	}
	

	
}
