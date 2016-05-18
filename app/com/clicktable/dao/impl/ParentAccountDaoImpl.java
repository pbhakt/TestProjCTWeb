package com.clicktable.dao.impl;


import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.ParentAccountDao;
import com.clicktable.model.ParentAccount;

@Service
public class ParentAccountDaoImpl extends GraphDBDao<ParentAccount> implements
		ParentAccountDao {

	public ParentAccountDaoImpl() {
		super();
		this.setType(ParentAccount.class);
	}
	
	  


}
