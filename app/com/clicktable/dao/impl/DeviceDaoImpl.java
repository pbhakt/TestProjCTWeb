package com.clicktable.dao.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.DeviceDao;
import com.clicktable.model.Device;
import com.clicktable.util.Constants;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class DeviceDaoImpl extends GraphDBDao<Device> implements
		DeviceDao {

	public DeviceDaoImpl() {
		super();
		this.setType(Device.class);
	}
		
	
	@Override
	protected StringBuilder getMatchClause(Map<String, Object> params)
	{
	    StringBuilder query = new StringBuilder("MATCH (r:Restaurant");	       
	    if (params.containsKey(Constants.REST_GUID)) 
		{
		   query.append("{guid:{"+Constants.REST_GUID+"}}");
		} 
		
		query.append(")-[:REST_HAS_DEVICE]->(t:Device)");
		
		return query;

	}	    
	    
	    @Override
		protected StringBuilder getReturnClause(Map<String, Object> params) 
	    {
			StringBuilder query= new StringBuilder();
			query.append("RETURN t,r.guid");
			query = handleOrderBy(query, params);
			Integer pageSize = getPageSize(params);
			Integer startIndex = getIndex(params, pageSize);
			return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);
		}
	    
	    
	    @Override
	    protected List<Device> executeQuery(String query, Map<String, Object> params) 
	    {
	    	List<Device> list = new ArrayList<Device>();
			Logger.debug(query);
			params.forEach((x, y) -> Logger.debug(x + ">" + y));
			 Iterator<Map<String, Object>> results = template.query(query, params).iterator();
			 while(results.hasNext())
			 {
				 Map<String, Object> map = results.next();
				 Device device =template.convert(map.get("t"), Device.class);
				 String restGuid = map.get("r.guid").toString();
				 device.setRestaurantGuid(restGuid);
				 list.add(device);
			 }
			
			 Logger.debug("returning device"+list);
			return list;
			
	    	
	    }
	    
	
	   /* @Override
	    public Device findDeviceByGuid(String guid)
	    {
	    	StringBuilder query = new StringBuilder();
	    	query.append("MATCH (d:" + Constants.DEVICE_LABEL + ")");		
	    	query.append(" WHERE d.guid={" + Constants.GUID+ "}");
	    	query.append(" return d");
	    	
	    	Map<String, Object> params =new HashMap<String, Object>();
	    	params.put(Constants.GUID, guid);
	    	
			Iterator<Map<String, Object>> results =  template.query(query.toString(), params ).iterator();
	    	Device device = null;
	    	if(results.hasNext())
	    	{
	    		Map<String, Object> map = results.next();
	    		device =template.convert(map.get("d"), Device.class);
	    	}
	    	return device;

	    }	 */   

	    /**
	     * Method to create relationship of a restaurant with a device
	     */
	    @Override     
	    public Long addRestaurantDevice(Device device, String restaurantGuid) 
	    {
	    	String query = "MATCH (r:Restaurant {guid:{"+Constants.REST_GUID+"}}),(t:Device) WHERE t.guid={"+Constants.GUID+"} \n";
	    	query = query + "MERGE (r)-[q:REST_HAS_DEVICE{__type__:'RestaurantHasDevice'}]->(t) Return id(q)";
	    		
	    	Map<String, Object> params = new HashMap<String, Object>();
	    	params.put(Constants.REST_GUID, restaurantGuid);
	    	params.put(Constants.GUID, device.getGuid());

	    	return getResultId(executeWriteQuery(query, params ));
	    	
	    } 


      /**
       * Method to delete relationship of a restaurant with a device
       */
    @Override     
    public boolean deleteDevice(String deviceGuid) 
 	{
	        Boolean isDeleted = false;
	        try
	        {
 	    	String query = "MATCH (r:Restaurant)-[q:REST_HAS_DEVICE]->(t:Device) WHERE t.guid={"+Constants.GUID+"} DELETE q,t";
 	    	
	    	Map<String, Object> params = new HashMap<String, Object>();
	    	params.put(Constants.GUID, deviceGuid);

	    	Result<Map<String, Object>> r = executeWriteQuery(query, params);
 	        Logger.debug("query executed,Result is "+r);
 	        isDeleted = true;
	        }
	        catch(Exception e)
	        {
	            isDeleted = false;
	            e.printStackTrace();
	        }
 	        
 	        return isDeleted;
 	}



}
