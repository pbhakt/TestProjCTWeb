/**
 * 
 */
package com.clicktable.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.clicktable.model.Restaurant;

/**
 * @author a.thakur
 *
 */
@org.springframework.stereotype.Service
public interface RestaurantRepo extends GraphRepository<Restaurant> {

	@Query("MATCH (n:Restaurant) WHERE n.guid={0} RETURN n")
	Restaurant findByguid(String guid);
	
	@Query("MATCH (rest:Restaurant)-[:HAS_GUEST]->(guest:GuestProfile) where rest.guid={0} RETURN rest")
	Restaurant getGuidByRestaurant(String rest_guid);

}
