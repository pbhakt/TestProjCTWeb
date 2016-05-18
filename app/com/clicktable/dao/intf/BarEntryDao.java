package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.BarEntry;

@org.springframework.stereotype.Service
public interface BarEntryDao extends GenericDao<BarEntry> {
	String addBarEntry(BarEntry barEntry );
	List<BarEntry> findByCustomeFields(Class<BarEntry> class1, Map<String, Object> params);
	BarEntry update(Map<String, Object> valuesToUpdate);
	Map<String, Object> getGuestAndRestDataFromBarEntry(BarEntry barEntry);
}
