package com.clicktable.util;

public class Constants {
	public static final String RESERVATION_OVERLAP_TIME = "reservationOverlapTime";

	public static final Integer SUCCESSFULLY_AUTHENTICATED = 1;
	public static final Integer INVALID_USERNAME = 2;
	public static final Integer WRONG_PASSWORD = 3;
	public static final Integer INVALID_ACCOUNT = 4;
	public static final String INVALID_USERNAME_MSG = "username or email";
	public static final String WRONG_PASSWORD_MSG = "password is incorrect";

	public static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	public static final String PAGE_NO = "pageNo";
	public static final String PAGE_SIZE = "pageSize";
	public static final String LIKE = "Like";
	public static final String WHERE = "WHERE";
	public static final String AND = "AND";
	public static final String PRE_LIKE_STRING = "(?i).*";
	public static final String PRE_START_WITH_STRING = "(?i)";
	public static final String POST_LIKE_STRING = ".*";
	public static final String CREATED_BEFORE = "createdBefore";
	public static final String CREATED_AFTER = "createdAfter";
	public static final String CREATED_ON = "createdOn";
	public static final String CREATED_DATE = "createdDate";
	public static final String UPDATED_BEFORE = "updatedBefore";
	public static final String UPDATED_AFTER = "updatedAfter";
	public static final String UPDATED_ON = "updatedOn";
	public static final String UPDATED_DATE = "updatedDate";
	public static final String UPDATED_DT = "updated_dt";
	public static final String CREATED_DT = "created_dt";

	public static final String ORDER_BY = "orderBy";
	public static final String ORDER_PREFERENCE = "orderPreference";
	public static final String START_WITH = "startWith";
	public static final String NAME_STARTS_WITH = "nameStartsWith";
	public static final String FIRST_NAME_STARTS_WITH = "firstNameStartsWith";
	public static final String LAST_NAME_STARTS_WITH = "lastNameStartsWith";
	public static final String FIRST_NAME_LIKE = "firstNameLike";
	public static final String LAST_NAME_LIKE = "lastNameLike";
	public static final String EMAIL_LIKE = "emailLike";
	public static final String MOBILE_NO_LIKE = "mobileNoLike";
	public static final String NAME_LIKE = "nameLike";
	public static final String DISPLAY_NAME_LIKE = "displayNameLike";
	public static final String TAGLINE_LIKE = "tagLineLike";
	public static final String FREE_SEARCH = "freeSearch";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String RESTAURANT_NAME = "restaurantName";
	public static final String QUERY = "query";
	public static final String ADDRESS = "address";
	public static final String ADDRESS_LINE_1 = "addressLine1";
	public static final String ADDRESS_LINE_2 = "addressLine2";
	public static final String CITY = "city";
	public static final String STATE = "STATE";
	public static final String LOCALITY = "LOCALITY";
	public static final String BUILDING = "BUILDING";
	public static final String COUNTRY = "COUNTRY";
	public static final String COUNTRY_CODE = "countryCode";
	public static final String ZIPCODE = "ZIPCODE";
	public static final String ZIP_CODE = "zipcode";

	public static final String TIME_FORMATTING = "HH:mm a";
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String EXT_TIMESTAMP_FORMAT = "E MMM dd HH:mm:ss zzz yyyy";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String CSV_DATE_FORMAT = "dd-MM-yyyy";
	public static final String DATE_FORMAT_STRING = "yyyyMMdd";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss z";
	public static final String TIME_FORMAT_CONFIGURATION = "HH:mm";

	public static final String NEO4J_PATH = "neo4j.path";
	public static final String ERRORS_FILE = "error.properties";
	public static final String ENUMS_FILE = "enum.properties";
	public static final String ENUMS_FILE_PATH = "conf/enum.properties";
	public static final String STORMPATH_FILE = "conf/application.conf";
	// public static final String STORMPATH_FILE = "conf/stormpath.properties";
	public static final String PERMISSION_FILE = "Permissions.json";
	public static final String BYPASS_REQ_FILE = "ByPassRequest.json";
	public static final String SESSION_CONFIG = "conf.properties";
	public static final String WALK_IN_CUSTOMER_DIRECTORY = "Walk-in Customers";
	public static final String STAFF_DIRECTORY = "Staff";
	public static final String STORMPATH_HREF_PATH_STRING = "https://api.stormpath.com/v1/accounts/";
	public static final String STORMPATH_HREF_APP_STRING = "https://api.stormpath.com/v1/applications/";
	public static final String CHECKDND_HREF_APP_STRING = "https://enterprise.smsgupshup.com/GatewayAPI/rest?v=1.1";

	public static final String DEFAULT_PCKG = "";
	public static final String DAO_INTF_PCKG = "com.clicktable.dao.intf";
	public static final String NEO_4J_REPO_PCKG = "com.clicktable.repository";
	public static final String DAO_IMPL_PCKG = "com.clicktable.dao.impl";
	public static final String SERVICE_INTF_PCKG = "com.clicktable.service.intf";
	public static final String SERVICE_IMPL_PCKG = "com.clicktable.service.impl";
	public static final String CONTROLLERS_PCKG = "com.clicktable.controllers";
	public static final String VALIDATE_PCKG = "com.clicktable.validate";
	public static final String MODEL_PCKG = "com.clicktable.model";
	public static final String WAITLIST_PCKG = "com.clicktable.controllers.WaitlistController";
	public static final String WAITLIST_METHOD = "addToWaitlist";
	public static final String REL_MODEL_PCKG = "com.clicktable.relationshipModel";
	public static final String SCHEDULER_PCKG = "com.clicktable.scheduler";
	public static final String DEFAULT = "default";
	public static final String TRANSACTION_MANAGER = "transactionManager";

