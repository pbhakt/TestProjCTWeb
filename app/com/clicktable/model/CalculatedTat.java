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
public class CalculatedTat extends Entity 
{
  
	/**
     * 
     */
    private static final long serialVersionUID = -2087280040172858335L;
    
	@JsonIgnore
	private Integer value;
	

	public CalculatedTat()
	{
	  super();
	}
	
	public CalculatedTat(Integer value)
	{
	  this.setGuid(UtilityMethods.generateCtId());
	  this.value = value;
	}

	public Integer getValue() {
	    return value;
	}

	public void setValue(Integer value) {
	    this.value = value;
	}


	
	


	


	
	
	

}
