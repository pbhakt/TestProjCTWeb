package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.BUILDING_GUID_REQUIRED;
import static com.clicktable.util.ErrorCodes.BUILDING_LOCALITY_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.BUILDING_LOCALITY_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.BUILDING_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.BUILDING_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.BUILDING_REGION_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.BUILDING_REGION_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.BUILDING_STATUS_REQUIRED;

import java.io.Serializable;

import org.neo4j.graphdb.Node;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Building implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4011764561511732343L;

	public Building() {}
	
	public Building(Node node) {
		this.guid = (String) node.getProperty("guid");
		this.name = (String) node.getProperty("name");
		this.status = (String) node.getProperty("status");
		this.localityName = (String) node.getProperty("locality_name");
		this.regionName = (String) node.getProperty("region_name");
	}
	
	public Building(Building building) {
		this.guid = building.getGuid();
		this.name = building.getName();
		this.status = building.getStatus();
		this.localityName = building.getLocalityName();
		this.regionName = building.getRegionName();
	}
	
	
	@GraphId
	@JsonIgnore
	private Long id;

	@Required(message=BUILDING_GUID_REQUIRED)
	@Indexed(unique = true)
	private String guid;

	@Required(message=BUILDING_STATUS_REQUIRED)
	private String status;
	
	@Required(message=BUILDING_NAME_REQUIRED)
	@MaxLength(message=BUILDING_NAME_MAX_LENGTH,value=50)
	private String name;
	
	@Required(message=BUILDING_LOCALITY_NAME_REQUIRED)
	@MaxLength(message=BUILDING_LOCALITY_NAME_MAX_LENGTH,value=30)
	@GraphProperty(propertyName = "locality_name")
	private String localityName;
	
	@Required(message=BUILDING_REGION_NAME_REQUIRED)
	@MaxLength(message=BUILDING_REGION_NAME_MAX_LENGTH,value=30)
	@GraphProperty(propertyName = "region_name")
	private String regionName;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocalityName() {
		return localityName;
	}

	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	

}
