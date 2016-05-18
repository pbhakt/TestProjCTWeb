package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Building;
import com.clicktable.model.Locality;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.HAS_BUILDING)
public class HasBuilding  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -239970805065351646L;
	@Fetch
	@StartNode
	private Locality locality;
	@Fetch
	@EndNode
	private Building building;

	public HasBuilding() {
		super();
	}

	public HasBuilding(Locality locality, Building building) {
		super();
		this.locality = locality;
		this.building = building;
	}
	
	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

	public Locality getLocality() {
		return locality;
	}

	public void setLocality(Locality locality) {
		this.locality = locality;
	}

	
	
	
}
