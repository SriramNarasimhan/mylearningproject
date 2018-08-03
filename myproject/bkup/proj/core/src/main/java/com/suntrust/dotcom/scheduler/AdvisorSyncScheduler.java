package com.suntrust.dotcom.scheduler;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.suntrust.dotcom.config.AdvisorConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.services.ServiceAgentService;
import com.suntrust.dotcom.services.WorkflowPackageManager;

@Component(immediate = true, metatype = true, label = "Advisor Syncup Scheduler")
@Service(value = Runnable.class)
public class AdvisorSyncScheduler implements Runnable {
	
	/** DataSourcePool class reference variable */
	@Reference
	private DataSourcePool dataSourcePool;
	
	/** SuntrustDotcomService class reference variable */
	@Reference
	private SuntrustDotcomService suntrustDotcomService;
	
	/** ServiceAgentService class reference variable */
	@Reference
	private ServiceAgentService serviceAgentService;
	
	/** WorkflowPackageManager class reference variable */
	@Reference
	private WorkflowPackageManager workflowPackageManager;

	/** WorkflowService class reference variable */
    @Reference
    private WorkflowService wfService;
    
    /** AdvisorConfigService class reference variable */
    @Reference
    private AdvisorConfigService configService;
    
    /** RolloutManager class reference variable */
    @Reference
    private RolloutManager rolloutManager;
    
    /** EmailService class reference variable*/
	@Reference
	private EmailService emailService;
	
	/** Replicator object reference variable*/
	@Reference
	private Replicator replicator;
	
	/** ResourceResolver object reference variable*/
    private ResourceResolver resolver;
    
    /** Session object reference variable*/
    private Session session;
    
    /** Logger class reference variable */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorSyncScheduler.class);
	
	@Override
	public void run() {
		//start the workflow as part of batch job
		try {
			
			this.resolver = serviceAgentService.getServiceResourceResolver("dotcomreadservice");
			this.session= resolver.adaptTo(Session.class);
			WorkflowSession wfSession = wfService.getWorkflowSession(this.session);
			LOGGER.debug("wfSession:" + wfSession);
			WorkflowModel model = wfSession
					.getModel("/etc/workflow/models/dotcom/people-upload-group-approval-workflow/jcr:content/model");
			WorkflowData data = wfSession.newWorkflowData("JCR_PATH",
					"/content/dam/suntrust/us/en/internal-applications/advisor/advisor-page.csv");

			Map<String, Object> metaData = new HashMap<String, Object>();
			metaData.put("workflowTitle", "Advisor page nightly job deployment");
			LOGGER.debug("==> Workflow started");
			wfSession.startWorkflow(model, data, metaData);
		} catch (LoginException | RepositoryException | WorkflowException e) {
			LOGGER.error("Error encountered in AdvisorSyncHelper ==>",e);
		}
		finally{
			serviceAgentService.release(this.resolver.adaptTo(Session.class));
			serviceAgentService.release(this.resolver);
		}
	}
}
