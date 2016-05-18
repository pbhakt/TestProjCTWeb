package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Restaurant;
import com.clicktable.model.Tat;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

@RelationshipEntity(type = RelationshipTypes.REST_HAS_TAT)
public class RestaurantHasTat  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2515266193848201573L;


	@Fetch
	@StartNode
	@JsonBackReference(value="rest")
	Restaurant rest;

	@Fetch
	@EndNode
	Tat tat;

	private Integer value;
	private Integer family_tat;
	

	public Restaurant getRest() {
		return rest;
	}

	public void setRest(Restaurant rest) {
		this.rest = rest;
	}

	

	public Tat getTat() {
	    return tat;
	}

	public void setTat(Tat tat) {
	    this.tat = tat;
	}

	public Integer getValue() {
	    return value;
	}

	public void setValue(Integer value) {
	    this.value = value;
	}
	
	
	

	public Integer getFamily_tat() {
		return family_tat;
	}

	public void setFamily_tat(Integer family_tat) {
		this.family_tat = family_tat;
	}

	/**
	 * 
	 */
	public RestaurantHasTat() {
		super();
	}

	/*public RestaurantHasTat(Restaurant rest, Tat tat) {
		// super();
		this.tat = tat;
		this.rest = rest;
		

	}*/


}
