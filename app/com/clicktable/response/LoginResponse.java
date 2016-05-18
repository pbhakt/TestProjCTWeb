package com.clicktable.response;

import com.clicktable.model.Restaurant;
import com.clicktable.model.UserInfoModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class LoginResponse extends BaseResponse
{
    public UserInfoModel userInfo;
    public Restaurant rest;
    public boolean otpRequire;
    public String staff_guid;
    public SMSResponse smsResponse;
    @JsonIgnore
    public String refreshToken; 
    private String access_token;
	public String token;
	private String timeinmilli;
	private String timezone;
	private String dateformat;
	private String time;
    
    public LoginResponse()
    {
	super();
    }
    
    public LoginResponse(UserInfoModel userInfo)
    {
	this.userInfo = userInfo;
    }


    public UserInfoModel getUserInfo() {
        return userInfo;
    }


    public void setUserInfo(UserInfoModel userInfo) {
        this.userInfo = userInfo;
    }


    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    public Restaurant getRest() {
        return rest;
    }


    public void setRest(Restaurant rest) {
        this.rest = rest;
    }

	public String getTimeinmilli() {
		return timeinmilli;
	}

	public void setTimeinmilli(String timeinmilli) {
		this.timeinmilli = timeinmilli;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getDateformat() {
		return dateformat;
	}

	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return the staff_guid
	 */
	public String getStaff_guid() {
		return staff_guid;
	}

	/**
	 * @param staff_guid the staff_guid to set
	 */
	public void setStaff_guid(String staff_guid) {
		this.staff_guid = staff_guid;
	}

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
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
		 * @return the smsResponse
		 */
		public SMSResponse getSmsResponse() {
			return smsResponse;
		}

		/**
		 * @param smsResponse the smsResponse to set
		 */
		public void setSmsResponse(SMSResponse smsResponse) {
			this.smsResponse = smsResponse;
		}

		/**
		 * @return the otpRequire
		 */
		public boolean isOtpRequire() {
			return otpRequire;
		}

		/**
		 * @param otpRequire the otpRequire to set
		 */
		public void setOtpRequire(boolean otpRequire) {
			this.otpRequire = otpRequire;
		}
    

    
	
	
}
