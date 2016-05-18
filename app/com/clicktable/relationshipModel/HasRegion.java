package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.City;
import com.clicktable.model.Region;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.HAS_REGION)
public class HasRegion  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6875623484699811779L;

	@Fetch
	@StartNode
	private City city;
	@Fetch
	@EndNode
	private Region region;

	public HasRegion() {
		super();
	}

	public HasRegion(City city,Region region) {
		super();
		this.city = city;
		this.region = region;
	}



	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}
	
	
	
	
}
