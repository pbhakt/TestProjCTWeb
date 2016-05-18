package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.STATE_CODE_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.STATE_CODE_REQUIRED;
import static com.clicktable.util.ErrorCodes.STATE_COUNTRY_CODE_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.STATE_COUNTRY_CODE_REQUIRED;
import static com.clicktable.util.ErrorCodes.STATE_GUID_REQUIRED;
import static com.clicktable.util.ErrorCodes.STATE_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.STATE_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.STATE_STATUS_REQUIRED;

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
public class State implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -327703505293250534L;

	public State(Node node) {
		this.guid=(String) node.getProperty("guid");
		this.name=(String) node.getProperty("name");
		this.status=(String) node.getProperty("status");
		this.countryCode=(String) node.getProperty("country_code");
		this.stateCode=(String) node.getProperty("state_code");
	}
	
	public State(State state) {
		this.guid=state.getGuid();
		this.name=state.getName();
		this.status=state.getStatus();
		this.countryCode=state.getCountryCode();
		this.stateCode=state.getStateCode();
	}
	
	public State(){}

	@GraphId
	@JsonIgnore
	private Long id;

	@Required(message=STATE_GUID_REQUIRED)
	@Indexed(unique = true)
	private String guid;
	
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

	@Required(message=STATE_NAME_REQUIRED)
	@MaxLength(message=STATE_NAME_MAX_LENGTH,value=50)
	private String name;
	
	@Required(message=STATE_STATUS_REQUIRED)
	private String status;
	
	@Required(message=STATE_COUNTRY_CODE_REQUIRED)
	@MaxLength(message=STATE_COUNTRY_CODE_MAX_LENGTH,value=10)
	@GraphProperty(propertyName = "country_code")
	private String countryCode;

	@Required(message=STATE_CODE_REQUIRED)
	@MaxLength(message=STATE_CODE_MAX_LENGTH,value=10)
	@GraphProperty(propertyName = "state_code")
	private String stateCode;
	
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	

}
