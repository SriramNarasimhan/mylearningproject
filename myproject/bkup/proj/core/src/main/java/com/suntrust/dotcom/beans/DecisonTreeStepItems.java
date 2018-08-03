package com.suntrust.dotcom.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DecisonTreeStepItems {
	
	private String steptitle;
	private List<DecisionTreeSlideBean> slideItems;
	@JsonIgnore
	private String stepSelected;
	@JsonIgnore
	private String firstSlideURL;
	@JsonIgnore
	private String firstSlideAjaxURL;
	@JsonIgnore
	private String firstSlidePageAjaxURL;
	@JsonIgnore
	private String firstSlideresourceURL;
	
	public String getSteptitle() {
		return steptitle;
	}
	public void setSteptitle(String steptitle) {
		this.steptitle = steptitle;
	}
	public List<DecisionTreeSlideBean> getSlideItems() {
		return slideItems;
	}
	public void setSlideItems(List<DecisionTreeSlideBean> slideItems) {
		this.slideItems = slideItems;
	}
	
	public String getStepSelected() {
		return stepSelected;
	}
	public void setStepSelected(String stepSelected) {
		this.stepSelected = stepSelected;
	}
	public String getFirstSlideURL() {
		return firstSlideURL;
	}
	public void setFirstSlideURL(String firstSlide) {
		this.firstSlideURL = firstSlide;
	}
	public String getFirstSlideAjaxURL() {
		return firstSlideAjaxURL;
	}
	public void setFirstSlideAjaxURL(String firstSlideAjaxURL) {
		this.firstSlideAjaxURL = firstSlideAjaxURL;
	}
	public String getFirstSlideresourceURL() {
		return firstSlideresourceURL;
	}
	public void setFirstSlideresourceURL(String firstSlideresourceURL) {
		this.firstSlideresourceURL = firstSlideresourceURL;
	}
	public String getFirstSlidePageAjaxURL() {
		return firstSlidePageAjaxURL;
	}
	public void setFirstSlidePageAjaxURL(String firstSlidePageAjaxURL) {
		this.firstSlidePageAjaxURL = firstSlidePageAjaxURL;
	}

	
	
}
