package com.clicktable.dao.intf;

import com.clicktable.model.Attribute;
import com.clicktable.relationshipModel.HasAttribute;

/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface AttributeDao extends GenericDao<Attribute>
{
    public HasAttribute saveRelationModel(HasAttribute relationModel);
   
    public boolean addRestaurantAttributes(String restGuid, String[] attrGuid) ;
    
    public boolean addCountryAttributes(String countryGuid, String[] attrGuidArr);
    
}
