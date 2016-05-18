package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Cuisine;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

@RelationshipEntity(type = RelationshipTypes.HAS_CUISINE)
public class RestaurantHasCuisineRelationshipModel  extends RelEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5688825053539157275L;
	
	@Fetch
	@StartNode
	@JsonBackReference(value="cuisine")
	Restaurant restaurant;
	@Fetch
	@EndNode
	Cuisine cuisine;

	private String state;
	private String city;
	private String area;
	private String locality;
	private String building;

	/**
	 * Default Constructor
	 */
	public RestaurantHasCuisineRelationshipModel() {
		super();
	}

	/**
	 * @param restaurant
	 * @param cuisine
	 */
	public RestaurantHasCuisineRelationshipModel(Restaurant restaurant,
			Cuisine cuisine) {
		super();
		this.restaurant = restaurant;
		this.cuisine = cuisine;
	}


	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the locality
	 */
	public String getLocality() {
		return locality;
	}

	/**
	 * @param locality
	 *            the locality to set
	 */
	public void setLocality(String locality) {
		this.locality = locality;
	}

	/**
	 * @return the area
	 */
	public String getArea() {
		return area;
	}

	/**
	 * @param area
	 *            the area to set
	 */
	public void setArea(String area) {
		this.area = area;
	}

	/**
	 * @return the building
	 */
	public String getBuilding() {
		return building;
	}

	/**
	 * @param building
	 *            the building to set
	 */
	public void setBuilding(String building) {
		this.building = building;
	}

	/**
	 * @return the restaurant
	 */
	public Restaurant getRestaurant() {
		return restaurant;
	}

	/**
	 * @param restaurant
	 *            the restaurant to set
	 */
	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	/**
	 * @return the cuisine
	 */
	public Cuisine getCuisine() {
		return cuisine;
	}

	/**
	 * @param cuisine
	 *            the cuisine to set
	 */
	public void setCuisine(Cuisine cuisine) {
		this.cuisine = cuisine;
	}

}
