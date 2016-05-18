package com.clicktable.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

import com.clicktable.config.StormpathConfig;
import com.clicktable.dao.intf.BarEntryDao;
import com.clicktable.dao.intf.CorporateOffersDao;
import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.model.BarEntry;
import com.clicktable.model.CorporateOffers;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.GuestProfileCustomModel;
import com.clicktable.model.Reservation;
import com.clicktable.model.Restaurant;
import com.clicktable.model.TagModelOld;
import com.clicktable.model.UserInfoModel;
import com.clicktable.model.UserToken;
import com.clicktable.repository.GuestProfileRepo;
import com.clicktable.repository.ReservationRepo;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.LoginResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.response.SMSResponse;
import com.clicktable.response.UpdateResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CustomerLoginService;
import com.clicktable.service.intf.GuestHasTagsService;
import com.clicktable.service.intf.NotificationService;
import com.clicktable.service.intf.UserTokenService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CustomValidations;
import com.clicktable.validate.CustomerValidator;
import com.clicktable.validate.ReportValidator;
import com.clicktable.validate.ValidationError;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.google.common.collect.Lists;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.directory.Directories;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.sdk.resource.ResourceException;


@Service
public class CustomerLoginServiceImpl implements CustomerLoginService {
	com.stormpath.sdk.application.Application application = StormpathConfig.getInstance().getApplication();
	Client client = StormpathConfig.getInstance().getClient();

	private static final Logger.ALogger log = Logger.of(CustomerLoginServiceImpl.class);
	
	@Autowired
	CustomerDao customerDao;
	
	@Autowired
	CorporateOffersDao corporateDao;

	@Autowired
	GuestHasTagsService guestHasTagService;

	@Autowired
	RestaurantDao restDao;

	@Autowired
	AuthorizationService authorizationService;

	@Autowired
	UserTokenService userTokenService;

	@Autowired
	NotificationService notification;

	@Autowired
	CustomerValidator customerValidator;

	@Autowired
	GuestProfileRepo guestRepo;
	
	
	@Autowired
	ReservationRepo resvRepo;
	
	@Autowired
	BarEntryDao barEntryDao;

	@Autowired
	ReportValidator reportValidator;
	
	/**
	 * Service Method to add staff to storm path.whenever a staff member is
	 * created this method is called
	 */
	@Override
	public String addCustomerToStormPath(GuestProfile customer) {

		String href = "";
		DirectoryList dirList = StormpathConfig.getInstance().getTenant()
				.getDirectories(Directories.where(Directories.name().eqIgnoreCase(Constants.WALK_IN_CUSTOMER_DIRECTORY)));
		Client client = StormpathConfig.getInstance().getClient();
		Directory directory = null;
		for (Directory dir : dirList) {
			directory = dir;
		}
		com.stormpath.sdk.application.Application application = StormpathConfig.getInstance().getApplication();

		Account account = client.instantiate(Account.class);
		// Set the account properties
		account.setGivenName(customer.getFirstName());

		/*
		 * if(customer.getLastName() != null) {
		 * account.setSurname(customer.getLastName()); }
		 */
		account.setEmail(customer.getEmailId());
		// use the generatePassword method of UtilityMethods class to generate
		// random password
		String password = UtilityMethods.generatePassword();
		account.setPassword(password);

		// Create the account using the directory object
		Account createdAccount = directory.createAccount(account);
		href = createdAccount.getHref();
		String[] subStr = href.split("/");
		// whenever a new guest is added to storm path a password reset email is
		// send to that guest to change the login password
		application.sendPasswordResetEmail(customer.getEmailId());

		return subStr[subStr.length - 1];

	}

	@Transactional
	private BaseResponse socialLogin(String token, ProviderAccountRequest request, String socialID, String socialAccount) {
		// try {
		log.debug("in social login");
		ProviderAccountResult result = application.getAccount(request);
		Account account = result.getAccount();
		String generatedToken = null;
		BaseResponse loginResponse = null;
		UserInfoModel userInfo = null;
		GuestProfile customer = new GuestProfile(account, socialID, socialAccount);
		Map<String, Object> params = null;
		if (null != customer.getFid()) {
			params = new HashMap<String, Object>();
			params.put(Constants.FACEBOOK_ID, customer.getFid());
		} else {
			params = new HashMap<String, Object>();
			params.put(Constants.GOOGLE_ID, customer.getGid());
		}
		GuestProfile customerDb = customerDao.findGuest(params);
		/*
		 * Retrieve Existing Customer Acc or Creating New on on behalf of FID ||
		 * GID
		 */
		if (customerDb == null) {
			customer.setStatus(Constants.ACTIVE_STATUS);
			customer.setAvailablePoints(0);
			customer.setTotalPoints(0);
			customer.setGuid(UtilityMethods.generateCtId());
			customer.setIsVip(false);
			customerDao.create(customer);
			userInfo = new UserInfoModel(customer);
		} else {
			System.out.println("customer DB--------" + customerDb.getFid());
			log.debug("customer created");
			userInfo = new UserInfoModel(customerDb);
			log.debug("new session created");
		}

		loginResponse = new LoginResponse(userInfo);
		generatedToken = UtilityMethods.generateToken(Constants.CUSTOMER + userInfo.getGuid() + userInfo.getRoleId());
		((LoginResponse) loginResponse).setToken(generatedToken);
		// add token to loggedinusersmap
		authorizationService.addNewSession(generatedToken, userInfo);

		// check whether token already exists in database,if not exists then
		// insert entry for that token in database
		boolean exists = userTokenService.tokenExists(generatedToken);
		if (!exists) {
			UserToken userToken = new UserToken();
			userToken.setToken(generatedToken);
			userToken.setGuid(userInfo.getGuid());
			userToken.setUserId(Constants.CUSTOMER + userInfo.getGuid());
			log.debug("adding user token");
			userTokenService.addUserToken(userToken);
		}
		loginResponse.createResponse(ResponseCodes.SOCIAL_LOGIN_SUCCESS, true);
		log.debug("login response is " + loginResponse.toString());

		return loginResponse;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse loginWithGoogle(String token, String socialID) {
		log.debug("in google login");
		ProviderAccountRequest request = Providers.GOOGLE.account().setAccessToken(token).build();
		log.debug("in google login request" + request);
		String socialAccount = Constants.GOOGLE;
		return socialLogin(token, request, socialID, socialAccount);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse loginWithFacebook(String token, String socialID) {
		log.debug("in facebook login");

		ProviderAccountRequest request = Providers.FACEBOOK.account().setAccessToken(token).build();
		log.debug("in facebook login request" + request);
		String socialAccount = Constants.FACEBOOK;
		return socialLogin(token, request, socialID, socialAccount);
	}

	/*
	 * public BaseResponse getCustomers() { BaseResponse response = null;
	 * List<GuestProfile> customers = customerDao.findAll(GuestProfile.class);
	 * 
	 * for (GuestProfile customer : customers) {
	 * customer.setAccount(client.getResource(customer.getStormpathId(),
	 * Account.class)); }
	 * 
	 * if (customers.size() > 1) response = new
	 * GetResponse<GuestProfile>(ResponseCodes
	 * .CUSTOMER_RECORD_FETCH_SUCCESFULLY, customers); return response; }
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public BaseResponse updateProfile(GuestProfile customer, String token, List<TagModelOld> tag) {
		BaseResponse response;
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		customer.setUpdatedBy(userInfo.getGuid());

		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		// if user is not ct admin then check if guest profile being created and
		// logged in staff member both belongs to same restaurant
		if ((!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))) {
			if (((customer.getRestGuid() != null) && (!customer.getRestGuid().equals("")))) {
				listOfError = customerValidator.validateCustomerForRestaurant(customer, userInfo.getRestGuid());
				if (!listOfError.isEmpty()) {
					response = new ErrorResponse(ResponseCodes.CUSTOMER_ADDED_FAILURE, listOfError);
					log.debug("returning response");
					return response;
				}
			}
		}


		GuestProfile cust = null;

		if (!customer.getIsVip()) {
			customer.setReason(null);
		}

		if (customer.getGuid() == null)
			listOfError.add(customerValidator.createError(Constants.GUID, ErrorCodes.CUST_ID_REQUIRED));
		else {
          
			cust = customerDao.findGuestForRest(customer);
			log.debug("customer with guid is " + cust);
			if (cust == null)
				listOfError.add(customerValidator.createError(Constants.GUID, ErrorCodes.INVALID_GUEST_GUID));

			else {
				//customer.copyExistingValues(cust);
				// customer.setStormpathId(cust.getStormpathId());
				listOfError.addAll(customerValidator.validateCustomerOnUpdate(customer));
			}
		}

		if (listOfError.isEmpty()) {

			if ((cust.getFirstName().equalsIgnoreCase(Constants.DUMMY_FIRSTNAME) /*
																				 * &&
																				 * (
																				 * cust
																				 * .
																				 * getLastName
																				 * (
																				 * )
																				 * !=
																				 * null
																				 * &&
																				 * cust
																				 * .
																				 * getLastName
																				 * (
																				 * )
																				 * .
																				 * equalsIgnoreCase
																				 * (
																				 * Constants
																				 * .
																				 * DUMMY_LASTNAME
																				 * )
																				 * )
																				 */) || cust.getMobile().equals(Constants.DUMMY_MOBILE)) {
				listOfError.add(customerValidator.createError(Constants.GUID, ErrorCodes.DUMMY_GUEST_CANNOT_BE_UPDATED));
			}

