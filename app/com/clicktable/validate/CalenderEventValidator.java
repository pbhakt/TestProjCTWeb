package com.clicktable.validate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.i18n.Messages;

import com.clicktable.dao.intf.CalenderEventDao;
import com.clicktable.dao.intf.TableDao;
import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Table;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;




@Service
public class CalenderEventValidator extends EntityValidator<CalenderEvent> {

	@Autowired 
	TableDao tableDao;
	
	@Autowired
	CalenderEventDao calEventDao;

	public void validateCalenderEvent(CalenderEvent calEvent,boolean validateCategory,
			List<ValidationError> listOfError) {
		listOfError.addAll(validateOnAdd(calEvent));
		if(validateCategory){
			validateAgainstExistingEventsCategory(calEvent, listOfError);
		}
		if(listOfError.isEmpty() && calEvent.getCategory().equals(Constants.BLOCK)){
			// validate blocked tables
			Date eventStart = UtilityMethods.addTimeToDate(calEvent.getEventDate(), calEvent.getStartTime()).toDate();
			Date eventEnd = UtilityMethods.addTimeToDate(calEvent.getEventDate(), calEvent.getEndTime()).toDate();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.GUID, calEvent.getBlockingArea());
			params.put(Constants.START_TIME, eventStart);
			params.put(Constants.END_TIME, eventEnd);
			params.put(Constants.TABLE_STATUS, Constants.ALL);
            List<Table> tables = tableDao.findByFields(Table.class, params);
			for(Table table: tables){
				if(table.getTableStatus()!=null && !table.getTableStatus().equals(Constants.AVAILABLE))
					listOfError.add(new ValidationError(Constants.BLOCKING_AREA, Messages.get(ErrorCodes.TABLE_NOT_AVAILABLE, table.getGuid(), table.getTableStatus()),ErrorCodes.TABLE_NOT_AVAILABLE));
			}
			
		}
		
	}

	private String validateAgainstExistingEventsCategory(CalenderEvent calEvent, List<ValidationError> listOfError) {
		Map<String, Object> params = new HashMap<String, Object>();

		//params.put(Constants.EVENT_DATE, calEvent.getEventDate());
		params.put(Constants.START_TIME_BEFORE, calEvent.getEndTime());
		params.put(Constants.END_TIME_AFTER, calEvent.getStartTime());
		params.put(Constants.REST_GUID, calEvent.getRestaurantGuid());
		List<CalenderEvent> calEvents = calEventDao.findByFields(CalenderEvent.class, params);
		for(CalenderEvent existing: calEvents){
			if(listOfError.isEmpty() && !eventsCanOverlap(existing, calEvent))
				listOfError.add(new ValidationError(Constants.CATEGORY, Messages.get(ErrorCodes.EVENT_CANT_COEXIST, calEvent.getCategory(), existing.getCategory()),ErrorCodes.EVENT_CANT_COEXIST));
		}
			
		return null;
	}

	private boolean eventsCanOverlap(CalenderEvent existing,
			CalenderEvent calEvent) {
		if(UtilityMethods.getEnumValues(Constants.CATEGORY, existing.getCategory()).contains(calEvent.getCategory()))
			return false;
		else if(existing.getCategory().equals(calEvent.getCategory()))
			return false;
		return true;
	}
	
	

	@Override
	public String getMissingGuidErrorCode() {
		return ErrorCodes.GUID_REQUIRED;
	}

	@Override
	public String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_GUID;
	}

	public void validateOngoingCalendarEventOnUpdate(CalenderEvent calEvent,
			CalenderEvent existing, List<ValidationError> listOfError) {
		calEvent.copyExistingValues(existing);
		if(!existing.getStartTime().equals(calEvent.getStartTime())){	// start time updated
			listOfError.add(new ValidationError(Constants.START_TIME, ErrorCodes.EVENT_START_DATE_NOT_EDITABLE_AFTER_START));
		}else{
			if(!existing.getEndTime().equals(calEvent.getEndTime())){	// end time updated
				Date currentTime= DateTime.now().toDate();
				if(!calEvent.getEndTime().after(currentTime)){
					calEvent.setEndTime(currentTime);
				}
			}
			
		}
	}
	
	@Override
	public Map<String, Object> validateFinderParams(Map<String, Object> params,
			Class type) {		
		Map<String, Object> validParams = super.validateFinderParams(params, type);
		if(params.containsKey(Constants.ALL))
			validParams.put(Constants.ALL, params.get(Constants.ALL));
		return validParams;
	}
}
