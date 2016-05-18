package com.clicktable.model;

public class Oauth implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3672403001565937651L;
	private String refresh_token;
	   private String access_token;
	   private String token_type;
	   private String expires_in;
	   private String stormpath_access_token_href;
	   private String status;
	   private String code;
	   private String message;
	   private String developerMessage;
	   private String moreInfo;
	   private String error;

   

	/**
	 * @param code the code to set
	 */

	/**
 * @return the expires_in
 */
public String getExpires_in() {
	return expires_in;
}
/**
 * @param expires_in the expires_in to set
 */
public void setExpires_in(String expires_in) {
	this.expires_in = expires_in;
}
/**
 * @return the stormpath_access_token_href
 */
public String getStormpath_access_token_href() {
	return stormpath_access_token_href;
}
/**
 * @param stormpath_access_token_href the stormpath_access_token_href to set
 */
public void setStormpath_access_token_href(String stormpath_access_token_href) {
	this.stormpath_access_token_href = stormpath_access_token_href;
}
	public Oauth() {
		super();
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
 * @return the status
 */
public String getStatus() {
	return status;
}
/**
 * @param status the status to set
 */
public void setStatus(String status) {
	this.status = status;
}
/**
 * @return the code
 */
public String getCode() {
	return code;
}
/**
 * @param code the code to set
 */
public void setCode(String code) {
	this.code = code;
}
/**
 * @return the message
 */
public String getMessage() {
	return message;
}
/**
 * @param message the message to set
 */
public void setMessage(String message) {
	this.message = message;
}
/**
 * @return the developerMessage
 */
public String getDeveloperMessage() {
	return developerMessage;
}
/**
 * @param developerMessage the developerMessage to set
 */
public void setDeveloperMessage(String developerMessage) {
	this.developerMessage = developerMessage;
}
/**
 * @return the moreInfo
 */
public String getMoreInfo() {
	return moreInfo;
}
/**
 * @param moreInfo the moreInfo to set
 */
public void setMoreInfo(String moreInfo) {
	this.moreInfo = moreInfo;
}
/**
 * @return the error
 */
public String getError() {
	return error;
}
/**
 * @param error the error to set
 */
public void setError(String error) {
	this.error = error;
}


}
