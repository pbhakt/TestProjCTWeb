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

import com.clicktable.dao.intf.RegionDao;
import com.clicktable.model.Region;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;

@Service
public class RegionDaoImpl extends GraphDBDao<Region> implements RegionDao {

	public RegionDaoImpl() {
		super();
		this.setType(Region.class);
	}


	@Override
  public String addRegion(Region region) {
        
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> params1 = getGraphProperty(UtilityMethods.introspect(region));
        params.putAll(params1);
        params.put(Constants.PROP_MAP, params1);
        
        String query = "MATCH (c:City {name:{"+getPropertyName(Constants.CITY_NAME)+"}, state_code:{"+getPropertyName(Constants.STATE_CODE)+"}, status:{"+getPropertyName(Constants.STATUS)+"}}) \n";
          query = query + "MERGE (c)-[cha:HAS_REGION]->(t:" + Constants.REGION_LABEL+ /*":_" + Constants.REGION_LABEL+*/"{id:{"
                  +Constants.PROP_MAP
                  +"}.id}) ON CREATE SET t={"+Constants.PROP_MAP+"}"+ "RETURN t";

   		
   		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), params);
   		return getSingleResultGuid(result);  		
	}

	@Override
	public List<Region> findRegions(Map<String, Object> params) {
		System.out.println("-----params----"+params.size());
		StringBuilder query = new StringBuilder("MATCH (t:`"  + Constants.REGION_LABEL+ "`)");

		if (containsKey(params, "city")) {
			query.append(" <-[r:HAS_REGION]-(c:City) WHERE 1=1");
			if (params.containsKey(Constants.CITY_NAME)) {
				query.append(" AND c.name = {" + Constants.CITY_NAME + "}");
			}
			if (params.containsKey(Constants.CITY_GUID)) {
				query.append(" AND c.guid = {" + Constants.CITY_GUID + "}");
			}

		}else if (containsKey(params, "state")) {
			query.append(" <-[r:HAS_REGION]-(c:City) WHERE 1=1");
			if (params.containsKey(Constants.CITY_STATECODE)) {
				query.append(" AND c.state_code = {" + Constants.CITY_STATECODE
						+ "}");
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
		if (params.containsKey(Constants.CITY_NAME)) {
			query.append(" AND t.city_name = {" + Constants.CITY_NAME + "} ");
		}
		if (params.containsKey(Constants.STATE_CODE)) {
			query.append(" AND t.state_code = {" + Constants.STATE_CODE + "} ");
		}
		if (params.containsKey(Constants.STATUS)) {
			query.append(" AND t.status = {" + Constants.STATUS + "} ");
		}
		//query.append(" RETURN t ");
		query.append(" RETURN t.name as name,t.guid as guid,t.status as status,t.state_code as stateCode,t.city_name as cityName ");
		
		List<Region> regionList = executeQuery(query.toString(), params);				
		return regionList;
	}


	@Override
	public boolean hasChildRelationships(Region existing) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, existing.getGuid());
		StringBuilder query = new StringBuilder("MATCH (c:`" + "Region" + "`)-[r:HAS_LOCALITY]->(l:`Locality`) WHERE c.guid={"+Constants.GUID+"} AND l.status='ACTIVE' return r");
		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), params);
		Iterator<Map<String, Object>> itr = result.iterator();
		
		return itr.hasNext();
	}
	
	@Override
	public Region updateRegion(Region toupdatestate, Region existing) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("existingName", existing.getName());
		params.put("updatedName", toupdatestate.getName());
		Region updated=super.update(toupdatestate);
		if(!toupdatestate.getName().equals(existing.getName())){
		StringBuilder query = new StringBuilder("MATCH (n:`Locality`),(m:`Building`) WHERE n.region_name={existingName} AND m.region_name={existingName} SET n.region_name={updatedName}, m.region_name={updatedName}");
		executeWriteQuery(query.toString(), params);
		}
		return updated;
	}

	protected List<Region> convertResultToList(Result<Map<String, Object>> results) {
		List<Region> list = new ArrayList<Region>();
		if (results != null) {
			Iterator<Map<String, Object>> i = results.iterator();
			while (i.hasNext()) {
				Map<String, Object> map = i.next();

				BeanWrapper wrapper = new BeanWrapperImpl(Region.class);
				wrapper.setPropertyValues(map);
				Region region = (Region) wrapper.getWrappedInstance();
				list.add(region);
			}
		}
		return list;
	}
	
	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		query.append(" RETURN t.name as name,t.guid as guid,t.status as status,t.state_code as stateCode,t.city_name as cityName ");
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);// +
																			// " collect(t) as betAnswers";

	}
}
