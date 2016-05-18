package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Event;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;


@RelationshipEntity(type = RelationshipTypes.HAS_CAL_EVENT)
public class HasCalanderEvent extends RelEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6272882643725075321L;
	@Fetch
	@StartNode
	private Event event;
	@Fetch
	@EndNode
	@JsonBackReference(value="event")
	private CalenderEvent calanderEvent;
	
	public HasCalanderEvent(Event event, CalenderEvent calenderEvent) {
		this.event= event;
		this.calanderEvent = calenderEvent;
	}
	
	public HasCalanderEvent() {
		super();
	}

	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public CalenderEvent getCalanderEvent() {
		return calanderEvent;
	}
	public void setCalanderEvent(CalenderEvent calanderEvent) {
		this.calanderEvent = calanderEvent;
	}
}
