package com.clicktable.validate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;

import com.clicktable.dao.intf.BuildingDao;
import com.clicktable.dao.intf.LocalityDao;
import com.clicktable.dao.intf.RegionDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.model.BlackOutHours;
import com.clicktable.model.BlackOutShift;
import com.clicktable.model.OperationalHours;
import com.clicktable.model.Reservation;
import com.clicktable.model.RestSystemConfigModel;
import com.clicktable.model.Restaurant;
import com.clicktable.model.RestaurantContactInfo;
import com.clicktable.model.RestaurantContactInfoAdmin;
import com.clicktable.model.RestaurantGeneralInfo;
import com.clicktable.model.Shift;
import com.clicktable.model.Staff;
import com.clicktable.model.UserInfoModel;
import com.clicktable.repository.RestaurantRepo;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;
import com.itextpdf.text.log.SysoCounter;

@org.springframework.stereotype.Service
public class RestaurantValidator extends EntityValidator<Restaurant> {

	//private static final Entity Shift = null;

	/**
	 * validations on restaurant at the time of addition
	 * 
	 * @param restaurant
	 * @return
	 */

	@Autowired
	RestaurantDao restDao;
	
	@Autowired
	RegionDao regionDao;
	
	@Autowired
	LocalityDao localityDao;
	
	@Autowired
	BuildingDao buildingDao;

	@Autowired
	RestaurantRepo restRepo;
	
	@Autowired
	StaffValidator staffValidator;

	public List<ValidationError> validateRestaurantOnAdd(Restaurant restaurant) {
		List<ValidationError> errorList = validateOnAdd(restaurant);
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, restaurant.getStatus(), restaurant.getLanguageCode());

