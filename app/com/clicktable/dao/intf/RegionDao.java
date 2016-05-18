package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Region;

@org.springframework.stereotype.Service
public interface RegionDao extends GenericDao<Region> {

	public String addRegion(Region area);

	public List<Region> findRegions(Map<String, Object> params);

	public boolean hasChildRelationships(Region existing);

	Region updateRegion(Region toupdatestate, Region existing);

}
