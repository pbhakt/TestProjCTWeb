/**
 * 
 */
package com.clicktable.dao.intf;

import com.clicktable.relationshipModel.RestaurantHasTable;

/**
 * @author g.singh
 *
 */
@org.springframework.stereotype.Service
public interface RestaurantHasTableDao extends GenericDao<RestaurantHasTable> {

	void addRestHasTableRelationship(RestaurantHasTable rest_has_table);

}
