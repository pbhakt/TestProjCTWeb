package com.clicktable.util;

public class ErrorCodes {

	// Basic Errors
	public static final String INVALID_STATUS = "1";
	public static final String INVALID_LANG_CODE = "2";
	public static final String INVALID_REGION = "3";
	public static final String INVALID_MOBILE_NO = "4";
	public static final String INVALID_EMAIL_FORMAT = "5";
	public static final String EMAIL_ACCOUNT_NOT_EXIST = "6";
	public static final String INVALID_VALUE = "7";
	public static final String REQUIRED = "error.required";
	public static final String NOT_UPDATABLE = "9";
	public static final String ONE_OF_THESE_REQUIRED = "10";

	public static final String GUID_REQUIRED = "11";
	public static final String INVALID_GUID = "12";

	public static final String INVALID_PATTERN = "11111";
	public static final String INVALID_MOBILE = "11112";

	public static final String INVALID_HEADER = "702";
	public static final String LANG_CD = "703";

	// customer errors
	public static final String CUST_ID_REQUIRED = "10001";
	public static final String CUST_MOBILE_FORMAT = "10002";
	public static final String INVALID_CUST_ID = "10003";
	public static final String INVALID_DATE_FORMAT = "10004";
	public static final String DOB_AFTER_CURRENT_DATE = "10005";
	public static final String ANNIVERSARY_AFTER_CURRENT_DATE = "10006";
	public static final String DOB_AFTER_ANNIVERSARY = "10007";
	public static final String NO_ACCESS_TO_CREATE_OR_UPDATE_CUSTOMER_OF_OTHER_REST = "10008";
	public static final String CUST_MOBILE_REQUIRED = "10009";
	public static final String VIP_REASON_REQUIRED = "10010";
	public static final String INVALID_VIP_REASON = "10011";
	public static final String CUSTOMER_ALREADY_EXISTS = "10012";
	public static final String INVALID_GENDER = "10013";
	public static final String EMAIL_ACCOUNT_ALREADY_EXISTS = "10014";
	public static final String INVALID_GUEST_GUID = "10015";
	public static final String RESERVATION_EXIST_FOR_GUEST = "10016";
	public static final String BAR_ENTRY_EXIST_FOR_GUEST = "10017";
	public static final String GUEST_FIRST_NAME_MIN_LENGTH = "10201";
	public static final String GUEST_FIRST_NAME_MAX_LENGTH = "10202";
	public static final String GUEST_FIRST_NAME = "10203";
	public static final String GUEST_LAST_NAME_MIN_LENGTH = "10204";
	public static final String GUEST_LAST_NAME_MAX_LENGTH = "10205";
	public static final String GUEST_LAST_NAME = "10206";
	public static final String GUEST_MOBILE_MIN_LENGTH = "10207";
	public static final String GUEST_MOBILE_MAX_LENGTH = "10208";
	public static final String GUEST_EMAIL_MAX_LENGTH = "10209";
	public static final String GUEST_ADD_MAX_LENGTH = "10210";
	public static final String GUEST_COUNTRY_CODE_MAX_LENGTH = "10211";
	public static final String GUEST_STATE_CODE_MAX_LENGTH = "10212";
	public static final String GUEST_CITY_CODE_MAX_LENGTH = "10213";
	public static final String GUEST_PHOTO_URL_MAX_LENGTH = "10214";
	public static final String GUEST_IS_VIP = "10215";
	public static final String GUEST_GENDER = "10216";
	public static final String GUEST_RESTID = "10217";
	public static final String HEADERS_MISSING = "10218";
	public static final String DUMMY_GUEST_CANNOT_BE_UPDATED = "10219";
	public static final String NAME_OF_DUMMY = "10220";
	public static final String MOBILE_OF_DUMMY = "10221";
	public static final String EMAIL_OF_DUMMY = "10222";
	public static final String BIRTH_DATE = "10223";
	public static final String ANNIVERSARY_DATE = "10224";
	public static final String GUESTS_NOT_FOUND = "10225";
	public static final String INVALID_FILE_FORMAT = "10226";
	public static final String FILE_NOT_GNRTD = "11226";
	public static final String FILE_NOT_CREATED = "10227";
	public static final String REPORT_CREATION_EXCEPTION = "10228";
	public static final String FILE_FORMAT_REQUIRED = "10229";
	public static final String DUPLICATE_MOBILE_NUMBER = "10230";
	public static final String CSV_FILE_NOT_FOUND = "10231";
	public static final String FILE_CORRUPTED = "10232";
	
	// table errors
	public static final String INVALID_TABLE_ID = "20001";
	public static final String TABLE_ID_REQUIRED = "20002";
	public static final String MIN_GREATER_THEN_MAX = "20003";
	public static final String DUPLICATE_TABLE_NAME = "20004";
	public static final String NO_ACCESS_TO_CREATE_OR_UPDATE_TABLE_OF_OTHER_REST = "20005";
	public static final String TABLE_NOT_AVAILABLE = "20006";
	public static final String RESERVATION_EXIST_FOR_TABLE = "20007";
	public static final String TABLE_NAME_REQUIRED = "20201";
	public static final String TABLE_NAME_MAX_LENGTH = "20202";
	public static final String TABLE_REST_GUID_REQUIRED = "20203";
	public static final String TABLE_MAX_COVERS_REQUIRED = "20204";
	public static final String TABLE_MAX_COVERS_MAX_VALUE = "20205";
	public static final String TABLE_MAX_COVERS_MIN_VALUE = "20206";
	public static final String TABLE_MIN_COVERS_REQUIRED = "20207";
	public static final String TABLE_MIN_COVERS_MAX_VALUE = "20208";
	public static final String TABLE_MIN_COVERS_MIN_VALUE = "20209";
	public static final String TABLE_TYPE_REQUIRED = "20210";

	// restaurant errors
	public static final String INVALID_REST_ID = "30001";
	public static final String REST_ID_REQUIRED = "30002";
	public static final String RESTAURANT_NOT_EDITABLE = "30003";
	public static final String CAPACITY_LESS_THAN_MIN_CAPACITY = "30004";
	public static final String CAPACITY_MORE_THAN_MAX_CAPACITY = "30005";
	public static final String MIN_CAPACITY_MORE_THAN_MAX_CAPACITY = "30006";
	public static final String MIN_CAPACITY_SHOULD_BE_GREATER_THAN_ZERO = "30007";
	public static final String MAX_CAPACITY_SHOULD_BE_GREATER_THAN_ZERO = "30008";
	public static final String NO_ACCESS_TO_UPDATE_DETAILS_OF_OTHER_REST = "30009";
	public static final String REST_FOR_STAFF_NOT_VALID = "30010";

