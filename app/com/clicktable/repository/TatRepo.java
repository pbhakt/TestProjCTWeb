
package com.clicktable.repository;

import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.clicktable.model.Reservation;
import com.clicktable.model.Tat;

public interface TatRepo extends GraphRepository<Tat>, CypherDslRepository<Reservation> {
	
	/*List of all the TAT */
	/*@Query("MATCH (tat:Tat)<-[rel:"+RelationshipTypes.REST_HAS_TAT+"]-(restaurant:`Restaurant`) WHERE restaurant.guid={0} AND tat.guid={1} RETURN rel.value")
	int tat_value(String restguid,String tat);
	
	@Query("MATCH (tat:Tat)<-[rel:"+RelationshipTypes.REST_HAS_TAT+"]-(restaurant:`Restaurant`)"
			+ " WHERE restaurant.guid={0} AND toInt(tat.max_covers)>=toInt({1})"
			+ " AND toInt(tat.min_covers)<=toInt({1}) AND tat.day={2} AND toInt(tat.min_covers)<>toInt(0) "
			+ " RETURN MAX(rel.value)")
	int tat_value_weekday(String restguid,String tat_cover,String day);
	
	@Query("MATCH (tat:Tat)<-[rel:"+RelationshipTypes.REST_HAS_TAT+"]-(restaurant:`Restaurant`)"
			+ " WHERE restaurant.guid={0} AND toInt(tat.max_covers)>=toInt({1})  AND tat.day={2}"
			+ " RETURN MAX(rel.value)")
	int tat_value_weekend(String restguid,String tat_cover,String day);
	
	@Query("MATCH (tat:Tat)<-[rel:"+RelationshipTypes.REST_HAS_TAT+"]-(restaurant:`Restaurant`)"
			+ " WHERE restaurant.guid={0} AND tat.day={1}"
			+ " RETURN MAX(rel.value)")
	int tat_max(String restguid,String day);*/
	
}
