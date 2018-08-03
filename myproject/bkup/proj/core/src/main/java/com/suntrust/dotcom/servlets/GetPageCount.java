package com.suntrust.dotcom.servlets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.ServiceAgentService;

@SlingServlet(paths = "/dotcom/pagecount")
public class GetPageCount  extends SlingSafeMethodsServlet {
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	@Reference
	private ServiceAgentService serviceAgent;
	@Reference
    private QueryBuilder builder;
	@Reference
	private SuntrustDotcomService dotcomService;
	@Reference
	private SlingSettingsService settingsService;
	private static final Logger LOGGER = LoggerFactory.getLogger(GetPageCount.class);
	private final Set<String> listOfTemplates = new HashSet<String>();
	private String staticTemplatesParentPath = "/apps/dotcom/components/content";
	private String dynamicTemplatesParentPath = "/apps/dotcom/components/page";
	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
		try {
			ResourceResolver resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource resource = resourceResolver.getResource(staticTemplatesParentPath);
			for (Resource childResource : resource.getChildren()) {
				listOfTemplates.add(childResource.getPath());
			}
			resource = resourceResolver.getResource(dynamicTemplatesParentPath);
			for (Resource childResource : resource.getChildren()) {
				listOfTemplates.add(childResource.getPath());
			}
			for (String templatePath : listOfTemplates) {
				LOGGER.info("Template Paths " + templatePath);
			}
			/*for (String templatePath : listOfTemplates) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("1_property", "jcr:content/cq:template");
				map.put("1_property.value",templatePath);
	    	    map.put("p.limit", "-1");
				Map<String, Object> param = new HashMap<String, Object>();
		        param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
				resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
				Session session = resourceResolver.adaptTo(Session.class);
	    	    Query query = builder.createQuery(PredicateGroup.create(map), session);
				LOGGER.info("Query " + query.getPredicates());
	    	    SearchResult result = query.getResult();
	    	    LOGGER.info("Number of Pages with "+templatePath+" " + result.getHits().size());
			}*/
			
			
		} catch (LoginException | RepositoryException e) {
			LOGGER.info("Error in GetPageCount ", e);
		}
		
	}

}
