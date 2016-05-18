package com.clicktable.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;

import com.clicktable.dao.intf.CalenderEventDao;
import com.clicktable.dao.intf.CustomerDao;
import com.clicktable.dao.intf.ReservationDao;
import com.clicktable.dao.intf.RestaurantDao;
import com.clicktable.model.CalenderEvent;
import com.clicktable.model.GuestProfileCustomModel;
import com.clicktable.model.Reservation;
import com.clicktable.model.Restaurant;
import com.clicktable.model.UserInfoModel;
import com.clicktable.response.BaseResponse;
import com.clicktable.response.ErrorResponse;
import com.clicktable.response.GetResponse;
import com.clicktable.response.SupportResponse;
import com.clicktable.service.intf.AuthorizationService;
import com.clicktable.service.intf.CustomerLoginService;
import com.clicktable.service.intf.EventService;
import com.clicktable.service.intf.ReportService;
import com.clicktable.service.intf.ReservationService;
import com.clicktable.util.Constants;
import com.clicktable.util.ErrorCodes;
import com.clicktable.util.ResponseCodes;
import com.clicktable.util.UtilityMethods;
import com.clicktable.validate.CalenderEventValidator;
import com.clicktable.validate.ConversationValidator;
import com.clicktable.validate.CustomerValidator;
import com.clicktable.validate.ReportValidator;
import com.clicktable.validate.ReservationValidator;
import com.clicktable.validate.RestaurantValidator;
import com.clicktable.validate.ValidationError;
import com.csvreader.CsvWriter;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Component
public class ReportServiceImpl implements ReportService{

	@Autowired
	AuthorizationService authorizationService;
	
	@Autowired
	ReportValidator reportValidator;
	
	@Autowired
	CustomerValidator customerValidator;
	
	@Autowired
	CustomerDao customerDao;
	

	@Autowired
	CustomerLoginService custService;
	
	@Autowired
	EventService calenderEvntService;
	
	@Autowired
	RestaurantDao restaurantDao;
	
	@Autowired
	ReservationValidator reservationValidator;
	
	@Autowired
	ReservationDao reservationDao;
	
	@Autowired
	ReservationService reservationService;
	
	@Autowired
	CalenderEventValidator calenderEventValidator;
	
	@Autowired
	CalenderEventDao calEventDao;
	
	@Autowired
	RestaurantValidator restValidator;
	
	@Autowired
	ConversationValidator conversationValidator;
	
