package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Queue;
import com.clicktable.model.Reservation;
import com.clicktable.model.Restaurant;

/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface QueueDao extends GenericDao<Queue>
{

    Boolean addQueue(List<Queue> queueList, Restaurant rest);

    Map<Integer, Queue> getQueue(String restGuid);

    Boolean updateQueueData(Queue queue, Reservation reservation);

    Queue getQueueForReservation(Reservation reservation);

    void deleteQueueReservation(Reservation reservation);

    void deleteAllQueueReservation(String restGuid);

	List<Reservation> getQueuedReservation(Map<String, Object> queueMap);

	void deleteAllQueueReservationBySchedular();

	Queue updateAllProperties(Queue queue);
    
  
   
    
}
