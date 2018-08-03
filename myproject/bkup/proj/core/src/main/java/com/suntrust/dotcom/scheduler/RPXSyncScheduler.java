package com.suntrust.dotcom.scheduler;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.workflow.WorkflowService;
import com.suntrust.dotcom.config.STELConfigService;
import com.suntrust.dotcom.services.ServiceAgentService;
import com.suntrust.dotcom.workflow.RPXSyncHelper;

/**
 * This class is called via AEM scheduler to sync up the RPX rates data.
 */
@Component(immediate = true, metatype = true, label = "Suntrust RPX Sync Process Scheduler")
@Service(value = Runnable.class)
@Property(label = "Cron expression defining when this Scheduled Service will run", description = "[every minute = 0 * * * * ?], [12:01am daily = 0 1 0 ? * *]", name = "scheduler.expression", value = "0 5 0 1/1 * ? *")

public class RPXSyncScheduler implements Runnable {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
   @Reference
    WorkflowService wfService;
    ResourceResolver resolver = null;
	@Reference
	ServiceAgentService serviceAgentService;
	@Reference
	STELConfigService stelConfigService;
	/**
	 * Run method calls the helper class method to sync up
	 * rates
	 * @return
	 */	
	@Override
	public void run() {
		try {
			RPXSyncHelper helper = new RPXSyncHelper();			
			this.resolver = serviceAgentService.getServiceResourceResolver("dotcomreadservice");
			LOGGER.debug("Scheduler Kicks off the workflow");
			helper.workflowInitiator(wfService, resolver,stelConfigService.getPropertyValue("rpx.schedule.model.path"),stelConfigService.getPropertyValue("rpx.schedule.payload.path"));	
		} catch (LoginException e) {			
			LOGGER.error("LoginException occurred {} TRACE: {}" + e.getMessage(), e);
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException occurred {} TRACE: {}" + e.getMessage(), e);
		}
		finally{
			serviceAgentService.release(this.resolver.adaptTo(Session.class));
			serviceAgentService.release(this.resolver);
		}
			
	}
}
