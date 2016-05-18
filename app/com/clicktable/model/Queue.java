package com.clicktable.model;

import org.springframework.data.neo4j.annotation.NodeEntity;

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
public class Queue extends Entity 
{
    /**
     * 
     */
    private static final long serialVersionUID = 5323682124535755652L;
    
	@JsonIgnore
	private Integer count = 0;

	public Integer getCount() {
	    return count;
	}

	public void setCount(Integer count) {
	    this.count = count;
	}
	
	

}
