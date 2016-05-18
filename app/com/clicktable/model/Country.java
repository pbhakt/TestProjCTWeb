package com.clicktable.model;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author g.singh
 *
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@TypeAlias("Country")
@JsonInclude(Include.NON_NULL)
public class Country extends Entity {

	/**
     * 
     */
	
	
public Country(Country existing) {
		this.name = existing.getName();
		this.countryCode = existing.getCountryCode();
	}
	
	public Country() {
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = -7597966125422569941L;
	@Required(message=ErrorCodes.COUNTRY_NAME)
	@MaxLength(message=ErrorCodes.COUNTRY_NAME_MAXLENGTH,value=100)
	private String name;
	@Required(message=ErrorCodes.COUNTRY_CODE)
	@MaxLength(message=ErrorCodes.COUNTRY_CODE__MAXLENGTH,value=5)
	@GraphProperty(propertyName = "country_cd")
	private String countryCode;

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name!=null?name.trim():null;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode!=null?countryCode.trim():null;
	}

}
