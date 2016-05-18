package com.clicktable.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Component;

import com.clicktable.response.SMSResponse;
import com.clicktable.service.intf.NotificationService;
import com.clicktable.util.Constants;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage.MessageContent;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage.Recipient;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import com.stormpath.sdk.impl.util.Base64;

/**
 * @author s.gupta
 *
 */
@Component
public class NotificationServiceImpl implements NotificationService {
	MandrillApi mandrillApi = UtilityMethods.getMandrillApi();
	
	/*public void sendSMSProm(List<String> guestmobiles, EventPromotion promotion, UserInfoModel userInfo) {
		
		String urlString = "http://enterprise.smsgupshup.com/GatewayAPI/rest";
		List<List<String>> numbersList = Lists.partition(guestmobiles, 100);
		numbersList.forEach(listNumbers -> {
			String numbers = String.join(",91", listNumbers);
			WS.url(urlString)
					.setQueryParameter("send_to", 91+numbers)
					.setQueryParameter("msg", promotion.getMessage())
					.setQueryParameter("msg_type", "TEXT")
					.setQueryParameter("format", "JSON")
					.setQueryParameter("v", "1.1")
					.setQueryParameter("method", "SendMessage")
					.setQueryParameter("userid", UtilityMethods.getConfString(Constants.GUPSHUP_PROMOTION_USER_ID))
					.setQueryParameter("password", UtilityMethods.getConfString(Constants.GUPSHUP_PROMOTION_PASSWORD))
					.get()
					.map(response -> {
						List<Map<String, String>> mapList = new ArrayList<>();
						ObjectMapper mapper = new ObjectMapper();
						JsonNode jsonNode = response.asJson();
						if (jsonNode.get("response").get("status").textValue().equalsIgnoreCase("success")) {
							if(jsonNode.has("data")){
								mapList = mapper.readValue(jsonNode.get("data").get("response_messages").toString(), ArrayList.class);
							}else{
								mapList = mapper.readValue("["+jsonNode.get("response").toString()+"]", ArrayList.class);
							}
							mapList.forEach(map -> {
								map.put("guid", UtilityMethods.generateCtId());
							});

							List<List<Map<String, String>>> numLists = Lists.partition(mapList, 50);
							numLists.forEach(conversationList -> {
								conversation.addMultipleConversations(conversationList, promotion, userInfo);
							});
						} else {
							ArrayList<String> to = new ArrayList<String>();
							ArrayList<String> tags = new ArrayList<String>();
							UtilityMethods.getEnumValues(Constants.ERROR,Constants.EMAIL).forEach(tos -> {
								to.add(tos);
							});
							
							tags.add(Constants.EVENT_PROMOTION_LABEL);
							sendEmail(to, "Error in Sending Promotional Messages", response.getUri() + "\n"
									+ jsonNode.get("response").get("details").asText(), tags);
						}
						return null;
					});
		});

	}*/

	public List<SMSResponse> sendSMS(List<String> list, String message, Boolean promotion) {
		List<SMSResponse> smsResponses = new ArrayList<SMSResponse>();
		try {
			String urlString = "http://enterprise.smsgupshup.com/GatewayAPI/rest";
			String numbers = String.join(",", list);
			String data = "";
			data += "method=SendMessage";
			if (promotion) {
				data += "&userid=" + UtilityMethods.getConfString(Constants.GUPSHUP_PROMOTION_USER_ID);
				data += "&password=" + URLEncoder.encode(UtilityMethods.getConfString(Constants.GUPSHUP_PROMOTION_PASSWORD), "UTF-8");
			} else {
				data += "&userid=" + UtilityMethods.getConfString(Constants.GUPSHUP_USER_ID);
				data += "&password=" + URLEncoder.encode(UtilityMethods.getConfString(Constants.GUPSHUP_PASSWORD), "UTF-8");
			}
			data += "&msg=" + URLEncoder.encode(message, "UTF-8");
			data += "&send_to=" + numbers;
			data += "&msg_type=TEXT";
			data += "&format=TEXT";
			data += "&v=1.1";
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlString + "?" + data);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-Type", "application/json");
			HttpResponse response = client.execute(request);

			StatusLine sl = response.getStatusLine();

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			Stream<String> lines = rd.lines();
			lines.forEach(line -> {
				SMSResponse sms_response = new SMSResponse();
				String[] datas = line.split("[|]");
				String status = datas[0].trim();

				sms_response.setMessage(message);
				if (status.equals("success")) {
					sms_response.setStatus(Constants.MSG_SENT);
					sms_response.setSmsStatus(ResponseCodes.SMS_SENT);
					sms_response.setId(datas[2].trim());
					sms_response.setPhone(datas[1]);
				} else {
					sms_response.setStatus(Constants.ERROR);
					sms_response.setSmsStatus(ResponseCodes.SMS_ERROR);
					sms_response.setId(datas[1]);
				}
				smsResponses.add(sms_response);
			});

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return smsResponses;
	}

