package com.ziya05.ScaleDataImport.Bean;

import com.ziya05.ScaleDataImport.MySqlFor;

@MySqlFor(name = "Factor")
public class FactorBean {
	
	@MySqlFor(name = "scaleId")
	private int scaleId;
	
	@MySqlFor(name = "factorId", iskey = true)
	private int factorId;
	
	@MySqlFor(name = "name", isvalue = true)
	private String name;
	
	@MySqlFor(name = "formula")
	private String formula;
	
	@MySqlFor(name = "levelCount")
	private int levelCount;
	
	@MySqlFor(name = "inChart")
	private Boolean inChart;
	
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

	public int getFactorId() {
		return factorId;
	}

	public void setFactorId(int factorId) {
		this.factorId = factorId;
	}

	public int getLevelCount() {
		return levelCount;
	}

	public void setLevelCount(int levelCount) {
		this.levelCount = levelCount;
	}

	public Boolean getInChart() {
		return inChart;
	}

	public void setInChart(Boolean inChart) {
		this.inChart = inChart;
	}
	
	
	
}
