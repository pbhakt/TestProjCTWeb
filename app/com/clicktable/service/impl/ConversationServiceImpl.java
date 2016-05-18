package com.clicktable.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;

import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.PromotionDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.dao.intf.TemplateDao;
import com.clicktable.model.EventPromotion;
import com.clicktable.model.GuestConversation;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.Miscall;
import com.clicktable.model.Reservation;
import com.clicktable.model.Restaurant;
import com.clicktable.model.Template;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.CountResponse;
//import com.clicktable.response.CountResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.PostResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.ConversationService;
import com.clicktable.service.intf.NotificationService;
import com.clicktable.service.intf.ReservationService;
import com.clicktable.util.Constants;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.ConversationValidator;
import com.clicktable.validate.CustomerValidator;
import com.clicktable.validate.ValidationError;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;

/**
 * 
 * @author g.singh
 *
 */

@org.springframework.stereotype.Service
public class ConversationServiceImpl implements ConversationService {

	@Autowired
	RestaurantDao restaurantDao;

	@Autowired
	PromotionDao promotionDao;

	@Autowired
	ConversationValidator validateConversationObject;
	@Autowired
	CustomerDao customerDao;
	@Autowired
	ReservationDao reservationDao;

	@Autowired
	NotificationService notification;

	@Autowired
	TemplateDao templateDao;

	@Autowired
	RestaurantDao restDao;

	@Autowired
	ReservationService resvService;

	@Autowired
	AuthorizationService authService;

	@Autowired
	CustomerValidator custValidator;

	@Override
	@Transactional
	public void addGuestResponse(Miscall miscall) {
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		listOfError.addAll(validateConversationObject.validateMiscall(miscall));
		if (listOfError.isEmpty()) {
			Map<String, Object> qryParamMap = new HashMap<String, Object>();
			qryParamMap.put(Constants.MOBILE, miscall.getMsisdn().substring(2));
			qryParamMap.put(Constants.STATUS, Constants.ACTIVE_STATUS);
			List<GuestProfile> customerList = customerDao.findByFields(
					GuestProfile.class, qryParamMap);
			if (!customerList.isEmpty()) {
				GuestProfile guest = customerList.get(0);
				List<Reservation> reservations = reservationDao
						.getGuestUpcomingReservation(guest.getGuid(),
								miscall.getExtension());
				reservations.forEach(res -> {
					Reservation reservation = res;
					reservation.setReservationStatus(Constants.CANCELLED);
					reservation.setReasonToCancel("missed call");
					reservation.setUpdatedDate(new Date(Calendar.getInstance()
							.getTimeInMillis()));
					reservation.setUpdatedBy(res.getRestaurantGuid());
					String token = authService.loginAsInternal();
					resvService.patchReservation(reservation, token);
				});
			}
		}
	}

	@Override
	@Transactional
	public BaseResponse addConversation(GuestConversation conversation,
			String token) {
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		List<ValidationError> listOfError = new ArrayList<ValidationError>();
		listOfError.addAll(validateConversationObject
				.validateConversationOnAdd(conversation, userInfo));
		BaseResponse response = null;
		if (listOfError.isEmpty()) {
			response = addConversationAndMsg(conversation, false);
		} else {
			response = new ErrorResponse(
					ResponseCodes.CONVERSATION_ADDED_FAILURE, listOfError);
		}
		return response;
	}

	@Override
	public BaseResponse addConversationAndMsg(GuestConversation conversation,
			boolean b) {
		String createdBy = null;
		String updatedBy = null;
		List<ValidationError> listOfError = new ArrayList<ValidationError>();

		if (conversation.getGuestMobileNum() == null) {
			GuestProfile guest = custValidator.validateGuid(
					conversation.getGuestGuid(), listOfError);
			conversation.setGuestMobileNum(guest.getMobile());
		}

		if(conversation!=null){
			createdBy = conversation.getCreatedBy();
			updatedBy = conversation.getUpdatedBy();
		}
		
		JsonNode jsonNode = Json.toJson(conversation);
		ObjectNode jNode = (ObjectNode) jsonNode; 
		
		jNode.put("createdBy", createdBy);
		jNode.put("updatedBy", updatedBy);
		System.out.println("Conversation Json:"+jsonNode.toString());
		Logger.info("Conversation Json:"+jsonNode.toString());
		if (listOfError.isEmpty()) {
			Promise<JsonNode> accResponse = WS
					.url(UtilityMethods.getConfString(Constants.CT_SUPPORT_URL)
							+ Constants.CONVERSATION_URI)
					.post(jsonNode).map(accessResponse -> {
						JsonNode responseJson = accessResponse.asJson();
						return responseJson;
					});
			return new BaseResponse(ResponseCodes.CONVERSATION_INITIATD_SUCCESFULLY, true, null);
		} else {
			return new ErrorResponse(ResponseCodes.CONVERSATION_ADDED_FAILURE,
					listOfError);
		}

	}

