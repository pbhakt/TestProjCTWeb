package com.clicktable.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Note implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3788811125825860922L;

	@GraphId
	@JsonIgnore
	private Long id;

	@Indexed(unique = true)
	private String guid;
	
	@Required(message=ErrorCodes.NOTE_REST_ID_REQUIRED)
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;
	

	private String staff_FirstName;
	private String staff_LastName;
	
	@Required(message=ErrorCodes.NOTE_REQUIRED)
	@MaxLength(message=ErrorCodes.NOTE_MAX_LENGTH,value=3000)
	private String note;
	

	@GraphProperty(propertyName = "created_dt")
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone=Constants.TIMEZONE)
	private Date createdDate = new Timestamp(new Date().getTime());

	@GraphProperty(propertyName = "created_by")
	private String createdBy;

	@Required(message=ErrorCodes.NOTE_LANG_CD_REQUIRED)
	@GraphProperty(propertyName = "lang_cd")
	private String languageCode = UtilityMethods.getEnumValues(Constants.COMMON_MODULE, Constants.LANG_CD).get(0);

	private static Set<String> finderParams = new HashSet<String>();


	

	public String getGuid() {
		return guid;
	}

	// @JsonIgnore
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public String getRestaurantGuid() {
		return restaurantGuid;
	}

	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid;
	}



	/*static {
		finderParams.add(Constants.CREATED_AFTER);
		finderParams.add(Constants.CREATED_BEFORE);
		finderParams.add(Constants.CREATED_ON);
	}*/

	public static Set<String> getFinderParams() {
		return finderParams;
	}

	public void copyExistingValues(Entity existing) {
		this.setId(existing.getId());
		this.setCreatedBy(existing.getCreatedBy());
		this.setCreatedDate(existing.getCreatedDate());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public Date getCreatedDate() {
		return createdDate == null ? null : (Date) createdDate.clone();
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate == null ? null : (Date) createdDate.clone();
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setInfoOnCreate(UserInfoModel userInfo) {
		this.setGuid(UtilityMethods.generateCtId());
		this.setCreatedBy(userInfo.getGuid());
	}


	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note.trim();
	}

	

	/**
	 * @return the staff_FirstName
	 */
	public String getStaff_FirstName() {
		return staff_FirstName;
	}

	/**
	 * @param staff_FirstName
	 *            the staff_FirstName to set
	 */
	public void setStaff_FirstName(String staff_FirstName) {
		this.staff_FirstName = staff_FirstName;
	}

	/**
	 * @return the staff_LastName
	 */
	public String getStaff_LastName() {
		return staff_LastName;
	}

	/**
	 * @param staff_LastName
	 *            the staff_LastName to set
	 */
	public void setStaff_LastName(String staff_LastName) {
		this.staff_LastName = staff_LastName;
	}
	
}
