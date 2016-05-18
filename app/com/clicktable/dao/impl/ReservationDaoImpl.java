
package com.clicktable.dao.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;
import play.i18n.Messages;

import com.clicktable.dao.intf.CorporateOffersDao;
import com.clicktable.dao.intf.QueueDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.model.CustomResvOpHr;
import com.clicktable.model.Entity;
import com.clicktable.model.GuestConversation;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.Reservation;
import com.clicktable.model.ReservationForTables;
import com.clicktable.model.ReservationHistory;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Server;
import com.clicktable.model.Table;
import com.clicktable.relationshipModel.TableReservationRelation;
import com.clicktable.repository.ConversationRepo;
import com.clicktable.repository.GuestProfileRepo;
import com.clicktable.repository.ReservationHistoryRepo;
import com.clicktable.repository.ReservationRepo;
import com.clicktable.repository.TableRepo;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.ReservationValidator;
import com.google.common.collect.Lists;

@Service
public class ReservationDaoImpl extends GraphDBDao<Reservation> implements ReservationDao {

	@Autowired
	ReservationRepo reservation_repo;
	@Autowired
	TableRepo table_repo;

	@Autowired
	GuestProfileRepo guest_repo;

	@Autowired
	ConversationRepo conversationRepo;

	@Autowired
	ReservationValidator reservation_validator;
	@Autowired
	ReservationHistoryRepo reservation_history;
	
	@Autowired
	QueueDao queueDao;
	
	
	@Autowired
	CorporateOffersDao corporateDao;

	public ReservationDaoImpl() {
		super();
		this.setType(Reservation.class);
	}
	
	
/*	@Override
	public List<Reservation> findByCustomeFields(Class<Reservation> class1, Map<String, Object> params) {
		// TODO Auto-generated method stub
		List<Reservation> reservation = new ArrayList<Reservation>();
		StringBuilder query = getMatchClause(params);
		query.append(" <-[guestRel:`GUEST_HAS_RESV`]-(guest:`GuestProfile`) \n");
		 Manage reservationStatus in URL_PARAM 
		if ((null != params.get("reservationStatus")) && !(params.get("reservationStatus") instanceof List) && (!params.get("reservationStatus").equals(""))) {
			params.put("reservationStatus", Arrays.asList(params.get("reservationStatus").toString().split(",")));
		}
		query.append(getWhereClause(params));
		Logger.debug("query is--------------------------------- " + query);
		query.append(" RETURN DISTINCT t,guest");
		Map<String, Object> map = new HashMap<String, Object>();

		Result<Map<String, Object>> results = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			map = i.next();
			// template.postEntityCreation((Node) map.get("reservation"),
			// Reservation.class);
			Reservation reservationData = template.convert(map.get("t"), Reservation.class);
			reservationData.setBookedBy(null);
			reservationData.setBookedById(null);
			reservationData.setConversation(null);
			reservationData.setCreatedBy(null);
			reservationData.setHistory(null);
			reservationData.setLanguageCode(null);
			reservationData.setUpdatedBy(null);
			reservationData.setUpdatedDate(null);
			reservationData.setServerGuids(null);
			reservationData.setServerNames(null);

			// template.postEntityCreation((Node) map.get("guest"),
			// GuestProfile.class);
			GuestProfile guest = template.convert(map.get("guest"), GuestProfile.class);
			reservationData.setGuest_email(guest.getEmailId());
			reservationData.setGuest_firstName(guest.getFirstName());
//			/reservationData.setGuest_lastName(guest.getLastName());
			reservationData.setGuest_mobile(guest.getMobile());
			reservationData.setGuestGuid(guest.getGuid());
			reservationData.setIsVIP(guest.getIsVip().toString());

			reservation.add(reservationData);

		}

		return reservation;
	}*/

	@Override
	public List<Reservation> findByCustomeFields(Class<Reservation> class1, Map<String, Object> params) {
		// TODO Auto-generated method stub
		List<Reservation> reservation = new ArrayList<Reservation>();
		StringBuilder query = new StringBuilder();
		
		Map<String, Object> map = new HashMap<String, Object>();

		
	
		query.append("MATCH (rest:Restaurant {guid:{restaurantGuid}})-[rhq:REST_HAS_BARENTRY]->(bar:BarEntry{status:'CREATED'})");
		query.append(" RETURN count(rhq) AS barCount");
		Result<Map<String, Object>> results = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = results.iterator();
		Integer barCount = 0;
		while (i.hasNext()) {
			map = i.next();
			 barCount = (Integer) map.get("barCount");
		}
		
		query = new StringBuilder();
		
		query.append("MATCH (t:`Reservation` {rest_guid : {restaurantGuid}})");
		query.append("<-[guestRel:`GUEST_HAS_RESV`]-(guest:`GuestProfile`)<-[hg:`HAS_GUEST`]-(rest:`Restaurant`{guid : {restaurantGuid}}) \n");
		query.append( " WHERE (NOT t.reservation_status IN ['FINISHED', 'CANCELLED', 'NO_SHOW'] AND (toInt(t.est_start_time) > timestamp() OR "
				+ "(toInt(t.est_start_time) <= timestamp() AND toInt(t.est_end_time) >= timestamp())) "
				+ "AND (toInt(t.est_end_time) <= toInt({currentShiftEnd}))"
				+ " AND t.booking_mode = 'ONLINE') OR t.reservation_status='SEATED' OR "
				+ "(NOT t.reservation_status IN ['FINISHED', 'CANCELLED', 'NO_SHOW'] AND t.booking_mode = 'WALKIN') \n");
		query.append(" RETURN DISTINCT t,guest, hg  ");
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		query.append(" SKIP " + startIndex + " LIMIT " + pageSize);// +
		
		
		map = new HashMap<String, Object>();

		results = template.query(query.toString(), params);
		 i = results.iterator();
		while (i.hasNext()) {
			map = i.next();

			Reservation reservationData = template.convert(map.get("t"), Reservation.class);
			reservationData.setBookedBy(null);
			reservationData.setBookedById(null);
			reservationData.setConversation(null);
			reservationData.setCreatedBy(null);
			reservationData.setHistory(null);
			reservationData.setLanguageCode(null);
			reservationData.setUpdatedBy(null);
			reservationData.setUpdatedDate(null);
			reservationData.setServerGuids(null);
			reservationData.setServerNames(null);
			reservationData.setBarCount(barCount);

			GuestProfile guest = template.convert(map.get("guest"), GuestProfile.class);
			
			reservationData.setGuestGuid(guest.getGuid());
			reservationData.setGuest_mobile(guest.getMobile());
			reservationData.setGuest_isd_code(guest.getIsd_code());
			
			

			Relationship relation = (Relationship) map.get("hg");
			try {
				reservationData.setGuest_firstName((null == relation.getProperty(Constants.FIRST_NAME)) ? "" : relation.getProperty(Constants.FIRST_NAME).toString());
			 } catch (NotFoundException e) {
				 reservationData.setGuest_firstName("");
			}
			try {
				reservationData.setIsVIP(relation.getProperty("is_vip").toString());
			 } catch (NotFoundException e) {
				 reservationData.setIsVIP(String.valueOf(false));
			}
			try {
				if(Boolean.valueOf(reservationData.getIsVIP()))
				{
				reservationData.setReason(relation.getProperty("reason").toString());
				}
				else
				{
					reservationData.setReason(null);
				}
			 } catch (NotFoundException e) {
				 reservationData.setReason(null);
			}
			try {
				reservationData.setFirstSeatedTime((null == relation.getProperty("first_seated")) ? null : new Date(Long.valueOf(relation.getProperty("first_seated").toString())));
			 } catch (NotFoundException e) {
				 reservationData.setFirstSeatedTime(null);
			}
			
			
			reservation.add(reservationData);

		}
		
		

		
		return reservation;
	}

	@Override
	public List<Reservation> findByFields(Class type, Map<String, Object> params) {

		List<Reservation> reservation = new ArrayList<Reservation>();
		StringBuilder query1 = new StringBuilder();
		StringBuilder query = getMatchClause(params);
		String name = "", lastName = "", mobile = "";

		Logger.debug("query after match clause is " + query + " and params are ================" + params);

		query.append('\n');
		query.append("MATCH ");
		if (null != params.get("freeSearch")) {
			query.append("(guest:`GuestProfile`)-[`GUEST_HAS_RESV`]->");

			if (null != params.get("freeSearch")) {
				name = params.get("freeSearch").toString();
				params.remove("freeSearch");
			}

		}
		query.append("(t)");
		/* Manage Table GUID in URL_PARAM */
		if (null != params.get("tableGuid")) {
			//query.append("<-[:`" + RelationshipTypes.TBL_HAS_RESV + "`]-(table:`Table`)");
			String[] result = params.get("tableGuid").toString().split(",");
			query1.append("(");
			for (String tableGuid : result) {
				query1.append(" '" + tableGuid + "' IN t.table_guid OR");
			}
			params.remove("tableGuid");
			if (query1.toString().contains(Constants.OR))
				query1 = new StringBuilder(query1.substring(0, query1.length() - 2));
				query1.append(")");
			
			//query1.append(" t.table_guid IN {tableGuidList} ");
		}

		/* Adding params of Guest in search Param */

		if (name.trim().length() > 0) {

			if (query1.length() > 0) {
				query1.append(" AND ");
			}

			query1.append(" ( guest.first_name=~('(?i).*" + name + ".*')  OR guest.last_name=~('(?i).*" + name + ".*') OR" + "  guest.mobile=~('(?i).*" + name + ".*')     )");
		}

		/* Manage reservationStatus in URL_PARAM */
		if ((null != params.get("reservationStatus")) && !(params.get("reservationStatus") instanceof List) && (!params.get("reservationStatus").equals(""))) {

			params.put("reservationStatus", Arrays.asList(params.get("reservationStatus").toString().split(",")));

		}

		Logger.debug("query is " + query + " query1 is " + query1);

		query.append(getWhereClause(params));

		if ((!query.toString().contains("WHERE"))) {
			if (query1.length() > 0)
				query.append(" WHERE ").append(query1);
		} else {
			if (query1.length() > 0)
				query.append(" AND ").append(query1);

		}

		query.append(getReturnClause(params));

		System.out.println("----------Reservation Query--------" + query.toString());

		List<Reservation> results = executeQuery(query.toString(), params);
		if (!params.containsKey(Constants.DASHBOARD)) {
			if (params.containsKey(Constants.REST_GUID))
				reservation = getReservationDetailsWithCypher(results, params.get(Constants.REST_GUID).toString());
			else
				reservation = getReservationDetailsWithCypher(results, null);
		} else
			reservation = results;
		return reservation;

	}
	
	
	
