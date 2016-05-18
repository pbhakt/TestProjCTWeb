package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;

import com.clicktable.dao.intf.SectionDao;
import com.clicktable.dao.intf.TableDao;
import com.clicktable.model.Section;
import com.clicktable.model.Table;
import com.clicktable.model.UserInfoModel;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class TableValidator extends EntityValidator<Table> {

	//@Autowired
	//RestaurantDao restDao;

	@Autowired
	TableDao tableDao;

	@Autowired
	SectionDao sectionDao;

	public List<ValidationError> validateTableOnAdd(Table table) {
		List<ValidationError> errorList = validateOnAdd(table);
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, table.getStatus(), table.getLanguageCode());
		errorList.addAll(validateEnumValues(table, Constants.TABLE_MODULE));
		if (table.getMinCovers() != null && table.getMaxCovers() != null)
			if (table.getMinCovers() > table.getMaxCovers()) {
				errorList = CustomValidations.populateErrorList(errorList, Constants.MIN_COVERS, UtilityMethods.getErrorMsg(ErrorCodes.MIN_GREATER_THEN_MAX),ErrorCodes.MIN_GREATER_THEN_MAX);
			}
		return errorList;
	}

	/**
	 * validations on table at the time of updation
	 * 
	 * @param table
	 * @return
	 */
	public List<ValidationError> validateTableOnUpdate(Table table) {
		List<ValidationError> errorList = validateTableOnAdd(table);
		return errorList;
	}


	public void validateTableForRestaurant(Table table, UserInfoModel userInfo, List<ValidationError> errorList) {
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))){
			if ((table.getRestId() != null) && (!(table.getRestId().equals(userInfo.getRestGuid())))){
				errorList.add(createError(Constants.REST_ID, ErrorCodes.INVALID_REST_ID));
			}
		}
		if (errorList.isEmpty())
			if (tableDao.tableWithNameExistsForRestaurant(table.getName(), table.getRestId()))
				CustomValidations.populateErrorList(errorList, Constants.NAME, UtilityMethods.getErrorMsg(ErrorCodes.DUPLICATE_TABLE_NAME),ErrorCodes.DUPLICATE_TABLE_NAME);
	}

	// validate for existance of section name for a specific restaurant
	public void validateTableForSection(Table table, List<ValidationError> errorList) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GUID, table.getSectionId());
		params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		params.put(Constants.REST_GUID, table.getRestId());
		List<Section> sectionList = sectionDao.findByFields(Section.class, params);
		if (sectionList.isEmpty()) {
			errorList.add(createError(Constants.SECTION_ID, ErrorCodes.NO_SUCH_SECTION_EXISTS_FOR_SPECIFIED_RESTAURANT));
		}
	}

	public void validateTableForRestaurantOnUpdate(Table table, UserInfoModel userInfo, List<ValidationError> errorList) {
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)))
			if (!(table.getRestId().equals(userInfo.getRestGuid())))
				errorList.add(createError(Constants.REST_ID, ErrorCodes.INVALID_REST_ID));
		if (errorList.isEmpty())
			if (tableDao.otherTableWithSameNameExists(table.getName(), table.getGuid(), table.getRestId()))
				CustomValidations.populateErrorList(errorList, Constants.NAME, UtilityMethods.getErrorMsg(ErrorCodes.DUPLICATE_TABLE_NAME),ErrorCodes.DUPLICATE_TABLE_NAME);
	}

	public Map<String, Object> validateFinderParams(Map<String, Object> params) {
	    Logger.debug("param in validator is "+params);
		Map<String, Object> validParamMap = new HashMap<String, Object>();
		validParamMap.putAll(super.validateFinderParams(params, Table.class));
		
		Logger.debug("param in validator is "+params+" valid param map is "+validParamMap);
		
		if(params.containsKey(Constants.TIME))
		{
		    Logger.debug("time is "+params.get(Constants.TIME)+"  time after parsing is "+ UtilityMethods.parseDate((String) params.get(Constants.TIME), Constants.TIMESTAMP_FORMAT));
		    validParamMap.put(Constants.TIME,UtilityMethods.parseDate((String) params.get(Constants.TIME), Constants.TIMESTAMP_FORMAT));
		}

		return validParamMap;
	}


	public List<Table> tableExistForRestaurant(List<String> list, String restId) {
		return tableDao.tableExistForRestaurant(list, restId);
	}

	public List<Table> validateTableOfSection(String restaurantGuid, List<String> sectionGuids) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.REST_GUID, restaurantGuid);
		params.put(Constants.SECTION_GUID, sectionGuids);
		return tableDao.findByFields(Table.class, params);
	}
	
	public List<ValidationError> deleteTable(Table table, List<ValidationError> listOfError) {
		Map<String, Object> mapObject = tableDao
				.validateTableBeforeDelete(table);
		
			if (null != mapObject.get(Constants.TABLE_ID)) {
				if (null != mapObject.get(Constants.RESERVATION_ID)					
					&& Integer.parseInt(mapObject.get(Constants.RESERVATION_ID)
							.toString()) > 0) {
				listOfError.add(createError(Constants.SECTION_GUID,
						ErrorCodes.TABLE_HAS_RESV));
				return listOfError;
			}
			if (null != mapObject.get(Constants.BLOCKED)
					&& Integer.parseInt(mapObject.get(Constants.BLOCKED)
							.toString()) > 0) {
				listOfError.add(createError(Constants.SECTION_GUID,
						ErrorCodes.TABLE_HAS_CALEVENT));
				return listOfError;

			}
		}

		return listOfError;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.TABLE_ID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_TABLE_ID;
	}

}
