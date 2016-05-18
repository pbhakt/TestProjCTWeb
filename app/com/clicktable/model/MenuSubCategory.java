package com.clicktable.model;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonInclude(Include.NON_NULL)
public class MenuSubCategory extends Entity{
	private static final long serialVersionUID = -6971626144008794612L;
	
	private String name;
	private String description;
	@GraphProperty(propertyName = "category_guid")
	private String categoryGuid;
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategoryGuid() {
		return categoryGuid;
	}
	public void setCategoryGuid(String categoryGuid) {
		this.categoryGuid = categoryGuid;
	}
	public String getRestaurantGuid() {
		return restaurantGuid;
	}
	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid;
	}


	
	
}
