package com.clicktable.model;

import java.util.List;


/**
 * 
 * @author p.singh
 * @company Clicktable Technologies LLP
 */

public class WaitlistResultMobile 
{

    private List<TableWaitingTimeMobile> tblWaitlist;
    private Integer queueCount;
    public List<TableWaitingTimeMobile> getTblWaitlist() {
        return tblWaitlist;
    }
    public void setTblWaitlist(List<TableWaitingTimeMobile> tblWaitlist) {
        this.tblWaitlist = tblWaitlist;
    }
    public Integer getQueueCount() {
        return queueCount;
    }
    public void setQueueCount(Integer queueCount) {
        this.queueCount = queueCount;
    }
    
    
  
  
	
	

}
