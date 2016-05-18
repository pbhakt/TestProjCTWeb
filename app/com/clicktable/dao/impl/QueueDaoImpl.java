package com.clicktable.dao.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.QueueDao;
import com.clicktable.model.Queue;
import com.clicktable.model.Reservation;
import com.clicktable.model.Restaurant;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;
import com.clicktable.util.UtilityMethods;
import com.google.common.collect.Lists;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class QueueDaoImpl extends GraphDBDao<Queue> implements
		QueueDao {

	public QueueDaoImpl() {
		super();
		this.setType(Queue.class);
	}

      
      /**
       * Method to create relationship of cover with calculated Tat 
       */
    @Override     
    public Boolean addQueue(List<Queue> queueList, Restaurant rest) 
 	{
	        Boolean created = false;
 	    	int count = 1;
 	    	Map<String, Object> params = new HashMap<>();
	    	params.put(Constants.REST_GUID, rest.getGuid());
	    	try
	    	{
	    	    for(Queue queue : queueList)
	    	    {
	    	    	params.put(Constants.GUID, queue.getGuid());
	    	    	params.put(Constants.COVERS, count);
	    	    	String query = "MATCH (r:Restaurant{guid:{" + Constants.REST_GUID + "}}),(queue:Queue) WHERE queue.guid={" + Constants.GUID + "} \n";
	    	    	query = query + "MERGE (r)-[q:" + RelationshipTypes.REST_HAS_QUEUE + "{__type__:'RestaurantHasQueue',cover:{" + Constants.COVERS + "},rest_id:{";
	    	    	query = query + Constants.REST_GUID + "}}]->(queue) Return id(q)";
	    	    	count++;
	    		
	    	    	Logger.debug("query is "+query);
	    	    	Iterator<Map<String, Object>> result = template.query(query, params).iterator();
	    	    	while(result.hasNext())
	    	    	{
	    	    		Map<String, Object> map = result.next();
	    	    		Long id = Long.valueOf( map.get("id(q)").toString());
	    		    
	    	    	}
	    	    }
	    	    
	    	    created =true;
	    	}
 	    	catch(Exception e)
 	    	{
 	           created = false;
 	            e.printStackTrace();
 	            
 	    	}
 	    	return created;
 	    	
 	}
    
    
    
    /**
     * Method to create relationship of cover with calculated Tat 
     */
  @Override     
  public Map<Integer,Queue> getQueue(String restGuid) 
  {
	  Map<String,Object> param = new HashMap<>();
	  param.put(Constants.REST_GUID, restGuid);
	  Map<Integer,Queue> queueMap = new HashMap<Integer, Queue>();
	  String query = "MATCH (r:Restaurant)-[q:" + RelationshipTypes.REST_HAS_QUEUE + "{rest_id:{restaurantGuid}}]->(queue:Queue) Return q.cover,queue";
	  Logger.debug("query is "+query);
	  Iterator<Map<String, Object>> result = template.query(query, param).iterator();

	  Map<String, Object> map;
	  Queue queue;
	  Integer cover;
	  while(result.hasNext())
	  {
		  map = result.next();
		  queue = template.convert(map.get("queue"), Queue.class);
		  cover = (Integer) map.get("q.cover");
		  queueMap.put(cover, queue);

	  }

	  return queueMap;

  }



    @Override
    public Boolean updateQueueData(Queue queue, Reservation reservation) 
    {
	    Long id;
	    Boolean created = false;
	    Map<String, Object> params = new HashMap<>();
	    params.put(Constants.RESERVATION_GUID, reservation.getGuid());
	    params.put(Constants.GUID, queue.getGuid());
	    try
	    {
	       String query = "MATCH (r:Reservation{guid:{" + Constants.RESERVATION_GUID + "}}),(queue:Queue) WHERE queue.guid={" + Constants.GUID + "} \n";
	       query = query + "MERGE (r)<-[q:" + RelationshipTypes.QUEUE_HAS_RESV + "{__type__:'QueueHasReservation'}]-(queue) Return id(q)";
	       Logger.debug("query is "+query);
	       Iterator<Map<String, Object>> result = template.query(query, params).iterator();
	       while(result.hasNext())
	       {
		   Map<String, Object> map = result.next();  		    
		   id = Long.valueOf( map.get("id(q)").toString());
	       } 
	       created =true;
	    }
	    catch(Exception e)
	    {
		created = false;
		e.printStackTrace();
	    }
	    	
	    return created;
    }
    
    
    @Override
    public Queue getQueueForReservation(Reservation reservation) 
    {
	Queue queue = null;
	Map<String,Object> params = new HashMap<>();
	params.put(Constants.REST_ID, reservation.getRestaurantGuid());
	params.put(Constants.GUID, reservation.getGuid());
	
	String query = "MATCH (rest:Restaurant)-[rhq:" + RelationshipTypes.REST_HAS_QUEUE + "{rest_id:{restId}}]->(queue:Queue)";
	query = query + "-[qhr:" + RelationshipTypes.QUEUE_HAS_RESV + "{__type__:'QueueHasReservation'}]->(resv:Reservation{guid:{guid}}) RETURN queue";
	Logger.debug("query is "+query);
	Iterator<Map<String, Object>> result = template.query(query, params).iterator();
	while(result.hasNext())
	{
	    Map<String, Object> map = result.next();
	    queue = template.convert(map.get("queue"), Queue.class);
	}
	
	return queue;
    }


    @Override
    public void deleteQueueReservation(Reservation reservation) 
    {
    	// TODO Auto-generated method stub

    	Map<String,Object> params = new HashMap<>();
    	params.put(Constants.REST_ID, reservation.getRestaurantGuid());
    	params.put(Constants.GUID, reservation.getGuid());

    	String query = "MATCH (rest:Restaurant)-[rhq:" + RelationshipTypes.REST_HAS_QUEUE + "{rest_id:{restId}}]->(queue:Queue)";
    	query = query + "-[qhr:" + RelationshipTypes.QUEUE_HAS_RESV + "{__type__:'QueueHasReservation'}]->(resv:Reservation{guid:{guid}}) DELETE qhr";
    	Logger.debug("query is "+query);
    	Iterator<Map<String, Object>> result = template.query(query, params).iterator();
    }
    
    
    @Override
    public void deleteAllQueueReservation(String restGuid) 
    {

    	// TODO Auto-generated method stub
    	String query = "MATCH (rest:Restaurant";
    	/*System.out.println("Calling delete queue in dao");*/
    	if(restGuid != null && !restGuid.equals(""))
    	{
    		query = query + "{guid:'" + restGuid + "'}";
    	}
    	
    	
    	query = query + "MATCH ()";
    	
    	
    	query = query +")-[rhq:" + RelationshipTypes.REST_HAS_QUEUE + "]->(queue:Queue) SET queue.count=0 WITH queue \n";
    	query = query + " OPTIONAL MATCH (queue)-[qhr:" + RelationshipTypes.QUEUE_HAS_RESV + "{__type__:'QueueHasReservation'}]->(resv:Reservation) SET resv.est_end_time=toInt(timestamp()-2*60*1000) DELETE qhr";
    	/*System.out.println("query is "+query);
    	System.out.println("Calling delete queue in dao query printed");*/
    	Iterator<Map<String, Object>> result = template.query(query, null).iterator();
    	/*System.out.println("Calling delete queue in dao query executed");*/
    	
    
    }
    
    
    
    @Override
    public void deleteAllQueueReservationBySchedular() 
    {

    	// TODO Auto-generated method stub
    	String query = " MATCH (reservation:Reservation)<-[qhr:" + RelationshipTypes.QUEUE_HAS_RESV + "{__type__:'QueueHasReservation'}]-(queue:Queue)  RETURN reservation.guid as guid"
						+ ",reservation.created_by as createdBy,reservation.booked_by as bookedBy \n";
    	
    	Map<String, Object> map;
		
    	List<Map<String, Object>> mapList  = new ArrayList<>();
	
		Result<Map<String, Object>> results = template.query(query.toString(), null);
		
		Iterator<Map<String, Object>> i = results.iterator();
		
		while (i.hasNext()) {
			map = i.next();

			map.put("historyGuid", UtilityMethods.generateCtId());
			mapList.add(map);
		}
		
		Map<String,Object> params = new HashMap<>();
		if(mapList.size() > 0){
			List<List<Map<String,Object>>> resvList = Lists.partition(mapList, 25);
			resvList.forEach(listOfReservations -> {
				params.put("resvList", listOfReservations);
				StringBuilder q = new StringBuilder();
				q.append("UNWIND {resvList} AS resvList \n"
						+ "MATCH (resv: Reservation {guid : resvList.guid})<-[qhr:" + RelationshipTypes.QUEUE_HAS_RESV + "{__type__:'QueueHasReservation'}]-(queue) DELETE qhr \n"
						+ "WITH resv, queue, resvList "
						+ "SET resv.reservation_status = 'NO_SHOW',resv.est_end_time=toInt(timestamp()-2*60*1000), queue.count = 0  \n"
						+ "MERGE (resvHistory:ReservationHistory:_ReservationHistory {guid:resvList.historyGuid, created_dt: timeStamp(), "
						+ "created_by:resvList.createdBy,booked_by:resvList.bookedBy,resv_status:'"
						+  Constants.NO_SHOW_STATUS
						+ "'})<-[r:RESV_HISTORY{__type__:'ReservationHasHistory',guid:resvList.historyGuid}]-(resv) ");
				System.out.println("QUEUE QUery is------------------------------------------" + q);
				template.query(q.toString(), params);
			});	
		}
    	
    	
    	/*query = query +")-[rhq:" + RelationshipTypes.REST_HAS_QUEUE + "]->(queue:Queue) SET queue.count=0 WITH queue \n";
    	query = query + " OPTIONAL MATCH (queue)-[qhr:" + RelationshipTypes.QUEUE_HAS_RESV + "{__type__:'QueueHasReservation'}]->(resv:Reservation) SET resv.est_end_time=toInt(timestamp()-2*60*1000) DELETE qhr";
    	System.out.println("query is "+query);
    	System.out.println("Calling delete queue in dao query printed");
    	Iterator<Map<String, Object>> result = template.query(query, null).iterator();
    	System.out.println("Calling delete queue in dao query executed");
    	
    	MATCH (resv:Reservation)-[qhr:" + RelationshipTypes.QUEUE_HAS_RESV + "{__type__:'QueueHasReservation'}]->(q)*/
    }
    
  
    
    
    @Override
    public List<Reservation> getQueuedReservation(Map<String,Object> queueMap) 
    {
    	List<Reservation> resvList = new ArrayList<Reservation>();
    	//List<Reservation> resvList1 = new ArrayList<Reservation>();
    	Long queueQueryTime = new Date().getTime();
    	String query = "MATCH (rest:Restaurant {guid:{restId}})-[rhq:" + RelationshipTypes.REST_HAS_QUEUE + "]->(queue:Queue)";
    	query = query + "-[qhr:" + RelationshipTypes.QUEUE_HAS_RESV + "{__type__:'QueueHasReservation'}]->(resv:Reservation) ";
    	
    	/* if condition has been added for table get */
    	if(queueMap.containsKey("currentTimeFilter"))
    	{
    		query = query + " WHERE toInt(resv.est_start_time) <= {currentTimeFilter} AND toInt(resv.est_end_time) > {currentTimeFilter} ";
    	}
    	
    	if(queueMap.containsKey("includeOutOfOpHrResv") && !Boolean.valueOf(queueMap.get("includeOutOfOpHrResv").toString()))
    	{
    		if(query.contains("WHERE"))
    		{
    			query = query + " AND HAS (resv.est_end_time) ";
    		}
    		else
    		{
    			query = query + " WHERE HAS (resv.est_end_time) ";
    		}
    	}
    	
    	query = query + "RETURN toInt(resv.est_start_time) as estStartTime,toInt(resv.est_end_time) as estEndTime,resv.guid as guid,resv.num_covers as numCovers,resv.table_guid as tableGuid,resv.tat as tat,"
    			+ "resv.preferred_table as preferredTable, resv.booking_mode as bookingMode,resv.reservation_status as reservationStatus,"
    			+ "toInt(resv.created_dt) as createdDate,resv.guest_guid as guestGuid ORDER BY toInt(resv.created_dt)";
    	Logger.debug("query is "+query);
    	Iterator<Map<String, Object>> result = template.query(query, queueMap).iterator();
    	Logger.debug("Time taken in query of getting queued resv is---------------------------------" + (new Date().getTime() - queueQueryTime));
    	//ObjectMapper mapper = new ObjectMapper();
    	
    	while(result.hasNext())
    	{
    		Map<String, Object> map = result.next();
    		if(map.get("estStartTime") != null)
    		{
    			map.put("estStartTime", new Date(Long.valueOf(map.get("estStartTime").toString())));
    		}
    		if(map.get("estEndTime") != null)
    		{
    			map.put("estEndTime", new Date(Long.valueOf(map.get("estEndTime").toString())));
    		}
    		map.put("createdDate", new Date(Long.valueOf( map.get("createdDate").toString())));
    		
    		BeanWrapper wrapper = new BeanWrapperImpl(Reservation.class);
    		wrapper.setPropertyValues(map);
    		Reservation resv = (Reservation) wrapper.getWrappedInstance();
    		resvList.add(resv);	
    	}
    	
    	return resvList;
   }
    
    @Override
	public Queue updateAllProperties(Queue queue){
		return template.save(queue);
	}



}

