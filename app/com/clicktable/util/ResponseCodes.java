package com.clicktable.util;

public class ResponseCodes {

	// Basic Responses
	public static final String OK = "200";
	public static final String NO_CONTENT = "204";
	public static final String MOVED_PERMANENTLY = "301";
	public static final String MOVED_TEMPORARILY = "302";
	public static final String UNAUTHORIZED = "401";
	public static final String PERMISSION_DENIED = "402";

	public static final String FORBIDDEN = "403";
	public static final String NOT_FOUND = "404";
	public static final String INTERNAL_SERVER_ERROR = "500";
	public static final String NOT_IMPLEMENTED = "501";
	public static final String SERVICE_UNAVAILABLE = "503";
	public static final String ROUTE_NOT_FOUND = "601";
	public static final String BAD_REQUEST = "602";
	public static final String ACCESS_TOKEN_MISSING = "700";
	public static final String INVALID_ACCESS_TOKEN = "701";

	public static Integer CUSTOMER_GET_SUCCESS = 101;

	// Customer Responses
	public static final String CUSTOMER_RECORD_FETCH_SUCCESFULLY = "1001";
	public static final String SOCIAL_LOGIN_SUCCESS = "1002";
	public static final String SOCIAL_LOGIN_FAILURE = "1003";
	public static final String CUSTOMER_UPDATED_SUCCESS = "1004";
	public static final String CUSTOMER_UPDATED_FAILURE = "1005";
	public static final String NO_USER_FOUND = "1006";
	public static final String SOCIAL_LOGIN_FAILURE_WRONG_PASSWORD = "1007";
	public static final String SOCIAL_LOGIN_FAILURE_INVALID_USERNAME = "1008";
	public static final String CUSTOMER_ADDED_SUCCESFULLY = "1009";
	public static final String CUSTOMER_ADDED_FAILURE = "1010";
	public static final String SOCIAL_LOGIN_FAILURE_INVALID_ACCOUNT = "1011";
	public static final String GUESTBOOK_DATA_FETCH_SUCCESFULLY = "1012";
	public static final String GUESTBOOK_DATA_FETCH_FAILURE = "1013";
	public static final String GUESTBOOK_DATA_FETCH_FAILURE_EMPTY_LIST = "1014";
	public static final String SOCIAL_LOGIN_FAILURE_INVALID_USER = "1015";
	public static final String SOCIAL_LOGIN_FAILURE_INVALID_OTP = "1016";
	public static final String SOCIAL_LOGIN_SUCCESS_VALID_OTP = "1017";
	public static final String CUSTOMERS_CSV_PROCESSING = "1018";
	public static final String CUSTOMERS_CSV_FAILURE = "1019";
	public static final String CUSTOMERS_CSV_EPORTING = "1020";
	public static final String STAFF_LOGIN_FAILURE = "1021";

	public static final String GUEST_CSV_FETCH_SUCCESFULLY = "1022";
	public static final String GUEST_CSV_FETCH_FAILURE = "1023";
	public static final String GUEST_FILE_FETCH_FAILURE = "1024";
	public static final String GUEST_FILE_FETCH_SUCCESS = "1025";
	public static final String INVOICE_FILE_FETCH_SUCCESS = "1125";
	public static final String INVOICE_FILE_FETCH_FAILURE = "1126";

	public static final String SOCIAL_LOGIN_FAILURE_USER_LOGGED_OUT = "1026";


