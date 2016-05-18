package com.clicktable.dao.intf;

import com.clicktable.model.DayOfWeek;
import com.clicktable.model.HistoricalTat;

/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface DayOfWeekDao extends GenericDao<DayOfWeek>
{

    Long addDayOfWeek(HistoricalTat hist, String daysOfW);
   
    
}
