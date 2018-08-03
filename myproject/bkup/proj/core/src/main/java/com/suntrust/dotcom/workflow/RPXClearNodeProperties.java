package com.suntrust.dotcom.workflow;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.suntrust.dotcom.config.STELConfigService;

/**
 * Workflow process step to clear the node properties
 * that are set by RPX scheduler
 */
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust RPX Clear Node Properties"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust RPX Clear Node Properties") })
public class RPXClearNodeProperties implements WorkflowProcess {
	/** ResourceResolverFactory class reference variable */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	/** STEL config class reference variable */
	@Reference
	private STELConfigService stelConfigService;
	/** Logger variable */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RPXClearNodeProperties.class);
	/**
	 * Default execute method to clear the node properties 
	 * that are set by RPX scheduler
	 * @param workitem
	 * @param workflowSession
	 * @param metaDataMap
	 * @return
	 */
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap metaDataMap) {		
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			Session session=null;
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			ResourceResolver resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);			
			session = resourceResolver.adaptTo(Session.class);
			String pagePath = workItem.getWorkflowData().getPayload().toString();			
			Node node = session.getNode(pagePath).getNode("jcr:content");
			if(node.hasProperty("rpx.status")){
				node.getProperty("rpx.status").remove();	
			}
			if(node.hasProperty("rpx.retry.count")){
				node.getProperty("rpx.retry.count").remove();	
			}			
			session.save();					
		} catch (LoginException | RepositoryException e) {			
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
		}
	}

}