	// Restaurant Responses
	public static final String RESTAURANT_RECORD_FETCH_SUCCESFULLY = "2001";
	public static final String RESTAURANT_ADDED_SUCCESFULLY = "2002";
	public static final String RESTAURANT_UPDATED_SUCCESFULLY = "2003";
	public static final String RESTAURANT_ADDED_FAILURE = "2004";
	public static final String RESTAURANT_UPDATION_FAILURE = "2005";
	public static final String RESTAURANT_SYSTEM_CONFIGURATION_FETCH_SUCCESFULLY = "2006";
	public static final String RESTAURANT_SYSTEM_CONFIGURATION_FETCH_FAILURE = "2007";
	public static final String RESTAURANT_SYSTEM_CONFIGURATION_UPDATED_SUCCESFULLY = "2008";
	public static final String RESTAURANT_SYSTEM_CONFIGURATION_UPDATION_FAILURE = "2009";
	public static final String RESTAURANT_ATTRIBUTES_UPDATED_SUCCESFULLY = "2010";
	public static final String RESTAURANT_ATTRIBUTES_UPDATION_FAILURE = "2011";
	public static final String REST_OTP_MOBILE = "2012";
	public static final String RESTAURANT_DATA_DELETED_SUCCESFULLY = "2013";
	public static final String RESTAURANT_DATA_DELETION_FAILURE = "2014";

	// StormPath Response
	public static final String STORMPATH_RESOURCE_EXCEPTION = "3001";

	// Table Responses
	public static final String TABLE_RECORD_FETCH_SUCCESFULLY = "4001";
	public static final String TABLE_ADDED_SUCCESFULLY = "4002";
	public static final String TABLE_UPDATED_SUCCESFULLY = "4003";
	public static final String TABLE_ADDED_FAILURE = "4004";
	public static final String TABLE_UPDATED_FAILURE = "4005";
	public static final String TABLE_DELETED_SUCCESFULLY = "4006";
	public static final String TABLE_DELETION_FAILURE = "4007";

	// Staff Responses
	public static final String STAFF_RECORD_FETCH_SUCCESFULLY = "5001";
	public static final String STAFF_ADDED_SUCCESFULLY = "5002";
	public static final String STAFF_UPDATED_SUCCESFULLY = "5003";
	public static final String STAFF_ADDED_FAILURE = "5004";
	public static final String STAFF_UPDATION_FAILURE = "5005";
	public static final String EMAIL_SENT_SUCCESSFULLY = "5006";
	public static final String EMAIL_SENT_FAILURE = "5007";
	public static final String STAFF_DELETED_SUCCESFULLY = "5008";
	public static final String STAFF_DELETION_FAILURE = "5009";

	// password change responsee
	public static final String PASSWORD_CHANGED_SUCCESFULLY = "6001";
	public static final String PASSWORD_CHANGE_FAILURE = "6002";

	// Cuisine Responses
	public static final String CUISINE_RECORD_FETCH_SUCCESFULLY = "7001";
	public static final String CUISINE_ADDED_SUCCESFULLY = "7002";
	public static final String CUISINE_UPDATED_SUCCESFULLY = "7003";
	public static final String CUISINE_ADDED_FAILURE = "7004";
	public static final String CUISINE_UPDATED_FAILURE = "7005";
	public static final String CUISINE_REMOVED_SUCCESFULLY = "7006";
	public static final String CUISINE_REMOVED_FAILURE = "7007";

	// Onboarding Responses
	public static final String ONBOARD_REQUEST_SUCCESFUL = "8001";
	public static final String ONBOARD_REQUEST_UPDATE_SUCCESFUL = "8002";
	public static final String ONBOARD_REQUEST_ADD_FAILURE = "8003";
	public static final String ONBOARD_REQUEST_UPDATE_FAILURE = "8004";
	public static final String ONBOARD_RECORD_FETCH_SUCCESFULLY = "8005";
	public static final String CODE_VERIFICATION_FAILURE = "8006";
	public static final String CODE_VERIFIED_SUCCESFULLY = "8007";
	public static final String CODE_RESEND_FAILURE = "8008";
	public static final String CODE_RESEND_SUCCESFULLY = "8009";

