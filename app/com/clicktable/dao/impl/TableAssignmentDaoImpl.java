package com.clicktable.dao.impl;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.TableAssignmentDao;
import com.clicktable.model.TableAssignment;
import com.clicktable.util.Constants;

@Service
public class TableAssignmentDaoImpl extends GraphDBDao<TableAssignment> implements
		TableAssignmentDao {

	public TableAssignmentDaoImpl() 
	{
		super();
		this.setType(TableAssignment.class);
	}
	
	
	@Override
	protected StringBuilder getMatchClause(Map<String, Object> params)
	{
		if (params.containsKey(Constants.REST_GUID)) 
		{
		return new StringBuilder("MATCH (r:Restaurant {"+Constants.GUID+" :{" + Constants.REST_GUID+ "}})-[:REST_HAS_TBL]->(t:Table) WITH t MATCH (t)-[a:TBL_HAS_SERVER]->(s:Server)");
		} 
		else
		{
			return super.getMatchClause(params);
		}

	}
	
	
	
	/**
	 * Method to assign a server to multiple tables
	 */
      
      @Override
      public boolean assignTablesToServer(TableAssignment assignTable, String[] tableGuidArr) 
	{
	  
	  /**
	   * TO DO: to do task of unassignment and assignment in a single query
	   */
	  
	  Boolean isAssigned = false;
	  String starTime = assignTable.getStartTime();
	  String endTime = assignTable.getEndTime();
	  String dateStr = assignTable.getDate();
	  
	  SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
	  SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
	  
	  Long start_time = 0L , end_time = 0L, date = 0L;
	  try
	  {
	    start_time = dateTimeFormat.parse(starTime).getTime();
	    end_time = dateTimeFormat.parse(endTime).getTime();
	    date = dateFormat.parse(dateStr).getTime();
	  }
	  catch (ParseException e) 
	  {
	    e.printStackTrace();
	  }
	  
	  try
	  {
	  //calling method to unassign all tables who are to be assigned
	  Boolean isDeleted = unassignAllTables( tableGuidArr);
	  Logger.debug("relationship deleted"+isDeleted);
	  
	  
	  //code to assign a server to a table
	    	String query = "MATCH (s:Server {guid:'"+assignTable.getServerGuid()+"'}),(t:Table) WHERE";
		for(String tableGuid : tableGuidArr)
		{
		    query = query + " t.guid='"+tableGuid+"' OR";
		}
		if(query.contains("OR"))
		{
		    query = query.substring(0,query.length()-2) + "\n";
		}
		else
		{
		    query = query.substring(0,query.length()-5) + "\n";
		}
		query = query + "MERGE (s)<-[:TBL_HAS_SERVER {__type__:'TableHasServer',start_time:"+start_time+",end_time:"+end_time+",date:"+date+"}]-(t)";
	        Logger.debug("query is "+query);
		
	        Result<Map<String, Object>> r = template.query(query, null);
	        Logger.debug("query executed,Result is "+r);
	        isAssigned = true;
	  } 
	  catch(Exception e)
	  {
		  e.printStackTrace();
	      isAssigned = false;
	  }
	        return isAssigned;
	}
      
      /**
       * Method to unassign a server from tables
       */
      @Override
      public boolean unassignTablesToServer(TableAssignment assignTable, String[] tableGuidArr) 
     	{
    	  	Boolean isUnassigned = false;
    	  	Map<String , Object> params = new HashMap<>();
    	  	params.put(Constants.SERVER_GUID, assignTable.getServerGuid());
    	  	params.put(Constants.TABLE_GUID, tableGuidArr);
	  
    	  	try
    	  	{
    	  		String query = "MATCH (s:Server {guid:{"+Constants.SERVER_GUID+"}})<-[r:TBL_HAS_SERVER]-(t:Table) ";
    	  		if(tableGuidArr.length > 0)
    	  		{
    	  			query += " WHERE t.guid IN {"+Constants.TABLE_GUID+" }"; 
    	  		}
    	  		query = query + " DELETE r";
    	  		Logger.debug("query is "+query);
     		
    	  		Result<Map<String, Object>> r = template.query(query, params);
    	  		Logger.debug("query executed,Result is "+r);
    	  		isUnassigned = true;
    	  	}
    	  	catch(Exception e)
    	  	{
    	  		Logger.debug("Exception is --------------" + e.getLocalizedMessage());
    	  		isUnassigned = false;
    	  	}
     	    
    	  	return isUnassigned;
     	}
      
      
      /**
       * Method to unassign all tables who are to be assigned to a server
       */
      @Override
      public boolean unassignAllTables(String[] tableGuidArr) 
     	{
    	  Boolean isUnassigned = false;
    	  Map<String , Object> params = new HashMap<>();
    	  params.put(Constants.TABLE_GUID, tableGuidArr);
    	  try
    	  {
     	  	String query = "MATCH (s:Server)<-[r:TBL_HAS_SERVER]-(t:Table) ";
     	  	if(tableGuidArr.length > 0)
     	  	{
     	  		query += " WHERE t.guid IN {"+Constants.TABLE_GUID+" }"; 
     	  	}
     		
     		query += " DELETE r";
     	    Logger.debug("query is "+query);
     		
     	    Result<Map<String, Object>> r = template.query(query, params);
     	    Logger.debug("query executed,Result is "+r);
            isUnassigned = true;
    	  }
    	  catch(Exception e)
    	  {
    		  Logger.debug("Exception is ------------- " + e.getLocalizedMessage());
    		isUnassigned = false;
    	  }
     	  
    	  return isUnassigned;
     	}
      
      
      
      @Override
      public Iterator<Map<String, Object>>  getTableAssignment(Map<String,Object> params)
       {
	  	String query = " MATCH (a:Restaurant";
	  	if((params.get(Constants.REST_GUID) != null) && (params.get(Constants.REST_GUID).equals("")))
	  	{
	  	    query = query + "{guid:{"+Constants.REST_GUID+"}}";
	  	}
	  	query = query + ")-[r:`REST_HAS_SERVER`]->(b:Server{guid:{"+Constants.SERVER_GUID+"}})<-[q:`TBL_HAS_SERVER`]-(t:Table) ";
	  	query += "RETURN t.guid AS tableGuid,t.max_covers AS maxCovers,q.start_time as startTime,q.end_time AS endTime,q.date AS date,b.guid AS serverGuid,b.name AS name,b.color_code AS colorCode";
	  	query += ",b.created_dt AS createdDate,b.updated_dt AS updatedDate,b.created_by AS createdBy,b.updated_by AS updatedBy,b.rest_id AS restaurantGuid";
		Logger.debug("match query is ------------------"+query);
		Result<Map<String, Object>> r = template.query(query, params);
		Iterator<Map<String, Object>> itr = r.iterator();
		
		return itr;
       }
      
      
      
      
      
      @Override
      public Iterator<Map<String, Object>>  getTableServerAndRest(TableAssignment tableAssignmment , String [] tableGuid )
       {
    	  Map<String , Object> params = new HashMap<>();
  	  	params.put(Constants.REST_GUID, tableAssignmment.getRestaurantGuid());
  	  	params.put(Constants.SERVER_GUID, tableAssignmment.getServerGuid());
  	  	params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
  	  	params.put(Constants.TABLE_GUID, tableGuid);
    	  
	  	String query = " MATCH (t:Table)<-[:`REST_HAS_TBL`]-(a:Restaurant{guid:{"+Constants.REST_GUID+"}}";
	  	query = query + ")-[r:`REST_HAS_SERVER`]->(b:Server{guid:{"+Constants.SERVER_GUID+"}}) WHERE b.status={"+Constants.STATUS+"} ";
	  	
	  	if(tableGuid.length > 0)
	  	    query = query + "AND t.guid IN {"+Constants.TABLE_GUID+" } ";
	  	    	  	 
	  	query += " RETURN t.guid AS tableGuid,b.guid AS serverGuid,a.guid AS restaurantGuid";
	  	
		Logger.debug("match query is ------------------"+query);
		Result<Map<String, Object>> r = template.query(query, params);
		Iterator<Map<String, Object>> itr = r.iterator();
		
		return itr;
       }
     

}
