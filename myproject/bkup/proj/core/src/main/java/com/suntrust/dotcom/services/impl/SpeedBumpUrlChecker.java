package com.suntrust.dotcom.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.ServiceAgentService;

/**
* SpeedBumpUrlChecker is used to verify whether the url clicked by end-user is whitelisted or not and redirect the user accordingly.
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
*/

@SuppressWarnings("serial")
@SlingServlet(paths = "/dotcom/external")
public class SpeedBumpUrlChecker extends SlingSafeMethodsServlet {
	/**	Service variable to read JCR repository * */ 
	@Reference
	private ServiceAgentService serviceAgent;
	/**	Service variable to read the run-mode configurations * */ 
	@Reference
	private SuntrustDotcomService suntrustDotcomService;	
	/**	Logger variable to log program state * */ 
	private static final Logger LOGGER = LoggerFactory.getLogger(SpeedBumpUrlChecker.class);
    
	
    /** 
     * To handle HTTP GET request
    */
    @Override   
    protected void doGet(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response){
    	String whiteListedUrls = null;
    	String clickedUrl = null;
    	String requestedUrl = request.getQueryString();
    	requestedUrl = requestedUrl.substring(11,requestedUrl.length());
    	try{
    		String whiteListedDocPath= null;
	    	String speedBumpPagePath= null;
    		response.setContentType("text/html");  	
	    	clickedUrl = request.getParameter("clickedUrl");
	    	
	        if (suntrustDotcomService != null) {
	        	whiteListedDocPath = suntrustDotcomService.getPropertyValue("white.listed.document.url");
	        	speedBumpPagePath = suntrustDotcomService.getPropertyValue("speed.bump.page.url");
	        }
			String whiteListedDoc = whiteListedDocPath+"/jcr:content/renditions/original/jcr:content";
	    	whiteListedUrls = getWhiteListedUrls(whiteListedDoc);
	    	if(StringUtils.isNotBlank(clickedUrl) && (clickedUrl.contains("https") || clickedUrl.contains("http"))){
	    		clickedUrl = clickedUrl.split("//")[1];
	    	}
	    	if(StringUtils.isNotBlank(clickedUrl) && clickedUrl.contains("?")){
	    		clickedUrl = clickedUrl.split("\\?")[0];
	    	}
	    	if(StringUtils.isNotBlank(clickedUrl) && clickedUrl.contains("#")){
	    		clickedUrl = clickedUrl.split("#")[0];
	    	}
	    	if(StringUtils.isNotBlank(clickedUrl) && clickedUrl.contains("/")){
	    		clickedUrl = clickedUrl.split("/")[0];
	    	}
	    	if(StringUtils.isNotBlank(whiteListedUrls)){
		    	if(whiteListedUrls.contains(clickedUrl)){ 
		    		response.sendRedirect(requestedUrl);
		    	}else{
		    		response.sendRedirect(speedBumpPagePath+"?url="+requestedUrl);
		    	}
	    	}
    	}catch(IOException iOException){
    		LOGGER.error(iOException.getMessage(), iOException);
    	}catch(RepositoryException repositoryException){
    		LOGGER.error(repositoryException.getMessage(), repositoryException);
    	} catch (LoginException loginException) {
			LOGGER.error(loginException.getMessage(), loginException);
		}
    }
    
    /**
     * @param whiteListedDoc
     * @return whiteListedUrls
     * @throws RepositoryException 
     * @throws LoginException 
     * @throws IOException 
    */
    public String getWhiteListedUrls(String whiteListedDoc) throws LoginException, RepositoryException, IOException {
		ResourceResolver resourceResolver = null;
		String whiteListedUrls = null;
		resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
		Resource res = resourceResolver.getResource(whiteListedDoc);
		if(res != null){
			Node whiteListedDocNode = res.adaptTo(Node.class);
			InputStream inputStream = whiteListedDocNode.getProperty("jcr:data").getBinary().getStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, "UTF-8");
			whiteListedUrls = writer.toString().toLowerCase(Locale.ENGLISH);		
		}
		resourceResolver.close();		
		return whiteListedUrls;
	}
}

