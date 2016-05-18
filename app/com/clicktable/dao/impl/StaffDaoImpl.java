package com.clicktable.dao.impl;

import com.clicktable.dao.intf.StaffDao;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Staff;
import com.clicktable.model.StaffInfo;
import com.clicktable.relationshipModel.RestaurantHasUser;
import com.clicktable.util.Constants;
import org.neo4j.graphdb.Relationship;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;
import play.Logger;

import java.util.*;


@Service
public class StaffDaoImpl extends GraphDBDao<Staff> implements StaffDao
{
	public static final Logger.ALogger log = Logger.of(StaffDaoImpl.class);

	public StaffDaoImpl() {
		super();
		this.setType(Staff.class);
	}

	@Override
	public RestaurantHasUser saveRelationModel(RestaurantHasUser relationModel)
	{
		return template.save(relationModel);
	}
	
	
	
	@Override
	protected StringBuilder getMatchClause(Map<String, Object> params) {
		return new StringBuilder("MATCH (t:Staff)-[shi:STAFF_HAS_INFO]->(si:StaffInfo) ");
	}



	@Override
	protected StringBuilder getWhereClause(Map<String, Object> params) 
	{
		StringBuilder query = super.getWhereClause(params);

		//if freeSearch parameter comes in param string then it searches for first name,last name,email and mobile no having given value
		if(params.containsKey(Constants.FREE_SEARCH))
		{
			String regularExpString = Constants.PRE_LIKE_STRING+params.get(Constants.FREE_SEARCH)+Constants.POST_LIKE_STRING;
			params.put(Constants.FREE_SEARCH, regularExpString );
			query = applyFreeSearch(Constants.FREE_SEARCH, query);
		}
		return query;
	}


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
			query.append(" AND (t.first_name=~{"+likeValue+"} OR t.last_name=~{"+likeValue+"} OR t.mobile=~{"+likeValue+"} OR t.email=~{"+likeValue+"})");
		}
		else
		{
			query.append(" WHERE (t.first_name=~{"+likeValue+"} OR t.last_name=~{"+likeValue+"} OR t.mobile=~{"+likeValue+"} OR t.email=~{"+likeValue+"})");
		}

		return query;
	}
	
	
	
	
	@Override
	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();

		query.append("RETURN DISTINCT t,si");
		query = handleOrderBy(query, params);
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);
	}
	
	
	
	
	@Override
	protected List<Staff> executeQuery(String query,
			Map<String, Object> params) {
		List<Staff> list = new ArrayList<Staff>();
		log.debug(query);
		params.forEach((x, y) -> log.debug(x + ">" + y));
		Iterator<Map<String, Object>> results = template.query(query, params)
				.iterator();
		Staff staff = null;
		StaffInfo staffInfo = null;
		while (results.hasNext()) {
			Map<String, Object> map = results.next();
			log.debug("map is " + map + " param is ----------------------"
					+ params);

			staff = template.convert(map.get("t"), Staff.class);
			staffInfo = template.convert(map.get("si"), StaffInfo.class);
			staff.setIs_otp_require(staffInfo.isIs_otp_require());
			if(staffInfo.getLoginHistory() != null && staffInfo.getLoginHistory().size() > 0){
				String lastLogin = staffInfo.getLoginHistory().get(staffInfo.getLoginHistory().size() - 1);
				Date lastKloginDate = new Date(Long.valueOf(lastLogin));
				staff.setLastLogin(lastKloginDate);
			}
			
			list.add(staff);
		}
		return list;

	}



	/**
	 * Method to create relationship of a restaurant with staff
	 */
	@Override     
	public Long addRestaurantStaff(String restGuid, Staff staff) 
	{
		Long id = 0L;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.REST_GUID, restGuid);
		params.put(Constants.GUID, staff.getGuid());
		
		String query = "MATCH (r:Restaurant {guid:{"+Constants.REST_GUID+"}}),(t:Staff) WHERE t.guid={"+Constants.GUID+"} \n";
		query = query + "MERGE (r)-[rel:REST_HAS_USER{__type__:'RestaurantHasUser',isActive:true,";
		if(staff.getRoleId().equals(Constants.SERVER_ROLE_ID))
		{
			query = query +"isServer:true,isUser:false}]->(t) return rel";
		}
		else if(staff.getRoleId().equals(Constants.CUSTOMER_ROLE_ID))
		{
			query = query +"isServer:false,isUser:true}]->(t) return rel";
		}
		else
		{
			query = query +"isServer:false,isUser:false}]->(t) return rel";
		}

		Result<Map<String, Object>> result = executeWriteQuery(query, params);//template.query(query, null);
		log.debug("query executed,Result is " + result);
		Iterator<Map<String, Object>> itr = result.iterator();
		while(itr.hasNext())
		{
			for(Map.Entry<String, Object> entry  : ((Map<String, Object>) itr.next()).entrySet())
			{
				Relationship rel = (Relationship) entry.getValue();
				log.debug("relationship is " + rel.getId());
				id = rel.getId();  
			}
		}

		return id;
	}


	@Override
	public Staff updateLastLoginInfo(Staff staff) 
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.LAST_LOGIN, staff.getLastLogin().getTime());
		params.put(Constants.GUID, staff.getGuid());		
		params.put(Constants.OPT_GEN_TIME, staff.getOtp_generated_time());
		
		
		String query = "MATCH (t:Staff{guid:{"+Constants.GUID+"}}) SET t.last_login={ "+Constants.LAST_LOGIN+"} "
				+ " , t.otp_generated_time={ "+Constants.OPT_GEN_TIME+"}"
				+ "RETURN t";

		log.debug("query is " + query);
		Result<Staff> r = executeWriteQuery(query, params).to(type);
		return r.singleOrNull();

	}
	
	@Override
	public Staff updateStaff(Staff staff) 
	{
		StringBuffer query=new StringBuffer();
		
		Map<String , Object> params = new HashMap<>();
		params.put(Constants.GUID, staff.getGuid());
		
		query.append("MATCH (t:Staff{guid:{"+Constants.GUID+"}}) SET");
		
		/*if(null!=staff.getCreatedBy()){
			query.append(" t.created_by='"+staff.getCreatedBy()+"'");
		}*/
		if(null!=staff.getRoleId()){
			params.put(Constants.ROLE_ID, staff.getRoleId());
			query.append(" t.roleId={"+Constants.ROLE_ID+"}");
		}
		/*if(null!=staff.getCreatedDate()){
			query.append(" , t.updated_dt=toInt("+staff.getUpdatedDate().getTime()+")");
		}*/
		if(null!=staff.getEmail()){
			params.put(Constants.EMAIL, staff.getEmail());
			query.append(" , t.email={"+Constants.EMAIL+"}");
		}
		if(null!=staff.getFirstName()){
			params.put(Constants.FIRST_NAME, staff.getFirstName());
			query.append(" , t.first_name={"+Constants.FIRST_NAME+"}");
		}
		if(null!=staff.getHref()){
			params.put("href", staff.getHref());
			query.append(" , t.href={href}");
		}
		if(null!=staff.getLanguageCode()){
			params.put(Constants.LANGUAGE_CODE, staff.getLanguageCode());
			query.append(" , t.lang_cd={"+Constants.LANGUAGE_CODE+"}");
		}
		if(null!=staff.getLastLogin()){
			params.put(Constants.LAST_LOGIN, staff.getLastLogin().getTime());
			query.append(" , t.last_login=toInt({"+Constants.LAST_LOGIN+"})");
		}
		if(null!=staff.getLastName()){
			params.put(Constants.LAST_NAME, staff.getLastName());
			query.append(" , t.last_name={"+Constants.LAST_NAME+"}");
		}
		if(null!=staff.getMobileNo()){
			params.put(Constants.MOBILE, staff.getMobileNo());
			query.append(" , t.mobile={"+Constants.MOBILE+"}");
		}
		if(null!=staff.getNickname()){
			params.put("nickname", staff.getNickname());
			query.append(" , t.nickname={nickname}");
		}  
		if(null!=staff.getOtp_generated_time()){
			params.put("otp_generated_time", staff.getOtp_generated_time().getTime());
			query.append(" , t.otp_generated_time=toInt({otp_generated_time})");
		}
		if(null!=staff.getOtpToken()){
			params.put("otpToken", staff.getOtpToken());
			query.append(" , t.otpToken={otpToken}");
		}
		
		if(null!=staff.getRestaurantGuid()){
			params.put(Constants.REST_GUID, staff.getRestaurantGuid());
			query.append(" , t.rest_guid={"+Constants.REST_GUID+"}");
		}
		if(null!=staff.getStatus()){
			params.put(Constants.STATUS, staff.getStatus());
			query.append(" , t.status={"+Constants.STATUS+"}");
		}
		if(null!=staff.getUpdatedBy()){
			params.put(Constants.UPDATED_BY, staff.getUpdatedBy());
			query.append(" , t.updated_by={"+Constants.UPDATED_BY+"}");
		}
		if(null!=staff.getUpdatedDate()){
			params.put(Constants.UPDATED_DT, staff.getUpdatedDate().getTime());
			query.append(" , t.updated_dt=toInt({"+Constants.UPDATED_DT+"})");
		}
		
		params.put("otp_require", staff.isIs_otp_require());
		query.append(" , t.otp_require={otp_require}");

		log.debug("query is " + query);
		
		template.query(query.toString(), params);
		return staff;
	}
	
	@Override
	public List<StaffInfo> getLogOutUsersList(Map<String, Object> paramMap) {
		
		String query = "MATCH (t:Staff{status:'ACTIVE'})-[sh:STAFF_HAS_INFO]->(si:StaffInfo) WHERE HAS(si.current_login_time) ";
		if(paramMap.containsKey(Constants.STAFF_GUID)){
			query = query + " AND t.guid = {"+Constants.STAFF_GUID +"}";
		}
		if(paramMap.containsKey(Constants.REST_GUID)){
		query = query + " AND t.rest_guid={"+Constants.REST_GUID+"} \n";
		}
				
		query = query + "RETURN si";
		
		List<StaffInfo> staffInfoList = new ArrayList<StaffInfo>();
		
		Iterator<Map<String, Object>> itr = executeWriteQuery(query, paramMap).iterator();
		while(itr.hasNext())
		{
			Map<String,Object> map = itr.next();
			StaffInfo staffInfo = (map.get("si") == null) ? null : template.convert(map.get("si"),StaffInfo.class);
			staffInfoList.add(staffInfo);
		}

		return staffInfoList;
	}

	@Override
	public Map<String,Object> findActiveStaffWithActiveRest(Map<String, Object> paramMap) {


		String query = "MATCH ";
		boolean isCtAdmin = false;
		if(paramMap.containsKey(Constants.TOKEN))
		{
			String token = paramMap.get(Constants.TOKEN).toString();
			if(token.endsWith("1"))
			{
				isCtAdmin = true;
			}
		}
		
		if(!isCtAdmin)
		{
			query = query + "(r:Restaurant {status:'"+Constants.ACTIVE_STATUS+"'})-[rhu:REST_HAS_USER]-> ";
		}
		
		query = query + "(t:Staff)-[shi:STAFF_HAS_INFO]->(si:StaffInfo) ";

		String appendString = null;
		for(Map.Entry<String, Object> mapObject : paramMap.entrySet()){

			if(mapObject.getKey().equals(Constants.EMAIL)){
				appendString = appendString == null ? " WHERE t.email={"+Constants.EMAIL+"}" : appendString + " AND t.email={"+Constants.EMAIL+"}";
			}
			else if(mapObject.getKey().equals(Constants.GUID)){
				appendString = appendString == null ? " WHERE t.guid={"+Constants.GUID+"}" : appendString + " AND t.guid={"+Constants.GUID+"}";
			}

			else if(mapObject.getKey().equals(Constants.TOKEN)){
				appendString = appendString == null ? " WHERE si.token={"+Constants.TOKEN+"}" : appendString +  " AND si.token={"+Constants.TOKEN+"}";
			}

		}

		if(!paramMap.containsKey("include_inactive")){
			appendString = appendString == null ?  " WHERE t.status='ACTIVE'" : appendString +  " AND t.status='ACTIVE'";
		}

		query = appendString == null ? query : query + appendString;

		query = query + " RETURN DISTINCT t, si";
		
		if(!isCtAdmin)
		{
			query = query + ",r";
		}

		Map<String,Object> resultMap = new HashMap<>(); 
		Iterator<Map<String, Object>> itr = null;
		try{
			itr = template.query(query, paramMap).iterator();
			while (itr.hasNext()) {
				Map<String, Object> map = itr.next();
				Staff staff = (map.get("t") == null) ? null : template.convert(map.get("t"), Staff.class);
				Restaurant rest = (map.get("r") == null) ? null : template.convert(map.get("r"), Restaurant.class);
				StaffInfo staffInfo = (map.get("si") == null) ? null : template.convert(map.get("si"), StaffInfo.class);
				resultMap.put("staff", staff);
				resultMap.put("staffInfo", staffInfo);
				resultMap.put("rest", rest);
			}
		}catch(Exception e){
			log.warn("Exception in query", e);
		}

		return resultMap;
	}
	
	
	@Override
	public Map<String,Object> findCtAdminDetails(Map<String, Object> paramMap) {


		String query = "MATCH (t:Staff)-[shi:STAFF_HAS_INFO]->(si:StaffInfo) ";
		
		String appendString = null;
		for(Map.Entry<String, Object> mapObject : paramMap.entrySet()){
			
			if(mapObject.getKey().equals(Constants.EMAIL)){
				appendString = appendString == null ? " WHERE t.email={"+Constants.EMAIL+"}" : appendString + " AND t.email={"+Constants.EMAIL+"}";
			}
			else if(mapObject.getKey().equals(Constants.GUID)){
				appendString = appendString == null ? " WHERE t.guid={"+Constants.GUID+"}" : appendString + " AND t.guid={"+Constants.GUID+"}";
			}
			
			else if(mapObject.getKey().equals(Constants.TOKEN)){
				appendString = appendString == null ? " WHERE si.token={"+Constants.TOKEN+"}" : appendString +  " AND si.token={"+Constants.TOKEN+"}";
			}
			
		}
		
		if(!paramMap.containsKey("include_inactive")){
			appendString = appendString == null ?  " WHERE t.status='ACTIVE'" : appendString +  " AND t.status='ACTIVE'";
		}
		
		query = appendString == null ? query : query + appendString;
		
		query = query + " RETURN DISTINCT t, si";

		Map<String, Object> resultMap = new HashMap<>();
		Iterator<Map<String, Object>> itr = null;
		try{
			itr = template.query(query, paramMap).iterator();
			while (itr.hasNext()) {
				Map<String, Object> map = itr.next();
				Staff staff = (map.get("t") == null) ? null : template.convert(map.get("t"), Staff.class);
				StaffInfo staffInfo = (map.get("si") == null) ? null : template.convert(map.get("si"), StaffInfo.class);
				resultMap.put("staff", staff);
				resultMap.put("staffInfo", staffInfo);
			}
		}catch(Exception e){
			log.warn("Exception in query", e);
		}
		
		return resultMap;
	}


}
