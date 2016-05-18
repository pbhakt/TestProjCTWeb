package com.clicktable.validate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.data.validation.Validation;
import play.i18n.Messages;

import com.clicktable.dao.intf.GenericDao;
import com.clicktable.model.Entity;
import com.clicktable.model.Reservation;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

public abstract class EntityValidator<E> {

	@Autowired(required =false)
	GenericDao<E> dao;

	/**
	 * validations on a class at the time of addition
	 * 
	 * @param <T>
	 * @return
	 * */
	public <T> List<ValidationError> validateOnAdd(T t) {

		Set<ConstraintViolation<Object>> violations = (Set) Validation.getValidator().validate(t, Default.class);
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		Logger.debug(" outsideis ");
		for (ConstraintViolation<Object> violation : violations) {
			String name = violation.getPropertyPath().iterator().next().getName();
			Logger.debug(name);
			Long value = 0L;
			String errorCode = "";
			Logger.debug(" violation.getMessageTemplate() is " + violation.getMessageTemplate());
			Logger.debug(" violation.getMessageTemplate() is " + violation.getMessage() + " <<<<<<<< " + violation.getConstraintDescriptor().getAnnotation());

			if (violation.getConstraintDescriptor().getAnnotation().toString().contains("MinLength")) {
				value = UtilityMethods.getClassField(name, t.getClass()).getAnnotation(MinLength.class).value();
				errorCode = UtilityMethods.getClassField(name, t.getClass()).getAnnotation(MinLength.class).message();
			} else if (violation.getConstraintDescriptor().getAnnotation().toString().contains("MaxLength")) {
				value = UtilityMethods.getClassField(name, t.getClass()).getAnnotation(MaxLength.class).value();
				errorCode = UtilityMethods.getClassField(name, t.getClass()).getAnnotation(MaxLength.class).message();
			} else if (violation.getConstraintDescriptor().getAnnotation().toString().contains("Min")) {
				value = UtilityMethods.getClassField(name, t.getClass()).getAnnotation(Min.class).value();
				errorCode = UtilityMethods.getClassField(name, t.getClass()).getAnnotation(Min.class).message();
			} else if (violation.getConstraintDescriptor().getAnnotation().toString().contains("Max")) {
				value = UtilityMethods.getClassField(name, t.getClass()).getAnnotation(Max.class).value();
				errorCode = UtilityMethods.getClassField(name, t.getClass()).getAnnotation(Max.class).message();
			} else if (violation.getConstraintDescriptor().getAnnotation().toString().contains("Required")) {
				errorCode = UtilityMethods.getClassField(name, t.getClass()).getAnnotation(Required.class).message();
			} else if (violation.getConstraintDescriptor().getAnnotation().toString().contains("Email")) {
				errorCode = UtilityMethods.getClassField(name, t.getClass()).getAnnotation(Email.class).message();
			} else if (violation.getConstraintDescriptor().getAnnotation().toString().contains("Pattern")) {
				errorCode = UtilityMethods.getClassField(name, t.getClass()).getAnnotation(Pattern.class).message();
			}

			Logger.debug("code is<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<    " + errorCode + "   value is >>>>>>>>>>>>>" + value);
			errorList = CustomValidations.populateErrorList(errorList, name, UtilityMethods.getErrorMsg(errorCode), errorCode);

		}

		return errorList;
	}