	@Override
	@Transactional(readOnly = true)
	public BaseResponse getCustomersReport(String token,Map<String, Object> stringParamMap) {
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		String fileFormat = null;
		if(stringParamMap.containsKey(Constants.FILE_FORMAT)){
			fileFormat = stringParamMap.get(Constants.FILE_FORMAT).toString();
			reportValidator.validateFileFormat(fileFormat, errorList);
		}else{
			errorList.add(new ValidationError(Constants.FILE_FORMAT, UtilityMethods.getErrorMsg(ErrorCodes.FILE_FORMAT_REQUIRED), ErrorCodes.FILE_FORMAT_REQUIRED));
		}
		
		if (errorList.isEmpty()) {
			File file = createTempFile(Constants.GUEST_LABEL, fileFormat, errorList);
			String outputFile = file.getPath();
			GetResponse<GuestProfileCustomModel> resp = (GetResponse<GuestProfileCustomModel>)custService.getCustomers(stringParamMap, token);
			if(resp.getResponseStatus()){
			List<GuestProfileCustomModel> customerList = resp.getList();
				if (customerList.size() < 2) {
					errorList.add(customerValidator.createError(Constants.GUEST_LABEL,ErrorCodes.GUESTS_NOT_FOUND));
				}
				if (errorList.isEmpty()) {
					try {
						if (Constants.CSV.equals(fileFormat)) {
							createGuestCSV(outputFile, customerList);
						} else {
							UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
							Logger.debug("role id is " + userInfo.getRoleId());
							String restaurantGuid=null;
									if ((!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))) {
										Logger.debug("staff logged in rest id is " + userInfo.getRestGuid());
										restaurantGuid=userInfo.getRestGuid();
									}else if(stringParamMap.containsKey(Constants.REST_GUID)){
										restaurantGuid=stringParamMap.get(Constants.REST_GUID).toString();
									}else{
										errorList.add(customerValidator.createError(Constants.REST_GUID,ErrorCodes.REST_GUID_REQUIRED));
									}
									
							if (errorList.isEmpty()) {
								Restaurant restaurant = restaurantDao.findRestaurantByGuid(restaurantGuid);
								createGuestPdf(outputFile, customerList, restaurant);
							}
						}
					} catch (DocumentException | IOException e) {
						// TODO catch exception
						Logger.error(e.getMessage());
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
	
	private void createGuestPdf(String outputFile, List<GuestProfileCustomModel> customerList, Restaurant restaurant) throws DocumentException, IOException {
		// TODO Auto-generated method stub

		int counter = 0;
		
		FileOutputStream out = null;
		Document document = null;
		String name, email,contact, gender, vip; 
		
		String[] header = UtilityMethods.getEnumValues(Constants.CUSTOMER_MODULE, Constants.CSV_HEADERS).stream().toArray(String[]::new);
		int column = header.length;
		com.itextpdf.text.Font fontRegular = UtilityMethods.getFont("SansSerif", 6, false);
		com.itextpdf.text.Font fontbold = UtilityMethods.getFont("SansSerif", 7, true);
		fontRegular.setColor(new BaseColor(89, 89, 89));
		BaseColor bgColor = null;
		BaseColor CUSTM_GREY = new BaseColor(242, 242, 242);
		Logger.debug("~~~~~~~~~~~~~~~"+customerList.size()+"~~~~~~~~~~~~~"+outputFile);

		
		out = new FileOutputStream(outputFile);
		document = new Document();
		PdfWriter.getInstance(document, out);
		document.open();

		PdfPTable tableMain = new PdfPTable(2); // Outer Table
		tableMain.setWidthPercentage(100);

		PdfPTable table = new PdfPTable(column);
		table.setWidthPercentage(100);
		float[] width = { 8,3,6,3};		//Relative Width of column
		table.setWidths(width);
		generateTableColumns(table, header, column, BaseColor.WHITE, fontbold, 1);
		table.setHeaderRows(1);
			
		for (GuestProfileCustomModel guest : customerList) {
				name = guest.getFirstName();
				email = guest.getEmailId();
				contact = "+" + guest.getIsd_code() + guest.getMobile();
				gender = guest.getGender();

/*				if (guest.getIsVip())
					vip = guest.getReason();
				else
					vip = "";*/

				if (counter % 2 == 0)
					bgColor = CUSTM_GREY;
				else
					bgColor = BaseColor.WHITE;
				String[] data = { name, contact, email, gender};
				generateTableColumns(table, data, column, bgColor, fontRegular,	0);
				counter++;
				
		}
		Logger.debug("Data added to table rows");
		fillRestDetail(tableMain, restaurant, Integer.toString(counter)+" Guests");
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
	
	private void createGuestCSV(String outputFile,List<GuestProfileCustomModel> customerList) throws IOException{
		FileWriter fileWriter = new FileWriter(outputFile, true);
		CsvWriter writer = new CsvWriter(fileWriter, ',');
		writer.writeRecord(UtilityMethods.getEnumValues(Constants.CUSTOMER_MODULE, Constants.CSV_HEADERS).stream().toArray(String[]::new));
		for (GuestProfileCustomModel guest : customerList) {
			writer.write(guest.getFirstName());
			writer.write("+" + guest.getIsd_code() + guest.getMobile());
			writer.write(guest.getEmailId());
			writer.write(guest.getGender());
			writer.endRecord();
		}
		writer.close();
		fileWriter.close();
	}
	
	@Override
	public BaseResponse getReservationsReport(String token, Map<String, Object> stringParamMap) {
		
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		String fileFormat = null;
		if(stringParamMap.containsKey(Constants.FILE_FORMAT)){
			fileFormat = stringParamMap.get(Constants.FILE_FORMAT).toString();
			reportValidator.validateFileFormat(fileFormat, errorList);
		}else{
			errorList.add(new ValidationError(Constants.FILE_FORMAT, UtilityMethods.getErrorMsg(ErrorCodes.FILE_FORMAT_REQUIRED), ErrorCodes.FILE_FORMAT_REQUIRED));
		}
		BaseResponse resvBaseResp = reservationService.getReservation(stringParamMap, token);
		if(resvBaseResp.getResponseStatus()){
			GetResponse<Reservation> resvResponse = (GetResponse<Reservation>) resvBaseResp;
			List<Reservation> reservationData = resvResponse.getList();
			if (reservationData.isEmpty()) {
				errorList.add(reservationValidator.createError(Constants.RESERVATION_LABEL,	ErrorCodes.RESERVATION_NOT_FOUND));
			}else{
				File file = createTempFile(Constants.GUEST_LABEL, fileFormat, errorList);
				String outputFile = file.getPath();
				try {
					if ("csv".equals(fileFormat)) {
						createReservationCSV(outputFile, reservationData);
					} else {
						UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
						Logger.debug("role id is " + userInfo.getRoleId());
						String restaurantGuid=null;
								if ((!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))) {
									Logger.debug("staff logged in rest id is " + userInfo.getRestGuid());
									restaurantGuid=userInfo.getRestGuid();
								}else if(stringParamMap.containsKey(Constants.REST_GUID)){
									restaurantGuid=stringParamMap.get(Constants.REST_GUID).toString();
								}else{
									errorList.add(customerValidator.createError(Constants.REST_GUID,ErrorCodes.REST_GUID_REQUIRED));
								}
						Restaurant restaurant = restaurantDao.findRestaurantByGuid(restaurantGuid);
						createReservationPdf(outputFile, reservationData,restaurant);
					}
				} catch (DocumentException | IOException e) {
					// TODO catch
					Logger.debug("--------------------------------------------------------------------------------");
					Logger.error(e.getMessage());
					errorList.add(new ValidationError(Constants.RESERVATION_LABEL, e.getMessage(), ErrorCodes.REPORT_CREATION_EXCEPTION));
				}
				
				if (errorList.isEmpty()) {
					return new SupportResponse<File>(ResponseCodes.RESERVATION_FILE_FETCH_SUCCESFULLY, file);
				}				
				
			}
		}else{
			errorList.addAll(((ErrorResponse)resvBaseResp).getErrorList());
	
		}
		return new ErrorResponse(ResponseCodes.RESERVATION_FILE_FETCH_FAILURE, errorList);

	}
	
	private void createReservationPdf(String outputFile,List<Reservation> reservationData, Restaurant restaurant) throws DocumentException, IOException{
		// TODO Auto-generated method stub

		int counter = 0;
		
		FileOutputStream out = null;
		Document document = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.REPORT_DATE_FORMAT);
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		String name,contactNo, covers, date, estimatedTime, status, note, createdDate, createdTime; 
		
		String[] header = UtilityMethods.getEnumValues(Constants.RESERVATION, Constants.CSV_HEADERS).stream().toArray(String[]::new);
		int column = header.length;				  
		com.itextpdf.text.Font fontRegular = UtilityMethods.getFont("SansSerif", 8, false);
		com.itextpdf.text.Font fontbold = UtilityMethods.getFont("SansSerif", 8, true);
		fontRegular.setColor(new BaseColor(89, 89, 89));
		BaseColor bgColor = null;
		BaseColor CUSTM_GREY = new BaseColor(242, 242, 242);

		out = new FileOutputStream(outputFile);
		document = new Document(new RectangleReadOnly(850, 1191));
		PdfWriter.getInstance(document, out);
		document.open();

		PdfPTable tableMain = new PdfPTable(2); // Outer Table
		tableMain.setWidthPercentage(100);

		PdfPTable table = new PdfPTable(column);
		table.setWidthPercentage(100);
		float[] width = { 2f, 1.2f, 0.9f, 1f, 1.1f, 1f, 3f, 1f, 0.9f };
		table.setWidths(width);
		generateTableColumns(table, header, column, BaseColor.WHITE, fontbold, 1);
		table.setHeaderRows(1);
			
		for (Reservation resv : reservationData) {
			name = resv.getGuest_firstName();
			contactNo = "+" + resv.getGuest_isd_code()+ resv.getGuest_mobile();
			covers = resv.getNumCovers().toString();
			date = dateFormat.format(resv.getEstStartTime());
			estimatedTime = timeFormat.format(resv.getEstStartTime());
			status = resv.getReservationStatus();
			note = resv.getReservationNote();
			createdDate = dateFormat.format(resv.getCreatedDate());
			createdTime = timeFormat.format(resv.getCreatedDate());

			/*if (Boolean.valueOf(resv.getIsVIP()))
				vip = resv.getReason();
			else
				vip = "";*/

			if (counter % 2 == 0)
				bgColor = CUSTM_GREY;
			else
				bgColor = BaseColor.WHITE;
			String[] data = { name, contactNo, covers, date, estimatedTime, status, note, createdDate, createdTime };
			generateTableColumns(table, data, column, bgColor, fontRegular, 0);
			counter++;
		}
		fillRestDetail(tableMain, restaurant, Integer.toString(counter)+" Reservations");
		tableMain.setSplitLate(false);
		PdfPCell cell1 = new PdfPCell();
		cell1.setPaddingRight(-0.1f);
		cell1.setPaddingLeft(-0.1f);
		table.setSplitLate(false);
		cell1.addElement(table);
		cell1.setBorder(0);
		cell1.setColspan(2);
		tableMain.addCell(cell1);
		
		String imagePath = "conf/Clicktable.jpg";
		Image image = Image.getInstance(imagePath);
		image.setAbsolutePosition(425f, 1110f);
		image.scaleAbsolute(50f, 50f);
		document.add(image);
		
		document.add(tableMain);
		if (document != null)
			document.close();
		
	}

		
	private void createReservationCSV(String outputFile,List<Reservation> reservationData) throws IOException{
		FileWriter fileWriter = new FileWriter(outputFile, true);
		CsvWriter writer = new CsvWriter(fileWriter, ',');
		//Logger.debug(UtilityMethods.getEnumValues(Constants.RESERVATION, Constants.CSV_HEADERS).stream().toArray(String[]::new)+"");
		writer.writeRecord(UtilityMethods.getEnumValues(Constants.RESERVATION, Constants.CSV_HEADERS).stream().toArray(String[]::new));
		for (Reservation resv : reservationData) {
			String name = resv.getGuest_firstName();
			writer.write(name);
			writer.write("+"+resv.getGuest_isd_code()+resv.getGuest_mobile());
			/*if (Boolean.valueOf(resv.getIsVIP()))
				writer.write(resv.getReason());
			else
				writer.write("");*/
			writer.write(resv.getNumCovers().toString());
			SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.REPORT_DATE_FORMAT);
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
			writer.write(dateFormat.format(resv.getEstStartTime()));
			writer.write(timeFormat.format(resv.getEstStartTime()));
			writer.write(resv.getReservationStatus());
			writer.write(resv.getReservationNote());
			writer.write(dateFormat.format(resv.getCreatedDate()));
			writer.write(timeFormat.format(resv.getCreatedDate()));
			writer.endRecord();
		}
		writer.close();
		fileWriter.close();
	}
	
	@Override
	@Transactional(readOnly=true)
	public BaseResponse getCalenderEventsReport(Map<String, Object> params,String token) {
		
		
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		String fileFormat = null;
		if(params.containsKey(Constants.FILE_FORMAT)){
			fileFormat = params.get(Constants.FILE_FORMAT).toString();
			reportValidator.validateFileFormat(fileFormat, errorList);
		}else{
			errorList.add(new ValidationError(Constants.FILE_FORMAT, UtilityMethods.getErrorMsg(ErrorCodes.FILE_FORMAT_REQUIRED), ErrorCodes.FILE_FORMAT_REQUIRED));
		}
		BaseResponse resvBaseResp = calenderEvntService.getCalenderEvents(params, token);
		if(resvBaseResp.getResponseStatus()){
			GetResponse<CalenderEvent> calEvntResponse = (GetResponse<CalenderEvent>) resvBaseResp;
			List<CalenderEvent> calEvents = calEvntResponse.getList();
			if (calEvents.isEmpty()) {
				errorList.add(calenderEventValidator.createError(Constants.CALEVENT_MODULE, ErrorCodes.CALEVENT_NOT_FOUND));
			}else{
				File file = createTempFile(Constants.GUEST_LABEL, fileFormat, errorList);
				String outputFile = file.getPath();
				try {
					if (Constants.CSV.equals(fileFormat)) {
						createCalenderEventCSV(outputFile, calEvents);
					} else {
						UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
						Logger.debug("role id is " + userInfo.getRoleId());
						String restaurantGuid=null;
								if ((!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))) {
									Logger.debug("staff logged in rest id is " + userInfo.getRestGuid());
									restaurantGuid=userInfo.getRestGuid();
								}else if(params.containsKey(Constants.REST_GUID)){
									restaurantGuid=params.get(Constants.REST_GUID).toString();
								}else{
									errorList.add(customerValidator.createError(Constants.REST_GUID,ErrorCodes.REST_GUID_REQUIRED));
								}
								
						if (errorList.isEmpty()) {
							Restaurant restaurant = restaurantDao.findRestaurantByGuid(restaurantGuid);
							createCalenderEventPdf(outputFile, calEvents, restaurant);
						}
					}
				} catch (IOException | DocumentException e) {
					Logger.error(e.getMessage());
					errorList.add(new ValidationError(Constants.CALEVENT_MODULE, e.getMessage(),ErrorCodes.REPORT_CREATION_EXCEPTION));
					return new ErrorResponse(ResponseCodes.CALEVENT_FILE_FETCH_FAILURE,errorList);
				}
				if (errorList.isEmpty()) {
					return new SupportResponse<File>(ResponseCodes.CALEVENT_FILE_FETCH_SUCCESFULLY,file);
				}	
			}
		}else{
			errorList.addAll(((ErrorResponse)resvBaseResp).getErrorList());
		}
		
				return new ErrorResponse(ResponseCodes.CALEVENT_FILE_FETCH_FAILURE, errorList);	
	}
	
	@Override
	@Transactional(readOnly=true)
	public BaseResponse getCalenderEventAttendenceReport(Map<String, Object> params,String token) {
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		String fileFormat = null;
		if(params.containsKey(Constants.FILE_FORMAT)){
			fileFormat = params.get(Constants.FILE_FORMAT).toString();
			reportValidator.validateFileFormat(fileFormat, errorList);
		}else{
			errorList.add(new ValidationError(Constants.FILE_FORMAT, UtilityMethods.getErrorMsg(ErrorCodes.FILE_FORMAT_REQUIRED), ErrorCodes.FILE_FORMAT_REQUIRED));
		}
		BaseResponse resvBaseResp = calenderEvntService.getCalenderEvents(params, token);
		if(resvBaseResp.getResponseStatus()){
			GetResponse<CalenderEvent> calEvntResponse = (GetResponse<CalenderEvent>) resvBaseResp;
			List<CalenderEvent> calEvents = calEvntResponse.getList();
			if (calEvents.isEmpty()) {
				errorList.add(calenderEventValidator.createError(Constants.CALEVENT_MODULE, ErrorCodes.CALEVENT_NOT_FOUND));
			}else{
				File file = createTempFile(Constants.GUEST_LABEL, fileFormat, errorList);
				String outputFile = file.getPath();
				try {
					if (Constants.CSV.equals(fileFormat)) {
								createCalenderEventAttendenceCSV(outputFile, calEvents);
							} else {
								UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
								Logger.debug("role id is " + userInfo.getRoleId());
								String restaurantGuid=null;
										if ((!userInfo.getRoleId().equals(Constants.CUSTOMER_ROLE_ID)) && (!userInfo.getRoleId().equals(Constants.CT_ADMIN_ROLE_ID))) {
											Logger.debug("staff logged in rest id is " + userInfo.getRestGuid());
											restaurantGuid=userInfo.getRestGuid();
										}else if(params.containsKey(Constants.REST_GUID)){
											restaurantGuid=params.get(Constants.REST_GUID).toString();
										}else{
											errorList.add(customerValidator.createError(Constants.REST_GUID,ErrorCodes.REST_GUID_REQUIRED));
										}
										
								if (errorList.isEmpty()) {
									Restaurant restaurant = restaurantDao.findRestaurantByGuid(restaurantGuid);
									createCalenderEventAttendencePdf(outputFile, calEvents, restaurant);
								}
							}
						} catch (IOException | DocumentException e) {
							Logger.debug("--------------------------------------------------------------------------------");
							Logger.error(e.getMessage());
							errorList.add(new ValidationError(Constants.CALEVENT_MODULE, e.getMessage(), ErrorCodes.REPORT_CREATION_EXCEPTION));
						}

						if (errorList.isEmpty()) {
							return new SupportResponse<File>(ResponseCodes.CALEVENT_FILE_FETCH_SUCCESFULLY, file);
						}
					}
		}
		return new ErrorResponse(ResponseCodes.CALEVENT_FILE_FETCH_FAILURE, errorList);
	}
	
	private void createCalenderEventPdf(String outputFile, List<CalenderEvent> eventList, Restaurant restaurant) throws IOException, DocumentException{
		// TODO Auto-generated method stub

		int counter = 0;
		FileOutputStream out = null;
		Document document = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.REPORT_DATE_FORMAT);
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		String name, description, category, startDate, startTime, endDate, endTime, status, createdDate; 
		
		String[] header = UtilityMethods.getEnumValues(Constants.CALEVENT_MODULE, Constants.CSV_HEADERS).stream().toArray(String[]::new);
		int column = header.length;
			  
		com.itextpdf.text.Font fontRegular = UtilityMethods.getFont("SansSerif", 8, false);
		com.itextpdf.text.Font fontbold = UtilityMethods.getFont("SansSerif", 8, true);
		fontRegular.setColor(new BaseColor(89, 89, 89));
		BaseColor bgColor = null;
		BaseColor CUSTM_GREY = new BaseColor(242, 242, 242);

		
		out = new FileOutputStream(outputFile);
		document = new Document(new RectangleReadOnly(850, 1191));
		PdfWriter.getInstance(document, out);
		document.open();

		PdfPTable tableMain = new PdfPTable(2); // Outer Table
		tableMain.setWidthPercentage(102);

		PdfPTable table = new PdfPTable(column);
		table.setWidthPercentage(100);
		float[] width = {0.95f,2.9f,3.9f,1.05f,0.95f,0.95f,1f};
		table.setWidths(width);
		generateTableColumns(table, header, column, BaseColor.WHITE, fontbold, 1);
		table.setHeaderRows(1);
			
		for (CalenderEvent cal : eventList) {
			startDate = dateFormat.format(cal.getStartTime());
			name = cal.getName();
			description = cal.getEventDescription();
			category = cal.getCategory();
			startTime = timeFormat.format(cal.getStartTime());
			endTime = timeFormat.format(cal.getEndTime());
			status = cal.getStatus();
			if (counter % 2 == 0)
				bgColor = CUSTM_GREY;
			else
				bgColor = BaseColor.WHITE;
			String[] data = { startDate,name, description, category, startTime, endTime, status};
			generateTableColumns(table, data, column, bgColor, fontRegular, 0);
			counter++;
		}
		Logger.debug("Data added to table rows");
		fillRestDetail(tableMain, restaurant, Integer.toString(counter)+" Events");
			
		tableMain.setSplitLate(false);
		PdfPCell cell1 = new PdfPCell();
		cell1.setPaddingRight(-0.1f);
		cell1.setPaddingLeft(-0.1f);
		table.setSplitLate(false);
		cell1.addElement(table);
		cell1.setBorder(0);
		cell1.setColspan(2);
		tableMain.addCell(cell1);

		String imagePath = "conf/Clicktable.jpg";
		Image image = Image.getInstance(imagePath);
		image.setAbsolutePosition(425f, 1110f);
		image.scaleAbsolute(50f, 50f);
		document.add(image);

		document.add(tableMain);
		document.close();
	}

	
	

