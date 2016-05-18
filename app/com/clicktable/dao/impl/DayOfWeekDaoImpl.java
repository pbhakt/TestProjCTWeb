package com.clicktable.dao.impl;


import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.DayOfWeekDao;
import com.clicktable.model.DayOfWeek;
import com.clicktable.model.HistoricalTat;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class DayOfWeekDaoImpl extends GraphDBDao<DayOfWeek> implements
		DayOfWeekDao {

	public DayOfWeekDaoImpl() {
		super();
		this.setType(DayOfWeek.class);
	}
	
	
	
	/**
	 * Method to create relationship of a restaurant with historical tat
	 */
	@Override     
	public Long addDayOfWeek(HistoricalTat hist, String daysOfWeek) 
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, hist.getGuid());
		String [] guidArr = daysOfWeek.split(",");
		params.put(Constants.DAY_OF_THE_WEEK, guidArr);

		String query = "MATCH (t:HistoricalTat {guid:{"+Constants.GUID+"}}),(r:DayOfWeek) WHERE r.guid IN { "+Constants.DAY_OF_THE_WEEK+" }";
		query = query + " MERGE (t)-[q:"+RelationshipTypes.ON_DAY+"{__type__:'OnDay'}]->(r) Return id(q)";

		return getResultId(executeWriteQuery(query, params));
	} 
	
	@Override
	public StringBuilder getMatchClause(Map<String, Object> params) 
	{
	    StringBuilder query = new StringBuilder();
	    query.append(" MATCH (rest:Restaurant{guid:{restaurantGuid}})-[r:HISTORICAL_TAT]->(ht)-[d:ON_DAY]->(t:DayOfWeek) ");
	    return query;
	}
	    


}
