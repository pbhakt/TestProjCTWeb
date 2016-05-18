package com.clicktable.dao.intf;

import com.clicktable.model.EventPromotion;

/**
 * 
 * @author g.singh
 *
 */
@org.springframework.stereotype.Service
public interface PromotionDao extends GenericDao<EventPromotion> {
	String addEventPromotion(EventPromotion eventPromotion);
/*	ArrayList<HashMap<String, String>> addPromotionalConversation(EventPromotion eventPromotion);
*/
}
