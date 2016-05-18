package com.clicktable.relationshipModel;


import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.MenuCategory;
import com.clicktable.model.MenuSubCategory;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.HAS_SUB_CATEGORY)
public class HasSubCategory  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1478098686185731215L;
	@Fetch
	@StartNode
	private MenuCategory category;
	@Fetch
	@EndNode
	private MenuSubCategory subCategory;
	
	public MenuCategory getCategory() {
		return category;
	}
	public void setCategory(MenuCategory category) {
		this.category = category;
	}
	public MenuSubCategory getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(MenuSubCategory subCategory) {
		this.subCategory = subCategory;
	}

}
