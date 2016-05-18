package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Restaurant;
import com.clicktable.model.Table;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

@RelationshipEntity(type = RelationshipTypes.REST_HAS_TBL)
public class RestaurantHasTable  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4612882125879100868L;


	@Fetch
	@StartNode
	@JsonBackReference(value="rest")
	Restaurant rest;

	@Fetch
	@EndNode
	Table table;

	@GraphProperty(propertyName = "min_covers")
	private Integer minCovers;
	@GraphProperty(propertyName = "max_covers")
	private Integer maxCovers;

	public Integer getMinCovers() {
		return minCovers;
	}

	public void setMinCovers(Integer minCovers) {
		this.minCovers = minCovers;
	}

	public Integer getMaxCovers() {
		return maxCovers;
	}

	public void setMaxCovers(Integer maxCovers) {
		this.maxCovers = maxCovers;
	}

	public Restaurant getRest() {
		return rest;
	}

	public void setRest(Restaurant rest) {
		this.rest = rest;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * 
	 */
	public RestaurantHasTable() {
		super();
	}

	public RestaurantHasTable(Restaurant rest, Table table) {
		// super();
		this.table = table;
		this.rest = rest;
		this.minCovers = table.getMinCovers();
		this.maxCovers = table.getMaxCovers();

	}

}
