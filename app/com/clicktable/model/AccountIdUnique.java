package com.clicktable.model;

import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonInclude(Include.NON_NULL)
public class AccountIdUnique extends Entity {
	

	private static final long serialVersionUID = 7808872265947382941L;
	
	private long accountId;

	/**
	 * @return the accountId
	 */
	public long getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	
}
