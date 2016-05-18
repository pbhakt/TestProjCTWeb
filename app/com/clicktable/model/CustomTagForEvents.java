
package com.clicktable.model;



/**
 * 
 * @author p.singh
 *
 */
 

public class CustomTagForEvents extends CustomTag
{

        /**
	 * 
	 */
	private static final long serialVersionUID = 8232902575099458645L;
		private Integer count;


	public CustomTagForEvents() 
	{
	// TODO Auto-generated constructor stub
	    super();
	}
	
	public CustomTagForEvents(TagModelOld tag, Integer count)
	{
	    this.setGuid(tag.getGuid());
	    this.setName(tag.getName());
	    this.setAddedBy(tag.getAddedBy());
	    this.setCount(count);
	}
	
	public CustomTagForEvents(Tag tag, Integer count)
	{
	    this.setGuid(tag.getGuid());
	    this.setName(tag.getName());
	    this.setAddedBy(tag.getAddedBy());
	    this.setCount(count);
	    this.setType(tag.getType());
	}

	public Integer getCount() {
	    return count;
	}

	public void setCount(Integer count) {
	    this.count = count;
	}

	

	

	
	
	
}
