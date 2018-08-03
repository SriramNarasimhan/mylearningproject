package com.suntrust.dotcom.workflow;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.Externalizer;
import com.day.cq.workflow.collection.ResourceCollection;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.day.cq.workflow.collection.ResourceCollectionUtil;
import com.suntrust.dotcom.config.AssetConfigService;
import com.suntrust.dotcom.services.EmailService;

/**
 * @author ugjy26
 *
 */
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Asset Archival Workflow Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Asset Archival Workflow Service") 
})
public class AssetArchivalProcess implements WorkflowProcess{  	 
    @Reference
	private ResourceResolverFactory resourceResolverFactory;
    @Reference
	private SlingSettingsService slingSettings;
    @Reference
	private ResourceCollectionManager resourceCollectionManager;
    @Reference
    private AssetConfigService assetService;
    @Reference
    private EmailService emailService;
    
    ResourceResolver resourceResolver=null;
	Session session=null;
	Externalizer externalizer=null;
	UserManager userManager=null;
	private static final String NOTIFICATION_TEMPLATEPATH="/etc/notification/email/html/dotcom/assetarchivalnotification.html";
	private static final String EMAIL_SENDER_ADDRESS="cms@SunTrust.com";
	private static final String EMAIL_SENDER_NAME="SunTrust CMS";
	private static final String PUBLISHEDPATH="publishedpath";
	private static final String[] DEFAULT_WF_PACKAGE_TYPES = {"dam:Asset"};
	private String workflowModelTitle=null;
	private String pageLink=null;
	private String recipientName=null;
	private String workflowInitiateDate=null;
	private String workflowTitle=null;
	private String environment=null;
	private String[] toAddress=null;
	private String[] ccAddress=null;

	private String emailId = ""; 
	
