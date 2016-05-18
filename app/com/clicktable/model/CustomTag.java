
package com.clicktable.model;

import java.io.Serializable;



/**
 * 
 * @author p.singh
 *
 */
 

public class CustomTag implements Serializable
{

    	/**
	 * 
	 */
	private static final long serialVersionUID = 6333500141372745729L;
		private String name;
        private String addedBy;
        private String guid;
        private String type;
	


	public CustomTag() 
	{
	// TODO Auto-generated constructor stub
	    super();
	}
	
	public CustomTag(TagModelOld tag)
	{
	    this.setGuid(tag.getGuid());
	    this.setName(tag.getName());
	    this.setAddedBy(tag.getAddedBy());
	    
	}
	
	public CustomTag(Tag tag)
	{
	    this.setGuid(tag.getGuid());
	    this.setName(tag.getName());
	    this.setAddedBy(tag.getAddedBy());
	    this.setType(tag.getType());
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
	}

	public String getGuid() {
	    return guid;
	}

	public void setGuid(String guid) {
	    this.guid = guid;
	}

	public String getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}

	



	

	
	
	
}
