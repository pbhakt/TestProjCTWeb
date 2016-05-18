package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.BlackOutHours;
import com.clicktable.model.Onboarding;
import com.clicktable.model.OperationalHours;
import com.clicktable.model.RestSystemConfigModel;
import com.clicktable.model.Restaurant;
import com.clicktable.model.RestaurantContactInfo;
import com.clicktable.model.RestaurantContactInfoAdmin;
import com.clicktable.model.RestaurantGeneralInfo;
import com.clicktable.model.Staff;
import com.clicktable.response.BaseResponse;


@org.springframework.stereotype.Service
public interface RestaurantService {
	
	/**
	 * Add new Restaurant
	 * @param rest	Restaurant to be added
	 * @param staff	Restaurant Admin
	 * @return
	 */
	BaseResponse addRestaurant(Restaurant rest,Staff staff,String token);
	
	/**
	 * Get restaurants based on parameters
	 * @param params	search parameters
	 * @return
	 */
	BaseResponse getRestaurants(Map<String,Object> params);
	
	/**
	 * Update Restaurant data
	 * @param rest
	 * @return
	 */
	BaseResponse updateRestaurant(Restaurant rest, String token);

	BaseResponse addRestaurant(Onboarding onboard, String token);
	
	
	BaseResponse addAttributes(String restGuid, String attrGuid, String token);
	
	
	BaseResponse addSystemConfig(RestSystemConfigModel rest, String token);
	
	public BaseResponse getSystemConfig(Map<String,Object> params);
	
	
	BaseResponse addSection(String restGuid, String sectionGuid, String token);
	
	
	BaseResponse deleteSection(String restGuid, String sectionGuid, String token);

	BaseResponse getRestaurantsSection(Map<String, Object> stringParamMap);

	BaseResponse updateContactInfo(RestaurantContactInfo contactInfo, String token);
	
	BaseResponse updateRestaurantGeneralInfo(RestaurantGeneralInfo rest, String header);
	public BaseResponse addOperationalHours(OperationalHours ophr, String token);

	BaseResponse getOperationalHours(Map<String, Object> params);

	BaseResponse getHistoricalTat(Map<String, Object> params);

	BaseResponse addBlackOutHours(BlackOutHours ophr, String header);

	BaseResponse getBlackOutHours(Map<String, Object> params);

	BaseResponse cleanRestaurantData(Map<String, Object> stringParamMap, String token);

	BaseResponse getRestaurantWeather(String string);

	BaseResponse updateContactInfoCtAdmin(RestaurantContactInfoAdmin rest,
			String token);

	BaseResponse statusUpdateRestaurant(Restaurant rest, String token);

	BaseResponse reactivateRestaurant(Staff staff, String token);

	
}
