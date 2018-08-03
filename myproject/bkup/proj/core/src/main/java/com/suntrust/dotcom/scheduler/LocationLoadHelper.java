package com.suntrust.dotcom.scheduler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.suntrust.dotcom.beans.LocationBean;
import com.suntrust.dotcom.config.LocationConfigService;
import com.suntrust.dotcom.services.WorkflowPackageManager;
import com.suntrust.dotcom.utils.Utils;


public class LocationLoadHelper {
	
    private RolloutManager rolloutManager = null;
    
    private LocationConfigService configService = null;
    
    private WorkflowPackageManager workflowPackageManager = null;
    
    private WorkflowService wfService = null;
    
	//private SuntrustDotcomService suntrustDotcomService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationLoadHelper.class);

    private final String USER_AGENT = "Mozilla/5.0";


    private Session session;
    private ResourceResolver resolver;
    private ArrayList<String> activateUrl;

    private static  String ROOT_PATH_DEFAULT = null;
    private static  String BLUEPRINT_PAGE_PATH_BRANCH = null;
    private static  String BLUEPRINT_PAGE_PATH_ATM  = null;
    private static  String LIVE_COPY_TEMPLATE_DEFAULT  = null;
    private String packageName ="";
    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    
    //protected void runLocationPageCreation(final SlingHttpServletRequest request,final SlingHttpServletResponse response) {
    public void runLocationPageCreation(final SlingHttpServletRequest request,final SlingHttpServletResponse response, ResourceResolver rResolver, LocationConfigService confService, RolloutManager rolloutMngr, WorkflowPackageManager workflowPkgMngr, WorkflowService wrfService) throws IOException{
        LOGGER.debug("IN runLocationPageCreation");

        try {
            int count = execute(rResolver, confService, rolloutMngr, workflowPkgMngr, wrfService);
            LOGGER.debug("Location load:after create location pages. Count: "+count);
            response.setContentType("text/html");
            if(count > 0) {
                Iterator<String> pageItr = activateUrl.iterator();
                StringBuffer buffer = new StringBuffer();
                buffer.append("Live copy created. Please verify the sync status");
                buffer.append("<br>");
                buffer.append("Total pages created:"+count);
                buffer.append("<br>");
                buffer.append("Page list");
                buffer.append("<br>");
                while(pageItr.hasNext())
                {
                    buffer.append(pageItr.next());
                    buffer.append("<br>");
                }
                response.getWriter().print(buffer.toString());
            } else {
            	 response.getWriter().print("Location page creation/update completed. No Page had been updated or created or page creation failed.");
            }
      } catch (Exception e) {
            LOGGER.error("Exception Message: {} , Trace: {}",e.getMessage(), e);
           response.getWriter().print("Live copy not created. Please check the logs");
        }
        finally
        {
            if(resolver != null)
            {
                resolver.close();
            }
        }
    }

    public void runLocationPageCreation(ResourceResolver rResolver, LocationConfigService confService, RolloutManager rolloutMngr, WorkflowPackageManager workflowPkgMngr, WorkflowService wrfService) {
        LOGGER.debug("IN runLocationPageCreation");

        try {

            int count = execute(rResolver, confService, rolloutMngr, workflowPkgMngr, wrfService);
            if(count > 0) {
                LOGGER.debug(count+" page's created/updated");
            } else {
            	LOGGER.debug("No Page was updated today");
            }

        } catch (Exception e) {
            LOGGER.error("Exception: ",e);
        }
        
    }

