package com.clicktable.dao.impl;


import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.LanguageDao;
import com.clicktable.model.Language;

/**
 * 
 * @author p.singh
 *
 */

@Service
public class LanguageDaoImpl extends GraphDBDao<Language> implements
		LanguageDao {

	public LanguageDaoImpl() {
		super();
		this.setType(Language.class);
	}
	
	
	
	


}
