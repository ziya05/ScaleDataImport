package com.ziya05.ScaleDataImport.Bean;

import com.ziya05.ScaleDataImport.MySqlFor;

@MySqlFor(name = "Level")
public class LevelBean {
	
	@MySqlFor(name = "scaleId")
	private int scaleId;
	
	@MySqlFor(name = "factorId")
	private int factorId;
	
	private String factorName;
	
	@MySqlFor(name = "description")
	private String description;
	
	@MySqlFor(name = "levelId")
	private int levelId;
	
	@MySqlFor(name = "advice")
	private String advice;
	
	public LevelBean() {}

	public LevelBean(int scaleId, int factorId, String description, int levelId, String advice) {
		super();
		this.scaleId = scaleId;
		this.factorId = factorId;
		this.description = description;
		this.levelId = levelId;
		this.advice = advice;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLevelId() {
		return levelId;
	}

	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getFactorName() {
		return factorName;
	}

	public void setFactorName(String factorName) {
		this.factorName = factorName;
	}
	
	
}
