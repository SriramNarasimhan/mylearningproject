package com.suntrust.dotcom.services;

import javax.jcr.RepositoryException;

/**
* This is faq service
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
 */
public interface FAQService {		
	/**
	 * @param pageUrl
	 * @param propertyName
	 * @return string
	 * @throws RepositoryException
	 */
	String getPageProperty(String pageUrl,String propertyName) throws RepositoryException;
}
