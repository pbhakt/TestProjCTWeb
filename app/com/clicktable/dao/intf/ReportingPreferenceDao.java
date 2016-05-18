package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.ReportingPreference;

@org.springframework.stereotype.Service
public interface ReportingPreferenceDao extends GenericDao<ReportingPreference> {
	String addPrefernce(ReportingPreference reportingPreference);
	List<ReportingPreference> findByCustomeFields(Class<ReportingPreference> class1, Map<String, Object> params);
	//ReportingPreference update(Map<String, Object> valuesToUpdate);
	ReportingPreference update(ReportingPreference reportingPreference);
}
