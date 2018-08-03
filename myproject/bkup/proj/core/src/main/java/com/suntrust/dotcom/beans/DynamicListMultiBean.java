package com.suntrust.dotcom.beans;

import java.util.List;

public class DynamicListMultiBean
{
  private List<DynamicListItemsBean> items;

public List<DynamicListItemsBean> getItems() {
	return items;
}

public void setItems(List<DynamicListItemsBean> items) {
	this.items = items;
}
}
