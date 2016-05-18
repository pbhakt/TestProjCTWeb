package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import play.Logger;

import com.clicktable.dao.intf.CalenderEventDao;
import com.clicktable.dao.intf.EventDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.TableDao;
import com.clicktable.model.CalenderEvent;
import com.clicktable.model.CustomCalendarEvent;
import com.clicktable.model.CustomEventModel;
import com.clicktable.model.Event;
import com.clicktable.model.Restaurant;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.DeleteResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.EventService;
import com.clicktable.service.intf.TableShuffleService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CalenderEventValidator;
import com.clicktable.validate.CustomerValidator;
import com.clicktable.validate.EventValidator;
import com.clicktable.validate.ReportValidator;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.TableValidator;
import com.clicktable.validate.ValidationError;

/**
 * @author s.gupta
 *
 */
@Component
public class EventServiceImpl implements EventService {

	@Autowired
	EventDao eventDao;

	@Autowired
	CalenderEventDao calEventDao;
	
	@Autowired
	ReservationDao resvDao;

	@Autowired
	EventValidator eventValidator;

	@Autowired
	RestaurantDao restDao;
	
	@Autowired 
	RestaurantValidator restaurantValidator;

	@Autowired
	CalenderEventValidator calenderEventValidator;

	@Autowired
	TableValidator tableValidator;

	@Autowired
	TableDao tableDao;
	
	@Autowired
	TableShuffleService shuffleService;

	@Autowired
	RestaurantValidator restValidator;
	
	@Autowired
	CustomerValidator customerValidator;
	
	@Autowired
	ReportValidator reportValidator;
	
	
	@Override
	@Transactional
	public BaseResponse addEvent(Event event, String token) 
	{
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();		
		List<CalenderEvent> calEvents = new ArrayList<CalenderEvent>();
				
		// Validate Restaurant
		Restaurant restaurant=restaurantValidator.validateGuid(event.getRestaurantGuid(), listOfError);		
		// Validate Event fields 
		if(listOfError.isEmpty())
			listOfError.addAll(eventValidator.validateEventOnCreate(event));

		if(listOfError.isEmpty() && event.getType().equals(Constants.BLOCK)){
			eventValidator.validateBlockedArea(listOfError,event);	
		}

		//Calculate and validate Calendar Events
		if(listOfError.isEmpty()){						
			calEvents = getCalanderEvents(event);
			for(CalenderEvent calEvent:calEvents){
				if(listOfError.isEmpty())
					calenderEventValidator.validateCalenderEvent(calEvent, event.isValidateCategory(), listOfError);
			}										
		}		

		// create Event and Relationships
		if(listOfError.isEmpty())
		{
			String eventGuid=eventDao.addEvent(restaurant, event, calEvents);
			response = new PostResponse<Event>(ResponseCodes.EVENT_ADDED_SUCCESFULLY, eventGuid);				
		}else{
			response = new ErrorResponse(ResponseCodes.EVENT_ADDED_FAILURE, listOfError);
		}

		return response;
	}


	@Override
	@Transactional
	public BaseResponse updateEvent(Event event, String token) {
		BaseResponse response;
		
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Event existing = eventValidator.validateGuid(event.getGuid(), listOfError);
		Restaurant restaurant=restaurantValidator.validateGuid(event.getRestaurantGuid(), listOfError);	
		
		if(listOfError.isEmpty()){
			event.copyExistingValues(existing);											
			listOfError.addAll(eventValidator.validateEventOnUpdate(event, existing));
			eventValidator.validateBlockAreaOnUpdate(event, existing, listOfError);	
			if(listOfError.isEmpty()){
			if(event.isPromoteValidation()){
				eventValidator.validateEventAgainstPromotion(existing, listOfError);
			}
			if(event.isOngoingValidation()){
				eventValidator.validateEventForOngoing(existing, listOfError);
			}
			}
		}
		
		
		List<CalenderEvent> calEvents = new ArrayList<CalenderEvent>();
		Event updated = null;
		
		if(listOfError.isEmpty()){
			// Update event 
			updated=eventDao.update(event);			

			int deleted=calEventDao.deleteCalanderEventsAfterUpdate(event);				
			updateCalEventList(deleted, event,existing, calEvents);							

			// Validate Calendar Events
			for(CalenderEvent calEvent:calEvents){
				if(listOfError.isEmpty())
					calenderEventValidator.validateCalenderEvent(calEvent, event.isValidateCategory(),listOfError);
			}
		}
		
		// Create Calendar Events and Relationships
		if(listOfError.isEmpty()){
			calEventDao.createMultiple(calEvents);
			calEventDao.createCalEventRelationShips(restaurant,event,calEvents);
			//shuffleTable(existing, token);
			response = new UpdateResponse<>(ResponseCodes.EVENT_UPDATED_SUCCESFULLY, updated);

		}else{
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			response = new ErrorResponse(ResponseCodes.EVENT_UPDATION_FAILURE, listOfError);
		}
			
		return response;
	}


