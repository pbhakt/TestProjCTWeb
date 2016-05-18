package com.clicktable.controllers;

import static com.clicktable.util.Constants.ACCESS_TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.clicktable.model.Menu;
import com.clicktable.model.MenuCategory;
import com.clicktable.model.MenuItem;
import com.clicktable.model.MenuSubCategory;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.MenuService;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.stereotype.Controller
public class MenuController  extends Controller{
	
	@Autowired
	AuthorizationService authService;
	
	@Autowired
	MenuService menuService;
	
	public Result addMenu() {
		JsonNode json = request().body().asJson();		
		Menu menu = Json.fromJson(json, Menu.class);
		menu.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = menuService.addMenu(menu, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateMenu() {
		JsonNode json = request().body().asJson();		
		Menu menu = Json.fromJson(json, Menu.class);
		menu.setInfoOnUpdate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = menuService.updateMenu(menu, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result getMenu() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = menuService.getMenu(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result addMenuCategory() {
		JsonNode json = request().body().asJson();		
		MenuCategory menuCategory = Json.fromJson(json, MenuCategory.class);	
		menuCategory.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = menuService.addMenuCategory(menuCategory, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateMenuCategory() {
		JsonNode json = request().body().asJson();		
		MenuCategory menuCategory = Json.fromJson(json, MenuCategory.class);	
		menuCategory.setInfoOnUpdate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = menuService.updateMenuCategory(menuCategory, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result getMenuCategory() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = menuService.getMenuCategory(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result addMenuSubCategory() {
		JsonNode json = request().body().asJson();		
		MenuSubCategory menuSubCategory = Json.fromJson(json, MenuSubCategory.class);	
		menuSubCategory.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = menuService.addMenuSubCategory(menuSubCategory, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result updateMenuSubCategory() {
		JsonNode json = request().body().asJson();		
		MenuSubCategory menuSubCategory = Json.fromJson(json, MenuSubCategory.class);	
		menuSubCategory.setInfoOnUpdate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = menuService.updateMenuSubCategory(menuSubCategory, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result getMenuSubCategory() {
		Map<String, Object> stringParamMap = UtilityMethods.convertQueryStringToMap(request().queryString());
		BaseResponse response = menuService.getMenuSubCategory(stringParamMap, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	public Result addMenuItem() {
		JsonNode json = request().body().asJson();		
		MenuItem menuItem = Json.fromJson(json, MenuItem.class);	
		menuItem.setInfoOnCreate(authService.getUserInfoByToken(request().getHeader(ACCESS_TOKEN)));
		BaseResponse response = menuService.addMenuItem(menuItem, request().getHeader(ACCESS_TOKEN));
		JsonNode result = Json.toJson(response);
		Logger.debug(Json.stringify(result));
		return ok(result);
	}
	
	
}