	// Reservation Responses
	public static final String RESERVATION_RECORD_FETCH_SUCCESFULLY = "9001";
	public static final String RESERVATION_ADDED_SUCCESFULLY = "9002";
	public static final String RESERVATION_UPDATED_SUCCESFULLY = "9003";
	public static final String RESERVATION_ADDED_FAILURE = "9004";
	public static final String RESERVATION_UPDATED_FAILURE = "9005";
	public static final String RESERVATION_RECORD_FETCH_FAILURE = "9006";
	public static final String RESERVATION_CSV_FETCH_FAILURE = "9007";
	public static final String RESERVATION_CSV_FETCH_SUCCESFULLY = "9008";
	public static final String RESERVATION_FILE_FETCH_FAILURE = "9011";
	public static final String RESERVATION_FILE_FETCH_SUCCESFULLY = "9012";
	public static final String RESERVATION_UPDATION_FAILURE_DUE_TO_SEATED = "9999"; // unique
																					// code

	// Note Responses
	public static final String NOTES_FETCH_SUCCESFULLY = "10001";
	public static final String NOTE_ADDED_SUCCESFULLY = "10002";
	public static final String NOTE_UPDATED_SUCCESFULLY = "10003";
	public static final String NOTE_ADDED_FAILURE = "10004";
	public static final String NOTE_UPDATED_FAILURE = "10005";
	public static final String NOTE_DEL_SUCCESFULLY = "10006";
	public static final String NOTE_DEL_FAILURE = "10007";

	public static final String GUEST_TAG_UPDATED_REL_FAILED = "110002";
	public static final String GUEST_TAG_UPDATED_REL_SUCCESS = "110003";
	public static final String GUEST_TAG_DELETED_REL_SUCCESS = "110005";
	public static final String GUEST_TAG_DELETED_REL_FAILED = "110009";
	public static final String GUEST_TAG_FETCH_REL_FAILED = "110007";
	public static final String GUEST_TAG_FETCH_REL_SUCCESS = "110008";

	// Section Responses
	public static final String SECTION_RECORD_FETCH_SUCCESFULLY = "1201";
	public static final String SECTION_ADDED_SUCCESFULLY = "1202";
	public static final String SECTION_UPDATED_SUCCESFULLY = "1203";
	public static final String SECTION_ADDED_FAILURE = "1204";
	public static final String SECTION_UPDATION_FAILURE = "1205";
	public static final String SECTION_DELETED_SUCCESFULLY = "1206";
	public static final String SECTION_DELETED_FAILURE = "1207";

	// Attribute Responses
	public static final String ATTRIBUTE_RECORD_FETCH_SUCCESFULLY = "1301";
	public static final String ATTRIBUTE_ADDED_SUCCESFULLY = "1302";
	public static final String ATTRIBUTE_UPDATED_SUCCESFULLY = "1303";
	public static final String ATTRIBUTE_ADDED_FAILURE = "1304";
	public static final String ATTRIBUTE_UPDATION_FAILURE = "1305";
	public static final String RELATIONSHIP_WITH_COUNTRY_CREATED_SUCCESFULLY = "1306";
	public static final String RELATIONSHIP_WITH_COUNTRY_CREATION_FAILURE = "1307";
	public static final String ATTRIBUTE_RECORD_FETCH_FAILURE = "1308";

	// Event Responses
	public static final String EVENT_FETCH_SUCCESFULLY = "16001";
	public static final String EVENT_ADDED_SUCCESFULLY = "16002";
	public static final String EVENT_UPDATED_SUCCESFULLY = "16003";
	public static final String EVENT_ADDED_FAILURE = "16004";
	public static final String EVENT_UPDATION_FAILURE = "16005";
	public static final String EVENT_DELETED_SUCCESFULLY = "16006";
	public static final String CALEVENT_DELETED_SUCCESSFULLY = "16007";
	public static final String CALEVENT_DELETION_FAILURE = "16008";
	public static final String CALEVENT_UPDATED_SUCCESSFULLY = "16009";
	public static final String CALEVENT_UPDATION_FAILURE = "16010";
	public static final String EVENT_DELETION_FAILURE= "16011";
	public static final String CALEVENT_CSV_FETCH_FAILURE = "9007";
	public static final String CALEVENT_CSV_FETCH_SUCCESFULLY = "9008";
	