	/**
	 * validations on parameters passed to find an entity
	 * 
	 * @param params
	 *            map of search parameters
	 * @return map with valid search parameters
	 */
	public Map<String, Object> validateFinderParams(Map<String, Object> params, Class type) {
		Map<String, Object> validParamMap = new HashMap<String, Object>();
		String dateStr;
		for (Entry<String, Object> entry : params.entrySet()) {
			Field field;
			if (entry.getKey().equals(Constants.PAGE_NO) || entry.getKey().equals(Constants.PAGE_SIZE)|| entry.getKey().equals(Constants.FREE_SEARCH))
				validParamMap.put(entry.getKey(), (String) entry.getValue());
			else if (entry.getKey().equals(Constants.ORDER_BY)) {
				String validOrderParams = getOrderByParam(entry, type);
				if (validOrderParams != null)
					validParamMap.put(Constants.ORDER_BY, validOrderParams);
				Logger.debug("validOrderParams are " + validOrderParams);
			} else if (entry.getKey().equals(Constants.ORDER_PREFERENCE)) {
				if (UtilityMethods.getEnumValues(Constants.COMMON_MODULE, Constants.ORDER_PREFERENCE).contains(entry.getValue())) {
					Logger.debug("enum list is " + UtilityMethods.getEnumValues(Constants.COMMON_MODULE, Constants.ORDER_PREFERENCE));
					validParamMap.put(Constants.ORDER_PREFERENCE, entry.getValue());
				}
				Logger.debug("valid param map is " + validParamMap);
			} else if ((field = UtilityMethods.getClassField(entry.getKey(), type)) != null) {
				validateFieldValues(field, entry,validParamMap);

			}else if (entry.getKey().endsWith(Constants.BEFORE)	|| entry.getKey().endsWith(Constants.AFTER)
					|| entry.getKey().endsWith(Constants.BEFORE_OR_ON) || entry.getKey().endsWith(Constants.AFTER_OR_ON)
					|| entry.getKey().endsWith(Constants.ON)) {
					Date date =null;
					if(entry.getValue() instanceof java.util.Date){
						date = (Date)entry.getValue();
					}else{
						dateStr = entry.getValue().toString();

						date= UtilityMethods.parseDate(dateStr, Constants.TIMESTAMP_FORMAT);									
						//UtilityMethods.parseDate(dateStr, Constants.DATE_FORMAT);
						if(date==null || !((entry.getKey().endsWith(Constants.BEFORE)	|| entry.getKey().endsWith(Constants.AFTER)))){						
							date=UtilityMethods.parseDate(dateStr, Constants.DATE_FORMAT);
						}	
					}
					if(date !=null)
						validParamMap.put(entry.getKey(),date);							
			}else if (entry.getKey().endsWith(Constants.LIKE)){
				validParamMap.put(entry.getKey(), Constants.PRE_LIKE_STRING+entry.getValue()+Constants.POST_LIKE_STRING);
			}else if (entry.getKey().endsWith(Constants.START_WITH)  || entry.getKey().endsWith(Constants.STARTS_WITH)){
				validParamMap.put(entry.getKey(), Constants.PRE_START_WITH_STRING+entry.getValue()+Constants.POST_LIKE_STRING);
			}else if (entry.getKey().endsWith(Constants.LESS)
					|| entry.getKey().endsWith(Constants.GREATER)
					|| entry.getKey().endsWith(Constants.LESS_EQUAL)
					|| entry.getKey().endsWith(Constants.GREATER_EQUAL)
					) {
				validParamMap.put(entry.getKey(),Integer.parseInt((String)entry.getValue()));	
			}
		}
		return validParamMap;
	}

	private void validateFieldValues(Field field, Entry<String, Object> entry,
			Map<String, Object> validParamMap) {
		Object value = entry.getValue();
		if (null != value) {
			if (field.getType().isAssignableFrom(Integer.class)) {
				validParamMap.put(field.getName(), Integer.parseInt(value.toString()));
			} else if (field.getType().isAssignableFrom(Long.class) && !field.getName().equalsIgnoreCase("id")){
				validParamMap.put(field.getName(), Long.parseLong((String) value));
			}else if (field.getType().isAssignableFrom(Date.class)) {
				String format = field.getAnnotation(JsonFormat.class).pattern();
				validParamMap.put(field.getName(), UtilityMethods.parseDate(value.toString(), format));
			} else if (field.getType().isAssignableFrom(Boolean.class)){
				validParamMap.put(field.getName(), Boolean.parseBoolean((String)value));
			}else if (field.getType().isAssignableFrom(String.class)) {
				String val = (String) value;
				if (val.contains(",")) {
					validParamMap.put(field.getName(), Arrays.asList(val.split(",")));
				} else
					validParamMap.put(field.getName(), value);
			}
		}
		
	}

