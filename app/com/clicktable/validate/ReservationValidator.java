package com.clicktable.validate;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.i18n.Messages;

import com.clicktable.dao.intf.EventDao;
import com.clicktable.dao.intf.QueueDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.TableShuffleDao;
import com.clicktable.dao.intf.TatDao;
import com.clicktable.dao.intf.WaitlistDao;
import com.clicktable.model.Event;
import com.clicktable.model.OperationalHours;
import com.clicktable.model.Reservation;
import com.clicktable.model.ReservationHistory;
import com.clicktable.model.Shift;
import com.clicktable.model.Table;
import com.clicktable.model.UserInfoModel;
import com.clicktable.repository.CalenderEventRepo;
import com.clicktable.repository.ReservationHistoryRepo;
import com.clicktable.repository.TableRepo;
import com.clicktable.service.intf.TableShuffleService;
import com.clicktable.service.intf.WaitlistService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

@org.springframework.stereotype.Service
public class ReservationValidator extends EntityValidator<Reservation> {

	@Autowired
	ReservationDao reservationDao;
	@Autowired
	RestaurantDao restaurantDao;
	@Autowired
	ReservationHistoryRepo reservationHistoryRepo;
	@Autowired
	TableRepo tableRepo;
	@Autowired
	TatDao tatDao;
	@Autowired
	CalenderEventRepo calEventRepo;
	@Autowired
	RestaurantValidator restValidator;
	
	@Autowired
	TableShuffleDao shuffleDao;
	@Autowired
	TableShuffleService shuffleService;
	
	@Autowired
	WaitlistDao waitlistDao;
	@Autowired
	WaitlistService waitlistService;
	@Autowired
	QueueDao queueDao;
	@Autowired
	EventDao eventDao;

