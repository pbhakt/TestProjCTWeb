package com.clicktable.service.intf;

import com.clicktable.response.BaseResponse;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface JobService {

	BaseResponse runSchedulerJob(String schedulerName, String token);
}
