package com.clicktable.validate;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.CalenderEventDao;
import com.clicktable.dao.intf.EventDao;
import com.clicktable.dao.intf.PromotionDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.model.CalenderEvent;
import com.clicktable.model.Event;
import com.clicktable.model.EventPromotion;
import com.clicktable.model.Table;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@Service
public class EventValidator extends EntityValidator<Event> {

	@Autowired
	EventDao eventDao;

	@Autowired
	TableValidator tableValidator;
	
	@Autowired
	PromotionDao promotionDao;
	
	@Autowired
	CalenderEventDao calEventDao;
	
	@Autowired
	ReservationDao resvDao;


	public List<ValidationError> validateEventOnUpdate(Event event, Event existing) {
		List<ValidationError> errorList = validateEvent(event);
		if(errorList.isEmpty()){
			if(!event.getStartDate().equals(existing.getStartDate()) && !event.getStartTime().equals(existing.getStartTime()))
				validateEventForBackDate(event, errorList);
			errorList.addAll(validateAgainstExisting(event, existing));
		}
		return errorList;
	}

	public List<ValidationError> validateEventOnCreate(Event event) {
		List<ValidationError> errorList = validateEvent(event);
		if(errorList.isEmpty())
			validateEventForBackDate(event, errorList);
		return errorList;
	}	

	private List<ValidationError> validateEvent(Event event) {
		List<ValidationError> errorList = validateOnAdd(event);
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, event.getStatus(), event.getLanguageCode());

		if(errorList.isEmpty()){
			if(event.getEndDate().before(event.getStartDate())){
				errorList.add(new ValidationError(Constants.END_DATE, UtilityMethods.getErrorMsg(ErrorCodes.ENDDATE_BEFORE_STARTDATE), ErrorCodes.ENDDATE_BEFORE_STARTDATE));				
			}else if(UtilityMethods.addTimeToDate(event.getEndDate(), event.getEndTime()).isBefore(UtilityMethods.addTimeToDate(event.getStartDate(), event.getStartTime()))){
				errorList.add(new ValidationError(Constants.END_TIME, UtilityMethods.getErrorMsg(ErrorCodes.ENDDATE_BEFORE_STARTDATE), ErrorCodes.ENDDATE_BEFORE_STARTDATE));				
			}	
			
			validateMandatoryFields(event, errorList);			
			errorList.addAll(validateEnumValues(event, Constants.EVENT_MODULE));

			// validations for Recurrence
			if(errorList.isEmpty() && event.isRecurring()){			
				validateRecurEndType(event, errorList);		
				if(errorList.isEmpty()){
					long eventDuration= UtilityMethods.addTimeToDate(event.getEndDate(), event.getEndTime()).getMillis()-UtilityMethods.addTimeToDate(event.getStartDate(), event.getStartTime()).getMillis();
					long minTimeBetweenEvents = validateRecurranceType(event, errorList);
					if(errorList.isEmpty()){
						if((minTimeBetweenEvents-eventDuration)<0)
							errorList.add(new ValidationError(Constants.NUM_OF_RECURRENCE, UtilityMethods.getErrorMsg(ErrorCodes.DURATION_LESSTHAN_FREQUENCY),ErrorCodes.DURATION_LESSTHAN_FREQUENCY));
					}
				}
			}
		}

