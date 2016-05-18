package com.clicktable.scheduler;

import static com.clicktable.util.Constants.FAILURE;
import static com.clicktable.util.Constants.MAX_RETRY_COUNT;
import static com.clicktable.util.Constants.NOT_FOUND;
import static com.clicktable.util.Constants.RETRY_COUNT_LESS;
import static com.clicktable.util.Constants.UNAVAILABLE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import play.Logger;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;

import com.clicktable.dao.intf.WSRequestDao;
import com.clicktable.model.WSRequest;
import com.clicktable.service.intf.NotificationService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;

public class RetryFailedFbSyncRequests implements Runnable {

	private static Lock lock = new ReentrantLock();

	WSRequestDao requestDao;
	NotificationService notification;
	public RetryFailedFbSyncRequests(WSRequestDao requestDao, NotificationService notification) {
		this.requestDao = requestDao;
		this.notification = notification;
	}

	@Override
	public void run() {
		if (RetryFailedFbSyncRequests.lock.tryLock()) {
			try {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(RETRY_COUNT_LESS, MAX_RETRY_COUNT);
				List<WSRequest> requests = requestDao.findByFields(WSRequest.class, params);
				for (WSRequest request : requests) {
					String message;
					try {
						Integer status = callWebService(request);

						if (status == 200) {
							requestDao.delete(WSRequest.class, request.getId());
							message = "Success: " + request.getMethod() + ":" + request.getUri() + ":" + request.getInputJson();
							Logger.info(message);
						} else if (status == 400) {
							requestDao.delete(WSRequest.class, request);
							message = "Bad Data: " + request.getMethod() + ":" + request.getUri() + ":" + request.getInputJson();
							Logger.error(message);
						} else if (status == 404) {
							request.setRetryCount(request.getRetryCount() + 1);
							request.setResponseStatus(status.toString());
							request.setUpdatedDate(new Date());
							request.setStatus(NOT_FOUND);
							requestDao.update(request);
							message = "Not Found: " + request.getMethod() + ":" + request.getUri() + ":" + request.getInputJson();
							Logger.error(message);
						} else {
							request.setResponseStatus(status.toString());
							request.setUpdatedDate(new Date());
							request.setRetryCount(request.getRetryCount() + 1);
							request.setStatus(UNAVAILABLE);
							requestDao.update(request);
							message = "Unavailable: " + request.getMethod() + ":" + request.getUri() + ":" + request.getInputJson();
							Logger.error(message);
						}
					} catch (Exception e) {
						request.setRetryCount(request.getRetryCount() + 1);
						request.setStatus(FAILURE);
						request.setError(e.getMessage());
						requestDao.update(request);
						message = "Error (" + e.getMessage() + "): " + request.getMethod() + ":" + request.getUri() + ":" + request.getInputJson();
						Logger.error(message);
					}

					ArrayList<String> to = new ArrayList<String>();
					ArrayList<String> tags = new ArrayList<String>();
					to.add(UtilityMethods.getConfString(Constants.SUPPORT_USERNAME));
					tags.add(Constants.FIREBASE);
					notification.sendEmail(to, "Firebase Sync Request Retry Notifaction", message, tags);
				}
			}catch(Exception e){
				Logger.info(" RetryFailedFbSyncRequests Exception is ..........");	
				Logger.debug("RetryFailedFbSyncRequests Exception is........... "+e.getMessage());
				Logger.error("RetryFailedFbSyncRequests Exception is........... "+e.getMessage());
			
			}
			finally {
				RetryFailedFbSyncRequests.lock.unlock();
			}
		}

	}

	private Integer callWebService(WSRequest request) {
		Promise<WSResponse> res = null;
		WSRequestHolder req = WS.url(request.getUri());
		if (request.getMethod().equals("PATCH")) {
			req.setContentType("application/json");
			res = req.patch(request.getInputJson());
		} else if (request.getMethod().equals("DELETE")) {
			res = req.delete();
		}

		if (res != null) {
			Promise<Integer> status = res.map(x -> {
				return x.getStatus();
			});
			return status.get(5000);
		} else
			return 0;
	}

	/*
	 * public String sendMail(String message) { Email email = new Email();
	 * email.setSubject("Firebase Sync Request Retry Notifaction");
	 * email.setFrom("no-reply@clicktable.com");
	 * email.addTo("support@clicktable.com"); email.setBodyText(message);
	 * 
	 * String id =MailerPlugin.send(email); return id; }
	 */

}
