package com.suntrust.dotcom.servlets;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.AWSUtils;


/**
 * Class used by the authors if they want to clear the aws cache with the form submission in the author mode.
 */

@SuppressWarnings("serial")
@SlingServlet(
        metatype = true,
        label = "SunTrust - AWS Cache Clear - Sling Safe Methods Servlet",
        description = "Implementation of AWS Cache Clear Servlet.",
        paths = { "/dotcom/awscacheclear" },
        methods = { "POST" }
)
public final class AWSCacheClearServlet extends SlingAllMethodsServlet  {
	
	 @Reference
	 SuntrustDotcomService dotcomService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSCacheClearServlet.class);
    
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {          
        	        	
        	LOGGER.debug("req param::"+request.getParameter("data")); 
        	Set<String> awsUrlSet = new HashSet<String>();
            JSONObject json = (JSONObject) new JSONParser().parse(request.getParameter("data"));                                                                            	           
        	         	
            String pagePath1 =  (String) json.get("input1"); 
            String pagePath2 =  (String) json.get("input2");
            String pagePath3 =  (String) json.get("input3");
            String pagePath4 =  (String) json.get("input4");
            String pagePath5 =  (String) json.get("input5");
            String pagePath6 =  (String) json.get("input6");
            String pagePath7 =  (String) json.get("input7");
            String pagePath8 =  (String) json.get("input8");
            String pagePath9 =  (String) json.get("input9");
            String pagePath10 = (String) json.get("input10");                        
            
            if(StringUtils.isNotBlank(pagePath1)) {
            	awsUrlSet.add(pagePath1);
            }
            if(StringUtils.isNotBlank(pagePath2)) {
            	awsUrlSet.add(pagePath2);
            }
            if(StringUtils.isNotBlank(pagePath3)) {
            	awsUrlSet.add(pagePath3);
            }
            if(StringUtils.isNotBlank(pagePath4)) {
            	awsUrlSet.add(pagePath4);
            }
            if(StringUtils.isNotBlank(pagePath5)) {
            	awsUrlSet.add(pagePath5);
            }
            if(StringUtils.isNotBlank(pagePath6)) {
            	awsUrlSet.add(pagePath6);
            }
            if(StringUtils.isNotBlank(pagePath7)) {
            	awsUrlSet.add(pagePath7);
            }
            if(StringUtils.isNotBlank(pagePath8)) {
            	awsUrlSet.add(pagePath8);
            }
            if(StringUtils.isNotBlank(pagePath9)) {
            	awsUrlSet.add(pagePath9);
            }
            if(StringUtils.isNotBlank(pagePath10)) {
            	awsUrlSet.add(pagePath10);
            }             
	       	if(!awsUrlSet.isEmpty()){                   	 
            	 try{
            		 AWSUtils.flushAWSCache(awsUrlSet, dotcomService);
            		 response.getWriter().write("Success");
            	 }catch(Exception e){
            		 response.getWriter().write("Failure"); 
            	 }            	 
            }else{
            	LOGGER.debug("The AWSUrlSet is Empty");            	
            }  	       		       	
                      
        } catch (Exception e) {
        	LOGGER.error("Replication Exception 4: Message: {}, Trace: {}",e.getMessage(), e);
        }
    }

    @Activate
    protected void activate(final Map<String, Object> config) {
    	LOGGER.debug("The activate method is invoked");
    }
}
