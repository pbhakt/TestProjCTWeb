package com.clicktable.dao.intf;

import com.clicktable.model.Country;

/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface CountryDao extends GenericDao<Country>
{

	boolean hasChildRelationships(Country existing);

	Country updateCountry(Country toupdatecountry, Country existing);
	  
}
