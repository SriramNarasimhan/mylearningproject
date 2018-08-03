package com.suntrust.dotcom.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class WhatToExpectNextStepsBean {
	
	private String textbody;
	private String icon;
	private String alttext;
	private String stepurl;
	private String urlparam;
	private String anchortag;
	private String target;
	@JsonIgnore
	private String modifiedURL;
	
	public String getTextbody() {
		return textbody;
	}
	public void setTextbody(String textbody) {
		this.textbody = textbody;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getAlttext() {
		return alttext;
	}
	public void setAlttext(String alttext) {
		this.alttext = alttext;
	}
	public String getStepurl() {
		return stepurl;
	}
	public void setStepurl(String stepurl) {
		this.stepurl = stepurl;
	}
	public String getUrlparam() {
		return urlparam;
	}
	public void setUrlparam(String urlparam) {
		this.urlparam = urlparam;
	}
	public String getAnchortag() {
		return anchortag;
	}
	public void setAnchortag(String anchortag) {
		this.anchortag = anchortag;
	}
	public String getModifiedURL() {
		return modifiedURL;
	}
	public void setModifiedURL(String modifiedURL) {
		this.modifiedURL = modifiedURL;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	
	

}
