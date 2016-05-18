package com.clicktable.dao.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.exception.ClicktableException;
import com.clicktable.model.EventPromotion;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.Restaurant;
import com.clicktable.model.TagModelOld;
import com.clicktable.relationshipModel.HasGuest;
import com.clicktable.response.DNDResponse;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;
import com.clicktable.util.UtilityMethods;

@Service
public class CustomerDaoImpl extends GraphDBDao<GuestProfile> implements
		CustomerDao {
	private static Logger.ALogger log = Logger.of(CustomerDaoImpl.class);
	public CustomerDaoImpl() {
		super();
		this.setType(GuestProfile.class);
	}

	@Override
	public List<List<String>> getGuestMobileByTagsForEvent(
			EventPromotion promotion) {

		StringBuilder query = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		createQueryForFilterGuest(promotion, query, params);
		query.append(" WITH DISTINCT(g.mobile) as mob, g.guid as guest_guid, (CASE (HAS(hg.dnd_mobile)) WHEN true THEN hg.dnd_mobile ELSE false END) as dnd_mob,");
		query.append(" (CASE(HAS(g.is_dnd_user_enable)) WHEN true THEN  g.is_dnd_user_enable ELSE false END) as dnd_user,");
		query.append(" (CASE(HAS(g.is_dnd_trai_enable)) WHEN true THEN  g.is_dnd_trai_enable ELSE false END) as dnd_trai");
		query.append(" RETURN ");
		query.append(" CASE (dnd_mob OR dnd_user OR dnd_trai)");
		query.append(" WHEN false THEN collect([mob, guest_guid]) ");
		// query.append(" ELSE collect([0,0])");
		query.append(" END as count");
		params.put(getPropertyName(Constants.STATUS), Constants.ACTIVE_STATUS);
		params.put(getPropertyName(Constants.REST_GUID),
				promotion.getRestaurantGuid());
		params.put(getPropertyName(Constants.RESERVATION_STATUS),
				Constants.FINISHED);
		
		Logger.debug(query.toString());
		Logger.debug(params+",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,params");
		Result<Map<String, Object>> r = template
				.query(query.toString(), params);
		// Map<String, Object> map = r.singleOrNull();
		Iterator<Map<String, Object>> itr = r.iterator();
		// List<String[]> list = (List) map.get("count");
		List<List<String>> list = new ArrayList<List<String>>();
		while (itr.hasNext()) {
			Map<String, Object> map = itr.next();
			List<List<String>> numbers = (List) map.get("count");
			// list = (List) map.get("count");

			if (numbers != null)
				list.addAll(numbers);
		}
		return list;
	}

	@Override
	public HasGuest saveRelationModel(HasGuest relationModel) {
		return template.save(relationModel);
	}

	/*
	 * @Override public GuestProfile find(Object id) { String query =
	 * "MATCH (t:GuestProfile) WHERE t." + Constants.GUID + "={" +
	 * Constants.GUID + "} RETURN t";
	 * 
	 * Map<String, Object> param = new HashMap<String, Object>();
	 * param.put(Constants.GUID, id.toString()); Logger.debug("query is " +
	 * query);
	 * 
	 * Result<GuestProfile> r = template.query(query, param).to(type); return
	 * r.singleOrNull(); }
	 */

	@Override
	public GuestProfile findGuestForRest(GuestProfile guest) {
		String query = "MATCH (res:" + Constants.RESTAURANT_LABEL + "{guid:{"
				+ Constants.REST_GUID + "}})-[rhg:"
				+ RelationshipTypes.HAS_GUEST + "]->(t:GuestProfile) WHERE t."
				+ Constants.GUID + "={" + Constants.GUID
				+ "} AND rhg.status='ACTIVE' RETURN t";

		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Constants.GUID, guest.getGuid());
		param.put(Constants.REST_GUID, guest.getRestGuid());

		Logger.debug("query is " + query);
		Result<GuestProfile> r = template.query(query, param).to(type);
		return r.singleOrNull();
	}

	@Override
	protected List<GuestProfile> executeQuery(String query,
			Map<String, Object> params) {
		List<GuestProfile> list = new ArrayList<GuestProfile>();
		Logger.debug(query);
		params.forEach((x, y) -> Logger.debug(x + ">" + y));
		Iterator<Map<String, Object>> results = template.query(query, params)
				.iterator();
		GuestProfile profile = null, guest;
		String restGuid;
		while (results.hasNext()) {
			Map<String, Object> map = results.next();
			Logger.debug("map is " + map + " param is ----------------------"
					+ params);

			guest = template.convert(map.get("t"), GuestProfile.class);
			if (params.containsKey(Constants.ROLE_ID)
					&& params.get(Constants.ROLE_ID).equals(
							Constants.STAFF_ROLE_ID)) {
				restGuid = map.get("r.guid").toString();
				guest.setRestGuid(restGuid);
			}
			if (params.containsKey(Constants.ROLE_ID)
					&& (params.get(Constants.ROLE_ID).equals(
							Constants.STAFF_ROLE_ID) || params.get(
							Constants.ROLE_ID).equals(Constants.ADMIN_ROLE_ID) || params.get(
									Constants.ROLE_ID).equals(Constants.MANAGER_ROLE_ID) )) {
				Relationship relation = (Relationship) map.get("rel");
				try {
					guest.setFirstName((null == relation
							.getProperty(Constants.FIRST_NAME)) ? "" : relation
							.getProperty(Constants.FIRST_NAME).toString());
				} catch (NotFoundException e) {
				}

				/*
				 * try{
				 * guest.setLastName((null==relation.getProperty(Constants.LAST_NAME
				 * ))?"":relation.getProperty(Constants.LAST_NAME).toString());
				 * }catch(NotFoundException e){ }
				 */
				try {
					Object dob = relation.getProperty(Constants.DOB);
					if (null != dob)
						guest.setDob(new Date(Long.valueOf(relation
								.getProperty(Constants.DOB).toString())));
				} catch (NotFoundException e) {
				}
				try {
					Object anniversary = relation
							.getProperty(Constants.ANNIVERSARY);
					if (null != anniversary)
						guest.setAnniversary(new Date(Long.valueOf(relation
								.getProperty(Constants.ANNIVERSARY).toString())));
				} catch (NotFoundException e) {
				}
				try {
					guest.setDnd_email(Boolean.parseBoolean(relation
							.getProperty("dnd_email").toString()));
				} catch (NotFoundException e) {
				}
				try {
					guest.setDnd_mobile(Boolean.parseBoolean(relation
							.getProperty("dnd_mobile").toString()));
				} catch (NotFoundException e) {
				}
				try {
					guest.setStatus(relation.getProperty(Constants.STATUS)
							.toString());
				} catch (NotFoundException e) {
				}

				try {
					guest.setCorporate(relation.getProperty("corporate_guid")
							.toString());
				} catch (NotFoundException e) {
					guest.setCorporate(null);
				}
				try {
					guest.setIsVip(Boolean.parseBoolean(relation.getProperty(
							"is_vip").toString()));
				} catch (NotFoundException e) {
				}
				try {
					guest.setGender(relation.getProperty("gender").toString());
				} catch (NotFoundException e) {
				}
				try {
					guest.setEmailId(relation.getProperty("email_id")
							.toString());
				} catch (NotFoundException e) {
					guest.setEmailId(null);
				}
				try {
					guest.setReason(relation.getProperty("reason").toString());
				} catch (NotFoundException e) {
					guest.setReason(null);
				}
				try {
					guest.setFirstSeatedTime((null == relation
							.getProperty("first_seated")) ? null : new Date(
							Long.valueOf(relation.getProperty("first_seated")
									.toString())));
				} catch (NotFoundException e) {
				}

			}
			list.add(guest);

		}

		return list;

	}

	@Override
	protected StringBuilder getMatchClause(Map<String, Object> params) {
		if (null != params.get(Constants.ROLE_ID)
				&& params.get(Constants.ROLE_ID).equals(
						Constants.CUSTOMER_ROLE_ID)) {
			return new StringBuilder("MATCH (t:GuestProfile)");
		} else {
			return new StringBuilder(
					"MATCH (r:Restaurant )-[rel:HAS_GUEST {status : 'ACTIVE'}]->(t:GuestProfile)");
		}
	}

	@Override
	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();

		if (null != params.get(Constants.ROLE_ID)
				&& params.get(Constants.ROLE_ID).equals(
						Constants.CUSTOMER_ROLE_ID)) {
			query.append("RETURN DISTINCT t");
		} else {
			query.append("RETURN DISTINCT rel,r.guid,t");
		}
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);
	}

	@Override
	public GuestProfile update(GuestProfile guest) {

		Map<String, Object> params = new HashMap<String, Object>();
		String searchParams = null;
		if (null != guest.getFirstName()) {
			searchParams = guest.getFirstName().replaceAll(" ", "");
		}
		/*
		 * if(null!=guest.getLastName()){
		 * searchParams=searchParams+guest.getLastName().replaceAll(" ", ""); }
		 */
		if (null != guest.getMobile()) {
			searchParams = searchParams + guest.getMobile();
		}
		guest.setSearchParams(searchParams);
		params.put(Constants.GUEST_GUID, guest.getGuid());
		params.put(Constants.PROP_MAP,
				getGraphProperty(UtilityMethods.introspect(guest)));

		StringBuilder query = new StringBuilder();
		query.append("MATCH(t:" + Constants.GUESTPROFILE_LABEL + "{guid:{"
				+ Constants.GUEST_GUID + "}}) ");
		query.append("SET t= { " + Constants.PROP_MAP + " } ");
		query.append("return t");

		Iterator<Map<String, Object>> results = executeWriteQuery(
				query.toString(), params).iterator();
		GuestProfile profile = null;
		while (results.hasNext()) {
			Map<String, Object> map = results.next();
			Logger.debug("map is " + map);
			profile = template.convert(map.get("t"), GuestProfile.class);
		}

		return profile;

	}

	@Override
	protected StringBuilder getWhereClause(Map<String, Object> params) {

		// Long dob = 0L, anniversary = 0L;
		String restId = "", status = "", first_name = "", first_name_starts_with = "";
		String prefix = "t";
		if (null != params.get(Constants.ROLE_ID)
				&& (params.get(Constants.ROLE_ID).equals(
						Constants.STAFF_ROLE_ID) || params.get(
						Constants.ROLE_ID).equals(Constants.ADMIN_ROLE_ID) || params.get(
								Constants.ROLE_ID).equals(Constants.MANAGER_ROLE_ID))) {

			if (params.containsKey(Constants.EMAIL_LIKE)) {
				params.remove(Constants.EMAIL_LIKE);
			}
			if (null != params.get(Constants.STATUS)
					&& params.containsKey(Constants.STATUS)) {
				status = params.get(Constants.STATUS).toString();
				params.remove(Constants.STATUS);
			}
			if (null != params.get(Constants.FIRSTNAME)
					&& params.containsKey(Constants.FIRSTNAME)) {
				first_name = params.get(Constants.FIRSTNAME).toString();
				params.remove(Constants.FIRSTNAME);
			}

			if (null != params.get(Constants.FIRST_NAME_STARTS_WITH)
					&& params.containsKey(Constants.FIRST_NAME_STARTS_WITH)) {
				first_name_starts_with = params.get(
						Constants.FIRST_NAME_STARTS_WITH).toString();
				params.remove(Constants.FIRST_NAME_STARTS_WITH);
			}

		}

		if (null != params.get(Constants.REST_GUID)
				&& params.containsKey(Constants.REST_GUID)) {
			restId = params.get(Constants.REST_GUID).toString();
			params.remove(Constants.REST_GUID);
		}

		StringBuilder query = super.getWhereClause(params, prefix);

		if (!restId.equals("")) {
			play.Logger.debug("restid  is " + restId);
			addPrefix(query);
			query.append("r.guid= '" + restId + "' ");
		}

		if (!status.equals("")) {
			addPrefix(query);
			query.append('(');
			if (status.contains("[") && status.contains("]")) {
				status = status.substring(1, status.length() - 1);
			}
			String[] result = status.split(",");
			for (String status_index : result) {
				if (status_index.startsWith(" ")) {
					status_index = status_index.substring(1);
				}
				query.append("rel.status='" + status_index + "' OR");
			}
			if (query.toString().contains(Constants.OR))
				query = new StringBuilder(
						query.substring(0, query.length() - 2));
			query.append(" ) ");

		}

		if (!first_name.equals("")) {
			play.Logger.debug("first_name  is " + first_name);
			addPrefix(query);
			query.append("rel.first_name= '" + first_name + "' ");
		}

		if (!first_name_starts_with.equals("")) {
			play.Logger.debug("first_name  is " + first_name_starts_with);
			addPrefix(query);
			// params.put("firstName", "~('(?i).*"
			// +first_name_starts_with+".*')");
			query.append("rel.first_name=~('" + first_name_starts_with + ".*')");

		}

		// if freeSearch parameter comes in param string then it searches for
		// first name,last name,email and mobile no having given value
		if (params.containsKey(Constants.FREE_SEARCH)) {
			// String regularExpString = Constants.PRE_LIKE_STRING
			// + params.get(Constants.FREE_SEARCH)
			// + Constants.POST_LIKE_STRING;
			// params.put(Constants.FREE_SEARCH, regularExpString);
			if (null != params.get(Constants.ROLE_ID)
					&& (params.get(Constants.ROLE_ID).equals(
							Constants.STAFF_ROLE_ID) || params.get(
							Constants.ROLE_ID).equals(Constants.ADMIN_ROLE_ID) || params.get(
									Constants.ROLE_ID).equals(Constants.MANAGER_ROLE_ID))) {
				String prefixRel = "rel";
				query = applyFreeSearch(params.get(Constants.FREE_SEARCH)
						.toString(), query, prefixRel);
			}

		}
		System.out.println("query->" + query);
		return query;
	}

	/**
	 * private method that creates query for like parameters first name,last
	 * name,email and mobile no
	 * 
	 * @param likeValue
	 * @param query
	 * @return
	 */
	private StringBuilder applyFreeSearch(String likeValue,
			StringBuilder query, String prefix) {
		String[] searchParam = likeValue.split(" ");
		// String params = "";

		if (query.toString().contains(Constants.WHERE)) {
			query.append(" AND (");
			for (int i = 0; i < searchParam.length; i++) {
				if (searchParam[i].trim().length() > 0) {
					query.append(prefix + ".search_params=~('(?i).*"
							+ searchParam[i].replaceAll("[^a-zA-Z0-9]", "")
							+ ".*') AND ");
				}

			}
			query = new StringBuilder(query.substring(0, query.length() - 4));
			query.append(')'); // //
			// t.email_id=~{" + likeValue + "})");
		} else {
			query.append(" (");
			for (int i = 0; i < searchParam.length; i++) {
				if (searchParam[i].trim().length() > 0) {
					query.append(prefix + ".search_params=~('(?i).*"
							+ searchParam[i].replaceAll("[^a-zA-Z0-9]", "")
							+ ".*') AND ");
				}

			}
			query = new StringBuilder(query.substring(0, query.length() - 4));
			query.append(')');
			// //
			// t.email_id=~{" + likeValue + "})");
		}
		return query;
	}

	/**
	 * Method to create relationship of a restaurant with guest
	 */
	@Override
	public String addRestaurantGuest(Restaurant rest, GuestProfile guest) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.REST_GUID, rest.getGuid());
		params.put(Constants.GUEST_GUID, guest.getGuid());
		// Long id = 0l;
		String searchParamGuest = guest.getFirstName().replaceAll(" ", "");
		String query = "MATCH (r:Restaurant {guid:{" + Constants.REST_GUID
				+ "}}),(t:GuestProfile) WHERE t.guid={" + Constants.GUEST_GUID
				+ "} \n";
		query = query + "MERGE (r)-[rel:HAS_GUEST]->(t) " + "SET ";
		if (null != guest.getFirstName()) {
			query = query + " rel.first_name='" + guest.getFirstName() + "',";
		}
		query = query + " rel.created_dt='"
				+ Calendar.getInstance().getTimeInMillis() + "',";
		query = query + " rel.is_vip=" + guest.getIsVip() + ",";
		if (null != guest.getReason()) {
			query = query + " rel.reason='" + guest.getReason() + "',";
		} else {
			query = query + " rel.reason=NULL ,";
		}
		if (null != guest.getEmailId()) {
			query = query + " rel.email_id='" + guest.getEmailId() + "',";
		} else {

			query = query + " rel.email_id=NULL ,";
		}

		if (null != guest.getGender()) {
			query = query + " rel.gender='" + guest.getGender() + "',";
		}
		query = query + " rel.dnd_mobile=" + guest.isDnd_mobile() + ",";
		query = query + " rel.dnd_email=" + guest.isDnd_email() + ",";
		
		if (null != guest.getStatus()) {
			query = query + " rel.status='" + guest.getStatus() + "',";
		}
		if (null != guest.getDob()) {
			query = query + " rel.dob='" + guest.getDob().getTime() + "',";
		}
		if (null != guest.getAnniversary()) {
			query = query + " rel.anniversary='"
					+ guest.getAnniversary().getTime() + "',";
		}
		if (null != guest.getCorporate()) {
			query = query + " rel.corporate_guid='" + guest.getCorporate()
					+ "',";
		} else {
			query = query + " rel.corporate_guid = NULL,";
		}
		if (null != guest.getCorporateName()) {
			query = query + " rel.corporate_name='" + guest.getCorporateName()
					+ "',";
		} else {
			query = query + " rel.corporate_name = NULL,";
		}
		query = query + " rel.updated_dt='"
				+ Calendar.getInstance().getTimeInMillis() + "',";
		query = query + "rel.review_count='0',";
		query = query + "rel.cumulative_rating='0',";
		query = query + " rel.search_params='" + searchParamGuest/*
																 * +(null==guest.
																 * getLastName
																 * ()?"":guest.
																 * getLastName
																 * ())
																 */
				+ guest.getMobile() + "'";
		executeWriteQuery(query, params);
		return guest.getGuid();
	}

	/**
	 * Method to create relationship of a restaurant with guest via Consumer APP
	 */
	@Override
	public String updateRestaurantGuest(GuestProfile guest,
			GuestProfile tempCustomer) {
		Map<String, Object> params = new HashMap<String, Object>();
		String searchParamGuest = guest.getFirstName().replaceAll(" ", "");
		params.put(Constants.GUEST_GUID, guest.getGuid());
		params.put(Constants.NEW_GUEST_GUID, tempCustomer.getGuid());
		/* Deleting Temp Guest Node first */
		String query1 = "MATCH (t1:GuestProfile) WHERE t1.guid={"
				+ Constants.NEW_GUEST_GUID + "}  DELETE t1 \n";
		executeWriteQuery(query1, params);
		/* Updating Guest Node all having relationship with Restaurant (s) */
		String query = "MATCH (r:Restaurant)-[rel:HAS_GUEST]->(t:GuestProfile {guid:{"
				+ Constants.GUEST_GUID + "}}) \n" + " SET ";
		if (null != guest.getFirstName()) {
			query = query + " rel.first_name='" + guest.getFirstName() + "',";
		}
		// if(null!=guest.getLastName()) { query=query+
		// " rel.last_name='"+guest.getLastName()+"',"; }
		if (guest.getIsVip()) {
			query = query + " rel.is_vip=" + guest.getIsVip() + ",";
		}
		if (null != guest.getReason()) {
			query = query + " rel.reason='" + guest.getReason() + "',";
		}
		if (null != guest.getGender()) {
			query = query + " rel.gender='" + guest.getGender() + "',";
		}
		if (guest.isDnd_mobile()) {
			query = query + " rel.dnd_mobile=" + guest.isDnd_mobile() + ",";
		}
		if (guest.isDnd_email()) {
			query = query + " rel.dnd_email=" + guest.isDnd_email() + ",";
		}
		query = query + " rel.updated_dt='"
				+ Calendar.getInstance().getTimeInMillis() + "',";
		if (null != guest.getStatus()) {
			query = query + " rel.status='" + guest.getStatus() + "',";
		}
		if (null != guest.getDob()) {
			query = query + " rel.dob='" + guest.getDob().getTime() + "',";
		}
		if (null != guest.getAnniversary()) {
			query = query + " rel.anniversary='"
					+ guest.getAnniversary().getTime() + "',";
		}
		if (null != guest.getCorporate()) {
			query = query + " rel.corporate_guid='" + guest.getCorporate()
					+ "',";
		}
		query = query + " rel.search_params='" + searchParamGuest/*
																 * +(null==guest.
																 * getLastName
																 * ()?"":guest.
																 * getLastName
																 * ())
																 */
				+ guest.getMobile() + "'";
		executeWriteQuery(query, params);
		return guest.getGuid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.clicktable.dao.intf.CustomerDao#getRestaurantGuest(java.lang.Class,
	 * java.util.Map)
	 */
	@Override
	public List<GuestProfile> getRestaurantGuest(Class<GuestProfile> class1,
			Map<String, Object> params) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub

		// Long id = 0l;
		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:Restaurant)-[rel:HAS_GUEST]->(t:GuestProfile) WHERE r.guid={"
				+ Constants.REST_GUID + "} \n");
		query.append(" AND t.guid={" + Constants.GUID
				+ "} RETURN  DISTINCT rel,t");

		System.out
				.println(" ************ GET ALL  GUEST WRT RESTAURANT******************** \n "
						+ query.toString()
						+ params.get(Constants.REST_GUID).toString());
		Result<Map<String, Object>> result = executeWriteQuery(
				query.toString(), params);// template.query(query,
		// null);
		Logger.debug("query executed,Result is " + result);
		Iterator<Map<String, Object>> itr = result.iterator();
		List<GuestProfile> guestProfile = new ArrayList<GuestProfile>();
		while (itr.hasNext()) {
			Map<String, Object> map = itr.next();
			Relationship relation = (Relationship) map.get("rel");
			GuestProfile guest = template.convert(map.get("t"),
					GuestProfile.class);
			try {
				guest.setFirstName((null == relation
						.getProperty(Constants.FIRST_NAME)) ? "" : relation
						.getProperty(Constants.FIRST_NAME).toString());
			} catch (NotFoundException e) {
				// guest.setLastName("");
			}
			/*
			 * try{
			 * guest.setLastName((null==relation.getProperty(Constants.LAST_NAME
			 * ))?"":relation.getProperty(Constants.LAST_NAME).toString());
			 * }catch(NotFoundException e){ //guest.setLastName(""); }
			 */
			try {
				guest.setDob(new Date(Long.valueOf(relation.getProperty(
						Constants.DOB).toString())));
			} catch (NotFoundException e) {
				// guest.setDob(null);
			}
			try {
				guest.setAnniversary(new Date(Long.valueOf(relation
						.getProperty(Constants.ANNIVERSARY).toString())));
			} catch (NotFoundException e) {
				// guest.setAnniversary(null);
			}
			try {
				guest.setDnd_email(Boolean.parseBoolean(relation.getProperty(
						"dnd_email").toString()));
			} catch (NotFoundException e) {
				// guest.setDnd_email(false);setIsVip
			}
			try {
				guest.setIsVip(Boolean.parseBoolean(relation.getProperty(
						"is_vip").toString()));
			} catch (NotFoundException e) {
				// guest.setDnd_email(false);setIsVip
			}
			try {
				guest.setDnd_mobile(Boolean.parseBoolean(relation.getProperty(
						"dnd_mobile").toString()));
			} catch (NotFoundException e) {
				// guest.setDnd_mobile(false);
			}
			
			try {
				guest.setStatus(relation.getProperty(Constants.STATUS)
						.toString());
			} catch (NotFoundException e) {
				// guest.setStatus(null);
			}

			try {
				guest.setCorporate(relation.getProperty("corporate_guid")
						.toString());
			} catch (NotFoundException e) {
				// guest.setCorporate_name(null);
			}
			try {
				guest.setCreatedDate(new Date(Long.valueOf(relation.getProperty("created_dt")
						.toString())));
			} catch (NotFoundException e) {
				// guest.setCorporate_name(null);
			}
			try {
				guest.setUpdatedDate(new Date(Long.valueOf(relation.getProperty("updated_dt")
						.toString())));
			} catch (NotFoundException e) {
				// guest.setCorporate_name(null);
			}
			guestProfile.add(guest);
		}
		return guestProfile;

	}

	/**
	 * Method to delete relationship of a guest with tag
	 */
	@Override
	public Boolean deleteGuestHasTag(String restGuid, String guestGuid,
			String tagsToDelete) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.REST_GUID, restGuid);
		params.put(Constants.GUEST_GUID, guestGuid);

		String query = "MATCH (g:GuestProfile{guid:{" + Constants.GUEST_GUID
				+ "}})-[r:GUEST_HAS_TAG]->(t:Tag) WHERE r.rest_guid={"
				+ Constants.REST_GUID + "} ";

		String[] tagTypeArr = tagsToDelete.split(",");
		if (tagTypeArr.length > 0) {
			params.put(Constants.TAG_GUIDS, tagTypeArr);
			query = query + "AND t.type IN { " + Constants.TAG_GUIDS + " }";
		}
		query = query + " DELETE r";

		Result<Map<String, Object>> result = executeWriteQuery(query, params);// template.query(query,
																				// null);
		Logger.debug("query executed,Result is " + result);

		return true;
	}

	/**
	 * Method to add relationship of a guest with tag
	 */
	@Override
	public Boolean addGuestHasTag(String restGuid, String guestGuid,
			List<TagModelOld> tagListWithGuid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.REST_GUID, restGuid);
		params.put(Constants.GUEST_GUID, guestGuid);

		String query = "MATCH (g:GuestProfile{guid:{" + Constants.GUEST_GUID
				+ "}}),(t:Tag)";
		if (tagListWithGuid != null && !tagListWithGuid.isEmpty()) {
			List<String> guids = getGuids(tagListWithGuid);
			query = query + " WHERE t.guid IN {" + guids + " }";
		}

		query = query + "\n MERGE (g)-[r:GUEST_HAS_TAG]->(t) SET r.rest_guid={"
				+ Constants.REST_GUID + "}";

		Result<Map<String, Object>> result = executeWriteQuery(query, params);
		Logger.debug("query executed,Result is " + result);
		return true;
	}

	@Override
	public Map<String, Integer> getGuestCountByTagsForEvent(
			EventPromotion promotion) {

		StringBuilder query = new StringBuilder();
		Map<String, Integer> output = new HashMap<String, Integer>();
		Map<String, Object> params = new HashMap<String, Object>();

		createQueryForFilterGuest(promotion, query, params);

		query.append(" WITH DISTINCT(g), (CASE (HAS(hg.dnd_mobile)) WHEN true THEN hg.dnd_mobile ELSE false END) as dnd_mob,");
		query.append(" (CASE(HAS(g.is_dnd_user_enable)) WHEN true THEN  g.is_dnd_user_enable ELSE false END) as dnd_user,");
		query.append(" (CASE(HAS(g.is_dnd_trai_enable)) WHEN true THEN  g.is_dnd_trai_enable ELSE false END) as dnd_trai,");

		query.append(" 1 as c, 2 as d");
		query.append(" RETURN ");
		query.append(" CASE (dnd_mob OR dnd_user OR dnd_trai)");
		query.append(" WHEN true THEN collect(c)");
		query.append(" ELSE collect(d)");
		query.append(" END as count");
		params.put(getPropertyName(Constants.REST_GUID),
				promotion.getRestaurantGuid());
		params.put(getPropertyName(Constants.RESERVATION_STATUS),
				Constants.FINISHED);
		params.put(getPropertyName(Constants.STATUS), Constants.ACTIVE_STATUS);
		Logger.debug("--------------------------");
		Logger.debug(query.toString());
		Logger.debug("--------------------------");
		Logger.debug(params+"--------------params------------");
		
		Result<Map<String, Object>> result = template.query(query.toString(),
				params);
		Iterator<Map<String, Object>> itr = result.iterator();

		Integer count = 0;
		output.put("dnd", 0);
		while (itr.hasNext()) {
			Map<String, Object> map = itr.next();
			List<Integer> nos = (List<Integer>) map.get("count");
			if (nos.size() > 0)
				if (nos.get(0) == 1)
					output.put("dnd", nos.size());

			count += nos.size();
		}
		output.put("count", count);
		return output;

	}

	@Override
	public Integer totalGuestCount(String restId) {
		StringBuilder query = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		query.append("MATCH (r:Restaurant{guid:{"
				+ getPropertyName(Constants.REST_GUID)
				+ "}})-[hg:HAS_GUEST{status:{"
				+ getPropertyName(Constants.STATUS)
				+ "}}]->(g:GuestProfile{dummy:false,status:{"
				+ getPropertyName(Constants.STATUS) + "}})");
		query.append("return Count(DISTINCT g) as c");
		params.put(getPropertyName(Constants.REST_GUID), restId);
		params.put(getPropertyName(Constants.STATUS), Constants.ACTIVE_STATUS);
		Map<String, Object> result = template.query(query.toString(), params)
				.singleOrNull();
		if (result != null)
			return (Integer) result.get("c");
		else
			return null;
	}

	@Override
	public List<String> getGuestExceptDummyGuest(Class<GuestProfile> class1,
			Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:Restaurant{guid:{restaurantGuid}})-[hg:"
				+ RelationshipTypes.HAS_GUEST
				+ "]->(g:GuestProfile) WHERE g.mobile<>'0000000000' ");

		query.append("return DISTINCT g.guid");

		Result<Map<String, Object>> r = template
				.query(query.toString(), params);
		Iterator<Map<String, Object>> itr = r.iterator();
		List<String> guests = new ArrayList<>();
		while (itr.hasNext()) {
			Map<String, Object> map = itr.next();
			String guid = (String) map.get("g.guid");
			guests.add(guid);
		}

		return guests;
	}

	@Override
	public List<GuestProfile> getGuestsForOtherRest(Class<GuestProfile> class1,
			List<String> guestForRest, String restGuid) {
		StringBuilder query = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.REST_GUID, restGuid);
		params.put(Constants.GUEST_GUID, guestForRest);
		query.append("MATCH (r:Restaurant)-[hg:" + RelationshipTypes.HAS_GUEST
				+ "]->(g:GuestProfile) WHERE r.guid<>{" + Constants.REST_GUID
				+ "} AND g.guid IN {" + Constants.GUEST_GUID + "}");
		query.append(" AND hg IS NULL return DISTINCT g");

		Result<Map<String, Object>> r = template
				.query(query.toString(), params);
		Iterator<Map<String, Object>> itr = r.iterator();
		List<GuestProfile> guests = new ArrayList<>();
		while (itr.hasNext()) {
			Set<Entry<String, Object>> entrySet = itr.next().entrySet();
			for (Map.Entry<String, Object> entry : entrySet) {
				GuestProfile guest = template.convert(entry.getValue(),
						GuestProfile.class);
				guests.add(guest);
			}
		}

		return guests;
	}

	@Override
	public Boolean isGuestForRest(String guestGuid, String restaurantGuid) {
		String query = "MATCH (res:" + Constants.RESTAURANT_LABEL + "{guid:{"
				+ Constants.REST_GUID + "}})-[rhg:"
				+ RelationshipTypes.HAS_GUEST + "]->(t:GuestProfile) WHERE t."
				+ Constants.GUID + "={" + Constants.GUID
				+ "} AND t.status='ACTIVE' RETURN t";

		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Constants.GUID, guestGuid);
		param.put(Constants.REST_GUID, restaurantGuid);

		Logger.debug("query is " + query);
		Result<GuestProfile> r = template.query(query, param).to(type);

		return (r.singleOrNull() != null);
	}

	@Override
	public Map<String, Object> validateRestGuestExist(Map<String, Object> params) {
		Map<String, Object> paramsReturn = new HashMap<String, Object>();
		String query = "Match (guest:GuestProfile {mobile:{"
				+ Constants.MOBILE
				+ "} , status:{"
				+ Constants.ACTIVE_STATUS
				+ "}}) \n"
				+ "OPTIONAL MATCH (guest)<-[r1:HAS_GUEST]-(rest:Restaurant {guid:{"
				+ Constants.REST_GUID
				+ "}}) \n"
				+ " RETURN rest as restGuid ,guest as guestGuid ,r1.status as status";
		System.out.println("---------Query -------" + query.toString());
		Map<String, Object> map;// = new HashMap<String, Object>();

		Result<Map<String, Object>> results = template.query(query.toString(),
				params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			map = i.next();
			paramsReturn.put(
					Constants.REST_NODE,
					(map.get("restGuid") != null) ? template.convert(
							map.get("restGuid"), Restaurant.class) : null);
			paramsReturn.put(
					Constants.GUEST_NODE,
					(map.get("guestGuid") != null) ? template.convert(
							map.get("guestGuid"), GuestProfile.class) : null);
			paramsReturn.put(Constants.STATUS,
					(map.get("status") != null) ? map.get("status").toString()
							: null);

		}

		return paramsReturn;

	}

	@Override
	public Map<String, Object> validateGuestExist(Map<String, Object> params) {
		// TODO Auto-generated method stub
		Map<String, Object> paramsReturn = new HashMap<String, Object>();
		String query = "Match (guest:GuestProfile {mobile:{" + Constants.MOBILE
				+ "}})-[" + RelationshipTypes.HAS_GUEST + "]-(r:Restaurant) \n"
				+ " RETURN guest as guestGuid ORDER BY guest.created_dt ASC";
		System.out.println("---------Query -------" + query.toString());
		Map<String, Object> map;// = new HashMap<String, Object>();

		Result<Map<String, Object>> results = template.query(query.toString(),
				params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			map = i.next();
			if (map.get("guestGuid") != null) {
				GuestProfile guest = template.convert(map.get("guestGuid"),
						GuestProfile.class);
				paramsReturn.put(Constants.GUEST_NODE, (guest != null) ? guest
						: null);
			}
		}

		return paramsReturn;

	}

	public GuestProfile findGuest(Map<String, Object> params) {
		// TODO Auto-generated method stub
		GuestProfile guest = null;
		String query = "Match (guest:GuestProfile { \n";
		if (null != params && null != params.get(Constants.GUID)) {
			query = query + " guid:{" + Constants.GUID + "},";
		}
		if (null != params && null != params.get(Constants.STATUS)) {
			query = query + " status:{" + Constants.STATUS + "},";
		}
		if (null != params && null != params.get(Constants.MOBILE)) {
			query = query + " mobile:{" + Constants.MOBILE + "},";
		}
		if (null != params && null != params.get(Constants.FACEBOOK_ID)) {
			query = query + " fid:{" + Constants.FACEBOOK_ID + "},";
		}
		if (null != params && null != params.get(Constants.GOOGLE_ID)) {
			query = query + " gid:{" + Constants.GOOGLE_ID + "},";
		}
		query = query.substring(0, query.length() - 1);
		query = query + "  }) RETURN guest as guestGuid";

		System.out.println("---------Query -------" + query.toString());
		Map<String, Object> map;// = new HashMap<String, Object>();

		Result<Map<String, Object>> results = template.query(query.toString(),
				params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			map = i.next();
			if (map.get("guestGuid") != null) {
				guest = template.convert(map.get("guestGuid"),
						GuestProfile.class);
				return guest;

			}

		}

		return guest;
	}

	@Override
	public GuestProfile deleteGuest(Map<String, Object> params) {
		// TODO Auto-generated method stub
		GuestProfile guest = null;
		StringBuilder query = new StringBuilder("Match (rest:"
				+ Constants.RESTAURANT_LABEL + "{ \n");
		query.append(" guid:{" + Constants.REST_GUID + "}}");
		query.append(")-[r:HAS_GUEST]-(guest:" + Constants.GUESTPROFILE_LABEL
				+ "{ \n");
		query.append(" guid:{" + Constants.GUID + "}})");
		query.append(" SET r.status={" + Constants.STATUS/*
														 * +"} and r.updated_dt={"
														 * +
														 * Constants.UPDATED_DATE
														 */+ "}");
		System.out.println("---------Query -------" + query.toString());
		System.out.println(params);
		// Map<String, Object> map;// = new HashMap<String, Object>();
		template.query(query.toString(), params);
		// Iterator<Map<String, Object>> i = results.iterator();
		return guest;
	}

	@Override
	public void reassign(List<GuestProfile> guestProfileList) throws ClicktableException {
		// TODO Auto-generated method stub
		String guestProfileString = "";
		StringBuffer dndEnable = new StringBuffer();
		StringBuffer dndDisable = new StringBuffer();
		StringBuilder query = new StringBuilder();
		for (GuestProfile guestProfile : guestProfileList) {
			guestProfileString = guestProfileString +"91"+guestProfile.getMobile()
					+ ",";
		}
		query = new StringBuilder(guestProfileString.substring(0,
				guestProfileString.length() - 1));
		DNDResponse response;
			response = UtilityMethods.callDndToVerifyMobilePhone(query
					.toString());
		
		if(response.getResponse().getStatus().equalsIgnoreCase(Constants.SUCCESS.toLowerCase())){
			List<com.clicktable.response.Result> dndList=java.util.Arrays.asList(response.getData().getResult());
			for(com.clicktable.response.Result result : dndList){
				if(result.getStatus().equalsIgnoreCase(Constants.DND)){
					if(result.getPhone().startsWith("91")){
						dndEnable.append("'" + result.getPhone().substring(2) + "',");	
					}
					
				}else if(result.getStatus().equalsIgnoreCase(Constants.UNDND)){
					if(result.getPhone().startsWith("91")){
					dndDisable.append("'" + result.getPhone().substring(2) + "',");
					}
				}
			}
			
			if (dndEnable.toString().length() > 0) {
				dndEnable = new StringBuffer(dndEnable.substring(0,
						dndEnable.length() - 1));
				String query1 = "MATCH (g:GuestProfile) where g.mobile in ["
						+ dndEnable + "] SET g.is_dnd_trai_enable=true ";
				log.info(" Printing query " + query1);
				template.query(query1, null);
			}
			if (dndDisable.toString().length() > 0) {
				dndDisable = new StringBuffer(dndDisable.substring(0,
						dndDisable.length() - 1));
				String query1 = "MATCH (g:GuestProfile) where g.mobile in ["
						+ dndDisable + "] SET g.is_dnd_trai_enable=false ";
				log.info(" Printing query " + query1);
				template.query(query1, null);
			}
		}else{
			log.error("Scrubbing Error "+ response.toString());
		 }
		
		
	}

	public StringBuilder handleOrderBy(StringBuilder query,
			Map<String, Object> params) {

		// if orderBy parameter comes in param string then it applies order by
		// query on the basis of comma separated parameters
		String prefix = "rel";
		if (null != params.get(Constants.ROLE_ID)
				&& params.get(Constants.ROLE_ID).equals(
						Constants.CUSTOMER_ROLE_ID)) {
			prefix = "t";
		}

		if ((params.containsKey(Constants.ORDER_BY))
				&& (!params.get(Constants.ORDER_BY).equals(""))) {
			query.append(" ORDER BY ");
			String[] orderParams = params.get(Constants.ORDER_BY).toString()
					.split(",");

			String queryString = "";
			Field field;
			for (String fieldName : orderParams) {

				if (null != (field = UtilityMethods.getClassField(fieldName,
						type))) {
					if (field.getType().isAssignableFrom(String.class)) {
						queryString = queryString + "LOWER(" + prefix + "."
								+ getPropertyName(fieldName) + "),";
						break;
					} else if (field.getType().isAssignableFrom(Date.class)) {
						queryString = queryString + "toInt(" + prefix + "."
								+ getPropertyName(fieldName) + "),";
						break;
					}

				}

				queryString = queryString + "" + prefix + "."
						+ getPropertyName(fieldName) + ",";

			}

			query.append(queryString.substring(0, queryString.length() - 1));

			// if orderPreference parameter comes in param string then it
			// applies ascending or descending order accoring to value given
			// otherwise it gives in ascending order
			if (params.containsKey(Constants.ORDER_PREFERENCE)) {
				query.append(" " + params.get(Constants.ORDER_PREFERENCE));
			}
		}
		return query;
	}

	public StringBuilder handlePromotionOrderBy(StringBuilder query,
			Map<String, Object> params) {

		if ((params.containsKey(Constants.ORDER_BY))
				&& (!params.get(Constants.ORDER_BY).equals(""))) {

			String[] orderParams = params.get(Constants.ORDER_BY).toString()
					.split(",");

			String queryString = "";
			Field field;
			for (String fieldName : orderParams) {

				if (null != (field = UtilityMethods.getClassField(fieldName,
						type))) {

					query.append(" ORDER BY ");
					if (field.getType().isAssignableFrom(String.class)) {
						queryString = queryString + "LOWER("
								+ getPropertyName(fieldName) + "),";
						break;
					} else if (field.getType().isAssignableFrom(Date.class)) {
						queryString = queryString + "toInt("
								+ getPropertyName(fieldName) + "),";
						break;
					}

				}

				Logger.info("Invalid Values passed in orderBy!");

			}

			if (!queryString.isEmpty())
				query.append(queryString.substring(0, queryString.length() - 1));

			// if orderPreference parameter comes in param string then it
			// applies ascending or descending order accoring to value given
			// otherwise it gives in ascending order
			if (params.containsKey(Constants.ORDER_PREFERENCE)) {
				query.append(" " + params.get(Constants.ORDER_PREFERENCE));
			}
		}
		return query;
	}

	/*
	 * private void appendStartWithQuery(StringBuilder query, Entry<String,
	 * Object> entry, String prefix) {
	 * 
	 * String fieldName = null; if
	 * (entry.getKey().endsWith(Constants.START_WITH)) fieldName =
	 * entry.getKey().substring(0, entry.getKey().length() -
	 * Constants.START_WITH.length()); else fieldName =
	 * entry.getKey().substring(0, entry.getKey().length() -
	 * Constants.STARTS_WITH.length()); Field field; if (null != (field =
	 * UtilityMethods.getClassField(fieldName, type)) &&
	 * field.getType().isAssignableFrom(String.class)) { query.append(" " +
	 * prefix + "." + getPropertyName(fieldName) + " =~{" + entry.getKey() +
	 * "} AND"); }
	 * 
	 * }
	 */

	/**
	 * Method to set first seated time of guest
	 */
	@Override
	public Long addFirstSeatedTime(String restGuid, String guestGuid,
			Long seatedTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.REST_GUID, restGuid);
		params.put(Constants.GUEST_GUID, guestGuid);
		Long firstSeated = 0l;
		String query = "MATCH (r:Restaurant {guid:{" + Constants.REST_GUID
				+ "}})-[rel:HAS_GUEST]->(t:GuestProfile) WHERE t.guid={"
				+ Constants.GUEST_GUID + "}  WITH rel,";
		query = query
				+ " CASE WHEN NOT HAS(rel.first_seated) THEN toInt("
				+ seatedTime
				+ ") ELSE toInt(rel.first_seated) END AS seatedTime \n SET rel.first_seated = toInt(seatedTime)";

		query = query + " RETURN toInt(seatedTime) AS firstSeated";
		Result<Map<String, Object>> results = template.query(query.toString(),
				params);
		Iterator<Map<String, Object>> i = results.iterator();
		Map<String, Object> map = new HashMap<>();
		while (i.hasNext()) {
			map = i.next();
			if (map.get("firstSeated") != null) {
				firstSeated = Long.valueOf(map.get("firstSeated").toString());
			}
		}
		return firstSeated;
	}

	private void createQueryForFilterGuest(EventPromotion promotion,
			StringBuilder query, Map<String, Object> params) {

		Boolean where = false;

		query.append("MATCH (r:" + Constants.RESTAURANT_LABEL + "{guid:{"
				+ getPropertyName(Constants.REST_GUID)
				+ "}})-[hg:HAS_GUEST{status:{"
				+ getPropertyName(Constants.STATUS) + "}");

		if (!promotion.getGuestType().equalsIgnoreCase(Constants.BOTH)) {
			query.append(",is_vip:{" + Constants.GUEST_TYPE + "}");
			params.put(
					Constants.GUEST_TYPE,
					promotion.getGuestType().equalsIgnoreCase(
							Constants.VIP));
		}
		query.append("}]->(g:GuestProfile");
		query.append("{dummy:false");
		query.append(",status:{" + getPropertyName(Constants.STATUS) + "}");
		query.append(",isd_code:'91'");
		if (!promotion.getGender().equalsIgnoreCase(Constants.BOTH)) {
			query.append(",gender:{" + Constants.GENDER + "}");
			params.put(Constants.GENDER, promotion.getGender());
		}
		query.append("})");

		if (!promotion.getTagGuids().isEmpty()) {
			query.append("-[ght:GUEST_HAS_TAG]->(t:Tag) ");
			query.append("WHERE t.guid in {guids}");
			params.put("guids", promotion.getTagGuids());
			query.append("MATCH (resv:Reservation{reservation_status:{"
					+ getPropertyName(Constants.RESERVATION_STATUS)
					+ "},rest_guid:{" + getPropertyName(Constants.REST_GUID)

					+ "}})<-[ghr:" + Constants.GUEST_HAS_RESV + "]-(g)");

		} else if (promotion.getTagGuids().isEmpty()
				&& promotion.getVisitedInLast() > 0) {
			query.append("-[ghr:" + Constants.GUEST_HAS_RESV
					+ "]->(resv:Reservation{reservation_status:{"
					+ getPropertyName(Constants.RESERVATION_STATUS)
					+ "},rest_guid:{" + getPropertyName(Constants.REST_GUID)
					+ "}})");

		}

		if (promotion.getVisitedInLast() > 0) {
			/*
			 * if (query.toString().contains("WHERE")) query.append(" AND");
			 * else
			 */
			query.append(" WHERE");

			query.append(" toInt(resv.act_end_time)>{"
					+ Constants.RESERVED_AFTER + "} ");
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("IST"));
			cal.add(Calendar.MONTH, -promotion.getVisitedInLast());
			Date reservedAfter = cal.getTime();
			params.put(Constants.RESERVED_AFTER, reservedAfter);
			where = true;
		}

		if (!promotion.getCorporatesGuids().isEmpty()) {
			if (where)
				query.append(" AND");
			else
				query.append(" WHERE");
			query.append(" hg.corporate_guid in {" + Constants.CORPORATE_GUIDS
					+ "}");
			params.put(Constants.CORPORATE_GUIDS,
					promotion.getCorporatesGuids());
			where = true;
		}

		if (promotion.getBirthdayAfter() != null
				&& promotion.getBirthdayBefore() != null) {
			if (where)
				query.append(" AND");
			else
				query.append(" WHERE");
			where = true;
			query.append(" HAS(hg.dob)  AND toInt(hg.dob) >= {"
					+ Constants.DOB_AFTER + "}");
			query.append(" AND toInt(hg.dob) <= {" + Constants.DOB_BEFORE + "}");
			params.put(getPropertyName(Constants.DOB_AFTER),
					promotion.getBirthdayAfter());
			params.put(getPropertyName(Constants.DOB_BEFORE),
					promotion.getBirthdayBefore());

		}
		if (promotion.getAnniversaryAfter() != null
				&& promotion.getAnniversaryBefore() != null) {
			if (where)
				query.append(" AND");
			else
				query.append(" WHERE");
			where = true;
			query.append(" HAS(hg.dob)  AND toInt(hg.dob) >= {"
					+ Constants.ANNIVERSARY_AFTER + "}");
			query.append(" AND toInt(hg.dob) <= {"
					+ Constants.ANNIVERSARY_BEFORE + "}");
			params.put(getPropertyName(Constants.ANNIVERSARY_AFTER),
					promotion.getAnniversaryAfter());
			params.put(getPropertyName(Constants.ANNIVERSARY_BEFORE),
					promotion.getAnniversaryBefore());
		}

		/*
		 * if (promotion.getBirthdayAfter() != null) { if (where)
		 * query.append(" AND"); else query.append(" WHERE");
		 * query.append(" HAS(g.anniversary)  AND toInt(g.anniversary) >= {" +
		 * Constants.ANNIVERSARY_AFTER + "}");
		 * query.append(" AND toInt(g.dob) <= {" + Constants.ANNIVERSARY_BEFORE
		 * + "}"); }
		 */
	}

	@Override
	public List<Map<String, Object>> getfilteredEventGuest(
			EventPromotion promotion, Map<String, Object> queryParams) {
		StringBuilder query = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		createQueryForFilterGuest(promotion, query, params);

		query.append(" WITH DISTINCT(g.mobile) as mobile, g.guid as guid, hg.first_name as first_name,g.isd_code as isd_code,hg.reason as reason,hg.is_vip as is_vip, (CASE(HAS(hg.dnd_mobile)) WHEN true THEN hg.dnd_mobile ELSE false END) as dnd_mob,");
		query.append(" (CASE(HAS(g.is_dnd_user_enable)) WHEN true THEN  g.is_dnd_user_enable ELSE false END) as dnd_user,");
		query.append(" (CASE(HAS(g.is_dnd_trai_enable)) WHEN true THEN  g.is_dnd_trai_enable ELSE false END) as dnd_trai");
		query.append(" WHERE NOT (dnd_mob OR dnd_user OR dnd_trai)");
		query.append(" RETURN");
		query.append(" mobile,guid, first_name,isd_code,reason,is_vip");

		query = handlePromotionOrderBy(query, queryParams);
		Integer pageSize = getPageSize(queryParams);
		Integer startIndex = getIndex(queryParams, pageSize);
		query.append(" SKIP " + startIndex + " LIMIT " + pageSize);

		params.put(getPropertyName(Constants.STATUS), Constants.ACTIVE_STATUS);
		params.put(getPropertyName(Constants.REST_GUID),
				promotion.getRestaurantGuid());
		params.put(getPropertyName(Constants.RESERVATION_STATUS),
				Constants.FINISHED);

		Result<Map<String, Object>> r = template
				.query(query.toString(), params);

		Iterator<Map<String, Object>> itr = r.iterator();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		while (itr.hasNext()) {
			Map<String, Object> map = itr.next();
			list.add(map);
		}
		return list;
	}
	
	@Override
	public List<Map<String, Object>> filterGuestMobileNumbers(Map<String, Object> queryParams) {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (g:GuestProfile{dummy:false,status:{"+Constants.STATUS+"}}) ");
		query.append("WHERE g.mobile IN {"+Constants.MOBILE+"} ");
		query.append("OPTIONAL MATCH (g)-[hg:HAS_GUEST]-(r:Restaurant{guid:{"+Constants.REST_GUID+"}}) ");
		query.append("RETURN DISTINCT g as guestNode,g.mobile as mobile, COUNT(r)>0 as isGuest, hg.status as status;");
		
		Result<Map<String, Object>> r = template.query(query.toString(), queryParams);
		Iterator<Map<String, Object>> itr = r.iterator();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (itr.hasNext()) {
			Map<String, Object> map = itr.next();
			Map<String, Object> paramsReturn = new HashMap<String,Object>();
			paramsReturn .put("isGuest",map.get("isGuest"));
			paramsReturn .put(Constants.MOBILE,(map.get("mobile") != null) ? map.get("mobile").toString(): null);
			paramsReturn.put(Constants.GUEST_NODE,(map.get("guestNode") != null) ? template.convert(map.get("guestNode"), GuestProfile.class) : null);

			paramsReturn.put(Constants.STATUS,(map.get("status") != null) ? map.get("status").toString(): Constants.ACTIVE_STATUS);
			/*if guest is of another restaurant at that case status is null 
			 */
			
			list.add(paramsReturn);
		}
		return list;
	}
	
	@Override
	public Integer createMultipleGuests(Map<String, Object> queryParams) {
		StringBuilder query=new StringBuilder();
		query.append("UNWIND {mapList} as map ");
		query.append("MATCH (r:" + Constants.RESTAURANT_LABEL + "{guid:{" + Constants.REST_GUID + "}}),(g:" + Constants.GUESTPROFILE_LABEL + "{guid: map.guid"
				+ "}) ");
		query.append("MERGE (r)-[hg:"+RelationshipTypes.HAS_GUEST+"]->(g) ");
		query.append("SET hg.first_name=map.firstName,hg.is_vip=map.isVip, hg.reason=map.reason,hg.email_id=map.emailId,hg.gender=map.gender,hg.updated_dt=map.updatedDate, ");
		query.append(" hg.dnd_mobile=false,hg.dnd_email=false, hg.created_dt=map.createdDate,hg.status='"+Constants.ACTIVE_STATUS+"',hg.review_count=0,hg.cumulative_rating=0, ");
		query.append(" hg.search_params=replace(map.firstName+map.mobile,' ', '') ");
		query.append("RETURN COUNT(g) as count");
		
		Result<Map<String, Object>> r = template.query(query.toString(), queryParams);
		Iterator<Map<String, Object>> itr = r.iterator();
		int count=0;
		while (itr.hasNext()) {
			Map<String, Object> map = itr.next();
			count=(int) map.get("count");
		}
		return count;
	}
	
	
	

}