	@Override
	@Transactional
	public BaseResponse addEventPromotion(EventPromotion eventPromotion,
			String token) {
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		List<ValidationError> listOfErrorForConversation = validateConversationObject
				.validatePromotionalConversation(eventPromotion, userInfo);
		BaseResponse response = null;
		if (listOfErrorForConversation.isEmpty()) {
			String guid = promotionDao.addEventPromotion(eventPromotion);
			if (guid != null) {

				List<List<String>> guestmobiles = customerDao
						.getGuestMobileByTagsForEvent(eventPromotion);
				GuestConversation guestConversation = new GuestConversation(
						null, eventPromotion);
				response = addConversationAndMsg(guestmobiles,
						guestConversation, eventPromotion);

			} else {
				response = new ErrorResponse(
						ResponseCodes.EVENT_PROMOTION_ADDED_FAILURE,
						listOfErrorForConversation);
			}
		} else {
			response = new ErrorResponse(
					ResponseCodes.EVENT_PROMOTION_ADDED_FAILURE,
					listOfErrorForConversation);
		}

		return response;
	}

	private BaseResponse addConversationAndMsg(List<List<String>> guestmobiles,
			GuestConversation guestConversation, EventPromotion eventPromotion) {

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jNode = mapper.createObjectNode();
		jNode.set("Conversation", Json.toJson(guestConversation));
		ArrayNode aNode = mapper.createArrayNode();
		for (List<String> guestInfo : guestmobiles) {
			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("guestGuid", guestInfo.get(1));
			infoMap.put("guestMobileNum", guestInfo.get(0));
			aNode.add(Json.toJson(infoMap));
		}
		jNode.set("GuestInfo", aNode);
		
		JsonNode json = jNode.get("Conversation");
		((ObjectNode) json).put("createdBy", eventPromotion.getCreatedBy());
		((ObjectNode) json).put("updatedBy", eventPromotion.getUpdatedBy());
		jNode.set("Conversation", json);
		
		System.out.println("Promotion Json:"+jNode.toString());
		Logger.info("Promotion Json:"+jNode.toString());
		Promise<JsonNode> accResponse = WS
				.url(UtilityMethods.getConfString(Constants.CT_SUPPORT_URL)
						+ Constants.CONVERSATION_URI + "/multiple").post(jNode)
				.map(accessResponse -> {
					JsonNode responseJson = accessResponse.asJson();
					return responseJson;
				});

		JsonNode responseJson = accResponse.get(120000);
		return Json.fromJson(responseJson, PostResponse.class);
	}

	@Override
	public BaseResponse getEventPromotionGuestCount(
			EventPromotion eventPromotionCount, String token) {

		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		eventPromotionCount.setGuestCount(1);
		eventPromotionCount.setGuid(UtilityMethods.generateCtId());
		eventPromotionCount.setMessage(Constants.MESSAGE);
		List<ValidationError> listOfErrorForConversation = validateConversationObject
				.validatePromotionalCount(eventPromotionCount, userInfo);

		BaseResponse response = null;
		if (listOfErrorForConversation.isEmpty()) {
			Integer total = customerDao.totalGuestCount(eventPromotionCount
					.getRestaurantGuid());
			Map<String, Integer> countMap = new HashMap<String, Integer>();
			if (total > 0)
				countMap = customerDao
						.getGuestCountByTagsForEvent(eventPromotionCount);
			Integer count = 0;
			Integer dnd = 0;
			count = countMap.get("count");
			dnd = countMap.get("dnd");

			response = new CountResponse<EventPromotion>(
					ResponseCodes.EVENT_PROMOTION_COUNT_FETCH_SUCCESFULLY, dnd,
					count, total, 0.0f, 0.0f);

		} else {
			response = new ErrorResponse(
					ResponseCodes.EVENT_PROMOTION_COUNT_FAILURE,
					listOfErrorForConversation);
		}
		return response;
	}

