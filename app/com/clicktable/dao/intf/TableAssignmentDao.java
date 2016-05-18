package com.clicktable.dao.intf;

import java.util.Iterator;
import java.util.Map;

import com.clicktable.model.TableAssignment;

@org.springframework.stereotype.Service
public interface TableAssignmentDao extends GenericDao<TableAssignment>
{
  
    
    
     boolean assignTablesToServer(TableAssignment assignTable, String[] attrGuidArr); 
     
     boolean unassignTablesToServer(TableAssignment assignTable, String[] attrGuidArr);
     
     Iterator<Map<String, Object>>  getTableAssignment(Map<String,Object> params);
     
     boolean unassignAllTables(String[] tableGuidArr);
     
     Iterator<Map<String, Object>>  getTableServerAndRest(TableAssignment tableAssignmment, String [] tableGuid);
   
}
