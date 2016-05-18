package com.clicktable.service.intf;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.tenant.Tenant;

public interface StormpathService {
		   	  
	    public Client getClient() ;

	    
	    public Tenant getTenant();
	   
	    
	    public Application getApplication();
	    	
	    
	    public Directory getDirectory();

		
}
