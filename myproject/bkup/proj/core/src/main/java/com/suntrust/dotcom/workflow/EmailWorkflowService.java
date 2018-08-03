package com.suntrust.dotcom.workflow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.google.common.collect.Lists;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.utils.Utils;

/**
 * Base version used for sending email through workflow 
 * 
 * @author uiam82
 *
 */
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust EMail Wrokflow Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust EMail Workflow Service") 
})
public class EmailWorkflowService  implements WorkflowProcess{
	
	/** ResourceResolverFactory class reference variable */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	/** SuntrustDotcomService class reference variable */
	@Reference 
	private SuntrustDotcomService dotcomServiceconfig;
	
	/** EmailService class reference variable */
	@Reference
	private EmailService emailService;
	
	/** SlingSettingsService class reference variable */
	@Reference
	private SlingSettingsService slingSettings;
	
	/** ResourceCollectionManager class reference variable */
	@Reference
	private ResourceCollectionManager resourceCollectionManager;
	
	/** Logger variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailWorkflowService.class);
	
	/** Task approve email template path */
	private static final String NOTIFICATION_TEMPLATEPATH="/etc/notification/email/html/dotcom/emailnotification.html";
	
	/** MAIL_BODY_COMPLIANCE_ASSIGN_SUBREVIEW */
	private static final String MAIL_BODY_COMPLIANCE_ASSIGN_SUBREVIEW="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, you are assigned as the Primary Compliance Reviewer for this in-progress workflow. </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>Please read the Workflow Description in the Comments section below to determine if Compliance Reviewers are needed to complete the workflow. If you have additional questions, please contact the Requester listed in the Contributors section below.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>When ready, click the orange button below to add Compliance reviewers or skip this step and move the workflow to the next stage where you can approve or reject this workflow.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you are in the CC: field of this e-mail, it is just a courtesy notification and no action by you is required.</span></p>";
	
	/** MAIL_BODY_LEGAL_ASSIGN_SUBREVIEW */
	private static final String MAIL_BODY_LEGAL_ASSIGN_SUBREVIEW="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, you are assigned as the Primary Legal Reviewer for this in-progress workflow. </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>Please read the Workflow Description in the Comments section below to determine if Legal Reviewers are needed to complete the workflow. If you have additional questions, please contact the Requester listed in the Contributors section below.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>When ready, click the orange button below to add Legal reviewers or skip this step and move the workflow to the next stage where you can approve or reject this workflow.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you are in the CC: field of this e-mail, it is just a courtesy notification and no action by you is required.</span></p>";
	
	/** MAIL_BODY_COMPLIANCE_APPROVEREJECT */
	private static final String MAIL_BODY_COMPLIANCE_APPROVEREJECT="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, you are assigned as the Primary Compliance Reviewer for this in-progress workflow.  </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>Previously, the Primary Compliance Reviewer assigned or skipped Compliance sub-review tasks. All of those tasks have been completed and it is time for the Primary Compliance Reviewer to Approve or Reject this workflow.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you approve, the workflow will advance to the Legal Reviewer selection where the Primary Legal Reviewer will choose necessary Legal reviewers or choose to skip Legal reviews and advance the workflow to Content Team for publication.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you reject, the workflow will revert back to the Content Development portion of the workflow and the process will start over.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you have additional questions, please contact the Requester listed in the Contributors section below. When ready, click the orange button below to Start Task.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you are in the CC: field of this e-mail, it is just a courtesy notification and no action by you is required.</span></p>";
	
	/** MAIL_BODY_COMPLIANCE_SUB_REVIEW_APPROVED */
	private static final String MAIL_BODY_COMPLIANCE_SUB_REVIEW_APPROVED="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, the Compliance sub-review has been approved by the sub-reviewer of this task.  </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>There is no action necessary at this time. When all sub-reviews are completed, the Workflow will transition back to the Primary Compliance Reviewer for an Approve/Reject.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If there are any questions, please contact the sub-reviewer shown below in the Approved By field.</span></p>";
	
	/** MAIL_BODY_COMPLIANCE_SUB_REVIEW_APPROVED */
	private static final String MAIL_BODY_COMPLIANCE_SUB_REVIEW_REJECTED="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, the Compliance sub-review has been rejected by the sub-reviewer of this task.  </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>There is no action necessary at this time. When all sub-reviews are completed, the Workflow will transition back to the Primary Compliance Reviewer for an Approve/Reject task which will give the Primary Compliance Reviewer the option to reject the Workflow back to the content development stage. </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If there are any questions, please contact the sub-reviewer shown below in the Rejected By field. </span></p>";
	
	/** MAIL_BODY_COMPLIANCE_SUB_REVIEW_APPROVED */
	private static final String MAIL_BODY_LEGAL_SUB_REVIEW_APPROVED="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, the Legal sub-review has been approved by the sub-reviewer of this task.  </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>There is no action necessary at this time. When all sub-reviews are completed, the Workflow will transition back to the Primary Legal Reviewer for an Approve/Reject.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If there are any questions, please contact the sub-reviewer shown below in the Approved By field.</span></p>";
	
	/** MAIL_BODY_COMPLIANCE_SUB_REVIEW_APPROVED */
	private static final String MAIL_BODY_LEGAL_SUB_REVIEW_REJECTED="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, the Legal sub-review has been rejected by the sub-reviewer of this task.  </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>There is no action necessary at this time. When all sub-reviews are completed, the Workflow will transition back to the Primary Legal Reviewer for an Approve/Reject task which will give the Primary Legal Reviewer the option to reject the Workflow back to the content development stage. </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If there are any questions, please contact the sub-reviewer shown below in the Rejected By field. </span></p>";
	
	/** MAIL_BODY_COMPLIANCE_SUB_REVIEW_ASSIGNED */
	private static final String MAIL_BODY_COMPLIANCE_SUB_REVIEW_ASSIGNED="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, you are assigned as a <b>Secondary Compliance Reviewer</b> for this in-progress workflow.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>Please read the Workflow Description in the <b>Comments</b> section below to determine what reviews are necessary to approve/reject the Workflow. If you have questions, please contact the Primary Compliance Reviewer listed in the <b>Contributors</b> section below. </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>When ready, click the orange button below to <b>Start Task.</b> </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you are in the CC: field of this e-mail, it is just a courtesy notification and no action by you is required. </span></p>";
		
