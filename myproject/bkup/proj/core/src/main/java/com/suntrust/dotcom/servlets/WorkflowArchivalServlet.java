package com.suntrust.dotcom.servlets;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.model.WorkflowNode;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.suntrust.dotcom.config.SuntrustPDFService;

/**
 * Class used by the authors if they want to archive completed workflows
 * in a specified duration.
 */

@SuppressWarnings("serial")
@SlingServlet(metatype = false, paths = { "/dotcom/workflowarchive" }, methods = { "GET" })
public final class WorkflowArchivalServlet extends SlingAllMethodsServlet {

	/** ResourceResolverFactory service reference */
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	/** SlingSettingsService service reference */
	@Reference
	private SlingSettingsService slingSettings;
	
	/** ResourceCollectionManager service reference */
	@Reference
	private ResourceCollectionManager resourceCollectionManager;
	
	/** SuntrustPDFService service reference */
	@Reference
	private SuntrustPDFService pdfService;

	/** ResourceResolver class reference variable */
	private ResourceResolver resolver = null;

	/** Session class reference variable */
	private Session jcrSession = null;
	
	/** Workflow models excluded from archival */
	private static final String[] EXCLUDED_MODELS = { "/etc/workflow/models/dotcom/content-expiry-owner-notification-workflow/jcr:content/model",
			"/etc/workflow/models/dotcom/page-id-generator-workflow/jcr:content/model" };
	
	/** xml archival folder name */
	private static final String WORKFLOW_ARCHIVAL_FOLDER = "workflow_archived";
	
	/** List of output xml files */
	private List<File> outputFile = null;
	
	/** xml doc root */
	private Document doc = null;
	
	/** xml mainRootElement */
	private Element mainRootElement = null;
	
	/** Archival output directory */
	private String xmlOutputDirectory = null;
	
	/** Workflow process steps */
	private static final String[] PROCESSES = {"com.suntrust.dotcom.workflow.WorkflowArchivalProcess", "com.day.cq.wcm.workflow.process.ActivatePageProcess",
			"com.day.cq.wcm.workflow.process.DeactivatePageProcess", "com.suntrust.dotcom.workflow.RatesProdDeployment"};
	
	/** Date pattern for folder creation */
	private static final SimpleDateFormat FOLDER_FORMAT = new SimpleDateFormat("MMddyyyy");
	
	/** Date pattern for file creation */
	private static final SimpleDateFormat FILE_FORMAT = new SimpleDateFormat("HHMMSSss");
	
