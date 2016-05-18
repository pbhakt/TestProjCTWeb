package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;

import com.clicktable.model.Section;
import com.clicktable.relationshipModel.HasSection;

/**
 * 
 * @author p.singh
 *
 */
@org.springframework.stereotype.Service
public interface SectionDao extends GenericDao<Section>
{
    public HasSection saveRelationModel(HasSection relationModel);

	List<Section> sectionExistForRestaurant(List<String> list, String restId);
	
	public String addRestaurantSection(Section section);

	public boolean deleteRestaurantSection(Section section);
	public String updateRestaurantSection(Section section);
	public List<Section> countSection(String restId);

	Map<String, Object> validateSectionBeforeDelete(Section section);

	List<Section> getCustomSections(Map<String, Object> params);
    
	
}