	// Play.application().configuration().getString("your.key")

	public static final String GUEST_LABEL = "Guest";
	public static final String RESTAURANT_LABEL = "Restaurant";
	public static final String GUEST_CONVERSATION_LABEL = "GuestConversation";
	public static final String TEMPLATES = "templates";
	public static final String MESSAGE = "message";
	public static final String SENT_BY = "sentBy";
	public static final String TABLE_LABEL = "Table";
	public static final String NOTE_LABEL = "Note";
	public static final String SECTION_LABEL = "Section";
	public static final String COUNTRY_LABEL = "Country";
	public static final String DEVICE_LABEL = "Device";
	public static final String CITY_LABEL = "City";
	public static final String REGION_LABEL = "Region";
	public static final String LOCALITY_LABEL = "Locality";
	public static final String BUILDING_LABEL = "Building";

	public static final String RESTAURANT_MODULE = "restaurant";
	public static final String TABLE_MODULE = "table";
	public static final String CUSTOMER_MODULE = "customer";
	public static final String TAG_MODULE = "tag";
	public static final String GUEST_HAS_TAG_REL = "GUEST_HAS_TAG_REL";
	public static final String STORMPATH_MODULE = "stormpath";
	public static final String CLICKTABLE_MODULE = "clicktable";
	public static final String CLICKTABLE = "Clicktable";
	public static final String STAFF_MODULE = "staff";
	public static final String CUISINE_MODULE = "cuisine";
	public static final String ONBOARDING_MODULE = "onboarding";
	public static final String COMMON_MODULE = "common";
	public static final String SESSION_MODULE = "session";
	public static final String RESERVATION_MODULE = "reservation";
	public static final String GUEST_HAS_RESV = "GUEST_HAS_RESV";
	public static final Object SECTION_MODULE = "Section";

	public static final String REQUEST_STATUS = "requestStatus";
	public static final String LANG_CD = "languageCode";
	public static final String REGION = "region";
	public static final String STATUS = "status";
	public static final String NAME = "name";
	public static final String COLOR_CODE = "colorCode";
	public static final String REST_ID = "restId";
	public static final String RESTGUID = "restGuid";
	public static final String ROLE_ID = "roleId";
	public static final String DOB = "dob";
	public static final String ANNIVERSARY = "anniversary";
	public static final String CAPACITY = "capacity";
	public static final String MIN_CAPACITY = "minimum capacity";
	public static final String MAX_CAPACITY = "maximum capacity";
	public static final String SECTION_NAME = "Section Name";
	public static final String SECTION = "Section";
	public static final String SECTION_ID = "sectionId";
	public static final String NOTE = "note";

	public static final String ID = "id";
	public static final String GUID = "guid";
	public static final String REST_GUID = "restaurantGuid";
	public static final String COUNTRY_GUID = "countryGuid";
	public static final String RESERVATION_ID = "reservationId";
	public static final String CITY_NAME = "cityName";
	public static final String CITY_ZIPCODE = "cityZipcode";
	public static final String STATE_NAME = "stateName";
	public static final String COUNTRY_NAME = "countryName";
	public static final String LOCALITY_NAME = "localityName";
	public static final String REGION_NAME = "regionName";

	public static final String ATTR_GUID = "attributesGuid";
	public static final String SECTION_GUID = "sectionGuid";
	public static final String SERVER_GUID = "serverGuid";
	public static final String MIN_COVERS = "minCovers";
	public static final String MIN_COVERS_GREATER = "minCoversGreaterThan";
	public static final String MIN_COVERS_LESS = "minCoversLessThan";
	public static final String MIN_COVERS_GREATER_EQUAL = "minCoversGreaterThenAndEqualTo";
	public static final String MIN_COVERS_LESS_EQUAL = "minCoversLessThenAndEqualTo";
	public static final String MAX_COVERS = "maxCovers";
	public static final String MAX_COVERS_GREATER = "maxCoversGreaterThan";
	public static final String MAX_COVERS_LESS = "maxCoversLessThan";
	public static final String MAX_COVERS_GREATER_EQUAL = "maxCoversGreaterThenAndEqualTo";
	public static final String MAX_COVERS_LESS_EQUAL = "maxCoversLessThenAndEqualTo";

	public static final String USERNAME = "userName";
	public static final String PASSWORD = "password";
	public static final String OLD_PASSWORD = "oldPassword";
	public static final String NEW_PASSWORD = "newPassword";
	public static final String EMAIL = "email";
	public static final String MOBILE = "mobile";
	public static final String VIP_REASON = "reason";
	public static final String GENDER = "gender";
	public static final String DELETED_STATUS = "DELETED";
	public static final String ACTIVE_STATUS = "ACTIVE";
	public static final String INACTIVE_STATUS = "INACTIVE";
	public static final String SESSION_LIMIT = "sessionTimeOut";
	public static final String THREAD_RUNNIG_INTERVAL = "threadSchedule";

	public static final String ACCESS_TOKEN = "access_token";
	public static final String APPLICATION_JSON = "application/json";
	public static final String SP_TOKEN = "sptoken";
	public static final String TOKEN = "token";
	public static final String STAFF = "STAFF_";
	public static final String CUSTOMER = "CUST_";
	public static final String STAFF_STRING = "STAFF";
	public static final String CT_ADMIN_STRING = "CTADMIN";
	public static final String CUSTOMER_STRING = "CUST";
	public static final String CUSTOMER_TYPE = "CUSTOMER";
	public static final String STAFF_TYPE = "STAFF";
	public static final String CREATED_BY_NEO4J = "created_by";
	public static final String UPDATED_BY_NEO4J = "updated_by";
	public static final String CREATED_BY = "createdBy";
	public static final String UPDATED_BY = "updatedBy";
	public static final Integer USER_ID = 7000004;

