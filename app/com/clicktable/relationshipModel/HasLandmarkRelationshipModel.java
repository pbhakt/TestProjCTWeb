package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Landmark;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.HAS_LANDMARK)
public class HasLandmarkRelationshipModel  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8245745324137845645L;
	@Fetch
	@StartNode
	Restaurant rest;
	@Fetch
	@EndNode
	@JsonBackReference(value="landmark")
	Landmark landmark;
	
	
	public HasLandmarkRelationshipModel() 
	{
	    super();
	}
	
	public HasLandmarkRelationshipModel(Restaurant rest,Landmark landmark)
	{
	    super();
	    this.rest = rest;
	    this.landmark = landmark;
	}

	public Restaurant getRest() {
	    return rest;
	}

	public void setRest(Restaurant rest) {
	    this.rest = rest;
	}

	

	public Landmark getLandmark() {
	    return landmark;
	}

	public void setLandmark(Landmark landmark) {
	    this.landmark = landmark;
	}


	


}
