

/**
 * 
 */
package com.clicktable.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.TableShuffleDao;
import com.clicktable.model.Reservation;
import com.clicktable.model.Table;
import com.clicktable.util.Constants;

/**
 * 
 * @author p.singh
 *
 */
@Service
public class TableShuffleDaoImpl implements TableShuffleDao{ //extends
		//GraphDBDao<Reservation> implements TableShuffleDao {
    
   @Autowired
   Neo4jTemplate template;
    
    
    
    
    @Override
    public List<Reservation> getTablesHavingReservation( Map<String, Object> params)
    {
    	StringBuilder query=new StringBuilder();
    	query.append("MATCH (t:`Reservation` {rest_guid : {restaurantId}})"
    			+ " WHERE (NOT t.reservation_status IN ['FINISHED', 'CANCELLED', 'NO_SHOW']");
    	if(params.containsKey("currentShiftEnd"))
    	{
    			query.append( " AND (toInt(t.est_start_time)>toInt({startTime}) OR "
    			+ "(toInt(t.est_start_time) <= toInt({startTime}) AND toInt(t.est_end_time) >= toInt({startTime}))) "
    			+ "AND (toInt(t.est_end_time) <= toInt({currentShiftEnd}))"
    			+ " AND t.booking_mode = 'ONLINE') OR t.reservation_status='SEATED' \n");
    	}
    	else
    	{
    		/*for get tables API*/
    		query.append( " AND (t.booking_mode = 'ONLINE' AND toInt(t.est_start_time) <= toInt({startTime}) AND toInt(t.est_end_time) >= toInt({startTime}))) "
        			+ " OR t.reservation_status='SEATED' \n");
    		
    	}
    	query.append(" RETURN DISTINCT t ORDER BY toInt(t.created_dt)");

    	Result<Map<String, Object>> result = template.query(query.toString(),params);
    	Iterator<Map<String, Object>> i = result.iterator();
    	Reservation reservation = null;
    	List<Reservation> resvList = new ArrayList<>();
    	while (i.hasNext()) 
    	{
    		Map<String, Object> map = i.next();
    		reservation = template.convert(map.get("t"), Reservation.class);
    		reservation.setIsUpdated(true);
    		resvList.add(reservation);
    	}
    	return resvList;
    }
    

