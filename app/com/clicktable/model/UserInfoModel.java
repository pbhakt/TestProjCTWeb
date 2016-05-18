package com.clicktable.model;

import java.io.Serializable;
import java.util.Date;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(Include.NON_NULL)
public class UserInfoModel implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 6518912246651256177L;

	private Long roleId;
	private String roleName;
	private String userType;
	private String fid;
	private String gid;
	private boolean mobile_verified;	

	private String guid;
	private String restGuid;
	private String userFirstName;
	private String userLastName;
	private String email;
	
	@JsonFormat(pattern = Constants.TIME_FORMAT, timezone = Constants.TIMEZONE)
	private Date updateTime;
	private String mobile;

		
	private String token_type;

	

	public UserInfoModel() 
	{
		super();
	}

	public UserInfoModel(GuestProfile guestProfile)
	{
		this.guid = guestProfile.getGuid();
		this.email = guestProfile.getEmailId();
		this.roleId = Constants.CUSTOMER_ROLE_ID;
		this.userType = Constants.CUSTOMER_TYPE;
		this.fid=guestProfile.getFid();
		this.gid=guestProfile.getGid();
		this.mobile_verified=guestProfile.isIs_mobile_verified();
		this.mobile=guestProfile.getMobile();
		this.userFirstName = guestProfile.getFirstName();
		//this.userLastName = guestProfile.getLastName();
		this.updateTime = guestProfile.getUpdatedDate();
	}

	public UserInfoModel(Staff staff) {
		 this.guid = staff.getGuid();
		this.email = staff.getEmail();
		this.roleId = staff.getRoleId();
		this.userType = Constants.STAFF_TYPE;
		this.restGuid = staff.getRestaurantGuid();
		this.userFirstName = staff.getFirstName();
		this.userLastName = staff.getLastName();
		//this.updateTime = new Time((new Date()).getTime());
		//this.token_type=staff.getToken_type();

	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRestGuid() {
	    return restGuid;
	}

	public void setRestGuid(String restGuid) {
	    this.restGuid = restGuid;

	}

	
	
	/**
	 * @return the fid
	 */
	public String getFid() {
		return fid;
	}

	/**
	 * @param fid the fid to set
	 */
	public void setFid(String fid) {
		this.fid = fid;
	}

	/**
	 * @return the gid
	 */
	public String getGid() {
		return gid;
	}

	/**
	 * @param gid the gid to set
	 */
	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * @return the mobile_verified
	 */
	public boolean isMobile_verified() {
		return mobile_verified;
	}

	/**
	 * @param mobile_verified the mobile_verified to set
	 */
	public void setMobile_verified(boolean mobile_verified) {
		this.mobile_verified = mobile_verified;
	}

	/**
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	
}
