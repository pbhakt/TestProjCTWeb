package com.clicktable.service.intf;

import java.util.Map;

import play.libs.F.Promise;

import com.clicktable.model.EventPromotion;
import com.clicktable.model.GuestConversation;
import com.clicktable.model.Miscall;
import com.clicktable.model.Template;
import com.clicktable.response.BaseResponse;

/**
 * 
 * @author p.singh
 *
 */

@org.springframework.stereotype.Service
public interface ConversationService {

	/**
	 * Add new Conversation
	 * 
	 * @param conversation
	 *            Conversation to be added
	 * @return
	 */
	// BaseResponse addConversation(GuestConversation conversation,String
	// token);

	/**
	 * Get conversations based on parameters
	 * 
	 * @param params
	 *            search parameters
	 * @return
	 */
	BaseResponse getConversation(Map<String, Object> params);

	BaseResponse addConversation(GuestConversation conversation, String token);

	void addGuestResponse(Miscall miscall);

	BaseResponse getEventPromotion(Map<String, Object> params);

	BaseResponse addEventPromotion(EventPromotion eventPromotion, String token);

	BaseResponse addTemplate(Template template, String token);

	BaseResponse getTemplate(Map<String, Object> params);

	// void updateSmsStatus(Delivery delivery);

	BaseResponse getEventPromotionGuestCount(EventPromotion eventPromotion,
			String token);

	BaseResponse addConversationAndMsg(GuestConversation conversation, boolean b);

	/*Promise<BaseResponse> getEventPromotionReport(String header,
			Map<String, Object> stringParamMap);*/

	/**
	 * @param eventPromotion
	 * @param token
	 * @param queryParams
	 * @return
	 */
	BaseResponse getEventPromotionGuestInfo(EventPromotion eventPromotion,
			String token, Map<String, Object> queryParams);

}