	private void updateCalEventList(int deleted, Event event, Event existing, List<CalenderEvent> calEvents) {
		// TODO correct logic
		List<CalenderEvent> ongoingCalEvents = calEventDao.getOngoingCalendarEvents(event);
		CalenderEvent ongoingCalEvent =null;
		if(!ongoingCalEvents.isEmpty()){
			ongoingCalEvent = ongoingCalEvents.get(0);
		}
		
		List<Date> eventDates = new ArrayList<Date>();
		if(event.isRecurring())
		{
			int numRecur = getNumRecur(deleted, event, existing);	
			if(ongoingCalEvent!=null)
				numRecur++;
			Date fromDate;
			if(event.getStartDate().after(event.getUpdatedDate()))
				fromDate = event.getStartDate();
			else
				fromDate= UtilityMethods.truncateTime(event.getUpdatedDate());//getFromDate(event);
			eventDates = calculateEventdates(fromDate, numRecur, event);		
		}else{
			eventDates.add(event.getStartDate());
		}
								
		for(Date eventDate: eventDates)
		{			
			CalenderEvent calEvent = new CalenderEvent(event,eventDate);
			if(eventDate.after(event.getUpdatedDate())){ // start after current day
				calEvents.add(calEvent);
			}else{		// Current day Event
				Date currentTime = calEvent.getUpdatedDate();
				if(calEvent.getStartTime().after(currentTime)){	// yet to start
					calEvents.add(calEvent);
					if(ongoingCalEvent!=null){
						ongoingCalEvent.setEndTime(currentTime);
						calEventDao.update(ongoingCalEvent);
					}
				}else if(calEvent.getEndTime().after(currentTime)){ // started already
					if(ongoingCalEvent!=null){
						calEvent.setGuid(ongoingCalEvent.getGuid());
						calEvent.setStartTime(ongoingCalEvent.getStartTime());
						calEventDao.update(calEvent);
					}else{
						calEvent.setStartTime(currentTime);
						calEvents.add(calEvent);
					}
				}else{												// Finished already
					if(ongoingCalEvent!=null){
						calEvent.setGuid(ongoingCalEvent.getGuid());
						calEvent.setEndTime(currentTime);
						calEventDao.update(calEvent);
					}
				}					
			}
		}
											
	}




	/*private Date getFromDate(Event event) {
		Date fromDate;
		if(event.getUpdatedDate().after(event.getStartDate())){	
			DateTime updateDt = new DateTime(event.getUpdatedDate().getTime());
			// Running event
			if(updateDt.getMillisOfDay()>new DateTime(event.getStartTime()).getMillisOfDay() &&
					updateDt.getMillisOfDay()<new DateTime(event.getEndTime()).getMillisOfDay()){
				fromDate = updateDt.plusDays(1).toDate();
			}else if(updateDt.getMillisOfDay()<=new DateTime(event.getStartTime()).getMillisOfDay()){ // event yet to start
				fromDate = updateDt.toDate();
			}else {	// event already finished
				fromDate = updateDt.plusDays(1).toDate();	
			}
		}								
		else	
			fromDate=event.getStartDate();
		return UtilityMethods.truncateTime(fromDate);
	}*/


	private int getNumRecur(int deleted, Event event, Event existing) {
		int numRecur = 0;
		if(event.getRecurEndType().equals(existing.getRecurEndType())){
			if(event.getRecurEndType().equals(Constants.END_AFTER))
				numRecur =  deleted-existing.getNumOfRecurrence()+event.getNumOfRecurrence();											
		}else if(event.getRecurEndType().equals(Constants.END_AFTER))										
			numRecur= (event.getNumOfRecurrence() - getExistingCount(event.getGuid()));
		if(numRecur<0)
			numRecur =0;
		return numRecur;
	}


	private int getExistingCount(String guid) {
		return calEventDao.countForParentEventGuid(guid);
	}


	