		return errorList;
		
	}
	
	@Override
	protected List<ValidationError> validateEnumValues(Object event, String moduleName) {
		List<ValidationError> listOfError = super.validateEnumValues(event, moduleName);
		if(listOfError.isEmpty()){
			// validate category
			List<String> categories = new ArrayList<String>();

			List<String> subCategories= new ArrayList<String>();
			switch(((Event)event).getType()){
			case Constants.EVENT:

				categories=UtilityMethods.getEnumValues(Constants.EVENT_MODULE, Constants.EVENTCATEGORY);
				subCategories=UtilityMethods.getEnumValues(Constants.EVENT_MODULE, Constants.EVENTSUBCATEGORY);
				break;
			case Constants.OFFER:

				categories=UtilityMethods.getEnumValues(Constants.EVENT_MODULE, Constants.OFFERCATEGORY);
				subCategories=UtilityMethods.getEnumValues(Constants.EVENT_MODULE, Constants.EVENTSUBCATEGORY);
				break;
			case Constants.BLOCK:
				categories=UtilityMethods.getEnumValues(Constants.EVENT_MODULE, Constants.BLOCKCATEGORY);
				break;
			}
			
			if (!categories.isEmpty() && !categories.contains(((Event)event).getCategory())) {
				listOfError.add(new ValidationError(Constants.EVENT_CATEGORY, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_CATEGORY),
						ErrorCodes.INVALID_CATEGORY));
			}
			
			//validate subcategory
			if(listOfError.isEmpty()){
				//List<String> subCategories=UtilityMethods.getEnumValues(Constants.EVENT_MODULE, Constants.EVENTSUBCATEGORY);
				if (!subCategories.isEmpty() && !subCategories.contains(((Event)event).getSubCategory())) {
					listOfError.add(new ValidationError(Constants.EVENT_SUBCATEGORY, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_EVENT_SUBCATEGORY),
							ErrorCodes.INVALID_EVENT_SUBCATEGORY));
				}
			}
		}
		return listOfError;
	}

	private void validateMandatoryFields(Event event,
			List<ValidationError> errorList) {
		if(event.isRecurring()){			
			if(event.getRecurrenceType() == null || event.getRecurrenceType().isEmpty())
				errorList.add(new ValidationError(Constants.RECURRANCE_TYPE,  UtilityMethods.getErrorMsg(ErrorCodes.RECURRANCE_TYPE_REQUIRED), ErrorCodes.RECURRANCE_TYPE_REQUIRED));	
			if(event.getRecurEndType() == null || event.getRecurEndType().isEmpty())
				errorList.add(new ValidationError(Constants.RECUR_END_TYPE,UtilityMethods.getErrorMsg(ErrorCodes.RECUR_END_TYPE_MISSING),ErrorCodes.RECUR_END_TYPE_MISSING));
			if(event.getRecurEvery()<=0)
				errorList.add(new ValidationError(Constants.RECUR_EVERY, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_VALUE_RECUR_EVERY),ErrorCodes.INVALID_VALUE_RECUR_EVERY));
		}	
		
		//validations for blocking
		if(event.getCategory().equals(Constants.BLOCK)){
			if(event.getBlockingType()== null)
				errorList.add(new ValidationError(Constants.BLOCKING_TYPE, UtilityMethods.getErrorMsg(ErrorCodes.BLOCKING_TYPE_REQUIRED), ErrorCodes.BLOCKING_TYPE_REQUIRED));
			else if(event.getBlockingArea()== null || event.getBlockingArea().isEmpty() || event.getBlockingArea().get(0).isEmpty())
				errorList.add(new ValidationError(Constants.BLOCKING_AREA, UtilityMethods.getErrorMsg(ErrorCodes.BLOCKING_AREA_REQUIRED), ErrorCodes.BLOCKING_AREA_REQUIRED));			
		}
	}

	private void validateEventForBackDate(Event event,
			List<ValidationError> errorList) {
		DateTime now =DateTime.now();
		//if(event.getCategory().equals(Constants.OP_HR) || event.isAllday())
		//	now=now.toDateMidnight().toDateTime();
		
		if( event.getStartDate().before(now.toDateMidnight().toDate()))
			errorList.add(new ValidationError(Constants.START_DATE, UtilityMethods.getErrorMsg(ErrorCodes.BACK_DATED_EVENT),ErrorCodes.BACK_DATED_EVENT));
		else if(!event.isAllday() && UtilityMethods.addTimeToDate(event.getStartDate(), event.getStartTime()).isBefore(now))
			errorList.add(new ValidationError(Constants.START_TIME, UtilityMethods.getErrorMsg(ErrorCodes.BACK_TIMED_EVENT),ErrorCodes.BACK_TIMED_EVENT));
		
	}

	private long validateRecurranceType(Event event, List<ValidationError> errorList) {
		long minTimeBetweenEvents =0;
		switch(event.getRecurrenceType()){				
		case Constants.DAILY:
			minTimeBetweenEvents= event.getRecurEvery()*Constants.DAY;
			break;
		case Constants.MONTHLY:
			if((event.getDateOfMonth()<=0  && event.getWeekOfMonth() <=0) || (event.getDateOfMonth()>0  && event.getWeekOfMonth() >0)){ //|| (event.getDateOfMonth()>0  && event.getWeekOfMonth() <=0) || (event.getDateOfMonth()<=0  && event.getWeekOfMonth() >0)){
				errorList.add(new ValidationError(Constants.DATE_OF_MONTH+", "+Constants.WEEK_OF_MONTH, UtilityMethods.getErrorMsg(ErrorCodes.ONE_OF_THESE_REQUIRED), ErrorCodes.ONE_OF_THESE_REQUIRED));
			}else if(event.getWeekOfMonth()>0 && (event.getDayOfTheWeek().size()!=1)){
				errorList.add(new ValidationError(Constants.DAY_OF_THE_WEEK, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_DAY_OF_WEEK),ErrorCodes.INVALID_DAY_OF_WEEK));
			}

			if(errorList.isEmpty()){
				if(event.getDateOfMonth()>0){
					minTimeBetweenEvents = event.getRecurEvery()*28*Constants.DAY;
				}else{
					minTimeBetweenEvents = event.getRecurEvery()*4*7*Constants.DAY;
				}
			}
			break;
		case Constants.WEEKLY:
			if(event.getDayOfTheWeek().isEmpty())
				errorList.add(new ValidationError(Constants.DAY_OF_THE_WEEK, UtilityMethods.getErrorMsg(ErrorCodes.DAY_OF_WEEK_REQUIRED), ErrorCodes.DAY_OF_WEEK_REQUIRED));
			else{
				for(String day: event.getDayOfTheWeek()){
					if(!(UtilityMethods.getEnumValues(Constants.EVENT_MODULE, Constants.DAY_OF_THE_WEEK).contains(day)))
						errorList.add(new ValidationError(Constants.DAY_OF_THE_WEEK, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_DAY_OF_WEEK) ,ErrorCodes.INVALID_DAY_OF_WEEK));
				}
			}
			if(errorList.isEmpty()){
				int minDifference =7;
				if(event.getDayOfTheWeek().size()>1){						
					for(String day:event.getDayOfTheWeek()){
						int calDay =UtilityMethods.getDay(day);	
						for(String inDay:event.getDayOfTheWeek()){
							int difference = Math.abs(calDay-UtilityMethods.getDay(inDay));	
							if(difference!=0)
								minDifference = Math.min(minDifference, difference);															
						}
						if(minDifference ==1)
							break;
					}
				}
				minTimeBetweenEvents = event.getRecurEvery()*minDifference*Constants.DAY;							
			}
			break;
		}
		return minTimeBetweenEvents;
	}

	private void validateRecurEndType(Event event, List<ValidationError> errorList) {
		switch(event.getRecurEndType()){
		case Constants.END_ON_DATE:
			if(event.getRecurrenceEndDate() == null)
				errorList.add(new ValidationError(Constants.RECURRANCE_END_DATE, UtilityMethods.getErrorMsg(ErrorCodes.RECURRANCE_END_DATE_REQUIRED), ErrorCodes.RECURRANCE_END_DATE_REQUIRED));
			else if(event.getRecurrenceEndDate().before(event.getStartDate()))
				errorList.add(new ValidationError(Constants.END_DATE, UtilityMethods.getErrorMsg(ErrorCodes.RECURENDDATE_BEFORE_STARTDATE),ErrorCodes.RECURENDDATE_BEFORE_STARTDATE));
			break;
		case Constants.END_AFTER:
			if(event.getNumOfRecurrence()<= 0)
				errorList.add(new ValidationError(Constants.NUM_OF_RECURRENCE, UtilityMethods.getErrorMsg(ErrorCodes.NUM_OF_RECURRENCE_POSITIVE),ErrorCodes.NUM_OF_RECURRENCE_POSITIVE));
			break;
		/*case Constants.NEVER:
			Date recurrenceEndDate= new DateTime(event.getStartDate().getTime()).plusMonths(2).toDate();
			event.setRecurrenceEndDate(recurrenceEndDate);
			break;*/
		}
	}

	public List<ValidationError> validateAgainstExisting(Event event, Event existing) {
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		if (!event.getRestaurantGuid().equals(existing.getRestaurantGuid()))
			listOfError.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.RESTAURANT_NOT_EDITABLE),ErrorCodes.RESTAURANT_NOT_EDITABLE));
		if (!event.getType().equals(existing.getType()))
			listOfError.add(new ValidationError(Constants.EVENT_TYPE, UtilityMethods.getErrorMsg(ErrorCodes.EVENT_TYPE_NOT_EDITABLE),ErrorCodes.EVENT_TYPE_NOT_EDITABLE));
		
		if(!listOfError.isEmpty()){
		int pastCount= calEventDao.getPastCalEventsCount(event.getGuid());
		//if((UtilityMethods.addTimeToDate(existing.getStartDate(), existing.getStartTime()).toDate()).before(DateTime.now().toDate())){ // Event already started
		if(pastCount>0){
			if(!existing.getStartDate().equals(event.getStartDate()))
				listOfError.add(new ValidationError(Constants.START_DATE, UtilityMethods.getErrorMsg(ErrorCodes.EVENT_START_DATE_NOT_EDITABLE_AFTER_START),ErrorCodes.EVENT_START_DATE_NOT_EDITABLE_AFTER_START));
			if(isRecurranceUpdated(event, existing)){
				listOfError.add(new ValidationError(Constants.START_DATE, ErrorCodes.EVENT_RECURRENCE_NOT_EDITABLE_AFTER_START));
			}
			if(event.isRecurring() && event.getRecurEndType().equals(Constants.END_AFTER)){
				if(!event.getRecurEndType().equals(existing.getRecurEndType()) || event.getNumOfRecurrence()<existing.getNumOfRecurrence()){
					//int pastCount= calEventDao.getPastCalEventsCount(event.getGuid());
					if(pastCount>= event.getNumOfRecurrence()){
						listOfError.add(new ValidationError(Constants.NUM_OF_RECURRENCE, ErrorCodes.NUM_RECURRANCE_ALREADY_OCCURED));

					}
				}
				
			}
			
		}
		}
		return listOfError;
	}

	public List<Table> validateBlockedArea(List<ValidationError> listOfError, Event event) {
		List<Table> tableList = tableValidator.tableExistForRestaurant(event.getBlockingArea(), event.getRestaurantGuid());
		if (tableList.size() != event.getBlockingArea().size())
			listOfError.add(new ValidationError(Constants.BLOCKING_AREA, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_BLOCKING_AREA),ErrorCodes.INVALID_BLOCKING_AREA));
		return tableList;

	}
	
	private boolean isRecurranceUpdated(Event event, Event existing) {
		boolean isUpdated = false;
		if(!(existing.isRecurring() == (event.isRecurring())))
			isUpdated = true;
		else if(event.isRecurring())
		{
			if(!event.getStartDate().equals(existing.getStartDate()))
				isUpdated = true;
			if(!event.getRecurrenceType().equals(existing.getRecurrenceType()) 
					//|| !event.getRecurEndType().equals(existing.getRecurEndType())
					|| event.getRecurEvery()!=existing.getRecurEvery())
				isUpdated = true;
			else
			{
				/*switch(event.getRecurEndType()){
				case Constants.END_ON_DATE:
					if(!event.getRecurrenceEndDate().equals(existing.getRecurrenceEndDate()))
						isUpdated = true;
					break;
				case Constants.END_AFTER:
					if(event.getNumOfRecurrence()!=existing.getNumOfRecurrence())
						isUpdated = true;
					break;
				}*/

				switch(event.getRecurrenceType()){
				case Constants.MONTHLY:
					if((event.getDateOfMonth()!=existing.getDateOfMonth() || (event.getWeekOfMonth()!=existing.getWeekOfMonth()))){
						isUpdated = true;
					}else if(event.getWeekOfMonth()>0 && !(event.getDayOfTheWeek().get(0).equals(existing.getDayOfTheWeek().get(0)))){
						isUpdated = true;
					}
					break;
				case Constants.WEEKLY:
					if(event.getDayOfTheWeek().size()!=existing.getDayOfTheWeek().size() 
					|| !event.getDayOfTheWeek().containsAll(existing.getDayOfTheWeek()))
						isUpdated = true;
					break;	
				}
			}
		}
		return isUpdated;
	}

