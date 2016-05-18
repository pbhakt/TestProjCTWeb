package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.GuestProfile;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.HAS_GUEST)
public class HasGuest  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1914755334762844468L;

	@Fetch
	@StartNode
	Restaurant rest;
	@Fetch
	@EndNode
	@JsonBackReference(value="guest")
	GuestProfile guest;
	public HasGuest() 
	{
	    super();
	}
	
	public HasGuest(Restaurant rest,GuestProfile guest)
	{
	    super();
	    this.rest = rest;
	    this.guest = guest;
	}

	public Restaurant getRest() {
	    return rest;
	}

	public void setRest(Restaurant rest) {
	    this.rest = rest;
	}

	

	public GuestProfile getGuest() {
	    return guest;
	}

	public void setGuest(GuestProfile guest) {
	    this.guest = guest;
	}


	


}
