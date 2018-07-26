/**
 * 
 */
package com.first.myproject.services;

/**
 * @author Welcome
 *
 */
public interface CustomerService {
	
	//Stores customer data in the Adobe CQ JCR
    public int injestCustData(String firstName, String lastName, String address, String desc);
     
    /*
     * Retrieves customer data from the AEM JCR and returns all customer
     * data within an XML schema
     *The filter argument specifies one of the following values:
     *    
     *Customer - retrieves all customer data
     *Active Customer- retrieves current customers from the JCR
     *Past Customer - retrieves old customers no longer current customers
     */
    public String getCustomerData(String filter); 

}
