package com.clicktable.model;




import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonInclude(Include.NON_NULL)
public class Menu extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4640462343160299851L;
	
	private String name;
	@GraphProperty(propertyName = "display_name")
	private String displayName;
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;
	private String description;
	private String type;
	private boolean published;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getRestaurantGuid() {
		return restaurantGuid;
	}
	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	


	
	
	

}
