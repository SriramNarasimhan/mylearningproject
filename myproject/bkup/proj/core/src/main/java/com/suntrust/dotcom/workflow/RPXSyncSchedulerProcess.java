package com.suntrust.dotcom.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.suntrust.dotcom.config.STELConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.utils.RPXUtils;

/**
 * Workflow process step class to update RPX rates 
 */
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust RPX Sync Process Scheduler"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust RPX Sync Process Scheduler") })
public class RPXSyncSchedulerProcess implements WorkflowProcess {
	/** ResourceResolverFactory class reference variable */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	/** STEL config class reference variable */
	@Reference
	private STELConfigService stelConfigService;
	/** EmailService class reference variable */
	@Reference
	private EmailService emailService;
	/** Logger variable */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RPXSyncSchedulerProcess.class);
	/** DataSourcePool class reference variable */
	@Reference
	private DataSourcePool dataSourcePool;
	/** DataSourcePool class reference variable */
	@Reference
	SuntrustDotcomService dotcomService;

	/**
	 * RPX Sync Process execute method to sync up RPX rates
	 */
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap metaDataMap) {		
		try {
			int retryMaxCount=Integer.parseInt(stelConfigService.getPropertyValue("rpx.prod.retry.max.count"));
			Map<String, Object> param = new HashMap<String, Object>();
			Session session=null;		
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			ResourceResolver resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);			
			session = resourceResolver.adaptTo(Session.class);
			UserManager userManager = resourceResolver.adaptTo(UserManager.class);
			List<String> ccRecipients=null;
			ccRecipients=new ArrayList<String>();
			ccRecipients.add(stelConfigService
					.getPropertyValue("rpx.dcm.recipient.dl"));			
			ccRecipients.add(stelConfigService
					.getPropertyValue("rpx.cognition.recipient.dl"));			
			List<String> toRecipients=null;
			toRecipients=new ArrayList<String>();
			toRecipients=RPXUtils.getUsersByGroup(userManager, stelConfigService
					.getPropertyValue("rpx.stel.recipient.dl"));		
			toRecipients.add(stelConfigService
					.getPropertyValue("rpx.ps.recipient.dl"));
			
			RPXSyncHelper rpxSyncHelper = new RPXSyncHelper();
			// Retrieve the process argument supplied from workflow process step
			String pagePath = workItem.getWorkflowData().getPayload().toString();			
			Node node = session.getNode(pagePath).getNode("jcr:content");
			if(node.hasProperty("rpx.retry.count")==false){
				LOGGER.debug("RPX sync first try");
				node.setProperty("rpx.retry.count", 0);				
			}
			boolean rpxSyncStatus=rpxSyncHelper.loadRPXProdRates(dataSourcePool, stelConfigService,
					emailService, resourceResolverFactory, dotcomService);
			if(rpxSyncStatus == false) {
				LOGGER.debug("RPX sync update failed");
				node.setProperty("rpx.status", "failed");
				if(node.hasProperty("rpx.retry.count") && node.getProperty("rpx.retry.count").getLong()<=retryMaxCount){
					long retryCount=node.getProperty("rpx.retry.count").getLong()+1;				
					LOGGER.debug("RPX sync re-try >"+retryCount);
					RPXUtils.sendNotification(resourceResolver, workItem,
							workflowSession, stelConfigService, emailService,
							"rpx.schedule.failure.mail.template",
							"rpx.schedule.failure.mail.subject", "rpx.schedule.failure.mail.body",toRecipients,ccRecipients);
							node.setProperty("rpx.retry.count", retryCount);					
				}else{
					LOGGER.debug("All RPX sync retries failed, so terminating the schedule job");
					workflowSession.terminateWorkflow(workItem.getWorkflow());	
				}	
			}else{
				LOGGER.debug("RPX sync update successful");
				rpxSyncHelper.flushCache(dotcomService, resourceResolverFactory);
				node.setProperty("rpx.status", "success");		
			}					
			session.save();	
		} catch (LoginException | RepositoryException | WorkflowException e) {			
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
		}
	}

}
