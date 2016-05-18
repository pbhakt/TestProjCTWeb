package com.clicktable.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.CypherDslRepository;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.clicktable.model.GuestConversation;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface ConversationRepo extends GraphRepository<GuestConversation> ,
                  CypherDslRepository<GuestConversation>{	
	
	
	
	/* Retrieve Conversation for this reservation  */
	@Query("MATCH (conversation : GuestConversation) "
			+ "WHERE conversation.rest_id={0} AND conversation.guest_id={1} RETURN conversation LIMIT 1 ")
	GuestConversation getGuestConversation(String restId,String guestId);
	 
	 
	
	
}