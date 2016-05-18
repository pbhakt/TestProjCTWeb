package com.clicktable.model;

import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;

import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author p.singh
 *
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Device extends Entity {

	/**
     * 
     */
	private static final long serialVersionUID = 1051870412093325208L;

	@Required(message=ErrorCodes.DEVICE_ID)
	@MaxLength(message=ErrorCodes.DEVICE_ID_MAXLENGTH,value=10)
	@Pattern(".*\\S+.*")
	@GraphProperty(propertyName = "device_id")
	private String deviceId;
	@Required(message=ErrorCodes.DEVICE_TYPE)
	@MaxLength(message=ErrorCodes.DEVICE_TYPE_MAXLENGTH,value=100)
	@Pattern(".*\\S+.*")
	private String type;
	@Required(message=ErrorCodes.DEVICE_MANUFACTURER)
	@MaxLength(message=ErrorCodes.DEVICE_MANUFACTURER_MAXLENGTH,value=100)
	@Pattern(".*\\S+.*")
	private String manufacturer;
	/*
	 * @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="IST")
	 * 
	 * @GraphProperty(propertyName = "last_login") private Date lastLogin;
	 */
	@Required(message=ErrorCodes.DEVICE_REST_ID)
	@Pattern(".*\\S+.*")
	@Transient
	private String restaurantGuid;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		/*
		 * if(null != deviceId) { this.deviceId = deviceId.trim(); } else
		 */
		this.deviceId = deviceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		/*
		 * if(null != type) { this.type = type.trim(); } else
		 */
		this.type = type;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		/*
		 * if(null != manufacturer) { this.manufacturer = manufacturer.trim(); }
		 * else
		 */
		this.manufacturer = manufacturer;
	}

	/*
	 * public Date getLastLogin() { return lastLogin; } public void
	 * setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }
	 */
	public String getRestaurantGuid() {
		return restaurantGuid;
	}

	public void setRestaurantGuid(String restaurantGuid) {
		/*
		 * if(null != restaurantGuid) { this.restaurantGuid =
		 * restaurantGuid.trim(); } else
		 */
		this.restaurantGuid = restaurantGuid;
	}

}
