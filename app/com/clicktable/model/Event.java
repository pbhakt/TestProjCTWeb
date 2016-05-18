package com.clicktable.model;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Event extends Entity {
	

	private static final long serialVersionUID = 5386785117205954649L;
	
	@Required(message=ErrorCodes.EVENT_NAME)
	@MaxLength(message=ErrorCodes.EVENT_NAME_MAXLENGTH,value=50)
	private String name;
	@GraphProperty(propertyName = "event_desc")
	private String eventDescription;
	@Required(message=ErrorCodes.EVENT_START_DT)
	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "start_dt")
	private Date startDate;
	@Required(message=ErrorCodes.EVENT_END_DT)
	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "end_dt")
	private Date endDate;
	@Required(message=ErrorCodes.EVENT_INVALID_RESTID)
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;
	@Required(message=ErrorCodes.EVENT_TYPE)
	private String type;
	@Required(message=ErrorCodes.EVENT_IS_DRAFT)
	@GraphProperty(propertyName = "is_draft")
	private Boolean isDraft = false;
	@Required(message=ErrorCodes.EVENT_CATEGORY)
	private String category;
	private String subCategory;
	@GraphProperty(propertyName = "day_of_the_week")
	private List<String> dayOfTheWeek = new ArrayList<String>();	
	@GraphProperty(propertyName = "blocking_type")
	private String blockingType;
	/*@GraphProperty(propertyName = "blocking_area_type")
	private String blockingAreaType;*/
	@GraphProperty(propertyName = "blocking_area")
	private List<String> blockingArea = new ArrayList<String>();
	@Required(message=ErrorCodes.EVENT_START_TIME)
	@JsonFormat(pattern = Constants.TIME_FORMAT,  timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "start_time")
	private Date startTime;
	@Required(message=ErrorCodes.EVENT_END_TIME)
	@JsonFormat(pattern = Constants.TIME_FORMAT,  timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "end_time")
	private Date endTime;
	@GraphProperty(propertyName = "recurrence_type")
	private String recurrenceType;
	@GraphProperty(propertyName = "recur_every")
	private int recurEvery;
	@GraphProperty(propertyName = "recur_on")
	private String recurOn;
	@GraphProperty(propertyName = "recur_end_type")
	private String recurEndType;	
	@GraphProperty(propertyName = "num_of_recurrence")
	private int numOfRecurrence;
	@GraphProperty(propertyName = "recurrence_end_dt")
	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone=Constants.TIMEZONE)
	private Date recurrenceEndDate;
	private boolean allday = false;
	private boolean recurring = false;
	@Min(message=ErrorCodes.EVENT_MIN_DATE_OF_MONTH,value=0)
	@Max(message=ErrorCodes.EVENT_MAX_DATE_OF_MONTH,value=31)
	private int dateOfMonth;
	@Min(message=ErrorCodes.EVENT_MIN_WEEK_OF_MONTH,value=0)
	@Max(message=ErrorCodes.EVENT_MAX_WEEK_OF_MONTH,value=5)
	private int weekOfMonth;
	private boolean validateCategory =true;
	private boolean ongoingValidation = true;
	private boolean promoteValidation =true;
	@MaxLength(message=ErrorCodes.PHOTOURL_MAXLENGTH,value=500)
	@GraphProperty(propertyName = "photo_url")
	private String photoURL;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,  timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "delete_time")
	private Date deleteTime;	
	
	public String getPhotoURL() {
		return photoURL;
	}
	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}
	public Date getDeleteTime() {
		return deleteTime;
	}
	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}
	public String getName() {
		return name;
	}
	public String getRecurEndType() {
		return recurEndType;
	}
	public void setRecurEndType(String recurEndType) {
		this.recurEndType = recurEndType;
	}
	public void setName(String name) {
		if(name !=null)
			this.name = name.trim();
	}
	public String getEventDescription() {
		return eventDescription;
	}
	public void setEventDescription(String eventDescription) {
		if(eventDescription!=null)
			this.eventDescription = eventDescription.trim();
	}
	public Date getStartDate() {
		return startDate == null ? null : (Date) startDate.clone();
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate== null ? null : (Date) startDate.clone();
	}
	public Date getEndDate() {
		return endDate== null ? null : (Date) endDate.clone();
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate== null ? null : (Date) endDate.clone();
	}
	public String getRestaurantGuid() {
		return restaurantGuid;
	}
	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	public List<String> getDayOfTheWeek() {
		return dayOfTheWeek;
	}
	public void setDayOfTheWeek(List<String> dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}
	public String getBlockingType() {
		return blockingType;
	}
	public void setBlockingType(String blockingType) {
		this.blockingType = blockingType;
	}
	public Date getStartTime() {
		return startTime == null ? null : (Date) startTime.clone();
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime == null ? null : (Date) startTime.clone();
	}
	public Date getEndTime() {
		return endTime == null ? null : (Date) endTime.clone();
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime == null ? null : (Date) endTime.clone();
	}
	public String getRecurrenceType() {
		return recurrenceType;
	}
	public void setRecurrenceType(String recurrenceType) {
		this.recurrenceType = recurrenceType;
	}
	public int getRecurEvery() {
		return recurEvery;
	}
	public void setRecurEvery(int recurEvery) {
		this.recurEvery = recurEvery;
	}
	public String getRecurOn() {
		return recurOn;
	}
	public void setRecurOn(String recurOn) {
		this.recurOn = recurOn;
	}
	public int getNumOfRecurrence() {
		return numOfRecurrence;
	}
	public void setNumOfRecurrence(int numOfRecurrence) {
		this.numOfRecurrence = numOfRecurrence;
	}
	public Date getRecurrenceEndDate() {
		return recurrenceEndDate == null ? null : (Date) recurrenceEndDate.clone();
	}
	public void setRecurrenceEndDate(Date recurrenceEndDate) {
		this.recurrenceEndDate = recurrenceEndDate == null ? null : (Date) recurrenceEndDate.clone();
	}
	public boolean isAllday() {
		return allday;
	}
	public void setAllday(boolean allday) {
		this.allday = allday;
	}
	public boolean isRecurring() {
		return recurring;
	}
	public void setRecurring(boolean recurring) {
		this.recurring = recurring;
	}
	
	public void setInfoOnCreate(UserInfoModel userInfo) {
		super.setInfoOnCreate(userInfo);
		if(userInfo.getUserType().equals(Constants.STAFF_STRING) && !userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)){
			this.setRestaurantGuid(userInfo.getRestGuid());
		}if(this.allday==true){
			this.startTime=new LocalDate(0).toDate();
			this.endTime = new Date(startTime.getTime()+24*60*60*1000-1);
		}if(this.type.equals(Constants.HOLIDAY))
			this.category = Constants.HOLIDAY;
	}
	
	@Override
	public void setInfoOnUpdate(UserInfoModel userInfo) {
		super.setInfoOnUpdate(userInfo);
		if(userInfo.getUserType().equals(Constants.STAFF_STRING) && !userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)){
			this.setRestaurantGuid(userInfo.getRestGuid());
		}
		if(this.allday==true){
			this.startTime=new LocalDate(0).toDate();
			this.endTime = new Date(startTime.getTime()+Constants.DAY- 1000);			
		}
	}
	
	public int getDateOfMonth() {
		return dateOfMonth;
	}
	public void setDateOfMonth(int dateOfMonth) {
		this.dateOfMonth = dateOfMonth;
	}
	public int getWeekOfMonth() {
		return weekOfMonth;
	}
	public void setWeekOfMonth(int weekOfMonth) {
		this.weekOfMonth = weekOfMonth;
	}
	/*public String getBlockingAreaType() {
		return blockingAreaType;
	}
	public void setBlockingAreaType(String blockingAreaType) {
		this.blockingAreaType = blockingAreaType;
	}*/
	public List<String> getBlockingArea() {
		return blockingArea;
	}
	public void setBlockingArea(List<String> blockingArea) {
		this.blockingArea = blockingArea;
	}
	public Boolean getIsDraft() {
	    return isDraft;
	}
	public void setIsDraft(Boolean isDraft) {
	    this.isDraft = isDraft;
	}
	public boolean isValidateCategory() {
		return validateCategory;
	}
	public void setValidateCategory(boolean validateCategory) {
		this.validateCategory = validateCategory;
	}
	public boolean isOngoingValidation() {
		return ongoingValidation;
	}
	public void setOngoingValidation(boolean ongoingValidation) {
		this.ongoingValidation = ongoingValidation;
	}
	public boolean isPromoteValidation() {
		return promoteValidation;
	}
	public void setPromoteValidation(boolean promoteValidation) {
		this.promoteValidation = promoteValidation;
	}

        
	
}
