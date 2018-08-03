package com.suntrust.dotcom.workflow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.Externalizer;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.utils.Utils;

/**
 * Quick Deploy Workflow email notification class
 * 
 * @author Nandakumaran Kasinathan (ugnk52)
 *
 */
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Quick Deployment Email Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust Quick Deployment Email Service") 
})
public class QuickDeploymentEmailNotification  implements WorkflowProcess{
	
	/** ResourceResolverFactory class reference variable*/
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	/** ResourceResolverFactory class reference variable*/
	@Reference 
	private SuntrustDotcomService dotcomServiceconfig;
	
	/** ResourceResolverFactory class reference variable*/
	@Reference
	private EmailService emailService;
	
	/** ResourceResolverFactory class reference variable*/
	@Reference
	private SlingSettingsService slingSettings;
	
	/** ResourceResolverFactory class reference variable*/
	@Reference
	private ResourceCollectionManager resourceCollectionManager;
	
	/** Logger variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(QuickDeploymentEmailNotification.class);
	
	/** Approve notification email template path */
	private static final String COMPLETION_TEMPLATEPATH="/etc/notification/email/html/dotcom/quickdeploynotification.html";
	
	/** Variable to hold workflow model title */
	private String workflowModelTitle = null;
	
	/** Variable to hold workflow title */
	private String workflowTitle = null;
	
	/** Variable to hold task name */
	private String taskName = null;
	
	/** Variable to hold task item path  */
	private String taskItemPath = null;
	
	/** Variable to hold work ite path */
	private String workItemPath = null;
	
	/** Variable to hold workflow initiated date */
	private String workflowInitiateDate = null;
	
	/** Variable to hold page path */
	private String pagePath = null;
	
	/** Variable to hold inbox path */
	private String inboxPath = null;
	
	/** Variable to hold recipient name */
	private String recipientName = null;
	
	/**
	 * Variable to hold email recipients. Recipient of email will always be an
	 * individual
	 */
	private List<String> emailRecipients = null; 
	
	/** Variable to hold CC list. can be a group or an individual */
	private List<String> ccRecipients = null; 
	
	/** Variable to hold approver group */
	private String dotcom_author_group = null;
	
	/** Variable to hold task argument */
	private String wfProcessArg = null;
	
	/** Workflow package check flag */
	private boolean iswfPkg = false;
	
	/** Variable to hold environment */
	private String environmentDetails = null;
	
	/** Variable to hold publish environment page path */
	private String productionPagePath = null;
	
	/** ResourceResolver class reference variable */
	private ResourceResolver resourceResolver=null;
	
	/** Session class reference variable */
	private Session session=null;
	
	/** Externalizer class reference variable */
	private Externalizer externalizer=null;
	
	/** UserManager class reference variable */
	private UserManager userManager=null;
	

	/**
	 * Execute method
	 */
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
		
		try{
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
			session = resourceResolver.adaptTo(Session.class);
			externalizer = resourceResolver.adaptTo(Externalizer.class);
			userManager = resourceResolver.adaptTo(UserManager.class);
			
			
			workflowModelTitle = workItem.getWorkflow().getWorkflowModel().getTitle();
			LOGGER.debug("EMAIL WORKFLOW -- WORKFLOW MODEL TITLE :::"+workflowModelTitle);
			
			workflowTitle = workItem.getWorkflow().getWorkflowData().getMetaDataMap().get("workflowTitle", String.class);
			List<Route> routes = workflowSession.getRoutes(workItem,true);
			taskName = routes.get(0).getName();
			
			pagePath = workItem.getWorkflowData().getPayload().toString();
			//Map<String, String> pathDetails= getAbsolutePaths(workItem,pagePath);
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
				iswfPkg = true;
				pathDetails.get(Utils.WFPKGPATH);
			}
				
			LOGGER.debug("EMAIL WORKFLOW -- WORKFLOW PAGE PATH :::"+taskItemPath);
			
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
			workflowInitiateDate=formatter.format(workItem.getWorkflow().getTimeStarted());
			LOGGER.debug("EMAIL WORKFLOW -- WORKFLOW INITIATED DATE :::"+workflowInitiateDate);
			