		errorList.addAll(validateEnumValues(restaurant, Constants.RESTAURANT_MODULE));
		// minCapacity<capacity and minCapacity>0
		if ((restaurant.getCapacity() != null) && (restaurant.getMinimumCapacity() != null)) {
			if (restaurant.getMinimumCapacity() > restaurant.getCapacity()) {
				errorList = CustomValidations.populateErrorList(errorList, Constants.MIN_CAPACITY, UtilityMethods.getErrorMsg(ErrorCodes.CAPACITY_LESS_THAN_MIN_CAPACITY),ErrorCodes.CAPACITY_LESS_THAN_MIN_CAPACITY);
			}
			if (restaurant.getMinimumCapacity() <= 0) {
				errorList = CustomValidations.populateErrorList(errorList, Constants.MIN_CAPACITY, UtilityMethods.getErrorMsg(ErrorCodes.MIN_CAPACITY_SHOULD_BE_GREATER_THAN_ZERO),ErrorCodes.MIN_CAPACITY_SHOULD_BE_GREATER_THAN_ZERO);
			}

		}
		// maxCapacity>capacity and maxCapacity>0
		if ((restaurant.getCapacity() != null) && (restaurant.getMaximumCapacity() != null)) {
			if (restaurant.getMaximumCapacity() < restaurant.getCapacity()) {
				errorList = CustomValidations.populateErrorList(errorList, Constants.MAX_CAPACITY, UtilityMethods.getErrorMsg(ErrorCodes.CAPACITY_MORE_THAN_MAX_CAPACITY),ErrorCodes.CAPACITY_MORE_THAN_MAX_CAPACITY);
			}
			if (restaurant.getMaximumCapacity() <= 0) {
				errorList = CustomValidations.populateErrorList(errorList, Constants.MAX_CAPACITY, UtilityMethods.getErrorMsg(ErrorCodes.MAX_CAPACITY_SHOULD_BE_GREATER_THAN_ZERO),ErrorCodes.MAX_CAPACITY_SHOULD_BE_GREATER_THAN_ZERO);
			}
		}
		// minCapacity<=maxCapacity
		if ((restaurant.getMinimumCapacity() != null) && (restaurant.getMaximumCapacity() != null) && (restaurant.getMinimumCapacity() > restaurant.getMaximumCapacity())) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.MIN_CAPACITY, UtilityMethods.getErrorMsg(ErrorCodes.MIN_CAPACITY_MORE_THAN_MAX_CAPACITY),ErrorCodes.MIN_CAPACITY_MORE_THAN_MAX_CAPACITY);
		}
		return errorList;
	}
	
	
	public List<ValidationError> validateRestaurantGeneralInfoOnAdd(RestaurantGeneralInfo restaurantGeneralInfo) {
		List<ValidationError> errorList = validateOnAdd(restaurantGeneralInfo);
		errorList = CustomValidations.validateLanguageCode(errorList, restaurantGeneralInfo.getLanguageCode());

		errorList.addAll(validateEnumValues(restaurantGeneralInfo, Constants.RESTAURANT_GENERAL_INFO));
		
		return errorList;
	}
	
	public List<ValidationError> validateRestaurantContactInfo(RestaurantContactInfo restaurant) 
	{
	    List<ValidationError> errorList = validateOnAdd(restaurant);

	    return errorList;
	}
	
	
	

	/**
	 * validations on restaurant at the time of updation
	 * 
	 * @param restaurant
	 * @return
	 */

	public List<ValidationError> validateRestaurantOnUpdate(Restaurant restaurant) {
		List<ValidationError> errorList = validateRestaurantOnAdd(restaurant);
		return errorList;
	}
	
	public List<ValidationError> validateRestaurantGeneralInfoOnUpdate(RestaurantGeneralInfo restaurantGeneralInfo) {
		List<ValidationError> errorList = validateRestaurantGeneralInfoOnAdd(restaurantGeneralInfo);
		return errorList;
	}


	@Override
	public Map<String, Object> validateFinderParams(Map<String, Object> params, Class type) {
		Map<String, Object> validParamMap;
		validParamMap = super.validateFinderParams(params, type);
		for (Entry<String, Object> entry : params.entrySet()) {
			if(Restaurant.getCustomFinderParams().contains(entry.getKey()))
				validParamMap.put(entry.getKey(), (String) entry.getValue());
		}
		return validParamMap;
	}
	
	
	public Restaurant validateRestaurantInNeo4j(String restId, UserInfoModel userInfo, List<ValidationError> errorList) {
		Restaurant rest = restDao.find(restId);
		if (rest == null)
			errorList.add(createError(Constants.REST_GUID, ErrorCodes.INVALID_REST_ID));
		else if (!(userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))) 
		{
			if (!(restId.equals(userInfo.getRestGuid())))
				errorList.add(createError(Constants.REST_GUID, ErrorCodes.REST_FOR_STAFF_NOT_VALID));
		}
		return rest;
	}
	
	public void validateRestaurant(String restId, UserInfoModel userInfo, List<ValidationError> listOfError) 
	{
		if ((!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))) 
		{
			System.out.println((restId.equals(userInfo.getRestGuid())));
			if (!(restId.equals(userInfo.getRestGuid()))) 
			{
				listOfError.add(createError(Constants.REST_GUID, ErrorCodes.INVALID_REST_ID));
			}
		}
	}
	
	
	
	
	public List<Map<String, Long>> validateOperationalHours(OperationalHours ophr,OperationalHours existingOphr,List<ValidationError> listOfError)
	{
		
		//restGuid is mandatory if accesstoken is of CT-ADMIN. Check ophr.getRestGuid should not be null
		//  sunday....... saturday is mandatory
		// for each sunday....... saturday , atleast one shift entity is required.
		/*//for each shift, diff between shiftstartTime and endtime(in mins)/diningslot should be zero.
		// shiftName should be from enum [BREAKFAST,LUNCH,DINNER,ALLDAY] */
		
		List<List<Shift>> queue=new ArrayList<List<Shift>>();
		
		Map<String,List<Reservation>> resultMap = new HashMap<>();
		
		if(null!=ophr){
			if(null!=ophr.getMonday() && ophr.getMonday().size()>0){
				                      queue.add(ophr.getMonday());
				if(null!=ophr.getTuesday() && ophr.getTuesday().size()>0){
					                  queue.add(ophr.getTuesday());
					if(null!=ophr.getWednesday() && ophr.getWednesday().size()>0){
						              queue.add(ophr.getWednesday());
						if(null!=ophr.getThursday() && ophr.getThursday().size()>0){
							          queue.add(ophr.getThursday());
							if(null!=ophr.getFriday() && ophr.getFriday().size()>0){
								      queue.add(ophr.getFriday());
								if(null!=ophr.getSaturday() && ophr.getSaturday().size()>0){
									  queue.add(ophr.getSaturday());
									if(null!=ophr.getSunday() && ophr.getSunday().size()>0){
									  queue.add(ophr.getSunday());
									}else{
										listOfError
										.add(new ValidationError(
												Constants.OP_HRS+Constants.SUNDAY,
												UtilityMethods
														.getErrorMsg(ErrorCodes.SHIFT_OPERATIONAL_HOURS),ErrorCodes.SHIFT_OPERATIONAL_HOURS));
									}
								}else{listOfError
								.add(new ValidationError(
										Constants.OP_HRS+Constants.SATURDAY,
										UtilityMethods
												.getErrorMsg(ErrorCodes.MISSING_OPERATIONAL_HOURS),ErrorCodes.MISSING_OPERATIONAL_HOURS));	
								}
							}else{listOfError
							.add(new ValidationError(
									Constants.OP_HRS+Constants.FRIDAY,
									UtilityMethods
											.getErrorMsg(ErrorCodes.MISSING_OPERATIONAL_HOURS),ErrorCodes.MISSING_OPERATIONAL_HOURS));	
							}
						}else{listOfError
						.add(new ValidationError(
								Constants.OP_HRS+Constants.THURSDAY,
								UtilityMethods
										.getErrorMsg(ErrorCodes.MISSING_OPERATIONAL_HOURS),ErrorCodes.MISSING_OPERATIONAL_HOURS));
						}
					}else{listOfError
						.add(new ValidationError(
								Constants.OP_HRS+Constants.WEDNESDAY,
								UtilityMethods
										.getErrorMsg(ErrorCodes.MISSING_OPERATIONAL_HOURS),ErrorCodes.MISSING_OPERATIONAL_HOURS));
						}	
				}else{listOfError
					.add(new ValidationError(
							Constants.OP_HRS+Constants.TUESDAY,
							UtilityMethods
									.getErrorMsg(ErrorCodes.MISSING_OPERATIONAL_HOURS),ErrorCodes.MISSING_OPERATIONAL_HOURS));
					}
				
				
			}else{listOfError
				.add(new ValidationError(
						Constants.OP_HRS+Constants.MONDAY,
						UtilityMethods
								.getErrorMsg(ErrorCodes.MISSING_OPERATIONAL_HOURS),ErrorCodes.MISSING_OPERATIONAL_HOURS));
				}
            
            
            
			
		}
		List<Reservation> resvList = new ArrayList<>();
		List<Map<String, Long>> resultShiftList = new ArrayList<Map<String, Long>>();
		
		if (listOfError.isEmpty()) {
			
			
			

			if (null != existingOphr) {
				if (null != existingOphr.getMonday() && existingOphr.getMonday().size() > 0	&& null != ophr.getMonday()	&& ophr.getMonday().size() > 0) {
					List<Shift> existing = existingOphr.getMonday();
					List<Shift> current = ophr.getMonday();
					populateShiftList(existing, current, resultShiftList, Calendar.MONDAY);
				}


				if (null != existingOphr.getTuesday() && existingOphr.getTuesday().size() > 0	&& null != ophr.getTuesday()	&& ophr.getTuesday().size() > 0) {
					List<Shift> existing = existingOphr.getTuesday();
					List<Shift> current = ophr.getTuesday();
					populateShiftList(existing, current, resultShiftList, Calendar.TUESDAY);
				}


				if (null != existingOphr.getWednesday() && existingOphr.getWednesday().size() > 0	&& null != ophr.getWednesday()	&& ophr.getWednesday().size() > 0) {
					List<Shift> existing = existingOphr.getWednesday();
					List<Shift> current = ophr.getWednesday();
					populateShiftList(existing, current, resultShiftList, Calendar.WEDNESDAY);

				}

				if (null != existingOphr.getThursday() && existingOphr.getThursday().size() > 0	&& null != ophr.getThursday()	&& ophr.getThursday().size() > 0) {
					List<Shift> existing = existingOphr.getThursday();
					List<Shift> current = ophr.getThursday();
					populateShiftList(existing, current, resultShiftList, Calendar.THURSDAY);

				}

				if (null != existingOphr.getFriday() && existingOphr.getFriday().size() > 0	&& null != ophr.getFriday()	&& ophr.getFriday().size() > 0) {
					List<Shift> existing = existingOphr.getFriday();
					List<Shift> current = ophr.getFriday();
					populateShiftList(existing, current, resultShiftList, Calendar.FRIDAY);

				}

				if (null != existingOphr.getSaturday() && existingOphr.getSaturday().size() > 0	&& null != ophr.getSaturday()	&& ophr.getSaturday().size() > 0) {
					List<Shift> existing = existingOphr.getSaturday();
					List<Shift> current = ophr.getSaturday();
					populateShiftList(existing, current, resultShiftList, Calendar.SATURDAY);

				}

				if (null != existingOphr.getSunday() && existingOphr.getSunday().size() > 0	&& null != ophr.getSunday()	&& ophr.getSunday().size() > 0) {
					List<Shift> existing = existingOphr.getSunday();
					List<Shift> current = ophr.getSunday();
					populateShiftList(existing, current, resultShiftList, Calendar.SUNDAY);

				}

			}
			
			
		}
		
		
		
		
		if(listOfError.isEmpty()){
			//Iterator<List<com.clicktable.model.Shift>> iterator=queue.iterator();
			
			for(int queueN=0;queueN<queue.size();queueN++)
			{
			 List<Shift> shiftList=null;
			//do{
				shiftList=queue.get(queueN);
				for(int i=0;i<shiftList.size();i++){
					Shift shift=shiftList.get(i);
					/* Validating Overlapping Shift Slot :*/
					for(int j=i+1;j<shiftList.size();j++){
						
						Shift overlap_shift=shiftList.get(j);
						if((shift.getStartTimeInMillis()==overlap_shift.getStartTimeInMillis() && shift.getEndTimeInMillis()==overlap_shift.getEndTimeInMillis()) ||
								(shift.getStartTimeInMillis()<=overlap_shift.getEndTimeInMillis() && shift.getEndTimeInMillis()>=overlap_shift.getStartTimeInMillis())||
								(shift.getStartTimeInMillis()>=overlap_shift.getStartTimeInMillis() && shift.getEndTimeInMillis()<=overlap_shift.getEndTimeInMillis())){
							
							listOfError
							.add(new ValidationError(
									Constants.OP_HRS+overlap_shift.getDay() +"["+shift.getShiftName()+":"+overlap_shift.getShiftName()+"]",UtilityMethods
									.getErrorMsg(ErrorCodes.OVERLAPPIN_SHIFT_OPHOURS),ErrorCodes.OVERLAPPIN_SHIFT_OPHOURS));
							return resultShiftList;
							
						}
						
						
					}
					//Validating Enum
					
					List<String> error=validateEnumObjectState(shift.getShiftName());
					if( error.isEmpty()){
						String startTimeArr[]=shift.getStartTime().split(":");
						String endTimeArr[]=shift.getEndTime().split(":");
						if(Integer.parseInt(startTimeArr[0])<0 || Integer.parseInt(endTimeArr[0])<0){
							listOfError
							.add(new ValidationError(
									Constants.OP_HRS+shiftList.get(i).getDay(),UtilityMethods
									.getErrorMsg(ErrorCodes.NEGATIVE_SHIFT_OPHOURS),ErrorCodes.NEGATIVE_SHIFT_OPHOURS));
							return resultShiftList;
						}
						if(shift.getEndTimeInMillis()<shift.getStartTimeInMillis()  )
						{
							listOfError
							.add(new ValidationError(
									Constants.OP_HRS+shiftList.get(i).getDay(),UtilityMethods
									.getErrorMsg(ErrorCodes.INVALID_SHIFT_OPHOURS),ErrorCodes.INVALID_SHIFT_OPHOURS));
							return resultShiftList;
						}
						if(((shift.getEndTimeInMillis()-shift.getStartTimeInMillis())%(shift.getDiningSlot()*60*1000L))!=0){
							
							listOfError
							.add(new ValidationError(
									Constants.OP_HRS+shiftList.get(i).getDay(),UtilityMethods
									.getErrorMsg(ErrorCodes.INVALID_SHIFT_OPERATIONAL_HOURS),ErrorCodes.INVALID_SHIFT_OPERATIONAL_HOURS));
							return resultShiftList;
						}
							
					}else{
						listOfError
						.add(new ValidationError(
								Constants.OP_HRS+shiftList.get(i).getDay(),error));
						
						return resultShiftList;
					}
				
					}
				
		//	}while(iterator.hasNext());
				Long ongoingShiftStart = 0L;
				Long ongoingShiftEnd = 0L;
				Long circularShiftEnd= 0L; // Need for Sunday
				Shift tempShift=null;
				Shift circularShift=null; // Need for Sunday
				for(Shift shift : shiftList){
					
					tempShift=shift;
					if(ongoingShiftEnd>=0 && ongoingShiftEnd<shift.getEndTimeInMillis()){
						ongoingShiftEnd = shift.getEndTimeInMillis();
						
					}else if(ongoingShiftEnd==0L){
						ongoingShiftEnd = shift.getEndTimeInMillis();
					}
					
					if(ongoingShiftStart>=0 && ongoingShiftStart>shift.getStartTimeInMillis()){
						ongoingShiftStart = shift.getStartTimeInMillis();
						
					}else if(ongoingShiftStart==0L){
						ongoingShiftStart = shift.getStartTimeInMillis();
					}
					   
					
				}
				if(tempShift.getDay().equalsIgnoreCase(Constants.MONDAY)){
					
					System.out.println( "--------ONE TIME CALL---------");
					for(int queueK=queue.size()-1;;){
						List<Shift> circular_shift=queue.get(queueK);
						for(Shift shift : circular_shift){
							circularShift=shift;
							if(circularShiftEnd>=0 && circularShiftEnd<shift.getEndTimeInMillis()){
								circularShiftEnd = shift.getEndTimeInMillis();
								
							}else if(circularShiftEnd==0L){
								circularShiftEnd = shift.getEndTimeInMillis();
							}
						}
						break;
					}
					
					 //Validation For Sunday and Monday Shifts 
					
					if((circularShiftEnd>24*60*60*1000 && ongoingShiftStart<24*60*60*1000)){
						if(circularShiftEnd-24*60*60*1000>=ongoingShiftStart){
						listOfError
						.add(new ValidationError(
								Constants.OP_HRS+circularShift.getDay() ,UtilityMethods
								.getErrorMsg(ErrorCodes.OVERLAPPIN_SHIFT_OPHOURS),ErrorCodes.OVERLAPPIN_SHIFT_OPHOURS));
						return resultShiftList;
						}
					}
					
				}
				
				for(int queueM=queueN+1;queueM<queue.size()-1 ;queueM++){
						List<Shift> overlap_shift=queue.get(queueM);
						ongoingShiftStart=0L;
					for(Shift shift : overlap_shift){
						
						if(ongoingShiftStart>=0 && ongoingShiftStart>shift.getStartTimeInMillis()){
							ongoingShiftStart = shift.getStartTimeInMillis();
							
						}else if(ongoingShiftStart==0L){
							ongoingShiftStart = shift.getStartTimeInMillis();
						}
						
						
					}
					break;
				}
					if((ongoingShiftEnd>24*60*60*1000 && ongoingShiftStart<24*60*60*1000)){
						if(ongoingShiftEnd-24*60*60*1000>=ongoingShiftStart){
						listOfError
						.add(new ValidationError(
								Constants.OP_HRS+tempShift.getDay() +"["+tempShift.getShiftName()+"]",UtilityMethods
								.getErrorMsg(ErrorCodes.OVERLAPPIN_SHIFT_OPHOURS),ErrorCodes.OVERLAPPIN_SHIFT_OPHOURS));
						return resultShiftList;
						}
					}
				
			}
		}
		
		
		
		return resultShiftList;
		
	}
	
	
	private List<String> validateEnumObjectState(String shiftName) 
	{
	    Logger.debug("validating enum values ...........................................................");
		List<String> errorList = new ArrayList<String>();
		boolean isContains=UtilityMethods.getEnumValues(Constants.OPERATION_HOUR_MODULE, Constants.OPERATION_HOUR_CATEGORY).contains(shiftName);
		if(!isContains){
			errorList.add(UtilityMethods
					.getErrorMsg(ErrorCodes.INVALID_SHIFT_NAMES));
		}
		
		return errorList;
	}
	
	
	public List<ValidationError> validateBlackOutOperationalHours(BlackOutHours ophr,List<ValidationError> listOfError)
	{
		
	 /* Get Operation Hour and check overlapping  */	
	  Map<String,Object> params=new HashMap<String,Object>();
	  params.put(Constants.REST_GUID, ophr.getRestGuid());
	  OperationalHours op_hr= restDao.getOperationalHours(params);
		
		
     
		
		if ((null != ophr.getMonday() && ophr.getMonday().size() > 0)) {
			if (null != op_hr && op_hr.getMonday().size() > 0) {
				 validateBlackOutHoursList(ophr.getMonday(),
						op_hr.getMonday(), listOfError);
				 if(!listOfError.isEmpty()){
					 return listOfError;
				 }
			} else {

				listOfError.add(new ValidationError(Constants.OP_HRS
						+ ophr.getMonday(), UtilityMethods
						.getErrorMsg(ErrorCodes.SHIFT_OPHOURS_MISSING),
						ErrorCodes.SHIFT_OPHOURS_MISSING));
				return listOfError;
			}

		}
		if (null != ophr.getTuesday() && ophr.getTuesday().size() > 0) {
			if (null != op_hr && op_hr.getTuesday().size() > 0) {
				 validateBlackOutHoursList(ophr.getTuesday(),
						op_hr.getTuesday(), listOfError);
				if(!listOfError.isEmpty()){
					 return listOfError;
				 }
			} else {

				listOfError.add(new ValidationError(Constants.OP_HRS
						+ ophr.getTuesday(), UtilityMethods
						.getErrorMsg(ErrorCodes.SHIFT_OPHOURS_MISSING),
						ErrorCodes.SHIFT_OPHOURS_MISSING));
				return listOfError;
			}
		}
		if (null != ophr.getWednesday() && ophr.getWednesday().size() > 0) {

			if (null != op_hr && op_hr.getWednesday().size() > 0) {
				 validateBlackOutHoursList(ophr.getWednesday(),
						op_hr.getWednesday(), listOfError);
				 if(!listOfError.isEmpty()){
					 return listOfError;
				 }
			} else {

				listOfError.add(new ValidationError(Constants.OP_HRS
						+ ophr.getWednesday(), UtilityMethods
						.getErrorMsg(ErrorCodes.SHIFT_OPHOURS_MISSING),
						ErrorCodes.SHIFT_OPHOURS_MISSING));
				return listOfError;
			}

		}
		if (null != ophr.getThursday() && ophr.getThursday().size() > 0) {

			if (null != op_hr && op_hr.getThursday().size() > 0) {
				 validateBlackOutHoursList(ophr.getThursday(),
						op_hr.getThursday(), listOfError);
				 if(!listOfError.isEmpty()){
					 return listOfError;
				 }
			} else {

				listOfError.add(new ValidationError(Constants.OP_HRS
						+ ophr.getThursday(), UtilityMethods
						.getErrorMsg(ErrorCodes.SHIFT_OPHOURS_MISSING),
						ErrorCodes.SHIFT_OPHOURS_MISSING));
				return listOfError;
			}

		}
		if (null != ophr.getFriday() && ophr.getFriday().size() > 0) {

			if (null != op_hr && op_hr.getFriday().size() > 0) {
				 validateBlackOutHoursList(ophr.getFriday(),
						op_hr.getFriday(), listOfError);
				 if(!listOfError.isEmpty()){
					 return listOfError;
				 }
			} else {

				listOfError.add(new ValidationError(Constants.OP_HRS
						+ ophr.getFriday(), UtilityMethods
						.getErrorMsg(ErrorCodes.SHIFT_OPHOURS_MISSING),
						ErrorCodes.SHIFT_OPHOURS_MISSING));
				return listOfError;
			}

		}
		if (null != ophr.getSaturday() && ophr.getSaturday().size() > 0) {
			if (null != op_hr && op_hr.getSaturday().size() > 0) {
				 validateBlackOutHoursList(ophr.getSaturday(),
						op_hr.getSaturday(), listOfError);
				 if(!listOfError.isEmpty()){
					 return listOfError;
				 }
			} else {

				listOfError.add(new ValidationError(Constants.OP_HRS
						+ ophr.getSaturday(), UtilityMethods
						.getErrorMsg(ErrorCodes.SHIFT_OPHOURS_MISSING),
						ErrorCodes.SHIFT_OPHOURS_MISSING));
				return listOfError;
			}
		}
		if (null != ophr.getSunday() && ophr.getSunday().size() > 0) {

			if (null != op_hr && op_hr.getSunday().size() > 0) {
				 validateBlackOutHoursList(ophr.getSunday(),
						op_hr.getSunday(), listOfError);
				 if(!listOfError.isEmpty()){
					 return listOfError;
				 }
			} else {

				listOfError.add(new ValidationError(Constants.OP_HRS
						+ ophr.getSunday(), UtilityMethods
						.getErrorMsg(ErrorCodes.SHIFT_OPHOURS_MISSING),
						ErrorCodes.SHIFT_OPHOURS_MISSING));
				return listOfError;
			}

		}   
                              
		
		return listOfError;
	}
	

	public List<ValidationError> validateRestaurantSystemConfigModelOnAdd(RestSystemConfigModel restSystemConfigModel) {
		List<ValidationError> errorList = validateOnAdd(restSystemConfigModel);
		return errorList;
	}
	
	
	private List<ValidationError> validateBlackOutHoursList(List<BlackOutShift> black_out_shift,List<Shift> op_hr_shift,List<ValidationError> listOfError){
		
		String day="";
		for(int n=0;n<black_out_shift.size();n++){	
			boolean flag=false;
			
		for(int m=0;m<op_hr_shift.size();m++){
			if(m==0){
			day=op_hr_shift.get(m).getDay();
			}
			if((op_hr_shift.get(m).getStartTimeInMillis()<=black_out_shift.get(n).getStartTimeInMillis() && op_hr_shift.get(m).getStartTimeInMillis()<=black_out_shift.get(n).getEndTimeInMillis())
			&&  (op_hr_shift.get(m).getEndTimeInMillis()>=black_out_shift.get(n).getStartTimeInMillis()  &&  op_hr_shift.get(m).getEndTimeInMillis()>=black_out_shift.get(n).getEndTimeInMillis())){
				flag=true;
			  }
			}
			if(!flag){
				listOfError
				.add(new ValidationError(
						Constants.BLACKOUT_OP_HRS+day,UtilityMethods
						.getErrorMsg(ErrorCodes.INVALID_BLACKOUT_OPHR),ErrorCodes.INVALID_BLACKOUT_OPHR));
				return listOfError;
			}
		}
		
			
		
		
		
		for(int i=0;i<black_out_shift.size();i++){
			 BlackOutShift shift=black_out_shift.get(i);
							
			/* Validating Overlapping Blackout Shift Slot :*/
			for(int j=i+1;j<black_out_shift.size() && i<2;j++){					
				BlackOutShift overlap_shift=black_out_shift.get(j);
				if((shift.getStartTimeInMillis()==overlap_shift.getStartTimeInMillis() && shift.getEndTimeInMillis()==overlap_shift.getEndTimeInMillis()) ||
						(shift.getStartTimeInMillis()<overlap_shift.getEndTimeInMillis() && shift.getEndTimeInMillis()>overlap_shift.getStartTimeInMillis())||
						(shift.getStartTimeInMillis()>overlap_shift.getStartTimeInMillis() && shift.getEndTimeInMillis()<overlap_shift.getEndTimeInMillis())){
					
					listOfError
					.add(new ValidationError(
							Constants.BLACKOUT_OP_HRS+day,UtilityMethods
							.getErrorMsg(ErrorCodes.BLACK_OVERLAPPIN_SHIFT_OPHOURS),ErrorCodes.BLACK_OVERLAPPIN_SHIFT_OPHOURS));
					return listOfError;
					
				}					
			}
			String startTimeArr[]=shift.getStartTime().split(":");
			String endTimeArr[]=shift.getEndTime().split(":");
			if(Integer.parseInt(startTimeArr[0])<0 || Integer.parseInt(endTimeArr[0])<0){
				listOfError
				.add(new ValidationError(
						Constants.BLACKOUT_OP_HRS+black_out_shift.get(i).getDay(),UtilityMethods
						.getErrorMsg(ErrorCodes.NEGATIVE_SHIFT_OPHOURS),ErrorCodes.NEGATIVE_SHIFT_OPHOURS));
				return listOfError;
			}
			if(shift.getEndTimeInMillis()<shift.getStartTimeInMillis()  )
			{
				listOfError
				.add(new ValidationError(
						Constants.BLACKOUT_OP_HRS+black_out_shift.get(i).getDay(),UtilityMethods
						.getErrorMsg(ErrorCodes.INVALID_SHIFT_OPHOURS),ErrorCodes.INVALID_SHIFT_OPHOURS));
				return listOfError;
			}
		}
		
		return listOfError;
	  }

	public List<ValidationError> validateRestaurantContactInfoAdmin(
			RestaurantContactInfoAdmin contactInfo) {
		
		List<ValidationError> errorList = validateOnAdd(contactInfo);

		return errorList;
	}
	
	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.REST_GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_REST_ID;
	}


	public List<ValidationError> validateRestaurantOnPatchUpdate(Restaurant rest) {
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		listOfError = CustomValidations.validateStatusAndLanguageCode(listOfError, rest.getStatus(), rest.getLanguageCode());
		return listOfError;
	}


	public List<ValidationError> validateRestaurantOnReactivation(Staff staff) {
		List<ValidationError> listOfError = staffValidator.validateStaffOnAdd(staff);
		if (listOfError.isEmpty() && staff.getRestaurantGuid()!=null) {
			Restaurant rest = restDao.findRestaurantByGuid(staff.getRestaurantGuid());
			if(rest == null){
				listOfError
				.add(new ValidationError(
						Constants.REST_GUID,UtilityMethods
						.getErrorMsg(ErrorCodes.INVALID_RESTAURANT_GUID),ErrorCodes.INVALID_RESTAURANT_GUID));
				return listOfError;
			}else{
				if(rest.getStatus().equals(Constants.ACTIVE_STATUS)){
					listOfError
					.add(new ValidationError(
							Constants.REST_GUID,UtilityMethods
							.getErrorMsg(ErrorCodes.RESTAURANT_ALREADY_ACTIVE),ErrorCodes.RESTAURANT_ALREADY_ACTIVE));
					return listOfError;
				}else if(rest.getStatus().equals(Constants.DELETED_STATUS)){
					listOfError
					.add(new ValidationError(
							Constants.REST_GUID,UtilityMethods
							.getErrorMsg(ErrorCodes.DELETED_RESTAURANT_CAN_NOT_BE_DEACTIVATED),ErrorCodes.DELETED_RESTAURANT_CAN_NOT_BE_DEACTIVATED));
					return listOfError;
				}
			}
			
		}else{
			listOfError
			.add(new ValidationError(
					Constants.REST_GUID,UtilityMethods
					.getErrorMsg(ErrorCodes.REST_GUID_REQUIRED),ErrorCodes.REST_GUID_REQUIRED));
			return listOfError;
		}
		return listOfError;
	}

	
	private void getAllShiftForReservation(List<Map<String,Long>> shiftList,List<com.clicktable.model.Shift> existing,
			List<com.clicktable.model.Shift> current,
			int day, Long currentDayShiftStartTime, Long currentDayShiftEndTime){
		
			  for(int i=0;i<existing.size();i++){
				   currentDayShiftStartTime = currentDayShiftStartTime == 0L ? existing.get(i).getStartTimeInMillis() : 
						(currentDayShiftStartTime < existing.get(i).getStartTimeInMillis() ? currentDayShiftStartTime : existing.get(i).getStartTimeInMillis()); 

			       currentDayShiftEndTime = currentDayShiftEndTime == 0 ? existing.get(i).getEndTimeInMillis2() :
						(currentDayShiftEndTime < existing.get(i).getEndTimeInMillis2() ? existing.get(i).getEndTimeInMillis2() : currentDayShiftEndTime);
			  }	
			

			  Calendar c1 = Calendar.getInstance();			  
			  Calendar c2 = Calendar.getInstance();
			  c2.add(Calendar.MONTH, 2);
			  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			  while(c2.after(c1)) {
				    if(c1.get(Calendar.DAY_OF_WEEK)==day){
				    	try {
				    		Map<String,Long> map=new HashMap<String, Long>();
							System.out.println(sdf.parse(sdf.format(new java.util.Date(c1.getTimeInMillis()))).getTime());
							map.put(Constants.EST_START_TIME, sdf.parse(sdf.format(new java.util.Date(c1.getTimeInMillis()))).getTime()+currentDayShiftStartTime);
							map.put(Constants.EST_END_TIME, sdf.parse(sdf.format(new java.util.Date(c1.getTimeInMillis()))).getTime()+currentDayShiftEndTime);
							shiftList.add(map);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }    
				     c1.add(Calendar.DATE,1);
				}
      	}
	
	private int getCountOfShiftList(List<com.clicktable.model.Shift> existing,
			List<com.clicktable.model.Shift> current){
		int count=0;
		for (int i = 0; i < existing.size(); i++) {
			for (int j = 0; j < current.size(); j++) {
				if ((existing.get(i).getStartTimeInMillis() == current
						.get(j).getStartTimeInMillis() && existing
						.get(i).getEndTimeInMillis() == current
						.get(j).getEndTimeInMillis())) {
					count++;
				}
			}
	   }
		return count;
	}
	
	
	private void populateShiftList(List<Shift> existing,List<Shift> current,List<Map<String, Long>> resultShiftList,int day)
	{
		int count = 0;
		
		if (existing.size() == current.size()) {
			count = getCountOfShiftList(existing, current);
			if (count != existing.size()) {
				getAllShiftForReservation(resultShiftList,existing, current, day, 0L, 0L);
			}
		} else {
			   getAllShiftForReservation(resultShiftList, existing,current, day, 0L, 0L);
		}
	}
	
	
}
	



