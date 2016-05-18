package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.TAG_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.TAG_NAME_REQUIRED;

import java.io.Serializable;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author a.thakur
 *
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TagModelOld extends Entity implements Serializable {

	private static final long serialVersionUID = 7302443121713211191L;

	@Required(message = TAG_NAME_REQUIRED)
	@MaxLength(message = TAG_NAME_MAX_LENGTH, value = 60)
	private String name;

	@GraphProperty(propertyName = "added_by")
	private String addedBy;
    @JsonIgnore
	private boolean isExist = false;

	/**
	 * @return the isExit
	 */
	public boolean isExist() {
		return isExist;
	}

	/**
	 * @param isExit
	 *   the isExit to set
	 */
	public void setExist(boolean isExist) {
		this.isExist = isExist;
	}

	public TagModelOld() {
		// TODO Auto-generated constructor stub
	}

	public TagModelOld(String name2, String addedBy2) {
		// TODO Auto-generated constructor stub
		this.name = name2;
		this.addedBy = addedBy2;

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

}
