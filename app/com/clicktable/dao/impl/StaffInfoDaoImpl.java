package com.clicktable.dao.impl;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.dao.intf.StaffInfoDao;
import com.clicktable.model.StaffInfo;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.ValidationError;


@Service
public class StaffInfoDaoImpl extends GraphDBDao<StaffInfo> implements StaffInfoDao
{


	public StaffInfoDaoImpl() {
		super();
		this.setType(StaffInfo.class);
	}

/*	@Override
	public StaffHasInfo saveRelationModel(StaffHasInfo relationModel)
	{
		return template.save(relationModel);
	}
*/
	@Override
	public void updateAllProperties(StaffInfo staffInfo){
		template.save(staffInfo);
	}
	
	@Override
	public void updateAllNodes(List<StaffInfo> staffInfoList){
		template.save(staffInfoList);
	}


	/**
	 * Method to create relationship of a restaurant with staff
	 */
	@Override     
	public StaffInfo addStaffInfo(StaffInfo staffInfo) 
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.STAFF_GUID, staffInfo.getStaffGuid());
		params.put("otp_require", staffInfo.isIs_otp_require());
		String staffInfoGuid = UtilityMethods.generateCtId();
		params.put(Constants.GUID, staffInfoGuid);
		
		String query = "MATCH (s : Staff {guid : {"+Constants.STAFF_GUID+"}}) "
				+ "MERGE (s)-[rel : STAFF_HAS_INFO{__type__ : 'StaffHasInfo'}]->"
				+ "(si : StaffInfo : _StaffInfo{guid : {" + Constants.GUID + "},staff_guid : {"+Constants.STAFF_GUID+"},otp_require : {otp_require}}) return si";
	
		Result<Map<String, Object>> result = executeWriteQuery(query, params);//template.query(query, null);
		Logger.debug("query executed,Result is "+result);
		Iterator<Map<String, Object>> itr = result.iterator();
		StaffInfo staffInfoObj = new StaffInfo();
		while(itr.hasNext())
		{
			Map<String,Object> map = itr.next();
			staffInfoObj = template.convert(map.get("si"), StaffInfo.class);
			
		}

		Logger.debug("StaffInfo Object is-------------------------" + staffInfoObj);
		return staffInfoObj;
	}



/*
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

		Logger.debug("query is " + query);
		Result<Staff> r = executeWriteQuery(query, params).to(type);
		return r.singleOrNull();

	}*/
	
/*	@Override
	public Staff updateStaff(Staff staff) 
	{
		StringBuffer query=new StringBuffer();
		
		Map<String , Object> params = new HashMap<>();
		params.put(Constants.GUID, staff.getGuid());
		
		query.append("MATCH (t:Staff{guid:{"+Constants.GUID+"}}) SET");
		
		if(null!=staff.getCreatedBy()){
			query.append(" t.created_by='"+staff.getCreatedBy()+"'");
		}
		if(null!=staff.getRoleId()){
			params.put(Constants.ROLE_ID, staff.getRoleId());
			query.append(" t.roleId={"+Constants.ROLE_ID+"}");
		}
		if(null!=staff.getCreatedDate()){
			query.append(" , t.updated_dt=toInt("+staff.getUpdatedDate().getTime()+")");
		}
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
						

		Logger.debug("query is " + query);
		
		template.query(query.toString(), params);
		return staff;
	}
*/
	
	
	@Override
	@Transactional
	public void setStaffInfoList(Map<String, Object> mapList, List<ValidationError> errorList) {
		// TODO Auto-generated method stub

		/*Map<String,Object> params = new HashMap<>();
		params.put("mapList", mapList);*/

		
		String query = " MATCH (s:Staff {guid : {staff_guid}}) "
				+ "MERGE (s)-[rel:STAFF_HAS_INFO{__type__:'StaffHasInfo'}]->"
				+ "(si : StaffInfo : _StaffInfo{guid : {guid},staff_guid : {staff_guid},otp_require : {otp_require}}) Return s.guid as guid";

		String staffGuid = null;
		try{
			Iterator<Map<String, Object>> results = executeWriteQuery(query, mapList).iterator();
			while (results.hasNext()) {
				Map<String, Object> map = results.next();
				staffGuid = map.get("guid").toString();
			}
		}catch(Exception e){
			errorList.add(new ValidationError("error addning staff", mapList.get("guid")));
		}
		
		
		if(staffGuid == null)
		{
			errorList.add(new ValidationError("error addning staff", mapList.get("guid")));
		}
		


	}
}
