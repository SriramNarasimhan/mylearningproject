package com.suntrust.dotcom.workflow;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.commons.Externalizer;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.workflow.WorkflowService;
import com.suntrust.dotcom.beans.AdvisorProfileBean;
import com.suntrust.dotcom.beans.LocationBean;
import com.suntrust.dotcom.config.AdvisorConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.scheduler.AdvisorLoadHelper;
import com.suntrust.dotcom.scheduler.AdvisorSyncHelper;
import com.suntrust.dotcom.scheduler.DBConnectionManager;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.services.ServiceAgentService;
import com.suntrust.dotcom.services.WorkflowPackageManager;
import com.suntrust.dotcom.utils.AWSUtils;
import com.suntrust.dotcom.utils.Utils;


@Component(immediate=true, enabled=true, metatype=true)
@Service(value=WorkflowProcess.class)
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Advisor Load Process"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust Advisor Load Process") 
})
public class AdvisorLoadProcess implements WorkflowProcess{

	/** RolloutManager object reference variable*/
    //private RolloutManager rolloutManager;

    /** Logger variable*/
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorLoadProcess.class);

    /** AdvisorConfigService object reference variable*/
    //private AdvisorConfigService configService;

    /** Session object reference variable*/
    private Session session;
    
    /** ResourceResolver object reference variable*/
    private ResourceResolver resolver;
    
    /** ReplicationStatus object reference variable*/
    private ReplicationStatus replicationStatus=null;
    
    /** Replicator object reference variable*/   
	//private Replicator replicator = null;
    
    /** String array list to store activate urls*/
    private ArrayList<String> activateUrl;

    /** String variable to hold root path*/
    private static String pageRootPath = null;
    
    /** String variable to hold blue print page path*/
    private static String bluePrintPagePath =  null;
    
    /** String variable to hold live copy templath path*/
    private static String liveCopyTemplateDefault = null;
    
    /** Workflow package name*/
    private String packageName = "";
    
    /** WorkflowPackageManager object reference variable*/
    //private WorkflowPackageManager workflowPackageManager = null;
    
    /** WorkflowService object reference variable*/
    //private WorkflowService wfService;
    
    /** SlingHttpServletRequest object reference variable*/
    private SlingHttpServletRequest request = null;
    
    /** RolloutManager object reference variable*/
    //private SuntrustDotcomService suntrustDotcomService = null;
    
    /** Connection object reference variable*/
	private Connection connection = null;
	
	/** Statement object reference variable*/
	private Statement statement = null;
	
	/** ResultSet object reference variable*/
	private  ResultSet resultSet = null;
	
	/** Advisor table query*/
	private String advisorSqlQry = "SELECT * FROM tbl_delta_ftp_advisors";
	
	/** Address SQL query*/
	private String advisorAddressSql = "SELECT * FROM tbl_delta_ftp_advisors_address order by EmailAddress";
	
	/** DBConnectionManager class reference variable*/
	private DBConnectionManager connectionManager = null;
	
	/** Root page object variable */
	private Page locationRootPage = null;
	
	/** Page Manager object variable */
	private PageManager pageManager = null;	
	
	//Newly added variables for email
	/** Variable to hold approver group */
	private String approverGroup = null;
		
	/** UserManager class reference variable */
	private UserManager userManager = null;
    
	 /** Externalizer class reference variable */
	private Externalizer externalizer=null;
		
	/** Variable to hold environment */
	private String environmentDetails = null;
	
	/** EmailService object reference variable*/   
	//private EmailService emailService;
	
	/** DataSourcePool class reference variable */
	@Reference
	private DataSourcePool dataSourcePool;
	
	/** SuntrustDotcomService class reference variable */
	@Reference
	private SuntrustDotcomService suntrustDotcomService;
	
	/** ServiceAgentService class reference variable */
	@Reference
	private ServiceAgentService serviceAgentService;
	
	/** WorkflowPackageManager class reference variable */
	@Reference
	private WorkflowPackageManager workflowPackageManager;

	/** WorkflowService class reference variable */
    @Reference
    private WorkflowService wfService;
    
    /** AdvisorConfigService class reference variable */
    @Reference
    private AdvisorConfigService configService;
    
    /** RolloutManager class reference variable */
    @Reference
    private RolloutManager rolloutManager;
	
	/** Replicator object reference variable*/
	@Reference
	private Replicator replicator;
	
	/** EmailService class reference variable*/
	@Reference
	private EmailService emailService;
	
	/** Approve notification email template path */
	private static final String NOTIFICATION_TEMPLATEPATH = "/etc/notification/email/html/dotcom/advisorstatusemailnotification.html";

    /**
     * Main method that calls respective methods to create/update people page and initiates workflow.
     * Workflow will not be initiated if page create/update count is zero 
     * 
     * @return
     * @throws IOException
     */
    @Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap metaDataMap) {
    	 int count = 0;
    	 WorkflowData data=null;
    	 LOGGER.debug("*** People Servlet *****");
        try {
        	
        	LOGGER.debug("*** Table Update Start*****");
        	AdvisorSyncHelper syncHelper = new AdvisorSyncHelper();
        	syncHelper.loadAdvisors(dataSourcePool, suntrustDotcomService, serviceAgentService, emailService, configService);
        	LOGGER.debug("Table update completed");
        	
        	LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();
            int hour = now.getHour();
            int minute = now.getMinute();
            int second = now.getSecond();
            packageName = ""+month+"_"+day+"_"+year+"_"+hour+minute+second;
            
            //this.resolver = request.getResourceResolver();
            this.resolver = serviceAgentService.getServiceResourceResolver("dotcomreadservice");
            this.session= resolver.adaptTo(Session.class);
            this.pageManager = resolver.adaptTo(PageManager.class);
            activateUrl= new ArrayList<String>();
            LOGGER.debug("==>session::"+session.getUserID());
            pageRootPath = configService.getPropertyValue("ROOT_PATH");
            bluePrintPagePath = configService.getPropertyValue("BLUEPRINT_PAGE_PATH");
            liveCopyTemplateDefault = configService.getPropertyValue("LIVE_COPY_TEMPLATE");
            
            LOGGER.debug("==>PeopleLoad:Start load:"+new Date());
            this.connectionManager = new DBConnectionManager(dataSourcePool);
            List<AdvisorProfileBean> peopleProfileBeanArrayList = getPeopleData();
            LOGGER.debug("Release 2 issue peopleProfileBeanArrayList >>"+peopleProfileBeanArrayList.size());
            
            LOGGER.debug("==>PeopleLoad:start people page creation:"+new Date());
            count = createPeoplePages(peopleProfileBeanArrayList,request);
            LOGGER.debug("==>PeopleLoad:end people page creation:"+count);
            peopleProfileBeanArrayList.clear();
            //payload path set to empty
            data = workflowSession.newWorkflowData("JCR_PATH", StringUtils.EMPTY);
            workflowSession.updateWorkflowData(workItem.getWorkflow(), data);
            if(count > 0){
			    LOGGER.debug("wfSession:"+workflowSession);
			    data =  workflowSession.newWorkflowData("JCR_PATH", "/etc/workflow/packages/advisor/"+packageName);
			    workflowSession.updateWorkflowData(workItem.getWorkflow(), data);
			    //workItem.getWorkflowData().getMetaDataMap().put("pageCount", count);
			} else {
				LOGGER.debug("No page creation/update available.");
			}
            //workItem.getMetaDataMap().put("pageCount", count);
            workItem.getWorkflowData().getMetaDataMap().put("pageCount", count);
            //my logic goes here
            checkNMLSFromDB();             
            
        } catch (Exception e) {
        	LOGGER.error("Exception: Message 1: {}, Trace: {}",e.getMessage(), e);
        } finally {
        	closeResultSet(resultSet);
        	closeStatement(statement);			
			closeConnection(connection);			
        }
    }
    
    /*public void sendArgs(Replicator replicator, EmailService emailService){
    	this.replicator = replicator; 
    	this.emailService = emailService; 
    }*/

    private Map<Integer, String> readDBAdvisorData(){    	
    	String advisorSqlQry = "SELECT NMLS_Registry_ID, SunTrust_Registry_Status FROM tbl_ftp_PeopleFinderComplianceData WHERE NMLS_Registry_ID IS NOT NULL";   
    	/** advisorMap Variable holds the advisor page status*/
    	Map<Integer, String> advisorMap = new HashMap<Integer, String>();
    	if(connectionManager != null) {    		 
             connection = connectionManager.getConnection();
             statement = connectionManager.getStatement();                         
             try {
				 resultSet = statement.executeQuery(advisorSqlQry);
				  LOGGER.debug("Release 2 issue statement readDBAdvisorData>>"+statement); 
                  LOGGER.debug("Release 2 issue resultSet readDBAdvisorData>>"+resultSet); 
				 // Iterate through the data in the result set and display it.	            	             
	             while (resultSet.next()) {	            	            
	            	 advisorMap.put(resultSet.getInt("NMLS_Registry_ID"), resultSet.getString("SunTrust_Registry_Status"));	     	            	
	             }	
	             LOGGER.debug("advisorMap.size is"+advisorMap.size());
			} catch (SQLException e) {
                e.printStackTrace();				
				LOGGER.error("SQL Exception: Message: {}, Trace: {}",e.getMessage(), e);
			}                           
    	 }
		return advisorMap; 
    	
    }
    private void checkNMLSFromDB() {
    	LOGGER.debug("checkNMLSFromDB is called");    	 
    	Set<String> awsUrlSet = new HashSet<String>(); 
    	Set<String> dispUrlSet = new HashSet<String>(); 
    	List<String> activateList = new ArrayList<String>();
    	List<String> inActivateList = new ArrayList<String>();
    	Map<Integer, String> advisorMap = new HashMap<Integer, String>();
    	advisorMap = readDBAdvisorData();       	
    	QueryManager queryManager = null;
    	try {
			queryManager = session.getWorkspace().getQueryManager();
		} catch (RepositoryException e) {			
			LOGGER.error("Repository Exception: Message: {}, Trace: {}",e.getMessage(), e);
		}
    	
    	String sqlStatement = "";
    	String contextUrl = "/content/suntrust/dotcom/us/en/profile";	    	
    	String QUERYSELECTSTATEMENT = "SELECT * FROM [cq:Page] AS s WHERE ISDESCENDANTNODE([";	
    	
		sqlStatement = QUERYSELECTSTATEMENT + contextUrl + "])";
		Query query;
		try {
			query = queryManager.createQuery(sqlStatement, "JCR-SQL2");
			QueryResult result = query.execute();
			NodeIterator iterator = result.getNodes();
			List<String> canonicalUrl = suntrustDotcomService.getPropertyArray("canonical.urls");
			while (iterator.hasNext()) {
				Node profileNode = iterator.nextNode();
				String path = profileNode.getPath();               				
				Node jcrcontentNode = profileNode.getNode("jcr:content");
				if(jcrcontentNode != null && jcrcontentNode.hasProperty("adv_nmls"))
				{
					String nmlsId = jcrcontentNode.getProperty("adv_nmls").getString();					
					if(StringUtils.isNotBlank(nmlsId))
					{						
						int convert_nmlsId = Integer.parseInt(nmlsId);
						String status = advisorMap.get(convert_nmlsId); 						
						if(status != null && !status.isEmpty()){ 																				
							replicationStatus=replicator.getReplicationStatus(session, path);							
							if(status.equals("Active")){								
								if(replicationStatus.isActivated()){									
								}else{																
									try {										
										replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
									} catch (ReplicationException e) {										
										e.printStackTrace();
										LOGGER.error("Replication Exception 3: Message: {}, Trace: {}",e.getMessage(), e);
									}
									String pulisherCanonicalPath = Utils.getCanonicalUrl(canonicalUrl,path,resolver); 
									LOGGER.debug("pulisherCanonicalPath"+pulisherCanonicalPath);
									awsUrlSet.add(pulisherCanonicalPath+"*");
									activateList.add(pulisherCanonicalPath); 
								}																
							}else if(status.equals("Inactive")){
								if(replicationStatus.isDeactivated()){									
								}else{
									try {
										replicator.replicate(session, ReplicationActionType.DEACTIVATE, path);
									} catch (ReplicationException e) {										
										LOGGER.error("Replication Exception 4: Message: {}, Trace: {}",e.getMessage(), e);
									}
									String pulisherCanonicalPath = Utils.getCanonicalUrl(canonicalUrl,path,resolver); 
									awsUrlSet.add(pulisherCanonicalPath+"*");
									inActivateList.add(pulisherCanonicalPath);
								}								
							}else{
								LOGGER.debug("Entry doesn't exist in DB");
							}
						}						
					}						
				}								
			}	
			//do the aws cache flush & sitemap profile sitemap and index			
			String advisorPath=configService.getPropertyValue("ROOT_PATH")+"/profile";
			awsUrlSet.add(Utils.getCanonicalUrl(canonicalUrl, advisorPath + ".index.xml", resolver));
			//we dont need to add the url to flush if its lob page is in the level1
			dispUrlSet.add(advisorPath + ".index.xml");
			
			//site map lob page is getting update,so flush the sitemap index page 
			String indexpage = suntrustDotcomService.getPropertyValue("sitemap.index.url");
			awsUrlSet.add(Utils.getCanonicalUrl(canonicalUrl,indexpage+".index.xml",resolver));
			dispUrlSet.add(indexpage+".index.xml");
			
			AWSUtils.flushAWSCache(awsUrlSet, suntrustDotcomService);
			AWSUtils.flushDispatcher(dispUrlSet, "Activate", suntrustDotcomService);			 			
			
			sendEmail(activateList,inActivateList);
			
		} catch (InvalidQueryException e) {
			LOGGER.error("InvalidQuery Exception 1: Message: {}, Trace: {}",e.getMessage(), e);
		} catch (RepositoryException e) {
			LOGGER.error("Repository Exception 1 : {}, Trace: {}",e.getMessage(), e);
		}								
	}
    
    
    private void sendEmail(List<String> activateList, List<String> inActivateList){
    	
    	// Send email with the list of the activated/deactivated page paths			
		userManager = resolver.adaptTo(UserManager.class);
		externalizer= resolver.adaptTo(Externalizer.class);
		environmentDetails = externalizer.publishLink(resolver, ""); 
		LOGGER.debug("environemt: "+environmentDetails);
					
		if(null != suntrustDotcomService){
			approverGroup = suntrustDotcomService.getPropertyValue("dotcom_people_finder_author");
		}			
		List<String> emailRecipients =  new ArrayList<String>();					
		emailRecipients = Utils.setRecipients(approverGroup,userManager);																	
					
		/*Create List of activated profiles*/								
		StringJoiner activateJoiner = new StringJoiner("</li>","","</li>");
		if(activateList.isEmpty()){
			activateJoiner.add("None");
		}else{
			activateList.stream().forEach((profilelist) -> {			 
				activateJoiner.add("<li>"+externalizer.publishLink(resolver,profilelist));  
			});						
		}
							
		/*Create List of deactiavted profiles*/								
		StringJoiner inActivateJoiner = new StringJoiner("</li>","","</li>");	
		if(inActivateList.isEmpty()){
			inActivateJoiner.add("None");
		}else{
			inActivateList.stream().forEach((profilelist) -> {			 
				inActivateJoiner.add("<li>"+externalizer.publishLink(resolver,profilelist));   
			});		
		}
		
		LOGGER.info("Pages to be deleted: "+activateJoiner.toString());			
		Utils.sendEmail(emailService, emailRecipients,NOTIFICATION_TEMPLATEPATH,activateJoiner,inActivateJoiner);	
    }
    

	/**
     * Getting people data list
     * 
     * @return
     * @throws Exception
     */
    private List<AdvisorProfileBean> getPeopleData() {
        List<AdvisorProfileBean> peopleList = new ArrayList<AdvisorProfileBean>();
        LOGGER.debug("People list size >>"+peopleList.size());
       if(peopleList.size()!=0){
    	   peopleList.clear();    	   
       }
        AdvisorProfileBean peopleProfileBean = null;
        LocationBean locationBean = null;
        HashMap<String,ArrayList<LocationBean>> locationMap = new HashMap<String,ArrayList<LocationBean>>();
        try {
        	
            Node contentNode = session.getNode(pageRootPath);
            Page rootPage = pageManager.getPage(contentNode.getPath());

            if(rootPage != null)
            {
                //check if profile (root) page is present
            	locationRootPage = Utils.checkPageExist(rootPage.getPath(), "profile", pageManager); 
            	if(locationRootPage == null) {
            		LOGGER.debug("Release 2 issue locationRootPage is null >>");
	                locationRootPage = Utils.createCustomPage(rootPage.getPath(), "profile", liveCopyTemplateDefault, "Profile", pageManager, false);
	                Utils.setHideInNav(locationRootPage);
	                Utils.setHideFromExtSearch(locationRootPage);
	                Utils.setHideFromSearch(locationRootPage);
	                
	                // Profile (root) page is not present. So run full load 
	                advisorSqlQry = "SELECT * FROM tbl_final_ftp_advisors";
	                advisorAddressSql = "SELECT * FROM tbl_final_ftp_advisors_address order by EmailAddress";
            	}else{
            		 // Profile (root) page present, update only delta 
	                advisorSqlQry = "SELECT * FROM tbl_delta_ftp_advisors";
	                advisorAddressSql = "SELECT * FROM tbl_delta_ftp_advisors_address order by EmailAddress";
            	}
            }
            LOGGER.debug("Release 2 issue advisorSqlQry >>"+advisorSqlQry);
            LOGGER.debug("Release 2 issue advisorAddressSql >>"+advisorAddressSql);  
            
            if(connectionManager != null) {
                    connection = connectionManager.getConnection();
                    statement = connectionManager.getStatement();
                    resultSet = statement.executeQuery(advisorSqlQry);                  
                    // Iterate through the data in the result set and display it.
                    LOGGER.debug("Release 2 issue statement getPeopleData>>"+statement); 
                    LOGGER.debug("Release 2 issue resultSet getPeopleData>>"+resultSet); 
                    
                    LOGGER.debug("record count: "+resultSet.getFetchSize());
                    while (resultSet.next()) {
                        peopleProfileBean= new AdvisorProfileBean();
                        peopleProfileBean.setEmailAddress(resultSet.getString("EmailAddress"));
                        peopleProfileBean.setJobTitle(resultSet.getString("JobTitle"));
                        
                        if(StringUtils.isNotEmpty(peopleProfileBean.getJobTitle()))
                        {
                        	int poistion = peopleProfileBean.getJobTitle().indexOf(",");
                        	if(poistion > 0)
                        	{
                        		peopleProfileBean.setAdv_title1(peopleProfileBean.getJobTitle().substring(0,poistion));
                        		peopleProfileBean.setAdv_title2(peopleProfileBean.getJobTitle().substring(poistion+1));
                        	} else {
                        		peopleProfileBean.setAdv_title1(peopleProfileBean.getJobTitle());
                        	}
                        }
                        
                        peopleProfileBean.setDesignationCode(resultSet.getString("DesignationCode"));
                        peopleProfileBean.setLastName(resultSet.getString("LastName"));
                        peopleProfileBean.setFirstName(resultSet.getString("FirstName"));
                        peopleProfileBean.setPhone(resultSet.getString("Phone"));
                        peopleProfileBean.setStreet(resultSet.getString("Street"));
                        peopleProfileBean.setCity(resultSet.getString("City"));
                        peopleProfileBean.setState(resultSet.getString("State"));
                        peopleProfileBean.setZipCode(resultSet.getString("Zip"));
                        peopleProfileBean.setPrimaryAddress(resultSet.getString("PrimaryAddress"));
                        peopleProfileBean.setSpeciality(resultSet.getString("Specialty"));
                        peopleProfileBean.setProfilePhoto(resultSet.getString("ProfilePhoto"));
                        peopleProfileBean.setBio(resultSet.getString("Bio"));
                        peopleProfileBean.setFaxNumber(resultSet.getString("Fax"));
                        peopleProfileBean.setCellNumber(resultSet.getString("Cell"));
                        peopleProfileBean.setnMLSRId(resultSet.getString("NMLSR"));
                        peopleProfileBean.setSpanish(resultSet.getString("Spanish"));
                        peopleProfileBean.setCd(resultSet.getString("Cd"));
                        peopleProfileBean.setAppString(resultSet.getString("AppString"));
                        peopleProfileBean.setTestimonials(resultSet.getString("Testimonials"));
                        peopleProfileBean.setLink1text(resultSet.getString("Link1text"));
                        peopleProfileBean.setLink1url(resultSet.getString("Link1url"));
                        peopleProfileBean.setLink2text(resultSet.getString("Link2Text"));
                        peopleProfileBean.setLink2url(resultSet.getString("Link2Url"));
                        peopleProfileBean.setLink3text(resultSet.getString("Link3Text"));
                        peopleProfileBean.setLink3url(resultSet.getString("Link3Url"));
                        peopleProfileBean.setLink4text(resultSet.getString("Link4Text"));
                        peopleProfileBean.setLink4url(resultSet.getString("Link4Url"));
                        peopleProfileBean.setLink5text(resultSet.getString("Link5Text"));
                        peopleProfileBean.setLink5url(resultSet.getString("Link5Url"));
                        peopleProfileBean.setLink6text(resultSet.getString("Link6Text"));
                        peopleProfileBean.setLink6url(resultSet.getString("Link6Url"));
                        peopleProfileBean.setLink7text(resultSet.getString("Link7Text"));
                        peopleProfileBean.setLink7url(resultSet.getString("Link7Url"));
                        peopleProfileBean.setLink8text(resultSet.getString("Link8Text"));
                        peopleProfileBean.setLink8url(resultSet.getString("Link8Url"));
                        peopleProfileBean.setLink9text(resultSet.getString("Link9Text"));
                        peopleProfileBean.setLink9url(resultSet.getString("Link9Url"));
                        peopleProfileBean.setLink10text(resultSet.getString("Link10Text"));
                        peopleProfileBean.setLink10url(resultSet.getString("Link10Url"));
                        peopleProfileBean.setFacebook(resultSet.getString("Facebook"));
                        peopleProfileBean.setTwitter(resultSet.getString("Twitter"));
                        peopleProfileBean.setLinkedin(resultSet.getString("LinkedIn"));
                        peopleProfileBean.setYoutube(resultSet.getString("YouTube"));
                        peopleProfileBean.setGoogleplus(resultSet.getString("GooglePlus"));
                        peopleProfileBean.setNmlsCompliance(resultSet.getString("NmlsCompliance"));
                        peopleList.add(peopleProfileBean);
                    }
                    LOGGER.debug("people count"+peopleList.size());
                    //statement = null;
                    resultSet= null;
                    // Create and execute an SQL statement that returns advisors data.
                    resultSet = statement.executeQuery(advisorAddressSql);
                    // Iterate through the data in the result set and display it.
                    String currentEmailAddress="";
                    String prevEmailAddress="";
                    ArrayList<LocationBean> locationBeanArrayList = null;
                    int size = 0;
                    while (resultSet.next()) {
                        locationBean= new LocationBean();
                        if(size == 0) {
                            locationBeanArrayList = new ArrayList<>();
                        }
                        currentEmailAddress = resultSet.getString("EmailAddress");
                        locationBean.setEmailAddress(resultSet.getString("EmailAddress"));
                        locationBean.setAddress(resultSet.getString("Street"));
                        locationBean.setCity(resultSet.getString("City"));
                        locationBean.setPostal(resultSet.getString("Zip"));
                        locationBean.setState(resultSet.getString("State"));
                        if(currentEmailAddress.equalsIgnoreCase(prevEmailAddress)) {
                            //same location bean
                            locationBeanArrayList.add(locationBean);
                        }
                        else {
                            locationBeanArrayList = new ArrayList<>();
                            locationBeanArrayList.add(locationBean);
                        }
                        locationMap.put(currentEmailAddress,locationBeanArrayList);
                        prevEmailAddress= currentEmailAddress;
                        size++;
                    }
                    LOGGER.debug("sub table count"+locationMap.size());
                //}
                LOGGER.debug("PeopleLoad:End of table data load:"+new Date());
                Iterator<AdvisorProfileBean> itr = peopleList.iterator();
                while(itr.hasNext())
                {
                    AdvisorProfileBean bean =itr.next();
                    String emailAdd = bean.getEmailAddress();
                    List<String> addressList = new ArrayList<String>();
                    String primaryLocation = bean.getStreet()+","+bean.getCity()+","+bean.getState()+","+bean.getZipCode();
                    addressList.add(primaryLocation);
                    if(locationMap.containsKey(emailAdd))
                    {
                        ArrayList<LocationBean> locationBeanList = locationMap.get(emailAdd);
                        Iterator<LocationBean> locationItr = locationBeanList.iterator();
                        while(locationItr.hasNext())
                        {
                            LocationBean locbean = locationItr.next();
                            addressList.add(locbean.getAddress()+","+locbean.getCity()+","+locbean.getState()+","+locbean.getPostal());
                        }
                        bean.setLocationBeanArrayList(locationMap.get(emailAdd));
                    }
                    //get mapquest data and set it back
                    HashMap<String,String> stringHashMap = getMapQuestData(addressList);
                    //set the primary lat/long
                    if(stringHashMap.containsKey(primaryLocation))
                    {
                        String latlong = (String)stringHashMap.get(primaryLocation);
                        String[] latlongstr = latlong.split(",");
                        bean.setLatitude(latlongstr[0]);
                        bean.setLongitude(latlongstr[1]);
                    }
                    //set the data for secondary locations
                    if(locationMap.containsKey(emailAdd))
                    {
                        ArrayList<LocationBean> locationBeanList = locationMap.get(emailAdd);
                        Iterator<LocationBean> locationItr = locationBeanList.iterator();
                        while(locationItr.hasNext())
                        {
                            LocationBean locbean = locationItr.next();
                            String loc = locbean.getAddress()+","+locbean.getCity()+","+locbean.getState()+","+locbean.getPostal();
                            if(stringHashMap.containsKey(loc))
                            {
                                String latlong = (String)stringHashMap.get(loc);
                                String[] latlongstr = latlong.split(",");
                                locbean.setLatitude(latlongstr[0]);
                                locbean.setLongitude(latlongstr[1]);
                            }
                        }
                        bean.setLocationBeanArrayList(locationBeanList);
                    }
                }
                LOGGER.debug("PeopleLoad:End of mapquest data load:"+new Date());

            } else {
            	LOGGER.error("Connection manager is null");
            }
        } catch(Exception e)         {
            LOGGER.error("Exception getting people data. Message {}, Trace {}: ",e.getMessage(),e);
        }        
        return peopleList;
    }

    /**
     * Get address coordinates from mapquest
     * 
     * @param locationList
     * @return
     * @throws Exception
     */
    private HashMap<String,String> getMapQuestData(List<String> locationList) throws IOException {

        HttpURLConnection con = null;
        BufferedReader bufferedReader = null;
        HashMap<String,String> mapQuestData = new HashMap<String,String>();
        try {
			// http://www.mapquestapi.com/geocoding/v1/address?key=Gmjtd%7Clu6zn1ua2d%2C70%3Do5-l0850&outFormat=json&location=121
			// PERIMETER CENTER, Atlanta, GA, 30346
            String url = configService.getPropertyValue("mapquest.url")+"?key="+configService.getPropertyValue("mapquest.key")+"&inFormat=kvp&outFormat=json&key=KEY";
            Iterator<String> itr = locationList.iterator();
            String locationKey ="&location=";
            while(itr.hasNext())
            {
                String location = itr.next();
                //location= location.replaceAll(" ","%20");
                url = url+locationKey+encode(location);
            }
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            // Send post request
            con.setDoOutput(true);
            //int responseCode = con.getResponseCode();
            //LOGGER.debug("nSending 'POST' request to URL :"+ url+"Response Code : " + responseCode);
            bufferedReader = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String output;
            StringBuffer response = new StringBuffer();
            //int i = 0;
            while ((output = bufferedReader.readLine()) != null) {
                response.append(output);
               // i++;
            }
            JSONObject jsonObject = new JSONObject(response.toString());
            if(jsonObject.has("results"))
            {
                JSONArray jsonArray = (JSONArray)jsonObject.get("results");
                if(jsonArray.length() > 0)
                {
                    for (int k=0;k<jsonArray.length();k++)
                    {
                        JSONObject jsonObject1 = (JSONObject)jsonArray.get(k);
                        JSONObject providedLocation = (JSONObject)jsonObject1.get("providedLocation");
                        JSONArray locationObj = (JSONArray)jsonObject1.get("locations");
                        String location = "";
                        String latitude= "";
                        String longitude="";
                        if(providedLocation != null && providedLocation.length() > 0 && providedLocation.has("location")) {
                        	location = (String)providedLocation.get("location");
                        }
                        if(locationObj != null && locationObj.length() > 0) {
                            JSONObject dataObj = (JSONObject)locationObj.get(0);
                            if(dataObj.has("latLng")) {
                                JSONObject latLongObj = (JSONObject)dataObj.get("latLng");
                                latitude= String.valueOf((Double)latLongObj.get("lat"));
                                longitude =String.valueOf((Double)latLongObj.get("lng"));
                            }
                        }

                        if(StringUtils.isNotBlank(location) && StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude)) {
                            mapQuestData.put(location,latitude+","+longitude);
                        }

                    }
                }

            }
        }
        catch(Exception e)
        {
            LOGGER.error("Exception getting mapquest data. Message {}, Trace {}: ",e.getMessage(),e);
        }
        finally {
            if(con != null)
            {
                con.disconnect();
            }
            if(bufferedReader != null)
            {
                bufferedReader.close();
            }
        }
        return mapQuestData;
    }

    /**
     * Creates people pages
     * @param peopleList
     * @param slingHttpServletRequest
     * @return
     * @throws Exception
     */
    public int createPeoplePages(List<AdvisorProfileBean> peopleList,SlingHttpServletRequest slingHttpServletRequest) {
        int pageCount = 0;
        try {
            LOGGER.debug("inside createPeoplePages method ");
            ListIterator<AdvisorProfileBean> locItr = peopleList.listIterator();
            /*PageManager pageManager = resolver.adaptTo(PageManager.class);
            Node contentNode = session.getNode(pageRootPath);
            Page rootPage = pageManager.getPage(contentNode.getPath());*/
            //Page locationRootPage = null;
            Page blueprintPage = null;

           /* if(rootPage !=null)
            {
                //check if locations root page is present
                locationRootPage = Utils.createCustomPage(rootPage.getPath(), "profile", liveCopyTemplateDefault, "Profile", pageManager, false);
                Utils.setHideInNav(locationRootPage);
                Utils.setHideFromExtSearch(locationRootPage);
                Utils.setHideFromSearch(locationRootPage);
            }*/
            blueprintPage=  pageManager.getPage(bluePrintPagePath);
            while(locItr.hasNext())
            {
                AdvisorProfileBean peopleProfileBean = locItr.next();
                if(locationRootPage != null) {
                    Utils.setHideInNav(locationRootPage);
                    Page peoplePage = Utils.createCustomPage(locationRootPage.getPath(),getPeoplePageUrl(peopleProfileBean) , liveCopyTemplateDefault, peopleProfileBean.getFirstName() + " " + peopleProfileBean.getLastName(), pageManager, false);
                    
                    //live relation ship
                    if(peoplePage != null) {
                        if (blueprintPage != null) {
                            Utils.createLiveRelationShip(peoplePage, blueprintPage, resolver, rolloutManager);
                        }
                        setPageMetadata(peopleProfileBean, peoplePage);
                        pageCount++;
                        activateUrl.add(peoplePage.getPath());
                    }
                }
            }
            if(activateUrl.isEmpty() == false){
	            String[] listString = activateUrl.toArray(new String[activateUrl.size()]);
	            workflowPackageManager.create(resolver, "advisor", packageName, listString);
            }
            // session.save();
        } catch(Exception e) {
            LOGGER.error("Exception in createPeoplePages()",e);
            return pageCount;
        }
        return pageCount;
    }


    /**
     * Sets the page metadata
     * @param peopleProfileBean
     * @param peoplePage
     * @param slingHttpServletRequest
     * @throws Exception
     */
    private void setPageMetadata(AdvisorProfileBean peopleProfileBean,Page peoplePage) throws Exception
    {
        String speciality= Utils.removeSpecialChar(peopleProfileBean.getSpeciality().toLowerCase());
        Node jcrNode = peoplePage.getContentResource().adaptTo(Node.class);
        jcrNode.setProperty("adv_firstname",peopleProfileBean.getFirstName());
        jcrNode.setProperty("adv_lastname",peopleProfileBean.getLastName());
        jcrNode.setProperty("adv_emailaddress",peopleProfileBean.getEmailAddress());
        jcrNode.setProperty("adv_title1",peopleProfileBean.getAdv_title1());
        jcrNode.setProperty("adv_title2",peopleProfileBean.getAdv_title2());
        jcrNode.setProperty("adv_nmls",peopleProfileBean.getnMLSRId());
        jcrNode.setProperty("adv_phone",peopleProfileBean.getPhone());
        jcrNode.setProperty("adv_cell",peopleProfileBean.getCellNumber());
        jcrNode.setProperty("adv_fax",peopleProfileBean.getFaxNumber());
        jcrNode.setProperty("adv_specialty","advisor-specialty:"+speciality);
        jcrNode.setProperty("adv_facebook",peopleProfileBean.getFacebook());
        jcrNode.setProperty("adv_twitter",peopleProfileBean.getTwitter());
        jcrNode.setProperty("adv_linkedin",peopleProfileBean.getLinkedin());
        jcrNode.setProperty("adv_youtube",peopleProfileBean.getYoutube());
        jcrNode.setProperty("adv_googleplus",peopleProfileBean.getGoogleplus());
        jcrNode.setProperty("adv_appstring",peopleProfileBean.getAppString());
        jcrNode.setProperty("pageTitle",peopleProfileBean.getAdv_title1()+" "+peopleProfileBean.getFirstName()+" "+peopleProfileBean.getLastName()+" in "+peopleProfileBean.getCity()+", "+peopleProfileBean.getState()+" "+peopleProfileBean.getZipCode()+"|SunTrust Bank");
        String vanityEmailUrl=getAdvisorVanityURL(peopleProfileBean.getEmailAddress().toLowerCase());
        jcrNode.setProperty("sling:vanityPath","/"+vanityEmailUrl); 
        StringBuilder keys = new StringBuilder(peopleProfileBean.getFirstName()+" "+peopleProfileBean.getLastName());
        keys.append(",");
        keys.append(peopleProfileBean.getCity()+" "+peopleProfileBean.getState()+" "+peopleProfileBean.getZipCode());
        keys.append(",");
        keys.append(peopleProfileBean.getSpeciality());
        jcrNode.setProperty("keywords", keys.toString());

        // save to repository
        ArrayList<String> designationList=null;
        if(StringUtils.isNotEmpty(peopleProfileBean.getDesignationCode()))
        {
            designationList = new ArrayList<String>();
            String[] desList= peopleProfileBean.getDesignationCode().split(",");
            for(int i=0;i<desList.length;i++)
            {
                String designationcode = desList[i].trim();
                designationcode = Utils.removeSpecialChar(designationcode.toLowerCase());
                designationList.add("designationcodes:"+designationcode);
            }
        }
        if(designationList != null){
            jcrNode.setProperty("adv_designationcodes",designationList.toArray(new String[designationList.size()]));
        }

        //set other component nodes
        if(jcrNode.hasNode("adv_details_right"))
        {
            NodeIterator ndItr= jcrNode.getNode("adv_details_right").getNodes();
            while(ndItr.hasNext())
            {
                Node subNode = ndItr.nextNode();
                if(subNode.hasProperty("sling:resourceType"))
                {
                    String resourceType = subNode.getProperty("sling:resourceType").getString();
                    //finracheck
                    if(resourceType.contains("htmlcontainer") && checkFinraLink(speciality))
                    {
                        
                    	StringWriter finraText = new StringWriter();
                    	if (suntrustDotcomService != null) {
                			String filePath = suntrustDotcomService
                					.getPropertyValue("finra-text");
                			filePath = filePath
                					+ "/jcr:content/renditions/original/jcr:content";
                			Resource res = resolver.getResource(filePath);
                			if (res != null) {
                				Node docNode = res.adaptTo(Node.class);
                				InputStream inputStream = docNode.getProperty("jcr:data").getBinary()
                						.getStream();
                				IOUtils.copy(inputStream, finraText, "UTF-8");
                			}
                		}
                    	
                    	//populate the data
                        /*subNode.setProperty("htmlcontainer",slingHttpServletRequest
                                .getResourceBundle(slingHttpServletRequest.getLocale())
                                .getString("finra.text") );*/
                        subNode.setProperty("htmlcontainer",finraText.toString());
                        Utils.cancelPropertyRelationship(subNode.getPath(),resolver,new String[]{"htmlcontainer"});
                    }
                    else if(resourceType.contains("bio"))
                    {
                        subNode.setProperty("adv_bio",peopleProfileBean.getBio());
                        Utils.cancelPropertyRelationship(subNode.getPath(),resolver,new String[]{"adv_bio"});
                    }
                    else if(resourceType.contains("relatedlinks") && !checkFinraLink(speciality))
                    {
                        //set the related links section
                        List<String> jsonArray = getRelatedLinks(peopleProfileBean);
                        if(jsonArray.isEmpty() == false) {
                            subNode.setProperty("adv_iItems", jsonArray.toArray(new String[jsonArray.size()]));
                            Utils.cancelPropertyRelationship(subNode.getPath(),resolver,new String[]{"adv_iItems"});
                        }
                    }
                }
            }
        }
        //get advisor address and mapquest details
        Map<String, Object> details = getAdvisorLocationDetails(peopleProfileBean);
        jcrNode.setProperty("adv_addressItems", (String[])details.get("addressItems"));
        jcrNode.setProperty("adv_address", (String[])details.get("adv_address"));
        jcrNode.setProperty("adv_hiddenData", (String)details.get("hiddenData"));
        @SuppressWarnings("unchecked")
		HashSet<String> set = (HashSet<String>)details.get("cityList");
        HashSet<String> zipCodeList = (HashSet<String>)details.get("zipCodeList");
        jcrNode.setProperty("adv_cities",(Value)null);
        jcrNode.setProperty("adv_state",(Value)null);
        jcrNode.setProperty("adv_zipcodes",(Value)null);
        resolver.commit();
        if(zipCodeList.size()<=1){
        	jcrNode.setProperty("adv_zipcodes",zipCodeList.iterator().next().toString());
        }else{
        	jcrNode.setProperty("adv_zipcodes",StringUtils.join(zipCodeList.iterator(),","));
        }
        if(set.size()<=1){
        	jcrNode.setProperty("adv_cities",set.iterator().next().toString());
        }else{
        	jcrNode.setProperty("adv_cities",StringUtils.join(set.iterator(),","));
        }
        if(details.size()<=1){
        	jcrNode.setProperty("adv_state",(String)details.get("stateList"));
        }else{        	
        	jcrNode.setProperty("adv_state",(String[])details.get("stateList"));
        }
        if(jcrNode.hasNode("image") && StringUtils.isNotBlank(peopleProfileBean.getProfilePhoto()))
        {
            //set the thumbnail image
            Node imageNode = jcrNode.getNode("image");
            imageNode.setProperty("fileReference",peopleProfileBean.getProfilePhoto());
            Utils.cancelPropertyRelationship(imageNode.getPath(),resolver,new String[]{"fileReference"});
        }

        //slingHttpServletRequest.getResourceResolver().commit();
        resolver.commit();
    }

    /**
     * Get the location details for advisors
     * @param peopleProfileBean
     * @return
     * @throws Exception
     */
    private Map<String, Object> getAdvisorLocationDetails(AdvisorProfileBean peopleProfileBean) {
        //populate the primary address
    	Map<String, Object> hmap = new HashMap<String, Object>();
    	try{	
	        String mapHiddenValue=";";
	        HashSet<String> cityList = new HashSet<String>();
	        ArrayList<String> dataList = new ArrayList<String>();
	        ArrayList<String> addressList = new ArrayList<String>();
	        HashSet<String> stateList = new HashSet<String>();
	        HashSet<String> zipCodeList = new HashSet<String>();
	        JSONObject primaryObj = new JSONObject();
			primaryObj.put("adv_location_name", "");
	        primaryObj.put("adv_address", peopleProfileBean.getStreet());
	        primaryObj.put("adv_city", peopleProfileBean.getCity());
	        primaryObj.put("adv_state", peopleProfileBean.getState());
	        primaryObj.put("adv_zipcode", peopleProfileBean.getZipCode());
	        primaryObj.put("adv_latitude",peopleProfileBean.getLatitude());
	        primaryObj.put("adv_longitude",peopleProfileBean.getLongitude());
	        mapHiddenValue = mapHiddenValue+peopleProfileBean.getLatitude()+"$"+peopleProfileBean.getLongitude()+"$"+peopleProfileBean.getStreet()+","+peopleProfileBean.getCity()+","+peopleProfileBean.getState()+","+peopleProfileBean.getZipCode()+";";
	        dataList.add(primaryObj.toString());
	        addressList.add(peopleProfileBean.getStreet());
	        cityList.add(peopleProfileBean.getCity());
	        stateList.add("states:"+peopleProfileBean.getState().toLowerCase());
	        zipCodeList.add(peopleProfileBean.getZipCode());
	        //populate the data for secondary address
	        List<LocationBean> locationBeanArrayList = peopleProfileBean.getLocationBeanArrayList();
	        if(locationBeanArrayList != null && locationBeanArrayList.isEmpty() == false) {
	            Iterator<LocationBean> itr = locationBeanArrayList.iterator();
	            JSONObject jsonObject = null;
	            while (itr.hasNext()) {
	                LocationBean locationBean = itr.next();
	                jsonObject = new JSONObject();
	                jsonObject.put("adv_location_name", "");
	                jsonObject.put("adv_address", locationBean.getAddress());
	                jsonObject.put("adv_city", locationBean.getCity());
	                jsonObject.put("adv_state", locationBean.getState());
	                jsonObject.put("adv_zipcode", locationBean.getPostal());
	                jsonObject.put("adv_latitude",locationBean.getLatitude());
	                jsonObject.put("adv_longitude",locationBean.getLongitude());
	                mapHiddenValue = mapHiddenValue+locationBean.getLatitude()+"$"+locationBean.getLongitude()+"$"+locationBean.getAddress()+","+locationBean.getCity()+","+locationBean.getState()+","+locationBean.getPostal()+";";
	                //set the lat /long
	                zipCodeList.add(locationBean.getPostal());
	                cityList.add(locationBean.getCity());
	                addressList.add(locationBean.getAddress());
	                stateList.add("states:"+locationBean.getState().toLowerCase());
	                dataList.add(jsonObject.toString());
	            }
	        }
	
	        hmap.put("addressItems",dataList.toArray(new String[dataList.size()]));
	        hmap.put("adv_address",addressList.toArray(new String[addressList.size()]));
	        hmap.put("hiddenData",mapHiddenValue);
	        hmap.put("zipCodeList",zipCodeList);
	        hmap.put("cityList",cityList);
	        hmap.put("stateList",stateList.toArray(new String[stateList.size()]));
	    } catch (JSONException e) {
			LOGGER.error("JSONException captured. Message: {} Trace: {}", e.getMessage(), e);
			return hmap;
		}
        return hmap;
    }

    /**
     * Get the advisor related links
     * @param peopleProfileBean
     * @param speciality
     * @return
     * @throws JSONException
     */
    private List<String> getRelatedLinks(AdvisorProfileBean peopleProfileBean) throws JSONException {
        List<String> jsonArray = new ArrayList<String>();
        //check if the user has his own links
        if(StringUtils.isNotBlank(peopleProfileBean.getLink1url()) && StringUtils.isNotBlank(peopleProfileBean.getLink1text()))
        {
            jsonArray.add(populateLinks(peopleProfileBean.getLink1url(),peopleProfileBean.getLink1text()));
        }
        if(StringUtils.isNotBlank(peopleProfileBean.getLink2url()) && StringUtils.isNotBlank(peopleProfileBean.getLink2text()))
        {
            jsonArray.add(populateLinks(peopleProfileBean.getLink2url(),peopleProfileBean.getLink2text()));
        }
        if(StringUtils.isNotBlank(peopleProfileBean.getLink3url()) && StringUtils.isNotBlank(peopleProfileBean.getLink3text()))
        {
            jsonArray.add(populateLinks(peopleProfileBean.getLink3url(),peopleProfileBean.getLink3text()));
        }
        if(StringUtils.isNotBlank(peopleProfileBean.getLink4url()) && StringUtils.isNotBlank(peopleProfileBean.getLink4text()))
        {
            jsonArray.add(populateLinks(peopleProfileBean.getLink4url(),peopleProfileBean.getLink4text()));
        }
        if(StringUtils.isNotBlank(peopleProfileBean.getLink5url()) && StringUtils.isNotBlank(peopleProfileBean.getLink5text()))
        {
            jsonArray.add(populateLinks(peopleProfileBean.getLink5url(),peopleProfileBean.getLink5text()));
        }
        if(StringUtils.isNotBlank(peopleProfileBean.getLink6url()) && StringUtils.isNotBlank(peopleProfileBean.getLink6text()))
        {
            jsonArray.add(populateLinks(peopleProfileBean.getLink6url(),peopleProfileBean.getLink6text()));
        }
        if(StringUtils.isNotBlank(peopleProfileBean.getLink7url()) && StringUtils.isNotBlank(peopleProfileBean.getLink7text()))
        {
            jsonArray.add(populateLinks(peopleProfileBean.getLink7url(),peopleProfileBean.getLink7text()));
        }
        if(StringUtils.isNotBlank(peopleProfileBean.getLink8url()) && StringUtils.isNotBlank(peopleProfileBean.getLink8text()))
        {
            jsonArray.add(populateLinks(peopleProfileBean.getLink8url(),peopleProfileBean.getLink8text()));
        }
        if(StringUtils.isNotBlank(peopleProfileBean.getLink9url()) && StringUtils.isNotBlank(peopleProfileBean.getLink9text()))
        {
            jsonArray.add(populateLinks(peopleProfileBean.getLink9url(),peopleProfileBean.getLink9text()));
        }
        if(StringUtils.isNotBlank(peopleProfileBean.getLink10url()) && StringUtils.isNotBlank(peopleProfileBean.getLink10text()))
        {
            jsonArray.add(populateLinks(peopleProfileBean.getLink10url(),peopleProfileBean.getLink10text()));
        }
        return jsonArray;
    }

    /**
     * Populate the related links
     * @param linkurl
     * @param linktext
     * @return
     * @throws JSONException
     */
    private String populateLinks(String linkurl,String linktext) throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("page", linktext);
        jsonObject.put("linkURL", linkurl);
        jsonObject.put("urlParams", "");
        jsonObject.put("target", "_blank");
        jsonObject.put("anchorTag", "");
        jsonObject.put("titletag", "");
        return jsonObject.toString();
    }

    /**
     * Checks if the speciality has FINRA link enabled
     * @param speciality
     * @return
     */
    private boolean checkFinraLink(String speciality)
    {
        String finracheck= configService.getPropertyValue(speciality+".finra");
        if(finracheck.equalsIgnoreCase("yes"))
        {
            return true;
        }
        return false;
    }
    
    /**
     * Encode url to UTF-8 fromat
     * @param url
     * @return
     */
    public static String encode(String url)
    {
        try {
            String encodeURL=URLEncoder.encode( url, "UTF-8" );
            return encodeURL;
        } catch (UnsupportedEncodingException e) {
            LOGGER.debug("Issue while encoding: ",e);
        }
        return null;
    }
    
    /**
     * To get new page format name
     * 
     * @param peopleProfileBean
     * @return
     */
    private String getPeoplePageUrl(AdvisorProfileBean peopleProfileBean)
    {
    	if(peopleProfileBean.getAdv_title1() != null){
    		return Utils.removeSpecialChar(getAdvisorVanityURL(peopleProfileBean.getEmailAddress().toLowerCase()))
					+ "-"
					+ Utils.removeSpecialChar(peopleProfileBean.getAdv_title1()
							.toLowerCase())
					+ "-"
					+ Utils.removeSpecialChar(peopleProfileBean.getCity()
							.toLowerCase())
					+ "-"
					+ Utils.removeSpecialChar(peopleProfileBean.getState()
							.toLowerCase())
					+ "-"
					+ Utils.removeSpecialChar(peopleProfileBean.getZipCode()
							.toLowerCase());
    	} else {
			return Utils.removeSpecialChar(getAdvisorVanityURL(peopleProfileBean.getEmailAddress().toLowerCase()))
					+ "-"
					+ Utils.removeSpecialChar(peopleProfileBean.getCity()
							.toLowerCase())
					+ "-"
					+ Utils.removeSpecialChar(peopleProfileBean.getState()
							.toLowerCase())
					+ "-"
					+ Utils.removeSpecialChar(peopleProfileBean.getZipCode()
							.toLowerCase());
    	}
    	
    }  
    
    /**
     * Returns advisor vanity url
     * 
     * @param emailAddress
     * @return
     */
    public static String getAdvisorVanityURL(String emailAddress){	
    	if(emailAddress.contains("@suntrust.com")){
    		emailAddress=emailAddress.split("@")[0];		
    	}
    	return emailAddress;
    }
    
    /**
     * Method to close connection
     * @param connection
     */
 	public void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				LOGGER.error("Exception in closing connection: ", e);
			}
		}
	}
 	
 	/**
 	 * Method to close statement 
 	 * @param statement
 	 */
	public void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				LOGGER.error("Exception in closing statement: ", e);
			}
		}
	}	
	
	/**
	 * Method to close result set
	 * @param resultSet
	 */
	public void closeResultSet(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				LOGGER.error("Exception in closing result set: ", e);
			}
		}
	}

}
