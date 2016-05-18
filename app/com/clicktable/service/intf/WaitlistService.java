package com.clicktable.service.intf;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.clicktable.model.Reservation;
import com.clicktable.model.Table;
import com.clicktable.model.TableWaitingTime;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.validate.ValidationError;
@org.springframework.stereotype.Service
public interface WaitlistService 
{
    BaseResponse getWaitlistResult(Map<String, Object> stringParamMap, String header);

   
  /*  TableWaitingTime getWaitlistTimeForTable(Map<String, Object> params,
	    List<Reservation> reservationList, Reservation resv);*/

	String isValidRest(UserInfoModel userInfo, Map<String, Object> params,
			List<ValidationError> errorList);

	TableWaitingTime getTableWaitingTime(Table t,
			List<Reservation> reservationList, Map<String, Object> waitParams,String tableGuid);


	Map<String, Object> getApplicableShifts(String restaurantId,
			List<ValidationError> errorList, Date dateObject, Boolean tomorrow);


	Map<String, Object> getTableWaitingList(Map<String, Object> params,
			String token, List<ValidationError> errorList);


	BaseResponse getWaitlistResultForMobile(Map<String, Object> stringParamMap,
			String header);


	
	
}
