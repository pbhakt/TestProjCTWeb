package com.clicktable.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.CalenderEventDao;
import com.clicktable.dao.intf.EventDao;
import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Event;
import com.clicktable.model.Restaurant;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * @author s.gupta
 *
 */
@Service
public class EventDaoImpl extends GraphDBDao<Event> implements EventDao {	

	@Autowired
	CalenderEventDao calanderDao;

	public EventDaoImpl() {
		super();
		this.setType(Event.class);
	}

	public String addEvent(Restaurant restaurant, Event event, List<CalenderEvent> calEvents) {

		event=template.save(event);		
		calEvents=calanderDao.createMultiple(calEvents);		
		calanderDao.createCalEventRelationShips(restaurant, event, calEvents);
		System.out.println("addEvent method");
		return event.getGuid();
	}

	@Override
	public List<Event> eventExistForRestaurant(String eventName, String restId) {

		Map<String, Object> params = new java.util.HashMap<String, Object>();
		params.put(Constants.REST_ID, restId);
		params.put(Constants.NAME, eventName);
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);

		StringBuilder query = new StringBuilder("MATCH (r:" + Constants.RESTAURANT_LABEL + ")-[:`" + RelationshipTypes.HAS_EVENT + "`]->(e:" + Constants.EVENT_LABEL + ") where r.guid={"
				+ Constants.REST_ID + "} AND e." + Constants.NAME + " = {" + Constants.NAME + "} AND e." + Constants.STATUS + " = {" + Constants.STATUS + "} RETURN e ");
		return executeQuery(query.toString(), params);		
	}
	
	
	@Override
	protected StringBuilder getWhereClause(Map<String, Object> params) {
		
		StringBuilder query = super.getWhereClause(params);		
		if(params.containsKey(Constants.FREE_SEARCH))
	   	{
	   	    String regularExpString = Constants.PRE_LIKE_STRING+params.get(Constants.FREE_SEARCH)+Constants.POST_LIKE_STRING;
	   	    params.put(Constants.FREE_SEARCH, regularExpString );
	   	    query = applyFreeSearch(Constants.FREE_SEARCH, query);
	   	}

		return query;
	}
		
	/**
	 * private method that creates query for like parameters name,category
	 * @param likeValue
	 * @param query
	 * @return
	 */
	private StringBuilder applyFreeSearch(String likeValue , StringBuilder query)
	{
		if(query.toString().contains(Constants.WHERE))
		{
			query.append(" AND (t.name=~{"+likeValue+"} OR t.category=~{"+likeValue+"} OR t.event_desc=~{"+likeValue+"})");
		}
		else
		{
			query.append(" WHERE (t.name=~{"+likeValue+"} OR t.category=~{"+likeValue+"} OR t.event_desc=~{"+likeValue+"})");
		}

		return query;
	}
	


}
