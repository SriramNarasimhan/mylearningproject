package com.suntrust.dotcom.beans;

/**
 * ContactUsBean class is used define the bean objects for the Contact Us component 
 *  
 * @author Cognizant
 * @version 1.0
 * @since 26 May 2017
 *
 */

public class ContactUsBean {

	private String icon;
	private String iconAltText;	
	private String phoneNumber;
	private String miniDesc;	
	private String subDesc;
	private String label;	
	private String title;
	private String linkURL;
	private String target;	
	private String urlParams;
	private String anchorTag;
	private String titleTag;	
	private Boolean loDataPersistCheckbox;		
	
	//start US19707
	/*private Boolean firstPhoneNumber;

	public Boolean getFirstPhoneNumber() {
		return firstPhoneNumber;
	}

	public void setFirstPhoneNumber(Boolean firstPhoneNumber) {
		this.firstPhoneNumber = firstPhoneNumber;
	}*/
	
	//end US19707
	
	public Boolean getLoDataPersistCheckbox() { 
		return loDataPersistCheckbox;
	}

	public void setLoDataPersistCheckbox(Boolean loDataPersistCheckbox) {
		this.loDataPersistCheckbox = loDataPersistCheckbox;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getMiniDesc() {
		return miniDesc;
	}

	public void setMiniDesc(String miniDesc) {
		this.miniDesc = miniDesc;
	}	
	public String getSubDesc() {
		return subDesc;
	}

	public void setSubDesc(String subDesc) {
		this.subDesc = subDesc;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getIconAltText() {
		return iconAltText;
	}

	public void setIconAltText(String iconAltText) {
		this.iconAltText = iconAltText;
	}
	public String getUrlParams() {
		return urlParams;
	}

	public void setUrlParams(String urlParams) {
		this.urlParams = urlParams;
	}

	public String getAnchorTag() {
		return anchorTag;
	}

	public void setAnchorTag(String anchorTag) {
		this.anchorTag = anchorTag;
	}
	public String getTitleTag() {
		return titleTag;
	}
	public void setTitleTag(String titleTag) {
		this.titleTag = titleTag;
	}
	
}