			if (customer.getFirstName().equalsIgnoreCase(Constants.DUMMY_FIRSTNAME)) {
				listOfError.add(customerValidator.createError(Constants.FIRST_NAME, ErrorCodes.NAME_OF_DUMMY));
			}

			if (customer.getMobile().equalsIgnoreCase(Constants.DUMMY_MOBILE)) {
				listOfError.add(customerValidator.createError(Constants.MOBILE, ErrorCodes.MOBILE_OF_DUMMY));
			}

			if (customer.getMobile().equalsIgnoreCase(Constants.DUMMY_EMAIL)) {
				listOfError.add(customerValidator.createError(Constants.EMAIL, ErrorCodes.EMAIL_OF_DUMMY));

			}
			/* Validating Corporate Offer */

			if (null != customer.getCorporate()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(Constants.GUID, customer.getCorporate());
				params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				List<CorporateOffers> corporateOffer = corporateDao.findCorporateOffers(params);
				if (null == corporateOffer || corporateOffer.isEmpty()) {
					listOfError.add(customerValidator.createError(Constants.CORPORATE_OFFERS_LABEL, ErrorCodes.CORPORATE_OFFERS_DOESNT_EXIST));

				}
				else
				{
					customer.setCorporateName(corporateOffer.get(0).getName());
				}
			}
		}
		if (listOfError.isEmpty()) {

			/*if(null!=cust.getEmailId()){
				customer.setEmailId(cust.getEmailId());
			}
			customer.setMobile(cust.getMobile());
			*/

			Restaurant rest = new Restaurant();
			rest.setGuid(customer.getRestGuid());
			log.debug("updating customer");
			if(userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)){
			customer.setStatus(cust.getStatus());
			customer.setGuid(cust.getGuid());
			customerDao.update(customer);
			//customerDao.updateRestaurantGuest()
			}
			//TO DO : relationship update with all restaurants 
			else if(!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))
			customerDao.addRestaurantGuest(rest, customer);

			log.debug("customer updated");
			response = new UpdateResponse<GuestProfile>(ResponseCodes.CUSTOMER_UPDATED_SUCCESS, customer.getGuid());
		}

		else {
			response = new ErrorResponse(ResponseCodes.CUSTOMER_UPDATED_FAILURE, listOfError);
		}

		return response;
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	@Transactional(readOnly = true)
	public BaseResponse getCustomers(Map<String, Object> params, String token) {
		BaseResponse getResponse;

		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		log.debug("role id is " + userInfo.getRoleId());
		// if role is customer than show record of that particular customer

		if (userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) {
			params.put(Constants.GUID, userInfo.getGuid());
		}
		Map<String, Object> qryParamMap = customerValidator.validateFinderParams(params, GuestProfile.class);

		/*if (!qryParamMap.containsKey(Constants.STATUS)) {
			qryParamMap.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		}*/

		// if role is staff than show record of that customers who are guests of
		// restaurant of that staff member
		if ((!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))) {
			log.debug("staff logged in rest id is " + userInfo.getRestGuid());
			qryParamMap.put(Constants.REST_GUID, userInfo.getRestGuid());

			/*
			 * List<GuestProfile> guestList =
			 * customerDao.findByFields(GuestProfile.class, qryParamMap);
			 * log.info("guest list is " + guestList); getResponse =new
			 * GetResponse
			 * <GuestProfile>(ResponseCodes.CUSTOMER_RECORD_FETCH_SUCCESFULLY,
			 * guestList); return getResponse;
			 */

		}
		// if above two conditions are not true than logged in user is ct admin
		// and show all records of all customers to that member
		qryParamMap.put(Constants.ROLE_ID, userInfo.getRoleId());
		List<GuestProfile> customerList = customerDao.findByFields(GuestProfile.class, qryParamMap);
		List<GuestProfileCustomModel> customGuestList = new ArrayList<GuestProfileCustomModel>();
		GuestProfileCustomModel customGuest;

		for (GuestProfile guest : customerList) {
			/* Code to not show Unknown Guest in Guest Book */
			if (!guest.isDummy()) {
				customGuest = new GuestProfileCustomModel(guest);
				customGuestList.add(customGuest);
			}
		}
		getResponse = new GetResponse<GuestProfileCustomModel>(ResponseCodes.CUSTOMER_RECORD_FETCH_SUCCESFULLY, customGuestList);
		return getResponse;

	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public GuestProfile getCustomerById(Long
	 * id) { GuestProfile customer = customerDao.find(id);
	 * customer.setAccount(client.getResource(customer.getStormpathId(),
	 * Account.class)); return customer; }
	 */

	/**
	 * {@inheritDoc}
	 */

	@Override
	@Transactional
	public BaseResponse addCustomer(GuestProfile customer, String token) {
		BaseResponse response = null;

		customer.setStatus(Constants.ACTIVE_STATUS);
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		customer.setGuid(UtilityMethods.generateCtId());
		customer.setCreatedBy(userInfo.getGuid());
		customer.setUpdatedBy(userInfo.getGuid());

		Map<String, Object> params = new HashMap<>();
		List<ValidationError> listOfErrorForCustomer;
		// if user is not ct admin then check if guest profile being created and
		// logged in staff member both belongs to same restaurant
		if (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID)) {
			if (((customer.getRestGuid() != null) && (!customer.getRestGuid().equals("")))) {
				listOfErrorForCustomer = customerValidator.validateCustomerForRestaurant(customer, userInfo.getRestGuid());
				if (!listOfErrorForCustomer.isEmpty()) {
					response = new ErrorResponse(ResponseCodes.CUSTOMER_ADDED_FAILURE, listOfErrorForCustomer);
					log.debug("returning response");
					return response;
				}
			}

		}

		if (!customer.getIsVip()) {
			customer.setReason(null);
		}

		listOfErrorForCustomer = customerValidator.validateCustomerOnAdd(customer);
		Restaurant rest = null;
		if ((customer.getRestGuid() != null) && (!customer.getRestGuid().equals(""))) {
			rest = restDao.findRestaurantByGuid(customer.getRestGuid());
			log.debug("finding restaurant " + rest);
			if (rest == null) {
				listOfErrorForCustomer.add(customerValidator.createError(Constants.RESTGUID, ErrorCodes.INVALID_REST_ID));
				log.debug("list of error is " + listOfErrorForCustomer);
			}
		}
		
		/* Validating Corporate Offer */

		if (null != customer.getCorporate()) {
			params.put(Constants.GUID, customer.getCorporate());
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			List<CorporateOffers> corporateOffer = corporateDao.findCorporateOffers(params);
			if (null == corporateOffer || corporateOffer.isEmpty()) {
				listOfErrorForCustomer.add(customerValidator.createError(Constants.CORPORATE_OFFERS_LABEL, ErrorCodes.CORPORATE_OFFERS_DOESNT_EXIST));
				response = new ErrorResponse(ResponseCodes.CUSTOMER_ADDED_FAILURE, listOfErrorForCustomer);
				return response;
			}
			
			customer.setCorporateName(corporateOffer.get(0).getName());
		}

		if (listOfErrorForCustomer.isEmpty()) {

			try {
				params.put(Constants.MOBILE, customer.getMobile());
				params.put(Constants.ACTIVE_STATUS, Constants.ACTIVE_STATUS);
				params.put(Constants.REST_GUID, customer.getRestGuid());
				Map<String, Object> customersForMobileNo = customerDao.validateRestGuestExist(params);

				log.debug("no of customer with same mobile no are " + customersForMobileNo.size());
				String guid = "";
				if (null != customersForMobileNo && null != customersForMobileNo.get(Constants.GUEST_NODE)) {

					GuestProfile oldCustomer = (GuestProfile) customersForMobileNo.get(Constants.GUEST_NODE);

					if (null != oldCustomer && null != customersForMobileNo.get(Constants.REST_NODE) &&
							!customersForMobileNo.get(Constants.STATUS).toString().equalsIgnoreCase(Constants.DELETED_STATUS)) {
						listOfErrorForCustomer = new ArrayList<>();
						listOfErrorForCustomer.add(customerValidator.createError(Constants.MOBILE, ErrorCodes.CUSTOMER_ALREADY_EXISTS));
						response = new ErrorResponse(ResponseCodes.CUSTOMER_ADDED_FAILURE, listOfErrorForCustomer);
						return response;
					} else {

						Long id = 1l;
						/* Not allowing Restaurant to update Guest Info Globally */
						oldCustomer.setAnniversary((null != customer.getAnniversary()) ? customer.getAnniversary() : oldCustomer.getAnniversary());
						oldCustomer.setCountryCode((null != customer.getCountryCode()) ? customer.getCountryCode() : oldCustomer.getCountryCode());
						oldCustomer.setDob((null != customer.getDob()) ? customer.getDob() : oldCustomer.getDob());
						oldCustomer.setEmailId((null != customer.getEmailId()) ? customer.getEmailId() : oldCustomer.getEmailId());
						oldCustomer.setFirstName((null != customer.getFirstName()) ? customer.getFirstName() : oldCustomer.getFirstName());
						oldCustomer.setGender((null != customer.getGender()) ? customer.getGender() : oldCustomer.getGender());
						oldCustomer.setIsVip((null != customer.getIsVip()) ? customer.getIsVip() : oldCustomer.getIsVip());
						// oldCustomer.setLastName((null!=customer.getLastName())?customer.getLastName():oldCustomer.getLastName());
						oldCustomer.setReason((null != customer.getReason()) ? customer.getReason() : null);
						oldCustomer.setStatus(Constants.ACTIVE_STATUS);

						oldCustomer.setCorporate((null != customer.getCorporate()) ? customer.getCorporate() : null);
						oldCustomer.setCorporateName((null != customer.getCorporateName()) ? customer.getCorporateName() : null);

						oldCustomer.setDnd_email((customer.isDnd_email()) ? customer.isDnd_email() : oldCustomer.isDnd_email());
						oldCustomer.setDnd_mobile((customer.isDnd_mobile()) ? customer.isDnd_mobile() : oldCustomer.isDnd_mobile());
						oldCustomer.setIs_mobile_verified((customer.isIs_mobile_verified()) ? customer.isIs_mobile_verified() : oldCustomer
								.isIs_mobile_verified());
						log.debug("creating relationship only");
						// create relationship only
						customerDao.addRestaurantGuest(rest, oldCustomer);

						guid = oldCustomer.getGuid();
					}
				}

				// if customer with new mobile no then add customer and create
				// relationship
				else {
					log.debug("adding customer and relationship");

					/*
					 * Create relationship of customer with restaurant
					 */
					String searchParams = null;
					if (null != customer.getFirstName()) {
						searchParams = customer.getFirstName().replaceAll(" ", "");
					}

					/*
					 * if(null!=customer.getLastName()){
					 * searchParams=searchParams
					 * +customer.getLastName().replaceAll(" ", ""); }
					 */
					if (null != customer.getMobile()) {
						searchParams = searchParams + customer.getMobile();
					}
					customer.setSearchParams(searchParams);
					GuestProfile newCustomer = customerDao.create(customer);
					log.debug("customer created");

					customerDao.addRestaurantGuest(rest, newCustomer);

					guid = newCustomer.getGuid();
				}

				response = new PostResponse<GuestProfile>(ResponseCodes.CUSTOMER_ADDED_SUCCESFULLY, guid);
			} catch (ResourceException e) {
				log.debug("stormpath exception..........." + e.getDeveloperMessage());

				if (e.getDeveloperMessage().contains("Account with that email already exists")) {
					listOfErrorForCustomer.add(new ValidationError(Constants.EMAIL, UtilityMethods
							.getErrorMsg(ErrorCodes.EMAIL_ACCOUNT_ALREADY_EXISTS), ErrorCodes.EMAIL_ACCOUNT_ALREADY_EXISTS));
				} else {
					listOfErrorForCustomer.add(new ValidationError(Constants.STORMPATH_MODULE, e.getMessage()));
				}
				response = new ErrorResponse(ResponseCodes.CUSTOMER_ADDED_FAILURE, listOfErrorForCustomer);
			}

		} else {
			// if validation error then send error response
			response = new ErrorResponse(ResponseCodes.CUSTOMER_ADDED_FAILURE, listOfErrorForCustomer);
		}

		return response;
	}

	
	private String[] writeError(CsvReader reader,List<ValidationError> errorList) throws IOException{
		//String[] values = reader.getValues();
		StringBuilder errorMsg = new StringBuilder();
		errorList.forEach(x -> {
			errorMsg.append("," + x.getErrorMessage().toString());
		});
		if (errorMsg.length() > 0)
			errorMsg.deleteCharAt(0);
		String[] errors = {reader.get("firstName"),reader.get("isdCode"),reader.get("mobile"),reader.get("gender"),reader.get("emailId"), errorMsg.toString() };
		return ArrayUtils.addAll(errors);
	}
	
	private String[] writeError(GuestProfile guest,String error){
		String[] values = {guest.getFirstName(),guest.getIsd_code(),guest.getMobile(),guest.getGender(),guest. getEmailId(),error};
		return values;
	}
	
	/**
	 * {@inheritDoc}  
	 */
	@Override
	@Transactional
	public BaseResponse addCustomersFromCSV(File file, String token, String restaurantGuid) {
		BaseResponse response;
		List<ValidationError> errors = new ArrayList<ValidationError>();
		if(file==null){
			errors.add(new ValidationError(Constants.CSV,UtilityMethods.getErrorMsg(ErrorCodes.CSV_FILE_NOT_FOUND),ErrorCodes.CSV_FILE_NOT_FOUND ));
		}else{
			try {
				CsvReader reader;
				reader = new CsvReader(file.getAbsolutePath());
				reader.readHeaders();
				errors.addAll(customerValidator.validateCSVHeaders(new ArrayList<String>(Arrays.asList(reader.getHeaders()))));
			} catch (IOException e) {
				errors.add(new ValidationError(Constants.CSV,UtilityMethods.getErrorMsg(ErrorCodes.FILE_CORRUPTED),ErrorCodes.FILE_CORRUPTED ));
				e.printStackTrace();
			}
		}
		Restaurant restaurant = restDao.findRestaurantByGuid(restaurantGuid);
		
		if (restaurant == null) {
			errors.add(customerValidator.createError(Constants.REST_GUID, ErrorCodes.INVALID_REST_ID));
		}
		if (errors.isEmpty()) {
			Runnable guestsUploadTask = () -> { 
				//Restaurant rest = restDao.findRestaurantByGuid(restaurantGuid);
				UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
			try {
					CsvReader reader;
						reader = new CsvReader(file.getAbsolutePath());
					reader.readHeaders();
					File dir = new File(Constants.INVALID);
					dir.mkdir();
					StringBuilder outputFile = new StringBuilder(Constants.INVALID+"/" + restaurantGuid + "-" + UtilityMethods.timestamp() + "."+Constants.CSV);
					String csvFileName =  UtilityMethods.timestamp() + "."+Constants.CSV;
					CsvWriter writer = new CsvWriter(new FileWriter(outputFile.toString(), true), ',');
					List<String> headers = UtilityMethods.getEnumValues(Constants.INVALID, Constants.CSV_HEADERS);
					writer.writeRecord(headers.toArray(new String[headers.size()]));
					Integer validCount = 0,invalidCount=0;
					Map<String, GuestProfile> mobileMap=new HashMap<String, GuestProfile>();

					while (reader.readRecord()) {
						GuestProfile guest = new GuestProfile(reader);
						guest.setInfoOnCreate(userInfo);
						guest.setStatus(Constants.ACTIVE_STATUS);
						List<ValidationError> errorList = customerValidator.validateCSVCustomerOnAdd(guest);
						if(errorList.isEmpty()){
							if(mobileMap.containsKey(reader.get(Constants.MOBILE).toString())){
								errorList = CustomValidations.populateErrorList(errorList, Constants.MOBILE, UtilityMethods.getErrorMsg(ErrorCodes.DUPLICATE_MOBILE_NUMBER), ErrorCodes.DUPLICATE_MOBILE_NUMBER);
								writer.writeRecord(writeError(reader, errorList));
								invalidCount++;
							}else{
								mobileMap.put(reader.get("mobile"), guest);
								validCount++;
							}
						}else{
							writer.writeRecord(writeError(reader, errorList));
							invalidCount++;
						}
					}
					
					Map<String, Object> queryParams= new HashMap<String,Object>();
					queryParams.put(Constants.STATUS, Constants.ACTIVE_STATUS);
					List<Map<String, Object>> guestsMapList = new ArrayList<Map<String, Object>>(); 
					queryParams.put(Constants.REST_GUID,restaurantGuid);
					List<List<String>> partialNumberList = Lists.partition(new ArrayList<String>(mobileMap.keySet()), 50);
					partialNumberList.forEach(num->{
						queryParams.put(Constants.MOBILE,num);
						guestsMapList.addAll(customerDao.filterGuestMobileNumbers(queryParams));
					});
					List<String> otherRestaurantsGuests= new ArrayList<String>();
					List<String> alreadyGuests= new ArrayList<String>();
					List<GuestProfile> guests=new ArrayList<GuestProfile>();
					Set<String> oldGuestsMobiles = guestsMapList.stream().map(k->{
						String mobile = k.get(Constants.MOBILE).toString();
						if((Boolean)k.get("isGuest")&&!(k.get(Constants.STATUS).toString().equalsIgnoreCase(Constants.DELETED_STATUS))){
								alreadyGuests.add(k.get(Constants.MOBILE).toString());
						}else{
							GuestProfile customer= mobileMap.get(mobile);
							GuestProfile oldCustomer= (GuestProfile) k.get(Constants.GUEST_NODE);
							oldCustomer.setCountryCode((null != customer.getCountryCode()) ? customer.getCountryCode() : oldCustomer.getCountryCode());
							oldCustomer.setEmailId((null != customer.getEmailId()) ? customer.getEmailId() : oldCustomer.getEmailId());
							oldCustomer.setFirstName((null != customer.getFirstName()) ? customer.getFirstName() : oldCustomer.getFirstName());
							oldCustomer.setGender((null != customer.getGender()) ? customer.getGender() : oldCustomer.getGender());
							oldCustomer.setIsVip((null != customer.getIsVip()) ? customer.getIsVip() : oldCustomer.getIsVip());
							oldCustomer.setReason((null != customer.getReason()) ? customer.getReason() : null);
							oldCustomer.setStatus(Constants.ACTIVE_STATUS);
							oldCustomer.setCreatedBy(userInfo.getGuid());
							oldCustomer.setUpdatedBy(userInfo.getGuid());
							oldCustomer.setCreatedDate(new Timestamp(new Date().getTime()));
							oldCustomer.setUpdatedDate(new Timestamp(new Date().getTime()));
							otherRestaurantsGuests.add(mobile);
							guests.add(oldCustomer);
						}
						return mobile;
					}).collect(Collectors.toSet());
					
					for (String mob : alreadyGuests) {
						writer.writeRecord(writeError(mobileMap.get(mob),UtilityMethods.getErrorMsg(ErrorCodes.CUSTOMER_ALREADY_EXISTS)));
					}
					
					mobileMap.keySet().removeAll(oldGuestsMobiles);
					List<List<GuestProfile>> splitList = Lists.partition(new ArrayList(mobileMap.values()), 50);
					splitList.forEach(list->{
								guests.addAll(customerDao.createMultiple(list));
					});
					
					List<List<GuestProfile>> splitListOfExstingGuests = Lists.partition(guests, 50);
					splitListOfExstingGuests.forEach(list->{
						createGuestsRelationShip(list, queryParams);
					});
					writer.close();
					reader.close();
					ArrayList<String> to = new ArrayList<String>(),tags = new ArrayList<String>();
					to.add(userInfo.getEmail());
					tags.add(Constants.CSV);
					tags.add(Constants.GUEST_LABEL);
					
					Map<String, String> templateContent = new java.util.HashMap<String, String>();
					templateContent.put(Constants.FIRST_NAME, userInfo.getUserFirstName());
					templateContent.put(Constants.VALID_COUNT, validCount.toString());
					templateContent.put(Constants.RESTAURANT_NAME, restaurant.getName());
					
					if (invalidCount > 0) {
						tags.add(Constants.INVALID);
						templateContent.put(Constants.INVALID_COUNT, invalidCount.toString());
						notification.sendEmail(to, tags, Constants.CSV_INVALID_MANDRILL_TEMPLATE_NAME,templateContent, new File(outputFile.toString()), csvFileName);
					} else {
						tags.add(Constants.SUCCESS);
						notification.sendEmail(to, tags, Constants.CSV_SUCCESS_MANDRILL_TEMPLATE_NAME, templateContent);
					}

			} catch (IOException e) {
				log.warn(e.getStackTrace().toString());
			}
				
			
			};
			// start the thread
			new Thread(guestsUploadTask).start();
			response = new PostResponse<GuestProfile>(ResponseCodes.CUSTOMERS_CSV_PROCESSING,ResponseCodes.CUSTOMERS_CSV_PROCESSING);
		} else {
			response = new ErrorResponse(ResponseCodes.CUSTOMERS_CSV_FAILURE, errors);
		}

		return response;
	}
	
	private Integer createGuestsRelationShip(List<GuestProfile> list,Map<String, Object> queryParams){
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		for (GuestProfile g : list) {
			log.debug(g.getMobile());
			listMap.add(UtilityMethods.introspect(g));
		}
		//making relationship with new and old guests of other restaurants
		queryParams.put("mapList",listMap);
		return customerDao.createMultipleGuests(queryParams);
		
	}
	

	/*@Override
	@Transactional(readOnly = true)
	public BaseResponse getCustomersReport(String token, String restGuid,
			String fileFormat) {

		String restaurantGuid = restGuid;
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		reportValidator.validateFileFormat(fileFormat, errorList);

		if (errorList.isEmpty()) {
			
			UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
			if (userInfo.getRoleId() != 1L) {
				restaurantGuid = userInfo.getRestGuid();
			}

			File file = UtilityMethods.createTempFile(Constants.GUEST_LABEL, fileFormat, errorList);

			if (errorList.isEmpty()) {
				String outputFile = file.getPath();

				Map<String, Object> qryParamMap = new HashMap<String, Object>();
				qryParamMap.put(Constants.REST_GUID, restaurantGuid);
				qryParamMap.put(Constants.STATUS, Constants.ACTIVE_STATUS);
				qryParamMap.put(Constants.ORDER_BY, Constants.FIRST_NAME);
				qryParamMap.put(Constants.PAGE_SIZE, (Integer.MAX_VALUE) + "");
				qryParamMap.put(Constants.ROLE_ID, userInfo.getRoleId());
				List<GuestProfile> customerList = customerDao.findByFields(
						GuestProfile.class, qryParamMap);
				if (customerList.size() < 2) {
					errorList.add(customerValidator.createError(Constants.GUEST_LABEL,ErrorCodes.GUESTS_NOT_FOUND));
				}

				if (errorList.isEmpty()) {
					try {
						if ("csv".equals(fileFormat)) {
							createGuestCSV(outputFile, customerList);
						} else {
							Restaurant restaurant = restDao.findRestaurantByGuid(restGuid);
							createGuestPdf(outputFile, customerList, restaurant);
						}
					} catch (DocumentException | IOException e) {
						// TODO catch exception
						log.error(e.getMessage());
						errorList.add(new ValidationError(Constants.GUEST_LABEL, e.getMessage(), ErrorCodes.REPORT_CREATION_EXCEPTION));
					}
					if (errorList.isEmpty()) {
						return new SupportResponse<File>(ResponseCodes.GUEST_FILE_FETCH_SUCCESS, file);
					}
				}
			}
		}
		return new ErrorResponse(ResponseCodes.GUEST_FILE_FETCH_FAILURE,errorList);
	}
	
	
	private void createGuestPdf(String outputFile, List<GuestProfile> customerList, Restaurant restaurant) throws DocumentException, IOException {
		// TODO Auto-generated method stub

		int counter = 0;
		int column = 5;
		FileOutputStream out = null;
		Document document = null;
		String name, email,contact, gender, vip; 
		
		String[] header = UtilityMethods.getEnumValues(Constants.CUSTOMER_MODULE, Constants.CSV_HEADERS).stream().toArray(String[]::new);
		com.itextpdf.text.Font fontRegular = UtilityMethods.getFont("SansSerif", 6, false);
		com.itextpdf.text.Font fontbold = UtilityMethods.getFont("SansSerif", 7, true);
		fontRegular.setColor(new BaseColor(89, 89, 89));
		BaseColor bgColor = null;
		BaseColor CUSTM_GREY = new BaseColor(242, 242, 242);
		log.debug("~~~~~~~~~~~~~~~"+customerList.size()+"~~~~~~~~~~~~~"+outputFile);

		
		out = new FileOutputStream(outputFile);
		document = new Document();
		PdfWriter.getInstance(document, out);
		document.open();

		PdfPTable tableMain = new PdfPTable(2); // Outer Table
		tableMain.setWidthPercentage(100);

		PdfPTable table = new PdfPTable(column);
		table.setWidthPercentage(100);
		float[] width = { 8,5,3,2,2};		//Relative Width of column
		table.setWidths(width);
		UtilityMethods.generateTableColumns(table, header, column, BaseColor.WHITE, fontbold, 1);
		table.setHeaderRows(1);
			
		for (GuestProfile guest : customerList) {
			if (!guest.isDummy()) {
				name = guest.getFirstName();
				email = guest.getEmailId();
				contact = "+" + guest.getIsd_code() + guest.getMobile();
				gender = guest.getGender();

				if (guest.getIsVip())
					vip = guest.getReason();
				else
					vip = "";

				if (counter % 2 == 0)
					bgColor = CUSTM_GREY;
				else
					bgColor = BaseColor.WHITE;
				String[] data = { name, contact, email, gender, vip };
				UtilityMethods.generateTableColumns(table, data, column, bgColor, fontRegular,	0);
				counter++;
			}
				
		}
		log.debug("Data added to table rows");
		UtilityMethods.fillRestDetail(tableMain, restaurant, Integer.toString(counter)+" Guests");
		tableMain.setSplitLate(false);
		PdfPCell cell1 = new PdfPCell();
		cell1.setPaddingRight(-0.1f);
		cell1.setPaddingLeft(-0.1f);
		table.setSplitLate(false);
		cell1.addElement(table);
		cell1.setBorder(0);
		cell1.setColspan(2);
		
		String imagePath = "conf/Clicktable.jpg";
		Image image = Image.getInstance(imagePath);
		image.setAbsolutePosition(290f, 760f);
		image.scaleAbsolute(50f, 50f);
		document.add(image);
		
		tableMain.addCell(cell1);
		document.add(tableMain);
		if (document != null)
			document.close();
	}
	
	private void createGuestCSV(String outputFile,List<GuestProfile> customerList) throws IOException{
		FileWriter fileWriter = new FileWriter(outputFile, true);
		CsvWriter writer = new CsvWriter(fileWriter, ',');
		writer.writeRecord(UtilityMethods.getEnumValues(Constants.CUSTOMER_MODULE, Constants.CSV_HEADERS).stream().toArray(String[]::new));
		customerList.stream().filter(g->!g.isDummy()).forEach(guest->{
					GuestProfileCustomModel customGuest;
					customGuest = new GuestProfileCustomModel(guest);
					try {
						writer.write(customGuest.getFirstName());
						writer.write(customGuest.getMobile());
						writer.write(customGuest.getEmailId());
						writer.write(customGuest.getGender());
						if (customGuest.getIsVip())
							writer.write(customGuest.getReason());
						else
							writer.write("");
						writer.endRecord();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			});
		writer.close();
		fileWriter.close();
	}*/
	

	@Override
	public BaseResponse updateConsumerProfile(GuestProfile customer) {

		BaseResponse response = null;
		List<ValidationError> listOfErrorForCustomer = null;

		/* Retrieving User Info Model */
		// UserInfoModel userInfo =
		// authorizationService.getUserInfoByToken(header);
		Map<String, Object> params = new HashMap<>();


		/*
		 * Always validate the Customer Mobile No if already existed in GRAPH DB
		 * send Verification OTP to verify
		 */
		params.put(Constants.MOBILE, customer.getMobile());
		/* params.put(Constants.EMAIL, customer.getEmailId()); */

		Map<String, Object> customersForMobileNo = customerDao.validateGuestExist(params);
		if (null != customersForMobileNo && null != customersForMobileNo.get(Constants.GUEST_NODE)) {
			GuestProfile oldCustomer = (GuestProfile) customersForMobileNo.get(Constants.GUEST_NODE);

			if (customer.getMobile().equalsIgnoreCase(oldCustomer.getMobile()) && customer.isIs_mobile_verified()) {
				/*
				 * Mobile Verified after verifying MOBILE OTP in Neo4j
				 * Uniqueness of Customer EMAIL is not required as discussed or
				 * mentioned in new design
				 */
				oldCustomer.setAddress((null != customer.getAddress()) ? customer.getAddress() : oldCustomer.getAddress());
				oldCustomer.setAnniversary((null != customer.getAnniversary()) ? customer.getAnniversary() : oldCustomer.getAnniversary());
				oldCustomer.setCity((null != customer.getCity()) ? customer.getCity() : oldCustomer.getCity());
				oldCustomer.setCountryCode((null != customer.getCountryCode()) ? customer.getCountryCode() : oldCustomer.getCountryCode());
				oldCustomer.setDob((null != customer.getDob()) ? customer.getDob() : oldCustomer.getDob());
				oldCustomer.setEmailId((null != customer.getEmailId()) ? customer.getEmailId() : oldCustomer.getEmailId());
				oldCustomer.setFirstName((null != customer.getFirstName()) ? customer.getFirstName() : oldCustomer.getFirstName());
				oldCustomer.setGender((null != customer.getGender()) ? customer.getGender() : oldCustomer.getGender());
				oldCustomer.setGroupUrl((null != customer.getGroupUrl()) ? customer.getGroupUrl() : oldCustomer.getGroupUrl());
				oldCustomer.setGuestType((null != customer.getGuestType()) ? customer.getGuestType() : oldCustomer.getGuestType());
				oldCustomer.setIsVip((null != customer.getIsVip()) ? customer.getIsVip() : oldCustomer.getIsVip());
				oldCustomer.setLanguageCode((null != customer.getLanguageCode()) ? customer.getLanguageCode() : oldCustomer.getLanguageCode());
				// oldCustomer.setLastName((null!=customer.getLastName())?customer.getLastName():oldCustomer.getLastName());
				oldCustomer.setPassword((null != customer.getPassword()) ? customer.getPassword() : oldCustomer.getPassword());
				oldCustomer.setPhotoUrl((null != customer.getPhotoUrl()) ? customer.getPhotoUrl() : oldCustomer.getPhotoUrl());
				oldCustomer.setPincode((null != customer.getPincode()) ? customer.getPincode() : oldCustomer.getPincode());
				oldCustomer.setReason((null != customer.getReason()) ? customer.getReason() : oldCustomer.getReason());
				oldCustomer.setState((null != customer.getState()) ? customer.getState() : oldCustomer.getState());
				oldCustomer.setStatus(Constants.ACTIVE_STATUS);
				oldCustomer.setCorporate((null != customer.getCorporate()) ? customer.getCorporate() : oldCustomer.getCorporate());
				oldCustomer.setDnd_email((customer.isDnd_email()) ? customer.isDnd_email() : oldCustomer.isDnd_email());
				oldCustomer.setDnd_mobile((customer.isDnd_mobile()) ? customer.isDnd_mobile() : oldCustomer.isDnd_mobile());
				oldCustomer.setFid((null != customer.getFid()) ? customer.getFid() : oldCustomer.getFid());
				oldCustomer.setGid((null != customer.getGid()) ? customer.getGid() : oldCustomer.getGid());
				oldCustomer.setIs_mobile_verified((customer.isIs_mobile_verified()) ? customer.isIs_mobile_verified() : oldCustomer.isIs_mobile_verified());
				/*
				 * 
				 * Guest Model Attributes updated with New Guest Book
				 * functionality
				 */

				String searchParams = null;
				if (null != oldCustomer.getFirstName()) {
					searchParams = oldCustomer.getFirstName().replaceAll(" ", "");
				}
				/*
				 * if(null!=oldCustomer.getLastName()){
				 * searchParams=searchParams
				 * +oldCustomer.getLastName().replaceAll(" ", ""); }
				 */
				if (null != oldCustomer.getMobile()) {
					searchParams = searchParams + oldCustomer.getMobile();
				}
				oldCustomer.setSearchParams(searchParams);
				// oldCustomer.setRestGuid(customer.getRestGuid());

				customerDao.update(oldCustomer);
				response = new LoginResponse();
				/* Updating all Relationship in Restaurant for Consumer */
				customerDao.updateRestaurantGuest(oldCustomer, customer);
				UserInfoModel userInfoModel = new UserInfoModel(oldCustomer);
				((LoginResponse) response).setUserInfo(userInfoModel);
				response.createResponse(ResponseCodes.CUSTOMER_UPDATED_SUCCESS, true);

			}
		} else {
			/*
			 * Mobile is not matching so directly create the Guest Model in
			 * Neo4j Uniqueness of Customer email is not required as discussed
			 * or mentioned in new design Create Guest in DB
			 */
			customer.setIs_mobile_verified(true);
			listOfErrorForCustomer = customerValidator.validateCustomerOnAdd(customer);
			response = new LoginResponse();
			if (listOfErrorForCustomer.isEmpty()) {
				if (!customer.getIsVip()) {
					customer.setReason(null);
				}
				customer.setStatus(Constants.ACTIVE_STATUS);
				log.debug("adding customer and relationship");
				GuestProfile newCustomer = customerDao.update(customer);
				newCustomer.getGuid();
				UserInfoModel userInfoModel = new UserInfoModel(customer);
				((LoginResponse) response).setUserInfo(userInfoModel);
				response.createResponse(ResponseCodes.CUSTOMER_ADDED_SUCCESFULLY, true);
			} else {

				return new ErrorResponse(ResponseCodes.STAFF_ADDED_FAILURE, listOfErrorForCustomer);
			}
		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.clicktable.service.intf.CustomerLoginService#customerVerification
	 * (java.lang.String, java.lang.String, boolean, java.lang.Object)
	 */
	@Override
	public BaseResponse customerVerification(String guid, String otp_token, String header) {

		UserInfoModel userInfo = null;
		List<ValidationError> listOfErrorForCustomer = new ArrayList<ValidationError>();
		BaseResponse loginResponse = null;

		/* Validating User Info Token before go ahead */
		if (null != header && !header.trim().equalsIgnoreCase("")) {
			userInfo = authorizationService.getUserInfoByToken(header);
		} else {
			listOfErrorForCustomer.add(new ValidationError(Constants.ACCESS_TOKEN, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_ACCESS_TOKEN),
					ErrorCodes.INVALID_ACCESS_TOKEN));
			return new ErrorResponse(ResponseCodes.INVALID_ACCESS_TOKEN, listOfErrorForCustomer);
		}

		/* Validate User Info Model */
		if (null != userInfo && userInfo.getRoleId() == Constants.CUSTOMER_ROLE_ID) {
			/* Validate Guest | Mobile */

			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.GUID, guid);
			params.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			GuestProfile profile = customerDao.findGuest(params);
			if (null == profile) {
				listOfErrorForCustomer.add(new ValidationError(Constants.GUEST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID),
						ErrorCodes.INVALID_GUEST_GUID));
				return new ErrorResponse(ResponseCodes.NO_USER_FOUND, listOfErrorForCustomer);
			}

			if (null != otp_token && null != profile) {
				Calendar cal = Calendar.getInstance();
				long currentTime = cal.getTimeInMillis();
				if (null != profile.getOtp_generated_time()
						&& (currentTime - profile.getOtp_generated_time().getTime()) < Constants.OTP_VALIDITY * 60 * 1000
						&& (profile.getOtpToken().equalsIgnoreCase(otp_token))) {

					profile.setOtp_generated_time(new Timestamp(new Date().getTime() - 20 * 60 * 1000));
					profile.setIs_mobile_verified(true);
					/* Call to Update Consumer Profile Once Mobile Verified */
					loginResponse = updateConsumerProfile(profile);
					/*
					 * Customer Profile Token need to updated /* Removing Token
					 * From Session Service First
					 */
					authorizationService.removeSession(header);
					/*
					 * Updating User Info model from Updated Guid, Created when
					 * account merged !
					 */
					userInfo = ((LoginResponse) loginResponse).getUserInfo();
					/* Generating new Token */
					String generatedToken = UtilityMethods.generateToken(Constants.CUSTOMER + userInfo.getGuid() + userInfo.getRoleId());
					((LoginResponse) loginResponse).setToken(generatedToken);
					// add token to loggedinusersmap
					authorizationService.addNewSession(generatedToken, userInfo);

					// check whether token already exists in database,if not
					// exists then
					// insert entry for that token in database
					boolean exists = userTokenService.tokenExists(generatedToken);
					if (!exists) {
						UserToken userToken = new UserToken();
						userToken.setToken(generatedToken);
						userToken.setGuid(userInfo.getGuid());
						userToken.setUserId(Constants.CUSTOMER + userInfo.getGuid());
						log.debug("adding user token");
						userTokenService.addUserToken(userToken);
					}

					loginResponse.setResponseCode(ResponseCodes.SMS_TOKEN_VERIFIED);
					loginResponse.setResponseMessage("SMS Token Verified !");
					loginResponse.setResponseStatus(Boolean.valueOf(true));

				} else {
					userInfo = new UserInfoModel(profile);
					loginResponse = new LoginResponse(userInfo);
					loginResponse.setResponseCode(ResponseCodes.SMS_TOKEN_INAVALID);
					loginResponse.setResponseMessage("SMS Token Not Verified !");
					loginResponse.setResponseStatus(Boolean.valueOf(false));
				}
			}

		} else {
			/* User Info can't be Null */
			listOfErrorForCustomer = new ArrayList<ValidationError>();
			listOfErrorForCustomer.add(new ValidationError(Constants.ACCESS_TOKEN, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_ACCESS_TOKEN),ErrorCodes.INVALID_ACCESS_TOKEN));
			return new ErrorResponse(ResponseCodes.OTP_FAILED, listOfErrorForCustomer);
		}

		return loginResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.clicktable.service.intf.CustomerLoginService#customerResendOTP(java
	 * .lang.String)
	 */
	@Override
	public BaseResponse customerResendOTP(String guid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse sendOTP(GuestProfile customer, String header) {
		// TODO Auto-generated method stub

		UserInfoModel userInfo = null;
		List<ValidationError> listOfErrorForCustomer = new ArrayList<ValidationError>();
		BaseResponse response;
		GuestProfile profile = null;
		/* Validating User Info Token before go ahead */
		if (null != header && !header.trim().equalsIgnoreCase("")) {
			userInfo = authorizationService.getUserInfoByToken(header);
		} else {
			listOfErrorForCustomer.add(new ValidationError(Constants.ACCESS_TOKEN, UtilityMethods.getErrorMsg(ErrorCodes.ACCESS_TOKEN_MISSING),
					ErrorCodes.ACCESS_TOKEN_MISSING));
			return new ErrorResponse(ResponseCodes.INVALID_ACCESS_TOKEN, listOfErrorForCustomer);
		}

		/* Validate Guest GUID */
		if (null != userInfo && userInfo.getRoleId() == Constants.CUSTOMER_ROLE_ID) {
			if (null != customer.getGuid()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(Constants.GUID, customer.getGuid());
				params.put(Constants.STATUS, customer.getStatus());
				profile = customerDao.findGuest(params);
				if (null == profile) {
					listOfErrorForCustomer.add(new ValidationError(Constants.GUEST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_GUEST_GUID),
							ErrorCodes.INVALID_GUEST_GUID));
					return new ErrorResponse(ResponseCodes.NO_USER_FOUND, listOfErrorForCustomer);
				}
			}


			/* Validate Guest Mobile */
			if (null != customer.getMobile()) {

				/* Sending Verification Code */
				List<String> list = new ArrayList<String>();
				list.add(customer.getMobile());

				/* Generate Message */
				int otp_token = UtilityMethods.generateOTP();
				SMSResponse sms_response = new SMSResponse();
				Object param[] = { profile.getFirstName()/*
														 * +" "+profile.getLastName
														 * ()
														 */, String.valueOf(otp_token) };
				String sms_messge = UtilityMethods.sendSMSFormat(param, Constants.SMS_LOGIN_OTP_MSG);
				sms_response = notification.sendSMS(list, sms_messge, false).get(0);
				if (((SMSResponse) sms_response).getSmsStatus().equalsIgnoreCase(ResponseCodes.SMS_SENT)) {
					profile.setMobile(customer.getMobile());
					profile.setOtp_generated_time(new Timestamp(new Date().getTime()));
					profile.setOtpToken(String.valueOf(otp_token));
					customerDao.update(profile);
					response = new LoginResponse();
					UserInfoModel userInfoModel = new UserInfoModel(customer);

					((LoginResponse) response).setUserInfo(userInfoModel);
					response.createResponse(ResponseCodes.SMS_SENT, true);
					((LoginResponse) response).setOtpRequire(true);

				} else {
					listOfErrorForCustomer = new ArrayList<ValidationError>();
					listOfErrorForCustomer.add(new ValidationError(Constants.SMS_NOT_SENT, UtilityMethods.getErrorMsg(ErrorCodes.SMS_NOT_DELIVERED),
							ErrorCodes.SMS_NOT_DELIVERED));
					return new ErrorResponse(ResponseCodes.SMS_ERROR, listOfErrorForCustomer);
				}

			} else {
				listOfErrorForCustomer = new ArrayList<ValidationError>();
				listOfErrorForCustomer.add(new ValidationError(Constants.MOBILE, UtilityMethods.getErrorMsg(ErrorCodes.CUST_MOBILE_REQUIRED),
						ErrorCodes.CUST_MOBILE_REQUIRED));
				return new ErrorResponse(ResponseCodes.CUSTOMER_ADDED_FAILURE, listOfErrorForCustomer);
			}
		} else {
			/* User Info can't be Null */
			listOfErrorForCustomer = new ArrayList<ValidationError>();
			listOfErrorForCustomer.add(new ValidationError(Constants.ACCESS_TOKEN, UtilityMethods.getErrorMsg(ErrorCodes.INVALID_ACCESS_TOKEN),
					ErrorCodes.INVALID_ACCESS_TOKEN));
			return new ErrorResponse(ResponseCodes.INVALID_ACCESS_TOKEN, listOfErrorForCustomer);
		}
		return response;
	}


	@Override
	public BaseResponse deleteCustomer(GuestProfile customer, String header) {
		List<ValidationError> listOfErrorForCustomer=new ArrayList<ValidationError>();

		if (null != header && !header.trim().equalsIgnoreCase("")) {

			UserInfoModel userInfo = authorizationService.getUserInfoByToken(header);

			//customer.setInfoOnCreate(userInfo);

			if ((!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))) {
				customer.setRestGuid(userInfo.getRestGuid());
			} else if(customer.getRestGuid()==""||customer.getRestGuid()==null){
				listOfErrorForCustomer.add(new ValidationError(Constants.REST_GUID, UtilityMethods.getErrorMsg(ErrorCodes.REST_GUID_REQUIRED),ErrorCodes.REST_GUID_REQUIRED));
			}
			if(customer.getGuid()==""||customer.getGuid()==null){
				listOfErrorForCustomer.add(new ValidationError(Constants.GUID, UtilityMethods.getErrorMsg(ErrorCodes.GUID_REQUIRED),
						ErrorCodes.GUID_REQUIRED));
			}
			if(listOfErrorForCustomer.isEmpty()){
				listOfErrorForCustomer.addAll(customerValidator.validateCustomerForRestaurant(customer, customer.getRestGuid()));
				if(listOfErrorForCustomer.isEmpty()){
					Map<String, Object> params=new HashMap<String,Object>();
					params.put(Constants.STATUS, Constants.DELETED_STATUS);
					params.put(Constants.REST_GUID, customer.getRestGuid());
					params.put(Constants.GUID, customer.getGuid());
					
					List<Reservation> reservations = resvRepo.get_reservations_for_guest(customer.getGuid(), customer.getRestGuid());
					  if(reservations.size() > 0 )
					  {
					      String errorMsg = UtilityMethods.getErrorMsg(ErrorCodes.RESERVATION_EXIST_FOR_GUEST) + " Reservation guids are : ";
					      for(Reservation resv : reservations)
					      {
						  errorMsg += (resv.getGuid() + " , ");
					      }
					       
					      errorMsg = errorMsg.substring(0, errorMsg.length() - 1);
					      
					      listOfErrorForCustomer.add(new ValidationError(Constants.GUID, errorMsg, ErrorCodes.RESERVATION_EXIST_FOR_GUEST));
					      return new ErrorResponse(ResponseCodes.CUSTOMER_UPDATED_FAILURE, listOfErrorForCustomer);
					  }
					  
					
					  Map<String,Object> barParams=new HashMap<String,Object>();
					    //barParams.put(Constants.STATUS, Constants.CREATED);
					    barParams.put(Constants.REST_GUID, customer.getRestGuid());
					    barParams.put(Constants.GUEST_GUID, customer.getGuid());
						
						List<BarEntry> barEntries = barEntryDao.findByFields(BarEntry.class, barParams);
						  if(barEntries.size() > 0 )
						  {
						      String errorMsg = UtilityMethods.getErrorMsg(ErrorCodes.BAR_ENTRY_EXIST_FOR_GUEST) + " Bar Entry guids are : ";
						      for(BarEntry bar : barEntries)
						      {
							  errorMsg += (bar.getGuid() + " , ");
						      }
						       
						      errorMsg = errorMsg.substring(0, errorMsg.length() - 1);
						      
						      listOfErrorForCustomer.add(new ValidationError(Constants.GUID, errorMsg, ErrorCodes.BAR_ENTRY_EXIST_FOR_GUEST));
						      return new ErrorResponse(ResponseCodes.CUSTOMER_UPDATED_FAILURE, listOfErrorForCustomer);
						  }

					
					//params.put(Constants.UPDATED_DATE, customer.getUpdatedDate());
					customerDao.deleteGuest(params);
					LoginResponse response = new LoginResponse();
					response.createResponse(ResponseCodes.CUSTOMER_UPDATED_SUCCESS, true);
					return response;
				}else{
					return new ErrorResponse(ResponseCodes.CUSTOMER_UPDATED_FAILURE, listOfErrorForCustomer);
				}
				
			}else{
				return new ErrorResponse(ResponseCodes.CUSTOMER_UPDATED_FAILURE, listOfErrorForCustomer);

			}
			
			
		} else {
			listOfErrorForCustomer.add(new ValidationError(Constants.ACCESS_TOKEN, UtilityMethods.getErrorMsg(ErrorCodes.ACCESS_TOKEN_MISSING),
					ErrorCodes.ACCESS_TOKEN_MISSING));
			return new ErrorResponse(ResponseCodes.INVALID_ACCESS_TOKEN, listOfErrorForCustomer);
		}

		
		

	}
	 

}



