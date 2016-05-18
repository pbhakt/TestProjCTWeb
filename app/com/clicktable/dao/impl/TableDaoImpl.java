package com.clicktable.dao.impl;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDateTime;
import org.neo4j.graphdb.Node;
import org.neo4j.rest.graphdb.query.CypherTransactionExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.TableDao;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Section;
import com.clicktable.model.Table;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;
import com.clicktable.util.UtilityMethods;

@Service
public class TableDaoImpl extends GraphDBDao<Table> implements TableDao {

    @Autowired
    ReservationDao reservationDao;

    public TableDaoImpl() {
	super();
	this.setType(Table.class);
    }

    @Override
    public List<Table> findByFields(Class type, Map<String, Object> params) {
    	//String restId="", name=null;
    	
    	/*if (params.containsKey(Constants.REST_ID)){
    		 params.put(Constants.REST_GUID, params.get(Constants.REST_ID));
    	}*/
    	
	StringBuilder query = getMatchClause(params);
	Logger.debug("query after match is " + query);
	query.append(getWhereClause(params));
	Logger.debug("query after where is " + query);
	query.append(getReturnClause(params));
	List<Table> tableList = executeQuery(query.toString(), params);
	Logger.debug("query after execute is======================= " + query);
	
	tableList = getTableStatus(tableList, params);

	return tableList;

    }

    
    @Override
    public List<Table> findAllTables(Class type, Map<String, Object> params) {
    	
    	StringBuilder query = new StringBuilder();
    	query.append("Match (table:`Table`)");
    	if(params.containsKey(Constants.REST_ID)){
    		query.append(" where table.rest_id={"+ Constants.REST_ID
    				+ "}");
    	}
    	query.append(" return table");
	List<Table> tableList = executeQuery(query.toString(), params);
	Logger.debug("query after execute is======================= " + query);
	
	return tableList;
    }
    
    
    private List<Table> getTableStatus(List<Table> tableList,
	    Map<String, Object> params) {

	List<Table> returnTableList = new ArrayList<Table>();
	
	
	// Get blocked tables
	Map<String, Table> blockedTables = getBlockedTables(tableList, params);

	// Get The reserved tables
			params.put(Constants.TABLE_GUID, UtilityMethods
				.cypherCompatibleString(getGuids(tableList)));
		

			Map<String, Table> reservedTables = null;
		    try
		    {
		     reservedTables = getReservedTables(params);
		    }
		    catch(CypherTransactionExecutionException ex)
		    {
		        Logger.debug("Cypher transaction occured---------------------" + ex.getMessage());
		        
		        try {
		            Thread.sleep(100);
		        } catch (InterruptedException e) {
		            // TODO Auto-generated catch block
		            Logger.debug("Interrupted exception occured------------------------" + e.getMessage());
		        }
		        
		        reservedTables = getReservedTables(params);
		    }
	
			// Process the tableList , Also implement the search param tableSatatus

	String status = Constants.AVAILABLE;
	for (Table table : tableList) {
	    status = Constants.AVAILABLE;
	    if (reservedTables.containsKey(table.getGuid())) {
		if (blockedTables.containsKey(table.getGuid())) {
		    status = reservedTables.get(table.getGuid())
			    .getTableStatus() + "," + Constants.BLOCKED;
		    table.setBlockGuid(blockedTables.get(table.getGuid())
			    .getBlockGuid());
		} else {
		    status = reservedTables.get(table.getGuid())
			    .getTableStatus();

		}
		// set the reservation specific values in table here
		table.setReservation_EndTime(reservedTables
			.get(table.getGuid()).getReservation_EndTime());
		table.setReservation_StartTime(reservedTables.get(
			table.getGuid()).getReservation_StartTime());
		table.setReservationGuid(reservedTables.get(table.getGuid())
			.getReservationGuid());
		table.setSeated_time(reservedTables.get(table.getGuid())
			.getSeated_time());

		table.setTat(reservedTables.get(table.getGuid())
					.getTat());

	    } else if (blockedTables.containsKey(table.getGuid())) {
		status = Constants.BLOCKED;
		table.setBlockGuid(blockedTables.get(table.getGuid())
			.getBlockGuid());
	    }

	    if (params.get(Constants.TABLE_STATUS) != null) {
		if (containsStatus(params.get(Constants.TABLE_STATUS)
			.toString(), status)) {
		    table.setTableStatus(status);
		    returnTableList.add(table);
		}
		continue;

	    }
	    table.setTableStatus(status);
	    returnTableList.add(table);
	}

	return returnTableList;
    }

    /**
     * private method to check whether status from params is within status get
     * from tables
     * 
     */
    private Boolean containsStatus(String statusParam, String status) {
	Boolean contains = false;
	Logger.debug("statusparam is " + statusParam + " status is " + status);
	String[] statusParamArr = statusParam.split(",");
	String[] statusArr = status.split(",");
	//Logger.debug("status param arr is " + statusParamArr
	//	+ " status arr is " + statusArr);
	for (int i = 0; i < statusArr.length; i++) {
	    for (int j = 0; j < statusParamArr.length; j++) {
		if (statusArr[i].equals(statusParamArr[j])) {
		    contains = true;

		}
	    }
	}

	return contains;
    }

    /**
     * @param params
     * @return Map<String,Table>
     * 
     *         Return a Map<String,Table>. Search params here can be (within
     *         params map) tableGuid : comma separated table guid time : Time
     *         which should be in between Reservation start and end time.
     *         startTime,endTime : These start time, end time should overlap
     *         with the Reservation start and end time.
     * 
     */

