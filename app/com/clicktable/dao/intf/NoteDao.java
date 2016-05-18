package com.clicktable.dao.intf;

import java.util.Map;

import com.clicktable.model.Note;

@org.springframework.stereotype.Service
public interface NoteDao extends GenericDao<Note> {

	/*
	 * String addRestaurantTable(Restaurant restaurant, Table table); String
	 * updateRestaurantTable(Table table,Restaurant restaurant,boolean
	 * isRelationShipChanged); //public List<Table> getAllTable(String
	 * rest_id,String role,String min_cover) String addRestaurantNote(Note
	 * note);
	 */
	String addRestaurantNote(Note note, String restId);

	String deleteNote(Map<String, Object> qryParamMap);

}