			if(null != dotcomServiceconfig) {
				dotcom_author_group = dotcomServiceconfig.getPropertyValue("dotcom-authors");
			}
			
			//Set EMAIL RECIPIENTS
			if(workItem.getNode().getMetaDataMap().containsKey("PROCESS_ARGS")){
				wfProcessArg = workItem.getNode().getMetaDataMap().get("PROCESS_ARGS", String.class).toString().trim();
				setRecipients(wfProcessArg, workItem.getWorkflow().getInitiator());
			} else {
				throw new WorkflowException("No MetaData found. Enter data in arguements section of workflow step");
			}
			
			//SEND EMAIL
			sendEmail();
			
		}catch(Exception e) {
			LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN FROM EXECUTE METHOD MESSAGE: {} TRACE: {}",e.getMessage(),e);
		}
	}
	
	/**
	 * Sets TOO (selected approver) and CC (always dotcom author group) list
	 * 
	 * @param stage
	 * @param initiator
	 */
	private void setRecipients(String stage, String initiator){
		try {
			String workUser = null;
			if(session.getNode(pagePath).getNode("jcr:content").hasProperty(stage)) {
				workUser = session.getNode(pagePath).getNode("jcr:content").getProperty(stage).getString();
			} else {
				workUser = initiator;
			}
			Authorizable userAuthorizable = userManager.getAuthorizable(workUser);
			
			String emailId = userAuthorizable.getProperty("./profile/email") != null ? userAuthorizable
					.getProperty("./profile/email")[0].getString()
					: "someone@SunTrust.com";
			String givenName = userAuthorizable
					.getProperty("./profile/givenName") != null ? userAuthorizable
					.getProperty("./profile/givenName")[0].getString() : "";
			String familyName = userAuthorizable
					.getProperty("./profile/familyName") != null ? userAuthorizable
					.getProperty("./profile/familyName")[0].getString() : "";
			
			emailRecipients = new ArrayList<>();
			emailRecipients.add(emailId); //email is mapped for all AD users
			recipientName = givenName+" "+familyName+"["+Utils.CORP_DOMAIN+"\\"+workUser+"]";
			
			Authorizable authorGroupAuthorizable = userManager.getAuthorizable(dotcom_author_group);
			if(null!=authorGroupAuthorizable && authorGroupAuthorizable.isGroup()){
				Group group = (Group)authorGroupAuthorizable;
				Iterator<Authorizable> groupUsers = group.getMembers();
				ccRecipients = new ArrayList<>();
				groupUsers.forEachRemaining(authorizable -> {
				try{
					ccRecipients.add(authorizable
							.getProperty("./profile/email") != null ? authorizable
							.getProperty("./profile/email")[0]
							.getString() : "someone@SunTrust.com");
				}catch(Exception e) {
					LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN WHILE ADDING CC USERS:::"+e);
				}
				});
			} else {
				throw new Exception("Dotocm author group not found");
			}
			
		}catch(Exception e) {
			LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN IN SET RECIPIENTS METHOD. MESSAGE: {} TRACE: {}",e.getMessage(),e);
		}
		
	}

	/**
	 * Send email to given distribution list using specified template
	 */
	private void sendEmail(){
		
		Map<String,String> emailParams=new HashMap<>();
		String subject=workflowTitle+" - "+taskName;
		String template=COMPLETION_TEMPLATEPATH;
		
		if(wfProcessArg.equals("quickdeploy")){
			subject="Deployed to Production: "+productionPagePath;
			if(iswfPkg){
				subject="Deployed to Production";
			}
		}
		
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
		emailParams.put("recipientName",recipientName);
		emailParams.put("workFlowDate", workflowInitiateDate);
		emailParams.put("environment", environmentDetails);
		
		emailService.sendEmail(template, emailParams, emailRecipients, ccRecipients);
	}
	
}
