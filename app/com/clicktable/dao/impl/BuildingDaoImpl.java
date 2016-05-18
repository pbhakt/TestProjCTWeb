package com.clicktable.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.BuildingDao;
import com.clicktable.model.Building;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;

@Service
public class BuildingDaoImpl extends GraphDBDao<Building> implements
		BuildingDao {

	public BuildingDaoImpl() {
		super();
		this.setType(Building.class);
	}

	@Override
	public List<Building> findBuildings(Map<String, Object> params) {

		StringBuilder query = new StringBuilder("MATCH (t:`"  + Constants.BUILDING_LABEL+ "`)");

		if (containsKey(params, "locality")) {
			query.append(" <-[r:HAS_BUILDING]-(l:Locality) WHERE 1=1");
			
			if (params.containsKey(Constants.LOCALITY_NAME)) {
				query.append(" AND l.name = {" + Constants.LOCALITY_NAME + "}");
			}
			
			if (params.containsKey(Constants.LOCALITY_GUID)) {
				query.append(" AND l.guid = {" + Constants.LOCALITY_GUID + "}");
			}

		} else if (containsKey(params, "region")) {
			query.append(" <-[r:HAS_BUILDING]-(l:Locality) WHERE 1=1");
			
			if (params.containsKey(Constants.REGION_NAME)) {
				query.append(" AND l.region_name = {" + Constants.REGION_NAME + "}");
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
		if (params.containsKey(Constants.LOCALITY_NAME)) {
			query.append(" AND t.locality_name = {" + Constants.LOCALITY_NAME + "} ");
		}
		if (params.containsKey(Constants.REGION_NAME)) {
			query.append(" AND t.region_name = {" + Constants.REGION_NAME + "} ");
		}
		if (params.containsKey(Constants.STATUS)) {
			query.append(" AND t.status = {" + Constants.STATUS + "} ");
		}
		//query.append(" RETURN t ");
		query.append(" RETURN t.name as name,t.guid as guid,t.status as status,t.region_name as regionName,t.locality_name as localityName ");
		
		return executeQuery(query.toString(),params);


	}
	
	@Override
	public String addBuilding(Building building) {
		Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> params1 = getGraphProperty(UtilityMethods.introspect(building));
        params.putAll(params1);
        params.put(Constants.PROP_MAP, params1);
		
		String query = "MATCH (c:Locality {name:{"+getPropertyName(Constants.LOCALITY_NAME)+"},region_name:{"+getPropertyName(Constants.REGION_NAME)+"}, status:{"+getPropertyName(Constants.STATUS)+"}}) \n";
   		query = query + "MERGE (c)-[cha:HAS_BUILDING]->(t:" + Constants.BUILDING_LABEL+/*":_"+ Constants.BUILDING_LABEL+*/" {id:{"
   				+Constants.PROP_MAP
   				+"}.id}) ON CREATE SET t={"+Constants.PROP_MAP+"}"+ "RETURN t";
		
   		Map<String, Object> result = executeWriteQuery(query, params).singleOrNull();
		if (result == null)
			return null;
		else
			return (String) ((Node) result.get("t")).getProperty(Constants.GUID); 	
	}

	protected List<Building> convertResultToList(Result<Map<String, Object>> results) {
		List<Building> list = new ArrayList<Building>();
		if (results != null) {
			Iterator<Map<String, Object>> i = results.iterator();
			while (i.hasNext()) {
				Map<String, Object> map = i.next();

				BeanWrapper wrapper = new BeanWrapperImpl(Building.class);
				wrapper.setPropertyValues(map);
				Building city = (Building) wrapper.getWrappedInstance();
				list.add(city);
			}
		}
		return list;
	}
	
	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		query.append(" RETURN t.name as name,t.guid as guid,t.status as status,t.region_name as regionName,t.locality_name as localityName ");
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);// +
																			// " collect(t) as betAnswers";

	}
	
}
