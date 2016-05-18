package com.clicktable.service.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.clicktable.dao.intf.RoleDao;
import com.clicktable.model.Role;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.RoleService;
import com.clicktable.util.ResponseCodes;
import com.clicktable.validate.RoleValidator;
import com.clicktable.validate.ValidationError;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	RoleDao roleDao;

	@Autowired
	RoleValidator validateRoleObject;

	@Autowired
	AuthorizationService authorizationService;

        /**
	 * {@inheritDoc}
	 */
	/*
	@Override
	@Transactional
        public BaseResponse addRole(Role role, String token)
	{

		role.setGuid(UtilityMethods.generateCtId());
		role.setStatus(Constants.ACTIVE_STATUS);
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		role.setInfoOnCreate(userInfo);
		
		List<ValidationError> listOfErrorForRole = new ArrayList<>();
		listOfErrorForRole = validateRoleObject.validateRoleOnAdd(role);
		
		Map<String,Object> params = new HashMap<String, Object>();
		params.put(Constants.NAME, role.getName());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		List<Role> roleList = roleDao.findByFields(Role.class, params);
		BaseResponse response=null;
		
		if (listOfErrorForRole.isEmpty())
		{
		    if(roleList.size()>0)
			{
			    ValidationError error = new ValidationError(Constants.NAME,UtilityMethods.getErrorMsg(ErrorCodes.COUNTRY_NAME_ALREADY_EXISTS));
			   listOfErrorForRole.add(error);
			    response = new ErrorResponse(ResponseCodes.COUNTRY_ADDED_FAILURE,listOfErrorForRole);
				
			}
		    else
		    {
		    roleDao.create(role);
		    // System.out.println("Role created with id "+role.getId());
		    response = new PostResponse<Restaurant>(ResponseCodes.COUNTRY_ADDED_SUCCESFULLY,role.getGuid());
		    }
		
		} 
		else 
		{
		    response = new ErrorResponse(ResponseCodes.COUNTRY_ADDED_FAILURE,listOfErrorForRole);
		}

		return response;
	}*/

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly =true)
	public BaseResponse getRole(Map<String, Object> params)
	{
		BaseResponse getResponse; 
		Map<String, Object> qryParamMap = validateRoleObject.validateFinderParams(params, Role.class);
		List<Role> roleList = roleDao.getRoles(qryParamMap);
		getResponse= new GetResponse<Role>(ResponseCodes.ROLE_RECORD_FETCH_SUCCESFULLY, roleList);
		return getResponse;

	}

	@Override
	@Transactional
	public BaseResponse addRole(Role role, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = validateRoleObject.validateRoleOnAdd(role);
		if(listOfError.isEmpty()){
			roleDao.addRole(role);
			response = new PostResponse<Role>(ResponseCodes.ROLE_ADDED_SUCCESFULLY, role.getRoleId().toString());
			return response;
		}
		
			return new ErrorResponse(ResponseCodes.ROLE_ADDITION_FAILURE, listOfError);
	}
	
	@Override
	@Transactional
	public BaseResponse addRoles(List<Role> roles) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		for(Role role:roles)
			listOfError.addAll(validateRoleObject.validateRoleOnAdd(role));
		if(listOfError.isEmpty()){
			List<Role> rolesAdded = roleDao.createMultiple(roles);
			List<Long> ids = new ArrayList<Long>();
			rolesAdded.forEach((s)->ids.add(s.getRoleId()));
			response = new PostResponse<Role>(ResponseCodes.ROLE_ADDED_SUCCESFULLY, ids.toString());
			return response;
		}
		
		return new ErrorResponse(ResponseCodes.ROLE_ADDITION_FAILURE, listOfError);
	}


	

}
