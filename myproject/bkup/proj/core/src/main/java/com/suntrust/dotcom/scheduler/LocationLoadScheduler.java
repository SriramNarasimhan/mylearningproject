package com.suntrust.dotcom.scheduler;

import java.io.IOException;

import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.workflow.WorkflowService;
import com.suntrust.dotcom.config.LocationConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.ServiceAgentService;
import com.suntrust.dotcom.services.WorkflowPackageManager;

@SuppressWarnings("serial")
@SlingServlet(paths = "/dotcom/locationload")
public class LocationLoadScheduler extends SlingSafeMethodsServlet {
	
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    
    @Reference
    RolloutManager rolloutManager;
    
    @Reference
    LocationConfigService configService;
    
    @Reference
    ServiceAgentService serviceAgentService;
    
    @Reference
    WorkflowPackageManager workflowPackageManager;
    
	@Reference
	private SuntrustDotcomService suntrustDotcomService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationLoadScheduler.class);

    private ResourceResolver resolver;

    @Reference
    WorkflowService wfService;
    
    @Override
    protected void doGet(final SlingHttpServletRequest request,
                         final SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug("doGet in servlet------------------");
        try{
        this.resolver = serviceAgentService.getServiceResourceResolver("dotcomreadservice");
        //this.session= resolver.adaptTo(Session.class);
        LocationLoadHelper helper = new LocationLoadHelper();
        helper.runLocationPageCreation(request, response, resolver, configService, rolloutManager, workflowPackageManager, wfService);
        } catch(Exception e){
            LOGGER.error("Exception: ",e);
        }
        finally{
			serviceAgentService.release(this.resolver.adaptTo(Session.class));
			serviceAgentService.release(this.resolver);
		}
        
    }


}

