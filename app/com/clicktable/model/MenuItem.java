package com.clicktable.model;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonInclude(Include.NON_NULL)
public class MenuItem extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4429254128686901275L;

	private String name;
	private String description;
	private String itemType;
	@GraphProperty(propertyName = "spice_level")
	private String spiceLevel;
	@GraphProperty(propertyName = "chef_recommendation")
	private boolean chefRecommendation;
	private int calories;
	@GraphProperty(propertyName = "dish_rating")
	private int dishRating;
	@GraphProperty(propertyName = "popularity_quotient")
	private int popularityQuotient;
	private double price;
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;
	@GraphProperty(propertyName = "category_guid")
	private String categoryGuid;
	@GraphProperty(propertyName = "sub_category_guid")
	private String subCategoryGuid;

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
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getSpiceLevel() {
		return spiceLevel;
	}
	public void setSpiceLevel(String spiceLevel) {
		this.spiceLevel = spiceLevel;
	}
	public boolean isChefRecommendation() {
		return chefRecommendation;
	}
	public void setChefRecommendation(boolean chefRecommendation) {
		this.chefRecommendation = chefRecommendation;
	}
	public int getCalories() {
		return calories;
	}
	public void setCalories(int calories) {
		this.calories = calories;
	}
	public int getDishRating() {
		return dishRating;
	}
	public void setDishRating(int dishRating) {
		this.dishRating = dishRating;
	}
	public int getPopularityQuotient() {
		return popularityQuotient;
	}
	public void setPopularityQuotient(int popularityQuotient) {
		this.popularityQuotient = popularityQuotient;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getRestaurantGuid() {
		return restaurantGuid;
	}
	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid;
	}
	public String getCategoryGuid() {
		return categoryGuid;
	}
	public void setCategoryGuid(String categoryGuid) {
		this.categoryGuid = categoryGuid;
	}
	public String getSubCategoryGuid() {
		return subCategoryGuid;
	}
	public void setSubCategoryGuid(String subCategoryGuid) {
		this.subCategoryGuid = subCategoryGuid;
	}
	
	
	

	

}
