package com.clicktable.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.TemplateDao;
import com.clicktable.model.Template;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;

/**
 * 
 * @author g.singh
 *
 */

@Service
public class TemplateDaoImpl extends GraphDBDao<Template> implements TemplateDao {


	public TemplateDaoImpl() {
		super();
		this.setType(Template.class);
	}

	@Override
	public String addTemplate(Template msgTemplate) {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:" + Constants.RESTAURANT_LABEL + "{" + getPropertyName(Constants.GUID) + ":{" + Constants.REST_GUID + "}}) ");
		query.append("MERGE (r)-[rht:" + RelationshipTypes.REST_HAS_TEMPLATE + "{__type__:'RestHasTemplate'}]->(t:" + Constants.TEMPLATE_LABEL + ":_" + Constants.TEMPLATE_LABEL + ")");
		query.append(" SET ");
		Map<String, Object> params=new HashMap<String,Object>();

		/*if (msgTemplate.getTemplates() == null) {
			query.append(" t." + getPropertyName(Constants.TEMPLATES) + "={" + getPropertyName(Constants.TEMPLATES) + "},");
			params.put(getPropertyName(Constants.TEMPLATES), new ArrayList<String>());
		}*/
		params.putAll(updatingNodeProperties(query, msgTemplate, "t"));
		query.append(" return t");

		Map<String, Object> result = executeWriteQuery(query.toString(), params).singleOrNull();
		String output = null;
		if (result != null)
			output = (String) ((Node) result.get("t")).getProperty(Constants.GUID);
		return output;

	}


	public String addTemplateBasic(Template msgTemplate) {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (r:" + Constants.RESTAURANT_LABEL + "{" + getPropertyName(Constants.GUID) + ":{" + Constants.REST_GUID + "}}) ");
		query.append("CREATE (r)-[rht:" + RelationshipTypes.REST_HAS_TEMPLATE + "{__type__:'RestHasTemplate'}]->(t:" + Constants.TEMPLATE_LABEL + ":_" + Constants.TEMPLATE_LABEL + "{");
		Map<String, Object> params = addingNodeProperties(query, msgTemplate);
		query.append("}) return t");
		Map<String, Object> result = executeWriteQuery(query.toString(), params).singleOrNull();
		String output = null;
		if (result != null)
			output = (String) ((Node) result.get("t")).getProperty(Constants.GUID);
		return output;
	}

}
