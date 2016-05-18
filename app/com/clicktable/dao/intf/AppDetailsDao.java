package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.ApplicationDetails;
import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Event;
import com.clicktable.model.Restaurant;
import com.clicktable.model.State;

@org.springframework.stereotype.Service
public interface AppDetailsDao extends GenericDao<ApplicationDetails> {

	String addApplicationDetails(ApplicationDetails appDetails);

	List<ApplicationDetails> findApplicationDetails(Map<String, Object> param);

	/*List<State> findStates(Map<String, Object> params);

	String addState(State state);

	boolean hasChildRelationships(State existing);
	
	State updateState(State toUpdateState, State existing);*/
}