	/** Date pattern for workflow date in xml */
	private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("MM/dd/yy hh:mm a");

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowArchivalServlet.class);
	
	/**
	 * startDate and endDate should be in any pattern provided by parsePatterns.
	 */
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException,
			IOException {

		try {
			LOGGER.debug("inside WorkflowArchivalServlet *** ");

			String startDateString = request.getParameter("startdate");
			String endDateString = request.getParameter("enddate");
			String cleanDir = request.getParameter("cleandir");
			
			Date startDate = DateUtils.parseDateStrictly(startDateString, "MM-dd-yyyy");
			Date endDate = DateUtils.parseDateStrictly(endDateString, "MM-dd-yyyy");
			
			LOGGER.debug("Searching for workflows that completed from"+startDate+ " to "+DateUtils.addDays(endDate, 1));
			
			if(startDate.after(endDate)) {
				response.getWriter().write("Please choose start date to be before end date.");
			}
			
			startDateString = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
			endDateString = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			resolver = resolverFactory.getServiceResourceResolver(param);

			xmlOutputDirectory = slingSettings.getAbsolutePathWithinSlingHome(pdfService.getPdfOutputPath()) + "/"
					+ WORKFLOW_ARCHIVAL_FOLDER;
			LOGGER.debug("Archival files will be saved under " + xmlOutputDirectory);
			
			File xmlsFiles = new File(xmlOutputDirectory);
			
			if(xmlsFiles!=null && xmlsFiles.isDirectory() && 
					StringUtils.equalsIgnoreCase(cleanDir,"true") && xmlsFiles.listFiles().length > 0) {
					FileUtils.cleanDirectory(xmlsFiles);
			}
			
			jcrSession = resolver.adaptTo(Session.class);
			
			int fileCount = 0;
			
			NodeIterator nodeIterator = createQuery(startDateString, endDateString, EXCLUDED_MODELS);

			while (nodeIterator.hasNext()) {
				Node workFlowNode = nodeIterator.nextNode();

				LOGGER.debug("workFlowNode is " + workFlowNode.getPath());

				WorkflowSession wfSession = resolver.adaptTo(WorkflowSession.class);

				Workflow workflow = wfSession.getWorkflow(workFlowNode.getPath());
				
				if(workflow.getWorkflowModel() == null) {
					continue;
				}
				
				String wfInitiateDate = DATE_TIME_FORMATTER.format(workflow.getTimeStarted());

				DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder icBuilder;

				icBuilder = icFactory.newDocumentBuilder();
				doc = icBuilder.newDocument();

				mainRootElement = doc.createElement("Workflow");
				doc.appendChild(mainRootElement);

				Element workflowDate = doc.createElement("Workflow_Date");
				workflowDate.appendChild(doc.createTextNode(wfInitiateDate));
				mainRootElement.appendChild(workflowDate);

				String workflowTitle = workflow.getWorkflowData().getMetaDataMap()
						.get("workflowTitle", String.class);
				if (StringUtils.isNotBlank(workflowTitle))
					mainRootElement.appendChild(createElement("Workflow_Title", workflowTitle, doc));

				String jobId = workflow.getId();
				if (StringUtils.isNotBlank(jobId))
					mainRootElement.appendChild(createElement("JobId", jobId, doc));

				final String initiator = workflow.getInitiator();
				if (StringUtils.isNotBlank(initiator)) {
					UserManager manager = resolver.adaptTo(UserManager.class);
					Authorizable authorizable = manager.getAuthorizable(initiator);
					String givenName = authorizable.getProperty("./profile/givenName") != null ? authorizable
							.getProperty("./profile/givenName")[0].getString().concat(", ") : "";
					String familyName = authorizable.getProperty("./profile/familyName") != null ? authorizable
							.getProperty("./profile/familyName")[0].getString() : "";

					String initiatorName = givenName + familyName + "(" + initiator + ")";
					mainRootElement.appendChild(createElement("Job_Initiator", initiatorName, doc));

					Value[] email = authorizable.getProperty("./profile/email");
					if (email != null) {
						mainRootElement.appendChild(createElement("Job_Initiator_Email_Id", email[0].getString(), doc));
					}
				}

				String jobName = workflow.getWorkflowModel().getTitle();
				if (StringUtils.isNotBlank(jobName)) {
					mainRootElement.appendChild(createElement("Workflow_Name", jobName, doc));
				}
				
				String endTime = DATE_TIME_FORMATTER.format(workflow.getTimeEnded());
				
				String payLoadPath = workflow.getWorkflowData().getPayload().toString();
				
				List<HistoryItem> history = wfSession.getHistory(workflow);
				
				HistoryItem historyItem = history.get(history.size()-1);
				
				String historyLastStepEndTime = historyItem.getWorkItem().getTimeEnded().toString();
				
				Date wfEndTime = DateUtils.parseDateStrictly(historyLastStepEndTime, "EEE MMM d HH:mm:ss z yyyy");
				
				Node payLoadNode = null; 
						
				if(resolver.getResource(payLoadPath) != null && !StringUtils.equals(payLoadPath,".")) {
					
					payLoadNode = jcrSession.getNode(payLoadPath);
					
					if(payLoadNode.hasNode("jcr:content")) {
						payLoadNode = payLoadNode.getNode("jcr:content");
					}
					
					if(StringUtils.containsIgnoreCase(payLoadPath, "jcr:content")) {
						String assetPath = StringUtils.substringBefore(payLoadPath, "/jcr:content");
						payLoadNode = jcrSession.getNode(assetPath);
					}

					NodeType[] nodeType = payLoadNode.getMixinNodeTypes();
					boolean iswfPkg = Arrays.asList(nodeType).stream()
							.anyMatch(node -> "vlt:Package".equalsIgnoreCase(node.getName()));

					if (iswfPkg) {
						LOGGER.debug("payLoad is a wf pkg");
						Node resFilterNode = JcrUtils.getNodeIfExists(payLoadNode, "vlt:definition/filter");
						if(resFilterNode != null) {
							NodeIterator resIterator = resFilterNode.getNodes();
							Element resourcesElem = doc.createElement("Resources");
							outputFile = new ArrayList<File>();
							while(resIterator.hasNext()) {
								Node resNode = resIterator.nextNode();
								if(resNode.hasProperty("root")){
									Element resElem = createElement("Resource", resNode.getProperty("root").getString(), doc);
									resourcesElem.appendChild(resElem);
									outputFile.add((File) getFileStructure(resNode.getProperty("root").getString(), "xml", wfEndTime).get("fileData"));
								}
							}
							mainRootElement.appendChild(resourcesElem);
						}
					} else {
						outputFile = new ArrayList<File>();
						LOGGER.debug("payLoad is not a wf pkg");
						mainRootElement.appendChild(createElement("resource", payLoadPath, doc));
						if (payLoadNode.hasProperty("cq:lastReplicated")) {
							mainRootElement.appendChild(createElement("Change_Type",
									"Content update in existing page and activation", doc));
						} else {
							mainRootElement.appendChild(createElement("Change_Type", "New page activation", doc));
						}
						outputFile.add((File) getFileStructure(payLoadPath, "xml", wfEndTime).get("fileData"));
					}
				} else { // incase of payload moved/deleted
					outputFile = new ArrayList<File>();
					mainRootElement.appendChild(createElement("resource", payLoadPath, doc));
					if(StringUtils.equals(payLoadPath,".")) {
						mainRootElement.appendChild(createElement("Change_Type",
								"people-upload-group-approval-workflow - no profile was updated", doc));
					} else {
						mainRootElement.appendChild(createElement("Change_Type",
								"Content update in existing page and activation", doc));
					}
					
					outputFile.add((File) getFileStructure(payLoadPath, "xml", wfEndTime).get("fileData"));
				}
				
				readHistory(wfSession, workflow, workFlowNode);

				if (workflow.getWorkflowData().getMetaDataMap().containsKey("skiplegalreview")) {
					String skipLegalReview = workflow.getWorkflowData().getMetaDataMap()
							.get("skiplegalreview", String.class).toString();
					if (StringUtils.isNotBlank(skipLegalReview))
						mainRootElement.appendChild(createElement("Skip_Legal_Review", skipLegalReview, doc));
				}

				if (workflow.getWorkflowData().getMetaDataMap().containsKey("skipqareview")) {
					String skipLegalReview = workflow.getWorkflowData().getMetaDataMap()
							.get("skipqareview", String.class).toString();
					if (StringUtils.isNotBlank(skipLegalReview))
						mainRootElement.appendChild(createElement("Skip_QA_Review", skipLegalReview, doc));
				}

				if (workflow.getWorkflowData().getMetaDataMap().containsKey("skipuiuxreview")) {
					String skipLegalReview = workflow.getWorkflowData().getMetaDataMap()
							.get("skipuiuxreview", String.class).toString();
					if (StringUtils.isNotBlank(skipLegalReview))
						mainRootElement.appendChild(createElement("Skip_UI_UX_Review", skipLegalReview, doc));
				}

				Element endElem = doc.createElement("Step");
				Element endTimeElem = doc.createElement("End_Time");
				
				endTimeElem.appendChild(doc.createTextNode(endTime));
				endElem.appendChild(endTimeElem);

				Element endTimeTitle = doc.createElement("Title");
				endTimeTitle.appendChild(doc.createTextNode("End"));
				endElem.appendChild(endTimeTitle);

				mainRootElement.appendChild(endElem);

				for (File out : outputFile) {
					fileCount++;
					LOGGER.debug("saving file " + out.getAbsolutePath());
					if (!out.getParentFile().exists())
						out.getParentFile().mkdirs();
					saveXML(doc, out);
				}
				LOGGER.debug("Workflow XML generation completed");
			}

			if(fileCount > 0) {
				response.getWriter().write(fileCount +" workflow archival file(s) generated. Please check in the path - "+ xmlOutputDirectory);
			}
			else {
				response.getWriter().write("No workflow archival file(s) generated.");
			}

		} catch (WorkflowException e) {
			LOGGER.error("WorkflowException", e);
			response.getWriter().write("There was a problem generating workflow archival files for the specified period"+e.getMessage());
		} catch (ParserConfigurationException e) {
			LOGGER.error("ParserConfigurationException", e);
			response.getWriter().write("There was a problem generating workflow archival files for the specified period"+e.getMessage());
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException", e);
			response.getWriter().write("There was a problem generating workflow archival files for the specified period"+e.getMessage());
		} catch (LoginException e) {
			LOGGER.error("LoginException", e);
			response.getWriter().write("There was a problem generating workflow archival files for the specified period"+e.getMessage());
		} catch (ParseException e) {
			LOGGER.error("ParseException", e);
			response.getWriter().write("There was a problem parsing date. Please use the date in MM-dd-yyyy format.");
		} finally {
			if (resolver != null && resolver.isLive()) {
				resolver.close();
			}
			if (jcrSession != null && jcrSession.isLive()) {
				jcrSession = null;
			}
		}

	}

	/**
	 * @param wfSession
	 * @param workflow
	 * @param workFlowNode
	 * @throws WorkflowException
	 * @throws RepositoryException
	 */
	private void readHistory(WorkflowSession wfSession, Workflow workflow, Node workFlowNode) throws WorkflowException, RepositoryException {
		
		List<HistoryItem> history = wfSession.getHistory(workflow);
		Iterator<HistoryItem> historyIterator = history.iterator();
		Element historyElem = doc.createElement("History");
		Element activateLater = null;
		String absTime = null;
		
		String currentAssignee = "";
		String activateLaterTime = null;
		
		while (historyIterator.hasNext()) {
			HistoryItem prevHistoryItem = historyIterator.next();
			String stepType = prevHistoryItem.getWorkItem().getNode().getType();
			String workItemId = prevHistoryItem.getWorkItem().getNode().getId();
			if(StringUtils.isNotBlank(workItemId)) {
				WorkflowNode modelNode = workflow.getWorkflowModel().getNode(workItemId);
				if(modelNode != null) {
					if(StringUtils.containsIgnoreCase(workFlowNode.getPath(), "student-lending-rates-review-workflow")) {
						if (modelNode.getMetaDataMap().containsKey("PROCESS_ARGS") &&
							modelNode.getMetaDataMap().get("PROCESS_ARGS", String.class).equals("rpx.uat.approve")) {
							LOGGER.debug("FOUNDMATCH in student-lending-rates-review-workflow last step written in xml: "+modelNode.getTitle());
							break;
						}
					} else if(StringUtils.containsIgnoreCase(workFlowNode.getPath(), "student-lending-rates-scheduler-workflow")) {
						if (modelNode.getMetaDataMap().containsKey("PROCESS_ARGS") &&
								modelNode.getMetaDataMap().get("PROCESS_ARGS", String.class).equals("rpx.schedule.success")) {
								LOGGER.debug("FOUNDMATCH in student-lending-rates-scheduler-workflow last step written in xml: "+modelNode.getTitle());
								break;
							}
					} else {
						if(stepType.equals(WorkflowNode.TYPE_PROCESS) && 
							Arrays.asList(PROCESSES).contains(modelNode.getMetaDataMap().get("PROCESS", String.class))) {
							LOGGER.debug("FOUNDMATCH  Last step written in xml: "+modelNode.getTitle());
							break;
						}
					}
				}
			}
			Element node = doc.createElement("Step");

			if (stepType != null && stepType.equals(WorkflowNode.TYPE_PARTICIPANT)
					|| stepType.equals(WorkflowNode.TYPE_DYNAMIC_PARTICIPANT)) {
				String ownerComment = prevHistoryItem.getWorkItem().getMetaDataMap()
						.get("comment", String.class);
				if (StringUtils.isNotBlank(ownerComment))
					node.appendChild(createElement("Comment", ownerComment, doc));
				currentAssignee = prevHistoryItem.getWorkItem().getCurrentAssignee();
				String wfProcessArg;
				if (workflow.getMetaDataMap().containsKey("PROCESS_ARGS")) {
					wfProcessArg = workflow.getMetaDataMap().get("PROCESS_ARGS", String.class)
							.toString().trim();
					if (StringUtils.containsIgnoreCase(wfProcessArg, "compliance"))
						mainRootElement.appendChild(createElement("Compliance_Approval", currentAssignee,
								doc));
					if (StringUtils.containsIgnoreCase(wfProcessArg, "legal"))
						mainRootElement.appendChild(createElement("Legal_Approval", currentAssignee, doc));
				}
			}

			absTime = prevHistoryItem.getWorkItem().getNode().getMetaDataMap()
					.get("absoluteTime", String.class);

			if (StringUtils.isNotBlank(absTime)) {
				activateLater = doc.createElement("Scheduled_Activation");
				Long absoluteTime = Long.valueOf(absTime);
				activateLaterTime = DATE_TIME_FORMATTER.format(absoluteTime * 1000);
				activateLater
						.appendChild(createElement("Scheduled_Activation_Time", activateLaterTime, doc));
				if (StringUtils.isNotBlank(prevHistoryItem.getWorkItem().getNode().getTitle()))
					activateLater.appendChild(createElement("Title", prevHistoryItem.getWorkItem()
							.getNode().getTitle(), doc));
				if (StringUtils.isNotBlank(prevHistoryItem.getWorkItem().getNode().getDescription()))
					activateLater.appendChild(createElement("Description", prevHistoryItem
							.getWorkItem().getNode().getDescription(), doc));
				if (StringUtils.isNotBlank(prevHistoryItem.getWorkItem().getCurrentAssignee())
						&& !prevHistoryItem.getWorkItem().getCurrentAssignee()
								.equalsIgnoreCase("system"))
					activateLater.appendChild(createElement("Assignee", prevHistoryItem.getWorkItem()
							.getCurrentAssignee(), doc));
			} else {

				if (prevHistoryItem.getWorkItem().getDueTime() != null)
					node.appendChild(createElement("Due_Time", prevHistoryItem.getWorkItem()
							.getDueTime().toString(), doc));
				if (prevHistoryItem.getWorkItem().getTimeStarted() != null)
					node.appendChild(createElement("Start_Time", prevHistoryItem.getWorkItem()
							.getTimeStarted().toString(), doc));
				if (prevHistoryItem.getWorkItem().getTimeEnded() != null)
					node.appendChild(createElement("End_Time", prevHistoryItem.getWorkItem()
							.getTimeEnded().toString(), doc));
				if (prevHistoryItem.getWorkItem().getStatus() != null)
					node.appendChild(createElement("Status", prevHistoryItem.getWorkItem().getStatus()
							.toString(), doc));
				if (prevHistoryItem.getWorkItem().getNode().getTitle() != null)
					node.appendChild(createElement("Task_Name", prevHistoryItem.getWorkItem().getNode()
							.getTitle(), doc));
				if (StringUtils.isNotBlank(prevHistoryItem.getWorkItem().getCurrentAssignee())
						&& !prevHistoryItem.getWorkItem().getCurrentAssignee()
								.equalsIgnoreCase("system"))
					node.appendChild(createElement("Approver", prevHistoryItem.getWorkItem()
							.getCurrentAssignee(), doc));
				historyElem.appendChild(node);
			}
			
			if(StringUtils.containsIgnoreCase(workFlowNode.getPath(), "quick")) {
				break;
			}
		}
		
		mainRootElement.appendChild(historyElem);
		if (StringUtils.isNotBlank(absTime))
			mainRootElement.appendChild(activateLater);
	}

	private NodeIterator createQuery(String startDateString, String endDateString, String[] excludedModels) throws RepositoryException {
		final StringBuilder queryString = new StringBuilder("SELECT * FROM [cq:Workflow] AS s WHERE ISDESCENDANTNODE('/etc/workflow/instances')");
		for(String excludedModel : excludedModels) {
			queryString.append(" AND NOT [modelId] ='");
			queryString.append(excludedModel);
			queryString.append("'");
		}
		queryString.append(" AND [status] = 'COMPLETED' AND s.startTime >= CAST('");
		queryString.append(startDateString);
		queryString.append("T00:00:00.000Z' AS DATE) AND s.endTime <= CAST('");
		queryString.append(endDateString);
		queryString.append("T23:59:59.999Z' AS DATE)");
		
		LOGGER.debug("queryString:" + queryString.toString());

		Session session = resolver.adaptTo(Session.class);
		LOGGER.debug("session user:" + session.getUserID());

		final QueryManager qm = session.getWorkspace().getQueryManager();

		final Query query = qm.createQuery(queryString.toString(), Query.JCR_SQL2);

		QueryResult queryResult = query.execute();

		NodeIterator nodeIterator = queryResult.getNodes();
		
		LOGGER.debug("No. of completed workflows found :" + nodeIterator.getSize());
		
		return nodeIterator;
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

	private Element createElement(String elementName, String textNode, Document doc) {
		Element element = doc.createElement(elementName);
		element.appendChild(doc.createTextNode(textNode));
		return element;
	}

	private Map<String, ? extends Object> getFileStructure(String page, String extn, Date endDateSeconds) throws ParseException {

		String fileName = null;
		Map<String, Object> fileMap = new HashMap<>();
		Date endDate = endDateSeconds;
		
		if(endDate == null) {
			endDate = new Date();
		}
		
		LOGGER.debug("Workflow endDate in getFileStructure:"+endDate);

		fileName = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(page, "/"), ".") + "_"
				+ FOLDER_FORMAT.format(endDate) + FILE_FORMAT.format(endDate) + "." + extn;

		fileMap.put("fileName", fileName);
		fileMap.put("fileData", new File(xmlOutputDirectory, fileName));

		LOGGER.debug("Inside getFileStructure method ::: File Name ==> " + fileName);

		return fileMap;

	}

}
