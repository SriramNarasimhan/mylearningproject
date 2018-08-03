package com.suntrust.dotcom.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust RPX Sync Process"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust RPX Sync Process") })
public class RPXSyncProcess implements WorkflowProcess {
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
			.getLogger(RPXSyncProcess.class);
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
		RPXSyncHelper rpxSyncHelper = null;
		String workflowName=workItem.getWorkflow().getWorkflowModel().getTitle();
		try {
			// Retrieve the process argument supplied from workflow process step
			String rpxEnvironment = null;			
			if (metaDataMap.containsKey("PROCESS_ARGS")) {
				rpxEnvironment = metaDataMap.get("PROCESS_ARGS", "string")
						.toString().trim();
			}
			// rpxEnvironment args prefix the environment token to identify the
			// environment specific config nodes
			// rpx.uat for UAT(Eg:rpx.uat.servlet.url), rpx for
			// prod(Eg:rpx.servlet.url)
			LOGGER.debug("RPX environment prefix config variable value:"
					+ rpxEnvironment);
			LOGGER.debug("Workflow invoked: "+workflowName);
			
			String rpxServletUrl = stelConfigService
					.getPropertyValue(rpxEnvironment+".servlet.url");
			String deleteQry = stelConfigService
					.getPropertyValue(rpxEnvironment+".delete.table");
			String insertSpStatement = stelConfigService
					.getPropertyValue(rpxEnvironment+".insert.sp.statement");
			String recordCountSelectQry = stelConfigService
					.getPropertyValue(rpxEnvironment+".record.count");

			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			ResourceResolver resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			rpxSyncHelper = new RPXSyncHelper();
			UserManager userManager = resourceResolver
					.adaptTo(UserManager.class);

			List<String> ccRecipients = null;
			ccRecipients = new ArrayList<String>();
			ccRecipients.add(stelConfigService
					.getPropertyValue("rpx.dcm.recipient.dl"));
			List<String> toRecipients = null;
			toRecipients = new ArrayList<String>();
			toRecipients = RPXUtils.getUsersByGroup(userManager,
					stelConfigService
							.getPropertyValue("rpx.stel.recipient.dl"));
			if (rpxEnvironment.equalsIgnoreCase("rpx.prod")) {				
				boolean rpxSyncStatus = rpxSyncHelper.loadRPXProdRates(
						dataSourcePool, stelConfigService, emailService,
						resourceResolverFactory, dotcomService);
				if (rpxSyncStatus) {
					LOGGER.debug("RPX prod sync update success");
					ccRecipients.add(stelConfigService
							.getPropertyValue("rpx.ps.recipient.dl"));
					RPXUtils.sendNotification(resourceResolver, workItem,
							workflowSession, stelConfigService, emailService,
							rpxEnvironment+".success.mail.template",
							rpxEnvironment+".success.mail.subject",
							rpxEnvironment+".success.mail.body", toRecipients,
							ccRecipients);
					rpxSyncHelper.flushCache(dotcomService,
							resourceResolverFactory);
				} else {					
					LOGGER.debug("RPX prod sync update failed, so terminating the workflow");
					toRecipients.add(stelConfigService
							.getPropertyValue("rpx.ps.recipient.dl"));
					RPXUtils.sendNotification(resourceResolver, workItem,
							workflowSession, stelConfigService, emailService,
							rpxEnvironment+".system.failure.mail.template",
							rpxEnvironment+".system.failure.mail.subject",
							rpxEnvironment+".system.failure.mail.body", toRecipients,
							ccRecipients);
					workflowSession.terminateWorkflow(workItem.getWorkflow());
				}			
			 } else if (rpxEnvironment.equalsIgnoreCase("rpx.uat")) {	
				 LOGGER.debug("RPX UAT sync update failed, so terminating the workflow");
					if (rpxSyncHelper.loadRPXRates(dataSourcePool,
						stelConfigService, emailService,
						resourceResolverFactory, dotcomService, rpxServletUrl,
						deleteQry, insertSpStatement, recordCountSelectQry) == false) {	
					toRecipients.add(stelConfigService
							.getPropertyValue("rpx.ps.recipient.dl"));
					ccRecipients.add(stelConfigService
							.getPropertyValue("rpx.cognition.recipient.dl"));
					RPXUtils.sendNotification(resourceResolver, workItem,
							workflowSession, stelConfigService, emailService,
							rpxEnvironment + ".system.failure.mail.template",
							rpxEnvironment + ".system.failure.mail.subject",
							rpxEnvironment + ".system.failure.mail.body",
							toRecipients, ccRecipients);
					workflowSession.terminateWorkflow(workItem.getWorkflow());
				}
			}else{
				LOGGER.debug("Required process argument parameter is missing or its null");
			}			 

		} catch (WorkflowException | LoginException e) {
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
		}

	}
}