	public List<ValidationError> validateReservationOnCreate(Reservation reservation, String mode) {
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		if (!mode.equalsIgnoreCase(Constants.PATCH)) {
			errorList = validateOnAdd(reservation);
			errorList.addAll(validateEnumValues(reservation, Constants.RESERVATION_MODULE));
		} else {
			errorList.addAll(validateEnumValues(reservation, Constants.RESERVATION_MODULE));
		}
		if (reservation.getTableGuid().size() == 0) {
			errorList.add(new ValidationError(Constants.TABLE_GUID, UtilityMethods.getErrorMsg(ErrorCodes.TABLE_ID_REQUIRED), ErrorCodes.TABLE_ID_REQUIRED));
		}
		if (null == reservation.getRestaurantGuid()) {
			errorList.add(new ValidationError(Constants.REST_ID, UtilityMethods.getErrorMsg(ErrorCodes.REST_ID_REQUIRED), ErrorCodes.REST_ID_REQUIRED));
		}

		if (reservation.getReservationStatus().equals(Constants.CANCELLED) && mode.equalsIgnoreCase("Update")) {
			if (reservation.getCancelledBy() == null)
				errorList.add(new ValidationError(Constants.CANCELLED_BY, UtilityMethods.getErrorMsg(ErrorCodes.CANCELLED_BY_REQUIRED), ErrorCodes.CANCELLED_BY_REQUIRED));
			if (reservation.getCancelledById() == null)
				errorList.add(new ValidationError(Constants.CANCELLED_BY_ID, UtilityMethods.getErrorMsg(ErrorCodes.CANCELLED_BY_ID_REQUIRED), ErrorCodes.CANCELLED_BY_ID_REQUIRED));
			if (reservation.getCancelledBy() == null)
				errorList.add(new ValidationError(Constants.CANCELLED_TIME, UtilityMethods.getErrorMsg(ErrorCodes.CANCELLED_TIME_REQUIRED), ErrorCodes.CANCELLED_TIME_REQUIRED));
		} /*else if (reservation.getReservationStatus().equals(Constants.CANCELLED) && mode.equalsIgnoreCase("Add")) {
			errorList.add(new ValidationError(Constants.RESERVATION_STATUS, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_RESERVATION), ErrorCodes.INVALID_RESERVATION));
		}*/

		if (!UtilityMethods.getEnumValues(Constants.TABLE_MODULE, Constants.TYPE).contains(reservation.getPrefferedTableType()) && (reservation.getPrefferedTableType() != null)) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.PREFFERED_TABLE_TYPE, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_PREFFERED_TABLE_TYPE),
					ErrorCodes.INVALID_PREFFERED_TABLE_TYPE);
		}
		
		if(reservation.getOfferId() != null)
		{
			errorList = validateOfferId(reservation, errorList);
		}

		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, reservation.getStatus(), reservation.getLanguageCode());

		return errorList;

	}

	public List<ValidationError> validateReservationOnUpdate(Reservation reservation, String mode) {
		List<ValidationError> errorList = validateReservationOnCreate(reservation, mode);
		return errorList;
	}


	/*public Restaurant validateRestaurantGuid(String guid, List<ValidationError> listOfError) {
		Restaurant restaurant = null;
		if (guid == null) {
			listOfError.add(createError(Constants.GUID, ErrorCodes.REST_ID_REQUIRED));
		} else {
			restaurant = restaurantDao.find(guid);
			if (restaurant == null)
				listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REST_ID), ErrorCodes.INVALID_REST_ID));
		}
		return restaurant;
	}*/

	public void validateTableAgainstRestaurant(Table table, String restGuid, List<ValidationError> listOfError) {
		restValidator.validateGuid(restGuid, listOfError);
		if (!table.getRestId().equals(restGuid) && listOfError.isEmpty())
			listOfError.add(new ValidationError(Constants.TABLE_ID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_TABLE_ID) + " " + table.getGuid(), ErrorCodes.INVALID_TABLE_ID));

	}

	public void validateTat(List<ValidationError> listOfError, Reservation reservation) {
		int tat_value = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("IST"));

		String tat = reservation.getTat();

		if ((null == tat || tat.trim().length() == 0 || tat.equals("0")) && reservation.getTableGuid().size() == 1) {
			Calendar calc = Calendar.getInstance();
			String dayNames[] = new DateFormatSymbols().getWeekdays();
			String value = "weekday";
			if (dayNames[calc.get(Calendar.DAY_OF_WEEK)].equalsIgnoreCase("SUNDAY") || dayNames[calc.get(Calendar.DAY_OF_WEEK)].equalsIgnoreCase("SATURDAY")) {
				value = "weekend";
			}
			tat_value = tatDao.get_tat_value(reservation.getRestaurantGuid(), reservation.getNumCovers(), value);
			if (tat_value > 0) {

				tat = String.valueOf(tat_value);
				reservation.setTat(tat);
			} else {
				listOfError.add(new ValidationError(Constants.TAT, UtilityMethods.getErrorMsg(ErrorCodes.TAT_VALUE_UNDEFINED) + " " + reservation.getNumCovers(), ErrorCodes.TAT_VALUE_UNDEFINED));
				return;
			}
		} else if ((null == tat || tat.trim().length() == 0) && reservation.getTableGuid().size() > 1) {
			listOfError.add(new ValidationError(Constants.TAT, UtilityMethods.getErrorMsg(ErrorCodes.TAT_VALUE_IS_MISSING) + " " + reservation.getNumCovers(), ErrorCodes.TAT_VALUE_IS_MISSING));
			return;
		} 


		Long time = reservation.getEstStartTime().getTime() + Integer.parseInt(tat.trim()) * 60 * 1000;
		sdf.format(new Date(time));
		try {
			reservation.setEstEndTime(sdf.parse(sdf.format(new Date(time))));
		} catch (ParseException e) {
			e.printStackTrace();
	}
	}

	public void validateTableReservedSlotTime(String restGuid, Reservation reservation, List<ValidationError> listOfError) {
		List<Table> tableList = null;
		if (reservation.getBookingMode().equals(Constants.ONLINE_STATUS)) {
			tableList = tableRepo.table_has_resv_rel_slottedTime(restGuid, reservation.getEstStartTime().getTime(), reservation.getEstEndTime().getTime(), reservation.getTableGuid());
		} else {
			tableList = tableRepo.table_has_walkin_rel_slottedTime(restGuid, reservation.getEstStartTime().getTime(), reservation.getEstEndTime().getTime(), reservation.getTableGuid());
		}
		// List<Table> tableList =
		// tableRepo.table_has_resv_rel_slottedTime(restGuid,
		// reservation.getEstStartTime().getTime(),reservation.getEstEndTime().getTime(),reservation.getTableGuid());
		if (tableList.size() > 0) {
			StringBuilder allReservedTables = new StringBuilder();

			for (Table table : tableList) {
				allReservedTables.append(table.getName() + ",");
			}

			String str = allReservedTables.substring(0, allReservedTables.length() - 1);// = allReservedTables.substring(0, allReservedTables.length() - 1);
			listOfError.add(new ValidationError(Constants.RESERVATION_TIME, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_TABLE_RESERVATION_TIME), ErrorCodes.INVALID_TABLE_RESERVATION_TIME,
					str));
		}
	}

	public void validateTableReservedSlotTimeforUpdate(String restGuid, Reservation reservation, List<ValidationError> listOfError, List<String> list_existing) {

		List<Table> uniqueGuid = new ArrayList<Table>();
		List<Table> tableList;
		if (reservation.getBookingMode().equals(Constants.ONLINE_STATUS)) {
			tableList = tableRepo.table_has_resv_rel_slottedTime(restGuid, reservation.getEstStartTime().getTime(), reservation.getEstEndTime().getTime(), reservation.getTableGuid());
		} else {
			tableList = tableRepo.table_has_walkin_rel_slottedTime(restGuid, reservation.getEstStartTime().getTime(), reservation.getEstEndTime().getTime(), reservation.getTableGuid());
		}

		uniqueGuid.addAll(tableList);
		for (String existingGuid : list_existing) {
			String converguids[] = UtilityMethods.replaceSpecialCharacter(existingGuid);
			existingGuid = converguids[0];
			for (Table tables : uniqueGuid) {
				if (existingGuid.equalsIgnoreCase(tables.getGuid())) {
					tableList.remove(tables);
					System.out.println(" Table Removed ----------" + tables.getName());
					break;
				}
			}
		}

		for (String table_guid : reservation.getTableGuid()) {
			for (Table tables : tableList) {
				if (tables.getGuid().equalsIgnoreCase(table_guid)) {
					listOfError.add(new ValidationError(Constants.RESERVATION_TIME, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_TABLE_RESERVATION_TIME) + " " + table_guid,
							ErrorCodes.INVALID_TABLE_RESERVATION_TIME));
				}
			}
		}
	}

	public Map<String, Object> validateFinderParams(Map<String, Object> params, Class type) {
		Map<String, Object> validParamMap = new HashMap<String, Object>();

		for (String finderParam : Reservation.getCustomFinderParams()) {
			if (params.containsKey(finderParam) && !params.containsKey("CLASS")) {

				/*if (finderParam.equals(Constants.RESERVED_BETWEEN))
					validParamMap.putAll(processBetweenParams(params, Constants.RESERVED_BETWEEN, Constants.RESERVED_AFTER, Constants.RESERVED_BEFORE));
				else if (finderParam.equals(Constants.CANCELLED_BETWEEN))
					validParamMap.putAll(processBetweenParams(params, Constants.CANCELLED_BETWEEN, Constants.CANCELLED_AFTER, Constants.CANCELLED_BEFORE));
				else if (finderParam.equals(Constants.EST_START_BETWEEN))
					validParamMap.putAll(processBetweenParams(params, Constants.EST_START_BETWEEN, Constants.EST_START_AFTER, Constants.EST_END_BEFORE));
				else if (finderParam.equals(Constants.EST_END_BETWEEN))
					validParamMap.putAll(processBetweenParams(params, Constants.EST_END_BETWEEN, Constants.EST_END_AFTER, Constants.EST_START_BEFORE));
				else if (finderParam.equals(Constants.ACT_START_BETWEEN))
					validParamMap.putAll(processBetweenParams(params, Constants.ACT_START_BETWEEN, Constants.ACT_START_AFTER, Constants.ACT_START_BEFORE));
				else if (finderParam.equals(Constants.ACT_END_BETWEEN))
					validParamMap.putAll(processBetweenParams(params, Constants.ACT_END_BETWEEN, Constants.ACT_END_AFTER, Constants.ACT_END_BEFORE));
				else*/ if (finderParam.equals(Constants.COVERS_LESS_THAN)) {
					//String[] dates = params.get(finderParam).toString().split(",");
					validParamMap.put(Constants.COVERS_LESS_THAN, params.get(Constants.COVERS_LESS_THAN));
					params.remove(finderParam);
					/*
					 * validParamMap.put(Constants.RESERVED_AFTER,
					 * UtilityMethods .parseDate(dates[0],
					 * Constants.TIMESTAMP_FORMAT) );
					 * validParamMap.put(Constants.RESERVED_BEFORE,
					 * UtilityMethods .parseDate(dates[1],
					 * Constants.TIMESTAMP_FORMAT) );
					 */
				} /*else if (finderParam.equals(Constants.CANCELLED_BETWEEN)) {
					String[] dates = params.get(finderParam).toString().split(",");
					params.remove(finderParam);
					validParamMap.put(Constants.CANCELLED_AFTER, UtilityMethods.parseDate(dates[0], Constants.TIMESTAMP_FORMAT));
					validParamMap.put(Constants.CANCELLED_BEFORE, UtilityMethods.parseDate(dates[1], Constants.TIMESTAMP_FORMAT));
				}  EST Param Start 
				else if (finderParam.equals(Constants.EST_START_AFTER)) {

					validParamMap.put(Constants.EST_START_AFTER, UtilityMethods.parseDate(params.get(finderParam).toString(), Constants.TIMESTAMP_FORMAT).getTime());
					params.remove(finderParam);

				}*/ else if (finderParam.equals(Constants.FREE_SEARCH)) {

					validParamMap.put(Constants.FREE_SEARCH, params.get(Constants.FREE_SEARCH).toString());
					params.remove(finderParam);

				}

				/*else if (finderParam.equals(Constants.EST_START_BEFORE)) {

					validParamMap.put(Constants.EST_START_BEFORE, UtilityMethods.parseDate(params.get(finderParam).toString(), Constants.TIMESTAMP_FORMAT).getTime());
					params.remove(finderParam);

				} else if (finderParam.equals(Constants.EST_END_AFTER)) {

					validParamMap.put(Constants.EST_END_AFTER, UtilityMethods.parseDate(params.get(finderParam).toString(), Constants.TIMESTAMP_FORMAT).getTime());
					params.remove(finderParam);

				} else if (finderParam.equals(Constants.EST_END_BEFORE)) {

					validParamMap.put(Constants.EST_END_BEFORE, UtilityMethods.parseDate(params.get(finderParam).toString(), Constants.TIMESTAMP_FORMAT).getTime());
					params.remove(finderParam);

				}/* ACT Param Start 
				else if (finderParam.equals(Constants.ACT_START_AFTER)) {

					validParamMap.put(Constants.ACT_START_AFTER, UtilityMethods.parseDate(params.get(finderParam).toString(), Constants.TIMESTAMP_FORMAT));
					params.remove(finderParam);

				} else if (finderParam.equals(Constants.ACT_START_BEFORE)) {

					validParamMap.put(Constants.ACT_START_BEFORE, UtilityMethods.parseDate(params.get(finderParam).toString(), Constants.TIMESTAMP_FORMAT));
					params.remove(finderParam);

				} else if (finderParam.equals(Constants.ACT_END_AFTER)) {

					validParamMap.put(Constants.ACT_END_AFTER, UtilityMethods.parseDate(params.get(finderParam).toString(), Constants.TIMESTAMP_FORMAT));
					params.remove(finderParam);

				} else if (finderParam.equals(Constants.ACT_END_BEFORE)) {

					validParamMap.put(Constants.ACT_END_BEFORE, UtilityMethods.parseDate(params.get(finderParam).toString(), Constants.TIMESTAMP_FORMAT).getTime());
					params.remove(finderParam);

				}/* Cancelled Params 
				else if (finderParam.equals(Constants.CANCELLED_AFTER)) {

					validParamMap.put(Constants.CANCELLED_AFTER, UtilityMethods.parseDate(params.get(finderParam).toString(), Constants.TIMESTAMP_FORMAT));
					params.remove(finderParam);

				} else if (finderParam.equals(Constants.CANCELLED_BEFORE)) {

					validParamMap.put(Constants.CANCELLED_BEFORE, UtilityMethods.parseDate(params.get(finderParam).toString(), Constants.TIMESTAMP_FORMAT).getTime());
					params.remove(finderParam);

				} else if (finderParam.equals(Constants.EST_START_BETWEEN)) {
					String[] dates = params.get(finderParam).toString().split(",");
					params.remove(finderParam);
					validParamMap.put(Constants.EST_START_AFTER, UtilityMethods.parseDate(dates[0], Constants.TIMESTAMP_FORMAT));
					validParamMap.put(Constants.EST_START_BEFORE, UtilityMethods.parseDate(dates[1], Constants.TIMESTAMP_FORMAT));
				} else if (finderParam.equals(Constants.EST_END_BETWEEN)) {
					String[] dates = params.get(finderParam).toString().split(",");
					params.remove(finderParam);
					validParamMap.put(Constants.EST_END_AFTER, UtilityMethods.parseDate(dates[0], Constants.TIMESTAMP_FORMAT));
					validParamMap.put(Constants.EST_END_BEFORE, UtilityMethods.parseDate(dates[1], Constants.TIMESTAMP_FORMAT));
				} else if (finderParam.equals(Constants.ACT_START_BETWEEN)) {
					String[] dates = params.get(finderParam).toString().split(",");
					params.remove(finderParam);
					validParamMap.put(Constants.ACT_START_AFTER, UtilityMethods.parseDate(dates[0], Constants.TIMESTAMP_FORMAT));
					validParamMap.put(Constants.ACT_START_BEFORE, UtilityMethods.parseDate(dates[1], Constants.TIMESTAMP_FORMAT));
				} else if (finderParam.equals(Constants.ACT_END_BETWEEN)) {
					String[] dates = params.get(finderParam).toString().split(",");
					params.remove(finderParam);
					validParamMap.put(Constants.ACT_END_AFTER, UtilityMethods.parseDate(dates[0], Constants.TIMESTAMP_FORMAT));
					validParamMap.put(Constants.ACT_END_BEFORE, UtilityMethods.parseDate(dates[1], Constants.TIMESTAMP_FORMAT));
				}*/ else if (finderParam.equals(Constants.COVERS_LESS_THAN)) {
					validParamMap.put(Constants.COVERS_LESS_THAN, params.get(Constants.COVERS_LESS_THAN));
					params.remove(finderParam);
				} else if (finderParam.equals(Constants.COVERS_MORE_THAN)) {
					validParamMap.put(Constants.COVERS_MORE_THAN, params.get(Constants.COVERS_MORE_THAN));

					params.remove(finderParam);
				} else {
					if (null != params.get(finderParam)) {
						validParamMap.put(finderParam, UtilityMethods.parseDate(params.get(finderParam).toString(), Constants.TIMESTAMP_FORMAT));
					}
				}

			}
		}
		Logger.debug("valid param map before super is >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + validParamMap);
		validParamMap.putAll(super.validateFinderParams(params, type));
		Logger.debug("valid param map after super is >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + validParamMap);
		return validParamMap;
	}

	private Map<String, Object> processBetweenParams(Map<String, Object> params, String betweenParam, String afterParam, String beforeParam) {
		Map<String, Object> validParamMap = new HashMap<String, Object>();
		String[] dates = params.get(betweenParam).toString().split(",");
		params.remove(betweenParam);
		validParamMap.put(afterParam, UtilityMethods.parseDate(dates[0], Constants.TIMESTAMP_FORMAT));
		validParamMap.put(beforeParam, UtilityMethods.parseDate(dates[1], Constants.TIMESTAMP_FORMAT));
		return validParamMap;

	}

	public void validateReservationHistory(Reservation reservation, List<ValidationError> listOfError) {
		// TODO Auto-generated method stub

		List<ReservationHistory> history = reservationHistoryRepo.getreservationHistoryList(reservation.getGuid());
		if (history.size() > 0) {
			Iterator<ReservationHistory> i = history.iterator();
			while (i.hasNext()) {
				ReservationHistory historyObj = i.next();
				if ((historyObj.getReservationStatus().equalsIgnoreCase("SEATED") && (reservation.getReservationStatus().equalsIgnoreCase("ARRIVED")
						|| reservation.getReservationStatus().equalsIgnoreCase("MSG_SENT") || reservation.getReservationStatus().equalsIgnoreCase("CALLED")
						|| reservation.getReservationStatus().equalsIgnoreCase("CREATED") || reservation.getReservationStatus().equalsIgnoreCase("NO_SHOW")))) {
					listOfError.add(new ValidationError(Constants.RESERVATION_STATUS, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_RESERVATION_STATUS), ErrorCodes.INVALID_RESERVATION_STATUS));
				}
				if ((historyObj.getReservationStatus().equalsIgnoreCase("ARRIVED"))
						&& (reservation.getReservationStatus().equalsIgnoreCase("MSG_SENT") || reservation.getReservationStatus().equalsIgnoreCase("CALLED")
								|| reservation.getReservationStatus().equalsIgnoreCase("NO_SHOW") || reservation.getReservationStatus().equalsIgnoreCase("CREATED") || reservation
								.getReservationStatus().equalsIgnoreCase("CONFIRMED"))) {
					listOfError.add(new ValidationError(Constants.RESERVATION_STATUS, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_RESERVATION_STATUS), ErrorCodes.INVALID_RESERVATION_STATUS));
				}

				if ((historyObj.getReservationStatus().equalsIgnoreCase("FINISHED"))
						&& (reservation.getReservationStatus().equalsIgnoreCase("MSG_SENT") || reservation.getReservationStatus().equalsIgnoreCase("CALLED")
								|| reservation.getReservationStatus().equalsIgnoreCase("NO_SHOW") || reservation.getReservationStatus().equalsIgnoreCase("CREATED") || reservation
								.getReservationStatus().equalsIgnoreCase("CONFIRMED"))) {
					listOfError.add(new ValidationError(Constants.RESERVATION_STATUS, UtilityMethods.getErrorMsg(ErrorCodes.NOT_ALLOW_RESERVATION_STATUS), ErrorCodes.NOT_ALLOW_RESERVATION_STATUS));
				}
			}
		}

	}

	public List<ValidationError> validateReservationTime(Reservation reservation, List<ValidationError> listOfError, String mode) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();

		if (mode.equalsIgnoreCase(Constants.CREATED)) {

			/*
			 * Allow Reservation to update within past 15 mins EST_START_TIME
			 * Always Require
			 */

			if (null == reservation.getEstStartTime() && listOfError.size() == 0) {
				listOfError.add(new ValidationError(Constants.EST_START_TIME, Messages.get(ErrorCodes.REQUIRED, Constants.EST_START_TIME)));
			} else if (null != reservation.getEstStartTime() && listOfError.size() == 0) {
				cal.add(Calendar.MINUTE, -15);
				if (reservation.getEstStartTime().getTime() < cal.getTimeInMillis())
					listOfError.add(new ValidationError(Constants.EST_START_TIME, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_RESERVATION_EST_TIME), ErrorCodes.INVALID_RESERVATION_EST_TIME));
			}

		}

		if (null != reservation.getActEndTime() && null != reservation.getActStartTime() && reservation.getActEndTime().getTime() < reservation.getActStartTime().getTime()) {
			listOfError.add(new ValidationError(Constants.RESERVATION_TIME, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_RESERVATION_ACT_TIME), ErrorCodes.INVALID_RESERVATION_ACT_TIME));
		}
		return listOfError;
	}

	public List<ValidationError> validateReservationTimeForUpdate(Reservation reservation, List<ValidationError> listOfError, boolean isValidationESTTimeRequired) {

		if (reservation.getReservationStatus().equalsIgnoreCase(Constants.SEATED)) {

			if (null == reservation.getActStartTime()) {
				listOfError
						.add(new ValidationError(Constants.ACT_START_TIME, UtilityMethods.getErrorMsg(ErrorCodes.MISSING_RESERVATION_ACT_START_TIME), ErrorCodes.MISSING_RESERVATION_ACT_START_TIME));
			}

		}
		if (reservation.getReservationStatus().equalsIgnoreCase(Constants.FINISHED)) {

			if (null == reservation.getActEndTime()) {
				listOfError.add(new ValidationError(Constants.ACT_END_TIME, UtilityMethods.getErrorMsg(ErrorCodes.MISSING_RESERVATION_ACT_END_TIME), ErrorCodes.MISSING_RESERVATION_ACT_END_TIME));
			}

		}

		return listOfError;
	}

	/**
	 * private method which validates date related filters at the time of get
	 * 
	 * @param validParamMap
	 * @param fieldName
	 * @param dateStr
	 * @return validParamMap
	 */
	protected Map<String, Object> validateDateTime(Map<String, Object> validParamMap, String fieldName, String dateStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		if (UtilityMethods.isValidDate(dateStr, dateFormat)) {
			try {
				validParamMap.put(fieldName, dateFormat.parse(dateStr).getTime());
			} catch (ParseException pe) {
				// TODO handle it
				pe.printStackTrace();
			}
		}

		return validParamMap;
	}

	public List<ValidationError> validateReservationStatus(Reservation reservation, List<ValidationError> listOfError) {
		// TODO Auto-generated method stub
		if (null == reservation.getReservationStatus()) {
			listOfError.add(new ValidationError(Constants.RESERVATION_STATUS, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_RESERVATION_STATUS), ErrorCodes.INVALID_RESERVATION_STATUS));

		}
		return listOfError;
	}

	public List<ValidationError> validateOperationalHour(Reservation reservation, List<ValidationError> listOfError) {
		String dayNames[] = new DateFormatSymbols().getWeekdays();
		SimpleDateFormat formatDate = new SimpleDateFormat(Constants.DATE_FORMAT);
		SimpleDateFormat formatTime = new SimpleDateFormat(Constants.TIME_FORMAT);
		Map<String, Object> params = new HashMap<String, Object>();
		Logger.debug("--------Reservation Start Date------" + formatDate.format(new Date(reservation.getEstStartTime().getTime())));
		//long reservation_start_day = formatDate.parse(formatDate.format(new Date(reservation.getEstStartTime().getTime()))).getTime();
		Logger.debug("--------Reservation End Date------" + formatDate.format(new Date(reservation.getEstStartTime().getTime())));
		//long reservation_end_day = formatDate.parse(formatDate.format(new Date(reservation.getEstEndTime().getTime()))).getTime();
		Logger.debug("--------Reservation Start Time------" + formatTime.format(new Date(reservation.getEstStartTime().getTime())));
		/*
		 * long
		 * reservation_start_time=formatTime.parse(formatTime.format(new
		 * Date(reservation.getEstStartTime().getTime()))).getTime();
		 * Logger.
		 * debug("--------Reservation End Time------"+formatTime.format(new
		 * Date(reservation.getEstEndTime().getTime())));
		 */

		// long reservation_end_time=formatTime.parse(formatTime.format(new
		// Date(reservation.getEstEndTime().getTime()))).getTime();
		String reservation_Start_time = formatTime.format(new Date(reservation.getEstStartTime().getTime()));
		String[] splittedTime = reservation_Start_time.split(":");
		String timeHr = splittedTime[0];
		String timeMin = splittedTime[1];
		String timesec = splittedTime[2];

		long reservation_start_time = Long.parseLong(timeHr) * 60 * 60 * 1000 + Long.parseLong(timeMin) * 60 * 1000 + Long.parseLong(timesec) * 1000;
		long reservation_end_time = reservation_start_time + Long.parseLong(reservation.getTat()) * 60 * 1000;

		/* Validating Operational Hours */
		Calendar calc = Calendar.getInstance();
		calc.setTimeInMillis(reservation.getEstStartTime().getTime());
		params.put(Constants.REST_GUID, reservation.getRestaurantGuid());
		int day_of_week = calc.get(Calendar.DAY_OF_WEEK);
		int last_day_of_week = day_of_week == Calendar.SUNDAY ? Calendar.SATURDAY : day_of_week - 1;
		params.put(Constants.DAY_NAME, (dayNames[last_day_of_week].toUpperCase() + "," + dayNames[day_of_week].toUpperCase()));
		OperationalHours ophr = restaurantDao.getOperationalHours(params);
		boolean isReservationPossible = false;

		if (null != ophr.getMonday() && dayNames[day_of_week].toUpperCase().equals(Constants.MONDAY)) {
			List<Shift> actualDay = ophr.getMonday();
			List<Shift> previousDay = ophr.getSunday();
			actualDay = addingShiftToPrevious(actualDay, previousDay);
			isReservationPossible = true;
			listOfError = validateOperationShiftTime(actualDay, reservation_start_time, reservation_end_time, listOfError);
		}
		if (null != ophr.getTuesday() && dayNames[day_of_week].toUpperCase().equals(Constants.TUESDAY)) {
			List<Shift> actualDay = ophr.getTuesday();
			List<Shift> previousDay = ophr.getMonday();
			actualDay = addingShiftToPrevious(actualDay, previousDay);
			isReservationPossible = true;
			listOfError = validateOperationShiftTime(actualDay, reservation_start_time, reservation_end_time, listOfError);

		}
		if (null != ophr.getWednesday() && dayNames[day_of_week].toUpperCase().equals(Constants.WEDNESDAY)) {
			List<Shift> actualDay = ophr.getWednesday();
			List<Shift> previousDay = ophr.getTuesday();
			actualDay = addingShiftToPrevious(actualDay, previousDay);
			isReservationPossible = true;
			listOfError = validateOperationShiftTime(actualDay, reservation_start_time, reservation_end_time, listOfError);

		}
		if (null != ophr.getThursday() && dayNames[day_of_week].toUpperCase().equals(Constants.THURSDAY)) {
			List<Shift> actualDay = ophr.getThursday();
			List<Shift> previousDay = ophr.getWednesday();
			actualDay = addingShiftToPrevious(actualDay, previousDay);
			isReservationPossible = true;
			listOfError = validateOperationShiftTime(actualDay, reservation_start_time, reservation_end_time, listOfError);
		}
		if (null != ophr.getFriday() && dayNames[day_of_week].toUpperCase().equals(Constants.FRIDAY)) {
			List<Shift> actualDay = ophr.getFriday();
			List<Shift> previousDay = ophr.getThursday();
			actualDay = addingShiftToPrevious(actualDay, previousDay);
			isReservationPossible = true;
			listOfError = validateOperationShiftTime(actualDay, reservation_start_time, reservation_end_time, listOfError);
		}
		if (null != ophr.getSaturday() && dayNames[day_of_week].toUpperCase().equals(Constants.SATURDAY)) {
			List<Shift> actualDay = ophr.getSaturday();
			List<Shift> previousDay = ophr.getFriday();
			actualDay = addingShiftToPrevious(actualDay, previousDay);
			isReservationPossible = true;
			listOfError = validateOperationShiftTime(actualDay, reservation_start_time, reservation_end_time, listOfError);
		}
		if (null != ophr.getSunday() && dayNames[day_of_week].toUpperCase().equals(Constants.SUNDAY)) {
			List<Shift> actualDay = ophr.getSunday();
			List<Shift> previousDay = ophr.getSaturday();
			actualDay = addingShiftToPrevious(actualDay, previousDay);
			isReservationPossible = true;
			listOfError = validateOperationShiftTime(actualDay, reservation_start_time, reservation_end_time, listOfError);
		}

		/* Restaurant Holiday Validation */
		if (!isReservationPossible) {
			listOfError.add(new ValidationError(Constants.OPERATIONAL_HOURS, UtilityMethods.getErrorMsg(ErrorCodes.MISSING_OPERATIONAL_HOURS), ErrorCodes.MISSING_OPERATIONAL_HOURS));
			return listOfError;
		} else if (isReservationPossible && !listOfError.isEmpty()) {

			return listOfError;
		} else {/*
				 * //TODO In future - Phase 2 com.clicktable.model.Event
				 * holidayEvent
				 * =calEventRepo.findHolidayDay(reservation.getRestaurantGuid
				 * (),reservation_start_day,
				 * reservation_end_day,reservation_start_time
				 * ,reservation_end_time); if(null!=holidayEvent){
				 * listOfError .add(new ValidationError(
				 * Constants.RESERVATION_TIME, UtilityMethods
				 * .getErrorMsg(ErrorCodes
				 * .HOLIDAY_HOURS),ErrorCodes.HOLIDAY_HOURS)); return
				 * listOfError; }
				 */
		}

		return listOfError;
	}

	private List<Shift> addingShiftToPrevious(List<Shift> actualDay, List<Shift> previousday) {
		// TODO Auto-generated method stub
		long biggest = 0;
		for (Shift shift : previousday) {
			if (shift.getEndTimeInMillis() > biggest)
				biggest = shift.getEndTimeInMillis();
		}
		Shift prevShift = null;
		if (biggest > 24 * 3600 * 1000) {
			prevShift = new Shift();
			prevShift.setStartTimeInMillis(0);
			prevShift.setEndTimeInMillis(biggest - 24 * 3600 * 1000);
			actualDay.add(prevShift);
		}
		return actualDay;
	}

	private List<ValidationError> validateOperationShiftTime(List<Shift> shift, long reservation_start_time, long reservation_end_time, List<ValidationError> listOfError) {
		boolean isReservationPossible = false;
		if (null != shift && shift.size() > 0) {
			for (int i = 0; i < shift.size(); i++) {
				if ((shift.get(i).getStartTimeInMillis() <= reservation_start_time && shift.get(i).getStartTimeInMillis() <= reservation_end_time)
						&& (shift.get(i).getEndTimeInMillis() >= reservation_start_time && shift.get(i).getEndTimeInMillis() >= reservation_end_time)) {
					isReservationPossible = true;
					break;
				}
			}
		} else {
			listOfError.add(new ValidationError(Constants.OPERATIONAL_HOURS, UtilityMethods.getErrorMsg(ErrorCodes.MISSING_OPERATIONAL_HOURS), ErrorCodes.MISSING_OPERATIONAL_HOURS));
		}

		if (!isReservationPossible) {
			listOfError.add(new ValidationError(Constants.OPERATIONAL_HOURS, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_OPERATIONAL_HOURS), ErrorCodes.INVALID_OPERATIONAL_HOURS));
		}

		return listOfError;

	}

	public Map<String, Object> validateGetStatsParams(Map<String, Object> params, List<ValidationError> errorList) {

		// Map<String, Object> validParamMap = new HashMap<String, Object>();
		String calenderType;
		DateTime startDate = null;
		DateTime endDate;

		if (params.containsKey(Constants.CALENDER_TYPE)) {
			calenderType = (String) params.get(Constants.CALENDER_TYPE);
			List<String> values = UtilityMethods.getEnumValues(Constants.RESERVATION_MODULE, Constants.CALENDER_TYPE);
			if (calenderType != null && !values.contains(calenderType))
				errorList.add(new ValidationError(Constants.CALENDER_TYPE, Messages.get(ErrorCodes.INVALID_VALUE, calenderType, values), ErrorCodes.INVALID_VALUE));
			else {
				// validParamMap.put(Constants.CALENDER_TYPE, calenderType);
				if (params.containsKey(Constants.START_DATE)) {
					String dateStr = (String) params.get(Constants.START_DATE);
					startDate = getValidDate(dateStr, errorList);
					if (errorList.isEmpty()) {
						params.put(Constants.EST_END_AFTER, startDate);
						params.put(Constants.EST_START_AFTER, startDate);
						if (params.containsKey(Constants.END_DATE)) {
							String enddateStr = (String) params.get(Constants.END_DATE);
							endDate = getValidDate(enddateStr, errorList);
						} else {
							endDate = getEndDate(startDate, calenderType);
						}
					/*	System.out.println(endDate.getMillis()+"<<EST_START_BEFORE");
						System.out.println(startDate.getMillis()+"<<EST_END_AFTER");*/
						params.put(Constants.EST_START_BEFORE, endDate);
					}
				} else
					errorList.add(new ValidationError(Constants.START_DATE, Messages.get(ErrorCodes.REQUIRED, Constants.START_DATE), ErrorCodes.REQUIRED));
			}
		} else {
			errorList.add(new ValidationError(Constants.CALENDER_TYPE, Messages.get(ErrorCodes.REQUIRED, Constants.CALENDER_TYPE), ErrorCodes.REQUIRED));
		}
		return params;
	}

	private DateTime getValidDate(String dateStr, List<ValidationError> errorList) {
		DateTime date = null;
		try {
			date = DateTime.parse(dateStr);
		} catch (Exception e) {
			Logger.error(e.getMessage());
			errorList.add(new ValidationError(Constants.START_DATE, e.getMessage(), ErrorCodes.INVALID_DATE_FORMAT));
		}
		return date;
	}
	
	private DateTime getValidDate(String dateStr,String fieldName, List<ValidationError> errorList) {
		DateTime date = null;
		try {
			date = DateTime.parse(dateStr);
		} catch (Exception e) {
			errorList.add(new ValidationError(fieldName, e.getMessage(), ErrorCodes.INVALID_DATE_FORMAT));
		}
		return date;
	}
	

	private DateTime getEndDate(DateTime startDate, String calenderType) {
		DateTime end = null;
		switch (calenderType) {
		case Constants.DAYS:
			if (startDate.toLocalDate().equals(new LocalDate()))
				end = new DateTime();
			else
				end = startDate.plusDays(1).minusMinutes(1);
			break;
		case Constants.WEEK:
			if (startDate.isAfter(new DateTime().minusWeeks(1)))
				end = new DateTime();
			else
				end = startDate.plusWeeks(1).minusMinutes(1);
			break;
		case Constants.MONTH:
			if (startDate.isAfter(new DateTime().minusMonths(1)))
				end = new DateTime();
			else
				end = startDate.plusMonths(1).minusMinutes(1);
			break;
		case Constants.YEAR:
			if (startDate.isAfter(new DateTime().minusYears(1)))
				end = new DateTime();
			else
				end = startDate.plusYears(1).minusMinutes(1);
			break;

		}
		return end;
	}

	private DateTime getNextDate(DateTime startDate) {
		return startDate.plusDays(1).minusMinutes(1);
	}

	public Map<String, Object> showAlldayReservation(Map<String, Object> params) {

		String dayNames[] = new DateFormatSymbols().getWeekdays();
		Calendar calc = Calendar.getInstance();
		int day_of_week = calc.get(Calendar.DAY_OF_WEEK);
		int last_day_of_week = day_of_week == Calendar.SUNDAY ? Calendar.SATURDAY : day_of_week - 1;
		params.put(Constants.DAY_NAME, (dayNames[last_day_of_week].toUpperCase() + "," + dayNames[day_of_week].toUpperCase()));

		OperationalHours ophr = restaurantDao.getOperationalHours(params);
		params.remove(Constants.DAY_NAME);

		if (null != ophr.getMonday() && dayNames[day_of_week].toUpperCase().equals(Constants.MONDAY)) {
			List<Shift> actualDay = ophr.getMonday();
			List<Shift> previousDay = ophr.getSunday();
			return shiftValue(params, actualDay, previousDay);

		}
		if (null != ophr.getTuesday() && dayNames[day_of_week].toUpperCase().equals(Constants.TUESDAY)) {
			List<Shift> actualDay = ophr.getTuesday();
			List<Shift> previousDay = ophr.getMonday();
			return shiftValue(params, actualDay, previousDay);
		}
		if (null != ophr.getWednesday() && dayNames[day_of_week].toUpperCase().equals(Constants.WEDNESDAY)) {
			List<Shift> actualDay = ophr.getWednesday();
			List<Shift> previousDay = ophr.getTuesday();
			return shiftValue(params, actualDay, previousDay);
		}
		if (null != ophr.getThursday() && dayNames[day_of_week].toUpperCase().equals(Constants.THURSDAY)) {
			List<Shift> actualDay = ophr.getThursday();
			List<Shift> previousDay = ophr.getWednesday();
			return shiftValue(params, actualDay, previousDay);
		}
		if (null != ophr.getFriday() && dayNames[day_of_week].toUpperCase().equals(Constants.FRIDAY)) {
			List<Shift> actualDay = ophr.getFriday();
			List<Shift> previousDay = ophr.getThursday();
			return shiftValue(params, actualDay, previousDay);
		}
		if (null != ophr.getSaturday() && dayNames[day_of_week].toUpperCase().equals(Constants.SATURDAY)) {
			List<Shift> actualDay = ophr.getSaturday();
			List<Shift> previousDay = ophr.getFriday();
			return shiftValue(params, actualDay, previousDay);
		}
		if (null != ophr.getSunday() && dayNames[day_of_week].toUpperCase().equals(Constants.SUNDAY)) {
			List<Shift> actualDay = ophr.getSunday();
			List<Shift> previousDay = ophr.getSaturday();
			return shiftValue(params, actualDay, previousDay);
		}

		return params;
	}

	private Map<String, Object> shiftValue(Map<String, Object> params, List<Shift> actualDay, List<Shift> previousDay) {
		Calendar calc = Calendar.getInstance();
		String estStartAfter, estStartBefore;

		SimpleDateFormat formatDate = new SimpleDateFormat(Constants.DATE_FORMAT);
		SimpleDateFormat formatTime = new SimpleDateFormat(Constants.TIME_FORMAT);

		//String currentDate = formatDate.format(new Date(calc.getTimeInMillis()));
		String currentTime = formatTime.format(new Date(calc.getTimeInMillis()));

		String time[] = currentTime.split(":");
		//long longTime = Integer.parseInt(time[0]) * 3600 * 1000 + Integer.parseInt(time[1]) * 60 * 1000 + Integer.parseInt(time[2]) * 1000;
		long min_actual_shiftValue = 0;
		long max_actual_ShiftValue = 0;
		long min_previous_shiftValue = 0;
		long max_previous_ShiftValue = 0;
		String max_Shift = "";
		String max_previous_Shift = "";
		int count = 0;

		Long currentShiftStart = 0L;
		Long currentShiftEnd = 0L;

		/* Actual Day Minimum and Maximum shift values */
		for (Shift actualShift : actualDay) {
			if (count == 0) {
				min_actual_shiftValue = actualShift.getStartTimeInMillis();
				count++;
			}
			if (min_actual_shiftValue > actualShift.getStartTimeInMillis())
				min_actual_shiftValue = actualShift.getStartTimeInMillis();

			if (actualShift.getEndTimeInMillis() > max_actual_ShiftValue) {
				max_actual_ShiftValue = actualShift.getEndTimeInMillis();
				max_Shift = actualShift.getEndTime();
			}
		}

		Long currentDateStart = 0L;
		try {
			currentDateStart = formatDate.parse(formatDate.format(new Date())).getTime();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		currentShiftStart = currentDateStart + min_actual_shiftValue;
		currentShiftEnd = currentDateStart + max_actual_ShiftValue;

		if (new Date().getTime() < currentShiftStart) {
			/* Previous Day Minimum and Maximum shift values */
			count = 0;
			for (Shift previousShift : previousDay) {
				if (count == 0) {
					min_previous_shiftValue = previousShift.getStartTimeInMillis();
					count++;
				}
				if (min_previous_shiftValue > previousShift.getStartTimeInMillis())
					min_previous_shiftValue = previousShift.getStartTimeInMillis();

				if (previousShift.getEndTimeInMillis() > max_previous_ShiftValue) {
					max_previous_ShiftValue = previousShift.getEndTimeInMillis();
					max_previous_Shift = previousShift.getEndTime();
				}

			}

			currentDateStart = currentDateStart - 24 * 60 * 60 * 1000;
			currentShiftStart = currentDateStart + min_previous_shiftValue;
			currentShiftEnd = currentDateStart + max_previous_ShiftValue;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		currentShiftStart = currentShiftStart - 1 * 60 * 1000;
		currentShiftEnd = currentShiftEnd + 1 * 60 * 1000;

		params.put(Constants.EST_START_AFTER, sdf.format(new Date(currentShiftStart)));
		params.put(Constants.EST_END_BEFORE, sdf.format(new Date(currentShiftEnd)));

		/*
		 * String timeArray[]=max_Shift.split(":"); String
		 * timeArrayPreviousMax[]=max_previous_Shift.split(":");
		 * 
		 * if (max_actual_ShiftValue > 24 * 3600 * 1000) { max_Shift = "" +
		 * (Integer.parseInt(timeArray[0]) - 24) + ":" + timeArray[1]; timeArray
		 * = max_Shift.split(":"); } else { max_Shift = "00:00:01"; timeArray =
		 * max_Shift.split(":"); } if (max_previous_ShiftValue > 24 * 3600 *
		 * 1000) { max_previous_Shift = "" +
		 * (Integer.parseInt(timeArrayPreviousMax[0]) - 24) + ":" +
		 * timeArray[1]; timeArrayPreviousMax = max_previous_Shift.split(":"); }
		 * else { max_previous_Shift = "" + "00:00:01"; timeArrayPreviousMax =
		 * max_previous_Shift.split(":"); }
		 * 
		 * try { if (max_actual_ShiftValue>24*3600*1000 && longTime <
		 * min_actual_shiftValue) {
		 * 
		 * estStartAfter = formatDate.format(formatDate.parse(currentDate)
		 * .getTime() - 24 * 60 * 60 * 100) +
		 * " "+timeArrayPreviousMax[0]+":"+timeArrayPreviousMax[1]+":"+"00";
		 * params.put(Constants.EST_START_AFTER,estStartAfter); estStartBefore =
		 * currentDate + " "+timeArray[0]+":"+timeArray[1]+":"+"00";
		 * params.put(Constants.EST_START_BEFORE,estStartBefore); } else {
		 * estStartAfter = formatDate.format(formatDate.parse(currentDate)
		 * .getTime()) +
		 * " "+timeArrayPreviousMax[0]+":"+timeArrayPreviousMax[1]+":"+"00";
		 * params.put(Constants.EST_START_AFTER,estStartAfter);
		 * 
		 * estStartBefore = formatDate.format(formatDate
		 * .parse(currentDate).getTime() + 24 * 60 * 60 * 1000) +
		 * " "+timeArray[0]+":"+timeArray[1]+":"+"00";
		 * 
		 * params.put(Constants.EST_START_BEFORE,estStartBefore);
		 * 
		 * } } catch (ParseException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		return params;

	}

	public Map<String, Object> validateRestGuestTable(Reservation reservation, List<ValidationError> listOfError) {

		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> mapResult = new HashMap<String, Object>();

		SimpleDateFormat format = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		params.put(Constants.REST_ID, reservation.getRestaurantGuid());

		/* Validating Guest */
		if (null != reservation.getGuestGuid()) {
			params.put(Constants.GUEST_ID, reservation.getGuestGuid());
		} else if (null == reservation.getGuestGuid() || "".equalsIgnoreCase(reservation.getGuestGuid())) {
			listOfError.add(new ValidationError(Constants.GUEST_ID, UtilityMethods.getErrorMsg(ErrorCodes.CUST_ID_REQUIRED), ErrorCodes.CUST_ID_REQUIRED));
/*			mapResult.put("listOfError", listOfError);*/
			return mapResult;
		}

		/* Validating Table Guid */
		if (null != reservation.getTableGuid()) {
			params.put(Constants.TABLE_GUID, reservation.getTableGuid());
		} else if (null == reservation.getTableGuid() || reservation.getTableGuid().size() == 0) {
			listOfError.add(new ValidationError(Constants.TABLE_ID, UtilityMethods.getErrorMsg(ErrorCodes.TABLE_ID_REQUIRED), ErrorCodes.TABLE_ID_REQUIRED));
/*			mapResult.put("listOfError", listOfError);*/
			return mapResult;
		}

		/* Validating num_covers */
		if (reservation.getNumCovers() <= 0) {
			listOfError.add(new ValidationError(Constants.COVERS, UtilityMethods.getErrorMsg(ErrorCodes.COVERS_SHOULD_BE_GREATER_THAN_ZERO), ErrorCodes.COVERS_SHOULD_BE_GREATER_THAN_ZERO));
/*			mapResult.put("listOfError", listOfError);*/
			return mapResult;
		}

		mapResult = reservationDao.validateRestGuestTable(params);

		if (null != mapResult) {
			mapResult.put("listOfError", listOfError);		
			if (null != mapResult.get(Constants.REST_ID) && (Integer) mapResult.get(Constants.REST_ID) == 0) {
				listOfError.add(new ValidationError(Constants.REST_ID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_REST_ID), ErrorCodes.INVALID_REST_ID));
/*				mapResult.put("listOfError", listOfError);*/
				return mapResult;
			} else if (null != mapResult.get(Constants.GUEST_ID) && (Integer) mapResult.get(Constants.GUEST_ID) == 0) {
				listOfError.add(new ValidationError(Constants.GUEST_ID, UtilityMethods.getErrorMsg(ErrorCodes.CUST_ID_REQUIRED), ErrorCodes.CUST_ID_REQUIRED));
				/*mapResult.put("listOfError", listOfError);*/
				return mapResult;
			} else if (null != mapResult.get(Constants.TABLE_GUID) && (Integer) mapResult.get(Constants.TABLE_GUID) == 0) {
				listOfError.add(new ValidationError(Constants.TABLE_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_TABLE_ID), ErrorCodes.INVALID_TABLE_ID));
				/*mapResult.put("listOfError", listOfError);*/
				return mapResult;
			}

		}

		if (null != mapResult.get(Constants.COVERS) && Integer.parseInt(mapResult.get(Constants.COVERS).toString()) < reservation.getNumCovers()) {
			listOfError.add(new ValidationError(Constants.COVERS, UtilityMethods.getErrorMsg(ErrorCodes.MIN_CAPACITY_MORE_THAN_MAX_CAPACITY) + " " + reservation.getNumCovers()
					+ " Total Max Covers for booked table :" + mapResult.get(Constants.COVERS).toString(), ErrorCodes.MIN_CAPACITY_MORE_THAN_MAX_CAPACITY));
/*			mapResult.put("listOfError", listOfError);*/
			return mapResult;
		}

		if (listOfError.isEmpty()) {
			/* Validating Reservation EST_START_TIME and EST_END_TIME */
			
			//TODO change this method
			
			validateReservationTime(reservation, listOfError, reservation.getReservationStatus());
			if (listOfError.isEmpty()) {
				/* Validating Reservation Status */
				
				//TODO remove method and build this logic in parent method
				
				
				validateReservationHistory(reservation, listOfError);
			} else {
/*				mapResult.put("listOfError", listOfError);*/
				return mapResult;
			}
		}
		/*
		 * Change the Reservation Time with Format : Defect not able to search
		 * Reservation with ReservationTime param
		 */
		
		// TODO : Need to remove . Handle in reservation model
		
		if (null != reservation.getReservationTime()) {
			try {
				reservation.setReservationTime(format.parse(format.format(reservation.getReservationTime())));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return mapResult;
	}

	public Map<String, Object> validateParamsForReport(Map<String, Object> paramMap, List<ValidationError> errorList, UserInfoModel userInfo) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (paramMap.containsKey(Constants.START_DATE)) {
			if (paramMap.containsKey(Constants.END_DATE)) {
				DateTime startDate = getValidDate(paramMap.get(Constants.START_DATE).toString(),Constants.START_DATE,errorList);
				DateTime endDate = getValidDate(paramMap.get(Constants.END_DATE).toString(),Constants.END_DATE, errorList);
				if(errorList.isEmpty()){
					endDate.plusDays(1);
					if(endDate.isBefore(startDate)){
							errorList.add(new ValidationError(Constants.END_DATE, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_END_DATE), ErrorCodes.INVALID_END_DATE));
					}
					params.put(Constants.EST_START_AFTER, startDate.getMillis());
					params.put(Constants.EST_START_BEFORE, endDate.getMillis());
				}
			}else {
					errorList.add(new ValidationError(Constants.END_DATE, Messages.get(ErrorCodes.REQUIRED, Constants.END_DATE), ErrorCodes.REQUIRED));
			}
		} else {
			errorList.add(new ValidationError(Constants.START_DATE, Messages.get(ErrorCodes.REQUIRED, Constants.START_DATE), ErrorCodes.REQUIRED));
		}
		if(paramMap.containsKey(Constants.FILE_FORMAT)){
			if ((!UtilityMethods.getEnumValues(Constants.REPORTS, Constants.FILE_FORMAT).contains(paramMap.get(Constants.FILE_FORMAT)))) {
				errorList.add(new ValidationError(Constants.FILE_FORMAT, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_FILE_FORMAT), ErrorCodes.INVALID_FILE_FORMAT));
			}else{
				params.put(Constants.FILE_FORMAT, paramMap.get(Constants.FILE_FORMAT));
			}
			
		}
		if (userInfo.getRoleId() != 1l) {
			params.put(Constants.REST_GUID, userInfo.getRestGuid());
		} else if (paramMap.containsKey(Constants.REST_GUID)) {
			params.put(Constants.REST_GUID, paramMap.get(Constants.REST_GUID));
		} else {
			errorList.add(new ValidationError(Constants.REST_GUID, Messages.get(ErrorCodes.REQUIRED, Constants.REST_GUID), ErrorCodes.REQUIRED));
		}
		return params;
	}

	
	
	/*New Validations for reservations slot*/
	
	public Boolean validateReservationTimeSlot(Reservation resv,List<ValidationError> errorList)
	{
		Boolean isValid = true;
		//Long bufferTime = 60*60*1000L;
		Long overlapTime = 0*60*1000l; 
		String restaurantId = resv.getRestaurantGuid();
		
		
		List<Object> allTablesMap = waitlistDao.getAllTables(restaurantId);
		Map<String,Table> allTablesWithGuid = (Map<String, Table>) allTablesMap.get(1);
		
		/*Get shift data*/
		Map<String, Object> shiftTime = new HashMap<>();
		shiftTime = waitlistService.getApplicableShifts( resv.getRestaurantGuid(), errorList, resv.getEstStartTime(),false);
		if(!errorList.isEmpty()){
			return false;
		}
		
		Long currentDateTime = 0l,  startTime = 0l ;
		startTime = (Long) shiftTime.get("startTime");
		currentDateTime = (Long) shiftTime.get("currentDateTime");
		Long currentDayShiftEndTime = (Long) shiftTime.get("currentDayShiftEndTime");
		List<Shift> shiftList = new ArrayList<>();
		shiftList = (List<Shift>) shiftTime.get("shiftList");
		Long tat = Long.parseLong(resv.getTat());
		Long endTime = (startTime + (tat*60*1000) - overlapTime);
		resv.setEstEndTime(new Date(endTime));
		
		/*check if the reservation lies in operation hours*/
		if(resv.getBookingMode().equals(Constants.ONLINE_STATUS)){
			Boolean isOutOfOpHr = true;
			for(Shift shift : shiftList){
				if(startTime >= shift.getStartTimeInMillis() && endTime <= shift.getEndTimeInMillis2()){
					isOutOfOpHr = false;
					break;
				}
			}
			
			if(isOutOfOpHr){
				errorList.add(new ValidationError(Constants.OPERATIONAL_HOURS, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_OPERATIONAL_HOURS), ErrorCodes.INVALID_OPERATIONAL_HOURS));
				return false;
			}
		}
		
		/* get all blocked tables*/
		Map<String,Object> blockResvParam = new HashMap<>();
		blockResvParam.put(Constants.REST_GUID, restaurantId);
		blockResvParam.put("currentDateTime", currentDateTime);
		blockResvParam.put("nextDateTime", (currentDateTime + 24*60*60*1000));
		blockResvParam.put("currentShiftEndTime", (currentDayShiftEndTime + 60*60*1000L));
		blockResvParam.put("currentTime", startTime);

		Map<String, List<Reservation>> blockListMap = new HashMap<String, List<Reservation>>();
		blockListMap = shuffleDao.getBlockedTables(blockResvParam, blockListMap);
		blockResvParam = null;

		/* get all the reservations*/
		Map<String, List<Reservation>> reservationListMap = new HashMap<String, List<Reservation>>();
		Map<String,Object> resvParam = new HashMap<>();	
		resvParam.put(Constants.START_TIME, startTime);
		resvParam.put("currentShiftEnd", (currentDayShiftEndTime + 60*60*1000L));
		resvParam.put("restaurantId", restaurantId);
		List<Reservation> allReservationList = shuffleDao.getTablesHavingReservation(resvParam);
		resvParam = null;
		
		//remove the reservation that needs to be modified from blockListMap
		for(Reservation r : allReservationList){
			if(r.getGuid().equals(resv.getGuid())){
				allReservationList.remove(r);
				break;
			}
		}

		shuffleService.addAllResvToBlockMap(allReservationList, reservationListMap);

		/* get all the Walkin*/
		Map<String,Object> queueMap = new HashMap<>();
		queueMap.put(Constants.REST_ID, restaurantId);		
		List<Reservation> queuedResv = queueDao.getQueuedReservation(queueMap);

		shuffleService.addAllResvToBlockMap(queuedResv, reservationListMap);

