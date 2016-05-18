package com.clicktable.filter;


import org.springframework.beans.factory.annotation.Autowired;

import play.api.libs.iteratee.Iteratee;
import play.api.mvc.EssentialAction;
import play.api.mvc.EssentialFilter;
import play.api.mvc.RequestHeader;
import play.api.mvc.Result;
import scala.runtime.AbstractFunction1;

import com.clicktable.service.intf.AuthorizationService;

public class AccessFilter implements EssentialFilter{
	
	@Autowired
	AuthorizationService authService;

	@Override
	public EssentialAction apply(EssentialAction action) {
		// TODO Auto-generated method stub
		return new AccessControlAction(){
			@Override
			public EssentialAction apply() {
				// TODO Auto-generated method stub
				return action.apply();
			}
		};
	}
	
	private abstract class AccessControlAction extends
    AbstractFunction1<RequestHeader, Iteratee<byte[], Result>> implements EssentialAction{

		@Override
		public Iteratee<byte[], Result> apply(RequestHeader requestHeader) {
			//String token= requestHeader.headers().get("access_token").get();
			//Role role=authService.getRoleByToken(token);
			//if(!authService.hasAccess(role, requestHeader.uri()))
				//return 
			return null;
		}

	}
}