	public static final Long CT_ADMIN_ROLE_ID = 1L;
	public static final Long ADMIN_ROLE_ID = 2L;
	public static final Long MANAGER_ROLE_ID = 3L;
	public static final Long STAFF_ROLE_ID = 4L;
	public static final Long SERVER_ROLE_ID = 5L;
	public static final Long CUSTOMER_ROLE_ID = 6L;

	public static final Integer AFTER_CONSTANT = (24 * 60 * 60 * 1000 - 1);
	public static final Integer PAGE_SIZE_LIMIT = 10000;

	public static final String RESERVATION_BOOKED_BY = "modifier";
	public static final String RESERVATION_CANCELLED_BY = "modifier";
	public static final String RESERVATION_BOOKING_MODE = "bookingMode";
	public static final String RESERVATION_STATUS = "reservationStatus";
	public static final String RESERVATION_BOOKED_BY_TEXT = "bookedBy";
	public static final String RESERVATION_CANCELLED_BY_TEXT = "cancelledBy";
	public static final String GUEST_ID = "guestId";

	public static final String RESERVED_BEFORE = "reservedBefore";
	public static final String RESERVED_AFTER = "reservedAfter";
	public static final String RESERVED_BETWEEN = "reservedBetween";
	public static final String EST_START_BEFORE = "estStartBefore";
	public static final String EST_START_AFTER = "estStartAfter";
	public static final String EST_START_BETWEEN = "estStartBetween";
	public static final String EST_END_BEFORE = "estEndBefore";
	public static final String EST_END_AFTER = "estEndAfter";
	public static final String EST_END_BETWEEN = "estEndBetween";
	public static final String CANCELLED_BEFORE = "cancelledBefore";
	public static final String CANCELLED_AFTER = "cancelledAfter";
	public static final String CANCELLED_BETWEEN = "cancelledBetween";
	public static final String ACT_START_BEFORE = "actStartBefore";
	public static final String ACT_START_AFTER = "actStartAfter";
	public static final String ACT_START_BETWEEN = "actStartBetween";
	public static final String ACT_END_BEFORE = "actEndBefore";
	public static final String ACT_END_AFTER = "actEndAfter";
	public static final String ACT_END_BETWEEN = "actEndBetween";
	public static final String CANCELLED = "CANCELLED";
	public static final String CANCELLED_BY = "cancelledBy";
	public static final String CANCELLED_BY_ID = "cancelledById";
	public static final String CANCELLED_TIME = "cancelTime";
	public static final String TABLE_GUID = "tableGuid";
	public static final String BOOKED_BY = "bookedBy";
	public static final String BOOKED_BY_ID = "bookedById";
	public static final String GUEST_GUID = "guestGuid";
	public static final String OFFER_ID = "offerId";

	public static final String NEW_REQUEST = "NEW_REQUEST";
	public static final String APPROVED = "APPROVED";

	public static final String GUEST_TAG_MODULE = "Guest Tag Relationship";

	public static final String TABLE_ID = "tableId";
	public static final String NOTE_ID = "noteId";

	// Event
	public static final String EVENT_MODULE = "event";
	public static final String EVENT_LABEL = "Event";
	public static final String EVENT_TYPE = "type";
	public static final String DAY_OF_THE_WEEK = "dayOfTheWeek";
	public static final String EVENT_SUBTYPE = "subtype";
	public static final String BLOCKING_TYPE = "blockingType";
	public static final String BLOCKING_AREA_TYPE = "blockingAreaType";
	public static final String RECURRANCE_TYPE = "recurranceType";
	public static final String RECUR_END_TYPE = "recurEndType";
	public static final String END_DATE = "endDate";
	public static final String DAILY = "DAILY";
	public static final String MONTHLY = "MONTHLY";
	public static final String WEEKLY = "WEEKLY";
	public static final String END_ON_DATE = "END_ON_DATE";
	public static final String RECURRANCE_END_DATE = "recurrenceEndDate";
	public static final String NEVER = "NEVER";
	public static final String END_AFTER = "END_AFTER";
	public static final String RECUR_EVERY = "recurEvery";
	public static final long DAY = 24 * 60 * 60 * 1000L;
	public static final String NUM_OF_RECURRENCE = "numOfRecurrence";
	public static final String BLOCK = "BLOCK";
	public static final String BLOCKING_AREA = "blockingArea";
	public static final String DAY_NAME = "day";
	public static final String EVENT = "EVENT";
	public static final String OFFER = "OFFER";
	public static final String EVENTCATEGORY = "eventCategory";
	public static final String OFFERCATEGORY = "offerCategory";
	public static final String BLOCKCATEGORY = "blockCategory";
	public static final String EVENTSUBCATEGORY = "eventSubCategory";
	public static final String BLOCKSUBCATEGORY = "blockSubCategory";

	/* Reservation */
	public static final CharSequence OR = "OR";
	public static final String EST_END_TIME = "estEndTime";
	public static final String EST_START_TIME = "estStartTime";
	public static final String RESERVATION_TIME = "reservationTime";
	public static final String ACT_START_TIME = "actStartTime";
	public static final String ACT_END_TIME = "actEndTime";

