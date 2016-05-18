package com.clicktable.validate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.RoleDao;
import com.clicktable.model.Role;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;

@Service
public class RoleValidator extends EntityValidator<Role> {

	@Autowired
	RoleDao roleDao;
	
	public List<ValidationError> validateRoleOnAdd(Role role) {
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		String roleName = role.getRoleName();
		Long roleId = role.getRoleId();
		List<Role> roleList = roleDao.getRoles(null);
		for(Role singlerole:roleList){
			if(((singlerole.getRoleId().toString()).equals(roleId.toString()))){
				errorList.add(createError(Constants.ROLE_ID, ErrorCodes.ROLE_ID_EXISTS)); 
				break;
			}else if((singlerole.getRoleName().equals(roleName))){
				errorList.add(createError(Constants.ROLE_NAME, ErrorCodes.ROLE_NAME_EXISTS)); 
				break;
			}
		}
		
		return errorList;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.ROLE_ID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_ROLE_ID;
	}
}
