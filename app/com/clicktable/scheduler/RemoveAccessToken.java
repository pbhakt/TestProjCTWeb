package com.clicktable.scheduler;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import play.Logger;

import com.clicktable.dao.intf.UserTokenDao;
import com.clicktable.model.UserToken;
import com.clicktable.service.intf.AuthorizationService;

public class RemoveAccessToken implements Runnable {

	private static Lock lock = new ReentrantLock();
	UserTokenDao tokenDao;
	AuthorizationService authService;

	public RemoveAccessToken(UserTokenDao tokenDao, AuthorizationService authService) {
		this.tokenDao= tokenDao;
		this.authService=authService;
	}

	@Override
	public void run() {
		if(RemoveAccessToken.lock.tryLock()){
			try{	
				List<UserToken> tokens = tokenDao.findAll(UserToken.class);
				for(UserToken token:tokens){
					if(!authService.isRecentToken(token.getToken()))
						tokenDao.deleteToken(token);
				}
			}catch(Exception e){
				Logger.info("RemoveAccessToken Exception is ..........");	
				Logger.debug("RemoveAccessToken Exception is........... "+e.getMessage());
				Logger.error("RemoveAccessToken Exception is........... "+e.getMessage());
			}
			finally{
				RemoveAccessToken.lock.unlock();
			}
		}
	}

}

