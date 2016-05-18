package com.clicktable.scheduler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import play.Play;
import play.libs.Json;

import com.clicktable.model.Attribute;
import com.clicktable.model.Country;
import com.clicktable.model.Role;
import com.clicktable.model.Staff;
import com.clicktable.model.Tat;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.service.intf.AddressService;
import com.clicktable.service.intf.AttributeService;
import com.clicktable.service.intf.CountryService;
import com.clicktable.service.intf.RoleService;
import com.clicktable.service.intf.StaffService;
import com.clicktable.service.intf.TatService;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class PopulateMasterData implements Runnable {
	
	RoleService roleService;
	
	TatService tatService;

	AttributeService attributeService;
	
	AddressService addressService;
	
	StaffService staffService;
	
	CountryService countryService;
	
	public PopulateMasterData(RoleService roleService, TatService tatService,
			AttributeService attributeService, AddressService addressService,
			StaffService staffService, CountryService countryService) {
		super();
		this.roleService = roleService;
		this.tatService = tatService;
		this.attributeService = attributeService;
		this.addressService = addressService;
		this.staffService = staffService;
		this.countryService =countryService;
	}

	@Override
	public void run() {
		

		JsonNode json = UtilityMethods.readJsonFromFile("master.json", "masterdata");
		List<Attribute> attributes = new ArrayList<Attribute>();
		List<Role> roles = new ArrayList<Role>();
		List<Tat> tats = new ArrayList<Tat>();
		Staff ctadmin = null, internaladmin = null ;

		for (Iterator<Entry<String, JsonNode>> itr1 = json.fields(); itr1.hasNext();) {
			Entry<String, JsonNode> entry = itr1.next();
			String uri = entry.getKey();
			ArrayNode arr = (ArrayNode)entry.getValue();

			if(uri.equals("Attribute")){
				Iterator<JsonNode> itr = arr.iterator();
				while (itr.hasNext()) {
					JsonNode attrJson = itr.next();
					Attribute attribute =  Json.fromJson(attrJson, Attribute.class);
					attributes.add(attribute);
				}

			}else if(uri.equals("Role")){
				Iterator<JsonNode> itr = arr.iterator();
				while (itr.hasNext()) {
					JsonNode roleJson = itr.next();
					Role role =  Json.fromJson(roleJson, Role.class);
					roles.add(role);

				}
			}else if(uri.equals("Tat")){
				Iterator<JsonNode> itr = arr.iterator();
				while (itr.hasNext()) {
					JsonNode tatJson = itr.next();
					Tat tat =  Json.fromJson(tatJson, Tat.class);					
					tats.add(tat);					
				}
			}else if(uri.equals("Staff")){
									
					JsonNode staffJson = arr.get(0);
					ctadmin =  Json.fromJson(staffJson, Staff.class);				
				
					JsonNode staffJson1 = arr.get(1);
					internaladmin =  Json.fromJson(staffJson1, Staff.class);
			}
		
		}
		
	
		// Create CT-Admin
		if(ctadmin != null){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.EMAIL, ctadmin.getEmail());
			GetResponse<Staff> response = (GetResponse<Staff>)staffService.getStaffMembers(params);
			if(response.getList().isEmpty()){
				ctadmin.setGuid(UtilityMethods.generateCtId());
				ctadmin.setCreatedBy(ctadmin.getGuid());
				ctadmin.setUpdatedBy(ctadmin.getGuid());
				staffService.addStaffMember(ctadmin);
			}else
				ctadmin = response.getList().get(0);
		}
		
		// Create Internal CT-Admin
				if(internaladmin != null){
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(Constants.EMAIL, internaladmin.getEmail());
					GetResponse<Staff> response = (GetResponse<Staff>)staffService.getStaffMembers(params);
					if(response.getList().isEmpty()){
						internaladmin.setGuid(UtilityMethods.generateCtId());
						internaladmin.setCreatedBy(internaladmin.getGuid());
						internaladmin.setUpdatedBy(internaladmin.getGuid());
						staffService.addStaffMember(internaladmin);
					}else
						internaladmin = response.getList().get(0);
				}
		
		// Insert Roles
		if(roles!=null && roles.size()>0){
			Role role = roles.get(0);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.ROLE_ID, role.getId());			
			GetResponse<Role> response = (GetResponse<Role>)roleService.getRole(params);
			if(response.getList().isEmpty())
				roleService.addRoles(roles);
		}
		
		// Insert TATs
		if(tats!=null && tats.size()>0){
			Tat tat0 = tats.get(0);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.NAME, tat0.getName());			
			GetResponse<Tat> response = (GetResponse<Tat>)tatService.getTats(params);
			if(response.getList().isEmpty()){
				for(Tat tat:tats){
					tat.setGuid(UtilityMethods.generateCtId());
					tat.setCreatedBy(internaladmin.getGuid());
					tat.setUpdatedBy(internaladmin.getGuid());
					tat.setStatus(Constants.ACTIVE_STATUS);
					tat.setValuesFromName();				
				}			
				tatService.addTats(tats);

			}			
		}
		
		// Insert Adress data
		GetResponse<Country> get_country_response = (GetResponse<Country>) countryService
				.getCountry(new HashMap<String, Object>());
		
		if (get_country_response.getList().isEmpty()) {
			InputStream is = Play.application().resourceAsStream("Book2.xls");
			addressService.addAddress(is);
		}
		
		
		// Insert Attributes
		if(attributes!=null && attributes.size()>0){
			Attribute attr = attributes.get(0);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.NAME, attr.getName());			
			GetResponse<Attribute> response2 = (GetResponse<Attribute>)attributeService.getAttributes(params);
			if(response2.getList().isEmpty()){
				for(Attribute attribute:attributes){
					attribute.setGuid(UtilityMethods.generateCtId());
					attribute.setCreatedBy(internaladmin.getGuid());
					attribute.setUpdatedBy(internaladmin.getGuid());
					attribute.setStatus(Constants.ACTIVE_STATUS);
				}
				
				PostResponse<Attribute> res = (PostResponse<Attribute>)attributeService.addAttributes(attributes);
				
				//GetResponse<Attribute> response4 = (GetResponse<Attribute>)attributeService.getAttributes(new HashMap<String, Object>());
				//List<Attribute> attrlist = response4.getList();
				
				Object[] list_of_attr = res.getGuid();
				String atr =  (String) list_of_attr[0];
				int endindex = atr.length()-1;
				String attribute_guids = (atr.substring(1, endindex)).replaceAll("\\s+", "");
				
				GetResponse<Country> response3 = (GetResponse<Country>) countryService
						.getCountry(new HashMap<String, Object>());
				List<Country> countries = response3.getList();
				//String attribute_guids = "";
				/*for(Object attrib:atr){
					attribute_guids = attribute_guids+(","+attrib.toString());
				}*/
				if(attribute_guids.length()>	0)
					attribute_guids = attribute_guids.substring(1);
				for(Country country:countries){
					String countryGuid = country.getGuid();
					if(!attribute_guids.equals("") && !countryGuid.equals("")){
					 attributeService.addCountryAttributes(countryGuid, attribute_guids, null);
					
					}
					}
				
				
			}
		}
		
		

	
	}



}