    private Map<String, Table> getReservedTables(Map<String, Object> params) {
	Date time = new Date();
	Logger.debug("params in get reserved tables is " + params);
	if (!params.containsKey(Constants.START_TIME)
		&& !params.containsKey(Constants.END_TIME)
		&& !params.containsKey(Constants.TIME)) {
	    time = new Date();
	    Logger.debug("! params.containsKey(Constants.START_TIME) && ! params.containsKey(Constants.END_TIME) && ! params.containsKey(Constants.TIME)");

	} else if (params.get(Constants.TIME) != null) {
	    Logger.debug("params.get(Constants.TIME)======"
		    + params.get(Constants.TIME));

	    time = (Date) params.get(Constants.TIME);
	}

	StringBuilder query = new StringBuilder();
	query.append("Match (restaurant:Restaurant)-[:REST_HAS_TBL]->(table:`Table`)-[rel:`TBL_HAS_RESV`]->(reservation:`Reservation`) WHERE NOT (reservation.reservation_status IN ['CANCELLED','NO_SHOW','FINISHED']) ");
	/*if (params.containsKey(Constants.TABLE_GUID)) {
	    query.append(" AND table.guid IN [ "
		    + params.get(Constants.TABLE_GUID).toString() + " ] ");
	}*/
	if (params.containsKey(Constants.REST_GUID)) {
	    query.append(" AND restaurant.guid  = '"
		    + params.get(Constants.REST_GUID).toString() + "' ");
	}

	Logger.debug("(! params.containsKey(Constants.START_TIME)) && (! params.containsKey(Constants.END_TIME)) ===== "
		+ ((!params.containsKey(Constants.START_TIME)) && (!params
			.containsKey(Constants.END_TIME))));

	if ((!params.containsKey(Constants.START_TIME))
		&& (!params.containsKey(Constants.END_TIME))) {
	   /* query.append(" AND ( toInt(reservation.est_start_time)<toInt("
		    + time.getTime()
		    + ") OR toInt(reservation.act_start_time)<toInt("
		    + time.getTime() + ") )");
	    query.append(" AND toInt(reservation.est_end_time)>toInt("
		    + time.getTime() + ") ");*/
	    
	    query.append(" AND (( toInt(reservation.est_start_time)<toInt("
		    + time.getTime()
		    + ") AND toInt(reservation.est_end_time)>toInt("
		    + time.getTime() + ") )");
	    query.append(" OR (NOT HAS (reservation.act_end_time ) AND HAS (reservation.act_start_time))) ");		    
		    
//	    query.append(" OR (HAS (reservation.act_start_time) AND NOT HAS (reservation.act_end_time ))");
	}
	if (params.containsKey(Constants.START_TIME)
		&& params.containsKey(Constants.END_TIME)) {
	    query.append(" AND toInt(reservation.est_start_time)<toInt("
		    + ((Date) params.get(Constants.END_TIME)).getTime() + ") ");
	    query.append(" AND toInt(reservation.est_end_time)>toInt("
		    + ((Date) params.get(Constants.START_TIME)).getTime()
		    + ") ");
	    
	    query.append(" OR (toInt(reservation.est_start_time) > toInt("+((Date) params.get(Constants.START_TIME)).getTime()+") AND toInt(reservation.est_end_time)=toInt("+((Date) params.get(Constants.END_TIME)).getTime()+"))");
	    
	    query.append(" OR (toInt(reservation.est_start_time) = toInt("+((Date) params.get(Constants.START_TIME)).getTime()+") AND toInt(reservation.est_end_time)<toInt("+((Date) params.get(Constants.END_TIME)).getTime()+"))");
	    //query.append(" OR (HAS (reservation.act_start_time) AND NOT HAS (reservation.act_end_time )) ");
	    query.append(" OR (toInt(reservation.est_start_time)=toInt("
		    + ((Date) params.get(Constants.END_TIME)).getTime() + ")"
		    		+ "AND toInt(reservation.est_end_time)=toInt("
		    + ((Date) params.get(Constants.START_TIME)).getTime()
		    + ") )");
	    
	    
	}

	query.append("RETURN DISTINCT table,reservation ");
	Logger.debug("query is================================================"
		+ query);
	Map<String, Table> tableMap = new HashMap<String, Table>();

	Result<Map<String, Object>> results = executeWriteQuery(query.toString(),
		null);
	Iterator<Map<String, Object>> i = results.iterator();
	while (i.hasNext()) {
	    Map<String, Object> map = i.next();

	    
	    Table table = new Table();
	    table.setGuid(((Node) map.get("table")).getProperty("guid")
		    .toString());
	    Node resNode = (Node) map.get("reservation");
	    if (resNode.getProperty("reservation_status").toString()
		    .equalsIgnoreCase("SEATED")) {
		table.setTableStatus("SEATED");
		table.setSeated_time(new Date(Long.parseLong(resNode
			.getProperty("act_start_time").toString())));
	    } else{
		    table.setTableStatus("ALLOCATED");	
	    }
		
	    
	    table.setTat(resNode.getProperty("tat").toString());
	    table.setReservation_StartTime(new Date(Long.parseLong(resNode
		    .getProperty("est_start_time").toString())));
	    table.setReservation_EndTime(new Date(Long.parseLong(resNode
		    .getProperty("est_end_time").toString())));
	    table.setReservationGuid(resNode.getProperty("guid").toString());
	    
	    if(tableMap.get(table.getGuid()) == null){
		tableMap.put(((Node) map.get("table")).getProperty("guid")
			    .toString(), table);
	    }else if (tableMap.get(table.getGuid()).getTableStatus().equals("SEATED")){
		
	    }else{
		tableMap.put(((Node) map.get("table")).getProperty("guid")
			    .toString(), table);
	    }
	    
	    

	}

	return tableMap;

    }

 