	/** MAIL_BODY_COMPLIANCE_SUB_REVIEW_ASSIGNED */
	private static final String MAIL_BODY_LEGAL_SUB_REVIEW_ASSIGNED="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, you are assigned as a <b>Secondary Legal Reviewer</b> for this in-progress workflow.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>Please read the Workflow Description in the <b>Comments</b> section below to determine what reviews are necessary to approve/reject the Workflow. If you have questions, please contact the Primary Legal Reviewer listed in the <b>Contributors</b> section below. </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>When ready, click the orange button below to <b>Start Task.</b> </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you are in the CC: field of this e-mail, it is just a courtesy notification and no action by you is required. </span></p>";
	
	/** MAIL_BODY_LEGAL_APPROVEREJECT */
	private static final String MAIL_BODY_LEGAL_APPROVEREJECT="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, you are assigned as the Primary Legal Reviewer for this in-progress workflow.  </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>Previously, the Primary Legal Reviewer assigned or skipped Legal sub-review tasks. All of those tasks have been completed and it is time for the Primary Legal Reviewer to Approve or Reject this workflow.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you approve, the workflow will advance to Content Team for publication.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you reject, the workflow will revert back to the Content Development portion of the workflow and the process will start over.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you have additional questions, please contact the Requester listed in the Contributors section below. When ready, click the orange button below to Start Task.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>If you are in the CC: field of this e-mail, it is just a courtesy notification and no action by you is required.</span></p>";

	/** MAIL_BODY_GENERAL */
	private static final String MAIL_BODY_GENERAL="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, a task has been assigned to you or your group by the Content Management System. Details of the task are described below. When you are ready to begin the task, click the task link below to Start Task. Please note: If your name is not in the To: field and only in the CC: field, this is a courtesy notification and there is no task for you to complete.</p>";
	
	/** MAIL_BODY_WAIT_FOR_DEPLOYMENT */
	private static final String MAIL_BODY_WAIT_FOR_DEPLOYMENT="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, the following page or pages have been scheduled for publication at a future date/time per your request:</p>";
	
	/** MAIL_BODY_WAIT_FOR_UNPUBLISH_DEPLOYMENT */
	private static final String MAIL_BODY_WAIT_FOR_UNPUB_DEPLOYMENT="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, the following page or pages have been scheduled for unpublication at a future date/time per your request:</p>";
	
	/** MAIL_CTA_GENERAL */
	private static final String MAIL_CTA_GENERAL="Start Task";
	
	/** MAIL_CTA_REVIEWER_SELECTION */
	private static final String MAIL_CTA_REVIEWER_SELECTION="Make Reviewer Selection";
	
	/** MAIL_CTA_REVIEWER_SELECTION */
	//private static final String MAIL_CUSTOM_STYLE="<style>.hide-in-email{display:none;}</style>";
	private static final String MAIL_CUSTOM_STYLE=""
			+ "<style>"
			+ " .hide-in-email-workTitle{display:none;}"
			+ " .hide-in-email-assignedInitiated{display:none;}"
			+ " .hide-in-email-cta{display:none;}"
			+ " .hide-in-email-scheduledTime{display:none;}"
			+ "</style>";
	
	private static final String DEFAULT_MAIL_STYLE=""
			+ "<style>"
			+ " .hide-in-email-scheduledTime{display:none;}"
			+ "</style>";
	
	private static final String SCHEDULED_DEPLOYMENT_MAIL_STYLE=""
			+ "<style>"
			+ " .hide-in-email-assignedInitiated{display:none;}"
			+ " .hide-in-email-cta{display:none;}"
			+ "</style>";
	
	/** MAIL_BODY_PRIMARY_COMPLIANCE_REJECTED */
	private  String MAIL_BODY_PRIMARY_COMPLIANCE_REJECTED="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, the Primary Compliance Review has been rejected.  </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>There is no action necessary at this time. The workflow will be reverted back to the Content Development task and the review process will need to start again.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>Requesters should coordinate with the person who rejected the workflow to determine if additional content development is needed and then communicate needed changes with the Content Team. In many cases, it may be helpful to have the Content Team terminate this workflow and the Requester submit a new request.</span></p>";
	
	/** MAIL_BODY_PRIMARY_LEGAL_REJECTED */
	private  String MAIL_BODY_PRIMARY_LEGAL_REJECTED="<p><span style='font-family: 'Trebuchet MS', sans-serif;'>Hello, the Primary Legal Review has been rejected.  </span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>There is no action necessary at this time. The workflow will be reverted back to the Content Development task and the review process will need to start again.</span></p><p><span style='font-family: 'Trebuchet MS', sans-serif;'>Requesters should coordinate with the person who rejected the workflow to determine if additional content development is needed and then communicate needed changes with the Content Team. In many cases, it may be helpful to have the Content Team terminate this workflow and the Requester submit a new request.</span></p>";
	
	/** Final deployment email template path */
	private static final String COMPLETION_TEMPLATEPATH="/etc/notification/email/html/dotcom/completionnotification.html";
	
	/** Unpublish task approve email template path */
	private static final String UNP_NOTIFICATION_TEMPLATEPATH="/etc/notification/email/html/dotcom/unpublishemailnotification.html";
	
	/** Unpublish final deployment email template path */
	private static final String UNP_COMPLETION_TEMPLATEPATH="/etc/notification/email/html/dotcom/unpublishcompletionnotification.html";
	
	/** Workflow approval steps list */
	private static final String[] WORKFLOW_USER_STEPS={WorkflowNode.TYPE_DYNAMIC_PARTICIPANT,WorkflowNode.TYPE_PARTICIPANT,WorkflowNode.TYPE_START};

	/** Variable to hold tasknames for standard v2 workflow */
	String[] subTaskNamesApprovedRejected = new String[]{"Legal Sub-Review Task 2.1 - Rejected","Legal Sub-Review Task 2.2 - Rejected","Legal Sub-Review Task 2.3 - Rejected","Legal Sub-Review Task 2.4 - Rejected","Legal Sub-Review Task 2.5 - Rejected","Legal Sub-Review Task 2.1 - Approved","Legal Sub-Review Task 2.2 - Approved","Legal Sub-Review Task 2.3 - Approved","Legal Sub-Review Task 2.4 - Approved","Legal Sub-Review Task 2.5 - Approved","Compliance Sub-Review Task 1.1 - Rejected","Compliance Sub-Review Task 1.2 - Rejected","Compliance Sub-Review Task 1.3 - Rejected","Compliance Sub-Review Task 1.4 - Rejected","Compliance Sub-Review Task 1.5 - Rejected","Compliance Sub-Review Task 1.1 - Approved","Compliance Sub-Review Task 1.2 - Approved","Compliance Sub-Review Task 1.3 - Approved","Compliance Sub-Review Task 1.4 - Approved","Compliance Sub-Review Task 1.5 - Approved"};
	
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
	
