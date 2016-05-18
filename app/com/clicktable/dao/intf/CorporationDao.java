package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.Corporation;

@Service
public interface CorporationDao extends GenericDao<Corporation> {
	
	public List<Corporation> findCorporations(Map<String, Object> params);

	public String addCorporation(Corporation corporation);

	Corporation updateCorporation(Corporation toupdatestate, Corporation existing);

	
}
