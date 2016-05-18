package com.clicktable.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.joda.time.DateTime;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.QueueDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.StaffDao;
import com.clicktable.model.BlackOutHours;
import com.clicktable.model.BlackOutShift;
import com.clicktable.model.CustomBlackOutHours;
import com.clicktable.model.CustomOperationalHour;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.HistoricalTat;
import com.clicktable.model.OperationalHours;
import com.clicktable.model.ParentAccount;
import com.clicktable.model.Reservation;
import com.clicktable.model.RestSystemConfigModel;
import com.clicktable.model.Restaurant;
import com.clicktable.model.RestaurantAddress;
import com.clicktable.model.RestaurantContactInfo;
import com.clicktable.model.RestaurantContactInfoAdmin;
import com.clicktable.model.RestaurantGeneralInfo;
import com.clicktable.model.Shift;
import com.clicktable.relationshipModel.HasAddress;
import com.clicktable.service.intf.StaffService;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;
import com.clicktable.util.UtilityMethods;

@Service
public class RestaurantDaoImpl extends GraphDBDao<Restaurant> implements RestaurantDao {

	public RestaurantDaoImpl() {
		super();
		this.setType(Restaurant.class);
	}

	@Autowired
	QueueDao queueDao;

	@Autowired
	CustomerDao guestDao;

	@Autowired
	StaffDao staffDao;
	
	@Autowired
	StaffService staffService;
	
	@Override
	protected StringBuilder getWhereClause(Map<String, Object> params) {
		StringBuilder query = super.getWhereClause(params);

		if (params.containsKey(Constants.SHIFT_END_TIME_BEFORE)) {
			addPrefix(query);
			params.put(Constants.SHIFT_END_TIME_BEFORE, ((DateTime) params.get(Constants.SHIFT_END_TIME_BEFORE)).getMillisOfDay());
			query.append("toInt(t." + getPropertyName(Constants.FORCED_SHIFT_END_TIME) + ") > toInt({" + Constants.SHIFT_END_TIME_BEFORE + "})");
		}
		if (params.containsKey(Constants.SHIFT_END_TIME_AFTER)) {
			addPrefix(query);
			params.put(Constants.SHIFT_END_TIME_AFTER, ((DateTime) params.get(Constants.SHIFT_END_TIME_AFTER)).getMillisOfDay());
			query.append("toInt(t." + getPropertyName(Constants.FORCED_SHIFT_END_TIME) + ") <= toInt({" + Constants.SHIFT_END_TIME_AFTER + "})");
		}

		return query;
	}

	@Override
	public Iterator<Map<String, Object>> getSystemConfig(Map<String, Object> params) {
		String query = " MATCH (a:Restaurant{guid:{"
				+ Constants.REST_GUID
				+ "}})-[r:`REST_HAS_TAT`]->(b) RETURN r.value,r.family_tat,b.name,a.reserve_overlap_time,a.reserve_release_time,a.dining_slot_interval,a.waitlist_release_time,a.otp_mobile,a.forced_shift_end_time,a.buffer_open_time,a.bar,a.bar_max_time";
		Logger.debug("match query is ------------------" + query);
		Result<Map<String, Object>> r = template.query(query, params);
		Iterator<Map<String, Object>> itr = r.iterator();
		Logger.debug("delete query executed,Result is " + r);
		return itr;
	}
	
	@Override
	public Iterator<Map<String, Object>> getCustomSystemConfig(Map<String, Object> params) {
		StringBuilder query = new StringBuilder(" MATCH (a:Restaurant{guid:{"
				+ Constants.REST_GUID
				+ "}})-[r:`REST_HAS_TAT`]->(b) ");
		if(params.containsKey(Constants.UPDATED_AFTER)){
					query.append(" WHERE toInt(a.updated_dt)>toInt({"+Constants.UPDATED_AFTER+"}) OR toInt(b.updated_dt)>toInt({"+Constants.UPDATED_AFTER+"})");					
		}
		query.append(" RETURN r.value,r.family_tat,b.name,a.bar,a.bar_max_time,a.disp_name as displayName ,a.name as restaurantName");
		Logger.debug("match query is ------------------" + query);
		Result<Map<String, Object>> r = template.query(query.toString(), params);
		Iterator<Map<String, Object>> itr = r.iterator();
		Logger.debug("delete query executed,Result is " + r);
		return itr;
	}



	@Override
	public HasAddress saveRelationModel(HasAddress relationModel) {

		return template.save(relationModel);

	}

