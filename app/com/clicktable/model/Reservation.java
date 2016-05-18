package com.clicktable.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@TypeAlias("Reservation")
@JsonInclude(Include.NON_NULL)
public class Reservation extends Entity implements Cloneable ,Comparable<Reservation> {

	private static final long serialVersionUID = 2877641618882922694L;

	// @Required(message=ErrorCodes.RESERVATION_REST_GUID)
	@GraphProperty(propertyName = "rest_guid")
	private String restaurantGuid;

	@GraphProperty(propertyName = "short_id")
	private String shortId;

	// @Required(message=ErrorCodes.RESERVATION_GUEST_GUID)
	@GraphProperty(propertyName = "guest_guid")
	private String guestGuid;

	@GraphProperty(propertyName = "table_guid")
	public List<String> tableGuid = new ArrayList<String>();

	@GraphProperty(propertyName = "reservation_status")
	private String reservationStatus;

	@GraphProperty(propertyName = "customer_remark")
	private String customerRemark;

	@Required(message = ErrorCodes.RESERVATION_BOOKING_MODE)
	@GraphProperty(propertyName = "booking_mode")
	private String bookingMode;

	// /@Required(message=ErrorCodes.RESERVATION_BOOKING_BY)
	@GraphProperty(propertyName = "booked_by")
	private String bookedBy;

	@GraphProperty(propertyName = "booked_by_id")
	private String bookedById;

	@Required(message = ErrorCodes.RESERVATION_COVERS)
	@GraphProperty(propertyName = "num_covers")
	private Integer numCovers;

	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "reservation_time")
	private Date reservationTime = new Timestamp(new Date().getTime());

	@Required(message = ErrorCodes.RESERVATION_EST_START_TIME)
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "est_start_time")
	private Date estStartTime;

	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "est_end_time")
	private Date estEndTime;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "act_start_time")
	private Date actStartTime;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "act_end_time")
	private Date actEndTime;

	@GraphProperty(propertyName = "cancelled_by_id")
	private String cancelledById;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "cancel_time")
	private Date cancelTime;

	@GraphProperty(propertyName = "reason_to_cancel")
	private String reasonToCancel;

	@GraphProperty(propertyName = "cancelled_by")
	private String cancelledBy;
	@GraphProperty(propertyName = "tat")
	private String tat;
	@GraphProperty(propertyName = "quoted_time")
	private String quotedTime;

	@GraphProperty(propertyName = "notes")
	private String reservationNote;

	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	private Date currentServerTime;

	@GraphProperty(propertyName = "preffered_section")
	private String prefferedSection;

	@GraphProperty(propertyName = "preffered_table_type")
	private String prefferedTableType;

	@GraphProperty(propertyName = "preferred_table")
	private String preferredTable;

	@GraphProperty(propertyName = "server_guids")
	private String serverGuids;

	@GraphProperty(propertyName = "server_names")
	private String serverNames;

	@GraphProperty(propertyName = "status_updated_time")
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	private Date statusUpdatedTime;

	private String estWaitingTime;

	private List<ReservationHistory> history;
	private String guest_firstName;
	private String guest_lastName;
	private String guest_mobile;
	private String guest_email;
	private String guest_isd_code;
	private String isVIP;
	private String total_guest_visit;
	private String reason;
	private String gender;
    private int cumulative_rating;
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
    private Date firstSeatedTime ;
    @GraphProperty(propertyName = "source")
	private String source;
    @GraphProperty(propertyName = "offer_id")
   	private String offerId;
    
    @GraphProperty(propertyName = "offer_name")
   	private String offerName;
	

	private String conversation = "";

	@JsonIgnore
	private Boolean toBypass;

	private boolean queued = false;
	
	private Boolean isUpdated = true;
	
	private String corporateName;
	
	private String corporateGuid;
	
	private Integer barCount;

	
	/**
	 * @return the isUnknown
	 */
	public Boolean getIsUnknown() {
		return isUnknown;
	}

	/**
	 * @param isUnknown
	 *            the isUnknown to set
	 */
