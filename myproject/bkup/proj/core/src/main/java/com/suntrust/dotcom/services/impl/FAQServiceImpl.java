package com.suntrust.dotcom.services.impl;

import javax.jcr.RepositoryException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.services.*;

/**
* This is faq service implementation
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
 */
@Component
@Service
public class FAQServiceImpl implements FAQService {
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FAQService.class);
	

	/**
	 * serviceagentservice 
	 */
	@Reference
	private ServiceAgentService serviceAgent;


	@Override
	/**
	 * @see FAQService#getPageProperty(String, String)	
	 */
	public String getPageProperty(String pageUrl,String propertyName){
		ResourceResolver resourceResolver = null;
		String pagePropertyValue = null;
		String fullUrl=null;			
		fullUrl = pageUrl + "/jcr:content";

		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(fullUrl);
			if (res != null) {
				ValueMap properties = res.adaptTo(ValueMap.class);
				pagePropertyValue = properties.get(propertyName, String.class);
				LOGGER.info("pagePropertyValue =" + pagePropertyValue);
				return pagePropertyValue;
			}
		} catch (LoginException e) {
			LOGGER.error("FAQServiceImpl : getPageProperty() :Exception, {}",e.getMessage(),e);
		} catch (RepositoryException e) {
			LOGGER.error(e.getMessage(),e);
		} finally {
			resourceResolver.close();
		}
		return pagePropertyValue;
	}
}
