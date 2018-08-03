package com.suntrust.dotcom.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.day.cq.commons.Externalizer;
import com.suntrust.dotcom.config.STELConfigService;
import com.suntrust.dotcom.services.EmailService;


/**
 * RPX util code provides the common RPX related utility methods which
 * can be used in other classes based on the needs.
 */

public class RPXUtils{
	/** Logger instance variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(RPXUtils.class);
	/**
	 * Method to send email notification for RPX sync process
	 * @param resolver
	 * @param workItem
	 * @param workflowSession
	 * @param stelConfigService
	 * @param emailService
	 * @param mailTemplate
	 * @param mailBody
	 * @param toRecipients
	 * @param ccRecipients
	 * @return boolean
	 */
	public static boolean sendNotification(ResourceResolver resolver,WorkItem workItem,WorkflowSession workflowSession,STELConfigService stelConfigService,
			EmailService emailService,String mailTemplate, String subject,String mailBody,List<String> toRecipients,List<String> ccRecipients) {
		Externalizer externalizer = resolver.adaptTo(Externalizer.class);
		UserManager userManager = resolver.adaptTo(UserManager.class);
		String workflowInitiator=workItem.getWorkflow().getInitiator();
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");			
		String jobInitiatedDate=formatter.format(workItem.getWorkflow().getTimeStarted());
		
      	Map<String, String> userMap=getUserProperties(userManager,workItem.getWorkflow().getInitiator());		
		workflowInitiator=userMap.get("givenName")+" "+userMap.get("familyName")+"["+Utils.CORP_DOMAIN+"\\"+workflowInitiator+"]";
		
		Map<String,String> reviewerTaskMap=getReviewHistory(workItem,workflowSession);
		String workflowReviewer=reviewerTaskMap.get("reviewer");
		if(workflowReviewer!=null && workflowReviewer.isEmpty()==false){
			Map<String, String> reviewerMap=getUserProperties(userManager,workflowReviewer);		
			workflowReviewer=reviewerMap.get("givenName")+" "+reviewerMap.get("familyName")+"["+Utils.CORP_DOMAIN+"\\"+workflowReviewer+"]";
		}
		String jobDescription=workItem.getWorkflow().getWorkflowData().getMetaDataMap().get("workflowTitle", String.class);
		String reviewComment=reviewerTaskMap.get("comment");
		
		Map<String, String> emailParams = new HashMap<>();
		emailParams.put("senderEmailAddress",
				stelConfigService.getPropertyValue("rpx.sender.emailid"));
		emailParams.put("senderName",
				stelConfigService.getPropertyValue("rpx.sender.displayname"));
		emailParams.put("subject",stelConfigService.getPropertyValue(subject));
		emailParams.put("environment",
				externalizer.authorLink(resolver, "").toString());	
		emailParams.put("publishenvironment",
				externalizer.publishLink(resolver, "").toString());	
		emailParams.put("mailbody", stelConfigService.getPropertyValue(mailBody));
		emailParams.put("workflowDescription", workItem.getWorkflow().getWorkflowModel().getTitle());
		emailParams.put("jobDescription", jobDescription);
		emailParams.put("jobInitiatedDate", jobInitiatedDate);
		emailParams.put("jobInitiator", workflowInitiator);
		emailParams.put("workItemPath", getWorkItemReviewLink(workItem,resolver));
		emailParams.put("reviewer", workflowReviewer);	
		emailParams.put("comment",reviewComment );
		if(reviewComment==null || reviewComment.isEmpty()){
			emailParams.put("hideReviewComment",  "display:none;height:0");
		}else{
			emailParams.put("hideReviewComment",  "display:block;height:0");
		}
		if(jobDescription==null ||jobDescription.isEmpty()){
			emailParams.put("hideInitiatorComment",  "display:none;height:0");
		}else{
			emailParams.put("hideInitiatorComment",  "display:block;height:0");
		}
			
		if (stelConfigService.getPropertyValue("rpx.send.mail").equalsIgnoreCase(
				"yes")) {
			emailService.sendEmail(stelConfigService.getPropertyValue(mailTemplate), emailParams,
					toRecipients, ccRecipients);
		}
		return true;
	}
	/**
	 * Method to get the user properties
	 * @param userManager
	 * @param workflowInitiator
	 * @return map
	 */
	public static Map<String, String> getUserProperties(UserManager userManager,String workflowInitiator){		
		Map<String, String> userMap=new HashMap<String, String>();
		Authorizable userAuthorizable;
		try {
			userAuthorizable = userManager.getAuthorizable(workflowInitiator);			
			userMap.put("email",userAuthorizable.getProperty("./profile/email")!=null?userAuthorizable.getProperty("./profile/email")[0].getString():"someone@SunTrust.com");
			userMap.put("givenName",userAuthorizable.getProperty("./profile/givenName")!=null?userAuthorizable.getProperty("./profile/givenName")[0].getString():"");
			userMap.put("familyName",userAuthorizable.getProperty("./profile/familyName")!=null?userAuthorizable.getProperty("./profile/familyName")[0].getString():"");
		} catch (RepositoryException e) {
			LOGGER.error("EXCEPTION THROWN FROM getWorkflowInitiatorProperties METHOD MESSAGE: {} TRACE: {}",e.getMessage(),e);
		}
		return userMap;
	}
	/**
	 * Method to get the work item review link
	 * @param workItem
	 * @param resourceResolver
	 * @return string
	 */
	public static String getWorkItemReviewLink(WorkItem workItem, ResourceResolver resourceResolver){		
		   StringBuffer workItemBuffer=new StringBuffer();
		   Externalizer externalizer=resourceResolver.adaptTo(Externalizer.class);
		   workItemBuffer.append(externalizer.authorLink(resourceResolver, "/mnt/overlay/cq/inbox/content/inbox/details.html?item="));
		   String[] workSplitId=workItem.getId().split("_",3);
		   int nodeNumber=Integer.parseInt(workSplitId[1].substring(4));
		   String nodeName=workSplitId[2];
		   String workflowId=workItem.getWorkflow().getId();			   
		   workItemBuffer.append(workflowId+"/workItems/node"+Integer.toString(nodeNumber+1)+"_"+nodeName);
		   workItemBuffer.append("&type=");
		   workItemBuffer.append(workItem.getItemType());
		   workItemBuffer.append("&_charset_=utf-8");		   
		   return workItemBuffer.toString();
	}
	/**
	 * Method to get the workitem review link for approval
	 * @param workItem
	 * @param workflowSession
	 * @return map
	 */
	public static Map<String,String> getReviewHistory(WorkItem workItem,WorkflowSession workflowSession){		
		 List<HistoryItem> historyItem;
		 Map<String,String> historyMap=new HashMap<String, String>();		 
		try {
			historyItem = workflowSession.getHistory(workItem.getWorkflow());
	        int listSize = historyItem.size();
	        HistoryItem lastWorkItem = historyItem.get(listSize-1);
	        HistoryItem previousItem =lastWorkItem.getPreviousHistoryItem();
	        if(previousItem!=null){	        	
	 			historyMap.put("comment", previousItem.getComment());
	        	historyMap.put("reviewer", previousItem.getUserId());
	        }   
	             
		} catch (WorkflowException e) {			
			e.printStackTrace();
		}
	        return historyMap;
	}
	
