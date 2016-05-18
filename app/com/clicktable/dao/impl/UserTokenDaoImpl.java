package com.clicktable.dao.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.UserTokenDao;
import com.clicktable.model.UserToken;
import com.clicktable.util.Constants;


@Service
public class UserTokenDaoImpl extends GraphDBDao<UserToken> implements UserTokenDao
{

	public UserTokenDaoImpl() {
		super();
		this.setType(UserToken.class);
	}
	@Override
	public List<UserToken> findByFields(Map<String, Object> params) {
		// TODO Auto-generated method stub
		List<UserToken> userInfoToken=new ArrayList<UserToken>();
		StringBuffer query=new StringBuffer();
		query.append("MATCH (n:`UserToken`) where n.token={"+Constants.TOKEN+"} ");
		if(params.containsKey("user_id")){
			query.append(" OR n.user_id={user_id}");	
		}
		query.append(" RETURN n");
		Result<UserToken> result = template.query(query.toString(), params).to(UserToken.class);
		result.forEach(userInfoToken::add);
		return userInfoToken;
		
	}

	@Override
	public void deleteToken(UserToken userToken) {
		// TODO Auto-generated method stub
		Map<String, Object> params=new HashMap<String,Object>();
		params.put(Constants.TOKEN,userToken.getToken());
		StringBuffer query=new StringBuffer();
		query.append("MATCH (n:`UserToken`) where n.token={"+Constants.TOKEN+"} DELETE n");
		template.query(query.toString(), params).to(UserToken.class);
	}

	@Override
	public String addUserToken(UserToken user_token) {
		StringBuffer query1=new StringBuffer();
		Map<String, Object> params = new HashMap<>();
		params.put(Constants.TOKEN, user_token.getToken());
		params.put(Constants.GUID, user_token.getGuid());
		params.put(Constants.USER, user_token.getUserId());
		query1.append("CREATE (n:`UserToken`:`_UserToken`{token:{"+Constants.TOKEN+"},guid:{"+Constants.GUID+"},user_id:{"+
				Constants.USER+ "}}) RETURN n");
		Map<String, Object> result = template.query(query1.toString(), params)
				.singleOrNull();
		if (result == null)
			return null;
		else
			return (String) ((Node) result.get("n"))
					.getProperty(Constants.GUID);
	}

}
