
package com.clicktable.dao.impl;




import static com.clicktable.util.Constants.EVENT_GUID;
import static com.clicktable.util.Constants.EVENT_ID;
import static com.clicktable.util.Constants.REST_GUID;
import static com.clicktable.util.Constants.START_TIME;
import static com.clicktable.util.Constants.UPDATED_DATE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.CalenderEventDao;
import com.clicktable.model.CalenderEvent;
import com.clicktable.model.CustomCalendarEvent;
import com.clicktable.model.Event;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Table;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;



/**
 * @author s.gupta
 *
 */
@Service
public class CalenderEventDaoImpl extends GraphDBDao<CalenderEvent> implements
CalenderEventDao {

	public CalenderEventDaoImpl() {
		super();
		this.setType(CalenderEvent.class);
	}

	@Override
	public int deleteCalanderEventsAfterUpdate(Event event) {
		StringBuilder query=new StringBuilder("Match (e:Event {guid:{"+EVENT_ID+"}})-[r]->(c:`"+type.getSimpleName()+"`) WHERE ");

		query.append("toInt(c."+getPropertyName(START_TIME)+") > toInt({"+UPDATED_DATE+"}) ");		
		query.append(" WITH c,r OPTIONAL MATCH (c)-[rel]-() ");
		query.append("DELETE c,r, rel");
		query.append(" RETURN count(distinct(c))");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(EVENT_ID, event.getGuid());
		params.put(UPDATED_DATE,DateTime.now().toDate());

		Result<Integer> result = executeWriteQuery(query.toString(), params).to(Integer.class);
		return result.single();
	}

	
	@Override
	public void createCalEventRelationShips(Restaurant restaurant, Event event,
			List<CalenderEvent> calEvents) {
		StringBuilder query = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(REST_GUID, restaurant.getGuid());
		params.put(EVENT_GUID, event.getGuid());
		/* Get Restaurant Match Clause */
		query.append("MATCH (restaurant:`");
		query = getGenericMatchClause(Restaurant.class, query).append('`').append("{guid:{" + REST_GUID + "}}").append(')');

		/* Get Event Match Clause */
		query.append(" , (event:`");
		query = getGenericMatchClause(Event.class, query).append('`').append("{guid:{" + EVENT_GUID + "}}").append(')');

		if (event.getType()!=null && event.getType().equalsIgnoreCase("BLOCK")) {
			query.append(" , (table:`");
			query = getGenericMatchClause(Table.class, query).append("`)");
			params.put(Constants.TABLE_GUID, event.getBlockingArea());
			addPrefix(query);
			query.append(" table.guid IN {" + Constants.TABLE_GUID + "} ");
			appendWith(query);
			query.append("collect(table) as tableCollection,");
		}
		appendWith(query);
		query.append(" {" + Constants.CALEVENT_GUID + "} as caleventCollection, ");
		params.put(Constants.CALEVENT_GUID, getGuids(calEvents));

		appendWith(query);
		query.append("restaurant as r, event as e ");

		
		query.append("UNWIND caleventCollection as CalEvents ");
		query.append("MATCH (cEvent:`CalenderEvent` {guid : CalEvents}) ");
		query.append("MERGE (r)-[:`" + RelationshipTypes.HAS_EVENT + "`{__type__:'" + "HasEvent" + "', guid:{" + EVENT_GUID + "}}]->e ");
		query.append("MERGE (r)-[:`REST_HAS_CAL` {__type__:'RestaurantHasCalender',type:cEvent.type}]->(cEvent) ");
		query.append("MERGE (e)-[:`HAS_CAL_EVENT` {__type__:'HasCalanderEvent'}]->(cEvent) ");
		
		if(event.getCategory()!=null && event.getCategory().equalsIgnoreCase("BLOCK")){
			query.append("FOREACH (t IN tableCollection | MERGE (cEvent)-[:`CALC_BLOCKED_TBL` "
				+ "{__type__:'CalenderBlockedTable',event_dt:cEvent.event_dt,start_time:cEvent.start_time,end_time:cEvent.end_time,table_guid:t.guid}]->(t)) ");
		}
					
		Logger.debug("query is ="+query);
		executeWriteQuery(query.toString(), params);

	}
	
	private void appendWith(StringBuilder query) {
		if (!query.toString().contains("WITH")) 
			query.append(" WITH ");
	}



	@Override
	public int countForParentEventGuid(String guid) {
		StringBuilder query=new StringBuilder("Match (e:Event {guid:{"+EVENT_ID+"}})-[r]->(c:`"+type.getSimpleName()+"`)  RETURN count(distinct(c))");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(EVENT_ID, guid);		
		Result<Integer> result = executeWriteQuery(query.toString(), params).to(Integer.class);
		return result.single();
		
	}

	@Override
	public List<CalenderEvent> getOngoingCalendarEvents(Event event) {
		Map<String, Object> params = new HashMap<String, Object>();
		Date currentTime = DateTime.now().toDate();
		params .put(Constants.START_TIME_BEFORE, currentTime);
		params.put(Constants.END_TIME_AFTER, currentTime);
		params.put(Constants.PARENT_EVENT_GUID, event.getGuid());
		List<CalenderEvent> ongoingEvents = findByFields(CalenderEvent.class, params);
		return ongoingEvents;
	}
	
	
	
	
	
	protected StringBuilder getMatchClause(Map<String, Object> params) {
		if(params.containsKey(Constants.REST_GUID)){
			return new StringBuilder("MATCH (r:"+Constants.RESTAURANT_LABEL+"{guid:{"+Constants.REST_GUID+"}})-[rhc:"+RelationshipTypes.REST_HAS_CAL+"]-(t:`" + type.getSimpleName() + "`)");
		}else if(params.containsKey(Constants.EVENT_GUID)){
			return new StringBuilder("Match (e:Event {guid:{"+Constants.EVENT_GUID+"}})-[r:"+RelationshipTypes.HAS_CAL_EVENT+"]->(c:"+type.getSimpleName()+")");
		}else
			return new StringBuilder("MATCH (t:`" + type.getSimpleName() + "`)");	
	}
	
	
	
	@Override
	public List<CalenderEvent> findDetailsByFields(Class type1, Map<String, Object> params) {
		
		StringBuilder query = new StringBuilder("MATCH (e:`" + Constants.EVENT_LABEL + "`) -[]->(t:`"+ type.getSimpleName()+ "`) ");
		query.append(getWhereClause(params));
		query.append(" RETURN t,e ");
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		query.append(" SKIP " + startIndex + " LIMIT " + pageSize);
		
		Result<Map<String, Object>> results = template.query(query.toString(), params);
		
		List<CalenderEvent> list = new ArrayList<CalenderEvent>();
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			CustomCalendarEvent calEvent = new CustomCalendarEvent(template.convert(map.get("t"), CalenderEvent.class));
			Event event = template.convert(map.get("e"), Event.class);
			//calEvent.setAllday(event.isAllday());
			calEvent.setRecurring(event.isRecurring());
			calEvent.setRecurrenceType(event.getRecurrenceType());	
			list.add(calEvent);
		}

		return list;
	}
	
	
	@Override
	public List<CalenderEvent> getHolidays(Map<String, Object> params) {
		StringBuilder query = new StringBuilder("MATCH (r:`" + Restaurant.class.getSimpleName() + "`{guid:{"+Constants.REST_GUID+"}}) -[rhc:"+RelationshipTypes.REST_HAS_CAL+"]->(t:`"+ CalenderEvent.class.getSimpleName()+ "`) ");
		query.append(getWhereClause(params));
		query.append(" RETURN t.guid as guid , t.name as name , t.start_time as startTime ");
		Result<Map<String, Object>> results = template.query(query.toString(), params);
		List<CalenderEvent> list = new ArrayList<CalenderEvent>();
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) 
		{
		    Map<String, Object> map = i.next();
		    CalenderEvent event=new CalenderEvent();
		    event.setCreatedDate(null);
		    event.setStatus(null);
		    event.setGuid((String) map.get("guid"));
		    event.setName((String) map.get("name"));
		    event.setStartDate((Long) map.get("startTime"));
		    list.add(event);
		}

		return list;
	}

	@Override
	public int getPastCalEventsCount(String eventGuid) {
		Map<String, Object> params = new HashMap<String, Object>();
		Date currentTime = DateTime.now().toDate();
		params .put(Constants.START_TIME_BEFORE, currentTime);		
		params.put(Constants.PARENT_EVENT_GUID, eventGuid);
		StringBuilder query = getMatchClause(params);
		query.append(getWhereClause(params));
		query.append(" RETURN count(distinct(t))");
		
		Result<Integer> result = executeWriteQuery(query.toString(), params).to(Integer.class);
		return result.single();
	}
	
}


