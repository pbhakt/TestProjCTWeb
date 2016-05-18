package com.clicktable.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.clicktable.model.ReservationHistory;
@org.springframework.stereotype.Service
public interface ReservationHistoryRepo extends GraphRepository<ReservationHistory> ,
                  CypherDslRepository<ReservationHistory>{	
	
	
	/* Retrieve List of ReservationHistory , to track the history of Reservation  */
	@Query("MATCH (reservation:Reservation)-[:RESV_HISTORY]->(resv:ReservationHistory) "
			+ "WHERE reservation.guid={0}   RETURN DISTINCT resv ORDER BY resv.created_dt ASC")
	List<ReservationHistory> getreservationHistoryList(String reservation);
	
	/* Retrieve List of ReservationHistory , to track the history of Reservation  */
	@Query("MATCH (reservation:Reservation)-[:RESV_HISTORY]->(resv:ReservationHistory) "
			+ "WHERE reservation.guid={0} AND resv.resv_status={1} RETURN resv LIMIT 1 ")
	ReservationHistory getreservationHistory(String reservation,String resv_status);
	 
	 
	
	
}