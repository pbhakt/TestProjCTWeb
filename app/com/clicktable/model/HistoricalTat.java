package com.clicktable.model;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author p.singh
 * @company Clicktable Technologies LLP
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class HistoricalTat extends Entity 
{
  
	/**
     * 
     */
    private static final long serialVersionUID = -2087280040172858335L;
    
	@JsonIgnore
	@GraphProperty(propertyName="rest_id")
	private String restaurantGuid;
	

	public HistoricalTat()
	{
	  super();
	}
	
	
	public HistoricalTat(String restId)
	{
	  this.setGuid(UtilityMethods.generateCtId());
	  this.restaurantGuid = restId;
	}


	public String getRestaurantGuid() {
	    return restaurantGuid;
	}


	public void setRestaurantGuid(String restaurantGuid) {
	    this.restaurantGuid = restaurantGuid;
	}
		
	
	

}
