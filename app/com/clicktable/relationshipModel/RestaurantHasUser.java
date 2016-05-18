package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Restaurant;
import com.clicktable.model.Staff;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.REST_HAS_USER)
public class RestaurantHasUser  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -578729451240644992L;
	@Fetch
	@StartNode
	Restaurant rest;
	@Fetch
	@EndNode
	Staff staff;
	
	
	 Boolean isUser;
	 Boolean isServer;
	 Boolean isActive;
	 
	 
	
	public RestaurantHasUser() 
	{
	    super();
	}
	
	public RestaurantHasUser(Restaurant rest,Staff staff)
	{
	    super();
	    this.rest = rest;
	    this.staff = staff;
	    boolean isServer = false;
	    boolean isUser = false;
	    if(staff.getRoleId().equals(Constants.SERVER_ROLE_ID))
	    {
		isServer = true;
	    }
	    if(staff.getRoleId().equals(Constants.CUSTOMER_ROLE_ID))
	    {
		isUser = true;
	    }

	    this.isServer = isServer;
	    this.isUser = isUser;
    
	}


	public Restaurant getRest() {
	    return rest;
	}

	public void setRest(Restaurant rest) {
	    this.rest = rest;
	}

	public Staff getStaff() {
	    return staff;
	}

	public void setStaff(Staff staff) {
	    this.staff = staff;
	}

	public Boolean getIsUser() {
	    return isUser;
	}

	public void setIsUser(Boolean isUser) {
	    this.isUser = isUser;
	}

	public Boolean getIsServer() {
	    return isServer;
	}

	public void setIsServer(Boolean isServer) {
	    this.isServer = isServer;
	}

	public Boolean getIsActive() {
	    return isActive;
	}

	public void setIsActive(Boolean isActive) {
	    this.isActive = isActive;
	}
	


}
