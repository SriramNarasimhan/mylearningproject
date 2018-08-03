package com.suntrust.dotcom.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DecisionTreeCTABean {
	
	private String ctatext;
	private String ctaicon;
	private String ctaurl;
	private String ctaurlparam;
	private String ctatag;
	private String ctatarget;
	private String ctahelptext;
	private String helptextposition;
	@JsonIgnore
	private String relativeCtaURL;
	@JsonIgnore
	private String ctaAjaxUrl;
	@JsonIgnore
	private String ctaPageAjaxUrl;
	
	
	
	
	public String getCtatext() {
		return ctatext;
	}
	public void setCtatext(String ctatext) {
		this.ctatext = ctatext;
	}
	public String getCtaicon() {
		return ctaicon;
	}
	public void setCtaicon(String ctaicon) {
		this.ctaicon = ctaicon;
	}
	public String getCtaurl() {
		return ctaurl;
	}
	public void setCtaurl(String ctaurl) {
		this.ctaurl = ctaurl;
	}
	public String getCtaurlparam() {
		return ctaurlparam;
	}
	public void setCtaurlparam(String ctaurlparam) {
		this.ctaurlparam = ctaurlparam;
	}
	public String getCtatag() {
		return ctatag;
	}
	public void setCtatag(String ctatag) {
		this.ctatag = ctatag;
	}
	public String getCtatarget() {
		return ctatarget;
	}
	public void setCtatarget(String ctatarget) {
		this.ctatarget = ctatarget;
	}
	public String getCtahelptext() {
		return ctahelptext;
	}
	public void setCtahelptext(String ctahelptext) {
		this.ctahelptext = ctahelptext;
	}
	public String getHelptextposition() {
		return helptextposition;
	}
	public void setHelptextposition(String helptextposition) {
		this.helptextposition = helptextposition;
	}
	public String getRelativeCtaURL() {
		return relativeCtaURL;
	}
	public void setRelativeCtaURL(String relativeCtaURL) {
		this.relativeCtaURL = relativeCtaURL;
	}
	public String getCtaAjaxUrl() {
		return ctaAjaxUrl;
	}
	public void setCtaAjaxUrl(String ctaAjaxUrl) {
		this.ctaAjaxUrl = ctaAjaxUrl;
	}
	public String getCtaPageAjaxUrl() {
		return ctaPageAjaxUrl;
	}
	public void setCtaPageAjaxUrl(String ctaPageAjaxUrl) {
		this.ctaPageAjaxUrl = ctaPageAjaxUrl;
	}

	
}
