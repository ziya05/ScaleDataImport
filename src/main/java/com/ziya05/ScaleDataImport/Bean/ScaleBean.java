package com.ziya05.ScaleDataImport.Bean;

import com.ziya05.ScaleDataImport.MySqlFor;

@MySqlFor(name = "Scale")
public class ScaleBean {
	
	@MySqlFor(name = "name")
	private String scaleName;
	
	@MySqlFor(name = "scaleNumber")
	private String scaleNumber;
	
	@MySqlFor(name = "description")
	private String description;
	
	public ScaleBean() {
		
	}

	public ScaleBean(String scaleName, String description) {
		super();
		this.scaleName = scaleName;
		this.description = description;
	}

	public String getScaleName() {
		return scaleName;
	}

	public void setScaleName(String scaleName) {
		this.scaleName = scaleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getScaleNumber() {
		return scaleNumber;
	}

	public void setScaleNumber(String scaleNumber) {
		this.scaleNumber = scaleNumber;
	}

	
}
