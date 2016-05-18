package com.clicktable.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Required;

import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Template extends Entity {

	private static final long serialVersionUID = 4457962269699502291L;

	@Required(message=ErrorCodes.REST_GUID_REQUIRED)
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;

	//@Required(message=ErrorCodes.TEMPLATES_REQUIRED)
	List<String> templates=new ArrayList<String>();

	public String getRestaurantGuid() {
		return restaurantGuid;
	}

	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid;
	}

	public List<String> getTemplates() {
		return templates;
	}

	public void setTemplates(List<String> templates) {
		this.templates = templates;
	}

}
