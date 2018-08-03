package com.suntrust.dotcom.workflow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowNode;
import com.day.cq.commons.Externalizer;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.utils.Utils;

/**
 * Advisor batch job page publish worklfow
 * 
 * @author ugnk52 Nandakumaran Kasinathan
 *
 */
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Advisor Email Wrokflow Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust Advisor Email Workflow Service") 
})
public class AdvisorEmailWorkflowService  implements WorkflowProcess{
	
	/** ResourceResolverFactory class reference variable*/
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
	/** SuntrustDotcomService class reference variable*/
	@Reference 
	SuntrustDotcomService dotcomServiceconfig;
	
	/** EmailService class reference variable*/
	@Reference
	EmailService emailService;
	
	/** SlingSettingsService class reference variable*/
	@Reference
	SlingSettingsService slingSettings;
	
	/** ResourceCollectionManager class reference variable*/
	@Reference
	private ResourceCollectionManager resourceCollectionManager;
	
	/** Logger variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorEmailWorkflowService.class);
	
	/** Approve notification email template path */
	private static final String NOTIFICATION_TEMPLATEPATH = "/etc/notification/email/html/dotcom/advisoremailnotification.html";
	
	/** Final notification email template path */
	private static final String COMPLETION_TEMPLATEPATH = "/etc/notification/email/html/dotcom/completionnotification.html";
	
	/** Unpublish final notification email template path */
	private static final String TERMINATION_TEMPLATEPATH = "/etc/notification/email/html/dotcom/terminationnotification.html";
	
	/** Participant steps from which comments has to be captured */
	private static final String[] WORKFLOW_USER_STEPS = {WorkflowNode.TYPE_DYNAMIC_PARTICIPANT,WorkflowNode.TYPE_PARTICIPANT,WorkflowNode.TYPE_START};
	
	/** Variable to hold workflow model title */
	private String workflowModelTitle = null;
	
	/** Variable to hold workflow title */
	private String workflowTitle = null;
	
	/** Variable to hold task name */
	private String taskName = null;
	
	/** Variable to hold task path */
	private String taskItemPath = "";
	
	/** Variable to hold workflow item path */
	private String workItemPath = "";
	
	/** Variable to hold workflow initiated date */
	private String workflowInitiateDate = null;
	
	/** Variable to hold inbox path */
	private String inboxPath = "";
	
	/** Variable to hold email recipients */
	private List<String> emailRecipients = null;
	
	/** Variable to hold approver group */
	private String approverGroup = null;
	
	/** Variable to hold task argument */
	private String wfProcessArg = "";
	
	/** Workflow package check flag */
	private boolean iswfPkg = false;
	
	/** Variable to hold environment */
	private String environmentDetails = "";
	
	/** Variable to hold publish environment page path */
	private String productionPagePath = "";
	
	/** Variable to identify unpublish qorkflow */
	private String workflowTerminted = "";
	
	/** ResourceResolver class reference variable */
	private ResourceResolver resourceResolver = null;
	
	/** Session class reference variable */
	private Session session = null;
	
	/** Externalizer class reference variable */
	private Externalizer externalizer = null;
	
	/** UserManager class reference variable */
	private UserManager userManager = null;
	

	/**
	 * Main method which gets task details and task user details for sending email.
	 * 
	 */
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
		
