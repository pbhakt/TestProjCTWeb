package com.clicktable.service.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Role;
import com.clicktable.response.BaseResponse;


/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface RoleService {
	
	/**
	 * Add new Role
	 * @param role	Role to be added
	 * @return
	 */
	//BaseResponse addRole(Role role,String token);
	
	/**
	 * Get roles based on parameters
	 * @param params	search parameters
	 * @return
	 */
	BaseResponse getRole(Map<String,Object> params);

	BaseResponse addRole(Role role, String token);

	BaseResponse addRoles(List<Role> roles);
	
	/**
	 * Update Role data
	 * @param role
	 * @return
	 */
	//BaseResponse updateRole(Role role, String token);

	
}
