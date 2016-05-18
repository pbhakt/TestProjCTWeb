package com.clicktable.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.CorporationDao;
import com.clicktable.model.Corporation;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;

@Service
public class CorporationDaoImpl extends GraphDBDao<Corporation> implements CorporationDao {

	public CorporationDaoImpl() {
		super();
		this.setType(Corporation.class);
	}

	public List<Corporation> findCorporations(Map<String, Object> params) {

		StringBuilder query = new StringBuilder("MATCH (t:`" + "Corporation" + "`)");

			query.append(" WHERE 1=1 ");
		//like query for name and website??

			
		if (params.containsKey(Constants.NAME)) {
			query.append(" AND t.name = {" + Constants.NAME + "} ");
		}
		if (params.containsKey(Constants.GUID)) {
			query.append(" AND t.guid = {" + Constants.GUID + "} ");
		}
		if (params.containsKey(Constants.STATUS)) {
			query.append(" AND t.status = {" + Constants.STATUS + "} ");
		}
		if (params.containsKey(Constants.WEBSITE)) {
			query.append(" AND t.website = {" + Constants.WEBSITE + "} ");
		}
		query.append(" RETURN t ");
		
		return executeQuery(query.toString(), params);

	}

	@Override
	public String addCorporation(Corporation corporation) {
   		Map<String, Object> params = new HashMap<String, Object>();
   		//params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
   		params.put(Constants.PROP_MAP, getGraphProperty(UtilityMethods.introspect(corporation)));
   		
   		String query = "CREATE (t:Corporation {"
   				+Constants.PROP_MAP+"}) RETURN t";

   		return getSingleResultGuid(executeWriteQuery(query, params));
	}


	@Override
	public Corporation updateCorporation(Corporation toupdatecorp, Corporation existing) {
		//Map<String, Object> params = new HashMap<String, Object>();
		Corporation updated=super.update(toupdatecorp);
		return updated;
	}


}
