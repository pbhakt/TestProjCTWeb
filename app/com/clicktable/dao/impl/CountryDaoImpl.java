package com.clicktable.dao.impl;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.CountryDao;
import com.clicktable.model.Country;
import com.clicktable.util.Constants;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class CountryDaoImpl extends GraphDBDao<Country> implements
		CountryDao {

	public CountryDaoImpl() {
		super();
		this.setType(Country.class);
	}

	@Override
	public boolean hasChildRelationships(Country existing) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, existing.getGuid());
		StringBuilder query = new StringBuilder("MATCH (c:`" + "Country" + "`)-[r]->(m) WHERE c.guid={"+Constants.GUID+"} AND m.status='ACTIVE' return r");
		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), params);
		Iterator<Map<String, Object>> itr = result.iterator();
		return itr.hasNext();
	}

	@Override
	public Country updateCountry(Country toupdatecountry, Country existing) {
		Map<String, Object> params = new HashMap<String, Object>();
		Country updated=super.update(toupdatecountry);
		if(!toupdatecountry.getCountryCode().equals(existing.getCountryCode())){
			params.put(Constants.COUNTRY_CODE, existing.getCountryCode());
			params.put(Constants.UPDATED_COUNTRY_CODE, toupdatecountry.getCountryCode());
		StringBuilder query = new StringBuilder("MATCH (n:`State`) WHERE n.country_code={"+Constants.COUNTRY_CODE+"} SET n.country_code={"+Constants.UPDATED_COUNTRY_CODE+"}");
		executeWriteQuery(query.toString(), params);
		}
		return updated;
	}

}
