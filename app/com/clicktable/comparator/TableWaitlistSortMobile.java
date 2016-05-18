package com.clicktable.comparator;

import java.io.Serializable;
import java.util.Comparator;

import com.clicktable.model.TableWaitingTime;
import com.clicktable.model.TableWaitingTimeMobile;

public class TableWaitlistSortMobile implements Comparator<TableWaitingTimeMobile>, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2107836441452918211L;

	@Override
    public int compare(TableWaitingTimeMobile t1, TableWaitingTimeMobile t2) {
        return t1.getWaitTime().compareTo(t2.getWaitTime());
    }
}
