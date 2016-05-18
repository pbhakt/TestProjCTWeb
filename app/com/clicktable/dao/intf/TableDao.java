package com.clicktable.dao.intf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clicktable.model.Section;
import com.clicktable.model.Table;

@org.springframework.stereotype.Service
public interface TableDao extends GenericDao<Table> {

	Boolean tableWithNameExistsForRestaurant(String tableName, String restId);

	String addRestaurantTable(Table table);
	//public List<Table> getAllTable(String rest_id,String role,String min_cover)

	List<Table> tableExistForRestaurant(List<String> list, String restId);
	
	String updateRestaurantTable(Table table);

	Boolean otherTableWithSameNameExists(String tableName, String guid, String restId);

	List<Table> findBySection(List<String> blockingArea);

	int deleteTable(Table table);

	Map<String, String> getCustomTable(Map<String, Table> tableMap);

	Map<String, Object> validateTableBeforeDelete(Table table);

	Map<String, Integer> getTables(Object object);

	List<String> getBlockedTables(Map<String, Object> queryMap);

	List<Table> findAllTables(Class type, Map<String, Object> params);

	Map<Section, List<Table>> getSectionsAndTables(String restGuid);

	List<Table> getCustomTables(Map<String, Object> params);

}
