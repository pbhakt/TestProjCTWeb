package com.clicktable.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.joda.time.DateTime;

import play.Logger;

import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.model.Reservation;
import com.clicktable.model.Restaurant;

import static com.clicktable.util.Constants.*;

import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;

/**
 * @author s.gupta
 *
 */
public class ReservationStatusUpdate implements Runnable {

	RestaurantDao restDao;
	ReservationDao resvDao;
	private static Lock lock = new ReentrantLock();


	public ReservationStatusUpdate(RestaurantDao restDao, ReservationDao resvDao) {
		super();
		this.restDao = restDao;
		this.resvDao = resvDao;
	}



	@Override
	public void run() {
		if(ReservationStatusUpdate.lock.tryLock()){
			try{
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(RESERVATION_STATUS, UtilityMethods.getEnumValues(RESERVATION_MODULE, STATUS_TO_CHANGE));
				params.put(EST_START_BEFORE, DateTime.now());
				List<Reservation> resvList = resvDao.findByFields(Reservation.class, params);

				Map<String, Restaurant> restaurantMap = new HashMap<String, Restaurant>();
				for(Reservation reservation:resvList){
					String restGuid=reservation.getRestaurantGuid();
					if(!restaurantMap.containsKey(restGuid)){
						Restaurant rest = restDao.findRestaurantByGuid(restGuid);
						if(rest!=null)
							restaurantMap.put(restGuid, rest);
					}
					Restaurant restaurant = restaurantMap.get(restGuid);
					if(null!=restaurant){
					Integer timeToAdd=0;
					if(reservation.getBookingMode()!=null && reservation.getBookingMode().equals(ONLINE_STATUS) && null!=restaurant.getReserveReleaseTime())
						timeToAdd = restaurant.getReserveReleaseTime()*60*1000;
					else if(restaurant.getWaitlistReleaseTime()!=null)
						timeToAdd = restaurant.getWaitlistReleaseTime()*60*1000;					
					if(reservation.getEstStartTime().getTime() + timeToAdd < new DateTime().getMillis()){
						reservation.setReservationStatus(Constants.NO_SHOW_STATUS);
						resvDao.updateReservationViaSchedular(reservation);
					}
					}
				}
			}
			catch(Exception e){
				Logger.info("ReservationStatusUpdate Exception is ..........");	
				Logger.debug("ReservationStatusUpdate Exception is........... "+e.getMessage());
				Logger.error("ReservationStatusUpdate Exception is........... "+e.getMessage());
			}
			finally{
				ReservationStatusUpdate.lock.unlock();
			}
		}


	}

}
