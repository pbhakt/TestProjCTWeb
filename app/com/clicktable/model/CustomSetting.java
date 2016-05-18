package com.clicktable.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class CustomSetting {
	
	private Restaurant restaurant;
	
	private RestSystemConfigModel config;
	
	private CustomOperationalHour operationalHours;
	
	private CustomBlackOutHours blackOutHours;
	
	private List<CalenderEvent> closedDays;
	
	private List<Section> sections;
	
	private List<Table> tables;
	
	private List<CorporateOffers> corporateOffers;

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	
	public RestSystemConfigModel getConfig() {
		return config;
	}

	public void setConfig(RestSystemConfigModel config) {
		this.config = config;
	}

	public CustomOperationalHour getOperationalHours() {
		return operationalHours;
	}

	public void setOperationalHours(CustomOperationalHour operationalHours) {
		this.operationalHours = operationalHours;
	}

	public CustomBlackOutHours getBlackOutHours() {
		return blackOutHours;
	}

	public void setBlackOutHours(CustomBlackOutHours blackOutHours) {
		this.blackOutHours = blackOutHours;
	}

	public List<CalenderEvent> getClosedDays() {
		return closedDays;
	}

	public void setClosedDays(List<CalenderEvent> closedDays) {
		this.closedDays = closedDays;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	public List<CorporateOffers> getCorporateOffers() {
		return corporateOffers;
	}

	public void setCorporateOffers(List<CorporateOffers> corporateOffers) {
		this.corporateOffers = corporateOffers;
	}

}