	public static final String LATITUDE_REQUIRED = "30201";
	public static final String LATITUDE_MAX_LENGTH = "30202";
	public static final String LONGITUDE_REQUIRED = "30203";
	public static final String LONGITUDE_MAX_LENGTH = "30204";
	public static final String WEBSITE_MAX_LENGTH = "30205";
	public static final String PHONE_NO1_MAX_LENGTH = "30206";
	public static final String PHONE_NO2_MAX_LENGTH = "30207";
	public static final String REGION_REQUIRED = "30208";
	public static final String REGION_MAX_LENGTH = "30209";
	public static final String LOCALITY_REQUIRED = "30210";
	public static final String LOCALITY_MAX_LENGTH = "30211";
	public static final String LOCALITY_MIN_LENGTH = "30212";
	public static final String BUILDING_MAX_LENGTH = "30213";
	public static final String COST_FOR_2_REQUIRED = "30251";
	public static final String TAGLINE_MAX_LENGTH = "30252";
	public static final String REST_TIMEZONE_REQUIRED = "30253";
	public static final String REST_PREFERRED_DATE_FORMAT_REQUIRED = "30254";
	public static final String REST_PREFERRED_TIME_FORMAT_REQUIRED = "30255";
	public static final String REST_CURRENCY_REQUIRED = "30256";
	public static final String REST_TEMPERATURE_SCALE_REQUIRED = "30257";
	public static final String REST_NAME_REQUIRED = "30301";
	public static final String REST_NAME_MAX_LENGTH = "30302";
	public static final String DISPLAY_NAME_REQUIRED = "30303";
	public static final String DISPLAY_NAME_MAX_LENGTH = "30304";
	public static final String FOOD_SOURCE_MAX_LENGTH = "30305";
	public static final String WASTE_DISPOSAL_MAX_LENGTH = "30306";
	public static final String REST_OTP_MOBILE = "30307";
	public static final String BUILDING_REQUIRED = "30258";
	public static final String PHONE_NO1_REQUIRED = "30259";

	// staff errors
	public static final String INVALID_STAFF_ID = "40001";
	public static final String STAFF_ID_REQUIRED = "40002";
	public static final String NO_ACCESS_TO_CREATE_OR_UPDATE_STAFF_OF_OTHER_REST = "40003";
	public static final String NO_ACCESS_TO_CREATE_OR_UPDATE_OTHER_PERSONS_PROFILE = "40004";
	public static final String MANAGER_CAN_CREATE_OR_UPDATE_ONLY_STAFF_OR_SERVER = "40005";
	public static final String ADMIN_CAN_CREATE_OR_UPDATE_ONLY_MANAGER_STAFF_OR_SERVER = "40006";
	public static final String INVALID_SP_TOKEN = "40007";
	public static final String USER_CANNOT_DELETE_HIS_OWN_ACCOUNT = "40008";
	public static final String STAFF_INFO_CREATION_FAILURE = "40009";
	public static final String ONLY_ADMIN_HAS_ACCESS = "40010";
	public static final String VERSION_CONFLICT = "40011";
	public static final String STAFF_FIRST_NAME_REQUIRED = "40201";
	public static final String STAFF_FIRST_NAME_MAX_LENGTH = "40202";
	public static final String STAFF_LAST_NAME_REQUIRED = "40203";
	public static final String STAFF_LAST_NAME_MAX_LENGTH = "40204";
	public static final String STAFF_NICK_NAME_MAX_LENGTH = "40205";
	public static final String STAFF_EMAIL_REQUIRED = "40206";
	public static final String STAFF_EMAIL_INVALID_FORMAT = "40207";
	public static final String STAFF_EMAIL_MAX_LENGTH = "40208";
	public static final String STAFF_MOBILE_REQUIRED = "40209";
	public static final String STAFF_MOBILE_MAX_LENGTH = "40210";
	public static final String STAFF_MOBILE_MIN_LENGTH = "40211";
	public static final String STAFF_ROLE_ID_REQUIRED = "40212";
	public static final String USER_ALREADY_DELETED = "40413";
	public static final String RESTAURANT_NOT_ACTIVE = "40414";

	// cuisine errors
	public static final String INVALID_CUISINE_ID = "50001";
	public static final String CUISINE_ID_REQUIRED = "50002";
	public static final String DUPLICATE_CUISINE_NAME = "50003";
	public static final String CUISINE_NAME = "50201";
	public static final String CUISINE_NAME_MAXLENGTH = "50202";

	// reservation errors
	public static final String INVALID_RESERVATION_ID = "60001";
	public static final String RESERVATION_ID_REQUIRED = "60002";
	public static final String INVALID_RESERVATION_BOOKED_BY = "60003";
	public static final String INVALID_RESERVATION_CANCELLED_BY = "60004";
	public static final String INVALID_RESERVATION_BOOKING_MODE = "60005";
	public static final String INVALID_RESERVATION_STATUS = "60006";
	public static final String CANCELLED_BY_REQUIRED = "60007";
	public static final String CANCELLED_BY_ID_REQUIRED = "60008";
	public static final String CANCELLED_TIME_REQUIRED = "60009";
	public static final String NO_ACCESS_TO_UPDATE_THIS_RESERVATION = "60010";
	public static final String COVERS_SHOULD_BE_GREATER_THAN_ZERO = "60011";
	public static final String NOT_ALLOW_RESERVATION_STATUS = "60012";
	public static final String INVALID_PREFFERED_TABLE_TYPE = "60013";
	public static final String INVALID_PREFFERED_SECTION = "60014";
	public static final String NO_ALLOW_TO_UPDATE_THIS_RESERVATION = "60015";
	public static final String TAT_REQUIRED = "60016";
	public static final String RESERVATION_CANNOT_BE_SEATED_BEFORE_30_MIN = "60017";
	public static final String RESERVATION_NOT_FOUND = "60018";

	public static final String INVALID_END_DATE = "60019";

	public static final String INVALID_OFFER_ID = "60020";

