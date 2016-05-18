package com.clicktable.comparator;

import java.io.Serializable;
import java.util.Comparator;

import com.clicktable.model.Reservation;

public class ReservationSort implements Comparator<Reservation>, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6333218257046068197L;

	@Override
    public int compare(Reservation r1, Reservation r2) {
        return r1.getEstStartTime().compareTo(r2.getEstStartTime());
    }
}
