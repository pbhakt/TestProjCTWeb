
package com.clicktable.dao.impl;
import org.springframework.stereotype.Component;

import com.clicktable.dao.intf.TableReservationRelationDao;
import com.clicktable.relationshipModel.TableReservationRelation;

@Component
public class TableReservationRelationDaoImpl extends
		GraphDBDao<TableReservationRelation> implements
		TableReservationRelationDao {

	TableReservationRelationDaoImpl(){
		super();
		this.setType(TableReservationRelation.class);
	}
	
	public TableReservationRelation findRelationshipById(Class startType,
			String startGuid, Class endType, String endguid){
		return super.findRelationshipById(startType, startGuid, endType, endguid);
	}

	
}


