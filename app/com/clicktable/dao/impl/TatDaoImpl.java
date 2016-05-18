package com.clicktable.dao.impl;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.TatDao;
import com.clicktable.model.Tat;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class TatDaoImpl extends GraphDBDao<Tat> implements
		TatDao {

	public TatDaoImpl() {
		super();
		this.setType(Tat.class);
	}		
	
	@Override
	protected StringBuilder getMatchClause(Map<String, Object> params)
	{
	       
	       if (params.containsKey(Constants.REST_GUID)) 
		   return new StringBuilder("MATCH (r:Restaurant)-[:REST_HAS_TAT]->(t:Tat)");
		
		else
		   return super.getMatchClause(params);
		

	}
	  
	    @Override
	    protected StringBuilder getWhereClause(Map<String, Object> params) 
	    {
			StringBuilder query = super.getWhereClause(params);
			
			Logger.debug("query after super where clause is ");
			
			if (params.containsKey(Constants.REST_GUID)) 
			{
				addPrefix(query);
				query.append("r.guid= {" + Constants.REST_GUID+ "} ");
			}			
		   Logger.debug("final query in where clause is "+query);
		   
		return query;
	    }
	    
	    @Override
	    public int get_tat_value(String rest_guid, int num_covers, String day){
	    	StringBuffer query=new StringBuffer();
	    	Map<String , Object> params = new HashMap<>();
	    	params.put(Constants.REST_GUID, rest_guid);
	    	params.put(Constants.NUM_COVERS, num_covers);
	    	params.put(Constants.DAY_NAME, day);
	    	int  tat_value=0;
	    	if(num_covers>8){
	    		
	    		num_covers=9;
	    	}
	    	query.append("MATCH (tat:Tat)<-[rel:"+RelationshipTypes.REST_HAS_TAT+"]-(restaurant:`Restaurant`)"
	    			+ " WHERE restaurant.guid={"+Constants.REST_GUID+"} AND toInt(tat.max_covers)>=toInt({"+ Constants.NUM_COVERS +"}) AND"
	    			+ " toInt(tat.min_covers)<=toInt({"+ Constants.NUM_COVERS +"}) AND tat.day={"+Constants.DAY_NAME+"} RETURN DISTINCT rel.value as tat_value ");
	    	
	    	
	    	///System.out.println(" Query-----"+query.toString());
	    	Map<String, Object> map = new HashMap<String, Object>();

			Result<Map<String, Object>> results = executeWriteQuery(query.toString(),
				params);
			Iterator<Map<String, Object>> i = results.iterator();	
			while (i.hasNext()) {
			        map = i.next();
			        tat_value=Integer.parseInt(map.get("tat_value").toString());
			}   	
	    	
	    	return tat_value;
	    }

		
		
	   


}
