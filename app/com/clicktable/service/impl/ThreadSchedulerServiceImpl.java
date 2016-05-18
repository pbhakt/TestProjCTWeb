package com.clicktable.service.impl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Akka;
import play.libs.Time.CronExpression;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import com.clicktable.dao.intf.CalenderEventDao;
import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.GuestHasTagsDao;
import com.clicktable.dao.intf.GuestTagDao;
import com.clicktable.dao.intf.QueueDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.UserTokenDao;
import com.clicktable.dao.intf.WSRequestDao;
import com.clicktable.scheduler.BarEntryStatusUpdate;
import com.clicktable.scheduler.CheckDND;
//import com.clicktable.scheduler.ExtendRecurEndDateForNever;
import com.clicktable.scheduler.PopulateMasterData;
import com.clicktable.scheduler.RemoveAccessToken;
import com.clicktable.scheduler.RemoveServer;
import com.clicktable.scheduler.ReservationStatusUpdate;
import com.clicktable.scheduler.ReservationStatusUpdateAtShiftEnd;
import com.clicktable.scheduler.RetryFailedFbSyncRequests;
import com.clicktable.scheduler.SMSScheduler;
import com.clicktable.service.intf.AddressService;
import com.clicktable.service.intf.AttributeService;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.BarEntryService;
import com.clicktable.service.intf.ConversationService;
import com.clicktable.service.intf.CountryService;
import com.clicktable.service.intf.EventService;
import com.clicktable.service.intf.NotificationService;
import com.clicktable.service.intf.RoleService;
import com.clicktable.service.intf.StaffService;
import com.clicktable.service.intf.TatService;
import com.clicktable.service.intf.ThreadSchedulerService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.ReservationValidator;

/**
 * @author s.gupta
 *
 */
@org.springframework.stereotype.Service
public class ThreadSchedulerServiceImpl implements ThreadSchedulerService {

	@Autowired
	WSRequestDao requestDao;
	@Autowired
	UserTokenDao tokenDao;
	@Autowired
	AuthorizationService authService;
	@Autowired
	GuestHasTagsDao assignTagDao;
	@Autowired
	CalenderEventDao calEventDao;
	//@Autowired
	//TagServiceDao tagDao;
	@Autowired
	ReservationDao reservationDao;
	@Autowired
	RestaurantDao restaurantDao;
	@Autowired
	EventService eventService;
	@Autowired
	RoleService roleService;
	@Autowired
	TatService tatService;
	@Autowired
	AttributeService attributeService;
	@Autowired
	AddressService addressService;
	@Autowired
	StaffService staffService;
	@Autowired
	CountryService countryService;
	@Autowired
	QueueDao queueDao;
	@Autowired
	ReservationValidator reservationValidator;

	@Autowired
	NotificationService notification;
	@Autowired
	BarEntryService barService;
	
	@Autowired
    GuestTagDao guestTagDao;
	
	@Autowired
    CustomerDao customerDao;
	
	@Autowired
	ConversationService conversationService;
	
	@Override
	public void startThreads() {

		startThread(new RetryFailedFbSyncRequests(requestDao,notification));
		startThread(new RemoveAccessToken(tokenDao, authService));
		startThread(new RemoveServer());
		startThread(new ReservationStatusUpdate(restaurantDao, reservationDao));
		startThread(new SMSScheduler(reservationDao, conversationService, authService));
		//startThread(new ExtendRecurEndDateForNever(eventService, authService));
		startThread(new BarEntryStatusUpdate(barService,authService));
		startThreadOneTime(new PopulateMasterData(roleService, tatService, attributeService, addressService, staffService, countryService));
		startThreadEarlyMorning(new ReservationStatusUpdateAtShiftEnd(restaurantDao, reservationDao, queueDao));
		startThreadDNDCheck(new CheckDND(customerDao));
	}

