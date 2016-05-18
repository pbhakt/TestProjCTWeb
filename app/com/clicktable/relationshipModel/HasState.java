package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Country;
import com.clicktable.model.State;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.HAS_STATE)
public class HasState  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7488416211691332642L;
	@Fetch
	@StartNode
	private Country country;
	@Fetch
	@EndNode
	private State state;

	public HasState() {
		super();
	}

	public HasState(Country country, State state) {
		super();
		this.country = country;
		this.state = state;
	}


	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	
	
	
}
