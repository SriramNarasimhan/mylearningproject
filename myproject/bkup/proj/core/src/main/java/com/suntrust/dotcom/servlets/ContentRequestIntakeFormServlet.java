package com.suntrust.dotcom.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.AssetManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.suntrust.dotcom.beans.ContentRequestIntakeFormBean;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.utils.GenericEnum;
import com.suntrust.dotcom.utils.Utils;

/**
 * @author UGRK104
 *
 *Purpose - This servlet is used to process content in take form request.
 */

@SuppressWarnings("serial")
@SlingServlet(paths = "/dotcom/content-intake-search",methods = "POST")
public class ContentRequestIntakeFormServlet extends org.apache.sling.api.servlets.SlingAllMethodsServlet{
	
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentRequestIntakeFormServlet.class);
	
	/** Static variable holds "year" string. * */
	private static final String CURRENT_YEAR;
	/** Static variable holds "month" string. * */
	private static final String CURRENT_MONTH;
	/** static variable to hold a months collection in Map * */
	private static Map<Integer, String> monthMap = null;
	/** static variable. * */
	private static final String NOT_AVAILABLE = "N/A";
	/** instance variable to hold content intake form path details. * */
	private String contentRequestIntakeFormRootPath = null;
	/** instance variable to hold asset upload root path details. * */
	private String rootAssetUploadPath = null;
	/** instance variable * */
	private ContentRequestIntakeFormBean contentRequestIntakeFormBean = null;
	/** Boolean instance variable to hold form submission status * */
	private Boolean formSubmissionCheck = false;
	
	/**	Service variable to read the run-mode configurations * */
	@Reference
	private SuntrustDotcomService suntrustDotcomService;
	
	/**	Service variable to read the email configuration * */
	@Reference
	EmailService emailService;
	
	/**	Service variable to resolve resources * */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;	
		
	
	static{
		CURRENT_YEAR = "year";
		CURRENT_MONTH = "month";
		monthMap = new HashMap<Integer, String>();
		
		monthMap.put(1, "January");
		monthMap.put(2, "February ");
		monthMap.put(3, "March ");
		monthMap.put(4, "April ");
		monthMap.put(5, "May ");
		monthMap.put(6, "June ");
		monthMap.put(7, "July ");
		monthMap.put(8, "August ");
		monthMap.put(9, "September ");
		monthMap.put(10, "October ");
		monthMap.put(11, "November");
		monthMap.put(12, "December");
	}
	
	
   
    /* (non-Javadoc)
     * @see org.apache.sling.api.servlets.SlingAllMethodsServlet#doPost(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
     */
    @SuppressWarnings("unchecked")
	protected void doPost(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response) throws ServletException, IOException {
    	
    	//Encode the submitted form data to JSON
        JSONObject obj=new JSONObject();
        
        Map<String, String> currentYearMonthMap = getCurrentYearMonthMap();
		String currentYear = currentYearMonthMap.get(CURRENT_YEAR).trim();
		String currentMonth = currentYearMonthMap.get(CURRENT_MONTH).trim();
    	
    	response.setContentType("text/html");
    	
    	this.contentRequestIntakeFormBean = new ContentRequestIntakeFormBean();
    	this.contentRequestIntakeFormRootPath = suntrustDotcomService.getPropertyValue(GenericEnum.CONTENT_INTAKE_FORM_PATH.getValue());
    	this.rootAssetUploadPath = suntrustDotcomService.getPropertyValue(GenericEnum.CONTENT_INTAKE_FORM_ASSET_UPLOAD_PATH.getValue());
    	
    	Boolean damAssetUploadCheck = false;
    	Boolean uniqueIDPageCheck = false;
    	Page uniqueIDPage = null;
    	
    	ResourceResolver resourceResolver = null;
    	
    	LOGGER.info("ContentRequestIntakeFormServlet called");
    	
    	
        try
        {	
        	Map<String, Object> serviceParams = new HashMap<String, Object>();
			  serviceParams.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			  resourceResolver = resourceResolverFactory.getServiceResourceResolver(serviceParams);
			  
			  final boolean isMultipart = org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);
			  
			  LOGGER.info("isMultipart : " + isMultipart);
        	
        	
           //Get the submitted form data that is sent from the
                //CQ web page  
 			  String wfType = request.getParameter("wftype"),
 					 jobType = request.getParameter("Jobtype"),
 					 userName = request.getParameter("Name"),
 					 userEmailID = request.getParameter("Email"),
 					 userID = request.getParameter("racf"),
 					 legalReviewer = request.getParameter("selectedLegalReviewerName"),
 					 pageName = URLDecoder.decode(request.getParameter("encodedContentIntakePageName"), "UTF-8"),
 					 complianceReviewer = request.getParameter("selectedCompilanceReviewerName"),
 					 url = URLDecoder.decode(request.getParameter("encodedExistingUrls"), "UTF-8"),
 					 jobDescription = URLDecoder.decode(request.getParameter("encodeJobDescription"), "UTF-8"),
 					 suggestedTag = request.getParameter("tag"),
 					 uploadedAssetPath = request.getParameter("fileUpolad"),
 					 replaceExistingAsset = request.getParameter("replaceAsset"),
 					 assetExpirationDate = request.getParameter("expirationDateText"),
	 				 publishComments = URLDecoder.decode(request.getParameter("encodedPublishComments"), "UTF-8"),
	 				 publishType = request.getParameter("PublishType"),
	 				 plannedPublishdate = request.getParameter("publishDateText");
 			  
	  			uploadedAssetPath = StringUtils.substringAfterLast(uploadedAssetPath, "\\");
 			 
 			  	//Setting submitted values in beans
 			  	wfType = StringUtils.isEmpty(wfType) ? NOT_AVAILABLE : wfType.trim();
 			    
 			    String uniqueID = getUniquePageName(wfType);
 			    
 			    jobType = StringUtils.isEmpty(jobType) ? NOT_AVAILABLE : jobType.trim();
 			    userName = StringUtils.isEmpty(userName) ? NOT_AVAILABLE : userName.trim();
 			    userEmailID = StringUtils.isEmpty(userEmailID) ? NOT_AVAILABLE : userEmailID.trim();
 			    userID = StringUtils.isEmpty(userID) ? NOT_AVAILABLE : userID.trim();
 			    complianceReviewer = StringUtils.isEmpty(complianceReviewer) ? NOT_AVAILABLE : complianceReviewer.trim();
 			    legalReviewer = StringUtils.isEmpty(legalReviewer) ? NOT_AVAILABLE : legalReviewer.trim();
 			    pageName = StringUtils.isEmpty(pageName) ? NOT_AVAILABLE : pageName.trim();
 			    url = StringUtils.isEmpty(url) ? NOT_AVAILABLE : url.trim();
 			    //url = (url != null && !url.trim().equals(GenericEnum.EMPTY_STRING.getValue())) ? url.trim() : NOT_AVAILABLE;
 			    jobDescription = StringUtils.isEmpty(jobDescription) ? NOT_AVAILABLE : jobDescription.trim();
 			    suggestedTag = (suggestedTag != null && !suggestedTag.trim().equals(GenericEnum.EMPTY_STRING.getValue())) ? suggestedTag.trim() : NOT_AVAILABLE;
 			    uploadedAssetPath = (uploadedAssetPath != null && !uploadedAssetPath.trim().equals(GenericEnum.EMPTY_STRING.getValue())) ? uploadedAssetPath.trim() : NOT_AVAILABLE;
 			    
 			    replaceExistingAsset = StringUtils.isEmpty(replaceExistingAsset) ? NOT_AVAILABLE : replaceExistingAsset.trim();
 			    assetExpirationDate = StringUtils.isEmpty(assetExpirationDate) ? NOT_AVAILABLE : assetExpirationDate.trim();
	 			
	 			publishComments = (publishComments != null && !publishComments.trim().equals(GenericEnum.EMPTY_STRING.getValue())) ? publishComments.trim() : NOT_AVAILABLE;
	 			
	 			publishType = StringUtils.isEmpty(publishType) ? NOT_AVAILABLE : publishType.trim();
	 			
	 			plannedPublishdate = StringUtils.isEmpty(plannedPublishdate) ? NOT_AVAILABLE : plannedPublishdate.trim();
 			  
 	            
 	            contentRequestIntakeFormBean.setUniqueID(uniqueID);            
 	            contentRequestIntakeFormBean.setWorkflowType(wfType);            
 	            contentRequestIntakeFormBean.setJobType(jobType);
 	            contentRequestIntakeFormBean.setUserName(userName);
 	            contentRequestIntakeFormBean.setUserEmailID(userEmailID);
 	            contentRequestIntakeFormBean.setUserID(userID);
 	            contentRequestIntakeFormBean.setComplianceReviewer(complianceReviewer);
 	            contentRequestIntakeFormBean.setLegalReviewer(legalReviewer);
 	            contentRequestIntakeFormBean.setPageName(pageName);
 	            contentRequestIntakeFormBean.setUrl(url);
 	            contentRequestIntakeFormBean.setJobDescription(jobDescription);
 	            contentRequestIntakeFormBean.setSuggestedTag(suggestedTag);
 	            contentRequestIntakeFormBean.setUploadedAssetPath(uploadedAssetPath);   	           
 	            contentRequestIntakeFormBean.setReplaceExistingAsset(replaceExistingAsset); 
 	            contentRequestIntakeFormBean.setAssetExpirationDate(assetExpirationDate);
 	            contentRequestIntakeFormBean.setAssetComments(publishComments);
 	            contentRequestIntakeFormBean.setPublishType(publishType);
 	            contentRequestIntakeFormBean.setPlannedPublishDate(plannedPublishdate);
 			  
 			 if (isMultipart) {
 				 
 	             final Map<String, RequestParameter[]> params = request.getRequestParameterMap();
 	             for (final Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
 	            	 
 	               pairs.getKey();
 	               final RequestParameter[] pArr = pairs.getValue();
 	               
 	               for(RequestParameter requestParameter : pArr)
 	               {
 	            	   
 	            	   String fileUpolad = requestParameter.getName(); 	            	   
 	            	   if ("fileUpolad".equals(fileUpolad)) {
 	            		   
 	            		  InputStream inputStream = null;
 	            		 inputStream = requestParameter.getInputStream();
 	            		 
 	    	             LOGGER.info("stream.available() : " + inputStream.available());
 	    	             
 	    	             if (inputStream == null || inputStream.available() == 0) {
 	    					LOGGER.info("stream is null or empty");
 	    					damAssetUploadCheck = true;
 	    	             }
 	    	             else
 	    	             {
 	    	            	LOGGER.info("Content Type : " + requestParameter.getContentType()); 	    	            	
 	    	            	String mimeTypeValue = requestParameter.getContentType(); 	    	            	
 	    	            	String uploadedFileName = requestParameter.getFileName();
 	    	            	if(uploadedFileName.contains("\\")){
 	    	            		uploadedFileName = StringUtils.substringAfterLast(uploadedFileName, "\\");
 	    	            	}
 	    	            	LOGGER.info("Uploaded File Name : " + uploadedFileName);
 	    	            	damAssetUploadCheck = this.assetUploadToDAM(resourceResolver ,  response , inputStream , mimeTypeValue , uploadedFileName , contentRequestIntakeFormBean);
 	    	             }						
					}
 	               } 	               
 	             }
 	           }
            
 			if(damAssetUploadCheck)
            {
            	uniqueIDPage = this.createAEMPage(resourceResolver,contentRequestIntakeFormBean,request);
            	
            	if(uniqueIDPage != null)
                {
            		// Page is available
                	uniqueIDPageCheck = true;
                }
            	else{
            		
            		//delete uniquie ID Dam folder
            		
            		String uniqueIDAssetNodePath = this.rootAssetUploadPath + GenericEnum.BACKWORD_SLASH_SYMBOL.getValue() + currentYear + GenericEnum.BACKWORD_SLASH_SYMBOL.getValue() + currentMonth + GenericEnum.BACKWORD_SLASH_SYMBOL.getValue() + contentRequestIntakeFormBean.getUniqueID();
            		
            		removeNode(resourceResolver , uniqueIDAssetNodePath);
            		
	        		 obj.put("uniqueIDPageStatus","Form request page creation is failed"); 
	        		 obj.put("assetUploadStatus",GenericEnum.EMPTY_STRING.getValue());
            	}
            }
 			
 			else{
 				 obj.put("assetUploadStatus","Asset upload is failed");
 				obj.put("uniqueIDPageStatus",GenericEnum.EMPTY_STRING.getValue()); 
 			}
         
            
            
            obj.put("uniqueID", contentRequestIntakeFormBean.getUniqueID()); 
            
            if(damAssetUploadCheck && uniqueIDPageCheck)
            {
            	obj.put("formSubmisionStatus","successful");
            	
            	obj.put("assetUploadStatus",GenericEnum.EMPTY_STRING.getValue());
            	obj.put("uniqueIDPageStatus",GenericEnum.EMPTY_STRING.getValue());
            	// Email Notification
                this.sendEmail(contentRequestIntakeFormBean);
            }
            else
            {
            	obj.put("formSubmisionStatus","un-successful");
            }

             
            //Get the JSON formatted data    
            String jsonData = obj.toJSONString();
             
           //Return the JSON formatted data
           response.getWriter().write(jsonData);   
           
           
          
           
        }
        catch(Exception exception)
        {
        	LOGGER.error("Exception cought in doPost() method of the servlet ContentRequestIntakeFormServlet : " + exception);
        }

    }   
    
    

    
    /**
     * This method returns unique id page
     * @param resourceResolver {@link ResourceResolver}
     * @param contentRequestIntakeFormBean {@link ContentRequestIntakeFormBean}
     * @param request {@link SlingHttpServletRequest}
     * @return {@link Page}
     */
    public Page createAEMPage(ResourceResolver resourceResolver, ContentRequestIntakeFormBean contentRequestIntakeFormBean,SlingHttpServletRequest request) {

		Session session = resourceResolver.adaptTo(Session.class);
		
		String blankTemplatePath = suntrustDotcomService.getPropertyValue(GenericEnum.DOTCOM_BLANK_TEMPLATE_PATH.getValue());
		
		String contentFormDiaplayTemplatePath = suntrustDotcomService.getPropertyValue(GenericEnum.CONTENT_REQUEST_FORM_DATA_TEMPLATE_PATH.getValue());
		
		Map<String, String> currentYearMonthMap = getCurrentYearMonthMap();
		String currentYear = currentYearMonthMap.get(CURRENT_YEAR).trim();
		String currentMonth = currentYearMonthMap.get(CURRENT_MONTH).trim();
		//String uniquePageName = getUniquePageName();
		
		LOGGER.info("currentYear new : " + currentYear);
    	LOGGER.info("currentMonth new : " + currentMonth);
    	
    	
		Page yearPg = null;
		Page mpnthPg = null;
		Page uniqueIdPg = null;
		try {
			if (session != null) {
				
				// there will be root folder /content/suntrust/dotcom/contentrequestintakeform
				

				// Create Page
				PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
				
				yearPg = Utils.createCustomPage(this.contentRequestIntakeFormRootPath, currentYear , blankTemplatePath , currentYear , pageManager, false);
				
				if(yearPg != null)
				{
					mpnthPg = Utils.createCustomPage(yearPg.getPath(), currentMonth , blankTemplatePath, currentMonth , pageManager, false);
				}
				
				if(mpnthPg != null)
				{
					uniqueIdPg =  Utils.createCustomPage(mpnthPg.getPath(), contentRequestIntakeFormBean.getUniqueID(), contentFormDiaplayTemplatePath, contentRequestIntakeFormBean.getUniqueID(), pageManager, false);
				}
				
				if(uniqueIdPg != null )
				{
					Node uniqueIdPgNode = uniqueIdPg.getContentResource().adaptTo(Node.class);
					uniqueIdPgNode.setProperty("uniqueId", contentRequestIntakeFormBean.getUniqueID());
					uniqueIdPgNode.setProperty("wfType", contentRequestIntakeFormBean.getWorkflowType()); 
					uniqueIdPgNode.setProperty("jobType", contentRequestIntakeFormBean.getJobType()); 
					uniqueIdPgNode.setProperty("userName", contentRequestIntakeFormBean.getUserName()); 
					uniqueIdPgNode.setProperty("userEmailID", contentRequestIntakeFormBean.getUserEmailID()); 
					uniqueIdPgNode.setProperty("userID", contentRequestIntakeFormBean.getUserID()); 
					uniqueIdPgNode.setProperty("complianceReviewer", contentRequestIntakeFormBean.getComplianceReviewer()); 
					uniqueIdPgNode.setProperty("legalReviewer", contentRequestIntakeFormBean.getLegalReviewer()); 
					uniqueIdPgNode.setProperty("pageName", contentRequestIntakeFormBean.getPageName()); 
					uniqueIdPgNode.setProperty("jobDescription", contentRequestIntakeFormBean.getJobDescription()); 
					uniqueIdPgNode.setProperty("url", contentRequestIntakeFormBean.getUrl());
					uniqueIdPgNode.setProperty("suggestedTag", contentRequestIntakeFormBean.getSuggestedTag());
					uniqueIdPgNode.setProperty("uploadedAssetPath", contentRequestIntakeFormBean.getUploadedAssetPath());
					uniqueIdPgNode.setProperty("replaceExistingAsset", contentRequestIntakeFormBean.getReplaceExistingAsset()); 
					uniqueIdPgNode.setProperty("assetExpirationDate", contentRequestIntakeFormBean.getAssetExpirationDate());
					uniqueIdPgNode.setProperty("assetComments", contentRequestIntakeFormBean.getAssetComments()); 
					uniqueIdPgNode.setProperty("publishType", contentRequestIntakeFormBean.getPublishType());  
					uniqueIdPgNode.setProperty("plannedPublishDate", contentRequestIntakeFormBean.getPlannedPublishDate()); 
					
					resourceResolver.commit();
					
				}

				return uniqueIdPg;
			}

		} catch (Exception exception) {
			LOGGER.error("Exception cought in createAEMPage() Method : " + exception);
		}
		

		return null;
	}

    
    /**
     * This method returns asset upload status.
     * 
     * @param resourceResolver {@link ResourceResolver}
     * @param response {@link SlingHttpServletResponse}
     * @param inputStream {@link InputStream}
     * @param mimeTypeValue {@link String}
     * @param uploadedFileName {@link String}
     * @param contentRequestIntakeFormBean {@link ContentRequestIntakeFormBean}
     * @return {@link Boolean}
     */
    public Boolean assetUploadToDAM(ResourceResolver resourceResolver , SlingHttpServletResponse response , InputStream inputStream , String mimeTypeValue , String uploadedFileName , ContentRequestIntakeFormBean contentRequestIntakeFormBean) {
		
    	
    	
    	try {
			// Use AssetManager to place the file into the AEM DAM
			AssetManager assetMgr = resourceResolver.adaptTo(AssetManager.class);
			String newDAMAssetPath = null;
			Map<String, String> currentYearMonthMap = getCurrentYearMonthMap();
			String currentYear = currentYearMonthMap.get(CURRENT_YEAR).trim();
			String currentMonth = currentYearMonthMap.get(CURRENT_MONTH).trim();
			LOGGER.info(this.rootAssetUploadPath + " upload path");
			Resource assetUploadRes = resourceResolver.getResource(this.rootAssetUploadPath);
			
			if(assetUploadRes != null)
			{
				Node assetUploadNode = assetUploadRes.adaptTo(Node.class);
				Node currentYearNode = null;
				Node currentMonthNode = null;
				Node uniqueIDNode = null;
				
				if(!assetUploadNode.hasNode(currentYear))
				{
					LOGGER.info(currentYear + " node not exist");
					
					currentYearNode = createNode(resourceResolver, assetUploadNode, currentYear);
					
					currentMonthNode = createNode(resourceResolver, currentYearNode, currentMonth);

				}
				
				else
				{
					currentYearNode = assetUploadNode.getNode(currentYear);
					
					if(!currentYearNode.hasNode(currentMonth))
					{
						LOGGER.info(currentMonth + " node not exist");
						
						currentMonthNode = createNode(resourceResolver, currentYearNode, currentMonth);
					}
					else
					{
						currentMonthNode = currentYearNode.getNode(currentMonth);
					}					
					
				}
				
				uniqueIDNode = createNode(resourceResolver, currentMonthNode, contentRequestIntakeFormBean.getUniqueID());
				
				newDAMAssetPath = uniqueIDNode.getPath() + GenericEnum.BACKWORD_SLASH_SYMBOL.getValue() + uploadedFileName;
				
				LOGGER.info("newDAMAssetPath : " + newDAMAssetPath);
				
				assetMgr.createAsset(newDAMAssetPath, inputStream,mimeTypeValue, true);
	
				this.formSubmissionCheck = true;
				contentRequestIntakeFormBean.setUploadedAssetPath(newDAMAssetPath);
				resourceResolver.commit();
			}
			
			else
			{
				this.formSubmissionCheck = false;
			}
			
			

		} catch (Exception exception) {
			LOGGER.error("Exception cought in assetUploadToDAM() Method. Message: {}, Trace: {} ", exception.getMessage(),exception);
			this.formSubmissionCheck = false;
		}

		return this.formSubmissionCheck;
	}
    
    
    
    /**
     * This method returns a Map collection of current year and month
     * @return {@link Map}
     */
    private static Map<String, String> getCurrentYearMonthMap()
    {

    	Map<String, String> currentYearMonthMap = new HashMap<String, String>();
    	Calendar now = Calendar.getInstance();
    	new DateFormatSymbols();
    	
    	String currentYear = Integer.toString(now.get(Calendar.YEAR));
    	String currentMonth = monthMap.get(now.get(Calendar.MONTH) + 1);
    	currentYearMonthMap.put(CURRENT_YEAR, currentYear);
    	currentYearMonthMap.put(CURRENT_MONTH, currentMonth);
    	
    	return currentYearMonthMap;
    }
    
    
    
    /**
     * @param wfType (@link String}
     * @return unique id page name.
     * @throws StringIndexOutOfBoundsException {@link StringIndexOutOfBoundsException}
     */
    private static String getUniquePageName(String wfType) throws StringIndexOutOfBoundsException
    {
    	//TODO - Check for location for SDF generation.
    	SimpleDateFormat SDF = new SimpleDateFormat("MMddyyHHmmss");
    	Map<String, String> map = getCurrentYearMonthMap();
    	map.get(CURRENT_MONTH).trim();
    	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    	
    	return wfType.substring(0,2) + GenericEnum.DASH_SYMBOL.getValue() + SDF.format(timestamp);
    }
    
    
    
    /**
     * @param resourceResolver {@link ResourceResolver}
     * @param node {@link Node}
     * @param nodeName {@link String}
     * @return newly created node.
     */
    private static Node createNode(ResourceResolver resourceResolver , Node node , String nodeName)
    {
    	
    	Node newJcrNode = null;
    	
    	try
    	{
    		newJcrNode = node.addNode(nodeName,"sling:OrderedFolder");
			Node jcrContentNode = newJcrNode.addNode("jcr:content", "nt:unstructured");
			jcrContentNode.setProperty("jcr:title", nodeName);
			resourceResolver.commit();
    	}
    	catch(Exception exception)
    	{
    		LOGGER.error("Exception cought in createNode(ResourceResolver resourceResolver , Node node , String nodeName) method : " + exception);
    	}
    	   	
    	
		return newJcrNode;
    	
    }
    
    
    
    
    /**
     * @param resourceResolver {@link ResourceResolver}
     * @param nodePath {@link String}
     * @return jcr node removal status.
     */
    private static Boolean removeNode(ResourceResolver resourceResolver , String nodePath)
    {
    	
    	Node jcrNode = null;
    	
    	try{
    		Resource resource = resourceResolver.getResource(nodePath);
        	
    		if(resource != null)
    		{    			
    			jcrNode = resource.adaptTo(Node.class);
    			jcrNode.remove();
    			resourceResolver.commit();
    			return true;
    		}
    	}
    	catch(Exception exception)
    	{
    		LOGGER.error("Exception cought in the removeNode(ResourceResolver resourceResolver , String nodePath) function : " + exception);
    		if (LOGGER.isDebugEnabled()) {
				exception.getStackTrace();
			} 
    	}    	
		   	
    	return false; 
    }
    
    
    
    
    /**
     * This method will send email to DCM Team.
     * @param contentRequestIntakeFormBean {@link ContentRequestIntakeFormBean}
     */
    private void sendEmail(ContentRequestIntakeFormBean contentRequestIntakeFormBean){
		
		Map<String,String> emailParams=new HashMap<>();
		
		String subject = contentRequestIntakeFormBean.getUniqueID() + GenericEnum.DASH_SYMBOL.getValue() + contentRequestIntakeFormBean.getPageName();		
		String notificationTemplatePath = suntrustDotcomService.getPropertyValue(GenericEnum.CONTENT_FORM_EMAIL_NOTIFICATION_TEMPLATE_PATH.getValue());
		
		notificationTemplatePath = StringUtils.isEmpty(notificationTemplatePath) ? GenericEnum.EMPTY_STRING.getValue() : notificationTemplatePath.trim();
		
		List<String> emailRecipients = new ArrayList<String>();
		List<String> ccRecipients = new ArrayList<String>();
		
		String emailID = suntrustDotcomService.getPropertyValue(GenericEnum.DL_DIGITAL_CONTENT_MGMT_EMAIL_ID.getValue());
		String recipientEmailID = StringUtils.isEmpty(emailID) ? contentRequestIntakeFormBean.getUserEmailID() : emailID.trim();
		
		emailRecipients.add(recipientEmailID);
		ccRecipients.add(contentRequestIntakeFormBean.getUserEmailID());
		emailParams.put("subject", subject);
		emailParams.put("senderEmailAddress",GenericEnum.SENDER_EMAIL_ADDRESS.getValue());  
		emailParams.put("senderName",GenericEnum.SENDER_NAME.getValue());
		
		emailParams.put("uniqueId",contentRequestIntakeFormBean.getUniqueID());
		emailParams.put("wfType",contentRequestIntakeFormBean.getWorkflowType());
		emailParams.put("jobType", contentRequestIntakeFormBean.getJobType());
		emailParams.put("userName", contentRequestIntakeFormBean.getUserName());
		emailParams.put("userEmailID", contentRequestIntakeFormBean.getUserEmailID());
		emailParams.put("userID", contentRequestIntakeFormBean.getUserID());
		emailParams.put("pageName", contentRequestIntakeFormBean.getPageName());
		emailParams.put("complianceReviewer", contentRequestIntakeFormBean.getComplianceReviewer());
		emailParams.put("legalReviewer", contentRequestIntakeFormBean.getLegalReviewer());
		emailParams.put("jobDescription", contentRequestIntakeFormBean.getJobDescription());
		
		String uploadedAssetPath = (contentRequestIntakeFormBean.getUploadedAssetPath() != null && !contentRequestIntakeFormBean.getUploadedAssetPath().equals(GenericEnum.EMPTY_STRING.getValue())) ? contentRequestIntakeFormBean.getUploadedAssetPath() : NOT_AVAILABLE;
		
		emailParams.put("url", contentRequestIntakeFormBean.getUrl());
		emailParams.put("suggestedTag", contentRequestIntakeFormBean.getSuggestedTag());
		emailParams.put("uploadedAssetPath", uploadedAssetPath);
		emailParams.put("replaceExistingAsset", contentRequestIntakeFormBean.getReplaceExistingAsset());
		emailParams.put("assetExpirationDate", contentRequestIntakeFormBean.getAssetExpirationDate());
		emailParams.put("assetComments", contentRequestIntakeFormBean.getAssetComments());
		emailParams.put("publishType", contentRequestIntakeFormBean.getPublishType());
		emailParams.put("plannedPublishDate", contentRequestIntakeFormBean.getPlannedPublishDate());
		
		
		
		emailService.sendEmail(notificationTemplatePath, emailParams, emailRecipients, ccRecipients);
	}

}