	@Override
	public BaseResponse getEventPromotionGuestInfo(
			EventPromotion eventPromotion, String token, Map<String,Object> params) {

		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		
		Map<String, Object> queryParams = validateConversationObject
				.validateFinderParams(params, EventPromotion.class);
		
		eventPromotion.setGuestCount(1);
		eventPromotion.setGuid(UtilityMethods.generateCtId());
		eventPromotion.setMessage(Constants.MESSAGE);
		List<ValidationError> listOfErrorForConversation = validateConversationObject
				.validatePromotionalCount(eventPromotion, userInfo);

		BaseResponse response = null;

		if (listOfErrorForConversation.isEmpty()) {
			List<Map<String, Object>> guestInfo = customerDao
					.getfilteredEventGuest(eventPromotion,queryParams);

			List<GuestProfile> guestList = new ArrayList<GuestProfile>();
			addFileterdGuestModel(guestList, guestInfo);

			response = new GetResponse<GuestProfile>(
					ResponseCodes.FILTERED_GUEST_FETCHED_SUCCESSFULLY,
					guestList);
		} else {
			response = new ErrorResponse(
					ResponseCodes.FILTERED_GUEST_CAN_NOT_BE_FECTHED,
					listOfErrorForConversation);
		}
		return response;
	}

	@Override
	@Transactional
	public BaseResponse addTemplate(Template template, String token) {
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		List<ValidationError> listOfErrorForConversation = validateConversationObject
				.validateTemplateOnAdd(template, userInfo);
		BaseResponse response = null;
		if (listOfErrorForConversation.isEmpty()) {
			String result = templateDao.addTemplate(template);
			if (result != null)
				response = new PostResponse<Restaurant>(
						ResponseCodes.TEMPLATE_ADDED_SUCCESFULLY, result);
			else
				response = new ErrorResponse(
						ResponseCodes.TEMPLATE_ADDED_FAILURE,
						listOfErrorForConversation);
		} else {
			response = new ErrorResponse(ResponseCodes.TEMPLATE_ADDED_FAILURE,
					listOfErrorForConversation);
		}
		return response;
	}

	@Override
	public BaseResponse getConversation(Map<String, Object> params) {
		BaseResponse getResponse = null;

		Map<String, Object> qryParamMap = validateConversationObject
				.validateFinderParams(params, GuestConversation.class);
		// TODO call support API to fetch conversations
		/*
		 * List<GuestConversation> conversationList = conversationDao
		 * .findByFields(GuestConversation.class, qryParamMap); getResponse =
		 * new GetResponse<GuestConversation>(
		 * ResponseCodes.CONVERSATION_FETCH_SUCCESFULLY, conversationList);
		 */
		return getResponse;
	}

	@Override
	public BaseResponse getEventPromotion(Map<String, Object> params) {
		BaseResponse getResponse;
		Map<String, Object> qryParamMap = validateConversationObject
				.validateFinderParams(params, EventPromotion.class);
		List<EventPromotion> eventPromotions = promotionDao.findByFields(
				EventPromotion.class, qryParamMap);
		getResponse = new GetResponse<EventPromotion>(
				ResponseCodes.EVENT_PROMOTION_FETCH_SUCCESFULLY,
				eventPromotions);
		return getResponse;
	}

	@Override
	public BaseResponse getTemplate(Map<String, Object> params) {
		BaseResponse getResponse;
		Map<String, Object> qryParamMap = validateConversationObject
				.validateFinderParams(params, Template.class);
		List<Template> template = templateDao.findByFields(Template.class,
				qryParamMap);
		getResponse = new GetResponse<Template>(
				ResponseCodes.TEMPLATE_FETCH_SUCCESFULLY, template);
		return getResponse;
	}

