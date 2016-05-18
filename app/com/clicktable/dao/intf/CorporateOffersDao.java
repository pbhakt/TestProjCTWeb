package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.CorporateOffers;

@Service
public interface CorporateOffersDao extends GenericDao<CorporateOffers> {

	public String addCorporateOffers(CorporateOffers corporate_offers);

	public List<CorporateOffers> findCorporateOffers(Map<String, Object> params);

	public CorporateOffers updateCorporateOffers(
			CorporateOffers corporate_offers, CorporateOffers corporateOffers);

	List<CorporateOffers> getCustomCorporateOffers(Map<String, Object> params);
	
}
