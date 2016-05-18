package com.clicktable.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.GuestBookDao;
import com.clicktable.model.GuestBook;
import com.clicktable.model.GuestProfile;
import com.clicktable.util.Constants;

@Service
public class GuestBookDaoImpl extends GraphDBDao<GuestBook> implements GuestBookDao {
	
    public GuestBookDaoImpl() {
		super();
		this.setType(GuestProfile.class);
	}
    
    
 
    @Override
    protected List<GuestBook> executeQuery(String query, Map<String, Object> params) 
    {
    	List<GuestBook> list = new ArrayList<GuestBook>();
		Logger.debug(query);
		params.forEach((x, y) -> Logger.debug(x + ">" + y));
		 Iterator<Map<String, Object>> results = executeWriteQuery(query, params).iterator();
		 while(results.hasNext())
		 {
			 Map<String, Object> map = results.next();
			 GuestBook profile =template.convert(map.get("t"), GuestBook.class);
			 list.add(profile);
		 }
		return list;
		
    }
    
	@Override
	protected StringBuilder getMatchClause(Map<String, Object> params) 
	{
		if (params.containsKey(Constants.REST_GUID)) 
		{
			return new StringBuilder(
					"MATCH (r:Restaurant {"+Constants.GUID+" :{" + Constants.REST_GUID+ "}})-[:HAS_GUEST]->(t:GuestProfile) "
							+ "WITH t MATCH (t)-[a:GUEST_HAS_RESV]->(m:Reservation)");
		}

		return super.getMatchClause(params);
		

	}
	
