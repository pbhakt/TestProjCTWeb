package com.clicktable.model;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.csvreader.CsvReader;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.stormpath.sdk.account.Account;

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@TypeAlias("GuestProfile")
@JsonInclude(Include.NON_NULL)
public class GuestProfile extends Entity {

	private static final long serialVersionUID = 6002272385970405201L;

	@Transient
	@JsonIgnore
	public final static String NO_GROUP = "Basic";

	/*@GraphProperty(propertyName = "stormpath_id")
	private String stormpathId;*/
	@MinLength(message = ErrorCodes.GUEST_FIRST_NAME_MIN_LENGTH, value = 1)
	@MaxLength(message = ErrorCodes.GUEST_FIRST_NAME_MAX_LENGTH, value = 60)
	@Required(message = ErrorCodes.GUEST_FIRST_NAME)
	@GraphProperty(propertyName = "first_name")
	private String firstName;
	/*@MinLength(message = ErrorCodes.GUEST_LAST_NAME_MIN_LENGTH, value = 1)
	@MaxLength(message = ErrorCodes.GUEST_LAST_NAME_MAX_LENGTH, value = 50)
	@GraphProperty(propertyName = "last_name")
	private String lastName;*/
	@MinLength(message = ErrorCodes.GUEST_MOBILE_MIN_LENGTH, value = 10)
	@MaxLength(message = ErrorCodes.GUEST_MOBILE_MAX_LENGTH, value = 10)
	private String mobile;

	@Email(message = ErrorCodes.INVALID_EMAIL_FORMAT)
	@MaxLength(message = ErrorCodes.GUEST_EMAIL_MAX_LENGTH, value = 100)
	@GraphProperty(propertyName = "email_id")
	private String emailId;
	@MaxLength(message = ErrorCodes.GUEST_ADD_MAX_LENGTH, value = 500)
	private String address;
	private Integer pincode;
	@MaxLength(message = ErrorCodes.GUEST_COUNTRY_CODE_MAX_LENGTH, value = 5)
	@GraphProperty(propertyName = "country_cd")
	private String countryCode;
	@MaxLength(message = ErrorCodes.GUEST_STATE_CODE_MAX_LENGTH, value = 60)
	private String state;
	@MaxLength(message = ErrorCodes.GUEST_CITY_CODE_MAX_LENGTH, value = 60)
	private String city;
	@GraphProperty(propertyName = "total_points")
	private Integer totalPoints;
	@GraphProperty(propertyName = "available_points")
	private Integer availablePoints;
	@GraphProperty(propertyName = "redeemed_points")
	private Integer redeemedPoints;

	@MaxLength(message = ErrorCodes.GUEST_PHOTO_URL_MAX_LENGTH, value = 300)
	private String photoUrl;
	@Required(message = ErrorCodes.GUEST_IS_VIP)
	@GraphProperty(propertyName = "is_vip")
	private Boolean isVip;
	private String reason;

	//@Required(message = ErrorCodes.GUEST_GENDER)
	private String gender;

	@Transient
	//@Required(message = ErrorCodes.GUEST_RESTID)
	private String restGuid;

	@Transient
	@JsonIgnore
	private String password; // used during password set/reset only. Do NOT
								// store users' passwords - let Stormpath do
								// that safely for you.

	@Transient
	@JsonIgnore
	private String sptoken; // used during password reset only.

	@Transient
	@JsonIgnore
	private String groupUrl;

	@Transient
	@JsonIgnore
	private Account account;

	@GraphProperty(propertyName = "dummy")
	private boolean isDummy;

	@GraphProperty(propertyName = "guest_type")
	private String guestType;

	@GraphProperty(propertyName = "search_params")
	private String searchParams;

	/* Changes With new Guest Book Release */
	@GraphProperty(propertyName = "fid")
	private String fid;

