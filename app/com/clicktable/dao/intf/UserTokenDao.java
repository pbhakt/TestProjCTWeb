package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.UserToken;

@Service
public interface UserTokenDao extends GenericDao<UserToken>
{

	public List<UserToken> findByFields(Map<String,Object> params);

	public void deleteToken(UserToken userToken);

	public String addUserToken(UserToken user_token);
}
