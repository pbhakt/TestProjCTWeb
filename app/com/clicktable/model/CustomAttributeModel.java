package com.clicktable.model;


/**
 * 
 * @author p.singh
 *
 */

public class CustomAttributeModel extends Attribute 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -6894658111391914939L;
	private boolean restHasAttr;
    
    public CustomAttributeModel()
    {
	super();
    }
    
    public CustomAttributeModel(Attribute attr)
    {
	this.setCreatedBy(attr.getCreatedBy());
	this.setCreatedDate(attr.getCreatedDate());
	this.setGuid(attr.getGuid());
	this.setId(attr.getId());
	this.setLanguageCode(attr.getLanguageCode());
	this.setName(attr.getName());
	this.setStatus(attr.getStatus());
	this.setUpdatedBy(attr.getUpdatedBy());
	this.setUpdatedDate(attr.getUpdatedDate());
    }

    public boolean isRestHasAttr() {
        return restHasAttr;
    }

    public void setRestHasAttr(boolean restHasAttr) {
        this.restHasAttr = restHasAttr;
    }
    
    
    
	

}
