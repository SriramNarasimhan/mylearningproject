package com.suntrust.dotcom.beans;

import java.util.List;

/**
 * 
 * Purpose - The DialogMultiBean program is a generic pojo class for any dialog
 * which has certain set of items i.e. widgets in a multifield.
 * 
 * @author UGRK104
 *
 */
public class DialogMultiBean<T> {

	private List<T> items;

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {

		this.items = items;

	}

}