/*	public void setUnknown(Boolean isUnknown) {
		this.isUnknown = isUnknown;
	}
	
	public Boolean isUnknown() {
		return isUnknown;
	}
	*/
	public void setIsUnknown(Boolean isUnknown) {
		this.isUnknown = isUnknown;
	}

	@GraphProperty(propertyName = "is_unknown")
	private Boolean isUnknown;

	/* private List<Server> server; */

	private static String[] customFinderParams = { Constants.EST_START_BETWEEN, 
			Constants.EST_END_BETWEEN,  Constants.ACT_START_BETWEEN, 
			Constants.ACT_END_BETWEEN,  Constants.RESERVED_BETWEEN, 
			Constants.CANCELLED_BETWEEN, Constants.COVERS_LESS_THAN, Constants.COVERS_MORE_THAN, Constants.FREE_SEARCH };

	@GraphProperty(propertyName = "gupshup_extension")
	private String gupshupExtension;

	private String blockResv = "RESERVATION";

	
	public String getBlockResv() {
	    return blockResv;
	}

	public void setBlockResv(String blockResv) {
	    this.blockResv = blockResv;
	}

	public Reservation() {
		super();
	}
	
	/*public Reservation(Map<String, Object> resvMap){
		for(Map.Entry<String, Object> entry : resvMap.entrySet())
		{
			try {
				PropertyUtils.setSimpleProperty(this, entry.getKey(), entry.getValue());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/

	public Reservation(Long estStartTime, Long estEndTime, Long actStartTime, Long actEndTime, String tat, String blockResv, String blockReason) {
		if (actEndTime != null) {
			this.actEndTime = new Date(actEndTime);
		}
		if (actStartTime != null) {
			this.actStartTime = new Date(actStartTime);
		}
		this.estEndTime = new Date(estEndTime);
		this.estStartTime = new Date(estStartTime);
		this.tat = tat;
		if(blockResv != null)
		{
		this.blockResv = blockResv;
		}
		
		if(blockReason != null)
		{
		this.reasonToCancel = blockReason;
		}
	}

	public String getCustomerRemark() {
		return customerRemark;
	}

	public void setCustomerRemark(String customerRemark) {
		this.customerRemark = customerRemark;
	}

	public Integer getNumCovers() {
		return numCovers;
	}

	public void setNumCovers(Integer numCovers) {
		this.numCovers = numCovers;
	}

	public String getShortId() {
		return shortId;
	}

	public void setShortId(String shortId) {
		this.shortId = shortId;
	}

	public String getRestaurantGuid() {
		return restaurantGuid;
	}

	public void setRestaurantGuid(String restGuid) {
		this.restaurantGuid = restGuid;
	}

	public String getGuestGuid() {
		return guestGuid;
	}

	public void setGuestGuid(String guestGuid) {
		this.guestGuid = guestGuid;
	}

	public List<String> getTableGuid() {
		return processTableGuid(tableGuid);
	}

	public void setTableGuid(List<String> tableGuid) {
		this.tableGuid = tableGuid;
	}

	public String getReservationStatus() {
		return reservationStatus;
	}

	public void setReservationStatus(String reservationStatus) {
		this.reservationStatus = reservationStatus;
	}

	public String getBookedById() {
		return bookedById;
	}

	public void setBookedById(String bookedById) {
		this.bookedById = bookedById;
	}
	
	public Date getReservationTime() {
		return reservationTime == null ? null : (Date) reservationTime.clone();
	}

	public void setReservationTime(Date reservationTime) {
		this.reservationTime = reservationTime == null ? null : (Date) reservationTime.clone();
	}

	public Date getEstStartTime() {
		return estStartTime == null ? null : (Date) estStartTime.clone();
	}

	public void setEstStartTime(Date estStartTime) {
		this.estStartTime = estStartTime == null ? null : (Date) estStartTime.clone();
	}

	public Date getEstEndTime() {
		return estEndTime == null ? null : (Date) estEndTime.clone();
	}

	public void setEstEndTime(Date estEndTime) {
		this.estEndTime = estEndTime == null ? null : (Date) estEndTime.clone();
	}

	public Date getActStartTime() {
		return actStartTime == null ? null : (Date) actStartTime.clone();
	}

	public void setActStartTime(Date actStartTime) {
		this.actStartTime = actStartTime == null ? null : (Date) actStartTime.clone();
	}

	public Date getActEndTime() {
		return actEndTime == null ? null : (Date) actEndTime.clone();
	}

	public void setActEndTime(Date actEndTime) {
		this.actEndTime = actEndTime == null ? null : (Date) actEndTime.clone();
	}

	public String getCancelledById() {
		return cancelledById;
	}

	public void setCancelledById(String cancelledById) {
		this.cancelledById = cancelledById;
	}

	public Date getCancelTime() {
		return cancelTime == null ? null : (Date) cancelTime.clone();
	}

	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime == null ? null : (Date) cancelTime.clone();
	}

	public String getReasonToCancel() {
		return reasonToCancel;
	}

	public void setReasonToCancel(String reasonToCancel) {
		this.reasonToCancel = reasonToCancel;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public String getBookingMode() {
		return bookingMode;
	}

	public void setBookingMode(String bookingMode) {
		this.bookingMode = bookingMode;
	}

	public String getBookedBy() {
		return bookedBy;
	}

	public void setBookedBy(String bookedBy) {
		this.bookedBy = bookedBy;
	}

	public static String[] getCustomFinderParams() {
		return customFinderParams.clone();
	}

	/**
	 * @return the history
	 */
	public List<ReservationHistory> getHistory() {
		return history;
	}

	/**
	 * @param history
	 *            the history to set
	 */
	public void setHistory(List<ReservationHistory> history) {
		this.history = history;
	}

	@Override
	public void setInfoOnCreate(UserInfoModel userInfo) {
		super.setInfoOnCreate(userInfo);
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
			if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
				this.setBookedBy(Constants.CUSTOMER_ENUM);
			} else {
				this.setRestaurantGuid(userInfo.getRestGuid());
				this.setBookedBy(Constants.RESTAURANT_ENUM);
			}
		}
		this.setBookedById(userInfo.getGuid());
		if ((reservationStatus != null) && (reservationStatus.equals(Constants.CANCELLED))) {
			this.setCancelledBy(userInfo.getUserType());
			this.setCancelledById(userInfo.getGuid());
			this.setCancelTime(new Timestamp(new Date().getTime()));
		}

	}

	@Override
	public void setInfoOnUpdate(UserInfoModel userInfo) {

		this.setUpdatedBy(userInfo.getGuid());
		
		if ((reservationStatus != null) && (reservationStatus.equals(Constants.CANCELLED))) {
			this.setCancelledBy(userInfo.getUserType());
			this.setCancelledById(userInfo.getGuid());
			this.setCancelTime(new Timestamp(new Date().getTime()));
		}

	}

	/**
	 * @return the guest_firstName
	 */
	public String getGuest_firstName() {
		return guest_firstName;
	}

	/**
	 * @param guest_firstName
	 *            the guest_firstName to set
	 */
	public void setGuest_firstName(String guest_firstName) {
		this.guest_firstName = guest_firstName;
	}

	/**
	 * @return the guest_lastName
	 */
	public String getGuest_lastName() {
		return guest_lastName;
	}

	/**
	 * @param guest_lastName
	 *            the guest_lastName to set
	 */
	public void setGuest_lastName(String guest_lastName) {
		this.guest_lastName = guest_lastName;
	}

	/**
	 * @return the guest_mobile
	 */
	public String getGuest_mobile() {
		return guest_mobile;
	}

	/**
	 * @param guest_mobile
	 *            the guest_mobile to set
	 */
	public void setGuest_mobile(String guest_mobile) {
		this.guest_mobile = guest_mobile;
	}

	/**
	 * @return the resvNotes
	 */
	/*
	 * public String getResvNotes() { return resvNotes; }
	 *//**
	 * @param resvNotes
	 *            the resvNotes to set
	 */
	/*
	 * public void setResvNotes(String resvNotes) { this.resvNotes = resvNotes;
	 * }
	 */

	/**
	 * @return the tat
	 */
	public String getTat() {
		return tat;
	}

	/**
	 * @param tat
	 *            the tat to set
	 */
	public void setTat(String tat) {
		this.tat = tat;
	}

	/**
	 * @return the guest_email
	 */
	public String getGuest_email() {
		return guest_email;
	}

	/**
	 * @param guest_email
	 *            the guest_email to set
	 */
	public void setGuest_email(String guest_email) {
		this.guest_email = guest_email;
	}

	/**
	 * @return the server
	 */
	/*
	 * public List<Server> getServer() { return server; }
	 *//**
	 * @param server
	 *            the server to set
	 */
	/*
	 * public void setServer(List<Server> server) { this.server = server; }
	 */
	/**
	 * @return the isVIP
	 */
	public String getIsVIP() {
		return isVIP;
	}

	/**
	 * @param isVIP
	 *            the isVIP to set
	 */
	public void setIsVIP(String isVIP) {
		this.isVIP = isVIP;
	}

	/**
	 * @return the total_guest_visit
	 */
	public String getTotal_guest_visit() {
		return total_guest_visit;
	}

	/**
	 * @param total_guest_visit
	 *            the total_guest_visit to set
	 */
	public void setTotal_guest_visit(String total_guest_visit) {
		this.total_guest_visit = total_guest_visit;
	}

	/**
	 * @return the quotedTime
	 */
	public String getQuotedTime() {
		return quotedTime;
	}

	/**
	 * @param quotedTime
	 *            the quotedTime to set
	 */
	public void setQuotedTime(String quotedTime) {
		this.quotedTime = quotedTime;
	}

	/**
	 * @return the currentServerTime
	 */
	public Date getCurrentServerTime() {
		return currentServerTime == null ? null : (Date) currentServerTime.clone();
	}

	/**
	 * @param currentServerTime
	 *            the currentServerTime to set
	 */
	public void setCurrentServerTime(Date currentServerTime) {
		this.currentServerTime = currentServerTime == null ? null : (Date) currentServerTime.clone();
	}

	/**
	 * @return the reservationNote
	 */
	public String getReservationNote() {
		return reservationNote;
	}

	/**
	 * @param reservationNote
	 *            the reservationNote to set
	 */
	public void setReservationNote(String reservationNote) {
		this.reservationNote = reservationNote;
	}

	public String getConversation() {
		return conversation;
	}

	public void setConversation(String conversation) {
		this.conversation = conversation;
	}

	public String getPrefferedSection() {
		return prefferedSection;
	}

	public void setPrefferedSection(String prefferedSection) {
		this.prefferedSection = prefferedSection;
	}

	public String getPrefferedTableType() {
		return prefferedTableType;
	}

	public void setPrefferedTableType(String prefferedTableType) {
		this.prefferedTableType = prefferedTableType;
	}

	public String getPreferredTable() {
		return preferredTable;
	}

	public void setPreferredTable(String preferredTable) {
		this.preferredTable = preferredTable;
	}

	public String getServerGuids() {
		return serverGuids;
	}

	public void setServerGuids(String serverGuids) {
		this.serverGuids = serverGuids;
	}

	public String getServerNames() {
		return serverNames;
	}

	public void setServerNames(String serverNames) {
		this.serverNames = serverNames;
	}

	/**
	 * @return the estWaitingTime
	 */
	public String getEstWaitingTime() {
		return estWaitingTime;
	}

	/**
	 * @param estWaitingTime
	 *            the estWaitingTime to set
	 */
	public void setEstWaitingTime(String estWaitingTime) {
		this.estWaitingTime = estWaitingTime;
	}

	private List<String> processTableGuid(List<String> guids) {
		List<String> list = new ArrayList<String>();
		for (String str : guids) {
			list.add(str.replaceAll("\\[", "").replaceAll("\\]", ""));
		}
		return list;
	}

	public Boolean getToBypass() {
		return toBypass;
	}

	public void setToBypass(Boolean toBypass) {
		this.toBypass = toBypass;
	}

	/**
	 * @return the statusUpdatedTime
	 */
	public Date getStatusUpdatedTime() {
		return statusUpdatedTime == null ? null : (Date) statusUpdatedTime.clone();
	}

	/**
	 * @param statusUpdatedTime
	 *            the statusUpdatedTime to set
	 */
	public void setStatusUpdatedTime(Date statusUpdatedTime) {
		this.statusUpdatedTime = statusUpdatedTime == null ? null : (Date) statusUpdatedTime.clone();
	}

	public String getGupshupExtension() {
		return gupshupExtension;
	}

	public void setGupshupExtension(String gupshupExtension) {
		this.gupshupExtension = gupshupExtension;
	}

	public boolean isQueued() {
		return queued;
	}

	public void setQueued(boolean queued) {
		this.queued = queued;
	}

	/**
	 * @return the cummulative_rating
	 */
	public int getCumulative_rating() {
		return cumulative_rating;
	}

	/**
	 * @param cummulative_rating the cummulative_rating to set
	 */
	public void setCumulative_rating(int cumulative_rating) {
		this.cumulative_rating = cumulative_rating;
	}


	public Boolean getIsUpdated() {
		return isUpdated;
	}

	public void setIsUpdated(Boolean isUpdated) {
		this.isUpdated = isUpdated;
	}
	
	
	
	
	
	
	
	 public Date getFirstSeatedTime() {
		return firstSeatedTime;
	}

	public void setFirstSeatedTime(Date firstSeatedTime) {
		this.firstSeatedTime = firstSeatedTime;
	}
	
	
	

	public String getGuest_isd_code() {
		return guest_isd_code;
	}

	public void setGuest_isd_code(String guest_isd_code) {
		this.guest_isd_code = guest_isd_code;
	}

	public Object clone() throws CloneNotSupportedException {
		 /*
		 Employee copyObj = new Employee();
		 copyObj.setDesignation(this.designation);
		 copyObj.setName(this.name);
		 return copyObj;
		 */
		 return super.clone();
		 }

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}


	@Override
	public int compareTo(Reservation reservation) {
		
		// TODO Auto-generated method stub
		return this.getEstStartTime().compareTo(reservation.getEstStartTime());
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getOfferName() {
		return offerName;
	}

	public void setOfferName(String offerName) {
		this.offerName = offerName;
	}

	public String getCorporateName() {
		return corporateName;
	}

	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCorporateGuid() {
		return corporateGuid;
	}

	public void setCorporateGuid(String corporateGuid) {
		this.corporateGuid = corporateGuid;
	}

	public Integer getBarCount() {
		return barCount;
	}

	public void setBarCount(Integer barCount) {
		this.barCount = barCount;
	}




	 
	 
}