	protected String getOrderByParam(Entry<String, Object> entry, Class type) {
		String[] orderParams = entry.getValue().toString().split(",");
		StringBuilder validOrderParams = new StringBuilder();
		for (String fieldName : orderParams) {
			try {
				if (validOrderParams.length() == 0)
					validOrderParams.append(fieldName);
				else
					validOrderParams.append("," + fieldName);
				// validOrderParams = validOrderParams + fieldName + ",";
			} catch (SecurityException e) {
				Logger.debug("Exception is ------------ " + e.getLocalizedMessage());

				Logger.warn("field name:" + entry.getKey() + "not searchable, Ignoring.");
				break;
			}
		}
		return validOrderParams.toString();
	}
	protected List<ValidationError> validateEnumValues(String moduleName, String propertyName, Object value) {
		Logger.debug("validating enum values ");
		List<ValidationError> errorList = new ArrayList<ValidationError>();
				if (value != null && !UtilityMethods.getEnumValues(moduleName, propertyName).contains(value)) {
					errorList.add(new ValidationError(propertyName, Messages.get(ErrorCodes.INVALID_VALUE, value, UtilityMethods.getEnumValues(moduleName, propertyName).toString()),
							ErrorCodes.INVALID_VALUE));
				}
		return errorList;
	}

	protected List<ValidationError> validateEnumValues(Object entity, String moduleName) {
		Logger.debug("validating enum values ");
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		Config config = ConfigFactory.load(Constants.ENUMS_FILE).getConfig(moduleName);

		for (Entry<String, ConfigValue> enumField : config.entrySet()) {
			Logger.debug("enum field is " + enumField);
			List<String> values = new ArrayList<String>();
			try {
				Object property = PropertyUtils.getProperty(entity, enumField.getKey());
				if (property instanceof List) {
					values = (List<String>) property;
				} else
					values.add((String) property);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				Logger.debug("Exception is ------------ " + e.getLocalizedMessage());
				Logger.debug("enum value " + enumField.getKey() + " not present");
			}

			for (String value : values) {
				if (value != null && !UtilityMethods.getEnumValues(moduleName, enumField.getKey()).contains(value)) {
					errorList.add(new ValidationError(enumField.getKey(), Messages.get(ErrorCodes.INVALID_VALUE, value, UtilityMethods.getEnumValues(moduleName, enumField.getKey()).toString()),
							ErrorCodes.INVALID_VALUE));
				}
			}
		}
		return errorList;
	}

	public <T> List<String> requiredFields(T t) {
		// t must be empty object
		Set<ConstraintViolation<Object>> violations = (Set) Validation.getValidator().validate(t, Default.class);
		List<String> requiredFields = new ArrayList<String>();
		violations.stream().filter(violation -> violation.getConstraintDescriptor().getAnnotation().toString().contains("Required"))
				.forEach(violation -> requiredFields.add(violation.getPropertyPath().iterator().next().getName()));
		return requiredFields;
	}

	public ValidationError createError(String fieldName, String errorcode) {
		return new ValidationError(fieldName, UtilityMethods.getErrorMsg(errorcode), errorcode);
	}

	public List<String> getGuids(List<? extends Entity> entities) {
		List<String> guids = new ArrayList<String>();
		entities.forEach((e) -> {
			guids.add(e.getGuid());
		});
		return guids;
	}
	
	public E validateGuid(String guid, List<ValidationError> listOfError) {
		E e = null;
		Logger.debug("guid is "+guid);
		if (guid == null) 
		{
			listOfError.add(createError(Constants.GUID, getMissingGuidErrorCode()));
		} 
		else
		{
			e = dao.find(guid);
			if (e == null)
				listOfError.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(getInvalidGuidErrorCode()) + " " + guid,ErrorCodes.INVALID_GUID));
		}
		return e;
	}
	
	abstract public String getMissingGuidErrorCode();
	abstract public String getInvalidGuidErrorCode();

	public List<ValidationError> validateOfferId(Reservation reservation,
			List<ValidationError> errorList) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
