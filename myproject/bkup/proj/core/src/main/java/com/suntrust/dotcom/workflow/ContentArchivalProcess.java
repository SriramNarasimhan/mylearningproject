package com.suntrust.dotcom.workflow;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.lang.StringUtils;
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
import com.day.cq.workflow.collection.ResourceCollection;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.day.cq.workflow.collection.ResourceCollectionUtil;
import com.suntrust.dotcom.config.STELConfigService;
import com.suntrust.dotcom.config.SuntrustPDFService;
import com.suntrust.dotcom.services.EmailService;

/**
 * @author uiam82
 *
 */
@Component
@Service
@Properties({ @Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Archival Wrokflow Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust Archival Wrokflow Service") })
public class ContentArchivalProcess implements WorkflowProcess { 

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

	ResourceResolver resourceResolver = null;
	Session session = null;
	Externalizer externalizer = null; 
	UserManager userManager = null;
	private static final String NOTIFICATION_TEMPLATEPATH = "/etc/notification/email/html/dotcom/archivalnotification.html";
	private static final String EMAIL_SENDER_ADDRESS = "cms@SunTrust.com";
	private static final String EMAIL_SENDER_NAME = "SunTrust CMS";
	private static final String CHECK_STEL_ARCHIVAL = "stelarchival";
	private static final String[] DEFAULT_WF_PACKAGE_TYPES = { "cq:Page", "cq:PageContent" };
	private String workflowModelTitle = null;
	private String pageLink = null;
	private String recipientName = null;
	private String workflowInitiateDate = null;
	private String workflowTitle = null;
	private String environment = null;
	private String resource = null;
	private String[] toAddress = null;
	private String[] ccAddress = null;
	private String failureReason = null;
	private String jsHelperPath = null;;
	private String callToPhantomJS = null;
	private String pathToWorkerJS = null;
	private String paperSize = null;
	private String jsEvalOption = null;
	private String username = null;
	private String pwd = null;
	private String outputType = null;
	private boolean isAuthorEnv = false;	
	private String pdfOutputDirectory = null;
	Map<String, String> fileNamePathsMap = null;
	SimpleDateFormat folderFormat = new SimpleDateFormat("MMddyyyy");
	SimpleDateFormat fileFormat = new SimpleDateFormat("HHMMSSss");

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentArchivalProcess.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workSession, MetaDataMap metaDataMap)
			throws WorkflowException {
		LOGGER.info("Archival Started");
		try {
			if (pdfService.isArchivalEnabled()) {

				Map<String, Object> param = new HashMap<String, Object>();
				param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
				resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
				externalizer = resourceResolver.adaptTo(Externalizer.class);
				session = resourceResolver.adaptTo(Session.class);
				userManager = resourceResolver.adaptTo(UserManager.class);
				
				String wfProcessArg=null;
				String phantomPath = null;				
				fileNamePathsMap = new HashMap<String, String>();
				isAuthorEnv = "author".equalsIgnoreCase(pdfService.getEnviroment());

				String payLoadPath = workItem.getWorkflowData().getPayload().toString();
				/**
				 * Path is set empty if there is no profile to be published for
				 * advisor workflow. AEM replaces empty string with DOT. No
				 * action is required in this case.
				 */
				if(payLoadPath.equals(".")){
					return;
				}
				
				if(workItem.getNode().getMetaDataMap().containsKey("PROCESS_ARGS")){
					wfProcessArg=workItem.getNode().getMetaDataMap().get("PROCESS_ARGS", String.class).toString().trim();
				}
				
				if(null!=wfProcessArg && wfProcessArg.equals(CHECK_STEL_ARCHIVAL)){					
					LOGGER.info("Contain STEL Archival");															
										
					if(null!=stelConfigService.getPropertyValue("rpx.prod.servlet.url") && !stelConfigService.getPropertyArray("rpx.product.codes").isEmpty())
						getStelProdPaths();
					
					if(null!=stelConfigService.getPropertyValue("rpx.paymentstable.servlet.config.path"))
						getRpxServletPaths(stelConfigService.getPropertyArray("rpx.paymentstable.servlet.config.path"));
						
				}else{
					LOGGER.info("Doesn't Contains STEL Archival"); 					
					getAbsolutePublishPaths(payLoadPath);  
					
				}

				workflowModelTitle = workItem.getWorkflow().getWorkflowModel().getTitle();
				workflowTitle = workItem.getWorkflow().getWorkflowData().getMetaDataMap().get("workflowTitle",String.class);
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
				workflowInitiateDate = formatter.format(workItem.getWorkflow().getTimeStarted());

				String initiator = workItem.getWorkflow().getInitiator();
				Authorizable userAuthorizable = userManager.getAuthorizable(initiator);
				
				String givenName = userAuthorizable.getProperty("./profile/givenName") != null
						? userAuthorizable.getProperty("./profile/givenName")[0].getString() : "";
				String familyName = userAuthorizable.getProperty("./profile/familyName") != null
						? userAuthorizable.getProperty("./profile/familyName")[0].getString() : "";

				recipientName = givenName + " " + familyName + "[" + "USCORP" + "\\" + initiator + "]";

				if (null == pdfService.getEmailTo())
					throw new IllegalArgumentException(
							"Please configure Email user list in Suntrust PDF Service Configuration");

				if (isAuthorEnv && (StringUtils.isBlank(pdfService.getUsername())
						|| StringUtils.isBlank(pdfService.getPassword())))
					throw new IllegalArgumentException(
							"Username & password is mandatory when Author Environment is chosen. Please enter values in Suntrust Dotcom Service Configuration");

				toAddress = pdfService.getEmailTo();

				ccAddress = pdfService.getEmailCC();

				environment = externalizer.authorLink(resourceResolver, "");
				/* initialise all user properties */
				jsHelperPath = slingSettings.getAbsolutePathWithinSlingHome(pdfService.getHelperPath());
				phantomPath = pdfService.getPhantomJsPath();
				LOGGER.info("Html2PdfConverter==> JSHelper path:" + jsHelperPath);
				callToPhantomJS = phantomPath + "/phantomjs";
				pathToWorkerJS = jsHelperPath + "/" + pdfService.getHelperJSName();
				paperSize = pdfService.getPaperSize();
				jsEvalOption = pdfService.getJsEval();
				username = pdfService.getUsername();
				pwd = pdfService.getPassword();
				pdfOutputDirectory = slingSettings.getAbsolutePathWithinSlingHome(pdfService.getPdfOutputPath());

				/* Create HTML with Phantom JS */
				if(!fileNamePathsMap.isEmpty()){  
					
					if(null!=wfProcessArg && wfProcessArg.contains(CHECK_STEL_ARCHIVAL)){
						outputType = "json";
						createJsonFile(fileNamePathsMap); 
					}else{						
						outputType="html";
						createHtmlFile(fileNamePathsMap); 						
					}
					
					/*Clear Map after use*/
					fileNamePathsMap.clear(); 
				}
				else{
					LOGGER.info(
							"Skipping Content Archival Process Step. No payload path found to execute content archival process");
					return;
				}

			} else {
				LOGGER.info(
						"Skipping Content Archival Process Step. To activate, please enable Archival from Suntrust PDF Service Configuration");
				return;

			}
		} catch (IllegalArgumentException | RepositoryException | LoginException e) {
			LOGGER.error("Error in creating pdf file==>", e);
		}
		finally {
			fileNamePathsMap.clear();
			if(null!=resourceResolver){
				resourceResolver.close();
			}
			LOGGER.info("The content Archival Process is completed");
		}

	}	 
	
	private void createJsonFile(Map<String,String> absolutePublishPathsMap) {
		LOGGER.info("CreateJsonFile method is getting called");

		absolutePublishPathsMap.forEach((fileParam, page) -> {
			try {
				String requestPath = page;
				File outputFile = (File) getFileStructure(page, fileParam, "json").get("fileData");
				if (!outputFile.getParentFile().exists())
					outputFile.getParentFile().mkdirs();

				Runtime runtime = Runtime.getRuntime();
				Process process = runtime
						.exec(createPhantomJsScript(requestPath, outputFile.getAbsolutePath(), "json")); 
				int exitStatus = process.waitFor(); // do a wait here to prevent
													// it running for ever
				if (exitStatus != 0) {
					try (final BufferedReader errorReader = new BufferedReader(
							new InputStreamReader(process.getErrorStream()))) {
						String line = null;
						if ((line = errorReader.readLine()) != null)
							throw new IOException(line);
						else
							throw new IOException(
									"If you are converting author URL, check that username/password in config is correctly entered. This might also result from file read/write access errors. Please check and try again.");
					} catch (final IOException e) {
						LOGGER.error("Error reading error while executing phantomjs during PNG processing==>", e);
					}
				}
			} 
			catch (IOException | InterruptedException e) {
				LOGGER.error("Html2PdfConverter==> Error while converting to JSON {} ", page, e);
				pageLink = page;
				resource = "JSON";
				failureReason = e.getMessage();
				sendEmail();
			}
		});
		
	}
	
	private void createHtmlFile(Map<String,String> absolutePublishPathsMap) {

		absolutePublishPathsMap.forEach((fileParam, page) -> {
			try {
				String requestPath = page;
				File outputFile = (File) getFileStructure(page, fileParam, "html").get("fileData");
				if (!outputFile.getParentFile().exists())
					outputFile.getParentFile().mkdirs();

				Runtime runtime = Runtime.getRuntime();
				Process process = runtime.exec(createPhantomJsScript(requestPath, outputFile.getAbsolutePath(), "HTML")); 
				int exitStatus = process.waitFor(); // do a wait here to prevent
													// it running for ever
				if (exitStatus != 0) {
					try (final BufferedReader errorReader = new BufferedReader(
							new InputStreamReader(process.getErrorStream()))) {
						String line = null;
						if ((line = errorReader.readLine()) != null)
							throw new IOException(line);
						else
							throw new IOException(
									"If you are converting author URL, check that username/password in config is correctly entered. This might also result from file read/write access errors. Please check and try again.");
					} catch (final IOException e) {
						LOGGER.error("Error reading error while executing phantomjs during HTML processing==>", e);
					}
				}
			} 
			catch (IOException | InterruptedException e) {
				LOGGER.error("Html2PdfConverter==> Error while converting to HTML {} ", page, e);
				pageLink = page;
				resource = "HTML";
				failureReason = e.getMessage();
				sendEmail();
			}
		});
	}

	private String createPhantomJsScript(String url, String outputFile, String output) {
		String command = null;
					
			LOGGER.info("Phantomjs command executed for html archival ===>  "+ callToPhantomJS + " " + pathToWorkerJS + " " + url + " " + outputFile + " " + jsEvalOption + " "
					+ username + " " + pwd + " " + paperSize + " " + outputType);
			
			command = callToPhantomJS + " " + pathToWorkerJS + " " + url + " " + outputFile + " " + jsEvalOption + " "
					+ username + " " + pwd + " " + paperSize + " " + outputType;			

		return command;
	}

	private void getAbsolutePublishPaths(String payloadPath) throws RepositoryException {
		String resourceExtension = "";
		Node payLoadNode = session.getNode(payloadPath).getNode("jcr:content");
		
		if(payloadPath.startsWith("/content/dam/")){
			LOGGER.info("payloadPath have assets");
		}else{
			LOGGER.info("payloadPath don't have assets");
			NodeType[] nodeType = payLoadNode.getMixinNodeTypes();
			boolean iswfPkg = Arrays.asList(nodeType).stream().anyMatch(type -> "vlt:Package".equalsIgnoreCase(type.getName()));
			boolean isCqPage = session.getNode(payloadPath).isNodeType("cq:Page");
			if (isCqPage)
				resourceExtension = ".html?pdfoutput=true";

			if (iswfPkg) {
				getWorkflowPkgPublishPaths(payLoadNode); 
			} else {
				fileNamePathsMap.put("page:cqpage", externalizer.externalLink(resourceResolver, "pdfurl", payloadPath + resourceExtension));				
			}
		}		
	}
	
	/**
	 * @param payLoadNode
	 * @return Published Paths of workflow package
	 * @throws RepositoryException
	 */
	
	private void getWorkflowPkgPublishPaths(Node payLoadNode) throws RepositoryException {

		final ResourceCollection resourceCollection = ResourceCollectionUtil.getResourceCollection(payLoadNode,	resourceCollectionManager);
		if (null != resourceCollection) {
			final List<Node> members = resourceCollection.list(DEFAULT_WF_PACKAGE_TYPES); 
			for(int i=0; i < members.size(); i++){
				if(!members.get(i).getPath().startsWith("/content/dam/")){					
					String resourceExtension = members.get(i).isNodeType("cq:Page") ? ".html?pdfoutput=true" : "";
					fileNamePathsMap.put("page:cqpage"+i, externalizer.externalLink(resourceResolver, "pdfurl", members.get(i).getPath() + resourceExtension));
				}
								
			}
		}
	}
	
	private void getRpxServletPaths(List<String> payloadPathList) throws RepositoryException {
		payloadPathList.stream().forEach(payloadPath -> {
			fileNamePathsMap.put("rpxrates:db_rpx_rates", externalizer.externalLink(resourceResolver, "pdfurl", payloadPath));
		});

	}
	
	private void getStelProdPaths(){
		String prodServletUrl = stelConfigService.getPropertyValue("rpx.prod.servlet.url");
		List<String> rpxProductCodes = stelConfigService.getPropertyArray("rpx.product.codes");
		String resourceJoiner = "?linkId=";
		rpxProductCodes.stream().forEach(code -> {
			fileNamePathsMap.put("rpxcode:"+code, prodServletUrl+resourceJoiner+code);
		});

	}
	
	private Map<String, ? extends Object> getFileStructure(String page, String param, String extn){
		
		String[] fileParam = param.split(":");
		String fileName = null;
		Map<String, Object> fileMap = new HashMap<>();
		switch (fileParam[0]) {
			case "rpxcode":
			{
				fileName = fileParam[1]
						+ "_" + folderFormat.format(new Date()) + "_" + fileFormat.format(new Date()) + "." + extn;
			}
				break;
			case "rpxrates":
			{
				fileName = fileParam[1]
						+ "_" + folderFormat.format(new Date()) + "_" + fileFormat.format(new Date()) + "." + extn;
			}
				break;			
			case "page":
			{
				fileName = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(page, "/"), ".")
						+ "_" + folderFormat.format(new Date()) + fileFormat.format(new Date()) + "." + extn;
			}
				break;
		}
		
		fileMap.put("fileName", fileName);
		fileMap.put("fileData", new File(pdfOutputDirectory, fileName));
		
		LOGGER.info("Inside getFileStructure method ::: File Name ==> "+fileName);
		LOGGER.info("Inside getFileStructure method ::: Output Directory ==> "+pdfOutputDirectory);
		
		return fileMap;
		
	}

	
	/*private void skipWorkflowStep(WorkflowSession workSession) throws WorkflowException{
		
		WorkItem newWorkItem = workSession.getActiveWorkItems()[0];
		workSession.complete(newWorkItem, workSession.getRoutes(newWorkItem, true).get(0));
		
	}*/

	/**
	 * Send email to configured recipient when archival fails
	 */
	private void sendEmail() {

		Map<String, String> emailParams = new HashMap<>();
		String subject = resource + " creation failed for: " + pageLink;
		String template = NOTIFICATION_TEMPLATEPATH;
		List<String> emailRecipients = Arrays.asList(toAddress);

		List<String> ccRecipients = Optional.ofNullable(ccAddress).map(Arrays::stream).orElseGet(Stream::empty)
				.collect(Collectors.toList());

		emailParams.put("subject", subject);
		emailParams.put("senderEmailAddress", EMAIL_SENDER_ADDRESS);
		emailParams.put("senderName", EMAIL_SENDER_NAME);

		emailParams.put("pageLink", pageLink);
		emailParams.put("resource", resource);
		emailParams.put("failureReason", failureReason);
		emailParams.put("workflowModelTitle", workflowModelTitle);
		emailParams.put("workflowTitle", workflowTitle);
		emailParams.put("recipientName", recipientName);
		emailParams.put("workFlowDate", workflowInitiateDate);
		emailParams.put("environment", environment);

		emailService.sendEmail(template, emailParams, emailRecipients, ccRecipients);
	}

}
