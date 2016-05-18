package com.clicktable.dao.impl;


import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.NumberOfCoversDao;
import com.clicktable.model.DayOfWeek;
import com.clicktable.model.NumberOfCovers;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class NumberOfCoversDaoImpl extends GraphDBDao<NumberOfCovers> implements
		NumberOfCoversDao {

	public NumberOfCoversDaoImpl() {
		super();
		this.setType(NumberOfCovers.class);
	}
	
	
	
	/**
	 * Method to create relationship of a restaurant with historical tat
	 */
	@Override     
	public Long addForNumberOfCovers(NumberOfCovers cover, DayOfWeek dayOfWeek, Integer tatValue) 
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, cover.getGuid());
		params.put(Constants.DAY_OF_THE_WEEK, dayOfWeek.getGuid());
		params.put(Constants.NUM_COVERS, cover.getCovers());
		
		String query = "MATCH (c:NumberOfCovers{guid:{"+Constants.GUID+"}}),(d:DayOfWeek) WHERE d.guid={"+Constants.DAY_OF_THE_WEEK+"} \n";
		query = query + "MERGE (d)-[q:"+RelationshipTypes.FOR_COVERS+"{__type__:'ForCovers',cover:{"+Constants.NUM_COVERS+"}}]->(c) Return id(q)";

		return getResultId(executeWriteQuery(query, params));		
	} 
	
	
	
	@Override
	public StringBuilder getMatchClause(Map<String, Object> params) 
	{
	    StringBuilder query = new StringBuilder();
	    query.append("MATCH (d:DayOfWeek{guid:{"+ Constants.GUID+"}})-[q:"+RelationshipTypes.FOR_COVERS+"]->(t:NumberOfCovers) ");
	    return query;
	}
	




}
