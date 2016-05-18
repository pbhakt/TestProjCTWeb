package com.clicktable.service.impl;

import com.clicktable.model.*;
import com.clicktable.response.BaseResponse;
import com.clicktable.service.intf.*;
import com.clicktable.util.AddressConstants;
import com.clicktable.util.Constants;
import org.apache.http.client.ClientProtocolException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@org.springframework.stereotype.Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	CountryService countryService;

	@Autowired
	StateService stateService;

	@Autowired
	CityService cityService;

	@Autowired
	RegionService regionService;

	@Autowired
	LocalityService localityService;

	@Autowired
	BuildingService buildingService;

	@Autowired
	AuthorizationService authService;
	private Logger.ALogger log = Logger.of(AddressServiceImpl.class);

	@Override
	public BaseResponse addAddress(Map<String, Object> params) {
		BaseResponse response = new BaseResponse();
		String filePath="";
		if(!params.containsKey(Constants.FILE_NAME)){			
		response.createResponse( "File path missing!", false);
		return response;
		}else
			filePath = (String) params.get(Constants.FILE_NAME);
		
		InputStream file = null;
		try {
			file = new FileInputStream(filePath);				
			response=addAddress(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			log.warn("Exception in service", e);
		}
		return response;
	}

	@Override
	public BaseResponse addAddress(InputStream file) {
		
		try {
			ArrayList<String> building = new ArrayList<String>();
			ArrayList<String> locality = new ArrayList<String>();
			ArrayList<String> region = new ArrayList<String>();
			ArrayList<String> city = new ArrayList<String>();
			// ArrayList<String> city_zipcode = new ArrayList<String>();
			ArrayList<String> state = new ArrayList<String>();
			ArrayList<String> state_code = new ArrayList<String>();
			ArrayList<String> country = new ArrayList<String>();
			ArrayList<String> country_code = new ArrayList<String>();

			ArrayList<String> locality_building_region = new ArrayList<String>();
			ArrayList<String> region_locality_city = new ArrayList<String>();
			ArrayList<String> city_region_sc = new ArrayList<String>();
			ArrayList<String> state_code_city = new ArrayList<String>();
			ArrayList<String> country_ccode_state_scode = new ArrayList<String>();
			ArrayList<String> country_and_country_code = new ArrayList<String>();
			int COUNTRY_CELL = 0;
			int COUNTRYCODE_CELL = 0;
			int STATE_CELL = 0;
			int STATECODE_CELL = 0;
			int CITY_CELL = 0;
			int REGION_CELL = 0;
			int LOCALITY_CELL = 0;
			int BUILDING_CELL = 0;
			

			ArrayList<Row> invalid_row_list = new ArrayList<Row>();

			// Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			// Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);

			// Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			Row row = sheet.getRow(0);

			
			Iterator<Cell> cellIterator = row.cellIterator();
			for (int j = 0; (cellIterator.hasNext() && j < 12); j++) {
				Cell cell = cellIterator.next();

				if (cell.getCellType() == 1) {
					if ((cell.getStringCellValue())
							.equals(AddressConstants.COUNTRY_NAME)) {
						COUNTRY_CELL = j;
					} else if ((cell.getStringCellValue())
							.equals(AddressConstants.COUNTRYCODE_NAME)) {
						COUNTRYCODE_CELL = j;
					} else if ((cell.getStringCellValue())
							.equals(AddressConstants.STATE_NAME)) {
						STATE_CELL = j;
					} else if ((cell.getStringCellValue())
							.equals(AddressConstants.STATECODE_NAME)) {
						STATECODE_CELL = j;
					} else if ((cell.getStringCellValue())
							.equals(AddressConstants.CITY_NAME)) {
						CITY_CELL = j;
					} else if ((cell.getStringCellValue())
							.equals(AddressConstants.REGION_NAME)) {
						REGION_CELL = j;
					} else if ((cell.getStringCellValue())
							.equals(AddressConstants.LOCALITY_NAME)) {
						LOCALITY_CELL = j;
					} else if ((cell.getStringCellValue())
							.equals(AddressConstants.BUILDING_NAME)) {
						BUILDING_CELL = j;
					}

				} else {
					break;
				}

			}
			
			for (int i = 0; (rowIterator.hasNext()); i++) {
				row = rowIterator.next();
				if (i == 0) {
					row = rowIterator.next();
				}
				Iterator<Cell> cellIterator1 = row.cellIterator();

				for (int j = 0; (cellIterator1.hasNext()); j++) {

					Cell cell = cellIterator1.next();
					if (j == BUILDING_CELL) {						
						building.add(cell.getStringCellValue());						
					} else if (j == LOCALITY_CELL) {					
						locality.add(cell.getStringCellValue());						
					} else if (j == REGION_CELL) {					
						region.add(cell.getStringCellValue());						
					} else if (j == CITY_CELL) {						
						city.add(cell.getStringCellValue());						
					}					
					else if (j == STATE_CELL) {						
						state.add(cell.getStringCellValue());						
					} else if (j == STATECODE_CELL) {						
						state_code.add(cell.getStringCellValue());						
					} else if (j == COUNTRY_CELL) {						
						country.add(cell.getStringCellValue());						
					} else if (j == COUNTRYCODE_CELL) {						
						country_code.add(cell.getStringCellValue());						
					}

				}

			}
			
			for (int i = 0; i < country.size(); i++) {
				country_ccode_state_scode.add(country_code.get(i) + ":"
						+ country.get(i) + "#" + state.get(i) + "*"
						+ state_code.get(i));
				state_code_city.add(state_code.get(i) + ":" + city.get(i)
				/**
				 * + "*" + city_zipcode . get(i )
				 */
				);
				city_region_sc.add(city.get(i) + ":" + region.get(i) + "#"
						+ state_code.get(i));
				region_locality_city.add(region.get(i) + ":" + locality.get(i)
						+ "#" + city.get(i));
				locality_building_region.add(locality.get(i) + ":"
						+ building.get(i) + "#" + region.get(i));
				country_and_country_code.add(country_code.get(i) + ":"
						+ country.get(i));
			}
			country_ccode_state_scode = distinct_data(country_ccode_state_scode);
			country_and_country_code = distinct_data(country_and_country_code);
			state_code_city = distinct_data(state_code_city);
			city_region_sc = distinct_data(city_region_sc);
			region_locality_city = distinct_data(region_locality_city);
			locality_building_region = distinct_data(locality_building_region);


			HashMap<String, ArrayList<String>> l_b_a = get_Key_Value(locality,
					locality_building_region);
			HashMap<String, ArrayList<String>> a_l_c = get_Key_Value(region,
					region_locality_city);
			HashMap<String, ArrayList<String>> c_a_sc = get_Key_Value(city,
					city_region_sc);
			HashMap<String, ArrayList<String>> s_c = get_Key_Value(state_code,
					state_code_city);
			HashMap<String, ArrayList<String>> c_s_cc = get_Key_Value(
					country_code, country_ccode_state_scode);
			HashMap<String, ArrayList<String>> cou_cc = get_Key_Value(
					country_code, country_and_country_code);

			// Add countries
			add(AddressConstants.COUNTRY, cou_cc);
			// Add states
			add(AddressConstants.STATE, c_s_cc);

			// Add cities
			add(AddressConstants.CITY, s_c);

			// Add regions
			add(AddressConstants.REGION, c_a_sc);

			// Add localities
			add(AddressConstants.LOCALITY, a_l_c);

			// Add buildings
			add(AddressConstants.BUILDING, l_b_a);

			file.close();

		} catch (FileNotFoundException e) {
			log.warn("Exception in service", e);
		} catch (IOException e) {
			log.warn("Exception in service", e);
		}
		
		BaseResponse response = new BaseResponse();
		//return new PostR
		response.createResponse("Address master data successfully added!", true);
		return response;

	}

	private static HashMap<String, ArrayList<String>> get_Key_Value(
			ArrayList<String> key_list, ArrayList<String> keyvaluelist) {
		HashMap<String, ArrayList<String>> _map = new HashMap<String, ArrayList<String>>();
		for (int i = 0; i < key_list.size(); i++) {
			String key = key_list.get(i);
			ArrayList<String> values = new ArrayList<String>();
			for (int j = 0; j < keyvaluelist.size(); j++) {
				String single_key_value = keyvaluelist.get(j);
				if (single_key_value
						.substring(0, single_key_value.indexOf(":"))
						.equals(key)) {
					values.add(single_key_value.substring(single_key_value
							.indexOf(":") + 1));
				}
			}
			_map.put(key, values);

		}
		return _map;
	}

	private static ArrayList<String> distinct_data(ArrayList<String> data) {
		Set<String> hs = new HashSet<>();
		hs.addAll(data);
		data.clear();
		data.addAll(hs);
		return data;
	}

	private boolean add(String api, HashMap<String, ArrayList<String>> map) throws ClientProtocolException,
			IOException {

		try{
		JSONObject jsonobj;// = new JSONObject();
		Set<String> sets = map.keySet();
		Iterator<String> itr = sets.iterator();
		
		if (api.equals(AddressConstants.COUNTRY)) {
			while (itr.hasNext()) {
				String country_code = itr.next();
				ArrayList<String> countries = map.get(country_code);
				ArrayList<Country> country_list = new ArrayList<Country>();
				for (int i = 0; i < countries.size(); i++) {
					jsonobj = new JSONObject();
					String country_name = countries.get(i);
					if (!country_code.trim().equals("")
							&& !country_name.trim().equals("")) {
						Country country = new Country();
						country.setName(country_name);
						country.setCountryCode(country_code);
						country_list.add(country);
					}
				}
				BaseResponse res = countryService.addCountry(country_list, null);
			}

		} else if (api.equals(AddressConstants.STATE)) {
			while (itr.hasNext()) {
				String country_code = itr.next();
				ArrayList<String> country_ccode_state_scode = map
						.get(country_code);

				for (int i = 0; i < country_ccode_state_scode.size(); i++) {
					String single_country_ccode_state_scode = country_ccode_state_scode
							.get(i);

					String statename = single_country_ccode_state_scode
							.substring(single_country_ccode_state_scode
									.indexOf("#") + 1,
									single_country_ccode_state_scode
											.indexOf("*"));
					String statecode = single_country_ccode_state_scode
							.substring(single_country_ccode_state_scode
									.indexOf("*") + 1);

					if (!country_code.trim().equals("")
							&& !statename.trim().equals("")
							&& !statecode.trim().equals("")) {

						State state = new State();
						state.setName(statename);
						state.setStateCode(statecode);
						state.setCountryCode(country_code);

						stateService.addState(state, null);
					}

				}
			}

		} else if (api.equals(AddressConstants.CITY)) {
			while (itr.hasNext()) {
				String state_code = itr.next();
				ArrayList<String> city = map.get(state_code);

				for (int i = 0; i < city.size(); i++) {
					String city_name = city.get(i);
					if (!city_name.trim().equals("")
							&& !state_code.trim().equals("")) {

						City cityObj = new City();
						cityObj.setName(city_name);
						cityObj.setStateCode(state_code);

						cityService.addCity(cityObj, null);
					}

				}
			}

		} else if (api.equals(AddressConstants.REGION)) {
			while (itr.hasNext()) {
				String cityName = itr.next();
				ArrayList<String> city_region = map.get(cityName);

				for (int i = 0; i < city_region.size(); i++) {
					String region_country_code = city_region.get(i);
					String region_name = region_country_code.substring(0,
							region_country_code.indexOf("#"));
					String state_code = region_country_code
							.substring(region_country_code.indexOf("#") + 1);

					if (!region_name.trim().equals("")
							&& !cityName.trim().equals("")
							&& !state_code.trim().equals("")) {

						Region region = new Region();
						region.setName(region_name);
						region.setCityName(cityName);
						region.setStateCode(state_code);

						regionService.addRegion(region, null);
					}

				}
			}

		} else if (api.equals(AddressConstants.LOCALITY)) {
			while (itr.hasNext()) {
				String region_name = itr.next();
				ArrayList<String> regionNames = map.get(region_name);
				for (int i = 0; i < regionNames.size(); i++) {
					String locality_city = regionNames.get(i);
					String locality_name = locality_city.substring(0,
							locality_city.indexOf("#"));
					String city_name = locality_city.substring(locality_city
							.indexOf("#") + 1);

					if (!region_name.trim().equals("")
							&& !locality_name.trim().equals("")
							&& !city_name.trim().equals("")) {

						Locality locality = new Locality();
						locality.setName(locality_name);
						locality.setRegionName(region_name);
						locality.setCityName(city_name);

						localityService.addLocality(locality, null);
					}

				}
			}

		} else if (api.equals(AddressConstants.BUILDING)) {
			while (itr.hasNext()) {
				String locality_name = itr.next();
				ArrayList<String> loc_building_region = map.get(locality_name);

				for (int i = 0; i < loc_building_region.size(); i++) {
					String single_building_region = loc_building_region.get(i);
					String building_name = single_building_region.substring(0,
							single_building_region.indexOf("#"));
					String region_name = single_building_region
							.substring(single_building_region.indexOf("#") + 1);

					if (!region_name.trim().equals("")
							&& !locality_name.trim().equals("")
							&& !building_name.trim().equals("")) {

						Building building = new Building();
						building.setName(building_name);
						building.setLocalityName(locality_name);
						building.setRegionName(region_name);

						buildingService.addBuilding(building, null);
					}

				}
			}

		}
		return true;

	}
	 catch(Exception e){
		 log.warn("Exception in service", e);
		return true;
	}
	}
}
