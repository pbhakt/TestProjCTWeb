package com.clicktable.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.clicktable.dao.intf.CorporateOffersDao;
import com.clicktable.dao.intf.EventDao;
import com.clicktable.dao.intf.GuestTagDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.TemplateDao;
import com.clicktable.model.CorporateOffers;
import com.clicktable.model.EventPromotion;
import com.clicktable.model.GuestConversation;
import com.clicktable.model.Miscall;
import com.clicktable.model.Reservation;
import com.clicktable.model.Tag;
import com.clicktable.model.Template;
import com.clicktable.model.UserInfoModel;
import com.clicktable.repository.TagPreferencesRepo;
import com.clicktable.util.Constants;
//import com.clicktable.repository.ConversationRepo;
//import com.clicktable.dao.intf.ConversationDao;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.UtilityMethods;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public class ConversationValidator extends EntityValidator<GuestConversation> {
	@Autowired
	EventDao eventDao;
	@Autowired
	TemplateDao templateDao;
	@Autowired
	TagPreferencesRepo tag_repo;
	@Autowired
	ReservationDao reservationDao;

	@Autowired
	GuestTagDao tagDao;
	
	@Autowired
	CorporateOffersDao corpDao;
	
	@Autowired
	RestaurantValidator restValidator;
	@Autowired
	EventValidator eventValidator;


	public List<ValidationError> validatePromotionalConversation(EventPromotion eventPromotion, UserInfoModel userInfo) {
		List<ValidationError> errorList = validateOnAdd(eventPromotion);
		if (errorList.isEmpty()) {
			restValidator.validateRestaurantInNeo4j(eventPromotion.getRestaurantGuid(), userInfo, errorList);
			eventValidator.validateGuid(eventPromotion.getEventGuid(), errorList);
			//validate Corporate Guid
			//validate Enum values
			//validateEventId(eventPromotion.getEventGuid(), errorList, eventPromotion.getRestaurantGuid());
			for (String tag : eventPromotion.getTagGuids()) {
				if (tag.isEmpty())
					eventPromotion.getTagGuids().remove(tag);
				else {
					Map<String, Object> params3 = new HashMap<String, Object>();
					params3.put(Constants.GUID, tag);
					params3.put(Constants.STATUS, Constants.ACTIVE_STATUS);
					List<Tag> tagModelList = tagDao.findByFields(Tag.class, params3);
					
					//TagModelOld tagModel = tag_repo.getTag(tag);
					if (tagModelList.size() != 1) {
						errorList.add(createError(Constants.TAG_GUID, ErrorCodes.INVALID_TAG_ID));
					}
				}
			}

		}
		return errorList;
	}

	public List<ValidationError> validatePromotionalCount(EventPromotion eventPromotionCount, UserInfoModel userInfo) {
		List<ValidationError> errorList = validateOnAdd(eventPromotionCount);
		/*if ((!UtilityMethods.getEnumValues(Constants.GUEST_CONVERSATION_LABEL, Constants.GUEST_TYPE).contains(eventPromotionCount.getGuestType())) && (eventPromotionCount.getGuestType() != null)) {
			errorList.add(new ValidationError(Constants.GUEST_TYPE, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GUEST_TYPE), ErrorCodes.INVALID_GUEST_TYPE));
		}*/
		errorList.addAll(validateEnumValues(eventPromotionCount.getClass().getSimpleName(), Constants.GUEST_TYPE,eventPromotionCount.getGuestType()));
		errorList.addAll(validateEnumValues(eventPromotionCount.getClass().getSimpleName(), Constants.GENDER,eventPromotionCount.getGender()));
		
		if (errorList.isEmpty()) {
			restValidator.validateRestaurantInNeo4j(eventPromotionCount.getRestaurantGuid(), userInfo, errorList);
			eventPromotionCount.getTagGuids().removeIf(x -> x == null || x == "");
			eventPromotionCount.getTagGuids().forEach(tag->{
				Map<String, Object> params3 = new HashMap<String, Object>();
				params3.put(Constants.GUID, tag);
				params3.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				List<Tag> tagModelList = tagDao.findByFields(Tag.class, params3);
				if (tagModelList.size() != 1) {
					errorList.add(createError(Constants.TAG_GUIDS_EVENT_PROMOTION, ErrorCodes.INVALID_TAG_ID));
				}
			});
			if (errorList.isEmpty()) {
				eventPromotionCount.getCorporatesGuids().removeIf(x -> x == null || x == "");
				eventPromotionCount.getCorporatesGuids().forEach(corp->{
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(Constants.GUID, corp);
					params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
					List<CorporateOffers> offers = corpDao.findByFields(CorporateOffers.class, params);
					if (offers.size() != 1) {
						errorList.add(createError(Constants.CORPORATE_GUIDS, ErrorCodes.INVALID_CORPORATE_OFFERS_GUID));
					}
				});
				
			}
			
		}
		return errorList;
	}

	public List<ValidationError> validateTemplateOnAdd(Template template, UserInfoModel userInfo) {
		List<ValidationError> errorList = validateOnAdd(template);
		if (template.getTemplates().size() > 20) {
			errorList.add(createError(Constants.TEMPLATES, ErrorCodes.TEMPLATE_SIZE));
		} else if (!template.getTemplates().stream().allMatch(x -> x.length() < 320)) {
			errorList.add(createError(Constants.TEMPLATES, ErrorCodes.TEMPLATE_MAXLENGTH));
		}
		restValidator.validateRestaurantInNeo4j(template.getRestaurantGuid(), userInfo, errorList);
		return errorList;
	}

	public List<ValidationError> validateTemplateOnUpdate(Template template, UserInfoModel userInfo) {
		List<ValidationError> errorList = validateTemplateOnAdd(template, userInfo);
		if (errorList.isEmpty()) {
			if (templateDao.find(template.getGuid()) == null) {
				errorList.add(createError(Constants.GUID, ErrorCodes.INVALID_TEMPLATE_GUID));
			}
		}
		return errorList;
	}

	public List<ValidationError> validateConversationOnAdd(GuestConversation conversation, UserInfoModel userInfo) {
		List<ValidationError> errorList = validateOnAdd(conversation);
		if ((!UtilityMethods.getEnumValues(Constants.GUEST_CONVERSATION_LABEL, Constants.ORIGIN).contains(conversation.getOrigin())) && (conversation.getOrigin() != null)) {
			errorList = CustomValidations.populateErrorList(errorList, Constants.ORIGIN, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_ORIGIN), ErrorCodes.INVALID_ORIGIN);
		}
		errorList = CustomValidations.validateStatusAndLanguageCode(errorList, conversation.getStatus(), conversation.getLanguageCode());
		if (errorList.isEmpty()) {
			restValidator.validateRestaurantInNeo4j(conversation.getRestaurantGuid(), userInfo, errorList);
			if (conversation.getOrigin() == Constants.EVENT_PROMOTION_ENUM_VALUE) {
				errorList = CustomValidations.populateErrorList(errorList, Constants.ORIGIN_GUID, UtilityMethods.getErrorMsg(ErrorCodes.GUESTCONVERSATION_ORIGIN_ID),
						ErrorCodes.GUESTCONVERSATION_ORIGIN_ID);
				eventValidator.validateGuid(conversation.getOriginGuid(), errorList);
				//validateEventId(conversation.getOriginGuid(), errorList, conversation.getRestaurantGuid());
			} else if (conversation.getOrigin() == Constants.RESERVATION_ENUM_VALUE) {
				validateReservationId(conversation.getOriginGuid(), errorList, conversation.getRestaurantGuid());
			}
		}
		return errorList;
	}

	public List<ValidationError> validateMiscall(Miscall miscall) {
		return validateOnAdd(miscall);
	}



	public Reservation validateReservationId(String reservationId, List<ValidationError> listOfError, String restId) {
		Reservation reservation = null;
		if (reservationId == null) {
			listOfError.add(createError(Constants.RESERVATION_GUID, ErrorCodes.RESERVATION_ID_REQUIRED));
		} else {
			reservation = reservationDao.find(reservationId);
			if (reservation == null)
				listOfError.add(new ValidationError(Constants.RESERVATION_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_RESERVATION_ID), ErrorCodes.INVALID_RESERVATION_ID));
			else if (!reservation.getRestaurantGuid().equals(restId))
				listOfError.add(new ValidationError(Constants.RESERVATION_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_RESERVATION_ID), ErrorCodes.INVALID_RESERVATION_ID));
		}
		return reservation;
	}

	@Override
	public
	String getMissingGuidErrorCode() {
		return ErrorCodes.GUID_REQUIRED;
	}

	@Override
	public
	String getInvalidGuidErrorCode() {
		return ErrorCodes.INVALID_GUID;
	}
	


}
