package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Reservation;
import com.clicktable.model.ReservationHistory;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.RESV_HISTORY)
public class ReservationHasHistory  extends RelEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2713920542313623681L;
	
	
	@Fetch
	@StartNode
	private Reservation reservation;
	
	@Fetch
	@EndNode
	private ReservationHistory history;

	public ReservationHasHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the reservation
	 */
	public Reservation getReservation() {
		return reservation;
	}

	/**
	 * @param reservation the reservation to set
	 */
	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	/**
	 * @return the history
	 */
	public ReservationHistory getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(ReservationHistory history) {
		this.history = history;
	}
	
	

}
