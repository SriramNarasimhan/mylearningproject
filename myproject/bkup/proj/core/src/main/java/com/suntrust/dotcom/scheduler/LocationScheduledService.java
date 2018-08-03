package com.suntrust.dotcom.scheduler;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.workflow.WorkflowService;
import com.suntrust.dotcom.config.LocationConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.ServiceAgentService;
import com.suntrust.dotcom.services.WorkflowPackageManager;

@Component(
        label = "Location Page Scheduler",
        description = "Location Page Scheduler",
        immediate = true,
        metatype = true
)

@Property(
        label = "Cron expression defining when this Scheduled Service will run",
        description = "[every minute = 0 * * * * ?], [12:01am daily = 0 1 0 ? * *]",
        name = "scheduler.expression",
        value = "0 1 0 ? * *"
)

/*@Property(
        label = "Cron expression defining when this Scheduled Service will run",
        description = "[every minute = 0 * * * * ?], [12:01am daily = 0 1 0 ? * *]",
        name = "scheduler.period",
        longValue = 300
)*/
 
@Service(value = Runnable.class)
public class LocationScheduledService implements Runnable {
 
	 @Reference
	 ServiceAgentService serviceAgentService;
	 
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
 
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    ResourceResolver resolver = null;
    
    @Reference
    RolloutManager rolloutManager;
    
    @Reference
    LocationConfigService configService;
    
    @Reference
    WorkflowPackageManager workflowPackageManager;
    
	@Reference
	private SuntrustDotcomService suntrustDotcomService;

    @Reference
    WorkflowService wfService;
    
    @Override
    public void run() {
       
        try {
            this.resolver = serviceAgentService.getServiceResourceResolver("dotcomreadservice");
            // Write your code logic here.
            LOGGER.debug("******************* Schedular started *******************");
            LocationLoadHelper helper = new LocationLoadHelper();
            helper.runLocationPageCreation(resolver, configService, rolloutManager, workflowPackageManager, wfService);
            LOGGER.debug("Schedular ended");
        } catch (Exception ex) {
        	LOGGER.error("EXception captured ",ex);
        } finally {
            // ALWAYS close resolvers you open
            if (resolver != null) {
            	resolver.close();
            }
        }
    }
 
    @Activate
    protected void activate(final ComponentContext componentContext) throws Exception {
        final Map<String, String> properties = (Map<String, String>) componentContext.getProperties();
    }
 
    @Deactivate
    protected void deactivate(ComponentContext ctx) {
 
    }
}