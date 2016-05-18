package com.clicktable.service.impl;

import com.clicktable.exception.ClicktableException;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.scheduler.*;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.JobService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.validate.ValidationError;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author p.vishwakarma
 */

@org.springframework.stereotype.Service
public class JobServiceImpl implements JobService {

	@Autowired
	AuthorizationService authService;

	@Autowired
	private CheckDND checkDND;

	@Autowired
	private ReservationStatusUpdateAtShiftEnd resvUpdateAtShiftEnd;

	//@Autowired
	//private ExtendRecurEndDateForNever extRecurEndDateForNever;

	@Autowired
	private BarEntryStatusUpdate barEntryStatusUpdate;

	@Autowired
	private SMSScheduler smsScheduler;

	private static final Logger.ALogger log = Logger.of(JobServiceImpl.class);

	@Override
	public BaseResponse runSchedulerJob(String schedulerName, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<>();
		try {
			runJob(schedulerName);
			response = new PostResponse<>(ResponseCodes.SHEDULER_JOB_RUN_SUCCESSFULLY, "");
		} catch (ClicktableException e) {
			log.debug("Exception in running job", e);
			listOfError.add(new ValidationError(Constants.JOB_NAME, ErrorCodes.JOB_ALREADY_RUNNING));
			response = new ErrorResponse(ResponseCodes.SHEDULER_JOB_RUN_FAILURE, listOfError);
		}
		return response;
	}

	private void runJob(String schedulerName) throws ClicktableException {
		switch (schedulerName) {
		case Constants.SEND_RESERVATION_SMS:
			smsScheduler.runSMSScheduler();
			break;
		case Constants.RESERVATION_STATUS_UPDATE_AT_SHIFT_END:
			resvUpdateAtShiftEnd.runReservationStatusUpdateAtShiftEnd();
			break;
		case Constants.BAR_ENTRY_STATUS_UPDATE:
			barEntryStatusUpdate.runBarEntryStatusUpdate();
			break;
		//case Constants.EXTEND_RECUR_END_DATE_FOR_NEVER:
		//	break;
		case Constants.CHECK_DND:
			checkDND.runDND();
			break;
		}
	}

}
