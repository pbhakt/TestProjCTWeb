package com.clicktable.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@NodeEntity
@TypeAlias("AppDetails")
public class ApplicationDetails implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4672989592891731996L;
	@GraphId
	@JsonIgnore
	private Long id;
	
	
	@GraphProperty(propertyName = "app_name")
	private String appName;
	private String platform;
	@GraphProperty(propertyName = "build_version")
	private String buildVersion;
	@GraphProperty(propertyName = "force_update")
	private boolean forceUpdate;
	@GraphProperty(propertyName = "old_versions")
	private List<String> oldVersions;
	private String status;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getBuildVersion() {
		return buildVersion;
	}
	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}
	public boolean isForceUpdate() {
		return forceUpdate;
	}
	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}
	public List<String> getOldVersions() {
		return oldVersions;
	}
	public void setOldVersions(List<String> oldVersions) {
		this.oldVersions = oldVersions;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
	
	
}
