package com.clicktable.response;

public class Category {
	 private String catId;

	    private String isEnabled;

	    public String getCatId ()
	    {
	        return catId;
	    }

	    public void setCatId (String catId)
	    {
	        this.catId = catId;
	    }

	    public String getIsEnabled ()
	    {
	        return isEnabled;
	    }

	    public void setIsEnabled (String isEnabled)
	    {
	        this.isEnabled = isEnabled;
	    }

	    @Override
	    public String toString()
	    {
	        return "ClassPojo [catId = "+catId+", isEnabled = "+isEnabled+"]";
	    }
}
