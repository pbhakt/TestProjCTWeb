package com.clicktable.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.clicktable.model.Reservation;

public interface ReservationRepo extends GraphRepository<Reservation>,
		CypherDslRepository<Reservation> {

	/* Check Guest Relationship exist with Reservation */
	@Query("MATCH (reservation:Reservation)<-[rel:GUEST_HAS_RESV]-(guest:GuestProfile) WHERE reservation.guid={0} AND guest.guid={1}  RETURN COUNT(rel) ")
	int guest_has_resv_rel(String reservation_guid, String guest_guid);

	/* Delete Existing Table Relationship exist with Reservation */
	@Query("MATCH (reservation:Reservation)<-[rel:TBL_HAS_RESV]-(table:Table) WHERE reservation.guid={0} AND table.guid={1}  DELETE rel ")
	int delete_table_has_resv_rel(String reservation_guid, String guest_guid);

	
	/* Check Guest Relationship exist with Reservation */
	@Query("MATCH (reservation:Reservation)<-[rel:GUEST_HAS_RESV]-(guest:GuestProfile) WHERE reservation.guid={0} AND guest.guid={1}  RETURN COUNT(reservation) ")
	int guest_reservation_count(String reservation_guid, String guest_guid);
	
	
	/* Get reservations on a paricular table */
	@Query("MATCH (reservation:Reservation)<-[rel:TBL_HAS_RESV]-(table:Table) WHERE table.guid={0}  RETURN reservation ")
	List<Reservation> get_reservations_for_table(String table_guid );
	
	@Query("MATCH (reservation:Reservation)  WHERE {0} IN reservation.table_guid AND NOT reservation.reservation_status IN ['FINISHED','CANCELLED','NO_SHOW'] RETURN reservation ")
	List<Reservation> get_reservations_for_table_new(String table_guid );
	
	@Query("MATCH (r:Restaurant {guid : {1}})-[hg:HAS_GUEST]->(gp:GuestProfile {guid : {0}})-[hr:GUEST_HAS_RESV]-(reservation:Reservation{rest_guid:{1}})  "
			+ /*"WHERE NOT reservation.reservation_status IN ['FINISHED','CANCELLED','NO_SHOW']*/ " RETURN reservation ")
	List<Reservation> get_reservations_for_guest(String guest_guid, String rest_guid );
	
	
	
}