	private void createCalenderEventAttendencePdf(String outputFile,List<CalenderEvent> events, Restaurant restaurant) throws IOException, DocumentException{
		int counter = 0;
		int coverCount = 0;
		
		FileOutputStream out = null;
		Document document = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.REPORT_DATE_FORMAT);
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		com.itextpdf.text.Font fontRegular = UtilityMethods.getFont("SansSerif", 7, false);
		com.itextpdf.text.Font fontbold = UtilityMethods.getFont("SansSerif", 7, true);
		fontRegular.setColor(new BaseColor(89, 89, 89));
		BaseColor bgColor = null;
		BaseColor CUSTM_GREY = new BaseColor(242, 242, 242);
		
		String name, description, category, startDate, startTime, endDate, endTime, createdDate, covers, engagementCount, repeatCount, newCount, announymousCount;
		String[] header = UtilityMethods.getEnumValues(Constants.CALEVENT_ATTNDNS_MODULE, Constants.CSV_HEADERS).stream().toArray(String[]::new);	
		int column = header.length;
		Map<String, Object> resvParams=new HashMap<>();
		resvParams.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		resvParams.put(Constants.RESERVATION_STATUS, Constants.FINISHED);
		Logger.debug("outputFile>>"+outputFile);
		
				
		out = new FileOutputStream(outputFile);

