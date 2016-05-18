package com.clicktable.response;

import play.i18n.Messages;

public class CountResponse<T> extends BaseResponse {

	public CountResponse(String responseCode, Integer dnd,Integer count, Integer total, Float acBal, Float estCost) {
		this.setResponseStatus(true);
		this.setResponseCode(responseCode);
		this.setResponseMessage(Messages.get(responseCode));
		this.setDnd(dnd);
		this.setCount(count);
		this.setTotal(total);
		this.setAccountBalance(acBal);
		this.setEstimatedCost(estCost);
	}

	public CountResponse() {
		super();
	}

	private Integer dnd;
	private Integer count;
	private Integer total;
	private Float accountBalance;
	private Float estimatedCost;

	public Integer getDnd() {
		return dnd;
	}

	public void setDnd(Integer dnd) {
		this.dnd = dnd;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Float getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(Float accountBalance) {
		this.accountBalance = accountBalance;
	}

	public Float getEstimatedCost() {
		return estimatedCost;
	}

	public void setEstimatedCost(Float estimatedCost) {
		this.estimatedCost = estimatedCost;
	}

}