    /*private List<TableReservationRelation> fetchReservationWithRespectToFetchTables(
	    List<Table> tableList, String status, String dateTime) {
	StringBuilder query = new StringBuilder();
	Map<String, Object> param = new HashMap<String, Object>();
	// param.put(Constants.TABLE_GUID, UtilityMethods.getGuids(tableList));
	long time = new Date().getTime();
	query.append("Match (reservation:`Reservation`)<-[rel:`TBL_HAS_RESV`]-(table:`Table`) WHERE ");
	query.append("  table.guid IN [");
	for (Table table : tableList) {
	    query.append("'" + table.getGuid() + "',");
	}
	query = new StringBuilder(query.substring(0, query.length() - 1));
	query.append(']');

	if ((status != null) && (status.length() > 0)
		&& (!status.equalsIgnoreCase(Constants.ALL))) {
	    // param.put(Constants.TABLE_STATUS, status);
	    String[] statusArr = status.split(",");
	    query.append(" AND rel.reservation_status IN [");
	    for (String tableStatus : statusArr) {
		query.append("'" + tableStatus + "',");
	    }
	    query = new StringBuilder(query.substring(0, query.length() - 1));
	    query.append(']');
	}

	if ((dateTime != null) && (!dateTime.equals(""))) {
	    try {
		SimpleDateFormat sdf = new SimpleDateFormat(
			Constants.TIMESTAMP_FORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone("IST"));
		Logger.debug("time before parsing is " + time);
		time = sdf.parse(dateTime).getTime();
		Logger.debug("rime after parsing is " + time);

		Logger.debug("date time after parsing is "
			+ sdf.format(new Date(time)));
	    } catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	}

	query.append(" AND");
	query.append(" toInt(rel.endDate)>=toInt(" + time + ")"
		+ " AND  toInt(rel.startDate)<=toInt(" + time + ")");
	query.append(" RETURN DISTINCT rel ORDER BY rel.startDate DESC");

	Logger.debug("query is " + query);

	Result<Map<String, Object>> resvList = executeWriteQuery(query.toString(),
		param);
	Iterator<Map<String, Object>> i = resvList.iterator();
	TableReservationRelation table = null;
	List<TableReservationRelation> tables = new ArrayList<TableReservationRelation>();
	while (i.hasNext()) {
	    Map<String, Object> map = i.next();
	    for (Map.Entry<String, Object> entry : map.entrySet()) {
		template.postEntityCreation(
			(org.neo4j.graphdb.Relationship) entry.getValue(),
			TableReservationRelation.class);
		table = template.convert(entry.getValue(),
			TableReservationRelation.class);
		tables.add(table);
	    }
	}
	return tables;
    }*/

    /*private void removeBlockedFromList(List<Table> tableList,
	    List<Table> blockedTables) {
	// remove blocked tables from table list
	Set<String> blockedTableGuids = new HashSet<String>();
	for (Table table : blockedTables)
	    blockedTableGuids.add(table.getGuid());

	Iterator<Table> itr = tableList.iterator();
	while (itr.hasNext()) {
	    if (blockedTableGuids.contains(itr.next().getGuid()))
		itr.remove();
	}

    }*/

    private Map<String, Table> getBlockedTables(List<Table> tables,
	    Map<String, Object> params) {
    	
    Map<String, Object> blockParams = new HashMap<String, Object>();
	StringBuilder query =new StringBuilder("MATCH ");
		
	if(params.containsKey(Constants.REST_GUID)){
		query.append("(restaurant:Restaurant {guid:{"+Constants.REST_GUID+"}})-[`REST_HAS_CAL`]->");	
		blockParams.put(Constants.REST_GUID,params.get(Constants.REST_GUID));
	}
	query.append("(c:`" + "CalenderEvent" + "`)-[b:`"+ RelationshipTypes.CALC_BLOCKED_TBL + "`]->(t:`Table`)");		
	query.append(" WHERE  t.guid IN {" + Constants.TABLE_GUID + "} ");	
	blockParams.put(Constants.TABLE_GUID, getGuids(tables));
	
	if (params.get(Constants.START_TIME) != null && params.get(Constants.END_TIME) != null) {
	    blockParams.put(Constants.EVENT_DATE, UtilityMethods.truncateTime((Date) params.get(Constants.START_TIME)));
	    blockParams.put(Constants.START_TIME, UtilityMethods.truncateDate((Date) params.get(Constants.START_TIME)));
	    blockParams.put(Constants.END_TIME, UtilityMethods.truncateDate((Date) params.get(Constants.END_TIME)));
	    
	}else if(params.get(Constants.TIME) != null){
		 blockParams.put(Constants.EVENT_DATE, UtilityMethods.truncateTime((Date) params.get(Constants.TIME)));
		 blockParams.put(Constants.START_TIME, UtilityMethods.truncateDate((Date) params.get(Constants.TIME)));
		 blockParams.put(Constants.END_TIME, UtilityMethods.truncateDate((Date) params.get(Constants.TIME)));
		 
	}else{
		blockParams.put(Constants.EVENT_DATE,UtilityMethods.truncateTime(new LocalDateTime().toDate()));
		blockParams.put(Constants.START_TIME, UtilityMethods.truncateDate(new LocalDateTime().toDate()));
		blockParams.put(Constants.END_TIME, UtilityMethods.truncateDate(new LocalDateTime().toDate()));
	}
	
	
	    query.append("AND toInt(c.event_dt) = toInt({" + Constants.EVENT_DATE + "}) ");
	    query.append("AND toInt(c.end_time) > toInt({"   + Constants.START_TIME + "}) ");
	

	query.append("RETURN DISTINCT t,c ");

	Logger.debug("query in get block tables is >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
		+ query);

	Map<String, Table> tableMap = new HashMap<String, Table>();

	Result<Map<String, Object>> results = executeWriteQuery(query.toString(),
		blockParams);
	Logger.debug("result is " + results);
	Iterator<Map<String, Object>> i = results.iterator();
	while (i.hasNext()) {
	    Map<String, Object> map = i.next();

	    Table table = new Table();
	    table.setGuid(((Node) map.get("t")).getProperty("guid").toString());
	    Node calNode = (Node) map.get("c");

	    table.setBlockGuid(calNode.getProperty("guid").toString());

	    tableMap.put(((Node) map.get("t")).getProperty("guid").toString(),
		    table);

	}

	return tableMap;

	/*
	 * List<Table> list = executeQuery(query.toString(), blockParams);
	 * list.forEach(t-> t.setTableStatus(Constants.BLOCK)); return list;
	 */
    }
    


