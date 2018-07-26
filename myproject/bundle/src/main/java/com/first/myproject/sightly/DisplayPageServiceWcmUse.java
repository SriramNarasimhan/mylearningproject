package com.first.myproject.sightly;

import java.util.Iterator;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUse;

/**
 * DisplayPageService Component
 */
@Component(immediate = true)
@Service(DisplayPageServiceWcmUse.class)
public class DisplayPageServiceWcmUse extends WCMUse {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String noOfPages;

	@Override
	public void activate() throws Exception {
		noOfPages = getProperties().get("noOfPages", String.class);
		logger.info("noOfPages" + noOfPages);
	}

	public Iterator<Map<String, String>> getPages() {

		ResourceResolver resolver = getResourceResolver();
		logger.info("resolver" + resolver);

		SearchPagesService searchPagesService = getSlingScriptHelper().getService(SearchPagesService.class);

		return searchPagesService.getPagessList(resolver, noOfPages);

	}

	public String getNoOfPages() {
		return noOfPages;
	}

}