	public static final String CALEVENT_FILE_FETCH_FAILURE = "9009";
	public static final String CALEVENT_FILE_FETCH_SUCCESFULLY = "9010";

	// Country Responses
	public static final String COUNTRY_RECORD_FETCH_SUCCESFULLY = "1501";
	public static final String COUNTRY_ADDED_SUCCESFULLY = "1502";
	public static final String COUNTRY_UPDATED_SUCCESFULLY = "1503";
	public static final String COUNTRY_ADDED_FAILURE = "1504";
	public static final String COUNTRY_UPDATION_FAILURE = "1505";

	// Tat Responses
	public static final String TAT_RECORD_FETCH_SUCCESFULLY = "1601";
	public static final String TAT_ADDED_SUCCESFULLY = "1602";
	public static final String TAT_UPDATED_SUCCESFULLY = "1603";
	public static final String TAT_ADDED_FAILURE = "1604";
	public static final String TAT_UPDATION_FAILURE = "1605";

	// Server Responses
	public static final String SERVER_RECORD_FETCH_SUCCESFULLY = "1701";
	public static final String SERVER_ADDED_SUCCESFULLY = "1702";
	public static final String SERVER_UPDATED_SUCCESFULLY = "1703";
	public static final String SERVER_ADDED_FAILURE = "1704";
	public static final String SERVER_UPDATION_FAILURE = "1705";
	public static final String SERVER_DELETED_SUCCESFULLY = "1706";
	public static final String SERVER_DELETED_FAILURE = "1707";
	public static final String SERVER_RECORD_FETCH_FAILURE = "1708";

	// Table Assignment Responses
	public static final String TABLE_ASSIGNMENT_FETCH_SUCCESFULLY = "1801";
	public static final String TABLES_ASSIGNED_SUCCESFULLY = "1802";
	public static final String TABLE_ASSIGNMENT_UPDATED_SUCCESFULLY = "1803";
	public static final String TABLE_ASSIGNMENT_FAILURE = "1804";
	public static final String TABLE_ASSIGNMENT_UPDATION_FAILURE = "1805";
	public static final String TABLES_UNASSIGNED_SUCCESFULLY = "1806";
	public static final String TABLE_UNASSIGNMENT_FAILURE = "1807";
	public static final String TABLE_ASSIGNMENT_FETCH_FAILURE = "1808";
	public static final String TABLE_ASSIGNMENT_FETCH_FAILURE_EMPTY_LIST = "1809";

	// Language Responses
	public static final String LANGUAGE_RECORD_FETCH_SUCCESFULLY = "1901";

	// City Response
	public static final String CITY_RECORD_FETCH_SUCCESFULLY = "2101";
	public static final String CITY_ADDED_SUCCESFULLY = "2102";
	public static final String CITY_ADDITION_FAILURE = "2104";

	// State Response
	public static final String STATE_RECORD_FETCH_SUCCESFULLY = "2201";
	public static final String STATE_ADDED_SUCCESFULLY = "2202";
	public static final String STATE_ADDITION_FAILURE = "2204";

	public static final String REGION_RECORD_FETCH_SUCCESFULLY = "2301";
	public static final String REGION_ADDED_SUCCESFULLY = "2302";
	public static final String REGION_ADDITION_FAILURE = "2304";

	// Locality Response
	public static final String LOCALITY_RECORD_FETCH_SUCCESFULLY = "2401";
	public static final String LOCALITY_ADDED_SUCCESFULLY = "2402";
	public static final String LOCALITY_ADDITION_FAILURE = "2404";

	// Building Response
	public static final String BUILDING_RECORD_FETCH_SUCCESFULLY = "2501";
	public static final String BUILDING_ADDED_SUCCESFULLY = "2508";
	public static final String BUILDING_ADDITION_FAILURE = "2509";

