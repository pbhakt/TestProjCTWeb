package com.clicktable.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import play.Logger;
import play.api.libs.Crypto;
import play.cache.Cache;
import play.libs.Json;

import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.StaffDao;
import com.clicktable.dao.intf.StaffInfoDao;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Role;
import com.clicktable.model.Staff;
import com.clicktable.model.StaffInfo;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.LoginResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.StaffService;
import com.clicktable.service.intf.UserTokenService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;

@Service
@Configurable
public class AuthorizationServiceImpl implements AuthorizationService 
{
    @Autowired
    UserTokenService userTokenService;
    @Autowired
    CustomerDao customerDao;
    @Autowired
    StaffDao staffDao;
    @Autowired
    StaffService staffService;

	Map<String, UserInfoModel> loggedInUsersMap;
	
	String sysadminToken;
	
	@Autowired
	StaffInfoDao staffInfoDao;
	

	public AuthorizationServiceImpl() {
		loggedInUsersMap = new HashMap<String, UserInfoModel>();
		LoggedInUsersManagingThreadService loginScheduler = new LoggedInUsersManagingThreadService();
		loginScheduler.setDaemon(true);
		loginScheduler.start();
	}

	@Override
	public Long getRoleByToken(String token) {
		try {
			return ((UserInfoModel)Cache.get(token)).getRoleId();
		} catch (Exception ex) {
			Logger.error(ex.getMessage());
			Logger.error("Error while getting RoleId by access-token", ex);
			return null;
		}

	}

