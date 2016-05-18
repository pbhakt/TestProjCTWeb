package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.TAG_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.TAG_NAME_REQUIRED;

import java.io.Serializable;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author p.vishwakarma
 *
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Tag extends Entity implements Serializable {

	private static final long serialVersionUID = 7302443121713211191L;
	
	@Required(message = TAG_NAME_REQUIRED)
	@MaxLength(message = TAG_NAME_MAX_LENGTH, value = 60)
	private String name;


	private String type;
	
	@GraphProperty(propertyName = "added_by")
	private String addedBy;
	
	private String is_merged;

	public Tag() {
		// TODO Auto-generated constructor stub
	}


	public Tag(String name2, String addedBy2, String type2) {
		// TODO Auto-generated constructor stub
		this.name = name2;
		this.addedBy = addedBy2;
		this.type = type2;

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the is_merged
	 */
	public String getIs_merged() {
		return is_merged;
	}

	/**
	 * @param is_merged the is_merged to set
	 */
	public void setIs_merged(String is_merged) {
		this.is_merged = is_merged;
	}


}
