package com.suntrust.dotcom.components;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.config.AdvisorConfigService;
import com.suntrust.dotcom.services.PageThumbnailService;

public class AdvisorEmailForm extends WCMUsePojo {
	/** Logger variable*/
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorEmailForm.class);
    
    /** Variable to store speciality*/
    private String specialty = null;
    
    /** AdvisorConfigService class variable*/
	private AdvisorConfigService advisorConfigService = null;
	
	/** PageThumbnailService class variable*/
	private PageThumbnailService pageThumbnailService = null;
	
	/** Variable to store page path*/
	private String pageURL =null;
		
	/** Variable to store email Address*/
	private String emailAddress =null;
		
	
	/**
	 * Overrided method
	 */
    @Override
    public void activate() {  
    	try{
	    	pageThumbnailService = getSlingScriptHelper().getService(PageThumbnailService.class);
			advisorConfigService = getSlingScriptHelper().getService(AdvisorConfigService.class);
	    	pageURL = getResourcePage().getPath();	    	
	    	LOGGER.info("pageURL "+pageURL);
	        specialty = get("specialty", String.class);
			if(specialty == null || specialty.isEmpty()){
				LOGGER.info("Specialty property is null or empty");
			}			
			//US49726 - Send the email address in place of lo id & nmls id
			if(getPageProperties().containsKey("adv_emailaddress"))
			{
				 emailAddress= (String)getPageProperties().get("adv_emailaddress");
				 emailAddress= emailAddress.replace("@","%40"); 
			}else{
				LOGGER.info("adv_emailaddress property is null or empty");
			}
			
			
    	} catch (Exception e) {
    		LOGGER.error("Failed to activate Use class", e);
		}
    }

    /**
     * @return the property
     */
    public String getEmailFormName() {
    	return getConfigNode(specialty,".emailformname");
    }
    
    /**
     * Return App Name
     * @return
     */
    public String getAppName() {
    	return getConfigNode(specialty,".appname");
    }
    
    /**
     * Returns subject
     * @return
     */
    public String getSubject() {
    	return getConfigNode(specialty,".subject");
    }
    
    /**
     * Returns Finra Link
     * @return
     */
    public String isFinraLink() {		
    	return getConfigNode(specialty,".finralink");
	}
    
    /**
     * Returns Apply now button link
     * @return
     */
    public String getApplyNowURL() {		
    	return getConfigNode(specialty,".applynow.url");
	}
    
    /**
     * Returns Form URL
     * @return
     */
    public String getFormURL() {		
    	return getConfigNode("advisor",".form.url");
	}
   
    /**
     * Returns value of given key from 
     * 
     * @param prefix
     * @param fieldName
     * @return
     */
    public String getConfigNode(String prefix, String fieldName) {
    	String propertyName=prefix+fieldName;    	
    	if (StringUtils.isNotEmpty(propertyName)) {            
            if (advisorConfigService != null) {
            	return advisorConfigService.getPropertyValue(propertyName);
            }
        }
        return "";
    }
    
    /**
     * Returns profile image
     * 
     * @return
     */
    public String getProfileImage(){
    	try {
			return pageThumbnailService.getPageThumbnail(pageURL);
		} catch (RepositoryException e) {
			LOGGER.error("Exception captured: ", e);
		}
		return "";
    }
    
    /**
     * Returns Email Address
     * @return
     */
    public String getEmailAddress() {		
    	return emailAddress;
	}
}