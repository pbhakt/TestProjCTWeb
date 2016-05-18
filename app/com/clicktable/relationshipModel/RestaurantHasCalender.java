package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

@RelationshipEntity(type = RelationshipTypes.REST_HAS_CAL)
public class RestaurantHasCalender  extends RelEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1921166041537825246L;
	@Fetch
	@StartNode
	private Restaurant restaurant;
	@Fetch
	@EndNode
	@JsonBackReference(value="rest_cal")
	private CalenderEvent calanderEvent;
	

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public CalenderEvent getCalanderEvent() {
		return calanderEvent;
	}

	public void setCalanderEvent(CalenderEvent calanderEvent) {
		this.calanderEvent = calanderEvent;
	}

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
