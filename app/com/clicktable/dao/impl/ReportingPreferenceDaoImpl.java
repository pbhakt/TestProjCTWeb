package com.clicktable.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.ReportingPreferenceDao;
import com.clicktable.model.ReportingPreference;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author g.singh
 *
 */

@Service
public class ReportingPreferenceDaoImpl extends GraphDBDao<ReportingPreference> implements ReportingPreferenceDao {

	public ReportingPreferenceDaoImpl() {
		super();
		this.setType(ReportingPreference.class);
	}

	@Override
	public String addPrefernce(ReportingPreference reportingPreference) {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:" + Constants.RESTAURANT_LABEL + "{guid:{" + Constants.REST_GUID + "}})");
		query.append("CREATE (r)-[hrp:" + RelationshipTypes.HAS_REPORTING_PREFERENCE + "]->(t:" + Constants.REPORTING_PREFERENCE_LABEL + ":_" + Constants.REPORTING_PREFERENCE_LABEL + "{");
		Map<String, Object> params = addingNodeProperties(query, reportingPreference);
		query.append("}) return t");
		System.out.println(query);
		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), params);
		return getSingleResultGuid(result);
	}

	@Override
	public List<ReportingPreference> findByCustomeFields(Class<ReportingPreference> class1, Map<String, Object> params) {
		List<ReportingPreference> prefernces = new ArrayList<ReportingPreference>();
		StringBuilder query = getMatchClause(params);
		query.append(" <-[rel:`" + RelationshipTypes.HAS_REPORTING_PREFERENCE + "`]-(rest:`"+Constants.RESTAURANT_LABEL+"`) ");
		query.append(getWhereClause(params));
		query.append(" RETURN DISTINCT t");
		System.out.println("query is--------------------------------- " + query);
		System.out.println("params is--------------------------------- " + params);
		Map<String, Object> map;// = new HashMap<String, Object>();

		Result<Map<String, Object>> results = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			map = i.next();
			ReportingPreference prefernce = template.convert(map.get("t"), ReportingPreference.class);
			prefernces.add(prefernce);
		}

		return prefernces;
	}

	@Override
	public ReportingPreference update(ReportingPreference preference) {
		template.saveOnly(preference);
		return preference;
	}
	
/*	@Override
	public ReportingPreference update(Map<String, Object> valuesToUpdate) {
		StringBuilder query = new StringBuilder("MATCH (t:" + type.getSimpleName() + ": _" + type.getSimpleName() + " {guid:{" + Constants.GUID + "}}) ");
		query.append("SET ");
		valuesToUpdate.keySet().stream().filter(key -> !(key.equals(Constants.GUID)||key.equals(Constants.REST_GUID))).forEach(key -> {
			query.append(" t." + getPropertyName(key) + "={" + key + "},");
		});
		//query.deleteCharAt(query.lastIndexOf(","));

		List<String> valuesToRemove=new ArrayList<String>();
		valuesToRemove.add("salesPersonMobile");
		valuesToRemove.add("ownerEmail");
		valuesToRemove.add("ownerMobile");
		valuesToRemove.add("ownerName");
		valuesToRemove.add("managerName");
		valuesToRemove.add("managerEmail");
		valuesToRemove.add("managerMobile");
		
		valuesToRemove.stream().filter(x->valuesToUpdate.get(x)==null).forEach(property->{
			query.append(" t." + getPropertyName(property) + "=null,");
		});
		query.deleteCharAt(query.lastIndexOf(","));
		query.append(" RETURN t");
		System.out.println(query);
		System.out.println(valuesToUpdate);

		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), valuesToUpdate);
		ReportingPreference reportingPreference = (ReportingPreference) template.projectTo(result.single().get("t"), type);
		// template.saveOnly(t);
		return reportingPreference;
	}*/
	
	/*
	public ReportingPreference get(Map<String, Object> params) {
		StringBuilder query = new StringBuilder("MATCH (t:" + type.getSimpleName() + ": _" + type.getSimpleName() + " {guid:{" + Constants.GUID + "}}) ");
		query.append("-[rpr:"+RelationshipTypes.HAS_REPORTING_PREFERENCE+"]->(t)");
		query.append(" RETURN t");
		System.out.println(query);
		System.out.println(params);
		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), params);
		ReportingPreference reportingPreference = (ReportingPreference) template.projectTo(result.single().get("t"), type);
		// template.saveOnly(t);
		return reportingPreference;
	}*/

}