	// Reservation Model Error Codes
	public static final String RESERVATION_REST_GUID = "60201";
	public static final String RESERVATION_GUEST_GUID = "60202";
	public static final String RESERVATION_BOOKING_MODE = "60203";
	public static final String RESERVATION_BOOKING_BY = "60204";
	public static final String RESERVATION_COVERS = "60205";
	public static final String RESERVATION_EST_START_TIME = "60206";
	// reservation history
	public static final String RESERVATION_HISTORY_GUID = "60301";
	public static final String RESERVATION_HISTORY_CREATED_DATE = "60302";
	public static final String RESERVATION_HISTORY_CREATED_BY = "60303";
	public static final String RESERVATION_STATUS = "60304";
	public static final String RESERVATION_HISTORY_BOOKED_BY = "60305";

	// onboarding errors
	public static final String INVALID_ONBOARD_ID = "70001";
	public static final String ONBOARD_ID_REQUIRED = "70002";
	public static final String INVALID_REQUEST_STATUS = "70003";
	public static final String EMAIL_ALREADY_EXIST = "700004";
	public static final String RESTAURANT_NAME_ALREADY_EXSIT = "700005";
	public static final String VERIFICATION_CODE_REQUIRED = "700006";
	public static final String READ_TERMS_AND_CONDITIONS = "700007";

	public static final String ONBOARD_FIRST_NAME_REQUIRED = "70201";
	public static final String ONBOARD_FIRST_NAME_MAX_LENGTH = "70202";
	public static final String ONBOARD_LAST_NAME_REQUIRED = "70203";
	public static final String ONBOARD_LAST_NAME_MAX_LENGTH = "70204";
	public static final String ONBOARD_EMAIL_REQUIRED = "70205";
	public static final String ONBOARD_INVALID_EMAIL_FORMAT = "70206";
	public static final String ONBOARD_EMAIL_MAX_LENGTH = "70207";
	public static final String ONBOARD_DESIGNATION_REQUIRED = "70208";
	public static final String ONBOARD_DESIGNATION_MAX_LENGTH = "70209";
	public static final String ONBOARD_MOBILE_REQUIRED = "70210";
	public static final String ONBOARD_MOBILE_MAX_LENGTH = "70211";
	public static final String ONBOARD_REQUEST_STATUS_REQUIRED = "70212";
	public static final String ONBOARD_REST_NAME_REQUIRED = "70213";
	public static final String ONBOARD_REST_NAME_MAX_LENGTH = "70214";
	public static final String ONBOARD_CONTACT_REQUIRED = "70215";
	public static final String ONBOARD_CONTACT_MAX_LENGTH = "70216";
	public static final String ONBOARD_ADDRESS_LINE_1_REQUIRED = "70217";
	public static final String ONBOARD_ADDRESS_LINE_1_MAX_LENGTH = "70218";
	public static final String ONBOARD_ADDRESS_LINE_2_MAX_LENGTH = "70219";
	public static final String ONBOARD_LANDMARK_MAX_LENGTH = "70220";
	public static final String ONBOARD_COUNTRY_CODE_REQUIRED = "70221";
	public static final String ONBOARD_COUNTRY_CODE_MIN_LENGTH = "70222";
	public static final String ONBOARD_COUNTRY_CODE_MAX_LENGTH = "70223";
	public static final String ONBOARD_STATE_REQUIRED = "70224";
	public static final String ONBOARD_STATE_MAX_LENGTH = "70225";
	public static final String ONBOARD_CITY_REQUIRED = "70226";
	public static final String ONBOARD_CITY_MAX_LENGTH = "70227";
	public static final String ONBOARD_LOCALITY_REQUIRED = "70228";
	public static final String ONBOARD_LOCALITY_MAX_LENGTH = "70229";
	public static final String ONBOARD_ZIPCODE_REQUIRED = "70230";
	// Role errors
	public static final String INVALID_ROLE_ID = "80001";
	public static final String ROLE_ID_REQUIRED = "80002";
	public static final String UNAUTHORIZED = "80003";
	public static final String ROLE_NAME_REQUIRED = "80201";
	public static final String ROLE_NAME_MAX_LENGTH = "80202";

	// Note
	public static final String INVALID_NOTE_ID = "90001";
	public static final String NOTE_ID_REQUIRED = "90002";

	public static final String NOTE_GUID_REQUIRED = "90201";
	public static final String NOTE_REST_ID_REQUIRED = "90202";
	public static final String NOTE_REQUIRED = "90203";
	public static final String NOTE_MAX_LENGTH = "90204";
	public static final String NOTE_LANG_CD_REQUIRED = "90205";

	// Tag error
	public static final String INVALID_TAG_ID = "110001";
	public static final String REL_DOES_NOT_EXIST = "110006";
	public static final String REST_HAS_TABLE_FAILED = "100002";

	// section error
	public static final String NAME_ALREADY_EXISTS = "120001";
	public static final String NO_SUCH_SECTION_EXISTS_FOR_SPECIFIED_RESTAURANT = "120002";
	public static final String SECTION_ID_REQUIRED = "120003";
	public static final String INVALID_SECTION_ID = "120004";
	public static final String SECTION_NAME_REQUIRED = "120201";
	public static final String SECTION_NAME_MAX_LENGTH = "120202";
	public static final String SECTION_DESCRIPTION_MAX_LENGTH = "120203";
	public static final String SECTION_MAX_LIMIT_REACH = "120204";
	public static final String SECTION_CONTAINS_TABLE = "120205";
	public static final String TABLE_HAS_RESV = "120206";
	public static final String TABLE_HAS_CALEVENT = "120207";

	// attribute error
	public static final String ATTRIBUTE_NAME_ALREADY_EXISTS = "130001";
	public static final String ATTR_ID_REQUIRED = "130002";
	public static final String INVALID_ATTRIBUTE_ID = "130003";
	public static final String ATTR_NAME_REQUIRED = "130201";
	public static final String ATTR_NAME_MAX_LENGTH = "130202";