	public void sendEmail(ArrayList<String> to, String subject, String text, ArrayList<String> tags, File attachment, String attachmentName) {
		try {
			
			MandrillMessage message = new MandrillMessage();
			message.setSubject(subject);
			message.setText(text);
			message.setAutoText(true);

			message.setFromEmail(UtilityMethods.getServerConfigString(Constants.SMTP_USER));
			// add recipients
			ArrayList<Recipient> recipients = new ArrayList<Recipient>();
			to.forEach(emailId -> {
				Recipient recipient = new Recipient();
				recipient.setEmail(emailId);
				recipients.add(recipient);
			});
			message.setTo(recipients);
			
			message.setPreserveRecipients(true);
			
			if (!tags.isEmpty())
				message.setTags(tags);
			
			if (attachment != null) {
				List<MessageContent> attachments = new ArrayList<MessageContent>();
				MessageContent mc = new MessageContent();
				InputStream is = new FileInputStream(attachment);
				
				long length = attachment.length();
				
				byte[] bytes = new byte[(int) length];
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
				}
				
				is.close();
				byte[] encoded = Base64.encodeBase64(bytes);
				
				String encodedString = new String(encoded);
				mc.setContent(encodedString);
				
				mc.setName(attachmentName);
				attachments.add(mc);
				
				message.setAttachments(attachments);
			}

			// message.setAttachments(attachments);

			MandrillMessageStatus[] messageStatusReports = mandrillApi.messages().send(message, false);
		} catch (MandrillApiError | IOException e) {
			// TODO Auto-generated catch block
			play.Logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public void sendEmail(ArrayList<String> to, String subject, String text, ArrayList<String> tags) {
		try {
			// create your message
			MandrillMessage message = new MandrillMessage();
			message.setSubject(subject);
			message.setText(text);
			message.setAutoText(true);
			message.setFromEmail(UtilityMethods.getServerConfigString(Constants.SMTP_USER));

			
			// add recipients
			ArrayList<Recipient> recipients = new ArrayList<Recipient>();
			to.forEach(emailId -> {
				Recipient recipient = new Recipient();
				recipient.setEmail(emailId);
				recipients.add(recipient);
			});
			message.setTo(recipients);
			
			message.setPreserveRecipients(true);
			
			if (!tags.isEmpty())
				message.setTags(tags);
			
			// message.setAttachments(attachments);

			MandrillMessageStatus[] messageStatusReports = mandrillApi.messages().send(message, false);
		} catch (MandrillApiError | IOException e) {
			
			
			System.out.println(".........................error............................");
			// TODO Auto-generated catch block
			play.Logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public void sendEmail(ArrayList<String> to, ArrayList<String> tags, String templateName, Map<String, String> templateContent) {
		try {
			MandrillMessage message = new MandrillMessage();
			message.setAutoText(true);
			message.setFromEmail(UtilityMethods.getServerConfigString(Constants.SMTP_USER));

			if (!tags.isEmpty())
				message.setTags(tags);
			// add recipients
			ArrayList<Recipient> recipients = new ArrayList<Recipient>();
			to.forEach(emailId -> {
				Recipient recipient = new Recipient();
				recipient.setEmail(emailId);
				recipients.add(recipient);
			});
			message.setTo(recipients);
			MandrillMessageStatus[] messageStatusReports = mandrillApi.messages().sendTemplate(templateName, templateContent, message, true);
		} catch (MandrillApiError | IOException e) {
			// TODO Auto-generated catch block
			play.Logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public void sendEmail(ArrayList<String> to, ArrayList<String> tags, String templateName, Map<String, String> templateContent, File attachment,
			String attachmentName) {
		try {
			MandrillMessage message = new MandrillMessage();
			message.setAutoText(true);

			message.setFromEmail(UtilityMethods.getServerConfigString(Constants.SMTP_USER));
			if (!tags.isEmpty())
				message.setTags(tags);
			// add recipients
			ArrayList<Recipient> recipients = new ArrayList<Recipient>();
			to.forEach(emailId -> {
				Recipient recipient = new Recipient();
				recipient.setEmail(emailId);
				recipients.add(recipient);
			});
			if (attachment != null) {
				List<MessageContent> attachments = new ArrayList<MessageContent>();
				MessageContent mc = new MessageContent();
				InputStream is = new FileInputStream(attachment);
				long length = attachment.length();
				byte[] bytes = new byte[(int) length];
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
				}
				is.close();
				byte[] encoded = Base64.encodeBase64(bytes);
				String encodedString = new String(encoded);
				mc.setContent(encodedString);
				mc.setName(attachmentName);
				attachments.add(mc);

				message.setAttachments(attachments);
			}

			// message.setAttachments(attachments);
			message.setTo(recipients);
			MandrillMessageStatus[] messageStatusReports = mandrillApi.messages().sendTemplate(templateName, templateContent, message, true);
		} catch (MandrillApiError | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
