package com.clicktable.model;

import java.util.List;


/**
 * 
 * @author p.singh
 * @company Clicktable Technologies LLP
 */

public class WaitlistResult 
{

    private List<TableWaitingTime> tblWaitlist;
    private Integer queueCount;
    public List<TableWaitingTime> getTblWaitlist() {
        return tblWaitlist;
    }
    public void setTblWaitlist(List<TableWaitingTime> tblWaitlist) {
        this.tblWaitlist = tblWaitlist;
    }
    public Integer getQueueCount() {
        return queueCount;
    }
    public void setQueueCount(Integer queueCount) {
        this.queueCount = queueCount;
    }
    
    
  
  
	
	

}
