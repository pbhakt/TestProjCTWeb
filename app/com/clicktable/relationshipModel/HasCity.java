package com.clicktable.relationshipModel;


import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.City;
import com.clicktable.model.State;
import com.clicktable.util.RelationshipTypes;


@RelationshipEntity(type = RelationshipTypes.HAS_CITY)
public class HasCity extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6039561489139770017L;

	@Fetch
	@StartNode
	private State state;
	@Fetch
	@EndNode
	private City city;

	public HasCity() {
		super();
	}

	public HasCity(State state, City city) {
		super();
		this.state = state;
		this.city = city;
	}


	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
	

	
	
	
	
	
	
}
