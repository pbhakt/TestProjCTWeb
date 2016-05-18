package com.clicktable.model;

import javax.persistence.Column;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class GuestConversation extends Entity {

	public GuestConversation(String guestId, EventPromotion eventPromotion) {
		super();
		this.restaurantGuid = eventPromotion.getRestaurantGuid();
		this.message = eventPromotion.getMessage();
		this.guestGuid = guestId;
		this.sentBy = Constants.RESTAURANT_ENUM;
		this.origin = Constants.EVENT_PROMOTION_ENUM_VALUE;
		this.originGuid = eventPromotion.getGuid();
	}

	public GuestConversation(Reservation reservation, String message) {
		super();
		this.restaurantGuid = reservation.getRestaurantGuid();
		this.message = message;
		this.guestGuid = reservation.getGuestGuid();
		this.sentBy = Constants.RESTAURANT_ENUM;
		this.origin = Constants.RESERVATION_ENUM_VALUE;
		this.originGuid = reservation.getGuid();
	}

	public GuestConversation(BarEntry barEntry, String message) {
		super();
		this.restaurantGuid = barEntry.getRestaurantGuid();
		this.message = message;
		this.guestGuid = barEntry.getGuestGuid();
		this.sentBy = Constants.RESTAURANT_ENUM;
		this.origin = Constants.BAR_ENTRY_ENUM_VALUE;
		this.originGuid = barEntry.getGuid();
	}

	public GuestConversation() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 4457962269699502291L;

	@Required(message = ErrorCodes.GUESTCONVERSATION_REST_GUID)
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;

	@Required(message = ErrorCodes.GUESTCONVERSATION_MESSAGE_REQUIRED)
	@MaxLength(message = ErrorCodes.GUESTCONVERSATION_MESSAGE_MAXLENGTH, value = 300)
	private String message;

	@Required(message = ErrorCodes.GUESTCONVERSATION_GUEST_GUID)
	@GraphProperty(propertyName = "guest_guid")
	private String guestGuid;

	/**
	 * Field guestMobileNum.
	 */
	@Column(name = "mobile_number")
	// @Required(message = ErrorCodes.GUESTCONVERSATION_GUEST_MOBILE_NUM)
	// @MaxLength(message =
	// ErrorCodes.GUESTCONVERSATION_GUEST_MOBILE_MAX_LENGTH, value = 10)
	// @MinLength(message =
	// ErrorCodes.GUESTCONVERSATION_GUEST_MOBILE_MIN_LENGTH, value = 10)
	private String guestMobileNum;

	public String getGuestMobileNum() {
		return guestMobileNum;
	}

	public void setGuestMobileNum(String guestMobileNum) {
		this.guestMobileNum = guestMobileNum;
	}

	public String getSmsStatusCause() {
		return smsStatusCause;
	}

	public void setSmsStatusCause(String smsStatusCause) {
		this.smsStatusCause = smsStatusCause;
	}

	@Required(message = ErrorCodes.GUESTCONVERSATION_SENTBY)
	@GraphProperty(propertyName = "sent_by")
	private String sentBy;

	@Required(message = ErrorCodes.GUESTCONVERSATION_ORIGIN)
	private String origin;

	@GraphProperty(propertyName = "origin_guid")
	private String originGuid;

	@GraphProperty(propertyName = "sms_status")
	private String smsStatus = Constants.WAITING;
	@GraphProperty(propertyName = "sms_id")
	private String smsId;

	@GraphProperty(propertyName = "sms_status_cause")
	private String smsStatusCause = Constants.WAITING;

	@GraphProperty(propertyName = "ref_guid")
	private String refGuid;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSentBy() {
		return sentBy;
	}

	public void setSentBy(String sentBy) {
		this.sentBy = sentBy;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
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

	public String getOriginGuid() {
		return originGuid;
	}

	public void setOriginGuid(String originGuid) {
		this.originGuid = originGuid;
	}

	public String getSmsStatus() {
		return smsStatus;
	}

	public void setSmsStatus(String smsStatus) {
		this.smsStatus = smsStatus;
	}

	public String getSmsId() {
		return smsId;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

}
