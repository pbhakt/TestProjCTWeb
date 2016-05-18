package com.clicktable.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.QuickSearchDao;
import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Table;
import com.clicktable.util.Constants;

/**
 * 
 * @author p.singh
 *
 */
@Service
public class QuickSearchDaoImpl implements QuickSearchDao {
//extends GraphDBDao<Table> implements QuickSearchDao {

	@Autowired
	Neo4jTemplate template;
	
	
	
	@Override
	public List<Table> getAllTables(Map<String, Object> params)
	{
		StringBuilder query=new StringBuilder();
		boolean relHasExtraProperties=false;
		//params.put(Constants.REST_ID, restaurantId);
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		query.append("MATCH (r:Restaurant{guid:{restId}})-[rht:REST_HAS_TBL]->(t:`Table`) WHERE t.status={status} ");
		
		if(null!=params && params.containsKey(Constants.COVERS))
		{
			query.append(" AND ");
			query.append(" toInt(t.min_covers)<= toInt({covers}) AND" );
			query.append(" toInt(t.max_covers)>= toInt({covers}) AND" );
			relHasExtraProperties=true;
		}
		if(relHasExtraProperties)
		{
	     	query = new StringBuilder(query.substring(0, query.length() - 3));
		}	
		query.append( "RETURN t ORDER BY t.max_covers,t.min_covers,t.name");
		Result<Map<String, Object>> result = template.query(query.toString(),
				params);
		Iterator<Map<String, Object>> i = result.iterator();
		Table table = null;
		List<Table> tables=new ArrayList<Table>();
		while (i.hasNext()) 
		{
			Map<String, Object> map = i.next();
			for (Map.Entry<String, Object> entry : map.entrySet()) 
			{
				template.postEntityCreation((Node) entry.getValue(),Table.class);
				table = template.convert(entry.getValue(), Table.class);
				table.setTableStatus(Constants.AVAILABLE);
				tables.add(table);
			}
		}
		//Logger.debug("all tables in dao impl is "+tables);
		return tables;
	}
	
	
	@Override
	public Map<String, Table> getTablesHavingReservation(long startDate,long endDate,String restID,Map<String, Object> params, Map<String, Table> allTables , List<String> tableGuidList)
	{
	    Map<String,Object> queryParam = new HashMap<>();
	    queryParam.put("TableGuidList", tableGuidList);
	    queryParam.put("startDate", startDate);
	    queryParam.put("endDate", endDate);
	    queryParam.put("restID", restID);
	    if(null!=params && params.containsKey(Constants.COVERS))
	    {
		queryParam.put("covers", params.get(Constants.COVERS));
	    }
		StringBuilder query=new StringBuilder();
		query.append(" MATCH (table:`Table`{rest_id:{restID}})-[rel:`TBL_HAS_RESV`"
				+ "]->(t:`Reservation`)"
				+ "  WHERE table.guid IN {TableGuidList}"
				+ "  AND t.reservation_status<>'FINISHED' AND t.reservation_status<>'CANCELLED' AND t.reservation_status<>'NO_SHOW' "
				+ "  AND ( ( toInt(rel.endDate)>toInt({startDate}) AND  toInt(rel.startDate)<toInt({startDate})  )"
				+ "  OR  ( toInt(rel.endDate)>toInt({endDate}) AND  toInt(rel.startDate)<toInt({endDate})  ) "
				+ "  OR (toInt(t.est_start_time) > toInt({startDate}) AND toInt(t.est_end_time)=toInt({endDate})) "
				+ "  OR (toInt(t.est_start_time) = toInt({startDate}) AND toInt(t.est_end_time)<toInt({endDate})) "
				+ "  OR  ( toInt(rel.startDate)>toInt({startDate}) AND  toInt(rel.endDate)<toInt({endDate})  ) "
				+ "  OR  ( toInt(rel.startDate)=toInt({startDate}) AND  toInt(rel.endDate)=toInt({endDate})  ) )"
				+ "  AND");
		
		
		
		
		
		if(null!=params && params.containsKey(Constants.COVERS))
		{
			query.append( " toInt(table.min_covers)<= toInt({covers}) AND");
			query.append(" toInt(table.max_covers)>= toInt({covers}) AND");
		}
	     	query = new StringBuilder(query.substring(0, query.length() - 3));
				
			query.append( " RETURN DISTINCT table ");
		
		//System.out.println("Query" + query.toString());
		//SimpleDateFormat format = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		//String date = format.format(endDate);
		//System.out.println("Date  "+date);
		Result<Map<String, Object>> result = template.query(query.toString(),
				queryParam);
		Iterator<Map<String, Object>> i = result.iterator();
		Table table = null;
		while (i.hasNext()) 
		{
			Map<String, Object> map = i.next();
			for (Map.Entry<String, Object> entry : map.entrySet()) 
			{
				template.postEntityCreation((Node) entry.getValue(),
						Table.class);
				table = template.convert(entry.getValue(), Table.class);
				/*if(allTables.containsKey(table.getGuid()))
				{*/
				    allTables.get(table.getGuid()).setTableStatus(Constants.ALLOCATED);
				//}
			}
		}
		
		return allTables;
	}
	
	
	/* Get cal events of type HOLIDAY,BLOCK,FULL_BLOCK */
	
