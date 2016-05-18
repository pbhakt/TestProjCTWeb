package com.clicktable.dao.impl;


import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.AddressDao;
import com.clicktable.model.RestaurantAddress;

@Service
public class AddressDaoImpl extends GraphDBDao<RestaurantAddress> implements
		AddressDao {

	public AddressDaoImpl() {
		super();
		this.setType(RestaurantAddress.class);
	}
	
	  


}
