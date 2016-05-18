package com.clicktable.scheduler;

import com.clicktable.exception.ClicktableException;
import com.clicktable.model.BarEntry;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.BarEntryService;
import com.clicktable.util.Constants;
import com.clicktable.util.ResponseCodes;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import play.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class BarEntryStatusUpdate implements Runnable {

	public BarEntryStatusUpdate() {
	}

	@Autowired
	BarEntryService barService;
	private static Lock lock = new ReentrantLock();

	@Autowired
	AuthorizationService authService;

	private static Logger.ALogger log = Logger.of(BarEntryStatusUpdate.class);

	public BarEntryStatusUpdate(BarEntryService barService, AuthorizationService authService) {
		this.barService = barService;
		this.authService = authService;
	}

	@Override
	public void run() {
		log.info("Started Scheduler: Bar entry status update");
		runBarEntryStatusUpdate();
		log.info("Finished Scheduler: Bar entry status update");
	}

	/**
	 * Updates bar entry status
	 */
	public void runBarEntryStatusUpdate() {
		if (BarEntryStatusUpdate.lock.tryLock()) {
			Logger.info("Started job BarEntryStatusUpdate");
			DateTime currentTime = DateTime.now();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.STATUS, Constants.CREATED);
			params.put(Constants.END_TIME_BEFORE, currentTime.toDate());
			try {
				BaseResponse response = barService.getBarEntry(params);
				if (response.getResponseCode().equals(ResponseCodes.BARENTRY_FETCHED_SUCCESSFULLY)) {
					GetResponse<BarEntry> getResponse = (GetResponse<BarEntry>) response;
					List<BarEntry> barList = getResponse.getList();
					for (BarEntry barEntry : barList) {
						Map<String, Object> barEntryMap = new HashMap<String, Object>();
						barEntryMap.put(Constants.GUID, barEntry.getGuid());
						barEntryMap.put(Constants.STATUS, Constants.FINISHED);
						barService.updateBarEntry(barEntryMap, authService.loginAsInternal());
					}
				}
			} finally {
				BarEntryStatusUpdate.lock.unlock();
			}
		} else {
			log.warn("Could not acquire lock...Job is already running.");
			throw new ClicktableException("Could not acquire lock...Job is already running.");
		}
	}

}