	/*@Override
	public Map<String,Table> getCalendarEvents(String restGuid,long startTime,long endTime, Map<String, Table> allTables)
	{
	    Map<String, Object> queryMap = new HashMap<>();
	    queryMap.put(Constants.REST_GUID, restGuid);
	    queryMap.put(Constants.START_TIME, startTime);
	    queryMap.put(Constants.END_TIME, endTime);
	    
	    StringBuilder query=new StringBuilder();
	    
	    a = (toInt(calEvent.event_dt) + toInt(calEvent.start_time))
	     b = (toInt(calEvent.event_dt) + toInt(calEvent.end_time))
	    
	    c = toInt({"+Constants.START_TIME+"})
	    
	    d = toInt({"+Constants.END_TIME+"})
	    
	    
	    
	    query.append("MATCH (rest:Restaurant)-[REST_HAS_CAL]->(calEvent:CalenderEvent) WHERE rest.guid={"+Constants.REST_GUID+"} "
			+ " AND  calEvent.category IN ['BLOCK','FULL_BLOCK'] AND calEvent.type='EVENT' "
			+" AND ("
			+ "((toInt(calEvent.event_dt) + toInt(calEvent.start_time)) > toInt({"+Constants.START_TIME+"}) AND (toInt(calEvent.event_dt) + toInt(calEvent.start_time)) < toInt({"+Constants.END_TIME+"})) "
			+ "OR ((toInt(calEvent.event_dt) + toInt(calEvent.end_time)) > toInt({"+Constants.START_TIME+"}) AND (toInt(calEvent.event_dt) + toInt(calEvent.end_time)) < toInt({"+Constants.END_TIME+"})) "
			+ "OR (toInt({"+Constants.START_TIME+"}) >= (toInt(calEvent.event_dt) + toInt(calEvent.start_time)) AND toInt({"+Constants.END_TIME+"}) <= (toInt(calEvent.event_dt) + toInt(calEvent.end_time)))"
			+ ") "
			+ " RETURN  DISTINCT calEvent");
		
	    
	    
	    (( <=toInt({"+Constants.START_TIME+"})) AND ( >=toInt({"+Constants.START_TIME+"}))) "
		+" OR (((toInt(calEvent.event_dt) + toInt(calEvent.start_time)) <=toInt({"+Constants.END_TIME+"})) AND ((toInt(calEvent.event_dt) + toInt(calEvent.end_time)) >=toInt({"+Constants.END_TIME+"})))"
			+ ""
			+ "
	    //SimpleDateFormat format = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
	    //String date = format.format(endTime);
	    Result<Map<String, Object>> result = template.query(query.toString(),queryMap);
	    Iterator<Map<String, Object>> i = result.iterator();
	    CalenderEvent calEvent = null;
	    
	    while (i.hasNext()) 
	    {
		Map<String, Object> map = i.next();
		for (Map.Entry<String, Object> entry : map.entrySet()) 
		{
		    template.postEntityCreation((Node) entry.getValue(),CalenderEvent.class);
		    calEvent = template.convert(entry.getValue(), CalenderEvent.class);
		    if(calEvent.getCategory() != null && calEvent.getCategory().equals(Constants.BLOCK))
		    {
			if(calEvent.getBlockingArea() != null)
			{
			    for(String tableGuid : calEvent.getBlockingArea())
			    {
				if(allTables.containsKey(tableGuid))
				{
				    allTables.get(tableGuid).setTableStatus(Constants.BLOCKED);
				}
			    }
			}
		    }
		    else if(calEvent.getCategory() != null && calEvent.getCategory().equals(Constants.HOLIDAY))
		    {
			allTables = null;
			break;
		    }
		    else if(calEvent.getCategory() != null && calEvent.getCategory().equals(Constants.FULL_BLOCK))
		    {
			for (Map.Entry<String,Table> entryTbl : allTables.entrySet()){
			    entryTbl.getValue().setTableStatus(Constants.BLOCKED);
			}
			break;
		    }
			
		}
	    }
	    
	    return allTables;
		
	}
	*/
	
	
	/* Get cal events of type HOLIDAY*/
	
	@Override
	public Boolean getHoliday(String restGuid,long startTime)
	{
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put(Constants.REST_GUID, restGuid);
		queryMap.put(Constants.START_TIME, startTime);

		StringBuilder query=new StringBuilder();

		/*query.append("MATCH (rest:Restaurant)-[REST_HAS_CAL]->(calEvent:CalenderEvent) WHERE rest.guid={"+Constants.REST_GUID+"} "
				+ " AND  calEvent.category='HOLIDAY' AND calEvent.type='EVENT' AND  calEvent.status='ACTIVE' "
				+" AND toInt(calEvent.event_dt)=toInt({"+Constants.START_TIME+"})  RETURN  calEvent");*/
		
		query.append("MATCH (rest:Restaurant)-[HAS_EVENT]-(e:Event{status:'ACTIVE'})-[HAS_CAL_EVENT]->(calEvent:CalenderEvent) WHERE rest.guid={"+Constants.REST_GUID+"} "
				+ " AND  calEvent.type='HOLIDAY' AND  calEvent.status='ACTIVE' "
				+" AND toInt(calEvent.start_time)<=toInt({"+Constants.START_TIME+"}) AND toInt(calEvent.end_time)>=toInt({"+Constants.START_TIME+"})  RETURN  calEvent");
		
		
		Result<Map<String, Object>> result = template.query(query.toString(),queryMap);
		Iterator<Map<String, Object>> i = result.iterator();
		Boolean isHoliday = false;
		while (i.hasNext()) 
		{
			isHoliday = true;
			i.next();
		}

		return isHoliday;

	}	
}