	@Override
	public String getLoggedInUser(String token) {
		if(token == null )
		{
			return null;
		}
		else
		{
			try
			{
			token = Crypto.decryptAES(token);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		try {
			return ((UserInfoModel)Cache.get(token)).getGuid();
		} catch (Exception ex) {
			Logger.error(ex.getMessage());
			Logger.error("Error while getting User Guid by access-token", ex);
			return null;
		}
		
	}

	@Override
	public boolean isRecentToken(String token) {
		//return loggedInUsersMap.containsKey(token);
		return (Cache.get(token) != null);
	}
	
	@Override
	public boolean hasAccess(Long roleId, List<Role> roles) 
	{
		if (roleId != 0) 
		{
			for (Role permittedRole : roles) 
			{
				Long permittedId =permittedRole.getRoleId();
				Logger.debug("permitted id is "+permittedId+" role id is "+roleId);
				if (permittedId.intValue() == roleId.intValue()) 
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean addNewSession(String token, UserInfoModel userInfo) {
		//loggedInUsersMap.put(token, userInfo);
		Cache.set(token, userInfo, Constants.TTLForCache);
		return true;
	}

	@Override
	public boolean removeSession(String token) {
		/*if (loggedInUsersMap.containsKey(token)) {
			loggedInUsersMap.remove(token);
		}*/
		
		if (Cache.get(token) != null) {
			Cache.remove(token);
		}
		return true;
	}

	public Map<String, UserInfoModel> getLoggedInUsersMap() {
		return loggedInUsersMap;
	}
	
	@Override
	public String loginAsInternal(){
		if(sysadminToken == null || getUserInfoByToken(sysadminToken) == null){
			String password = UtilityMethods.getConfString("sysadmin.secret");
			String userName = UtilityMethods.getConfString("sysadmin.user");
			BaseResponse response = staffService.staffLogin(userName, password);
			LoginResponse loginResponse ;
			if(response instanceof LoginResponse)
			{
				loginResponse = (LoginResponse)response;
				sysadminToken = loginResponse.getToken();
			}
			else
			{
				Logger.error("LOGIN FAILURE SYS ADMIN ");
				ErrorResponse error = (ErrorResponse)response;
				Logger.error("Error response in sys admin login is=====================" + Json.toJson(error));
			}
				
			
		}
		return Crypto.encryptAES(sysadminToken);
	}
	
	@Override
	//@Transactional(readOnly =true)
	public UserInfoModel getUserInfoByToken(String token)
	{
		if(token == null )
		{
			return null;
		}
		else
		{
			try
			{
			token = Crypto.decryptAES(token);
			}
			catch(Exception e)
			{
				Logger.debug(e.getMessage());
				e.printStackTrace();
			}
		}
		
		UserInfoModel userInfo = (UserInfoModel) Cache.get(token);
		
		Logger.debug("user info from logged in user map is "+userInfo);
		
		Long roleId = 0L;
		//if user exists in map then update update time and get role id
		if(userInfo != null){
			if(!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))
			{
		    Cache.set(token, userInfo, Constants.TTLForCache);
			}
		}
		else
		{
		    userInfo = null;
		    Logger.debug("Searching token in database");
		    
		 	
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put(Constants.TOKEN, token);
			Map<String,Object> resultMap = staffDao.findActiveStaffWithActiveRest(paramMap);
			
			Staff staff = (Staff) resultMap.get("staff");
			Restaurant rest = (Restaurant) resultMap.get("rest");
			StaffInfo staffInfo = (StaffInfo) resultMap.get("staffInfo");
		   
			if(staffInfo != null && staff != null)
			{
				roleId = staff.getRoleId();

				if( roleId.equals(Constants.ADMIN_ROLE_ID) || roleId.equals(Constants.MANAGER_ROLE_ID) || roleId.equals(Constants.STAFF_ROLE_ID))
				{
					userInfo = new UserInfoModel(staff);
					Cache.set(token, userInfo, Constants.TTLForCache);
				}
				if(roleId.equals(Constants.CT_ADMIN_ROLE_ID))
				{
					userInfo = new UserInfoModel(staff);
					userInfo.setRoleId(Constants.CT_ADMIN_ROLE_ID);
					Cache.set(token, userInfo);
				}
				
			}
		}
	    return userInfo;
	}

	/**
	 * inner class for scheduler that checks for inactive logged in users and
	 * removes them from LoggedInUsersMap
	 * 
	 * @author p.singh
	 *
	 */
	public class LoggedInUsersManagingThreadService extends Thread {/*


		 public LoggedInUsersManagingThreadService singleThread = null;
		Long currentTime;
		Long updateTime;
		Long sessionLimit;
		Long threadScheduleTime;

		@Override
		public void run() {

		   	try {
		        	Map<String, UserInfoModel> loggedInUsers = getLoggedInUsersMap();
		        	Logger.debug("thread started");

				currentTime = new Time(new Date().getTime()).getTime();
				sessionLimit = Long.parseLong(UtilityMethods.getProperty(Constants.SESSION_CONFIG, Constants.SESSION_MODULE, Constants.SESSION_LIMIT));
				threadScheduleTime = Long.parseLong(UtilityMethods.getProperty(Constants.SESSION_CONFIG, Constants.SESSION_MODULE, Constants.THREAD_RUNNIG_INTERVAL));
				Logger.debug("logged in users are "+loggedInUsers);
				Logger.debug("current time is "+currentTime);
				for (Map.Entry<String, UserInfoModel> entry : loggedInUsers.entrySet()) 
				{
					updateTime = entry.getValue().getUpdateTime().getTime();
                                         Logger.debug("user is "+entry.getKey()+" update time is "+entry.getValue().getUpdateTime());
					if ((currentTime - updateTime) > sessionLimit) 
					{
					    Logger.debug("Session time out");
					    loggedInUsers.remove(entry.getKey());
					}
				}
				Thread.sleep(threadScheduleTime);
				run();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	*/}

	@Override
	public String getTokenForStaff(String guid) {
		// TODO Auto-generated method stub
		
		String userToken = null;
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Constants.GUID, guid);
		Map<String,Object> resultMap = staffDao.findActiveStaffWithActiveRest(paramMap);
		
		StaffInfo staffInfo = (StaffInfo) resultMap.get("staffInfo");
		userToken = (staffInfo != null) ? staffInfo.getToken() : null;
		return userToken;
	}

	

}