	// event error
	public static final String INVALID_EVENT_ID = "140001";
	public static final String EVENT_ID_REQUIRED = "140002";
	public static final String ENDDATE_BEFORE_STARTDATE = "140003";
	public static final String BACK_DATED_EVENT = "140004";
	public static final String OVERLAPPING_EVENT_EXIST = "140005";
	public static final String AREA_NOT_AVAILABLE = "140006";
	public static final String RECURENDDATE_BEFORE_STARTDATE = "140007";
	public static final String EVENT_CANT_COEXIST = "140008";
	public static final String INVALID_CATEGORY = "140009";
	public static final String INVALID_DAY_OF_WEEK = "140010";
	public static final String DAY_OF_WEEK_REQUIRED = "140011";
	public static final String INVALID_EVENT_SUBCATEGORY = "140012";
	public static final String RECURRANCE_TYPE_REQUIRED = "140013";
	public static final String BLOCKING_TYPE_REQUIRED = "140014";
	public static final String BLOCKING_AREA_REQUIRED = "140015";
	public static final String RECURRANCE_END_DATE_REQUIRED = "140016";
	public static final String EVENT_INVALID_RESTID = "140201";
	public static final String CAL_EVENT_NAME = "140202";
	public static final String CAL_EVENT_TYPE = "140203";
	public static final String CAL_EVENT_CATEGORY = "140204";
	public static final String EVENT_NAME = "140205";
	public static final String EVENT_NAME_MAXLENGTH = "140206";
	public static final String EVENT_DESC = "140207";
	public static final String EVENT_DESC_MINLENGTH = "140208";
	public static final String EVENT_DESC_MAXLENGTH = "140209";
	public static final String EVENT_START_DT = "140210";
	public static final String EVENT_END_DT = "140211";
	public static final String EVENT_TYPE = "140212";
	public static final String EVENT_IS_DRAFT = "140213";
	public static final String EVENT_CATEGORY = "140214";
	public static final String EVENT_START_TIME = "140215";
	public static final String EVENT_END_TIME = "140216";
	public static final String EVENT_MIN_DATE_OF_MONTH = "140217";
	public static final String EVENT_MAX_DATE_OF_MONTH = "140218";
	public static final String EVENT_MIN_WEEK_OF_MONTH = "140219";
	public static final String EVENT_MAX_WEEK_OF_MONTH = "140220";
	public static final String DURATION_LESSTHAN_FREQUENCY = "140221";
	public static final String BACK_TIMED_EVENT = "140222";
	public static final String RECUR_END_TYPE_MISSING = "140223";
	public static final String INVALID_VALUE_RECUR_EVERY = "140224";
	public static final String NUM_OF_RECURRENCE_POSITIVE = "140225";
	public static final String EVENT_TYPE_NOT_EDITABLE = "140226";
	public static final String EVENT_CATEGORY_NOT_EDITABLE = "140237";
	public static final String EVENT_START_DATE_NOT_EDITABLE_AFTER_START = "140228";

	public static final String INVALID_BLOCKING_AREA = "140229";
	public static final String PHOTOURL_MAXLENGTH = "140230";
	public static final String HOLIDAY_NOT_ALLDAY = "140231";
	public static final String FINISHED_EVENT_NOT_DELETED = "140232";
	public static final String EVENT_ALREADY_PROMOTED = "140233";
	public static final String EVENT_ONGOING = "140234";
	public static final String FINISHED_EVENT_NOT_UPDATED = "140235";
	public static final String CALEVENT_NOT_FOUND = "140236";
	public static final String EVENT_RECURRENCE_NOT_EDITABLE_AFTER_START = "140237";
	public static final String NUM_RECURRANCE_ALREADY_OCCURED = "140238";
	public static final String HAS_RESERVATOINS_ON_OFFER = "140239";

	// country error
	public static final String COUNTRY_NAME_ALREADY_EXISTS = "150001";
	public static final String COUNTRY_ID_REQUIRED = "150002";
	public static final String INVALID_COUNTRY_ID = "150003";
	public static final String COUNTRY_WITH_CODE_EXISTS = "150004";
	public static final String COUNTRY_NAME = "150201";
	public static final String COUNTRY_NAME_MAXLENGTH = "150202";
	public static final String COUNTRY_CODE = "150203";
	public static final String COUNTRY_CODE__MAXLENGTH = "150204";
	public static final String CANT_DELETE_COUNTRY = "150005";

	// tat error
	public static final String TAT_NAME_ALREADY_EXISTS = "160001";
	public static final String TAT_ID_REQUIRED = "160002";
	public static final String INVALID_TAT_ID = "160003";
	public static final String TAT_NAME_REQUIRED = "160201";
	public static final String TAT_NAME_MAX_LENGTH = "160202";

	// server error
	public static final String COLOR_CODE_ALREADY_EXISTS = "170001";
	public static final String INVALID_SERVER_ID = "170002";
	/*
	 * public static final String
	 * NO_SUCH_SECTION_EXISTS_FOR_SPECIFIED_RESTAURANT = "120002";
	 */
	public static final String SERVER_GUID_REQUIRED = "170003";
	public static final String INVALID_RESERVATION_EST = "170004";
	public static final String INVALID_RESERVATION_ACT_TIME = "170005";
	public static final String BAD_RESERVATION_ACT_TIME = "170006";
	public static final String INVALID_RESERVATION_EST_TIME = "170007";
	public static final String INVALID_TABLE_RESERVATION_TIME = "170008";
	public static final String INVALID_RESERVATION = "170009";
	public static final String SERVER_ID_ALREADY_EXISTS = "170010";
	public static final String SERVER_NAME_REQUIRED = "170201";
	public static final String SERVER_NAME_MAX_LENGTH = "170202";
	public static final String COLOR_CODE_REQUIRED = "170203";
	public static final String COLOR_CODE_MAX_LENGTH = "170204";
	public static final String SERVER_REST_ID_REQUIRED = "170205";
	public static final String SERVER_ID_REQUIRED = "170206";
	public static final String SERVER_ID_MAX_LENGTH = "170207";

	// Device errors

	public static final String DEVICE_ID_ALREADY_EXISTS = "180001";
	public static final String DEVICE_GUID_REQUIRED = "180002";
	public static final String INVALID_DEVICE_GUID = "180003";
	public static final String INVALID_DEVICE_TYPE = "180004";
	public static final String DEVICE_ID = "180201";
	public static final String DEVICE_ID_MAXLENGTH = "180202";
	public static final String DEVICE_TYPE = "180203";
	public static final String DEVICE_TYPE_MAXLENGTH = "180204";
	public static final String DEVICE_MANUFACTURER = "180205";
	public static final String DEVICE_MANUFACTURER_MAXLENGTH = "180206";
	public static final String DEVICE_REST_ID = "180207";

	public static final String INVALID_TAG_MODEL = "190001";
	public static final String TAG_EXIST = "190002";
	public static final String TAT_VALUE_IS_MISSING = "190003";
	public static final String TAT_VALUE_UNDEFINED = "190004";
	public static final String NO_ALLOW_TO_UPDATE_THIS_RESERVATION_STATUS = "190005";
	// public static final String MORE_THAN_FIVE_FOOD_PREFERENCES = "190006";
	// public static final String MORE_THAN_FIVE_SEATING_PREFERENCES = "190007";
	public static final String TAG_NAME_REQUIRED = "190201";
	public static final String TAG_NAME_MAX_LENGTH = "190202";
	public static final String TAG_TYPE_REQUIRED = "190203";