	// restaurant system configuration
	public static final String VALUE = "value";
	public static final String RESERVE_RELEASE_TIME = "reserve_release_time";
	public static final String WAITLIST_RELEASE_TIME = "waitlist_release_time";
	public static final String DINING_SLOT_INTERVAL = "dining_slot_interval";
	public static final String TAT_WD_12 = "tat_wd_12";
	public static final String TAT_WE_12 = "tat_we_12";
	public static final String TAT_WD_34 = "tat_wd_34";
	public static final String TAT_WE_34 = "tat_we_34";
	public static final String TAT_WD_56 = "tat_wd_56";
	public static final String TAT_WE_56 = "tat_we_56";
	public static final String TAT_WD_78 = "tat_wd_78";
	public static final String TAT_WE_78 = "tat_we_78";
	public static final String TAT_WD_8P = "tat_wd_8P";
	public static final String TAT_WE_8P = "tat_we_8P";
	public static final String WEEKDAY = "weekday";
	public static final String WEEKEND = "weekend";
	public static final CharSequence FAMILY_TAT = "family_tat";

	public static final String DATE_OF_MONTH = "dateOfMonth";
	public static final String WEEK_OF_MONTH = "weekOfMonth";
	public static final String START_DATE = "startDate";
	public static final String RECURRENCE_END_DATE = "recurrenceEndDate";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";
	public static final String DATE = "date";
	public static final String EVENT_DATE = "eventDate";
	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String DATE_FORMAT_LONG = "EEE MMM dd HH:mm:ss zzz yyyy";
	public static final String EVENT_CATEGORY = "category";
	public static final String EVENT_ID = "eventId";
	public static final String EVENT_GUID = "eventGuid";
	public static final String CALEVENT_MODULE = "CalenderEvent";
	public static final String CALEVENT_ATTNDNS_MODULE = "CalenderEventAttendence";
	public static final String ADD = "Add";
	public static final String UPDATE = "Update";
	public static final String CONFIRMED = "CONFIRMED";
	public static final String SEATED = "SEATED";
	public static final String DEFAULT_TIMEZONE = "GMT";
	public static final String NUM_COVERS = "num_covers";
	public static final String NUMCOVERS = "numCovers";
	public static final String COVERS_LESS_THAN = "coverLessThan";
	public static final String COVERS_MORE_THAN = "coverMoreThan";

	public static final String ACCESS_DENIED = "access denied";
	public static final String INVALID_ACCESS_TOKEN = "invalid access token";
	public static final String ACCESS_TOKEN_MISSING = "access token is missing";

	// reservation status
	public static final String NO_SHOW_STATUS = "NO_SHOW";

	// booking mode for reservation
	public static final String TABLE_STATUS = "tableStatus";
	public static final String ONLINE_STATUS = "ONLINE";
	public static final String WALKIN_STATUS = "WALKIN";
	public static final String FOOD_TAG = "Food Preferences";
	public static final String SEATING_TAG = "Seating Preferences";
	public static final String EVENTS_TAG = "Events And Offers";
	public static final String SPECIAL_EVENTS_TAG = "SPECIAL_EVENTS";

	public static final String TIMEZONE = "IST";

	public static final String DEVICE_ID = "deviceId";
	public static final String DEVICEID = "device_id";
	public static final String DEVICE_GUID = "deviceGuid";

	public static final String ALL = "ALL";
	public static final String AVAILABLE = "AVAILABLE";
	public static final String RESERVATION_LABEL = "Reservation";
	public static final String RESERVATION = "reservation";
	public static final String CSV_HEADERS = "csvHeaders";
	public static final String TAG_GUID = " TAG_GUID";
	public static final String PATCH = "PATCH";
	public static final String CREATED = "CREATED";
	public static final String FINISHED = "FINISHED";
	public static final String START_TIME_BEFORE = "startTimeBefore";
	public static final String START_TIME_AFTER = "startTimeAfter";
	public static final String END_TIME_BEFORE = "endTimeBefore";
	public static final String END_TIME_AFTER = "endTimeAfter";
	public static final String CATEGORY = "category";
	public static final String FULL_BLOCK = "FULL_BLOCK";
	public static final String TAG_MODEL = "TAG_MODEL";

	public static final String CALEVENT_GUID = "calenderEventGuid";
	public static final String GUESTPROFILE_LABEL = "GuestProfile";
	public static final String TAG_GUIDS = "tagGuid";

	public static final String STATE_CODE = "stateCode";
	public static final String STATE_LABEL = "State";

	public static final String DEVICE_MODULE = "device";
	public static final String DEVICE_TYPE = "type";
	//public static final String EVENT_START_BEFORE = "eventStartBefore";
	//public static final String EVENT_START_AFTER = "eventStartAfter";
	//public static final String EVENT_END_AFTER = "eventEndAfter";
	//public static final String EVENT_END_BEFORE = "eventEndBefore";

	public static final String ALLOCATED = "ALLOCATED";
	public static final String ARRIVED = "ARRIVED";
	public static final String CALLED = "CALLED";
	public static final String MSG_SENT = "MSG_SENT";
	public static final String COVERS = "covers";
	public static final String TIMES = "hh:mm a";
	public static final String TYPE = "type";
	public static final String TIME = "time";
	public static final String VERIFICATION_CODE = "verificationCode";
	public static final String MODE = "mode";
	public static final String APP = "app";
	public static final String TAT = "Tat";

	public static final String FILE_NAME = "fileName";
	public static final String FILE_EXT = "fileExt";
	public static final String FILE_FORMAT = "fileFormat";
	