	/** Variable to hold rejectedBy name */
	private String rejectedByorApprovedBy = "";
	
	/** Variable to hold actionTakenby name */
	private String actionTakenby = "";
	
	/**
	 * Variable to hold email recipients. Recipient of email will always be an
	 * individual
	 */
	private List<String> emailRecipients = null; 
	
	/** Variable to hold CC list. can be a group or an individual */
	private List<String> ccRecipients = null; 
	
	/** Variable to hold CC set. can be a group or an individual */
	private Set<String> ccRecipientsSet = null; 
	
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
	
	/** Variable to hold page absolute path */
	private String absolutePagePath = null;
	
	/** Variable to hold page scheduledDeploymentTime */
	private String scheduledDeploymentTime = null;
	
	/** Rate Type String constant */
	private String rateParam = "ratetype";
	
	/** Variable to hold rates type */
	private String rateType = null;
	
	/** ResourceResolver class reference variable */
	private ResourceResolver resourceResolver=null;
	
	/** WorkItem Metadata class reference variable */
	private MetaDataMap workItemMetaDataMap = null;
	
	/** CurrentDateTime flag class reference variable */
	private boolean isCurrentDateAndTime = false;
	
	/** Session class reference variable */
	private Session session=null;
	
	/** Externalizer class reference variable */
	private Externalizer externalizer=null;
	
	/** UserManager class reference variable */
	private UserManager userManager=null;
	
	private Map<String, String> contributorMap = new LinkedHashMap<String, String>();

	/**
	 * Main method  
	 */
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
		
