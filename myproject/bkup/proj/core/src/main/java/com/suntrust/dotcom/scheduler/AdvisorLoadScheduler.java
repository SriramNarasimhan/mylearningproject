package com.suntrust.dotcom.scheduler;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.workflow.WorkflowService;
import com.suntrust.dotcom.config.AdvisorConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.services.ServiceAgentService;
import com.suntrust.dotcom.services.WorkflowPackageManager;

@SuppressWarnings("serial")
@SlingServlet(paths = "/dotcom/peopleload_old")

/**
 * Advisor scheduler class
 * 
 * @author Nandakumaran Kasinathan(ugnk52)
 *
 */
public class AdvisorLoadScheduler extends SlingSafeMethodsServlet {

	/** RolloutManager class reference variable */
    @Reference
    private RolloutManager rolloutManager;

    /** DataSourcePool class reference variable */
    @Reference
    private DataSourcePool dataSourcePool;

    /** AdvisorConfigService class reference variable */
    @Reference
    private AdvisorConfigService configService;

    /** ServiceAgentService class reference variable */
    @Reference
    private ServiceAgentService serviceAgentService;
    
    /** WorkflowPackageManager class reference variable */
    @Reference
    private WorkflowPackageManager workflowPackageManager;
    
    /** WorkflowService class reference variable */
    @Reference
    private WorkflowService wfService;
    
    /** SuntrustDotcomService class reference variable */
    @Reference
	private SuntrustDotcomService suntrustDotcomService;
    
    /** EmailService class reference variable*/
    @Reference
	private EmailService emailService;
    
    /** Replicator object reference variable*/
    @Reference
	private Replicator replicator;
    
    /** Logger reference variable */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorLoadScheduler.class);

    /**
     * Overriden method which calls advisor page creation process
     */
    @Override
    protected void doGet(final SlingHttpServletRequest request,
                         final SlingHttpServletResponse response) throws ServletException,IOException {
        LOGGER.debug("People doGet in servlet------------------");
        try {
        	// Call to read CSV and update table
        	AdvisorSyncHelper syncHelper = new AdvisorSyncHelper();        	
        	syncHelper.loadAdvisors(dataSourcePool,suntrustDotcomService,serviceAgentService, emailService, configService);        	
        	// Call to read table and create pages
        	AdvisorLoadHelper helper = new AdvisorLoadHelper();
        	helper.sendArgs(replicator,emailService); 
            helper.runPeoplePageCreation(request, response, suntrustDotcomService, serviceAgentService, configService, rolloutManager, workflowPackageManager, wfService, dataSourcePool);            
        } catch (Exception e) {
            LOGGER.error("Exception, {}",e);
        }
    }
}