	private List<CalenderEvent> getCalanderEvents(Event event) 
	{
		List<CalenderEvent> calEvents = new ArrayList<CalenderEvent>();
		Logger.debug("within get calendar events");
		
		if(event.isRecurring())
		{
			Logger.debug("event is recurring");
			List<Date> eventDates = calculateEventdates(event.getStartDate(), event.getNumOfRecurrence(), event);	
			Logger.debug("event date size is "+eventDates.size());
			for(Date eventDate: eventDates)
			{	
				//TODO
				if(event.getType().equals(Constants.HOLIDAY)){
					//restDao.g
				}
				calEvents.add(new CalenderEvent(event, eventDate));	
			}
		}
		else
		{
			calEvents.add(new CalenderEvent(event, event.getStartDate()));
		}
		Logger.debug("cal events size is  " + calEvents.size() + " cal events are "+calEvents);
		return calEvents;
	}


	private List<Date> calculateEventdates(Date startDate, int numRecurrence,
			Event event) {
		List<Date> eventDates = new ArrayList<Date>();

		switch(event.getRecurrenceType())
		{
		case Constants.DAILY:
			Date eventDate = startDate;
			//int count=0;
			while(((event.getRecurEndType().equals(Constants.END_ON_DATE)) && eventDate.compareTo(event.getRecurrenceEndDate())<=0)
					||(event.getRecurEndType().equals(Constants.END_AFTER) && numRecurrence>0))
			{		  
				eventDates.add(eventDate);
				eventDate = new DateTime(eventDate.getTime()).plusDays(event.getRecurEvery()).toDate();//+(event.getRecurEvery()*Constants.DAY));
				numRecurrence--;
				//Logger.debug("count is "+count++);
			}
			break;
		case Constants.WEEKLY:
			eventDate = getNextEventDate(startDate, event.getDayOfTheWeek());
			Calendar c = Calendar.getInstance();
			c.setTime(eventDate);
			int eventStartDay = c.get(Calendar.DAY_OF_WEEK);
			int previousEventDay = eventStartDay;
			while(((event.getRecurEndType().equals(Constants.END_ON_DATE)) && eventDate.compareTo(event.getRecurrenceEndDate())<=0)
					||(event.getRecurEndType().equals(Constants.END_AFTER) && numRecurrence>0))
			{
				eventDates.add(eventDate);
				eventDate = getNextEventDate(new Date(eventDate.getTime()+Constants.DAY), event.getDayOfTheWeek());				
				c.setTime(eventDate);
				int eventDay= c.get(Calendar.DAY_OF_WEEK);
				if(eventDay-previousEventDay<=0)
					eventDate =getNextEventDate(new Date(eventDate.getTime()+((event.getRecurEvery()-1)*7*Constants.DAY)), event.getDayOfTheWeek());
				//TODO optimize it
				previousEventDay = eventDay;
				numRecurrence--;
			}
			break;
		case Constants.MONTHLY:
			eventDate = getNextEventDate(startDate,event.getDateOfMonth(), event.getWeekOfMonth(), event.getDayOfTheWeek());
			while(((event.getRecurEndType().equals(Constants.END_ON_DATE) ) && eventDate.compareTo(event.getRecurrenceEndDate())<=0)
					||(event.getRecurEndType().equals(Constants.END_AFTER) && numRecurrence>0))
			{
				eventDates.add(eventDate);	
				c = Calendar.getInstance();
				c.setTime(eventDate);
				c.set(Calendar.MONTH, c.get(Calendar.MONTH)+event.getRecurEvery());
				c.set(Calendar.DATE, 1);
				Date newDate= new Date(c.getTimeInMillis());
				eventDate=getNextEventDate(newDate,event.getDateOfMonth(), event.getWeekOfMonth(), event.getDayOfTheWeek());			
				numRecurrence--;
			}
			break;
		}

		return eventDates;
	}


