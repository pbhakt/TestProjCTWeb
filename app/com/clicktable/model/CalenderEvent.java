package com.clicktable.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.joda.time.DateTime;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class CalenderEvent extends Entity {

	private static final long serialVersionUID = -597395093858003808L;

	@Required(message = ErrorCodes.CAL_EVENT_NAME)
	private String name;
	@Required(message = ErrorCodes.CAL_EVENT_TYPE)
	private String type;
	@Required(message = ErrorCodes.CAL_EVENT_CATEGORY)
	private String category;
	@Required(message = ErrorCodes.EVENT_INVALID_RESTID)
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;
	@GraphProperty(propertyName = "blocking_type")
	private String blockingType;
	@GraphProperty(propertyName = "parent_event_guid")
	private String parentEventGuid;
	@GraphProperty(propertyName = "event_desc")
	private String eventDescription;
	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "event_dt")
	private Date eventDate;
	@GraphProperty(propertyName = "sub_category")
	private String subCategory;
	/*
	 * @JsonFormat(pattern = Constants.DATE_FORMAT,
	 * timezone=Constants.DEFAULT_TIMEZONE)
	 * 
	 * @GraphProperty(propertyName = "event_end_dt") private Date eventEndDate;
	 */
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "start_time")
	private Date startTime;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "end_time")
	private Date endTime;
	@GraphProperty(propertyName = "blocking_area")
	private List<String> blockingArea;

	private boolean allday = false;

	@GraphProperty(propertyName = "photo_url")
	private String photoURL;

	@Transient
	private Long startDate;

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public CalenderEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CalenderEvent(Event event, Date eventDate, Date startTime,
			Date endTime, List<String> tableGuids) {
		this(event);
		this.startTime = new Date(startTime.getTime());
		this.endTime = new Date(endTime.getTime());
		this.eventDate = new Date(eventDate.getTime());
		this.blockingArea = tableGuids;
	}

	public CalenderEvent(Event event) {
		this.setGuid(UtilityMethods.generateCtId());
		copyValuesFromEvent(event);
		this.setBlockingArea(event.getBlockingArea());
	}

	public CalenderEvent(Event event, Date eventDate) {
		this(event);
		DateTime eventStart = UtilityMethods.addTimeToDate(
				event.getStartDate(), event.getStartTime());
		DateTime eventEnd = UtilityMethods.addTimeToDate(event.getEndDate(),
				event.getEndTime());
		long duration = eventEnd.getMillis() - eventStart.getMillis();
		DateTime startTime = UtilityMethods.addTimeToDate(eventDate,
				event.getStartTime());
		DateTime endTime = startTime.plus(duration);
		// Fix for bug CT-1096
		if(event.isRecurring() && event.getRecurEndType().equals(Constants.END_ON_DATE) 
				&& (new DateTime(event.getRecurrenceEndDate()).plusDays(1)).isBefore(endTime)){
			endTime = new DateTime(event.getRecurrenceEndDate()).plusDays(1).minusMinutes(1);
		}
		this.startTime = startTime.toDate();
		this.endTime = endTime.toDate();

	}

	public void copyValuesFromEvent(Event event) {
		this.name = event.getName();
		this.type = event.getType();
		this.category = event.getCategory();
		this.restaurantGuid = event.getRestaurantGuid();
		this.blockingType = event.getBlockingType();
		// this.eventDate = event.getStartDate();
		this.parentEventGuid = event.getGuid();
		this.eventDescription = event.getEventDescription();
		this.photoURL = event.getPhotoURL();
		// this.startTime = event.getStartTime();
		// this.endTime = event.getEndTime();

		this.subCategory = event.getSubCategory();
		this.allday =event.isAllday();
		this.setStatus(event.getStatus());
		this.setCreatedDate(event.getCreatedDate());
		this.setUpdatedDate(event.getUpdatedDate());
		this.setCreatedBy(event.getCreatedBy());
		this.setUpdatedBy(event.getUpdatedBy());
		// this.setBlockingArea(event.getBlockingArea());
	}

	public String getRestaurantGuid() {
		return restaurantGuid;
	}

	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid;
	}

	public String getBlockingType() {
		return blockingType;
	}

	public void setBlockingType(String blockingType) {
		this.blockingType = blockingType;
	}

	public String getParentEventGuid() {
		return parentEventGuid;
	}

	public void setParentEventGuid(String parentEventGuid) {
		this.parentEventGuid = parentEventGuid;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public Date getEventDate() {
		return eventDate == null ? null : (Date) eventDate.clone();
		// return new Date(eventDate.getTime());
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate == null ? null : (Date) eventDate.clone();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getBlockingArea() {
		return blockingArea;
	}

	public void setBlockingArea(List<String> blockingArea) {
		this.blockingArea = blockingArea;
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
	
	public boolean isAllday() {
		return allday;
	}

	public void setAllday(boolean allday) {
		this.allday = allday;
	}

	
	
	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	@Override
	public void setInfoOnUpdate(UserInfoModel userInfo) {
		super.setInfoOnUpdate(userInfo);
		if (userInfo.getUserType().equals(Constants.STAFF_STRING)
				&& !userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
			this.setRestaurantGuid(userInfo.getRestGuid());
		}
	}

}
