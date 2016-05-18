package com.clicktable.dao.intf;

import java.util.List;

import com.clicktable.model.CalculatedTat;
import com.clicktable.model.NumberOfCovers;

/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface CalculatedTatDao extends GenericDao<CalculatedTat>
{

    //Long addForNumberOfCovers(NumberOfCovers covers, DayOfWeek dayOfWeek , Integer tatValue);

    void addTatValue(NumberOfCovers cover, List<CalculatedTat> calTatList, int tatValue);
   
    
}
