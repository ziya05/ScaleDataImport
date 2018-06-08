package com.ziya05.ScaleDataImport.Bean;

import java.util.List;

import com.ziya05.ScaleDataImport.MySqlFor;

@MySqlFor(name="Question")
public class QuestionBean {
	
	@MySqlFor(name="scaleId")
	private int scaleId;
	
	@MySqlFor(name="questionId")
	private int questionId;
	
	@MySqlFor(name="title")
	private String title;
	
	@MySqlFor(name = "questionType")
	private int questionType;
	
	private List<OptionBean> optionItems;
	
	public QuestionBean() {
		 
	}

	public QuestionBean(int scaleId, int questionId, String title, int questionType) {
		super();
		this.scaleId = scaleId;
		this.questionId = questionId;
		this.title = title;
		this.questionType = questionType;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<OptionBean> getOptionItems() {
		return optionItems;
	}

	public void setOptionItems(List<OptionBean> optionItems) {
		this.optionItems = optionItems;
	}

	public int getQuestionType() {
		return questionType;
	}

	public void setQuestionType(int questionType) {
		this.questionType = questionType;
	}
	
	
	
}
