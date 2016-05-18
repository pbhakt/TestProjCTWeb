package com.clicktable.model;

import java.io.Serializable;




public class LoginResponseModel  implements Serializable 
{
	

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	private String ctId;
	private String token;
	private Long userId;
	public String getCtId() {
	    return ctId;
	}
	public void setCtId(String ctId) {
	    this.ctId = ctId;
	}
	public String getToken() {
	    return token;
	}
	public void setToken(String token) {
	    this.token = token;
	}
	public Long getUserId() {
	    return userId;
	}
	public void setUserId(Long userId) {
	    this.userId = userId;
	}
	
	
	
}
