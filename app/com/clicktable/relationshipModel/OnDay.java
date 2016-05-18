package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.DayOfWeek;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.ON_DAY)
public class OnDay  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3150064623951468428L;

	@Fetch
	@StartNode
	RestHasHistoricalTat histTat;

	@Fetch
	@EndNode
	DayOfWeek day;

	

	

	public RestHasHistoricalTat getHistTat() {
	    return histTat;
	}

	public void setHistTat(RestHasHistoricalTat histTat) {
	    this.histTat = histTat;
	}

	public DayOfWeek getDay() {
	    return day;
	}

	public void setDay(DayOfWeek day) {
	    this.day = day;
	}

	/**
	 * 
	 */
	public OnDay() {
		super();
	}

	/*public RestaurantHasTat(Restaurant rest, Tat tat) {
		// super();
		this.tat = tat;
		this.rest = rest;
		

	}*/

	

}
