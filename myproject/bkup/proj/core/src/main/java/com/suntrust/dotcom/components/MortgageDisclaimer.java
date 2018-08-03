package com.suntrust.dotcom.components;

import java.io.InputStream;
import java.io.StringWriter;

import javax.jcr.Node;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.config.AdvisorConfigService;

/**
 *This program is used to display the mortgage disclaimer text for the advisor pages.
 */
public class MortgageDisclaimer extends WCMUsePojo{ 
	/** Logger variable*/
	private static final Logger LOGGER = LoggerFactory.getLogger(MortgageDisclaimer.class);
	/** variable to store the disclaimer text*/
	private String disclaimerTextContent=null;
	/** variable to initialize the dotcom service config*/
	private  AdvisorConfigService advisorConfig = null;
	
	/**
	 * The activate method is used to precompute and store (in member variables) the values needed in your HTL code, 
	 * based on the current context 
	 * @throws Exception
	 */
	@Override
	public void activate() throws Exception { 
		LOGGER.info("##### My INVOKED ACTIVATE");	
		LOGGER.debug("##### My INVOKED ACTIVATE");
        StringWriter disclaimerText = new StringWriter(); 
        advisorConfig = getSlingScriptHelper().getService(AdvisorConfigService.class);
		String specialty= "";
        if(getPageProperties().containsKey("adv_specialty"))
		{
			specialty= (String)getPageProperties().get("adv_specialty");
			specialty= specialty.replace("advisor-specialty:","");
		}
        if (advisorConfig != null && checkMortgageDisclaimer(specialty)) {
            String filePath = advisorConfig.getPropertyValue("disclaimer-text"); 
            filePath = filePath+ "/jcr:content/renditions/original/jcr:content";
            LOGGER.debug("file path"+filePath);
            Resource res = getResourceResolver().getResource(filePath); 
            LOGGER.debug("res path"+res);
            if (res != null) {
               Node docNode = res.adaptTo(Node.class);
               InputStream inputStream = docNode.getProperty("jcr:data").getBinary().getStream();
               IOUtils.copy(inputStream, disclaimerText, "UTF-8");
               disclaimerTextContent = disclaimerText.toString();	
               LOGGER.info(" My disclaimerTextContent is::"+disclaimerTextContent);
               LOGGER.debug(" My disclaimerTextContent is::"+disclaimerTextContent);
            }
          }               					
	}
	/**
	 * The getDisclaimerText method is used to get the disclaimer text content	 
	 */
	public String getDisclaimerText() {		
		return disclaimerTextContent;
	}

	/**
	 * Checks if the speciality has FINRA link enabled
	 * @param speciality
	 * @return
	 */
	private boolean checkMortgageDisclaimer(String speciality)
	{	
		String mortgagecheck= advisorConfig.getPropertyValue(speciality+".disclaimer");
		if("yes".equalsIgnoreCase(mortgagecheck))
		{
			return true;
		}
		return false;
	}


}
