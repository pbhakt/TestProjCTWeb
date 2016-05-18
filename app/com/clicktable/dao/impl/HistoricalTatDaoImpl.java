package com.clicktable.dao.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.HistoricalTatDao;
import com.clicktable.model.HistoricalTat;
import com.clicktable.model.HistoricalTatResult;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class HistoricalTatDaoImpl extends GraphDBDao<HistoricalTat> implements
		HistoricalTatDao {

	public HistoricalTatDaoImpl() {
		super();
		this.setType(HistoricalTat.class);
	}
	
	
	
	/**
	 * Method to create relationship of a restaurant with historical tat
	 */
	@Override     
	public Long addRestaurantHistoricalTat(HistoricalTat hist, String restaurantGuid) 
	{
		//Long id=0L;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.REST_GUID, restaurantGuid);
		params.put(Constants.GUID, hist.getGuid());

		String query = "MATCH (r:Restaurant {guid:{"+Constants.REST_GUID+"}}),(t:HistoricalTat) WHERE t.guid={"+Constants.GUID+"} \n";
		query = query + "MERGE (r)-[q:"+RelationshipTypes.HISTORICAL_TAT+"{__type__:'RestHasHistoricalTat'}]->(t) Return id(q)";

		return getResultId(executeWriteQuery(query, params));
		
	}



	@Override
	public List<HistoricalTatResult> getHistoricalTat(Map<String, Object> params) 
	{
	    
	    	Integer tat = 0,cover=0;
	   	//boolean isActive = server.getStatus().equals(Constants.ACTIVE_STATUS);
	    	String query = "MATCH (a:Restaurant {guid:{"+Constants.REST_GUID+"}})-[b:"+RelationshipTypes.HISTORICAL_TAT+"]->(c:HistoricalTat)";
	    	query = query + "-[d:"+RelationshipTypes.ON_DAY+"]->(e:DayOfWeek{day:{"+Constants.DAY_OF_THE_WEEK+"}})-[f:"+RelationshipTypes.FOR_COVERS+"";
	    	if(params != null && params.containsKey(Constants.COVERS))
	    	{
	    	    query = query + "{cover:toInt({"+Constants.COVERS+"})}";
	    	}
	    	query = query + "]->(g:NumberOfCovers)-[h:"+RelationshipTypes.TAT_VALUE+" {type:{"+Constants.SHIFT+"}}]->(i:CalculatedTat) Return i.value,f.cover";
	    	 Logger.debug("query is "+query);

	    	 Iterator<Map<String, Object>> result = template.query(query, params).iterator();
	        List<HistoricalTatResult> histTatResultList = new ArrayList<HistoricalTatResult>();
	        HistoricalTatResult histTatResult ;
	        while(result.hasNext())
		 {
		     Map<String, Object> map = result.next();
		     Logger.debug("map is "+map);
		     tat = Integer.valueOf( map.get("i.value").toString());
		     Logger.debug("tat value is "+tat);
		     cover = Integer.valueOf( map.get("f.cover").toString());
		     Logger.debug("cover value is "+cover);
		     histTatResult = new HistoricalTatResult();
		     histTatResult.setCovers(cover);
		     histTatResult.setHistoricalTat(tat);
		     histTatResultList.add(histTatResult);
			    
		 }
	        
	        return histTatResultList;
	    
	} 
	    
	@Override
	public Map<Object,Object> getHistoricalTatStats(Map<String, Object> params) 
	{
	    
	    	//Integer tat = 0,cover=0;
	    	Map<Object, Object> histTatMap = new HashMap<Object, Object>();
	    	StringBuilder query = new StringBuilder("MATCH ");
	    	if(params.containsKey(Constants.REST_GUID))
	    		query.append("(a:Restaurant {guid: {"+Constants.REST_GUID+"}})-[rel*]->");
	    	query.append("(g:NumberOfCovers) -[]->(t:CalculatedTat) RETURN DISTINCT g.covers,avg(DISTINCT t.value)");
	    		    

	    	Iterator<Map<String, Object>> result = template.query(query.toString(), params).iterator();
	    	//List<HistoricalTatResult> histTatResultList = new ArrayList<HistoricalTatResult>();
	    	//HistoricalTatResult histTatResult ;
	    	while(result.hasNext())
	    	{
	    		Map<String, Object> map = result.next();
	    		histTatMap.put(map.get("g.covers"),map.get("avg(DISTINCT t.value)"));
	    		/*Logger.debug("map is "+map);
	    		tat = Integer.valueOf( map.get("i.value").toString());
	    		Logger.debug("tat value is "+tat);
	    		cover = Integer.valueOf( map.get("f.cover").toString());
	    		Logger.debug("cover value is "+cover);
	    		/*histTatResult = new HistoricalTatResult();
	    		histTatResult.setCovers(cover);
	    		histTatResult.setHistoricalTat(tat);
	    		histTatResultList.add(histTatResult);*/

	    	}
	        
	       
			return histTatMap;
	    
	} 
	
	
	
	@Override
	public Map<Integer , Long> getHistoricalTatMap(Map<String, Object> params) 
	{

		Integer tat = 0,cover=0;
		String query = "MATCH (a:Restaurant {guid:{"+Constants.REST_GUID+"}})-[b:"+RelationshipTypes.HISTORICAL_TAT+"]->(c:HistoricalTat)";
		query = query + "-[d:"+RelationshipTypes.ON_DAY+"]->(e:DayOfWeek{day:{"+Constants.DAY_OF_THE_WEEK+"}})-[f:"+RelationshipTypes.FOR_COVERS+"";
		if(params != null && params.containsKey(Constants.COVERS))
		{
			query = query + "{cover:toInt({"+Constants.COVERS+"})}";
		}
		query = query + "]->(g:NumberOfCovers)-[h:"+RelationshipTypes.TAT_VALUE+" {type:{"+Constants.SHIFT+"}}]->(i:CalculatedTat) Return i.value,f.cover";
		Logger.debug("query is "+query);

		Iterator<Map<String, Object>> result = template.query(query, params).iterator();
		Map<Integer,Long> tatMap = new HashMap<>();
		while(result.hasNext())
		{
			Map<String, Object> map = result.next();
			tat = Integer.valueOf( map.get("i.value").toString());
			cover = Integer.valueOf( map.get("f.cover").toString());
			tatMap.put(cover, tat*60*1000l);

		}
		return tatMap;

	} 

}
