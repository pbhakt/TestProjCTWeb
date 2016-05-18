package com.clicktable.dao.impl;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.ServerDao;
import com.clicktable.model.Server;
import com.clicktable.model.TableAssignment;
import com.clicktable.util.Constants;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class ServerDaoImpl extends GraphDBDao<Server> implements
		ServerDao {

	public ServerDaoImpl() {
		super();
		this.setType(Server.class);
	}
	
	
	@Override
	protected StringBuilder getMatchClause(Map<String, Object> params) {		
			return new StringBuilder("MATCH (r:Restaurant)-[rhs:REST_HAS_SERVER]->(t:Server)");		
	}
	
	  
	    @Override
	    protected StringBuilder getWhereClause(Map<String, Object> params) 
	    {
		String  tableId = "";//, shiftStatus="";
		/*if (params.containsKey(Constants.REST_GUID)) 
		{
			restId = (String) params.get(Constants.REST_GUID);
			params.remove(Constants.REST_GUID);
		}*/
		if (params.containsKey(Constants.TABLE_GUID)) 
		{
			tableId = (String) params.get(Constants.TABLE_GUID);
			params.remove(Constants.TABLE_GUID);
		}
		//comma separated list of status (ACTIVE,INACTIVE)
		StringBuilder query = super.getWhereClause(params);
			
			Logger.debug("query after super where clause is ");
			
			/*if (!restId.equals("")) 
			{
				params.put(Constants.REST_GUID, restId);
				if (query.toString().contains(Constants.WHERE))
					query.append(" AND ");
				else
					query.append(" WHERE ");
				query.append("r.guid= {" + Constants.REST_GUID+ "} ");
			}*/
			
			if (!tableId.equals("")) 
			{
				params.put(Constants.TABLE_GUID, tableId);
				if (query.toString().contains(Constants.WHERE))
					query.append(" AND ");
				else
					query.append(" WHERE ");
				query.append("s.guid= {" + Constants.TABLE_GUID + "} ");
			}
			
			
			query.append("\n  OPTIONAL MATCH (t)<-[ths:TBL_HAS_SERVER]-(s:Table) ");
		   Logger.debug("final query in where clause is "+query);
		   
		return query;
	    }
	    
	    
	    
		@Override
		protected StringBuilder getReturnClause(Map<String, Object> params) {
			StringBuilder query= new StringBuilder();
			/*if (params.containsKey(Constants.REST_GUID)) 
			{
				query.append("RETURN DISTINCT(t)");
			}else*/
				query.append("RETURN t,s.guid AS tableGuid,s.max_covers AS maxCovers,ths.start_time AS startTime,ths.end_time AS endTime,ths.date AS date");
			query = handleOrderBy(query, params);
			Integer pageSize = getPageSize(params);
			Integer startIndex = getIndex(params, pageSize);
			return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);
		}
		
		
		
		   @Override
		    protected List<Server> executeQuery(String query, Map<String, Object> params) 
		    {
		    	List<Server> list = new ArrayList<Server>();
		    	List<TableAssignment> assignedTablesList;// = new ArrayList<TableAssignment>();
				Logger.debug(query);
				params.forEach((x, y) -> Logger.debug(x + ">" + y));
				 Iterator<Map<String, Object>> results = template.query(query, params).iterator();
				 Map<String,Server> serverMap = new HashMap<String, Server>();
				 Map<String,Integer> orderMap = new HashMap<>();
				 Server server;
				 String tableGuid = null,startTimeStr = null,endTimeStr = null,dateStr = null;
				 int order = 0,maxCovers = 0;
				 TableAssignment assignedTable;
				 SimpleDateFormat timestampFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
				 SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
				 while(results.hasNext())
				 {
				     Map<String, Object> map = results.next();
				     Logger.debug("map is "+map);
				     //String restGuid = map.get("r.guid").toString();
				     server =template.convert(map.get("t"), Server.class);
				    // profile.setRestGuid(restGuid);
				     
				     assignedTable = new TableAssignment();
				     Logger.debug("server is "+server+" map.containsKey(Constants.TABLE_GUID)====="+map.containsKey(Constants.TABLE_GUID));
					    
				     //s.guid AS tableGuid,s.max_covers AS maxCovers,ths.start_time AS startTime,ths.end_time AS endTime,ths.date AS date
				     if(map.get(Constants.TABLE_GUID) != null)
				     {
				     tableGuid = map.get(Constants.TABLE_GUID).toString();
				     if(map.get(Constants.START_TIME) != null)
				     {
				     startTimeStr = timestampFormat.format(new Date(Long.parseLong(map.get(Constants.START_TIME).toString())));
				     }
				     if(map.get(Constants.END_TIME) != null)
				     {
				     endTimeStr = timestampFormat.format(new Date(Long.valueOf( map.get(Constants.END_TIME).toString())));
				     }
				     if(map.get(Constants.DATE) != null)
				     {
				     dateStr = dateFormat.format(new Date(Long.valueOf( map.get(Constants.DATE).toString())));
				     }
				     if(map.get(Constants.MAX_COVERS) != null)
				     {
				     maxCovers = Integer.parseInt(map.get(Constants.MAX_COVERS).toString());
				     }
				     
				     
				   //populate table
				     assignedTable.setDate(dateStr);
				     assignedTable.setEndTime(endTimeStr);
				     assignedTable.setMaxCovers(maxCovers);
				     assignedTable.setRestaurantGuid(server.getRestaurantGuid());
				     assignedTable.setServerGuid(server.getGuid());
				     assignedTable.setStartTime(startTimeStr);
				     assignedTable.setTableGuid(tableGuid);
				     
				     }
				     
				      
				     if(serverMap.containsKey(server.getGuid()))
				     {
					 Logger.debug("contains key----- order is "+order);
					 server = serverMap.get(server.getGuid());
					 assignedTablesList = server.getAssignedTables();
					 if(assignedTable.getTableGuid() != null)
					 {
					 assignedTablesList.add(assignedTable);
					 }
					 server.setAssignedTables(assignedTablesList);
					 serverMap.put(server.getGuid(), server);
				     }
				     else
				     {
					 Logger.debug("does not contain key----- order is "+order);
					 assignedTablesList = new ArrayList<TableAssignment>();
					 if(assignedTable.getTableGuid() != null)
					 {
					 assignedTablesList.add(assignedTable);
					 }
					 server.setAssignedTables(assignedTablesList);
					 serverMap.put(server.getGuid(), server);
					 orderMap.put(server.getGuid(), order);
					 list.add(server);
					 order++;
				     }
					 
				}
				 Logger.debug("order map is "+orderMap);
				 Logger.debug("server map is "+serverMap);
				 for(Map.Entry<String, Server> entry : serverMap.entrySet())
				 {
				     order = orderMap.get(entry.getKey());
				     Logger.debug("order is "+order);
				     list.set(order,entry.getValue());
				     
				     
				 }
				/*List<MyResult> resList = new ArrayList<MyResult>();
				
				results.forEach(resList::add);//.getReservations();
				for(GuestProfile profile: resList.get(0).getUsers()){
					profile.setReservations(resList.get(0).getReservations());
					list.add(profile);
				}
				
				// Consumer<Map<String, Object>> arg0;
				//results.forEach(arg0);
				//.to(type);
				//res.getUsers().forEach(list::add);*/
				return list;
				//return null;
		    	
		   }
		   
		   
		   
		   @Override
			public Server find(Object id) {
				String query = "MATCH (t:" + type.getSimpleName() + ") WHERE t." + Constants.GUID + "={" + Constants.GUID + "} and t.status='"+Constants.ACTIVE_STATUS+"' RETURN t";
				Map<String, Object> param = new HashMap<String, Object>();
				param.put(Constants.GUID, id.toString());
				Logger.debug("query is " + query);
				Result<Server> r = template.query(query, param).to(type);
				return r.singleOrNull();
			}
	   
	    
	    
	 
	    
	    /**
	         * Method to create relationship of a restaurant with a server
	         */
	      @Override     
	      public boolean addRestaurantServer(Server server) 
	   	{
		   	//boolean isActive = server.getStatus().equals(Constants.ACTIVE_STATUS);
	    	  Map<String, Object> params = new HashMap<>();
	    	  params.put(Constants.REST_GUID, server.getRestaurantGuid());
	    	  params.put(Constants.GUID, server.getGuid());
	   	    	String query = "MATCH (r:Restaurant {guid:{"+Constants.REST_GUID+"}}),(t:Server) WHERE t.guid={"+Constants.GUID+"} \n";
	   		query = query + "MERGE (r)-[:REST_HAS_SERVER{__type__:'RestaurantHasServer',rest_guid:{"+Constants.REST_GUID+"}}]->(t)";
	   	        Logger.debug("query is "+query);
	   		 Result<Map<String, Object>> r = template.query(query, params);
	   	        Logger.debug("query executed,Result is "+r);
	   	        
	   	        return true;
	   	} 
	      
	      
	      /**
	       * Method to delete relationship of a restaurant with a server
	       */
	    @Override     
	    public boolean deleteServer(String serverGuid) 
	 	{
	    	Map<String, Object> params = new HashMap<>();
	    	params.put(Constants.SERVER_GUID, serverGuid);
	    	params.put(Constants.STATUS, Constants.INACTIVE_STATUS);
	 	    	String query = "MATCH (r:Restaurant)-[q:REST_HAS_SERVER]->(t:Server{guid:{"+Constants.SERVER_GUID+"}}) SET t.status={"+Constants.INACTIVE_STATUS+"} RETURN t";
	 		 Logger.debug("query is "+query);
	 		 Result<Server> r = template.query(query, params).to(Server.class);
	 	        Logger.debug("query executed,Result is "+r.singleOrNull()+" deleted ");
	 	        if(r.singleOrNull() != null)
	 	        {
	 	            Logger.debug("Server with guid " + r.singleOrNull().getGuid()+" deleted");
	 	        }
	 	        
	 	        return true;
	 	}
	    
	    
	    /**
	       * Method to delete relationship of a restaurant with a server
	       */
	    @Override     
	    public boolean unassignAllTablesForServer(String serverGuid) 
	 	{
		 Boolean isUnassigned = false;
		 Map<String , Object> params = new HashMap<>();
		 params.put(Constants.SERVER_GUID, serverGuid);
		  try
		  {
	     	  	String query = "MATCH (s:Server {guid:{"+Constants.SERVER_GUID+"}})<-[r:TBL_HAS_SERVER]-(t:Table) ";
	     		query = query + " DELETE r";
	     	        Logger.debug("query is "+query);
	     		
	     	        Result<Map<String, Object>> r = template.query(query, params);
	     	        Logger.debug("query executed,Result is "+r);
	     	        isUnassigned = true;
		    }
		  catch(Exception e)
		  {
			  Logger.debug("Exception is------------------ " + e.getLocalizedMessage());
		      isUnassigned = false;
		  }
	     	        return isUnassigned;
	 	}
	    
	    
	    
	    @Override
	    public Server checkForColorCode(Server server) 
	    {
	    	Map<String , Object> params = new HashMap<>();
	    	params.put(Constants.REST_GUID, server.getRestaurantGuid());
	    	params.put(Constants.SERVER_ID, server.getServerId());
	    	params.put(Constants.COLOR_CODE, server.getColorCode());
	    	params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			StringBuilder query = new StringBuilder("MATCH (r:Restaurant{guid:{"+Constants.REST_GUID+"}})-[q:REST_HAS_SERVER]");
			query.append("->(t:Server) WHERE t.server_id<>{"+Constants.SERVER_ID+"} AND t.color_code={"+Constants.COLOR_CODE+"} AND");
			query.append(" t.status={"+Constants.ACTIVE_STATUS+"} RETURN t");
			Logger.debug("query is " + query);
			Result<Server> r = template.query(query.toString(), params).to(Server.class);
			return r.singleOrNull();
		}
	    

	
}

