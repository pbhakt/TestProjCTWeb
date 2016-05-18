package com.clicktable.service.intf;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.clicktable.model.Role;
import com.clicktable.model.UserInfoModel;


@Service
@Configurable
public interface AuthorizationService {


	
	public boolean addNewSession(String token, UserInfoModel userInfo);
	
	public boolean removeSession(String token);
	
	public Map<String, UserInfoModel> getLoggedInUsersMap();
	

	/**
	 * Get Role of user from access token
	 * @param token	access token
	 * @return		Role of user
	 */
	public Long getRoleByToken(String token);
	
	/**
	 * Check If loggedin user has access permission to call Web service API
	 * @param roleId	Role of logged user
	 * @param roles	Roles permitted to call API
	 * @return 
	 */
	public  boolean hasAccess(Long roleId, List<Role> roles);

	public String getLoggedInUser(String header);
	
	
	public UserInfoModel getUserInfoByToken(String token);

	boolean isRecentToken(String token);

	public String loginAsInternal();

	public String getTokenForStaff(String guid);



}
