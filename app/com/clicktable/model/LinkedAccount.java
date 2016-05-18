package com.clicktable.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LinkedAccount implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3329345003142846363L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) 
	public Long id;	
	
	public LinkedAccount() {
		// TODO Auto-generated constructor stub
	}
	
	//public LinkedAccount(UserProfile u){
	//	super(u);
	//}

}
