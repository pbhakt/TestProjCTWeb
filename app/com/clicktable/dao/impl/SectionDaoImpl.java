package com.clicktable.dao.impl;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.SectionDao;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Section;
import com.clicktable.relationshipModel.HasSection;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class SectionDaoImpl extends GraphDBDao<Section> implements
SectionDao {

	public SectionDaoImpl() {
		super();
		this.setType(Section.class);
	}


	@Override
	public HasSection saveRelationModel(HasSection relationModel)
	{
		return template.save(relationModel);
	}

	@Override
	protected StringBuilder getMatchClause(Map<String, Object> params) {
		if (params.containsKey(Constants.REST_GUID)) {
			return new StringBuilder(
					"MATCH (r:Restaurant)-[:HAS_SECTION]->(t:Section)");
		} else {
			return super.getMatchClause(params);
		}

	}


	@Override
	protected StringBuilder getWhereClause(Map<String, Object> params) 
	{
		StringBuilder query = super.getWhereClause(params);

		Logger.debug("query after super where clause is ");

		if (params.containsKey(Constants.REST_GUID)) 
		{
			addPrefix(query);
			query.append("r.guid= {" + Constants.REST_GUID+ "}");
		}

		Logger.debug("final query in where clause is "+query);

		return query;
	}





	@Override
	public List<Section> sectionExistForRestaurant(List<String> sectionGuids, String restId) 
	{
		List<Section> list = new ArrayList<Section>();
		Map<String, Object> params = new java.util.HashMap<String, Object>();
		//params.put(Constants.GUID, tableName);
		params.put(Constants.REST_GUID, restId);		
		StringBuilder query = new StringBuilder("MATCH (r:Restaurant)-[:HAS_SECTION]->(t:Section {status:'ACTIVE'}) WHERE r.guid= {" 
				+ Constants.REST_GUID+ "} AND t."+Constants.GUID+" IN {"+sectionGuids+"} "); 		
		query.append(" RETURN t ");
		Result<Section> result = executeWriteQuery(query.toString(), params).to(Section.class);
		result.forEach(list::add);
		return list;
	}
	
	@Override
	public List<Section> countSection(String restId) 
	{
		List<Section> list = new ArrayList<Section>();
		Map<String, Object> params = new java.util.HashMap<String, Object>();
		//params.put(Constants.GUID, tableName);
		params.put(Constants.REST_GUID, restId);		
		StringBuilder query = new StringBuilder("MATCH (r:Restaurant)-[:HAS_SECTION]->(t:Section) WHERE r.guid= {" 
				+ Constants.REST_GUID+ "} AND t.status='ACTIVE'"); 		
		query.append(" RETURN t ");
		Result<Section> result = executeWriteQuery(query.toString(), params).to(Section.class);
		result.forEach(list::add);
		return list;
	}


	@Override
	public String addRestaurantSection(Section section) {
		
        Map<String,Object> params=new HashMap<String,Object>();
        params.put(Constants.REST_GUID, section.getRestID());
		String query = "MATCH (r:Restaurant {guid:{" + Constants.REST_GUID
		+ "}})\n";
		query=query+"MERGE(r)-[:HAS_SECTION]->(section:`Section`:`_Section` { ";

		if(null!=section.getName()){
		params.put(Constants.NAME, section.getName());
		query = query + "name:{" + Constants.NAME + "},";
		}
		if(null!=section.getDescription()){
        params.put(Constants.DESCRIPTION, section.getDescription());
		query = query + "description:{" + Constants.DESCRIPTION + "}";
		}
		if(null!=section.getRestID()){
	        params.put(Constants.REST_GUID, section.getRestID());
			query = query + " ,rest_guid:{" + Constants.REST_GUID + "}";
		}
		if(null!=section.getGuid()){
	        params.put(Constants.GUID, section.getGuid());
			query = query + " , guid:{" + Constants.GUID + "}";
		}
		if(null!=section.getCreatedDate()){
	        params.put(Constants.CREATED_DT, section.getCreatedDate());
			query = query + " , created_dt:{" + Constants.CREATED_DT + "}";
		}
		if(null!=section.getCreatedBy()){
	        params.put(Constants.CREATED_BY, section.getCreatedBy());
			query = query + " , created_by:{" + Constants.CREATED_BY + "}";
		}
		if(null!=section.getUpdatedDate()){
	        params.put(Constants.UPDATED_DT, section.getUpdatedDate());
			query = query + " , updated_dt:{" + Constants.UPDATED_DT + "}";
		}
		if(null!=section.getUpdatedBy()){
	        params.put(Constants.UPDATED_BY, section.getUpdatedBy());
			query = query + " , updated_by:{" + Constants.UPDATED_BY + "}";
		}
		if(null!=section.getLanguageCode()){
	        params.put(Constants.LANG_CD, section.getLanguageCode());
			query = query + " , languageCode:{" + Constants.LANG_CD + "}";
		}
		if(null!=section.getStatus()){
	        params.put(Constants.ACTIVE_STATUS, section.getStatus());
			query = query + " , status:{" + Constants.ACTIVE_STATUS + "} })";
		}
		
		query = query + " RETURN section.guid";
		Iterator<Map<String, Object>> results = executeWriteQuery(
				query.toString(), params).iterator();
		String sectionGuid = null;
		if (results.hasNext()) {
			Map<String, Object> map = results.next();
			sectionGuid= map.get("section.guid").toString();

		}
		
		return sectionGuid;
	}


	@Override
	public boolean deleteRestaurantSection(Section section) {
		// TODO Auto-generated method stub
		Map<String,Object> params=new HashMap<String,Object>();
		params.put(Constants.GUID, section.getGuid());
		params.put(Constants.REST_GUID, section.getRestID());
		
			StringBuilder query = new StringBuilder("MATCH (r:Restaurant{guid:{"
					+ Constants.REST_GUID + "}})-[q:HAS_SECTION]->(t:Section) WHERE t.guid={"
					+ Constants.GUID + "} SET t.status='DELETED' \n");
			
			Logger.debug("query is " + query);
			Result<Map<String, Object>> r = executeWriteQuery(query.toString(), params);
			Logger.debug("query executed,Result is " + r);

			return true;
		
	}


	@Override
	public String updateRestaurantSection(Section section) {
		// TODO Auto-generated method stub
		 Map<String,Object> params=new HashMap<String,Object>();
	        params.put(Constants.REST_GUID, section.getRestID());
	        params.put(Constants.GUID, section.getGuid());
			String query = "MATCH (r:Restaurant {guid:{" + Constants.REST_GUID
			+ "}}),(section:`Section` {guid:{"+Constants.GUID +"}})\n";
			
			query=query+"MERGE(r)-[:HAS_SECTION]->(section) ON MATCH SET ";

			if(null!=section.getName()){
			params.put(Constants.NAME, section.getName());
			query = query + " section.name={" + Constants.NAME + "}";
			}
			if(null!=section.getDescription()){
	        params.put(Constants.DESCRIPTION, section.getDescription());
			query = query + ", section.description={" + Constants.DESCRIPTION + "}";
			}
			if(null!=section.getRestID()){
		        params.put(Constants.REST_GUID, section.getRestID());
				query = query + ", section.rest_guid={" + Constants.REST_GUID + "}";
			}			
			
			if(null!=section.getUpdatedDate()){
		        params.put(Constants.UPDATED_DT, section.getUpdatedDate());
				query = query + ", section.updated_dt={" + Constants.UPDATED_DT + "}";
			}
			if(null!=section.getUpdatedBy()){
		        params.put(Constants.UPDATED_BY, section.getUpdatedBy());
				query = query + ", section.updated_by={" + Constants.UPDATED_BY + "}";
			}
			if(null!=section.getLanguageCode()){
		        params.put(Constants.LANG_CD, section.getLanguageCode());
				query = query + ", section.languageCode={" + Constants.LANG_CD + "}";
			}
			if(null!=section.getStatus()){
		        params.put(Constants.ACTIVE_STATUS, section.getStatus());
				query = query + ", section.status={" + Constants.ACTIVE_STATUS + "} ";
			}
			
			query = query + " RETURN section.guid";
			Iterator<Map<String, Object>> results = executeWriteQuery(
					query.toString(), params).iterator();
			String sectionGuid = null;
			if (results.hasNext()) {
				Map<String, Object> map = results.next();
				sectionGuid= map.get("section.guid").toString();

			}
			
			return sectionGuid;
	}
	
	@Override
	public Map<String,Object> validateSectionBeforeDelete(Section section){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put(Constants.START_TIME,Calendar.getInstance().getTimeInMillis());
		params.put(Constants.REST_GUID, section.getRestID());
		params.put(Constants.SECTION_GUID,section.getGuid());
		StringBuffer query=new StringBuffer();
		query.append("MATCH (rest:Restaurant {guid:{"+Constants.REST_GUID+"}})-[HAS_SECTION]->(section:Section {guid:{"+Constants.SECTION_GUID+"}}) WHERE section.status='ACTIVE' WITH section \n");
		query.append("OPTIONAL MATCH (section)-[HAS_TBL]->(table:Table {status:'ACTIVE'}) WITH  count(table) as total_section_tables,section  \n");
		/*query.append("OPTIONAL MATCH (section)-[HAS_TBL]->(table:Table {status:'ACTIVE'})-[rel:TBL_HAS_RESV]->(reservation:Reservation) \n "
				+ "where toInt(reservation.est_start_time)<=toInt("+params.get(Constants.START_TIME)+") \n"
				+ "AND "
				+ "( reservation.reservation_status <> 'CANCELLED'   "
				+ "AND reservation.reservation_status <> 'NO_SHOW' AND "
				+ "reservation.reservation_status <> 'FINISHED')  "
			    + " AND "
			    + " toInt(reservation.est_end_time) >=toInt("+params.get(Constants.START_TIME)+")" +" WITH count(rel) as table_reservation,total_section_tables,section \n") ;
		query.append("OPTIONAL MATCH (section)-[HAS_TBL]->(table:Table {status:'ACTIVE'})<-[relCal:`CALC_BLOCKED_TBL`]-(cEvent:CalenderEvent) WITH count(relCal) as total_blocked_tables,table_reservation,total_section_tables,section,table \n");*/
		query.append("RETURN total_section_tables,section");
		
		System.out.println(" Query ----"+query.toString());
		
		Map<String, Object> map = new HashMap<String, Object>();

		Result<Map<String, Object>> results = executeWriteQuery(query.toString(),
			params);
		params=new HashMap<String,Object>();
		Iterator<Map<String, Object>> i = results.iterator();	
		while (i.hasNext()) {
		        map = i.next();	
		        
		        template.postEntityCreation((Node) map.get("section"), Section.class);
		        Section sectionObject=template.convert(map.get("section"), Section.class);
		        System.out.println("---Section Guid "+sectionObject);
		        
		        String totalTable=map.get("total_section_tables").toString();
		        System.out.println(" Total Table "+totalTable);
		       /* String tablehasReservation=map.get("table_reservation").toString();
		        System.out.println(" Table Has Reservation "+tablehasReservation);
		        String blockedTable=map.get("total_blocked_tables").toString();
		        System.out.println(" Blocked Table "+blockedTable);*/
		        
		        params.put(Constants.SECTION, sectionObject.getGuid());
		        params.put(Constants.TABLE_ID, totalTable);
		       /* params.put(Constants.RESERVATION_ID, tablehasReservation);
		        params.put(Constants.BLOCKED, blockedTable);*/
		        
		        return params;
		        
		}
		
		return params;
	}
	
	
	@Override
	public List<Section> getCustomSections(Map<String,Object> params) 
    {
	  

		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:"+Restaurant.class.getSimpleName()+"{guid:{"+Constants.REST_GUID+"}})-[hs:"+RelationshipTypes.HAS_SECTION+"]->(t:"+Section.class.getSimpleName()+"{status:{"+Constants.STATUS+"}}) ");
		query.append(" WHERE toInt(t.updated_dt)>toInt({"+Constants.UPDATED_AFTER+"})");
		query.append(" RETURN t.guid,t.name");
		Logger.debug("query is================================================"+ query);
		Result<Map<String, Object>> results = template.query(query.toString(),
			params);
		Iterator<Map<String, Object>> i = results.iterator();
    	List<Section> tablesList= new ArrayList<Section>();
		while (i.hasNext()) 
		{
		    Map<String, Object> map = i.next();
		    Section t = new Section();
	    	t.setCreatedDate(null);
		    t.setStatus(null);
	    	t.setGuid((String)map.get("t.guid"));
	    	t.setName((String)map.get("t.name"));
	    	tablesList.add(t);
		}

		return tablesList;

    }


}
