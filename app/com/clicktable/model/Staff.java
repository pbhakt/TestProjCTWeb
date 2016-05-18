package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.STAFF_EMAIL_INVALID_FORMAT;
import static com.clicktable.util.ErrorCodes.STAFF_EMAIL_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.STAFF_EMAIL_REQUIRED;
import static com.clicktable.util.ErrorCodes.STAFF_FIRST_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.STAFF_FIRST_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.STAFF_LAST_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.STAFF_LAST_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.STAFF_MOBILE_MIN_LENGTH;
import static com.clicktable.util.ErrorCodes.STAFF_NICK_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.STAFF_ROLE_ID_REQUIRED;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Staff extends Entity {
	/**
     * 
     */
    private static final long serialVersionUID = 3145434616113357869L;
	//@Required
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;
	
	@Required(message=STAFF_FIRST_NAME_REQUIRED)
	@MaxLength(message=STAFF_FIRST_NAME_MAX_LENGTH,value=50)
	@GraphProperty(propertyName = "first_name")
	private String firstName;
	@Required(message=STAFF_LAST_NAME_REQUIRED)
	@MaxLength(message=STAFF_LAST_NAME_MAX_LENGTH,value=50)
	@GraphProperty(propertyName = "last_name")
	private String lastName;
	@MaxLength(message=STAFF_NICK_NAME_MAX_LENGTH,value=20)
	private String nickname;
	@Required(message=STAFF_EMAIL_REQUIRED)
	@Email(message=STAFF_EMAIL_INVALID_FORMAT)
	@MaxLength(message=STAFF_EMAIL_MAX_LENGTH,value=100)
	private String email;
	//@Required(message=STAFF_MOBILE_REQUIRED)
	@MinLength(message=STAFF_MOBILE_MIN_LENGTH,value=10)
	@MaxLength(message=STAFF_MOBILE_MIN_LENGTH,value=10)
	@GraphProperty(propertyName = "mobile")
	private String mobileNo;

	@Required(message=STAFF_ROLE_ID_REQUIRED)
	private Long roleId;
	
	@GraphProperty(propertyName = "last_login")
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone=Constants.TIMEZONE)
	private Date lastLogin ;
	
	@GraphProperty(propertyName = "otp_require")
	private Boolean is_otp_require;

	
	@JsonIgnore
	private String href;
	private String otpToken;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "otp_generated_time")
	private Date otp_generated_time= new Timestamp(new Date().getTime());
    private String firebase_token;
    @JsonIgnore
    private String access_token;
    @JsonIgnore
    private String token_type;
    @JsonIgnore
    private String refresh_token;
   

	

	public Staff(Onboarding onboard) {
		firstName = onboard.getFirstName();
		lastName = onboard.getLastName();
		email = onboard.getEmail();
		mobileNo = onboard.getMobileNo();
		this.setLanguageCode(onboard.getLanguageCode());
		this.setCreatedBy(onboard.getCreatedBy());
		this.setUpdatedBy(onboard.getUpdatedBy());
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

	public Staff() {
		// TODO Auto-generated constructor stub
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRestaurantGuid() {
		return restaurantGuid;
	}

	public void setRestaurantGuid(String restaurantGuid) {
		this.restaurantGuid = restaurantGuid == null?null:restaurantGuid.trim();

	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName == null? null :firstName.trim();
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName == null? null :lastName.trim();
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname== null? null : nickname.trim();

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email =email == null? null : email.trim();
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo == null? null :mobileNo.trim();
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href == null? null :href.trim();

	}

	public Date getLastLogin() {
	    return lastLogin == null ? null : (Date) lastLogin.clone();
	}

	public void setLastLogin(Date lastLogin) {
	    this.lastLogin = lastLogin == null ? null : (Date) lastLogin.clone();
	}

	/**
	 * @return the otpToken
	 */
	public String getOtpToken() {
		return otpToken;
	}

	/**
	 * @param otpToken the otpToken to set
	 */
	public void setOtpToken(String otpToken) {
		this.otpToken = otpToken;
	}

	/**
	 * @return the otp_generated_time
	 */
	public Date getOtp_generated_time() {
		return otp_generated_time == null ? null : (Date) otp_generated_time.clone();
	}

	/**
	 * @param otp_generated_time the otp_generated_time to set
	 */
	public void setOtp_generated_time(Date otp_generated_time) {
		this.otp_generated_time = otp_generated_time == null ? null : (Date) otp_generated_time.clone();
	}

	/**
	 * @return the firebase_token
	 */
	public String getFirebase_token() {
		return firebase_token;
	}

	/**
	 * @param firebase_token the firebase_token to set
	 */
	public void setFirebase_token(String firebase_token) {
		this.firebase_token = firebase_token;
	}

	/**
	 * @return the access_token
	 */
	public String getAccess_token() {
		return access_token;
	}

	/**
	 * @param access_token the access_token to set
	 */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	/**
	 * @return the token_type
	 */
	public String getToken_type() {
		return token_type;
	}

	/**
	 * @param token_type the token_type to set
	 */
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}





	/**
	 * @return the refresh_token
	 */
	public String getRefresh_token() {
		return refresh_token;
	}





	/**
	 * @param refresh_token the refresh_token to set
	 */
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	
	
	
	

}
