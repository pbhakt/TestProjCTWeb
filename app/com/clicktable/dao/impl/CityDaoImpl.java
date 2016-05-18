package com.clicktable.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.CityDao;
import com.clicktable.model.City;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;

@Service
public class CityDaoImpl extends GraphDBDao<City> implements CityDao {

	public CityDaoImpl() {
		super();
		this.setType(City.class);
	}

	public List<City> findCities(Map<String, Object> params) {

		StringBuilder query = new StringBuilder("MATCH (t:`" + "City" + "`)");

		if (containsKey(params, "state")) {
			query.append(" <-[r:HAS_CITY]-(s:State) WHERE 1=1");

			if (params.containsKey(Constants.STATE_CODE)) {
				query.append(" AND s.state_code = {" + Constants.STATE_CODE
						+ "} ");
			}
			if (params.containsKey(Constants.STATE_NAME)) {
				query.append(" AND s.name = {" + Constants.STATE_NAME + "} ");
			}

		} else {
			query.append(" WHERE 1=1 ");
		}

		if (params.containsKey(Constants.NAME)) {
			query.append(" AND t.name = {" + Constants.NAME + "} ");
		}
		if (params.containsKey(Constants.GUID)) {
			query.append(" AND t.guid = {" + Constants.GUID + "} ");
		}
		if (params.containsKey(Constants.STATUS)) {
			query.append(" AND t.status = {" + Constants.STATUS + "} ");
		}
		/*if (params.containsKey(Constants.ZIP_CODE)) {
			query.append(" AND t.zipcode = {" + Constants.ZIP_CODE + "} ");
		}*/
		//query.append(" RETURN t ");
		query.append(" RETURN t.name as name,t.guid as guid,t.status as status,t.state_code as stateCode ");
		return executeQuery(query.toString(), params);

	}

	@Override
	public String addCity(City city) {
   		Map<String, Object> params = new HashMap<String, Object>();
   		params.put(Constants.STATE_CODE, city.getStateCode());
   		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
   		params.put(Constants.PROP_MAP, getGraphProperty(UtilityMethods.introspect(city)));
   		
   		String query = "MATCH (s:State {state_code:{"+Constants.STATE_CODE+"},status:{"+Constants.STATUS+"}}) \n";
   		query = query + "MERGE (s)-[shc:HAS_CITY]->(t:" + Constants.CITY_LABEL/*+ ":_" +Constants.CITY_LABEL*/+" {id:{"
   				+Constants.PROP_MAP
   				+"}.id}) ON CREATE SET t={"+Constants.PROP_MAP+"}"+ "RETURN t";

   		return getSingleResultGuid(executeWriteQuery(query, params));
	}

	@Override
	public boolean hasChildRelationships(City existing) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, existing.getGuid());
		
		StringBuilder query = new StringBuilder("MATCH (c:`" + "City" + "`)-[r:HAS_REGION]->(re:`Region`) WHERE c.guid={"+Constants.GUID+"} AND re.status='ACTIVE' return r");
		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), params);
		Iterator<Map<String, Object>> itr = result.iterator();
		return itr.hasNext();
	}

	@Override
	public City updateCity(City toupdatestate, City existing) {
		Map<String, Object> params = new HashMap<String, Object>();
		City updated=super.update(toupdatestate);
		if(!toupdatestate.getName().equals(existing.getName())){
			params.put(Constants.NAME, existing.getName());
			params.put(Constants.CITY_NAME, toupdatestate.getName());

			StringBuilder query = new StringBuilder("MATCH (n:`Region`),(m:`Locality`) WHERE n.city_name={"+Constants.NAME+"} AND m.city_name={"+Constants.NAME+"} "
					+ "SET n.city_name={"+Constants.CITY_NAME+"}, m.city_name={"+Constants.CITY_NAME+"}");
			executeWriteQuery(query.toString(), params);
		}
		return updated;
	}
	
	protected List<City> convertResultToList(Result<Map<String, Object>> results) {
		List<City> list = new ArrayList<City>();
		if (results != null) {
			Iterator<Map<String, Object>> i = results.iterator();
			while (i.hasNext()) {
				Map<String, Object> map = i.next();

				BeanWrapper wrapper = new BeanWrapperImpl(City.class);
				wrapper.setPropertyValues(map);
				City city = (City) wrapper.getWrappedInstance();
				list.add(city);
			}
		}
		return list;
	}
	
	
	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		query.append(" RETURN t.name as name,t.guid as guid,t.status as status,t.state_code as stateCode ");
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);// +
																			// " collect(t) as betAnswers";

	}
}
