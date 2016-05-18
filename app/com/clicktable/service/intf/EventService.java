package com.clicktable.service.intf;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Event;
import com.clicktable.response.BaseResponse;

@Service
public interface EventService {

	BaseResponse addEvent(Event event, String token);

	BaseResponse getCalenderEvents(Map<String, Object> stringParamMap,
			String header);

	//BaseResponse patchEvent(Event event, String header);

	BaseResponse updateEvent(Event event, String token);

	BaseResponse getEvents(Map<String, Object> params);

	BaseResponse deleteCalendarEvent(CalenderEvent calEvent, String token);

	BaseResponse deleteEvent(Event event, String token);

	BaseResponse updateCalendarEvent(CalenderEvent calEvent, String token);

	void shuffleTable(Event existing, String token);

	/*BaseResponse getCalenderEventsReport(Map<String, Object> params,
			String token);

	BaseResponse getCalenderEventAttendenceReport(Map<String, Object> params,
			String token);*/

	//BaseResponse getCalenderEventAttendenceReport(Map<String, Object> params,
	//		String token);

}
