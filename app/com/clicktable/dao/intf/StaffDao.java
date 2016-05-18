package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.Staff;
import com.clicktable.model.StaffInfo;
import com.clicktable.relationshipModel.RestaurantHasUser;

@Service
public interface StaffDao extends GenericDao<Staff>
{
    
   //public List<Staff> findByFields(Map<String,Object> params);
   public RestaurantHasUser saveRelationModel(RestaurantHasUser relationModel);
   public Long addRestaurantStaff(String restGuid, Staff staff) ;
   Staff updateLastLoginInfo(Staff staff);
   Staff updateStaff(Staff staff);
   
   public Map<String, Object> findActiveStaffWithActiveRest(Map<String, Object> paramMap);
   List<StaffInfo> getLogOutUsersList(Map<String, Object> paramMap);
Map<String, Object> findCtAdminDetails(Map<String, Object> paramMap);
   
}
