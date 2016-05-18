/**
 * 
 */
package com.clicktable.validate;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ch.qos.logback.classic.Logger;

import com.clicktable.model.GuestProfile;
import com.clicktable.repository.RestaurantRepo;
import com.clicktable.repository.TableRepo;

/**
 * @author a.thakur
 *
 */
@org.springframework.stereotype.Service
public class RestaurantHasTableValidator {

	public static final Logger log = (Logger) LoggerFactory.getLogger(RestaurantHasTableValidator.class);

	@Autowired
	static RestaurantRepo restaurant_repo;

	@Autowired
	static TableRepo table_repo;

	public static boolean validateRestGuid(String rest_guid) {
		return restaurant_repo.exists(Long.valueOf(rest_guid));
	}

	public static boolean validateRestaurant(GuestProfile guestProfile_model) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean validateTableGuid(String guid) {
		return table_repo.exists(Long.valueOf(guid));
	}

}
