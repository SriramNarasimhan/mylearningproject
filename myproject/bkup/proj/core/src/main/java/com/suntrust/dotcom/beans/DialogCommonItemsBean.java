package com.suntrust.dotcom.beans;

/**
 * Purpose - The DialogCommonItemsBean program is a common pojo class for any
 * dialog which has certain set of common items i.e. widgets.
 * 
 * @author UGRK104
 *
 */
public class DialogCommonItemsBean {

	private String title;
	private String linkURL;
	private String target;
	private String icon;
	private String urlParams;
	private String anchorTag;
	private String anchorTitle;
	
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

	public String getAnchorTitle() {
		return anchorTitle;
	}

	public void setAnchorTitle(String anchorTitle) {
		this.anchorTitle = anchorTitle;
	}

}
