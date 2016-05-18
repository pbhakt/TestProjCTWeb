package com.clicktable.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "address")
public class Address implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4672989592891731996L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) 
	private int id;	
	private String address;
	private String first_row;
	private String second_row;
	private int pincode;
	private String city;
	private String area;
	private String state;
	private enum Type{
		OFFICE,RESIDENCE
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address!=null?address.trim():null;	    
	}
	public String getFirst_row() {
		return first_row;
	}
	public void setFirst_row(String first_row) {		
		this.first_row = first_row!=null?first_row.trim():null;
	}
	public String getSecond_row() {
		return second_row;
	}
	public void setSecond_row(String second_row) {
		this.second_row = second_row!=null?second_row.trim():null;
	}
	public int getPincode() {
		return pincode;
	}
	public void setPincode(int pincode) {
		this.pincode = pincode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city!=null?city.trim():null;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area!=null?area.trim():null;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {	   
		this.state = state!=null?state.trim():null;
	};
	
	
	
	/*
	private DateTime created_on;
	private DateTime updated_on;
	private String created_by;
	private String updated_by;
	private String country_code;*/
	
	
	
	
}