	@Override
	public Reservation update(Reservation resv) {

		Map<String, Object> params = new HashMap<String, Object>();
	
		/*Boolean isWalkin = resv.getBookingMode().equals(Constants.WALKIN_STATUS);
		//resv.setQueued(isWalkin);*/
		
		params.put(Constants.GUID, resv.getGuid());
		params.put(Constants.PROP_MAP, getGraphProperty(UtilityMethods.introspect(resv)));

		StringBuilder query = new StringBuilder();
		query.append("MATCH(t:" + Constants.RESERVATION_LABEL + "{guid:{" + Constants.GUID + "}}) ");
		query.append("SET t= { " + Constants.PROP_MAP + " } ");
		query.append("return t");

		Iterator<Map<String, Object>> results = executeWriteQuery(query.toString(), params).iterator();
		Reservation updatedReservation = null;
		while (results.hasNext()) {
			Map<String, Object> map = results.next();
			Logger.debug("map is " + map);
			updatedReservation = template.convert(map.get("t"), Reservation.class);
		}

		return updatedReservation;

	}
	
	
	

	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		query.append(" RETURN DISTINCT t");
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);// +
																			// " collect(t) as betAnswers";

	}

	@Override
	protected StringBuilder getWhereClause(Map<String, Object> params) {
		Map<String, Object> customParams = new HashMap<String, Object>();
		for (String customParam : Reservation.getCustomFinderParams()) {
			if (params.containsKey(customParam)) {
				customParams.put(customParam, params.get(customParam));
				params.remove(customParam);
			}
		}
		StringBuilder query = super.getWhereClause(params);
		if (!customParams.isEmpty())
			query = appendCustomWhereClause(query, customParams);
		params.putAll(customParams);
		return query;
	}

	private StringBuilder appendCustomWhereClause(StringBuilder query, Map<String, Object> customParams) {
		for (Entry<String, Object> entry : customParams.entrySet()) {
			if (query.toString().contains(Constants.WHERE))
				query.append(" AND ");
			else
				query.append(" WHERE ");

			switch (entry.getKey()) {

			case Constants.RESERVED_BEFORE:
				query.append("toInt(t." + getPropertyName("reservationTime") + ")<toInt({" + entry.getKey() + "})");
				break;
			case Constants.RESERVED_AFTER:
				query.append("toInt(t." + getPropertyName("reservationTime") + ")>toInt({" + entry.getKey() + "})");
				break;
			case Constants.EST_START_BEFORE:
				query.append("toInt(t." + getPropertyName("estStartTime") + ")<toInt({" + entry.getKey() + "})");
				break;
			/*
			 * case Constants.EST_START_TIME: query.append("toInt(t." +
			 * getPropertyName("estStartTime") + ")={" + entry.getKey() + "}");
			 * break;
			 */
			/*case Constants.EST_START_AFTER:
				query.append("toInt(t." + getPropertyName("estStartTime") + ")>{" + entry.getKey() + "}");
				break;
			case Constants.EST_END_BEFORE:
				query.append("toInt(t." + getPropertyName("estEndTime") + ")<{" + entry.getKey() + "}");
				break;
			case Constants.EST_END_AFTER:
				query.append("toInt(t." + getPropertyName("estEndTime") + ")>{" + entry.getKey() + "}");
				break;
			case Constants.EST_END_TIME:
				query.append("toInt(t." + getPropertyName("estEndTime") + ")={" + entry.getKey() + "}");
				break;
			case Constants.ACT_START_BEFORE:
				query.append("toInt(t." + getPropertyName("actStartTime") + ")<{" + entry.getKey() + "}");
				break;
			case Constants.ACT_START_AFTER:
				query.append("toInt(t." + getPropertyName("actStartTime") + ")>{" + entry.getKey() + "}");
				break;
			case Constants.ACT_END_BEFORE:
				query.append("toInt(t." + getPropertyName("actEndTime") + ")<{" + entry.getKey() + "}");
				break;
			case Constants.ACT_END_AFTER:
				query.append("toInt(t." + getPropertyName("actEndTime") + ")>{" + entry.getKey() + "}");
				break;
			case Constants.CANCELLED_BEFORE:
				query.append("toInt(t." + getPropertyName("cancelTime") + ")<{" + entry.getKey() + "}");
				break;
			case Constants.CANCELLED_AFTER:
				query.append("toInt(t." + getPropertyName("cancelTime") + ")>{" + entry.getKey() + "}");
				break;*/
			case Constants.COVERS_LESS_THAN:
				query.append("toInt(t." + getPropertyName("numCovers") + ")<toInt({" + entry.getKey() + "})");
				break;
			case Constants.COVERS_MORE_THAN:
				query.append("toInt(t." + getPropertyName("numCovers") + ")>toInt({" + entry.getKey() + "})");
				break;
			case Constants.NUM_COVERS:
				query.append("toInt(t." + getPropertyName("numCovers") + ")>" + entry.getKey());
				break;
			default:

				break;
			}
		}
		return query;
	}

	@Override
	public Reservation addReservation(Reservation reservation_new, GuestProfile guest, List<Table> tableList) {
		long startTime = Calendar.getInstance().getTimeInMillis();
		Logger.info("**************RESERVATION DAO CYPHER   **************                  " + (Calendar.getInstance().getTimeInMillis() - startTime));
		StringBuilder query = new StringBuilder();
		/* Create Reservation */
		reservation_new.setStatusUpdatedTime(new Date(startTime));
/*		Boolean isWalkin = reservation_new.getBookingMode().equals(Constants.WALKIN_STATUS);
		//reservation_new.setQueued(isWalkin);
*/		
		Reservation reservation = template.save(reservation_new);

		Logger.info("**************RESERVATION SAVE CYPHER   **************                  " + (Calendar.getInstance().getTimeInMillis() - startTime));
		/* Create Reservation History */
		ReservationHistory history = new ReservationHistory();
		history.setReservationStatus(reservation.getReservationStatus());
		history.setCreatedBy(reservation.getCreatedBy());
		history.setBookedBy(reservation.getBookedBy());
		template.save(history);
		
		query.append("MATCH (reservation:`");
		query = getGenericMatchClause(Reservation.class, query).append('`').append("{guid:'" + reservation.getGuid() + "'}").append("),");

		/* Get Guest Match Clause */
		query.append("(guest:`");
		query = getGenericMatchClause(GuestProfile.class, query).append('`').append("{guid:'" + reservation.getGuestGuid() + "'}").append("),");

		/* Get History Clause */
		query.append("(history:`");

		query = getGenericMatchClause(ReservationHistory.class, query).append("`").append("{guid:'" + history.getGuid() + "'}").append(")");

		query.append('\n');
		/* Create Relationship as Guest with Reservation */
		query.append("MERGE (guest)-[:`" + RelationshipTypes.GUEST_HAS_RESV + "`");
		query = this.setReservationProperties(reservation, query, true, false);
		query.append("]-> (reservation) \n");
		query.append(" With reservation, history \n");



		/* Create History Event w.r.t to Resrvation */

		query.append("MERGE (reservation)-[:`" + RelationshipTypes.RESV_HISTORY + "`");
		query = this.setReservationHistProperties(history, query, true);
		query.append("]-> (history) \n");

		System.out.println(" Query ---------------" + query.toString());
		template.query(query.toString(), null);

		Logger.info("**************RESERVATION CYPHER END   **************            " + (Calendar.getInstance().getTimeInMillis() - startTime));
		
		List<Reservation> results = new ArrayList<>();
		results.add(reservation);
		List<Reservation> finalResvList = new ArrayList<>();
		finalResvList = getReservationDetailsWithCypher(results, reservation.getRestaurantGuid());
		
		
		return finalResvList.get(0);

	}

	private StringBuilder setReservationProperties(Entity reservation, StringBuilder query, boolean isReservation, boolean isUpdate) {
		String splitter = ",";
		String semiOrEquals = "=";
		String update_alias = "";
		if (isUpdate) {
			update_alias = " t.";
		} else {
			query.append('{');
			semiOrEquals = ":";
		}
		if (isReservation) {

			query.append(" __type__" + semiOrEquals + "'" + ("GuestReservationRelation") + "'" + splitter);
			if (((Reservation) reservation).getRestaurantGuid() != null)
				query.append(" rest_guid" + semiOrEquals + "'" + (((Reservation) reservation).getRestaurantGuid()) + "' " + splitter);
			if (((Reservation) reservation).getReservationStatus() != null)
				query.append(" status" + semiOrEquals + "'" + (((Reservation) reservation).getReservationStatus()) + "' " + splitter);
			if (((Reservation) reservation).getActStartTime() != null)
				query.append(" actStartTime" + semiOrEquals + "'" + (((Reservation) reservation).getActStartTime().getTime()) + "' " + splitter);
			if (((Reservation) reservation).getActEndTime() != null)
				query.append(" actEndTime" + semiOrEquals + "'" + (((Reservation) reservation).getActEndTime().getTime()) + "' " + splitter);

		} else {

			query.append(update_alias + "__type__" + semiOrEquals + "'" + ("TableReservationRelation") + "'" + splitter);
			if (((Reservation) reservation).getActStartTime() != null)
				query.append(update_alias + "start_time" + semiOrEquals + "'" + (((Reservation) reservation).getActStartTime().getTime()) + "' " + splitter);
			if (((Reservation) reservation).getActEndTime() != null)
				query.append(update_alias + "end_time" + semiOrEquals + "'" + (((Reservation) reservation).getActEndTime().getTime()) + "' " + splitter);
			if (((Reservation) reservation).getEstStartTime() != null)
				query.append(update_alias + "startDate" + semiOrEquals + "'" + (((Reservation) reservation).getEstStartTime().getTime()) + "' " + splitter);
			if (((Reservation) reservation).getEstEndTime() != null)
				query.append(update_alias + "endDate" + semiOrEquals + "'" + (((Reservation) reservation).getEstEndTime().getTime()) + "' " + splitter);
			if (((Reservation) reservation).getStatus() != null)
				query.append(update_alias + "reservation_status" + semiOrEquals + "'" + (((Reservation) reservation).getReservationStatus()) + "' " + splitter);
			query.append(update_alias + "resv_guid " + semiOrEquals + "reservation.guid,");

			query.append(update_alias + "table_guid " + semiOrEquals + "tbl.guid,");

		}
		query = new StringBuilder(query.substring(0, query.length() - 1));
		if (!isUpdate) {
			query.append('}');
		}

		return query;
	}

	private StringBuilder setReservationHistProperties(ReservationHistory history, StringBuilder query, boolean isHistory) {
		String splitter = ",";
		String semiOrEquals = "=";
		String history_alias = "";
		if (isHistory) {
			query.append('{');
			semiOrEquals = ":";
		}
		if (isHistory) {
			query.append(" __type__" + semiOrEquals + "'" + ("ReservationHasHistory") + "'" + splitter);
			if (((ReservationHistory) history).getGuid() != null)
				query.append(history_alias + " resv_status" + semiOrEquals + "'" + (((ReservationHistory) history).getReservationStatus()) + "' " + splitter);

			query = new StringBuilder(query.substring(0, query.length() - 1));
			query.append('}');
		}

		return query;
	}

	@Override
	public String updateReservation(Reservation reservation) {
		// TODO Auto-generated method stub
		StringBuilder query = new StringBuilder();
		ReservationHistory historyNew = null;
		boolean updateHist = false;

		query = null;
		if (null == query) {
			query = new StringBuilder();
		}

		historyNew = reservation_history.getreservationHistory(reservation.getGuid(), reservation.getReservationStatus());

		if (null == historyNew) {
			updateHist = true;
		} else {
			historyNew.setCreatedBy(reservation.getCreatedBy());
		}

		if (updateHist) {
			historyNew = new ReservationHistory();
			historyNew.setReservationStatus(reservation.getReservationStatus());
			historyNew.setCreatedBy(reservation.getCreatedBy());
			/* historyNew.setUpdatedBy(reservation.getUpdatedBy()); */
			historyNew.setBookedBy(reservation.getBookedBy());
			template.save(historyNew);
		}

		/* Get Table Match Clause */
		query.append("MATCH (h:`");
		query = getGenericMatchClause(Reservation.class, query).append('`').append("{guid:'" + reservation.getGuid() + "'}").append(')');

		query.append(",(t:`");
		query = getGenericMatchClause(ReservationHistory.class, query).append('`').append("{guid:'" + historyNew.getGuid() + "'}").append(')');
		query.append('\n');
		query.append("WITH h,t\n");
		query.append(" MATCH (h)  ");
		if(updateHist)
		{
			query.append(" SET h.status_updated_time=toInt(" + Calendar.getInstance().getTimeInMillis() + ")\n");
		}
		else
		{
			query.append(" \n");
		}
		query.append("MERGE (h)-[:`" + RelationshipTypes.RESV_HISTORY + "`" + "{resv_status:'" + historyNew.getReservationStatus() + "'}" + "]->(t)");

		template.query(query.toString(), null);
		return reservation.getGuid();
	}

	protected StringBuilder getDeleteTablequery(Reservation reservation, List<Table> deleteList) {
		StringBuilder query = new StringBuilder();
		/* Get Reservation Match Clause */
		query.append("\n MATCH (reservation:`");
		query = getGenericMatchClause(Reservation.class, query).append('`').append("{guid:'" + reservation.getGuid() + "'}").append(')');

		query.append("<-[t:`" + RelationshipTypes.TBL_HAS_RESV + "`]");

		/* Get Table Match Clause */
		query.append("-(table:`");
		query = getGenericMatchClause(Table.class, query).append("`)");
		String queryWhere = " WHERE table.guid IN {" + Constants.TABLE_GUID + "} ";
		query.append(queryWhere + "\n");
		query.append(" DELETE t");
		return query;
	}

	protected StringBuilder getReturnQuery(Map<String, Object> params, StringBuilder query) {

		for (java.util.Map.Entry<String, Object> entry : params.entrySet()) {
			if (!entry.getKey().toUpperCase().equalsIgnoreCase("CLASS")) {
				if (null != entry.getValue())
					switch (entry.getKey()) {
					case Constants.EST_START_TIME:
						query.append("t." + getPropertyName("estStartTime") + "=toInt({" + entry.getKey() + "}),");
						break;
					case Constants.EST_END_TIME:
						query.append("t." + getPropertyName("estEndTime") + "=toInt({" + entry.getKey() + "}),");
						break;
					case Constants.RESERVATION_TIME:
						query.append("t." + getPropertyName("reservationTime") + "=toInt({" + entry.getKey() + "}),");
						break;
					case Constants.UPDATED_DATE:
						query.append("t." + getPropertyName("updatedDate") + "=toInt({" + entry.getKey() + "}),");
						break;
					case Constants.CREATED_DATE:
						query.append("t." + getPropertyName("createdDate") + "=toInt({" + entry.getKey() + "}),");
						break;
					case Constants.ACT_START_TIME:
						query.append("t." + getPropertyName("actStartTime") + "=toInt({" + entry.getKey() + "}),");
						break;
					case Constants.ACT_END_TIME:
						query.append("t." + getPropertyName("actEndTime") + "=toInt({" + entry.getKey() + "}),");
						break;

					default:
						query.append(" t." + getPropertyName(entry.getKey()) + " ={" + entry.getKey() + "} ,\n");
						break;
					}

			}

		}
		return (new StringBuilder(query.substring(0, query.length() - 2)));
	}

	// method to get reservations of guest

	@Override
	public Map<String,Object> getReservationsForGuest(Map<String, Object> params) {

		
		String query = "MATCH (g:GuestProfile{guid:{" + Constants.GUID + "}})-[ghr:`GUEST_HAS_RESV`]->(resv:Reservation)  "
				+ "return ghr.rest_guid as rest_id,resv.reservation_status as resv_status, resv.booking_mode as booking_mode, "
				+ "collect([resv.guid, toInt(resv.est_start_time), toInt(resv.est_end_time), resv.reservation_status, toInt(resv.act_start_time), "
				+ "toInt(resv.act_end_time),resv.booking_mode, resv.rest_guid, toInt(resv.num_covers)]) as resvList";
		
		
		String queryBar = "MATCH (g:GuestProfile{guid:{" + Constants.GUID + "}})-[gib:" + RelationshipTypes.GUEST_IN_BAR + "]->(t:" + Constants.BAR_ENTRY_LABEL +")  "
				+ "return t.rest_guid as rest_id";
		
		
		
		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("ctCancelCount", 0);
		resultMap.put("ctNoShowCount", 0);
		resultMap.put("ctReservationCount", 0);
		resultMap.put("ctTotalVisits", 0);
		resultMap.put("ctWalkinCount", 0);
		resultMap.put("restCancelCount", 0);
		resultMap.put("restNoShowCount", 0);
		resultMap.put("restReservationCount", 0);
		resultMap.put("restTotalVisits", 0);
		resultMap.put("restWalkinCount", 0);
		
		
		
		    
		 Long queryTime = new Date().getTime();
		Result<Map<String, Object>> r = template.query(query, params);
		Result<Map<String, Object>> rBar = template.query(queryBar, params);
		
		System.out.println("Time in template.query is------------------------------------------------------" + (new Date().getTime() - queryTime));
		Iterator<Map<String, Object>> itr = r.iterator();
		List<Reservation> upcomingReservations = new ArrayList<>();
		List<List> reservationHistory = new ArrayList<>();
		List<Reservation> resHistoryList = new ArrayList<>();
		
		while (itr.hasNext()) {
			Long startT = new Date().getTime();
			Map<String, Object> map = itr.next();

			//populate rest stats
			if(map.get("rest_id").equals(params.get(Constants.REST_GUID))){
				if(map.get("resv_status").equals(Constants.NO_SHOW_STATUS)){
					resultMap.put("restNoShowCount", ((Integer)resultMap.get("restNoShowCount") + ((List<Map>) map.get("resvList")).size()));
				}
				else if(map.get("resv_status").equals(Constants.CANCELLED)){
					resultMap.put("restCancelCount", ((Integer)resultMap.get("restCancelCount") + ((List<Map>) map.get("resvList")).size()));
				}
				else if(map.get("resv_status").equals(Constants.SEATED)){
					reservationHistory.addAll((List<List>) map.get("resvList"));
				}
				else if(map.get("resv_status").equals(Constants.FINISHED))
				{
					reservationHistory.addAll((List<List>) map.get("resvList"));
					if(map.get("booking_mode").equals(Constants.WALKIN_STATUS)){
						resultMap.put("restWalkinCount", ((Integer)resultMap.get("restWalkinCount") + ((List<Map>) map.get("resvList")).size()));
					}else{
						resultMap.put("restReservationCount", ((Integer)resultMap.get("restReservationCount") + ((List<Map>) map.get("resvList")).size()));
					}
					resultMap.put("restTotalVisits", ((Integer)resultMap.get("restTotalVisits") + ((List<Map>) map.get("resvList")).size()));
					
				}else{
					for(List resvMap : (List<List>) map.get("resvList"))
					{
						/*if(upcomingReservations.size() >= 5)
						{
							break;
						}*/

						Reservation resv = getResvFromResultMap(resvMap);
						upcomingReservations.add(resv);
					}

				}
			}
			
			
			
			//populate ct stats
			if(map.get("resv_status").equals(Constants.NO_SHOW_STATUS)){
				resultMap.put("ctNoShowCount", ((Integer)resultMap.get("ctNoShowCount") + ((List<Map>) map.get("resvList")).size()));
			}
			else if(map.get("resv_status").equals(Constants.CANCELLED))
			{
				resultMap.put("ctCancelCount", ((Integer)resultMap.get("ctCancelCount") + ((List<Map>) map.get("resvList")).size()));
			}
			else if(map.get("resv_status").equals(Constants.FINISHED))
			{
				if(map.get("booking_mode").equals(Constants.WALKIN_STATUS)){
					resultMap.put("ctWalkinCount", ((Integer)resultMap.get("ctWalkinCount") + ((List<Map>) map.get("resvList")).size()));
				}else{
					resultMap.put("ctReservationCount", ((Integer)resultMap.get("ctReservationCount") + ((List<Map>) map.get("resvList")).size()));
				}
				resultMap.put("ctTotalVisits", ((Integer)resultMap.get("ctTotalVisits") + ((List<Map>) map.get("resvList")).size()));
			}
		}
		
		itr = rBar.iterator();
		Integer ctBarCount = 0;
		Integer restBarCount = 0;
		
		while (itr.hasNext()) {
			Map<String, Object> map = itr.next();
			if(map.get("rest_id").equals(params.get(Constants.REST_GUID))){
				restBarCount++;
			}
			ctBarCount++;
		}
		
		resultMap.put("restBarCount", restBarCount); 
		resultMap.put("ctBarCount", ctBarCount); 
		
		
		Long sortTime = new Date().getTime();
		reservationHistory.sort((p1, p2) -> ((Long) p2.get(1)).compareTo(((Long) p1.get(1))));
		System.out.println("Time in sorting is------------------------------------------------------" + (new Date().getTime() - sortTime));
		
		for(List resvMap : reservationHistory){
			/*if(resHistoryList.size() >=5 ){
				break;
			}*/
			Reservation resv = getResvFromResultMap(resvMap);
			resHistoryList.add(resv);
		}

		resultMap.put("reservationHistory", resHistoryList);
		resultMap.put("upcomingReservations", upcomingReservations);
		
		return resultMap;
	}
	
	
	
	private Reservation getResvFromResultMap(List inputMap)
	{

		Reservation resv = new Reservation();


		if( inputMap.get(1) != null)
		{
			resv.setEstStartTime(new Date(Long.valueOf(inputMap.get(1).toString())));
		}
		if( inputMap.get(2) != null)
		{
			resv.setEstEndTime(new Date(Long.valueOf(inputMap.get(2).toString())));
		}
		if(inputMap.get(0) != null)
		{
			resv.setGuid((String)inputMap.get(0));
		}
		if(inputMap.get(3) != null)
		{
			resv.setReservationStatus((String)inputMap.get(3));
		}

		if( inputMap.get(4) != null)
		{
			resv.setActStartTime(new Date(Long.valueOf(inputMap.get(4).toString())));
		}
		if( inputMap.get(5) != null)
		{
			resv.setActEndTime(new Date(Long.valueOf(inputMap.get(5).toString())));
		}
		if(inputMap.get(6) != null)
		{
			resv.setBookingMode((String)inputMap.get(6));
		}
		if(inputMap.get(7) != null)
		{
			resv.setRestaurantGuid((String) inputMap.get(7));
		}
		if(inputMap.get(8) != null)
		{
			resv.setNumCovers(Integer.valueOf(inputMap.get(8).toString()));
		}

		return resv;

	}

	// get servers for tables.
	@Override
	public List<String> getServersForTables(List<String> tableGuids) {
		StringBuilder query = new StringBuilder();
		Map<java.lang.String, Object> params = new HashMap<java.lang.String, Object>();
		params.put(Constants.TABLE_GUID, tableGuids);
		String server_guids = "", server_names = "";
		query.append(" MATCH (t:Table)-[ths:`TBL_HAS_SERVER`]->(s:Server)");
		if (tableGuids.size() > 0) {
			query.append(" WHERE t.guid IN {" + Constants.TABLE_GUID + "} ");
		}
		query.append("  RETURN DISTINCT s");

		Logger.debug("getserversfortables: query is " + query.toString());

		Iterator<Map<String, Object>> itr = template.query(query.toString(), params).iterator();
		while (itr.hasNext()) {
			if (!server_guids.equals("")) {
				server_guids = server_guids + ",";
				server_names = server_names + ",";
			}
			Map<String, Object> map = itr.next();
			server_guids = server_guids + (template.convert(map.get("s"), Server.class).getGuid());
			server_names = server_names + (template.convert(map.get("s"), Server.class).getName());
		}
		List<String> result = new ArrayList<String>();
		result.add(server_guids);
		result.add(server_names);
		return result;
	}

	@Override
	public Map<GuestConversation, Reservation> getAllGuestReservation() {
		// TODO Auto-generated method stub
		List<String> loopArray = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("IST"));
		SimpleDateFormat dateformat = new SimpleDateFormat("EEE, d MMM yyyy");
		SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
		Map<GuestConversation, Reservation> convList = new HashMap<GuestConversation, Reservation>();
		long _30_mins_milliSec = 30 * 60 * 1000;
		long _50_mins_milliSec = 50 * 60 * 1000;
		StringBuffer query = new StringBuffer();
		query.append("Match (restaurant:Restaurant)-[hg:`HAS_GUEST`]->(guest:GuestProfile{dummy:false})-[rel:GUEST_HAS_RESV]->(resv:Reservation{booking_mode:'ONLINE'})"
				+ " WHERE resv.rest_guid=restaurant.guid and resv.reservation_status='CREATED' AND " + " (((toInt(resv.est_start_time)-toInt(" + cal.getTimeInMillis() + ")>=toInt("
				+ _30_mins_milliSec + ")) AND " + " ( toInt(resv.est_start_time)-toInt(" + cal.getTimeInMillis() + "))<=toInt(" + _50_mins_milliSec + ")) )" + " return resv, guest, restaurant,hg");
		Result<Map<String, Object>> results = template.query(query.toString(), null);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			if (null != map) {
				Reservation resv = template.convert(map.get("resv"), Reservation.class);
				GuestProfile guest = template.convert(map.get("guest"), GuestProfile.class);
				Restaurant rest = template.convert(map.get("restaurant"), Restaurant.class);
				
				Relationship relation = (Relationship) map.get("hg");
				try {
					guest.setFirstName((null == relation.getProperty(Constants.FIRST_NAME)) ? "" : relation.getProperty(Constants.FIRST_NAME).toString());
				 } catch (NotFoundException e) {
					 guest.setFirstName(null);
				}
				try {
					guest.setIsVip(Boolean.valueOf((relation.getProperty("is_vip").toString())));
				 } catch (NotFoundException e) {
					 guest.setIsVip(false);
				}
				try {
					if(guest.getIsVip())
					{
					guest.setReason(relation.getProperty("reason").toString());
					}
					else
					{
						guest.setReason(null);
					}
				 } catch (NotFoundException e) {
					 guest.setReason(null);
				}
				try {
					guest.setGender((null == relation.getProperty(Constants.GENDER)) ? guest.getGender() : relation.getProperty(Constants.GENDER).toString());
				 } catch (NotFoundException e) {
				}
				try {
					guest.setFirstSeatedTime((null == relation.getProperty("first_seated")) ? null : new Date(Long.valueOf(relation.getProperty("first_seated").toString())));
				 } catch (NotFoundException e) {
					 guest.setFirstSeatedTime(null);
				}
				
				
				
				final List<String> finalValues = msgSentExtensions(guest.getGuid());
				List<String> availableExtns = new ArrayList<String>();
				List<String> extensions = UtilityMethods.getEnumValues(Constants.RESERVATION_MODULE, Constants.EXTENSION);
				extensions.forEach(x -> {
					if (availableExtns.isEmpty() && (!loopArray.contains(x)))
						if (!finalValues.contains(x)) {
							availableExtns.add(x);
						}
				});
				if (!availableExtns.isEmpty()) {
					String extn = availableExtns.get(0);
					loopArray.add(extn);
					String mobileNumber = Messages.get(extn);
					resv.setGupshupExtension(extn);
					resv.setReservationStatus(Constants.MSG_SENT);

					Object params[] = { guest.getFirstName(), rest.getName() + (null != rest.getLocality() ? ", " + rest.getLocality() : "") + (null != rest.getRegion() ? ", " + rest.getRegion() : ""),
							resv.getNumCovers(), dateformat.format(new Date(resv.getEstStartTime().getTime())), timeformat.format(new Date(resv.getEstStartTime().getTime())), mobileNumber,
							rest.getPhoneNo1() };
					String sms_message = UtilityMethods.sendSMSFormat(params, Constants.SMS_RESERVATION_AUTO_FOLLOW_UP);
					GuestConversation conversation = new GuestConversation(resv, sms_message);
					conversation.setGuid(UtilityMethods.generateCtId());
					conversation.setCreatedBy(conversation.getRestaurantGuid());
					conversation.setUpdatedBy(conversation.getRestaurantGuid());
					convList.put(conversation, resv);
				}
			}
		}
		return convList;
	}

	@Override
	public List<Reservation> getGuestUpcomingReservation(String guestGuid, String extension) {
		long _50_mins_milliSec = 50 * 60 * 1000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("IST"));
		List<Reservation> reservations = new ArrayList<Reservation>();
		StringBuffer query = new StringBuffer();
		query.append("Match (restaurant:Restaurant)-[`HAS_GUEST`]->(guest:GuestProfile{guid:'" + guestGuid + "',dummy:false})-[rel:GUEST_HAS_RESV]->(resv:Reservation{gupshup_extension:'" + extension
				+ "'})" + " WHERE " + "(resv.reservation_status='MSG_SENT' OR resv.reservation_status='CONFIRMED') AND  (((toInt(resv.est_start_time)-toInt(" + cal.getTimeInMillis() + ")>=toInt(" + 0
				+ ")) AND " + " ( toInt(resv.est_start_time)-toInt(" + cal.getTimeInMillis() + "))<=toInt(" + _50_mins_milliSec + ")) ) return DISTINCT resv " + " ");
		Result<Map<String, Object>> results = template.query(query.toString(), null);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			if (null != map) {
				template.postEntityCreation((Node) map.get("resv"), Reservation.class);
				Reservation resv = template.convert(map.get("resv"), Reservation.class);
				reservations.add(resv);
			}
		}
		return reservations;
	}

	@Override
	// @Transactional
	public Reservation updateReservationViaSchedular(Reservation reservation) {
		StringBuilder query = new StringBuilder();
		ReservationHistory historyNew;
		boolean updateHist = false;
		query.append("MATCH (t:`" + type.getSimpleName() + "`" + "{guid:'" + reservation.getGuid() + "'})");
		query.append(" SET t.reservation_status='" + reservation.getReservationStatus() + "'");
		if (reservation.getGupshupExtension() != null)
			query.append(" , t.gupshup_extension='" + reservation.getGupshupExtension() + "'");
		query.append(" RETURN t ");
		template.query(query.toString(), null);

		/* Deleting Reservation Relation with Table */
	/*	query = null;
		if (null == query) {
			query = new StringBuilder();
		}
		if (reservation.getReservationStatus().equalsIgnoreCase(Constants.FINISHED) || reservation.getReservationStatus().equalsIgnoreCase(Constants.CANCELLED)
				|| reservation.getReservationStatus().equalsIgnoreCase(Constants.NO_SHOW_STATUS)) {
			List<Table> table_list = table_repo.table_has_resv_rel(reservation.getGuid(), reservation.getGuestGuid());
			if (table_list.size() > 0) {
				query.append(getDeleteTablequery(reservation, table_list));

				Map<String, Object> params = new HashMap<String, Object>();
				params.put(Constants.TABLE_GUID, getGuids(table_list));
				template.query(query.toString(), params);
			}
		}*/

		/* Update Reservation History w.r.t Reservation */

		query = null;
		if (null == query) {
			query = new StringBuilder();
		}

		historyNew = reservation_history.getreservationHistory(reservation.getGuid(), reservation.getReservationStatus());

		if (null == historyNew) {
			updateHist = true;
		} else {
			historyNew.setCreatedBy(reservation.getCreatedBy());
			/*
			 * historyNew.setUpdatedBy(reservation.getUpdatedBy());
			 * historyNew.setUpdatedDate(new Timestamp(new Date().getTime()));
			 */
		}

		if (updateHist) {
			historyNew = new ReservationHistory();
			historyNew.setReservationStatus(reservation.getReservationStatus());
			historyNew.setCreatedBy(reservation.getCreatedBy());
			/* historyNew.setUpdatedBy(reservation.getUpdatedBy()); */
			historyNew.setBookedBy(reservation.getBookedBy());
			template.save(historyNew);
		}

		/* Get Table Match Clause */
		query.append("MATCH (h:`");
		query = getGenericMatchClause(Reservation.class, query).append('`').append("{guid:'" + reservation.getGuid() + "'}").append(')');

		query.append(",(t:`");
		query = getGenericMatchClause(ReservationHistory.class, query).append('`').append("{guid:'" + historyNew.getGuid() + "'}").append(')');
		query.append('\n');
		query.append("WITH h,t\n");
		
		query.append(" MATCH (h)  ");
		if(updateHist)
		{
			query.append(" SET h.status_updated_time=toInt(" + Calendar.getInstance().getTimeInMillis() + ")\n");
		}
		else
		{
			query.append(" \n");
		}

		query.append("MERGE (h)-[:`" + RelationshipTypes.RESV_HISTORY + "`" + "{resv_status:'" + historyNew.getReservationStatus() + "'}" + "]->(t)");

		template.query(query.toString(), null);

		return reservation;
	}

	@Override
	public Map<String, Object> getReservationStatusCounts(Map<String, Object> params) {

		String query = "match (t:" + Constants.RESERVATION_LABEL + ") " + getWhereClause(params) + " with t.reservation_status as Status,count(*) as StatusCount return collect([Status,StatusCount])";
		Logger.debug(query);
		params.forEach((x, y) -> Logger.debug(x + ">" + y));
		Result<Map<String, Object>> result = template.query(query, params);
		Map<String, Object> countMap = convertResultToMap(result, "collect([Status,StatusCount])");
		for (String status : UtilityMethods.getEnumValues(Constants.RESERVATION_MODULE, Constants.RESERVATION_FINAL_STATUS)) {
			if (!countMap.containsKey(status))
				countMap.put(status, 0);
		}
		return countMap;
	}

	@Override
	public Map<String, Object> getWaitlistStatusCounts(Map<String, Object> params) {

		String query = "match (t:" + Constants.RESERVATION_LABEL + ") " + getWhereClause(params) + " with t.reservation_status as Status,count(*) as StatusCount return collect([Status,StatusCount])";
		Logger.debug(query);
		params.forEach((x, y) -> Logger.debug(x + ">" + y));
		Result<Map<String, Object>> result = template.query(query, params);

		Map<String, Object> finalMap = new HashMap<>();

		Map<String, Object> countMap = convertResultToMap(result, "collect([Status,StatusCount])");
		for (String status : UtilityMethods.getEnumValues(Constants.RESERVATION_MODULE, Constants.WAITLIST_FINAL_STATUS)) {
			if (!countMap.containsKey(status))
				countMap.put(status, 0);
		}

		int seatCount = (int) countMap.get(Constants.SEATED) + (int) countMap.get(Constants.FINISHED);
		int dropoutCount = (int) countMap.get(Constants.NO_SHOW_STATUS) + (int) countMap.get(Constants.CANCELLED);

		finalMap.put("FINISHED", seatCount);
		finalMap.put("DROPOUT", dropoutCount);
		return finalMap;
	}

	@Override
	public Map<String, Object> getReservationModeCounts(Map<String, Object> params) {
		String query = "match (t:" + Constants.RESERVATION_LABEL + ") " + getWhereClause(params) + " with t.booking_mode as Mode,count(*) as ModeCount return  collect([Mode,ModeCount])";
		Result<Map<String, Object>> result = template.query(query, params);

		Map<String, Object> countMap = convertResultToMap(result, "collect([Mode,ModeCount])");
		for (String status : UtilityMethods.getEnumValues(Constants.RESERVATION_MODULE, Constants.RESERVATION_BOOKING_MODE)) {
			if (!countMap.containsKey(status))
				countMap.put(status, 0);
		}
		return countMap;
	}

	private Map<String, Object> convertResultToMap(Result<Map<String, Object>> result, String key) {
		Map<String, Object> countMap = new HashMap<String, Object>();
		if (result != null) {
			Map<String, Object> map = result.singleOrNull();
			Object values = map.get(key);
			for (List<String> value : (List<List<String>>) values) {
				countMap.put(value.get(0), value.get(1));
			}
		}
		return countMap;
	}

	@Override
	public Map<String, Object> getReservationGuestCounts(Map<String, Object> params) {
		Map<String, Object> guestMap = new HashMap<String, Object>();

		StringBuilder getQuery = new StringBuilder("Match (t:Reservation)-[]-(g:GuestProfile)");
		getQuery.append(getWhereClause(params));
		getQuery.append(" with g.guid as guestGuid,g.mobile as guestMobile, count(*) as guestCount return  collect([guestGuid,guestMobile,guestCount])");
		Result<Map<String, Object>> result = template.query(getQuery.toString(), params);
		Map<String, Object> guestGuidMap = new HashMap<String, Object>();
		if (result != null) {
			Map<String, Object> map = result.singleOrNull();
			Object values = map.get("collect([guestGuid,guestMobile,guestCount])");
			for (List<String> value : (List<List<String>>) values) {
				if (value.get(1).equals(Constants.DUMMY_MOBILE))
					guestMap.put("anonymous", value.get(2));
				else
					guestGuidMap.put(value.get(0), value.get(2));
			}
			if (!guestMap.containsKey("anonymous"))
				guestMap.put("anonymous", 0);
		}

		Map<String, Object> oldParams = new HashMap<String, Object>();
		oldParams.putAll(params);
		oldParams.put(Constants.GUEST_GUID, Arrays.asList(guestGuidMap.keySet().toArray()));
		oldParams.put(Constants.ACT_END_BEFORE, params.get(Constants.EST_END_AFTER));
		oldParams.remove(Constants.EST_END_AFTER);
		oldParams.remove(Constants.EST_START_BEFORE);
		StringBuilder query = new StringBuilder("Match (t:Reservation)-[]-(g:GuestProfile)");
		query.append(getWhereClause(oldParams));
		query.append(" with g.guid as guestGuid,count(*) as guestCount WHERE guestCount > 0 return  collect([guestGuid,guestCount])");
		Result<Map<String, Object>> qresult = template.query(query.toString(), oldParams);
		Map<String, Object> map = convertResultToMap(qresult, "collect([guestGuid,guestCount])");
		int repeatCount = 0;
		int newCount = 0;
		for (String repeatGuest : map.keySet()) {
			repeatCount += ((Integer) guestGuidMap.get(repeatGuest));
			guestGuidMap.remove(repeatGuest);
		}
		for (Entry<String, Object> newGuest : guestGuidMap.entrySet()) {
			newCount += 1;
			repeatCount += ((Integer) newGuest.getValue() - 1);
		}
		guestMap.put("repeat", repeatCount);
		guestMap.put("new", newCount);
		return guestMap;
	}

	private List<Reservation> getReservationDetailsWithCypher(List<Reservation> reservation, String restID) {
		String reservationList = "";
		for (Reservation reservList : reservation) {
			reservationList = reservationList + "'" + reservList.getGuid() + "',";
			// break;

		}
		if (reservationList.length() > 1) {
			reservationList = reservationList.substring(0, reservationList.length() - 1);
		}
		StringBuffer query = new StringBuffer();
		
		
		
		if (null != restID) {
			query.append("WITH [" + reservationList + "] as resList UNWIND resList as rGuid"
					+ "  MATCH (reservation:Reservation{guid:rGuid})<-[guestRel:`GUEST_HAS_RESV`]-(guest:`GuestProfile`)<-[hg:`HAS_GUEST`]-(rest:`Restaurant` {guid :'" + restID +"'}) WITH guest,reservation,hg \n");
			query.append("  "
					+ "OPTIONAL MATCH (guest)-[rel:`GUEST_HAS_RESV` {rest_guid:'" + restID + "'}]->(t:Reservation {reservation_status : 'FINISHED'})"
					+ " WITH guest,hg,reservation,count(rel) as total_visit \n"
					+ " OPTIONAL MATCH (guest)-[r:GUEST_HAS_CONVERSATION {rest_guid:'" + restID + "'}]->(conversation:GuestConversation) \n");
		} else {
			query.append("WITH [" + reservationList + "] as resList UNWIND resList as rGuid"
					+ "  MATCH (reservation:Reservation{guid:rGuid})<-[guestRel:`GUEST_HAS_RESV`]-(guest:`GuestProfile`)<-[hg:`HAS_GUEST`]-(rest:`Restaurant`) WITH guest,reservation,hg \n");
			query.append("  MATCH (rest:`Restaurant`)-[hg:`HAS_GUEST`]->(guest)-[rel:`GUEST_HAS_RESV`]->(t:Reservation {reservation_status : 'FINISHED'}) "
					+ " WITH guest,hg,reservation,count(rel) as total_visit \n"
					+ " OPTIONAL MATCH (guest)-[r:GUEST_HAS_CONVERSATION]->(conversation:GuestConversation) \n");
		}
		query.append(" WHERE toInt(conversation.created_dt)>toInt(reservation.created_dt)  WITH guest,hg,reservation,total_visit,conversation,count(r) as total_conversation\n"
				+ "  MATCH (reservation)-[historyRel:`RESV_HISTORY`]->(reservationHistory:`ReservationHistory`)  \n" + "  \n"
				+ " RETURN DISTINCT reservation, guest,hg,reservationHistory, total_visit,total_conversation  ORDER BY toInt(reservationHistory.created_dt) ASC \n");

		Map<String, Object> map;// = new HashMap<String, Object>();

		Result<Map<String, Object>> results = template.query(query.toString(), null);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			map = i.next();
			// template.postEntityCreation((Node) map.get("reservation"),
			// Reservation.class);
			Reservation reservationData = template.convert(map.get("reservation"), Reservation.class);

			// template.postEntityCreation((Node) map.get("guest"),
			// GuestProfile.class);
			GuestProfile guest = template.convert(map.get("guest"), GuestProfile.class);
			
			Relationship relation = (Relationship) map.get("hg");
			try {
				guest.setFirstName((null == relation.getProperty(Constants.FIRST_NAME)) ? "" : relation.getProperty(Constants.FIRST_NAME).toString());
			 } catch (NotFoundException e) {
				 guest.setFirstName("");
			}
			try {
				guest.setIsVip((null == relation.getProperty("is_vip")) ? false : Boolean.valueOf(relation.getProperty("is_vip").toString()));
			 } catch (NotFoundException e) {
				// e.printStackTrace();
				 guest.setIsVip(false);
			}
			try {
				if(guest.getIsVip())
				{
				guest.setReason((null == relation.getProperty("reason")) ? null : relation.getProperty("reason").toString());
				}
				else
				{
					guest.setReason(null);
				}
			 } catch (NotFoundException e) {
				 e.printStackTrace();
				 guest.setReason(null);
			}
			
			try {
				guest.setGender((null == relation.getProperty(Constants.GENDER)) ? guest.getGender() : relation.getProperty(Constants.GENDER).toString());
			 } catch (NotFoundException e) {
			}
			try {
				guest.setFirstSeatedTime((null == relation.getProperty("first_seated")) ? null : new Date(Long.valueOf(relation.getProperty("first_seated").toString())));
			 } catch (NotFoundException e) {
				 guest.setFirstSeatedTime(null);
			}
			try {
				guest.setCorporateName((null == relation.getProperty("corporate_name")) ? null : relation.getProperty("corporate_name").toString());
			} catch (NotFoundException e) {
				 guest.setCorporateName(null);
			}
			
			try {
				guest.setCorporate((null == relation.getProperty("corporate_guid")) ? null : relation.getProperty("corporate_guid").toString());
			} catch (NotFoundException e) {
				 guest.setCorporate(null);
			}
			

			// template.postEntityCreation((Node) map.get("reservationHistory"),
			// ReservationHistory.class);
			ReservationHistory resv = template.convert(map.get("reservationHistory"), ReservationHistory.class);

			// template.postEntityCreation((Node) map.get("conversation"),
			// GuestConversation.class);
			GuestConversation conversation = template.convert(map.get("conversation"), GuestConversation.class);

			String total_visit = map.get("total_visit").toString();
			String total_conversation = map.get("total_conversation").toString();

			for (Reservation reservationListModifed : reservation) {

				if (reservationListModifed.getGuid().equalsIgnoreCase(reservationData.getGuid())) {
					/*
					 * Don't know who put the below 2 line code in get API and
					 * modified ealier code wrongly
					 */
					if (reservationListModifed.getBookingMode().equalsIgnoreCase(Constants.WALKIN_STATUS)) {
						reservationListModifed.setEstWaitingTime(reservationListModifed.getQuotedTime());
					}

					reservationListModifed.setGuest_firstName(guest.getFirstName());
					//reservationListModifed.setGuest_lastName(guest.getLastName());
					reservationListModifed.setGuest_mobile(guest.getMobile());
					reservationListModifed.setGuest_email(guest.getEmailId());
					reservationListModifed.setGuest_isd_code(guest.getIsd_code());
					reservationListModifed.setCurrentServerTime(new Date());
					reservationListModifed.setFirstSeatedTime(guest.getFirstSeatedTime());
					reservationListModifed.setCorporateName(guest.getCorporateName());
					reservationListModifed.setCorporateGuid(guest.getCorporate());

					if (guest.getIsVip() != null) {
						reservationListModifed.setIsVIP(String.valueOf(guest.getIsVip()));
					} else {
						reservationListModifed.setIsVIP(String.valueOf(false));
					}

					reservationListModifed.setTotal_guest_visit(total_visit);
					reservationListModifed.setReason(guest.getReason());
					reservationListModifed.setGender(guest.getGender());

					reservationListModifed.setConversation(total_conversation);

					if (null != reservationData.getHistory()) {
						reservationListModifed.getHistory().add(resv);
					} else {
						List<ReservationHistory> historyList = new ArrayList<ReservationHistory>();
						historyList.add(resv);
						reservationListModifed.setHistory(historyList);
					}
					break;
				}

			}

		}
		return reservation;
	}

	@Override
	public Map<String, Object> getReservationCoversCounts(Map<String, Object> params) {
		// params.put(arg0, arg1)
		// reservations=findByFields(Reservation.class, params);
		return null;
	}

	@Override
	public void updateReservationForShuffle(List<Reservation> reservation) {

		List<String> reservationGuid = new ArrayList<>();
		List<Map<String, Object>> mapList  = new ArrayList<>();

		for(Reservation r : reservation){
			if(r.getIsUpdated()){
				Map<String,Object> paramMap = new HashMap<>();
				paramMap.put("resvGuid", r.getGuid());
				paramMap.put("tableGuidList", r.getTableGuid());
				reservationGuid.add(r.getGuid());
				mapList.add(paramMap);
			}
		}		

		Map<String,Object> params = new HashMap<>();
		params.put("resvList", mapList);
		StringBuilder query = new StringBuilder();
		query.append("UNWIND {resvList} AS resvList"
				+ " MATCH (resv: Reservation {guid : resvList.resvGuid}) SET resv.table_guid=resvList.tableGuidList ");
		template.query(query.toString(), params);

	}
	
	
	
	@Override
	public void updateWalkinsForShuffle(List<Reservation> walkinList) 
	{

		List<Map<String, Object>> mapList  = new ArrayList<>();
		Long walkinTime = new Date().getTime();
		List<String> resvGuidList = new ArrayList<>();
		
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		
		
		Integer x = 0;
		
		for(Reservation r : walkinList)
		{
			Map<String,Object> paramMap = new HashMap<>();
			paramMap.put("resvGuid", r.getGuid());
			try {
				if(r.getEstEndTime() != null){
					paramMap.put("estEndTime", sdf.parse(sdf.format(r.getEstEndTime())));
				}else{
					paramMap.put("estEndTime", null);
				}
				paramMap.put("estStartTime",sdf.parse(sdf.format(r.getEstStartTime())));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			paramMap.put("tableGuidList", r.getTableGuid());
			mapList.add(paramMap);
			x++;
			
		}	


		Map<String,Object> params1 = new HashMap<>();
		params1.put("resvList", mapList);

		StringBuilder query = new StringBuilder();
		
		query.append("UNWIND {resvList} AS resvList"
				+ " MATCH (resv: Reservation {guid : resvList.resvGuid})"
				+ " SET resv.table_guid=resvList.tableGuidList,resv.est_start_time=resvList.estStartTime,resv.est_end_time=resvList.estEndTime");
		
	//	walkinTime = new Date().getTime();
		template.query(query.toString(), params1);
	//	Logger.debug("total time in getting update of walkin is---------------------------------------------" + (new Date().getTime() - walkinTime));

	}
	
	
	@Override
	public void updateReservationForTableShuffle(Reservation reservation, String tableGuid) {

		StringBuilder query = new StringBuilder();

		Map<String, Object> params = new HashMap<>();
		params.put(Constants.GUID, reservation.getGuid());
		params.put(Constants.TABLE_GUID, tableGuid);
		params.put("tableList", reservation.getTableGuid());
		params.put(Constants.EST_START_TIME, reservation.getEstStartTime());
		params.put(Constants.EST_END_TIME, reservation.getEstEndTime());
		params.put("relationshipName", TableReservationRelation.class.getName());
		params.put(Constants.RESERVATION_STATUS, reservation.getReservationStatus());

		query.append("MATCH (t:Reservation {guid:{guid}}),(table:Table {guid:{tableGuid}}) " + "SET t.table_guid={tableList},t.est_start_time={estStartTime},t.est_end_time={estEndTime} \n"
				+ "MERGE (table)-[:TBL_HAS_RESV {__type__:{relationshipName} , table_guid:{tableGuid} , "
				+ "reservation_status:{reservationStatus} , startDate:{estStartTime} , endDate:{estEndTime} , " + "resv_guid:{guid}}]->(t)");
		// Logger.debug("Updating reservation---=======================================================================");
		template.query(query.toString(), params);

	}

	@Override
	public List<Reservation> getQueueReservation(Map<String, Object> params) {
		List<Reservation> resvList = new ArrayList<>();
		String query = "MATCH (rest:Restaurant)-[p:" + RelationshipTypes.REST_HAS_QUEUE + "{rest_id:{restaurantGuid},cover:toInt({covers})}]->(queue:Queue)";
		query = query
				+ "-[q:"
				+ RelationshipTypes.QUEUE_HAS_RESV
				+ "]->(resv:Reservation) WHERE resv.reservation_status<>'SEATED' AND resv.reservation_status<>'FINISHED' AND resv.reservation_status<>'CANCELLED' AND resv.reservation_status<>'NO_SHOW' Return resv";
		Logger.debug("query is " + query);
		Iterator<Map<String, Object>> result = template.query(query, params).iterator();
		Reservation resv;
		String restGid = "";
		while (result.hasNext()) {
			Map<String, Object> map = result.next();
			Logger.debug("map is " + map);
			resv = template.convert(map.get("resv"), Reservation.class);
			restGid = resv.getRestaurantGuid();
			resvList.add(resv);
		}

		if (resvList.size() > 0) {
			resvList = this.getReservationDetailsWithCypher(resvList, restGid);
		}

		return resvList;

	}

	@Override
	public List<ReservationForTables> getReservationsForTables(Map<String, Object> params) {
		Long currentTime = (Long) params.get(Constants.START_TIME);
		//Long endTime = (Long) params.get(Constants.END_TIME);

		List<ReservationForTables> resvList = new ArrayList<>();
		String query = "MATCH (table: Table)-[rel:TBL_HAS_RESV]->(r:Reservation) WHERE table.guid IN {tableGuid} AND r.guid<>{guid} AND";
		query = query + " r.reservation_status <> 'FINISHED' AND r.reservation_status <> 'CANCELLED' AND r.reservation_status <> 'NO_SHOW' AND" + " ((r.reservation_status = 'SEATED') OR"
				+ " (r.reservation_status <> 'SEATED' AND toInt(r.est_start_time) <= toInt({startTime}) AND toInt(r.est_end_time) > toInt({startTime}) AND r.booking_mode = 'ONLINE') OR"
				+ " (r.reservation_status <> 'SEATED' AND toInt(r.est_start_time) < toInt({endTime}) AND toInt(r.est_start_time) > toInt({startTime}) AND r.booking_mode = 'ONLINE')) "
				+ "RETURN distinct r";

		Logger.debug("query is " + query);
		Iterator<Map<String, Object>> result = template.query(query, params).iterator();
		Reservation resv;
		ReservationForTables resvTable;
		while (result.hasNext()) {

			Map<String, Object> map = result.next();
			Logger.debug("map is " + map);
			resv = template.convert(map.get("r"), Reservation.class);
			resvTable = new ReservationForTables();
			resvTable.setEstEndTime(resv.getEstEndTime());
			resvTable.setEstStartTime(resv.getEstStartTime());
			resvTable.setReservationGuid(resv.getGuid());
			resvTable.setTableGuid(resv.getTableGuid());

			if (resv.getReservationStatus().equals(Constants.SEATED)) {
				resvTable.setReservationStatus(Constants.SEATED);
				resvTable.setAvailableAfter(null);
			} else if ((!resv.getReservationStatus().equals(Constants.SEATED)) && (resv.getEstStartTime().getTime() > currentTime)) {
				resvTable.setReservationStatus(Constants.RESERVED);
				resvTable.setAvailableAfter(resv.getEstStartTime().getTime() - (new Date().getTime()));
			} else {
				resvTable.setReservationStatus(Constants.ALLOCATED);
				resvTable.setAvailableAfter(resv.getEstEndTime().getTime() - (new Date().getTime()));
			}

			resvList.add(resvTable);

		}

		return resvList;
	}

	@Override
	public Boolean updateReservationMode(Reservation resv) {
		Boolean updated = false;
		Map<String, Object> params = new HashMap<>();
		params.put(Constants.GUID, resv.getGuid());
		params.put("bookingMode", resv.getBookingMode());
		params.put("queued", resv.isQueued());
		params.put(Constants.CREATED_DATE, resv.getCreatedDate().getTime());

		String query = "MATCH (r:Reservation {guid:{guid}}) SET r.booking_mode={bookingMode},r.created_dt=toInt({createdDate}),r.queued={queued} ";
		Logger.debug("query is " + query);
		template.query(query, params).iterator();
		updated = true;
		return updated;

	}

	@Override
	public Map<String, Object> validateRestGuestTable(Map<String, Object> params) {
		Map<String, Object> paramsReturn = new HashMap<String, Object>();

		String query = "Match (rest:Restaurant {guid:{" + Constants.REST_ID + "}}) \n" + "OPTIONAL MATCH (rest)-[r1:HAS_GUEST]->(guest:GuestProfile {guid:{" + Constants.GUEST_ID + "}}) \n"
				+ "OPTIONAL MATCH (rest)-[r:REST_HAS_TBL]->(t:Table{status:'ACTIVE'}) where t.guid IN {" + Constants.TABLE_GUID + "}" + " RETURN count(rest) as rest,rest.name as restName,"
				+ "rest.locality as RestLocalilty,rest.region as RestRegion,"
				+ "count(guest) as guest,r1.first_name as guestName,guest.mobile as guestMobile,count(r) as table, sum(toInt(t.max_covers)) as total_covers";
		System.out.println("---------Query -------" + query.toString());
		Map<String, Object> map;// = new HashMap<String, Object>();

		Result<Map<String, Object>> results = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			map = i.next();
			paramsReturn.put(Constants.REST_ID, (map.get("rest") != null) ? Integer.valueOf(map.get("rest").toString()) : null);
			paramsReturn.put(Constants.GUEST_ID, (map.get("guest") != null) ? Integer.valueOf(map.get("guest").toString()) : null);
			paramsReturn.put(Constants.TABLE_GUID, (map.get("table") != null) ? Integer.valueOf(map.get("table").toString()) : null);
			paramsReturn.put(Constants.RESTAURANT_NAME, (map.get("restName") != null) ? map.get("restName").toString() : null);
			paramsReturn.put(Constants.LOCALITY, (map.get("RestLocalilty") != null) ? map.get("RestLocalilty").toString() : null);
			paramsReturn.put(Constants.REGION, (map.get("RestRegion") != null) ? map.get("RestRegion").toString() : null);
			paramsReturn.put(Constants.NAME, (map.get("guestName") != null) ? map.get("guestName").toString() : null);
			paramsReturn.put(Constants.MOBILE, (map.get("guestMobile") != null) ? map.get("guestMobile").toString() : null);
			paramsReturn.put(Constants.COVERS, (map.get("total_covers") != null) ? map.get("total_covers").toString() : null);

		}

		return paramsReturn;

	}

	@Override
	public Reservation getReservationWithRespectToGuid(Map<String, Object> params) {
		List<Reservation> reservation = new ArrayList<Reservation>();
		StringBuffer query = new StringBuffer();
		query.append(" MATCH (reservation:Reservation{guid:{guid}})<-[guestRel:`GUEST_HAS_RESV`]-(guest:`GuestProfile`)<-[hg:`HAS_GUEST`]-(rest:`Restaurant`{guid:{" + Constants.REST_GUID + "}}) WITH guest,reservation,hg \n");
		query.append(" OPTIONAL MATCH(guest)-[r:GUEST_HAS_CONVERSATION]->(conversation:GuestConversation{rest_guid:{restaurantGuid}}) \n");
		query.append(" WHERE toInt(conversation.created_dt)>toInt(reservation.created_dt)  WITH guest,hg,reservation,count(r) as total_conversation\n"
				+ "    MATCH (reservation)-[historyRel:`RESV_HISTORY`]->(reservationHistory:`ReservationHistory`)  WITH guest,hg,reservation, total_conversation,reservationHistory \n"
				+ "    RETURN  reservation, guest,hg,reservationHistory, total_conversation  ORDER BY toInt(reservationHistory.created_dt) ASC \n");

		Map<String, Object> map = new HashMap<String, Object>();
		
		System.out.println("Query for getting resv with guid is ----------------------------" + query);

		Result<Map<String, Object>> results = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = results.iterator();
		Reservation reservationData = null;
		GuestProfile guest = null;
		while (i.hasNext()) {
			map = i.next();
			reservationData = template.convert(map.get("reservation"), Reservation.class);
			guest = template.convert(map.get("guest"), GuestProfile.class);
			
			Relationship relation = (Relationship) map.get("hg");
			try {
				guest.setFirstName((null == relation.getProperty(Constants.FIRST_NAME)) ? "" : relation.getProperty(Constants.FIRST_NAME).toString());
			 } catch (NotFoundException e) {
				 guest.setFirstName("");
			}
			try {
                guest.setIsVip((null == relation.getProperty("is_vip")) ? false : Boolean.valueOf(relation.getProperty("is_vip").toString()));
             } catch (NotFoundException e) {
            	 guest.setIsVip(false);
            }
			try {
				if(guest.getIsVip())
				{
				guest.setReason(relation.getProperty("reason").toString());
				}
				else
				{
					guest.setReason(null);
				}
			 } catch (NotFoundException e) {
				 guest.setReason(null);
			}
			try {
				guest.setGender((null == relation.getProperty(Constants.GENDER)) ? guest.getGender() : relation.getProperty(Constants.GENDER).toString());
			 } catch (NotFoundException e) {
			}
			try {
				guest.setFirstSeatedTime((null == relation.getProperty("first_seated")) ? null : new Date(Long.valueOf(relation.getProperty("first_seated").toString())));
			 } catch (NotFoundException e) {
				 guest.setFirstSeatedTime(null);
			}
			try {
				guest.setCorporateName((null == relation.getProperty("corporate_name")) ? null : relation.getProperty("corporate_name").toString());
			} catch (NotFoundException e) {
				 guest.setCorporateName(null);
			}
			
			try {
				guest.setCorporate((null == relation.getProperty("corporate_guid")) ? null : relation.getProperty("corporate_guid").toString());
			} catch (NotFoundException e) {
				 guest.setCorporate(null);
			}
			
			
			
			
			ReservationHistory resv = template.convert(map.get("reservationHistory"), ReservationHistory.class);

			String total_conversation = map.get("total_conversation").toString();

			if (!reservation.contains(reservationData)) {

				if (reservationData.getBookingMode().equalsIgnoreCase(Constants.WALKIN_STATUS)) {
					reservationData.setEstWaitingTime(reservationData.getQuotedTime());
				}
				reservationData.setConversation(total_conversation);
				List<ReservationHistory> history = new ArrayList<ReservationHistory>();
				history.add(resv);
				reservationData.setHistory(history);
				reservationData.setGuest_firstName(guest.getFirstName());
				//reservationData.setGuest_lastName(guest.getLastName());
				reservationData.setFirstSeatedTime(guest.getFirstSeatedTime());
				reservationData.setGuest_mobile(guest.getMobile());
				reservationData.setGuest_email(guest.getEmailId());
				reservationData.setIsVIP(guest.getIsVip().toString());
				reservationData.setReason(guest.getReason());
				reservationData.setGuest_isd_code(guest.getIsd_code());
				reservationData.setGender(guest.getGender());
				reservationData.setCorporateName(guest.getCorporateName());
				reservationData.setCorporateGuid(guest.getCorporate());
				reservation.add(reservationData);

			} else {

				if(reservation.size() > 0)
				reservation.get(0).getHistory().add(resv);
			}

		}

		if (guest != null) {
			params.put(Constants.GUEST_GUID, guest.getGuid());
			query = new StringBuffer();
			query.append("  MATCH (guest:`GuestProfile`{guid:{guestGuid}})-[rel:`GUEST_HAS_RESV`]->(t:Reservation{rest_guid:{restaurantGuid}}) WHERE t.reservation_status='FINISHED'  RETURN count(rel) as total_visit \n");

			map = new HashMap<String, Object>();

			results = template.query(query.toString(), params);
			i = results.iterator();
			while (i.hasNext()) {
				map = i.next();
				String total_visit = map.get("total_visit").toString();
				if(reservation.size() > 0)
				reservation.get(0).setTotal_guest_visit(total_visit);
			}
		}

		if(reservation.size() > 0)
		{
		return reservation.get(0);
		}
		else
		{
		 return null;
		}

	}

	@Override
	public void updateReservationWithShifEndCypherViaSchedular() {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (reservation:Reservation) " + " WHERE (reservation.reservation_status IN ['" + Constants.CREATED + "','" + Constants.ARRIVED + "','" + Constants.MSG_SENT + "','"
				+ Constants.ARRIVED + "','" + Constants.CONFIRMED + "','" + Constants.WAITING + "','" + Constants.CALLED
				+ "']  AND toInt(reservation.est_end_time)<=toInt('" + Calendar.getInstance().getTimeInMillis() + "') AND reservation.booking_mode = 'ONLINE') OR reservation.reservation_status='SEATED' \n" + "  RETURN reservation.guid as guid"
						+ ",reservation.reservation_status as status,reservation.created_by as createdBy,reservation.booked_by as bookedBy, reservation.booking_mode as bookingMode\n");
 
		/*Logger.info("*************Getting Reservation******************\n" + query.toString());*/

		Map<String, Object> map;
		
		List<Map<String, Object>> seatedMapList  = new ArrayList<>();
		List<Map<String, Object>> mapList  = new ArrayList<>();
		/*List<Map<String, Object>> walkinMapList  = new ArrayList<>();*/

		Result<Map<String, Object>> results = template.query(query.toString(), null);
		
		Iterator<Map<String, Object>> i = results.iterator();
		
		while (i.hasNext()) {
			map = i.next();
			String reserervationStatus = (String) map.get("status");
			String reserervationMode = (String) map.get("bookingMode");
			
			map.put("historyGuid", UtilityMethods.generateCtId());
			
			if(reserervationStatus.equals(Constants.SEATED))
				seatedMapList.add(map);
			/*else if(reserervationMode.equals(Constants.WALKIN_STATUS))
				map.put();
				walkinMapList.add(map);*/
			else
				mapList.add(map);
		}
		
		Map<String,Object> params = new HashMap<>();
		if(mapList.size() > 0){
			List<List<Map<String,Object>>> resvList = Lists.partition(mapList, 25);
			resvList.forEach(listOfReservations -> {
				params.put("resvList", listOfReservations);
				StringBuilder q = new StringBuilder();
				q.append("UNWIND {resvList} AS resvList \n"
						+ "MATCH (resv: Reservation {guid : resvList.guid}) SET resv.reservation_status = 'NO_SHOW' \n"
						+ "MERGE (resvHistory:ReservationHistory:_ReservationHistory {guid:resvList.historyGuid, created_dt: timeStamp(), "
						+ "created_by:resvList.createdBy,booked_by:resvList.bookedBy,resv_status:'"
						+  Constants.NO_SHOW_STATUS
						+ "'})<-[r:RESV_HISTORY{__type__:'ReservationHasHistory',guid:resvList.historyGuid}]-(resv) ");
				template.query(q.toString(), params);
			});	
		}
		
		if(seatedMapList.size() > 0){	
			List<List<Map<String,Object>>> resvList = Lists.partition(seatedMapList, 25);
			resvList.forEach(listOfReservations -> {
				params.put("resvList", listOfReservations);
				StringBuilder q = new StringBuilder();
				q.append("UNWIND {resvList} AS resvList \n"
						+ "MATCH (resv: Reservation {guid : resvList.guid})-[]-() SET resv.reservation_status = 'FINISHED', resv.act_end_time = timeStamp() \n"
						+ "MERGE (resvHistory:ReservationHistory:_ReservationHistory {guid: resvList.historyGuid, created_dt: timeStamp(), "
						+ "created_by: resvList.createdBy ,booked_by: resvList.bookedBy,resv_status:'"
						+  Constants.FINISHED
						+ "'})<-[r:RESV_HISTORY{__type__:'ReservationHasHistory',guid: resvList.historyGuid}]-(resv) ");
				template.query(q.toString(), params);
				
			});
		}
		
		
	}

	private List<String> msgSentExtensions(String guestGuid) {
		StringBuffer query2 = new StringBuffer();
		query2.append("Match (guest:GuestProfile{guid:'" + guestGuid + "'})-[rel:GUEST_HAS_RESV]->(resv:Reservation{reservation_status:'MSG_SENT'})"
				+ " return Collect(resv.gupshup_extension) as extensions");
		Result<Map<String, Object>> extensionsResult = template.query(query2.toString(), null);
		Map<String, Object> out = extensionsResult.singleOrNull();
		List<String> values = new ArrayList<String>();
		if (out != null) {
			values = ((List<String>) out.get("extensions"));
		}
		return values;
	}
	

	@Override
	public Map<Reservation, GuestProfile> getReservationDetailsOnDate(Map<String, Object> params) {
		Map<Reservation, GuestProfile> result = new HashMap<Reservation, GuestProfile>();
		StringBuffer query = new StringBuffer();

		Map<String, Object> map;// = new HashMap<String, Object>();

		query.append("Match (rest:`Restaurant`{guid:{" + Constants.REST_GUID + "}})-[hg:`HAS_GUEST`]->(guest:GuestProfile{status:'ACTIVE'})-[rel:GUEST_HAS_RESV{rest_guid:{" + Constants.REST_GUID + "}}]->(resv:Reservation{status:'ACTIVE',booking_mode:'ONLINE'})" + " WHERE toInt(resv."
				+ getPropertyName(Constants.EST_START_TIME) + ")>toInt({" + Constants.EST_START_AFTER + "}) AND" + " toInt(resv." + getPropertyName(Constants.EST_START_TIME) + ")<toInt({"
				+ Constants.EST_START_BEFORE + "})" + " return guest, resv, hg");

		Result<Map<String, Object>> results = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			map = i.next();
			Reservation reservationData = template.convert(map.get("resv"), Reservation.class);
			GuestProfile guest = template.convert(map.get("guest"), GuestProfile.class);
			
			Relationship relation = (Relationship) map.get("hg");
			try {
				guest.setFirstName((null == relation.getProperty(Constants.FIRST_NAME)) ? "" : relation.getProperty(Constants.FIRST_NAME).toString());
			 } catch (NotFoundException e) {
				 guest.setFirstName(null);
			}
			try {
				guest.setIsVip(Boolean.valueOf((relation.getProperty("is_vip").toString())));
			 } catch (NotFoundException e) {
				 guest.setIsVip(false);
			}
			try {
				if(guest.getIsVip())
				{
				guest.setReason(relation.getProperty("reason").toString());
				}
				else
				{
					guest.setReason(null);
				}
			 } catch (NotFoundException e) {
				 guest.setReason(null);
			}
			try {
				guest.setGender((null == relation.getProperty(Constants.GENDER)) ? guest.getGender() : relation.getProperty(Constants.GENDER).toString());
			 } catch (NotFoundException e) {
			}
			try {
				guest.setFirstSeatedTime((null == relation.getProperty("first_seated")) ? null : new Date(Long.valueOf(relation.getProperty("first_seated").toString())));
			 } catch (NotFoundException e) {
				 guest.setFirstSeatedTime(null);
			}
			
			
			
			result.put(reservationData, guest);
		}
		return result;
	}
	
	@Override
	public void addReviewToGuest(Map<String,Object> param) {       
       
		if(param.get(Constants.RESERVATION_STATUS).toString().equalsIgnoreCase(Constants.FINISHED)){
			String ratingReview = "MATCH (r:Restaurant {guid:{" + Constants.REST_GUID + "}})-[rel:HAS_GUEST{__type__:'HasGuest'}]->"
					+ "(guest:GuestProfile) WHERE guest.guid={" + Constants.GUEST_GUID + "} "
							+ " With r , guest , rel.review_count as count, rel.cumulative_rating as rating  \n";
			ratingReview = ratingReview + "MERGE (r)-[rel:HAS_GUEST{__type__:'HasGuest'}]"									
							                   + "->(guest) "
							                   + "SET rel.review_count=(toInt(count)+ 1),"
											   +  "rel.cumulative_rating=(toInt(rating)+{"+Constants.CUMULATIVE_RATING+"})"
							                   + "return rel";
			System.out.println("--*******************"+ratingReview.toString());
			template.query(ratingReview.toString(), param);
		}
		
		
	}
	
	
	
	@Override
	public void deleteWaitlistData(String resvGuid) 
	{
		
		Map<String , Object> params = new HashMap<>();
		params.put(Constants.GUID, resvGuid);

		StringBuilder query = new StringBuilder("MATCH (n:`Reservation`{guid:{" + Constants.GUID + "}})-[rh:RESV_HISTORY]->(h) DELETE rh,h");
		Logger.debug("query is " + query);
		Result<Map<String, Object>> r = template.query(query.toString(), params);
		
		query = new StringBuilder("MATCH (n:`Reservation`{guid:{" + Constants.GUID + "}}) \n OPTIONAL MATCH (n)-[r]-() DELETE r,n");
		Logger.debug("query is " + query);
		r = template.query(query.toString(), params);
		
	}


	@Override
	public List<Reservation> getReservationsForTime(Map<String,Object> paramMap) {
		// TODO Auto-generated method stub
		
		
		StringBuilder query = new StringBuilder();
		
		query.append("MATCH (t:`Reservation` {rest_guid : {restaurantGuid}})");
		query.append( " WHERE t.reservation_status IN ['CREATED','CONFIRMED','ARRIVED','SEATED','CALLED','MSG_SENT'] AND (toInt(t.est_start_time) >= toInt({startTime})  AND toInt(t.est_start_time) <= toInt({endTime})) ");
		query.append(" RETURN t.guid as guid,toInt(t.est_start_time) as startTime,toInt(t.est_end_time) as endTime  ");
		
		Map<String,Object> map = new HashMap<String, Object>();
		List<Reservation> resvList = new ArrayList<>();

		Iterator<Map<String, Object>> i = template.query(query.toString(), paramMap).iterator();
		while (i.hasNext()) 
		{
			map = i.next();
			Reservation reservation = new Reservation();
			reservation.setGuid(map.get("guid").toString());
			reservation.setEstEndTime(new Date((long) map.get("endTime")));
			reservation.setEstStartTime(new Date((long) map.get("startTime")));
			resvList.add(reservation);
		}
		
		
		
		return resvList;
	}

	
	@Override
	public List<CustomResvOpHr> getReservationDetailsByGuid(Map<String, Object> params) {
		List<CustomResvOpHr> reservationList = new ArrayList<>();
		Long estStartTime = 0l;
		Long estEndTime = 0l;

		
		//String query = " MATCH (resv:`Reservation`)<-[guestRel:`GUEST_HAS_RESV`]-(guest:`GuestProfile`)<-[hg:`HAS_GUEST`]-(rest:`Restaurant`{guid : {restaurantGuid}})"
				String query = " MATCH (rest:`Restaurant`{guid : {restaurantGuid}})-[hg:`HAS_GUEST`]->(guest:`GuestProfile`)-[guestRel:`GUEST_HAS_RESV`]->(resv:`Reservation`)"
				+ "  WHERE resv.guid IN {resvGuidList} ";
	
		query = query + " RETURN DISTINCT resv,guest, hg ";

		Logger.debug("Query------------------------------------------------------" + query.toString());
		Iterator<Map<String, Object>> i = template.query(query.toString(), params).iterator();
		Reservation reservation = null;
		CustomResvOpHr customResv = null;
		

		Map<String,Object> map ;

		while (i.hasNext()) 
		{
			map = i.next();

			reservation = template.convert(map.get("resv"), Reservation.class);
		
			GuestProfile guest = template.convert(map.get("guest"), GuestProfile.class);

			reservation.setGuestGuid(guest.getGuid());
			reservation.setGuest_mobile(guest.getMobile());
			reservation.setGuest_isd_code(guest.getIsd_code());
			


			Relationship relation = (Relationship) map.get("hg");
			try {
				reservation.setGuest_firstName((null == relation.getProperty(Constants.FIRST_NAME)) ? "" : relation.getProperty(Constants.FIRST_NAME).toString());
			} catch (NotFoundException e) {
				reservation.setGuest_firstName("");
			}
			try {
				reservation.setIsVIP(relation.getProperty("is_vip").toString());
			} catch (NotFoundException e) {
				reservation.setIsVIP(String.valueOf(false));
			}
			try {
				if(Boolean.valueOf(reservation.getIsVIP()))
				{
					reservation.setReason(relation.getProperty("reason").toString());
				}
				else
				{
					reservation.setReason(null);
				}
			} catch (NotFoundException e) {
				reservation.setReason(null);
			}

			customResv = new CustomResvOpHr(reservation);
			reservationList.add(customResv);

		}
		return reservationList;
	}
	
	@Override
	public TreeMap<Reservation, GuestProfile> getReservationDetails(Map<String, Object> params) {
		TreeMap<Reservation, GuestProfile> result = new TreeMap<Reservation, GuestProfile>();
		StringBuffer query = new StringBuffer();

		Map<String, Object> map;// = new HashMap<String, Object>();

		query.append("Match (rest:`Restaurant`{guid:{" + Constants.REST_GUID + "}})-[hg:`HAS_GUEST`]->(guest:GuestProfile{status:'ACTIVE'})-[rel:GUEST_HAS_RESV{rest_guid:{" + Constants.REST_GUID + "}}]->(resv:Reservation{status:'ACTIVE',booking_mode:'ONLINE'})" + " WHERE toInt(resv."
				+ getPropertyName(Constants.EST_START_TIME) + ")>{" + Constants.EST_START_AFTER + "} AND" + " toInt(resv." + getPropertyName(Constants.EST_START_TIME) + ")<{"
				+ Constants.EST_START_BEFORE + "}" + " return guest, resv, hg order by toInt(resv.est_start_time) ASC");

		Result<Map<String, Object>> results = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			map = i.next();
			Reservation reservationData = template.convert(map.get("resv"), Reservation.class);
			GuestProfile guest = template.convert(map.get("guest"), GuestProfile.class);
			
			Relationship relation = (Relationship) map.get("hg");
			try {
				guest.setFirstName((null == relation.getProperty(Constants.FIRST_NAME)) ? "" : relation.getProperty(Constants.FIRST_NAME).toString());
			 } catch (NotFoundException e) {
			}
			try {
				guest.setIsVip(Boolean.valueOf((relation.getProperty("is_vip").toString())));
			 } catch (NotFoundException e) {
			}
			try {
				guest.setGender((null == relation.getProperty(Constants.GENDER)) ? guest.getGender() : relation.getProperty(Constants.GENDER).toString());
			 } catch (NotFoundException e) {
			}
			
			
			
			result.put(reservationData, guest);
		}
		return result;
	}
	
	
	
	@Override
	public List<Map<String, Object>> getReservationsReportData(Map<String, Object> params) {
		StringBuffer query = new StringBuffer();
		query.append("MATCH (r:Restaurant{guid:{"+Constants.REST_GUID+"}})-[hg:HAS_GUEST]->(g:GuestProfile)-[ghr:GUEST_HAS_RESV]->(t:Reservation{reservation_status:{"+Constants.RESERVATION_STATUS+"},rest_guid:{"+Constants.REST_GUID+"}})");
		query.append(" WHERE (toInt(t.act_start_time)<={"+Constants.END_TIME+"} AND toInt(t.act_end_time)>={"+Constants.START_TIME+"})");
		query.append(" RETURN SUM(t.num_covers) as covers,g.dummy as dummy,COUNT(t) as resvCount ,(g.status={"+Constants.STATUS+"} AND hg.status={"+Constants.STATUS+"}) as status ");
		Result<Map<String, Object>> results = template.query(query.toString(), params);
		List<Map<String, Object>> list = new ArrayList<>();
		results.iterator().forEachRemaining(list::add);
		return list;
	}
	
	
	
	

}
