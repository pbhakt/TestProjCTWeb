package com.clicktable.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.joda.time.DateTime;

import play.Logger;

import com.clicktable.dao.intf.CalenderEventDao;
import com.clicktable.dao.intf.GuestHasTagsDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Reservation;
import com.clicktable.model.TagModelOld;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;




public class TagGuestWithEvent implements Runnable {

	private static Lock lock = new ReentrantLock();
	ReservationDao reservationDao;
	CalenderEventDao calEventDao;
	//TagServiceDao tagDao;
	GuestHasTagsDao assignTagDao;
	
	public TagGuestWithEvent(ReservationDao reservationDao,
	 CalenderEventDao calEventDao, GuestHasTagsDao assignTagDao) {
		this.reservationDao = reservationDao;
		//this.tagDao = tagDao;
		this.calEventDao = calEventDao;
		this.assignTagDao = assignTagDao;
	}

	@Override
	public void run() {
		if(TagGuestWithEvent.lock.tryLock()){
			try{				
				Date now = new DateTime().toDate();
				Date dayBefore = new DateTime().minusDays(1).toDate();
				
				Map<String, Object> params = new HashMap<String, Object>();				
				params.put(Constants.ACT_START_BEFORE, now);
				params.put(Constants.ACT_END_AFTER, dayBefore);				
				List<Reservation> resvList = reservationDao.findByFields(Reservation.class, params);
				
				params.put(Constants.START_TIME_BEFORE, now);
				params.put(Constants.END_TIME_AFTER, dayBefore);			
				List<CalenderEvent> eventList = calEventDao.findByFields(CalenderEvent.class, params);

				Set<String> nonTagableEventCategory = new HashSet<String>();
				nonTagableEventCategory.add(Constants.BLOCK);
				nonTagableEventCategory.add(Constants.OP_HR);
				nonTagableEventCategory.add( Constants.FULL_BLOCK);
				nonTagableEventCategory.add( Constants.HOLIDAY);
				
				for(CalenderEvent event:eventList){
					DateTime eventStartTime = UtilityMethods.addTimeToDate(event.getEventDate(), event.getStartTime());
					DateTime eventEndTime = UtilityMethods.addTimeToDate(event.getEventDate(), event.getEndTime());
					
					List<String> tagNameList = new ArrayList<String>();
					if(event.getSubCategory()!=null)
						tagNameList.add(event.getSubCategory());					
					if(!nonTagableEventCategory.contains(event.getCategory())){
						tagNameList.add(event.getCategory());
					}
					
					for(Reservation reservation:resvList){
						if(reservation.getRestaurantGuid().equals(event.getRestaurantGuid()) && reservation.getActStartTime().before(eventEndTime.toDate()) &&
								reservation.getActEndTime().after(eventStartTime.toDate())){

							Map<String, Object> param= new HashMap<String, Object>() ;
							param.put(Constants.NAME, tagNameList);	
							param.put(Constants.TYPE, Constants.EVENT_AND_OFFER);
							List<TagModelOld> tags = assignTagDao.findByFields(TagModelOld.class, param);
							if(tags.size() < tagNameList.size()){
								for(String name:tagNameList){
									boolean tagExist = false;
									for(TagModelOld tag:tags){
										if(tag.getName().equals(name))
											tagExist =true;
									}
									if(!tagExist){
										TagModelOld newTag = new TagModelOld(name, Constants.EVENT_AND_OFFER);
										assignTagDao.create(newTag);
										tags.add(newTag);
									}
								}							
							}
							assignTagDao.addGuestTagRelationship(reservation.getGuestGuid(), reservation.getRestaurantGuid(), tags);
						}
					}

				}
			}catch(Exception e){
				Logger.info(" TagGuestWithEvent Exception is ..........");	
				Logger.debug("TagGuestWithEvent Exception is........... "+e.getMessage());
				Logger.error("TagGuestWithEvent Exception is........... "+e.getMessage());
			
			}
			finally{
				TagGuestWithEvent.lock.unlock();
			}
		}
	}
	

}
