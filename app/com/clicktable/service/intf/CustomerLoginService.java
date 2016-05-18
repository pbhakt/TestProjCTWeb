package com.clicktable.service.intf;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.GuestProfile;
import com.clicktable.model.TagModelOld;
import com.clicktable.response.BaseResponse;

/**
 */
@Service
public interface CustomerLoginService {
	/**
	 * Log in with Google
	 * 
	 * @param token
	 *            Google access token
	
	 * @param socialID String
	 * @return BaseResponse
	 */
	BaseResponse loginWithGoogle(String token, String socialID);

	/**
	 * Log in with facebook
	 * 
	 * @param token
	 *            Facebook access token
	 * @param socialID 
	
	 * @return BaseResponse
	 */
	BaseResponse loginWithFacebook(String token, String socialID);

	// List<Customer> getAllCustomers();

	/**
	 * Get all customers
	 * 
	
	 * @param params Map<String,Object>
	 * @param token String
	 * @return BaseResponse
	 */
	//BaseResponse getCustomers();

	
	/**
	 * Get customers based on parameters
	 * 
	 * @param params
	 * @return
	 */
	BaseResponse getCustomers(Map<String, Object> params, String token);

	/**
	 * Get customer by Id
	 * 
	
	
	 * @param customer GuestProfile
	 * @return String
	 */
	//GuestProfile getCustomerById(Long id);
	
	
	/**
	 * Add customer in Stormpath
	 * 
	 * @param staff
	 * @return
	 */
	public String addCustomerToStormPath(GuestProfile customer) ;
	
	/**
	 * Add new Staff
	 * 
	
	
	 * @param customer GuestProfile
	 * @param token String
	 * @return BaseResponse
	 */
	public BaseResponse addCustomer(GuestProfile customer, String token);

	/**
	 * Method addCustomersFromCSV.
	 * @param file File (CSV file)
	 * @param token String (access token)
	 * @param restaurantGuid String
	 * @return BaseResponse
	 * 
	 * 
	 */
	BaseResponse addCustomersFromCSV(File file, String token, String restaurantGuid);
	
	/* Consumer will Create a Profile from consumer APP*/

	/**
	 * Method customerVerification.
	 * @param guid String
	 * @param otp_token String
	 * @param header String
	 * @return BaseResponse
	 */
	BaseResponse customerVerification(String guid,String otp_token, String header);

	/**
	 * Method customerResendOTP.
	 * @param guid String
	 * @return BaseResponse
	 */
	BaseResponse customerResendOTP(String guid);

	/**
	 * Method updateConsumerProfile.
	 * @param customer GuestProfile
	 * @return BaseResponse
	 */
	BaseResponse updateConsumerProfile(GuestProfile customer);

	/**
	 * Method updateProfile.
	 * @param guest GuestProfile
	 * @param header String
	 * @param listTag List<TagModelOld>
	 * @return BaseResponse
	 */
	BaseResponse updateProfile(GuestProfile guest, String header,
			List<TagModelOld> listTag);

	/**
	 * Method sendOTP.
	 * @param customer GuestProfile
	 * @param string String
	 * @return BaseResponse
	 */
	BaseResponse sendOTP(GuestProfile customer, String string);

	/**
	 * Method deleteCustomer.
	 * @param customer GuestProfile
	 * @param header String
	 * @return BaseResponse
	 */
	BaseResponse deleteCustomer(GuestProfile customer, String header);

	/*BaseResponse getCustomersReport(String token, String restGuid,
			String fileFormat);*/

}
