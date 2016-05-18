package com.clicktable.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.NoteDao;
import com.clicktable.model.Note;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Staff;
import com.clicktable.repository.StaffRepo;
import com.clicktable.util.Constants;
import com.clicktable.util.RelationshipTypes;
import com.clicktable.util.UtilityMethods;

@Service
public class NoteDaoImpl extends GraphDBDao<Note> implements NoteDao {

	@Autowired
	StaffRepo staff_repo;

	public NoteDaoImpl() {
		super();
		this.setType(Note.class);
	}

	@Override
	public List<Note> findByFields(Class type, Map<String, Object> params) {

		StringBuilder query = getMatchClause(params);
		query.append(getWhereClause(params));
		query.append(getReturnClause(params));
		List<Note> note = executeQuery(query.toString(), params);
		List<Note> new_list = new ArrayList<Note>();

		for (Note note_data : note) {

			Staff staff = staff_repo.findByguid(note_data.getCreatedBy());

			note_data.setStaff_FirstName(staff.getFirstName());
			note_data.setStaff_LastName(staff.getLastName());

			new_list.add(note_data);

		}
		return new_list;

	}

	@Override
	protected StringBuilder getMatchClause(java.util.Map<String, Object> params) {
		StringBuilder sb = new StringBuilder("MATCH (t:`" + type.getSimpleName() + "`) <-[rht:" + RelationshipTypes.REST_HAS_NOTE + "{");
		sb.append("}]-(r:" + Restaurant.class.getSimpleName() + "{");

		if (params.containsKey(Constants.REST_ID)) {
			sb.append(Constants.GUID + ":{" + Constants.REST_ID + "}");
			//params.remove(Constants.REST_ID);
		}
		sb.append("})");
		return sb;
	};


	@Override
	public String addRestaurantNote(Note note,String restId) {
		StringBuilder query = new StringBuilder();
		query.append("MATCH (res:" + Constants.RESTAURANT_LABEL + "{guid:{" + Constants.REST_GUID + "}})");
		query.append(" CREATE((res)-[rhn:" + RelationshipTypes.REST_HAS_NOTE + "{__type__:'RestaurantHasNote',rest_guid:{" + Constants.REST_GUID + "}" + ", " + "created_dt:{"
				+ Constants.CREATED_DATE + "}}]->(n:" + Constants.NOTE_LABEL + ":_" + Constants.NOTE_LABEL + "{");
		Map<String, Object> params = addingNodeProperties(query, note);
		params.put(Constants.CREATED_DATE, UtilityMethods.truncateTime(note.getCreatedDate()));
		query.append("})) return n");
		Map<String, Object> result = executeWriteQuery(query.toString(), params).singleOrNull();
		if (result == null)
			return null;
		else
			return (String) ((Node) result.get("n")).getProperty(Constants.GUID);
	}

	@Override
	public String deleteNote(Map<String, Object> qryParamMap) {
		
		StringBuilder query = new StringBuilder();
		query.append("MATCH (res:" + Constants.RESTAURANT_LABEL + "{guid:{" + Constants.REST_ID + "}})");
		query.append(" -[rhn:" + RelationshipTypes.REST_HAS_NOTE +"]->(n:" + Constants.NOTE_LABEL +")");
		query.append ( " WHERE n.guid={"+Constants.GUID+"} ");
		query.append (" DELETE rhn,n");

		template.query(query.toString(),qryParamMap);
		
		return qryParamMap.get(Constants.GUID).toString();
	}
}
