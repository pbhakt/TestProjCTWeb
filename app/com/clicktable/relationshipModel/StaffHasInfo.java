package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Staff;
import com.clicktable.model.StaffInfo;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.STAFF_HAS_INFO)
public class StaffHasInfo  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -578729451240644992L;
	@Fetch
	@EndNode
	StaffInfo staffInfo;
	@Fetch
	@StartNode
	Staff staff;
	
	
	 
	
	public StaffHasInfo() 
	{
	    super();
	}
	




	public StaffInfo getStaffInfo() {
		return staffInfo;
	}





	public void setStaffInfo(StaffInfo staffInfo) {
		this.staffInfo = staffInfo;
	}





	public Staff getStaff() {
	    return staff;
	}

	public void setStaff(Staff staff) {
	    this.staff = staff;
	}




}
