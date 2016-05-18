package com.clicktable.dao.impl;


import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.GuestReservationRelationDao;
import com.clicktable.relationshipModel.GuestReservationRelation;

@Service
public class GuestReservationRelationDaoImpl extends GraphDBDao<GuestReservationRelation> implements
		GuestReservationRelationDao {

	public GuestReservationRelationDaoImpl() {
		super();
		this.setType(GuestReservationRelation.class);
	}

}
