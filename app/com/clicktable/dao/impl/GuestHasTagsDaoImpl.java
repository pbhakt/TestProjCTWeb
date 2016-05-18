/**
 * 
 */
package com.clicktable.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.support.Neo4jTemplate;

import play.Logger;

import com.clicktable.dao.intf.GuestHasTagsDao;
import com.clicktable.model.Entity;
import com.clicktable.model.TagModelOld;
import com.clicktable.relationshipModel.GuestTagPreferencesRelationshipModel;
import com.clicktable.repository.GuestProfileRepo;
import com.clicktable.repository.TagPreferencesRepo;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.GuestHasTagsValidator;

/**
 * @author a.thakur
 *
 */

@org.springframework.stereotype.Service
public class GuestHasTagsDaoImpl extends GraphDBDao<TagModelOld> implements
		GuestHasTagsDao {

	@Autowired
	Neo4jTemplate template;

	@Autowired
	TagPreferencesRepo tag_repo;

	@Autowired
	GuestHasTagsValidator guestValidate;

	@Autowired
	GuestProfileRepo guestProfile_repo;
	GuestTagPreferencesRelationshipModel relation_model;
	
	public GuestHasTagsDaoImpl() {
		super();
		this.setType(TagModelOld.class);
	}

	/*
	 * Add Guest relationship with Existing Tag 
	 */
	@Override
	public String addGuestTagRelationship(String guestProfile_guid,
			String rest_guid, List<TagModelOld> tag, long roleId) {

		Map<String, Object> eventMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUEST_GUID, guestProfile_guid);
		params.put(Constants.REST_GUID, rest_guid);
		
		Result<Map<String, Object>> r = null;
		StringBuilder query = new StringBuilder();
		StringBuilder query1 = new StringBuilder();
		
		boolean existingRelationship = false;
		boolean creatingRelationship = false;
		String queryClause = "";		
		query.append("MATCH (guest:`GuestProfile`");
		query.append("{guid:{" + Constants.GUEST_GUID + "}}").append(")");
		if (Long.valueOf(roleId).equals(Constants.STAFF_ROLE_ID)	|| Long.valueOf(roleId).equals(Constants.MANAGER_ROLE_ID)	|| Long.valueOf(roleId).equals(Constants.ADMIN_ROLE_ID)){
			query.append(",(rest:`Restaurant`{guid:{" + Constants.REST_GUID + "}})  ");			
		}
		query1.append(" , (tag:`Tag`)");
		query1.append(" WHERE tag.guid IN [ ");
		for (TagModelOld tagModel : tag) {
			if (tagModel.isExist()) {
				existingRelationship = true;
				query1.append("'" + tagModel.getGuid() + "',");
			}
		}
		if (existingRelationship) {
			query1 = new StringBuilder(query1.substring(0, query1.length() - 1));
		}
		query1.append(" ] \n");

		 /*Get With Clause of Event Model */
		query.append("WITH [");
		for (TagModelOld tagModel : tag) {
			if (!tagModel.isExist()) {
				creatingRelationship = true;
				query.append(" {");
				eventMap = UtilityMethods.entityConversionToMap(tagModel);
				eventMap = guestValidate.validateFinderParams(eventMap,
						TagModelOld.class);
				eventMap = this.parseCustomeDate(eventMap, tagModel);
				query.append(getReturnQuery(eventMap, ""));
				query.append(" },");
			}
		}
		if (creatingRelationship)
		{
			/* Code for creating New Tag as well as relation ship with Tag Model*/ 
			queryClause = query.substring(0, query.length() - 1);

			query.setLength(0);
			query.append(queryClause).append("] AS tag , guest ");
			if (Long.valueOf(roleId).equals(Constants.STAFF_ROLE_ID)	|| Long.valueOf(roleId).equals(Constants.MANAGER_ROLE_ID)	|| Long.valueOf(roleId).equals(Constants.ADMIN_ROLE_ID)){
				query.append(",rest \n");
			}
			query.append("UNWIND tag AS e ");

			query.append("MERGE (guest)-[t:`" + RelationshipTypes.GUEST_HAS_TAG
					+ "`");
			query.append(" {__type__:'"
					+  RelationshipTypes.GUEST_HAS_TAG + "'");
			query.append(" }");
			query.append("]->(k1:Tag:_Tag {");
			query.append(getReturnQuery(eventMap, "e."));
			query.append("} )");
			if (Long.valueOf(roleId).equals(Constants.STAFF_ROLE_ID)	|| Long.valueOf(roleId).equals(Constants.MANAGER_ROLE_ID)	|| Long.valueOf(roleId).equals(Constants.ADMIN_ROLE_ID)){
				query.append("<-[t1:`REST_HAS_TAG`{__type__:'"+RelationshipTypes.REST_HAS_TAG+"'");
			    query.append(" }]-(rest)");
			   
			}
				
			
			query.append(" RETURN k1");
			

			System.out.println("Query  for creating New Relation ship \n"
					+ query);
			r = template.query(query.toString(), params);
		}

		if (existingRelationship) 
		{
			query = new StringBuilder(query.toString().substring(
					query.indexOf("MATCH"), query.indexOf("WITH")));
			query.append(query1);
			/* Code for merging relationship only */
			query.append("MERGE (guest)-[t:`" + RelationshipTypes.GUEST_HAS_TAG
					+ "`");
			query.append(" {__type__:'"
					+ RelationshipTypes.GUEST_HAS_TAG + "'");
			query.append(" }");
			query.append("]->(tag) RETURN tag");

			System.out.println("Query for merging relation ship \n" + query);
			r = template.query(query.toString(), params);
		}
		
		
		TagModelOld newTag = null;
		Iterator<Map<String, Object>> itr = r.iterator();
		while (itr.hasNext())
		{
			Set<Entry<String, Object>> entrySet = itr.next().entrySet();
			Logger.debug("entry set is " + entrySet);
			for (Map.Entry<String, Object> entry : entrySet) 
			{
				Logger.debug("entry is " + entry);
				template.postEntityCreation((Node) entry.getValue(), TagModelOld.class);
				newTag = template.convert(entry.getValue(), TagModelOld.class);
				
			}
		}

		return newTag.getGuid();
	}
	

	@Override
	public List<String> addTags(List<TagModelOld> tag) {
		// TODO Auto-generated method stub
		Map<String, Object> eventMap = new HashMap<String, Object>();
		StringBuilder query = new StringBuilder();
		String queryClause = "";
		List<String> returnList = new ArrayList<String>();

		/* Get With Clause of Event Model */
		query.append("WITH [");
		for (TagModelOld tagModel : tag) {
			returnList.add(tagModel.getGuid());
			query.append(" {");
			eventMap = UtilityMethods.entityConversionToMap(tagModel);
			eventMap = guestValidate.validateFinderParams(eventMap, TagModelOld.class);
			eventMap = this.parseCustomeDate(eventMap, tagModel);
			query.append(getReturnQuery(eventMap, ""));
			query.append(" },");
		}

		queryClause = query.substring(0, query.length() - 1);

		query.setLength(0);
		query.append(queryClause).append("] AS tag ");
		query.append("UNWIND tag AS e ");

		query.append("CREATE(tags:Tag:_Tag{");
		query.append(getReturnQuery(eventMap, "e."));
		query.append("} ) RETURN tags");

		System.out.println("Query  for creating New Relation ship \n" + query);
		template.query(query.toString(), null);
		return returnList;
	}

	// method to get tags of guest
	@Override
	public List<TagModelOld> getTagsForGuest(Map<String, Object> params) {
		String query = " MATCH (rest:Restaurant)-[hg:`HAS_GUEST`]->(g:GuestProfile{guid:'"
				+ params.get(Constants.GUID)
				+ "'})-[ght:`GUEST_HAS_TAG`]->(tag:Tag)";
		if (params.containsKey(Constants.REST_GUID)) {
			query = query + " WHERE rest.guid='"
					+ params.get(Constants.REST_GUID) + "'" ;
		}
		query = query + " RETURN tag";
		Logger.debug("match query is ------------------" + query);
		Result<Map<String, Object>> r = template.query(query, null);
		Iterator<Map<String, Object>> itr = r.iterator();
		List<TagModelOld> tagList = new ArrayList<>();
		while (itr.hasNext()) {
			Set<Entry<String, Object>> entrySet = itr.next().entrySet();
			Logger.debug("entry set is " + entrySet);
			for (Map.Entry<String, Object> entry : entrySet) {
				Logger.debug("entry is " + entry);
				template.postEntityCreation((Node) entry.getValue(), TagModelOld.class);
				TagModelOld tag = template.convert(entry.getValue(), TagModelOld.class);
				tagList.add(tag);
			}
		}

		Logger.debug("delete query executed,Result is " + r);
		return tagList;
	}

	protected StringBuilder getReturnQuery(Map<String, Object> params,
			String alias) {
		StringBuilder query = new StringBuilder();
		for (java.util.Map.Entry<String, Object> entry : params.entrySet()) {

			if (!entry.getKey().toUpperCase().equalsIgnoreCase("CLASS")) {
				if (null != entry.getValue())
					switch (entry.getKey()) {

					case Constants.UPDATED_DATE:
						query.append(getPropertyName("updatedDate")
								+ ":"
								+ (alias.equalsIgnoreCase("") ? "toInt("
										+ alias + entry.getValue() + ")"
										: alias
												+ getPropertyName("updatedDate"))
								+ ",\n");
						break;
					case Constants.CREATED_DATE:
						query.append(getPropertyName("createdDate")
								+ ":"
								+ (alias.equalsIgnoreCase("") ? "toInt("
										+ alias + entry.getValue() + ")"
										: alias
												+ getPropertyName("createdDate"))
								+ ",\n");
						break;

					default:
						query.append(getPropertyName(entry.getKey())
								+ ":"
								+ (alias.equalsIgnoreCase("") ? alias + "'"
										+ entry.getValue() + "'" : alias
										+ getPropertyName(entry.getKey()))
								+ ",\n");
						break;
					}

			}

		}
		return (new StringBuilder(query.substring(0, query.length() - 2)));
	}

	private Map<String, Object> parseCustomeDate(Map<String, Object> eventMap,
			Entity events) {
		// TODO Auto-generated method stub
		if (null != eventMap) {

			if (eventMap.containsKey(Constants.UPDATED_DATE)) {
				eventMap.put(Constants.UPDATED_DATE, ((java.util.Date) eventMap
						.get(Constants.UPDATED_DATE)).getTime());
			}

			if (eventMap.containsKey(Constants.CREATED_DATE)) {
				eventMap.put(Constants.CREATED_DATE, ((java.util.Date) eventMap
						.get(Constants.CREATED_DATE)).getTime());
			}

		}
		return eventMap;
	}

	@Override
	public List<String> removeGuestHasTagRelationship(String guid,
			List<TagModelOld> tags, String rest_guid) {
		// TODO Auto-generated method stub
		StringBuilder query = new StringBuilder();
		List<String> returnList = new ArrayList<String>();
		query.append("MATCH (tag:`Tag`)<-[rel:`"
				+ RelationshipTypes.GUEST_HAS_TAG + "` {__type__:'"
				+ "GuestTagPreferencesRelationshipModel" + "'");
		query.append(" , rest_guid:'" + (rest_guid) + "'}]-");
		query.append("(guest:`GuestProfile`)");
		query.append(" WHERE tag.guid IN [");
		for (TagModelOld tagModel : tags) {
			returnList.add(tagModel.getGuid());
			query.append("'" + tagModel.getGuid() + "',");
		}
		query = new StringBuilder(query.substring(0, query.length() - 1));
		query.append("  ]");
		query.append("  DELETE rel");
		System.out.println(" Removing Guest Tag Relationship \n" + query);
		template.query(query.toString(), null);
		return returnList;
	}

	@Override
	public List<String> removeTag(List<TagModelOld> tags) {
		// TODO Auto-generated method stub
		StringBuilder query = new StringBuilder();
		List<String> returnList = new ArrayList<String>();
		query.append("MATCH (tag:`Tag`)<-[rel:"
				+ RelationshipTypes.GUEST_HAS_TAG + " {__type__:'"
				+ "GuestTagPreferencesRelationshipModel" + "'}]");
		query.append("-(guest:`GuestProfile`)");
		query.append(" WHERE tag.guid IN [");
		for (TagModelOld tagModel : tags) {
			returnList.add(tagModel.getGuid());
			query.append("'" + tagModel.getGuid() + "',");
		}
		query = new StringBuilder(query.substring(0, query.length() - 1));
		query.append("  ]");
		query.append("  DELETE tag");
		System.out.println(" Removing Tag  \n" + query);
		template.query(query.toString(), null);
		return returnList;

	}
	
	@Override
	public List<TagModelOld> findByFields(Class type, Map<String, Object> params) {
		List<TagModelOld> tag=new ArrayList<TagModelOld>();
		StringBuilder query = getMatchClause(params);		
		
		if(params.containsKey(Constants.GUEST_GUID) && !params.containsKey(Constants.REST_GUID)){
			query.append("<-[rel:"
					+ RelationshipTypes.GUEST_HAS_TAG + " {__type__:'"+ RelationshipTypes.GUEST_HAS_TAG + "'}]");
				query.append("-(guest:`GuestProfile`)");
		}else if(params.containsKey(Constants.REST_GUID) && params.containsKey(Constants.GUEST_GUID)){
			query.append("<-[rel:"
					+ RelationshipTypes.REST_HAS_TAG + " {__type__:'"+ RelationshipTypes.REST_HAS_TAG + "'}]");
				query.append("-(rest:`Restaurant`)");
		}
			
		/* Manage URL Param*/
		query.append(getWhereClause(params));
		
		Logger.debug("query after where clause is "+query + " param map is "+params);
		query=(appendCustomWhereClause(query,params));
		Logger.debug("query after custom where clause is "+query + " param map is "+params);
		query.append(getReturnClause(params));
		Result<Map<String, Object>> result=template.query(query.toString(), params);
		tag=convertResultToList(result);
		/*Iterator<Map<String, Object>> i = result.iterator();
		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				template.postEntityCreation((Node) entry.getValue(),
						Tag.class);
			    Tag tagModel =  template.convert(entry.getValue(),Tag.class);
			    tag.add(tagModel);
			}
		}
		System.out.println( " Query First Execution--\n\n\n"+query);*/
		return tag;
	}
	@Override
	protected StringBuilder getMatchClause(Map<String, Object> params) {
		return new StringBuilder("MATCH (t:`Tag`)");
	}
	@Override
	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		query.append(" RETURN DISTINCT t");
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);// +
																			// " collect(t) as betAnswers";

	}
	private StringBuilder appendCustomWhereClause(StringBuilder query,Map<String, Object> customParams) 
	{
	    if (query.toString().contains(Constants.WHERE))
		query.append(" AND ");
	   else
		query.append(" WHERE ");
	    
		for (Entry<String, Object> entry : customParams.entrySet()) 
		{
			

			switch (entry.getKey()) 
			{

			case Constants.GUEST_GUID:
				query.append("guest.guid" +  "='" + entry.getValue() +"' AND ");
				break;
			case Constants.REST_GUID:
				query.append("rest.guid" 
			            + "='" + entry.getValue() +"' AND ");
				break;
			case Constants.TAG_GUIDS:
				if(null!=customParams.get(Constants.TAG_GUIDS))
				{
					
					query.append('(');
					String[] result = customParams.get(Constants.TAG_GUIDS).toString().split(",");
					for(String tagGuid:result)
					{
						query.append(" t.guid='"+tagGuid +"' OR");
					}
					if (query.toString().contains(Constants.OR))
						query = new StringBuilder(query.substring(0, query.length() - 2));
					query.append(" ) AND ");
					
				}	
			default:
				
				break;
			}
		}
		if(query.toString().contains("AND")){
			query = new StringBuilder(query.substring(0, query.length() - 4));
		}
		else
		{
		    query = new StringBuilder(query.substring(0, query.length() - 6));
		}
		return query;
	}

	@Override
	public List<TagModelOld> getTag(Class<TagModelOld> class1, Map<String, Object> qryParamMap) {
		// TODO Auto-generated method stub
		List<TagModelOld> tag=new ArrayList<TagModelOld>();
		StringBuilder query = getMatchClause(qryParamMap);
		query.append(getWhereClause(qryParamMap));
		query.append(getReturnClause(qryParamMap));
		Result<Map<String, Object>> result=template.query(query.toString(), qryParamMap);
		Iterator<Map<String, Object>> i = result.iterator();
		while (i.hasNext()) {
			Map<String, Object> map = i.next();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				template.postEntityCreation((Node) entry.getValue(),
						TagModelOld.class);
			    TagModelOld tagModel =  template.convert(entry.getValue(),TagModelOld.class);
			    tag.add(tagModel);
			}
		}
		System.out.println( " Query First Execution--\n\n\n"+query);
		return tag;
	}
	
	@Override	
	public String addGuestEventTagRelationship(String guestProfile_guid,
			String rest_guid, List<TagModelOld> tag) {

		Map<String, Object> eventMap = new HashMap<String, Object>();
		Result<Map<String, Object>> r = null;
		StringBuilder query = new StringBuilder();
		StringBuilder query1 = new StringBuilder();
		boolean existingRelationship = false;
		boolean creatingRelationship = false;
		String queryClause = "";
		query.append("MATCH (guest:`GuestProfile`");
		query.append("{guid:'" + guestProfile_guid + "'}").append(")\n");
		query1.append(" ,(tag:`Tag`)");
		query1.append(" WHERE tag.guid IN [");
		for (TagModelOld tagModel : tag) {
			if (tagModel.isExist()) {
				existingRelationship = true;
				query1.append("'" + tagModel.getGuid() + "',");
			}
		}
		if (existingRelationship) {
			query1 = new StringBuilder(query1.substring(0, query1.length() - 1));
		}
		query1.append("  ]");

		/* Get With Clause of Event Model */
		query.append("WITH [");
		for (TagModelOld tagModel : tag) {
			if (!tagModel.isExist()) {
				creatingRelationship = true;
				query.append(" {");
				eventMap = UtilityMethods.entityConversionToMap(tagModel);
				eventMap = guestValidate.validateFinderParams(eventMap,
						TagModelOld.class);
				eventMap = this.parseCustomeDate(eventMap, tagModel);
				query.append(getReturnQuery(eventMap, ""));
				query.append(" },");
			}
		}
		if (creatingRelationship)
		{
			/* Code for creating New Tag as well as relation ship with Tag Model */
			queryClause = query.substring(0, query.length() - 1);

			query.setLength(0);
			query.append(queryClause).append("] AS tag ,guest ");
			query.append("UNWIND tag AS e ");

			query.append("CREATE (guest)-[t:`" + RelationshipTypes.GUEST_HAS_TAG
					+ "`");
			query.append(" {__type__:'"
					+ "GuestTagPreferencesRelationshipModel" + "'");
			query.append(" , rest_guid:'" + (rest_guid) + "'}");
			query.append("]->(k1:Tag:_Tag{");
			query.append(getReturnQuery(eventMap, "e."));
			query.append("} ) RETURN k1");

			System.out.println("Query  for creating New Relation ship \n"
					+ query);
			r = template.query(query.toString(), null);
		}

		if (existingRelationship) 
		{
			query = new StringBuilder(query.toString().substring(
					query.indexOf("MATCH"), query.indexOf("WITH")));
			query.append(query1);
			/* Code for merging relationship only */
			query.append("CREATE (guest)-[t:`" + RelationshipTypes.GUEST_HAS_TAG
					+ "`");
			query.append(" {__type__:'"
					+ "GuestTagPreferencesRelationshipModel" + "'");
			query.append(" , rest_guid:'" + (rest_guid) + "'}");
			query.append("]->(tag) RETURN tag");

			System.out.println("Query for merging relation ship \n" + query);
			r = template.query(query.toString(), null);
		}
		
		
		TagModelOld newTag = null;
		Iterator<Map<String, Object>> itr = r.iterator();
		while (itr.hasNext())
		{
			Set<Entry<String, Object>> entrySet = itr.next().entrySet();
			Logger.debug("entry set is " + entrySet);
			for (Map.Entry<String, Object> entry : entrySet) 
			{
				Logger.debug("entry is " + entry);
				template.postEntityCreation((Node) entry.getValue(), TagModelOld.class);
				newTag = template.convert(entry.getValue(), TagModelOld.class);
				
			}
		}

		return newTag.getGuid();
	}

	
	    @Override
	public Map<String, Object> validateRestWithTagCount(
			Map<String, Object> params) {
		Map<String, Object> paramsReturn = new HashMap<String, Object>();
		String query = "Match (rest:Restaurant {guid:{"
				+ Constants.REST_GUID
				+ "}}) \n"
				+ "OPTIONAL MATCH (guest:GuestProfile {guid:{"
				+ Constants.GUEST_GUID
				+ "} , status:{"
				+ Constants.ACTIVE_STATUS
				+ "}})-[:HAS_GUEST]-(rest) \n"
				+ "OPTIONAL MATCH (rest)-[:REST_HAS_TAG]->(tagRest:Tag {added_by:'RESTAURANT'}) \n" + " RETURN rest,guest,tagRest";
		System.out.println("---------Query -------" + query.toString());
		Map<String, Object> map;// = new HashMap<String, Object>();

		Result<Map<String, Object>> results = template.query(query.toString(),
				params);
		Iterator<Map<String, Object>> i = results.iterator();
		List<TagModelOld> restListTag = new ArrayList<TagModelOld>();
		while (i.hasNext()) {
			map = i.next();
			if (!paramsReturn.containsKey(Constants.REST_NODE)) {
				paramsReturn.put(Constants.REST_NODE,
						(map.get("rest") != null) ? map.get("rest").toString()
								: null);
			}
			if (!paramsReturn.containsKey(Constants.GUEST_NODE)) {
				paramsReturn.put(Constants.GUEST_NODE,
						(map.get("guest") != null) ? map.get("guest")
								.toString() : null);
			}
			if (map.get("tagRest") != null) {

				TagModelOld tag = template.convert(map.get("tagRest"), TagModelOld.class);
				restListTag.add(tag);
			} else {
				restListTag = null;
			}

		}
		paramsReturn.put(Constants.REST_TAG_MODEL, restListTag);

		return paramsReturn;

	}
	    @Override
	public Map<String, Object> validateGuestWithTagCount(
			Map<String, Object> params) {
		Map<String, Object> paramsReturn = new HashMap<String, Object>();
		String query = "Match (guest:GuestProfile {guid:{"
				+ Constants.GUEST_GUID
				+ "} , status:{"
				+ Constants.ACTIVE_STATUS
				+ "}}) \n"
				+ "OPTIONAL MATCH (guest)-[r2:GUEST_HAS_TAG]->(tagGuest:Tag {added_by:'GUEST'}) \n"
				+ " RETURN guest as guestGuid,tagGuest";
		System.out.println("---------Query -------" + query.toString());
		Map<String, Object> map;// = new HashMap<String, Object>();

		Result<Map<String, Object>> results = template.query(query.toString(),
				params);
		Iterator<Map<String, Object>> i = results.iterator();
		List<TagModelOld> guestListTag = new ArrayList<TagModelOld>();
		while (i.hasNext()) {
			map = i.next();
			if (!paramsReturn.containsKey(Constants.GUEST_NODE)) {
				paramsReturn.put(Constants.GUEST_NODE,
						(map.get("guestGuid") != null) ? map.get("guestGuid")
								.toString() : null);
			}
			if (map.get("tagGuest") != null) {

				TagModelOld tag = template.convert(map.get("tagGuest"), TagModelOld.class);
				guestListTag.add(tag);
			} else {
				guestListTag = null;
			}

		}
		paramsReturn.put(Constants.GUEST_TAG_MODEL, guestListTag);
		return paramsReturn;
	}

		@Override
		public String addGuestTagRelationship(String guestProfile_guid,
				String rest_guid, List<TagModelOld> tag) {
			// TODO Auto-generated method stub
			return null;
		}

		
		
}

