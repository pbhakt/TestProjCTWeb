package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.libs.Json;

import com.clicktable.dao.intf.CalenderEventDao;
import com.clicktable.dao.intf.CorporateOffersDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.SectionDao;
import com.clicktable.dao.intf.TableDao;
import com.clicktable.model.CustomBlackOutHours;
import com.clicktable.model.CustomOperationalHour;
import com.clicktable.model.CalenderEvent;
import com.clicktable.model.CorporateOffers;
import com.clicktable.model.CustomSetting;
import com.clicktable.model.RestSystemConfigModel;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Section;
import com.clicktable.model.Table;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.service.intf.CustomSettingService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.ValidationError;

@org.springframework.stereotype.Service
public class CustomSettingServiceImpl implements CustomSettingService {

	@Autowired
	RestaurantValidator validateRestObject;

	@Autowired
	CalenderEventDao calDao;

	@Autowired
	TableDao tableDao;
	
	@Autowired
	SectionDao sectionDao;

	@Autowired
	RestaurantDao restDao;

	@Autowired
	CorporateOffersDao corpDao;

	@Override
	public BaseResponse getCustomSetting(Map<String, Object> params) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if ((params.get(Constants.REST_GUID) == null) || params.get(Constants.REST_GUID).equals(""))
			listOfError.add(validateRestObject.createError(Constants.REST_GUID, ErrorCodes.REST_ID_REQUIRED));
		if(listOfError.isEmpty()){
			if(params.containsKey(Constants.UPDATED_AFTER)){
				String date = (String) params.get(Constants.UPDATED_AFTER);
				if(!UtilityMethods.isValidNumericNumber(date)){
					listOfError.add(validateRestObject.createError(Constants.UPDATED_AFTER, ErrorCodes.INVALID_DATE_FORMAT));
				}
			}
		}

