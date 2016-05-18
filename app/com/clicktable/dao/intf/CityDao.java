package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.City;

@Service
public interface CityDao extends GenericDao<City> {
	
	public List<City> findCities(Map<String, Object> params);

	public String addCity(City city);

	public boolean hasChildRelationships(City existing);

	City updateCity(City toupdatestate, City existing);
	
}
