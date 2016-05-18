package com.clicktable.dao.intf;

import com.clicktable.model.Device;

/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface DeviceDao extends GenericDao<Device>
{
   
    public Long addRestaurantDevice(Device device, String restaurantGuid);
    public boolean deleteDevice(String deviceGuid);
}
