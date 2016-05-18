package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.BAR_ENTRY_GUEST_GUID_REQUIRED;
import static com.clicktable.util.ErrorCodes.BAR_ENTRY_NOTE_MAXLENGTH;
import static com.clicktable.util.ErrorCodes.BAR_ENTRY_REST_GUID_REQUIRED;
import static com.clicktable.util.ErrorCodes.NUM_COVERS_MIN_VALUE;
import static com.clicktable.util.ErrorCodes.NUM_COVERS_REQUIRED;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@TypeAlias("BarEntry")
@JsonInclude(Include.NON_NULL)
public class BarEntry extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4782310580874315095L;

	@Required(message = NUM_COVERS_REQUIRED)
	@Min(message = NUM_COVERS_MIN_VALUE, value = 1)
	@GraphProperty(propertyName = "num_covers")
	private Integer numCovers;

	@Required(message = BAR_ENTRY_REST_GUID_REQUIRED)
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;

	@Required(message = BAR_ENTRY_GUEST_GUID_REQUIRED)
	@GraphProperty(propertyName = "guest_guid")
	private String guestGuid;

	@MaxLength(message = BAR_ENTRY_NOTE_MAXLENGTH, value = 500)
	@GraphProperty(propertyName = "note")
	private String note;

	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "start_time")
	private Date startTime = new Timestamp(new Date().getTime());

	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "end_time")
	private Date endTime;

	private GuestProfile guestProfile;

	public void setNonUpdatableField() {
		this.setCreatedDate(null);
		this.setCreatedBy(null);
		this.setLanguageCode(null);
		this.setStartTime(null);
		this.setRestaurantGuid(null);
		this.setGuestGuid(null);

	}

	public String getRestaurantGuid() {
		return restaurantGuid;
	}

	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid;
	}

	public String getGuestGuid() {
		return guestGuid;
	}

	public void setGuestGuid(String guestGuid) {
		this.guestGuid = guestGuid;
	}

	public Integer getNumCovers() {
		return numCovers;
	}

	public void setNumCovers(Integer numCovers) {
		this.numCovers = numCovers;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getStartTime() {
		return startTime == null ? null : (Date) startTime.clone();
	}

	public void setStartTime(Date startTime) {
		this.startTime = this.getCreatedDate();
	}

	public Date getEndTime() {
		return endTime == null ? null : (Date) endTime.clone();
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public GuestProfile getGuestProfile() {
		return guestProfile;
	}

	public void setGuestProfile(GuestProfile guestProfile) {
		this.guestProfile = guestProfile;
	}

	@Override
	public void setInfoOnCreate(UserInfoModel userInfo) {
		super.setInfoOnCreate(userInfo);
		if (userInfo != null) {
			if (!(userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID) || userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) && (!userInfo.getRestGuid().isEmpty())) {
				this.setRestaurantGuid(userInfo.getRestGuid());
			}
		}

	}
}
