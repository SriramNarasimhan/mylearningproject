package com.suntrust.dotcom.components;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.SignOnItemsBean;
import com.suntrust.dotcom.beans.SignOnMultiBean;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.GenericEnum;
import com.suntrust.dotcom.utils.Utils;

public class SignOnComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SignOnComponent.class);

	private static final String A_ITEMS = "aItems";
	private static final String B_ITEMS = "bItems";
	private static final String C_ITEMS = "cItems";
	private static final String D_ITEMS = "dItems";
	private static final String I_ITEMS = "iItems";
	private static final String J_ITEMS = "jItems";
	private static final String OLB_HIDDEN_ITEMS = "olbHiddenItems";
	private static final String OCM_HIDDEN_ITEMS = "ocmHiddenItems";
	private static final String SIGNON_HERO_LINKS = "signonheroItems";

	private SignOnMultiBean mBean = null;

	private SignOnItemsBean iBean = null;

	private List<SignOnItemsBean> lBean = null;
	
	private List<SignOnMultiBean> multiList = null;
	private Map<String,String> maintenancemodeconfig;
	
	
	
	
	private SuntrustDotcomService suntrustDotcomService;
	@Override
	public void activate()  {

		multiList = new ArrayList<SignOnMultiBean>();

		Node currentNode = getResource().adaptTo(Node.class);

		String[] tabs = { "a", "b" ,"c", "d","i","j","olbHidden","ocmHidden","signonhero"};

		for (int i = 0; i < tabs.length; i++) {

			String currentItem = tabs[i] + "Items";

			try {
				if (currentNode.hasProperty(currentItem)) {

					//LOGGER.info("##### ITEMS ARE BEING SET" + currentItem);
					setItems(currentNode, currentItem);
					multiList.add(mBean);
				}
				else
				{

					multiList.add(new SignOnMultiBean());
				}
			} catch (RepositoryException e) {
				LOGGER.error("Exception e " + e.getMessage(), e);
			} catch (JSONException e) {
				LOGGER.error("Exception e " + e.getMessage(), e);
			}

		}
		setSignOnConfig();
		}

	private void setItems(Node currentNode, String tab) throws JSONException{


			Value[] value = null;

			JSONObject jObj = null;

			Property currentProperty = null;

			mBean = new SignOnMultiBean();

			lBean = new ArrayList<SignOnItemsBean>();

			try {
				currentProperty = currentNode.getProperty(tab);
			} catch (RepositoryException e) {
				LOGGER.error("Exception e " + e.getMessage(), e);
			}

			try {
				if (currentProperty.isMultiple()) {

					value = currentProperty.getValues();

				} else {

					value = new Value[1];

					value[0] = currentProperty.getValue();

				}
			} catch (RepositoryException e) {
				LOGGER.error("Exception e " + e.getMessage(), e);
			}

			for (int i = 0; i < value.length; i++) {

				try {
					jObj = new JSONObject(value[i].getString());
				} catch (IllegalStateException | JSONException
						| RepositoryException e) {
					LOGGER.error("Exception e " + e.getMessage(), e);
				}

				iBean = new SignOnItemsBean();

				String stringURL;

				if (tab.equalsIgnoreCase(A_ITEMS)||tab.equalsIgnoreCase(B_ITEMS) || tab.equalsIgnoreCase(C_ITEMS)||tab.equalsIgnoreCase(D_ITEMS) || tab.equalsIgnoreCase(SIGNON_HERO_LINKS)) {
										
						if (jObj.has("title"))
							try {
								iBean.setTitle(jObj.getString("title"));
							} catch (JSONException e) {
								LOGGER.error("Exception e " + e.getMessage(), e);
							}
						if (jObj.has("linkURL")) {
							String url = null;
							try {
								url = jObj.getString("linkURL");
							} catch (JSONException e) {
								LOGGER.error("Exception e " + e.getMessage(), e);
							}							
							url = (url == null) ? GenericEnum.EMPTY_STRING
									.getValue() : url.trim();
							//LOGGER.info("url value " + url);
							if (jObj.has("urlParams")) {
								String urlParams = null;
								try {
									urlParams = jObj.getString("urlParams");
								} catch (JSONException e) {
									LOGGER.error("Exception e " + e.getMessage(), e);
								}
								/*LOGGER.info("urlParams value "
										+ urlParams);*/
								urlParams = (urlParams == null) ? GenericEnum.EMPTY_STRING
										.getValue() : urlParams.trim();
								stringURL = Utils.getModifyURL(url, urlParams,
										"");								
								//LOGGER.debug("stringURL = " + stringURL);
								iBean.setLinkURL(stringURL.trim());
							}
						}
						if (jObj.has("target"))
							try {
								iBean.setTarget(jObj.getString("target"));
							} catch (JSONException e) {
								LOGGER.error("Exception e " + e.getMessage(), e);
							}
					
					if(tab.equalsIgnoreCase(A_ITEMS)||tab.equalsIgnoreCase(B_ITEMS)){						
						try {
							iBean.setEnableInHero(jObj.getString("enableLinkInHero"));
						} catch (JSONException e) {
							LOGGER.error("Exception e " + e.getMessage(), e);
						}
					}

					if(tab.equalsIgnoreCase(SIGNON_HERO_LINKS))	{
						try {
							iBean.setLinkText(jObj.getString("linktext"));
						
							iBean.setGlyphIcon(jObj.getString("heroMobileGlyphIconOcm"));
						
							iBean.setAltText(jObj.getString("alttext"));
						} catch (JSONException e) {
							LOGGER.error("Exception e " + e.getMessage(), e);
						}
					}
					
				} else if(tab.equalsIgnoreCase(I_ITEMS)){
					//iBean.setMessagetype(jObj.getString("messagetype"));
					try {
						if(jObj.getString("messagetype").equalsIgnoreCase("offline")) {
							iBean.setMessagetypeOlbOffline(jObj.getString("messagetype"));
							iBean.setMaintenancemessageOlbOffline(jObj.getString("maintenancemessage"));
						} else if(jObj.getString("messagetype").equalsIgnoreCase("offline-scheduled")) {
							iBean.setMessagetypeOlbScheduled(jObj.getString("messagetype"));
							iBean.setMaintenancemessageOlbScheduled(jObj.getString("maintenancemessage"));
						} else if(jObj.getString("messagetype").equalsIgnoreCase("offline-unscheduled")) {
							iBean.setMessagetypeOlbUnScheduled(jObj.getString("messagetype"));
							iBean.setMaintenancemessageOlbUnScheduled(jObj.getString("maintenancemessage"));
						}
					} catch (JSONException e) {
						LOGGER.error("Exception e " + e.getMessage(), e);
					}
				} else if(tab.equalsIgnoreCase(J_ITEMS)){
					//iBean.setMessagetype(jObj.getString("messagetype"));
					try {
						if(jObj.getString("messagetype").equalsIgnoreCase("offline")) {
							iBean.setMessagetypeOcmOffline(jObj.getString("messagetype"));
							iBean.setMaintenancemessageOcmOffline(jObj.getString("maintenancemessage"));
							
						}else if(jObj.getString("messagetype").equalsIgnoreCase("offline-scheduled")) {
							iBean.setMessagetypeOcmScheduled(jObj.getString("messagetype"));
							iBean.setMaintenancemessageOcmScheduled(jObj.getString("maintenancemessage"));
							
						} else if(jObj.getString("messagetype").equalsIgnoreCase("offline-unscheduled")) {
							iBean.setMessagetypeOcmUnScheduled(jObj.getString("messagetype"));
							iBean.setMaintenancemessageOcmUnScheduled(jObj.getString("maintenancemessage"));
							
						}
					} catch (JSONException e) {
						LOGGER.error("Exception e " + e.getMessage(), e);
					}
				} else if(tab.equalsIgnoreCase(OLB_HIDDEN_ITEMS)){
					
					//iBean.setMessagetype(jObj.getString("messagetype"));
					if(StringUtils.isNotBlank(jObj.getString("hiddenNameOlb"))) {
						iBean.setHiddenName(jObj.getString("hiddenNameOlb"));
					}
					if(StringUtils.isNotBlank(jObj.getString("hiddenValueOlb"))) {
						iBean.setHiddenValue(jObj.getString("hiddenValueOlb"));
					}

				} else if(tab.equalsIgnoreCase(OCM_HIDDEN_ITEMS)){
					
					//iBean.setMessagetype(jObj.getString("messagetype"));
					if(StringUtils.isNotBlank(jObj.getString("hiddenNameOcm"))) {
						iBean.setHiddenName(jObj.getString("hiddenNameOcm"));
					}
					if(StringUtils.isNotBlank(jObj.getString("hiddenValueOcm"))) {
						iBean.setHiddenValue(jObj.getString("hiddenValueOcm"));
					}
				}
				
				lBean.add(iBean);
				
			}

			mBean.setItems(lBean);
			

		
		
	
	}
	
	/*Sign On Config File read starts*/
	public void setSignOnConfig(){
	String signonDocPath1= null;
	String ocmconfig=null;
	String olbconfig=null;
	Properties prop = new Properties();
	InputStream configValues = null;
	String signOnConfig = null;
	maintenancemodeconfig = new HashMap<String, String>();
	

	
		suntrustDotcomService = getSlingScriptHelper().getService(SuntrustDotcomService.class);
		
    	
        if (suntrustDotcomService != null) {
        	
        	signonDocPath1 = suntrustDotcomService.getPropertyValue("signon.config.document.url");
        	signOnConfig= signonDocPath1+"/jcr:content/renditions/original/jcr:content";
           }
        Resource res = getResourceResolver().getResource(signOnConfig);
		if(res != null){

			try {
				configValues = res.adaptTo(Node.class).getProperty("jcr:data").getBinary().getStream();
			} catch (RepositoryException e) {
				LOGGER.error("Exception e " + e.getMessage(), e);
			}	
		}
       
        
        /*input = new FileInputStream(signOnConfig);
		
        LOGGER.info("File is...##############"+input);*/

		// load a properties file
		try {
			prop.load(configValues);
		} catch (IOException e) {
			LOGGER.error("Exception e " + e.getMessage(), e);
		}

		// get the property value and print it out
		ocmconfig=(prop.getProperty("maintenance.mode.ocm"));
		olbconfig=(prop.getProperty("maintenance.mode.olb"));
		
		
			
			maintenancemodeconfig = new HashMap<String, String>();
			maintenancemodeconfig.put("ocm",ocmconfig.toLowerCase());
			maintenancemodeconfig.put("olb",olbconfig.toLowerCase());
		   


	 
}
	
	public Map<String,String> getSignOnConfigMap() {
		return maintenancemodeconfig;
       }
	
	
	/*Sign On Config File read ends*/

	/*private String urlCheck(String url)
	{
		String stringURL = url.trim();
		if (stringURL.contains("http") || stringURL.contains("https")) {
			return stringURL;
		} else {
			return stringURL + ".html";
		}

	}


	String updateURL(JSONObject jObj) throws JSONException
	{

		StringBuffer updatedUrl = new StringBuffer();

		updatedUrl = updatedUrl.append(jObj.getString("linkURL").trim());
		String urlParams = jObj.getString("urlParams").trim();
		//String anchorTag = jObj.getString("anchorTag").trim();
		if (updatedUrl !=null && !updatedUrl.toString().isEmpty()) {

			updatedUrl.delete(0, updatedUrl.length());

			updatedUrl = updatedUrl.append(urlCheck(jObj.getString("linkURL")).trim());

			if (urlParams !=null && !urlParams.isEmpty()) {

				updatedUrl = updatedUrl.append("?").append(urlParams);
			}

			/*if (anchorTag !=null && !anchorTag.isEmpty()) {

				updatedUrl = updatedUrl.append("#").append(anchorTag);
			}

		}



		return updatedUrl.toString();

	}*/

	public List<SignOnMultiBean> getMBean() {

		return this.multiList;

	}

}