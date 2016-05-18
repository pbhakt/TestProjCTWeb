package com.clicktable.dao.intf;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.clicktable.model.CustomResvOpHr;
import com.clicktable.model.GuestConversation;
import com.clicktable.model.GuestProfile;
import com.clicktable.model.Reservation;
import com.clicktable.model.ReservationForTables;
import com.clicktable.model.Table;

@org.springframework.stereotype.Service
public interface ReservationDao extends GenericDao<Reservation> {

	Reservation addReservation(Reservation reservation, GuestProfile guest, List<Table> tableList);

	//String updateReservation(Reservation reservation, Reservation existing, List<Table> tableList, Map<String, Object> objectAsMap);

	Map<String, Object> getReservationsForGuest(Map<String, Object> params);

	List<String> getServersForTables(List<String> tableGuids);

	Map<GuestConversation, Reservation> getAllGuestReservation();

	// List<Reservation> getGuestUpcomingReservation(String guestGuid);

	/*
	 * When Status ='No_SHOW || CANCELLED || FINISHED " : isTableRelationRequire
	 * must be true else false
	 */
	Reservation updateReservationViaSchedular(Reservation reservation);

	Map<String, Object> getReservationStatusCounts(Map<String, Object> params);

	Map<String, Object> getReservationModeCounts(Map<String, Object> params);

	Map<String, Object> getReservationGuestCounts(Map<String, Object> params);

	Map<String, Object> getReservationCoversCounts(Map<String, Object> params);

	void updateReservationForTableShuffle(Reservation resv, String tableGuid);

	List<Reservation> getQueueReservation(Map<String, Object> params);

	List<ReservationForTables> getReservationsForTables(Map<String, Object> params);

	Boolean updateReservationMode(Reservation resv);

	Map<String, Object> validateRestGuestTable(Map<String, Object> params);

	Reservation getReservationWithRespectToGuid(Map<String, Object> params);

	List<Reservation> findByCustomeFields(Class<Reservation> class1, Map<String, Object> qryParamMap);

	void updateReservationWithShifEndCypherViaSchedular();

	Map<String, Object> getWaitlistStatusCounts(Map<String, Object> params);

	List<Reservation> getGuestUpcomingReservation(String guid, String msisdn);

	Map<Reservation, GuestProfile> getReservationDetailsOnDate(Map<String, Object> params);

	void addReviewToGuest(Map<String, Object> param);

	
	void updateReservationForShuffle(List<Reservation> reservation);

	void updateWalkinsForShuffle(List<Reservation> walkinList);

	void deleteWaitlistData(String resvGuid);

	String updateReservation(Reservation reservation);

	
	List<Reservation> getReservationsForTime(Map<String, Object> paramMap);

	List<CustomResvOpHr> getReservationDetailsByGuid(Map<String, Object> params);


	TreeMap<Reservation, GuestProfile> getReservationDetails(
			Map<String, Object> params);

	List<Map<String, Object>> getReservationsReportData(Map<String, Object> params);

}
