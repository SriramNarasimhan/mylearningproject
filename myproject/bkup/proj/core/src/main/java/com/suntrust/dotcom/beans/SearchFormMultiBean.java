package com.suntrust.dotcom.beans;

import java.util.List;

/**
 * Bean class to set and get search bean multifield.
 * 
 * @author Cognizant
 * @version 1.0
 * @since 05 September 2017
 * 
 */
public class SearchFormMultiBean {

	private List<SearchBean> items;

	public final List<SearchBean> getItems() {

		return items;

	}

	public final void setItems(List<SearchBean> items) {
		this.items = items;

	}

}
