package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.CAUSE_ID_REQUIRED;
import static com.clicktable.util.ErrorCodes.EXTENSION_REQUIRED;
import static com.clicktable.util.ErrorCodes.HUNG_UP_REQUIRED;
import static com.clicktable.util.ErrorCodes.MSISDN_REQUIRED;

import org.springframework.data.neo4j.annotation.GraphProperty;

import play.data.validation.Constraints.Required;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Miscall extends Entity {

	/**
	 * g.singh
	 */
	private static final long serialVersionUID = -7390722420361911878L;
	@Required(message=MSISDN_REQUIRED)
	@GraphProperty(propertyName = "mobile")
	private String msisdn;
	@Required(message=EXTENSION_REQUIRED)
	private String extension;
	@Required(message=CAUSE_ID_REQUIRED)
	private String causeId;
	@Required(message=HUNG_UP_REQUIRED)
	private Boolean hasUserHungUp;
	
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getCauseId() {
		return causeId;
	}
	public void setCauseId(String causeId) {
		this.causeId = causeId;
	}
	public Boolean getHasUserHungUp() {
		return hasUserHungUp;
	}
	public void setHasUserHungUp(Boolean hasUserHungUp) {
		this.hasUserHungUp = hasUserHungUp;
	}

	

}
