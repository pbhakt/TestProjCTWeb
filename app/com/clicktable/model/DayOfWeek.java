package com.clicktable.model;

import org.springframework.data.neo4j.annotation.NodeEntity;

import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author p.singh
 * @company Clicktable Technologies LLP
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class DayOfWeek extends Entity 
{
  
	/**
     * 
     */
    private static final long serialVersionUID = -2087280040172858335L;
    
	@JsonIgnore
	private String day;
	private String dayType;
	

	public DayOfWeek()
	{
	  super();
	}
	
	public DayOfWeek(int day)
	{
	  this.setGuid(UtilityMethods.generateCtId());
	  switch(day)
	  {
	  case 0:
	      this.day=Constants.SUNDAY;
	      this.dayType=Constants.WEEKDAY;
	      break;
	  case 1:
	      this.day=Constants.MONDAY;
	      this.dayType=Constants.WEEKDAY;
	      break;
	  case 2:
	      this.day=Constants.TUESDAY;
	      this.dayType=Constants.WEEKDAY;
	      break;
	  case 3:
	      this.day=Constants.WEDNESDAY;
	      this.dayType=Constants.WEEKDAY;
	      break;
	  case 4:
	      this.day=Constants.THURSDAY;
	      this.dayType=Constants.WEEKDAY;
	      break;
	  case 5:
	      this.day=Constants.FRIDAY;
	      this.dayType=Constants.WEEKEND;
	      break;
	  case 6:
	      this.day=Constants.SATURDAY;
	      this.dayType=Constants.WEEKEND;
	      break;
	  default: 
	      break;
	  }
	}


	public String getDay() {
	    return day;
	}


	public void setDay(String day) {
	    this.day = day;
	}

	public String getDayType() {
	    return dayType;
	}

	public void setDayType(String dayType) {
	    this.dayType = dayType;
	}


	
	
	
	

}
