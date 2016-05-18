package com.clicktable.model;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@TypeAlias(value="Section")
@JsonInclude(Include.NON_NULL)
public class Section extends Entity 
 {

	private static final long serialVersionUID = 2046175400754253852L;

	@Required(message=ErrorCodes.SECTION_NAME_REQUIRED)
	@MaxLength(message=ErrorCodes.SECTION_NAME_MAX_LENGTH,value=10)
	private String name;
	@MaxLength(message=ErrorCodes.SECTION_DESCRIPTION_MAX_LENGTH,value=100)
	private String description;

	
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;

	/**
	 * @return the restID
	 */
	public String getRestID() {
		return restaurantGuid;
	}

	/**
	 * @param restID the restID to set
	 */
	public void setRestID(String restID) {
		this.restaurantGuid = restID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null?null :name.trim();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null?null :description.trim();
	}

}
