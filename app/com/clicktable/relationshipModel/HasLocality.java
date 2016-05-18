package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Locality;
import com.clicktable.model.Region;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.HAS_LOCALITY)
public class HasLocality  extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1289159459604051439L;
	@Fetch
	@StartNode
	private Region region;
	@Fetch
	@EndNode
	private Locality locality;

	public HasLocality() {
		super();
	}

	public HasLocality(Region region, Locality locality) {
		super();
		this.region = region;
		this.locality = locality;
	}
	
	
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public Locality getLocality() {
		return locality;
	}

	public void setLocality(Locality locality) {
		this.locality = locality;
	}

	
	
	
}
