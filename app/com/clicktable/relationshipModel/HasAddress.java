package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Restaurant;
import com.clicktable.model.RestaurantAddress;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.HAS_ADDRESS)
public class HasAddress  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6302493893032906615L;
	@Fetch
	@StartNode
	Restaurant rest;
	@Fetch
	@EndNode
	@JsonBackReference(value="address")
	RestaurantAddress address;
	
	
	public HasAddress() 
	{
	    super();
	}
	
	public HasAddress(Restaurant rest,RestaurantAddress address)
	{
	    super();
	    this.rest = rest;
	    this.address = address;
	}



	public Restaurant getRest() {
	    return rest;
	}

	public void setRest(Restaurant rest) {
	    this.rest = rest;
	}

	


	public RestaurantAddress getAddress() {
	    return address;
	}

	public void setAddress(RestaurantAddress address) {
	    this.address = address;
	}

	


}
