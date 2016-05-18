package com.clicktable.model;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Onboarding extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7706717827767889923L;

	@Required(message = ErrorCodes.ONBOARD_FIRST_NAME_REQUIRED)
	@MaxLength(message = ErrorCodes.ONBOARD_FIRST_NAME_MAX_LENGTH, value = 100)
	@Pattern(message = ErrorCodes.INVALID_PATTERN, value = ".*\\S+.*")
	@GraphProperty(propertyName = "first_name")
	private String firstName;

	@Required(message = ErrorCodes.ONBOARD_LAST_NAME_REQUIRED)
	@MaxLength(message = ErrorCodes.ONBOARD_LAST_NAME_MAX_LENGTH, value = 60)
	@Pattern(message = ErrorCodes.INVALID_PATTERN, value = ".*\\S+.*")
	@GraphProperty(propertyName = "last_name")
	private String lastName;

	private String legalName;

	@Required(message = ErrorCodes.ONBOARD_EMAIL_REQUIRED)
	@Email(message = ErrorCodes.ONBOARD_INVALID_EMAIL_FORMAT)
	@MaxLength(message = ErrorCodes.ONBOARD_EMAIL_MAX_LENGTH, value = 100)
	private String email;

	@Required(message = ErrorCodes.ONBOARD_DESIGNATION_REQUIRED)
	@MaxLength(message = ErrorCodes.ONBOARD_DESIGNATION_MAX_LENGTH, value = 100)
	@Pattern(message = ErrorCodes.INVALID_PATTERN, value = ".*\\S+.*")
	private String designation;

	@Required(message = ErrorCodes.ONBOARD_MOBILE_REQUIRED)
	@MaxLength(message = ErrorCodes.ONBOARD_MOBILE_MAX_LENGTH, value = 60)
	@Pattern(message = ErrorCodes.INVALID_MOBILE, value = ".*[0-9].*")
	@GraphProperty(propertyName = "mobile")
	private String mobileNo;

	@Required(message = ErrorCodes.ONBOARD_REQUEST_STATUS_REQUIRED)
	private String requestStatus = Constants.NEW_REQUEST;

	@Required(message = ErrorCodes.ONBOARD_REST_NAME_REQUIRED)
	@MaxLength(message = ErrorCodes.ONBOARD_REST_NAME_MAX_LENGTH, value = 200)
	@GraphProperty(propertyName = "restaurant_name")
	@Pattern(message = ErrorCodes.INVALID_PATTERN, value = ".*\\S+.*")
	private String restaurantName;

	@Required(message = ErrorCodes.ONBOARD_CONTACT_REQUIRED)
	@MaxLength(message = ErrorCodes.ONBOARD_CONTACT_MAX_LENGTH, value = 40)
	private String contact;

	@Required(message = ErrorCodes.ONBOARD_ADDRESS_LINE_1_REQUIRED)
	@MaxLength(message = ErrorCodes.ONBOARD_ADDRESS_LINE_1_MAX_LENGTH, value = 550)
	@Pattern(message = ErrorCodes.INVALID_PATTERN, value = ".*\\S+.*")
	private String addressLine1;

	@MaxLength(message = ErrorCodes.ONBOARD_ADDRESS_LINE_2_MAX_LENGTH, value = 550)
	@Pattern(message = ErrorCodes.INVALID_PATTERN, value = ".*\\S+.*")
	private String addressLine2;

	@MaxLength(message = ErrorCodes.ONBOARD_LANDMARK_MAX_LENGTH, value = 550)
	@Pattern(message = ErrorCodes.INVALID_PATTERN, value = ".*\\S+.*")
	private String landmark;

	@Required(message = ErrorCodes.ONBOARD_COUNTRY_CODE_REQUIRED)
	@MinLength(message = ErrorCodes.ONBOARD_COUNTRY_CODE_MIN_LENGTH, value = 2)
	@MaxLength(message = ErrorCodes.ONBOARD_COUNTRY_CODE_MAX_LENGTH, value = 5)
	@GraphProperty(propertyName = "country_cd")
	private String countryCode;

	@Required(message = ErrorCodes.ONBOARD_STATE_REQUIRED)
	@MaxLength(message = ErrorCodes.ONBOARD_STATE_MAX_LENGTH, value = 60)
	@Pattern(message = ErrorCodes.INVALID_PATTERN, value = ".*\\S+.*")
	private String state;

	@Required(message = ErrorCodes.ONBOARD_CITY_REQUIRED)
	@MaxLength(message = ErrorCodes.ONBOARD_CITY_MAX_LENGTH, value = 60)
	@Pattern(message = ErrorCodes.INVALID_PATTERN, value = ".*\\S+.*")
	private String city;

	// @Required(message=ErrorCodes.ONBOARD_LOCALITY_REQUIRED)
	@MaxLength(message = ErrorCodes.ONBOARD_LOCALITY_MAX_LENGTH, value = 550)
	@Pattern(message = ErrorCodes.INVALID_PATTERN, value = ".*\\S+.*")
	private String locality;

	@GraphProperty(propertyName = "verification_code")
	private String verificationCode;

	@GraphProperty(propertyName = "is_verified")
	private Boolean isVerified;

	@GraphProperty(propertyName = "read_conditions")
	private Boolean readConditions = false;

	@Required(message = ErrorCodes.ONBOARD_ZIPCODE_REQUIRED)
	private Integer zipcode;

	private String authy_id;

	@GraphProperty(propertyName = "region")
	private String region;

	@GraphProperty(propertyName = "building")
	private String building;

	@GraphProperty(propertyName = "currency")
	private String currency;

	@GraphProperty(propertyName = "reason_to_reject")
	private String reasonToReject;

	@GraphProperty(propertyName = "rest_guid")
	private String restGuid;

	/**
	 * @return the authy_id
	 */
	public String getAuthy_id() {
		return authy_id;
	}

	/**
	 * @param authy_id
	 *            the authy_id to set
	 */
	public void setAuthy_id(String authy_id) {
		this.authy_id = authy_id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public Integer getZipcode() {
		return zipcode;
	}

	public void setZipcode(Integer zipcode) {
		this.zipcode = zipcode;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Boolean getReadConditions() {
		return readConditions;
	}

	public void setReadConditions(Boolean readConditions) {
		this.readConditions = readConditions;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getReasonToReject() {
		return reasonToReject;
	}

	public void setReasonToReject(String reasonToReject) {
		this.reasonToReject = reasonToReject;
	}

	public String getRestGuid() {
		return restGuid;
	}

	public void setRestGuid(String restGuid) {
		this.restGuid = restGuid;
	}

}
