package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.TableAssignment;
import com.clicktable.response.BaseResponse;


@org.springframework.stereotype.Service
public interface TableAssignmentService {
	
	/**
	 * 
	 * @param tableAssign
	 * @param token
	 * @return
	 */
	 
	BaseResponse assignTable(TableAssignment tableAssign,String token);
	
	/**
	 * 
	 * @param tableAssign
	 * @param token
	 * @return
	 */
	BaseResponse unassignTable(TableAssignment tableAssign, String token);
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	BaseResponse getTableAssignment(Map<String,Object> params); 
	
	
	
}
