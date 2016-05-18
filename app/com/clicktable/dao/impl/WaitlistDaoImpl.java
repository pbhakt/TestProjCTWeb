package com.clicktable.dao.impl;

import com.clicktable.dao.intf.WaitlistDao;
import com.clicktable.model.Reservation;
import com.clicktable.model.Table;
import com.clicktable.util.Constants;
import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import play.Logger;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author p.singh
 */
@Service
public class WaitlistDaoImpl implements WaitlistDao {

	@Autowired
	Neo4jTemplate template;

	public static final Logger.ALogger log = Logger.of(WaitlistDaoImpl.class);

	@Override
	public Map<String, List<Reservation>> getReservations(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		query.append(" MATCH (table:`Table`");
		if (params.containsKey(Constants.TABLE_GUID)) {
			query.append("{guid:{tableGuid}}");
		}

		query.append(")-[rel:`TBL_HAS_RESV`" + "]->(resv:`Reservation`)" + "  WHERE table.rest_id={restID}"
				+ "  AND resv.reservation_status<>'FINISHED' AND resv.reservation_status<>'CANCELLED' AND resv.reservation_status<>'NO_SHOW' "
				+ "  AND ( (HAS (resv.act_start_time) AND NOT HAS (resv.act_end_time )) "
				+ "  OR  ( toInt(resv.est_start_time)>=toInt({currentTime}))"
				+ "  OR  ( toInt(resv.est_start_time)<=toInt({currentTime}) AND  toInt(resv.est_end_time)>=toInt({currentTime})) )"
				+ "  AND ( toInt(resv.est_end_time)<=toInt({nextDayShiftStartTime}))  "
				+ "  AND ( toInt(resv.est_start_time)>=toInt({currentDayShiftStartTime}))  AND");

		query = new StringBuilder(query.substring(0, query.length() - 3));

		query.append(" RETURN DISTINCT resv");

		log.debug("Query-------------------" + query.toString());
		Result<Map<String, Object>> result = template.query(query.toString(), params);

		Iterator<Map<String, Object>> i = result.iterator();
		Reservation reservation = null;

		Map<String, List<Reservation>> reservationListMap = new HashMap<String, List<Reservation>>();

		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			reservation = template.convert(map.get("resv"), Reservation.class);
			reservation.setBlockResv("RESERVATION");
			for (String t : reservation.getTableGuid()) {
				List<Reservation> reservationList = new ArrayList<>();
				if (reservationListMap.containsKey(t)) {
					reservationList = reservationListMap.get(t);
				}
				reservationList.add(reservation);
				reservationListMap.put(t, reservationList);
			}
		}

