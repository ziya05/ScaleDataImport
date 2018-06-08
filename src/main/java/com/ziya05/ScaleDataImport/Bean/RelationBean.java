package com.ziya05.ScaleDataImport.Bean;

import com.ziya05.ScaleDataImport.MySqlFor;

@MySqlFor(name = "Relation")
public class RelationBean {
	
	@MySqlFor(name = "scaleId")
	private int scaleId;
	
	@MySqlFor(name = "factorId")
	private int factorId;
	
	private String factorName;
	
	@MySqlFor(name = "groupId")
	private int groupId;
	
	private String groupName;
	
	@MySqlFor(name = "points")
	private String points;
	
	public RelationBean() {}

	public RelationBean(int scaleId, int factorId, int groupId, String points) {
		super();
		this.scaleId = scaleId;
		this.factorId = factorId;
		this.groupId = groupId;
		this.points = points;
	}

	public int getScaleId() {
		return scaleId;
	}

	public void setScaleId(int scaleId) {
		this.scaleId = scaleId;
	}

	public int getFactorId() {
		return factorId;
	}

	public void setFactorId(int factorId) {
		this.factorId = factorId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public String getFactorName() {
		return factorName;
	}

	public void setFactorName(String factorName) {
		this.factorName = factorName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
}
