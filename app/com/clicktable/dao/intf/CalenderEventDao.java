package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Event;
import com.clicktable.model.Restaurant;

@Service
public interface CalenderEventDao extends GenericDao<CalenderEvent> {


	void createCalEventRelationShips(Restaurant restaurant, Event event,
			List<CalenderEvent> calEvents);

	int deleteCalanderEventsAfterUpdate(Event event);

	int countForParentEventGuid(String guid);

	List<CalenderEvent> getOngoingCalendarEvents(Event event);

	List<CalenderEvent> findDetailsByFields(Class type,
			Map<String, Object> qryParamMap);

	List<CalenderEvent> getHolidays(Map<String, Object> params);

	int getPastCalEventsCount(String guid);


}