	public static final String TAG_ADD_REL_SUCCESS = "2502";
	public static final String TAG_ADD_REL_FAILED = "2503";
	public static final String TAG_DELETED_REL_SUCCESS = "2504";
	public static final String TAG_DELETED_REL_FAILED = "2505";
	public static final String TAG_FETCH_SUCCESS = "2506";
	public static final String TAG_FETCH_FAILED = "2507";

	// Device Responses
	public static final String DEVICE_RECORD_FETCH_SUCCESFULLY = "2601";
	public static final String DEVICE_ADDED_SUCCESFULLY = "2602";
	public static final String DEVICE_UPDATED_SUCCESFULLY = "2603";
	public static final String DEVICE_ADDED_FAILURE = "2604";
	public static final String DEVICE_UPDATION_FAILURE = "2605";
	public static final String DEVICE_DELETED_SUCCESFULLY = "2606";
	public static final String DEVICE_DELETED_FAILURE = "2607";
	// public static final String RELATIONSHIP_WITH_COUNTRY_CREATED_SUCCESFULLY
	// = "1306";
	// public static final String RELATIONSHIP_WITH_COUNTRY_CREATION_FAILURE =
	// "1307";

	public static final String QUICK_SEARCH_RECORD_FETCH_SUCCESFULLY = "2608";
	public static final String QUICK_SEARCH_RECORD_FETCH_FAILURE = "2609";

	// Conversation Responses
	public static final String CONVERSATION_FETCH_SUCCESFULLY = "2701";
	public static final String CONVERSATION_ADDED_SUCCESFULLY = "2702";
	public static final String CONVERSATION_INITIATD_SUCCESFULLY = "2720";
	public static final String CONVERSATION_ADDED_FAILURE = "2703";
	public static final String RESERVATION_CANCEL_SMS = "2704";
	public static final String RESERVATION_CONFIRM_SMS = "2705";
	public static final String EVENT_PROMOTION_FETCH_SUCCESFULLY = "2706";
	public static final String EVENT_PROMOTION_ADDED_SUCCESFULLY = "2707";
	public static final String EVENT_PROMOTION_ADDED_FAILURE = "2708";
	public static final String TEMPLATE_FETCH_SUCCESFULLY = "2709";
	public static final String TEMPLATE_ADDED_SUCCESFULLY = "2710";
	public static final String TEMPLATE_ADDED_FAILURE = "2711";
	public static final String TEMPLATE_UPDATED_SUCCESFULLY = "2712";
	public static final String TEMPLATE_UPDATED_FAILURE = "2713";
	public static final String EVENT_PROMOTION_COUNT_FETCH_SUCCESFULLY = "2714";
	public static final String EVENT_PROMOTION_COUNT_FAILURE = "2715";
	public static final String CONVERSATION_REPORT_FAILURE = "2716";
	
//eventPromotion
	public static final String FILTERED_GUEST_FETCHED_SUCCESSFULLY = "2718";
	public static final String FILTERED_GUEST_CAN_NOT_BE_FECTHED = "2719";
	
	// Role responses
	public static final String ROLE_RECORD_FETCH_SUCCESFULLY = "2801";
	public static final String ROLE_ADDED_SUCCESFULLY = "2802";
	public static final String ROLE_ADDITION_FAILURE = "2803";

	// Enum Response
	public static final String ENUM_DATA_FETCH_SUCCESFULLY = "2901";

	// Auth Response
	public static final String AUTHY_ID_RETRIEVING_SUCCESS = "3001";
	public static final String AUTHY_ID_RETRIEVING_FAILURE = "3002";
	public static final String AUTHY_FAILURE = "3003";
	public static final String SMS_SENT = "3004";
	public static final String SMS_TOKEN_VERIFIED = "3005";
	public static final String SMS_TOKEN_INAVALID = "3006";
	public static final String SMS_ERROR = "3007";

	// Historical Tat Response
	public static final String HISTORICAL_TAT_VALUE_FETCH_SUCCESFULLY = "3101";
	public static final String HISTORICAL_TAT_VALUE_FETCH_FAILURE = "3102";