	@GraphProperty(propertyName = "gid")
	private String gid;

	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "dob")
	private Date dob;

	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "anniversary")
	private Date anniversary;

	@GraphProperty(propertyName = "dnd_mobile")
	private boolean dnd_mobile;
	@GraphProperty(propertyName = "is_dnd_trai_enable")
	private boolean is_dnd_permanent;
	@GraphProperty(propertyName = "is_dnd_user_enable")
	private boolean is_dnd_user_enable;

	@GraphProperty(propertyName = "dnd_email")
	private boolean dnd_email;

	@GraphProperty(propertyName = "corporate_guid")
	private String corporate;

	@GraphProperty(propertyName = "isd_code")
	private String isd_code;

	@GraphProperty(propertyName = "is_mobile_verified")
	private boolean mobile_verified;

	private String review_count;

	private String cumulative_rating;

	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	@GraphProperty(propertyName = "last_login")
	private Date last_login;

	private String salutation;

	private String status;
	

	private String otpToken;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT,timezone=Constants.TIMEZONE)
	@GraphProperty(propertyName = "otp_generated_time")
	private Date otp_generated_time= new Timestamp(new Date().getTime());
	
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
    private Date firstSeatedTime ;
	
	@GraphProperty(propertyName = "corporate_name")
	private String corporateName;
	
	
	public GuestProfile() {

	}

	public GuestProfile(GuestProfile customer) {
		if (customer != null) {
			setAccount(customer.getAccount());
			setEmailId(customer.getEmailId());
			setFirstName(customer.getFirstName());
			setGuid(customer.getGuid());
			//setLastName(customer.getLastName());
			setPassword(customer.getPassword());
			setFid(customer.getFid());
			setGid(customer.getGid());
		}
	}

	public GuestProfile(Account account, String socialID, String socialAccount) {
		if (account != null) {
			setEmailId(account.getEmail());
			setFirstName(account.getGivenName());
			//setLastName(account.getSurname());
			setAccount(account);
			String href = account.getHref();
			String[] subStr = href.split("/");
			//setStormpathId(subStr[subStr.length - 1]);
			
			/* Set Facebook ID Or Google ID */
			if (socialAccount.equalsIgnoreCase(Constants.FACEBOOK)) {
				setFid(subStr[subStr.length - 1]);
			} else if (socialAccount.equalsIgnoreCase(Constants.GOOGLE)) {
				setGid(subStr[subStr.length - 1]);
			}

		}
	}

	public GuestProfile(CsvReader reader) {
		try {
			setEmailId(reader.get("emailId"));

			// setAccount(customer.getAccount());
			setFirstName(reader.get("firstName"));

			//setLastName(reader.get("lastName"));

			setGender(reader.get("gender"));

			// setAddress(reader.get("address"));

/*			try {
				if (!reader.get("anniversary").equals("")) {
					SimpleDateFormat sdf = new SimpleDateFormat(
							Constants.CSV_DATE_FORMAT);
					Date csvAnniversary = sdf.parse(reader.get("anniversary"));
					sdf.applyPattern(Constants.DATE_FORMAT);
					Date anniversary = sdf.parse(sdf.format(csvAnniversary));
					setAnniversary(anniversary);
				}
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/
			// setCity(reader.get("city"));
			// setCountryCode(reader.get("countryCode"));

			/*try {
				if (!reader.get("dob").equals("")) {
					SimpleDateFormat sdf = new SimpleDateFormat(
							Constants.CSV_DATE_FORMAT);
					Date csvDob = sdf.parse(reader.get("dob"));
					sdf.applyPattern(Constants.DATE_FORMAT);
					Date dob = sdf.parse(sdf.format(csvDob));
					setDob(dob);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			if (!reader.get("vipCategory").equals("")
					&& reader.get("vipCategory") != null) {
				setIsVip(true);
				setReason(reader.get("vipCategory"));
			} else {
				setIsVip(false);
			}
			if (!reader.get("isdCode").equals("")
					&& reader.get("isdCode") != null) {
				setIsd_code(reader.get("isdCode"));
			} else {
				setIsd_code("91");
			}
			//
			setMobile(reader.get("mobile"));
			// setPincode(Integer.parseInt(reader.get("pincode")));

			// setState(reader.get("state"));

			// setPassword(customer.getPassword());
			// setStormpathId(customer.getStormpathId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Return the email associated with this user. If available, use the
	 * Stormpath SDK Account value, otherwise return the local property. We do
	 * this because the user class is a form backing bean for authentication,
	 * profile updates, etc.
	 *
	 * @return the email from the Stormpath SDK Account object if the account is
	 *         not null, otherwise return the underlying property
	 */
	public String getEmailId() {
		return account != null ? account.getEmail() : emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId == null ? null : emailId.trim();
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSptoken() {
		return sptoken;
	}

	public void setSptoken(String sptoken) {
		this.sptoken = sptoken;
	}

	/**
	 * Return the first name associated with this user. If available, use the
	 * Stormpath SDK Account value, otherwise return the local property. We do
	 * this because the user class is a form backing bean for authentication,
	 * profile updates, etc.
	 *
	 * @return the first name from the Stormpath SDK Account object if the
	 *         account is not null, otherwise return the underlying property
	 */
	public String getFirstName() {
		return account != null ? account.getGivenName() : firstName;
	}

	public void setFirstName(String firstName) {

		this.firstName = firstName == null ? null : firstName.trim();
		if (account != null) {
			account.setGivenName(this.firstName);
		}
	}

	/**
	 * Return the last name associated with this user. If available, use the
	 * Stormpath SDK Account value, otherwise return the local property. We do
	 * this because the user class is a form backing bean for authentication,
	 * profile updates, etc.
	 *
	 * @return the last name from the Stormpath SDK Account object if the
	 *         account is not null, otherwise return the underlying property
	 */
	/*public String getLastName() {
		return account != null ? account.getSurname() : lastName;
	}

	public void setLastName(String lastName) {

		this.lastName = lastName == null ? null : lastName.trim();
		if (account != null) {
			this.account.setSurname(this.lastName);
		}
	}*/

	@JsonIgnore
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Return the name of the group for this user.
	 *
	 * @return the name of the group associated with this user, or "Basic" if
	 *         the user is not assigned to any groups in Stormpath
	 */
	@JsonIgnore
	public String getGroupName() {
		if (this.account != null
				&& this.account.getGroupMemberships().iterator().hasNext()) {
			return this.account.getGroupMemberships().iterator().next()
					.getGroup().getName();
		} else {
			return NO_GROUP;
		}
	}

	@JsonIgnore
	public String getGroupUrl() {
		if (this.account != null
				&& this.account.getGroupMemberships().iterator().hasNext()) {
			return this.account.getGroupMemberships().iterator().next()
					.getGroup().getHref();
		} else {
			return groupUrl;
		}
	}

	public void setGroupUrl(String groupUrl) {
		this.groupUrl = groupUrl;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile == null ? null : mobile.trim();
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
		this.anniversary = anniversary == null ? null : (Date) anniversary
				.clone();
	}

	/*
	 * public Date getLastLogin() { return lastLogin; }
	 * 
	 * public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }
	 */

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address == null ? null : address.trim();
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode == null ? null : countryCode.trim();
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state == null ? null : state.trim();
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city == null ? null : city.trim();
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
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
		this.reason = reason == null ? null : reason.trim();
	}

	public String getRestGuid() {
		return restGuid;
	}

	public void setRestGuid(String restGuid) {
		this.restGuid = restGuid == null ? null : restGuid.trim();
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender == null ? null : gender.trim();
	}

	public boolean isDummy() {
		return isDummy;
	}

	public void setDummy(boolean isDummy) {
		this.isDummy = isDummy;
	}

	/**
	 * @return the guestType
	 */
	public String getGuestType() {
		return guestType;
	}

	/**
	 * @param guestType
	 *            the guestType to set
	 */
	public void setGuestType(String guestType) {
		this.guestType = guestType;
	}

	

	/**
	 * @return the dnd_mobile
	 */
	public boolean isDnd_mobile() {
		return dnd_mobile;
	}

	/**
	 * @param dnd_mobile
	 *            the dnd_mobile to set
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
	 * @param dnd_email
	 *            the dnd_email to set
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
	 * @param last_login
	 *            the last_login to set
	 */
	public void setLast_login(Date last_login) {
		this.last_login = last_login;
	}

	/**
	 * @return the salutation
	 */
	public String getSalutation() {
		return salutation;
	}

	/**
	 * @param salutation
	 *            the salutation to set
	 */
	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	/**
	 * @return the isd_code
	 */
	public String getIsd_code() {
		return isd_code;
	}

	/**
	 * @param isd_code
	 *            the isd_code to set
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
	 * @param review_count
	 *            the review_count to set
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
	 * @param cumulative_rating
	 *            the cumulative_rating to set
	 */
	public void setCumulative_rating(String cumulative_rating) {
		this.cumulative_rating = cumulative_rating;
	}

	/**
	 * @return the is_mobile_verified
	 */
	public boolean isIs_mobile_verified() {
		return mobile_verified;
	}

	/**
	 * @param is_mobile_verified
	 *            the is_mobile_verified to set
	 */
	public void setIs_mobile_verified(boolean is_mobile_verified) {
		this.mobile_verified = is_mobile_verified;
	}


	/**
	 * @return the corporate
	 */
	public String getCorporate() {
		return corporate;
	}

	/**
	 * @param corporate the corporate to set
	 */
	public void setCorporate(String corporate) {
		this.corporate = corporate;
	}

	/**
	 * @return the searchParams
	 */
	public String getSearchParams() {
		return searchParams;
	}

	/**
	 * @param searchParams
	 *            the searchParams to set
	 */
	public void setSearchParams(String searchParams) {
		this.searchParams = searchParams;
	}

	/**
	 * @return the otpToken
	 */
	public String getOtpToken() {
		return otpToken;
	}

	/**
	 * @param otpToken the otpToken to set
	 */
	public void setOtpToken(String otpToken) {
		this.otpToken = otpToken;
	}

	/**
	 * @return the otp_generated_time
	 */
	public Date getOtp_generated_time() {
		return otp_generated_time;
	}

	/**
	 * @param otp_generated_time the otp_generated_time to set
	 */
	public void setOtp_generated_time(Date otp_generated_time) {
		this.otp_generated_time = otp_generated_time;
	}

	/**
	 * @return the fid
	 */
	public String getFid() {
		return fid;
	}

	/**
	 * @param fid the fid to set
	 */
	public void setFid(String fid) {
		this.fid = fid;
	}

	/**
	 * @return the gid
	 */
	public String getGid() {
		return gid;
	}

	/**
	 * @param gid the gid to set
	 */
	public void setGid(String gid) {
		this.gid = gid;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getCorporateName() {
		return corporateName;
	}

	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
	}
	
	
	
	

}