		document = new Document(new RectangleReadOnly(842,595));
		PdfWriter.getInstance(document, out);
		document.open();
		PdfPTable tableMain = new PdfPTable(2); // Outer Table
		tableMain.setWidthPercentage(102.5f);

		PdfPTable table = new PdfPTable(column);
		table.setWidthPercentage(100);
		float[] width = { 0.8f,2.5f, 3.4f, 1f, 0.8f, 0.7f, 0.8f, 1f, 0.6f, 0.8f, 0.6f };
		table.setWidths(width);
		generateTableColumns(table, header, column, BaseColor.WHITE, fontbold, 1);
		table.setHeaderRows(1);
		Logger.debug("Header added to table");
			
		for (CalenderEvent cal : events) {
			resvParams.put(Constants.REST_GUID, cal.getRestaurantGuid());
			resvParams.put(Constants.START_TIME, cal.getStartTime());
			resvParams.put(Constants.END_TIME, cal.getEndTime());

			Logger.debug("1>>" + cal.getRestaurantGuid());
			startDate = dateFormat.format(cal.getStartTime());
			name = cal.getName();
			description = cal.getEventDescription();
			category = cal.getCategory();
			startTime = timeFormat.format(cal.getStartTime());
			endTime = timeFormat.format(cal.getEndTime());
			createdDate = dateFormat.format(cal.getCreatedDate());
			List<Map<String, Object>> dataMap = reservationDao.getReservationsReportData(resvParams);
			Integer[] totalCovers = { 0 };
			Integer[] engagements = { 0 };
			Integer[] repeated = { 0 };
			Integer[] newly = { 0 };
			Integer[] announymous = { 0 };
			dataMap.forEach(info -> {
				Integer i = (Integer) info.get("covers");
				totalCovers[0] = totalCovers[0] + i;
				Integer j = (Integer) info.get("resvCount");
				engagements[0] = engagements[0] + j;
				Boolean status = (Boolean) info.get("status");
				if (status) {
					Boolean dummy = (Boolean) info.get("dummy");
					if (dummy) {
						announymous[0] = announymous[0] + 1;
					} else {
						newly[0] = newly[0] + 1;
						repeated[0] = repeated[0] + (j - 1);
					}
				}
			});
			covers = totalCovers[0].toString();
			engagementCount = engagements[0].toString();
			repeatCount = repeated[0].toString();
			newCount = newly[0].toString();
			announymousCount = announymous[0].toString();
			coverCount = coverCount + totalCovers[0];
				
			if (counter % 2 == 0)
				bgColor = CUSTM_GREY;
			else
				bgColor = BaseColor.WHITE;
			String[] data = {  startDate,name, description, category, startTime, endTime, covers, engagementCount, repeatCount, newCount, announymousCount };
			generateTableColumns(table, data, column, bgColor, fontRegular, 0);
			counter++;
		}
		fillRestDetail(tableMain, restaurant, Integer.toString(coverCount)+" Covers");

