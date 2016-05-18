package com.clicktable.dao.impl;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.TransactionFailureException;
import org.neo4j.kernel.DeadlockDetectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.support.Neo4jTemplate;

import play.Logger;

import com.clicktable.dao.intf.GenericDao;
import com.clicktable.model.Entity;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;

/**
 * @author s.gupta
 *
 * @param <T>
 */
public abstract class GraphDBDao<T> implements GenericDao<T> {

	@Autowired
	Neo4jTemplate template;

	@Autowired
	GraphDatabaseService dbService;

	Class type;

	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public T create(T t) {
		t = template.save(t); // not creating unique node
		return t;
	}

	public List<Map<String, Object>> objectsToMap(List<T> list) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		for (T t : list) {
			mapList.add(getGraphProperty(UtilityMethods.introspect(t)));
		}
		return mapList;
	}

	@Override
	public List<T> createMultiple(List<T> list) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		for (T t : list) {
			mapList.add(getGraphProperty(UtilityMethods.introspect(t)));
		}
		String query = createCypherForMultiple();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("propMap", mapList);
		return executeQuery(query, params);
	}

	@Override
	public List<T> updateMultiple(List<String> guidList, Map<String, Object> valuesToUpdate) {

		Map<String, Object> params = new HashMap<String, Object>();
		valuesToUpdate = getGraphProperty(valuesToUpdate);
		params.putAll(valuesToUpdate);
		params.put(Constants.GUID, guidList);

		StringBuilder query = new StringBuilder("MATCH (n:" + type.getSimpleName() + ": _" + type.getSimpleName() + ") ");
		query.append(" WHERE n." + Constants.GUID + " IN {" + Constants.GUID + "} ");

		for (String key : valuesToUpdate.keySet()) {
			appendSet(query);
			query.append(" n." + key + "={" + key + "} ");
		}
		query.append("RETURN n");
		return executeQuery(query.toString(), params);
	}

	private void appendSet(@NotNull StringBuilder query) {
		if (query.toString().contains("SET ")) {
			query.append(" , ");
		} else {
			query.append("SET ");
		}
	}

	private String createCypherForMultiple() {
		String query = "CREATE (t:" + type.getSimpleName() + ": _" + type.getSimpleName() + " { propMap }) RETURN t";
		return query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAll(Class type) {

		List<T> list = new ArrayList<T>();
		Iterable<T> itr = template.findAll(type).as(type);
		itr.forEach(list::add);
		/*
		 * for (T t : itr) { list.add(t); }
		 */
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T find(Object id) {
		String query = "MATCH (t:" + type.getSimpleName() + ") WHERE t." + Constants.GUID + "={" + Constants.GUID + "} RETURN t";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Constants.GUID, id.toString());
		Logger.debug("query is " + query);
		List<T> res = executeQuery(query, param);
		if (!res.isEmpty())
			return res.get(0);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T update(T t) {
		Map<String, Object> valuesToUpdate = getGraphProperty(UtilityMethods.introspect(t));
		valuesToUpdate.remove(getPropertyName(Constants.CREATED_DATE));
		StringBuilder query = new StringBuilder("MATCH (t:" + type.getSimpleName() + ": _" + type.getSimpleName() + " {guid:{" + Constants.GUID + "}}) ");
		for (String key : valuesToUpdate.keySet()) {
			appendSet(query);
			query.append(" t." + key + "={" + key + "} ");
		}
		query.append("RETURN t");
		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), valuesToUpdate);
		t = (T) template.projectTo(result.single().get("t"), type);
		return t;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Class type, Object id) {
		template.delete(template.findOne((long) id, type));
	}
	
	@Override
	public void delete(String guid) {
		StringBuilder query=new StringBuilder("Match (c:`"+type.getSimpleName()+"` {guid:{"+Constants.GUID+"}})"
				+ " WITH c OPTIONAL MATCH (c)-[rel]-() DELETE c,rel ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, guid);	
		executeWriteQuery(query.toString(), params);
		//super.delete(type, id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findByFields(Class type, Map<String, Object> params) {
		StringBuilder query = getMatchClause(params);
		query.append(getWhereClause(params));
		query.append(getReturnClause(params));
		return executeQuery(query.toString(), params);
	}
	
	@Override
	public int getCountWithParams(Map<String, Object> params) {
		StringBuilder query = getMatchClause(params);
		query.append(getWhereClause(params));
		query.append(" RETURN count(distinct(t))");		
		Result<Integer> result = executeWriteQuery(query.toString(), params).to(Integer.class);
		return result.single();
	}

	protected StringBuilder getMatchClause(Map<String, Object> params) {
		return new StringBuilder("MATCH (t:`" + type.getSimpleName() + "`)");
	}

	protected StringBuilder getGenericMatchClause(Class type, StringBuilder query) {

		query.append(type.getSimpleName());
		return query;
	}

	protected StringBuilder getSetClause(Map<String, Object> params, StringBuilder query) {
		query.append(type.getSimpleName());
		return query;
	}

	protected List<T> executeQuery(String query, Map<String, Object> params) {
		Logger.debug(query);
		params.forEach((x, y) -> Logger.debug(x + ">" + y));
		Result<Map<String, Object>> results = template.query(query, params);

		return convertResultToList(results);
	}

	protected Result<Map<String, Object>> executeWriteQuery(String query, Map<String, Object> params) {
		Logger.debug(query);
		if (params != null)
			params.forEach((x, y) -> Logger.debug(x + ">" + y));
		Result<Map<String, Object>> results = null;
		int RETRIES = 5;
		int BACKOFF = 3000;
		for (int i = 0; i < RETRIES; i++) {
			try {
				results = template.query(query, params);
				return results;
			} catch (DeadlockDetectedException ex) {
				Logger.error("DEADLOCK DETECTED");
				if (i < RETRIES - 1) {
					try {
						Thread.sleep(BACKOFF);
					} catch (InterruptedException e) {
						throw new TransactionFailureException("Interrupted", e);
					}
				} else
					throw ex;
			}
		}
		return results;
	}

	protected List<T> convertResultToList(Result<Map<String, Object>> results) {
		List<T> list = new ArrayList<T>();
		if (results != null) {
			Iterator<Map<String, Object>> i = results.iterator();
			while (i.hasNext()) {
				Map<String, Object> map = i.next();
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					template.postEntityCreation((Node)entry.getValue(), type);
					T t = (T) template.projectTo(entry.getValue(), type);				
					list.add(t);
				}
			}
		}
		return list;
	}

	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		query.append(" RETURN t");
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);// +
																			// " collect(t) as betAnswers";

	}

	/**
	 * returns page size string,if no page size is provided in query string than
	 * it returns default page size string
	 * 
	 * @param params
	 * @return
	 */
	protected int getPageSize(Map<String, Object> params) {

		int pageSize = Integer.parseInt(play.i18n.Messages.get(Constants.PAGE_SIZE));
		// Fetch pagination info
		// check for page size
		// if page size is not numeric then default page size will be used
		if (params.containsKey(Constants.PAGE_SIZE)) {
			String pageSizeStr = (String) params.get(Constants.PAGE_SIZE);
			try {
				pageSize = Integer.parseInt(pageSizeStr);
				if ((pageSize > Constants.PAGE_SIZE_LIMIT) || (pageSize < 0)) {
					pageSize = Integer.parseInt(play.i18n.Messages.get(Constants.PAGE_SIZE));
				}
			} catch (NumberFormatException nfe) {
				Logger.debug("Exception is ---------" + nfe.getMessage());

				pageSize = Integer.parseInt(play.i18n.Messages.get(Constants.PAGE_SIZE));
			}

		}
		return pageSize;
	}

	protected int getIndex(Map<String, Object> params, int pageSize) {
		// check for page no
		int startIndex = 0;
		if (params.containsKey(Constants.PAGE_NO)) {
			String pageNoStr = (String) params.get(Constants.PAGE_NO);
			startIndex = UtilityMethods.getStartIndex(pageNoStr, pageSize);
		}
		return startIndex;
	}

	protected StringBuilder createDynamicFinderQuery(Class type, Map<String, Object> params) {
		return getMatchClause(params).append(getWhereClause(params));
	}
	protected StringBuilder getWhereClause(Map<String, Object> params) {
		return getWhereClause(params, "t");
	}

	protected StringBuilder getWhereClause(Map<String, Object> params, String prefix) {

		// code to build dyanamic query on the basis of fields in param Map

		StringBuilder query = new StringBuilder();
		query.append(" WHERE ");

		for (java.util.Map.Entry<String, Object> entry : params.entrySet()) {

			Field field;

			if (entry.getKey().equals(Constants.ID))
				query.append(" id(t) = {" + entry.getKey() + "} AND");
			else if (null != (field = UtilityMethods.getClassField(entry.getKey(), type))) {
				appendQueryForFieldValue(query,entry, field, "t");				
			}else if (entry.getKey().endsWith(Constants.BEFORE)
					|| entry.getKey().endsWith(Constants.AFTER)
					|| entry.getKey().endsWith(Constants.BEFORE_OR_ON)
					|| entry.getKey().endsWith(Constants.AFTER_OR_ON)
					|| entry.getKey().endsWith(Constants.ON)) {
				appendDateCompareClause(query,entry,prefix);								
			}else if (entry.getKey().endsWith(Constants.LIKE) ) {
				appendLikeQuery(query,entry, prefix);
			}else if(entry.getKey().endsWith(Constants.START_WITH)  || entry.getKey().endsWith(Constants.STARTS_WITH)){
				appendStartWithQuery(query,entry, prefix);
			}else if (entry.getKey().endsWith(Constants.LESS)
					|| entry.getKey().endsWith(Constants.GREATER)
					|| entry.getKey().endsWith(Constants.LESS_EQUAL)
					|| entry.getKey().endsWith(Constants.GREATER_EQUAL)
					) {
				appendNumberCompareQuery(query,entry);
			}
		}

		if (query.toString().contains(Constants.AND))
			query = new StringBuilder(query.substring(0, query.length() - 3));
		else
			query = new StringBuilder(query.substring(0, query.length() - 6));

		return query;
	}

	private void appendStartWithQuery(StringBuilder query,
			Entry<String, Object> entry, String prefix) {
		String fieldName =null;
		if(entry.getKey().endsWith(Constants.START_WITH))
			fieldName= entry.getKey().substring(0,entry.getKey().length()-Constants.START_WITH.length());
		else
			fieldName= entry.getKey().substring(0,entry.getKey().length()-Constants.STARTS_WITH.length());
		Field field;
		if(null != (field = UtilityMethods.getClassField(fieldName, type))
				&& field.getType().isAssignableFrom(String.class)){
			query.append(" "+prefix+"."+getPropertyName(fieldName)+" =~{"+entry.getKey()+"} AND");
			}
		
	}

	private void appendNumberCompareQuery(StringBuilder query,
			Entry<String, Object> entry) {
		String fieldName = null ;
		String comparator = null;
		if(entry.getKey().endsWith(Constants.LESS)){
			fieldName = entry.getKey().substring(0,entry.getKey().length()-Constants.LESS.length());
			comparator ="<";
		}else if(entry.getKey().endsWith(Constants.GREATER)){
			fieldName = entry.getKey().substring(0,entry.getKey().length()-Constants.GREATER.length());	
			comparator =">";
		}else if(entry.getKey().endsWith(Constants.LESS_EQUAL)){
			fieldName = entry.getKey().substring(0,entry.getKey().length()-Constants.LESS_EQUAL.length());
			comparator ="<=";
		}else if(entry.getKey().endsWith(Constants.GREATER_EQUAL)){
			fieldName = entry.getKey().substring(0,entry.getKey().length()-Constants.GREATER_EQUAL.length());
			comparator =">=";
		}
		Field field;
		
		if(fieldName!=null && (null != (field = UtilityMethods.getClassField(fieldName, type)))
				&& field.getType().isAssignableFrom(String.class)){
			query.append(" t."+getPropertyName(fieldName)+comparator+" {"+entry.getKey()+"} AND");
			}
		
	}

	private void appendQueryForFieldValue(StringBuilder query,
			Entry<String, Object> entry, Field field, String prefix) {
		if (field.getType().isAssignableFrom(List.class)) {
			query.append("HAS("+prefix+"." + getPropertyName(entry.getKey()) + ") AND ALL(m in [");
			//for (String listParam : entry.getValue().toString().split(","))
				query.append("{" + entry.getKey() + "}");
			query.append("] where m in "+prefix+"." + getPropertyName(entry.getKey()) + ")");
		} else if (entry.getValue() instanceof List) {
			query.append(" "+prefix+"." + getPropertyName(entry.getKey()) + " IN {" + entry.getKey() + "} AND");
		} else if (entry.getValue() instanceof Date)
			query.append(" toInt("+prefix+"." + getPropertyName(entry.getKey()) + ") =toInt({" + entry.getKey() + "}) AND");
		else
			query.append(" "+prefix+"." + getPropertyName(entry.getKey()) + " ={" + entry.getKey() + "} AND");		
	}

	private void appendLikeQuery(StringBuilder query,
			Entry<String, Object> entry, String prefix) {
		//StringBuilder query = new StringBuilder();
		String fieldName = entry.getKey().substring(0,entry.getKey().length()-Constants.LIKE.length());
		Field field;
		if(null != (field = UtilityMethods.getClassField(fieldName, type))
				&& field.getType().isAssignableFrom(String.class)){
			query.append(" "+prefix+"."+getPropertyName(fieldName)+" =~{"+entry.getKey()+"} AND");
			}
		
	}

	private void appendDateCompareClause(StringBuilder query, Entry<String, Object> entry, String prefix) {
		if(entry.getKey().endsWith(Constants.BEFORE)){
			query.append(getDateCompareClause(entry,Constants.BEFORE, prefix));			
		}else if(entry.getKey().endsWith(Constants.AFTER)){
			query.append(getDateCompareClause(entry,Constants.AFTER, prefix));	
		}else if(entry.getKey().endsWith(Constants.BEFORE_OR_ON)){
			query.append(getDateCompareClause(entry,Constants.BEFORE_OR_ON, prefix));	
		}else if(entry.getKey().endsWith(Constants.AFTER_OR_ON)){
			query.append(getDateCompareClause(entry,Constants.AFTER_OR_ON, prefix));	
		}else if(entry.getKey().endsWith(Constants.ON)){
			query.append(getDateCompareClause(entry,Constants.ON, prefix));	
		}
		
	}

	private StringBuilder getDateCompareClause(Entry<String, Object> entry,
			String comparator, String prefix) {
		StringBuilder query = new StringBuilder();
		String fieldName = entry.getKey().substring(0,entry.getKey().length()-comparator.length());
		Field field;
		/*if(type.getName().equals("com.clicktable.model.Reservation")){
			if(comparator.equals("Before") || comparator.equals("After")){
				fieldName = fieldName + "Time";
			}
		}*/
		if(null != (field = UtilityMethods.getClassField(fieldName, type))
				|| null != (field = UtilityMethods.getClassField(fieldName+Constants.DATESTR, type)) 
				|| null != (field = UtilityMethods.getClassField(fieldName+Constants.TIMESTR, type))
				&& (field.getType().isAssignableFrom(Date.class))){
			if (entry.getValue() instanceof Date || entry.getValue() instanceof DateTime){
				switch(comparator){
				case Constants.BEFORE:
					query.append(" toInt("+prefix+"." + getPropertyName(field.getName()) + ") < toInt({" + entry.getKey() + "}) AND");
					break;
				case Constants.AFTER:
					query.append(" toInt("+prefix+"." + getPropertyName(field.getName()) + ") > toInt({" + entry.getKey() + "}) AND");
					break;
				case Constants.BEFORE_OR_ON:
					query.append(" toInt("+prefix+"." + getPropertyName(field.getName()) + ") <= (toInt({" + entry.getKey() + "})+(24*60*60*1000-1)) AND");
					break;
				case Constants.AFTER_OR_ON:
					query.append(" toInt("+prefix+"." + getPropertyName(field.getName()) + ") >= toInt({" + entry.getKey() + "}) AND");
					break;
				case Constants.ON:
					query.append(" toInt("+prefix+"." + getPropertyName(field.getName()) + ") >= toInt({" + entry.getKey() + "}) AND ");
					query.append(" toInt("+prefix+"." + getPropertyName(field.getName()) + ") <= (toInt({" + entry.getKey() + "})+(24*60*60*1000-1)) AND");
					break;
				}
			}
		}
		return query;
	}

	
	protected String getPropertyName(String key) {

		String propertyName = key;
		GraphProperty property = null;
		try {
			property = UtilityMethods.getClassField(key, type).getAnnotation(GraphProperty.class);
		} catch (SecurityException e) {
			Logger.debug("Exception is --------" + e);
			Logger.debug("Graphproperty not set for " + key);
		} catch (NullPointerException e1) {
			Logger.debug("Exception is --------" + e1);
			Logger.debug("Graphproperty not allowed " + key);
		}
		if (property != null)
			propertyName = property.propertyName();

		return propertyName;

	}

	protected T findRelationshipById(Class startType, String startGuid, Class endType, String endguid) {
		String query = "MATCH (a:" + startType.getSimpleName() + " {" + Constants.GUID + ":{startGuid}})- [r] ->(b:" + endType.getSimpleName() + " {" + Constants.GUID + ":{endGuid}}) RETURN r";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("startGuid", startGuid);
		param.put("endGuid", endguid);
		Logger.debug("query is " + query);
		Iterator<T> itr = template.query(query, param).to(type).iterator();
		if (itr.hasNext())
			return itr.next();
		return null;

	}

	/**
	 * method to handle order by cause (
	 * 
	 * @param query
	 * @param status
	 *            (comma separated list of fields (Like firstName,lastName )
	 * @return
	 */
	public StringBuilder handleOrderBy(StringBuilder query, Map<String, Object> params) {

		// if orderBy parameter comes in param string then it applies order by
		// query on the basis of comma separated parameters
		if ((params.containsKey(Constants.ORDER_BY)) && (!params.get(Constants.ORDER_BY).equals(""))) {
			query.append(" ORDER BY ");
			Logger.debug("params.get(Constants.ORDER_BY)>>>"+params.get(Constants.ORDER_BY));
			String[] orderParams = params.get(Constants.ORDER_BY).toString().split(",");
			for (String string : orderParams) {
				Logger.debug("string>>"+string);
				
			}
			String queryString = "";
			Field field;
			Logger.debug("order by>>>>>>>>>>>>>>>>>>>>>>");
			for (String fieldName : orderParams) {
				Logger.debug("fieldName>>"+fieldName);
				if (null != (field = UtilityMethods.getClassField(fieldName, type))) {
					if (field.getType().isAssignableFrom(String.class)) {
						queryString = queryString + "LOWER(t." + getPropertyName(fieldName) + "),";
						//break;
					}else if(field.getType().isAssignableFrom(Date.class)){
						queryString = queryString + "toInt(t." + getPropertyName(fieldName) + "),";
						//break;
					}

				}else{
					queryString = queryString + "t." + getPropertyName(fieldName) + ",";
				}

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

	protected StringBuilder addPrefix(StringBuilder sb) {
		if (sb.toString().contains("WHERE")) {
			sb.append("  AND ");
		} else {
			sb.append(" " + Constants.WHERE + " ");
		}
		return sb;
	}

	protected Map<String, Object> addingNodeProperties(StringBuilder query, Object type) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = UtilityMethods.introspect(type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.debug("Exception is ----------" + e.getLocalizedMessage());
		}
		Map<String, Object> params = new HashMap<String, Object>();
		for (Entry<String, Object> entry : map.entrySet()) {
			if (!entry.getKey().toUpperCase().equalsIgnoreCase("CLASS") && (null != entry.getValue())) {
				query.append(getPropertyName(entry.getKey()) + ":{" + entry.getKey() + "},");
				if (entry.getValue().getClass().equals(Timestamp.class))
					params.put(entry.getKey(), ((Date) entry.getValue()).getTime());
				else
					params.put(entry.getKey(), entry.getValue());
			}
		}
		query.deleteCharAt(query.length() - 1);
		return params;
	}

	protected Map<String, Object> getGraphProperty(Map<String, Object> fieldMap) {
		Map<String, Object> propMap = new HashMap<String, Object>();
		for (Entry<String, Object> fieldName : fieldMap.entrySet()) {
			propMap.put(getPropertyName(fieldName.getKey()), fieldName.getValue());
		}
		propMap.remove("class");
		return propMap;
	}

	protected Map<String, Object> updatingNodeProperties(StringBuilder query, Object type, String variableName) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = UtilityMethods.introspect(type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> params = new HashMap<String, Object>();
		for (Entry<String, Object> entry : map.entrySet()) {
			System.out.println(entry.getKey() + "." + entry.getValue());
			if (!entry.getKey().equalsIgnoreCase("CLASS")) {
				query.append(variableName + "." + getPropertyName(entry.getKey()) + "={" + entry.getKey() + "},");
				if (entry.getValue().getClass().equals(Timestamp.class))
					params.put(entry.getKey(), ((Date) entry.getValue()).getTime());
				else
					params.put(entry.getKey(), entry.getValue());
			}

		}
		query.deleteCharAt(query.length() - 1);
		return params;
	}

	protected boolean containsKey(Map<String, Object> params, String key) {            //map by default have the feature of contains
		Set<Entry<String, Object>> entrySet = params.entrySet();

		for (Entry<String, Object> entry : entrySet) {
			if (entry.getKey().toLowerCase().contains(key.toLowerCase()))
				return true;
		}

		return false;
	}

	protected String getSingleResultGuid(Result<Map<String, Object>> results) {
		Map<String, Object> result = results.singleOrNull();
		if (result == null)
			return null;
		else
			return (String) ((Node) result.get("t")).getProperty(Constants.GUID);
	}

	protected Long getResultId(Result<Map<String, Object>> results) {
		Iterator<Map<String, Object>> result = results.iterator();
		Long id = 0L;
		while (result.hasNext()) {
			Map<String, Object> map = result.next();
			Logger.debug("map is " + map);
			id = Long.valueOf(map.get("id(q)").toString());
			Logger.debug("id is " + id);
		}
		return id;
	}

	public List<String> getGuids(List<? extends Entity> entities) {
		List<String> guids = new ArrayList<String>();
		entities.forEach((e) -> {
			guids.add(e.getGuid());
		});
		return guids;
	}
}
