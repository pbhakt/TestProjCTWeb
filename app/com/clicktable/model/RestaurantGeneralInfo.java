package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.TAGLINE_MAX_LENGTH;
import play.data.validation.Constraints.MaxLength;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class RestaurantGeneralInfo extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3463675858689217632L;
	private String accountID;
	private String parentAccountID;
	private String legalName;
	private String displayName;	

	@MaxLength(message=TAGLINE_MAX_LENGTH,value=200)
	private String tagLine;
	
	//@Required(message=COST_FOR_2_REQUIRED)
	private Double costFor2;
	
	/*@Required(message=REST_TIMEZONE_REQUIRED)
	private String timezone;
	
	@Required(message=REST_PREFERRED_DATE_FORMAT_REQUIRED)
	private String preferredDateFormat;
	
	@Required(message=REST_PREFERRED_TIME_FORMAT_REQUIRED)
	private String preferredTimeFormat;
	
	@Required(message=REST_CURRENCY_REQUIRED)
	private String currency;
	
	@Required(message=REST_TEMPERATURE_SCALE_REQUIRED)
	private String tempratureScale;
	*/
	private String currency;
	
	
	
	
	

	
	/**
	 * @return the accountID
	 */
	public String getAccountID() {
		return accountID;
	}
	
	
	/**
	 * @param accountID the accountID to set
	 */
	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}

	/**
	 * @return the parentAccountID
	 */
	public String getParentAccountID() {
		return parentAccountID;
	}

	/**
	 * @param parentAccountID the parentAccountID to set
	 */
	public void setParentAccountID(String parentAccountID) {
		this.parentAccountID = parentAccountID;
	}

	/**
	 * @return the legalName
	 */
	public String getLegalName() {
		return legalName;
	}

	/**
	 * @param legalName the legalName to set
	 */
	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	/**
	 * @return the tagLine
	 */
	public String getTagLine() {
		return tagLine;
	}

	/**
	 * @param tagLine the tagLine to set
	 */
	public void setTagLine(String tagLine) {
		this.tagLine = tagLine;
	}

	/**
	 * @return the costFor2
	 */
	public Double getCostFor2() {
		return costFor2;
	}

	/**
	 * @param costFor2 the costFor2 to set
	 */
	public void setCostFor2(Double costFor2) {
		this.costFor2 = costFor2;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
}