		tableMain.setSplitLate(false);
		PdfPCell cell1 = new PdfPCell();
		cell1.setPaddingRight(-0.1f);
		cell1.setPaddingLeft(-0.1f);
		table.setSplitLate(false);
		cell1.addElement(table);
		cell1.setBorder(0);
		cell1.setColspan(2);
		tableMain.addCell(cell1);

		String imagePath = "conf/Clicktable.jpg";
		Image image = Image.getInstance(imagePath);
		image.setAbsolutePosition(420f, 515f);
		image.scaleAbsolute(50f, 50f);
		document.add(image);

		document.add(tableMain);

		if (document != null)
			document.close();
	}
	
	private void createCalenderEventAttendenceCSV(String outputFile,List<CalenderEvent> events) throws IOException {
		FileWriter fileWriter = new FileWriter(outputFile, true);
		CsvWriter writer = new CsvWriter(fileWriter, ',');
		Logger.debug(UtilityMethods.getEnumValues(Constants.CALEVENT_ATTNDNS_MODULE, Constants.CSV_HEADERS).stream().toArray(String[]::new)+"");
		writer.writeRecord(UtilityMethods.getEnumValues(Constants.CALEVENT_ATTNDNS_MODULE, Constants.CSV_HEADERS).stream().toArray(String[]::new));
		Map<String, Object> resvParams=new HashMap<>();
		resvParams.put(Constants.STATUS, Constants.ACTIVE_STATUS);
		resvParams.put(Constants.RESERVATION_STATUS, Constants.FINISHED);
		Logger.debug("outputFile>>"+outputFile);
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.REPORT_DATE_FORMAT);
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		
		for (CalenderEvent cal : events) {
			resvParams.put(Constants.REST_GUID, cal.getRestaurantGuid());
			resvParams.put(Constants.START_TIME, cal.getStartTime());
			resvParams.put(Constants.END_TIME, cal.getEndTime());
			Logger.debug("1>>"+cal.getRestaurantGuid());
				writer.write(dateFormat.format(cal.getStartTime()));
				writer.write(cal.getName());
				writer.write(cal.getEventDescription());
				writer.write(cal.getCategory());
				writer.write(timeFormat.format(cal.getStartTime()));
				writer.write(timeFormat.format(cal.getEndTime()));
				List<Map<String, Object>> data = reservationDao.getReservationsReportData(resvParams);
				Integer[] totalCovers={0};
				Integer[] engagements={0};
				Integer[] repeated={0};
				Integer[] newly={0};
				Integer[] announymous={0};
				data.forEach(info -> {
						Integer i = (Integer) info.get("covers");
						totalCovers[0]=totalCovers[0]+i;
						Integer j = (Integer) info.get("resvCount");
						engagements[0]=engagements[0]+j;
						Boolean status = (Boolean) info.get("status");
						if(status){
							Boolean dummy = (Boolean) info.get("dummy");
							if(dummy){
								announymous[0]=announymous[0]+1;
							}else{
							newly[0]=newly[0]+1;
							repeated[0]=repeated[0]+(j-1);
							}
						}
				});	
				writer.write(totalCovers[0].toString());
				writer.write(engagements[0].toString());
				writer.write(repeated[0].toString());
				writer.write(newly[0].toString());
				writer.write(announymous[0].toString());
				writer.endRecord();
		}
		writer.close();
		fileWriter.close();
	}


	private void createCalenderEventCSV(String outputFile,List<CalenderEvent> events) throws IOException {
		FileWriter fileWriter = new FileWriter(outputFile, true);
		CsvWriter writer = new CsvWriter(fileWriter, ',');
		Logger.debug(UtilityMethods.getEnumValues(Constants.CALEVENT_MODULE, Constants.CSV_HEADERS).stream().toArray(String[]::new)+"");
		writer.writeRecord(UtilityMethods.getEnumValues(Constants.CALEVENT_MODULE, Constants.CSV_HEADERS).stream().toArray(String[]::new));
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.REPORT_DATE_FORMAT);
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		for (CalenderEvent cal : events) {
			writer.write(dateFormat.format(cal.getStartTime()));
			writer.write(cal.getName());
			writer.write(cal.getEventDescription());
			writer.write(cal.getCategory());
			writer.write(timeFormat.format(cal.getStartTime()));
			writer.write(timeFormat.format(cal.getEndTime()));
			writer.write(cal.getStatus());
			writer.endRecord();
		}
		writer.close();
		fileWriter.close();
		
	}
	
	@Override
	public Promise<BaseResponse> getEventPromotionReport(String token,
			Map<String, Object> stringParamMap) {
		UserInfoModel userInfo = authorizationService.getUserInfoByToken(token);
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		Map<String, Object> params = reportValidator
				.validateParams(stringParamMap, errorList, userInfo);

		
		if (errorList.isEmpty()) {
			return WS
					.url(UtilityMethods.getConfString(Constants.CT_SUPPORT_URL)
							+ stringParamMap.get("url") + "?"
							+ mapToParams(params))
					.get()
					.map(supportResponse -> {
						String restGuid, fileFormat;
						fileFormat = params.get(Constants.FILE_FORMAT).toString();
						restGuid = params.get(Constants.REST_GUID).toString();
						JsonNode json = supportResponse.asJson();
						File file = createTempFile(Constants.GUEST_LABEL, fileFormat, errorList);
						String outputFile = file.getPath();

						if (json.get("responseStatus").booleanValue()) {
							final JsonNode arrNode = json.get("mapList");
							Logger.debug(Json.stringify(arrNode)+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
							Integer count = Lists.newArrayList(arrNode.elements()).size();
							Logger.debug(count+"...........");
									try {
										if(count>0)
											if(Constants.CSV.equals(fileFormat))
											{
												createPromotionCSV(outputFile, arrNode);
											}else{
												Restaurant restaurant = restaurantDao.findRestaurantByGuid(restGuid);
												createPromotionPdf(outputFile, arrNode, restaurant);
											}
										else{
											errorList.add(conversationValidator.createError(Constants.CONVERSATION, ErrorCodes.CONVERSATIONS_NOT_FOUND));
											return (BaseResponse)(new ErrorResponse(ResponseCodes.CONVERSATION_REPORT_FAILURE, errorList));
										}
										
									} catch (Exception e) {
										// TODO catch
										Logger.debug("--------------------------------------------------------------------------------");
										Logger.error(e.getMessage());
										errorList.add(new ValidationError(Constants.CONVERSATION, e.getMessage(), ErrorCodes.REPORT_CREATION_EXCEPTION));
										return new ErrorResponse(ResponseCodes.CONVERSATION_REPORT_FAILURE, errorList);

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

	private void createPromotionPdf(String outputFile, JsonNode arrNode, Restaurant restaurant) throws MalformedURLException, IOException, DocumentException {
		// TODO Auto-generated method stub

		int counter = 0;
		FileOutputStream out = null;
		Document document = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.REPORT_DATE_FORMAT+" hh:mm a");

		String message, smsCount, dateTime, success, failure, dnd, pending, amtPayble;
		Integer count=0;
		String[] header = UtilityMethods
				.getEnumValues(Constants.PROMOTION, Constants.CSV_HEADERS)
				.stream().toArray(String[]::new);
		int column = header.length;
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
			
		PdfPTable tableMain = new PdfPTable(2);				//Outer Table
		tableMain.setWidthPercentage(102);
			
		PdfPTable table = new PdfPTable(column);
		table.setWidthPercentage(100);
		float[] width = {6f, 1f,2f,1f,1f};
		table.setWidths(width);
		generateTableColumns(table, header, column, BaseColor.WHITE, fontbold, 1);
		table.setHeaderRows(1);
			
		for (final JsonNode objNode : arrNode) {
			message = objNode.get("message").asText();
			smsCount = objNode.get("smsParts").asText();
			dateTime = dateFormat.format(objNode.get("sentDate").asLong());
			count=Integer.parseInt(objNode.get("success").asText())+Integer.parseInt(objNode.get("fail").asText())+Integer.parseInt(objNode.get("pending").asText());
			if (counter % 2 == 0)
				bgColor = CUSTM_GREY;
			else
				bgColor = BaseColor.WHITE;
				
			String[] data = { message,smsCount, dateTime, count.toString() };
			generateTableColumns(table, data, column, bgColor, fontRegular, 0);
			counter++;
		}
		Logger.debug("Data added to table rows");
		fillRestDetail(tableMain, restaurant, Integer.toString(counter)+" Conversations");
			
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
		writer.writeRecord(UtilityMethods
				.getEnumValues(Constants.PROMOTION, Constants.CSV_HEADERS)
				.stream().toArray(String[]::new));
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.REPORT_DATE_FORMAT+" hh:mm a");
		for (final JsonNode objNode : arrNode) {

			writer.write(objNode.get("message").asText());
			writer.write(objNode.get("smsParts").asText());
			writer.write(dateFormat.format(objNode.get("sentDate").asLong()));
			writer.write(objNode.get("success").asText());
			Integer count=Integer.parseInt(objNode.get("success").asText())+Integer.parseInt(objNode.get("fail").asText())+Integer.parseInt(objNode.get("pending").asText());
			writer.write(count.toString());
			writer.endRecord();
		}

		writer.close();
		fileWriter.close();

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
	
	private void fillRestDetail(PdfPTable table, Restaurant restaurant, String count){
		PdfPCell cell = null;
		int l = 6;
		String restName, restLocality, restState, restCity;

		String date = UtilityMethods.getDate(Constants.REPORT_DATE_FORMAT, 0);
		
		com.itextpdf.text.Font fontbold = UtilityMethods.getFont("SansSerif", 7, true);
		fontbold.setColor(new BaseColor(80, 147, 225));
		restName = restaurant.getName();
		restLocality = restaurant.getLocality();
		restState = restaurant.getState();
		restCity = restaurant.getCity();
		
		if(restLocality == null)
		{

			restLocality = restCity+", "+restState;
			l = 4;
		}
		String[] detail = {restName, date, restLocality, count, restCity+", "+restState,""};
		Logger.debug("Restaurant Details : "+restName+" "+ date+" "+restLocality+" "+count+" "+restCity+", "+restState);
		for (int i = 0; i < l; i++) {
			cell = new PdfPCell(new Paragraph(detail[i], fontbold));
			cell.setFixedHeight(20f);
			cell.setBorder(Rectangle.NO_BORDER);
			if (i % 2 == 0) {
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			} else {
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			}
			table.addCell(cell);
		}
	}
	
	private void generateTableColumns(PdfPTable table, String[] data, int size, BaseColor bgColor, com.itextpdf.text.Font font, int type) {
		int i;
		PdfPCell cell = new PdfPCell();
		for (i = 0; i < size; i++) {
			cell = new PdfPCell();
			cell.addElement(new Paragraph(data[i], font));
			cell.setBackgroundColor(bgColor);
			cell.setMinimumHeight(16f);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPaddingBottom(8f);
			cell.setPaddingLeft(5f);
			cell.setPaddingRight(5f);
			if (type == 0){
				cell.setBorder(0);
			}
			else{
				cell.setBorder(Rectangle.BOTTOM|Rectangle.TOP);
				cell.setBorderColor(new BaseColor(89, 89, 89));
			}
			table.addCell(cell);
		}
	}
	
	
	private File createTempFile(String label, String fileFormat, List<ValidationError> errorList){
		File file = null;
		try {
		    file = File.createTempFile("temp", "."+fileFormat);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.debug("e="+e.getMessage());
			e.printStackTrace();
			errorList.add(new ValidationError(label, ErrorCodes.FILE_NOT_CREATED));
		}
		return file;
	}





}
	
