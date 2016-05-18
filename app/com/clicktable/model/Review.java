package com.clicktable.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "review")
public class Review implements Serializable {

	private static final long serialVersionUID = 4987966595773324603L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private int reviewed_id;
	private int review_for;
	private String subject;
	private String comment;

	private enum Visit_Type {
		SOLO, FAMILY, KIDS
	}

	private Date visit_date;
	private String reply;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getReviewed_id() {
		return reviewed_id;
	}

	public void setReviewed_id(int reviewed_id) {
		this.reviewed_id = reviewed_id;
	}

	public int getReview_for() {
		return review_for;
	}

	public void setReview_for(int review_for) {
		this.review_for = review_for;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getVisit_date() {
		return visit_date == null ? null : (Date) visit_date.clone();
	}

	public void setVisit_date(Date visit_date) {
		this.visit_date = visit_date == null ? null : (Date) visit_date.clone();
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}
}
