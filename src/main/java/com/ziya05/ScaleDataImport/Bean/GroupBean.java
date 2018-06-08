package com.ziya05.ScaleDataImport.Bean;

import com.ziya05.ScaleDataImport.MySqlFor;

@MySqlFor(name = "`Group`")
public class GroupBean {
	
	@MySqlFor(name = "groupId", iskey = true)
	private int groupId;
	
	@MySqlFor(name = "scaleId")
	private int scaleId;
	
	@MySqlFor(name = "name", isvalue = true)
	private String name;
	
	@MySqlFor(name = "formula")
	private String formula;
	
	public GroupBean() {}

	public GroupBean(int groupId, int scaleId, String name, String formula) {
		super();
		this.groupId = groupId;
		this.scaleId = scaleId;
		this.name = name;
		this.formula = formula;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getScaleId() {
		return scaleId;
	}

	public void setScaleId(int scaleId) {
		this.scaleId = scaleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	
}
