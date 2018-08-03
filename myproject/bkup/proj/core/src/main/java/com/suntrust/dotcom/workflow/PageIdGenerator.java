package com.suntrust.dotcom.workflow;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.suntrust.dotcom.utils.Utils;
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Page Id Generator Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust Page Id Generator Service") })
public class PageIdGenerator implements WorkflowProcess {
	/** Session class reference variable */
	private Session jcrSession = null;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PageIdGenerator.class);
	@Override
	public void execute(WorkItem workItem, WorkflowSession workSession,
			MetaDataMap metaDataMap) throws WorkflowException {
		try {
			jcrSession = workSession.adaptTo(Session.class);
			String payLoadPath = workItem.getWorkflowData().getPayload()
					.toString();
			if (payLoadPath != null && StringUtils.isNotBlank(payLoadPath)) {
				Node payLoadNode = jcrSession.getNode(payLoadPath).getNode("jcr:content");
				
				if (!payLoadNode.hasProperty("pageid")) {
					setPageId(payLoadNode,jcrSession);
				}
				else if(payLoadNode.hasProperty("pageid")){
					if(payLoadNode.getProperty("pageid").toString().trim().isEmpty()){
						setPageId(payLoadNode,jcrSession);
					}
				}
			}
			
		} catch (RepositoryException e) {
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
		}finally {
			if (jcrSession != null && jcrSession.isLive()) {
				jcrSession = null;
			}
		}
	}
	
	private void setPageId(Node payLoadNode, Session jcrSession){
		
		try {
			payLoadNode.setProperty("pageid", Utils.generatePageId());
			LOGGER.info("PageId property:" + payLoadNode.getProperty("pageid").getString());
			jcrSession.save();
		} catch (RepositoryException e) {
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
		}
	}
	
}
