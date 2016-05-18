package com.clicktable.dao.intf;

import com.clicktable.model.Tat;

/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface TatDao extends GenericDao<Tat>
{

	int get_tat_value(String rest_guid, int num_covers, String day);
  
}