	public static final String INVALID_TAG_GUID = "190204";

	public static final String MORE_THAN_FIVE_PREFERENCES_BY_GUEST = "190301";
	public static final String MORE_THAN_FIVE_PREFERENCES_BY_REST = "190302";

	// conversation
	public static final String INVALID_ORIGIN = "200001";
	public static final String INVALID_GUEST_ID = "200002";
	public static final String TEMPLATE_MAXLENGTH = "200003";
	public static final String INVALID_TEMPLATE_GUID = "200004";
	public static final String TEMPLATE_SIZE = "200005";
	public static final String GUESTCONVERSATION_REST_GUID = "60201";

	public static final String GUESTCONVERSATION_MESSAGE_REQUIRED = "310202";
	public static final String GUESTCONVERSATION_MESSAGE_MAXLENGTH = "200203";
	public static final String GUESTCONVERSATION_GUEST_GUID = "60202";
	public static final String GUESTCONVERSATION_SENTBY = "200205";
	public static final String GUESTCONVERSATION_ORIGIN = "200206";
	public static final String GUESTCONVERSATION_ORIGIN_ID = "200207";
	public static final String CONVERSATIONS_NOT_FOUND = "200208";

	public static final String SMS_ID_DOES_NOT_EXIST = "200229";	
	// region error
	public static final String INVALID_REGION_ID = "210001";
	public static final String REGION_GUID_REQUIRED = "210201";
	public static final String REGION_STATUS_REQUIRED = "210202";
	public static final String REGION_NAME_REQUIRED = "210203";
	public static final String REGION_CITY_NAME_REQUIRED = "210204";
	public static final String REGION_NAME_MAX_LENGTH = "210205";
	public static final String REGION_CITY_NAME_MAX_LENGTH = "210206";
	public static final String CITY_NAME = "210207";
	public static final String CITY_NAME_MAXLENGTH = "210208";
	public static final String CITY_ZIPCODE = "210209";
	public static final String CITY_ZIPCODE_MAXLENGTH = "210210";
	public static final String CITY_STATECODE = "210211";
	public static final String CITY_STATECODE_MAXLENGTH = "210212";
	public static final String REGION_STATE_CODE_REQUIRED = "210213";
	public static final String REGION_STATE_CODE_MAX_LENGTH = "210214";
	public static final String CANT_DELETE_CITY = "210215";
	public static final String CANT_DELETE_REGION = "210216";

	// locality error
	public static final String INVALID_LOCALITY_ID = "220001";
	public static final String LOCALITY_GUID = "220201";
	public static final String LOCALITY_STATUS = "220202";
	public static final String LOCALITY_NAME_MAX_LENGTH = "220203";
	public static final String LOCALITY_NAME = "220204";
	public static final String LOCALITY_REGION_NAME = "220205";
	public static final String LOCALITY_REGION_NAME_MAX_LENGTH = "220206";
	public static final String LOCALITY_CITY_NAME = "220207";
	public static final String LOCALITY_CITY_NAME_MAX_LENGTH = "220208";
	public static final String CANT_DELETE_LOCALITY = "220209";

	// building error
	public static final String INVALID_BUILDING_ID = "230001";
	public static final String BUILDING_GUID_REQUIRED = "230201";
	public static final String BUILDING_STATUS_REQUIRED = "230202";
	public static final String BUILDING_NAME_REQUIRED = "230203";
	public static final String BUILDING_LOCALITY_NAME_REQUIRED = "230204";
	public static final String BUILDING_NAME_MAX_LENGTH = "230205";
	public static final String BUILDING_LOCALITY_NAME_MAX_LENGTH = "230206";
	public static final String BUILDING_REGION_NAME_REQUIRED = "230207";
	public static final String BUILDING_REGION_NAME_MAX_LENGTH = "230208";

	// Table Assignment Errors
	public static final String INVALID_TABLE_ASSIGNMENT_DETAILS = "240001";
	public static final String INVALID_SERVER_GUID = "240002";
	public static final String INVALID_RESTAURANT_GUID = "240003";

	public static final String TBL_ASSIGNMENT_TBL_GUID_REQUIRED = "240201";
	public static final String TBL_ASSIGNMENT_START_TIME_REQUIRED = "240202";
	public static final String TBL_ASSIGNMENT_END_TIME_REQUIRED = "240203";
	public static final String TBL_ASSIGNMENT_DATE_REQUIRED = "240204";
	public static final String TBL_ASSIGNMENT_SERVER_GUID_REQUIRED = "240205";
	public static final String TBL_ASSIGNMENT_REST_GUID_REQUIRED = "240206";

	// Operational Hours for Reservation
	public static final String MISSING_OPERATIONAL_HOURS = "250001";
	public static final String INVALID_OPERATIONAL_HOURS = "250002";
	public static final String HOLIDAY_HOURS = "250003";
	public static final String BAD_RESERVATION_TIME = "250004";
	public static final String MISSING_RESERVATION_ACT_START_TIME = "250005";
	public static final String MISSING_RESERVATION_ACT_END_TIME = "250006";
	public static final String SHIFT_OPERATIONAL_HOURS = "250007";
	public static final String INVALID_SHIFT_OPERATIONAL_HOURS = "250008";
	public static final String INVALID_SHIFT_NAMES = "250009";
	public static final String INVALID_SHIFT_OPHOURS = "250010";
	public static final String NEGATIVE_SHIFT_OPHOURS = "250011";
	public static final String OVERLAPPIN_SHIFT_OPHOURS = "250012";
	public static final String DINING_SLOT_INTERVAL_MISSING = "250013";
	public static final String INVALID_DINING_SLOT_INTERVAL = "250014";
	public static final String SHIFT_OPHOURS_MISSING = "250015";
	public static final String INVALID_BLACKOUT_OPHR = "250016";
	public static final String BLACK_OVERLAPPIN_SHIFT_OPHOURS = "250017";
	public static final String BLOCK_TABLE = "250018";
	
	// City Error Codes for Model

	// Calendar Event Error Codes for Model

	// state
	public static final String STATE_GUID_REQUIRED = "260201";
	public static final String STATE_STATUS_REQUIRED = "260202";
	public static final String STATE_NAME_REQUIRED = "260203";
	public static final String STATE_NAME_MAX_LENGTH = "260204";
	public static final String STATE_COUNTRY_CODE_REQUIRED = "260205";
	public static final String STATE_COUNTRY_CODE_MAX_LENGTH = "260206";
	public static final String STATE_CODE_REQUIRED = "260207";
	public static final String STATE_CODE_MAX_LENGTH = "260208";
	public static final String CANT_DELETE_STATE = "260209";