		if (listOfError.isEmpty()) {
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			
			CustomSetting setting = new CustomSetting();
			Iterator<Map<String, Object>> itr = restDao.getCustomSystemConfig(params);
			if (itr.hasNext()) {
				Integer barMaxTime = 0;
				Boolean bar = false;
				RestSystemConfigModel restConfig = new RestSystemConfigModel();
				Restaurant restaurant = new Restaurant();
				restaurant.setCreatedDate(null);
				restaurant.setStatus(null);
				restaurant.setCostFor2(null);
				while (itr.hasNext()) {
					Set<Entry<String, Object>> entrySet = itr.next().entrySet();
					Logger.debug("entry set is " + entrySet);
					String name = "";
					Integer value = 0;
					Integer familyTat = 0;
					for (Map.Entry<String, Object> entry : entrySet) {
						Logger.debug("entry is " + entry);
						if (entry.getKey().contains(Constants.NAME)) {
							name = (String) entry.getValue();
						}

						if (entry.getKey().equals(Constants.RESTAURANT_NAME)) {
							restaurant.setName((String) entry.getValue());
						}

						restaurant.setGuid(params.get(Constants.REST_GUID).toString());

						if (entry.getKey().equals(Constants.DISPLAY_NAME)) {
							restaurant.setDisplayName((String) entry.getValue());
						}

						if (entry.getKey().contains(Constants.VALUE)) {
							value = (Integer) entry.getValue();
						}

						if (entry.getKey().contains(Constants.FAMILY_TAT)) {
							familyTat = (Integer) entry.getValue();
						}

						if (entry.getKey().contains(Constants.BAR)) {
							Logger.debug("result is " + entry.getKey()+ " value====" + entry.getValue());
							if (entry.getKey().contains(
									Constants.BAR_MAX_TIME_DB)) {
								Logger.debug("result is " + entry.getKey()
										+ " value====" + entry.getValue());
								if (entry.getValue() != null) {
									barMaxTime = (Integer) entry.getValue();
								}
							} else if (entry.getValue() != null) {
								bar = (Boolean) entry.getValue();
							}
						}

					}

					switch (name) {
					case Constants.TAT_WD_12:
						restConfig.setTat_wd_12(value);
						restConfig.setFamily_tat_wd_12(familyTat);
						break;
					case Constants.TAT_WE_12:
						restConfig.setTat_we_12(value);
						restConfig.setFamily_tat_we_12(familyTat);
						break;
					case Constants.TAT_WD_34:
						restConfig.setTat_wd_34(value);
						restConfig.setFamily_tat_wd_34(familyTat);
						break;
					case Constants.TAT_WE_34:
						restConfig.setTat_we_34(value);
						restConfig.setFamily_tat_we_34(familyTat);
						break;
					case Constants.TAT_WD_56:
						restConfig.setTat_wd_56(value);
						restConfig.setFamily_tat_wd_56(familyTat);
						break;
					case Constants.TAT_WE_56:
						restConfig.setTat_we_56(value);
						restConfig.setFamily_tat_we_56(familyTat);
						break;
					case Constants.TAT_WD_78:
						restConfig.setTat_wd_78(value);
						restConfig.setFamily_tat_wd_78(familyTat);
						break;
					case Constants.TAT_WE_78:
						restConfig.setTat_we_78(value);
						restConfig.setFamily_tat_we_78(familyTat);
						break;
					case Constants.TAT_WD_8P:
						restConfig.setTat_wd_8P(value);
						restConfig.setFamily_tat_wd_8P(familyTat);
						break;
					case Constants.TAT_WE_8P:
						restConfig.setTat_we_8P(value);
						restConfig.setFamily_tat_we_8P(familyTat);
						break;
					default:
						break;
					}
				}

				restConfig.setBar(bar);
				restConfig.setBarMaxTime(barMaxTime);
				setting.setRestaurant(restaurant);
				setting.setConfig(restConfig);
			}
			
			
			
			List<Section> sections= new ArrayList<Section>();
			List<Table> tables= new ArrayList<Table>();
			
			if(params.containsKey(Constants.UPDATED_AFTER)){
				Logger.debug("-------------------updated after---------------------------");
				sections = sectionDao.getCustomSections(params);
				tables = tableDao.getCustomTables(params);
			}else{

				Map<Section, List<Table>> tableAndSections = tableDao
						.getSectionsAndTables(params.get(Constants.REST_GUID)
								.toString());
				sections = tableAndSections.keySet().stream()
				.collect(Collectors.toList());
				
				tables=tableAndSections.values().stream()
				.flatMap(l -> l.stream()).collect(Collectors.toList());
				
			}
			
			if(!sections.isEmpty())
				setting.setSections(sections);
			if(!tables.isEmpty())
				setting.setTables(tables);
			
			List<CorporateOffers> offers = corpDao.getCustomCorporateOffers(params);
			if(!offers.isEmpty())
			setting.setCorporateOffers(offers);
			Logger.debug(Json.toJson(setting).toString());
			
			CustomBlackOutHours blackOutHours = restDao.getCustomBlackOutHours(params);
			setting.setBlackOutHours(blackOutHours);
			
			CustomOperationalHour operationalHours = restDao.getCustomOperationalHours(params);
			setting.setOperationalHours(operationalHours);
			

			params.put(Constants.CATEGORY, Constants.HOLIDAY);
			List<CalenderEvent> holidays = calDao.getHolidays(params);
			if(!holidays.isEmpty())
			setting.setClosedDays(holidays);

			List<CustomSetting> list = new ArrayList<>();
			list.add(setting);
			response = new GetResponse<>(ResponseCodes.RESTAURANT_CUSTOM_SETTING_FETCH_SUCCESFULLY, list);

		} else {
			response = new ErrorResponse(ResponseCodes.RESTAURANT_CUSTOM_SETTING_FETCH_FAILURE,	listOfError);
		}

		return response;
	}

}
