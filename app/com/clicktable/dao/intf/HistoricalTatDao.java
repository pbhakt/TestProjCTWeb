package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.HistoricalTat;
import com.clicktable.model.HistoricalTatResult;

/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface HistoricalTatDao extends GenericDao<HistoricalTat>
{

    Long addRestaurantHistoricalTat(HistoricalTat hist, String restaurantGuid);

    List<HistoricalTatResult> getHistoricalTat(Map<String, Object> params);

	Map<Object, Object> getHistoricalTatStats(Map<String, Object> params);

	Map<Integer, Long> getHistoricalTatMap(Map<String, Object> params);
   
    
}
