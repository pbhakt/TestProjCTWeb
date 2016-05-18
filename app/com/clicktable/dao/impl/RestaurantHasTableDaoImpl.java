package com.clicktable.dao.impl;

import com.clicktable.dao.intf.RestaurantHasTableDao;
import com.clicktable.relationshipModel.RestaurantHasTable;

/**
 * @author g.singh
 *
 */

@org.springframework.stereotype.Service
public class RestaurantHasTableDaoImpl extends GraphDBDao<RestaurantHasTable> implements RestaurantHasTableDao {
	
	

	public RestaurantHasTableDaoImpl() {
		super();
		this.setType(RestaurantHasTable.class);
	}

	@Override
	public void addRestHasTableRelationship(RestaurantHasTable rest_has_table) {
		create(rest_has_table);
	}

}
