package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Restaurant;
import com.clicktable.model.Server;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.REST_HAS_SERVER)
public class RestaurantHasServer  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4475584148610919157L;
	@Fetch
	@StartNode
	Restaurant rest;
	@Fetch
	@EndNode
	@JsonBackReference(value="server")
	Server server;
	
	
	 
	 
	
	public RestaurantHasServer() 
	{
	    super();
	}
	
	public RestaurantHasServer(Restaurant rest,Server server)
	{
	    super();
	    this.rest = rest;
	    this.server = server;
	}


	public Restaurant getRest() {
	    return rest;
	}

	public void setRest(Restaurant rest) {
	    this.rest = rest;
	}

	public Server getServer() {
	    return server;
	}

	public void setServer(Server server) {
	    this.server = server;
	}


}