	@Override
	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query= new StringBuilder();
		if (params.containsKey(Constants.REST_GUID)) 
		{
			query.append("RETURN t,m");
		}else
			query.append("RETURN t");
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);
	}
	    
   
    @Override
    protected StringBuilder getWhereClause(Map<String, Object> params) 
    {


		StringBuilder query = super.getWhereClause(params);	
		if (params.containsKey(Constants.REST_GUID)) 
		{
			addPrefix(query);
			query.append("a.rest_guid= {" + Constants.REST_GUID+ "} ");
		}
	  
		
		//if freeSearch parameter comes in param string then it searches for first name,last name,email and mobile no having given value
	   	if(params.containsKey(Constants.FREE_SEARCH))
	   	{
	   	    String regularExpString = Constants.PRE_LIKE_STRING+params.get(Constants.FREE_SEARCH)+Constants.POST_LIKE_STRING;
	   	    params.put(Constants.FREE_SEARCH, regularExpString );
	   	    query = applyFreeSearch(Constants.FREE_SEARCH, query);
	   	}
	   	
	   	
	          //if firstNameStartsWith parameter comes in param string then it searches for first name that starts with given value
	 /*  	if(params.containsKey(Constants.FIRST_NAME_STARTS_WITH))
	   	{
	   	    String regularExpString = Constants.PRE_START_WITH_STRING+params.get(Constants.FIRST_NAME_STARTS_WITH)+Constants.POST_LIKE_STRING;
	   	    params.put(Constants.FIRST_NAME_STARTS_WITH, regularExpString );
	   	    query = applyNameStartsWithQuery(Constants.FIRST_NAME_STARTS_WITH, query, Constants.FIRST_NAME);
	   	}
	   	
	   	
	   	  //if lastNameStartsWith parameter comes in param string then it searches for last name that starts with given value
	   	if(params.containsKey(Constants.LAST_NAME_STARTS_WITH))
	   	{
	   	    String regularExpString = Constants.PRE_START_WITH_STRING+params.get(Constants.LAST_NAME_STARTS_WITH)+Constants.POST_LIKE_STRING;
	   	    params.put(Constants.LAST_NAME_STARTS_WITH, regularExpString );
	   	    query = applyNameStartsWithQuery(Constants.LAST_NAME_STARTS_WITH, query, Constants.LAST_NAME);
	   	}*/
	   		   
	   
	  
	return query;
    }
    
   

    
    /**
     * private method that creates query for like parameters first name,last name,email and mobile no
     * @param likeValue
     * @param query
     * @return
     */
   /* private StringBuilder applyLikeQuery(String likeValue , StringBuilder query)
    {
    	if(query.toString().contains(Constants.WHERE))
    	{
    		if(likeValue.equals(Constants.FIRST_NAME_LIKE))
    		{
    			query.append(" AND t.first_name=~{"+likeValue+"}");   
    		}
    		if(likeValue.equals(Constants.LAST_NAME_LIKE))
    		{
    			query.append(" AND t.last_name=~{"+likeValue+"}");   
    		}
    		if(likeValue.equals(Constants.EMAIL_LIKE))
    		{
    			query.append(" AND t.email_id=~{"+likeValue+"}");   
    		}
    		if(likeValue.equals(Constants.MOBILE_NO_LIKE))
    		{
    			query.append(" AND t.mobile=~{"+likeValue+"}");   
    		}
    	}
    	else
    	{
    		if(likeValue.equals(Constants.FIRST_NAME_LIKE))
    		{
    			query.append(" WHERE t.first_name=~{"+likeValue+"}");
    		}
    		if(likeValue.equals(Constants.LAST_NAME_LIKE))
    		{
    			query.append(" WHERE t.last_name=~{"+likeValue+"}");   
    		}
    		if(likeValue.equals(Constants.EMAIL_LIKE))
    		{
    			query.append(" WHERE t.email_id=~{"+likeValue+"}");   
    		}
    		if(likeValue.equals(Constants.MOBILE_NO_LIKE))
    		{
    			query.append(" WHERE t.mobile=~{"+likeValue+"}");   
    		}
    	}

    	return query;
    }*/
            
    /**
     * private method that creates query for first name or last name starts with  
     * @param likeValue
     * @param query
     * @return
     */

  /*  private StringBuilder applyNameStartsWithQuery(String likeValue , StringBuilder query , String nameField)
    {
    	if(query.toString().contains(Constants.WHERE))
    	{
    		query.append(" AND t."+nameField+"=~{"+likeValue+"} ");
    	}
    	else
    	{
    		query.append(" WHERE t."+nameField+"=~{"+likeValue+"} ");
    	}

    	return query;
    }*/
               
            
              

    /**
     * private method that creates query for like parameters first name,last name,email and mobile no
     * @param likeValue
     * @param query
     * @return
     */
    private StringBuilder applyFreeSearch(String likeValue , StringBuilder query)
    {
    	if(query.toString().contains(Constants.WHERE))
    	{
    		query.append(" AND (t.first_name=~{"+likeValue+"} OR t.last_name=~{"+likeValue+"} OR t.mobile=~{"+likeValue+"} OR t.email_id=~{"+likeValue+"})");
    	}
    	else
    	{
    		query.append(" WHERE (t.first_name=~{"+likeValue+"} OR t.last_name=~{"+likeValue+"} OR t.mobile=~{"+likeValue+"} OR t.email_id=~{"+likeValue+"})");
    	}

    	return query;
    }

        
        
    /**
     * private method that creates equal query for dates like dob,anniversary etc
     * @param likeValue
     * @param query
     * @return
     */
   /* private StringBuilder applyEqualQueryForDate(String likeValue , StringBuilder query)
    {
    	if(query.toString().contains(Constants.WHERE))
    	{
    		play.Logger.debug("before query is "+query);
    		query.append(" AND ( toInt({"+ likeValue +"})<toInt(t."+likeValue+")) AND (toInt(t."+likeValue+")<(toInt({"+ likeValue +"})+(24*60*60*1000-1))) ");
    		play.Logger.debug("after query is "+query);
    	}
    	else
    	{
    		query.append(" WHERE ( toInt({"+ likeValue +"})<toInt(t."+likeValue+")) AND (toInt(t."+likeValue+")<(toInt({"+ likeValue +"})+(24*60*60*1000-1))) ");

    	}

    	return query;
    }*/
                
            
                
                
                
            
}

