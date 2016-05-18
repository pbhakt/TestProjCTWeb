package com.clicktable.model;


import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Required;

import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Landmark extends Entity {

	
    /**
     * 
     */
    private static final long serialVersionUID = 7578609187660771497L;
    
    @GraphProperty(propertyName = "name")
	private String name;
    @Required(message=ErrorCodes.LANDMARK_RESTID)
	@GraphProperty(propertyName = "rest_guid")
	private String restGuid;
	

	
	/*@RelatedToVia(type = RelationshipTypes.HAS_LANDMARK)
	@JsonManagedReference(value = "landmark")
	//Collection<HasLandmarkRelationshipModel> hasLandmarkRelation;
	
	/*public HasLandmarkRelationshipModel addRelationTag(Restaurant rest,Landmark landmark) 
	{
	    HasLandmarkRelationshipModel relation_model = new HasLandmarkRelationshipModel(rest,landmark);
	    hasLandmarkRelation = new HashSet<HasLandmarkRelationshipModel>();
	    hasLandmarkRelation.add(relation_model);
	    return relation_model;

	}*/
	

	public String getRestGuid() {
	    return restGuid;
	}

	public void setRestGuid(String restGuid) {
	    this.restGuid = restGuid;
	}

	
	
	
	
	
	

}
