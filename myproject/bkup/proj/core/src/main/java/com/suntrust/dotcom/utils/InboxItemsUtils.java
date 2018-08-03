package com.suntrust.dotcom.utils;



import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboxItemsUtils {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(InboxItemsUtils.class);
	  
	  public static Integer getSelectorAsInteger(SlingHttpServletRequest request, int selectorIndex)
	  {
	    String[] selectors = request.getRequestPathInfo().getSelectors();
	    if ((selectors != null) && (selectors.length > selectorIndex))
	    {
	      String selectorValue = selectors[selectorIndex];
	      if (StringUtils.isNotBlank(selectorValue)) {
	        try
	        {
	          return Integer.valueOf(Integer.parseInt(selectorValue));
	        }
	        catch (NumberFormatException nfe)
	        {
	          LOGGER.debug("Failed to parse URL selector as integer, selector value is '" + selectorValue + "'", nfe);
	        }
	      }
	    }
	    return null;
	  }
	  
	  public static String constructInboxTypeId(String itemTypeName, String subTypeName)
	  {
	    String result = itemTypeName.toLowerCase();
	    if (StringUtils.isBlank(subTypeName)) {
	      subTypeName = "default";
	    }
	    result = result + ":" + subTypeName.toLowerCase();
	    return result;
	  }
 
}