	// Operational Hours Responses
	public static final String OPERATIONAL_HOUR_RECORD_ADDED_FAILURE = "5051";
	public static final String OPERATIONAL_HOUR_RECORD_ADDED_SUCCESS = "5052";
	public static final String OPERATIONAL_HOUR_RECORD_FETCH_FAILURE = "5053";
	public static final String OPERATIONAL_HOUR_RECORD_FETCH_SUCCESS = "5054";
	public static final String BLACK_OUT_OPERATIONAL_HOUR_RECORD_FETCH_SUCCESS = "5055";
	public static final String BLACK_OUT_OPERATIONAL_HOUR_RECORD_FETCH_FAILURE = "5056";
	public static final String BLACK_OUT_OPERATIONAL_HOUR_RECORD_ADDED_SUCCESS = "5057";
	public static final String BLACK_OUT_OPERATIONAL_HOUR_RECORD_ADDED_FAILURE = "5058";
	public static final String OPHR_UPDATION_FAILURE_DUE_TO_RESV = "5059";

	// Waitlist
	public static final String WAITLIST_RECORD_FETCH_SUCCESFULLY = "3201";
	public static final String WAITLIST_RECORD_FETCH_FAILURE = "3202";
	public static final String WAITLIST_ADDED_SUCCESFULLY = "3203";
	public static final String WAITLIST_ADDED_FAILURE = "3204";
	public static final String WAITLIST_REMOVE_FAILURE = "3205";
	public static final String WAITLIST_REMOVED_SUCCESFULLY = "3206";

	// support
	public static final String TICKET_SUCCESS = "3301";
	public static final String TICKET_FAILURE = "3302";
	public static final String ATTACHMENT_SUCCESS = "3303";
	public static final String ATTACHMENT_FAILURE = "3304";
	public static final String ATTACHMENT_DELETED_SUCCESFULLY = "3305";
	public static final String TOKEN_SUCCESS = "3306";
	// Log Out
	public static final String LOGGED_OUT_SUCCESSFULLY = "3401";
	public static final String LOG_OUT_FAILURE = "3402";
	public static final String DASHBOARD_STATS_FETCHED_SUCCESSFULLY = "3601";
	public static final String DASHBOARD_STATS_FETCHED_FAILURE = "3602";

	// Table Shuffle
	public static final String TABLE_SHUFFLED_SUCCESFULLY = "3501";
	public static final String TABLE_SHUFFLE_FAILURE = "3502";

	// User Token
	public static final String USER_TOKEN_ADDED_SUCCESSFULLY = "3701";
	public static final String USER_TOKEN_ADDED_FAILURE = "3702";

	// Address Apis
	public static final String STATE_UPDATED_SUCCESFULLY = "2205";
	public static final String STATE_UPDATION_FAILURE = "2206";
	public static final String CITY_UPDATED_SUCCESFULLY = "2105";
	public static final String CITY_UPDATION_FAILURE = "2106";
	public static final String REGION_UPDATED_SUCCESFULLY = "2305";
	public static final String REGION_UPDATION_FAILURE = "2306";
	public static final String LOCALITY_UPDATED_SUCCESFULLY = "2405";
	public static final String LOCALITY_UPDATION_FAILURE = "2406";
	public static final String BUILDING_UPDATED_SUCCESFULLY = "2510";
	public static final String BUILDING_UPDATION_FAILURE = "2511";
	public static final String CITY_DELETED_SUCCESFULLY = "2107";
	public static final String CITY_DELETION_FAILURE = "2108";
	public static final String COUNTRY_DELETED_SUCCESFULLY = "1506";
	public static final String COUNTRY_DELETION_FAILURE = "1507";
	public static final String STATE_DELETED_SUCCESFULLY = "2207";
	public static final String STATE_DELETION_FAILURE = "2208";
	public static final String REGION_DELETED_SUCCESFULLY = "2307";
	public static final String REGION_DELETION_FAILURE = "2308";
	public static final String LOCALITY_DELETED_SUCCESFULLY = "2407";
	public static final String LOCALITY_DELETION_FAILURE = "2408";
	public static final String BUILDING_DELETED_SUCCESFULLY = "2512";
	public static final String BUILDING_DELETION_FAILURE = "2513";