/*	public void validateOverlappingEventWithSameName(List<ValidationError> listOfError, Event event) {
		long eventStart = event.getStartDate().getTime() + event.getStartTime().getTime();
		long eventEnd = getEventEnd(event);
		List<Event> list = eventDao.eventExistForRestaurant(event.getName(), event.getRestaurantGuid());
		for (Event existing : list) {
			long existStart = existing.getStartDate().getTime() + existing.getStartTime().getTime();
			long existEnd = getEventEnd(existing);
			if(existStart<eventEnd && existEnd>eventStart){
				listOfError.add(new ValidationError(Constants.NAME, UtilityMethods.getErrorMsg(ErrorCodes.OVERLAPPING_EVENT_EXIST),ErrorCodes.OVERLAPPING_EVENT_EXIST));
				break;
			}
		}

	}

	/*private long getEventEnd(Event event) 
	{
		long eventEndTime = 0;
		//if recur end type is never then set end time after 2 months
		if(event.isRecurring())// && event.getRecurEndType().equals(Constants.NEVER))
		{
			switch(event.getRecurEndType()){
			case Constants.NEVER:
				Calendar cal = Calendar.getInstance();
				cal.setTime(event.getStartDate());
				cal.set(Calendar.MONTH, cal.get(Calendar.YEAR)+2);
				eventEndTime= cal.getTimeInMillis();
				break;
			case Constants.END_ON_DATE:
				eventEndTime= event.getRecurrenceEndDate().getTime();
				break;
			case Constants.END_AFTER:
				if(event.getRecurrenceType().equals(Constants.DAILY))
					eventEndTime= new DateTime(event.getStartDate()).plusDays(event.getRecurEvery()*event.getNumOfRecurrence()).getMillis();
				else if(event.getRecurrenceType().equals(Constants.MONTHLY))
					eventEndTime= new DateTime(event.getStartDate()).plusMonths(event.getRecurEvery()*event.getNumOfRecurrence()).getMillis();
				else if(event.getRecurrenceType().equals(Constants.WEEKLY))	
					eventEndTime= new DateTime(event.getStartDate()).plusWeeks(event.getRecurEvery()*event.getNumOfRecurrence()).getMillis();
				break;
			}
			return eventEndTime +event.getEndTime().getTime();
		} 
		else
			return event.getEndDate().getTime() + event.getEndTime().getTime();
	}*/

	public void validateEventAgainstPromotion(
			Event existing, List<ValidationError> listOfError) {		
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.EVENT_GUID, existing.getGuid());
			// already promoted event
			List<EventPromotion> promotions = promotionDao.findByFields(EventPromotion.class, params );
			if(!promotions.isEmpty()){
				listOfError.add(new ValidationError(Constants.GUID, ErrorCodes.EVENT_ALREADY_PROMOTED));
			}	
		
	}

	public boolean validateBlockAreaOnUpdate(Event event, Event existing, List<ValidationError> listOfError) {
		
		if(event.getType().equals(Constants.BLOCK)){
			if(event.getBlockingArea().size()!=existing.getBlockingArea().size() 
					||!event.getBlockingArea().containsAll(existing.getBlockingArea())){
				validateBlockedArea(listOfError, event);	
				return true;
			}		
		}
		return false;
	}

	public void validateEventForOngoing(Event existing,
			List<ValidationError> listOfError) {
		List<CalenderEvent> ongoingEvents = calEventDao.getOngoingCalendarEvents(existing);
		if(!ongoingEvents.isEmpty()){			
			listOfError.add(new ValidationError(Constants.START_TIME, ErrorCodes.EVENT_ONGOING));
		}		
	}

	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.EVENT_GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_EVENT_ID;
	}

	public void validateEventOnDelete(Event event, Event existing,
			List<ValidationError> listOfError) {
		if(event.isPromoteValidation()){
			validateEventAgainstPromotion(existing,listOfError);
		}
		if(event.isOngoingValidation()){
			validateEventForOngoing(existing, listOfError);
		}
		
		//fix for CT-1014 : If a reservation is associated with an offer than don't delete		
		if(existing.getType().equals(Constants.OFFER)){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.OFFER_ID, existing.getGuid());
			params.put(Constants.EST_END_AFTER, DateTime.now().toDate());
			int resvCount= resvDao.getCountWithParams(params);
			if(resvCount>0){
				listOfError.add(new ValidationError(Constants.OFFER_ID, ErrorCodes.HAS_RESERVATOINS_ON_OFFER));
			}
		}

	}

}
