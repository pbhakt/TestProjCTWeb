package com.clicktable.service.intf;

import com.clicktable.model.UserToken;
import com.clicktable.response.BaseResponse;


@org.springframework.stereotype.Service
public interface UserTokenService
{
	
	
	/**
	 * Add new user token
	 * @param userToken
	 * @return
	 */
	public void addUserToken(UserToken userToken);
	
	public void deleteToken(String token);
	
	public boolean tokenExists(String token);
	
	public UserToken findToken(String token);

	BaseResponse add_UserToken(UserToken user_token);
	

}