	@Override
	public Map<String, List<Reservation>> getBlockedTables(Map<String, Object> queryMap, Map<String, List<Reservation>> blockListMap)
	{
	    StringBuilder query=new StringBuilder();
	    
	    //according to old code for calender events

	 /*  if(queryMap.containsKey("nextDateTime") && queryMap.containsKey("currentShiftEndTime"))
	   {
		   query.append("MATCH (rest:Restaurant{guid:{"+Constants.REST_GUID+"}})-[REST_HAS_CAL]->(calEvent:CalenderEvent) WHERE  calEvent.status='ACTIVE' "
				   + " AND  calEvent.category='BLOCK' AND calEvent.type='EVENT' "
				   + " AND ((toInt(calEvent.event_dt) = toInt({currentDateTime}) AND ((toInt(calEvent.event_dt) + toInt(calEvent.end_time) + 330*60*1000) > toInt({currentTime}))) "
				   + "OR (toInt(calEvent.event_dt) = toInt({nextDateTime}) AND ((toInt(calEvent.event_dt) + toInt(calEvent.start_time) + 330*60*1000) < toInt({currentShiftEndTime})))) "
				   + " RETURN calEvent.blocking_area as tables, (toInt(calEvent.event_dt) + toInt(calEvent.start_time) + 330*60*1000) AS startTime, (toInt(calEvent.event_dt) + toInt(calEvent.end_time) + 330*60*1000) AS endTime, calEvent.sub_category AS reason");
	   }
	   else
	   {
		   for get tables API
		    query.append("MATCH (rest:Restaurant{guid:{"+Constants.REST_GUID+"}})-[REST_HAS_CAL]->(calEvent:CalenderEvent) WHERE  calEvent.status='ACTIVE' "
				+ " AND  calEvent.category='BLOCK' AND calEvent.type='EVENT' "
				+ " AND ((toInt(calEvent.event_dt) + toInt(calEvent.start_time) + 330*60*1000) <= toInt({currentTime})) AND ((toInt(calEvent.event_dt) + toInt(calEvent.end_time) + 330*60*1000) > toInt({currentTime})) "
				+ " RETURN calEvent.blocking_area as tables, (toInt(calEvent.event_dt) + toInt(calEvent.start_time) + 330*60*1000) AS startTime, (toInt(calEvent.event_dt) + toInt(calEvent.end_time) + 330*60*1000) AS endTime,  calEvent.sub_category AS reason");
		   
		   
	   }*/
	    
	    
	    //according to new code for calender event
	    
	    if(queryMap.containsKey("nextDateTime") && queryMap.containsKey("currentShiftEndTime"))
		   {
			   query.append("MATCH (rest:Restaurant{guid:{"+Constants.REST_GUID+"}})-[REST_HAS_CAL]->(calEvent:CalenderEvent) WHERE  calEvent.status='ACTIVE' "
					   + " AND  calEvent.type='BLOCK' "
					   + " AND (toInt(calEvent.start_time)>toInt({currentTime}) OR "
					   + "(toInt(calEvent.start_time) <= toInt({currentTime}) AND toInt(calEvent.end_time) >= toInt({currentTime}))) AND (toInt(calEvent.end_time) <= toInt({currentShiftEndTime}))"
					   + " RETURN calEvent.blocking_area as tables, toInt(calEvent.start_time) AS startTime,toInt(calEvent.end_time) AS endTime, calEvent.sub_category AS reason");
		   }
		   else
		   {
			   /*for get tables API*/
			    query.append("MATCH (rest:Restaurant{guid:{"+Constants.REST_GUID+"}})-[REST_HAS_CAL]->(calEvent:CalenderEvent) WHERE  calEvent.status='ACTIVE' "
					+ " AND  calEvent.type='BLOCK' "
					+ " AND (( toInt(calEvent.start_time)) <= toInt({currentTime})) AND ((toInt(calEvent.end_time)) > toInt({currentTime})) "
					+ " RETURN calEvent.blocking_area as tables,  toInt(calEvent.start_time) AS startTime,  toInt(calEvent.end_time) AS endTime,  calEvent.sub_category AS reason");
			   
			   
		   }
	    
	    
	    Result<Map<String, Object>> result = template.query(query.toString(),queryMap);
	    Iterator<Map<String, Object>> i = result.iterator();

	    Long startTime = 0l;
	    Long endTime = 0l;
	    String blockReason = "";
	   
	    while (i.hasNext()) 
	    {
	    	Map<String, Object> map = i.next();
	    	List<String> blockArea = (List<String>) map.get("tables");
	    	startTime = (Long) map.get("startTime");
	    	endTime = (Long) map.get("endTime");
	    	blockReason = map.get("reason") == null ? null : map.get("reason").toString();
	    	for(String tableGuid : blockArea)
	    	{
	    		int blockTat = (int) ((endTime - startTime)/(60*1000));
	    		Reservation blockResv = new Reservation(startTime, endTime,null,null,String.valueOf(blockTat), "BLOCK", blockReason);
	    		List<Reservation> blockTblList =  new ArrayList<>();
	    		if(blockResv != null){
	    			if(blockListMap.containsKey(tableGuid))
	    			{
	    				blockTblList = blockListMap.get(tableGuid);
	    			}
	    			blockTblList.add(blockResv);
	    			blockListMap.put(tableGuid, blockTblList);
	    		}
	    	}

	    }
	    
	    return blockListMap;
	}
	
	
	
	
    	@Override
		public Map<String,Table> getAllTables(Map<String, Object> params)
		{
			StringBuilder query=new StringBuilder();
			query.append("MATCH (r:Restaurant{guid:{restId}})-[rht:REST_HAS_TBL]->(t:`Table`) WHERE t.status={status} ");

			query.append( "RETURN t ");
			Result<Map<String, Object>> result = template.query(query.toString(),
					params);
			Iterator<Map<String, Object>> i = result.iterator();
			Table table = null;
			Map<String,Table> tableMap = new HashMap<>();
			while (i.hasNext()) 
			{
				Map<String, Object> map = i.next();
				table = template.convert(map.get("t"), Table.class);
				tableMap.put(table.getGuid(), table);
				
			}
			return tableMap;
		}
	

	
	
	
	
	
}


