package com.suntrust.dotcom.utils;

import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.config.SuntrustDotcomService;

/**
 * This UTIL is used to fetch config values on sightly component
 *
* @author  Radha
* @version 1.0
* @since   2017-10-05 
*
*/
public class ConfigServiceUse extends WCMUsePojo {
	
    /** Property */
    private String property;
    
    /** propertyArray */
    private List<String> propertyArray;
    
    /** propertyArrayAsString */
    private String propertyArrayAsString = "";

    /**
     *  activate method gets initiated when the component is called
     *  @return Nothing. 
     */
    @Override
    public void activate() throws Exception { 
        String key = get("property", String.class);
        if (StringUtils.isNotEmpty(key)) {
			SuntrustDotcomService dotcomServiceconfig = getSlingScriptHelper()
					.getService(SuntrustDotcomService.class);
            if (dotcomServiceconfig != null) {
                property = dotcomServiceconfig.getPropertyValue(key);
                propertyArray = dotcomServiceconfig.getPropertyArray(key);
            }
        }
    }

    /**
     * @return the property
     */
    public String getProperty() {
        return property;
    }
    
    /**
     * @return the property array
     */
    public String getPropertyArray() { 
    	ListIterator<String> propIterator = propertyArray.listIterator();    	
        while(propIterator.hasNext()) {
        	String prop = propIterator.next();
        	propertyArrayAsString = propertyArrayAsString.concat(prop).concat(" ");
        }
		return propertyArrayAsString;
    }

}


