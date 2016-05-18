package com.clicktable.model;

import java.io.Serializable;

import org.neo4j.graphdb.Node;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class City implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 569618963091022145L;


	public City(Node node) {
		this.guid = (String) node.getProperty("guid");
		this.status = (String) node.getProperty("status");
		this.name = (String) node.getProperty("name");
		//this.zipcode = (String) node.getProperty("zipcode");
		this.stateCode = (String) node.getProperty("state_code");	
	}

	
	
	public City() {}



	public City(City existing) {
		this.guid = existing.getGuid();
		this.status = existing.getStatus();
		this.name = existing.getName();
		this.stateCode = existing.getStateCode();
	}



	@GraphId
	@JsonIgnore
	private Long id;

	@Required
	@Indexed(unique = true)
	private String guid;

	@Required
	private String status;
	
	@Required(message=ErrorCodes.CITY_NAME)
	@MaxLength(message=ErrorCodes.CITY_NAME_MAXLENGTH,value=50)
	private String name;
	
/*	@Required(message=ErrorCodes.CITY_ZIPCODE)
	@MaxLength(message=ErrorCodes.CITY_ZIPCODE_MAXLENGTH,value=10)
	private String zipcode;*/

	@Required(message=ErrorCodes.CITY_STATECODE)
	@MaxLength(message=ErrorCodes.CITY_STATECODE_MAXLENGTH,value=10)
	@GraphProperty(propertyName = "state_code")
	private String stateCode;
	
	
	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

/*	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
