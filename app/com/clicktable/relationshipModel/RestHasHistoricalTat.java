package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.HistoricalTat;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

@RelationshipEntity(type = RelationshipTypes.HISTORICAL_TAT)
public class RestHasHistoricalTat  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9104835658071906436L;

	@Fetch
	@StartNode
	@JsonBackReference(value="rest")
	Restaurant rest;

	@Fetch
	@EndNode
	HistoricalTat histTat;

	

	public Restaurant getRest() {
		return rest;
	}

	public void setRest(Restaurant rest) {
		this.rest = rest;
	}

	



	

	public HistoricalTat getHistTat() {
	    return histTat;
	}

	public void setHistTat(HistoricalTat histTat) {
	    this.histTat = histTat;
	}

	/**
	 * 
	 */
	public RestHasHistoricalTat() {
		super();
	}



}
