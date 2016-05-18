package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.BAR_TIME_MAX_VALUE;
import static com.clicktable.util.ErrorCodes.BAR_TIME_MIN_VALUE;

import java.util.HashSet;
import java.util.Set;

import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



/**
 * 
 * @author p.singh
 *
 */

/*@NodeEntity*/
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class RestSystemConfigModel 
{
	
		private String restaurantGuid;
         private Integer reserveReleaseTime;
         private Integer reserveOverlapTime;
         private Integer waitlistReleaseTime;
         private Integer diningSlotInterval;
         @Pattern(message=ErrorCodes.FORCED_SHIFT_END_TIME_FORMAT, value="[0-2][0-9]:[0-5][0-9]$")
         private String forcedShiftEndTime;
         private Integer bufferOpenTime;
         @Required(message=ErrorCodes.OTP_MOBILE_REQUIRED)
         private String otpMobile;
         @Required(message=ErrorCodes.TAT_WD_12_REQUIRED)
         private Integer tat_wd_12;
         private Integer family_tat_wd_12;
         @Required(message=ErrorCodes.TAT_WE_12_REQUIRED)
         private Integer tat_we_12;
         private Integer family_tat_we_12;
         @Required(message=ErrorCodes.TAT_WD_34_REQUIRED)
         private Integer tat_wd_34;
         private Integer family_tat_wd_34;
         @Required(message=ErrorCodes.TAT_WE_34_REQUIRED)
         private Integer tat_we_34;
         private Integer family_tat_we_34;
         @Required(message=ErrorCodes.TAT_WD_56_REQUIRED)
         private Integer tat_wd_56;
         private Integer family_tat_wd_56;
         @Required(message=ErrorCodes.TAT_WE_56_REQUIRED)
         private Integer tat_we_56;
         private Integer family_tat_we_56;
         @Required(message=ErrorCodes.TAT_WD_78_REQUIRED)
         private Integer tat_wd_78;
         private Integer family_tat_wd_78;
         @Required(message=ErrorCodes.TAT_WE_78_REQUIRED)
         private Integer tat_we_78;
         private Integer family_tat_we_78;
         @Required(message=ErrorCodes.TAT_WD_8P_REQUIRED)
         private Integer tat_wd_8P;
         private Integer family_tat_wd_8P;
         @Required(message=ErrorCodes.TAT_WE_8P_REQUIRED)
         private Integer tat_we_8P;
         private Integer family_tat_we_8P;
         private Boolean bar =false;
         @Min(message=BAR_TIME_MIN_VALUE,value = 10)
         @Max(message=BAR_TIME_MAX_VALUE,value = 120)
         private Integer barMaxTime;        
         
         
	public String getRestaurantGuid() {
	    return restaurantGuid;
	}
	public void setRestaurantGuid(String restaurantGuid) {
	    this.restaurantGuid = restaurantGuid;
	}
	
	
	public Integer getReserveReleaseTime() {
	    return reserveReleaseTime;
	}
	public void setReserveReleaseTime(Integer reserveReleaseTime) {
	    this.reserveReleaseTime = reserveReleaseTime;
	}
	public Integer getWaitlistReleaseTime() {
	    return waitlistReleaseTime;
	}
	public void setWaitlistReleaseTime(Integer waitlistReleaseTime) {
	    this.waitlistReleaseTime = waitlistReleaseTime;
	}
	public Integer getDiningSlotInterval() {
	    return diningSlotInterval;
	}
	public void setDiningSlotInterval(Integer diningSlotInterval) {
	    this.diningSlotInterval = diningSlotInterval;
	}
	public Integer getTat_wd_12() {
	    return tat_wd_12;
	}
	public void setTat_wd_12(Integer tat_wd_12) {
	    this.tat_wd_12 = tat_wd_12;
	}
	public Integer getTat_we_12() {
	    return tat_we_12;
	}
	public void setTat_we_12(Integer tat_we_12) {
	    this.tat_we_12 = tat_we_12;
	}
	public Integer getTat_wd_34() {
	    return tat_wd_34;
	}
	public void setTat_wd_34(Integer tat_wd_34) {
	    this.tat_wd_34 = tat_wd_34;
	}
	public Integer getTat_we_34() {
	    return tat_we_34;
	}
	public void setTat_we_34(Integer tat_we_34) {
	    this.tat_we_34 = tat_we_34;
	}
	public Integer getTat_wd_56() {
	    return tat_wd_56;
	}
	public void setTat_wd_56(Integer tat_wd_56) {
	    this.tat_wd_56 = tat_wd_56;
	}
	public Integer getTat_we_56() {
	    return tat_we_56;
	}
	public void setTat_we_56(Integer tat_we_56) {
	    this.tat_we_56 = tat_we_56;
	}
	public Integer getTat_wd_78() {
	    return tat_wd_78;
	}
	public void setTat_wd_78(Integer tat_wd_78) {
	    this.tat_wd_78 = tat_wd_78;
	}
	public Integer getTat_we_78() {
	    return tat_we_78;
	}
	public void setTat_we_78(Integer tat_we_78) {
	    this.tat_we_78 = tat_we_78;
	}
	public Integer getTat_wd_8P() {
	    return tat_wd_8P;
	}
	public void setTat_wd_8P(Integer tat_wd_8P) {
	    this.tat_wd_8P = tat_wd_8P;
	}
	public Integer getTat_we_8P() {
	    return tat_we_8P;
	}
	public void setTat_we_8P(Integer tat_we_8P) {
	    this.tat_we_8P = tat_we_8P;
	}
	/**
	 * @return the reserveOverlapTime
	 */
	public Integer getReserveOverlapTime() {
		return reserveOverlapTime;
	}
	/**
	 * @param reserveOverlapTime the reserveOverlapTime to set
	 */
	public void setReserveOverlapTime(Integer reserveOverlapTime) {
		this.reserveOverlapTime = reserveOverlapTime;
	}
	
	public String getOtpMobile() {
	    return otpMobile;
	}
	public void setOtpMobile(String otpMobile) {
	    this.otpMobile = otpMobile;
	}
	public String getForcedShiftEndTime() {
		return forcedShiftEndTime;
	}
	public void setForcedShiftEndTime(String forcedShiftEndTime) {
		this.forcedShiftEndTime = forcedShiftEndTime;
	}
	public Integer getBufferOpenTime() {
		return bufferOpenTime;
	}
	public void setBufferOpenTime(Integer bufferOpenTime) {
		this.bufferOpenTime = bufferOpenTime;
	}
	public Boolean getBar() {
		return bar;
	}
	public void setBar(Boolean bar) {
		this.bar = bar;
	}
	public Integer getBarMaxTime() {
		return barMaxTime;
	}
	public void setBarMaxTime(Integer barMaxTime) {
		this.barMaxTime = barMaxTime;
	}
	
	
	private static Set<String> tatFields = new HashSet<String>();
	static{
		tatFields.add(Constants.TAT_WD_12);
		tatFields.add(Constants.TAT_WE_12);
		tatFields.add(Constants.TAT_WD_34);
		tatFields.add(Constants.TAT_WE_34);
		tatFields.add(Constants.TAT_WD_56);
		tatFields.add(Constants.TAT_WE_56);
		tatFields.add(Constants.TAT_WD_78);
		tatFields.add(Constants.TAT_WE_78);
		tatFields.add(Constants.TAT_WD_8P);
		tatFields.add(Constants.TAT_WE_8P);
	}     
         
	public static Set<String> getTatFields() {
		return tatFields;
	}
	public Integer getFamily_tat_wd_12() {
		return family_tat_wd_12;
	}
	public void setFamily_tat_wd_12(Integer family_tat_wd_12) {
		this.family_tat_wd_12 = family_tat_wd_12;
	}
	public Integer getFamily_tat_we_12() {
		return family_tat_we_12;
	}
	public void setFamily_tat_we_12(Integer family_tat_we_12) {
		this.family_tat_we_12 = family_tat_we_12;
	}
	public Integer getFamily_tat_wd_34() {
		return family_tat_wd_34;
	}
	public void setFamily_tat_wd_34(Integer family_tat_wd_34) {
		this.family_tat_wd_34 = family_tat_wd_34;
	}
	public Integer getFamily_tat_we_34() {
		return family_tat_we_34;
	}
	public void setFamily_tat_we_34(Integer family_tat_we_34) {
		this.family_tat_we_34 = family_tat_we_34;
	}
	public Integer getFamily_tat_wd_56() {
		return family_tat_wd_56;
	}
	public void setFamily_tat_wd_56(Integer family_tat_wd_56) {
		this.family_tat_wd_56 = family_tat_wd_56;
	}
	public Integer getFamily_tat_we_56() {
		return family_tat_we_56;
	}
	public void setFamily_tat_we_56(Integer family_tat_we_56) {
		this.family_tat_we_56 = family_tat_we_56;
	}
	public Integer getFamily_tat_wd_78() {
		return family_tat_wd_78;
	}
	public void setFamily_tat_wd_78(Integer family_tat_wd_78) {
		this.family_tat_wd_78 = family_tat_wd_78;
	}
	public Integer getFamily_tat_we_78() {
		return family_tat_we_78;
	}
	public void setFamily_tat_we_78(Integer family_tat_we_78) {
		this.family_tat_we_78 = family_tat_we_78;
	}
	public Integer getFamily_tat_wd_8P() {
		return family_tat_wd_8P;
	}
	public void setFamily_tat_wd_8P(Integer family_tat_wd_8P) {
		this.family_tat_wd_8P = family_tat_wd_8P;
	}
	public Integer getFamily_tat_we_8P() {
		return family_tat_we_8P;
	}
	public void setFamily_tat_we_8P(Integer family_tat_we_8P) {
		this.family_tat_we_8P = family_tat_we_8P;
	} 
	
	
	

}
