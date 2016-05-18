package com.clicktable.service.intf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.clicktable.response.SMSResponse;

@org.springframework.stereotype.Service
public interface NotificationService {
	//void sendSMSProm(List<String> list, EventPromotion promotion,UserInfoModel userInfo);
	List<SMSResponse> sendSMS(List<String> list, String message, Boolean promotion);
	void sendEmail(ArrayList<String> to, String subject, String text, ArrayList<String> tags, File attachment, String attachmentName);
	void sendEmail(ArrayList<String> to, String subject, String text, ArrayList<String> tags) ;
	void sendEmail(ArrayList<String> to, ArrayList<String> tags, String templateName, Map<String, String> templateContent); 
	void sendEmail(ArrayList<String> to, ArrayList<String> tags, String templateName, Map<String, String> templateContent, File attachment, String attachmentName);
}
