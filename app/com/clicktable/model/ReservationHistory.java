package com.clicktable.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@TypeAlias("ReservationHistory")
@JsonInclude(Include.NON_NULL)
public class ReservationHistory implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7983909435473429899L;

	@GraphId
	@JsonIgnore
	private Long id;

	@Required(message=ErrorCodes.RESERVATION_HISTORY_GUID)
	@Indexed(unique = true)
	private String guid = UtilityMethods.generateCtId();

	public String getGuid() {
		return guid;
	}


	@Required(message=ErrorCodes.RESERVATION_HISTORY_CREATED_DATE)
	@GraphProperty(propertyName = "created_dt")
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date createdDate = new Timestamp(new Date().getTime());

	

	@Required(message=ErrorCodes.RESERVATION_HISTORY_CREATED_BY)
	@GraphProperty(propertyName = "created_by")
	private String createdBy;

	

	@Required(message=ErrorCodes.RESERVATION_STATUS)
	@GraphProperty(propertyName = "resv_status")
	private String reservationStatus;
	@Required(message=ErrorCodes.RESERVATION_HISTORY_BOOKED_BY)
	@GraphProperty(propertyName = "booked_by")
	private String bookedBy;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}

	

	public ReservationHistory() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate == null ? null : (Date) createdDate.clone();
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate == null ? null : (Date) createdDate.clone();
	}

	
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	
	/**
	 * @return the bookedBy
	 */
	public String getBookedBy() {
		return bookedBy;
	}

	/**
	 * @param bookedBy
	 *            the bookedBy to set
	 */
	public void setBookedBy(String bookedBy) {
		this.bookedBy = bookedBy;
	}

	/**
	 * @return the reservationStatus
	 */
	public String getReservationStatus() {
		return reservationStatus;
	}

	/**
	 * @param reservationStatus
	 *            the reservationStatus to set
	 */
	public void setReservationStatus(String reservationStatus) {
		this.reservationStatus = reservationStatus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReservationHistory other = (ReservationHistory) obj;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
