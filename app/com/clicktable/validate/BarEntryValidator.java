package com.clicktable.validate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.i18n.Messages;

import com.clicktable.dao.intf.BarEntryDao;
import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.model.BarEntry;
import com.clicktable.model.Reservation;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;


@Service
public class BarEntryValidator extends EntityValidator<BarEntry> {

	@Autowired
	BarEntryDao barEntryDao;

	@Autowired
	CustomerDao customerDao;

	@Autowired
	ReservationDao resvDao;

	@Autowired
	ReservationValidator resvValidator;

	public List<ValidationError> validateBarEntryOnCreate(BarEntry barEntry) {
		List<ValidationError> errorList = validateOnAdd(barEntry);
		/*
		 * resvValidator.validateRestaurantGuid(barEntry.getRestaurantGuid(),
		 * errorList);
		 * 
		 * restaurant = restaurantDao.find(guid)
		 */
		if (errorList.isEmpty()) {
			if (!customerDao.isGuestForRest(barEntry.getGuestGuid(), barEntry.getRestaurantGuid())) {
				errorList.add(new ValidationError(Constants.GUEST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID), ErrorCodes.INVALID_GUEST_GUID));
			}
		}
		return errorList;
	}

	public List<ValidationError> validateBarEntryOnUpdate(Map<String, Object> barEntryMap) {
		List<ValidationError> errorList = new ArrayList<ValidationError>();// validateOnPatch(barEntry,

		if (barEntryMap.containsKey(Constants.STATUS)){
			List<String> enumValues = UtilityMethods.getEnumValues(Constants.BAR_ENTRY, Constants.STATUS);
			if(!enumValues.contains(barEntryMap.get(Constants.STATUS)))
					errorList.add(new ValidationError(Constants.STATUS, Messages
					.get(ErrorCodes.INVALID_VALUE, Constants.STATUS, enumValues.toString()), ErrorCodes.INVALID_VALUE));
		}
		if (barEntryMap.containsKey(Constants.NOTE))
			if (barEntryMap.get(Constants.NOTE).toString().length() > 500)
				errorList.add(new ValidationError(Constants.NOTE, UtilityMethods.getErrorMsg(ErrorCodes.BAR_ENTRY_NOTE_MAXLENGTH), ErrorCodes.BAR_ENTRY_NOTE_MAXLENGTH));

		if (barEntryMap.containsKey(Constants.NUMCOVERS))
			if ((Integer) barEntryMap.get(Constants.NUMCOVERS) > 500)
				errorList.add(new ValidationError(Constants.NUMCOVERS, UtilityMethods.getErrorMsg(ErrorCodes.NUM_COVERS_MIN_VALUE), ErrorCodes.NUM_COVERS_MIN_VALUE));
		if (barEntryMap.keySet().stream().filter(x -> !x.equals(Constants.GUID)).count() == 0)
			errorList.add(new ValidationError(Constants.UPDATE, UtilityMethods.getErrorMsg(ErrorCodes.NOTHING_TO_UPDATE), ErrorCodes.NOTHING_TO_UPDATE));

		BarEntry barEntry = validateGuid((String)barEntryMap.get(Constants.GUID), errorList);

		if (errorList.isEmpty() && barEntry.getStatus().equals(Constants.FINISHED))
				errorList.add(new ValidationError(Constants.STATUS, UtilityMethods.getErrorMsg(ErrorCodes.CANT_UPDATE_FINISHED_BARENTRY), ErrorCodes.CANT_UPDATE_FINISHED_BARENTRY));
		
		return errorList;
	}


	@Override
	public String getMissingGuidErrorCode() {
		return ErrorCodes.BAR_ENTRY_GUID_REQUIRED;
	}

	@Override
	public String getInvalidGuidErrorCode() {
		return ErrorCodes.BAR_ENTRY_GUID_INVALID;
	}

	public Reservation validateMoveToRestaurantData(
			Map<String, Object> dataMap, List<ValidationError> listOfError) {
		//Map<String, Object> validDataMap = new HashMap<String, Object>();
		Reservation reservation = new Reservation();

		BarEntry barEntry= validateGuid(dataMap.get(Constants.GUID).toString(), listOfError);
		/*if (listOfError.isEmpty() && barEntry.getStatus().equals(Constants.FINISHED))
			listOfError.add(new ValidationError(Constants.STATUS, UtilityMethods.getErrorMsg(ErrorCodes.CANT_UPDATE_FINISHED_BARENTRY), ErrorCodes.CANT_UPDATE_FINISHED_BARENTRY));
*/
		if(listOfError.isEmpty()){
			if ((dataMap.get(Constants.TABLE_GUID) != null) && dataMap.get(Constants.TABLE_GUID) instanceof List) {
				reservation.setTableGuid((List)dataMap.get(Constants.TABLE_GUID));
				dataMap.remove(Constants.TABLE_GUID);
			}
			if ((dataMap.get(Constants.TATSTR) != null) ) {
				reservation.setTat(dataMap.get(Constants.TATSTR).toString());
				dataMap.remove(Constants.TATSTR);
			}
			if ((dataMap.get("requestTime") != null) ) {
				Date estStartTime = new Date(Long.valueOf(dataMap.get("requestTime").toString()));
				/*if(estStartTime == null)
					listOfError.add(new ValidationError("requestTime", ErrorCodes.RESERVATION_EST_START_TIME));
				else{*/
					reservation.setEstStartTime(estStartTime);
					dataMap.remove("requestTime");
				//}
			}else{
				reservation.setEstStartTime(new Date());
			}
			
			if ((dataMap.get(Constants.NUMCOVERS) != null) ) {
				Integer numCovers = Integer.valueOf(dataMap.get(Constants.NUMCOVERS).toString());
				reservation.setNumCovers(numCovers);
				dataMap.remove(Constants.NUMCOVERS);
			}else{
				reservation.setNumCovers(barEntry.getNumCovers());
			}
		}

		return reservation;
	}

	
	
	
}
