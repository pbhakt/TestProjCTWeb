package com.clicktable.service.intf;

import java.util.Map;

import play.libs.F.Promise;

import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface ReportService {

	public BaseResponse getReservationsReport(String token, Map<String, Object> stringParamMap);
	
	public BaseResponse getCalenderEventsReport(Map<String, Object> params, String token);

	public BaseResponse getCalenderEventAttendenceReport(Map<String, Object> params, String token);
	
	public Promise<BaseResponse> getEventPromotionReport(String header, Map<String, Object> stringParamMap);

	public BaseResponse getCustomersReport(String header,
			Map<String, Object> stringParamMap);
}
