package com.clicktable.dao.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.RoleDao;
import com.clicktable.model.Role;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class RoleDaoImpl extends GraphDBDao<Role> implements
		RoleDao {

	public RoleDaoImpl() {
		super();
		this.setType(Role.class);
	}

	@Override
	public List<Role> getRoles(Map<String, Object> qryParamMap) {
		StringBuffer str = new StringBuffer();
		List<Role> list = new ArrayList<Role>();
		str.append("MATCH (role:Role) Return role");
		Result<Role> result=executeWriteQuery(str.toString(),null).to(Role.class);
		result.forEach(list::add);
		return list;
		 
	}

	@Override
	public Boolean addRole(Role role) {
		System.out.println("dao role1");
		 Map<String, Object> params = new HashMap<String, Object>();
	        Map<String, Object> params1 = getGraphProperty(UtilityMethods.introspect(role));
	        params.putAll(params1);
	        params.put(Constants.PROP_MAP, params1);
		String query = "CREATE (role:Role:_Role {role_name:{"+getPropertyName(Constants.ROLE_NAME)+"},role_id:{"+getPropertyName(Constants.ROLE_ID)+"}}) \n";
		query = query + "RETURN role";
		
		Map<String, Object> result = executeWriteQuery(query, params).singleOrNull();
		if (result == null)
			return null;
		else
		return true;
	}
	
	
	
	


}
