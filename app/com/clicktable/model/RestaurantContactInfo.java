package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.LATITUDE_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.LONGITUDE_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.PHONE_NO2_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.WEBSITE_MAX_LENGTH;
import play.data.validation.Constraints.MaxLength;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class RestaurantContactInfo extends Entity 
{

	private static final long serialVersionUID = -1029048878805229612L;
	/*private String name;*/
	//@Required(message=LATITUDE_REQUIRED)
	@MaxLength(message=LATITUDE_MAX_LENGTH,value=200)
	private String latitude;
	//@Required(message=LONGITUDE_REQUIRED)
	@MaxLength(message=LONGITUDE_MAX_LENGTH,value=200)
	private String longitude;
	@MaxLength(message=WEBSITE_MAX_LENGTH,value=100)
	private String website;

	/*@MaxLength(message=PHONE_NO1_MAX_LENGTH,value=20)
	private String phoneNo1;*/
	@MaxLength(message=PHONE_NO2_MAX_LENGTH,value=20)
	private String phoneNo2;
	/*private String addressLine1;*/
	private String addressLine2;

	/*@Required(message=REGION_REQUIRED)
	@MaxLength(message=REGION_MAX_LENGTH,value=100)
	private String region;*/
	/*private String countryCode;
	private String state;
	private String city;*/
	/*@Required(message=LOCALITY_REQUIRED)
	@MaxLength(message=LOCALITY_MAX_LENGTH,value=500)
	@MinLength(message=LOCALITY_MIN_LENGTH,value=2)
	private String locality;
	@MaxLength(message=BUILDING_MAX_LENGTH,value=100)
	private String building;
	private Integer zipcode;*/
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



	

}
