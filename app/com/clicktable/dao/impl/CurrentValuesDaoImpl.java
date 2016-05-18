package com.clicktable.dao.impl;

import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.CurrentValuesDao;
import com.clicktable.model.CurrentValues;

@Service
public class CurrentValuesDaoImpl extends GraphDBDao<CurrentValues> implements CurrentValuesDao {

	public CurrentValuesDaoImpl() {
		super();
		this.setType(CurrentValues.class);
	}

}
