package com.raymondchen.godutch;

import java.util.Date;
import java.util.List;

public class Trip {
	private long tripId;
	public long getTripId() {
		return tripId;
	}
	public void setTripId(long tripId) {
		this.tripId = tripId;
	}
	private String name;
	private Date time;
	private Integer totalAmount;
	private List<User> members;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Integer getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}
	public List<User> getMembers() {
		return members;
	}
	public void setMembers(List<User> members) {
		this.members = members;
	}
	public String getMemberIds() {
		if (members==null || members.size()==0) {
			return "";
		}
		String idListStr="";
		for (int i=0;i<members.size();i++) {
			idListStr+=members.get(i).getUserId();
			if (i!=members.size()-1) {
				idListStr+=",";
			}
		}
		return idListStr;
	}
}