	// miscall
	public static final String EXTENSION_REQUIRED = "270201";
	public static final String CAUSE_ID_REQUIRED = "270202";
	public static final String HUNG_UP_REQUIRED = "270203";
	public static final String MSISDN_REQUIRED = "270206";
	public static final String TIMESTAMP_REQUIRED = "270207";
	// landmark
	public static final String LANDMARK_RESTID = "280201";
	// language
	public static final String LANG_NAME = "290201";
	public static final String LANG_NAME_MAX_LENGTH = "290202";
	public static final String LANG_CD_MAX_LENGTH = "290203";

	// Quick Search Reservation
	public static final String COVERS_REQUIRED = "300001";
	public static final String MISSING_OP_HOURS = "300002";
	public static final String NO_SLOT_FOR_THIS_TIME = "300003";
	public static final String MISSING_ALL_TABLES = "300004";

	// EventPromotion
	public static final String VISITED_AFTER_REQUIRED = "310201";
	public static final String MESSAGE_REQUIRED = "310202";
	public static final String MESSAGE_MAXLENGTH = "310203";
	public static final String GUEST_TYPE_REQUIRED = "310204";
	public static final String GUEST_COUNT_REQUIRED = "310205";
	public static final String EVENT_IS_DRAFT_EVENT_PROMOTION = "310206";
	public static final String GUEST_COUNT_MIN = "310207";
	public static final String GENDER_REQUIRED = "310208";
	// public static final String REST_GUID_REQUIRED_EVENT_PROMOTION = "60201";
	public static final String EVENT_GUID_REQUIRED = "140002";

	// Template
	public static final String TEMPLATES_REQUIRED = "320201";
	public static final String REST_GUID_REQUIRED = "60201";

	// AUTHY
	public static final String AUTHY_ID_MISSING = "330001";
	public static final String SMS_NOT_DELIVERED = "330002";

	// Historical Tat
	public static final String SHIFT_REQUIRED = "340001";
	public static final String INVALID_SHIFT = "340002";
	public static final String INVALID_SHIFT_NAME = "340003";

	// Waitlist
	public static final String TIME_REQUIRED = "350001";
	public static final String TODAY_IS_HOLIDAY = "350002";
	public static final String NO_TABLE_AVAILABE = "350003";
	public static final String MASTER_DATA_IS_MISSING = "350004";

	public static final String ACCESS_TOKEN_MISSING = "370001";
	public static final String LOG_OUT_FAILURE = "370002";

	// support
	public static final String SUBJECT_REQUIRED_SUPPORT = "360001";
	public static final String ACCOUNT_ID_REQUIRED_SUPPORT = "360002";
	public static final String RESTAURANT_NAME_REQUIRED_SUPPORT = "360003";
	public static final String USERNAME_REQUIRED_SUPPORT = "360004";
	public static final String DEVICE_REQUIRED_SUPPORT = "360005";
	public static final String OS_REQUIRED_SUPPORT = "360006";
	public static final String ISSUE_TYPE_REQUIRED_SUPPORT = "360007";
	public static final String DESCRIPTION_REQUIRED_SUPPORT = "360008";
	public static final String ATTACHMENT_MAX = "360009";
	public static final String INVALID_ATTACHMENT = "360010";
	public static final String ATTACHMENT_REQUIRED = "360011";
	public static final String SUBJECT_MAXLENGTH_SUPPORT = "360012";
	public static final String INVALID_USERNAME_SUPPORT = "360013";
	public static final String DEVICE_MAXLENGTH_SUPPORT = "360014";
	public static final String OS_MAXLENGTH_SUPPORT = "360015";
	public static final String DESCRIPTION_MAXLENGTH_SUPPORT = "360016";
	public static final String INVALID_FILE_NAME = "360017";
	public static final String FILE_SIZE = "360018";
	public static final String INVALID_ISSUE_TYPE = "360019";

	// Restaurant Config
	public static final String OTP_MOBILE_REQUIRED = "380001";
	public static final String TAT_WD_12_REQUIRED = "380002";
	public static final String TAT_WE_12_REQUIRED = "380003";
	public static final String TAT_WD_34_REQUIRED = "380004";
	public static final String TAT_WE_34_REQUIRED = "380005";
	public static final String TAT_WD_56_REQUIRED = "380006";
	public static final String TAT_WE_56_REQUIRED = "380007";
	public static final String TAT_WD_78_REQUIRED = "380008";
	public static final String TAT_WE_78_REQUIRED = "380009";
	public static final String TAT_WD_8P_REQUIRED = "380010";
	public static final String TAT_WE_8P_REQUIRED = "380011";
	public static final String FORCED_SHIFT_END_TIME_FORMAT = "380012";

	// Onboarding reason to reject
	public static final String REASON_TO_REJECT_REQUIRED = "70231";
	public static final String ROLE_ID_EXISTS = "80203";
	public static final String ROLE_NAME_EXISTS = "80204";
	public static final String DUMMY_GUEST_ID_NOT_FOUND = "80205";

	// User Token
	public static final String TOKEN_ALREADY_EXISTS = "390201";
	public static final String REFRESH_TOKEN_EXPIRED = "403";
	public static final String INVALID_ACCESS_TOKEN = "402";
	public static final String INVALID_ACCESS_EXPIRED = "401";
	public static final String AUTH_TOKEN_MISSING = "404";
	public static final String INVALID_AUTH_TOKEN = "405";

	// Address Apis
	public static final String INVALID_STATE_GUID = "260210";
	public static final String COUNTRY_CODE_NOT_EDITABLE = "260211";
	public static final String COUNTRY_WITH_CODE_NOT_EXISTS = "260212";
	public static final String STATE_ALREADY_EXISTS = "260213";
	public static final String INVALID_CITY_GUID = "210217";
	public static final String STATE_CODE_NOT_EDITABLE = "210218";
	public static final String STATE_WITH_CODE_NOT_EXIST = "210219";
	public static final String REGION_ALREADY_EXISTS = "210220";
	public static final String CITY_WITH_NAME_NOT_EXISTS = "210221";
	public static final String CITY_NAME_NOT_EDITABLE = "210222";
	public static final String REGION_WITH_NAME_NOT_EXISTS = "210223";
	public static final String REGION_NAME_NOT_EDITABLE = "210224";
	public static final String LOCALITY_NAME_NOT_EDITABLE = "210225";
	public static final String BUILDING_ALREADY_EXISTS = "210226";
	public static final String LOCALITY_WITH_NAME_NOT_EXISTS = "210227";
	public static final String CITY_ALREADY_EXISTS = "210228";
	public static final String LOCALITY_ALREADY_EXISTS = "210229";
	public static final String STATE_WITH_CODE_EXISTS = "210230";
	public static final String CITY_GUID_REQUIRED = "210231";

