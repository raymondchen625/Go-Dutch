package com.raymondchen.godutch;

import java.util.Date;

public class Expense {
	private long expenseId;
	private long tripId;
	private String name;
	private double amount;
	private String sharedUserIds;
	private Date time;
	public long getExpenseId() {
		return expenseId;
	}
	public void setExpenseId(long expenseId) {
		this.expenseId = expenseId;
	}
	public long getTripId() {
		return tripId;
	}
	public void setTripId(long tripId) {
		this.tripId = tripId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getSharedUserIds() {
		return sharedUserIds;
	}
	public void setSharedUserIds(String sharedUserIds) {
		this.sharedUserIds = sharedUserIds;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
}