    private int execute(ResourceResolver rResolver, LocationConfigService confService, RolloutManager rolloutMngr, WorkflowPackageManager workflowPkgMngr, WorkflowService wrfService){
    	int count = 0;
        try {
        	
        	LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();
            int hour = now.getHour();
            int minute = now.getMinute();
            int second = now.getSecond();
            packageName = ""+month+"_"+day+"_"+year+"_"+hour+minute+second;
        	
            //this.resolver = request.getResourceResolver();
            //this.resolver = serviceAgentService.getServiceResourceResolver("dotcomreadservice");
            this.resolver = rResolver;
            this.session= resolver.adaptTo(Session.class);
            configService = confService;
            rolloutManager = rolloutMngr;
            workflowPackageManager = workflowPkgMngr;
            wfService = wrfService;
            
            activateUrl= new ArrayList<String>();
            LOGGER.debug("session::"+session.getUserID());
            ROOT_PATH_DEFAULT = configService.getPropertyValue("ROOT_PATH");
            BLUEPRINT_PAGE_PATH_BRANCH = configService.getPropertyValue("BLUEPRINT_PAGE_PATH_BRANCH");
            BLUEPRINT_PAGE_PATH_ATM = configService.getPropertyValue("BLUEPRINT_PAGE_PATH_ATM");
            LIVE_COPY_TEMPLATE_DEFAULT =configService.getPropertyValue("LIVE_COPY_TEMPLATE");
            LOGGER.debug("Location load start"+new Date());
            PageManager pageManager = resolver.adaptTo(PageManager.class);
            Node contentNode = session.getNode(ROOT_PATH_DEFAULT);
            Page rootPage = pageManager.getPage(contentNode.getPath());
            Page locationMainPage = Utils.checkPageExist(rootPage.getPath(), "locations", pageManager);
            resolver.commit();
            LOGGER.debug("========Location load:Start mapquest get data========"+new Date());
            ArrayList<LocationBean> locationBeanArrayList = getMapQuestDataCheck(locationMainPage);
            LOGGER.debug("========Location load:end mapquest get data and start create location page========"+new Date());
            count = createLocationPages(locationBeanArrayList);
            LOGGER.debug("========Location load:after create location pages. Count: "+count);

            locationMainPage = Utils.checkPageExist(rootPage.getPath(), "locations", pageManager);
            //if(locationMainPage != null){
            	session.getNode(locationMainPage.getPath()).getNode("jcr:content").setProperty("batchExecutionDate",dateFormat.format(yesterday()).toString());
            	resolver.commit();
            	LOGGER.debug("========Location page is available and execution date is set");
           // }
            
            
            if(count > 0){
	            WorkflowSession wfSession = wfService.getWorkflowSession(this.session); 
	            LOGGER.debug("========wfSession:========"+wfSession);
	            WorkflowModel model =  wfSession.getModel("/etc/workflow/models/dotcom/quick_deployment/jcr:content/model");
	            WorkflowData data =  wfSession.newWorkflowData("JCR_PATH", "/etc/workflow/packages/location/"+packageName); 
	            Map<String,Object> metaData = new HashMap<String, Object>();
	            metaData.put("workflowTitle", "Location page nightly job deployment");
	            LOGGER.debug("========Before workflow start ========");
	            LOGGER.debug("========printing wfSession:========"+wfSession);
	            wfSession.startWorkflow(model, data, metaData);
	            LOGGER.debug("========workflow end ========");
            } else {
            	LOGGER.debug("========workflow not initiated as there is no update ========");
            }

        } catch (Exception e) {
            LOGGER.error("Exception: ",e);
        }
        finally
        {
            if(resolver != null)
            {
                resolver.close();
            }
        }
    	return count;
    }
    
    private  ArrayList<LocationBean> getMapQuestDataCheck(Page page) throws Exception {
    	
    	ArrayList<LocationBean> branchList = new ArrayList<LocationBean>();
    	String postJsonData = "{'clientId': '"+configService.getPropertyValue("mapquest.clientid")+"','password': '"+configService.getPropertyValue("mapquest.password")+"','tableName': '"+configService.getPropertyValue("mapquest.tablename")+"','maxMatches':'-1'}";
        String url = configService.getPropertyValue("mapquest.url")+"&inFormat=json";
        URL obj = new URL(url);
        LOGGER.debug("POST URL: "+url);
    	if(null == page){
    		LOGGER.debug("Location main page does not exist. Full run");
    		branchList = getMapQuestData(branchList, obj, postJsonData); 
    	}else{
    		LOGGER.debug("Location main page exist. Page Update");
    		String lastExecutionDate = dateFormat.format(yesterday()).toString();
    		//lastExecutionDate="08/06/2017";
    		LOGGER.debug("Initialization date: "+lastExecutionDate);
    		if(session.getNode(page.getPath()).getNode("jcr:content").hasProperty("batchExecutionDate")){
    			lastExecutionDate = session.getNode(page.getPath()).getNode("jcr:content").getProperty("batchExecutionDate").getString();
    		}
    		LOGGER.debug("Last execution date from file path: "+lastExecutionDate);
    		Date lastExeDate = dateFormat.parse(lastExecutionDate);
			Date todayExcDate = yesterday();
			int differnece = (int)getTimeDiff(lastExeDate, todayExcDate);
			LOGGER.debug("Days to run: "+differnece);
			
        	for(int d= differnece; d > 0; d--){
	            String executionDate = dateFormat.format(getDate(d)).toString();
	            LOGGER.debug(executionDate);
	            String par = "[\""+executionDate+"\"]";
	            postJsonData = "{'clientId': '"+configService.getPropertyValue("mapquest.clientid")+"','password': '"+configService.getPropertyValue("mapquest.password")+"','tableName': '"+configService.getPropertyValue("mapquest.tablename")+"','extraCriteria':'last_modified_date=?','parameters':"+par+",'maxMatches':'-1'}";
	            //LOGGER.debug("JSON DATA------------------------------> "+postJsonData);
	            branchList = getMapQuestData(branchList, obj, postJsonData);
        	}
    	}
    	
    	return branchList;
    }
    
