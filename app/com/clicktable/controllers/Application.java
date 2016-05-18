package com.clicktable.controllers;


import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.views.html.index;
import com.clicktable.views.html.login;


@org.springframework.stereotype.Controller
public class Application extends Controller {
public Result index() {
		
		String user = session("sessionid");
		//System.out.println(user);
		if (user != null) {
			return ok(index.render("Welcome to Clicktable Technology"));
		} else {
			return temporaryRedirect("/auth/login");
		}
	}
	
	

	public Result login() {
		return ok(login.render("Login"));
	}

	public Result logout() {
		session().clear();
		
		
		
	     return temporaryRedirect("/auth/login");
	}
	
	public static Result preflight(String all) {
		//response().setHeader("Access-Control-Allow-Origin", "*");
		response().setHeader("Access-Control-Allow-Origin",  (null==request().getHeader("Origin"))?"*":request().getHeader("Origin").toString());
		//response().setHeader("Allow", "*");
		//response().setHeader("Access-Control-Allow-Credentaials", "*");
		response().setHeader("Access-Control-Allow-Credentials","true");
		response().setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS, PATCH");
		response().setHeader("Access-Control-Allow-Headers", "Origin,'X-Requested-With, Content-Type, Accept, Referer, User-Agent, access_token, mode, Authorization,withCredentials");
		return ok();
	}

	

}
