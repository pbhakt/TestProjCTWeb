package com.clicktable.dao.impl;

import java.util.Map;

import org.neo4j.graphdb.Node;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.PromotionDao;
import com.clicktable.model.EventPromotion;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author g.singh
 *
 */

@Service
public class PromotionDaoImpl extends GraphDBDao<EventPromotion> implements PromotionDao {

	public PromotionDaoImpl() {
		super();
		this.setType(EventPromotion.class);
	}

	@Override
	public String addEventPromotion(EventPromotion eventPromotion) {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:" + Constants.RESTAURANT_LABEL +"{"+getPropertyName(Constants.GUID)+":{"+Constants.REST_GUID+"}}) ");
		query.append("CREATE (r)-[rhp:" + RelationshipTypes.REST_HAS_PROMOTION + "{__type__:'RestHasPromotion',"
				+ getPropertyName(Constants.CREATED_DATE) + ":{" + Constants.CREATED_DATE + "}}]->(e:" + Constants.EVENT_PROMOTION_LABEL + ":_" + Constants.EVENT_PROMOTION_LABEL
				+ "{");
		Map<String, Object> params = addingNodeProperties(query, eventPromotion);
		query.append("}) return e");
		Map<String, Object> result = template.query(query.toString(), params).singleOrNull();
		String output = null;
		if (result != null) 
			output=(String) ((Node) result.get("e")).getProperty(Constants.GUID);
		return output;
	}

/*	@Override
	public ArrayList<HashMap<String, String>> addPromotionalConversation(EventPromotion eventPromotion) {

		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:Reservation{reservation_status:{" + getPropertyName(Constants.RESERVATION_STATUS) + "}})<-[ghr:" + Constants.GUEST_HAS_RESV
				+ "]-(g:GuestProfile)-[ght:GUEST_HAS_TAG]->(t:Tag)");
		query.append(" WHERE t.guid in [{guids}] and ");
		query.append("r." + getPropertyName(Constants.UPDATED_DATE) + ">{" + Constants.RESERVED_AFTER + "} \n");
		query.append("CREATE (g)-[ghc:" + RelationshipTypes.GUEST_HAS_CONVERSATION + "{__type__:'GuestHasConversation'," + getPropertyName(Constants.REST_ID) + ":'" + eventPromotion.getRestId()
				+ "'," + getPropertyName(Constants.CREATED_DATE) + ":" + eventPromotion.getCreatedDate().getTime() + "}]->(c:" + Constants.GUEST_CONVERSATION_LABEL + ":_"
				+ Constants.GUEST_CONVERSATION_LABEL + "{");
		query.append(getPropertyName(Constants.GUID) + ":{" + getPropertyName(Constants.GUID) + "},");
		query.append(getPropertyName(Constants.MESSAGE) + ":{" + getPropertyName(Constants.MESSAGE) + "},");
		query.append(getPropertyName(Constants.GUEST_ID) + ":g." + getPropertyName(Constants.GUID) + ",");
		query.append(getPropertyName(Constants.SENT_BY) + ":{" + getPropertyName(Constants.SENT_BY) + "},");
		query.append(getPropertyName(Constants.ORIGIN) + ":{" + getPropertyName(Constants.ORIGIN) + "},");
		query.append(getPropertyName(Constants.ORIGIN_ID) + ":{" + getPropertyName(Constants.ORIGIN_ID) + "},");
		query.append(getPropertyName(Constants.CREATED_DATE) + ":{" + getPropertyName(Constants.CREATED_DATE) + "},");
		query.append(getPropertyName(Constants.CREATED_BY) + ":{" + getPropertyName(Constants.CREATED_BY) + "},");
		query.append(getPropertyName(Constants.UPDATED_DATE) + ":{" + getPropertyName(Constants.UPDATED_DATE) + "},");
		query.append(getPropertyName(Constants.UPDATED_BY) + ":{" + getPropertyName(Constants.UPDATED_BY) + "},");
		query.append(getPropertyName(Constants.LANGUAGE_CODE) + ":{" + getPropertyName(Constants.LANGUAGE_CODE) + "},");
		query.append(getPropertyName(Constants.STATUS) + ":{" + getPropertyName(Constants.STATUS) + "}");
		query.append("}) return c, g");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("guids", String.join(",", eventPromotion.getTagIds()));
		params.put(getPropertyName(Constants.RESERVATION_STATUS), Constants.FINISHED);
		params.put(Constants.RESERVED_AFTER, eventPromotion.getAfter().getTime());
		params.put(getPropertyName(Constants.GUID), eventPromotion.getGuid());
		params.put(getPropertyName(Constants.MESSAGE), eventPromotion.getMessage());
		params.put(getPropertyName(Constants.SENT_BY), Constants.RESERVATION_ENUM_VALUE);
		params.put(getPropertyName(Constants.ORIGIN), Constants.EVENT_LABEL);
		params.put(getPropertyName(Constants.ORIGIN_ID), eventPromotion.getEventId());
		params.put(getPropertyName(Constants.CREATED_DATE), eventPromotion.getCreatedDate());
		params.put(getPropertyName(Constants.CREATED_BY), eventPromotion.getCreatedBy());
		params.put(getPropertyName(Constants.UPDATED_DATE), eventPromotion.getUpdatedDate());
		params.put(getPropertyName(Constants.UPDATED_BY), eventPromotion.getUpdatedBy() + "");
		params.put(getPropertyName(Constants.LANGUAGE_CODE), eventPromotion.getLanguageCode() + "");
		params.put(getPropertyName(Constants.STATUS), eventPromotion.getStatus() + "");
		System.out.println("ConversationDaoImpl.addPromotionalConversation()");
		System.out.println(query);
		System.out.println(Json.toJson(params));
		Result<Map<String, Object>> result = template.query(query.toString(), params);
		ArrayList<HashMap<String, String>> listOfMap = new ArrayList<HashMap<String, String>>();
		result.forEach(x -> {
			System.out.println(Json.toJson(x));
			HashMap<String, String> output = new HashMap<String, String>();
			if (x != null) {
				output.put(Constants.MOBILE, (String) ((Node) x.get("g")).getProperty(Constants.MOBILE));
				output.put(Constants.GUID, (String) ((Node) x.get("c")).getProperty(Constants.GUID));
				listOfMap.add(output);
			}
		});
		return listOfMap;*/
	
/*
	@Override
	public GuestConversation getGuestConversation(GuestProfile guest) {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (g:" + Constants.GUESTPROFILE_LABEL + "{" + Constants.GUID + ":{" + Constants.GUID + "}})-[ghc:" + RelationshipTypes.GUEST_HAS_CONVERSATION + "]->(c:"
				+ Constants.GUEST_CONVERSATION_LABEL + "{origin:{origin},sent_by:{sent_by}})");
		query.append(" return c ORDER BY c." + getPropertyName(Constants.CREATED_DATE) + " DESC limit 1");
		System.out.println(query);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, guest.getGuid());
		params.put("origin", Constants.RESERVATION_ENUM_VALUE);
		params.put("sent_by", Constants.RESTAURANT_LABEL);

		List<GuestConversation> objs = executeQuery(query.toString(), params);
		if (objs.isEmpty()) {
			return null;
		} else {
			return objs.get(0);
		}

	}
*/
}
