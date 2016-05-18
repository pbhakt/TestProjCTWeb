package com.clicktable.comparator;

import java.io.Serializable;
import java.util.Comparator;

import com.clicktable.model.TableWaitingTime;

public class TableWaitlistSort implements Comparator<TableWaitingTime>, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2107836441452918211L;

	@Override
    public int compare(TableWaitingTime t1, TableWaitingTime t2) {
        return t1.getWaitTime().compareTo(t2.getWaitTime());
    }
}
