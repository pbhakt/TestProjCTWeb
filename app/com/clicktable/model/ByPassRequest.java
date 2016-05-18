package com.clicktable.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import play.libs.Json;

import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;

public class ByPassRequest {
private Map<String,Map<String,List<Role>>> permissions;
	
	public ByPassRequest() {
		permissions = calculatePermissions(Constants.BYPASS_REQ_FILE);
		//Logger.debug("permissions are "+permissions);
	}

	public Map<String, Map<String, List<Role>>> getPermissions() {
		return permissions;
	}
	
	public List<Role> getRoles(String uri, String method){
		List<Role> roleList = new ArrayList<Role>();
		
		if(permissions.containsKey(uri)){
			Map<String, List<Role>> permission = permissions.get(uri);			
				if(permission.containsKey(method))
					roleList = permission.get(method);
		}
		return roleList;
	}
	public static Map<String, Map<String, List<Role>>> calculatePermissions(String filename) {

		Map<String, Map<String, List<Role>>> permissions = new HashMap<String, Map<String, List<Role>>>();

		JsonNode json = UtilityMethods.readJsonFromFile(filename, "ApiPermissions");

		for (Iterator<Entry<String, JsonNode>> itr1 = json.fields(); itr1.hasNext();) {
			Entry<String, JsonNode> entry = itr1.next();
			String uri = entry.getKey();
			Map<String, List<Role>> permissionMap = new HashMap<String, List<Role>>();
			for (Iterator<JsonNode> itr = entry.getValue().elements(); itr.hasNext();) {
				JsonNode elem = itr.next();
				for (Iterator<Entry<String, JsonNode>> itr2 = elem.fields(); itr2.hasNext();) {
					Entry<String, JsonNode> entry1 = itr2.next();
					String method = entry1.getKey();
					List<Role> roleList = new ArrayList<Role>();
					for (Iterator<JsonNode> itr3 = entry1.getValue().elements(); itr3.hasNext();) {
						Role role = Json.fromJson(itr3.next(), Role.class);
						roleList.add(role);
					}
					permissionMap.put(method, roleList);
				}
				permissions.put(uri, permissionMap);
			}
		}

		return permissions;
	}
}