	public static final int CACHE_LIFE_LIVE=3*60*60*1000;
	public static final int OTP_VALIDITY = 15;

	public static final String EVENT_PROMOTION_ENUM_VALUE = "EVENT_PROMOTION";
	public static final String RESERVATION_ENUM_VALUE = "RESERVATION";
	public static final String CONVERSATION_ENUM_VALUE = "CONVERSATION";
	public static final String BAR_ENTRY_ENUM_VALUE = "BAR_ENTRY";
	
	public static final String ORIGIN = "origin";
	public static final String ORIGIN_ID = "originId";
	public static final String EVENT_PROMOTION_LABEL = "EventPromotion";
	public static final String TAG_GUIDS_EVENT_PROMOTION = "tagGuids";
	public static final String TEMPLATE_LABEL = "Template";

	public static final String MANDRILL_API_KEY = "mandrill.key";

	public static final String GUPSHUP_USER_ID = "gupshup.userId";
	public static final String GUPSHUP_PASSWORD = "gupshup.password";
	public static final String GUPSHUP_PROMOTION_USER_ID = "gupshup.promotion.userId";
	public static final String GUPSHUP_PROMOTION_PASSWORD = "gupshup.promotion.password";
	public static final String TAG_LINE = "tagLine";
	public static final String COST_FOR_2 = "costFor2";
	public static final String CURRENCY = "currency";
	public static final String LANGUAGE = "language";
	public static final String LANGUAGE_CODE = "languageCode";
	public static final String TIME_ZONE = "timeZone";
	public static final String PREFER_DATE_FORMAT = "preferredDateFormat";
	public static final String PREFER_TIME_UNIT = "preferredTimeFormat";
	public static final String TEMPRATURE_SCALE = "tempratureScale";
	public static final String RESTAURANT_GENERAL_INFO = "restaurantGeneralInfo";

	public static final String PREFFERED_TABLE_TYPE = "prefferedTableType";
	public static final String PREFFERED_SECTION = "prefferedSection";
	public static final String CITY_GUID = "cityGuid";
	public static final String REGION_GUID = "regionGuid";
	public static final String LOCALITY_GUID = "localityGuid";

	public static final String TABLE_ASSIGNMENT_DETAILS = "Table assignment details";
	public static final String ACCOUNT_ID_PREFIX_STRING = "RES-";
	public static final String PARENT_ACCOUNT_ID_PREFIX_STRING = "RES-P-";
	public static final String READ_CONDITIONS = "readConditions";

	public static final String OPERATIONAL_HOURS = "operationalHours";

	public static final String BLOCKED = "BLOCKED";

	//public static final String EVENT_DATE_BEFORE = "eventDateBefore";
	//public static final String EVENT_DATE_AFTER = "eventDateAfter";

	public static final String INVALID_RESERVATION_TIME = "Invalid Reservation Time";
	public static final String SERVER_ID = "serverId";
	public static final String SHIFT_STATUS = "shiftStatus";
	public static final String CUSTOMER_ENUM = "CUSTOMER";
	public static final String RESTAURANT_ENUM = "RESTAURANT";
	public static final String RESERVE_OVERLAP_TIME = "reserve_overlap_time";
	public static final String TIME_INTERVAL = "timeInterval";

	// Authy

	public static final String DEFAULT_API_URI = "https://api.authy.com";
	public static final String VERSION = "1.0.1";
	public static final String REGISTER_USER_URI = "https://api.authy.com/protected/json/users/new";
	public static final String APIKEY = "H550yXbUYqIEI0DzGm5v1TvIa5vbqbwk";
	public static final String AUTHY_API_KEY = "AUTHY API KEY";
	public static final String AUTHY_ID = "AUTHY ID";
	public static final String SMS_SENT = "SMS Sent";
	public static final String SMS_NOT_SENT = "SMS Failed";
	public static final String SMS_TOKEN_VERIFIED = "SMS Token Verified";
	public static final String AUTHY_ID_RETRIEVING_SUCCESS = "AUTHY ID Retrieve Succesfully";
	public static final String AUTHY_ID_RETRIEVING_FAILED = "AUTHY ID Retrieve Failed";
	public static final String AUTHY_COUNTRY_CODE = "91";

	public static final String ORIGIN_GUID = "originGuid";

	// WS Request
	public static final String RETRY_COUNT = "retryCount";
	public static final String RETRY_COUNT_LESS = "retryCountLessThan";
	public static final Integer MAX_RETRY_COUNT = 10;
	public static final String SUCCESS = "SUCCESS";
	public static final String NOT_FOUND = "NOT_FOUND";
	public static final String UNAVAILABLE = "UNAVAILABLE";
	public static final String UNKNOWN = "UNKNOWN";
	public static final String FAILURE = "FAILURE";
	public static final String OP_HR = "OP_HR";
	public static final String HOLIDAY = "HOLIDAY";

	public static final String REST_OTP_MOBILE = "Rest OTP Mobile";

	public static final String SUNDAY = "SUNDAY";
	public static final String MONDAY = "MONDAY";
	public static final String TUESDAY = "TUESDAY";
	public static final String WEDNESDAY = "WEDNESDAY";
	public static final String THURSDAY = "THURSDAY";
	public static final String FRIDAY = "FRIDAY";
	public static final String SATURDAY = "SATURDAY";
	public static final Integer MAX_ALLOWED_COVERS = 9;
	public static final String RESERVATION_GUID = "reservationGuid";
	public static final String OTP_MOBILE = "otp_mobile";
	public static final String SHIFT = "shift";
	public static final String EVENT_SUBCATEGORY = "subCategory";
	public static final String SHIFT_CHANGE_TIME = "18:30:00";
	public static final String LAST_LOGIN = "lastLogin";
	public static final String PROP_MAP = "propertyMap";
	public static final String OP_HRS = "OP_HR_";
	public static final String BLACKOUT_OP_HRS = "BLACKOUT_OP_HR_";
	public static final String SMS_MESSAGE = " ClickTable Token : ";
	public static final String DUMMY_GUEST_GUID = "0ABCD0-0EFGH0-0IJKL0-0MNOP0-0QRST0";