    @Override
    protected StringBuilder getMatchClause(java.util.Map<String, Object> params) {
	StringBuilder sb = new StringBuilder("MATCH ");

	Logger.debug("params.containsKey(Constants.SECTION_GUID)    "
		+ params.containsKey(Constants.SECTION_GUID));

	if (params.containsKey(Constants.SECTION_GUID)) {
	    sb.append("(s:Section)-[:" + RelationshipTypes.HAS_TBL + "]->");
	    // sb.append("(s:Section{guid:'"+params.get(Constants.SECTION_GUID).toString()+"'})-[:"+RelationshipTypes.HAS_TBL+"]->");
	}

	sb.append("(t:`" + type.getSimpleName() + "`) <-[rht:"
		+ RelationshipTypes.REST_HAS_TBL + "{");
	Boolean relHasProperties = false;

	if (params.containsKey(Constants.MIN_COVERS)) {
	    sb.append("min_covers:" + params.get(Constants.MIN_COVERS) + ",");
	    params.remove(Constants.MIN_COVERS);
	    relHasProperties = true;
	}
	if (params.containsKey(Constants.MAX_COVERS)) {
	    sb.append("max_covers:" + params.get(Constants.MAX_COVERS) + ",");
	    params.remove(Constants.MAX_COVERS);
	    relHasProperties = true;
	}
	if (relHasProperties)
	    sb.deleteCharAt(sb.length() - 1);

	sb.append("}]-(r:" + Restaurant.class.getSimpleName() + "{");

	if (params.containsKey(Constants.REST_ID)) {
	    sb.append(Constants.GUID + ":'" + params.get(Constants.REST_ID)
		    + "'");
	    params.remove(Constants.REST_ID);
	}
	sb.append("})");
	Logger.debug("query in match is " + sb);
	return sb;
    }

    protected StringBuilder getWhereClause(java.util.Map<String, Object> params) {
	// Boolean relHasExtraProperties = false;
	//String name=null;

	StringBuilder sb = new StringBuilder();
	sb.append(super.getWhereClause(params));

	Logger.debug("table get query after guid is " + sb);

	//if (status != null)
	//    params.put(Constants.TABLE_STATUS, status);

	if (params.containsKey(Constants.SECTION_GUID)) {
	    addPrefix(sb);
	    sb.append("s." + Constants.GUID + " ");
	    if (params.get(Constants.SECTION_GUID) instanceof List)
		sb.append("IN ");
	    else
		sb.append("= ");
	    sb.append("{" + Constants.SECTION_GUID + "}");
	}

	if (params.containsKey(Constants.MIN_COVERS_GREATER)) {
	    addPrefix(sb);
	    sb.append("t.min_covers> {" + Constants.MIN_COVERS_GREATER + "} ");
	}
	if (params.containsKey(Constants.MIN_COVERS_GREATER_EQUAL)) {
	    addPrefix(sb);
	    sb.append("t.min_covers=>{" + Constants.MIN_COVERS_GREATER_EQUAL
		    + "} ");
	}
	/*if (name!=null) {
	    addPrefix(sb);
	    sb.append("(t.name= LOWER ({" + Constants.NAME
		    + "}) OR t.name= UPPER ({"+ Constants.NAME
		    + "}))");
	    params.put(Constants.NAME, name);
	}*/
	if (params.containsKey(Constants.MIN_COVERS_LESS)) {
	    addPrefix(sb);
	    sb.append("t.min_covers< {" + Constants.MIN_COVERS_LESS + "} ");
	}
	if (params.containsKey(Constants.MIN_COVERS_LESS_EQUAL)) {
	    addPrefix(sb);
	    sb.append("t.min_covers<= {" + Constants.MIN_COVERS_LESS_EQUAL
		    + "} ");
	}
	if (params.containsKey(Constants.MAX_COVERS_GREATER)) {
	    addPrefix(sb);
	    sb.append("t.max_covers> {" + Constants.MAX_COVERS_GREATER + "} ");
	}
	if (params.containsKey(Constants.MAX_COVERS_GREATER_EQUAL)) {
	    addPrefix(sb);
	    sb.append("t.max_covers>= {" + Constants.MAX_COVERS_GREATER_EQUAL
		    + "} ");
	}
	if (params.containsKey(Constants.MAX_COVERS_LESS)) {
	    addPrefix(sb);
	    sb.append("t.max_covers< {" + Constants.MAX_COVERS_LESS + "} ");
	}
	if (params.containsKey(Constants.MAX_COVERS_LESS_EQUAL)) {
	    addPrefix(sb);
	    sb.append("t.max_covers<= {" + Constants.MAX_COVERS_LESS_EQUAL
		    + "} ");
	}

	return sb;
    }

