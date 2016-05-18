package com.clicktable.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rating")
public class Rating implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6013233043454674847L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private enum Type {
		AMBIENCE, FOOD_TASTE
	};

	private int star_rate;
	private int review_id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStar_rate() {
		return star_rate;
	}

	public void setStar_rate(int star_rate) {
		this.star_rate = star_rate;
	}

	public int getReview_id() {
		return review_id;
	}

	public void setReview_id(int review_id) {
		this.review_id = review_id;
	}

}