	public static final String CITY_STATECODE = "stateCode";

	public static final String ENABLE_SCHEDULER = "enableScheduler";

	// zendesk
	public static final String SUPPORT_EMAIL_ID = "support@clicktable.com";
	public static final String SUPPORT_URL = "zendesk.url";
	public static final String SUPPORT_USERNAME = "zendesk.username";
	public static final String SUPPORT_PASSWORD = "zendesk.password";
	public static final String SUPPORT_TOKEN = "zendesk.token";
	public static final Long SUPPORT_ACCOUNT_ID = 27463811L;
	public static final Long SUPPORT_RESTAURANT_NAME = 27463831L;
	public static final Long SUPPORT_OS = 27493942L;
	public static final Long SUPPORT_DEVICE = 27493952L;
	public static final Long SUPPORT_ISSUE_TYPE = 27493962L;
	public static final String ATTACHMENT_IDS = "attachmentIds";
	public static final String ATTACHMENT = "attachment";

	public static final String EVENT_AND_OFFER = "Events And Offers";

	public static final String STATUS_TO_CHANGE = "statusToChange";
	public static final String STATUS_TO_CHANGE_AT_SHIFT_END = "statusToChangeAtShiftEnd";
	public static final String FORCED_SHIFT_END_TIME = "forced_shift_end_time";
	public static final String BUFFER_OPEN_TIME = "buffer_open_time";
	public static final String SHIFT_END_TIME_BEFORE = "shift_end_time_before";
	public static final String SHIFT_END_TIME_AFTER = "shift_end_time_after";

	public static final String LOG_OUT = "Log Out";

	public static final String ENABLE = "enable";
	public static final String START = "start";
	public static final String DURATION = "duration";
	public static final String OPT_GEN_TIME = "OPT_GEN_TIME";

	// Onboarding
	public static final String FIRSTNAME = "firstName";
	public static final String LASTNAME = "lastName";
	public static final String RESTAURANTNAME = "restaurantName";

	public static final String PARENT_EVENT_GUID = "parentEventGuid";

	public static final String CALENDER_TYPE = "calenderType";

	public static final String WEEK = "WEEK";
	public static final String MONTH = "MONTH";
	public static final String YEAR = "YEAR";
	public static final String DAYS = "DAY";
	public static final String ALL_TABLES = "allTables";

	public static final String SMTP_USER = "smtp.user";

	public static final String HEADERS = "headers";

	public static final String EMAIL_ID = "emailId";

	public static final String INVALID_DINING_SLOT_INTERVAL = "Invalid Dinning Slot";

	public static final String RESERVATION_FINAL_STATUS = "finalStatus";
	public static final String WAITLIST_FINAL_STATUS = "waitlistFinalStatus";

	public static final String OPERATION_HOUR_MODULE = "OperationHour";
	public static final String OPERATION_HOUR_CATEGORY = "subCategory";

	public static final Integer DINING_SLOT_INTERVAL_DEFAULT = 15;
	public static final Integer RESERVE_RELEASE_TIME_DEFAULT = 15;
	public static final Integer RESERVE_OVERLAP_TIME_DEFAULT = 15;
	public static final Integer WAITLIST_RELEASE_TIME_DEFAULT = 15;
	public static final Integer BUFFER_OPEN_TIME_DEFAULT = 30;
	public static final String FORCED_SHIFT_END_TIME_DEFAULT = "06:00";

	public static final String DESCRIPTION = "description";

	public static final String SMS_STATUS = "smsStatus";
	public static final String SMS_STATUS_CAUSE = "smsStatusCause";
	public static final String WAITING_LOWER = "waiting";
	public static final String SUCCESS_LOWER = "success";
	public static final String ERROR_LOWER = "error";

	public static final String REJECTED = "REJECTED";

	public static final String REASON_TO_REJECT = "reasonToReject";

	public static final int MAX_SECTIONS = 5;

	public static final String ISSUE_TYPE = "issueType";

	public static final String TICKET = "ticket";

	public static final String ROLE_NAME = "roleName";

	public static final String STATUS_DISTRIBUTION = "statusDistribution";
	public static final String MODE_DISTRIBUTION = "bookingModeDistribution";
	public static final String GUEST_DISTRIBUTION = "guestDistribution";
	public static final String TAT_DISTRIBUTION = "tatDistribution";
	public static final String WAIT_DISTRIBUTION = "waitDistribution";
	public static final String COVER_DISTRIBUTION = "coversDistrbution";
	public static final String QUEUE_DISTRIBUTION = "queueDistribution";

	// Neo4j constants
	public static final String NEO4J = "neo4j";
	public static final String PATH = "path";
	public static final String USER = "user";
	public static final String PWD = "password";
	public static final String FIREBASE_TOKEN = "JH6fuL0IoL3PnXAaxcczWPIvD9fbSnBZdPS9I0yu";
	public static final String FIREBASE_TOKEN_NAME = "FireBase Token";

