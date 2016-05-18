package com.clicktable.model;



import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Cuisine extends Entity {
	private static final long serialVersionUID = -6971626144008794612L;

	@Required(message=ErrorCodes.CUISINE_NAME)
	@MaxLength(message=ErrorCodes.CUISINE_NAME_MAXLENGTH,value=100)
	private String name;

	public Cuisine() {
		super();
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getName() {
		return name;
	}

}
