package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.TAT_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.TAT_NAME_REQUIRED;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
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
public class Tat extends Entity 
{
  
	/**
     * 
     */
    private static final long serialVersionUID = -2087280040172858335L;
    
	@Required(message=TAT_NAME_REQUIRED)
    	@MaxLength(message=TAT_NAME_MAX_LENGTH,value=100)
        private String name;
	@JsonIgnore
	@GraphProperty(propertyName="min_covers")
	private Integer minCovers;
	@JsonIgnore
	@GraphProperty(propertyName="max_covers")
	private Integer maxCovers;
	@JsonIgnore
	private String day;
	
	

	public Tat()
	{
	  super();
	}
	
	public void setValuesFromName(){
		
		if(this.minCovers==null && this.maxCovers==null && this.day==null){
			
	    if(this.name.equals(Constants.TAT_WD_12))
	    {
		
		this.minCovers = 1;
		this.maxCovers = 2;
		this.day = Constants.WEEKDAY;
	    }
	    
	    if(this.name.equals(Constants.TAT_WE_12))
	    {
		this.minCovers = 1;
		this.maxCovers = 2;
		this.day = Constants.WEEKEND;
	    }
	    
	    if(this.name.equals(Constants.TAT_WD_34))
	    {
		this.minCovers = 3;
		this.maxCovers = 4;
		this.day = Constants.WEEKDAY;
	    }
	    
	    if(this.name.equals(Constants.TAT_WE_34))
	    {
		this.minCovers = 3;
		this.maxCovers = 4;
		this.day = Constants.WEEKEND;
	    }
	    
	    if(this.name.equals(Constants.TAT_WD_56))
	    {
		this.minCovers = 5;
		this.maxCovers = 6;
		this.day = Constants.WEEKDAY;
	    }
	    
	    if(this.name.equals(Constants.TAT_WE_56))
	    {
		this.minCovers = 5;
		this.maxCovers = 6;
		this.day = Constants.WEEKEND;
	    }
	    
	    if(this.name.equals(Constants.TAT_WD_78))
	    {
		this.minCovers = 7;
		this.maxCovers = 8;
		this.day = Constants.WEEKDAY;
	    }
	    
	    if(this.name.equals(Constants.TAT_WE_78))
	    {
		this.minCovers = 7;
		this.maxCovers = 8;
		this.day = Constants.WEEKEND;
	    }
	    
	    if(this.name.equals(Constants.TAT_WD_8P))
	    {
		this.minCovers = 9;
		this.maxCovers = 10;
		this.day = Constants.WEEKDAY;
	    }
	    
	    if(this.name.equals(Constants.TAT_WE_8P))
	    {
		this.minCovers = 9;
		this.maxCovers = 10;
		this.day = Constants.WEEKEND;
	    }
		}
	}
	
	public Tat(String tatName)
	{
		this.name = tatName == null? null : tatName.trim();
	   	
		if(tatName != null){
	    if(tatName.equals(Constants.TAT_WD_12))
	    {
		
		this.minCovers = 1;
		this.maxCovers = 2;
		this.day = Constants.WEEKDAY;
	    }
	    
	    if(tatName.equals(Constants.TAT_WE_12))
	    {
		this.minCovers = 1;
		this.maxCovers = 2;
		this.day = Constants.WEEKEND;
	    }
	    
	    if(tatName.equals(Constants.TAT_WD_34))
	    {
		this.minCovers = 3;
		this.maxCovers = 4;
		this.day = Constants.WEEKDAY;
	    }
	    
	    if(tatName.equals(Constants.TAT_WE_34))
	    {
		this.minCovers = 3;
		this.maxCovers = 4;
		this.day = Constants.WEEKEND;
	    }
	    
	    if(tatName.equals(Constants.TAT_WD_56))
	    {
		this.minCovers = 5;
		this.maxCovers = 6;
		this.day = Constants.WEEKDAY;
	    }
	    
	    if(tatName.equals(Constants.TAT_WE_56))
	    {
		this.minCovers = 5;
		this.maxCovers = 6;
		this.day = Constants.WEEKEND;
	    }
	    
	    if(tatName.equals(Constants.TAT_WD_78))
	    {
		this.minCovers = 7;
		this.maxCovers = 8;
		this.day = Constants.WEEKDAY;
	    }
	    
	    if(tatName.equals(Constants.TAT_WE_78))
	    {
		this.minCovers = 7;
		this.maxCovers = 8;
		this.day = Constants.WEEKEND;
	    }
	    
	    if(tatName.equals(Constants.TAT_WD_8P))
	    {
		this.minCovers = 9;
		this.maxCovers = 10;
		this.day = Constants.WEEKDAY;
	    }
	    
	    if(tatName.equals(Constants.TAT_WE_8P))
	    {
		this.minCovers = 9;
		this.maxCovers = 10;
		this.day = Constants.WEEKEND;
	    }
		}
	    
	    
	}
	
	
	public String getName() {
	    return name;
	}

	public void setName(String name) {
		this.name = name == null ? null :name.trim();
	}

	public Integer getMinCovers() {
	    return minCovers;
	}

	public void setMinCovers(Integer minCovers) {
	    this.minCovers = minCovers;
	}

	public Integer getMaxCovers() {
	    return maxCovers;
	}

	public void setMaxCovers(Integer maxCovers) {
	    this.maxCovers = maxCovers;
	}

	public String getDay() {
	    return day;
	}

	public void setDay(String day) {
		this.day = day == null?null :day.trim();
	}
    	
    	
    
	
	

}
