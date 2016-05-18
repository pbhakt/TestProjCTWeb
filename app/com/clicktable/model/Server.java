package com.clicktable.model;

import java.util.List;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.ErrorCodes;
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
public class Server extends Entity 
{
    
  
	/**
     * 
     */
    private static final long serialVersionUID = -3678323754228573354L;

	@Required(message=ErrorCodes.SERVER_NAME_REQUIRED)
    	@MaxLength(message=ErrorCodes.SERVER_NAME_MAX_LENGTH,value=100)
        private String name;
	
	@Required(message=ErrorCodes.SERVER_ID_REQUIRED)
    	@MaxLength(message=ErrorCodes.SERVER_ID_MAX_LENGTH,value=50)
	@GraphProperty(propertyName="server_id")
        private String serverId;
	
	@Required(message=ErrorCodes.COLOR_CODE_REQUIRED)
	@MaxLength(message=ErrorCodes.COLOR_CODE_MAX_LENGTH,value=100)
	@GraphProperty(propertyName="color_code")
	private String colorCode;
	
	
	@Required(message=ErrorCodes.SERVER_REST_ID_REQUIRED)
	@GraphProperty(propertyName="rest_id")
	private String restaurantGuid;
	
	
	private List<TableAssignment> assignedTables;
	
	

	public String getName() {
	    return name;
	}

	public void setName(String name) {
		this.name = name == null ? null :name.trim();
	}

	public String getColorCode() {
	    return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode == null ? null :colorCode.trim();
	   
	}

	public String getRestaurantGuid() {
	    return restaurantGuid;
	}

	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid == null ? null : restaurantGuid.trim();
	}

	public List<TableAssignment> getAssignedTables() {
	    return assignedTables;
	}

	public void setAssignedTables(List<TableAssignment> assignedTables) {
	    this.assignedTables = assignedTables;
	}

	public String getServerId() {
	    return serverId;
	}

	public void setServerId(String serverId) 
	{
		this.serverId = serverId == null ? null : serverId.trim();
	}

	
    	
    	
    
	
	

}
