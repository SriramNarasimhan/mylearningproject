package com.suntrust.dotcom.beans;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DynamicListItemsBean {
	private String title;
	private String description;
	private String linkURL;
	private String target;
	private String icon;
	private String urlParams;
	private String anchorTag;
	private Date replicationDate;
	private String thumbnailPath;
	private String titleTags;
	private String altTags;
	private String pageDescription;
	private String pageTitle;
	private List<String> tags;
	private String thumbnailIconClassName;
	private String authorTags;
	private String authorLogic;
	private String authorResultLimit;
	private String authorSortByValue;
	private String valueType;
	
	public DynamicListItemsBean() {
		super();
	}
	public DynamicListItemsBean(String title, String linkURL, String target, String icon, String urlParams,
			String anchorTag, Date replicationDate) {
		super();
		this.title = title;
		this.linkURL = linkURL;
		this.target = target;
		this.icon = icon;
		this.urlParams = urlParams;
		this.anchorTag = anchorTag;
		this.replicationDate = replicationDate;
	}
	
	public String getThumbnailIconClassName() {
		return thumbnailIconClassName;
	}

	public void setThumbnailIconClassName(String thumbnailIconClassName) {
		this.thumbnailIconClassName = thumbnailIconClassName;
	}

	public List<String> getTags() {

		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	

	

	
	@JsonProperty("title")
	public String getTitle() {
		return this.title;
	}
	@JsonProperty("jcr:title")
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLinkURL() {
		return this.linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUrlParams() {
		return this.urlParams;
	}

	public void setUrlParams(String urlParams) {
		this.urlParams = urlParams;
	}

	public String getAnchorTag() {
		return this.anchorTag;
	}

	public void setAnchorTag(String anchorTag) {
		this.anchorTag = anchorTag;
	}

	public Date getReplicationDate() {
		return this.replicationDate;
	}

	public void setReplicationDate(Date replicationDate) {
		this.replicationDate = replicationDate;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	@JsonProperty("titleTags")
	public String getTitleTags() {
		return titleTags;
	}

	@JsonProperty("titleTag")
	public void setTitleTags(String titleTags) {
		this.titleTags = titleTags;
	}
	@JsonProperty("altTags")
	public String getAltTags() {
		return altTags;
	}
	@JsonProperty("altTag")
	public void setAltTags(String altTags) {
		this.altTags = altTags;
	}

	public String getPageDescription() {
		return pageDescription;
	}

	public void setPageDescription(String pageDescription) {
		this.pageDescription = pageDescription;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	@JsonProperty("authorTags")
	public String getAuthorTags() {
		return authorTags;
	}
	@JsonProperty("tags")
	public void setAuthorTags(String authorTags) {
		this.authorTags = authorTags;
	}
	@JsonProperty("authorLogic")
	public String getAuthorLogic() {
		return authorLogic;
	}
	@JsonProperty("logic")
	public void setAuthorLogic(String authorLogic) {
		this.authorLogic = authorLogic;
	}

	public String getAuthorResultLimit() {
		return authorResultLimit;
	}

	public void setAuthorResultLimit(String authorResultLimit) {
		this.authorResultLimit = authorResultLimit;
	}

	public String getAuthorSortByValue() {
		return authorSortByValue;
	}

	public void setAuthorSortByValue(String authorSortByValue) {
		this.authorSortByValue = authorSortByValue;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

}
