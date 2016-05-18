package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Role;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface RoleDao extends GenericDao<Role> {

	List<Role> getRoles(Map<String, Object> qryParamMap);

	Boolean addRole(Role role);

}
