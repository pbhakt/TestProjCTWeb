package com.clicktable.model;

import org.springframework.data.neo4j.annotation.NodeEntity;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class RestaurantAddress extends Entity {



    
        /**
     * 
     */
    private static final long serialVersionUID = -3710864769454447745L;
    
	private String addressLine1;
	private String addressLine2;
	private String country;
	private String state;
	private String city;
	private String locality;
	private String building;
	private Integer zipcode;
	
	
        public RestaurantAddress() 
	{
	    // TODO Auto-generated constructor stub
	}
	
	public RestaurantAddress(Restaurant rest)
	{
	    super();
	    this.addressLine1 = rest.getAddressLine1();
	    this.addressLine2 = rest.getAddressLine2();
	    this.building = rest.getBuilding();
	    this.city = rest.getCity();
	    this.country = rest.getCountryCode();
	    this.locality = rest.getLocality();
	    this.state = rest.getState();
	    this.zipcode = rest.getZipcode();
	    this.setLanguageCode(rest.getLanguageCode());
	    this.setCreatedBy(rest.getCreatedBy());
	    this.setCreatedDate(rest.getCreatedDate());
	    this.setUpdatedBy(rest.getUpdatedBy());
	    this.setUpdatedDate(rest.getUpdatedDate());
	    this.setStatus(Constants.ACTIVE_STATUS);
	    
	}

	
/*	@RelatedToVia(type = RelationshipTypes.HAS_ADDRESS)
	@JsonManagedReference(value = "address")
	Collection<HasAddress> hasAddressRelation;
	
	public HasAddress addRelationTag(Restaurant rest,RestaurantAddress address) 
	{
	    HasAddress relation_model = new HasAddress(rest,address);
	    hasAddressRelation = new HashSet<HasAddress>();
	    hasAddressRelation.add(relation_model);
	    return relation_model;

	}

	
	
	
	public Collection<HasAddress> getHasAddressRelation() {
	    return hasAddressRelation;
	}

	public void setHasAddressRelation(
		Collection<HasAddress> hasAddressRelation) {
	    this.hasAddressRelation = hasAddressRelation;
	}*/

	public String getCountry() {
	    return country;
	}

	public void setCountry(String country) {
	    this.country = country;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getState() {
	    return state;
	}

	public void setState(String state) {
	    this.state = state;
	}

	public String getCity() {
	    return city;
	}

	public void setCity(String city) {
	    this.city = city;
	}

	public String getLocality() {
	    return locality;
	}

	public void setLocality(String locality) {
	    this.locality = locality;
	}

	public String getBuilding() {
	    return building;
	}

	public void setBuilding(String building) {
	    this.building = building;
	}

	public Integer getZipcode() {
	    return zipcode;
	}

	public void setZipcode(Integer zipcode) {
	    this.zipcode = zipcode;
	}
	

	

	
	
	
	
	
	

}
