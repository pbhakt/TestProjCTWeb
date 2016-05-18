package com.clicktable.scheduler;

import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.exception.ClicktableException;
import com.clicktable.model.GuestProfile;
import com.clicktable.service.intf.CustomerLoginService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import play.Logger;
import play.libs.Akka;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class CheckDND implements Runnable {

	@Autowired
	CustomerDao customerDao;

	@Autowired
	CustomerLoginService customerLoginService;
	private static Lock lock = new ReentrantLock();

	private static Logger.ALogger log = Logger.of(CheckDND.class);

	public CheckDND() {
	}

	public CheckDND(CustomerDao customerDao2) {
		log.info("Started scheduler: Check DND");
		this.customerDao = customerDao2;
		log.info("Started scheduler: Check DND");
	}

	@Override
	public void run() {
		runDND();
	}

	public void runDND() {
		if (CheckDND.lock.tryLock()) {
			log.debug(" CHECK DND SYSTEM SHUTDOWN-  " + Akka.system().isTerminated());
			try {
				List<GuestProfile> guestProfile = customerDao.findAll(GuestProfile.class);
				log.debug(" Size of GuestProfile " + guestProfile.size());
				log.info("CheckDND Enable ");
					
					Lists.partition(guestProfile, 50).forEach(guestProfileList -> {
						try{
						      customerDao.reassign(guestProfileList);
					       }catch(ClicktableException e){
						      log.error(" Scrubbing DND Error "+ e.getMessage());
					     }
				});
			} finally {
				CheckDND.lock.unlock();
			}
		} else {
			log.warn("Could not acquire lock...Job is already running.");
			throw new ClicktableException("Could not acquire lock...Job is already running.");
		}
	}
}
