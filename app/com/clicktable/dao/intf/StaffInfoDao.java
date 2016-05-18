package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.StaffInfo;
import com.clicktable.validate.ValidationError;

@Service
public interface StaffInfoDao extends GenericDao<StaffInfo>
{

	//public List<Staff> findByFields(Map<String,Object> params);
	//public StaffHasInfo saveRelationModel(StaffHasInfo relationModel);
	//StaffInfo updateLastLoginInfo(StaffInfo staffInfo);
	//StaffInfo updateStaffInfo(StaffInfo staffInfo);

	StaffInfo addStaffInfo(StaffInfo staffInfo);

	void updateAllProperties(StaffInfo staffInfo);
	void updateAllNodes(List<StaffInfo> staffInfoList);

	
/*	void setStaffInfoList(Map<String, Object> map);*/

	void setStaffInfoList(Map<String, Object> mapList,
			List<ValidationError> errorList);

	/*void updateOtpDetails(Map<String, Object> infoUpdateMap);
*/


}