    @Override
    public List<Table> tableExistForRestaurant(List<String> tableGuids,
	    String restId) {
	List<Table> list = new ArrayList<Table>();
	Map<String, Object> params = new java.util.HashMap<String, Object>();
	// params.put(Constants.GUID, tableName);
	params.put(Constants.REST_ID, restId);
	params.put(Constants.TABLE_GUID, tableGuids);
	StringBuilder query = new StringBuilder(
		"MATCH (r:Restaurant)-[:REST_HAS_TBL]->(t:Table) where r.guid={restId} AND t."
			+ Constants.GUID + " IN ");
	query.append("{" + Constants.TABLE_GUID + "}");
	// for (String tableGuid : tableGuids)
	// query.append("'" + tableGuid + "',");
	// query.deleteCharAt(query.length() - 1);
	// query.append("] RETURN t ");
	query.append(" RETURN t ");
	Result<Table> result = executeWriteQuery(query.toString(), params).to(
		Table.class);
	result.forEach(list::add);
	return list;
    }

    @Override
    public Boolean tableWithNameExistsForRestaurant(String tableName,
	    String restId) {
	Map<String, Object> params = new java.util.HashMap<String, Object>();
	params.put(Constants.NAME,  tableName);
	params.put(Constants.REST_ID, restId);
	params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
	StringBuilder query = new StringBuilder("MATCH (t:`Table`) where t."
		+ getPropertyName(Constants.REST_ID) + "={" + Constants.REST_ID
		+ "} AND t." + getPropertyName(Constants.STATUS) + "={" + Constants.STATUS
		+ "} AND t." + getPropertyName(Constants.NAME) + "={"
		+ Constants.NAME + "} RETURN t LIMIT 1");
	Result<Map<String, Object>> itr = executeWriteQuery(query.toString(),
		params);
	return itr.iterator().hasNext();
    }

    @Override
    public Boolean otherTableWithSameNameExists(String tableName, String guid, String restId) {
	Map<String, Object> params = new java.util.HashMap<String, Object>();
	params.put(Constants.NAME, "(?i)" + tableName);
	params.put(Constants.GUID, guid);
	params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
	params.put(Constants.REST_ID, restId);
	StringBuilder query = new StringBuilder("MATCH (t:`Table`) where t."
		+ getPropertyName(Constants.GUID) + "<>{" + Constants.GUID
		+ "} AND t." + getPropertyName(Constants.STATUS) + "={" + Constants.STATUS
		+ "} AND t." + getPropertyName(Constants.REST_ID) + "={" + Constants.REST_ID
		+ "} AND t." + getPropertyName(Constants.NAME) + "=~{"
		+ Constants.NAME + "} RETURN t LIMIT 1");
	Result<Map<String, Object>> itr = executeWriteQuery(query.toString(),
		params);
	return itr.iterator().hasNext();
    }

