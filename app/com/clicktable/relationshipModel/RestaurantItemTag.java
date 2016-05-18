package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.ItemTag;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.REST_ITEM_TAG)
public class RestaurantItemTag  extends RelEntity{

		/**
	 * 
	 */
	private static final long serialVersionUID = 7392732441879770096L;
		@Fetch
		@StartNode
		private ItemTag itemTag;
		@Fetch
		@EndNode
		private Restaurant restaurant;
		
		private String city;
		private String area;
		private String state;
		private String locality;
		private String building;
		
	
		public ItemTag getItemTag() {
			return itemTag;
		}
		public void setItemTag(ItemTag itemTag) {
			this.itemTag = itemTag;
		}
		public Restaurant getRestaurant() {
			return restaurant;
		}
		public void setRestaurant(Restaurant restaurant) {
			this.restaurant = restaurant;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getArea() {
			return area;
		}
		public void setArea(String area) {
			this.area = area;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getLocality() {
			return locality;
		}
		public void setLocality(String locality) {
			this.locality = locality;
		}
		public String getBuilding() {
			return building;
		}
		public void setBuilding(String building) {
			this.building = building;
		}
}