		try{
			LOGGER.info("Email process start: "+new Date().toString());
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
			session = resourceResolver.adaptTo(Session.class);
			externalizer=resourceResolver.adaptTo(Externalizer.class);
			userManager=resourceResolver.adaptTo(UserManager.class);
			
			workItemMetaDataMap = workItem.getWorkflow().getWorkflowData().getMetaDataMap();
			
			if(workItem.getNode().getMetaDataMap().containsKey("PROCESS_ARGS")){
				wfProcessArg=workItem.getNode().getMetaDataMap().get("PROCESS_ARGS", String.class).toString().trim();
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
			
			if(workItemMetaDataMap.containsKey("absoluteTime")){
				Double timeInMilli = Double.parseDouble(workItemMetaDataMap.get("absoluteTime").toString());
				isCurrentDateAndTime = Boolean.valueOf(workItemMetaDataMap.get("isCurrentDateAndTime").toString());
				Date scheduledDate = new Date(new Double(timeInMilli).longValue());
				scheduledDeploymentTime = formatter.format(scheduledDate);
			}
			
			if("waitfordeployment".equals(wfProcessArg) && isCurrentDateAndTime){
				return;
			}
			
			
			workflowModelTitle=workItem.getWorkflow().getWorkflowModel().getTitle();
			LOGGER.debug("EMAIL WORKFLOW -- WORKFLOW MODEL TITLE :::"+workflowModelTitle); 		
			
			String workflowId = workItem.getWorkflow().getWorkflowModel().getId();
			LOGGER.debug("EMAIL WORKFLOW -- workflowId :::"+workflowId);	 
			
			workflowTitle=workItemMetaDataMap.get("workflowTitle", String.class);
			//logger.info("EMAIL WORKFLOW -- WORKFLOW TITLE :::"+workflowTitle);
			List<Route> routes=workflowSession.getRoutes(workItem,true);
			taskName=routes.get(0).getName();
			pagePath = workItem.getWorkflowData().getPayload().toString();
			Map<String, String> pathDetails= getAbsolutePaths(workItem,pagePath);
			taskItemPath=pathDetails.get(Utils.PAYLOADPATH);
			inboxPath=pathDetails.get(Utils.INBOXPATH);
			workItemPath=pathDetails.get(Utils.WORKITEMPATH);
			productionPagePath=pathDetails.get(Utils.PUBLISHEDPATH);
			if(pathDetails.containsKey(Utils.ISWFPKG)){
				iswfPkg = true;
			}
				
			//logger.info("EMAIL WORKFLOW -- WORKFLOW PAGE PATH :::"+taskItemPath);
			
			
			workflowInitiateDate=formatter.format(workItem.getWorkflow().getTimeStarted());
			
			//LOGGER.info("EMAIL WORKFLOW -- WORKFLOW INITIATED DATE :::"+workflowInitiateDate);
			
			List<HistoryItem> workflowHistory=workflowSession.getHistory(workItem.getWorkflow());
			//LOGGER.info("EMAIL WORKFLOW -- WORKFLOW PREVIOUS COMMENTS START :::");
			
			if(null!=dotcomServiceconfig)
				dotcom_author_group=dotcomServiceconfig.getPropertyValue("dotcom-authors");
			
			StringJoiner listedComments = new StringJoiner("<br/>","","<br/>");
			
			/*Get Workflow Comments in reverse Order*/
			if(StringUtils.isNotBlank(taskName)){ 
				if(taskName.contains("Goto Content Development - Primary Compliance Review - Rejected"))
					actionTakenby = getApprovedByRejectedByFromHistory("Primary Compliance Review Approve/Reject", workflowHistory, workflowSession);
				else if(taskName.contains("Goto Content Development - Primary Legal Review - Rejected"))
					actionTakenby = getApprovedByRejectedByFromHistory("Primary Legal Review Approve/Reject", workflowHistory, workflowSession);
				else if((taskName.contains("Compliance Sub-Review Task 1.1 - Approved") || taskName.contains("Compliance Sub-Review Task 1.1 - Rejected")))
					actionTakenby = getApprovedByRejectedByFromHistory("Compliance Sub-Review 1.1", workflowHistory, workflowSession);
				else if((taskName.contains("Compliance Sub-Review Task 1.2 - Approved") || taskName.contains("Compliance Sub-Review Task 1.2 - Rejected")))
					actionTakenby = getApprovedByRejectedByFromHistory("Compliance Sub-Review 1.2", workflowHistory, workflowSession);
				else if((taskName.contains("Compliance Sub-Review Task 1.3 - Approved") || taskName.contains("Compliance Sub-Review Task 1.3 - Rejected")))
					actionTakenby = getApprovedByRejectedByFromHistory("Compliance Sub-Review 1.3", workflowHistory, workflowSession);
				else if((taskName.contains("Compliance Sub-Review Task 1.4 - Approved") || taskName.contains("Compliance Sub-Review Task 1.4 - Rejected")))
					actionTakenby = getApprovedByRejectedByFromHistory("Compliance Sub-Review 1.4", workflowHistory, workflowSession);
				else if((taskName.contains("Compliance Sub-Review Task 1.5 - Approved") || taskName.contains("Compliance Sub-Review Task 1.5 - Rejected")))
					actionTakenby = getApprovedByRejectedByFromHistory("Compliance Sub-Review 1.5", workflowHistory, workflowSession);
				else if((taskName.contains("Legal Sub-Review Task 2.1 - Approved") || taskName.contains("Legal Sub-Review Task 2.1 - Rejected")))
					actionTakenby = getApprovedByRejectedByFromHistory("Legal Sub-Review 2.1", workflowHistory, workflowSession);
				else if((taskName.contains("Legal Sub-Review Task 2.2 - Approved") || taskName.contains("Legal Sub-Review Task 2.2 - Rejected")))
					actionTakenby = getApprovedByRejectedByFromHistory("Legal Sub-Review 2.2", workflowHistory, workflowSession);
				else if((taskName.contains("Legal Sub-Review Task 2.3 - Approved") || taskName.contains("Legal Sub-Review Task 2.3 - Rejected")))
					actionTakenby = getApprovedByRejectedByFromHistory("Legal Sub-Review 2.3", workflowHistory, workflowSession);
				else if((taskName.contains("Legal Sub-Review Task 2.4 - Approved") || taskName.contains("Legal Sub-Review Task 2.4 - Rejected")))
					actionTakenby = getApprovedByRejectedByFromHistory("Legal Sub-Review 2.4", workflowHistory, workflowSession);
				else if((taskName.contains("Legal Sub-Review Task 2.5 - Approved") || taskName.contains("Legal Sub-Review Task 2.5 - Rejected")))
					actionTakenby = getApprovedByRejectedByFromHistory("Legal Sub-Review 2.5", workflowHistory, workflowSession);
			}
			
			
			Lists.reverse(workflowHistory).stream()
			.filter(getValidData())
			.forEach(history->{
					listedComments.add(history.getWorkItem().getNode().getTitle()+": "
							+history.getComment()+" "+Utils.getUserNamebyId(history.getUserId(), userManager)+"["
							+Utils.CORP_DOMAIN+"\\"+history.getUserId()+":"
							+formatter.format(history.getDate())+"]");
					
			});
			if(workflowId.contains("standard-v2-contentapprovalworkflow") && StringUtils.equalsIgnoreCase(taskName, "Content Development")){
				Lists.reverse(workflowHistory).stream()
				.filter(getValidDataforContributor())
				.forEach(history->{
					try {
						String path="";
						if(history.getWorkItem().getMetaDataMap().containsKey("historyEntryPath")){
							path = history.getWorkItem().getMetaDataMap().get("historyEntryPath").toString();
							path = path +"/workItem/metaData";
						}
						if(workflowSession.adaptTo(Session.class).getNode(path) != null){
							String nodeId = workflowSession.adaptTo(Session.class).getNode(path).getParent().getProperty("nodeId").getString();
							if(!StringUtils.equalsIgnoreCase(nodeId, "node0") && !StringUtils.equalsIgnoreCase(nodeId, "node1") && !StringUtils.equalsIgnoreCase(nodeId, "node2"))
								workflowSession.adaptTo(Session.class).getNode(path).setProperty("oldItem", "true");
						}
						workflowSession.adaptTo(Session.class).save();
					} catch (Exception e) {
						LOGGER.error("error while saving"+e);
					}
				});
			}
			//LOGGER.info(listedComments.toString());
			//LOGGER.info("EMAIL WORKFLOW -- WORKFLOW PREVIOUS COMMENTS END :::");
			
			/*Create previous contributors map*/
			
			//LOGGER.info("EMAIL WORKFLOW -- WORKFLOW CONTRIBUTORS START :::");
			contributorMap.clear();
			if(workflowId.contains("standard-v2-contentapprovalworkflow")){
				contributorMap.put("Start", workItem.getWorkflow().getInitiator());
				for (HistoryItem historyItem : workflowHistory) {
					String path=null;
					if(historyItem.getWorkItem().getMetaDataMap().containsKey("historyEntryPath")){
						path = historyItem.getWorkItem().getMetaDataMap().get("historyEntryPath").toString();
						path = path +"/workItem/metaData";
						if(workflowSession.adaptTo(Session.class).getNode(path) != null){
							if(!workflowSession.adaptTo(Session.class).getNode(path).hasProperty("oldItem")){
								for (String itemType : Arrays.asList(WORKFLOW_USER_STEPS)) {
									if(StringUtils.equalsIgnoreCase(itemType, historyItem.getWorkItem().getNode().getType())){
										contributorMap.put(historyItem.getWorkItem().getNode().getTitle(), historyItem.getUserId());
									}
								}
							}
						}
					}
				}
			}else{
				contributorMap = workflowHistory.stream()
						.filter(getValidDataforContributor())
						.collect(Collectors.toMap(
								history->history.getWorkItem().getNode().getTitle(),
								history -> history.getUserId(),
								 (u, v) -> v,
								 LinkedHashMap::new
								));
			}
			
			if(!Arrays.asList(subTaskNamesApprovedRejected).contains(taskName)){
				routes.stream()
				.forEach(route->{
					contributorMap.put(route.getName(), "Pending");
				});
			}
				
				Map<String,String> finalContributorMap=new LinkedHashMap<>();
				for (Map.Entry<String, String> entry : contributorMap.entrySet()){
					if(!"pending".equalsIgnoreCase(entry.getValue()))
						finalContributorMap.put(entry.getKey(), entry.getValue());
					else{
						finalContributorMap.put(entry.getKey(), entry.getValue());
						break;
					}
				}
				if(Arrays.asList(subTaskNamesApprovedRejected).contains(taskName)){
					boolean activeWorkItemFlag = false;
					WorkItem workItems[] = workflowSession.getActiveWorkItems();
					for (WorkItem workItem2 : workItems) {
						if(StringUtils.equals(workItem2.getWorkflow().getId(), workItem.getWorkflow().getId())){
							finalContributorMap.put(workItem2.getNode().getTitle(), "Pending");
							activeWorkItemFlag=true;
						}
					}
					if(ArrayUtils.isEmpty(workItems) || !activeWorkItemFlag){
						if(StringUtils.contains(taskName, "Compliance Sub-Review Task"))
							finalContributorMap.put("Primary Compliance Review Approve/Reject", "Pending");
						else if(StringUtils.contains(taskName, "Legal Sub-Review Task"))
							finalContributorMap.put("Primary Legal Review Approve/Reject", "Pending");
					}
				}
				
				/*Create List of contributors*/
				
				StringJoiner listedContributors = new StringJoiner("</li>","","</li>");
				
				finalContributorMap.forEach((step,user)->
				
				{
					
						//LOGGER.info("Step name is::"+step+"User name is::"+user);
					listedContributors.add("<li>"
											+step+" : "
											+Utils.getUserNamebyId(user, userManager));
				}
			);
				//LOGGER.info(listedContributors.toString());
				//LOGGER.info("EMAIL WORKFLOW -- WORKFLOW CONTRIBUTORS END :::");
				
				/*Set EMAIL RECIPIENTS*/
				
				if(null!=wfProcessArg){
					setRecipients(workItemMetaDataMap,wfProcessArg, workItem.getWorkflow().getInitiator(),workflowId,finalContributorMap);
				}
				else{
					throw new WorkflowException("No MetaData found. Enter data in arguements section of workflow step");
				}
				
				
				/*SEND EMAIL*/
				if(workflowId.contains("standard-v2-contentapprovalworkflow")){
					if(!taskName.equalsIgnoreCase("Content Development"))
						sendEmail(listedComments.toString(),listedContributors.toString());
				}else
					sendEmail(listedComments.toString(),listedContributors.toString());
				
			
			
		}
		catch(Exception e){
			LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN FROM EXECUTE METHOD: Message: {}, Trace: {}", e.getMessage(),  e);
		}
	}
	
	
	public String getApprovedByRejectedByFromHistory(String taskName, List<HistoryItem> workflowHistory, WorkflowSession workflowSession){
		for(HistoryItem history : workflowHistory){
			String path = null;
			if(history.getWorkItem().getMetaDataMap().containsKey("historyEntryPath")){
				path = history.getWorkItem().getMetaDataMap().get("historyEntryPath").toString();
				path = path +"/workItem/metaData";
				try {
					if(workflowSession.adaptTo(Session.class).getNode(path) != null){
						if(!workflowSession.adaptTo(Session.class).getNode(path).hasProperty("oldItem")){
							if(taskName.equals(history.getWorkItem().getNode().getTitle())){
								rejectedByorApprovedBy = Utils.getUserNamebyId(history.getUserId(), userManager)+"["
									+Utils.CORP_DOMAIN+"\\"+history.getUserId()+"]";
							}
						}
					}
				} catch (Exception e) {
					LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN IN getApprovedByRejectedByFromHistory METHOD :::", e);
				}
			}
		}
		return rejectedByorApprovedBy;
	}
	/**
	 * Sets email recipients
	 * 
	 * @param stage
	 * @param initiator
	 */
	private void setRecipients(MetaDataMap workItemMetaDataMap, String stage, String initiator, String workflowId, Map<String,String> finalContributorMap){
		try {
			String workUser=null;
			if(session.getNode(pagePath).getNode("jcr:content").hasProperty(stage)) {
				workUser=session.getNode(pagePath).getNode("jcr:content").getProperty(stage).getString();
			} else if (stage.equals("contentdevelopmentcheckdone")
					&& finalContributorMap.containsKey("Requester Review")) {
				workUser = finalContributorMap.get("Requester Review");
			}
			else if(stage.equals("waitfordeployment")){
					if(workItemMetaDataMap.containsKey("requesterreviewer")){ 
						workUser=workItemMetaDataMap.get("requesterreviewer").toString();
					}
					else{
						workUser = initiator;
					}
			}
			else {
				workUser = initiator;
			}
			
			Authorizable userAuthorizable=userManager.getAuthorizable(workUser);
			
			String emailId=userAuthorizable.getProperty("./profile/email")!=null?userAuthorizable.getProperty("./profile/email")[0].getString():"cms@SunTrust.com";
			String givenName=userAuthorizable.getProperty("./profile/givenName")!=null?userAuthorizable.getProperty("./profile/givenName")[0].getString():"";
			String familyName=userAuthorizable.getProperty("./profile/familyName")!=null?userAuthorizable.getProperty("./profile/familyName")[0].getString():"";
			
			emailRecipients=new ArrayList<>();
			emailRecipients.add(emailId); //email is mapped for all AD users
			recipientName=givenName+" "+familyName+"["+Utils.CORP_DOMAIN+"\\"+workUser+"]";
			
			Authorizable authorGroupAuthorizable=userManager.getAuthorizable(dotcom_author_group);
			if(null!=authorGroupAuthorizable && authorGroupAuthorizable.isGroup()){
				Group group=(Group)authorGroupAuthorizable;
				Iterator<Authorizable> groupUsers=group.getMembers();
				ccRecipients=new ArrayList<>();
				groupUsers.forEachRemaining(authorizable -> {
				try{
				ccRecipients.add(authorizable.getProperty("./profile/email")!=null?authorizable.getProperty("./profile/email")[0].getString():"cms@SunTrust.com");
				}
				catch(Exception e){
					LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN WHILE ADDING CC USERS:::", e);
				}
				});
			}
			else
				throw new Exception("Dotcom author group not found");
			
			if ((workflowId.contains("standard-content-approval-workflow") || workflowId.contains("standard-v2-contentapprovalworkflow") ||workflowId
					.contains("expedited_content_approval_workflow"))
					&& stage.equals("requesterreviewer") == false) {
				String requester = "";
				if(session.getNode(pagePath).getNode("jcr:content").hasProperty("requesterreviewer")){
					requester = session.getNode(pagePath).getNode("jcr:content").getProperty("requesterreviewer").getString();
					Authorizable requesterAuthorizable = userManager.getAuthorizable(requester);
					ccRecipients
							.add(requesterAuthorizable
									.getProperty("./profile/email") != null ? requesterAuthorizable
									.getProperty("./profile/email")[0]
									.getString() : "cms@SunTrust.com");
				}
			}
			if ((workflowId.contains("standard-v2-contentapprovalworkflow")) && (taskName.equals("Compliance Sub-Review 1.1") || taskName.equals("Compliance Sub-Review 1.2") || taskName.equals("Compliance Sub-Review 1.3") || taskName.equals("Compliance Sub-Review 1.4") || taskName.equals("Compliance Sub-Review 1.5"))) {
				getEmailIdFromProcessArgs("compliancereviewer1");
			}
			if ((workflowId.contains("standard-v2-contentapprovalworkflow")) && (taskName.equals("Legal Sub-Review 2.1") || taskName.equals("Legal Sub-Review 2.2") || taskName.equals("Legal Sub-Review 2.3") || taskName.equals("Legal Sub-Review 2.4") || taskName.equals("Legal Sub-Review 2.5"))) {
				getEmailIdFromProcessArgs("compliancereviewer2");
			}
			if ((workflowId.contains("standard-v2-contentapprovalworkflow")) && (taskName.equals("Compliance Sub-Review Task 1.1 - Approved") || taskName.equals("Compliance Sub-Review Task 1.1 - Rejected") || taskName.equals("Compliance Sub-Review Task 1.2 - Approved") || taskName.equals("Compliance Sub-Review Task 1.2 - Rejected") || taskName.equals("Compliance Sub-Review Task 1.3 - Approved") || taskName.equals("Compliance Sub-Review Task 1.3 - Rejected") || taskName.equals("Compliance Sub-Review Task 1.4 - Approved") || taskName.equals("Compliance Sub-Review Task 1.4 - Rejected") || taskName.equals("Compliance Sub-Review Task 1.5 - Approved") || taskName.equals("Compliance Sub-Review Task 1.5 - Rejected"))) {
				getEmailIdFromProcessArgs("11compliancereviewer");
				getEmailIdFromProcessArgs("12compliancereviewer");
				getEmailIdFromProcessArgs("13compliancereviewer");
				getEmailIdFromProcessArgs("14compliancereviewer");
				getEmailIdFromProcessArgs("15compliancereviewer");
			} 
			if ((workflowId.contains("standard-v2-contentapprovalworkflow")) && (taskName.equals("Legal Sub-Review Task 2.1 - Approved") || taskName.equals("Legal Sub-Review Task 2.1 - Rejected") || taskName.equals("Legal Sub-Review Task 2.2 - Approved") || taskName.equals("Legal Sub-Review Task 2.2 - Rejected") || taskName.equals("Legal Sub-Review Task 2.3 - Approved") || taskName.equals("Legal Sub-Review Task 2.3 - Rejected") || taskName.equals("Legal Sub-Review Task 2.4 - Approved") || taskName.equals("Legal Sub-Review Task 2.4 - Rejected") || taskName.equals("Legal Sub-Review Task 2.5 - Approved") || taskName.equals("Legal Sub-Review Task 2.5 - Rejected"))) {
				getEmailIdFromProcessArgs("21compliancereviewer");
				getEmailIdFromProcessArgs("22compliancereviewer");
				getEmailIdFromProcessArgs("23compliancereviewer");
				getEmailIdFromProcessArgs("24compliancereviewer");
				getEmailIdFromProcessArgs("25compliancereviewer");
			} 
			
		} catch (Exception e) {
			LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN IN SET RECIPIENTS METHOD :::", e);
		}
		
	}
	@SuppressWarnings("unchecked")
	private void getEmailIdFromProcessArgs(String processArgs){
		String reviewer = "";
		try {
			if(session.getNode(pagePath).getNode("jcr:content").hasProperty(processArgs)){
				reviewer = session.getNode(pagePath).getNode("jcr:content").getProperty(processArgs).getString();
				Authorizable requesterAuthorizable = userManager.getAuthorizable(reviewer);
				ccRecipients
						.add(requesterAuthorizable
								.getProperty("./profile/email") != null ? requesterAuthorizable
								.getProperty("./profile/email")[0]
								.getString() : "cms@SunTrust.com");
				ccRecipientsSet = new LinkedHashSet<String>();
				ccRecipients = ListUtils.subtract(ccRecipients, emailRecipients);
				ccRecipientsSet.addAll(ccRecipients);
				ccRecipients.clear();
				ccRecipients.addAll(ccRecipientsSet);
			}
		} catch (IllegalStateException | RepositoryException e) {
			LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN IN getEmailIdFromProcessArgs METHOD :::", e);
		}
		
	}
	/**
	 * Get approval work iitem
	 * 
	 * @return
	 */
	private Predicate<HistoryItem> getValidData(){
		return history->
		Arrays.asList(WORKFLOW_USER_STEPS).contains(history.getWorkItem().getNode().getType()) &&
		null!=history.getWorkItem().getNode().getTitle() && 
		StringUtils.isNotBlank(history.getComment());
		
	}
	
