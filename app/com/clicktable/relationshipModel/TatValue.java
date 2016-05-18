package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.CalculatedTat;
import com.clicktable.model.NumberOfCovers;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.TAT_VALUE)
public class TatValue extends RelEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7238827102821844845L;


	@Fetch
	@StartNode
	NumberOfCovers cover;

	@Fetch
	@EndNode
	CalculatedTat tat;
	
	private String type;

	

	public NumberOfCovers getCover() {
	    return cover;
	}

	public void setCover(NumberOfCovers cover) {
	    this.cover = cover;
	}

	public CalculatedTat getTat() {
	    return tat;
	}

	public void setTat(CalculatedTat tat) {
	    this.tat = tat;
	}

	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
	}

	/**
	 * 
	 */
	public TatValue() {
		super();
	}


	

}
