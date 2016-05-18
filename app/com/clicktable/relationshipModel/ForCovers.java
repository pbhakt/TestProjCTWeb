package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.DayOfWeek;
import com.clicktable.model.NumberOfCovers;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.FOR_COVERS)
public class ForCovers  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7633851424445966903L;


	@Fetch
	@StartNode
	DayOfWeek day;

	@Fetch
	@EndNode
	NumberOfCovers covers;
	
	private Integer cover;

	


	public DayOfWeek getDay() {
	    return day;
	}

	public void setDay(DayOfWeek day) {
	    this.day = day;
	}
	
	
	

	public NumberOfCovers getCovers() {
	    return covers;
	}

	public void setCovers(NumberOfCovers covers) {
	    this.covers = covers;
	}

	

	public Integer getCover() {
	    return cover;
	}

	public void setCover(Integer cover) {
	    this.cover = cover;
	}

	/**
	 * 
	 */
	public ForCovers() {
		super();
	}

	/*public RestaurantHasTat(Restaurant rest, Tat tat) {
		// super();
		this.tat = tat;
		this.rest = rest;
		

	}*/

	/**
	 * @param relationship
	 *            the relationship to set
	 */



	

}