	@Override
	public boolean addSystemConfig(RestSystemConfigModel rest, String token) {

		Map<String,Object> param = new HashMap<>();
		param.put(Constants.REST_GUID, rest.getRestaurantGuid());

		String query = "MATCH (a:Restaurant {guid:{" + Constants.REST_GUID + "}})-[q:`REST_HAS_TAT`]->(b) RETURN count(q) AS count";
		/*Logger.debug("delete query is ------------------" + query);*/
		Iterator<Map<String, Object>> itr = template.query(query, param).iterator();
		Integer count = 0;
		while(itr.hasNext())
		{
			Map<String,Object> map = itr.next();
			count = (Integer) map.get("count");
		}


		Map<String,Object> paramMap ;
		List<Map<String, Object>> mapList = new ArrayList<>();


		if (rest.getTat_wd_12() != null) {
			paramMap = new HashMap<>();
			paramMap.put("name", "tat_wd_12");
			paramMap.put("value", rest.getTat_wd_12());
			paramMap.put("familyTatValue", (rest.getFamily_tat_wd_12() == null) ? rest.getTat_wd_12() : rest.getFamily_tat_wd_12());
			mapList.add(paramMap);

		}
		if (rest.getTat_we_12() != null) {
			paramMap = new HashMap<>();
			paramMap.put("name", "tat_we_12");
			paramMap.put("value", rest.getTat_we_12());
			paramMap.put("familyTatValue", (rest.getFamily_tat_we_12() == null) ? rest.getTat_we_12() : rest.getFamily_tat_we_12());
			mapList.add(paramMap);
		}
		if (rest.getTat_wd_34() != null) {
			paramMap = new HashMap<>();
			paramMap.put("name", "tat_wd_34");
			paramMap.put("value", rest.getTat_wd_34());
			paramMap.put("familyTatValue", (rest.getFamily_tat_wd_34() == null) ? rest.getTat_wd_34() : rest.getFamily_tat_wd_34());
			mapList.add(paramMap);
		}
		if (rest.getTat_we_34() != null) {
			paramMap = new HashMap<>();
			paramMap.put("name", "tat_we_34");
			paramMap.put("value", rest.getTat_we_34());
			paramMap.put("familyTatValue", (rest.getFamily_tat_we_34() == null) ? rest.getTat_we_34() : rest.getFamily_tat_we_34());
			mapList.add(paramMap);
		}
		if (rest.getTat_wd_56() != null) {
			paramMap = new HashMap<>();
			paramMap.put("name", "tat_wd_56");
			paramMap.put("value", rest.getTat_wd_56());
			paramMap.put("familyTatValue", (rest.getFamily_tat_wd_56() == null) ? rest.getTat_wd_56() : rest.getFamily_tat_wd_56());
			mapList.add(paramMap);
		}
		if (rest.getTat_we_56() != null) {
			paramMap = new HashMap<>();
			paramMap.put("name", "tat_we_56");
			paramMap.put("value", rest.getTat_we_56());
			paramMap.put("familyTatValue", (rest.getFamily_tat_we_56() == null) ? rest.getTat_we_56() : rest.getFamily_tat_we_56());
			mapList.add(paramMap);
		}
		if (rest.getTat_wd_78() != null) {
			paramMap = new HashMap<>();
			paramMap.put("name", "tat_wd_78");
			paramMap.put("value", rest.getTat_wd_78());
			paramMap.put("familyTatValue", (rest.getFamily_tat_wd_78() == null) ? rest.getTat_wd_78() : rest.getFamily_tat_wd_78());
			mapList.add(paramMap);
		}
		if (rest.getTat_we_78() != null) {
			paramMap = new HashMap<>();
			paramMap.put("name", "tat_we_78");
			paramMap.put("value", rest.getTat_we_78());
			paramMap.put("familyTatValue", (rest.getFamily_tat_we_78() == null) ? rest.getTat_we_78() : rest.getFamily_tat_we_78());
			mapList.add(paramMap);
		}
		if (rest.getTat_wd_8P() != null) {
			paramMap = new HashMap<>();
			paramMap.put("name", "tat_wd_8P");
			paramMap.put("value", rest.getTat_wd_8P());
			paramMap.put("familyTatValue", (rest.getFamily_tat_wd_8P() == null) ? rest.getTat_wd_8P() : rest.getFamily_tat_wd_8P());
			mapList.add(paramMap);
		}
		if (rest.getTat_we_8P() != null) {
			paramMap = new HashMap<>();
			paramMap.put("name", "tat_we_8P");
			paramMap.put("value", rest.getTat_we_8P());
			paramMap.put("familyTatValue", (rest.getFamily_tat_we_8P() == null) ? rest.getTat_we_8P() : rest.getFamily_tat_we_8P());
			mapList.add(paramMap);
		}


		Map<String,Object> params1 = new HashMap<>();
		params1.put("mapList", mapList);
		params1.put(Constants.REST_GUID, rest.getRestaurantGuid());

		query = " ";
		if(count == 0){
			query = query + "UNWIND {mapList} AS t  "
					+ "MATCH (r:Restaurant{guid:{" + Constants.REST_GUID + "}}), (k:Tat{name: t.name}) \n";
			query = query + "MERGE (r)-[rel:REST_HAS_TAT{__type__:'RestaurantHasTat',value:t.value,family_tat:t.familyTatValue}]-(k) ";
		}else{
			query = query + "UNWIND {mapList} AS t  MATCH (r:Restaurant{guid:{" + Constants.REST_GUID + "}})-[rel:REST_HAS_TAT{__type__:'RestaurantHasTat'}]-(k:Tat{name: t.name})\n";
			query = query + "SET rel.value=t.value,rel.family_tat=t.familyTatValue";
		}

		Logger.debug("final query is " + query);

		Result<Map<String, Object>> r = template.query(query, params1);
		Logger.debug("relationship creation query executed,Result is " + r);

		if (rest.getReserveOverlapTime() == null) {
			rest.setReserveOverlapTime(Constants.RESERVE_OVERLAP_TIME_DEFAULT);
		}


		try {
			query = "MATCH (r:Restaurant {guid:'" + rest.getRestaurantGuid() + "'}) SET ";
			if (rest.getReserveOverlapTime() == null) {		
				query = query + "r.reserve_overlap_time='" + Constants.RESERVE_OVERLAP_TIME_DEFAULT + "'";		
			} else {		
				query = query + "r.reserve_overlap_time=" + rest.getReserveOverlapTime();		
			}

			if (rest.getOtpMobile() != null) {
				query = query + ",r.otp_mobile='" + rest.getOtpMobile() + "'";
			}


			if (rest.getBar() != null) {
				query = query + ",r.bar=" + rest.getBar();
			}
			if (rest.getBarMaxTime() != null) {
				query = query + ",r.bar_max_time=" + rest.getBarMaxTime();
			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		query = query + " Return r";
		template.query(query, params1);
		//Logger.debug("update rest query executed,Result is " + r);

		return true;
	}

	/**
	 * Method to create relationship of a restaurant with a section
	 */
	@Override
	public boolean addRestaurantSection(String restGuid, String sectionGuid) {
		Map<String , Object> params = new HashMap<>();
		params.put(Constants.SECTION_GUID, sectionGuid);
		String query = "MATCH (r:Restaurant {guid:'" + restGuid + "'}),(t:Section) WHERE t.guid={" + Constants.SECTION_GUID + "} \n";
		query = query + "MERGE (r)-[:HAS_SECTION{__type__:'HasSection'}]->(t)";
		Logger.debug("query is " + query);
		Result<Map<String, Object>> r = template.query(query, params);
		Logger.debug("query executed,Result is " + r);

		return true;
	}

	@Override
	public OperationalHours getOperationalHours(Map<String, Object> params) {
		// TODO Auto-generated method stub

		StringBuffer query = new StringBuffer();
		OperationalHours op_hr = new OperationalHours();
		List<Shift> monday = new ArrayList<Shift>();
		List<Shift> tuesday = new ArrayList<Shift>();
		List<Shift> wednesday = new ArrayList<Shift>();
		List<Shift> thursday = new ArrayList<Shift>();
		List<Shift> friday = new ArrayList<Shift>();
		List<Shift> saturday = new ArrayList<Shift>();
		List<Shift> sunday = new ArrayList<Shift>();

		query.append("MATCH (a:Restaurant {guid:'" + params.get(Constants.REST_GUID).toString() + "'})-[rel:`REST_HAS_OPHR`]->(shift:`Shift`) ");
		if (null != params.get(Constants.DAY_NAME)) {
			StringTokenizer st = new StringTokenizer(params.get(Constants.DAY_NAME).toString(), ",");
			String day_name = "";
			while (st.hasMoreTokens()) {
				day_name = day_name + "'" + st.nextToken() + "',";
			}
			day_name = day_name.substring(0, (day_name.length() - 1));
			query.append(" WHERE rel.day IN [" + day_name + "] ");
		}
		query.append("Return shift,rel");
		Result<Map<String, Object>> results = template.query(query.toString(), null);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			Relationship relation = (Relationship) map.get("rel");
			Node node = (Node) map.get("shift");
			op_hr.setDiningSlot(Integer.parseInt(node.getProperty("diningSlot").toString()));
			op_hr.setRestGuid(params.get(Constants.REST_GUID).toString());
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.MONDAY)) {
				monday.add(getShiftObj(node, relation));
				op_hr.setMonday(monday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.TUESDAY)) {
				tuesday.add(getShiftObj(node, relation));
				op_hr.setTuesday(tuesday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.WEDNESDAY)) {
				wednesday.add(getShiftObj(node, relation));
				op_hr.setWednesday(wednesday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.THURSDAY)) {
				thursday.add(getShiftObj(node, relation));
				op_hr.setThursday(thursday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.FRIDAY)) {
				friday.add(getShiftObj(node, relation));
				op_hr.setFriday(friday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.SATURDAY)) {
				saturday.add(getShiftObj(node, relation));
				op_hr.setSaturday(saturday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.SUNDAY)) {
				sunday.add(getShiftObj(node, relation));
				op_hr.setSunday(sunday);
			}
		}

