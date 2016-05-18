package com.clicktable.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.BarEntryDao;
import com.clicktable.model.BarEntry;
import com.clicktable.model.GuestProfile;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author g.singh
 *
 */

@Service
public class BarEntryDaoImpl extends GraphDBDao<BarEntry> implements
		BarEntryDao {

	public BarEntryDaoImpl() {
		super();
		this.setType(BarEntry.class);
	}

	@Override
	public String addBarEntry(BarEntry barEntry) {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:" + Constants.RESTAURANT_LABEL + "{guid:{"
				+ Constants.REST_GUID + "}})-[hg:"
				+ RelationshipTypes.HAS_GUEST + "]->(g:"
				+ Constants.GUESTPROFILE_LABEL + "{guid:{"
				+ Constants.GUEST_GUID + "}}) ");
		query.append("CREATE (g)-[gib:" + RelationshipTypes.GUEST_IN_BAR
				+ "]->(t:" + Constants.BAR_ENTRY_LABEL + ":_"
				+ Constants.BAR_ENTRY_LABEL + "{");
		Map<String, Object> params = addingNodeProperties(query, barEntry);
		query.append("})<-[rhb:" + RelationshipTypes.REST_HAS_BARENTRY
				+ "]-(r) return t");
		Result<Map<String, Object>> result = executeWriteQuery(
				query.toString(), params);
		return getSingleResultGuid(result);
	}

	@Override
	public List<BarEntry> findByCustomeFields(Class<BarEntry> class1,
			Map<String, Object> params) {
		// TODO Auto-generated method stub
		List<BarEntry> barEntries = new ArrayList<BarEntry>();
		StringBuilder query = getMatchClause(params);

		Boolean fromScheduler = (params.get(Constants.REST_GUID) == null) ? true
				: false;
		if (fromScheduler)
			query.append(" <-[guestRel:`"
					+ RelationshipTypes.GUEST_IN_BAR
					+ "`]-(guest:`GuestProfile`)<-[hg:`HAS_GUEST`]-(rest:`Restaurant`{guid:t.rest_guid}) ");
		else {
			query.append(" <-[guestRel:`"
					+ RelationshipTypes.GUEST_IN_BAR
					+ "`]-(guest:`GuestProfile`)<-[hg:`HAS_GUEST`]-(rest:`Restaurant`{guid:{"
					+ Constants.REST_GUID + "}}) ");
		}

		query.append(getWhereClause(params));
		query.append(" RETURN DISTINCT t,guest,hg");
		
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		query.append(" SKIP " + startIndex + " LIMIT " + pageSize);
		
		Logger.debug("query is--------------------------------- " + query);
		Map<String, Object> map;// = new HashMap<String, Object>();

		Result<Map<String, Object>> results = template.query(query.toString(),
				params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			map = i.next();
			BarEntry barData = template.convert(map.get("t"), BarEntry.class);
			GuestProfile guest = template.convert(map.get("guest"),
					GuestProfile.class);

			Relationship relation = (Relationship) map.get("hg");
			try {
				guest.setFirstName((null == relation
						.getProperty(Constants.FIRST_NAME)) ? "" : relation
						.getProperty(Constants.FIRST_NAME).toString());
			} catch (NotFoundException e) {
				guest.setFirstName("");
			}
			try {
				guest.setIsVip(Boolean.valueOf((relation.getProperty("is_vip")
						.toString())));
			} catch (NotFoundException e) {
				guest.setIsVip(false);
			}
			try {
				guest.setReason(relation.getProperty("reason").toString());
			 } catch (NotFoundException e) {
				 guest.setReason(null);
			}
			try {
				guest.setGender((null == relation.getProperty(Constants.GENDER)) ? guest
						.getGender() : relation.getProperty(Constants.GENDER)
						.toString());
			} catch (NotFoundException e) {
			}
			try {
				guest.setFirstSeatedTime((null == relation.getProperty("first_seated")) ? null : new Date(Long.valueOf(relation.getProperty("first_seated").toString())));
			 } catch (NotFoundException e) {
				 guest.setFirstSeatedTime(null);
			}

			barData.setGuestProfile(guest);
			barEntries.add(barData);
		}

		return barEntries;
	}

	@Override
	public BarEntry update(Map<String, Object> valuesToUpdate) {
		StringBuilder query = new StringBuilder("MATCH (t:"
				+ type.getSimpleName() + ": _" + type.getSimpleName()
				+ " {guid:{" + Constants.GUID + "}}) ");
		query.append("SET ");
		valuesToUpdate
				.keySet()
				.stream()
				.filter(key -> !key.equals(Constants.GUID))
				.forEach(
						key -> {
							query.append(" t." + getPropertyName(key) + "={"
									+ key + "},");
						});
		query.deleteCharAt(query.lastIndexOf(","));
		query.append(" RETURN t");
		System.out.println(query);
		System.out.println(valuesToUpdate);

		Result<Map<String, Object>> result = executeWriteQuery(
				query.toString(), valuesToUpdate);
		BarEntry barEntry = (BarEntry) template.projectTo(
				result.single().get("t"), type);
		// template.saveOnly(t);
		return barEntry;
	}
	
	
	
	@Override
	public Map<String, Object> getGuestAndRestDataFromBarEntry(BarEntry barEntry) {
		Map<String, Object> paramsReturn = new HashMap<String, Object>();
		Map<String,Object> params = new HashMap<>();
		params.put(Constants.REST_ID, barEntry.getRestaurantGuid());
		params.put(Constants.GUEST_ID, barEntry.getGuestGuid());

		String query = "Match (rest:Restaurant {guid:{" + Constants.REST_ID + "}}) \n" + "OPTIONAL MATCH (rest)-[r1:HAS_GUEST]->(guest:GuestProfile {guid:{" + Constants.GUEST_ID + "}}) \n"
				+ " RETURN rest.name as restName,"
				+ "rest.locality as RestLocalilty,rest.region as RestRegion,"
				+ "r1.first_name as guestName,guest.mobile as guestMobile";
		System.out.println("---------Query -------" + query.toString());
		Map<String, Object> map;// = new HashMap<String, Object>();

		Result<Map<String, Object>> results = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			map = i.next();
			paramsReturn.put(Constants.RESTAURANT_NAME, (map.get("restName") != null) ? map.get("restName").toString() : null);
			paramsReturn.put(Constants.LOCALITY, (map.get("RestLocalilty") != null) ? map.get("RestLocalilty").toString() : null);
			paramsReturn.put(Constants.REGION, (map.get("RestRegion") != null) ? map.get("RestRegion").toString() : null);
			paramsReturn.put(Constants.NAME, (map.get("guestName") != null) ? map.get("guestName").toString() : null);
			paramsReturn.put(Constants.MOBILE, (map.get("guestMobile") != null) ? map.get("guestMobile").toString() : null);
		
		}

		return paramsReturn;

	}

	

}