	// Weather
	public static final String LAT_LONG_NOT_CONFIGURED = "400201";

	public static final String NOT_FOUND = "406";

	// Corporation
	public static final String CORPORATION_WITH_SAME_WEBSITE_ALREADY_EXISTS = "410008";
	public static final String INVALID_CORPORATION_GUID = "410009";

	// Coperate Module
	public static final String CORPORATE_NAME = "410001";
	public static final String CORPORATE_NAME_MAXLENGTH = "410002";
	public static final String CORPORATE_MESSAGE = "410003";
	public static final String CORPORATE_MESSAGE_MAXLENGTH = "410004";
	public static final String CORPORATE_DISCOUNT = "410005";
	public static final String CORPORATE_OFFER_START_DT = "410006";
	public static final String CORPORATE_OFFER_END_DT = "410007";
	public static final String INVALID_CORPORATE_OFFERS_GUID = "410010";
	// corporate offers
	public static final String CORPORATE_OFFERS_WITH_SAME_NAME_ALREADY_EXISTS = "410011";
	public static final String CORPORATE_OFFERS_NOTE_MAXLENGTH = "420003";
	public static final String CORPORATE_OFFERS_REST_GUID_REQUIRED = "60201";
	public static final String CORPORATE_OFFERS_NAME_REQUIRED = "410012";
	public static final String CORPORATE_OFFERS_NAME_MAXLENGTH = "410013";
	public static final String CORPORATE_OFFERS_NAME_MINLENGTH = "410014";
	public static final String CORPORATE_OFFERS_OFFER_REQUIRED = "410015";
	public static final String CORPORATE_OFFERS_OFFER_MAXLENGTH = "410016";
	public static final String CORPORATE_OFFERS_OFFER_MINLENGTH = "410018";

	// BarEntry
	public static final String NUM_COVERS_REQUIRED = "420001";
	public static final String NUM_COVERS_MIN_VALUE = "420002";
	public static final String BAR_ENTRY_REST_GUID_REQUIRED = "60201";
	public static final String BAR_ENTRY_GUEST_GUID_REQUIRED = "60202";
	public static final String BAR_ENTRY_NOTE_MAXLENGTH = "420003";
	public static final String BAR_TIME_MIN_VALUE = "420004";
	public static final String BAR_TIME_MAX_VALUE = "420005";
	public static final String RESTAURANT_NOT_CONFIGURED_FOR_BAR = "420006";
	public static final String INVALID_END_TIME = "420007";
	public static final String INVALID_WAITLIST_GUID = "420008";
	public static final String NOTHING_TO_UPDATE = "420009";
	public static final String CANT_UPDATE_FINISHED_BARENTRY = "420010";
	public static final String BAR_ENTRY_GUID_REQUIRED = "420011";
	public static final String BAR_ENTRY_GUID_INVALID = "420012";

	public static final String GUEST_GUID_REQUIRED = "190205";
	public static final String TAG_GUID_REQUIRED = "190206";
	public static final String CORPORATE_OFFERS_DOESNT_EXIST = "410017";
	public static final String INVALID_TAG_TYPE_TO_REMOVE = "190207";
	public static final String TAG_EXIST_WITH_GUEST = "190208";
	public static final String TAG_EXIST_IN_RESTAURANT = "190209";

	// reporting
	public static final String EMAIL_MAX_LENGTH = "430001";
	public static final String MOBILE_MIN_LENGTH = "430002";
	public static final String MOBILE_MAX_LENGTH = "430002";
	public static final String NAME_MIN_LENGTH = "430003";
	public static final String NAME_MAX_LENGTH = "430004";
	public static final String SALES_PERSON_EMAIL_REQUIRED = "430005";
	public static final String SALES_PERSON_NAME_REQUIRED = "430006";
	public static final String REPORTING_PREFERENCE_EXISTS = "430007";
	
	
	//application details
	public static final String INVALID_APPLICATION_NAME = "440001";
	public static final String INVALID_PLATFORM = "440002";
	

	// invoice
	public static final String INVOICE_GUID_REQUIRED = "500001";
	public static final String INVOICE_ID_REQUIRED = "500002";
	public static final String INVOICE_NUM_REQUIRED = "500003";
	public static final String INVOICE_MESSAGE_MAXLENGTH = "500004";
	public static final String INVOICE_REST_GUID_REQUIRED = "500005";
	public static final String INVOICE_OWNER_NAME_REQUIRED = "500006";
	public static final String INVOICE_OWNER_ADDRESS_REQUIRED = "500007";
	public static final String INVOICE_OWNER_EMAIL_REQUIRED = "500008";
	public static final String INVOICE_OWNER_PHONE_REQUIRED = "500009";
	public static final String INVOICE_DATE_REQUIRED = "500010";
	public static final String INVOICE_TAX_AMOUNT_REQUIRED = "500011";
	public static final String INVOICE_BASIC_AMOUNT_REQUIRED = "500012";
	public static final String INVOICE_DISCOUNT_REQUIRED = "500013";
	public static final String INVOICE_AMOUNT_REQUIRED = "500014";
	public static final String INVOICE_STATUS_REQUIRED = "500015";
	public static final String INVOICE_CURRENCY_REQUIRED = "500016";
	public static final String INVOICE_TAX_YEAR_REQUIRED = "500017";
	public static final String INVOICE_ROUND_AMOUNT_REQUIRED = "500018";
	public static final String INVOICE_DUE_DATE_REQUIRED = "500019";
	public static final String INVOICE_INV_TO_COUNTRY_REQUIRED = "500020";
	public static final String INVOICE_INV_TO_STATE_REQUIRED = "500021";

	// INVOICE MAX AND MIN

	public static final String INVOICE_ID_MAX_VALUE = "500022";
	public static final String INVOICE_ID_MIN_VALUE = "500023";
	public static final String INVOICE_TAX_AMOUNT_MAX_VALUE = "500024";
	public static final String INVOICE_TAX_AMOUNT_MIN_VALUE = "500025";
	public static final String INVOICE_DISCOUNT_MAX_VALUE = "500026";
	public static final String INVOICE_DISCOUNT_MIN_VALUE = "500027";
	public static final String INVOICE_AMOUNT_MAX_VALUE = "500028";
	public static final String INVOICE_AMOUNT_MIN_VALUE = "500029";

