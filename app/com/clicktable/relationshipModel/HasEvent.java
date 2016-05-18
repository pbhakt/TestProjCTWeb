package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Event;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

@RelationshipEntity(type = RelationshipTypes.HAS_EVENT)
public class HasEvent  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8914593204407807309L;
	@Fetch
	@StartNode
	private Restaurant rest;
	@Fetch
	@EndNode
	@JsonBackReference(value="event")
	private Event event;
	
	

	public HasEvent() {
		super();
	}
	
	public HasEvent(Restaurant restaurant, Event event) {
		super();
		this.rest = restaurant;
		this.event = event;
	}

	public Restaurant getRest() {
		return rest;
	}
	public void setRest(Restaurant rest) {
		this.rest = rest;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
}
