package com.clicktable.validate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.NoteDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.model.Note;
import com.clicktable.util.ErrorCodes;

@org.springframework.stereotype.Service
public class NoteValidator extends EntityValidator<Note> {
	@Autowired
	RestaurantDao restDao;

	@Autowired
	NoteDao noteDao;

	public List<ValidationError> validateNoteOnAdd(Note note) {
		List<ValidationError> errorList = validateOnAdd(note);
		errorList = CustomValidations.validateLanguageCode(errorList, note.getLanguageCode());
		return errorList;
	}

	/**
	 * validations on table at the time of updation
	 * 
	 * @param table
	 * @return
	 */
	public List<ValidationError> validateNoteOnUpdate(Note note) {
		List<ValidationError> errorList = validateNoteOnAdd(note);
		return errorList;
	}

	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.NOTE_GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_NOTE_ID;
	}

}
