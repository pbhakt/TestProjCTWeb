package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.MenuItem;
import com.clicktable.model.MenuSubCategory;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.HAS_ITEMS)
public class HasItems  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9088528131431016414L;
	@Fetch
	@StartNode
	private MenuSubCategory subCategory;
	@Fetch
	@EndNode
	private MenuItem menuItem;
	
	public MenuSubCategory getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(MenuSubCategory subCategory) {
		this.subCategory = subCategory;
	}
	public MenuItem getMenuItem() {
		return menuItem;
	}
	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

}
