package com.first.myproject.sightly;

import java.util.Iterator;
import java.util.Map;

import javax.script.Bindings;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sightly.java.api.Use;

/**
 * DisplayPageService Component
 */
@Component(immediate = true)
@Service(DisplayPageServiceUse.class)
public class DisplayPageServiceUse implements Use {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private String noOfPages;
	private ResourceResolver resourceResolver;
	private SearchPagesService searchPagesService;

	public Iterator<Map<String, String>> getPages() {

		logger.info("resourceResolver from init method ::: " + resourceResolver);
		return searchPagesService.getPagessList(resourceResolver, noOfPages);
	}

	public String getNoOfPages() {
		return noOfPages;
	}

	@Override
    public void init(Bindings bindings) {
		
		SlingHttpServletRequest request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);
		
		SlingScriptHelper sling = (SlingScriptHelper)bindings.get(SlingBindings.SLING);
		
		searchPagesService = sling.getService(SearchPagesService.class);
		
		resourceResolver = request.getResourceResolver();
		
		ValueMap properties = (ValueMap)bindings.get("properties"); 
		noOfPages = properties.get("noOfPages", String.class);
		logger.info("noOfPages" + noOfPages);
	    
    }

}