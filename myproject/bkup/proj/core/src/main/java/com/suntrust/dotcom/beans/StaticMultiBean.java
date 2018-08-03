package com.suntrust.dotcom.beans;

import java.util.List;

public class StaticMultiBean {
	private String dashboard;
	private List<StaticItemsBean> items;
	
	public List<StaticItemsBean> getItems() {
		return items;
	}
	public void setItems(List<StaticItemsBean> items) {
		this.items = items;
	}
	
	public String getDashboard() {
		return dashboard;
	}
	public void setDashboard(String dashboard) {
		this.dashboard = dashboard;
	}
}