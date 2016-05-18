package com.clicktable.model;


import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class CorporateOffers extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -965702759786523115L;



	
/*	public CorporateOffers(Node node) {
		this.name = (String) node.getProperty("name");
		this.notes = (String) node.getProperty("notes");
		this.offer = (String) node.getProperty("offer");
		this.restaurantGuid = (String) node.getProperty("rest_guid");
	}

	public CorporateOffers() {
	}

	public CorporateOffers(CorporateOffers existing) {
		this.guid = existing.getGuid();
		this.status = existing.getStatus();
		this.name = existing.getName();
		this.description = existing.getDescription();
	}*/

	@GraphProperty(propertyName = "name")
	private String name;

	@Required(message = ErrorCodes.REST_GUID_REQUIRED)
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;
	
	
	@GraphProperty(propertyName = "notes")
	private String notes;
	
	@GraphProperty(propertyName = "offer")
	private String offer;

	/*
	 * getters and setters
	 */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getRestaurantGuid() {
		return restaurantGuid;
	}

	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	
	
	public String getOffer() {
		return offer;
	}

	public void setOffer(String offer) {
		this.offer = offer;
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
