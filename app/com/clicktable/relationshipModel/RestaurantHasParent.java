package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.ParentAccount;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.REST_HAS_PARENT)
public class RestaurantHasParent  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6718190776629326461L;
	@Fetch
	@StartNode
	Restaurant rest;
	@Fetch
	@EndNode
	@JsonBackReference(value="parent")
	ParentAccount account;
	
	
	 
	 
	
	public RestaurantHasParent() 
	{
	    super();
	}
	
	public RestaurantHasParent(Restaurant rest,ParentAccount account)
	{
	    super();
	    this.rest = rest;
	    this.account = account;
	}


	public Restaurant getRest() {
	    return rest;
	}

	public void setRest(Restaurant rest) {
	    this.rest = rest;
	}

	public ParentAccount getAccount() {
	    return account;
	}

	public void setAccount(ParentAccount account) {
	    this.account = account;
	}
	


}
