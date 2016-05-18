package com.clicktable.relationshipModel;


import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Menu;
import com.clicktable.model.MenuCategory;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.HAS_CATEGORY)
public class HasCategory  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9194279285070743700L;
	@Fetch
	@StartNode
	private Menu menu;
	@Fetch
	@EndNode
	private MenuCategory category;
	

	public Menu getMenu() {
		return menu;
	}
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	public MenuCategory getCategory() {
		return category;
	}
	public void setCategory(MenuCategory category) {
		this.category = category;
	}

}
