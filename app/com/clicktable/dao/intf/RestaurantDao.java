package com.clicktable.dao.intf;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.clicktable.model.BlackOutHours;
import com.clicktable.model.CustomBlackOutHours;
import com.clicktable.model.CustomOperationalHour;
import com.clicktable.model.HistoricalTat;
import com.clicktable.model.OperationalHours;
import com.clicktable.model.ParentAccount;
import com.clicktable.model.Reservation;
import com.clicktable.model.RestSystemConfigModel;
import com.clicktable.model.Restaurant;
import com.clicktable.model.RestaurantAddress;
import com.clicktable.model.RestaurantContactInfo;
import com.clicktable.model.RestaurantContactInfoAdmin;
import com.clicktable.model.RestaurantGeneralInfo;
import com.clicktable.relationshipModel.HasAddress;

@org.springframework.stereotype.Service
public interface RestaurantDao extends GenericDao<Restaurant>
{
   //public void addRestaurant(Restaurant rest);
	//public List<Restaurant> getReataurants();
	//public List<Restaurant> findIteratorByFields(Class type, Map<String, Object> params);
    
    
    public HasAddress saveRelationModel(HasAddress relationModel);
    
    public  boolean addSystemConfig(RestSystemConfigModel rest, String token);
    
    public Iterator<Map<String, Object>> getSystemConfig(Map<String,Object> params);
    
    public Iterator<Map<String, Object>> getCustomSystemConfig(Map<String,Object> params);
    
    
    public boolean addRestaurantSection(String restGuid, String sectionGuid);
    
    public boolean deleteRestaurantSection(String restGuid, String sectionGuid);
    
    public Restaurant findRestaurantByGuid(String guid);
    
    public String updateRestaurantGeneralInfo(RestaurantGeneralInfo guid,Map<String, Object> objectAsMap);
    
    public Long addRestaurantAddress(Restaurant rest, RestaurantAddress address) ;

    public Restaurant updateContactInfo(RestaurantContactInfo rest);

    public Long addRestaurantAccount(Restaurant rest, ParentAccount account);

    public HistoricalTat findHistoricalTatForRest(String restGuid);

    public void addHistoricalTatData(RestSystemConfigModel rest);
    public boolean addOperationalHours( OperationalHours ophr) ;

    public OperationalHours getOperationalHours(Map<String, Object> params);

	public BlackOutHours getBlackOutHours(Map<String, Object> params);

	public CustomBlackOutHours getCustomBlackOutHours(Map<String, Object> params);
	
	public boolean addBlackOutHours(BlackOutHours ophr);

	public boolean deleteRestaurantData(String restGuid);

	public Restaurant updateContactInfoAdmin(
			RestaurantContactInfoAdmin contactInfo);

	public void setInactiveAllActiveStaff(String guid);

	
	List<Reservation> getReservationsForShifts(List<Map<String, Long>> shifts,
			String restGuid);

	public CustomOperationalHour getCustomOperationalHours(Map<String, Object> params);
   
}
