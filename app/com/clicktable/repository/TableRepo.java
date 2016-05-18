package com.clicktable.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.annotation.ResultColumn;
import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.clicktable.model.Reservation;
import com.clicktable.model.Table;

@org.springframework.stereotype.Service
public interface TableRepo extends GraphRepository<Table>,
    CypherDslRepository<Reservation>{

Table findByGuid(String guid); 


/* Get Table Relationship exist with Reservation */
@Query("MATCH (reservation:Reservation)<-[rel:TBL_HAS_RESV]-(table:Table) WHERE reservation.guid={0} AND reservation.guest_guid={1}  RETURN table ")
List<Table> table_has_resv_rel(String reservation_guid, String guest_guid);	

/* Get Table Relationship exist with Reservation */
@Query("MATCH (rest:Restaurant)-[rel:REST_HAS_TBL]->(table:Table) WHERE rest.guid={0}  RETURN table ")
List<Table> rest_has_table_rel(String rest_guid);

/* Get  Free Table Relationship exist with Reservation --Restaurant */
@Query("MATCH (rest:Restaurant)-[rel:REST_HAS_TBL]->(table:Table)-[rel1:TBL_HAS_RESV]->(reservation:Reservation) WHERE rest.guid={0}  RETURN table ")
List<Table> rest_has_allocated_table_rel(String rest_guid);

/* Count total Table with Restaurant*/
@Query("MATCH (restaurant:Restaurant)<-[rel:REST_HAS_TBL]->(table:Table) WHERE restaurant.guid={0}  RETURN COUNT(table) ")
int total_table(String reservation_guid);


/* Get Table Relationship exist with Reservation wrt to reservation slotted time */


@Query("MATCH (reservation:Reservation)<-[rel:TBL_HAS_RESV]-(table:Table)<-[relation:REST_HAS_TBL]-"
		+ "(restaurant:Restaurant) WHERE "
		+ "restaurant.guid={0} "
		+ "AND table.guid IN {3} "
		+ "AND "
		+ "( reservation.reservation_status <> 'CANCELLED'   "
		+ "AND reservation.reservation_status <> 'NO_SHOW' AND "
		+ "reservation.reservation_status <> 'FINISHED') AND "
		+ "((toInt(reservation.est_start_time) < toInt({1}) AND toInt(reservation.est_end_time)>toInt({1})) "
		+ " OR (toInt(reservation.est_start_time) = toInt({1}) AND toInt(reservation.est_end_time)=toInt({2}))"
		+ " OR (toInt(reservation.est_start_time) > toInt({1}) AND toInt(reservation.est_end_time)=toInt({2}))"
		+ " OR (toInt(reservation.est_start_time) = toInt({1}) AND toInt(reservation.est_end_time)<toInt({2}))"
		+ " OR (toInt(reservation.est_start_time) < toInt({2}) AND toInt(reservation.est_end_time)>toInt({2}))"
		+ " OR (toInt(reservation.est_start_time) > toInt({1}) AND toInt(reservation.est_end_time)<toInt({2}))) RETURN Distinct table")
List<Table> table_has_resv_rel_slottedTime(String rest_guid,long startTime,long endTime,List tableParam);	

/* Validate  Tables  exist with Num of Covers */
@Query("MATCH (table:Table)<-[relation:REST_HAS_TBL]-(restaurant:Restaurant)"
		+ " WHERE restaurant.guid={0} \n"
		+ " AND table.guid={1} \n "
		+ " AND toInt(table.min_covers)<=toInt({2})  \n"
		+ " AND toInt(table.max_covers)>=toInt({2})  \n"
		+ " RETURN table ")
List<Table> validate_num_of_covers(String rest_guid,String table,String num_cover);	

@Query("Match (table:`Table`)<-[`REST_HAS_TAT`]-(restaurant:`Restaurant`) where restaurant.guid={0} Return Max(table.max_covers) as max_cover, Min(table.min_covers) as min_cover ")
Cover getCover(String restId);
@QueryResult
public interface Cover {

    @ResultColumn("max_cover")
    int getMax_cover();

    @ResultColumn("min_cover")
    int getMin_cover();
    
}

@Query("MATCH (rest:Restaurant)-[rel:REST_HAS_TBL]->(table:Table) WHERE rest.guid={0} AND table.guid={1} RETURN table.max_covers ")
Integer  table_max_cover(String rest_guid, String tableGuid);


/*@Query("MATCH (reservation:Reservation)<-[rel:TBL_HAS_RESV]-(table:Table)<-[relation:REST_HAS_TBL]-"
	+ "(restaurant:Restaurant) WHERE "
	+ "restaurant.guid={0} "
	+ "AND table.guid IN {3} "
	+ "AND "
	+ "( reservation.reservation_status <> 'CANCELLED'   "
	+ "AND reservation.reservation_status <> 'NO_SHOW' AND "
	+ "reservation.reservation_status <> 'FINISHED') AND "
	+ "((reservation.reservation_status = 'SEATED' AND toInt(reservation.act_start_time) < toInt({1}) AND (toInt(reservation.act_start_time) + toInt(reservation.tat)*60*1000) > toInt({1}))"
	+ " OR (toInt(reservation.est_start_time) < toInt({1}) AND toInt(reservation.est_end_time)>toInt({1})) "
	+ " OR (toInt(reservation.est_start_time) = toInt({1}) AND toInt(reservation.est_end_time)=toInt({2}))"
	+ " OR (toInt(reservation.est_start_time) > toInt({1}) AND toInt(reservation.est_end_time)=toInt({2}))"
	+ " OR (toInt(reservation.est_start_time) = toInt({1}) AND toInt(reservation.est_end_time)<toInt({2}))"
	+ " OR (toInt(reservation.est_start_time) < toInt({2}) AND toInt(reservation.est_end_time)>toInt({2}))"
	+ " OR (toInt(reservation.est_start_time) > toInt({1}) AND toInt(reservation.est_end_time)<toInt({2}))) RETURN Distinct table")*/

@Query("MATCH (reservation:Reservation)<-[rel:TBL_HAS_RESV]-(table:Table)<-[relation:REST_HAS_TBL]-"
	+ "(restaurant:Restaurant) WHERE "
	+ "restaurant.guid={0} "
	+ "AND table.guid IN {3} "
	+ "AND " 
	+ "( reservation.reservation_status <> 'CANCELLED'   "
	+ "AND reservation.reservation_status <> 'NO_SHOW' AND "
	+ "reservation.reservation_status <> 'FINISHED') AND "
	+ "((reservation.reservation_status = 'SEATED' AND toInt(reservation.act_start_time) < toInt({1}) AND (toInt(reservation.act_start_time) + toInt(reservation.tat)*60*1000) > toInt({1}))"
	+ " OR (toInt(reservation.est_start_time) < toInt({1}) AND toInt(reservation.est_end_time)>toInt({1})) "
	+ " OR (toInt(reservation.est_start_time) > toInt({1}) AND toInt(reservation.est_end_time)=toInt({2})) "
	+ " OR (toInt(reservation.est_start_time) = toInt({1}) AND toInt(reservation.est_end_time)<toInt({2})) "
	+ " OR (toInt(reservation.est_start_time) = toInt({1}) AND toInt(reservation.est_end_time)=toInt({2}))  "
	+ " OR (toInt(reservation.est_start_time) < toInt({2}) AND toInt(reservation.est_end_time)>toInt({2}))"
	+ " OR (toInt(reservation.est_start_time) > toInt({1}) AND toInt(reservation.est_end_time)<toInt({2}))) RETURN Distinct table")
List<Table> table_has_walkin_rel_slottedTime(String rest_guid,long startTime,long endTime,List tableParam);


}

