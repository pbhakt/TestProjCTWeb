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
public class Locality implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8880207265612967181L;


	public Locality(Node node) {
		this.guid = (String) node.getProperty("guid");
		this.name = (String) node.getProperty("name");
		this.status = (String) node.getProperty("status");
		this.regionName = (String) node.getProperty("region_name");
		this.cityName = (String) node.getProperty("city_name");
	}
	
	
	
	public Locality() {
	}

	public Locality(Locality locality) {
		this.guid = locality.getGuid();
		this.name = locality.getName();
		this.status = locality.getStatus();
		this.regionName = locality.getRegionName();
		this.cityName = locality.getCityName();
	}

	
	@GraphId
	@JsonIgnore
	private Long id;

	@Required(message=ErrorCodes.LOCALITY_GUID)
	@Indexed(unique = true)
	private String guid;

	@Required(message=ErrorCodes.LOCALITY_STATUS)
	private String status;
	
	
	@Required(message=ErrorCodes.LOCALITY_NAME)
	@MaxLength(message=ErrorCodes.LOCALITY_NAME_MAX_LENGTH,value=50)
	private String name;
	
	@Required(message=ErrorCodes.LOCALITY_REGION_NAME)
	@MaxLength(message=ErrorCodes.LOCALITY_REGION_NAME_MAX_LENGTH,value=50)
	@GraphProperty(propertyName = "region_name")
	private String regionName;

	@Required(message=ErrorCodes.LOCALITY_CITY_NAME)
	@MaxLength(message=ErrorCodes.LOCALITY_CITY_NAME_MAX_LENGTH,value=50)
	@GraphProperty(propertyName = "city_name")
	private String cityName;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

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



	public String getCityName() {
		return cityName;
	}



	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

}
