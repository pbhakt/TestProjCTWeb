package com.clicktable.relationshipModel;

import java.util.Date;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import play.data.validation.Constraints.Required;

import com.clicktable.model.GuestProfile;
import com.clicktable.model.Reservation;
import com.fasterxml.jackson.annotation.JsonBackReference;

@RelationshipEntity(type = "GUEST_HAS_RESV")
public class GuestReservationRelation  extends RelEntity{



	/**
	 * 
	 */
	private static final long serialVersionUID = -5687070750449379935L;

	
	
	@Fetch
	@StartNode
	@JsonBackReference(value = "guest")
	private GuestProfile guestProfile;
	
	@Fetch
	@EndNode
	@JsonBackReference(value = "guest_to_resv")
	private Reservation reservation;
	
	@Required
	@GraphProperty(propertyName = "rest_guid")
	private String restGuid;
	
	@Required
	@GraphProperty(propertyName = "reservation_status")
	private String reservationStatus;
	
	@Required
	@GraphProperty(propertyName = "start_time")
	private Date estStartTime;
	
	@Required
	@GraphProperty(propertyName = "end_time")
	private Date estEndTime;

	public GuestReservationRelation() {
		super();
	}
	
	public GuestReservationRelation(Reservation reservation, GuestProfile guest) {
		this.setReservation(reservation);
		this.setEstStartTime(reservation.getEstStartTime());
		this.setEstEndTime(reservation.getEstEndTime());
		this.setRestGuid(reservation.getRestaurantGuid());
		this.setReservationStatus(reservation.getReservationStatus());
		this.setGuestProfile(guest);
	}

	public String getRestGuid() {
		return restGuid;
	}

	public void setRestGuid(String restGuid) {
		this.restGuid = restGuid;
	}

	public String getReservationStatus() {
		return reservationStatus;
	}

	public void setReservationStatus(String reservationStatus) {
		this.reservationStatus = reservationStatus;
	}

	public Date getEstStartTime() {
		return estStartTime == null ? null : (Date) estStartTime.clone();
	}

	public void setEstStartTime(Date estStartTime) {
		this.estStartTime = estStartTime == null ? null : (Date) estStartTime.clone();
	}

	public Date getEstEndTime() {
		return estEndTime == null ? null : (Date) estEndTime.clone();
	}

	public void setEstEndTime(Date estEndTime) {
		this.estEndTime = estEndTime == null ? null : (Date) estEndTime.clone();
	}

	private String relationship;


	public GuestProfile getGuestProfile() {
		return guestProfile;
	}

	public void setGuestProfile(GuestProfile guestProfile) {
		this.guestProfile = guestProfile;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
}