	//
	public static final String DUMMY_FIRSTNAME = "UNKNOWN GUEST";
	public static final String DUMMY_LASTNAME = "GUEST";
	public static final String DUMMY_MOBILE = "0000000000";
	public static final String DUMMY_EMAIL = "unknown@clicktable.com";
	public static final boolean DUMMY_IsVIP = false;
	public static final String DUMMY_GENDER = "MALE";

	public static final String SHOW_ALL_DAY = "showAllDay";

	public static final String ERROR = "ERROR";

	public static final String WAITING = "WAITING";

	public static final String SMS_ID = "smsId";

	public static final String TABLE_WAITLIST = "Table Waitlist";
	public static final String TABLE_WAITLIST_RESULT = "Table Waitlist Result";

	public static final String DUMMY_GUEST_ID = "UNKNOWN GUEST";
	public static final String STROMPATH_APPLICATION_ID = "application_id";
	public static final String STROMPATH_APPLICATION_KEY = "apiKey";
	public static final String STROMPATH_ID = "id";
	public static final String STROMPATH_SECRET = "secret";

	public static final String OAUTH_TOKEN_PATH = "/oauth/token";
	public static final String VALIDATE_OAUTH_TOKEN_PATH = "/authTokens";

	public static final String AUTHORIZATION = "Authorization";

	public static final String RESERVED = "RESERVED";

	// Reservation Id generation
	public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public static final int BASE = ALPHABET.length();
	public static final long MIN_RESERVATION_ID = 1410;
	public static final long MIN_ID_LENGTH = 5;

	public static final String REFRESH_TOKEN_EXPIRED = "Refresh Token Expired or Invalid value !";

	public static final String INVALID_AUTH_ACCESS_TOKEN = "Invalid OAUTH Access Token";
	public static final String INVALID_AUTH_ACCESS_TOKEN_EXPIRED = "Invalid OAUTH Access Token Expired";
	public static final String AUTH_ACCESS_TOKEN_MISSING = "Authorization Token is Missing !";

	public static final String SMS_LOGIN_OTP_MSG = "sms.login_otp";
	public static final String SMS_FILE = "sms.properties";

	public static final String SMS_CREATED_MSG = "sms.reservation_created";
	public static final String SMS_WAITLIST_MSG = "sms_waitlist_reservation";
	public static final String SMS_RESERVATION_AUTO_FOLLOW_UP = "sms_reservation_auto_follow_up";

	public static final String SMS_RESERVATION_RELEASED = "sms_reservation_released";

	public static final String SMS_RESERVATION_CANCELLED = "sms_reservation_cancelled";

	public static final String SMS_RESERVATION_NO_SHOW = "sms_reservation_no_show";
	public static final String DASHBOARD = "Dashboard";

	public static final long MIN_ACCOUNT_ID = 100501;

	public static final String SIGN_UP_SUBJECT = "sign_up_subject";

	public static final String CRON_EXPRESSION_TO_LAUNCH_THREAD = "cron_expression";

	public static final String RESTART = "restart";

	public static final int THREAD_PRIORITY = 5;

	public static final String SIGN_UP_BODY = "sign_up_body";
	public static final String SIGN_UP_MANDRILL_TEMPLATE_NAME = "sign-up";
	public static final String SIGN_UP_REJECT_MANDRILL_TEMPLATE_NAME = "sign-up-reject";
	public static final String SIGN_UP_APPROVED_MANDRILL_TEMPLATE_NAME = "sign-up-approved";
	public static final String SIGN_UP_NOTIFICATION_MANDRILL_TEMPLATE_NAME = "sign-up-notification";
	public static final String CSV = "csv";
	public static final String INVALID = "INVALID";
	public static final String EXPORT = "EXPORT";
	public static final String VALID_COUNT = "validCount";
	public static final String INVALID_COUNT = "invalidCount";
	public static final String CSV_INVALID_MANDRILL_TEMPLATE_NAME = "csv-invalid";
	public static final String CSV_SUCCESS_MANDRILL_TEMPLATE_NAME = "csv-success";
	public static final String CSV_EXPORT_MANDRILL_TEMPLATE_NAME = "csv-export";
	public static final String DAILY_REPORT_MANDRILL_TEMPLATE_NAME = "daily-report";
	public static final String GUEST_CSV_EXPORT_FILE_NAME = "guests.csv";
	public static final String RESERVATION_CSV_EXPORT_FILE_NAME = "reservations.csv";
	
	public static final String FIREBASE = "firebase";

	public static final String EXTENSION = "extension";

	public static final String MASTER_DATA = "MASTER DATA";
	public static final String OPENWEATHER_APPID = "openweather.appid";
	public static final String OPEN_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
	public static final String LAT_LONG = "lat_long";
	public static final String RUNTIME_EXCEPTION = "RuntimeException";


	public static final String REPORT = "REPORT";

	public static final String LEGAL_NAME = "legalName";

	public static final String COVER_GUID = "coverGuid";
	public static final String CAL_TAT_GUID = "calTatGuid";

	public static final String UPDATED_COUNTRY_CODE = "updatedCountryCode";

	public static final String WEBSITE = "website";


	public static final String APPEND_PROMTION_MSG = "promotion_append";

	
	public static final String BAR_ENTRY_LABEL = "BarEntry";
	public static final CharSequence BAR_MAX_TIME_DB = "bar_max_time";


	public static final String MOVED_TO_BAR = "MOVED_TO_BAR";

	public static final String CUMULATIVE_RATING = "cumulative_rating";

	public static final String BAR_ENTRY = "BarEntry";
	public static final String BAR = "bar";

	public static final int DEFAULT_BAR_MAX_TIME = 120;