    /*
     * @Override public String addRestaurantTable(Restaurant restaurant, Table
     * table) {
     * 
     * // TODO Auto-generated method stub
     * 
     * template would save new Table Table savedTable = template.save(table);
     * Adding Relationship b/w savedTable and Restaurant RestaurantHasTable
     * rest_has_table = new RestaurantHasTable(restaurant, savedTable);
     * restaurant.addHasTableRelationShip(rest_has_table);
     * template.save(rest_has_table);
     * 
     * 
     * 
     * //if section is not null then create relationship of table and section
     * if((table.getSection() != null) && (!table.getSection().equals(""))) {
     * relateSectionWithTable(savedTable); }
     * 
     * return savedTable.getGuid();
     * 
     * }
     */
    @Override
    public String addRestaurantTable(Table table) {
	StringBuilder query = new StringBuilder();
	StringBuilder sectionQuery = new StringBuilder();
	query.append("MATCH (res:" + Constants.RESTAURANT_LABEL + "{guid:'"
		+ table.getRestId() + "'})");
	if ((table.getSectionId() != null)
		&& (!table.getSectionId().equals("")))
	    query.append(", (sec:" + Constants.SECTION_LABEL + "{guid:'"
		    + table.getSectionId() + "'})");
	query.append(" CREATE((res)-[rht:" + RelationshipTypes.REST_HAS_TBL
		+ "{__type__:'RestaurantHasTable',max_covers:"
		+ table.getMaxCovers() + "" + ", " + "min_covers:"
		+ table.getMinCovers() + "" + "}]->(t:" + Constants.TABLE_LABEL
		+ ":_" + Constants.TABLE_LABEL + "{");
	query.append("guid:'" + table.getGuid() + "', ");
	query.append("created_by:'" + table.getCreatedBy() + "', ");
	query.append("lang_cd:'" + table.getLanguageCode() + "', ");
	query.append("name:'" + table.getName() + "', ");
	query.append("rest_id:'" + table.getRestId() + "', ");
	if ((table.getSectionId() != null)
		&& (!table.getSectionId().equals(""))) {
	    sectionQuery.append("MERGE (sec)-[:" + RelationshipTypes.HAS_TBL
		    + "{max_covers:" + table.getMaxCovers() + ", min_covers:"
		    + table.getMinCovers() + "}]->(t) ");
	    query.append("section:'" + table.getSectionId() + "', ");
	}
	query.append("status:'" + table.getStatus() + "', ");
	query.append("type:'" + table.getType() + "', ");
	query.append("updated_by:'" + table.getUpdatedBy() + "', ");
	query.append("max_covers:" + table.getMaxCovers() + ", ");
	query.append("min_covers:" + table.getMinCovers() + ", ");
	query.append("created_dt:" + table.getCreatedDate().getTime() + ",");
	query.append("updated_dt:" + table.getUpdatedDate().getTime() + "");
	query.append("})) ");
	query.append(sectionQuery);
	query.append("return t");
	Map<String, Object> result = executeWriteQuery(query.toString(), null)
		.singleOrNull();
	if (result == null)
	    return null;
	else
	    return (String) ((Node) result.get("t"))
		    .getProperty(Constants.GUID);
    }

    
    @Override
    public String updateRestaurantTable(Table table) {
	StringBuilder query = new StringBuilder();
	query.append("MATCH(t:" + Constants.TABLE_LABEL + "{guid:'"
		+ table.getGuid() + "'}), ");
	query.append("(res:" + Constants.RESTAURANT_LABEL + "{guid:'"
		+ table.getRestId() + "'})-[rht:"
		+ RelationshipTypes.REST_HAS_TBL + "]->(t) ");
	if (table.getSectionId() != null) {
	    query.append(",(sec:" + Constants.SECTION_LABEL + "{guid:'"
		    + table.getSectionId() + "'})-[ht:"
		    + RelationshipTypes.HAS_TBL + "]->(t) ");
	} else {
	    query.append("OPTIONAL MATCH(sec:" + Constants.SECTION_LABEL
		    + ")-[ht:" + RelationshipTypes.HAS_TBL + "]->(t) ");
	}

	query.append("SET ");
	// query.append("t.created_by:'" + table.getCreatedBy() + "', ");
	query.append("t.lang_cd='" + table.getLanguageCode() + "', ");
	query.append("t.name='" + table.getName() + "', ");
	query.append("t.rest_id='" + table.getRestId() + "', ");

	query.append("t.status='" + table.getStatus() + "', ");
	query.append("t.type='" + table.getType() + "', ");
	query.append("t.updated_by='" + table.getUpdatedBy() + "', ");
	query.append("t.max_covers=" + table.getMaxCovers() + ", ");
	query.append("t.min_covers=" + table.getMinCovers() + ", ");
	// query.append("t.created_dt:" + table.getCreatedDate().getTime() +
	// ", ");
	query.append("t.updated_dt=" + table.getUpdatedDate().getTime() + ", ");
	if (table.getSectionId() != null) {
	    query.append("t.section='" + table.getSectionId() + "', ");
	    query.append("ht.max_covers=" + table.getMaxCovers() + ", ");
	    query.append("ht.min_covers=" + table.getMinCovers() + ", ");
	}
	query.append("rht.max_covers=" + table.getMaxCovers() + ", ");
	query.append("rht.min_covers=" + table.getMinCovers() + " ");

	if (table.getSectionId() == null) {
	    query.append("remove t.section ");
	    query.append("delete ht ");
	}

	query.append("return t");
	
	Map<String, Object> result = executeWriteQuery(query.toString(), null)
		.singleOrNull();
	if (result == null)
	    return null;
	else
	    return (String) ((Node) result.get("t"))
		    .getProperty(Constants.GUID);
    }

  

   

   

    @Override
    public List<Table> findBySection(List<String> sectionIds) {
	StringBuilder query = new StringBuilder(
		"MATCH (s:Section)-[:HAS_TBL]->(t:Table) where s.guid IN "
			+ "={" + Constants.SECTION_GUID + "} AND t."
			+ Constants.GUID + " IN [");
	for (String sectionGuid : sectionIds)
	    query.append("'" + sectionGuid + "',");
	query.deleteCharAt(query.length() - 1);
	query.append("] RETURN t ");
	List<Table> list = executeQuery(query.toString(), new HashMap<>());
	return list;
    }

    @Override
    public int deleteTable(Table table) {
	Logger.debug("deleting table ");
	Map<String , Object> params = new HashMap<>();
	params.put(Constants.REST_GUID, table.getRestId());
	params.put(Constants.GUID, table.getGuid());

	String query = "MATCH ";
	if (table.getSectionId() != null && !table.getSectionId().equals("")) {
	    query = query + "(t)-[p:HAS_TBL]->";
	}
	query = query + "(s:Table)<-[q:REST_HAS_TBL]-(rest:Restaurant{guid:{"
		+ Constants.REST_GUID + "}}) WHERE s.guid={" + Constants.GUID
		+ "}";

	if (table.getSectionId() != null && !table.getSectionId().equals("")) {
	    query = query + "DELETE q,p";
	} else {
	    query = query + "DELETE q";
	}

	query = query + " RETURN count(distinct(q))";
	Logger.debug("query is " + query);
	Result<Integer> result = executeWriteQuery(query.toString(), params).to(
		Integer.class);
	Logger.debug("query executed,Result is " + result);

	return result.single();
    }

