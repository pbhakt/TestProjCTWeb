package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Menu;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.HAS_MENU)
public class HasMenu  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5991245588673284379L;
	@Fetch
	@StartNode
	private Restaurant restaurant;
	@Fetch
	@EndNode
	private Menu menu;
	
	private boolean published;
	
	public Restaurant getRestaurant() {
		return restaurant;
	}
	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}
	public Menu getMenu() {
		return menu;
	}
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	
}