		try{
			wfProcessArg = "";
			workflowTerminted = "";
			taskItemPath = "";
			inboxPath = "";
			workItemPath = "";
			productionPagePath = "";
			environmentDetails = "";
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
			session = resourceResolver.adaptTo(Session.class);
			externalizer = resourceResolver.adaptTo(Externalizer.class);
			userManager = resourceResolver.adaptTo(UserManager.class);
			
			workflowModelTitle = workItem.getWorkflow().getWorkflowModel().getTitle();
			workflowTitle = workItem.getWorkflow().getWorkflowData().getMetaDataMap().get("workflowTitle", String.class);
			if(workItem.getWorkflow().getWorkflowData().getMetaDataMap().containsKey("terminated")){
				workflowTerminted = workItem.getWorkflow().getWorkflowData().getMetaDataMap().get("terminated", String.class);
			}
			
			List<Route> routes = workflowSession.getRoutes(workItem,true);
			taskName = routes.get(0).getName();
			LOGGER.debug("EMAIL WORKFLOW -- WORKFLOW TASK NAME :::"+taskName);
			
			String pagePath = workItem.getWorkflowData().getPayload().toString();
			LOGGER.debug("Page path: "+pagePath);
			//Map<String, String> pathDetails = getAbsolutePaths(workItem, pagePath);
			/**
			 * Path is set empty if there is no profile to be published for
			 * advisor workflow. AEM replaces empty string with DOT. No
			 * action is required in this case.
			 */
			if(pagePath.equals(".") == false){
				Map<String, String> pathDetails = Utils.getAbsolutePaths(workItem,
						pagePath, session, externalizer, resourceCollectionManager,
						resourceResolver,
						dotcomServiceconfig.getPropertyArray("canonical.urls"));
				taskItemPath = pathDetails.get(Utils.PAYLOADPATH);
				inboxPath = pathDetails.get(Utils.INBOXPATH);
				workItemPath = pathDetails.get(Utils.WORKITEMPATH);
				productionPagePath = pathDetails.get(Utils.PUBLISHEDPATH);
				environmentDetails = pathDetails.get(Utils.ENV_DETAILS);
				
				if(pathDetails.containsKey(Utils.ISWFPKG)){
					iswfPkg=true;
					pathDetails.get(Utils.WFPKGPATH);
				}
			} else {
				environmentDetails = externalizer.publishLink(resourceResolver, "");
			}
				
			LOGGER.debug("EMAIL WORKFLOW -- WORKFLOW PAGE PATH :::"+taskItemPath);
			
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
			
			workflowInitiateDate = formatter.format(workItem.getWorkflow().getTimeStarted());
			LOGGER.debug("EMAIL WORKFLOW -- WORKFLOW INITIATED DATE :::"+workflowInitiateDate);
			
			List<HistoryItem> workflowHistory = workflowSession.getHistory(workItem.getWorkflow());
			if(null!=dotcomServiceconfig)
				approverGroup = dotcomServiceconfig.getPropertyValue("dotcom_people_finder_author");
			
			//Create previous contributors map
			LOGGER.debug("EMAIL WORKFLOW -- WORKFLOW CONTRIBUTORS START :::");
			
			Map<String, String> contributorMap = workflowHistory.stream()
			.filter(getValidDataforContributor())
			.collect(Collectors.toMap(
					history->history.getWorkItem().getNode().getTitle(),
					history -> history.getUserId(),
					 (user, keyValue) -> keyValue,
					 LinkedHashMap::new
					));
			
				routes.stream()
				.forEach(route->{
					contributorMap.put(route.getName(), "Pending");
				});
				
				Map<String,String> finalContributorMap=new LinkedHashMap<>();
				for (Map.Entry<String, String> entry : contributorMap.entrySet()){
					if("pending".equalsIgnoreCase(entry.getValue())){
						finalContributorMap.put(entry.getKey(), entry.getValue());
						break;
					} else {
						finalContributorMap.put(entry.getKey(), entry.getValue());
					}
				}
				
				//Create List of contributors
				StringJoiner listedContributors = new StringJoiner("</li>","","</li>");
				finalContributorMap.forEach((step,user)->
				{
					listedContributors.add("<li>"
											+step+" : "
											+Utils.getUserNamebyId(user, userManager));
				}
			);
				LOGGER.debug(listedContributors.toString());
				LOGGER.debug("EMAIL WORKFLOW -- WORKFLOW CONTRIBUTORS END :::");
				
				//Set EMAIL RECIPIENTS
				if(workItem.getNode().getMetaDataMap().containsKey("PROCESS_ARGS")){
					wfProcessArg = workItem.getNode().getMetaDataMap().get("PROCESS_ARGS", String.class).toString().trim();
				}
				setRecipients();
				//SEND EMAIL
				sendEmail(listedContributors.toString());
		} catch(Exception e){
			LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN FROM EXECUTE METHOD:::", e);
		}
	}
	
	/**
	 * Sets too email recipients to list 
	 */
	private void setRecipients(){
		try {
			
			Authorizable authorGroupAuthorizable = userManager.getAuthorizable(approverGroup);
			if(null != authorGroupAuthorizable && authorGroupAuthorizable.isGroup()){
				Group group = (Group)authorGroupAuthorizable;
				Iterator<Authorizable> groupUsers = group.getMembers();
				emailRecipients = new ArrayList<>();
				groupUsers.forEachRemaining(authorizable -> {
				try{
					emailRecipients.add(authorizable.getProperty("./profile/email") != null? authorizable.getProperty("./profile/email")[0].getString() : "someone@SunTrust.com");
				}
				catch(Exception e){
					LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN WHILE ADDING TO USERS:::", e);
				}
				});
			}
			else
				throw new Exception("dotcom_people_finder_author group not found");
			
		} catch (Exception e) {
			LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN IN SET RECIPIENTS METHOD :::", e);
		}
		
	}
	
	/**
	 * Gets only user steps
	 * 
	 * @return
	 */
	private Predicate<HistoryItem> getValidDataforContributor(){
		return history->
		Arrays.asList(WORKFLOW_USER_STEPS).contains(history.getWorkItem().getNode().getType()) &&
		null!=history.getWorkItem().getNode().getTitle();
	}
	
	/**
	 * Sends email with appropriate template
	 * 
	 * @param listedContributors
	 */
	private void sendEmail(String listedContributors){
		Map<String,String> emailParams = new HashMap<>();
		String subject = workflowTitle+" - "+taskName;
		String template = NOTIFICATION_TEMPLATEPATH;
		
		if("contentdevelopmentcheckdone".equals(wfProcessArg)){
			
			if("yes".equals(workflowTerminted)){
				template = TERMINATION_TEMPLATEPATH;
				subject = "Workflow terminated";
			} else {
				subject = "Deployed to Production: "+productionPagePath;
				if(iswfPkg)
					subject = "Deployed to Production";
				template=COMPLETION_TEMPLATEPATH;
			}
		}
		LOGGER.debug("Task Argument: "+wfProcessArg);
		LOGGER.debug("Template Selected: "+template);
		emailParams.put("subject", subject);
		emailParams.put("senderEmailAddress","cms@SunTrust.com");  
		emailParams.put("senderName","Suntrust CMS");
		
		emailParams.put("productionPagePath",productionPagePath);
		emailParams.put("pageLink", taskItemPath);
		emailParams.put("workflowModelTitle", workflowModelTitle);
		emailParams.put("workflowTitle", workflowTitle);
		emailParams.put("task", taskName);
		emailParams.put("inboxPath", inboxPath);
		emailParams.put("workItemPath", workItemPath);
		emailParams.put("workFlowDate", workflowInitiateDate);
		emailParams.put("contributors", listedContributors);
		emailParams.put("environment", environmentDetails);
		
		emailService.sendEmail(template, emailParams, emailRecipients);
	}
	
}
