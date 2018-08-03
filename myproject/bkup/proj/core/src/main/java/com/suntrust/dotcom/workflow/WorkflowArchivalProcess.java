package com.suntrust.dotcom.workflow;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowNode;
import com.day.cq.workflow.collection.ResourceCollection;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.day.cq.workflow.collection.ResourceCollectionUtil;
import com.suntrust.dotcom.config.STELConfigService;
import com.suntrust.dotcom.config.SuntrustPDFService;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.utils.Utils;


@Component
@Service
@Properties({ @Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Workflow History/Archival Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust Workflow History/Archival Service") })
public class WorkflowArchivalProcess implements WorkflowProcess {
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	@Reference
	private SlingSettingsService slingSettings;
	@Reference
	private ResourceCollectionManager resourceCollectionManager;
	@Reference
	private SuntrustPDFService pdfService;
	@Reference
	private EmailService emailService;
	@Reference
	STELConfigService stelConfigService;

	private static final String[] DEFAULT_WF_PACKAGE_TYPES = { "cq:Page", "cq:PageContent" };
	private String workflowInitiateDate = null;
	private String xmlOutputDirectory = null;
	Map<String, String> fileNamePathsMap = new HashMap<>();
	SimpleDateFormat folderFormat = new SimpleDateFormat("MMddyyyy");
	SimpleDateFormat fileFormat = new SimpleDateFormat("HHMMSSss");
	
	/** ResourceResolver class reference variable */
	private ResourceResolver resourceResolver = null;
	
	/** Session class reference variable */
	private Session jcrSession = null;
	
	private static final String WORKFLOW_ARCHIVAL_FOLDER = "workflow";
	
	List<File> outputFile = null;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowArchivalProcess.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workSession, MetaDataMap metaDataMap)
			throws WorkflowException {

		try {
			LOGGER.info("inside WorkflowArchivalProcess *** ");
			
			xmlOutputDirectory = slingSettings.getAbsolutePathWithinSlingHome(pdfService.getPdfOutputPath()) + "/" + WORKFLOW_ARCHIVAL_FOLDER;
			jcrSession = workSession.adaptTo(Session.class);
			
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
			workflowInitiateDate = formatter.format(workItem.getWorkflow().getTimeStarted());
			
			DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder icBuilder;

	        icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();

            Element mainRootElement = doc.createElement("Workflow");
            doc.appendChild(mainRootElement);
            
            Element workflowDate = doc.createElement("Workflow_Date");
            workflowDate.appendChild(doc.createTextNode(workflowInitiateDate));
            mainRootElement.appendChild(workflowDate);
            
            String workflowTitle = workItem.getWorkflow().getWorkflowData().getMetaDataMap().get("workflowTitle", String.class);
            if(StringUtils.isNotBlank(workflowTitle))
            mainRootElement.appendChild(createElement("Workflow_Title",workflowTitle, doc));
            
            String jobId = workItem.getWorkflow().getId();
            if(StringUtils.isNotBlank(jobId))
            mainRootElement.appendChild(createElement("JobId",jobId, doc));
            
            final String initiator = workItem.getWorkflow().getInitiator();
            if(StringUtils.isNotBlank(initiator)) {
            	resourceResolver = getResourceResolver(jcrSession);
	            UserManager manager = resourceResolver.adaptTo(UserManager.class);
	            Authorizable authorizable = manager.getAuthorizable(initiator);
            	String givenName = authorizable.getProperty("./profile/givenName") != null ? 
            			authorizable.getProperty("./profile/givenName")[0].getString().concat(", ") : "";
    			String familyName = authorizable.getProperty("./profile/familyName") != null ? 
    					authorizable.getProperty("./profile/familyName")[0].getString() : "";
    			
    			String initiatorName = givenName+familyName+"("+initiator+")";
	            mainRootElement.appendChild(createElement("Job_Initiator",initiatorName, doc));
	            
	            Value[] email = authorizable.getProperty("./profile/email");
	            if(email != null)
	            mainRootElement.appendChild(createElement("Job_Initiator_Email_Id",email[0].getString(), doc));
            }
            
            String jobName = workItem.getWorkflow().getWorkflowModel().getTitle();
            if(StringUtils.isNotBlank(jobName))
            mainRootElement.appendChild(createElement("Workflow_Name",jobName, doc));
            
            String payLoadPath = workItem.getWorkflowData().getPayload().toString();
            
            if(StringUtils.isNotBlank(payLoadPath) && !StringUtils.equals(payLoadPath, ".")) {
            	
	            Node payLoadNode = jcrSession.getNode(payLoadPath).getNode("jcr:content");
	    		
	    		NodeType[] nodeType = payLoadNode.getMixinNodeTypes();
	    		boolean iswfPkg = Arrays.asList(nodeType).stream().anyMatch(node -> "vlt:Package".equalsIgnoreCase(node.getName()));
	    		
	    		if(iswfPkg) {
	    			LOGGER.info("payLoad is a wf pkg");
	    			List<String> wfPkgPaths = getWorkflowPackagePaths(payLoadNode);
	    			Element resourcesElem = doc.createElement("Resources");
	    			outputFile = new ArrayList<File>();
	    			
	    			for(String wfResourcePath : wfPkgPaths) {
	    				Element resElem = createElement("Resource", wfResourcePath, doc);
	    				resourcesElem.appendChild(resElem);
	    				outputFile.add((File) getFileStructure(wfResourcePath, "xml").get("fileData"));
	    			}
	    			mainRootElement.appendChild(resourcesElem);
	    		} else {
	    			outputFile = new ArrayList<File>();
	    			LOGGER.info("payLoad is not a wf pkg");
	    			mainRootElement.appendChild(createElement("resource",payLoadPath, doc));
	    			if(payLoadNode.hasProperty("cq:lastReplicated")) {
	    				mainRootElement.appendChild(createElement("Change_Type", "Content update in existing page and activation", doc));
	    			} else {
	    				mainRootElement.appendChild(createElement("Change_Type", "New page activation", doc));
	    			}
	    			outputFile.add((File) getFileStructure(payLoadPath, "xml").get("fileData"));
	    		}
            
	            HistoryItem previousHistoryItem;
	            String ownerComment;
	            String stepType;
	            String currentAssignee = "";
	            String activateLaterTime = null;
	
	            List<HistoryItem> history = workSession.getHistory(workItem.getWorkflow());
	            Iterator<HistoryItem> historyIterator = history.iterator();
	            Element historyElem = doc.createElement("History");
	            Element activateLater = null;
	            String absTime = null;
	            
	            while (historyIterator.hasNext()) {
	
	                previousHistoryItem = historyIterator.next();
	                stepType = previousHistoryItem.getWorkItem().getNode().getType();
	                Element node = doc.createElement("Step");
	
	                if (stepType != null && stepType.equals(WorkflowNode.TYPE_PARTICIPANT) || stepType.equals(WorkflowNode.TYPE_DYNAMIC_PARTICIPANT)) {
	                	ownerComment = previousHistoryItem.getWorkItem().getMetaDataMap().get("comment", String.class);
	                	if(StringUtils.isNotBlank(ownerComment))
	                	node.appendChild(createElement("Comment",ownerComment, doc));
	                    currentAssignee = previousHistoryItem.getWorkItem().getCurrentAssignee();
	                    String wfProcessArg;
	                    if(workItem.getNode().getMetaDataMap().containsKey("PROCESS_ARGS")){
	    					wfProcessArg = workItem.getNode().getMetaDataMap().get("PROCESS_ARGS", String.class).toString().trim();
	    					if(StringUtils.containsIgnoreCase(wfProcessArg, "compliance"))
	    						mainRootElement.appendChild(createElement("Compliance_Approval",currentAssignee, doc));
	    					if(StringUtils.containsIgnoreCase(wfProcessArg, "legal"))
	    						mainRootElement.appendChild(createElement("Legal_Approval",currentAssignee, doc));
	    				}
	                }
	                
	                
		            absTime = previousHistoryItem.getWorkItem().getNode().getMetaDataMap().get("absoluteTime", String.class);
	            
		            if(StringUtils.isNotBlank(absTime)) {
		            	activateLater = doc.createElement("Scheduled_Activation");
		            	Long absoluteTime = Long.valueOf(absTime);
		                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
						LOGGER.info("\n dateFormatter.format calendar.getTime() : " + dateFormatter.format(absoluteTime*1000));
						activateLaterTime = dateFormatter.format(absoluteTime*1000);
						activateLater.appendChild(createElement("Scheduled_Activation_Time",activateLaterTime, doc));
						if(StringUtils.isNotBlank(previousHistoryItem.getWorkItem().getNode().getTitle()))
						activateLater.appendChild(createElement("Title",previousHistoryItem.getWorkItem().getNode().getTitle(), doc));
			        	if(StringUtils.isNotBlank(previousHistoryItem.getWorkItem().getNode().getDescription()))
		        		activateLater.appendChild(createElement("Description",previousHistoryItem.getWorkItem().getNode().getDescription(), doc));
			        	if(StringUtils.isNotBlank(previousHistoryItem.getWorkItem().getCurrentAssignee())  && 
		                		!previousHistoryItem.getWorkItem().getCurrentAssignee().equalsIgnoreCase("system"))
		        		activateLater.appendChild(createElement("Assignee",previousHistoryItem.getWorkItem().getCurrentAssignee(), doc));
		            } else {
	                
		                if(previousHistoryItem.getWorkItem().getDueTime() != null)
		                	node.appendChild(createElement("Due_Time",previousHistoryItem.getWorkItem().getDueTime().toString(), doc));
		                if(previousHistoryItem.getWorkItem().getTimeStarted() != null)
		                	node.appendChild(createElement("Start_Time",previousHistoryItem.getWorkItem().getTimeStarted().toString(), doc));
		                if(previousHistoryItem.getWorkItem().getTimeEnded() != null)
		                	node.appendChild(createElement("End_Time",previousHistoryItem.getWorkItem().getTimeEnded().toString(), doc));
		                if(previousHistoryItem.getWorkItem().getStatus() != null)
		                	node.appendChild(createElement("Status",previousHistoryItem.getWorkItem().getStatus().toString(), doc));
		                if(previousHistoryItem.getWorkItem().getNode().getTitle() != null)
		                	node.appendChild(createElement("Task_Name",previousHistoryItem.getWorkItem().getNode().getTitle(), doc));
		                if(StringUtils.isNotBlank(previousHistoryItem.getWorkItem().getCurrentAssignee())  && 
		                		!previousHistoryItem.getWorkItem().getCurrentAssignee().equalsIgnoreCase("system"))
	                		node.appendChild(createElement("Approver",previousHistoryItem.getWorkItem().getCurrentAssignee(), doc));
		                historyElem.appendChild(node);
		            }
	            }
	            
	            mainRootElement.appendChild(historyElem);
	            if(StringUtils.isNotBlank(absTime))
	            mainRootElement.appendChild(activateLater);
	            
	            if(workItem.getWorkflow().getWorkflowData().getMetaDataMap().containsKey("skiplegalreview")) {
	            	String skipLegalReview = workItem.getWorkflow().getWorkflowData().getMetaDataMap().get("skiplegalreview", String.class).toString();
	            	if(StringUtils.isNotBlank(skipLegalReview))
	            	mainRootElement.appendChild(createElement("Skip_Legal_Review",skipLegalReview, doc));
	            }
	            	
	            if(workItem.getWorkflow().getWorkflowData().getMetaDataMap().containsKey("skipqareview")) {
	            	String skipLegalReview = workItem.getWorkflow().getWorkflowData().getMetaDataMap().get("skipqareview", String.class).toString();
	            	if(StringUtils.isNotBlank(skipLegalReview))
	            	mainRootElement.appendChild(createElement("Skip_QA_Review",skipLegalReview, doc));
	            }
	            
	            if(workItem.getWorkflow().getWorkflowData().getMetaDataMap().containsKey("skipuiuxreview")) {
	            	String skipLegalReview = workItem.getWorkflow().getWorkflowData().getMetaDataMap().get("skipuiuxreview", String.class).toString();
	            	if(StringUtils.isNotBlank(skipLegalReview))
	            	mainRootElement.appendChild(createElement("Skip_UI_UX_Review",skipLegalReview, doc));
	            }
	            
	            Element endElem = doc.createElement("Step");
	            Element endTimeElem = doc.createElement("End_Time");
				String endTime = formatter.format(new Date());
				endTimeElem.appendChild(doc.createTextNode(endTime));
				endElem.appendChild(endTimeElem);
				
				Element endTimeTitle = doc.createElement("Title");
				endTimeTitle.appendChild(doc.createTextNode("End"));
				endElem.appendChild(endTimeTitle);
				
				mainRootElement.appendChild(endElem);
	            
				for(File out : outputFile) {
					LOGGER.info("saving file "+out.getAbsolutePath());
					if (!out.getParentFile().exists())
						out.getParentFile().mkdirs();
					saveXML(doc, out);
				}
	            
	    		LOGGER.info("Workflow XML generation completed");
            }

		} catch (IllegalArgumentException | WorkflowException e) {
			LOGGER.error("IllegalArgumentException or WorkflowException", e);
		} catch (ParserConfigurationException e) {
			LOGGER.error("ParserConfigurationException", e);
		} catch (TransformerFactoryConfigurationError e) {
			LOGGER.error("TransformerFactoryConfigurationError", e);
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException", e);
		} catch (Exception e) {
			LOGGER.error("Exception", e);
		} finally {
			if(resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
			if(jcrSession != null && jcrSession.isLive()) {
				jcrSession = null;
			}
		}

	}
	
	private void saveXML(Document doc, File outputFile) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
	        DOMSource source = new DOMSource(doc);
	        StreamResult result = new StreamResult(outputFile);
	        transformer.transform(source, result);
		} catch (TransformerException e) {
			LOGGER.error("TransformerException", e);
		}
       
	}

	private List<String> getWorkflowPackagePaths(Node payLoadNode) throws RepositoryException{
		final ResourceCollection resourceCollection = ResourceCollectionUtil.getResourceCollection(payLoadNode, resourceCollectionManager);
		if(null!=resourceCollection){
			final List<Node> members = resourceCollection.list(DEFAULT_WF_PACKAGE_TYPES);
			
			return members.stream()
					.map(node-> {
					try{
						return node.getPath();
					}
					catch(RepositoryException e){
						LOGGER.error("Error encountered while retrieving page paths", e);
					}
					return null;
					})
					.collect(Collectors.toList());
		}
		return null;
	}

	private Element createElement(String elementName, String textNode, Document doc) {
		Element element = doc.createElement(elementName);
		element.appendChild(doc.createTextNode(textNode));
        return element;
	}
		
    private Map<String, ? extends Object> getFileStructure(String page, String extn){
		
		String fileName = null;
		Map<String, Object> fileMap = new HashMap<>();
		
		fileName = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(page, "/"), ".")
						+ "_" + folderFormat.format(new Date()) + fileFormat.format(new Date()) + "." + extn;
			
		fileMap.put("fileName", fileName);
		fileMap.put("fileData", new File(xmlOutputDirectory, fileName));
		
		LOGGER.info("Inside getFileStructure method ::: File Name ==> "+fileName);
		LOGGER.info("Inside getFileStructure method ::: Output Directory ==> "+xmlOutputDirectory);
		
		return fileMap;
		
	}
	
	private ResourceResolver getResourceResolver(Session session) throws LoginException {
		return resourceResolverFactory.getResourceResolver(Collections.<String, Object> singletonMap(
				JcrResourceConstants.AUTHENTICATION_INFO_SESSION, session));

	}

}
