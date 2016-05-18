package com.clicktable.dao.impl;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.CalculatedTatDao;
import com.clicktable.model.CalculatedTat;
import com.clicktable.model.NumberOfCovers;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class CalculatedTatDaoImpl extends GraphDBDao<CalculatedTat> implements
		CalculatedTatDao {

	public CalculatedTatDaoImpl() {
		super();
		this.setType(CalculatedTat.class);
	}
  
      
      /**
       * Method to create relationship of cover with calculated Tat 
       */
    @Override     
    public void addTatValue(NumberOfCovers cover, List<CalculatedTat> calTatList, int tatValue) 
 	{
	         Long id=0L;
	    	Map<String, Object> params = new HashMap<String, Object>();
	    	params.put(Constants.COVER_GUID, cover.getGuid());
	    	
 	    	int count = 0;
 	    	for(CalculatedTat calTat : calTatList)
 	    	{
 	    		params.put(Constants.CAL_TAT_GUID, calTat.getGuid());
 	    	    String query = "MATCH (c:NumberOfCovers{guid:{"+Constants.COVER_GUID+"}}),(ct:CalculatedTat) WHERE ct.guid={"+Constants.CAL_TAT_GUID+"} ";
 	    	    query = query + "MERGE (c)-[q:"+RelationshipTypes.TAT_VALUE+"{__type__:'TatValue',";
 	    	    if(count == 0)
 	    	    {
 	    		query = query	+ "type:'LUNCH'}";
 	    	    }
 	    	    if(count == 1)
 	    	    {
 	    		query = query	+ "type:'DINNER'}";  
 	    	    }
 	    	
 	    	    query = query +"]->(ct) Return id(q)";
 	    	    count++;
 	    	
 	    	    Logger.debug("query is "+query);

				Iterator<Map<String, Object>> result = executeWriteQuery(query, params).iterator();
 	    	    while(result.hasNext())
 	    	    {
 		     Map<String, Object> map = result.next();
 		     Logger.debug("map is "+map);
 		     id = Long.valueOf( map.get("id(q)").toString());
 		     Logger.debug("id is "+id);
 			    
 	    	    }
 	    	
 	    	}
 	    	
 	}
    
    
    	@Override
	public StringBuilder getMatchClause(Map<String, Object> params) 
	{
	    StringBuilder query = new StringBuilder();
	    query.append("MATCH (c:NumberOfCovers{guid:{"+Constants.GUID+"}})-[q:"+RelationshipTypes.TAT_VALUE+"]-(t:CalculatedTat) ");
	    return query;
	}
    	




}
