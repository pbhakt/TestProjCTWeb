package com.clicktable.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.clicktable.model.TagModelOld;

@org.springframework.stereotype.Service
public interface TagPreferencesRepo extends GraphRepository<TagModelOld> {

	TagModelOld findByguid(String guid);

	@Query("MATCH (tag:Tag) WHERE tag.guid={0} RETURN COUNT(tag)")
	int count_row(String guid);

	/*
	 * Fetch Relationship of Guest and Tag on behalf of Relationship property
	 * (Rest_id")
	 */
	@Query("MATCH (guest:GuestProfile)-[rel:GUEST_HAS_TAG]->(tag:Tag) WHERE guest.guid={0} "
			+ "AND tag.guid={1} AND rel.rest_id={2} AND rel.type={3} RETURN COUNT(rel) ")
	int guest_has_tag_rel(String guestGuid, String tagGuid, String rest_id,
			String type);
	
	@Query("MATCH (tag:Tag) WHERE tag.guid={0} RETURN tag")
	TagModelOld getTag(String guid);

	@Query("MATCH (tag:Tag) WHERE tag.name={0} and tag.type={1} RETURN tag")
	TagModelOld  checkTagNameWithTypeExist(String name, String type);
}
