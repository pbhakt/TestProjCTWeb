package com.clicktable.controllers;

import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.service.intf.JobService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.validate.ValidationError;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

@org.springframework.stereotype.Controller
public class JobController extends Controller {

	@Autowired
	JobService manualschedulerService;

	public Result runSchedulers() {
		JsonNode json = request().body().asJson();
		String token = request().getHeader(ACCESS_TOKEN);
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<>();
		if (json.has("jobName")) {
			String jobName = json.get("jobName").asText();
			response = manualschedulerService.runSchedulerJob(jobName, token);
		} else {
			listOfError.add(new ValidationError(Constants.JOB_NAME, ErrorCodes.SCHEDULER_NAME_IS_REQUIRED));
			response = new ErrorResponse(ResponseCodes.SHEDULER_JOB_RUN_FAILURE, listOfError);
		}
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
}
