package com.ziya05.ScaleDataImport.Bean;

import com.ziya05.ScaleDataImport.MySqlFor;

@MySqlFor(name = "`Option`")
public class OptionBean {
	
	@MySqlFor(name = "scaleId")
	private int scaleId;
	
	@MySqlFor(name = "questionId")
	private int questionId;
	
	@MySqlFor(name = "optionId")
	private String optionId;
	
	@MySqlFor(name = "content")
	private String content;
	
	@MySqlFor(name = "score")
	private double score;
	
	@MySqlFor(name = "next")
	private int next = 0;
	
	public OptionBean() {}

	public OptionBean(int scaleId, int questionId, String optionId, String content, double score, int next) {
		super();
		this.scaleId = scaleId;
		this.questionId = questionId;
		this.optionId = optionId;
		this.content = content;
		this.score = score;
		this.next = next;
	}

	public int getScaleId() {
		return scaleId;
	}

	public void setScaleId(int scaleId) {
		this.scaleId = scaleId;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public String getOptionId() {
		return optionId;
	}

	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}
	
	
}
