/**
 * 
 */
package com.first.myproject.vo;

/**
 * @author Welcome
 *
 */
//This bean holds customer information
public class Customer {
	//Define private class members
	private String custId ;
    private String first;
    private String last;
    private String address;
    private String description;
    
	public final String getCustId() {
		return custId;
	}
	public final void setCustId(String custId) {
		this.custId = custId;
	}
	public final String getFirst() {
		return first;
	}
	public final void setFirst(String first) {
		this.first = first;
	}
	public final String getLast() {
		return last;
	}
	public final void setLast(String last) {
		this.last = last;
	}
	public final String getAddress() {
		return address;
	}
	public final void setAddress(String address) {
		this.address = address;
	}
	public final String getDescription() {
		return description;
	}
	public final void setDescription(String description) {
		this.description = description;
	} 

}
