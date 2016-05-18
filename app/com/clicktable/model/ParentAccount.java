package com.clicktable.model;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author p.singh
 *
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ParentAccount extends Entity 
{
    	/**
     * 
     */
    private static final long serialVersionUID = -3577581584859073740L;
    
    
	@GraphProperty(propertyName="account_id")
    	private String accountId;
	
	
	public ParentAccount()
	{
	    super();
	}
	
	public ParentAccount(Restaurant rest)
	{
	    this.setCreatedBy(rest.getCreatedBy());
	    this.setCreatedDate(rest.getCreatedDate());
	    this.setLanguageCode(rest.getLanguageCode());
	    this.setStatus(rest.getStatus());
	    this.setUpdatedBy(rest.getUpdatedBy());
	    this.setUpdatedDate(rest.getUpdatedDate());
	}


	public String getAccountId() {
	    return accountId;
	}


	public void setAccountId(String accountId) {
	    this.accountId = accountId;
	}
	
    	
    	
    	
    	
	
	
	

	

}