	private String   assetOutputDirectory=null;
	SimpleDateFormat folderFormat=new SimpleDateFormat("MMddyyyy");
	SimpleDateFormat fileFormat=new SimpleDateFormat("HHMMSSss"); 
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AssetArchivalProcess.class);
	@Override
	public void execute(WorkItem workItem, WorkflowSession workSession, MetaDataMap metaDataMap) throws WorkflowException {
		try {				
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
		resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
		externalizer= resourceResolver.adaptTo(Externalizer.class);
		session = resourceResolver.adaptTo(Session.class);
		userManager= resourceResolver.adaptTo(UserManager.class);
		
		String payLoadPath=workItem.getWorkflowData().getPayload().toString();
		Map<String, List<String>> absolutePublishPathsMap=getAbsolutePublishPaths(payLoadPath);
		List<String> absolutePublishPathsList=absolutePublishPathsMap.get(PUBLISHEDPATH); 
		
		workflowModelTitle=workItem.getWorkflow().getWorkflowModel().getTitle();
		workflowTitle=workItem.getWorkflow().getWorkflowData().getMetaDataMap().get("workflowTitle", String.class);
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
		workflowInitiateDate=formatter.format(workItem.getWorkflow().getTimeStarted());
		
		String initiator=workItem.getWorkflow().getInitiator();
		Authorizable userAuthorizable=userManager.getAuthorizable(initiator);
		
		emailId=userAuthorizable.getProperty("./profile/email")!=null?userAuthorizable.getProperty("./profile/email")[0].getString():null;
		String givenName=userAuthorizable.getProperty("./profile/givenName")!=null?userAuthorizable.getProperty("./profile/givenName")[0].getString():"";
		String familyName=userAuthorizable.getProperty("./profile/familyName")!=null?userAuthorizable.getProperty("./profile/familyName")[0].getString():"";
		
		recipientName=givenName+" "+familyName+"["+"USCORP"+"\\"+initiator+"]";				
		
		if(null==assetService.getEmailTo())
			throw new IllegalArgumentException("Please configure Email user list in Suntrust Asset Service Configuration");
		
		toAddress=assetService.getEmailTo(); 		
		ccAddress=assetService.getEmailCC();		
		environment=externalizer.authorLink(resourceResolver, "");			
		assetOutputDirectory=slingSettings.getAbsolutePathWithinSlingHome(assetService.getAssetOutputPath())+"/"+folderFormat.format(new Date());
			if(absolutePublishPathsList.size() != 0){
				createArchivalAsset(absolutePublishPathsList);	
			}else{
				LOGGER.debug("Assets are not atatched to the workflow for Archival");
			}			
		} catch (IllegalArgumentException | RepositoryException | LoginException  e) {			
			LOGGER.error("AssetArchival Execute Method : Message: {}, Trace: {}",e.getMessage(), e);
		}	        
	}		
	
	private void createArchivalAsset(List<String> absolutePublishPathsList){
		LOGGER.debug("createArchivalAsset called");
		absolutePublishPathsList.stream().forEach(page -> {
			try{
				String requestPath=null;				
				requestPath=page;
				String[] matchedMimeType= assetService.getMimeType();		    			    																 					   
				    try {					    		
					    	Resource resource = resourceResolver.getResource(requestPath);					    						    	
					    	if(resource != null){
					    		String fileExtension = FilenameUtils.getExtension(requestPath);					    		
						    	Asset asset = resource.adaptTo(Asset.class);
						    	String metadata = asset.getRendition("original").getMimeType().toString(); 
						    	for(String mimeType :matchedMimeType){						    		
							    	if(metadata != "" && metadata != null  && metadata.contains(mimeType)){	
							    		LOGGER.debug("macthed type:: "+metadata);
								    	InputStream inputStream = asset.getRendition("original").getStream();  															
										String assetFileName = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(requestPath, "/"),".")+"_"+folderFormat.format(new Date())+fileFormat.format(new Date())+"."+fileExtension;								
										LOGGER.debug("AssetCreation==> Asset Archival Output path: &  assetFileName::"+assetOutputDirectory+"::"+assetFileName);
										File outputFile = new File(assetOutputDirectory,assetFileName);
										if(outputFile.getParentFile() !=null && !outputFile.getParentFile().exists())
										   outputFile.getParentFile().mkdirs();
										FileOutputStream outputStream = new FileOutputStream(outputFile,false);								
										boolean status = copyInputStreamToFile(outputStream,inputStream);
										if(!status){
											LOGGER.debug("AssetCreation==> Asset Archival failed during creation::"+requestPath);
											pageLink = requestPath;
											sendEmail();
										}
							    	}else{
							    		LOGGER.debug("AssetCreation==> Asset doesn't have application/ or text/ MIME Type");
							    	}
						    	}
							}																			
						} catch (Exception e) {													
							LOGGER.error("AssetArchival assetCheck 1 : Message: {}, Trace: {}",e.getMessage(), e);
						}																																				
			}catch(Exception e){				
				LOGGER.error("AssetArchival assetCheck 2 : Message: {}, Trace: {}",e.getMessage(), e);
			}			
		});		
	}	
	
	private boolean copyInputStreamToFile(FileOutputStream outputStream,InputStream inputStream) { 
		boolean status = false;
		try{
		        byte[] buffer = new byte[10*1024];
		        for (int length; (length = inputStream.read(buffer)) != -1; ){
		            outputStream.write(buffer, 0, length);
		        }
		        status = true;
		    }catch (FileNotFoundException e){
		    	LOGGER.error("AssetArchival copyInputStreamToFile FileNotFoundException: {}, Trace: {}",e.getMessage(), e);
		    	status =  false;
		    }catch (IOException e){
		    	LOGGER.error("AssetArchival copyInputStreamToFile IOException: {}, Trace: {}",e.getMessage(), e);
		    	status =  false;
		    }finally{
		    	try {
					outputStream.close();
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}		    			    	
		    }		
		return status;
	}	
	
	private Map<String, List<String>> getAbsolutePublishPaths(String payloadPath) throws RepositoryException{
		Map<String, List<String>> absolutePaths = new HashMap<>();		
		Node payLoadNode = session.getNode(payloadPath).getNode("jcr:content");		
		NodeType[] nodeType = payLoadNode.getMixinNodeTypes();				
		boolean iswfPkg= Arrays.asList(nodeType).stream().anyMatch(node -> "vlt:Package".equalsIgnoreCase(node.getName()));		
		if(iswfPkg){			  
			  absolutePaths.put(PUBLISHEDPATH,getWorkflowPkgPublishPaths(payLoadNode));
		}else{
			  List<String> payloadAbsolutePublishPath=new ArrayList<>();		
			  if(payloadPath.contains("/content/dam/")){
				  payloadAbsolutePublishPath.add(payloadPath);
			  }			 
			  absolutePaths.put(PUBLISHEDPATH, payloadAbsolutePublishPath);
		}					
		return absolutePaths;
	}
	
	private List<String> getWorkflowPkgPublishPaths(Node payLoadNode) throws RepositoryException{  		
		List<String> payloadAbsolutePublishPath=new ArrayList<String>(); 
		final ResourceCollection resourceCollection = ResourceCollectionUtil.getResourceCollection(payLoadNode, resourceCollectionManager);
		if(null!=resourceCollection){
			final List<Node> members = resourceCollection.list(DEFAULT_WF_PACKAGE_TYPES);
			members.stream().forEach(member-> {
					   try{						    
							 payloadAbsolutePublishPath.add(member.getPath()); 						  
					   }catch(RepositoryException e){						   
						   LOGGER.error("AssetCreation RepositoryException==>: Message: {}, Trace: {}",e.getMessage(), e); 
					   }					   
				   });
			}
		return payloadAbsolutePublishPath;
	}
			
	private void sendEmail(){	 	
		Map<String,String> emailParams=new HashMap<>();
		String subject="Document creation failed for asset(s)";
		String template=NOTIFICATION_TEMPLATEPATH;		
		List<String> emailRecipients = null; 
		
		if(StringUtils.isBlank(emailId)){			
			emailRecipients = Arrays.asList(toAddress);	
		}else{
			emailRecipients = new ArrayList<String>();
			emailRecipients.add(emailId); 	 	
		}		
		List<String> ccRecipients = Optional.ofNullable(ccAddress)
				.map(Arrays::stream).orElseGet(Stream::empty)
				.collect(Collectors.toList());
		
		emailParams.put("subject", subject);
		emailParams.put("senderEmailAddress",EMAIL_SENDER_ADDRESS);  
		emailParams.put("senderName",EMAIL_SENDER_NAME);		
		emailParams.put("pageLink", pageLink);				
		emailParams.put("workflowModelTitle", workflowModelTitle);
		emailParams.put("workflowTitle", workflowTitle);
		emailParams.put("recipientName",recipientName);
		emailParams.put("workFlowDate", workflowInitiateDate);
		emailParams.put("environment", environment);
		
		emailService.sendEmail(template, emailParams, emailRecipients, ccRecipients); 
	}

}
