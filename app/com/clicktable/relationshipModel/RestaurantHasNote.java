package com.clicktable.relationshipModel;

import java.util.Date;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import play.data.validation.Constraints.Required;

import com.clicktable.model.Note;
import com.clicktable.model.Restaurant;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

@RelationshipEntity(type = RelationshipTypes.REST_HAS_NOTE)
public class RestaurantHasNote  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4758096183137599475L;

	@Fetch
	@StartNode
	@JsonBackReference(value="note")
	Restaurant rest;

	@Fetch
	@EndNode
	@JsonBackReference(value="restnote")
	Note note ;
	
	@Required
	@GraphProperty(propertyName = "created_dt")
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date createdDate;

	
	public Date getCreatedDate() {
		return createdDate == null ? null : (Date) createdDate.clone();
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate == null ? null : (Date) createdDate.clone();
	}

	public Restaurant getRest() {
		return rest;
	}

	public void setRest(Restaurant rest) {
		this.rest = rest;
	}
	
	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}


	public RestaurantHasNote() {
		// TODO Auto-generated constructor stub
		super();
	}


	public RestaurantHasNote(Restaurant restaurant, Note note) {
		this.rest=restaurant;
		this.note=note;
		this.setCreatedDate(note.getCreatedDate());
		// TODO Auto-generated constructor stub
	}

	
}