	private void startThreadDNDCheck(Runnable thread) {
		// TODO Auto-generated method stub
		try {
			boolean enable = Boolean.parseBoolean(UtilityMethods.getProperty(Constants.SESSION_CONFIG, thread.getClass().getSimpleName(), Constants.ENABLE));
			if (enable) {
			System.out.println(" startThreadDNDCheck SCHEDULAR STARTS  =========== " + new Date(Calendar.getInstance().getTimeInMillis()));
			CronExpression cronExpression = new CronExpression(UtilityMethods.getProperty(Constants.SESSION_CONFIG, thread.getClass().getSimpleName(), Constants.CRON_EXPRESSION_TO_LAUNCH_THREAD)
					.trim());

			Date nextValidTimeAfter = cronExpression.getNextValidTimeAfter(new Date());
			System.out.println(" NEXT startThreadDNDCheck SCHEDULAR STARTS ============" + nextValidTimeAfter);

			FiniteDuration finiteDuration = Duration.create(nextValidTimeAfter.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

			int restart = Integer.parseInt(UtilityMethods.getProperty(Constants.SESSION_CONFIG, thread.getClass().getSimpleName(), Constants.RESTART).trim());
			Akka.system().scheduler().schedule(finiteDuration, Duration.create(restart, TimeUnit.SECONDS), thread, Akka.system().dispatcher());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startThreadEarlyMorning(Runnable thread) {
		// TODO Auto-generated method stub

		try {
			boolean enable = Boolean.parseBoolean(UtilityMethods.getProperty(Constants.SESSION_CONFIG, thread.getClass().getSimpleName(), Constants.ENABLE));
			if (enable) {
			System.out.println(" startThreadEarlyMorning SCHEDULAR STARTS  =========== " + new Date(Calendar.getInstance().getTimeInMillis()));
			CronExpression cronExpression = new CronExpression(UtilityMethods.getProperty(Constants.SESSION_CONFIG, thread.getClass().getSimpleName(), Constants.CRON_EXPRESSION_TO_LAUNCH_THREAD)
					.trim());

			Date nextValidTimeAfter = cronExpression.getNextValidTimeAfter(new Date());
			System.out.println(" NEXT startThreadEarlyMorning SCHEDULAR STARTS ============" + nextValidTimeAfter);

			FiniteDuration finiteDuration = Duration.create(nextValidTimeAfter.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

			int restart = Integer.parseInt(UtilityMethods.getProperty(Constants.SESSION_CONFIG, thread.getClass().getSimpleName(), Constants.RESTART).trim());
			Akka.system().scheduler().schedule(finiteDuration, Duration.create(restart, TimeUnit.SECONDS), thread, Akka.system().dispatcher());
			}

			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void startThreadOneTime(Runnable thread) {
		boolean enable = Boolean.parseBoolean(UtilityMethods.getProperty(Constants.SESSION_CONFIG, thread.getClass().getSimpleName(), Constants.ENABLE));
		if (enable) {
			int start = Integer.parseInt(UtilityMethods.getProperty(Constants.SESSION_CONFIG, thread.getClass().getSimpleName(), Constants.START).trim());
			Akka.system().scheduler().scheduleOnce(Duration.create(start, TimeUnit.SECONDS), thread, Akka.system().dispatcher());
		}

	}

	private void startThread(Runnable thread) {
		boolean enable = Boolean.parseBoolean(UtilityMethods.getProperty(Constants.SESSION_CONFIG, thread.getClass().getSimpleName(), Constants.ENABLE));
		if (enable) {
			int start = Integer.parseInt(UtilityMethods.getProperty(Constants.SESSION_CONFIG, thread.getClass().getSimpleName(), Constants.START).trim());
			int duration = Integer.parseInt(UtilityMethods.getProperty(Constants.SESSION_CONFIG, thread.getClass().getSimpleName(), Constants.DURATION).trim());
			Logger.info("starting scheduler: "+ thread.getClass().getSimpleName());
			Akka.system().scheduler().schedule(Duration.create(start, TimeUnit.SECONDS), Duration.create(duration, TimeUnit.SECONDS), thread, Akka.system().dispatcher());	
		}

	}

	/*private FiniteDuration calculateDuration() {
		DateTime nextTime = new DateTime();
		if (DateTime.now().getHourOfDay() < 18)
			nextTime = nextTime.withHourOfDay(18).withMinuteOfHour(0).withSecondOfMinute(0);
		else
			nextTime = nextTime.plusDays(1).withHourOfDay(18).withMinuteOfHour(0).withSecondOfMinute(0);
		Seconds seconds = Seconds.secondsBetween(nextTime, DateTime.now());
		return Duration.create(seconds.getSeconds(), TimeUnit.SECONDS);
	}*/

}
