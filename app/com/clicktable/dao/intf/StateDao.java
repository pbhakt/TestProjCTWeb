package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.State;

@org.springframework.stereotype.Service
public interface StateDao extends GenericDao<State> {

	List<State> findStates(Map<String, Object> params);

	String addState(State state);

	boolean hasChildRelationships(State existing);
	
	State updateState(State toUpdateState, State existing);
}
