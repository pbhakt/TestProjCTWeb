package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Attribute;
import com.clicktable.model.Country;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.PERMISSIBLE_ATTRIBUTES)
public class PermissibleAttributes  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3093279159029205020L;

	@Fetch
	@StartNode
	Country country;
	@Fetch
	@EndNode
	@JsonBackReference(value="attribute")
	Attribute attribute;
	
	
	 
	 
	
	public PermissibleAttributes() 
	{
	    super();
	}
	
	public PermissibleAttributes(Country country,Attribute attribute)
	{
	    super();
	    this.country = country;
	    this.attribute = attribute;
	}



	public Country getCountry() {
	    return country;
	}

	public void setCountry(Country country) {
	    this.country = country;
	}

	public Attribute getAttribute() {
	    return attribute;
	}

	public void setAttribute(Attribute attribute) {
	    this.attribute = attribute;
	}

	


}
