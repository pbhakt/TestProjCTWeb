package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Device;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.REST_HAS_DEVICE)
public class RestaurantHasDevice  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4367794325884142715L;

	@Fetch
	@StartNode
	Restaurant rest;
	@Fetch
	@EndNode
	@JsonBackReference(value="device")
	Device device;
	
	
	 
	 
	
	public RestaurantHasDevice() 
	{
	    super();
	}
	
	public RestaurantHasDevice(Restaurant rest,Device device)
	{
	    super();
	    this.rest = rest;
	    this.device = device;
	}


	public Restaurant getRest() {
	    return rest;
	}

	public void setRest(Restaurant rest) {
	    this.rest = rest;
	}

	
	
	
	

	public Device getDevice() {
	    return device;
	}

	public void setDevice(Device device) {
	    this.device = device;
	}
	


}