    public static final String MALE = "MALE";
	
	public static final String FEMALE = "FEMALE";

	public static final String MALE_SALUTATION = "Mr";

	public static final String FEMALE_SALUTATION = "Ms";

	public static final CharSequence BAR_MAX_TIME = "barMaxTime";
	public static final String BEFORE = "Before";
	public static final String AFTER = "After";
	public static final String BEFORE_OR_ON = "BeforeOrOn";
	public static final String AFTER_OR_ON = "AfterOrOn";
	public static final String ON = "On";
	public static final String LESS = "LessThan";
	public static final String GREATER = "GreaterThan";
	public static final String LESS_EQUAL = "LessThanOrEqualTo";
	public static final String GREATER_EQUAL = "GreaterThanOrEqualTo";
	public static final String DATESTR = "Date";
	public static final String TIMESTR = "Time";
	
	public static final String REST_NODE = "restNode";
	public static final String GUEST_NODE = "guestNode";
	public static final String SERVER_OK = "200";
	public static final String REST_TAG_MODEL = "restTagModel";
	public static final String GUEST_TAG_MODEL = "guestTagModel";
	public static final String INAVLID_USERNAME_CODE = "7104";
	public static final String INVALID_PASSWORD_CODE = "7100";

	public static final String GOOGLE = "GOOGLE";
	public static final String FACEBOOK = "FACEBOOK";
	public static final String OAUTH_REFRESH_TOKEN_EXPIRED="10011";
	public static final String CORPORATE_OFFERS_LABEL = "CorporateOffers";
	public static final Object OFFER_FIELD = "offer";
	public static final Object NOTES = "notes";	
	public static final String NEW_GUEST_GUID ="new_guest_id" ;

	public static final String FACEBOOK_ID = "fid";
	public static final String GOOGLE_ID = "gid";
	public static final String OTP_TOKEN = "otpToken";


	public static final String TAG_NAME = "tagName";
	public static final String TAG_GUID_LABEL = "tagGuid";

	public static final String RESTAURANT_ADDED = "RESTAURANT";
	public static final String GUEST_ADDED = "GUEST";

	public static final String TAG_PREFERENCES = "PREFERENCES";

	public static final String TAG_TYPE = "tagType";

	public static final String EVENT_START_BEFORE = "eventStartBefore";
	public static final String EVENT_START_AFTER = "eventStartAfter";
	public static final String EVENT_END_BEFORE = "eventEndBefore";
	public static final String EVENT_END_AFTER = "eventEndAfter";

	public static final String ADDED_BY = "addedBy";

	public static final String STARTS_WITH = "StartsWith";

	public static final String TATSTR = "tat";


	//	reporting
	public static final String REPORTING_PREFERENCE_LABEL = "ReportingPreference";

	public static final String DISPLAY_NAME = "displayName";

	public static final String DISTINCT = "DISTINCT";

	public static final String STAFF_GUID = "staff_guid";

	public static final String STAFF_INFO = "STAFF_INFO";

	public static final Integer TTLForCache = 2*60*60;

	public static final String SMS_ADDED_TO_BAR_MSG = "sms.added_to_bar";


	public static final String BOTH = "BOTH";

	public static final String VIP = "VIP";

	public static final String GUEST_TYPE = "guestType";

	public static final String CORPORATE_GUIDS = "corporateguids";
	public static final String IST_TIME_ZONE = "IST";

	public static final String DOB_AFTER = "dobAfter";
	public static final String DOB_BEFORE = "dobBefore";
	public static final String ANNIVERSARY_AFTER = "anniversaryAfter";
	public static final String ANNIVERSARY_BEFORE = "anniversaryBefore";

	public static final String CT_SUPPORT_URL = "support.url";
	public static final String CONVERSATION_URI = "/conversation";

	//report
	public static final String REPORTS = "reports";
	public static final String REPORT_DATE_FORMAT = "dd-MM-yyyy";


	public static final String STORMPATH_ERROR = "Stormpath Error";
	
	
	//Application Details
	public static final String APP_NAME = "appName";
	public static final String PLATFORM = "platform";

	public static final String APPLICATION_DETAILS = "AppDetails";


	public static final String PROMOTION = "promotion";


	
	//Play WS access 
	public static final String CLICKTABLE_URL = "clicktable.url";
	public static final String CLICKTABLE_USER = "username.encode";
	public static final String CLICKTABLE_PASSWORD = "password.encode";
	public static final String ONBOARDING_URL = "onboarding.url";

	public static final String LOGIN_URI="/staff/login";	
	public static final String ONBOARDING_URI="/onboarding";

	public static final String CONVERSATION = "Conversation";

	//Manual Scheduled Job
	public static final String JOB_NAME = "JOB";
	public static final String SEND_RESERVATION_SMS = "SEND_RESERVATION_SMS";
	public static final String RESERVATION_STATUS_UPDATE_AT_SHIFT_END = "RESERVATION_STATUS_UPDATE_AT_SHIFT_END";
	public static final String BAR_ENTRY_STATUS_UPDATE = "BAR_ENTRY_STATUS_UPDATE";
	public static final String EXTEND_RECUR_END_DATE_FOR_NEVER = "EXTEND_RECUR_END_DATE_FOR_NEVER";
	public static final String CHECK_DND = "CHECK_DND";

	public static final String DND = "DND";
	public static final String UNDND = "UNDND";

	public static final String SCRUBBING_USER_ID = "scrubbing.userid";
	public static final String SCRUBBING_PASSWORD = "scrubbing.password";

	public static final int TIMEOUT = 30000;



}

