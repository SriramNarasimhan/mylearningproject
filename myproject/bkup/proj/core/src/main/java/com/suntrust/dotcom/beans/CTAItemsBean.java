/**
 * 
 */
package com.suntrust.dotcom.beans;

/**
 * 
 * Purpose - The CTAItemsBean program is a CTA component Dialog multifield pojo class
 * 
 * @author UGRK104
 *
 */
public class CTAItemsBean extends DialogCommonItemsBean {

	private String theme;
	private String allignment;
	
	// Start US19707
	
	private Boolean loDataPersistCheckbox;

	public Boolean getLoDataPersistCheckbox() {
		return loDataPersistCheckbox;
	}

	public void setLoDataPersistCheckbox(Boolean loDataPersistCheckbox) {
		this.loDataPersistCheckbox = loDataPersistCheckbox;
	}
	
	// End US19707

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getAllignment() {
		return allignment;
	}

	public void setAllignment(String allignment) {
		this.allignment = allignment;
	}



}
