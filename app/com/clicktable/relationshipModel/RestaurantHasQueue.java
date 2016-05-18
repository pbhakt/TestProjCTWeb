package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Queue;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.REST_HAS_QUEUE)
public class RestaurantHasQueue  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7911954858709928047L;
	@Fetch
	@StartNode
	Restaurant rest;
	@Fetch
	@EndNode
	@JsonBackReference(value="queue")
	Queue queue;
	
	private Integer covers;
	
	
	
	 
	 
	
	public RestaurantHasQueue() 
	{
	    super();
	}
	
	public RestaurantHasQueue(Restaurant rest,Queue queue)
	{
	    super();
	    this.rest = rest;
	    this.queue = queue;
	}


	public Restaurant getRest() {
	    return rest;
	}

	public void setRest(Restaurant rest) {
	    this.rest = rest;
	}

	
	public Queue getQueue() {
	    return queue;
	}

	public void setQueue(Queue queue) {
	    this.queue = queue;
	}

	public Integer getCovers() {
	    return covers;
	}

	public void setCovers(Integer covers) {
	    this.covers = covers;
	}

	


}
