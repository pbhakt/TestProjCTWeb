package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.BUILDING_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.BUILDING_REQUIRED;
import static com.clicktable.util.ErrorCodes.LATITUDE_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.LOCALITY_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.LOCALITY_MIN_LENGTH;
import static com.clicktable.util.ErrorCodes.LOCALITY_REQUIRED;
import static com.clicktable.util.ErrorCodes.LONGITUDE_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.ONBOARD_ADDRESS_LINE_1_REQUIRED;
import static com.clicktable.util.ErrorCodes.ONBOARD_CITY_REQUIRED;
import static com.clicktable.util.ErrorCodes.ONBOARD_COUNTRY_CODE_REQUIRED;
import static com.clicktable.util.ErrorCodes.ONBOARD_EMAIL_REQUIRED;
import static com.clicktable.util.ErrorCodes.ONBOARD_STATE_REQUIRED;
import static com.clicktable.util.ErrorCodes.ONBOARD_ZIPCODE_REQUIRED;
import static com.clicktable.util.ErrorCodes.PHONE_NO1_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.PHONE_NO1_REQUIRED;
import static com.clicktable.util.ErrorCodes.PHONE_NO2_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.REGION_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.REGION_REQUIRED;
import static com.clicktable.util.ErrorCodes.REST_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.WEBSITE_MAX_LENGTH;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class RestaurantContactInfoAdmin extends Entity 
{

	private static final long serialVersionUID = -1029048878805229612L;
	@Required(message=REST_NAME_REQUIRED)
	private String name;
	//@Required(message=LATITUDE_REQUIRED)
	@MaxLength(message=LATITUDE_MAX_LENGTH,value=200)
	private String latitude;
	//@Required(message=LONGITUDE_REQUIRED)
	@MaxLength(message=LONGITUDE_MAX_LENGTH,value=200)
	private String longitude;
	@MaxLength(message=WEBSITE_MAX_LENGTH,value=100)
	private String website;
	@Required(message=PHONE_NO1_REQUIRED)
	@MaxLength(message=PHONE_NO1_MAX_LENGTH,value=20)
	private String phoneNo1;
	@MaxLength(message=PHONE_NO2_MAX_LENGTH,value=20)
	private String phoneNo2;
	@Required(message=ONBOARD_ADDRESS_LINE_1_REQUIRED)
	private String addressLine1;
	private String addressLine2;

	@Required(message=REGION_REQUIRED)
	@MaxLength(message=REGION_MAX_LENGTH,value=100)
	private String region;
	@Required(message=ONBOARD_COUNTRY_CODE_REQUIRED)
	private String countryCode;
	@Required(message=ONBOARD_STATE_REQUIRED)
	private String state;
	@Required(message=ONBOARD_CITY_REQUIRED)
	private String city;
	@Required(message=LOCALITY_REQUIRED)
	@MaxLength(message=LOCALITY_MAX_LENGTH,value=500)
	@MinLength(message=LOCALITY_MIN_LENGTH,value=2)
	private String locality;
	//@Required(message=BUILDING_REQUIRED)
	@MaxLength(message=BUILDING_MAX_LENGTH,value=100)
	private String building;
	@Required(message=ONBOARD_ZIPCODE_REQUIRED)
	private Integer zipcode;
	@Required(message=ONBOARD_EMAIL_REQUIRED)
	private String email;
	
	private String landmark;

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * @param website the website to set
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * @return the phoneNo2
	 */
	public String getPhoneNo2() {
		return phoneNo2;
	}

	/**
	 * @param phoneNo2 the phoneNo2 to set
	 */
	public void setPhoneNo2(String phoneNo2) {
		this.phoneNo2 = phoneNo2;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the addressLine2
	 */
	public String getAddressLine2() {
		return addressLine2;
	}

	/**
	 * @param addressLine2 the addressLine2 to set
	 */
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the landmark
	 */
	public String getLandmark() {
		return landmark;
	}

	/**
	 * @param landmark the landmark to set
	 */
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNo1() {
		return phoneNo1;
	}

	public void setPhoneNo1(String phoneNo1) {
		this.phoneNo1 = phoneNo1;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
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

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public Integer getZipcode() {
		return zipcode;
	}

	public void setZipcode(Integer zipcode) {
		this.zipcode = zipcode;
	}



	

}