		return op_hr;
	}

	private Shift getShiftObj(Node node, Relationship relation) {

		Shift shift = new Shift();
		shift.setDay(relation.getProperty(Constants.DAY_NAME).toString());
		shift.setStartTimeInMillis(Long.parseLong(node.getProperty("startTimeInMillis").toString()));
		shift.setEndTimeInMillis(Long.parseLong(node.getProperty("endTimeInMillis").toString()));
		shift.setAll_day(Boolean.valueOf(node.getProperty("all_day").toString()));
		shift.setShiftName(node.getProperty("shiftName").toString());
		shift.setDiningSlot(Integer.parseInt(node.getProperty("diningSlot").toString()));
		int mins = (int) (shift.getStartTimeInMillis() / 1000 / 60);
		shift.setStartTime(getTimeInHHMM(mins));

		mins = (int) (shift.getEndTimeInMillis() / 1000 / 60);
		shift.setEndTime(getTimeInHHMM(mins));

		return shift;
	}
	
	@Override
	public CustomOperationalHour getCustomOperationalHours(Map<String, Object> params) {
		// TODO Auto-generated method stub

		StringBuffer query = new StringBuffer();
		CustomOperationalHour op_hr = new CustomOperationalHour();
		List<CustomOperationalHour.CustomShift> monday = new ArrayList<CustomOperationalHour.CustomShift>();
		List<CustomOperationalHour.CustomShift> tuesday = new ArrayList<CustomOperationalHour.CustomShift>();
		List<CustomOperationalHour.CustomShift> wednesday = new ArrayList<CustomOperationalHour.CustomShift>();
		List<CustomOperationalHour.CustomShift> thursday = new ArrayList<CustomOperationalHour.CustomShift>();
		List<CustomOperationalHour.CustomShift> friday = new ArrayList<CustomOperationalHour.CustomShift>();
		List<CustomOperationalHour.CustomShift> saturday = new ArrayList<CustomOperationalHour.CustomShift>();
		List<CustomOperationalHour.CustomShift> sunday = new ArrayList<CustomOperationalHour.CustomShift>();

		query.append("MATCH (a:Restaurant {guid:'" + params.get(Constants.REST_GUID).toString() + "'})-[rel:`REST_HAS_OPHR`]->(shift:`Shift`) ");
		if (null != params.get(Constants.DAY_NAME)) {
			StringTokenizer st = new StringTokenizer(params.get(Constants.DAY_NAME).toString(), ",");
			String day_name = "";
			while (st.hasMoreTokens()) {
				day_name = day_name + "'" + st.nextToken() + "',";
			}
			day_name = day_name.substring(0, (day_name.length() - 1));
			query.append(" WHERE rel.day IN [" + day_name + "] ");
		}
		
		if(params.containsKey(Constants.UPDATED_AFTER)){
			query.append(" WHERE toInt(a.updated_dt)>toInt({"+Constants.UPDATED_AFTER+"}) OR toInt(shift.updated_dt)>toInt({"+Constants.UPDATED_AFTER+"})");					
		}
		
		query.append("Return shift.shiftName as shiftName, shift.diningSlot as diningSlot, shift.startTimeInMillis as startTime,shift.endTimeInMillis as endTime, rel");
		Result<Map<String, Object>> results = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			Relationship relation = (Relationship) map.get("rel");
			//Node node = (Node) map.get("shift");
			//op_hr.setDiningSlot(new Integer(30));
			//op_hr.setRestGuid(params.get(Constants.REST_GUID).toString());
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.MONDAY)) {
				monday.add(getCustomShiftObj(map, relation));
				op_hr.setMonday(monday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.TUESDAY)) {
				tuesday.add(getCustomShiftObj(map, relation));
				op_hr.setTuesday(tuesday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.WEDNESDAY)) {
				wednesday.add(getCustomShiftObj(map, relation));
				op_hr.setWednesday(wednesday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.THURSDAY)) {
				thursday.add(getCustomShiftObj(map, relation));
				op_hr.setThursday(thursday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.FRIDAY)) {
				friday.add(getCustomShiftObj(map, relation));
				op_hr.setFriday(friday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.SATURDAY)) {
				saturday.add(getCustomShiftObj(map, relation));
				op_hr.setSaturday(saturday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.SUNDAY)) {
				sunday.add(getCustomShiftObj(map, relation));
				op_hr.setSunday(sunday);
			}
		}

		return op_hr;
	}

	private CustomOperationalHour.CustomShift getCustomShiftObj(Map<String, Object> map, Relationship relation) {

		CustomOperationalHour.CustomShift shift = new CustomOperationalHour().new CustomShift();
		//shift.setDay(relation.getProperty(Constants.DAY_NAME).toString());
		shift.setStartTimeInMillis(Long.parseLong(map.get("startTime").toString()));
		shift.setEndTimeInMillis(Long.parseLong(map.get("endTime").toString()));
		shift.setShiftName(map.get("shiftName").toString());
		shift.setDiningSlot(Integer.parseInt(map.get("diningSlot").toString()));

		return shift;
	}

	private String getTimeInHHMM(int mins) {
		StringBuilder time = new StringBuilder();
		time.append(mins / 60).append(':').append(mins % 60);
		return time.toString();
	}

	/**
	 * Method to create Operational Hours for a Restaurant
	 */
	@Override
	public boolean addOperationalHours(OperationalHours ophr)

	{
		StringBuffer query = new StringBuffer();
		SimpleDateFormat format = new SimpleDateFormat(Constants.TIME_FORMATTING);

		try {
			// delete all operational hours set before
			query.append("MATCH (a:Restaurant {guid:'" + ophr.getRestGuid() + "'})-[q:`REST_HAS_OPHR`]->(shift:`Shift`) DELETE q,shift");
			Logger.debug("------query is " + query);
			template.query(query.toString(), null);
			Logger.debug("------------Relationship and Operational Hour deleted ! ");

			// Add operational hours
			query = new StringBuffer();
			query.append(" MATCH (a:Restaurant {guid:'" + ophr.getRestGuid() + "'})  SET a.dining_slot_interval=" + ophr.getDiningSlot().toString() + "\n");
			if (null != ophr.getMonday() && !ophr.getMonday().isEmpty()) {
				setOperationalHourQuery(query, ophr.getMonday(), Constants.MONDAY);
			}
			if (null != ophr.getTuesday() && !ophr.getTuesday().isEmpty()) {
				setOperationalHourQuery(query, ophr.getTuesday(), Constants.TUESDAY);
			}
			if (null != ophr.getWednesday() && !ophr.getWednesday().isEmpty()) {
				setOperationalHourQuery(query, ophr.getWednesday(), Constants.WEDNESDAY);
			}
			if (null != ophr.getThursday() && !ophr.getThursday().isEmpty()) {
				setOperationalHourQuery(query, ophr.getThursday(), Constants.THURSDAY);
			}
			if (null != ophr.getFriday() && !ophr.getFriday().isEmpty()) {
				setOperationalHourQuery(query, ophr.getFriday(), Constants.FRIDAY);
			}
			if (null != ophr.getSaturday() && !ophr.getSaturday().isEmpty()) {
				setOperationalHourQuery(query, ophr.getSaturday(), Constants.SATURDAY);
			}
			if (null != ophr.getSunday() && !ophr.getSunday().isEmpty()) {
				setOperationalHourQuery(query, ophr.getSunday(), Constants.SUNDAY);
			}

			Logger.info("---------Query----------" + query.toString());

			template.query(query.toString(), null);

		} catch (Exception ex) {
			// catch Appropriate responses and update the error list
			Logger.debug("Exception is ---------" + ex.getLocalizedMessage());
			return false;
		}

		return true;
	}

	private String setOperationalHourQuery(StringBuffer query, List<Shift> shiftList, String day) {

		for (Shift shift : shiftList) {
			query.append(" CREATE (a)-[:`REST_HAS_OPHR` {day:'" + day + "'}]->(:`Shift` {");
			query.append(" shiftName:'" + shift.getShiftName().toString() + "' ,");
			query.append(" all_day:" + shift.isAll_day() + " ,");
			query.append(" endTimeInMillis:'" + shift.getEndTimeInMillis() + "' ,");
			query.append(" startTimeInMillis:'" + shift.getStartTimeInMillis() + "' ,");
			query.append(" diningSlot:'" + shift.getDiningSlot() + "' })\n");
		}

		return query.toString();
	}

	/**
	 * Method to delete relationship of a restaurant with a section
	 */
	@Override
	public boolean deleteRestaurantSection(String restGuid, String sectionGuid) {
		Map<String , Object> params = new HashMap<>();
		params.put(Constants.REST_GUID, restGuid);
		params.put(Constants.GUID, sectionGuid);
		StringBuilder query = new StringBuilder("MATCH (r:Restaurant{guid:{" + Constants.REST_GUID + "}})-[q:HAS_SECTION]->(t:Section) WHERE t.guid={" + Constants.GUID + "} \n");
		query.append("OPTIONAL MATCH (t:Section)-[p:HAS_TBL]->(s:Table) WHERE t.guid={" + Constants.GUID + "} DELETE q,p");
		Logger.debug("query is " + query);
		Result<Map<String, Object>> r = template.query(query.toString(), params);
		Logger.debug("query executed,Result is " + r);

		return true;
	}

	@Override
	public Restaurant findRestaurantByGuid(String guid) {
		StringBuilder query = new StringBuilder();
		Map<String , Object> params = new HashMap<>();
		params.put(Constants.GUID, guid);
		query.append("MATCH (res:" + Constants.RESTAURANT_LABEL + ")");

		query.append(" WHERE res.guid={" + Constants.GUID + "}");
		query.append(" return res");
		Iterator<Map<String, Object>> results = template.query(query.toString(), params).iterator();
		Restaurant rest = null;
		if (results.hasNext()) {
			Map<String, Object> map = results.next();
			rest = template.convert(map.get("res"), Restaurant.class);

		}

		return rest;

	}

	/**
	 * Method to create relationship of a restaurant with address
	 */
	@Override
	public Long addRestaurantAddress(Restaurant rest, RestaurantAddress address) {
		Long id = 0L;
		Map<String, Object> params = new HashMap<>();
		params.put(Constants.GUID, address.getGuid());
		params.put(Constants.REST_GUID, rest.getGuid());
		String query = "MATCH (r:Restaurant {guid:{" + Constants.REST_GUID + "}}),(t:RestaurantAddress) WHERE t.guid={" + Constants.GUID + "} \n";
		query = query + "MERGE (r)-[rel:HAS_ADDRESS{__type__:'HasAddress'}]->(t) return rel";

		Logger.debug("query is " + query);
		Result<Map<String, Object>> result = template.query(query, params);
		Logger.debug("query executed,Result is " + result);
		Iterator<Map<String, Object>> itr = result.iterator();
		while (itr.hasNext()) {
			// Set<Entry<String, Object>> entrySet = ((Map<String, Object>)
			// itr.next()).entrySet();
			// / Logger.debug("entry set is "+entrySet);
			for (Map.Entry<String, Object> entry : ((Map<String, Object>) itr.next()).entrySet()) {
				Relationship rel = (Relationship) entry.getValue();
				Logger.debug("relationship is " + rel.getId());
				id = rel.getId();
			}
		}

		return id;
	}

	@Override
	public String updateRestaurantGeneralInfo(RestaurantGeneralInfo rest, Map<String, Object> objectAsMap) {
		// TODO Auto-generated method stub
		StringBuilder query = new StringBuilder();

		objectAsMap.forEach((x, y) -> System.out.println(x + "__" + y));
		query.append("MATCH (t:`" + type.getSimpleName() + "`" + "{guid:'" + rest.getGuid() + "'})");
		query.append(" SET ");
		query = getReturnQuery(objectAsMap, query);
		query.append(" RETURN t");
		template.query(query.toString(), objectAsMap);
		return rest.getGuid();

	}

	/**
	 * Method to update contact info of restaurant
	 */
	@Override
	public Restaurant updateContactInfo(RestaurantContactInfo rest) {

		Map<String, Object> params = new HashMap<String, Object>();

		String query = "MATCH (r:Restaurant {guid:'" + rest.getGuid()
				+ "'}) SET ";

		if (null != rest.getPhoneNo2()) {
			params.put("phoneNo2", rest.getPhoneNo2());
			query = query + "r.phone_no_2={" + "phoneNo2" + "},";
		}
		if (null != rest.getWebsite()) {
			params.put("website", rest.getWebsite());
			query = query + "r.website={" + "website" + "},";
		}
		if (null != rest.getEmail()) {
			params.put(Constants.EMAIL, rest.getEmail());
			query = query + "r.email={" + Constants.EMAIL + "},";
		}
		if (null != rest.getLatitude()) {
			params.put("latitude", rest.getLatitude());
			query = query + "r.latitude={" + "latitude" + "},";
		}
		if (null != rest.getLongitude()) {
			params.put("longitude", rest.getLongitude());
			query = query + "r.longitude={" + "longitude" + "},";
		}
		if (null != rest.getLandmark()) {
			params.put("landmark", rest.getLandmark());
			query = query + "r.landmark={" + "landmark" + "},";
		}
		if (null != rest.getAddressLine2()) {
			params.put(Constants.ADDRESS_LINE_2, rest.getAddressLine2());
			query = query + "r.address_line_2={" + Constants.ADDRESS_LINE_2
					+ "},";
		}
		if (null != rest.getUpdatedBy()) {
			params.put(Constants.UPDATED_BY, rest.getUpdatedBy());
			query = query + "r.updated_by={" + Constants.UPDATED_BY + "}";
		}
		if (null != rest.getUpdatedDate()) {
			params.put(Constants.UPDATED_DT, rest.getUpdatedDate().getTime());
			query = query + ", r.updated_dt={" + Constants.UPDATED_DT + "}";
		}
		query = query + " Return r";
		Iterator<Map<String, Object>> results = template.query(
				query.toString(), params).iterator();
		Restaurant newRest = null;
		if (results.hasNext()) {
			Map<String, Object> map = results.next();
			newRest = template.convert(map.get("r"), Restaurant.class);

		}

		return newRest;
	}

	protected StringBuilder getReturnQuery(Map<String, Object> params, StringBuilder query) {

		for (java.util.Map.Entry<String, Object> entry : params.entrySet()) {
			if (!entry.getKey().toUpperCase().equalsIgnoreCase("CLASS")) {
				if (null != entry.getValue())
					switch (entry.getKey()) {
					case Constants.TAG_LINE:
						query.append("t." + getPropertyName("tagLine") + "=\"" + entry.getValue() + "\"" + ",");
						break;
					case Constants.COST_FOR_2:
						query.append("t." + getPropertyName("costFor2") + "=toFloat(" + entry.getValue() + "),");
						break;
					case Constants.CURRENCY:
						query.append("t." + getPropertyName("currency") + "='" + entry.getValue() + "'" + ",");
						break;
					case Constants.LANGUAGE:
						query.append("t." + getPropertyName("languageCode") + "='" + entry.getValue() + "'" + ",");
						break;
					case Constants.LEGAL_NAME:
						query.append("t." + getPropertyName("legalName")
								+ "=\"" + entry.getValue() + "\"" + ",");
						break;
					case Constants.TIME_ZONE:
						query.append("t." + getPropertyName("timezone") + "='" + entry.getValue() + "'" + ",");
						break;
					case Constants.PREFER_DATE_FORMAT:
						query.append("t." + getPropertyName("preferredDateFormat") + "='" + entry.getValue() + "'" + ",");
						break;
					case Constants.PREFER_TIME_UNIT:
						query.append("t." + getPropertyName("preferredTimeFormat") + "='" + entry.getValue() + "'" + ",");
						break;
					case Constants.TEMPRATURE_SCALE:
						query.append("t." + getPropertyName("tempratureScale") + "='" + entry.getValue() + "'" + ",");
						break;
					case Constants.LANGUAGE_CODE:
						query.append("t." + getPropertyName("lang_cd") + "='" + entry.getValue() + "'" + ",");
						break;
					case Constants.DISPLAY_NAME:
						query.append("t." + getPropertyName("displayName") + "=\"" + entry.getValue() + "\"" + ",");
						break;
					}

			}

		}
		return (new StringBuilder(query.substring(0, query.length() - 1)));
	}

	/**
	 * Method to create relationship of a restaurant with Parent Acccount
	 */
	@Override
	public Long addRestaurantAccount(Restaurant rest, ParentAccount account) {
		Long id = 0L;
		Map<String , Object> params = new HashMap<>();
		params.put(Constants.GUID, account.getGuid());
		params.put(Constants.REST_GUID, rest.getGuid());
		String query = "MATCH (r:Restaurant {guid:{" + Constants.REST_GUID + "}}),(t:ParentAccount) WHERE t.guid={" + Constants.GUID + "} \n";
		query = query + "MERGE (r)-[rel:REST_HAS_PARENT{__type__:'RestaurantHasParent'}]->(t) return rel";

		Logger.debug("account rest query is " + query);
		Result<Map<String, Object>> result = template.query(query, params);
		Logger.debug("account rest query executed,Result is " + result);
		Iterator<Map<String, Object>> itr = result.iterator();
		while (itr.hasNext()) {
			// Set<Entry<String, Object>> entrySet = ((Map<String, Object>)
			// itr.next()).entrySet();
			// / Logger.debug("entry set is "+entrySet);
			for (Map.Entry<String, Object> entry : ((Map<String, Object>) itr.next()).entrySet()) {
				Relationship rel = (Relationship) entry.getValue();
				Logger.debug("account relationship is " + rel.getId());
				id = rel.getId();
			}
		}

		return id;
	}

	@Override
	public HistoricalTat findHistoricalTatForRest(String restGuid) {
		StringBuilder query = new StringBuilder();
		Map<String , Object> params = new HashMap<>();
		params.put(Constants.REST_GUID, restGuid);
		query.append("MATCH (res:" + Constants.RESTAURANT_LABEL + ")-[:" + RelationshipTypes.HISTORICAL_TAT + "]->(hist:HistoricalTat)");

		query.append(" WHERE res.guid={" + Constants.REST_GUID + "}");
		query.append(" return hist");
		Iterator<Map<String, Object>> results = template.query(query.toString(), params).iterator();
		HistoricalTat hist = null;
		if (results.hasNext()) {
			Map<String, Object> map = results.next();
			hist = template.convert(map.get("hist"), HistoricalTat.class);

		}

		return hist;

	}

	@Override
	public boolean addBlackOutHours(BlackOutHours ophr) {
		// TODO Auto-generated method stub

		StringBuffer query = new StringBuffer();

		try {
			// delete all operational hours set before
			query.append("MATCH (a:Restaurant {guid:'" + ophr.getRestGuid() + "'})-[q:`REST_HAS_BLACKOUT_HR`]->(blackOutshift:`BlackOutShift`) DELETE q,blackOutshift");
			Logger.debug("------query is " + query);
			template.query(query.toString(), null);
			Logger.debug("------------Relationship and Black out Operational Hour deleted ! ");

			// Add operational hours
			query = new StringBuffer();
			query.append(" MATCH (a:Restaurant {guid:'" + ophr.getRestGuid() + "'})");
			if (null != ophr.getMonday() && !ophr.getMonday().isEmpty()) {
				setBlackOutOperationalHourQuery(query, ophr.getMonday(), Constants.MONDAY);
			}
			if (null != ophr.getTuesday() && !ophr.getTuesday().isEmpty()) {
				setBlackOutOperationalHourQuery(query, ophr.getTuesday(), Constants.TUESDAY);
			}
			if (null != ophr.getWednesday() && !ophr.getWednesday().isEmpty()) {
				setBlackOutOperationalHourQuery(query, ophr.getWednesday(), Constants.WEDNESDAY);
			}
			if (null != ophr.getThursday() && !ophr.getThursday().isEmpty()) {
				setBlackOutOperationalHourQuery(query, ophr.getThursday(), Constants.THURSDAY);
			}
			if (null != ophr.getFriday() && !ophr.getFriday().isEmpty()) {
				setBlackOutOperationalHourQuery(query, ophr.getFriday(), Constants.FRIDAY);
			}
			if (null != ophr.getSaturday() && !ophr.getSaturday().isEmpty()) {
				setBlackOutOperationalHourQuery(query, ophr.getSaturday(), Constants.SATURDAY);
			}
			if (null != ophr.getSunday() && !ophr.getSunday().isEmpty()) {
				setBlackOutOperationalHourQuery(query, ophr.getSunday(), Constants.SUNDAY);
			}

			query.append(" RETURN a");
			Logger.info("---------Query----------" + query.toString());
			template.query(query.toString(), null);

		} catch (Exception ex) {
			Logger.debug("Exception is ---------------------" + ex.getLocalizedMessage());
			return false;
		}

		return true;

	}

	private String setBlackOutOperationalHourQuery(StringBuffer query, List<BlackOutShift> shiftList, String day) {

		for (BlackOutShift shift : shiftList) {
			query.append(" CREATE (a)-[:`REST_HAS_BLACKOUT_HR` {day:'" + day + "'}]->(:`BlackOutShift` {");
			
			query.append(" created_dt:" + shift.getCreatedDate().getTime() + " ,");
			query.append(" updated_dt:" + shift.getUpdatedDate().getTime() + " ,");
			
			query.append(" endTimeInMillis:'" + shift.getEndTimeInMillis() + "' ,");
			query.append(" startTimeInMillis:'" + shift.getStartTimeInMillis() + "' }) \n");

		}

		return query.toString();
	}

	@Override
	public BlackOutHours getBlackOutHours(Map<String, Object> params) {
		// TODO Auto-generated method stub

		StringBuffer query = new StringBuffer();
		BlackOutHours op_hr = new BlackOutHours();
		List<BlackOutShift> monday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> tuesday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> wednesday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> thursday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> friday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> saturday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> sunday = new ArrayList<BlackOutShift>();

		query.append("MATCH (a:Restaurant {guid:'" + params.get(Constants.REST_GUID).toString() + "'})-[rel:`REST_HAS_BLACKOUT_HR`]->(blackOutshift:`BlackOutShift`) ");
		if (null != params.get(Constants.DAY_NAME)) {
			StringTokenizer st = new StringTokenizer(params.get(Constants.DAY_NAME).toString(), ",");
			String day_name = "";
			while (st.hasMoreTokens()) {
				day_name = day_name + "'" + st.nextToken() + "',";
			}
			day_name = day_name.substring(0, (day_name.length() - 1));
			query.append(" WHERE rel.day IN [" + day_name + "] ");
		}
		query.append("Return blackOutshift,rel");
		System.out.println(" Query----" + query);
		Result<Map<String, Object>> results = template.query(query.toString(), null);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			Relationship relation = (Relationship) map.get("rel");
			Node node = (Node) map.get("blackOutshift");
			op_hr.setRestGuid(params.get(Constants.REST_GUID).toString());
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.MONDAY)) {
				monday.add(getBlackOutShiftObj(node, relation));
				op_hr.setMonday(monday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.TUESDAY)) {
				tuesday.add(getBlackOutShiftObj(node, relation));
				op_hr.setTuesday(tuesday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.WEDNESDAY)) {
				wednesday.add(getBlackOutShiftObj(node, relation));
				op_hr.setWednesday(wednesday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.THURSDAY)) {
				thursday.add(getBlackOutShiftObj(node, relation));
				op_hr.setThursday(thursday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.FRIDAY)) {
				friday.add(getBlackOutShiftObj(node, relation));
				op_hr.setFriday(friday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.SATURDAY)) {
				saturday.add(getBlackOutShiftObj(node, relation));
				op_hr.setSaturday(saturday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.SUNDAY)) {
				sunday.add(getBlackOutShiftObj(node, relation));
				op_hr.setSunday(sunday);
			}
		}

		return op_hr;
	}

	private BlackOutShift getBlackOutShiftObj(Node node, Relationship relation) {

		BlackOutShift shift = new BlackOutShift();
		shift.setDay(relation.getProperty(Constants.DAY_NAME).toString());
		System.out.println(" Get Day ---------" + shift.getDay());
		shift.setStartTimeInMillis(Long.parseLong(node.getProperty("startTimeInMillis").toString()));
		shift.setEndTimeInMillis(Long.parseLong(node.getProperty("endTimeInMillis").toString()));
		
		shift.setCreatedDate(null);
		shift.setStatus(null);
		
		int mins = (int) (shift.getStartTimeInMillis() / 1000 / 60);
		shift.setStartTime(getTimeInHHMM(mins));

		mins = (int) (shift.getEndTimeInMillis() / 1000 / 60);
		shift.setEndTime(getTimeInHHMM(mins));

		return shift;
	}
	
	
	@Override
	public CustomBlackOutHours getCustomBlackOutHours(Map<String, Object> params) {
		// TODO Auto-generated method stub

		StringBuffer query = new StringBuffer();
		CustomBlackOutHours bl_hr = new CustomBlackOutHours();
		List<CustomBlackOutHours.CustomBlackOutShift> monday = new ArrayList<CustomBlackOutHours.CustomBlackOutShift>();
		List<CustomBlackOutHours.CustomBlackOutShift> tuesday = new ArrayList<CustomBlackOutHours.CustomBlackOutShift>();
		List<CustomBlackOutHours.CustomBlackOutShift> wednesday = new ArrayList<CustomBlackOutHours.CustomBlackOutShift>();
		List<CustomBlackOutHours.CustomBlackOutShift> thursday = new ArrayList<CustomBlackOutHours.CustomBlackOutShift>();
		List<CustomBlackOutHours.CustomBlackOutShift> friday = new ArrayList<CustomBlackOutHours.CustomBlackOutShift>();
		List<CustomBlackOutHours.CustomBlackOutShift> saturday = new ArrayList<CustomBlackOutHours.CustomBlackOutShift>();
		List<CustomBlackOutHours.CustomBlackOutShift> sunday = new ArrayList<CustomBlackOutHours.CustomBlackOutShift>();

		query.append("MATCH (a:Restaurant {guid:'" + params.get(Constants.REST_GUID).toString() + "'})-[rel:`REST_HAS_BLACKOUT_HR`]->(blackOutshift:`BlackOutShift`) ");
		if (null != params.get(Constants.DAY_NAME)) {
			StringTokenizer st = new StringTokenizer(params.get(Constants.DAY_NAME).toString(), ",");
			String day_name = "";
			while (st.hasMoreTokens()) {
				day_name = day_name + "'" + st.nextToken() + "',";
			}
			day_name = day_name.substring(0, (day_name.length() - 1));
			query.append(" WHERE rel.day IN [" + day_name + "] ");
		}
		if(params.containsKey(Constants.UPDATED_AFTER)){
			query.append(" WHERE toInt(a.updated_dt)>toInt({"+Constants.UPDATED_AFTER+"}) OR toInt(blackOutshift.updated_dt)>toInt({"+Constants.UPDATED_AFTER+"})");					
		}
		
		query.append("Return blackOutshift.startTimeInMillis as startTime, blackOutshift.endTimeInMillis as endTime ,rel");
		System.out.println(" Query----" + query);
		Result<Map<String, Object>> results = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			Relationship relation = (Relationship) map.get("rel");
			//Node node = (Node) map.get("blackOutshift");
			String startTime = (String) map.get("startTime");
			String endTime = (String) map.get("endTime");
			
			//op_hr.setRestGuid(params.get(Constants.REST_GUID).toString());
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.MONDAY)) {
				monday.add(getCustomBlackOutShiftObj(startTime, endTime, relation));
				bl_hr.setMonday(monday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.TUESDAY)) {
				tuesday.add(getCustomBlackOutShiftObj(startTime, endTime, relation));
				bl_hr.setTuesday(tuesday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.WEDNESDAY)) {
				wednesday.add(getCustomBlackOutShiftObj(startTime, endTime, relation));
				bl_hr.setWednesday(wednesday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.THURSDAY)) {
				thursday.add(getCustomBlackOutShiftObj(startTime, endTime, relation));
				bl_hr.setThursday(thursday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.FRIDAY)) {
				friday.add(getCustomBlackOutShiftObj(startTime, endTime, relation));
				bl_hr.setFriday(friday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.SATURDAY)) {
				saturday.add(getCustomBlackOutShiftObj(startTime, endTime, relation));
				bl_hr.setSaturday(saturday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.SUNDAY)) {
				sunday.add(getCustomBlackOutShiftObj(startTime, endTime, relation));
				bl_hr.setSunday(sunday);
			}
		}

		return bl_hr;
	}

	private CustomBlackOutHours.CustomBlackOutShift getCustomBlackOutShiftObj(String startTime, String endTime, Relationship relation) {

		CustomBlackOutHours.CustomBlackOutShift shift = new CustomBlackOutHours().new CustomBlackOutShift();
		
		shift.setStartTimeInMillis(Long.parseLong(startTime));
		shift.setEndTimeInMillis(Long.parseLong(endTime));
		
		return shift;
	}
	

	@Override
	public void addHistoricalTatData(RestSystemConfigModel rest) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deleteRestaurantData(String restGuid) {

		boolean deleted = false;
		queueDao.deleteAllQueueReservation(restGuid);
		deleteGuestAndTableRelationship(restGuid);
		deleteReservationHistoryData(restGuid);
		deleteReservationData(restGuid);
		deleteCalenderEventData(restGuid);
		deleteEventData(restGuid);
		// deleteEvent(restGuid);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.REST_GUID, restGuid);
		List<String> guestForRest = guestDao.getGuestExceptDummyGuest(GuestProfile.class, params);
		Logger.debug("Guest except dummy guest are--------" + guestForRest);
		List<GuestProfile> guestForOtherRests = guestDao.getGuestsForOtherRest(GuestProfile.class, guestForRest, restGuid);
		Logger.debug("Guests for other rest are--------" + guestForOtherRests);
		List<String> guestGuidsForOtherRest = getGuids(guestForOtherRests);
		Logger.debug("Guest guids for other rest are--------" + guestGuidsForOtherRest);
		deleteGuestRelationships(restGuid, guestForRest);
		List<String> guestToDelete = new ArrayList<>();
		for (String guid : guestForRest) {
			if (!guestGuidsForOtherRest.contains(guid)) {
				guestToDelete.add(guid);
			}
		}

		Logger.debug("Guest guids to delete are--------" + guestToDelete);

		deleteGuest(guestToDelete);

		deleted = true;

		return deleted;

	}

	private void deleteGuestAndTableRelationship(String restGuid) {
		Map<String , Object> params = new HashMap<>();
		params.put(Constants.REST_GUID, restGuid);
		StringBuilder query = new StringBuilder("MATCH (t:Table)-[thr:TBL_HAS_RESV]->(n:`Reservation`{rest_guid:{" + Constants.REST_GUID + "}})" + "<-[ghr:GUEST_HAS_RESV]-(g:GuestProfile) DELETE thr,ghr");
		Logger.debug("query is " + query);
		Result<Map<String, Object>> r = template.query(query.toString(), params);
		Logger.debug("query executed,Result is " + r);

	}

	private void deleteReservationHistoryData(String restGuid) {

		Map<String , Object> params = new HashMap<>();
		params.put(Constants.REST_GUID, restGuid);

		StringBuilder query = new StringBuilder("MATCH (n:`Reservation`{rest_guid:{" + Constants.REST_GUID + "}})-[rh:RESV_HISTORY]->(h) DELETE rh,h");
		Logger.debug("query is " + query);
		Result<Map<String, Object>> r = template.query(query.toString(), params);
		Logger.debug("query executed,Result is " + r);

	}

	private void deleteReservationData(String restGuid) {

		Map<String , Object> params = new HashMap<>();
		params.put(Constants.REST_GUID, restGuid);

		StringBuilder query = new StringBuilder("MATCH (n:`Reservation`{rest_guid:{" + Constants.REST_GUID + "}}) \n OPTIONAL MATCH (n)-[r]-() DELETE r,n");
		Logger.debug("query is " + query);
		Result<Map<String, Object>> r = template.query(query.toString(), params);
		Logger.debug("query executed,Result is " + r);

	}

	private void deleteCalenderEventData(String restGuid) {

		Map<String , Object> params = new HashMap<>();
		params.put(Constants.REST_GUID, restGuid);

		StringBuilder query = new StringBuilder("MATCH (rest:Restaurant{guid:{" + Constants.REST_GUID
				+ "}})-[rhc:REST_HAS_CAL]->(ce:CalenderEvent) WHERE ce.category<>'HOLIDAY' \n OPTIONAL MATCH ()-[r]-(ce)  DELETE r,rhc,ce");
		Logger.debug("query is " + query);
		Result<Map<String, Object>> r = template.query(query.toString(), params);
		Logger.debug("query executed,Result is " + r);

	}

	private void deleteEventData(String restGuid) {

		Map<String , Object> params = new HashMap<>();
		params.put(Constants.REST_GUID, restGuid);

		StringBuilder query = new StringBuilder("MATCH (e:Event{rest_guid:{" + Constants.REST_GUID + "}})-[r]-() WHERE e.category<>'HOLIDAY' DELETE r,e");
		Logger.debug("query is " + query);
		Result<Map<String, Object>> r = template.query(query.toString(), params);
		Logger.debug("query executed,Result is " + r);

	}

	/*
	 * private void deleteEvent(String restGuid) {
	 * 
	 * StringBuilder query = new
	 * StringBuilder("MATCH (e:Event{rest_guid:'"+restGuid
	 * +"'})-[r]-() WHERE e.category<>'HOLIDAY' DELETE r,e");
	 * Logger.debug("query is " + query); Result<Map<String, Object>> r =
	 * template.query(query.toString(), null);
	 * Logger.debug("query executed,Result is " + r);
	 * 
	 * }
	 */

	private void deleteGuestRelationships(String restGuid, List<String> guestGuids) {
		String guestGuidString = UtilityMethods.cypherCompatibleString(guestGuids);

		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:Restaurant)-[hg:" + RelationshipTypes.HAS_GUEST + "]->(g:GuestProfile) WHERE r.guid<>'" + restGuid + "' AND g.guid IN [" + guestGuidString + "]");
		query.append(" DELETE hg");
		Logger.debug("query is " + query);
		Result<Map<String, Object>> r = template.query(query.toString(), null);
		Logger.debug("query executed,Result is " + r);

	}

	private void deleteGuest(List<String> guests) {
		String guestGuidString = UtilityMethods.cypherCompatibleString(guests);

		StringBuilder query = new StringBuilder();
		query.append("MATCH (g:GuestProfile)-[r]-() WHERE  g.guid IN [" + guestGuidString + "]");
		query.append(" DELETE r,g");
		Logger.debug("query is " + query);
		Result<Map<String, Object>> r = template.query(query.toString(), null);
		Logger.debug("query executed,Result is " + r);

	}

	@Override
	public Restaurant updateContactInfoAdmin(RestaurantContactInfoAdmin rest) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.REST_GUID, rest.getGuid());
		String prequery = "MATCH (r:Restaurant {guid:{" + Constants.REST_GUID + "}}) SET ";
		String midquery = "", query = "";
		if (null != rest.getName()) {
			params.put(Constants.NAME, rest.getName());
			midquery = midquery + ",r.name={" + Constants.NAME + "}";
		}
		if (null != rest.getAddressLine1()) {
			params.put(Constants.ADDRESS_LINE_1, rest.getAddressLine1());
			midquery = midquery + ",r.address_line_1={" + Constants.ADDRESS_LINE_1 + "}";
		}
		if (null != rest.getCountryCode()) {
			params.put(Constants.COUNTRY_CODE, rest.getCountryCode());
			midquery = midquery + ",r.country_cd={" + Constants.COUNTRY_CODE + "}";
		}
		if (null != rest.getCity()) {
			params.put(Constants.CITY, rest.getCity());
			midquery = midquery + ",r.city={" + Constants.CITY + "}";
		}
		if (null != rest.getState()) {
			params.put("state", rest.getState());
			midquery = midquery + ",r.state={" + "state" + "}";
		}
		if (null != rest.getZipcode()) {
			params.put("zipcode", rest.getZipcode());
			midquery = midquery + ",r.zipcode={" + "zipcode" + "}";
		}
		if (null != rest.getPhoneNo1()) {
			params.put("phoneNo1", rest.getPhoneNo1());
			midquery = midquery + ",r.phone_no_1={" + "phoneNo1" + "}";
		}
		if (null != rest.getPhoneNo2()) {
			params.put("phoneNo2", rest.getPhoneNo2());
			midquery = midquery + ",r.phone_no_2={" + "phoneNo2" + "}";
		}
		if (null != rest.getWebsite()) {
			params.put("website", rest.getWebsite());
			midquery = midquery + ",r.website={" + "website" + "}";
		}
		if (null != rest.getEmail()) {
			params.put(Constants.EMAIL, rest.getEmail());
			midquery = midquery + ",r.email={" + Constants.EMAIL + "}";
		}
		if (null != rest.getLatitude()) {
			params.put("latitude", rest.getLatitude());
			midquery = midquery + ",r.latitude={" + "latitude" + "}";
		}
		if (null != rest.getLongitude()) {
			params.put("longitude", rest.getLongitude());
			midquery = midquery + ",r.longitude={" + "longitude" + "}";
		}
		if (null != rest.getLandmark()) {
			params.put("landmark", rest.getLandmark());
			midquery = midquery + ",r.landmark={" + "landmark" + "}";
		}
		if (null != rest.getAddressLine2()) {
			params.put(Constants.ADDRESS_LINE_2, rest.getAddressLine2());
			midquery = midquery + ",r.address_line_2={" + Constants.ADDRESS_LINE_2 + "}";
		}
		if (rest.getRegion() != null) {
			params.put(Constants.REGION, rest.getRegion());
			midquery = midquery + ",r.region={" + Constants.REGION + "}";
		}
		if (rest.getLocality() != null) {
			params.put("locality", rest.getLocality());
			midquery = midquery + ",r.locality={" + "locality" + "}";
		}
		if (rest.getBuilding() != null) {
			params.put("building", rest.getBuilding());
			midquery = midquery + ",r.building={" + "building" + "}";
		}
		if (null != rest.getUpdatedBy()) {
			params.put(Constants.UPDATED_BY, rest.getUpdatedBy());
			midquery = midquery + ",r.updated_by={" + Constants.UPDATED_BY + "}";
		}
		if (null != rest.getUpdatedDate()) {
			params.put(Constants.UPDATED_DT, rest.getUpdatedDate().getTime());
			midquery = midquery + ",r.updated_dt={" + Constants.UPDATED_DT + "}";
		}
		if(midquery.startsWith(","))
		midquery = midquery.toString().substring(1);
		query = prequery + midquery + " Return r";
		Iterator<Map<String, Object>> results = template.query(query.toString(), params).iterator();
		Restaurant newRest = null;
		if (results.hasNext()) {
			Map<String, Object> map = results.next();
			newRest = template.convert(map.get("r"), Restaurant.class);

		}

		return newRest;
	}

	@Override
	public void setInactiveAllActiveStaff(String guid) {

		StringBuilder query = new StringBuilder();
		query.append("MATCH (res:Restaurant)-[r:REST_HAS_USER]->(s:Staff) WHERE  res.guid='" + guid + "' AND s.status='"+Constants.ACTIVE_STATUS+"'");
		query.append(" SET r.isActive=false,s.status='"+Constants.INACTIVE_STATUS+"'");
		Logger.debug("query is " + query);
		Result<Map<String, Object>> r = template.query(query.toString(), null);

	}



	@Override
	public List<Reservation> getReservationsForShifts(List<Map<String, Long>> shifts, String restGuid)
	{
		List<Reservation> reservationList = new ArrayList<>();
		Long estStartTime = 0l;
		Long estEndTime = 0l;
		
		
		

		
		
		
		Map<String,Object> params = new HashMap<>();
		params.put("restaurantGuid", restGuid);

		//String query = " MATCH (resv:`Reservation`)<-[guestRel:`GUEST_HAS_RESV`]-(guest:`GuestProfile`)<-[hg:`HAS_GUEST`]-(rest:`Restaurant`{guid : {restaurantGuid}})"
				String query = " MATCH (rest:`Restaurant`{guid : {restaurantGuid}})-[hg:`HAS_GUEST`]->(guest:`GuestProfile`)-[guestRel:`GUEST_HAS_RESV`]->(resv:`Reservation`)"
				+ "  WHERE resv.reservation_status IN ['CREATED','CONFIRMED','ARRIVED','SEATED','CALLED','MSG_SENT'] AND (";
		
				
				
				
		for(Map<String,Long> shift : shifts)
		{
			estStartTime = shift.get(Constants.EST_START_TIME);
			estEndTime = shift.get(Constants.EST_END_TIME);
			query = query + " ( toInt(resv.est_start_time) >= toInt("+estStartTime+") AND toInt(resv.est_end_time) <= toInt("+estEndTime+")) OR";
		}

		if(query.toString().contains("OR"))
		{
			query = query.substring(0, query.length() - 3);
			query = query + ")";
		}
		else
		{
			query = query.substring(0,query.length() - 5);
		}

		query = query + " RETURN DISTINCT resv,guest, hg ";

		Logger.debug("Query------------------------------------------------------" + query.toString());
		Iterator<Map<String, Object>> i = template.query(query.toString(), params).iterator();
		Reservation reservation = null;

		Map<String,Object> map ;

		while (i.hasNext()) 
		{
			map = i.next();

			reservation = template.convert(map.get("resv"), Reservation.class);
			reservation.setBookedBy(null);
			reservation.setBookedById(null);
			reservation.setConversation(null);
			reservation.setCreatedBy(null);
			reservation.setHistory(null);
			reservation.setLanguageCode(null);
			reservation.setUpdatedBy(null);
			reservation.setUpdatedDate(null);
			reservation.setServerGuids(null);
			reservation.setServerNames(null);
			//reservation.setBarCount(barCount);

			GuestProfile guest = template.convert(map.get("guest"), GuestProfile.class);

			reservation.setGuestGuid(guest.getGuid());
			reservation.setGuest_mobile(guest.getMobile());


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

			reservationList.add(reservation);

		}
		return reservationList;
	}
	
	
	public BlackOutHours getShiftsAndHolidays(Map<String, Object> params) {
		// TODO Auto-generated method stub

		StringBuffer query = new StringBuffer();
		BlackOutHours op_hr = new BlackOutHours();
		List<BlackOutShift> monday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> tuesday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> wednesday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> thursday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> friday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> saturday = new ArrayList<BlackOutShift>();
		List<BlackOutShift> sunday = new ArrayList<BlackOutShift>();

		query.append("MATCH (a:Restaurant {guid:'" + params.get(Constants.REST_GUID).toString() + "'})-[rel:`REST_HAS_BLACKOUT_HR`]->(blackOutshift:`BlackOutShift`) ");
		query.append(",(a)-[rel:`REST_HAS_OPHR`]->(shift:`Shift`)");
		 
		if (null != params.get(Constants.DAY_NAME)) {
			StringTokenizer st = new StringTokenizer(params.get(Constants.DAY_NAME).toString(), ",");
			String day_name = "";
			while (st.hasMoreTokens()) {
				day_name = day_name + "'" + st.nextToken() + "',";
			}
			day_name = day_name.substring(0, (day_name.length() - 1));
			query.append(" WHERE rel.day IN [" + day_name + "] ");
		}
		query.append("Return blackOutshift,rel");
		System.out.println(" Query----" + query);
		Result<Map<String, Object>> results = template.query(query.toString(), null);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			Relationship relation = (Relationship) map.get("rel");
			Node node = (Node) map.get("blackOutshift");
			op_hr.setRestGuid(params.get(Constants.REST_GUID).toString());
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.MONDAY)) {
				monday.add(getBlackOutShiftObj(node, relation));
				op_hr.setMonday(monday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.TUESDAY)) {
				tuesday.add(getBlackOutShiftObj(node, relation));
				op_hr.setTuesday(tuesday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.WEDNESDAY)) {
				wednesday.add(getBlackOutShiftObj(node, relation));
				op_hr.setWednesday(wednesday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.THURSDAY)) {
				thursday.add(getBlackOutShiftObj(node, relation));
				op_hr.setThursday(thursday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.FRIDAY)) {
				friday.add(getBlackOutShiftObj(node, relation));
				op_hr.setFriday(friday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.SATURDAY)) {
				saturday.add(getBlackOutShiftObj(node, relation));
				op_hr.setSaturday(saturday);
			}
			if (relation.getProperty(Constants.DAY_NAME).equals(Constants.SUNDAY)) {
				sunday.add(getBlackOutShiftObj(node, relation));
				op_hr.setSunday(sunday);
			}
		}

		return op_hr;
	}
	

}
