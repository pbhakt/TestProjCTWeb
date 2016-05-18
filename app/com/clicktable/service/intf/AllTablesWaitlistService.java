package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.response.BaseResponse;
@org.springframework.stereotype.Service
public interface AllTablesWaitlistService 
{
 
  
    BaseResponse getAllTablesWaitlistResult(Map<String, Object> params,
	    String token);

    /*BaseResponse getAllTablesToConvertResvToWalkin(Map<String, Object> params,
	    String token);*/

}