		return reservationListMap;
	}

	@Override
	public List<Object> getAllTables(String restaurantId) {
		StringBuilder query = new StringBuilder();
		Map<String, Object> params = new HashMap<>();
		params.put(Constants.REST_ID, restaurantId);
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		query.append("MATCH (r:Restaurant{guid:{restId}})-[rht:REST_HAS_TBL]->(t:`Table`) WHERE t.status={status} ");
		query.append("RETURN t ORDER BY t.max_covers, t.min_covers,t.name");

		Result<Map<String, Object>> result = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = result.iterator();

		Table table = null;
		Map<Integer, List<Table>> tableWithCovers = new HashMap<>();
		Map<String, Table> tableWithGuid = new HashMap<>();
		List<Table> allTablesList = new ArrayList<>();

		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				table = template.convert(entry.getValue(), Table.class);
				table.setTableStatus(Constants.AVAILABLE);
				tableWithGuid.put(table.getGuid(), table);
				allTablesList.add(table);

				for (int j = table.getMinCovers(); j <= table.getMaxCovers(); j++) {
					List<Table> tableList = new ArrayList<>();
					if (tableWithCovers.containsKey(j)) {
						tableList = tableWithCovers.get(j);
					}
					tableList.add(table);
					tableWithCovers.put(j, tableList);
				}

			}
		}
		List<Object> tableList = new ArrayList<>();
		tableList.add(tableWithCovers);
		tableList.add(tableWithGuid);
		tableList.add(allTablesList);
		return tableList;
	}

	@Override
	public Map<Integer, Long> getActualTat(Map<String, Object> params) {
		String query = " MATCH (a:Restaurant{guid:{" + Constants.REST_GUID + "}})-[r:`REST_HAS_TAT`]->(b:Tat{day:{"
				+ Constants.DAY_NAME + "}}) RETURN r.value,b.max_covers,b.min_covers";
		log.debug("match query is ------------------" + query);
		Result<Map<String, Object>> r = template.query(query, params);
		Iterator<Map<String, Object>> itr = r.iterator();
		Integer value = 0, minCovers = 0, maxCovers = 0;
		Map<Integer, Long> tatMap = new HashMap<>();
		while (itr.hasNext()) {
			Set<Entry<String, Object>> entrySet = itr.next().entrySet();
			log.debug("entry set is " + entrySet);

			for (Map.Entry<String, Object> entry : entrySet) {
				if (entry.getKey().contains(Constants.VALUE)) {
					value = (Integer) entry.getValue();
				}

				if (entry.getKey().contains("min_covers")) {
					minCovers = (Integer) entry.getValue();
				}

				if (entry.getKey().contains("max_covers")) {
					maxCovers = (Integer) entry.getValue();
				}

			}

			if (minCovers == null && maxCovers == 8) {
				tatMap.put(maxCovers + 1, value * 60 * 1000L);
				continue;
			}

			if (minCovers == 9 && maxCovers == 10) {
				tatMap.put(minCovers, value * 60 * 1000L);
				continue;
			}
			tatMap.put(minCovers, value * 60 * 1000L);
			tatMap.put(maxCovers, value * 60 * 1000L);

		}

		return tatMap;
	}

	@Override
	public List<Table> getAllTablesList(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		query.append("MATCH (r:Restaurant{guid:{restId}})-[rht:REST_HAS_TBL]->(t:`Table`) WHERE t.status={status} ");

		query.append("RETURN t ORDER BY t.max_covers, t.min_covers,t.name");
		Result<Map<String, Object>> result = template.query(query.toString(), params);
		Iterator<Map<String, Object>> i = result.iterator();
		Table table = null;
		List<Table> tables = new ArrayList<Table>();
		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				template.postEntityCreation((Node) entry.getValue(), Table.class);
				table = template.convert(entry.getValue(), Table.class);
				table.setTableStatus(Constants.AVAILABLE);
				tables.add(table);
			}
		}
		return tables;
	}

	@Override
	public Map<String, List<Reservation>> getReservationsExceptProvidedOne(long currentTime,
			long currentDayShiftStartTime, long nextDayShiftStartTime, String restID, Map<String, Object> params,
			String resvGuid) {
		//List<Reservation> reservationList = new ArrayList<>();
		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("currentTime", currentTime);
		queryParams.put("currentDayShiftStartTime", currentDayShiftStartTime);
		queryParams.put("nextDayShiftStartTime", nextDayShiftStartTime);
		queryParams.put("restID", restID);
		queryParams.put("guid", resvGuid);
		StringBuilder query = new StringBuilder();
		query.append(" MATCH (table:`Table`)-[rel:`TBL_HAS_RESV`" + "]->(resv:`Reservation`)"
				+ "  WHERE table.rest_id={restID} AND resv.rest_guid={restID} AND resv.guid<>{guid} "
				+ "  AND resv.reservation_status<>'FINISHED' AND resv.reservation_status<>'CANCELLED' AND resv.reservation_status<>'NO_SHOW' "
				+ "  AND ( (HAS (resv.act_start_time) AND NOT HAS (resv.act_end_time )) "
				+ "  OR  ( toInt(resv.est_start_time)>=toInt({currentTime}))"
				+ "  OR  ( toInt(resv.est_start_time)<=toInt({currentTime}) AND  toInt(resv.est_end_time)>=toInt({currentTime})) )"
				+ "  AND ( toInt(resv.est_end_time)<=toInt({nextDayShiftStartTime}))  "
				+ "  AND ( toInt(resv.est_start_time)>=toInt({currentDayShiftStartTime}))  AND");
			
			
			/*if(null!=params && params.containsKey(Constants.COVERS))
			{
				query.append( " toInt(table.min_covers)<= "+ params.get(Constants.COVERS)+" AND");
				query.append(" toInt(table.max_covers)>= "+  params.get(Constants.COVERS)+" AND");
			}*/
		query = new StringBuilder(query.substring(0, query.length() - 3));
		query.append(" RETURN DISTINCT resv ");

		log.debug("Query------------------------------------------------------" + query.toString());
		Result<Map<String, Object>> result = template.query(query.toString(), queryParams);
		Iterator<Map<String, Object>> i = result.iterator();
		Reservation reservation = null;

		Map<String, List<Reservation>> reservationListMap = new HashMap<String, List<Reservation>>();

		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				template.postEntityCreation((Node) entry.getValue(), Reservation.class);
				reservation = template.convert(entry.getValue(), Reservation.class);
				for (String t : reservation.getTableGuid()) {
					List<Reservation> reservationList = new ArrayList<>();
					if (reservationListMap.containsKey(t)) {
						reservationList = reservationListMap.get(t);
					}
					reservationList.add(reservation);
					reservationListMap.put(t, reservationList);
				}
			}
		}
		return reservationListMap;
	}

}
