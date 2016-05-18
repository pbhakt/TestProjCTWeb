package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.clicktable.model.Cuisine;
import com.clicktable.model.Restaurant;

@org.springframework.stereotype.Service
public interface CuisineDao extends GenericDao<Cuisine> {

	public List<Cuisine> findByFields(Map<String, Object> params,String rest_guid,boolean isAdmin);

	Boolean cuisineWithNameExists(String name);

	Boolean otherCuisineWithSameNameExists(String name, Long id);

	public void removeHasCuisineRelationship(String rest_guid,String[] cuisineGuid);

	void addCuisineRelationship(Set<String> cuisineGuid, Restaurant restaurant);

}
