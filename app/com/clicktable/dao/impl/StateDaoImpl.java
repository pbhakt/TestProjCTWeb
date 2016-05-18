package com.clicktable.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.StateDao;
import com.clicktable.model.State;
import com.clicktable.util.Constants;

@Service
public class StateDaoImpl extends GraphDBDao<State> implements StateDao {

	public StateDaoImpl() {
		super();
		this.setType(State.class);
	}

	public List<State> findStates(Map<String, Object> params) {
		
		
		List<State> stateList=new ArrayList<State>();
		StringBuilder query = new StringBuilder("MATCH (t:`"+"State"+"`)");

		if (containsKey(params, "country")) {
			query.append(" <-[r:HAS_STATE]-(c:Country) WHERE 1=1");

			if (params.containsKey(Constants.COUNTRY_CODE)) {
				query.append(" AND c.country_cd = {"+Constants.COUNTRY_CODE+"}");
			}
			if (params.containsKey(Constants.COUNTRY_NAME)) {
				query.append(" AND c.name = {"+Constants.COUNTRY_NAME+"}");
			}
			if (params.containsKey(Constants.COUNTRY_GUID)) {
				query.append(" AND c.guid = {"+Constants.COUNTRY_GUID+"}");
			}
		} else {
			query.append(" WHERE 1=1 ");
		}

		if (params.containsKey(Constants.NAME)) {
			query.append(" AND t.name = {"+Constants.NAME+"} ");
		}
		if (params.containsKey(Constants.GUID)) {
			query.append(" AND t.guid = {"+Constants.GUID+"} ");
		}
		if (params.containsKey(Constants.STATE_CODE)) {
			query.append(" AND t.state_code = {"+Constants.STATE_CODE+"} ");
		}
		if (params.containsKey(Constants.STATUS)) {
			query.append(" AND t.status = {"+Constants.STATUS+"} ");
		}
		query.append(" RETURN t ");

		Logger.debug("  query :::: >>>>>>> " + query.toString());
		
		
		Result<Map<String, Object>> states = executeWriteQuery(query.toString(),
				params);

		for (Map<String, Object> state : states) {

			State st = new State((Node) state.get("t"));
			stateList.add(st);
			Logger.debug("  state :::: >>>>>>> " + st);

		}

		return stateList;
	}

	@Override
	public String addState(State state) {
		StringBuilder query = new StringBuilder("MATCH (c:Country {country_cd:{"+Constants.COUNTRY_CODE+"},status:{"+Constants.STATUS+"}}) \n");
		query = query.append("MERGE (c)-[chs:HAS_STATE]->(t:" + Constants.STATE_LABEL+":_" + Constants.STATE_LABEL+" {");
		Map<String, Object> params =addingNodeProperties(query, state);
		params.put(Constants.COUNTRY_CODE, state.getCountryCode());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
   		query.append("}) RETURN t");
   		System.out.println(state.getGuid());
   		System.out.println("query----"+query.toString());
   		Map<String, Object> result = executeWriteQuery(query.toString(), params).singleOrNull();
   		return (String) ((Node) result.get("t")).getProperty(Constants.GUID);
	}

	@Override
	public boolean hasChildRelationships(State existing) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, existing.getGuid());
		StringBuilder query = new StringBuilder("MATCH (c:`" + "State" + "`)-[r:HAS_CITY]->(ci:`City`) WHERE c.guid={"+Constants.GUID+"} AND ci.status='ACTIVE' return r");
		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), params);
		Iterator<Map<String, Object>> itr = result.iterator();
		
		return itr.hasNext();
	}

	@Override
	public State updateState(State toupdatestate, State existing) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("updatedStateCode", toupdatestate.getStateCode());
		params.put("existingStateCode", existing.getStateCode());
		State updated=super.update(toupdatestate);
		if(!toupdatestate.getStateCode().equals(existing.getStateCode())){
		StringBuilder query = new StringBuilder("MATCH (n:`City`),(m:`Region`) WHERE n.state_code={existingStateCode} AND m.state_code={existingStateCode} SET n.state_code={updatedStateCode}, m.state_code={updatedStateCode}");
		executeWriteQuery(query.toString(), params);
		}
		return updated;
	}

	

}