	private static String mapToParams(Map<String, Object> map) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String key : map.keySet()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append("&");
			}
			String value = map.get(key).toString();
			stringBuilder.append(key);
			stringBuilder.append("=");
			stringBuilder.append(value);
		}

		return stringBuilder.toString();
	}

	/*@Override
	public Promise<BaseResponse> getEventPromotionReport(String token,
			Map<String, Object> stringParamMap) {
		String restGuid, fileFormat;
		UserInfoModel userInfo = authService.getUserInfoByToken(token);
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		Map<String, Object> params = validateConversationObject
				.validateParamsForReport(stringParamMap, errorList, userInfo);

		fileFormat = params.get(Constants.FILE_FORMAT).toString();
		restGuid = params.get(Constants.REST_GUID).toString();
		if (errorList.isEmpty()) {
			return WS
					.url(UtilityMethods.getConfString(Constants.CT_SUPPORT_URL)
							+ stringParamMap.get("url") + "?"
							+ mapToParams(params))
					.get()
					.map(supportResponse -> {
						JsonNode json = supportResponse.asJson();
						File file = null;
						try {
							file = File
									.createTempFile("temp", "." + fileFormat);
						} catch (IOException e) {
							Logger.debug("e=" + e.getMessage());
							e.printStackTrace();
							errorList.add(new ValidationError(
									Constants.CONVERSATION, e.getMessage()));
							return (BaseResponse) (new ErrorResponse(
									ResponseCodes.CONVERSATION_REPORT_FAILURE,
									errorList));
						}
						String outputFile = file.getPath();
						if (json.get("responseStatus").booleanValue()) {
							final JsonNode arrNode = json.get("mapList");
							Integer count = Lists.newArrayList(
									arrNode.elements()).size();
							try {
								if (count > 0)
									if ("csv".equals(fileFormat)) {
										createPromotionCSV(outputFile, arrNode);
									} else {
										Restaurant restaurant = restaurantDao
												.findRestaurantByGuid(restGuid);
										createPromotionPdf(outputFile, arrNode,
												restaurant);
									}
								else {
									errorList.add(validateConversationObject
											.createError(
													Constants.CONVERSATION,
													ErrorCodes.CONVERSATIONS_NOT_FOUND));
									return (BaseResponse) (new ErrorResponse(
											ResponseCodes.CONVERSATION_REPORT_FAILURE,
											errorList));
								}

							} catch (Exception e) {
								// TODO catch
								Logger.debug("--------------------------------------------------------------------------------");
								Logger.error(e.getMessage());
								errorList.add(new ValidationError(
										Constants.CONVERSATION, e.getMessage(),
										ErrorCodes.REPORT_CREATION_EXCEPTION));
								return new ErrorResponse(
										ResponseCodes.CONVERSATION_REPORT_FAILURE,
										errorList);

							}

						} else {
							return (BaseResponse) (Json.fromJson(json,
									BaseResponse.class));
						}
						return (BaseResponse) (new SupportResponse<File>(
								ResponseCodes.RESERVATION_CSV_FETCH_SUCCESFULLY,
								file));
					});
		} else {
			return Promise.promise(() -> (BaseResponse) (new ErrorResponse(
					ResponseCodes.CONVERSATION_REPORT_FAILURE, errorList)));

		}
		

	}

	private void createPromotionPdf(String outputFile, JsonNode arrNode,
			Restaurant restaurant) throws MalformedURLException, IOException,
			DocumentException {
		// TODO Auto-generated method stub

		int counter = 0;
		int column = 8;
		FileOutputStream out = null;
		Document document = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

		String message, smsCount, dateTime, success, failure, dnd, pending, amtPayble;

		String[] header = UtilityMethods
				.getEnumValues(Constants.PROMOTION, Constants.CSV_HEADERS)
				.stream().toArray(String[]::new);

		com.itextpdf.text.Font fontRegular = UtilityMethods.getFont(
				"SansSerif", 7, false);
		com.itextpdf.text.Font fontbold = UtilityMethods.getFont("SansSerif",
				7, true);
		fontRegular.setColor(new BaseColor(89, 89, 89));
		BaseColor bgColor = null;
		BaseColor CUSTM_GREY = new BaseColor(242, 242, 242);

		out = new FileOutputStream(outputFile);
		document = new Document();
		PdfWriter.getInstance(document, out);
		document.open();

		PdfPTable tableMain = new PdfPTable(2); // Outer Table
		tableMain.setWidthPercentage(102);

		PdfPTable table = new PdfPTable(column);
		table.setWidthPercentage(100);
		float[] width = { 5f, 1f, 2f, 1f, 1f, 1f, 1f, 1f };
		table.setWidths(width);
		UtilityMethods.generateTableColumns(table, header, column,
				BaseColor.WHITE, fontbold, 1);
		table.setHeaderRows(1);

		for (final JsonNode objNode : arrNode) {
			message = objNode.get("message").asText();
			smsCount = objNode.get("smsLength").asText();
			dateTime = dateFormat.format(objNode.get("sentDate").asLong());
			success = objNode.get("success").asText();
			failure = objNode.get("fail").asText();
			dnd = objNode.get("dnd").asText();
			pending = objNode.get("pending").asText();
			amtPayble = objNode.get("amountPayable").asText();

			if (counter % 2 == 0)
				bgColor = CUSTM_GREY;
			else
				bgColor = BaseColor.WHITE;

			String[] data = { message, smsCount, dateTime, success, failure,
					dnd, pending, amtPayble };
			UtilityMethods.generateTableColumns(table, data, column, bgColor,
					fontRegular, 0);
			counter++;
		}
		Logger.debug("Data added to table rows");
		UtilityMethods.fillRestDetail(tableMain, restaurant,
				Integer.toString(counter) + " Conversations");

		tableMain.setSplitLate(false);
		PdfPCell cell1 = new PdfPCell();
		cell1.setPaddingRight(-0.1f);
		cell1.setPaddingLeft(-0.1f);
		table.setSplitLate(false);
		table.setSplitRows(false);
		cell1.addElement(table);
		cell1.setBorder(0);
		cell1.setColspan(2);
		tableMain.addCell(cell1);

		String imagePath = "conf/Clicktable.jpg";
		Image image = Image.getInstance(imagePath);
		image.setAbsolutePosition(290f, 760f);
		image.scaleAbsolute(50f, 50f);
		document.add(image);

		document.add(tableMain);

		if (document != null)
			document.close();
	}

	private void createPromotionCSV(String outputFile, JsonNode arrNode)
			throws IOException {
		FileWriter fileWriter = new FileWriter(outputFile, true);
		CsvWriter writer = new CsvWriter(fileWriter, ',');
		Logger.debug(UtilityMethods
				.getEnumValues(Constants.PROMOTION, Constants.CSV_HEADERS)
				.stream().toArray(String[]::new)
				+ "");
		writer.writeRecord(UtilityMethods
				.getEnumValues(Constants.PROMOTION, Constants.CSV_HEADERS)
				.stream().toArray(String[]::new));
		for (final JsonNode objNode : arrNode) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm a");
			writer.write(objNode.get("message").asText());
			writer.write(objNode.get("smsLength").asText());
			writer.write(dateFormat.format(objNode.get("sentDate").asLong()));
			writer.write(objNode.get("success").asText());
			writer.write(objNode.get("fail").asText());
			writer.write(objNode.get("dnd").asText());
			writer.write(objNode.get("pending").asText());
			writer.write(objNode.get("amountPayable").asText());
			writer.endRecord();
		}

		writer.close();
		fileWriter.close();

	}*/

	private void addFileterdGuestModel(List<GuestProfile> guestList,
			List<Map<String, Object>> guestInfo) {

		guestInfo
				.forEach(guestinfomationList -> {
					GuestProfile guest = new GuestProfile();

					if (guestinfomationList.size() == 6) {

						guest.setMobile(guestinfomationList.get("mobile") == null ? null
								: guestinfomationList.get("mobile").toString());

						guest.setGuid(guestinfomationList.get("guid") == null ? null
								: guestinfomationList.get("guid")
										.toString());
						guest.setFirstName(guestinfomationList
								.get("first_name") == null ? null
								: guestinfomationList.get("first_name")
										.toString());
						guest.setIsd_code(guestinfomationList.get("isd_code") == null ? null
								: guestinfomationList.get("isd_code")
										.toString());
						guest.setIsVip(guestinfomationList.get("is_vip") == null ? null
								: Boolean.valueOf(guestinfomationList.get(
										"is_vip").toString()));

						guest.setReason(guestinfomationList.get("reason") == null ? null
								: guestinfomationList.get("reason").toString());

						guestList.add(guest);
					}

				});
	}
}
