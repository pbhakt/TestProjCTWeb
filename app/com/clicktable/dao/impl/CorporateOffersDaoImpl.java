package com.clicktable.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.CorporateOffersDao;
import com.clicktable.model.CorporateOffers;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Section;
import com.clicktable.model.Table;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

@Service
public class CorporateOffersDaoImpl extends GraphDBDao<CorporateOffers> implements CorporateOffersDao {

	public CorporateOffersDaoImpl() {
		super();
		this.setType(CorporateOffers.class);
	}

	@Override
	public String addCorporateOffers(CorporateOffers corporateOffers) {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:" + Constants.RESTAURANT_LABEL + "{guid:{" + Constants.REST_GUID + "}})");
		query.append(" CREATE (r)-[rho:" + RelationshipTypes.REST_HAS_OFFER + "{__type__:'RestHasOffer'}]->(t:" + Constants.CORPORATE_OFFERS_LABEL + ":_" + Constants.CORPORATE_OFFERS_LABEL + "{");
		Map<String, Object> params = addingNodeProperties(query, corporateOffers);
		query.append("}) return t");
		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), params);
		return getSingleResultGuid(result);
	}

	@Override
	public List<CorporateOffers> findCorporateOffers(Map<String, Object> params) {

		StringBuilder query = new StringBuilder("MATCH (t:`" + Constants.CORPORATE_OFFERS_LABEL + "`)");

		query.append(" WHERE 1=1 ");

		// like query for name and notes
		
		if (params.containsKey(Constants.NAME)) {
			query.append(" AND t.name = {" + Constants.NAME + "} ");
		}
		if (params.containsKey(Constants.GUID)) {
			query.append(" AND t.guid = {" + Constants.GUID + "} ");
		}
		if (params.containsKey(Constants.STATUS)) {
			query.append(" AND t.status = {" + Constants.STATUS + "} ");
		}
		//NOTES and OFFER_FIELD both checked by notes??
		if (params.containsKey(Constants.NOTES)) {
			query.append(" AND t.notes = {" + Constants.NOTES + "} ");
		}
		if (params.containsKey(Constants.OFFER_FIELD)) {
			query.append(" AND t.notes = {" + Constants.OFFER_FIELD + "} "); //it must be t.offer
		}
		if (params.containsKey(Constants.REST_GUID)) {
			query.append(" AND t.rest_guid = {" + Constants.REST_GUID + "} ");
		}
		query.append(" RETURN t ");

		return executeQuery(query.toString(), params);

	}

	@Override
	public CorporateOffers updateCorporateOffers(CorporateOffers corporate_offers_to_update, CorporateOffers corporateOffers_existing) {
		// no-need of corporateOffers_existing  object??
		CorporateOffers updated = super.update(corporate_offers_to_update);
		return updated;
	}
	
	@Override
	public StringBuilder getWhereClause(Map<String, Object> params) {
		
		StringBuilder str = new StringBuilder();
		String name = (null == params.get("name")) ? null : params.get("name").toString().replace(" ", "").toUpperCase();
		if(params.containsKey("name"))
		 params.remove("name");
		str = getWhereClause(params, "t");
		if(name != null)
		{
			if(str.toString().contains("WHERE"))
			{
				str.append(" AND ");
			}
			else
			{
				str.append(" WHERE ");
			}
			
			str.append(" UPPER(TRIM(t.name)) = '"+name+"'");
		}
		
		return str; 
	}
	
	
	@Override
	public List<CorporateOffers> getCustomCorporateOffers(Map<String,Object> params) {
		StringBuilder query = new StringBuilder("MATCH (r:"+Restaurant.class.getSimpleName()+"{guid:{"+Constants.REST_GUID+"}})-[rho:"+RelationshipTypes.REST_HAS_OFFER+"]-(t:`" + Constants.CORPORATE_OFFERS_LABEL + "`{status:{"+Constants.STATUS+"}})");
		if(params.containsKey(Constants.UPDATED_AFTER))
			query.append(" WHERE toInt(t.updated_dt)>toInt({"+Constants.UPDATED_AFTER+"})");
		
		query.append(" RETURN t.guid as guid,t.name as name,t.notes as notes,t.offer as offer");
		List<CorporateOffers> offers=new ArrayList<CorporateOffers>();
		Result<Map<String, Object>> results = template.query(query.toString(),params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) 
		{
		    Map<String, Object> map = i.next();
		    CorporateOffers offer=new CorporateOffers();
		    offer.setCreatedDate(null);
		    offer.setStatus(null);
		    offer.setGuid((String) map.get("guid"));
		    offer.setName((String) map.get("name"));
		    offer.setNotes((String) map.get("notes"));
		    offer.setOffer((String) map.get("offer"));
		    offers.add(offer);
		}
		return offers;

	}
	
	
	
/*	@Override
	public List<CorporateOffers> getCustomCorporateOffers(HashMap<String,Object> params) {
		StringBuilder query = new StringBuilder("MATCH (r:"+Restaurant.class.getSimpleName()+"{guid:{"+Constants.REST_GUID+"}})-[rho:"+RelationshipTypes.REST_HAS_OFFER+"]-(o:`" + Constants.CORPORATE_OFFERS_LABEL + "`{status:{"+Constants.STATUS+"}})");
		query.append(" WITH DISTINCT r, o.guid as guid,o.name as name,o.notes as notes,o.offer as offer");
		query.append(" MATCH (r)-[rhc:"+RelationshipTypes.REST_HAS_CAL+"]-(t:"+CalenderEvent.class.getSimpleName()+")");
		super.getWhereClause(params);
		query.append(" RETURN guid,name,notes,offer ,t.guid as calGuid, t.name as calName, t.event_dt as startTime");
		List<CorporateOffers> offers=new ArrayList<CorporateOffers>();
		List<CalenderEvent> events=new ArrayList<CalenderEvent>();
		Result<Map<String, Object>> results = template.query(query.toString(),params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) 
		{
		    Map<String, Object> map = i.next();
		    CorporateOffers offer=new CorporateOffers();
		    offer.setCreatedDate(null);
		    offer.setStatus(null);
		    offer.setGuid((String) map.get("guid"));
		    offer.setName((String) map.get("name"));
		    offer.setNotes((String) map.get("notes"));
		    offer.setOffer((String) map.get("offer"));
		    offers.add(offer);
		    
		    CalenderEvent event=new CalenderEvent();
		    event.setCreatedDate(null);
		    event.setStatus(null);
		    event.setGuid((String) map.get("calGuid"));
		    event.setName((String) map.get("calName"));
		    event.setEventDate((Long) map.get("startTime"));
		    events.add(event);
		}
		return offers;

	}
	*/
	
	

}
