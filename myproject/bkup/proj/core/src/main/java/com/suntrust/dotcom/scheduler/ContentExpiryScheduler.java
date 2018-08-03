package com.suntrust.dotcom.scheduler;

import static org.jsoup.helper.StringUtil.isBlank;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.suntrust.dotcom.config.ContentExpiryConfigService;
import com.suntrust.dotcom.services.ServiceAgentService;

/**
 * Content expiry page finder scheduler
 */
@Component(immediate = true, metatype = true, label = "Content expiry page finder scheduler")
@Service(value = Runnable.class)
@Properties({
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(label = "Cron expression defining when this Scheduled Service will run", description = "[every minute = 0 * * * * ?], [12:01am daily = 0 1 0 ? * *]", name = "scheduler.expression", value = "0 1 0 ? * *") })
public class ContentExpiryScheduler implements Runnable {

	private final Logger LOG = LoggerFactory
			.getLogger(ContentExpiryScheduler.class);

	@Reference
	private WorkflowService workflowService;

	@Reference
	private ServiceAgentService serviceAgent;

	@Reference
	private ContentExpiryConfigService configService;

	private ResourceResolver resourceResolver;

	/**
	 * Run method call.
	 * 
	 */
	@Override
	public void run() {
		workflowInitiator();
	}

	/**
	 * WorkflowInitiator method call to start new workflow process
	 * 
	 */
	private void workflowInitiator() {
		try {
			resourceResolver = serviceAgent
					.getServiceResourceResolver("dotcomreadservice");

			Session session = resourceResolver.adaptTo(Session.class);
			WorkflowSession workflowSession = workflowService
					.getWorkflowSession(session);
			String payLoadPath = configService
					.getPropertyValue("workflow.payload.path");

			String workflowModel = configService
					.getPropertyValue("suntrust.attestation.model.path");

			if (workflowSession != null && !isBlank(workflowModel)
					&& !isBlank(payLoadPath)) {
				WorkflowModel model = workflowSession.getModel(workflowModel);
				WorkflowData data = workflowSession.newWorkflowData("JCR_PATH",
						payLoadPath);
				Map<String, Object> metaData = new HashMap<String, Object>();
				workflowSession.startWorkflow(model, data, metaData);
			} else {
				LOG.error("Required parameters are missing to initiate the workflow :AttestationPageFinderScheduler->workflowInitiator()");
			}
		} catch (LoginException | RepositoryException | WorkflowException e) {
			LOG.error(
					"WorkflowException occurred {} TRACE: {}" + e.getMessage(),
					e);
		} finally {
			serviceAgent.release(this.resourceResolver.adaptTo(Session.class));
			serviceAgent.release(this.resourceResolver);
		}
	}
}
