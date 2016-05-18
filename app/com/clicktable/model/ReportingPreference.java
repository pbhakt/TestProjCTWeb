package com.clicktable.model;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ReportingPreference extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 75856948822028910L;

	@Required(message = ErrorCodes.REST_GUID_REQUIRED)
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;
	
	@Email(message = ErrorCodes.INVALID_EMAIL_FORMAT)
	@MaxLength(message = ErrorCodes.EMAIL_MAX_LENGTH, value = 100)
	@GraphProperty(propertyName = "owner_email")
	private String ownerEmail;
	
	@MinLength(message = ErrorCodes.MOBILE_MIN_LENGTH, value = 10)
	@MaxLength(message = ErrorCodes.MOBILE_MAX_LENGTH, value = 10)
	@GraphProperty(propertyName = "owner_mobile")
	private String ownerMobile;
	
	@MinLength(message = ErrorCodes.NAME_MIN_LENGTH, value = 2)
	@MaxLength(message = ErrorCodes.NAME_MAX_LENGTH, value = 100)
	@GraphProperty(propertyName = "owner_name")
	private String ownerName;
	
	@Email(message = ErrorCodes.INVALID_EMAIL_FORMAT)
	@MaxLength(message = ErrorCodes.EMAIL_MAX_LENGTH, value = 100)
	@GraphProperty(propertyName = "manager_email")
	private String managerEmail;
	
	@MinLength(message = ErrorCodes.MOBILE_MIN_LENGTH, value = 10)
	@MaxLength(message = ErrorCodes.MOBILE_MAX_LENGTH, value = 10)
	@GraphProperty(propertyName = "manager_mobile")
	private String managerMobile;
	
	@MinLength(message = ErrorCodes.NAME_MIN_LENGTH, value = 2)
	@MaxLength(message = ErrorCodes.NAME_MAX_LENGTH, value = 100)
	@GraphProperty(propertyName = "manager_name")
	private String managerName;

	@Required(message = ErrorCodes.SALES_PERSON_EMAIL_REQUIRED)
	@Email(message = ErrorCodes.INVALID_EMAIL_FORMAT)
	@MaxLength(message = ErrorCodes.EMAIL_MAX_LENGTH, value = 100)
	@GraphProperty(propertyName = "sales_person_email")
	private String salesPersonEmail;
	
	@MinLength(message = ErrorCodes.MOBILE_MIN_LENGTH, value = 10)
	@MaxLength(message = ErrorCodes.MOBILE_MAX_LENGTH, value = 10)
	@GraphProperty(propertyName = "sales_person_mobile")
	private String salesPersonMobile;
	
	@Required(message = ErrorCodes.SALES_PERSON_NAME_REQUIRED)
	@MinLength(message = ErrorCodes.NAME_MIN_LENGTH, value = 2)
	@MaxLength(message = ErrorCodes.NAME_MAX_LENGTH, value = 100)
	@GraphProperty(propertyName = "sales_person_name")
	private String salesPersonName;


	public String getRestaurantGuid() {
		return restaurantGuid;
	}


	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid;
	}


	public String getOwnerEmail() {
		return ownerEmail;
	}


	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}


	public String getOwnerMobile() {
		return ownerMobile;
	}


	public void setOwnerMobile(String ownerMobile) {
		this.ownerMobile = ownerMobile;
	}


	public String getOwnerName() {
		return ownerName;
	}


	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}


	public String getManagerEmail() {
		return managerEmail;
	}


	public void setManagerEmail(String managerEmail) {
		this.managerEmail = managerEmail;
	}


	public String getManagerMobile() {
		return managerMobile;
	}


	public void setManagerMobile(String managerMobile) {
		this.managerMobile = managerMobile;
	}


	public String getManagerName() {
		return managerName;
	}


	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}


	public String getSalesPersonEmail() {
		return salesPersonEmail;
	}


	public void setSalesPersonEmail(String salesPersonEmail) {
		this.salesPersonEmail = salesPersonEmail;
	}


	public String getSalesPersonMobile() {
		return salesPersonMobile;
	}


	public void setSalesPersonMobile(String salesPersonMobile) {
		this.salesPersonMobile = salesPersonMobile;
	}


	public String getSalesPersonName() {
		return salesPersonName;
	}


	public void setSalesPersonName(String salesPersonName) {
		this.salesPersonName = salesPersonName;
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
