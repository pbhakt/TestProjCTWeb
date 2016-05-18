package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.Locality;

@Service
public interface LocalityDao extends GenericDao<Locality> {

	List<Locality> getLocalities(Map<String, Object> stringParamMap);

	public String addLocality(Locality locality);

	boolean hasChildRelationships(Locality existing);

	Locality updateLocality(Locality toUpdatelocality, Locality existing);
	
}
