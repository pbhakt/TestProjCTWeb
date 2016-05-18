package com.clicktable.dao.intf;



import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.exception.ClicktableException;
import com.clicktable.model.EventPromotion;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.Restaurant;
import com.clicktable.model.TagModelOld;
import com.clicktable.relationshipModel.HasGuest;

/**
 */
@Service
public interface CustomerDao extends GenericDao<GuestProfile>{
    
    /**
     * Method saveRelationModel.
     * @param relationModel HasGuest
     * @return HasGuest
     */
    public HasGuest saveRelationModel(HasGuest relationModel);
    
    /**
     * Method addRestaurantGuest.
     * @param rest Restaurant
     * @param guest GuestProfile
     * @return String
     */
    public String addRestaurantGuest(Restaurant rest, GuestProfile guest) ;
    
    /**
     * Method deleteGuestHasTag.
     * @param restGuid String
     * @param guestGuid String
     * @param tagsToDelete String
     * @return Boolean
     */
    public Boolean deleteGuestHasTag(String restGuid, String guestGuid, String tagsToDelete);
    
    /**
     * Method addGuestHasTag.
     * @param restGuid String
     * @param guestGuid String
     * @param tagListWithGuid List<TagModelOld>
     * @return Boolean
     */
    public Boolean addGuestHasTag(String restGuid, String guestGuid, List<TagModelOld> tagListWithGuid);

	/**
	 * Method getGuestMobileByTagsForEvent.
	 * @param promotion EventPromotion
	 * @return List<List<String>>
	 */
	List<List<String>> getGuestMobileByTagsForEvent(EventPromotion promotion);

	/**
	 * Method findGuestForRest.
	 * @param guest GuestProfile
	 * @return GuestProfile
	 */
	GuestProfile findGuestForRest(GuestProfile guest);

	/**
	 * Method getGuestCountByTagsForEvent.
	 * @param promotion EventPromotion
	 * @return Map<String,Integer>
	 */
	Map<String, Integer> getGuestCountByTagsForEvent(EventPromotion promotion);

	/**
	 * Method totalGuestCount.
	 * @param restId String
	 * @return Integer
	 */
	Integer totalGuestCount(String restId);

	/**
	 * Method getGuestExceptDummyGuest.
	 * @param class1 Class<GuestProfile>
	 * @param params Map<String,Object>
	 * @return List<String>
	 */
	public List<String> getGuestExceptDummyGuest(Class<GuestProfile> class1, Map<String, Object> params);

	/**
	 * Method getGuestsForOtherRest.
	 * @param class1 Class<GuestProfile>
	 * @param guestForRest List<String>
	 * @param restGuid String
	 * @return List<GuestProfile>
	 */
	public List<GuestProfile> getGuestsForOtherRest(
		Class<GuestProfile> class1, List<String> guestForRest, String restGuid);


	/**
	 * Method updateRestaurantGuest.
	 * @param oldCustomer GuestProfile
	 * @param tempGuid GuestProfile
	 * @return String
	 */
	public String updateRestaurantGuest(GuestProfile oldCustomer,GuestProfile tempGuid);

	/*public List<GuestProfile> getAllRestaurantGuest(Class<GuestProfile> class1,
			Map<String, Object> qryParamMap);*/

	/**
	 * Method getRestaurantGuest.
	 * @param class1 Class<GuestProfile>
	 * @param params Map<String,Object>
	 * @return List<GuestProfile>
	 */
	public List<GuestProfile> getRestaurantGuest(Class<GuestProfile> class1,
			Map<String, Object> params);

	/**
	 * Method isGuestForRest.
	 * @param guestGuid String
	 * @param restaurantGuid String
	 * @return Boolean
	 */
	Boolean isGuestForRest(String guestGuid, String restaurantGuid);

	/**
	 * Method validateRestGuestExist.
	 * @param params Map<String,Object>
	 * @return Map<String,Object>
	 */
	Map<String, Object> validateRestGuestExist(Map<String, Object> params);

	/**
	 * Method validateGuestExist.
	 * @param params Map<String,Object>
	 * @return Map<String,Object>
	 */
	public Map<String, Object> validateGuestExist(Map<String, Object> params);

	/**
	 * Method findGuest.
	 * @param paramMap Map<String,Object>
	 * @return GuestProfile
	 */
	public GuestProfile findGuest(Map<String, Object> paramMap);

	//List<GuestProfile> getGuestByTagsForEvent(List<String> tagGuids, String restId, Integer after, Date currentDateTime, Boolean toVip);

	/**
	 * Method deleteGuest.
	 * @param params Map<String,Object>
	 * @return GuestProfile
	 */
	GuestProfile deleteGuest(Map<String, Object> params);


	public void reassign(List<GuestProfile> guestProfileList) throws ClicktableException;


	/**
	 * Method addFirstSeatedTime.
	 * @param restGuid String
	 * @param guestGuid String
	 * @param seatedTime Long
	 * @return Long
	 */
	Long addFirstSeatedTime(String restGuid, String guestGuid, Long seatedTime);

	/**
	 * @param promotion
	 * @param queryParams
	
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getfilteredEventGuest(EventPromotion promotion,
			Map<String, Object> queryParams);

	/**
	 * Method filterGuestMobileNumbers.
	 * this method checks the provided numbers exists in database or numbers of same restaurant and the status with same Restaurant ,
	 * for other restaurant status is always active concerned. 
	 * prefer to pass 50 mobile numbers at one time.
	 * 
	 * @param queryParams Map<String,Object> (restaurantGuid,mobile and ACTIVE status)
	 * 
	 * @return List<Map<String,Object>> 
	 */
	List<Map<String, Object>> filterGuestMobileNumbers(
			Map<String, Object> queryParams);

	//Integer createMultipleRestaurantGuest(Map<String, Object> queryParams);

	/**
	 * Method createMultipleGuests.
	 * this method creates relationship with multiple guest to a restaurant
	 * prefer to make 50 relationship at one time
	 * 
	 * @param queryParams Map<String,Object>  (restaurantGuid,list of map of GuestProfile node and ACTIVE status)
	 * 
	 * @return Integer  (the number of relationship created)
	 */
	Integer createMultipleGuests(Map<String, Object> queryParams);

	//Map<String, Integer> getGuestCountByTagsForEvent(List<String> tagGuids,
	//		String restId, Integer after, Date currentDateTime, Boolean toVip);
    
	   /*public List<GuestProfile> findByFields(Map<String,Object> params);*/


}