	public static final String WEATHER_FETCH_SUCCESSFULLY = "3802";
	public static final String WEATHER_FETCH_FAILURE = "3801";

	public static final String CORPORATION_ADDED_SUCCESFULLY = "5202";
	public static final String CORPORATION_ADDITION_FAILURE = "5204";
	public static final String CORPORATION_RECORD_FETCH_SUCCESFULLY = "5201";
	public static final String CORPORATION_UPDATED_SUCCESFULLY = "5203";
	public static final String CORPORATION_UPDATION_FAILURE = "5205";

	// Bar Entry
	public static final String BARENTRY_FETCHED_SUCCESSFULLY = "3901";
	public static final String BARENTRY_CREATED_SUCCESSFULLY = "3902";
	public static final String BARENTRY_CREATION_FAILURE = "3903";
	public static final String BARENTRY_UPDATED_SUCCESSFULLY = "3904";
	public static final String BARENTRY_UPDATION_FAILURE = "3905";

	public static final String CORPORATE_OFFERS_ADDED_SUCCESFULLY = "5302";
	public static final String CORPORATE_OFFERS_ADDITION_FAILURE = "5304";
	public static final String CORPORATE_OFFERS_RECORD_FETCH_SUCCESFULLY = "5301";
	public static final String CORPORATE_OFFERS_UPDATED_SUCCESFULLY = "5303";
	public static final String CORPORATE_OFFERS_UPDATION_FAILURE = "5305";

	public static final String CORPORATE_OFFERS_FETCH_FAILURE = "5306";
	
	public static final String GUEST_TAG_ADDED_SUCCESSFULLY = "5402";
	public static final String GUEST_TAG_ADDITION_FAILURE = "5404";
	public static final String GUEST_TAG_REMOVED_SUCCESSFULLY = "5403";
	public static final String GUEST_TAG_REMOVE_FAILURE = "5405";
	public static final String GUEST_TAG_FETCHED_SUCCESSFULLY = "5401";
	public static final String GUEST_TAG_FETCH_FAILURE = "5406";

	public static final String OTP_FAILED = "3008";
	public static final String TAG_MERGED_SUCCESSFULLY = "6401";
	public static final String TAG_MERGED_FAILURE = "6402";
	
	// Reporting
	public static final String PREFERENCE_FETCHED_SUCCESSFULLY = "6501";
	public static final String PREFERENCE_CREATED_SUCCESSFULLY = "6502";
	public static final String PREFERENCE_CREATION_FAILURE = "6503";
	public static final String PREFERENCE_UPDATED_SUCCESSFULLY = "6504";
	public static final String PREFERENCE_UPDATION_FAILURE = "6505";
	

	public static final String RESTAURANT_ACTIVATION_FAILURE = "6604";
	public static final String RESTAURANT_ACTIVATED_SUCCESFULLY = "6605";
	
	//Application Details
	public static final String APPLICATION_DETAILS_RECORD_FETCH_SUCCESFULLY = "6601";
	public static final String APPLICATION_DETAILS_ADDITION_FAILURE = "6602";
	public static final String APPLICATION_DETAILS_ADDED_SUCCESFULLY = "6603";
	
	public static final String RESTAURANT_CUSTOM_SETTING_FETCH_SUCCESFULLY = "1111";
	public static final String RESTAURANT_CUSTOM_SETTING_FETCH_FAILURE = "1112";
	
	//Manual Scheduler
	public static final String SHEDULER_JOB_RUN_SUCCESSFULLY = "6701";
	public static final String SHEDULER_JOB_RUN_FAILURE = "6702";

}
