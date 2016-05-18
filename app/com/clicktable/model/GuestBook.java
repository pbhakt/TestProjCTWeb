package com.clicktable.model;

import java.util.Date;
import java.util.List;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class GuestBook extends Entity
{

	private static final long serialVersionUID = 6002272385970405201L;

	
	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private String address;
	private Integer pincode;
	private String countryCode;
	private String state;
	private String city;
	private Integer totalPoints;
	private Integer availablePoints;
	private Integer redeemedPoints;
	private Boolean isVip;
	private String reason;
	private String gender;
	private String isd_code;	
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
    private Date firstSeatedTime ;

	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone=Constants.TIMEZONE)
	private Date dob;
	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone=Constants.TIMEZONE)
	private Date anniversary;
	//private Date lastLogin;
	private String photoUrl;
	private String restGuid;
	private Integer restTotalVisits;
	private Integer restReservations;
	private Integer restWalkins;
	private Integer restCancellations;
	private Integer restNoShowCount;
	private Integer ctTotalVisits;
	private Integer ctReservations;
	private Integer ctWalkins;
	private Integer ctBarStats;
	private Integer restBarStats;
	private Integer ctCancellations;
	private Integer ctNoShowCount;
	private boolean is_dnd_permanent;
	private boolean is_dnd_user_enable;
	private List<CustomReservation> upcomingReservations;
	private List<CustomReservation> recentHistory;
	/*private List<CustomTag> foodPreferences;*/
	private List<CustomTag> tag;
	//private List<CustomTagForEvents> eventsAndOffers;
	private List<CustomTagForEvents> eventsAndOffers;
	private boolean dnd_mobile;
	private boolean dnd_email;
	private CorporateOffers corporate;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	private Date last_login;
	private String review_count;
	private String cumulative_rating;


	

	public GuestBook()
	{
	   super(); 
	}
	
	public GuestBook(GuestProfile guest)
	{
	    this.setAddress(guest.getAddress());	   
	    this.setAvailablePoints(guest.getAvailablePoints());
	    this.setCity(guest.getCity());
	    this.setCountryCode(guest.getCountryCode());
	    this.setCreatedBy(guest.getCreatedBy());
	    this.setCreatedDate(guest.getCreatedDate());	    
	    this.setEmailId(guest.getEmailId());
	    this.setFirstName(guest.getFirstName());
	    this.setGuid(guest.getGuid());
	    this.setId(guest.getId());
	    this.setIsVip(guest.getIsVip());
	    this.setGender(guest.getGender());
	    this.setReason(guest.getReason());
	    this.setLanguageCode(guest.getLanguageCode());	   
//	   / this.setLastName(guest.getLastName());
	    this.setMobile(guest.getMobile());
	    this.setPhotoUrl(guest.getPhotoUrl());
	    this.setPincode(guest.getPincode());
	    this.setRedeemedPoints(guest.getRedeemedPoints());
	    this.setRestGuid(guest.getRestGuid());
	    this.setState(guest.getState());
	    this.setStatus(guest.getStatus());
	    this.setTotalPoints(guest.getTotalPoints());
	    this.setUpdatedBy(guest.getUpdatedBy());
	    this.setUpdatedDate(guest.getUpdatedDate());	    
	    this.setDnd_email(guest.isDnd_email());
	    this.setDnd_mobile(guest.isDnd_mobile());
	    //this.setCorporate(guest.getCorporate());
	    this.setAnniversary(guest.getAnniversary());
	    this.setDob(guest.getDob());
	    this.setLast_login(guest.getLast_login());
	    this.setReview_count(guest.getReview_count());
	    this.setCumulative_rating(guest.getCumulative_rating());
	    this.setIs_dnd_permanent(guest.isIs_dnd_permanent());
	    this.setIs_dnd_user_enable(guest.isIs_dnd_user_enable());
	    this.setFirstSeatedTime(guest.getFirstSeatedTime());
	    this.setIsd_code(guest.getIsd_code());
	}
	
	
	
	
	public Boolean getIsVip() {
	    return isVip;
	}

	public void setIsVip(Boolean isVip) {
	    this.isVip = isVip;
	}

	public String getReason() {
	    return reason;
	}

	public void setReason(String reason) {
	    this.reason = reason;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	
	
	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName) 
	{
		
	    this.firstName = firstName;
		
	}

	
	public String getLastName() 
	
	{
	   return lastName;
	}

	public void setLastName(String lastName) 
	{
		
	   this.lastName = lastName;
		
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getPincode() {
		return pincode;
	}

	public void setPincode(Integer pincode) {
		this.pincode = pincode;
	}

	public Integer getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
	}

	public Integer getAvailablePoints() {
		return availablePoints;
	}

	public void setAvailablePoints(Integer availablePoints) {
		this.availablePoints = availablePoints;
	}

	public Integer getRedeemedPoints() {
		return redeemedPoints;
	}

	public void setRedeemedPoints(Integer redeemedPoints) {
		this.redeemedPoints = redeemedPoints;
	}

	public Date getDob() {
		return dob == null ? null : (Date) dob.clone();
	}

	public void setDob(Date dob) {
		this.dob = dob == null ? null : (Date) dob.clone();
	}

	public Date getAnniversary() {
		return anniversary == null ? null : (Date) anniversary.clone();
	}

	public void setAnniversary(Date anniversary) {
		this.anniversary = anniversary == null ? null : (Date) anniversary.clone();
	}

	/*public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
*/
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	
	public String getRestGuid() {
	    return restGuid;
	}

	public void setRestGuid(String restGuid) {
	    this.restGuid = restGuid;
	}

	

	public List<CustomReservation> getUpcomingReservations() {
	    return upcomingReservations;
	}

	public void setUpcomingReservations(List<CustomReservation> upcomingReservations) {
	    this.upcomingReservations = upcomingReservations;
	}

	public List<CustomReservation> getRecentHistory() {
	    return recentHistory;
	}

	public void setRecentHistory(List<CustomReservation> recentHistory) {
	    this.recentHistory = recentHistory;
	}

	

	
	
	/**
	 * @return the tag
	 */
	public List<CustomTag> getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(List<CustomTag> tag) {
		this.tag = tag;
	}

	/*public List<CustomTagForEvents> getEventsAndOffers() {
	    return eventsAndOffers;
	}

	public void setEventsAndOffers(List<CustomTagForEvents> eventsAndOffers) {
	    this.eventsAndOffers = eventsAndOffers;
	}*/

	public Integer getRestTotalVisits() {
	    return restTotalVisits;
	}

	public List<CustomTagForEvents> getEventsAndOffers() {
		return eventsAndOffers;
	}

	public void setEventsAndOffers(List<CustomTagForEvents> eventsAndOffers) {
		this.eventsAndOffers = eventsAndOffers;
	}

	public void setRestTotalVisits(Integer restTotalVisits) {
	    this.restTotalVisits = restTotalVisits;
	}

	public Integer getRestReservations() {
	    return restReservations;
	}

	public void setRestReservations(Integer restReservations) {
	    this.restReservations = restReservations;
	}

	public Integer getRestWalkins() {
	    return restWalkins;
	}

	public void setRestWalkins(Integer restWalkins) {
	    this.restWalkins = restWalkins;
	}

	public Integer getRestCancellations() {
	    return restCancellations;
	}

	public void setRestCancellations(Integer restCancellations) {
	    this.restCancellations = restCancellations;
	}

	public Integer getRestNoShowCount() {
	    return restNoShowCount;
	}

	public void setRestNoShowCount(Integer restNoShowCount) {
	    this.restNoShowCount = restNoShowCount;
	}

	public Integer getCtTotalVisits() {
	    return ctTotalVisits;
	}

	public void setCtTotalVisits(Integer ctTotalVisits) {
	    this.ctTotalVisits = ctTotalVisits;
	}

	public Integer getCtReservations() {
	    return ctReservations;
	}

	public void setCtReservations(Integer ctReservations) {
	    this.ctReservations = ctReservations;
	}

	public Integer getCtWalkins() {
	    return ctWalkins;
	}

	public void setCtWalkins(Integer ctWalkins) {
	    this.ctWalkins = ctWalkins;
	}

	public Integer getCtCancellations() {
	    return ctCancellations;
	}

	public void setCtCancellations(Integer ctCancellations) {
	    this.ctCancellations = ctCancellations;
	}

	public Integer getCtNoShowCount() {
	    return ctNoShowCount;
	}

	public void setCtNoShowCount(Integer ctNoShowCount) {
	    this.ctNoShowCount = ctNoShowCount;
	}

	public String getGender() {
	    return gender;
	}

	public void setGender(String gender) {
	    this.gender = gender;
	}

	/**
	 * @return the dnd_mobile
	 */
	public boolean isDnd_mobile() {
		return dnd_mobile;
	}

	/**
	 * @param dnd_mobile the dnd_mobile to set
	 */
	public void setDnd_mobile(boolean dnd_mobile) {
		this.dnd_mobile = dnd_mobile;
	}

	/**
	 * @return the dnd_email
	 */
	public boolean isDnd_email() {
		return dnd_email;
	}

	/**
	 * @param dnd_email the dnd_email to set
	 */
	public void setDnd_email(boolean dnd_email) {
		this.dnd_email = dnd_email;
	}

	
	/**
	 * @return the last_login
	 */
	public Date getLast_login() {
		return last_login;
	}

	/**
	 * @param last_login the last_login to set
	 */
	public void setLast_login(Date last_login) {
		this.last_login = last_login;
	}

	/**
	 * @return the isd_code
	 */
	public String getIsd_code() {
		return isd_code;
	}

	/**
	 * @param isd_code the isd_code to set
	 */
	public void setIsd_code(String isd_code) {
		this.isd_code = isd_code;
	}

	/**
	 * @return the review_count
	 */
	public String getReview_count() {
		return review_count;
	}

	/**
	 * @param review_count the review_count to set
	 */
	public void setReview_count(String review_count) {
		this.review_count = review_count;
	}

	/**
	 * @return the cumulative_rating
	 */
	public String getCumulative_rating() {
		return cumulative_rating;
	}

	/**
	 * @param cumulative_rating the cumulative_rating to set
	 */
	public void setCumulative_rating(String cumulative_rating) {
		this.cumulative_rating = cumulative_rating;
	}

	/**
	 * @return the corporate
	 */
	public CorporateOffers getCorporate() {
		return corporate;
	}

	/**
	 * @param corporate the corporate to set
	 */
	public void setCorporate(CorporateOffers corporate) {
		this.corporate = corporate;
	}

	/**
	 * @return the is_dnd_permanent
	 */
	public boolean isIs_dnd_permanent() {
		return is_dnd_permanent;
	}

	/**
	 * @param is_dnd_permanent the is_dnd_permanent to set
	 */
	public void setIs_dnd_permanent(boolean is_dnd_permanent) {
		this.is_dnd_permanent = is_dnd_permanent;
	}

	/**
	 * @return the is_dnd_user_enable
	 */
	public boolean isIs_dnd_user_enable() {
		return is_dnd_user_enable;
	}

	/**
	 * @param is_dnd_user_enable the is_dnd_user_enable to set
	 */
	public void setIs_dnd_user_enable(boolean is_dnd_user_enable) {
		this.is_dnd_user_enable = is_dnd_user_enable;
	}

	public Date getFirstSeatedTime() {
		return firstSeatedTime;
	}

	public void setFirstSeatedTime(Date firstSeatedTime) {
		this.firstSeatedTime = firstSeatedTime;
	}

	public Integer getCtBarStats() {
		return ctBarStats;
	}

	public void setCtBarStats(Integer ctBarStats) {
		this.ctBarStats = ctBarStats;
	}

	public Integer getRestBarStats() {
		return restBarStats;
	}

	public void setRestBarStats(Integer restBarStats) {
		this.restBarStats = restBarStats;
	}

	
	
	
	
	
	
	
	

}