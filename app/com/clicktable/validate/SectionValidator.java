package com.clicktable.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.SectionDao;
//import com.clicktable.dao.intf.SectionDao;
import com.clicktable.model.Section;
//import com.clicktable.repository.SectionRepo;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class SectionValidator extends EntityValidator<Section> 
{
   

	
	@Autowired
	SectionDao restDao;	
	

	public List<ValidationError> validateSectionOnAdd(Section section) {
		List<ValidationError> errorList = validateOnAdd(section);
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, section.getStatus(), section.getLanguageCode());

		return errorList;
	}

	/**
	 * validations on section at the time of updation
	 * 
	 * @param section
	 * @return
	 */

	public List<ValidationError> validateSectionOnUpdate(Section section) {
		List<ValidationError> errorList = validateSectionOnAdd(section);
		return errorList;
	}
	
	
	public List<ValidationError> checkSection(Section section, String operation) {
		List<Section> sectionList = restDao.countSection(section.getRestID());
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		int count = 0;
		if (sectionList.size() >= Constants.MAX_SECTIONS
				&& operation.equalsIgnoreCase(Constants.ADD)) {
			 errorList.add(createError(Constants.REST_GUID,
					ErrorCodes.SECTION_MAX_LIMIT_REACH));
			 return errorList;
		}
			for (Section sections : sectionList) {
				if(sections.getName().equalsIgnoreCase(section.getName())){
				count++;
				}
			}
			if (count >= 1 && operation.equalsIgnoreCase(Constants.ADD)) {
				 errorList
						.add(createError(Constants.NAME,
								ErrorCodes.NAME_ALREADY_EXISTS));
				return errorList;
				}else if(count >= 1 && operation.equalsIgnoreCase(Constants.UPDATE)){
					 errorList
						.add(createError(Constants.NAME,
								ErrorCodes.NAME_ALREADY_EXISTS));
				    return errorList;
				}

		
		return errorList;
	}
	
	public List<ValidationError> deleteSection(Section section, List<ValidationError> listOfError) {
		Map<String, Object> mapObject = restDao
				.validateSectionBeforeDelete(section);
		if (null != mapObject.get(Constants.SECTION)) {
			if (null != mapObject.get(Constants.TABLE_ID)
					&& Integer.parseInt(mapObject.get(Constants.TABLE_ID)
							.toString()) > 0) {
				listOfError.add(createError(Constants.SECTION_GUID,
						ErrorCodes.SECTION_CONTAINS_TABLE));
				return listOfError;
			}
			
		}

		return listOfError;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.SECTION_ID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_SECTION_ID;
	}

}
