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
public class HasAttr  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5344016397052232248L;
	@Fetch
	@StartNode
	Restaurant rest;
	@Fetch
	@EndNode
	@JsonBackReference(value="attribute")
	Attribute attribute;
	
	
	 
	 
	
	public HasAttr() 
	{
	    super();
	}
	
	public HasAttr(Restaurant rest,Attribute attribute)
	{
	    super();
	    this.rest = rest;
	    this.attribute = attribute;
	}


	public Restaurant getRest() {
	    return rest;
	}

	public void setRest(Restaurant rest) {
	    this.rest = rest;
	}

	
	

	public Attribute getAttribute() {
	    return attribute;
	}

	public void setAttribute(Attribute attribute) {
	    this.attribute = attribute;
	}

	
	


}