    @Override
    public Map<String, String> getCustomTable(Map<String, Table> tableMap) {
	Map<String, String> tbl_Server = new HashMap<String, String>();
	Set<String> keyset = tableMap.keySet();
	StringBuilder query = new StringBuilder(
		"MATCH (s:Server)<-[:TBL_HAS_SERVER]-(t:Table) where t.guid IN [");
	for (String tableguid : keyset) {
	    query.append("'" + tableguid + "',");
	}
	if (!keyset.isEmpty())
	    query.deleteCharAt(query.length() - 1);
	query.append("] RETURN s.guid,t.guid ");

	Iterator<Map<String, Object>> result = executeWriteQuery(query.toString(),
		null).iterator();
	while (result.hasNext()) {
	    Map<String, Object> map = result.next();
	    String tblGuid = (String) map.get("t.guid");
	    String serverGuid = (String) map.get("s.guid");
	    tbl_Server.put(tblGuid, serverGuid);
	}
	return tbl_Server;
    }
    
    @Override
	public Map<String,Object> validateTableBeforeDelete(Table table){
		Map<String,Object> params=new HashMap<String,Object>();
		Calendar calc = Calendar.getInstance();
		SimpleDateFormat formatDate = new SimpleDateFormat(
				Constants.DATE_FORMAT);
		SimpleDateFormat formatTimeStamp = new SimpleDateFormat(
				Constants.TIMESTAMP_FORMAT);
		SimpleDateFormat formatTime = new SimpleDateFormat(
				Constants.TIME_FORMAT);
		String currentDate = formatDate
				.format(new Date(calc.getTimeInMillis()));				
		try {
			params.put(Constants.START_TIME,Calendar.getInstance().getTimeInMillis());
			params.put(Constants.REST_GUID, table.getRestId());
			params.put(Constants.TABLE_GUID, table.getGuid());
			params.put(Constants.SECTION_GUID, table.getGuid());
			params.put(Constants.EVENT_DATE, formatDate.parse(currentDate).getTime());
			params.put(Constants.END_TIME, formatTimeStamp.parse(LocalDate.ofEpochDay(0)+" "+formatTime.format(Calendar.getInstance().getTimeInMillis())).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		StringBuffer query=new StringBuffer();
		query.append("MATCH (rest:Restaurant {guid:{"+Constants.REST_GUID+"}})-[REST_HAS_TBL]->(table:Table {guid:{"+Constants.TABLE_GUID+"}}) WHERE table.status='ACTIVE' WITH table \n");
		query.append("OPTIONAL MATCH (table)-[rel:TBL_HAS_RESV]->(reservation:Reservation) \n "
				+ "where "
				+ "(reservation.reservation_status <> 'CANCELLED'   "
				+ "AND reservation.reservation_status <> 'NO_SHOW' AND "
				+ "reservation.reservation_status <> 'FINISHED')  "
			    + " OR "
			    + " (toInt(reservation.est_end_time) >=toInt("+params.get(Constants.START_TIME)+"))"
			    +" WITH count(rel) as table_reservation,table \n") ;

		query.append("OPTIONAL MATCH (table)<-[relCal:`CALC_BLOCKED_TBL`]-(cEvent:CalenderEvent) "
				+ "WHERE toInt(cEvent.event_dt)>=toInt({"+Constants.EVENT_DATE+"})  AND toInt(cEvent.end_time)>=toInt({"+Constants.END_TIME+"}) "
				+ "WITH count(relCal) as total_blocked_tables,table_reservation,table\n");
		query.append(" RETURN total_blocked_tables,table_reservation,table");
		
		//Map<String, Object> map = new HashMap<String, Object>();

		Result<Map<String, Object>> results = executeWriteQuery(query.toString(),
			params);
		params=new HashMap<String,Object>();
		Iterator<Map<String, Object>> i = results.iterator();	
		while (i.hasNext()) {
			Map<String, Object> map = i.next();	
		        
		        template.postEntityCreation((Node) map.get("table"), Table.class);
		        Table tableObject=template.convert(map.get("table"), Table.class);
		       
		        String tablehasReservation=map.get("table_reservation").toString();
		        String blockedTable=map.get("total_blocked_tables").toString();
		      
		        params.put(Constants.TABLE_ID, tableObject);
		        params.put(Constants.RESERVATION_ID, tablehasReservation);
		        params.put(Constants.BLOCKED, blockedTable);
		        
		        return params;
		        
		}
		
		return params;
	}

    @Override
    public Map<String,Integer> getTables(Object restGuid) 
    {
	  

		StringBuilder query = new StringBuilder();
		Map<String,Object> params = new HashMap<>();
		StringBuilder query1 = new StringBuilder("");
		if((restGuid != null) && (!restGuid.equals("")) )
		{
		    params.put(Constants.REST_GUID, restGuid);
		    query1.append(" where t.rest_id={restaurantGuid} ");
		}

		query.append("MATCH (t:Table) ");
		query.append(query1);
		query.append(" RETURN t.guid,t.max_covers");
		Logger.debug("query is================================================"
			+ query);
		Map<String, Integer> tableMap = new HashMap<String, Integer>();
		String tableGuid = "";
		Integer maxCovers = 0;

		Result<Map<String, Object>> results = template.query(query.toString(),
			params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) 
		{
		    Map<String, Object> map = i.next();
		    tableGuid = (String) map.get("t.guid");
		    maxCovers = (Integer) map.get("t.max_covers");
		    tableMap.put(tableGuid, maxCovers);
		}

		return tableMap;

    }
    
    
    
    
    /* Get cal events of type HOLIDAY,BLOCK,FULL_BLOCK */
	
	@Override
	public List<String> getBlockedTables(Map<String, Object> queryMap)
	{
	    StringBuilder query=new StringBuilder();
	   
	    query.append("MATCH (rest:Restaurant{guid:{"+Constants.REST_GUID+"}})-[REST_HAS_CAL]->(calEvent:CalenderEvent) WHERE  calEvent.status='ACTIVE' "
			+ " AND  calEvent.category='BLOCK' AND calEvent.type='EVENT' "
			+" AND ((toInt(calEvent.event_dt) + toInt(calEvent.start_time)) <= toInt({"+Constants.START_TIME+"}) AND (toInt(calEvent.event_dt) + toInt(calEvent.end_time)) > toInt({"+Constants.START_TIME+"})) "
			+ " RETURN calEvent.blocking_area as tables");
		
	    Result<Map<String, Object>> result = template.query(query.toString(),queryMap);
	    Iterator<Map<String, Object>> i = result.iterator();
	    List<String> blockedTables = new ArrayList<>();
	    
	    while (i.hasNext()) 
	    {
		Map<String, Object> map = i.next();
		List<String> blockArea = (List<String>) map.get("tables");
		if(blockArea != null)
		{

		for(String tableGuid : blockArea)
		{
		    if(!blockedTables.contains(tableGuid))
		    {
			blockedTables.add(tableGuid);
		    }
		}
		}

	    }
	    
	    return blockedTables;
		
	}
	
	@Override
	public Map<Section, List<Table>> getSectionsAndTables(String restGuid) 
    {
	  
		StringBuilder query = new StringBuilder();
		Map<String,Object> params = new HashMap<>();
		params.put(Constants.REST_GUID,restGuid );
		params.put(Constants.STATUS,Constants.ACTIVE_STATUS);
		query.append("MATCH (r:"+Restaurant.class.getSimpleName()+"{guid:{"+Constants.REST_GUID+"}})-[hs:"+RelationshipTypes.HAS_SECTION+"]->(s:"+Section.class.getSimpleName()+"{status:{"+Constants.STATUS+"}})-[ht:"+RelationshipTypes.HAS_TBL+"]->(t:Table{status:{"+Constants.STATUS+"}}) ");
		query.append(" RETURN s.guid as guid, s.name as name ,COllECT([t.guid,t.name,t.max_covers,t.min_covers]) as table;");
		Logger.debug("query is================================================"+ query);
		Map<Section, List<Table>> tableSectionMap = new HashMap<Section, List<Table>>();
		Result<Map<String, Object>> results = template.query(query.toString(),params);
		Iterator<Map<String, Object>> i = results.iterator();
		while (i.hasNext()) 
		{
		    Map<String, Object> map = i.next();
		    Section s=new Section();
		    s.setCreatedDate(null);
		    s.setStatus(null);
		    s.setGuid((String) map.get("guid"));
		    s.setName((String) map.get("name"));
		    List<Table> tablesList=new ArrayList<Table>();
		    List<List<Object>> tables = (ArrayList<List<Object>>) map.get("table");
		    tables.forEach(table->{
		    	Table t = new Table();
		    	t.setCreatedDate(null);
			    t.setStatus(null);
		    	t.setGuid((String)table.get(0));
		    	t.setName((String)table.get(1));
		    	t.setMaxCovers((Integer)(table.get(2)));
		    	t.setMinCovers((Integer)(table.get(3)));
		    	t.setSectionId((String) map.get("guid"));
		    	t.setSectionName((String) map.get("name"));
		    	tablesList.add(t);
		    });
		    tableSectionMap.put(s, tablesList);
		}

		return tableSectionMap;

    }
	
	
	@Override
    public List<Table> getCustomTables(Map<String,Object> params) 
    {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:"+Restaurant.class.getSimpleName()+"{guid:{"+Constants.REST_GUID+"}})-[hs:"+RelationshipTypes.HAS_SECTION+"]->(s:"+Section.class.getSimpleName()+"{status:{"+Constants.STATUS+"}})-[ht:"+RelationshipTypes.HAS_TBL+"]->(t:Table{status:{"+Constants.STATUS+"}}) ");
		query.append(" WHERE toInt(t.updated_dt)>toInt({"+Constants.UPDATED_AFTER+"})");
		query.append(" RETURN t.guid,t.name,t.max_covers,t.min_covers,s.guid,s.name");
		Logger.debug("query is================================================"+ query);
		Result<Map<String, Object>> results = template.query(query.toString(),
			params);
		Iterator<Map<String, Object>> i = results.iterator();
    	List<Table> tablesList= new ArrayList<Table>();

		while (i.hasNext()) 
		{
		    Map<String, Object> map = i.next();
		    Table t = new Table();
	    	t.setCreatedDate(null);
		    t.setStatus(null);
	    	t.setGuid((String)map.get("t.guid"));
	    	t.setName((String)map.get("t.name"));
	    	t.setMaxCovers((Integer)map.get("t.max_covers"));
	    	t.setMinCovers((Integer)map.get("t.min_covers"));
	    	t.setSectionId((String) map.get("s.guid"));
	    	t.setSectionName((String) map.get("s.name"));
			tablesList.add(t);
		}

		return tablesList;

    }


}
