package com.clicktable.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author s.gupta
 *
 */
@NodeEntity
@JsonInclude(Include.NON_NULL)
public class WSRequest implements Serializable{
	
	private static final long serialVersionUID = 8860555291637868838L;
	
	@GraphId
	@JsonIgnore
	private Long id;
	
	private String  method;
	private String  header;
	private String  uri;
	@GraphProperty(propertyName = "input_json")
	private String  inputJson;
	@GraphProperty(propertyName = "response_status")
	private String  responseStatus;
	@GraphProperty(propertyName = "retry_count")
	private int  retryCount;
	@GraphProperty(propertyName = "created_dt")
	private Date  createdDate;
	@GraphProperty(propertyName = "updated_dt")
	private Date  updatedDate;
	private String error;
	private String  status;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getInputJson() {
		return inputJson;
	}
	public void setInputJson(String inputJson) {
		this.inputJson = inputJson;
	}
	public String getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public Date getCreatedDate() {
		return createdDate == null ? null : (Date) createdDate.clone();
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate== null ? null : (Date) createdDate.clone();
	}
	public Date getUpdatedDate() {
		return updatedDate== null ? null : (Date) updatedDate.clone();
	}
	public void setUpdatedDate(Date date) {
		this.updatedDate = date== null ? null : (Date) date.clone();
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
}
