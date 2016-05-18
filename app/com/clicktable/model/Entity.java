package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.GUID_REQUIRED;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

@NodeEntity
public abstract class Entity implements Serializable {

	private static final long serialVersionUID = -7131710209817376823L;

	@GraphId
	@JsonIgnore
	private Long id;

	@Required(message=GUID_REQUIRED)
	@Indexed(unique = true)
	private String guid;

	public String getGuid() {
		return guid;
	}

	// @JsonIgnore
	public void setGuid(String guid) {
		this.guid = guid;
	}

	@GraphProperty(propertyName = "created_dt")
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone=Constants.TIMEZONE)
	private Date createdDate = new Timestamp(new Date().getTime());

	@GraphProperty(propertyName = "updated_dt")
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT ,timezone=Constants.TIMEZONE)
	@JsonIgnore
	private Date updatedDate = new Timestamp(new Date().getTime());

	@GraphProperty(propertyName = "created_by")
	@JsonIgnore
	private String createdBy;

	@GraphProperty(propertyName = "updated_by")
	@JsonIgnore
	private String updatedBy;


	@Required(message=ErrorCodes.LANG_CD)
	@GraphProperty(propertyName = "lang_cd")
	@JsonIgnore
	private String languageCode = UtilityMethods.getEnumValues(Constants.COMMON_MODULE, Constants.LANG_CD).get(0);

	//@Required
	private String status = Constants.ACTIVE_STATUS;

	private static Set<String> finderParams = new HashSet<String>();

	static {
		//finderParams.add(Constants.CREATED_AFTER);
		//finderParams.add(Constants.CREATED_BEFORE);
		//finderParams.add(Constants.CREATED_ON);

		//finderParams.add(Constants.UPDATED_AFTER);
		//finderParams.add(Constants.UPDATED_BEFORE);
		//finderParams.add(Constants.UPDATED_ON);
	}

	public static Set<String> getFinderParams() {
		return finderParams;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
		this.createdDate = createdDate== null ? null : (Date) createdDate.clone();
	}

	public Date getUpdatedDate() {
		return updatedDate== null ? null : (Date) updatedDate.clone();
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate== null ? null : (Date) updatedDate.clone();
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public void setInfoOnCreate(UserInfoModel userInfo) {
		this.setGuid(UtilityMethods.generateCtId());
		if(userInfo!=null){
			this.setCreatedBy(userInfo.getGuid());
			this.setUpdatedBy(userInfo.getGuid());
		}
	}


	public void setInfoOnUpdate(UserInfoModel userInfo) {
		if(userInfo!=null)
			this.setUpdatedBy(userInfo.getGuid());
		this.setUpdatedDate( new Timestamp(new Date().getTime()));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
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
