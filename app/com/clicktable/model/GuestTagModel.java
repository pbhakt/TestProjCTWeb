
package com.clicktable.model;

import java.io.Serializable;



/**
 * 
 * @author p.vishwakarma
 *
 */
 

public class GuestTagModel implements Serializable
{

    	/**
	 * 
	 */
	private static final long serialVersionUID = 6333500141372745729L;
	
	  //  @Required(message=ErrorCodes.GUEST_GUID_REQUIRED)
		private String guestGuid;
        private String tagName;
        private String tagGuid;
        private String restaurantGuid;
        
        
		public String getGuestGuid() {
			return guestGuid;
		}
		public void setGuestGuid(String guestGuid) {
			this.guestGuid = guestGuid;
		}
		public String getTagName() {
			return tagName;
		}
		public void setTagName(String tagName) {
			this.tagName = tagName;
		}
		public String getTagGuid() {
			return tagGuid;
		}
		public void setTagGuid(String tagGuid) {
			this.tagGuid = tagGuid;
		}
		public String getRestaurantGuid() {
			return restaurantGuid;
		}
		public void setRestaurantGuid(String restaurantGuid) {
			this.restaurantGuid = restaurantGuid;
		}
	

        
}