	/**
	 * Returns approval task title
	 * 
	 * @return
	 */
	private Predicate<HistoryItem> getValidDataforContributor(){
		return history->
		Arrays.asList(WORKFLOW_USER_STEPS).contains(history.getWorkItem().getNode().getType()) &&
		null!=history.getWorkItem().getNode().getTitle();
		
	}
	
	/**
	* Returns page absolute path
	* 
	 * @param workItem
	* @param payloadPath
	* @return
	* @throws Exception
	*/
	private Map<String, String> getAbsolutePaths(WorkItem workItem, String payloadPath) throws Exception{
	   String inboxPath=null;
	   StringBuffer workItemBuffer=new StringBuffer();
	   Map<String, String> absolutePaths;
	   Node payLoadNode=session.getNode(payloadPath).getNode("jcr:content");
	   
	   NodeType[] nodeType=payLoadNode.getMixinNodeTypes();
	   boolean iswfPkg=Arrays.asList(nodeType).stream().anyMatch(node -> "vlt:Package".equalsIgnoreCase(node.getName()));
	   
	   if(iswfPkg){
	         //absolutePaths=getWorkflowPkgPaths(payLoadNode);
	         absolutePaths = Utils.getWorkflowPkgPaths(payLoadNode, resourceCollectionManager, externalizer, resourceResolver, dotcomServiceconfig
	               .getPropertyArray("canonical.urls"));
	   }else{
	      absolutePaths = new HashMap<>();
	   }
	   /** Resetting variable */
	   rateType = null;     
	   if(null != payLoadNode && payLoadNode.hasProperty(rateParam)){
	      rateType = payLoadNode.getProperty(rateParam).getValue().toString();            
	   }
	   LOGGER.debug("Rate type parameter from email  :"+rateType);
	   if(rateType!=null && !rateType.isEmpty() && payloadPath.toLowerCase().contains(".csv")){
	      if(rateType.equalsIgnoreCase("cdrates")){              
	         absolutePagePath=externalizer.authorLink(resourceResolver, dotcomServiceconfig.getPropertyValue("cdrates-page-path"));
	         productionPagePath=externalizer.publishLink(resourceResolver, dotcomServiceconfig.getPropertyValue("cdrates-page-path"));
	      }else if(rateType.equalsIgnoreCase("equityrates")){
	         absolutePagePath=externalizer.authorLink(resourceResolver, dotcomServiceconfig.getPropertyValue("equityrates-page-path"));
	         productionPagePath=externalizer.publishLink(resourceResolver, dotcomServiceconfig.getPropertyValue("equityrates-page-path"));        
	      }else{
	    	  LOGGER.info("Rate type parameter is not passing properly");
	      }
	   }else{
	      if(payloadPath.toLowerCase().contains("content/suntrust")){
	         PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
	         Page pg = pageManager.getPage(payloadPath);
	         if(pg != null && pg.getProperties().containsKey("sling:resourceType"))
	         {
	            String resourceType = (String)pg.getProperties().get("sling:resourceType");
	            if(resourceType.contains("dotcom/components/page/resourcecentertemplate"))
	            {
	               absolutePagePath=externalizer.authorLink(resourceResolver, Utils.getPrimaryWrapperPath(payloadPath, resourceResolver)+".html?wcmmode=disabled");
	            }
	            else
	            {
	               absolutePagePath=externalizer.authorLink(resourceResolver, payloadPath+".html?wcmmode=disabled");
	            }
	         }
	         //productionPagePath=externalizer.publishLink(resourceResolver, payloadPath+".html");
	         productionPagePath = externalizer.publishLink(resourceResolver,
	               Utils.getCanonicalUrl(dotcomServiceconfig
	                     .getPropertyArray("canonical.urls"),
	                     payloadPath, resourceResolver));
	      } else {
	         absolutePagePath=externalizer.authorLink(resourceResolver, payloadPath);
	         productionPagePath=externalizer.publishLink(resourceResolver, payloadPath);
	      }
	      LOGGER.info("Rate type parameter should not be empty or null");
	   }
	      environmentDetails=externalizer.authorLink(resourceResolver, "");
	      inboxPath=externalizer.authorLink(resourceResolver, "/aem/inbox");
	      workItemBuffer.append(externalizer.authorLink(resourceResolver, "/mnt/overlay/cq/inbox/content/inbox/details.html?item="));
	   //}
	   
	   /*create future work item inbox link*/
	   String[] workSplitId=workItem.getId().split("_",3);
	   int nodeNumber=Integer.parseInt(workSplitId[1].substring(4));
	   String nodeName=workSplitId[2];
	   String workflowId=workItem.getWorkflow().getId();
	   
	   workItemBuffer.append(workflowId+"/workItems/node"+Integer.toString(nodeNumber+1)+"_"+nodeName);
	   workItemBuffer.append("&type=");
	   workItemBuffer.append(workItem.getItemType());
	   workItemBuffer.append("&_charset_=utf-8");
	   
	   if(!absolutePaths.containsKey(Utils.PAYLOADPATH)){
	      absolutePaths.put(Utils.PAYLOADPATH, absolutePagePath);
	      absolutePaths.put(Utils.PUBLISHEDPATH, productionPagePath);
	   }
	   else{
	      absolutePaths.put(Utils.ISWFPKG, "true");
	      absolutePaths.put(Utils.WFPKGPATH,absolutePagePath);
	   }
	   absolutePaths.put(Utils.INBOXPATH, inboxPath);
	   absolutePaths.put(Utils.WORKITEMPATH, workItemBuffer.toString());
	   absolutePaths.put(Utils.ENV_DETAILS, environmentDetails);
	   
	   return absolutePaths;
	}

	
	/**
	 * Send email to recipients with template based on task
	 * 
	 * @param listedComments
	 * @param listedContributors
	 */
	private void sendEmail(String listedComments, String listedContributors){
		
		Map<String,String> emailParams=new HashMap<>();
		String subject = workflowTitle+" - "+taskName;
		String template = NOTIFICATION_TEMPLATEPATH;
		String mailContent = MAIL_BODY_GENERAL; 
		String mailCta = MAIL_CTA_GENERAL; 
		String customStyle = DEFAULT_MAIL_STYLE;
		if ("STcom Unpublish Workflow".equals(workflowModelTitle)
				|| "Quick Unpublish Workflow".equals(workflowModelTitle)) {
			template = UNP_NOTIFICATION_TEMPLATEPATH;
			if (wfProcessArg.equals("contentdevelopmentcheckdone")) {
				subject = "Unpublished from Production: " + productionPagePath;
				if (iswfPkg) {
					subject = "Unpublished from Production";
				}
				template = UNP_COMPLETION_TEMPLATEPATH;
			}
			if (wfProcessArg.equals("waitfordeployment")) {
				subject = "Scheduled for Deployment: " + workflowTitle;
				mailContent = MAIL_BODY_WAIT_FOR_UNPUB_DEPLOYMENT;
				customStyle = SCHEDULED_DEPLOYMENT_MAIL_STYLE;
				template = NOTIFICATION_TEMPLATEPATH;
			}

		} else if (wfProcessArg.equals("contentdevelopmentcheckdone")) {
			subject = "Deployed to Production: " + productionPagePath;
			if (iswfPkg)
				subject = "Deployed to Production";
			template = COMPLETION_TEMPLATEPATH;
		}
		else if (wfProcessArg.equals("waitfordeployment")) {
			subject = "Scheduled for Deployment: " + workflowTitle;
			mailContent = MAIL_BODY_WAIT_FOR_DEPLOYMENT;
			customStyle = SCHEDULED_DEPLOYMENT_MAIL_STYLE;
			template = NOTIFICATION_TEMPLATEPATH;
		}
		
		LOGGER.info("taskName " + taskName);
		if(StringUtils.isNotBlank(taskName) && taskName.contains("Primary Compliance Review Approve/Reject")){
			mailContent=MAIL_BODY_COMPLIANCE_APPROVEREJECT;
		}else if(StringUtils.isNotBlank(taskName) && taskName.contains("Primary Legal Review Approve/Reject")){
			mailContent=MAIL_BODY_LEGAL_APPROVEREJECT;
		}else if(StringUtils.isNotBlank(taskName) && taskName.contains("Select Additional Legal Reviewers")){
			mailContent=MAIL_BODY_LEGAL_ASSIGN_SUBREVIEW;
			mailCta=MAIL_CTA_REVIEWER_SELECTION;
		}else if(StringUtils.isNotBlank(taskName) && taskName.contains("Select Additional Compliance Reviewers")){
			mailContent=MAIL_BODY_COMPLIANCE_ASSIGN_SUBREVIEW;
			mailCta=MAIL_CTA_REVIEWER_SELECTION;
		}else if(StringUtils.isNotBlank(taskName) && taskName.contains("Goto Content Development - Primary Legal Review - Rejected")){
			getEmailIdFromProcessArgs("compliancereviewer2");
			subject = subject + "- Rejected";
			taskName = "Primary Legal Review - Rejected";
			subject = workflowTitle+" - "+taskName;
			listedContributors = listedContributors.replace("Goto Content Development - Primary Legal Review - Rejected", "Content Development");
			customStyle = MAIL_CUSTOM_STYLE;
			mailContent = MAIL_BODY_PRIMARY_LEGAL_REJECTED.concat("<p><br/><b><span style='color:#1F497D'>Rejected By</span></b><span style='font-family: 'Trebuchet MS', sans-serif; margin-right:30px;'>:"+actionTakenby+" </span></p>");
		}else if(StringUtils.isNotBlank(taskName) && taskName.contains("Goto Content Development - Primary Compliance Review - Rejected")){
			getEmailIdFromProcessArgs("compliancereviewer1");
			taskName = "Primary Compliance Review - Rejected";
			subject = workflowTitle+" - "+taskName;
			listedContributors = listedContributors.replace("Goto Content Development - Primary Compliance Review - Rejected", "Content Development");
			customStyle = MAIL_CUSTOM_STYLE;
			mailContent = MAIL_BODY_PRIMARY_COMPLIANCE_REJECTED.concat("<p><br/><b><span style='color:#1F497D'>Rejected By</span></b><span style='font-family: 'Trebuchet MS', sans-serif; margin-right:30px;'>:"+actionTakenby+" </span></p>");
		} else if(StringUtils.isNotBlank(taskName) && taskName.contains("Compliance Sub-Review Task 1.1 - Approved") || taskName.contains("Compliance Sub-Review Task 1.2 - Approved") || taskName.contains("Compliance Sub-Review Task 1.3 - Approved") || taskName.contains("Compliance Sub-Review Task 1.4 - Approved") || taskName.contains("Compliance Sub-Review Task 1.5 - Approved")){
			customStyle = MAIL_CUSTOM_STYLE;
			mailContent = MAIL_BODY_COMPLIANCE_SUB_REVIEW_APPROVED.concat("<p><br/><b><span style='color:#1F497D'>Approved By</span></b><span style='font-family: 'Trebuchet MS', sans-serif; margin-right:30px;'>:"+actionTakenby+" </span></p>");
		} else if(StringUtils.isNotBlank(taskName) && taskName.contains("Compliance Sub-Review Task 1.1 - Rejected") || taskName.contains("Compliance Sub-Review Task 1.2 - Rejected") || taskName.contains("Compliance Sub-Review Task 1.3 - Rejected") || taskName.contains("Compliance Sub-Review Task 1.4 - Rejected") || taskName.contains("Compliance Sub-Review Task 1.5 - Rejected")){
			customStyle = MAIL_CUSTOM_STYLE;
			mailContent = MAIL_BODY_COMPLIANCE_SUB_REVIEW_REJECTED.concat("<p><br/><b><span style='color:#1F497D'>Rejected By</span></b><span style='font-family: 'Trebuchet MS', sans-serif; margin-right:30px;'>:"+actionTakenby+" </span></p>");
		} else if(StringUtils.isNotBlank(taskName) && taskName.contains("Legal Sub-Review Task 2.1 - Approved") || taskName.contains("Legal Sub-Review Task 2.2 - Approved") || taskName.contains("Legal Sub-Review Task 2.3 - Approved") || taskName.contains("Legal Sub-Review Task 2.4 - Approved") || taskName.contains("Legal Sub-Review Task 2.5 - Approved")){
			customStyle = MAIL_CUSTOM_STYLE;
			mailContent = MAIL_BODY_LEGAL_SUB_REVIEW_APPROVED.concat("<p><br/><b><span style='color:#1F497D'>Approved By</span></b><span style='font-family: 'Trebuchet MS', sans-serif; margin-right:30px;'>:"+actionTakenby+" </span></p>");
		} else if(StringUtils.isNotBlank(taskName) && taskName.contains("Legal Sub-Review Task 2.1 - Rejected") || taskName.contains("Legal Sub-Review Task 2.2 - Rejected") || taskName.contains("Legal Sub-Review Task 2.3 - Rejected") || taskName.contains("Legal Sub-Review Task 2.4 - Rejected") || taskName.contains("Legal Sub-Review Task 2.5 - Rejected")){
			customStyle = MAIL_CUSTOM_STYLE;
			mailContent = MAIL_BODY_LEGAL_SUB_REVIEW_REJECTED.concat("<p><br/><b><span style='color:#1F497D'>Rejected By</span></b><span style='font-family: 'Trebuchet MS', sans-serif; margin-right:30px;'>:"+actionTakenby+" </span></p>");
		} else if(StringUtils.isNotBlank(taskName) && taskName.contains("Compliance Sub-Review 1.1") || taskName.contains("Compliance Sub-Review 1.2") || taskName.contains("Compliance Sub-Review 1.3") || taskName.contains("Compliance Sub-Review 1.4") || taskName.contains("Compliance Sub-Review 1.5")){
			mailContent = MAIL_BODY_COMPLIANCE_SUB_REVIEW_ASSIGNED;
		} else if(StringUtils.isNotBlank(taskName) && taskName.contains("Legal Sub-Review 2.1") || taskName.contains("Legal Sub-Review 2.2") || taskName.contains("Legal Sub-Review 2.3") || taskName.contains("Legal Sub-Review 2.4") || taskName.contains("Legal Sub-Review 2.5")){
			mailContent = MAIL_BODY_LEGAL_SUB_REVIEW_ASSIGNED;
		} 
		LOGGER.info("Custom Style ==>"+customStyle);
		emailParams.put("subject", subject);
		emailParams.put("senderEmailAddress","cms@SunTrust.com");  
		emailParams.put("senderName","Suntrust CMS");
		emailParams.put("mailContent",mailContent);
		emailParams.put("mailCta",mailCta);
		emailParams.put("customStyle",customStyle);
		emailParams.put("scheduledTime",scheduledDeploymentTime);
		emailParams.put("productionPagePath",productionPagePath);
		emailParams.put("pageLink", taskItemPath);
		emailParams.put("workflowModelTitle", workflowModelTitle);
		emailParams.put("workflowTitle", workflowTitle);
		emailParams.put("task", taskName);
		emailParams.put("inboxPath", inboxPath);
		emailParams.put("workItemPath", workItemPath);
		emailParams.put("recipientName",recipientName);
		emailParams.put("workFlowDate", workflowInitiateDate);
		emailParams.put("previousComments", listedComments);
		emailParams.put("contributors", listedContributors);
		emailParams.put("environment", environmentDetails);
		LOGGER.info("Email process end and SMTP start: "+new Date().toString());
		emailService.sendEmail(template, emailParams, emailRecipients, ccRecipients);
		LOGGER.info("SMTP completed: "+new Date().toString());
		
	}
	
}
