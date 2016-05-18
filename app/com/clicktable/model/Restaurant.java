package com.clicktable.model;

import static com.clicktable.util.ErrorCodes.DISPLAY_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.DISPLAY_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.FOOD_SOURCE_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.REST_NAME_MAX_LENGTH;
import static com.clicktable.util.ErrorCodes.REST_NAME_REQUIRED;
import static com.clicktable.util.ErrorCodes.WASTE_DISPOSAL_MAX_LENGTH;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Restaurant extends Entity {

	private static final long serialVersionUID = -1029048878805229612L;
	@Required(message=REST_NAME_REQUIRED)
	@MaxLength(message=REST_NAME_MAX_LENGTH,value=200)
	private String name;
	@Required(message=DISPLAY_NAME_REQUIRED)
	@MaxLength(message=DISPLAY_NAME_MAX_LENGTH,value=200)
	@GraphProperty(propertyName = "disp_name")
	private String displayName;

	private String legalName;

	@GraphProperty(propertyName = "tag_line")
	private String tagLine;
	private Integer capacity;
	@GraphProperty(propertyName = "min_capacity")
	private Integer minimumCapacity;
	@GraphProperty(propertyName = "max_capacity")
	private Integer maximumCapacity;
	@GraphProperty(propertyName = "cost_for_2")
	private Double costFor2 = 0.0;
	private String latitude;
	private String longitude;
	private String website;
	@GraphProperty(propertyName = "phone_no_1")
	private String phoneNo1;
	@GraphProperty(propertyName = "phone_no_2")
	private String phoneNo2;
	@GraphProperty(propertyName = "address_line_1")
	private String addressLine1;
	@GraphProperty(propertyName = "address_line_2")
	private String addressLine2;
	private String region;
	@GraphProperty(propertyName = "country_cd")
	private String countryCode;
	private String state;
	private String city;
	private String locality;
	private String building;
	private Integer zipcode;	
	@GraphProperty(propertyName = "physical_state")
	private String physicalState;
	
	@MaxLength(message=FOOD_SOURCE_MAX_LENGTH,value=100)
	@GraphProperty(propertyName = "food_source")
	private String foodSource;
	@MaxLength(message=WASTE_DISPOSAL_MAX_LENGTH,value=100)
	@GraphProperty(propertyName = "waste_disposal")
	private String wasteDisposal;
	@GraphProperty(propertyName = "reserve_release_time")
	private Integer reserveReleaseTime;
	@GraphProperty(propertyName = "waitlist_release_time")
	private Integer waitlistReleaseTime;
	@GraphProperty(propertyName = "reserve_overlap_time")
	private Integer reserveOverlapTime;
	@GraphProperty(propertyName = "dining_slot_interval")
	private Integer diningSlotInterval;
	@JsonFormat(pattern = Constants.TIME_FORMAT_CONFIGURATION,  timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "forced_shift_end_time")
	private Date forcedShiftEndTime;
	@GraphProperty(propertyName = "buffer_open_time")
	private Integer bufferOpenTime;
	@GraphProperty(propertyName = "otp_mobile")
	private String otpMobile;
	@GraphProperty(propertyName = "upfront_payment")
	private Boolean upfrontPayment;
	@GraphProperty(propertyName = "upfront_amount")
	private Integer upfrontAmount;
	@GraphProperty(propertyName = "cancellation_block_time")
	private Double cancellationBlockTime;

	private String timezone;
	@GraphProperty(propertyName = "preferred_date_format")
	private String preferredDateFormat;
	@GraphProperty(propertyName = "preferred_time_format")
	private String preferredTimeFormat;
	private String currency;
	@GraphProperty(propertyName = "temprature_scale")
	private String tempratureScale;
	private String landmark;
	private String email;
	
	private String accountId;
	private String parentAccountId;
    private Boolean bar;
    @GraphProperty(propertyName = "bar_max_time")
    private Integer barMaxTime;
	
	public Integer getBarMaxTime() {
		return barMaxTime;
	}

	public void setBarMaxTime(Integer barMaxTime) {
		this.barMaxTime = barMaxTime;
	}

	public String getPhysicalState() {
		return physicalState;
	}

	public void setPhysicalState(String physicalState) {
		this.physicalState = physicalState;
	}

	private static Set<String> customFinderParams = new HashSet<String>();
	static{
		customFinderParams.add(Constants.SHIFT_END_TIME_BEFORE);
		customFinderParams.add(Constants.SHIFT_END_TIME_AFTER);

	}

	public Restaurant() {
		// tables = new HashSet<RestaurantHasTable>();
	}

	public Restaurant(Onboarding onboard) {
		addressLine1 = onboard.getAddressLine1();
		addressLine2 = onboard.getAddressLine2();
		name = onboard.getRestaurantName();
		displayName = onboard.getRestaurantName();
		countryCode = onboard.getCountryCode();
		currency = onboard.getCurrency();
		state = onboard.getState();
		city = onboard.getCity();
		region = onboard.getRegion();
		building = onboard.getBuilding();
		zipcode = onboard.getZipcode();
		phoneNo1 = onboard.getContact();
		email = onboard.getEmail();
		landmark = onboard.getLandmark();
		locality = onboard.getLocality();
		legalName = onboard.getLegalName();
		this.setLanguageCode(onboard.getLanguageCode());
		this.setUpdatedBy(onboard.getUpdatedBy());
		this.setCreatedBy(onboard.getCreatedBy());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
			this.name = name== null ? null :name.trim();
	}


	public String getLatitude() {
	    return latitude;
	}

	public void setLatitude(String latitude) {
	    this.latitude = latitude;

	}

	public String getLongitude() {
	    return longitude;

	}

	public void setLongitude(String longitude) {
	    this.longitude = longitude;

	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website == null?null :website.trim();
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state == null? null :state.trim();

	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city == null?null:city.trim();
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
			this.locality = locality == null?null:locality.trim();

	}

	public Integer getZipcode() {
		return zipcode;
	}

	public void setZipcode(Integer zipcode) {
		this.zipcode = zipcode;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	/*
	 * public String getLandmark() { return landmark; }
	 * 
	 * public void setLandmark(String landmark) { this.landmark = landmark; }
	 */

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getTagLine() {
		return tagLine;
	}

	public void setTagLine(String tagLine) {
		this.tagLine = tagLine;
	}

	public Double getCostFor2() {
		return costFor2;
	}

	public void setCostFor2(Double costFor2) {
		this.costFor2 = costFor2;
	}

	public String getPhoneNo1() {
		return phoneNo1;
	}

	public void setPhoneNo1(String phoneNo1) {
		this.phoneNo1 = phoneNo1;
	}

	public String getPhoneNo2() {
		return phoneNo2;
	}

	public void setPhoneNo2(String phoneNo2) {
		this.phoneNo2 = phoneNo2;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getDiningSlotInterval() {
		return diningSlotInterval;
	}

	public void setDiningSlotInterval(Integer diningSlotInterval) {
		this.diningSlotInterval = diningSlotInterval;
	}

	public Boolean getUpfrontPayment() {
		return upfrontPayment;
	}

	public void setUpfrontPayment(Boolean upfrontPayment) {
		this.upfrontPayment = upfrontPayment;
	}

	public Integer getUpfrontAmount() {
		return upfrontAmount;
	}

	public void setUpfrontAmount(Integer upfrontAmount) {
		this.upfrontAmount = upfrontAmount;
	}

	public Double getCancellationBlockTime() {
		return cancellationBlockTime;
	}

	public void setCancellationBlockTime(Double cancellationBlockTime) {
		this.cancellationBlockTime = cancellationBlockTime;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Integer getMinimumCapacity() {
		return minimumCapacity;
	}

	public void setMinimumCapacity(Integer minimumCapacity) {
		this.minimumCapacity = minimumCapacity;
	}

	public Integer getMaximumCapacity() {
		return maximumCapacity;
	}

	public void setMaximumCapacity(Integer maximumCapacity) {
		this.maximumCapacity = maximumCapacity;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getFoodSource() {
		return foodSource;
	}

	public void setFoodSource(String foodSource) {
		this.foodSource = foodSource;
	}

	public String getWasteDisposal() {
		return wasteDisposal;
	}

	public void setWasteDisposal(String wasteDisposal) {
			this.wasteDisposal = wasteDisposal;
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

	/**
	 * @return the tables
	 */
	/*
	 * public Collection<RestaurantHasTable> getTables() { return tables; }
	 * 
	 * public Collection<RestaurantHasNote> getNotes() { return notes; }
	 * 
	 * public void setNotes(Collection<RestaurantHasNote> notes) { this.notes =
	 * notes; }
	 *//**
	 * @param tables
	 *            the tables to set
	 */
	/*
	 * public void setTables(Collection<RestaurantHasTable> tables) {
	 * this.tables = tables; }
	 *//**
	 * @return the has_cuisine
	 */
	/*
	 * public Collection<RestaurantHasCuisineRelationshipModel> getHas_cuisine()
	 * { return has_cuisine; }
	 */

	/**
	 * @param has_cuisine
	 *            the has_cuisine to set
	 */
	/*
	 * public void
	 * setHas_cuisine(Collection<RestaurantHasCuisineRelationshipModel>
	 * has_cuisine) { this.has_cuisine = has_cuisine; }
	 */

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
			this.timezone = timezone;
	}

	public String getPreferredDateFormat() {
		return preferredDateFormat;
	}

	public void setPreferredDateFormat(String preferredDateFormat) {
			this.preferredDateFormat = preferredDateFormat;
	}

	public String getPreferredTimeFormat() {
		return preferredTimeFormat;
	}

	public void setPreferredTimeFormat(String preferredTimeFormat) {
			this.preferredTimeFormat = preferredTimeFormat;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
			this.currency = currency;
	}

	public String getTempratureScale() {
		return tempratureScale;
	}

	public void setTempratureScale(String tempratureScale) {
			this.tempratureScale = tempratureScale;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
			this.legalName = legalName;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}


	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getEmail() {
	    return email;
	}

	public void setEmail(String email) {
	    this.email = email;
	}

	public String getAccountId() {
	    return accountId;
	}

	public void setAccountId(String accountId) {
	    this.accountId = accountId;
	}

	public String getParentAccountId() {
	    return parentAccountId;
	}

	public void setParentAccountId(String parentAccountId) {
	    this.parentAccountId = parentAccountId;
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

	public Date getForcedShiftEndTime() {
		return forcedShiftEndTime == null ? null : (Date) forcedShiftEndTime.clone();
	}

	public void setForcedShiftEndTime(Date forcedShiftEndTime) {
		this.forcedShiftEndTime = forcedShiftEndTime== null ? null : (Date) forcedShiftEndTime.clone();
	}

	public Integer getBufferOpenTime() {
		return bufferOpenTime;
	}

	public void setBufferOpenTime(Integer bufferOpenTime) {
		this.bufferOpenTime = bufferOpenTime;
	}

	public static Set<String> getCustomFinderParams() {
		return customFinderParams;
	}

	public Boolean getBar() {
		return bar;
	}

	public void setBar(Boolean bar) {
		this.bar = bar;
	}

	


	
	
	

}