	/**
	 * Method to get all users from group
	 * @param userManager
	 * @param groupName
	 * @return list
	 */
	public static List<String> getUsersByGroup(UserManager userManager,String groupName){
		List<String>emailRecipients=new ArrayList<String>();
		try {			
			Authorizable authorGroupAuthorizable = userManager.getAuthorizable(groupName);
			if(null != authorGroupAuthorizable && authorGroupAuthorizable.isGroup()){
				Group group = (Group)authorGroupAuthorizable;
				Iterator<Authorizable> groupUsers = group.getMembers();			
				groupUsers.forEachRemaining(authorizable -> {
				try{
					emailRecipients.add(authorizable.getProperty("./profile/email") != null? authorizable.getProperty("./profile/email")[0].getString() : "someone@SunTrust.com");
				}
				catch(Exception e){
					LOGGER.error("Exception thrown adding the users into list {} TRACE: {}",e.getMessage(),e);
				}
				});
			}
			else
				throw new Exception(groupName+" group not found");
			
		} catch (Exception e) {
			LOGGER.error("Exception thrown in retrieving the users {} TRACE: {}",e.getMessage(),e);
			return null;
		}
		LOGGER.debug("Email recipients:"+emailRecipients.toString());
		return emailRecipients;
	}
	/**
	 * Method to get date and time in 12/6/2017 at 8:04 PM
	 * @param datePattern
	 * @param timePattern
	 * @return String
	 */
	public static String getRPXImportDateNTitme(String datePattern, String timePattern){
		  DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern(datePattern); 
		  String dateString = dateFormater.format(LocalDateTime.now());
		  DateTimeFormatter timeFormater = DateTimeFormatter.ofPattern(timePattern);   
		  String timeString = timeFormater.format(LocalDateTime.now());	 
		  //return date and time as 12/6/2017 at 8:04 PM ET
		  LOGGER.debug("Effective date"+dateString+" at "+timeString+" ET");
		  return dateString+" at "+timeString+" ET";
	  }
	}