    /**
     * Gets map quest data from mapquest api
     * @return List of mapquest locations
     * @throws Exception
     */
    private ArrayList<LocationBean> getMapQuestData(ArrayList<LocationBean> branchList, URL object, String jsonQuery) throws Exception {
        HttpURLConnection con = null;
        BufferedReader in = null;
        //ArrayList<LocationBean> branchList = new ArrayList<LocationBean>();
        try {
            
        	String postJsonData = jsonQuery;
        	LOGGER.debug("JSON DATA------------------------------> "+postJsonData);
        	
            con = (HttpURLConnection) object.openConnection();
            // Setting basic post request
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json");
            // Send post request
            con.setDoOutput(true);
            
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());    
            wr.writeBytes(postJsonData);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            LOGGER.debug("'POST' Response Code : " + responseCode);
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String output;
            StringBuffer response = new StringBuffer();
            LOGGER.debug("Map Quest Response :"+ response.toString());
            int i = 0;
            while ((output = in.readLine()) != null) {
                response.append(output);
                i++;
            }
            JSONObject jsonObject = new JSONObject(response.toString());
            if(jsonObject.has("data")) {
            	int resultsCount= (int)jsonObject.get("resultsCount");
            	LOGGER.debug("resultsCount: "+resultsCount);
            	if(resultsCount > 0) {
	            	JSONObject value= (JSONObject)jsonObject.getJSONObject("data");
	                //JSONArray cols = value.getJSONArray("columns");
	                JSONArray rows = value.getJSONArray("rows");
	                for(int j=0;j<rows.length();j++)
	                {
	                    JSONArray childRow=rows.getJSONArray(j);
	                    if (childRow.isNull(86) || childRow.getString(86).equals("")) {
							continue;
						}
	                    LocationBean locationBean = new LocationBean();
	                    locationBean.setBranchId(childRow.isNull(3)? "":childRow.getString(3).trim());
	                    locationBean.setBranchName(childRow.isNull(4)?"":childRow.getString(4).trim());
	                    locationBean.setAddress(childRow.isNull(5)?"":childRow.getString(5).trim());
	                    locationBean.setCity(childRow.isNull(6)?"":childRow.getString(6).trim());
	                    locationBean.setCounty(childRow.isNull(7)?"":childRow.getString(7).trim());
	                    locationBean.setState(childRow.isNull(8)?"":childRow.getString(8).trim());
	                    locationBean.setPostal(childRow.isNull(9)?"":childRow.getString(9).trim());
	                    locationBean.setCountry(childRow.isNull(10)?"":childRow.getString(10).trim());
	                    locationBean.setSeoName(childRow.isNull(86)?"":childRow.getString(86).trim());
	                    locationBean.setPhone(childRow.isNull(23)?"":childRow.getString(23).trim());
	                    locationBean.setFax(childRow.isNull(24)?"":childRow.getString(24).trim());
	                    if( !childRow.isNull(29))
	                    {
	                        if(childRow.getInt(29) == 1)
	                        {
	                            locationBean.setBranch(true);
	                        }
	                    }
	                    if( !childRow.isNull(35)) {
	                        if (childRow.getInt(35) == 1) {
	                            locationBean.setAtm(true);
	                        }
	                    }
	                    LOGGER.debug("Branch ID: "+locationBean.getBranchId()+" Branch name: "+locationBean.getBranchName());
	                    locationBean.setMonBrHours(childRow.isNull(38)||(childRow.getString(38).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(38).trim()));
	                    locationBean.setTueBrHours(childRow.isNull(39)||(childRow.getString(39).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(39).trim()));
	                    locationBean.setWedBrHours(childRow.isNull(40)||(childRow.getString(40).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(40).trim()));
	                    locationBean.setThuBrHours(childRow.isNull(41)||(childRow.getString(41).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(41).trim()));
	                    locationBean.setFriBrHours(childRow.isNull(42)||(childRow.getString(42).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(42).trim()));
	                    locationBean.setSatBrHours(childRow.isNull(43)||(childRow.getString(43).trim()).isEmpty()?"Closed":getWeekendHrs(childRow.getString(43).trim(),"Sat"));
	                    locationBean.setSunBrHours(childRow.isNull(43)||(childRow.getString(43).trim()).isEmpty()?"Closed":getWeekendHrs(childRow.getString(43).trim(),"Sun"));
	
	                    locationBean.setMonDtHours(childRow.isNull(44)||(childRow.getString(44).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(44).trim()));
	                    locationBean.setTueDtHours(childRow.isNull(45)||(childRow.getString(45).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(45).trim()));
	                    locationBean.setWedDtHours(childRow.isNull(46)||(childRow.getString(46).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(46).trim()));
	                    locationBean.setThuDtHours(childRow.isNull(47)||(childRow.getString(47).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(47).trim()));
	                    locationBean.setFriDtHours(childRow.isNull(48)||(childRow.getString(48).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(48).trim()));
	                    locationBean.setSatDtHours(childRow.isNull(78)||(childRow.getString(78).trim()).isEmpty()?"Closed":getWeekendHrs(childRow.getString(78).trim(),"Sat"));
	                    locationBean.setSunDtHours(childRow.isNull(78)||(childRow.getString(78).trim()).isEmpty()?"Closed":getWeekendHrs(childRow.getString(78).trim(),"Sun"));
	
	                    locationBean.setMonTcHours(childRow.isNull(79)||(childRow.getString(79).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(79).trim()));
	                    locationBean.setTueTcHours(childRow.isNull(80)||(childRow.getString(80).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(80).trim()));
	                    locationBean.setWedTcHours(childRow.isNull(81)||(childRow.getString(81).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(81).trim()));
	                    locationBean.setThuTcHours(childRow.isNull(82)||(childRow.getString(82).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(82).trim()));
	                    locationBean.setFriTcHours(childRow.isNull(83)||(childRow.getString(83).trim()).isEmpty()?"Closed":formatTimings(childRow.getString(83).trim()));
	                    locationBean.setSatTcHours(childRow.isNull(84)||(childRow.getString(84).trim()).isEmpty()?"Closed":getWeekendHrs(childRow.getString(84).trim(),"Sat"));
	                    locationBean.setSunTcHours(childRow.isNull(84)||(childRow.getString(84).trim()).isEmpty()?"Closed":getWeekendHrs(childRow.getString(84).trim(),"Sun"));
	                    locationBean.setSpecialMessages(childRow.isNull(70)?"":childRow.getString(70).trim());
	                    locationBean.setPageTitle(getLocationType(locationBean.isBranch())+":"+locationBean.getCity()+", "+locationBean.getState().toUpperCase()+", "+locationBean.getPostal());
	                    locationBean.setLatitude(childRow.isNull(13)?"":childRow.getString(13));
	                    locationBean.setLongitude(childRow.isNull(14)?"":childRow.getString(14));
	                    locationBean.setServices(getServices(childRow.isNull(29)?"":childRow.getString(29).trim(),childRow.isNull(30)?"": childRow.getString(30).trim(),
	                            childRow.isNull(31)?"":childRow.getString(31).trim(), childRow.isNull(32)?"":childRow.getString(32).trim(),
	                            childRow.isNull(33)?"":childRow.getString(33).trim(), childRow.isNull(34)?"":childRow.getString(34).trim(),
	                            childRow.isNull(35)?"":childRow.getString(35).trim(), childRow.isNull(36)?"":childRow.getString(36).trim(),
	                            childRow.isNull(77)?"":childRow.getString(77).trim(), childRow.isNull(85)?"":childRow.getString(85).trim(),(!childRow.isNull(77) && childRow.getInt(77) == 1?true:false)));
	
	                    branchList.add(locationBean);
	                }
            	}
                LOGGER.debug("data length"+branchList.size());
            }
        }
        catch(Exception e)
        {
            LOGGER.error("branch size"+branchList.size());
            LOGGER.error("Exception getting mapquest data: Message {}, Trace {} ",e.getMessage(), e);
            throw e;
        }
        finally {
            if(con != null)
            {
                con.disconnect();

            }
            if(in != null)
            {
                in.close();
            }

        }
        return branchList;

    }

    /**
     * Creates location pages if exists or updates the data
     * @param locationList
     * @param slingHttpServletRequest
     * @return
     * @throws Exception
     */
    private int createLocationPages(ArrayList<LocationBean> locationList)
    {
        int i=0;
        try {
            LOGGER.debug("================inside createLocationPages method ===================");
            PageManager pageManager = resolver.adaptTo(PageManager.class);
            ListIterator<LocationBean> locItr= locationList.listIterator();
            Node contentNode = session.getNode(ROOT_PATH_DEFAULT);
            Page rootPage = pageManager.getPage(contentNode.getPath());
            Page locationRootPage = null;
            Page subRootPage =null;
            Page blueprintPage = null;
            Page stateblueprintPage = null;
            Page cityblueprintPage = null;
            Page zipblueprintPage=null;
            if(rootPage !=null)
            {
                //check if locations root page is present
                locationRootPage = Utils.createCustomPage(rootPage.getPath(), "locations", LIVE_COPY_TEMPLATE_DEFAULT, "Locations", pageManager, false);
                Utils.setHideInNav(locationRootPage);
                Utils.setHideFromExtSearch(locationRootPage);
                Utils.setHideFromSearch(locationRootPage);
                resolver.commit();
            }
            while(locItr.hasNext())
            {
            	LocationBean locationBean = locItr.next();
                if(locationBean.isBranch())
                {
                    //branch page
                    subRootPage = Utils.createCustomPage(locationRootPage.getPath(), "branch", LIVE_COPY_TEMPLATE_DEFAULT, "Branches", pageManager, false);
                    Page branchrootblueprintpage = pageManager.getPage("/content/suntrust/dotcom/language-masters/en/branchrootblueprint");
                    //live relation ship
                    if(branchrootblueprintpage != null) {
                        Utils.createLiveRelationShip(subRootPage, branchrootblueprintpage, resolver,rolloutManager);
                    }
                    blueprintPage=  pageManager.getPage(BLUEPRINT_PAGE_PATH_BRANCH);
                    stateblueprintPage = pageManager.getPage(configService.getPropertyValue("BLUEPRINT_PAGE_PATH_BRANCH_STATE"));
                    cityblueprintPage = pageManager.getPage(configService.getPropertyValue("BLUEPRINT_PAGE_PATH_BRANCH_CITY"));
                    zipblueprintPage = pageManager.getPage(configService.getPropertyValue("BLUEPRINT_PAGE_PATH_BRANCH_ZIP"));
                } else {
                    //atm page
                    subRootPage = Utils.createCustomPage(locationRootPage.getPath(), "atm", LIVE_COPY_TEMPLATE_DEFAULT, "ATMs", pageManager, false);
                    Page atmrootblueprintpage = pageManager.getPage("/content/suntrust/dotcom/language-masters/en/atmrootblueprint");
                    //live relation ship
                    if(atmrootblueprintpage != null) {
                        Utils.createLiveRelationShip(subRootPage, atmrootblueprintpage, resolver,rolloutManager);
                    }
                    blueprintPage=  pageManager.getPage(BLUEPRINT_PAGE_PATH_ATM);
                    stateblueprintPage = pageManager.getPage(configService.getPropertyValue("BLUEPRINT_PAGE_PATH_ATM_STATE"));
                    cityblueprintPage = pageManager.getPage(configService.getPropertyValue("BLUEPRINT_PAGE_PATH_ATM_CITY"));
                    zipblueprintPage = pageManager.getPage(configService.getPropertyValue("BLUEPRINT_PAGE_PATH_ATM_ZIP"));

                }
                Utils.setHideInNav(subRootPage);
                Utils.setHideFromSearch(subRootPage);

                TagManager tagManager = resolver.adaptTo(TagManager.class);
                Tag state = tagManager.resolve("states:"+locationBean.getState().toLowerCase());
                //state page
                //LOGGER.debug("Page names: ID: "+locationBean.getBranchId()+"state: "+locationBean.getState()+ " City: "+ locationBean.getCity()+" Postal: "+locationBean.getPostal()+ "Branch: "+ locationBean.getBranchName());
                Page statePage = Utils.createCustomPage(subRootPage.getPath(), locationBean.getState().toLowerCase(), LIVE_COPY_TEMPLATE_DEFAULT,(state != null) ? state.getTitle():locationBean.getState(), pageManager, false);
                Utils.setHideInNav(statePage);
                Utils.setHideFromSearch(statePage);
                if(stateblueprintPage != null) {
                    Utils.createLiveRelationShip(statePage, stateblueprintPage, resolver,rolloutManager);
                }

                String cityName= Utils.removeSpecialChar(locationBean.getCity().toLowerCase());
                Page cityPage = Utils.createCustomPage(statePage.getPath(),cityName, LIVE_COPY_TEMPLATE_DEFAULT, locationBean.getCity(), pageManager, false);
                Utils.setHideInNav(cityPage);
                Utils.setHideFromSearch(cityPage);
                if(cityblueprintPage != null) {
                    Utils.createLiveRelationShip(cityPage, cityblueprintPage, resolver,rolloutManager);
                }

                Page zipPage = Utils.createCustomPage(cityPage.getPath(),locationBean.getPostal(), LIVE_COPY_TEMPLATE_DEFAULT, locationBean.getPostal(), pageManager, false);
                Utils.setHideInNav(zipPage);
                Utils.setHideFromExtSearch(zipPage);
                Utils.setHideFromSearch(zipPage);
                if(zipblueprintPage != null) {
                    Utils.createLiveRelationShip(zipPage, zipblueprintPage, resolver,rolloutManager);
                }
                
                Page locationPage = Utils.createCustomPage(zipPage.getPath(),locationBean.getSeoName().toLowerCase(), LIVE_COPY_TEMPLATE_DEFAULT, locationBean.getBranchName(), pageManager, false);
                LOGGER.debug("Page created: "+i+ "branch ID: "+locationBean.getBranchId() + "name: "+locationBean.getBranchName() + "Location page obj: "+locationPage);
                //live relation ship
                if(blueprintPage != null) {
                    Utils.createLiveRelationShip(locationPage, blueprintPage, resolver,rolloutManager);
                }
                //set page metadata
                setPageMetadata(locationBean,locationPage);
                i++;
                activateUrl.add(locationPage.getPath());
            }
            String[] listString = activateUrl.toArray(new String[activateUrl.size()]);
            workflowPackageManager.create(resolver, "location", packageName, listString);
            // session.save();
        }
        catch(Exception e) {
            LOGGER.error("Exception in createLocationPages. Error message: {}. Trace: {}",e.getMessage(),e);
        }
        return i;
    }


    /**
     * Sets the page metadata from mapquest database
     * @param locationBean
     * @param locationPage
     * @param slingHttpServletRequest
     * @throws Exception
     */
    private void setPageMetadata(LocationBean locationBean,Page locationPage) throws Exception
    {
        Node jcrNode = locationPage.getContentResource().adaptTo(Node.class);
        jcrNode.setProperty("loc_locationname",locationBean.getBranchName());
        jcrNode.setProperty("loc_address",locationBean.getAddress());
        jcrNode.setProperty("loc_county",locationBean.getCounty());
        String statetag= "states:"+locationBean.getState().toLowerCase();
        jcrNode.setProperty("loc_state",statetag);
        TagManager tagManager = resolver.adaptTo(TagManager.class);
        Tag state = tagManager.resolve(statetag);
        String stateTitle = null;
        if(state != null){
	        stateTitle = state.getTitle();
	        jcrNode.setProperty("loc_state_title",stateTitle);
        }
        jcrNode.setProperty("loc_city",locationBean.getCity());
        jcrNode.setProperty("loc_zipcode",locationBean.getPostal());
        jcrNode.setProperty("loc_phone",locationBean.getPhone());
        jcrNode.setProperty("loc_fax",locationBean.getFax());
        jcrNode.setProperty("loc_messagedescription",locationBean.getSpecialMessages());
        //branch hours
        jcrNode.setProperty("loc_sunbranchhours",locationBean.getSunBrHours());
        jcrNode.setProperty("loc_monbranchhours",locationBean.getMonBrHours());
        jcrNode.setProperty("loc_tuebranchhours",locationBean.getTueBrHours());
        jcrNode.setProperty("loc_wedbranchhours",locationBean.getWedBrHours());
        jcrNode.setProperty("loc_thubranchhours",locationBean.getThuBrHours());
        jcrNode.setProperty("loc_fribranchhours",locationBean.getFriBrHours());
        jcrNode.setProperty("loc_satbranchhours",locationBean.getSatBrHours());
        //lobby hours
        jcrNode.setProperty("loc_sundriveinhours",locationBean.getSunDtHours());
        jcrNode.setProperty("loc_mondriveinhours",locationBean.getMonDtHours());
        jcrNode.setProperty("loc_tuedriveinhours",locationBean.getTueDtHours());
        jcrNode.setProperty("loc_weddriveinhours",locationBean.getWedDtHours());
        jcrNode.setProperty("loc_thudriveinhours",locationBean.getThuDtHours());
        jcrNode.setProperty("loc_fridriveinhours",locationBean.getFriDtHours());
        jcrNode.setProperty("loc_satdriveinhours",locationBean.getSatDtHours());
        //teller connect hours
        jcrNode.setProperty("loc_suntellerhours",locationBean.getSunTcHours());
        jcrNode.setProperty("loc_montellerhours",locationBean.getMonTcHours());
        jcrNode.setProperty("loc_tuetellerhours",locationBean.getTueTcHours());
        jcrNode.setProperty("loc_wedtellerhours",locationBean.getWedTcHours());
        jcrNode.setProperty("loc_thutellerhours",locationBean.getThuTcHours());
        jcrNode.setProperty("loc_fritellerhours",locationBean.getFriTcHours());
        jcrNode.setProperty("loc_sattellerhours",locationBean.getSatTcHours());
        jcrNode.setProperty("loc_services",locationBean.getServices().toArray(new String[locationBean.getServices().size()]));
        jcrNode.setProperty("jcr:title",locationBean.getBranchName()+" "+getLocationType(locationBean.isBranch()));
        jcrNode.setProperty("pageTitle",locationBean.getPageTitle());
        jcrNode.setProperty("loc_longitude",locationBean.getLongitude());
        jcrNode.setProperty("loc_latitude",locationBean.getLatitude());

        // save to repository
        String mapHiddenValue = ";"+locationBean.getLatitude()+"$"+locationBean.getLongitude()+"$"+locationBean.getAddress()+","+locationBean.getCity()+","+locationBean.getState()+","+locationBean.getPostal()+";";
        jcrNode.setProperty("adv_hiddenData", mapHiddenValue);
        
        List<String> locServices = locationBean.getServices();
        LinkedHashSet<String> services = new LinkedHashSet<String>();
        
        for(String locService : locServices) {
	        String servicestag= "services:"+locService;
	        Tag serviceTag = tagManager.resolve(servicestag);
	        if(serviceTag != null){
	        	services.add(serviceTag.getTitle());
	        }
        }
        
        StringBuilder keys = new StringBuilder(locationBean.getCity()+" "+stateTitle+" "+locationBean.getPostal());
        for(String service: services) {
        	keys.append(",");
        	keys.append(service);
        }
        jcrNode.setProperty("keywords", keys.toString());

        resolver.commit();
    }

    /**
     * Method to get services based on mapquest data.Returns the services as tag
     * @param Is_Branch
     * @param Is_Instore_Branch
     * @param Is_Commercial_Center
     * @param Is_Mortgage_Office
     * @param is_drive_in
     * @param Is_Investment_Center
     * @param Is_ATM
     * @param Is_Weekend_Hours
     * @param Is_Teller_Connect
     * @param Is_SB_Solutions
     * @return
     */
    private static ArrayList<String> getServices(String Is_Branch, String Is_Instore_Branch, String Is_Commercial_Center, String Is_Mortgage_Office, String is_drive_in, String Is_Investment_Center, String Is_ATM, String Is_Weekend_Hours, String Is_Teller_Connect, String Is_SB_Solutions, boolean tellerConnect){
        ArrayList<String> services = new ArrayList();
        if(Is_Branch.equalsIgnoreCase("1") || Is_Branch.equalsIgnoreCase("Y")){
            services.add("services:branch");
        }
        if(Is_ATM.equalsIgnoreCase("1")||Is_ATM.equalsIgnoreCase("Y")){
            services.add("services:atm");
        }
        if(Is_Instore_Branch.equalsIgnoreCase("1")||Is_Instore_Branch.equalsIgnoreCase("Y")){
            services.add("services:instore-branch");
        }
        if(is_drive_in.equalsIgnoreCase("1")||is_drive_in.equalsIgnoreCase("Y")){
            services.add("services:drive-thru-banking");
        }
        if(Is_Commercial_Center.equalsIgnoreCase("1")||Is_Commercial_Center.equalsIgnoreCase("Y")){
            services.add("services:commercial-center");
        }
        if(Is_Mortgage_Office.equalsIgnoreCase("1")||Is_Mortgage_Office.equalsIgnoreCase("Y")){
            services.add("services:mortgage-office");
        }
        if(Is_Investment_Center.equalsIgnoreCase("1")||Is_Investment_Center.equalsIgnoreCase("Y")){
            services.add("services:investment-center");
        }
        if(Is_Weekend_Hours.equalsIgnoreCase("1")||Is_Weekend_Hours.equalsIgnoreCase("Y")){
            services.add("services:weekend-hours");
        }
        if(Is_SB_Solutions.equalsIgnoreCase("1")||Is_SB_Solutions.equalsIgnoreCase("Y")){
            services.add("services:small-business-solutions");
        }
        if (tellerConnect) {
			services.add("services:teller-connect");			
	}

        return services;
    }

    /**
     * Get location type based on whether it is a branch or non branch
     * @param isBranch
     * @return
     */
    private static String getLocationType(boolean isBranch){
        if(!isBranch){
            return "SunTrust ATM";
        }
        return "SunTrust Branch";
    }

    /**
     * Method to format timings from map quest in display format
     * @param workingHrs
     * @return
     */
    private static String formatTimings(String workingHrs){
    	//LOGGER.error("timings:["+workingHrs+"]");
    	try {
	        if(workingHrs.isEmpty() == false){
	            workingHrs=workingHrs.replace(" ", "");
	            String startTime=workingHrs.split("-")[0];
	            if(!(startTime.contains(":"))){
	            	if(StringUtils.containsIgnoreCase(startTime, "Closed")) {
	            		return startTime;
	            	} else if(Integer.parseInt(startTime)<=6){
	                    return startTime+"PM to "+workingHrs.split("-")[1]+"PM";
	                }
	            }
	            return startTime+"AM to "+workingHrs.split("-")[1]+"PM";
	        } 
    	} catch(NumberFormatException ex) {
			LOGGER.error("NumberFormatException in formatTimings",ex);
			return "";
		}
        return "";
    }


    /**
     * Method to format the weekend hours for display
     * @param workingHrs
     * @param day
     * @return
     */
    private static String getWeekendHrs(String workingHrs, String day){
        workingHrs=workingHrs.trim();
        String weekEndDays[]=workingHrs.split(" ");
        String satHrs="";
        String sunHrs="";
        if(weekEndDays.length==8){
            String time = "";
            if(day.equalsIgnoreCase("Sat")){
                time = weekEndDays[1]+weekEndDays[2]+weekEndDays[3];
                satHrs=formatTimings(time);
            }else{
                time = weekEndDays[5]+weekEndDays[6]+weekEndDays[7];
                sunHrs=formatTimings(time);
            }
        } else if(weekEndDays.length==4 && (workingHrs.contains("Sat") || workingHrs.contains("Saturday"))&& (workingHrs.contains("Sun") || workingHrs.contains("Saturday"))){
            if(day.equalsIgnoreCase("Sat")){
                satHrs=formatTimings(weekEndDays[1]);
            }else{
                sunHrs=formatTimings(weekEndDays[3]);
            }
        } else if(weekEndDays.length==4 && (workingHrs.contains("Sat") || workingHrs.contains("Saturday"))){
            String time = weekEndDays[1]+weekEndDays[2]+weekEndDays[3];
            satHrs=formatTimings(time);
        } else if(weekEndDays.length==2){
            if(weekEndDays[0].equalsIgnoreCase("Sat")||weekEndDays[0].equalsIgnoreCase("Saturday")){
                satHrs=formatTimings(weekEndDays[1]);
            }
        }
        if(day.equalsIgnoreCase("Sat")){
            return satHrs;
        }else{
            return sunHrs;
        }
    }


    /**
     * Returns current day minus one day
     * 
     * @return Date
     */
    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    
    /**
     * Return todays date minus given days  
     * 
     * @param minusDay
     * @return date
     */
    private Date getDate(int minusDay) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -minusDay);
        return cal.getTime();
    }
    
    private String getLastExecutionDate(String filePath) {
		String lastExecutionDate = null;
		try {
			Resource res = resolver.getResource(filePath);
			if(res != null){
				Node docNode = res.adaptTo(Node.class);
				InputStream inputStream = docNode.getProperty("jcr:data").getBinary().getStream();
				StringWriter writer = new StringWriter();
				IOUtils.copy(inputStream, writer, "UTF-8");
				lastExecutionDate = writer.toString().toLowerCase();		
			}
		} catch (Exception e) {
			LOGGER.error("Exception captured when read date file: ",e);
		}
		return lastExecutionDate;
	}
    
    /**
     * Returns number of days present between the days passed
     * 
     * @param dateOne
     * @param dateTwo
     * @return long
     */
    private long getTimeDiff(Date dateOne, Date dateTwo) {
        long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
        long days = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
        LOGGER.debug("Days present in between given dates: "+days);
        return days;
    }

}

