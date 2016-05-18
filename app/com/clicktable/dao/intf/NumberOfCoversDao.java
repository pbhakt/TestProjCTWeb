package com.clicktable.dao.intf;

import com.clicktable.model.DayOfWeek;
import com.clicktable.model.NumberOfCovers;

/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface NumberOfCoversDao extends GenericDao<NumberOfCovers>
{

    Long addForNumberOfCovers(NumberOfCovers covers, DayOfWeek dayOfWeek , Integer tatValue);
   
    
}
