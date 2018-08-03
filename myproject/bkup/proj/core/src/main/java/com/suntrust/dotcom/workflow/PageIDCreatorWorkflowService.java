package com.suntrust.dotcom.workflow;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.suntrust.dotcom.utils.Utils;

@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Page ID Creator Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust Page ID Creator Service") })
public class PageIDCreatorWorkflowService implements WorkflowProcess{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PageIDCreatorWorkflowService.class);
	
	 private static final String SERVICE_ACCOUNT_IDENTIFIER = "dotcomreadservice";
	 private Session jcrSession = null;
	 private ResourceResolver serviceResolver = null;
	 
	 @Reference
	 private ResourceResolverFactory resourceResolverFactory;
	 
	@Override
	public void execute(WorkItem workItem, WorkflowSession workSession, MetaDataMap metaDataMap)
			throws WorkflowException {

		try {
			final Map<String, Object> authInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE,
					(Object) SERVICE_ACCOUNT_IDENTIFIER);

			serviceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo);
			jcrSession = serviceResolver.adaptTo(Session.class);

			String payLoadPath = workItem.getWorkflowData().getPayload().toString();
			if (payLoadPath != null && StringUtils.isNotBlank(payLoadPath)) {
				Resource paylaoadResource = serviceResolver.getResource(payLoadPath);
				if (null != paylaoadResource) {
					Page payloadPage = paylaoadResource.adaptTo(Page.class);
					if (null != payloadPage) {
						Node rootNode = jcrSession.getNode(payloadPage.getPath()).getNode("jcr:content");
						if(null!=rootNode){
							setPageId(rootNode);
						}
						Iterator<Page> listChildren = payloadPage.listChildren(new PageFilter(){
							@Override
							public boolean includes(Page page) {
								if(page.isHideInNav())
									return true;
								return true;
							}
						}, true);
						while (listChildren.hasNext()) {
							Node childPageNode = jcrSession.getNode(listChildren.next().getPath())
									.getNode("jcr:content");

							setPageId(childPageNode);
						}
					}
				}
			}

		} catch (RepositoryException | LoginException e) {
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
		} finally {
			if (jcrSession != null && jcrSession.isLive()) {
				jcrSession = null;
			}
		}

	}
	
	protected void setPageId(Node jcrNode) throws PathNotFoundException, RepositoryException{
		
		if (!jcrNode.hasProperty("pageid")) {
			Utils.setPageId(jcrNode, jcrSession);
		} else if (jcrNode.hasProperty("pageid")) {
			if (jcrNode.getProperty("pageid").toString().trim().isEmpty()) {
				Utils.setPageId(jcrNode, jcrSession);
			}
		}

	}
}