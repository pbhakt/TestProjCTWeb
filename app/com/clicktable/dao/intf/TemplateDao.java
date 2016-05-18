package com.clicktable.dao.intf;

import com.clicktable.model.Template;

/**
 * 
 * @author g.singh
 *
 */
@org.springframework.stereotype.Service
public interface TemplateDao extends GenericDao<Template> {
	String addTemplate(Template msgTemplate);
	//String updateTemplate(Template msgTemplate);
}
