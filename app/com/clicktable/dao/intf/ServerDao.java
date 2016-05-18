package com.clicktable.dao.intf;

import com.clicktable.model.Server;

/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface ServerDao extends GenericDao<Server>
{
    boolean addRestaurantServer(Server server) ;
    boolean deleteServer(String serverGuid) ;
    Server checkForColorCode(Server server);
    boolean unassignAllTablesForServer(String serverGuid); 
}
