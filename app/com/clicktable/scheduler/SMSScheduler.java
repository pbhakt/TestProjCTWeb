package com.clicktable.scheduler;

import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.exception.ClicktableException;
import com.clicktable.model.GuestConversation;
import com.clicktable.model.Reservation;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import play.Logger;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class SMSScheduler implements Runnable {

	@Autowired
	ReservationDao reservationDao;
	@Autowired
	ConversationService conversationService;
	@Autowired
	AuthorizationService authService;

	private static Lock lock = new ReentrantLock();
	private static Logger.ALogger log = Logger.of(SMSScheduler.class);

	public SMSScheduler(ReservationDao reservationDao, ConversationService conversationService,
			AuthorizationService authService) {
		this.reservationDao = reservationDao;
		this.conversationService = conversationService;
		this.authService = authService;
	}

	public SMSScheduler() {
	}

	@Override
	public void run() {
		log.info("Starting SMS scheduled job");
		runSMSScheduler();
		log.info("Finished SMS scheduled job");
	}

	/**
	 * Sends SMS periodically
	 */
	public void runSMSScheduler() {
		// Send SMS periodically
		if (SMSScheduler.lock.tryLock()) {
			try {
				log.info("SMS Job Running !");
				Map<GuestConversation, Reservation> conversationReservation = reservationDao.getAllGuestReservation();
				conversationReservation.keySet().forEach(conversation -> {
					conversationService.addConversation(conversation, authService.loginAsInternal());
					Reservation reservation = conversationReservation.get(conversation);
					reservationDao.updateReservationViaSchedular(reservation);
				});
			} finally {
				SMSScheduler.lock.unlock();
			}
		} else {
			log.warn("Could not acquire lock...Job is already running.");
			throw new ClicktableException("Could not acquire lock...Job is already running.");
		}
	}
}
