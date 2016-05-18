package com.clicktable.response;

import java.util.Map;

public class DNDResponse {

	 private Response response;

	    private Data data;

	    public Response getResponse ()
	    {
	        return response;
	    }

	    public void setResponse (Response response)
	    {
	        this.response = response;
	    }

	    public Data getData ()
	    {
	        return data;
	    }

	    public void setData (Data data)
	    {
	        this.data = data;
	    }

	    @Override
	    public String toString()
	    {
	        return "ClassPojo [response = "+response+", data = "+data+"]";
	    }
	
	
	
}
