/*package com.clicktable.scheduler;

import com.clicktable.exception.ClicktableException;
import com.clicktable.model.Event;
import com.clicktable.response.GetResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.EventService;
import com.clicktable.util.Constants;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import play.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ExtendRecurEndDateForNever implements Runnable {

	@Autowired
	EventService eventService;
	@Autowired
	AuthorizationService authService;
	private static Lock lock = new ReentrantLock();
	private static Logger.ALogger log = Logger.of(ExtendRecurEndDateForNever.class);

	public ExtendRecurEndDateForNever() {
	}

	public ExtendRecurEndDateForNever(EventService eventService, AuthorizationService authService) {
		this.eventService = eventService;
		this.authService = authService;
	}

	@Override
	public void run() {
		//log.info("Starting ExtendRecurEndDateForNever scheduled job");
		//runExtendRecurEndDateForNever();
		//log.info("Finished ExtendRecurEndDateForNever scheduled job");
	}

	public void runExtendRecurEndDateForNever() {
		if (ExtendRecurEndDateForNever.lock.tryLock()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.RECUR_END_TYPE, Constants.NEVER);
			params.put(Constants.RECURRANCE_END_DATE, new DateMidnight().toDate());
			log.info("Running Extend recur End Date Job");
			try {
				GetResponse<Event> response = (GetResponse<Event>) eventService.getEvents(params);
				for (Event event : response.getList()) {
					event.setRecurrenceEndDate(new DateMidnight().plusMonths(2).toDate());
					event.setUpdatedDate(DateTime.now().toDate());
					eventService.updateEvent(event, authService.loginAsInternal());
				}
			} finally {
				ExtendRecurEndDateForNever.lock.unlock();
			}
		} else {
			log.warn("Could not acquire lock...Job is already running.");
			throw new ClicktableException("Could not acquire lock...Job is already running.");
		}
	}
}
*/