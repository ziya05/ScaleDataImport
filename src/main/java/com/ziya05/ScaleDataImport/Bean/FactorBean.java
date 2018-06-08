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
	
	public FactorBean() {}

	public FactorBean(int scaleId, String name, String formula) {
		super();
		this.scaleId = scaleId;
		this.name = name;
		this.formula = formula;
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

	public int getFactorId() {
		return factorId;
	}

	public void setFactorId(int factorId) {
		this.factorId = factorId;
	}
	
	
	
}
