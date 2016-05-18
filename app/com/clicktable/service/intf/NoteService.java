package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.model.Note;
import com.clicktable.response.BaseResponse;

@org.springframework.stereotype.Service
public interface NoteService {
	/**
	 * Get table based on search parameters
	 * 
	 * @param params
	 * @return
	 */
	BaseResponse getNotes(Map<String, Object> params, String token);

	/**
	 * Update table data
	 * 
	 * @param table
	 * @return
	 */
	BaseResponse addNote(Note note, String token);

	BaseResponse deleteNotes(String GUID, String header);

}
