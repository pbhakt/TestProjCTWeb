package com.clicktable.model;

import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author s.gupta
 *
 */
@NodeEntity
@JsonInclude(Include.NON_NULL)
public class CurrentValues extends Entity {
	

	private static final long serialVersionUID = 7808872265947382941L;
	
	private long reservationId;

	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(long reservationId) {
		this.reservationId = reservationId;
	}
	
}
