/**
 * 
 */
package com.clicktable.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.clicktable.model.Staff;
/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface StaffRepo extends GraphRepository<Staff> 
{

//	@Query("MATCH (staff:Staff where staff.guid={c}) RETURN staff")
	Staff findByguid(String guid);

}
