package com.clicktable.model;

import java.util.Date;

import com.clicktable.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class GuestProfileCustomModel {

	private String firstName;
	// private String lastName;
	private String mobile;
	private String emailId;
	private Boolean isVip;
	private String reason;
	private String gender;
	private String guid;
	private String status;
	private boolean dnd_email;
	private boolean dnd_mobile;
	private boolean is_dnd_permanent;
	private boolean is_dnd_user_enable;
	private String corporate;
	private String isd_code;
	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone = Constants.TIMEZONE)
	private Date dob;
	@JsonFormat(pattern = Constants.DATE_FORMAT, timezone = Constants.TIMEZONE)
	private Date anniversary;
	private String salutation;
	@JsonFormat(pattern = Constants.TIMESTAMP_FORMAT, timezone = Constants.TIMEZONE)
	private Date firstSeatedTime;

	public GuestProfileCustomModel() {
		super();
	}

	public GuestProfileCustomModel(GuestProfile guest) {
		this.emailId = guest.getEmailId();
		this.firstName = guest.getFirstName();
		this.status = guest.getStatus();
		this.guid = guest.getGuid();
		this.isVip = guest.getIsVip();
		// this.lastName = guest.getLastName();
		this.mobile = guest.getMobile();
		this.reason = guest.getReason();
		this.corporate = guest.getCorporate();
		this.isd_code = guest.getIsd_code();
		this.dob = guest.getDob();
		this.anniversary = guest.getAnniversary();
		this.gender = guest.getGender();
		this.is_dnd_permanent=guest.isIs_dnd_permanent();
		this.is_dnd_user_enable=guest.isIs_dnd_user_enable();
		if (null != this.gender) {
			if (this.gender.equalsIgnoreCase(Constants.MALE))
				this.salutation = Constants.MALE_SALUTATION;
			else
				this.salutation = Constants.FEMALE_SALUTATION;
		}
		this.dnd_email = guest.isDnd_email();
		this.dnd_mobile = guest.isDnd_mobile();
		this.firstSeatedTime = guest.getFirstSeatedTime();

	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	/**
	 * @return the corporate_name
	 */
	public String getCorporate() {
		return corporate;
	}

	/**
	 * @param corporate_name
	 *            the corporate_name to set
	 */
	public void setCorporate_name(String corporate) {
		this.corporate = corporate;
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
	 * @return the dob
	 */
	public Date getDob() {
		return dob;
	}

	/**
	 * @param dob
	 *            the dob to set
	 */
	public void setDob(Date dob) {
		this.dob = dob;
	}

	/**
	 * @return the anniversary
	 */
	public Date getAnniversary() {
		return anniversary;
	}

	/**
	 * @param anniversary
	 *            the anniversary to set
	 */
	public void setAnniversary(Date anniversary) {
		this.anniversary = anniversary;
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

	
	
	
}
