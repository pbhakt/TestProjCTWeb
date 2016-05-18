
package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.GuestProfile;
import com.clicktable.model.TagModelOld;
import com.clicktable.util.RelationshipTypes;

@RelationshipEntity(type = RelationshipTypes.GUEST_HAS_TAG)
public class GuestTagPreferencesRelationshipModel  extends RelEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6483174165849965744L;

	@Fetch
	@StartNode
	GuestProfile guestProfile_tag;
	@Fetch
	@EndNode
	TagModelOld tag;
	
	public String relationship;
	public String rest_id;
	public String type;


	/**
	 * @return the relationship
	 */
	public String getRelationship() {
		return relationship;
	}	
	

	/**
	 * 
	 */
	public GuestTagPreferencesRelationshipModel() {
		super();
	}


	/**
	 * @param tag
	 * @param cust_tag
	 * @param relationship
	 */
	public GuestTagPreferencesRelationshipModel(TagModelOld tag,
			GuestProfile guestProfile_tag, String relationship) {
		super();
		this.tag = tag;
		this.guestProfile_tag = guestProfile_tag;
		this.relationship = relationship;
	}


	/**
	 * @param relationship the relationship to set
	 */
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}




	public TagModelOld getTag() {
		return tag;
	}

	public void setTag(TagModelOld tag) {
		this.tag = tag;
	}

	/**
	 * @return the guestProfile_tag
	 */
	public GuestProfile getGuestProfile_tag() {
		return guestProfile_tag;
	}


	/**
	 * @param guestProfile_tag the guestProfile_tag to set
	 */
	public void setGuestProfile_tag(GuestProfile guestProfile_tag) {
		this.guestProfile_tag = guestProfile_tag;
	}


	/**
	 * @return the rest_id
	 */
	public String getRest_id() {
		return rest_id;
	}


	/**
	 * @param rest_id the rest_id to set
	 */
	public void setRest_id(String rest_id) {
		this.rest_id = rest_id;
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}


	


}

