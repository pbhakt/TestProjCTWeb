package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.ATTR_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.ATTR_NAME_REQUIRED;

import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author p.singh
 *
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Attribute extends Entity 
{
    	/**
     * 
     */
    private static final long serialVersionUID = -1885399473287385799L;
	@Required(message=ATTR_NAME_REQUIRED)
    	@MaxLength(message=ATTR_NAME_MAX_LENGTH,value=100)
        private String name;
    	
    	
    	/*@RelatedToVia(type = RelationshipTypes.HAS_ATTR)
	@JsonManagedReference(value = "attr")
	@JsonIgnore
	Collection<HasAttribute> hasAttrRelation;*/
    	
	public String getName() {
	    return name;
	}
	public void setName(String name) {
		this.name = name!=null?name.trim():null;
	}
	
	
/*	
	public HasAttribute addRelationTag(Restaurant rest,Attribute attr) 
	{
	    HasAttribute relation_model = new HasAttribute(rest,attr);
	    hasAttrRelation = new HashSet<HasAttribute>();
	    hasAttrRelation.add(relation_model);
	    return relation_model;

	}
	*/

	/*public Collection<HasAttribute> getHasAttrRelation() {
	    return hasAttrRelation;
	}
	public void setHasAttrRelation(
		Collection<HasAttribute> hasAttrRelation) {
	    this.hasAttrRelation = hasAttrRelation;
	}
	*/
	

}
