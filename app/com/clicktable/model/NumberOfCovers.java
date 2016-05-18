package com.clicktable.model;

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
public class NumberOfCovers extends Entity 
{
  
	/**
     * 
     */
    private static final long serialVersionUID = -2087280040172858335L;
    
	@JsonIgnore
	private Integer covers;
	

	public NumberOfCovers()
	{
	  super();
	}
	
	public NumberOfCovers(Integer covers)
	{
	  this.setGuid(UtilityMethods.generateCtId());
	  this.covers = covers;
	}


	public Integer getCovers() {
	    return covers;
	}


	public void setCovers(Integer covers) {
	    this.covers = covers;
	}


	


	


	
	
	

}
