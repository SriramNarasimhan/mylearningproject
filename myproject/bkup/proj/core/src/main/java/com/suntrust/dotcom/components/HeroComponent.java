package com.suntrust.dotcom.components;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * The HeroComponent class is used to return sign on 
 *  properties and script file contents.
 * @author Cognizant
 * @version 1.0
 * @since 05 September 2017
 * 
 */
public class HeroComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(HeroComponent.class);
	/**
	 * This is used to retrieve signOnPage values
	 */
	private String signOnPage = null;
	/**
	 * This is used to retrieve signOnComp values
	 */
	private String signOnComp = null;
	
	/**
	 * This is used to retrieve signOn properties
	 */
	private ValueMap signOnProperties = null;

	/**
	 * @return signOnProperties
	 */
	public final ValueMap getSignOnProperties() {
		return signOnProperties;
	}

	/**
	 * @param signOnProperties
	 */
	public final void setSignOnProperties(ValueMap signOnProperties) {
		this.signOnProperties = signOnProperties;
	}
	
	/**
	 * Gets called on bundle activate. 
	 */
	@Override
	public void activate(){
		signOnPage = get("signOnPath", String.class);
		signOnComp = get("signOnComp", String.class);	
		
		setSignOnProperties(getValueMap());	
		
	}	

	/**
	 * Returns the page properties
	 *
	 * @return ValueMap
	 */
	public ValueMap getValueMap() {
		Page page = null;
		ValueMap signOnProp = null;
		if(org.apache.commons.lang.StringUtils.isNotBlank(signOnPage)) {
			Resource resource = getResourceResolver().getResource(signOnPage);
			PageManager pageManager = getResourceResolver().adaptTo(PageManager.class);
			if(resource != null && pageManager.getPage(signOnPage) != null) {
				page = resource.adaptTo(Page.class);
				if(page.getContentResource(signOnComp) != null) {
					Resource signOnResource = page.getContentResource(signOnComp);
					signOnProp = signOnResource.adaptTo(ValueMap.class);
					//LOGGER.debug("getSignOnProperties: "+signOnProp);
				}
			}
		}
		return signOnProp;
	}


	/**
	 * Returns the script file content
	 *
	 * @return scriptValue
	 */
	public String getScriptContent() {
		String scriptFilePath = get("scriptFilePath", String.class);
		try {
			Resource res = getResourceResolver().getResource(scriptFilePath);
			if(res != null){
				Asset asset = res.adaptTo(Asset.class);
				Rendition rendition = asset.getOriginal();
				InputStream inputStream = rendition.getStream();
				StringWriter writer = new StringWriter();
				IOUtils.copy(inputStream, writer, "UTF-8");
				return writer.toString();
			}
		} catch (Exception e) {
			LOGGER.error("Error reading script value",e.getMessage(), e);
		}
		return null;
	}

}