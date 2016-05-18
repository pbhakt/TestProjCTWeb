package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.REGION_CITY_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.REGION_CITY_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.REGION_GUID_REQUIRED;
import static com.clicktable.util.ErrorCodes.REGION_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.REGION_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.REGION_STATE_CODE_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.REGION_STATE_CODE_REQUIRED;
import static com.clicktable.util.ErrorCodes.REGION_STATUS_REQUIRED;

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
public class Region implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5903660653633434902L;



	public Region(Node node){
		this.guid=(String) node.getProperty("guid");
		this.name=(String) node.getProperty("name");
		this.status=(String) node.getProperty("status");
		this.cityName=(String) node.getProperty("city_name");
		this.stateCode=(String) node.getProperty("state_code");
	}
	
	public Region(){
		
	}
	
	public Region(Region existing) {
		this.guid=existing.getGuid();
		this.name=existing.getName();
		this.status=existing.getStatus();
		this.cityName=existing.getCityName();
		this.stateCode=existing.getStateCode();
	}
	

	@GraphId
	@JsonIgnore
	private Long id;

	@Required(message=REGION_GUID_REQUIRED)
	@Indexed(unique = true)
	private String guid;

	@Required(message=REGION_STATUS_REQUIRED)
	private String status;
	
	
	@Required(message=REGION_NAME_REQUIRED)
	@MaxLength(message=REGION_NAME_MAX_LENGTH,value=50)
	private String name;
	
	@Required(message=REGION_CITY_NAME_REQUIRED)
	@MaxLength(message=REGION_CITY_NAME_MAX_LENGTH,value=50)
	@GraphProperty(propertyName = "city_name")
	private String cityName;

	@Required(message=REGION_STATE_CODE_REQUIRED)
	@MaxLength(message=REGION_STATE_CODE_MAX_LENGTH,value=50)
	@GraphProperty(propertyName = "state_code")
	private String stateCode;
	
	
	
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	
}
