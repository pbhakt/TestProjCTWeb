package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.TABLE_MAX_COVERS_MAX_VALUE;
import static com.clicktable.util.ErrorCodes.TABLE_MAX_COVERS_MIN_VALUE;
import static com.clicktable.util.ErrorCodes.TABLE_MAX_COVERS_REQUIRED;
import static com.clicktable.util.ErrorCodes.TABLE_MIN_COVERS_MAX_VALUE;
import static com.clicktable.util.ErrorCodes.TABLE_MIN_COVERS_MIN_VALUE;
import static com.clicktable.util.ErrorCodes.TABLE_MIN_COVERS_REQUIRED;
import static com.clicktable.util.ErrorCodes.TABLE_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.TABLE_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.TABLE_REST_GUID_REQUIRED;
import static com.clicktable.util.ErrorCodes.TABLE_TYPE_REQUIRED;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@TypeAlias("Table")
@JsonInclude(Include.NON_NULL)
public class Table extends Entity {
	private static final long serialVersionUID = 5812939479303605849L;

	//private static final String SECTION_REQUIRED = null;

	@Required(message=TABLE_NAME_REQUIRED)
	@MaxLength(message=TABLE_NAME_MAX_LENGTH,value = 100)
	private String name;
	@Required(message=TABLE_REST_GUID_REQUIRED)
	@GraphProperty(propertyName = "rest_id")
	private String restId;
	@Required(message=TABLE_MAX_COVERS_REQUIRED)
	@Min(message=TABLE_MAX_COVERS_MIN_VALUE,value = 1)
	@Max(message=TABLE_MAX_COVERS_MAX_VALUE,value = 500)
	@GraphProperty(propertyName = "max_covers")
	private Integer maxCovers;
	@Required(message=TABLE_MIN_COVERS_REQUIRED)
	@Min(message=TABLE_MIN_COVERS_MIN_VALUE,value = 1)
	@Max(message=TABLE_MIN_COVERS_MAX_VALUE,value = 500)
	@GraphProperty(propertyName = "min_covers")
	private Integer minCovers;
	private String tableStatus;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)	
	private Date startTime;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date endTime;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date seated_time;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	private Date currentServerTime;

	private String reservationGuid;
	private String blockGuid;

	@Required(message=TABLE_TYPE_REQUIRED)
	private String type;
	@Required(message=ErrorCodes.SECTION_ID_REQUIRED)
	@GraphProperty(propertyName = "section")
	private String sectionGuid;
	
	@Transient
	private String sectionName;
	
	
	@GraphProperty(propertyName = "tat")
	private String tat;
	
	
	private String reasonForVip;
	
	public Table()
	{
	    super();
	}
	
	public Table(Table table)
	{
	   this.setGuid(table.getGuid());
	   this.tableStatus= table.getTableStatus();
		   
		   
	}

	/**
	 * @return the current_ServerTime
	 */
	public Date getCurrentServerTime() {
		return currentServerTime == null ? null : (Date) currentServerTime.clone();
	}

	/**
	 * @param current_ServerTime the current_ServerTime to set
	 */
	public void setCurrentServerTime(Date currentServerTime) {
		this.currentServerTime = currentServerTime == null ? null : (Date) currentServerTime.clone();
	}

	/**
	 * @return the reservation_StartTime
	 */
	public Date getReservation_StartTime() {
		return startTime == null ? null : (Date) startTime
				.clone();
	}

	/**
	 * @param reservation_StartTime
	 *            the reservation_StartTime to set
	 */
	public void setReservation_StartTime(Date reservation_StartTime) {
		this.startTime = reservation_StartTime == null ? null : (Date) reservation_StartTime
				.clone();
	}

	/**
	 * @return the reservation_EndTime
	 */
	public Date getReservation_EndTime() {
		return endTime == null ? null : (Date) endTime.clone();
	}

	/**
	 * @param reservation_EndTime
	 *            the reservation_EndTime to set
	 */
	public void setReservation_EndTime(Date reservation_EndTime) {
		this.endTime = reservation_EndTime == null ? null : (Date) reservation_EndTime.clone();
	}


	private static Set<String> minMaxParams = new HashSet<String>();

	static {
		minMaxParams.add(Constants.MIN_COVERS);
		minMaxParams.add(Constants.MIN_COVERS_GREATER);
		minMaxParams.add(Constants.MIN_COVERS_GREATER_EQUAL);
		minMaxParams.add(Constants.MIN_COVERS_LESS);
		minMaxParams.add(Constants.MIN_COVERS_LESS_EQUAL);

		minMaxParams.add(Constants.MAX_COVERS);
		minMaxParams.add(Constants.MAX_COVERS_GREATER);
		minMaxParams.add(Constants.MAX_COVERS_GREATER_EQUAL);
		minMaxParams.add(Constants.MAX_COVERS_LESS);
		minMaxParams.add(Constants.MAX_COVERS_LESS_EQUAL);

	}

	public static Set<String> getMinMaxParams() {
		return minMaxParams;
	}

	

	/*@RelatedToVia(type = RelationshipTypes.REST_HAS_TBL, direction = Direction.INCOMING)
	RestaurantHasTable hasTableRelationship;*/

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getRestId() {
		return restId;
	}

	public void setRestId(String restId) {
		this.restId = restId;
	}

	public Integer getMaxCovers() {
		return maxCovers;
	}

	public void setMaxCovers(Integer maxCovers) {
		this.maxCovers = maxCovers;
	}

	public Integer getMinCovers() {
		return minCovers;
	}

	public void setMinCovers(Integer minCovers) {
		this.minCovers = minCovers;
	}

	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the tableStatus
	 */
	public String getTableStatus() {
		return tableStatus;
	}

	/**
	 * @param tableStatus
	 *            the tableStatus to set
	 */
	public void setTableStatus(String tableStatus) {
		this.tableStatus = tableStatus;
	}

	/**
	 * @return the seated_time
	 */
	public Date getSeated_time() {
		return seated_time == null ? null : (Date) seated_time.clone();
	}

	/**
	 * @param seated_time
	 *            the seated_time to set
	 */
	public void setSeated_time(Date seated_time) {
		this.seated_time = seated_time == null ? null : (Date) seated_time.clone();
	}

	public String getSectionId() {
		return sectionGuid;
	}

	public void setSectionId(String sectionId) {
		this.sectionGuid = sectionId;
	}

	/**
	 * @return the reservationGuid
	 */
	public String getReservationGuid() {
		return reservationGuid;
	}

	/**
	 * @param reservationGuid the reservationGuid to set
	 */
	public void setReservationGuid(String reservationGuid) {
		this.reservationGuid = reservationGuid;
	}

	/**
	 * @return the blockGuid
	 */
	public String getBlockGuid() {
		return blockGuid;
	}

	/**
	 * @param blockGuid the blockGuid to set
	 */
	public void setBlockGuid(String blockGuid) {
		this.blockGuid = blockGuid;
	}
	
	public String getTat() {
		return tat;
	}
	
	public void setTat(String tat) {
		this.tat = tat;
	}


	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	
	

	public String getReasonForVip() {
		return reasonForVip;
	}

	public void setReasonForVip(String reasonForVip) {
		this.reasonForVip = reasonForVip;
	}

	
}
