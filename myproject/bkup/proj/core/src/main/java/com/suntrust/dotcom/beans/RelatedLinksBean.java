/**
 * 
 */
package com.suntrust.dotcom.beans;

/**
 * 
 * Purpose - The RelatedLinksBean program is a Related Links component Dialog multifield pojo class
 * 
 * @author UGRR162
 *
 */
public class RelatedLinksBean extends DialogCommonItemsBean {

	private String className;
	private String lo_phone;
	private String lo_id;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getLo_phone() {
		return lo_phone;
	}

	public void setLo_phone(String lo_phone) {
		this.lo_phone = lo_phone;
	}

	public String getLo_id() {
		return lo_id;
	}

	public void setLo_id(String lo_id) {
		this.lo_id = lo_id;
	}
}
