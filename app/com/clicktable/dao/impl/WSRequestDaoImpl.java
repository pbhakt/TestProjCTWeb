package com.clicktable.dao.impl;


import java.util.Map;

import org.springframework.stereotype.Component;

import com.clicktable.dao.intf.WSRequestDao;
import com.clicktable.model.WSRequest;
import com.clicktable.util.Constants;

/**
 * @author s.gupta
 *
 */
@Component
public class WSRequestDaoImpl extends GraphDBDao<WSRequest> implements WSRequestDao {
	
	public WSRequestDaoImpl(){
		super();
		this.setType(WSRequest.class);
	}
	
	@Override
	protected StringBuilder getWhereClause(Map<String, Object> params) {
		
		StringBuilder query = super.getWhereClause(params);		
		if(params.containsKey(Constants.RETRY_COUNT_LESS))
	   	{
	   	    addPrefix(query);
	   	    query.append(" t."+getPropertyName(Constants.RETRY_COUNT)+"< {"+Constants.RETRY_COUNT_LESS+ "}");
	   	}

		return query;
	}

}
