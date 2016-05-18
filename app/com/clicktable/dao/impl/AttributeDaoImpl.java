package com.clicktable.dao.impl;



import java.util.HashMap;
import java.util.Map;

import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.AttributeDao;
import com.clicktable.model.Attribute;
import com.clicktable.relationshipModel.HasAttribute;
import com.clicktable.util.Constants;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class AttributeDaoImpl extends GraphDBDao<Attribute> implements
		AttributeDao {

	public AttributeDaoImpl() {
		super();
		this.setType(Attribute.class);
	}
	
	
	
	 @Override
	    public HasAttribute saveRelationModel(HasAttribute relationModel)
	    {
		return template.save(relationModel);
	    }
	
	
	@Override
	protected StringBuilder getMatchClause(Map<String, Object> params)
	{
		StringBuilder matchClause;
		if (params.containsKey(Constants.REST_GUID))
		{
			matchClause= new StringBuilder("MATCH (r:Restaurant)-[:HAS_ATTR]->(t:Attribute)<-[:PERMISSIBLE_ATTRIBUTES]-(c:Country)");
		}
		else
		{
			matchClause= new StringBuilder("MATCH (t:Attribute)<-[:PERMISSIBLE_ATTRIBUTES]-(c:Country)");
		}
		return matchClause;

	}
	
	
	
	public boolean addRestaurantAttributes(String restGuid, String[] attrGuidArr) 
	{
		Map<String, Object> params = new HashMap<String, Object>();
        params.put(Constants.REST_GUID, restGuid);
        String delQuery = "MATCH (r:Restaurant)-[rel:HAS_ATTR{__type__:'HasAttr'}]->(t:Attribute) where r.guid ={"+Constants.REST_GUID+"} DELETE rel";
        executeQuery(delQuery,params);
        String query = "MATCH (r:Restaurant {guid: {"+Constants.REST_GUID+"}}),(t:Attribute) ";
        if(attrGuidArr.length>0){
            query= query+ " WHERE t.guid IN {"+Constants.ATTR_GUID+"} ";
            params.put(Constants.ATTR_GUID, attrGuidArr);
        }
        query = query + "MERGE (r)-[:HAS_ATTR{__type__:'HasAttr'}]->(t)";
        Result<Map<String, Object>> r = executeWriteQuery(query, params );
        Logger.debug("query executed,Result is "+r);
        return true;
	}
	
	
	
	
	
	public boolean addCountryAttributes(String countryGuid, String[] attrGuidArr) 
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.COUNTRY_GUID, countryGuid);

		String query = "MATCH (r:Country {guid:{"+Constants.COUNTRY_GUID+"}}),(t:Attribute) ";
		if(attrGuidArr.length>0){
			query= query+ " WHERE t.guid IN {"+Constants.ATTR_GUID+"} ";
			params.put(Constants.ATTR_GUID, attrGuidArr);
		}	
		query = query + "MERGE (r)-[:PERMISSIBLE_ATTRIBUTES{__type__:'PermissibleAttributes'}]->(t)";

		Result<Map<String, Object>> r = executeWriteQuery(query, params );
		Logger.debug("query executed,Result is "+r);

		return true;
	}
	
	  
	    @Override
	    protected StringBuilder getWhereClause(Map<String, Object> params) 
	    {
			StringBuilder query = super.getWhereClause(params);			
			Logger.debug("query after super where clause is ");
			
			if (params.containsKey(Constants.REST_GUID)) 
			{
				addPrefix(query);
				query.append("r.guid= {" + Constants.REST_GUID+ "} AND r.country_cd=c.country_cd");
			}			
			if (params.containsKey(Constants.COUNTRY_CODE)) 
			{
				addPrefix(query);
				query.append("c.country_cd= {" + Constants.COUNTRY_CODE+ "} ");
			}
		 	
		   Logger.debug("final query in where clause is "+query);		   
		   return query;
	    }
	    
	    
	
	    
	  


 
	  
}
