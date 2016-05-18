package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.Building;

@Service
public interface BuildingDao extends GenericDao<Building> {

	List<Building> findBuildings(Map<String, Object> params);

	String addBuilding(Building building);
	
}
