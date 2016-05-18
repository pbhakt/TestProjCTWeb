package com.clicktable.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.DateTime;

@Entity
@Table(name = "reserved_table_inventry")
public class ReservedTableInventry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6110288877616917258L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private DateTime slot_start_time;
	private DateTime slot_end_time;
	private int table_id;

	private enum Status {
		PARTIAL, ALLOCATED, BLOCKED
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DateTime getSlot_start_time() {
		return slot_start_time;
	}

	public void setSlot_start_time(DateTime slot_start_time) {
		this.slot_start_time = slot_start_time;
	}

	public DateTime getSlot_end_time() {
		return slot_end_time;
	}

	public void setSlot_end_time(DateTime slot_end_time) {
		this.slot_end_time = slot_end_time;
	}

	public int getTable_id() {
		return table_id;
	}

	public void setTable_id(int table_id) {
		this.table_id = table_id;
	};
}
