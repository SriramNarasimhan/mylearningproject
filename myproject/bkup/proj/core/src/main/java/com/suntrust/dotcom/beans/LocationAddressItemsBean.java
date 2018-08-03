package com.suntrust.dotcom.beans;

public class LocationAddressItemsBean {
	private String address;
	private String city;
	private String state;
	private String zipCode;	
	private String locationName;	
	private String rowOpen;
	private String rowClose;
	private String latitude;
	private String longitude;
	
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getRowOpen() {
		return rowOpen;
	}
	public void setRowOpen(String rowOpen) {
		this.rowOpen = rowOpen;
	}
	public String getRowClose() {
		return rowClose;
	}
	public void setRowClose(String rowClose) {
		this.rowClose = rowClose;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
