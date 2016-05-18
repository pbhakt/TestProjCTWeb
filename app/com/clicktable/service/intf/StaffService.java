package com.clicktable.service.intf;

import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.clicktable.model.Oauth;
import com.clicktable.model.Staff;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface StaffService {
	/**
	 * Add staff in Stormpath
	 * 
	 * @param staff
	 * @return
	 */
	public String addStaffToStormPath(Staff staff);

	/**
	 * Add new Staff
	 * 
	 * @param staff
	 * @return
	 */
	public BaseResponse addStaffMember(Staff staff, String token);

	/**
	 * Update staff data
	 * 
	 * @param staff
	 * @return
	 */
	public BaseResponse updateStaffMember(Staff staff, String token);

	/**
	 * Get Staff based on search parameters
	 * 
	 * @param params
	 * @return
	 */
	public BaseResponse getStaffMembers(Map<String, Object> params);

	/**
	 * Change Staff Password
	 * 
	 * @param userName
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public BaseResponse changePassword(String userName, String oldPassword,
			String newPassword);

	/**
	 * Request for forgot password
	 * 
	 * @param email
	 * @return
	 */
	public BaseResponse forgotPassword(String email);

	/**
	 * Request for reset password
	 * 
	 * @param email
	 * @return
	 */
	public BaseResponse resetPassword(String sptoken, String password);

	/**
	 * Verification with token and guid
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */

	BaseResponse staffVerification(String guid, String token, boolean isFlag);


	/**
	 * Authenticate with username and password
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public BaseResponse staffLogin(String userName, String password);

	public BaseResponse staffResendOTP(String guid);

	public BaseResponse logOut(String token);

	BaseResponse deleteStaffMember(String staffGuid, String token);

	public BaseResponse addStaffMember(Staff staff);

	public Oauth getOauthTokens(MultivaluedMap<String, String> formData);

	public BaseResponse updateStatusStaffMember(Staff staff, String token);

	
	BaseResponse logOutAllUsers(String token, Map<String, Object> params);

	public BaseResponse setStaffInfo(String token,
			Map<String, Object> stringParamMap);

	BaseResponse staffLoginWithCookies(String userName);

	

}
