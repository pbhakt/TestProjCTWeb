package com.clicktable.dao.intf;

import org.springframework.stereotype.Service;

import com.clicktable.relationshipModel.TableReservationRelation;

@Service
public interface TableReservationRelationDao extends
		GenericDao<TableReservationRelation> {


	//TableReservationRelation findRelationshipById(Class startType,
		//	String startGuid, Class endType, String endguid);

}
