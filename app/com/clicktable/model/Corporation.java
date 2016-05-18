package com.clicktable.model;

import org.neo4j.graphdb.Node;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Required;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Corporation {

	public Corporation(Node node) {
		this.guid = (String) node.getProperty("guid");
		this.status = (String) node.getProperty("status");
		this.name = (String) node.getProperty("name");
		this.website = (String) node.getProperty("website");
	}

	public Corporation() {
	}

	public Corporation(Corporation existing) {
		this.guid = existing.getGuid();
		this.status = existing.getStatus();
		this.name = existing.getName();
		this.website = existing.getWebsite();
	}

	@GraphId
	@JsonIgnore
	private Long id;

	@Required
	@Indexed(unique = true)
	private String guid;

	@Required
	private String status;

	@GraphProperty(propertyName = "name")
	private String name;

	@GraphProperty(propertyName = "website")
	private String website;

	
	/*
	 * getters and setters
	 */
	
	
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
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

}
