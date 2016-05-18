package com.clicktable.dao.intf;

import java.util.List;
import java.util.Set;

import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Event;
import com.clicktable.model.Restaurant;

@org.springframework.stereotype.Service
public interface EventDao extends GenericDao<Event> {


	String addEvent(Restaurant restaurant, Event event,
			List<CalenderEvent> calEvents);


	List<Event> eventExistForRestaurant(String eventName, String restId);



}