	public static final String INVOICE_TAX_YEAR_MAX_VALUE = "500030";
	public static final String INVOICE_TAX_YEAR_MIN_VALUE = "500031";
	public static final String INVOICE_ROUND_AMOUNT_MAX_VALUE = "500032";
	public static final String INVOICE_ROUND_AMOUNT_MIN_VALUE = "500033";

	public static final String INVOICE_GUID_MAXLENGTH = "500004";
	public static final String INVOICE_NUM_MAXLENGTH = "500004";
	public static final String INVOICE_REST_GUID_MAXLENGTH = "500004";
	public static final String INVOICE_OWNER_NAME_MAXLENGTH = "500004";
	public static final String INVOICE_OWNER_ADDRESS_MAXLENGTH = "500004";
	public static final String INVOICE_OWNER_EMAIL_MAXLENGTH = "500004";
	public static final String INVOICE_OWNER_PHONE_MAXLENGTH = "500004";
	public static final String INVOICE_STATUS_MAXLENGTH = "500004";
	public static final String INVOICE_REMARKS_MAXLENGTH = "500004";

	public static final String INVOICE_CONVERSATION_COUNT_REQUIRED = "500004";
	public static final String INVOICE_CONVERSATION_COUNT_MAX_VALUE = "500004";
	public static final String INVOICE_CONVERSATION_COUNT_MIN_VALUE = "500004";

	public static final String INVOICE_CONVERSATION_MONTH_REQUIRED = "500004";
	public static final String INVOICE_CONVERSATION_MONTH_MAX_VALUE = "500004";
	public static final String INVOICE_CONVERSATION_MONTH_MIN_VALUE = "500004";
	public static final String INVOICE_CONVERSATION_YEAR_REQUIRED = "500004";
	public static final String INVOICE_CONVERSATION_YEAR_MAX_VALUE = "500004";
	public static final String INVOICE_CONVERSATION_YEAR_MIN_VALUE = "500004";

	// TAX
	public static final String INVOICE_CONVERSATION_QUANTITY_REQUIRED = "600001";
	public static final String INVOICE_UNIT_PRICE_REQUIRED = "600002";
	public static final String INVOICE_TAX_CODE_REQUIRED = "600003";
	public static final String INVOICE_SENT_DATE_REQUIRED = "600004";
	public static final String INVOICE_CONVERSATION_TAX_AMOUNT_REQUIRED = "600005";
	public static final String INVOICE_SUBTOTAL_REQUIRED = "600006";

	public static final String INVOICE_CONVERSATION_QUANTITY_MAX_VALUE = "600007";
	public static final String INVOICE_CONVERSATION_QUANTITY_MIN_VALUE = "600008";
	public static final String INVOICE_UNIT_PRICE_MAX_VALUE = "600009";
	public static final String INVOICE_UNIT_PRICE_MIN_VALUE = "600010";
	public static final String INVOICE_TAX_AMOUNT_TXN_MAX_VALUE = "600011";
	public static final String INVOICE_TAX_AMOUNT_TXN_MIN_VALUE = "600012";

	public static final String INVOICE_SUBTOTAL_MAX_VALUE = "600013";
	public static final String INVOICE_SUBTOTAL_MIN_VALUE = "600014";

	public static final String INVOICE_TAX_CODE_MAXLENGTH = "600015";

	public static final String INVOICE_TAX_ID_REQUIRED = "600015";
	public static final String INVOICE_TAX_ID_MAX_VALUE = "600016";
	public static final String INVOICE_TAX_ID_MIN_VALUE = "600017";

	public static final String INVOICE_TAX_COUNTRY_REQUIRED = "600018";
	public static final String INVOICE_TAX_STATE_REQUIRED = "600019";
	public static final String INVOICE_TAX_RATE_REQUIRED = "600020";
	public static final String INVOICE_TAX_RATE_MAX_VALUE = "600021";
	public static final String INVOICE_TAX_RATE_MIN_VALUE = "600022";
	public static final String INVOICE_UNBILLED_AMOUNT_REQUIRED = "600023";

	public static final String INVOICE_UNBILLED_MAX_VALUE = "600024";
	public static final String INVOICE_UNBILLED_MIN_VALUE = "600025";

	// Category

	public static final String INVOICE_CATEGORY_CODE_REQUIRED = "700001";
	public static final String INVOICE_CATEGORY_NAME_REQUIRED = "700002";
	public static final String INVOICE_CATEGORY_VALID_FROM_REQUIRED = "700003";
	public static final String INVOICE_CATEGORY_VALID_TILL_REQUIRED = "700004";
	public static final String INVOICE_CATEGORY_STATUS_REQUIRED = "700005";

	public static final String INVOICE_CATEGORY_CODE_MAXLENGTH = "700006";
	public static final String INVOICE_CATEGORY_NAME_MAXLENGTH = "700007";
	public static final String INVOICE_CATEGORY_STATUS_MAXLENGTH = "700008";

	public static final String INVOICE_CAT_ID_REQUIRED = "700009";
	public static final String INVOICE_CAT_ID_MAX_VALUE = "700010";
	public static final String INVOICE_CAT_ID_MIN_VALUE = "700011";

	public static final String PREFERENCE_NAME_MAX_LENGTH = "800001";
	public static final String PREFERENCE_NAME_REQUIRED = "800002";
	public static final String PREFERENCE_ALREADY_EXISTS = "800003";
	public static final String GUESTPROFILE_DOES_NOT_EXISTS = "800004";
	
	//Restaurant De-activation API
	public static final String RESTAURANT_ALREADY_INACTIVE = "30308";
	public static final String DELETED_RESTAURANT_CAN_NOT_BE_DEACTIVATED = "30309";
	public static final String DELETED_RESTAURANT_CAN_NOT_BE_DELETED = "30310";
	public static final String RESTAURANT_ALREADY_ACTIVE = "30311";
	public static final String RESTAURANT_HAS_NOT_STAFF_MEMBERS = "30312";

	public static final String EMAIL_REQUIRED = "30313";
	
	//Manual Scheduler Job
	public static final String SCHEDULER_NAME_IS_REQUIRED = "31001";
	public static final String JOB_ALREADY_RUNNING = "31002";
	

}
