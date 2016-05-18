package com.clicktable.model;

import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@TypeAlias("StaffInfo")
@JsonInclude(Include.NON_NULL)
public class StaffInfo 
{
	@GraphId
	@JsonIgnore
	private Long id;
	
	@GraphProperty(propertyName = "token")
	private String token;
	
	
	@Indexed(unique = true)
	@GraphProperty(propertyName = "guid")
	private String guid;
	
	@GraphProperty(propertyName = "staff_guid")
	private String staffGuid;
	
	@GraphProperty(propertyName = "current_login_time")
	private Long currentLoginTime;
	
	@GraphProperty(propertyName = "login_history")
	private List<String> loginHistory;
	
	@GraphProperty(propertyName = "logout_history")
	private List<String> logoutHistory;
	
	@GraphProperty(propertyName = "otp_require")
	private Boolean is_otp_require;
	
	@GraphProperty(propertyName = "otp_generated_time")
	private Long otp_generated_time;
	
	@GraphProperty(propertyName = "otp_token")
	private String otpToken;
	

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}



	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}
	

	/**
	 * @return the is_otp_require
	 */
	public Boolean isIs_otp_require() {
		return is_otp_require;
	}

	/**
	 * @param is_otp_require the is_otp_require to set
	 */
	public void setIs_otp_require(Boolean is_otp_require) {
		this.is_otp_require = is_otp_require;
	}

	public Long getCurrentLoginTime() {
		return currentLoginTime;
	}

	public void setCurrentLoginTime(Long currentLoginTime) {
		this.currentLoginTime = currentLoginTime;
	}

	public List<String> getLoginHistory() {
		return loginHistory;
	}

	public void setLoginHistory(List<String> loginHistory) {
		this.loginHistory = loginHistory;
	}

	public List<String> getLogoutHistory() {
		return logoutHistory;
	}

	public void setLogoutHistory(List<String> logoutHistory) {
		this.logoutHistory = logoutHistory;
	}

	public Long getOtp_generated_time() {
		return otp_generated_time;
	}

	public void setOtp_generated_time(Long otp_generated_time) {
		this.otp_generated_time = otp_generated_time;
	}

	public String getOtpToken() {
		return otpToken;
	}

	public void setOtpToken(String otpToken) {
		this.otpToken = otpToken;
	}

	public String getStaffGuid() {
		return staffGuid;
	}

	public void setStaffGuid(String staffGuid) {
		this.staffGuid = staffGuid;
	}
	
	
		
}