	private Date getNextEventDate(Date startDate, int dateOfMonth,
			int weekOfMonth, List<String> dayOfWeekList) {
		Date eventDate =null;
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);		
		if(dateOfMonth>0){
			if(c.get(Calendar.DATE)>dateOfMonth) 

				c.set(Calendar.MONTH, c.get(Calendar.MONTH)+1);
						
			while(c.getActualMaximum(Calendar.DATE)<dateOfMonth)
				c.set(Calendar.MONTH, c.get(Calendar.MONTH)+1);
			c.set(Calendar.DATE, dateOfMonth);
			eventDate= new Date(c.getTimeInMillis());
		}else if(weekOfMonth>0){
			int dayOfWeek=UtilityMethods.getDay(dayOfWeekList.get(0));
			eventDate = getDateForThisMonth(c,dayOfWeek, weekOfMonth);
			if(eventDate.before(startDate)){
				c.set(Calendar.MONTH, c.get(Calendar.MONTH)+1);
				eventDate = getDateForThisMonth(c,dayOfWeek, weekOfMonth);
			}
		}
		return eventDate;
	}

	private Date getDateForThisMonth(Calendar c, int dayOfWeek, int weekOfMonth) {
		c.set(Calendar.DATE,1);
		int startDay = c.get(Calendar.DAY_OF_WEEK);
		int difference = dayOfWeek-startDay;
		if(difference<0)
			difference = difference+7;
		Date eventDate = new Date(c.getTimeInMillis()+difference*Constants.DAY+(weekOfMonth-1)*7*Constants.DAY);
		if(c.get(Calendar.MONTH)!=eventDate.getMonth())
			eventDate = new DateTime(eventDate.getTime()).minusWeeks(1).toDate();
		return eventDate;	
	}

	private Date getNextEventDate(Date startDate, List<String> list) {
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		//DateTime startDt = new DateTime(startDate.getTime());
		int startDay = c.get(Calendar.DAY_OF_WEEK);
		int difference =7;                         
		for(String day:list){
			int calDay =UtilityMethods.getDay(day);			
			difference = Math.min(difference, (calDay-startDay)<0?(calDay-startDay+7):(calDay-startDay));
		}					
		return new Date(startDate.getTime()+difference*Constants.DAY);
	}


	@Override
	@Transactional(readOnly=true)
	public BaseResponse getEvents(Map<String, Object> params) {
		BaseResponse getResponse; 
		Map<String, Object> qryParamMap = eventValidator.validateFinderParams(params, Event.class);
		List<Event> events = eventDao.findByFields(Event.class, qryParamMap);	
		
		Map<String,Integer> tableMap = tableDao.getTables(params.get(Constants.REST_GUID));	

		List<Event> eventList = new ArrayList<Event>();
		for(Event event:events){
			if(event.getType()!=null && event.getType().equals("BLOCK")){
				CustomEventModel cem = new CustomEventModel(event);
				List<String> blockedtables = event.getBlockingArea();
				int totalNumOfCovers = 0;
				//iterate block tables and increase total num of covers for event
				for(String tableGuid : blockedtables)
				{
				   if(tableMap.containsKey(tableGuid))
				   {
				       totalNumOfCovers = totalNumOfCovers + tableMap.get(tableGuid);
				   }
				}
				
				//int totalNumOfCovers = getSumOfMaxCoversOnTables(blockedtables);
				cem.setTotalNumOfCovers(totalNumOfCovers);
				cem.setTotalNumOfTables(blockedtables.size());
				eventList.add(cem);
			}else{
				eventList.add(event);
			}			
		}
		getResponse = new GetResponse<Event>(ResponseCodes.EVENT_FETCH_SUCCESFULLY, eventList);
		return getResponse;	

	}



	@Override
	@Transactional(readOnly=true)
	public BaseResponse getCalenderEvents(Map<String, Object> params,
			String token) {
		BaseResponse getResponse; 
		Map<String, Object> qryParamMap = calenderEventValidator.validateFinderParams(params, CalenderEvent.class);
		List<CalenderEvent> events;
		if(qryParamMap.containsKey(Constants.ALL)){
			qryParamMap.remove(Constants.ALL);
			events = calEventDao.findDetailsByFields(CustomCalendarEvent.class,qryParamMap);
		}else{
			events = calEventDao.findByFields(CalenderEvent.class, qryParamMap);
		}
		getResponse = new GetResponse<CalenderEvent>(ResponseCodes.EVENT_FETCH_SUCCESFULLY, events);
		return getResponse;	
	}


	@Override
	public void shuffleTable(Event existing, String token) {
		if(existing!=null && existing.getType()!=null && existing.getType().equals(Constants.BLOCK))
		{
			System.out.println("Shuffle table called------------------------------------------------------------------------");
			
			Map<String, Object> params = new HashMap<>();
			params.put(Constants.REST_ID, existing.getRestaurantGuid());
			Runnable runnableTask = () -> {
				shuffleService.shuffleTables(params, token);
			};
			new Thread(runnableTask).start();

		}
	}



	@Override
	@Transactional
	public BaseResponse deleteCalendarEvent(CalenderEvent calEvent, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		CalenderEvent existing = calenderEventValidator.validateGuid(calEvent.getGuid(), listOfError);
		if(listOfError.isEmpty()){
			if(!existing.getRestaurantGuid().equals(calEvent.getRestaurantGuid())){
				listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.UNAUTHORIZED),ErrorCodes.UNAUTHORIZED));
			}else{
				Date currrentTime= DateTime.now().toDate();
				if(existing.getStartTime().after(currrentTime)){ // Upcoming
					calEventDao.delete(calEvent.getGuid());
				}else{
					if(existing.getEndTime().after(currrentTime)){ // Ongoing
						calEvent.setEndTime(currrentTime);
						calEventDao.update(calEvent);
					}else{ // Finished
						listOfError.add(new ValidationError(Constants.END_TIME, ErrorCodes.FINISHED_EVENT_NOT_DELETED));
					}
				}
			}
		}
		
		if(listOfError.isEmpty())
			response = new DeleteResponse(ResponseCodes.CALEVENT_DELETED_SUCCESSFULLY, calEvent.getGuid());
		else
			response = new ErrorResponse(ResponseCodes.CALEVENT_DELETION_FAILURE, listOfError);
		return response;
	}


	@Override
	@Transactional
	public BaseResponse deleteEvent(Event event, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		Event existing = eventValidator.validateGuid(event.getGuid(), listOfError);
		eventValidator.validateEventOnDelete(event, existing,listOfError);		
				
		if(listOfError.isEmpty())
		{	
			calEventDao.deleteCalanderEventsAfterUpdate(existing);
			deleteOngoingCalendarEvents(existing, listOfError);		
			deleteMasterEvent(event, existing);					
		}
		
		if(listOfError.isEmpty()){
			//shuffleTable(existing, token);	
			response = new DeleteResponse(ResponseCodes.EVENT_DELETED_SUCCESFULLY, existing);
		}else{
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			response =new ErrorResponse(ResponseCodes.EVENT_DELETION_FAILURE, listOfError);
		}
		return response;
	}


	private void deleteMasterEvent(Event event, Event existing) {
		
		//if(UtilityMethods.addTimeToDate(existing.getStartDate(), existing.getStartTime()).isAfterNow()){ // Upcoming
		//	eventDao.delete(event.getGuid());
		//}else{
			//event.copyExistingValues(existing);
			existing.setStatus(Constants.DELETED_STATUS);
			existing.setDeleteTime(new Date());
			eventDao.update(existing);
		//}
	}


	private void deleteOngoingCalendarEvents(Event event, List<ValidationError> listOfError) {
		Date currentTime = DateTime.now().toDate();
		List<CalenderEvent> ongoingEvents = calEventDao.getOngoingCalendarEvents(event);

		for(CalenderEvent ongoing:ongoingEvents){
			ongoing.setEndTime(currentTime);
			calEventDao.update(ongoing);
		}					
		
	}


	@Override
	@Transactional
	public BaseResponse updateCalendarEvent(CalenderEvent calEvent, String token) {
		BaseResponse response;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		CalenderEvent existing = calenderEventValidator.validateGuid(calEvent.getGuid(), listOfError);
		if(listOfError.isEmpty()){
			if(!existing.getRestaurantGuid().equals(calEvent.getRestaurantGuid())){
				listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.UNAUTHORIZED),ErrorCodes.UNAUTHORIZED));
			}else{
				Date currrentTime= DateTime.now().toDate();
				if(existing.getStartTime().after(currrentTime)){ // Upcoming
					calEvent.copyExistingValues(existing);
				}else{
					if(existing.getEndTime().after(currrentTime)){ // Ongoing
						calenderEventValidator.validateOngoingCalendarEventOnUpdate(calEvent, existing, listOfError);					
					}else{ // Finished
						listOfError.add(new ValidationError(Constants.END_TIME, ErrorCodes.FINISHED_EVENT_NOT_UPDATED));
					}
				}
			}
		}
		if(listOfError.isEmpty()){		
			calEventDao.update(calEvent);
			response = new DeleteResponse(ResponseCodes.CALEVENT_UPDATED_SUCCESSFULLY, calEvent.getGuid());
		}else{
			response = new ErrorResponse(ResponseCodes.CALEVENT_UPDATION_FAILURE, listOfError);
		}
		return response;
	}	
	

	
	

}

