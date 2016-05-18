package com.clicktable.relationshipModel;

import java.util.Date;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.RelationshipEntity;

import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;

@RelationshipEntity(type = "TBL_HAS_RESV")
public class TableReservationRelation  extends RelEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1818870480081041910L;

	
	@Required
	@GraphProperty(propertyName = "reservation_status")
	private String reservation_status;
	
	@Required
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "start_time")
	private Date start_time;
	
	//@Required
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "end_time")
	private Date end_time;
	
	@Required
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "startDate")
	private Date startDate;
	
	//@Required
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "endDate")
	private Date endDate;
	
	@Required
	@GraphProperty(propertyName = "table_guid")
	private String table_guid;
	
	@Required
	@GraphProperty(propertyName = "resv_guid")
	private String resv_guid;
	
	public TableReservationRelation() {
		super();
	}


	/**
	 * @return the reservation_status
	 */
	public String getReservation_status() {
		return reservation_status;
	}

	/**
	 * @param reservation_status the reservation_status to set
	 */
	public void setReservation_status(String reservation_status) {
		this.reservation_status = reservation_status;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate == null ? null : (Date) startDate.clone();
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate == null ? null : (Date) startDate.clone();
	}

	/**
	 * @return the table_guid
	 */
	public String getTable_guid() {
		return table_guid;
	}

	/**
	 * @param table_guid the table_guid to set
	 */
	public void setTable_guid(String table_guid) {
		this.table_guid = table_guid;
	}


	/**
	 * @return the start_time
	 */
	public Date getStart_time() {
		return start_time == null ? null : (Date) start_time.clone();
	}

	/**
	 * @param start_time the start_time to set
	 */
	public void setStart_time(Date start_time) {
		this.start_time = start_time == null ? null : (Date) start_time.clone();
	}

	/**
	 * @return the end_time
	 */
	public Date getEnd_time() {
		return end_time == null ? null : (Date) end_time.clone();
	}

	/**
	 * @param end_time the end_time to set
	 */
	public void setEnd_time(Date end_time) {
		this.end_time = end_time== null ? null : (Date) end_time.clone();
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate== null ? null : (Date) endDate.clone();
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate== null ? null : (Date) endDate.clone();
	}

	/**
	 * @return the resv_guid
	 */
	public String getResv_guid() {
		return resv_guid;
	}

	/**
	 * @param resv_guid the resv_guid to set
	 */
	public void setResv_guid(String resv_guid) {
		this.resv_guid = resv_guid;
	}

	
	
}
