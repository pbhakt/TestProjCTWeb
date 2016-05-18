package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Attribute;
import com.clicktable.model.Restaurant;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.HAS_ATTR)
public class HasAttribute  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5071680785203019973L;
	@Fetch
	@StartNode
	Restaurant rest;
	@Fetch
	@EndNode
	@JsonBackReference(value="attr")
	Attribute attr;
	
	
	 
	 
	
	public HasAttribute() 
	{
	    super();
	}
	
	public HasAttribute(Restaurant rest,Attribute attr)
	{
	    super();
	    this.rest = rest;
	    this.attr = attr;
	}


	public Restaurant getRest() {
	    return rest;
	}

	public void setRest(Restaurant rest) {
	    this.rest = rest;
	}

	

	

	public Attribute getAttr() {
	    return attr;
	}

	public void setAttr(Attribute attr) {
	    this.attr = attr;
	}

	


}
