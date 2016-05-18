package com.clicktable.service.intf;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.response.BaseResponse;


@Service
public interface GuestBookService 
{
	

	

	/**
	 * Get guest book data based on parameters
	 * 
	 * @param params
	 * @return
	 */
	BaseResponse getGuestBookData(Map<String, Object> params);

	/**
	 * Get customer by Id
	 * 
	 * @param id
	 * @return
	 */
	//GuestProfile getCustomerById(Long id);
	
	

}
