package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Restaurant;
import com.clicktable.model.Section;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.HAS_SECTION)
public class HasSection  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5033072227131881110L;
	@Fetch
	@StartNode
	Restaurant rest;
	@Fetch
	@EndNode
	@JsonBackReference(value="section")
	Section section;
	
	
	 
	 
	
	public HasSection() 
	{
	    super();
	}
	
	public HasSection(Restaurant rest,Section section)
	{
	    super();
	    this.rest = rest;
	    this.section = section;
	}

	public Restaurant getRest() {
	    return rest;
	}

	public void setRest(Restaurant rest) {
	    this.rest = rest;
	}

	
	public Section getSection() {
	    return section;
	}

	public void setSection(Section section) {
	    this.section = section;
	}


	


}
