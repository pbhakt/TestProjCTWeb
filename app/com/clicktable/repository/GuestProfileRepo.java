package com.clicktable.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.clicktable.model.GuestProfile;
import com.clicktable.model.Restaurant;

/**
 * @author a.thakur
 *
 */
@org.springframework.stereotype.Service

public interface GuestProfileRepo extends GraphRepository<GuestProfile>,
		CypherDslRepository<GuestProfile> {

	/* Finding Guest by GUID */
	@Query("Match (guest:GuestProfile) where guest.guid={0} return guest")
	GuestProfile findByguid(String guid);
	
	/* Finding Restaurant by guest GUID */
	//@Query("Match (guest:GuestProfile) where guest.guid={0} return guest")
	@Query("Match (res:Restaurant)-[hs:HAS_GUEST]->(guest:GuestProfile) where guest.guid={0} return res")
	List<Restaurant> getRestByGuestguid(String guid);
	
	/* Finding Guest by GUID */
	@Query("Match (guest:GuestProfile)-[rel:GUEST_HAS_RESV]->(resv:Reservation) where guest.guid={0} and resv.guid={1}"
			+ " RETURN guest LIMIT 1")
	GuestProfile getGuest(String guid,String resv_guid);
	
	
	@Query("MATCH (rest:Restaurant)-[:HAS_GUEST]->(guest:GuestProfile) where rest.guid={0} RETURN guest")
	List<GuestProfile> getGuidByRestaurant(String rest_guid);

	

	/* Finding Guest by GUID and return count as int data type */
	@Query("MATCH (guest:GuestProfile) WHERE guest.guid={0} RETURN COUNT(guest)")
	int count_row(String guid);

	/*
	 * Finding Guest by GUID and has relationship as " GUEST_HAS_TAG" and return
	 * count as int data type
	 */
	@Query("MATCH (guest:GuestProfile)-[:GUEST_HAS_TAG]->(tag:Tag) WHERE guest.guid={0} RETURN COUNT(guest)")
	int count_guest_has_tag_exist(String guid);

	/*
	 * Deleting Relationship of Guest and Tag on behalf of Relationship property
	 * (Rest_id")
	 */
	@Query("MATCH (guest:GuestProfile)-[rel:GUEST_HAS_TAG]->(tag:Tag) WHERE guest.guid={0} AND tag.guid={1} AND rel.rest_id={2}  DELETE rel ")
	void delete_guest_has_tag_rel(String guestGuid, String tagGuid,
			String rest_id);
	
	@Query("Match (guest:GuestProfile)-[rel:GUEST_HAS_RESV]->(resv:Reservation)"
			+ " WHERE resv.reservation_status='CREATED' AND "
			+ " (toInt(resv.est_start_time)-toInt({0})>=toInt({1}) AND "
			+ "  toInt(resv.est_start_time)-toInt({0})<=toInt({2}) )"
			+ "  RETURN guest ")
	List<GuestProfile> sendSMSGuestProfile(long currentDateTime,
			long _30_mins_milliSec, long _50_mins_milliSec);

}
