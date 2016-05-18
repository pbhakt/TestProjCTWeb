package com.clicktable.dao.impl;


import static com.clicktable.util.Constants.REST_GUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.neo4j.conversion.Result;

import play.Logger;

import com.clicktable.dao.intf.GuestTagDao;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.GuestTagModel;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Tag;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;
import com.clicktable.util.UtilityMethods;

/**
 * @author p.vishwakarma
 *
 */

@org.springframework.stereotype.Service
public class GuestTagDaoImpl extends GraphDBDao<Tag> implements
		GuestTagDao {

	public GuestTagDaoImpl() {
		super();
		this.setType(Tag.class);
	}
	static int rows=0;
	static Map<String,Map<String,String>> duplicateTag=new HashMap<String,Map<String,String>>();
	
	@Override
	public String addNewTagWithGuest_ByGuest(Tag new_tag, String guest_guid) {
		
		StringBuilder query = new StringBuilder();
		query.append(" MATCH (guest:" + Constants.GUESTPROFILE_LABEL + "{guid:'"+guest_guid+"'})");
		//MERGE never used in this case
		query.append(" MERGE (guest)-[ght:" + RelationshipTypes.GUEST_HAS_TAG +"]->(t:`Tag`:`_Tag`{");
		Map<String, Object> params = addingNodeProperties(query, new_tag);
		query.append("})");
		query.append(" return t");
		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), params);
		return getSingleResultGuid(result);
	}

	@Override
	public String addExistingTagWithGuest(GuestTagModel guestTagModel) {
		String guid = null;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.TAG_GUID, guestTagModel.getTagGuid());
		params.put(Constants.GUEST_GUID, guestTagModel.getGuestGuid());
		StringBuilder query = new StringBuilder();
		query.append("MATCH (t:Tag{guid:'"+params.get(Constants.TAG_GUID)+"'})");
		query.append(" MATCH (guest:" + Constants.GUESTPROFILE_LABEL + "{guid:'"+params.get(Constants.GUEST_GUID)+"'})");
		query.append(" MERGE (t)<-[ght:" + RelationshipTypes.GUEST_HAS_TAG + "]-(guest)");
		query.append(" return t");
		List<Tag> result = executeQuery(query.toString(), params);
		if(result.size()>0){
			guid = result.get(0).getGuid();
		}
		return guid;
	
	}

	@Override
	public String addNewTagWithGuest_ByRestaurant(Tag new_tag,
			String guest_guid, String restGuid) {
		
		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:" + Constants.RESTAURANT_LABEL + "{guid:'"+restGuid+"'})");
		query.append(" MATCH (guest:" + Constants.GUESTPROFILE_LABEL + "{guid:'"+guest_guid+"'})");
		query.append(" MERGE (r)-[rht:" + RelationshipTypes.REST_HAS_TAG +"]->(t:`Tag`:`_Tag`{");
		Map<String, Object> params = addingNodeProperties(query, new_tag);
		query.append("})");
		query.append(" MERGE (guest)-[ght:" + RelationshipTypes.GUEST_HAS_TAG + "]->(t)");
		query.append(" return t");
		Result<Map<String, Object>> result = executeWriteQuery(query.toString(), params);
		return getSingleResultGuid(result);
	}
	
	@Override
	public String removeGuestTagOfCustomer_ByGuest(Map<String, Object> params) {
		String guid = null;
		StringBuilder query = new StringBuilder();
		query.append(" MATCH (guest:" + Constants.GUESTPROFILE_LABEL + "{guid:'"+params.get(Constants.GUEST_GUID)+"'})-[ght:" + RelationshipTypes.GUEST_HAS_TAG + "]->");
		query.append("(t:Tag{guid:'"+params.get(Constants.TAG_GUID_LABEL)+"'})");
		query.append(" delete ght,t return t");
		List<Tag> result = executeQuery(query.toString(), params);
		if(result.size()>0){
			guid = result.get(0).getGuid();
		}
		return guid;
	}

	@Override
	public String removeGuestTagOfCustomer_ByRestaurant(
			Map<String, Object> params) {
		System.out.println("**************************10");
		String guid = null;
		StringBuilder query = new StringBuilder();
		query.append(" MATCH (guest:" + Constants.GUESTPROFILE_LABEL + "{guid:'"+params.get(Constants.GUEST_GUID)+"'})-[ght:" + RelationshipTypes.GUEST_HAS_TAG + "]->");
		query.append("(t:Tag{guid:'"+params.get(Constants.TAG_GUID)+"'})<-[rht:" + RelationshipTypes.REST_HAS_TAG + "]-");
		query.append("(rest:" + Constants.RESTAURANT_LABEL + "{guid:'"+params.get(Constants.REST_GUID)+"'})");
		query.append(" delete ght return t");
		List<Tag> result = executeQuery(query.toString(), params);
		if(result.size()>0){
			guid = result.get(0).getGuid();
			}
		return guid;
	}
	
	@Override
	public List<Tag> findByFields(Class type, Map<String, Object> params) {
		List<Tag> tag=new ArrayList<Tag>();
		StringBuilder query = getMatchClause(params);		
		
		if(params.containsKey(Constants.GUEST_GUID) && !params.containsKey(Constants.REST_GUID)){
			query.append("<-[rel:"
					+ RelationshipTypes.GUEST_HAS_TAG + "]");
				query.append("-(guest:`GuestProfile`)");
		}else if(params.containsKey(Constants.REST_GUID) && !params.containsKey(Constants.GUEST_GUID)){
			query.append("<-[rel:"
					+ RelationshipTypes.REST_HAS_TAG  + "]");
				query.append("-(rest:`Restaurant`)");
		}else if(params.containsKey(Constants.REST_GUID) && params.containsKey(Constants.GUEST_GUID)){
			query.append(", (rest:`Restaurant`)-[rel1:"
					+ RelationshipTypes.REST_HAS_TAG  + "]");
				query.append("->(t)");
				query.append("<-[rel2:"
						+ RelationshipTypes.GUEST_HAS_TAG + "]");
					query.append("-(guest:`GuestProfile`)");
				
		}
			
		/* Manage URL Param*/
		String tagtype ="";
		if(params.containsKey(Constants.TYPE)){
		tagtype = params.get(Constants.TYPE).toString();
		params.remove(Constants.TYPE);
		}
		query.append(getWhereClause(params));
		if(!tagtype.equals("")){
		params.put(Constants.TYPE, tagtype);
		}
		Logger.debug("query after where clause is "+query + " param map is "+params);
		query=(appendCustomWhereClause(query,params));
		Logger.debug("query after custom where clause is "+query + " param map is "+params);
		query.append(getReturnClause(params));
		System.out.println("query to tag :  :: : : : :"+query);
		Result<Map<String, Object>> result=template.query(query.toString(), params);
		tag=convertResultToList(result);
		
		return tag;
	}
	@Override
	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		if(params.containsKey(Constants.DISTINCT)){
			query.append(" RETURN t");
		}else{
		query.append(" RETURN DISTINCT t");
		}
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
				if(!customParams.containsKey(Constants.REST_GUID)){
					query.append("NOT (t)-[:REST_HAS_TAG]-() AND");
				}
				break;
			case Constants.REST_GUID:
				query.append("rest.guid" 
			            + "='" + entry.getValue() +"' AND ");
				break;
			case Constants.TYPE:
				query.append('(');
				String[] result1 = customParams.get(Constants.TYPE).toString().split(",");
				for(String tagType:result1)
				{
					query.append(" t.type='"+tagType +"' OR");
				}
				if (query.toString().contains(Constants.OR))
					query = new StringBuilder(query.substring(0, query.length() - 2));
				query.append(" ) AND ");
				break;
			case Constants.ADDED_BY:
				query.append("t.added_by" 
			            + "='" + entry.getValue() +"' AND ");
				break;
			case Constants.TAG_NAME:
				if(null!=customParams.get(Constants.TAG_NAME))
				{
					
					query.append('(');
					String[] result2 = customParams.get(Constants.TAG_NAME).toString().split(",");
					for(String tagName:result2)
					{
						query.append(" t.name=~'(?i)"+tagName +".*' OR");
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
	public void addNewEventandOfferGuestTagWithGuest_ByRestaurant(
			List<Tag> tagslist, String guestGuid, String restGuid) {
		
		createMultiple(tagslist);
		
		StringBuilder query = new StringBuilder();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(REST_GUID, restGuid);
		params.put(Constants.GUEST_GUID, guestGuid);
		/* Get Restaurant Match Clause */
		query.append("MATCH (restaurant:`");
		query = getGenericMatchClause(Restaurant.class, query).append('`').append("{guid:{" + REST_GUID + "}}").append(')');

		/* Get Event Match Clause */
		query.append(" , (guest:`");
		query = getGenericMatchClause(GuestProfile.class, query).append('`').append("{guid:{" + Constants.GUEST_GUID + "}}").append(')');

		query.append(" WITH {" + Constants.TAG_GUID_LABEL + "} as guestTagCollection, ");
		params.put(Constants.TAG_GUID_LABEL, getGuids(tagslist));

		
		query.append("restaurant as r, guest as g ");

		
		query.append("UNWIND guestTagCollection as guestTags ");
		query.append("MATCH (gTags:`Tag` {guid : guestTags}) ");
		query.append("MERGE (r)-[:REST_HAS_TAG]->(gTags) ");
		query.append("MERGE (g)-[:GUEST_HAS_TAG {rest_guid:'" + params.get(REST_GUID) + "'}]->(gTags) ");
		
					
		Logger.debug("query is ="+query);
		executeWriteQuery(query.toString(), params);
	
	}

	@Override
	public void addExistingEventandOfferGuestTagWithGuest_ByRestaurant(
			List<String> existing_tag_guid_list, String guestGuid,
			String restGuid) {
		Map<String, Object> params = new HashMap<String, Object>();	
		params.put(Constants.GUEST_GUID, guestGuid);
		params.put(Constants.REST_GUID, restGuid);
		
			String query = "MATCH (t:Tag),  (g:GuestProfile{guid:'"+ guestGuid +"'})";
			query = query +" WHERE t.guid IN [";
			for (String guest_tag_guid : existing_tag_guid_list) {
				query = query +"'" + guest_tag_guid + "',";
			}
			query = query.substring(0, query.length() - 1)+"] ";
	   		query = query + "CREATE (g)-[ght:"+ RelationshipTypes.GUEST_HAS_TAG +" {rest_guid:'" + (restGuid) + "'}]->(t) RETURN t";
			
	   		
			executeQuery(query, params);
		
	}

	@Override
	public void addExistingRestandGuestEventandOfferGuestTagWithGuest_ByRestaurant(
			List<String> existing_tag_guid_list, String guestGuid,
			String restGuid) {

		Map<String, Object> params = new HashMap<String, Object>();

		params.put(Constants.GUEST_GUID, guestGuid);
		params.put(Constants.REST_GUID, restGuid);

		String query = "MATCH (t:Tag),  (g:GuestProfile{guid:'" + guestGuid
				+ "'})";
		query = query + " WHERE t.guid IN [";
		for (String guest_tag_guid : existing_tag_guid_list) {
			query = query + "'" + guest_tag_guid + "',";
		}
		query = query.substring(0, query.length() - 1) + "] ";
		query = query + "CREATE (g)-[ght:" + RelationshipTypes.GUEST_HAS_TAG
				+ " {rest_guid:'" + (restGuid) + "'}]->(t) RETURN t";

		executeQuery(query, params);

	}
	

	
	@Override
	public void mergingTag(	List<Tag> existing_tag_guid_list) {
		    Map<String,Object> map=new HashMap<String,Object>();
		    //boolean isDuplicate=false;
			
			/* Get All Relationship with Restaurant(S) and Guest(S) */
			for(Tag tag:existing_tag_guid_list){				
				System.out.println(" Tag Clone ---"+tag.getGuid());
                rows++;
				String query= " MATCH (t:Tag {guid:'"+tag.getGuid()+"'})<-[gt:GUEST_HAS_TAG]-(g:GuestProfile) WHERE g.first_name <> 'UNKNOWN GUEST'"					     				      
					      + " RETURN DISTINCT g.guid,t,count(gt) as totalRelationship,gt.rest_guid ORDER BY gt.rest_guid";
				
				System.out.println("First Query "+ query.toString());
				
				Result<Map<String, Object>> resultsProcess = template.query(query.toString(), null);
				Iterator<Map<String, Object>> iteratorProcess = resultsProcess.iterator();
				
				while (iteratorProcess.hasNext()) {
					map = iteratorProcess.next();
					String restaurantModel=(null!=map.get("gt.rest_guid"))?map.get("gt.rest_guid").toString() :null;
					Tag tagObject=template.convert(map.get("t"),Tag.class);
					String tagModel=tagObject.getGuid().toString();
					String guestModel=map.get("g.guid").toString();
					Tag newTag=new Tag();
					int totalRelationship=((Integer)map.get("totalRelationship")).intValue();
					System.out.println(" Guest Guid ---------------                TAG GUID    ------------                  TOTAL RELATIONSHIP   ----------                  REST GUID    \n");
			        System.out.println(guestModel+"            "+tagModel+"                 "+totalRelationship+"                               "+restaurantModel +"\n");		
					if(null!=restaurantModel){
					if(null!=duplicateTag.get(restaurantModel)){
						Map<String,String> tagMap=(Map<String, String>) duplicateTag.get(restaurantModel);
						if(tagMap.containsKey(tagObject.getName())){
							newTag.setGuid(tagMap.get(tagObject.getName()).toString());	
						}else{							
							newTag.setGuid(UtilityMethods.generateCtId());
							newTag.setAddedBy(tag.getAddedBy());
							//newTag.setCreatedBy("RESTAURANT");
							newTag.setCreatedDate(tag.getCreatedDate());
							//newTag.setUpdatedBy(tag.getUpdatedBy());
							newTag.setUpdatedDate(tag.getUpdatedDate());
							newTag.setLanguageCode(tag.getLanguageCode());
							newTag.setName(tag.getName());
							newTag.setStatus(tag.getStatus());
							newTag.setIs_merged("true");
							if(tag.getType().equalsIgnoreCase(Constants.EVENT)){
								newTag.setType(Constants.EVENT);
							}else if(tag.getType().equalsIgnoreCase(Constants.OFFER)){
								newTag.setType(Constants.OFFER);
							}else{
								newTag.setType(Constants.TAG_PREFERENCES);
							}
							tagMap.put(newTag.getName(), newTag.getGuid());								
							duplicateTag.put(restaurantModel,tagMap); 
							template.save(newTag);	
						}
					}else{
						Map<String,String> tagMap=new HashMap<String,String>();
						newTag.setGuid(UtilityMethods.generateCtId());
						newTag.setAddedBy(tag.getAddedBy());
						//newTag.setCreatedBy(tag.getCreatedBy());
						newTag.setCreatedDate(tag.getCreatedDate());
						//newTag.setUpdatedBy(tag.getUpdatedBy());
						newTag.setUpdatedDate(tag.getUpdatedDate());
						newTag.setLanguageCode(tag.getLanguageCode());
						newTag.setName(tag.getName());
						newTag.setStatus(tag.getStatus());
						newTag.setIs_merged("true");
						if(tag.getType().equalsIgnoreCase(Constants.EVENT)){
							newTag.setType(Constants.EVENT);
						}else if(tag.getType().equalsIgnoreCase(Constants.OFFER)){
							newTag.setType(Constants.OFFER);
						}else{
							newTag.setType(Constants.TAG_PREFERENCES);
						}
						template.save(newTag);
						tagMap.put(newTag.getName(), newTag.getGuid());
						duplicateTag.put(restaurantModel,tagMap); 
					}
						
					
				
					query= "MATCH (r:Restaurant {guid:'"+restaurantModel+"'}),(t:Tag {guid:'"+newTag.getGuid()+"'}),(g:GuestProfile {guid:'"+guestModel+"'}) \n";
					
					for(int i1=0;i1<totalRelationship;i1++){
							 query=query+(" CREATE (t)<-[:`" + RelationshipTypes.GUEST_HAS_TAG +"` {__type__:'"
									       +RelationshipTypes.GUEST_HAS_TAG + "',rest_guid:'"+restaurantModel+"'}]-(g)   \n");
						}
					query=query+  " MERGE (r)-[rt:`" + RelationshipTypes.REST_HAS_TAG +"` {__type__:'"
							        +   RelationshipTypes.REST_HAS_TAG + "',rest_guid:'"+restaurantModel+"'}]->(t) ";
							       
					System.out.println(" Query Merged ! \n"+ query.toString());					
					template.query(query.toString(), null);					
					System.out.println(" Total Rows Processes "+ rows);
					}
					
				}
			 }
	}
	
	@Override
	public List<Tag> getTagMergingTest(List<String> tag){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("tags", tag);
		
		/*String query=     "MATCH (t:Tag)<-[gt:GUEST_HAS_TAG]-(g:GuestProfile) WHERE t.guid IN {tags} AND g.first_name <> 'UNKNOWN GUEST'"					     				      
			      + "RETURN DISTINCT t";
		Result<Map<String, Object>> result=template.query(query.toString(), params);*/
		
		/*String query=" Match(n:GuestProfile {reason:'Select'}) Set n.reason=NULL \n";
		System.out.println(query);
		template.query(query,null);
		query=" Match (guest:GuestProfile) where HAS(guest.last_name) SET guest.first_name=guest.first_name+' '+guest.last_name,"
				+ "guest.last_name=NULL Return guest \n";
		System.out.println(query);
		template.query(query,null);
		query=" Match (guest:GuestProfile)-[rel:HAS_GUEST]-(r:Restaurant) SET "     
								+" rel.first_name=guest.first_name,"    
								+" rel.is_vip=guest.is_vip,"
								+" rel.reason=guest.reason,"
								+" rel.gender=guest.gender, "
								+" rel.dnd_mobile=guest.dnd_mobile,"
								+" rel.dnd_email=guest.dnd_email,"
								+" rel.updated_dt=guest.updated_dt,"
								+" rel.status=guest.status,"
								+" rel.dob=guest.dob,"
								+" rel.anniversary=guest.anniversary, "   
								+" rel.corporate_guid=guest.corporate_guid,"
								+" rel.status=guest.status,"
								+" rel.search_params=REPLACE(guest.first_name,' ','')+guest.mobile,"
								+" rel.last_name=NULL"
								+" RETURN guest \n";
		System.out.println(query);
		template.query(query,null);*/
		
		
		
		String query=" Match ()-[r:REST_HAS_TAG]-(t:Tag) DELETE r \n";
		System.out.println(query);
		template.query(query,null);
		query="MATCH (t:Tag)<-[gt:GUEST_HAS_TAG]-(g:GuestProfile {mobile:'0000000000'}) DELETE gt ";
		System.out.println(query);
		template.query(query,null);
		query= "MATCH (t:Tag ) WHERE NOT (t)-[]-()  DELETE t \n";
		System.out.println(query);
		template.query(query,null);
		query= "MATCH (t:Tag )<-[gt:GUEST_HAS_TAG]-(g:GuestProfile) Where gt.rest_guid is NULL DELETE gt, t";
		System.out.println(query);
		template.query(query,null);
		

		List<String> eventCategoryList = UtilityMethods.getEnumValues(Constants.EVENT_MODULE, Constants.EVENTCATEGORY);
		List<String> offerCategoryList = UtilityMethods.getEnumValues(Constants.EVENT_MODULE, Constants.OFFERCATEGORY);
		
		query= "MATCH (t:Tag) WHERE t.type='Events And Offers' AND t.name IN [";
		for(String type : eventCategoryList){
			query = query + "'"+ type +"',";
		}
		if(query.contains(","))
			query = query.substring(0, query.length()-1);
		query = query + "] SET t.type='EVENT' RETURN t";
		template.query(query,null);
		System.out.println(" EVENT Query \n" + query.toString());
		
		query= "MATCH (t:Tag) WHERE t.type='Events And Offers' AND t.name IN [";
		for(String type : offerCategoryList){
			query = query + "'"+ type +"',";
		}
		if(query.contains(","))
			query = query.substring(0, query.length()-1);
		query = query + "] SET t.type='OFFER'  RETURN t";
		System.out.println(" OFFER Query \n" + query.toString());
		template.query(query,null);
		
		query= "MATCH (t:Tag) WHERE t.is_merged IS NULL  Return t";
		Result<Map<String, Object>> result=template.query(query.toString(), params);	
		
		
		return convertResultToList(result);
	}
	
	@Override
	public void cleanup(){
		String query="MATCH (t:Tag)-[r]-() where t.is_merged is NULL DELETE r,t \n";
		 template.query(query.toString(), null);
		 System.out.println(" Ouery Deleted ! \n" + query.toString());
	}
}
