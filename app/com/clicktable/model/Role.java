package com.clicktable.model;

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

/**
 * 
 * @author p.singh
 *
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Role/* extends Entity */
{

	@GraphId
	@JsonIgnore
	private Long id;
	
	@Required(message = ErrorCodes.ROLE_NAME_REQUIRED)
	@MaxLength(message = ErrorCodes.ROLE_NAME_MAX_LENGTH, value = 100)
	@GraphProperty(propertyName = "role_name")	
	@Indexed(unique = true)	
	private String roleName;

	@GraphProperty(propertyName = "role_id")
	private Long roleId;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
