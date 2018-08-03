package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.Utils;

/**
 * GlobalAlertComponent class is to fetch the values for the Global Alert component
 *
 * @author Cognizant
 * @version 1.0
 * @since 26 OCT 2017
 *
 */
public class GlobalAlertComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalAlertComponent.class);
	/**
	 * This is used to retrieve suntrust DotcomService
	 */
	
	private SuntrustDotcomService suntrustDotcomService;	
	private ValueMap childProperties;		
	private String globalAlertPath= null;	
	private String finalPath="";
	private String finalAlertPath=null;
	/**	Service variable to get settings service * */
	private SlingSettingsService settingsService;
	
	public void activate() {			
		suntrustDotcomService = getSlingScriptHelper().getService(SuntrustDotcomService.class);		
		settingsService = getSlingScriptHelper().getService(SlingSettingsService.class);
		
        if (suntrustDotcomService != null) {        	
        	globalAlertPath = suntrustDotcomService.getPropertyValue("global.alert.url");
           }
	    
        try {
			getFinalAlertPath();
		} catch (JSONException e) {
			LOGGER.error("GlobalAlertComponent : activate() :Exception, {}",e.getMessage(), e);
		}       
	    

	}	
	
	public String getFinalAlertPath() throws JSONException{
		finalPath="";
		finalAlertPath="";		
		String requestUrl =getCurrentPage().getPath();		
		String requestPath=requestUrl;
		String fullUrl = null;
		JSONObject alertJson = null;
		String pageAlerts;				
		ResourceResolver resourceResolver = getResourceResolver();			
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        List<String> pageList = new ArrayList();
         Page rootPage = pageManager.getPage(globalAlertPath);
         if(rootPage!=null){
         Iterator<Page> rootPageIterator = rootPage.listChildren();         
         while(rootPageIterator.hasNext())
         {
         Page childPage = rootPageIterator.next();
         String childPath = childPage.getPath();
         //LOGGER.debug("childPath ="+childPath);
         fullUrl = childPath+"/jcr:content/globalalert";  
         //LOGGER.debug("fullUrl ="+fullUrl);
         Resource resource = resourceResolver.getResource(fullUrl);         
         ValueMap valuemap = resource.adaptTo(ValueMap.class);
         String[] pathIitems =valuemap.get("globalalert",String[].class); 
         if(pathIitems != null){
	         for(String alertPath : pathIitems) {
		         alertJson = new JSONObject(alertPath);
		         pageAlerts= alertJson.getString("alertpath");
		         if(pageAlerts.length()<=requestPath.length() && requestPath.indexOf(pageAlerts)>=0 && finalPath.length()<pageAlerts.length()){
		        	 finalPath= pageAlerts;
		        	 finalAlertPath= childPath;        	 
		        	 int alertLeg=pageAlerts.length();
		        	 if(pageAlerts.length() == finalPath.length()){
		             	break;
		             }
		         }         
	         } 
         }
         
         } 
         }
		
		boolean isPublishMode = Utils.isPublishRunMode(settingsService);
		if(isPublishMode){
		//LOGGER.debug("Canonical Url of finalAlertPath"+Utils.getCanonicalUrl(suntrustDotcomService.getPropertyArray("canonical.urls"),finalAlertPath,resourceResolver));		
		return Utils.getCanonicalUrl(suntrustDotcomService.getPropertyArray("canonical.urls"),finalAlertPath,resourceResolver);
		}else{
			return finalAlertPath+".html";
		}
	}		
	public String getFinalPath(){
		return finalPath;
	}

}
