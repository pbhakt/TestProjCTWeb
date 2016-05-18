package com.clicktable.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

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
public class EventPromotion extends Entity {

	private static final long serialVersionUID = 4457962269699502291L;
	@Required(message=ErrorCodes.REST_GUID_REQUIRED)
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;
	
	@Required(message=ErrorCodes.VISITED_AFTER_REQUIRED)
	@GraphProperty(propertyName = "visited_in_last")
	private Integer visitedInLast;
	
	@Required(message=ErrorCodes.MESSAGE_REQUIRED)
	@MaxLength(message=ErrorCodes.MESSAGE_MAXLENGTH,value=300)
	private String message;
	
	@Required(message=ErrorCodes.GUEST_TYPE_REQUIRED)
	@GraphProperty(propertyName = "guest_type")
	private String guestType;
	
	@Required(message=ErrorCodes.GENDER_REQUIRED)
	@GraphProperty(propertyName = "gender")
	private String gender;
	
	

	@Required(message=ErrorCodes.EVENT_GUID_REQUIRED)
	@GraphProperty(propertyName = "event_guid")
	private String eventGuid;

	@GraphProperty(propertyName = "dined_shift")
	private List<String> dinedShift = new ArrayList<String>();
	
	@GraphProperty(propertyName = "tag_guids")
	private List<String> tagGuids = new ArrayList<String>();

	@GraphProperty(propertyName = "corporate_guids")
	private List<String> corporateGuids = new ArrayList<String>();
	
	@Required(message=ErrorCodes.GUEST_COUNT_REQUIRED)
	@GraphProperty(propertyName = "guest_count")
	@Min(message=ErrorCodes.GUEST_COUNT_MIN,value=1)
	private Integer guestCount;

	
	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "birthday_after")
	private Date birthdayAfter;
	
	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "birthday_before")
	private Date birthdayBefore;
	
	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "anniversary_after")
	private Date anniversaryAfter;
	
	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "anniversary_before")
	private Date anniversaryBefore;
	
	
	public Integer getVisitedInLast() {
		return visitedInLast;
	}

	public void setVisitedInLast(Integer visitedInLast) {
		this.visitedInLast = visitedInLast;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getGuestType() {
		return guestType;
	}

	public void setGuestType(String guestType) {
		this.guestType = guestType;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRestaurantGuid() {
		return restaurantGuid;
	}

	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid;
	}

	public String getEventGuid() {
		return eventGuid;
	}

	public void setEventGuid(String eventGuid) {
		this.eventGuid = eventGuid;
	}

	public List<String> getTagGuids() {
		return tagGuids;
	}

	public void setTagGuids(List<String> tagGuids) {
		this.tagGuids = tagGuids;
	}
	
	public List<String> getCorporatesGuids() {
		return corporateGuids;
	}

	public void setCorporatesGuids(List<String> corporatesGuids) {
		this.corporateGuids = corporatesGuids;
	}

	public Integer getGuestCount() {
		return guestCount;
	}

	public void setGuestCount(Integer guestCount) {
		this.guestCount = guestCount;
	}

	public List<String> getDinedShift() {
		return dinedShift;
	}

	public void setDinedShift(List<String> dinedShift) {
		this.dinedShift = dinedShift;
	}

	public Date getBirthdayAfter() {
		return birthdayAfter;
	}

	public void setBirthdayAfter(Date birthdayAfter) {
		this.birthdayAfter = birthdayAfter;
	}

	public Date getBirthdayBefore() {
		return birthdayBefore;
	}

	public void setBirthdayBefore(Date birthdayBefore) {
		this.birthdayBefore = birthdayBefore;
	}

	public Date getAnniversaryAfter() {
		return anniversaryAfter;
	}

	public void setAnniversaryAfter(Date anniversaryAfter) {
		this.anniversaryAfter = anniversaryAfter;
	}

	public Date getAnniversaryBefore() {
		return anniversaryBefore;
	}

	public void setAnniversaryBefore(Date anniversaryBefore) {
		this.anniversaryBefore = anniversaryBefore;
	}

	
	
}
