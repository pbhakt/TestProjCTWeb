package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clicktable.config.StormpathConfig;
import com.clicktable.dao.intf.CorporateOffersDao;
import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.GuestBookDao;
import com.clicktable.dao.intf.GuestTagDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.model.CorporateOffers;
import com.clicktable.model.CustomTag;
import com.clicktable.model.CustomTagForEvents;
import com.clicktable.model.GuestBook;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.Tag;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.ModelGetResponse;
import com.clicktable.service.intf.GuestBookService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CustomerValidator;
import com.clicktable.validate.ValidationError;
import com.stormpath.sdk.client.Client;

@Service
public class GuestBookServiceImpl implements GuestBookService {
	//static com.stormpath.sdk.application.Application application = StormpathConfig.getInstance().getApplication();
	static Client client = StormpathConfig.getInstance().getClient();

	@Autowired
	CustomerDao customerDao;
	
	@Autowired
	GuestBookDao guestBookDao;
	
	@Autowired
	ReservationDao reservationDao;
	
	@Autowired
	GuestTagDao tagDao;

	@Autowired
	CustomerValidator customerValidator;
	
	@Autowired
	CorporateOffersDao corporateDao;

	@Override
	@Transactional(readOnly=true)
	public BaseResponse getGuestBookData(Map<String, Object> params) 
	{
	 //Logger.debug("getting guest book data");
	 BaseResponse getResponse;
	 String restGuid = params.containsKey(Constants.REST_GUID)?params.get(Constants.REST_GUID).toString():null;
	 if(( restGuid != null) && (!restGuid.equals("")))
	   {
            // params.remove(Constants.REST_GUID);

             Long guestGetStart = new Date().getTime();
             
             //TODO
             List<GuestProfile> guestList = customerDao.findByFields(GuestProfile.class, params);
			System.out
					.println("Getting guest list---------------------------------------------------"
							+ (new Date().getTime() - guestGetStart));
			GuestProfile cust = null;
			if (guestList.size() > 0) {
				cust = guestList.get(0);
			}
		/* GuestProfile guestProfile = new GuestProfile();
		guestProfile.setRestGuid(restGuid);
		guestProfile.setGuid(params.get("guid").toString());
		//TODO
		Long guestForRest = new Date().getTime();
		GuestProfile cust = customerDao.findGuestForRest(guestProfile);
		 System.out.println("Getting guest list---------------------------------------------------" + (new Date().getTime() - guestForRest));
		Logger.debug("customer with guid is " + cust);*/
		if (cust == null)
		{
		    List<ValidationError> errorList = new ArrayList<ValidationError>();
		    errorList.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID), ErrorCodes.INVALID_GUEST_GUID));
		    getResponse = new ErrorResponse(ResponseCodes.GUESTBOOK_DATA_FETCH_FAILURE, errorList);
		    return getResponse;
		}
		

	    //get details of customer
		Long timeToPopulateGuest = new Date().getTime();
		cust.setRestGuid(restGuid);
	    GuestBook guest = new GuestBook(cust);
	    
	  //get details of customer
	   // GuestBook guest = new GuestBook(guestList.get(0));
	    //Logger.debug("guest after getting details of customer is "+guest);
	    
	    
	    if(null!=cust.getCorporate()){
	    	Map<String, Object> params3 = new HashMap<String, Object>();
	    	params3.put(Constants.GUID, cust.getCorporate());
	    	params3.put(Constants.STATUS, Constants.ACTIVE_STATUS);
	    	params3.put(Constants.REST_GUID, restGuid);
			List<CorporateOffers> corporateOffer=corporateDao.findCorporateOffers(params3);
			if(null!=corporateOffer && !corporateOffer.isEmpty()){
				guest.setCorporate(corporateOffer.get(0));
			}
	    }
	    
		   
	    System.out.println("Populating guest ---------------------------------------------------" + (new Date().getTime() - timeToPopulateGuest));
	    
	    //Logger.debug("guest after getting details of customer is "+guest);
	    
	    params.put(Constants.REST_GUID, restGuid);
	    
	    //get all reservations of this guest for all restaurants
	    Long getResvTime = new Date().getTime();
	    //TODO
	    //List<Reservation> reservationList = reservationDao.getReservationsForGuest(params);
	    Map<String,Object> reservationDataMap = reservationDao.getReservationsForGuest(params);
	    System.out.println("Getting resv list---------------------------------------------------" + (new Date().getTime() - getResvTime));
	
	    guest.setCtCancellations((Integer) reservationDataMap.get("ctCancelCount"));
	    guest.setCtNoShowCount((Integer) reservationDataMap.get("ctNoShowCount"));
	    guest.setCtReservations((Integer) reservationDataMap.get("ctReservationCount"));
	    guest.setCtTotalVisits((Integer) reservationDataMap.get("ctTotalVisits"));
	    guest.setCtWalkins((Integer) reservationDataMap.get("ctWalkinCount"));
	    guest.setCtBarStats((Integer) reservationDataMap.get("ctBarCount"));
	    
	    
	    //populate stats for restaurant
	    guest.setRestCancellations((Integer) reservationDataMap.get("restCancelCount"));
	    guest.setRestNoShowCount((Integer) reservationDataMap.get("restNoShowCount"));
	    guest.setRestReservations((Integer) reservationDataMap.get("restReservationCount"));
	    guest.setRestTotalVisits((Integer) reservationDataMap.get("restTotalVisits"));
	    guest.setRestWalkins((Integer) reservationDataMap.get("restWalkinCount"));
	    guest.setRestBarStats((Integer) reservationDataMap.get("restBarCount"));
	    
	    
	    guest.setRecentHistory((List)reservationDataMap.get("reservationHistory"));
	    guest.setUpcomingReservations((List)reservationDataMap.get("upcomingReservations"));
	    
	    
	   //get tags and preferences of guest
	    
		List<CustomTagForEvents> eventsAndOffers = new ArrayList<>();

		// get all tags for guest
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put(Constants.GUEST_GUID, guest.getGuid());
		params1.put(Constants.REST_GUID, restGuid);
		params1.put(Constants.DISTINCT, false);
		
		List<Tag> tags = tagDao
				.findByFields(Tag.class, params1);
		System.out.println("...........tags.size()..." + tags.size());
		// map to hold tag name and count
		Map<String, Integer> unsortedCountMap = new HashMap<String, Integer>();
		// map to hold tag name and tag object
		Map<String, Tag> tagMap = new HashMap<String, Tag>();
		int count = 0;
		List<CustomTag> customTag = new ArrayList<CustomTag>();
		CustomTagForEvents customTagForEvents;

		for (Tag tag : tags) {
			if (tag.getType().equals(Constants.EVENT) ||tag.getType().equals(Constants.OFFER) ) {
				if (unsortedCountMap.containsKey(tag.getName())) {
					count = unsortedCountMap.get(tag.getName());
					count++;
					unsortedCountMap.put(tag.getName(), count);
				}
				// if count map doesn't contain tag name then add tag
				// name and count=1 in
				// count map,put tag object and name in tag map
				else {
					unsortedCountMap.put(tag.getName(), 1);
					tagMap.put(tag.getName(), tag);
				}
			} else {
				CustomTag cus_Tag = new CustomTag(tag);
				customTag.add(cus_Tag);
			}

		}

		Map<String, Integer> sortedMap = sortByValue(unsortedCountMap);

		// iterate sorted map and populate events and offers list
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			customTagForEvents = new CustomTagForEvents(
					tagMap.get(entry.getKey()), entry.getValue());
			// if(eventsAndOffers.size()<5)
			// {
			eventsAndOffers.add(customTagForEvents);
			// }
		}
		guest.setTag(customTag);
		guest.setEventsAndOffers(eventsAndOffers);
	     
	    
	    getResponse = new ModelGetResponse<GuestBook>(ResponseCodes.GUESTBOOK_DATA_FETCH_SUCCESFULLY, guest , true);
	  /*  }
	    else if(guestList.size()==0)
	    {
	    	getResponse = new GetResponse<GuestProfile>(ResponseCodes.GUESTBOOK_DATA_FETCH_FAILURE_EMPTY_LIST, guestList);
	    }*/
	  /*  else
	    {
		getResponse = new ModelGetResponse<GuestBook>(ResponseCodes.GUESTBOOK_DATA_FETCH_FAILURE, null , false);
	    }*/
	   }
         else
         {
             List<ValidationError> errorList = new ArrayList<ValidationError>();
             errorList.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.REST_ID_REQUIRED), ErrorCodes.REST_ID_REQUIRED));
             getResponse = new ErrorResponse(ResponseCodes.GUESTBOOK_DATA_FETCH_FAILURE, errorList);
         }
	    return getResponse;
	}
	
	
	
	public static Map<String,Integer> sortByValue(Map<String,Integer> unsortMap)
	{	 
		List list = new LinkedList(unsortMap.entrySet());
	 
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
							.compareTo(((Map.Entry) (o1)).getValue());
			}
		});
	 
		Map<String,Integer> sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey().toString(), (Integer)entry.getValue());
		}
		return sortedMap;
	}
	
	

	
}

