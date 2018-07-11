package com.ziya05.ScaleDataImport.Bean;

import com.ziya05.ScaleDataImport.MySqlFor;

@MySqlFor(name = "GlobalJump")
public class GlobalJumpBean {
	
	@MySqlFor(name = "scaleId")
	private int scaleId;
	
	@MySqlFor(name = "name")
	private String name;
	
	@MySqlFor(name = "begin")
	private int begin;
	
	@MySqlFor(name = "end")
	private int end;
	
	@MySqlFor(name = "continuous")
	private int continuous;
	
	@MySqlFor(name = "questionCount")
	private int questionCount;
	
	@MySqlFor(name = "score")
	private double score;
	
	@MySqlFor(name = "jumpNo")
	private int jumpNo;

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

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getContinuous() {
		return continuous;
	}

	public void setContinuous(int continuous) {
		this.continuous = continuous;
	}

	public int getQuestionCount() {
		return questionCount;
	}

	public void setQuestionCount(int questionCount) {
		this.questionCount = questionCount;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int getJumpNo() {
		return jumpNo;
	}

	public void setJumpNo(int jumpNo) {
		this.jumpNo = jumpNo;
	}
	
	
}
