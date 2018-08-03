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
import com.suntrust.dotcom.config.STELConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.utils.RPXUtils;

/**
 * RPX email notification class
 */
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust RPX Email Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust RPX Email Service") 
})
public class RPXEmailNotification  implements WorkflowProcess{	
	/** ResourceResolverFactory class reference variable*/
	@Reference
	private ResourceResolverFactory resourceResolverFactory;	
	/** SuntrustDotcomService class reference variable*/
	@Reference 
	private SuntrustDotcomService dotcomServiceconfig;
	/** STEL config class reference variable*/
	@Reference 
	private STELConfigService stelConfigService;	
	/** EmailService class reference variable*/
	@Reference
	private EmailService emailService;
	/** Logger variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(RPXEmailNotification.class);
	/**
	 * Workflow process step execute method
	 */
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
		try{
			
			String mailConfigKey=null;
			if(metaDataMap.containsKey("PROCESS_ARGS")){
				mailConfigKey = metaDataMap.get("PROCESS_ARGS","string").toString().trim();				
			}	
			LOGGER.debug("Process args parameter:"+mailConfigKey);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			ResourceResolver resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);		
			UserManager userManager = resourceResolver.adaptTo(UserManager.class);
			List<String> ccRecipients=null;
			ccRecipients=new ArrayList<String>();
			ccRecipients.add(stelConfigService
					.getPropertyValue("rpx.dcm.recipient.dl"));
			ccRecipients.add(stelConfigService
					.getPropertyValue("rpx.ps.recipient.dl"));
			List<String> toRecipients=null;
			toRecipients=new ArrayList<String>();
			toRecipients=RPXUtils.getUsersByGroup(userManager, stelConfigService
					.getPropertyValue("rpx.stel.recipient.dl"));	
			
			if(mailConfigKey.equalsIgnoreCase("rpx.uat.sync") || mailConfigKey.equalsIgnoreCase("rpx.uat.review")){
				ccRecipients.add(stelConfigService
						.getPropertyValue("rpx.cognition.recipient.dl"));
			}else{
				toRecipients.add(stelConfigService
						.getPropertyValue("rpx.cognition.recipient.dl"));
			}
			LOGGER.debug("To recipients:"+toRecipients);
			LOGGER.debug("Cc recipients:"+ccRecipients);
			RPXUtils.sendNotification(resourceResolver,workItem,workflowSession,stelConfigService,emailService,mailConfigKey+".mail.template",mailConfigKey+".mail.subject",mailConfigKey+".mail.body",toRecipients,ccRecipients);
			}catch(Exception e) {
			LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN FROM EXECUTE METHOD MESSAGE: {} TRACE: {}",e.getMessage(),e);
		}
	}
	

}
