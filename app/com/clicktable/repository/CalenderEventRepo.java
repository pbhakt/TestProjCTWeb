
package com.clicktable.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Event;
@org.springframework.stereotype.Service
public interface CalenderEventRepo extends GraphRepository<CalenderEvent>, CypherDslRepository<CalenderEvent> {
	
	
	/* Event  Get Operational Hours  */
	@Query("MATCH  (event:`Event`)<-[HAS_EVENT]-(rest:Restaurant) WHERE rest.guid={0} "
			+ " AND  event.category='OP_HR' "
			+ " AND ( (toInt(event.end_dt)=toInt({2}) AND toInt(event.start_dt)=toInt({1})) "
			+ " OR    (toInt(event.start_dt)<=toInt({1}) AND toInt(event.recurrence_end_dt)>=toInt({2})) ) "
			+ " AND ( (toInt(event.start_time)<=toInt({3}) AND toInt(event.end_time)>=toInt({3})) "
			+ " AND    (toInt(event.start_time)<=toInt({4}) AND toInt(event.end_time)>=toInt({4})) ) "
			+ " RETURN  DISTINCT event")
	Event findOperationalHour(String restGuid,long startDay,long endDay,long startTime,long endTime);
	
	/* Event  Get Holiday  */
	@Query("MATCH (rest:Restaurant)-[HAS_EVENT]->(event:Event)-[HAS_CAL_EVENT]->(calEvent:CalenderEvent)"
			+ " WHERE rest.guid={0} "
			+ " AND  event.category='HOLIDAY'  AND "
			+ "(toInt(calEvent.event_dt)   <=toInt({1}) AND toInt(event.end_dt)   >=toInt({1})) AND"
			+ "(toInt(calEvent.event_dt)   <=toInt({2}) AND toInt(event.end_dt)   >=toInt({2})) AND"
			+" (toInt(calEvent.start_time) <=toInt({3}) AND toInt(calEvent.end_time) >=toInt({3})) AND"
			+" (toInt(calEvent.start_time) <=toInt({4}) AND toInt(calEvent.end_time) >=toInt({4})) "
			+ " RETURN event")
	Event findHolidayDay(String restGuid,long startDay,long endDay,long startTime,long endTime);
	
	
	/* CalEvent  Check Operational Hours */
	@Query("MATCH  (event:`Event`)<-[HAS_EVENT]-(rest:Restaurant)-[REST_HAS_CAL]->(calEvent:CalenderEvent)<-[HAS_CAL_EVENT]-(event) WHERE rest.guid={0} "
			+ " AND  calEvent.category='OP_HR' AND event.category='OP_HR' "
			+ " AND  toInt(calEvent.event_dt)=toInt({1}) "
			+ " AND ( ( toInt(calEvent.start_time)<=toInt({2}) AND toInt(calEvent.end_time)>=toInt({2}) ) "
			+ " OR      toInt(calEvent.start_time)<=toInt({3}) AND toInt(calEvent.end_time)>=toInt({3}) ) "
			+ " RETURN  DISTINCT calEvent")
	CalenderEvent isOperationalHour(String restGuid,long startDay,long startTime,long endTime);

}

