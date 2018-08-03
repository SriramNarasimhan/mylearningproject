/**
 * 
 */
package com.suntrust.dotcom.beans;

/**
 * The SearchBean class is used to return the tag objects for Search results.
 * 
 * @author Cognizant
 * @version 1.0
 * @since 05 September 2017
 * 
 */
public class SearchBean {

	private String tagName;
	private String tagTitle;
	private String tagId;
	private String hiddenFieldName;
	private String hiddenFieldValue;

	public final String getTagName() {
		return tagName;
	}

	public final void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public final String getTagTitle() {
		return tagTitle;
	}

	public final void setTagTitle(String tagTitle) {
		this.tagTitle = tagTitle;
	}

	public final String getTagId() {
		return tagId;
	}

	public final void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public final String getHiddenFieldName() {
		return hiddenFieldName;
	}

	public final void setHiddenFieldName(String hiddenFieldName) {
		this.hiddenFieldName = hiddenFieldName;
	}

	public final String getHiddenFieldValue() {
		return hiddenFieldValue;
	}

	public final void setHiddenFieldValue(String hiddenFieldValue) {
		this.hiddenFieldValue = hiddenFieldValue;
	}

}
