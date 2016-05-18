package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Queue;
import com.clicktable.model.Reservation;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.QUEUE_HAS_RESV)
public class QueueHasReservation  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8920922958679926902L;
	@Fetch
	@StartNode
	Queue queue;
	@Fetch
	@EndNode
	@JsonBackReference(value="reservation")
	Reservation resv;
	
	 
	 
	
	public QueueHasReservation() 
	{
	    super();
	}
	
	public QueueHasReservation(Reservation resv,Queue queue)
	{
	    super();
	    this.resv = resv;
	    this.queue = queue;
	}

	public Reservation getResv() {
	    return resv;
	}

	public void setResv(Reservation resv) {
	    this.resv = resv;
	}

	public Queue getQueue() {
	    return queue;
	}

	public void setQueue(Queue queue) {
	    this.queue = queue;
	}


	


}
