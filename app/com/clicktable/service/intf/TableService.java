package com.clicktable.service.intf;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.clicktable.model.Table;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface TableService {
	/**
	 * Get table based on search parameters
	 * @param params
	 * @return
	 */
	BaseResponse getTables(Map<String,Object> params,String token);
	
	/**
	 * Update table data
	 * @param table
	 * @return
	 */
	BaseResponse addTable(Table table, String token);

	BaseResponse updateTable(Table table, String token);

	boolean getTablesAvailability(List<String> tableGuids, Date start,
			Date end);

	BaseResponse patchTable(Table table, String header);

	BaseResponse getBlockedTables(Map<String, Object> stringParamMap,
		String header);

	BaseResponse getCurrentTableStatus(Map<String, Object> params, String token);
}
