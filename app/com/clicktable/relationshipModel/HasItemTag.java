package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.ItemTag;
import com.clicktable.model.MenuItem;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.HAS_ITEM_TAG)
public class HasItemTag  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2277065582433723914L;

	@Fetch
	@StartNode
	private MenuItem menuItem;
	@Fetch
	@EndNode
	private ItemTag itemTag;
	

	public MenuItem getMenuItem() {
		return menuItem;
	}
	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}
	public ItemTag getItemTag() {
		return itemTag;
	}
	public void setItemTag(ItemTag itemTag) {
		this.itemTag = itemTag;
	}

}
