package com.clicktable.dao.impl;

import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.AccountValuesDao;
import com.clicktable.model.AccountIdUnique;


@Service
public class AccountValuesDaoImpl extends GraphDBDao<AccountIdUnique> implements
AccountValuesDao {

	public AccountValuesDaoImpl() {
		super();
		this.setType(AccountIdUnique.class);
	}
}
