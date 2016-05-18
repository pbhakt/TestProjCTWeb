package com.clicktable.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.clicktable.model.Cuisine;

public interface CuisineRepo extends GraphRepository<Cuisine>,
		CypherDslRepository<Cuisine> {

	/* Finding Guest by GUID */
	@Query("Match (cuisine:Cuisine) where cuisine.guid={0} return cuisine")
	Cuisine findByguid(String guid);

	/*
	 * Fetch Relationship of Restaurant and Cuisine on behalf of Relationship
	 * property (Rest_id")
	 */
	@Query("MATCH (restaurant:Restaurant)-[rel:HAS_CUISINE]->(cuisine:Cuisine) WHERE restaurant.guid={0} "
			+ "AND cuisine.guid={1}  RETURN COUNT(rel) ")
	int cuisine_has_tag_rel(String cuisine,String restaurant);
	/*
	 * Deleting Relationship of Restaurant and Cuisine on behalf of Relationship property
	 * (Rest_id")
	 */
	@Query("MATCH (restaurant:Restaurant)-[rel:HAS_CUISINE]->(cuisine:Cuisine) WHERE restaurant.guid={0} AND cuisine.guid={1}  DELETE rel ")
	void delete_has_cuisine_rel(String guid, String guid2);



}
