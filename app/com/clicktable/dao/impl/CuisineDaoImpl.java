package com.clicktable.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import play.Logger;

import com.clicktable.dao.intf.CuisineDao;
import com.clicktable.model.Cuisine;
import com.clicktable.model.Restaurant;
import com.clicktable.relationshipModel.RestaurantHasCuisineRelationshipModel;
import com.clicktable.repository.CuisineRepo;
import com.clicktable.repository.RestaurantRepo;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

@Service
public class CuisineDaoImpl extends GraphDBDao<Cuisine> implements CuisineDao {

	
	@Autowired
	RestaurantRepo restaurantRepo;

	@Autowired
	CuisineRepo cuisineRepo;
	RestaurantHasCuisineRelationshipModel restaurantHasCuisineRelationshipModel;

	public CuisineDaoImpl() {
		super();
		this.setType(Cuisine.class);
	}

	@Override
	public Boolean cuisineWithNameExists(String name) {
		Map<String, Object> params = new java.util.HashMap<String, Object>();
		params.put(Constants.NAME, "(?i)"+name);
		StringBuilder query = new StringBuilder("MATCH (t:"+Cuisine.class.getSimpleName()+") where t.name=~{"+Constants.NAME+"} RETURN t LIMIT 1");
		Result<Map<String, Object>> itr = executeWriteQuery(query.toString(), params);
		return itr.iterator().hasNext();
	}

	@Override
	public Boolean otherCuisineWithSameNameExists(String name, Long id) {
		Map<String, Object> params = new java.util.HashMap<String, Object>();
		params.put(Constants.NAME, "(?i)"+name);
		params.put(Constants.ID, id);
		StringBuilder query = new StringBuilder("MATCH (t:"+Cuisine.class.getSimpleName()+") where id(t)<>{id} AND t.name=~{"+Constants.NAME+"} RETURN t LIMIT 1");
		Result<Map<String, Object>> itr = executeWriteQuery(query.toString(), params);
		return itr.iterator().hasNext();
	}

	@Override
	public void addCuisineRelationship(Set<String> cuisineGuid, Restaurant restaurant) {

		/* Finding Restaurant Model from RestaurantRepo */

		for (String cusinieId : cuisineGuid) {
			/* Finding Tag Profile ID */
			Cuisine cuisine_model = cuisineRepo.findByguid(cusinieId);
			if (cuisine_model != null) {
				int relationship_exist = cuisineRepo.cuisine_has_tag_rel(restaurant.getGuid(), cuisine_model.getGuid());
				Logger.info("Relationship exist between Guest and Tag" + relationship_exist);
				restaurantHasCuisineRelationshipModel = template
						.createRelationshipBetween(restaurant, cuisine_model, RestaurantHasCuisineRelationshipModel.class, RelationshipTypes.HAS_CUISINE, false);
				restaurantHasCuisineRelationshipModel.setArea(restaurant.getRegion());
				restaurantHasCuisineRelationshipModel.setBuilding(restaurant.getBuilding());
				restaurantHasCuisineRelationshipModel.setCity(restaurant.getCity());
				restaurantHasCuisineRelationshipModel.setLocality(restaurant.getLocality());
				restaurantHasCuisineRelationshipModel.setState(restaurant.getState());
				// Template Save
				template.save(restaurantHasCuisineRelationshipModel);
			}
			
		}



	}

	@Override
	public void removeHasCuisineRelationship(String rest_guid, String[] cuisineGuid) {

		/* Finding Restaurant Model from RestaurantRepo */
		Restaurant restaurant = restaurantRepo.findByguid(rest_guid);

		for (int i = 0; i < cuisineGuid.length; i++) {

			/* Finding Tag Profile ID */
			Cuisine cuisine_model = cuisineRepo.findByguid(cuisineGuid[i]);

			// Deleting GUEST_HAS_TAG Relationship associated with "REST_GUID"
			if (cuisine_model != null)
				cuisineRepo.delete_has_cuisine_rel(restaurant.getGuid(), cuisine_model.getGuid());

		}

	}

	public List<Cuisine> findByFields(Map<String, Object> params, String rest_guid, boolean isAdmin) {

		StringBuilder query = super.getMatchClause(params);

		query.append("<-[rel :" + RelationshipTypes.HAS_CUISINE + "]-(n:" + "Restaurant");
		if (!isAdmin) {
			params.put(Constants.REST_GUID, rest_guid);
			query.append("{guid:{" + Constants.REST_GUID + "}})");
		} else {
			query.append(')');
		}

		query.append(super.getWhereClause(params));
		query.append(getReturnClause(params));
		params.remove(Constants.PAGE_NO);
		params.remove(Constants.PAGE_SIZE);
		System.out.println(query.toString());
		return executeQuery(query.toString(), params);

	}

	@Override
	protected StringBuilder getReturnClause(Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		Integer pageSize = getPageSize(params);
		Integer startIndex = getIndex(params, pageSize);
		query.append(" RETURN t");
		return query.append(" SKIP " + startIndex + " LIMIT " + pageSize);
	}

}

