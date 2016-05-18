package com.clicktable.scheduler;

import com.clicktable.dao.intf.QueueDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.exception.ClicktableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import play.Logger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ReservationStatusUpdateAtShiftEnd extends Thread {

	@Autowired
	RestaurantDao restDao;

	@Autowired
	ReservationDao resvDao;

	@Autowired
	QueueDao queueDao;
	private static Lock lock = new ReentrantLock();

	private static Logger.ALogger log = Logger.of(ReservationStatusUpdateAtShiftEnd.class);

	public ReservationStatusUpdateAtShiftEnd(RestaurantDao restDao, ReservationDao resvDao, QueueDao queueDao) {
		super();
		this.restDao = restDao;
		this.resvDao = resvDao;
		this.queueDao = queueDao;
	}

	public ReservationStatusUpdateAtShiftEnd() {
	}

	@Override
	public void run() {
		log.info("Started scheduler: Reservation staus update at shift end");
		runReservationStatusUpdateAtShiftEnd();
		log.info("Finished scheduler: Reservation status update at shift end");
	}

	/**
	 * Updates reservation status at shift end
	 */
	public void runReservationStatusUpdateAtShiftEnd() {
		if (ReservationStatusUpdateAtShiftEnd.lock.tryLock()) {
			try {
				log.info("Calling delete queue");
				queueDao.deleteAllQueueReservationBySchedular();
				resvDao.updateReservationWithShifEndCypherViaSchedular();
			} finally {
				ReservationStatusUpdateAtShiftEnd.lock.unlock();
			}
		} else {
			log.warn("Could not acquire lock...Job is already running.");
			throw new ClicktableException("Could not acquire lock...Job is already running.");
		}
	}

}