/*		if(null == allTablesWithGuid){
			Logger.error("Parameters in allocate tabe to resv are------  resv.tableguid===" + resv.getTableGuid() );
			Logger.error("allTablesWithGuid parameter not initialized ");
			Logger.error("allTablesWithGuid is =================================================" + allTablesWithGuid);
			//shuffleService.getAllTables(resv.getRestaurantGuid(),errorList);
			
		}*/
		
	

		for(String tableGuid : resv.getTableGuid()){
			
			if(!allTablesWithGuid.containsKey(tableGuid))
			{
				errorList.add(new ValidationError(Constants.TABLE_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_TABLE_ID), ErrorCodes.INVALID_TABLE_ID,
						tableGuid));
			}
			Boolean checkForBlockedResv = shuffleService.checkForBlockedReservation(resv, reservationListMap, tableGuid);
			if(checkForBlockedResv){
				isValid = false;
				errorList.add(new ValidationError(Constants.TABLE_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_TABLE_RESERVATION_TIME), ErrorCodes.INVALID_TABLE_RESERVATION_TIME,
						tableGuid));
				return isValid;
			}else{
				checkForBlockedResv = shuffleService.checkForBlockedReservation(resv, blockListMap,tableGuid);
				if(checkForBlockedResv && resv.getBookingMode().equals(Constants.WALKIN_STATUS)){
					isValid = false;
					errorList.add(new ValidationError(Constants.TABLE_GUID, UtilityMethods.getErrorMsg(ErrorCodes.BLOCK_TABLE), ErrorCodes.BLOCK_TABLE,
							tableGuid));
					return isValid;
				}
			}
		}

		return isValid;
	}	
	
	@Override
	public String getMissingGuidErrorCode() {
		return ErrorCodes.RESERVATION_ID_REQUIRED;
	}

	@Override
	public String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_RESERVATION_ID;
	}
	
	@Override
	public List<ValidationError> validateOfferId(Reservation reservation,List<ValidationError> errorList)
	{
		Map<String,Object> eventParams = new HashMap<>();
		eventParams.put(Constants.GUID, reservation.getOfferId());
		eventParams.put(Constants.REST_GUID, reservation.getRestaurantGuid());
		List<Event> eventList = eventDao.findByFields(Event.class, eventParams);
		if(eventList != null && eventList.size() == 0)
		{
			errorList = CustomValidations.populateErrorList(errorList, Constants.OFFER_ID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_OFFER_ID),
					ErrorCodes.INVALID_OFFER_ID);
		}
		else
		{
		reservation.setOfferName(eventList.get(0).getName());
		}
		
		return errorList;
	}
	
}
