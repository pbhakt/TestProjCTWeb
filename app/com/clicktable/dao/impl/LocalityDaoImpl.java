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

import com.clicktable.dao.intf.LocalityDao;
import com.clicktable.model.Locality;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;

@Service
public class LocalityDaoImpl extends GraphDBDao<Locality> implements LocalityDao {

	public LocalityDaoImpl() {
		super();
		this.setType(Locality.class);
	}

	@Override
	public List<Locality> getLocalities(Map<String, Object> params) {

		//List<Locality> localityList = new ArrayList<Locality>();
		StringBuilder query = new StringBuilder("MATCH (t:`"  + Constants.LOCALITY_LABEL+ "`)");

		if (containsKey(params, "region")) 
		{
			query.append(" <-[r:HAS_LOCALITY]-(a:Region) WHERE 1=1");

			if (params.containsKey(Constants.REGION_NAME)) 
			{
				query.append(" AND a.name = {" + Constants.REGION_NAME + "}");
			}
			
			if (params.containsKey(Constants.REGION_GUID)) 
			{
				query.append(" AND a.guid = {" + Constants.REGION_GUID + "}");
			}

		}
		else if (containsKey(params, "city")) 
		{
			query.append(" <-[r:HAS_LOCALITY]-(a:Region) WHERE 1=1");

			if (params.containsKey(Constants.CITY_NAME)) 
			{
				query.append(" AND a.city_name = {" + Constants.CITY_NAME + "}");
			}
			
		}
		else 
		{
			query.append(" WHERE 1=1 ");
		}

		if (params.containsKey(Constants.NAME)) {
			query.append(" AND t.name = {" + Constants.NAME + "} ");
		}
		if (params.containsKey(Constants.GUID)) {
			query.append(" AND t.guid = {" + Constants.GUID + "} ");
		}
		if (params.containsKey(Constants.REGION_NAME)) {
			query.append(" AND t.region_name = {" + Constants.REGION_NAME + "} ");
		}
		if (params.containsKey(Constants.CITY_NAME)) {
			query.append(" AND t.city_name = {" + Constants.CITY_NAME + "} ");
		}
		if (params.containsKey(Constants.STATUS)) {
			query.append(" AND t.status = {" + Constants.STATUS + "} ");
		}
		//query.append(" RETURN t ");
		query.append(" RETURN t.name as name,t.guid as guid,t.status as status,t.city_name as cityName,t.region_name as regionName ");
		
		
		
		List<Locality> localitys = executeQuery(query.toString(),
				params);
	/*	for (Map<String, Object> locality1 : localitys) {
			Locality ar = new Locality((Node) locality1.get("t"));
			localityList.add(ar);
			Logger.debug("  locality :::: >>>>>>> " + ar);

		}*/

		return localitys;
	}

	@Override
	public String addLocality(Locality locality) {
		 Map<String, Object> params = new HashMap<String, Object>();
	        Map<String, Object> params1 = getGraphProperty(UtilityMethods.introspect(locality));
	        params.putAll(params1);
	        params.put(Constants.PROP_MAP, params1);
   		
		String query = "MATCH (c:`Region` {name:{"+getPropertyName(Constants.REGION_NAME)+"},city_name:{"+getPropertyName(Constants.CITY_NAME)+"}, status:'"+Constants.ACTIVE_STATUS+"'}) \n";
   		query = query + "MERGE (c)-[ahl:HAS_LOCALITY]->(t:" + Constants.LOCALITY_LABEL+/*":_" + Constants.LOCALITY_LABEL+*/" {id:{"
   				+Constants.PROP_MAP
   				+"}.id}) ON CREATE SET t={"+Constants.PROP_MAP+"}"+ "RETURN t";
   		
		Map<String, Object> result = executeWriteQuery(query, params).singleOrNull();
		if (result == null)
			return null;
		else
			return (String) ((Node) result.get("t")).getProperty(Constants.GUID);
	}

	@Override
	public boolean hasChildRelationships(Locality existing) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, existing.getGuid());
		StringBuilder query = new StringBuilder("MATCH (c:`" + "Locality" + "`)-[r:HAS_BUILDING]->(b:`Building`) WHERE c.guid={"+Constants.GUID+"} AND b.status='ACTIVE' return r");
		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), params);
		Iterator<Map<String, Object>> itr = result.iterator();
		
		return itr.hasNext();
	}

	@Override
	public Locality updateLocality(Locality toUpdatelocality, Locality existing) {
		Map<String, Object> params = new HashMap<String, Object>();
		Locality updated=super.update(toUpdatelocality);
		if(!toUpdatelocality.getName().equals(existing.getName())){
			params.put(Constants.NAME, existing.getName());
			params.put(Constants.LOCALITY_NAME, toUpdatelocality.getName());
		StringBuilder query = new StringBuilder("MATCH (m:`Building`) WHERE  m.locality_name={"+Constants.NAME+"} SET m.locality_name={"+Constants.LOCALITY_NAME+"}");
		executeWriteQuery(query.toString(), params);
		}
		return updated;
	}
	
	protected List<Locality> convertResultToList(Result<Map<String, Object>> results) {
		List<Locality> list = new ArrayList<Locality>();
		if (results != null) {
			Iterator<Map<String, Object>> i = results.iterator();
			while (i.hasNext()) {
				Map<String, Object> map = i.next();

				BeanWrapper wrapper = new BeanWrapperImpl(Locality.class);
				wrapper.setPropertyValues(map);
				Locality city = (Locality) wrapper.getWrappedInstance();
				list.add(city);
			}
		}
		return list;
	}
	
	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		query.append(" RETURN t.name as name,t.guid as guid,t.status as status,t.city_name as cityName,t.region_name as regionName ");
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);// +
																			// " collect(t) as betAnswers";

	}
}
