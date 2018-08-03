package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.settings.SlingSettingsService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.ComboItemsBean;
import com.suntrust.dotcom.beans.ComboMultiBean;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.GenericEnum;
import com.suntrust.dotcom.utils.GenericUtil;
import com.suntrust.dotcom.utils.Utils;

/**
 * Handles combobox component dialog data
 * 
 * @author Nandakumaran Kasinathan (ugnk52)
 *
 */
public class ComboComponent extends WCMUsePojo {
	/** Log variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComboComponent.class);
	
	/** Dialog multifield item name */
	private static final String I_ITEMS = "iItems";
	
	/** ComboMultiBean class instance variable*/
	private ComboMultiBean mBean = null;
	
	/** ComboMultiBean list variable*/
	private List<ComboMultiBean> multiList = null;
	
	/** ResourceResolver instance variable*/
	private ResourceResolver resourceResolver = null;
	
	/** SlingSettingsService instance variable*/
	private SlingSettingsService slingService = null;

	@Override
	public void activate(){
		try {
			multiList = new ArrayList<ComboMultiBean>();
			resourceResolver = getResourceResolver();
			slingService = getSlingScriptHelper().getService(SlingSettingsService.class);
			Node currentNode = getResource().adaptTo(Node.class);
			String[] tabs = { "i"};
			for (int i = 0; i < tabs.length; i++) {
				String currentItem = tabs[i] + "Items";
					if (currentNode.hasProperty(currentItem)) {
						LOGGER.debug("##### ITEMS ARE BEING SET" + currentItem);
						setItems(currentNode, currentItem);
						multiList.add(mBean);
					}
			}
		} catch (Exception e) {
			LOGGER.error("JSONException captured: ",e);
		}
	}

	/**
	 * Creates bean object
	 * 
	 * @param currentNode
	 * @param tab
	 * @throws PathNotFoundException
	 * @throws RepositoryException
	 * @throws ValueFormatException
	 * @throws org.json.JSONException
	 */
	private void setItems(Node currentNode, String tab)
			throws PathNotFoundException, RepositoryException, ValueFormatException, JSONException {
		try {
			Value[] value;
			JSONObject jObj;
			Property currentProperty;
			ComboItemsBean iBean = null;
			mBean = new ComboMultiBean();
			List<ComboItemsBean> lBean = new ArrayList<ComboItemsBean>();
			currentProperty = currentNode.getProperty(tab);
			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();
			} else {
				value = new Value[1];
				value[0] = currentProperty.getValue();
			}
			GenericUtil genUtil = new GenericUtil();
			SuntrustDotcomService dotcomService = getSlingScriptHelper()
					.getService(SuntrustDotcomService.class);
			LOGGER.debug("=================> SuntrustDotcomService: "+dotcomService);
			for (int i = 0; i < value.length; i++) {
				jObj = new JSONObject(value[i].getString());
				iBean = new ComboItemsBean();
				String stringURL;
				if (tab.equalsIgnoreCase(I_ITEMS)) {
					iBean.setTitle(jObj.getString("jcr:title"));
					String url = jObj.getString("linkURL");
					url = (url == null)?GenericEnum.EMPTY_STRING.getValue():url.trim();
					String anchorTag  = jObj.getString("anchorTag");
					anchorTag = (anchorTag == null)?GenericEnum.EMPTY_STRING.getValue():anchorTag.trim();
					//stringURL = Utils.getModifyURL(url,"",anchorTag);
					//if(url.isEmpty() == false){
					stringURL = genUtil.getFullPageUrl(url, dotcomService, resourceResolver, slingService);
					if(anchorTag.startsWith(GenericEnum.HASH_SYMBOL.getValue())) {
						stringURL = stringURL + anchorTag;
                     } else {
                    	 stringURL = stringURL + GenericEnum.HASH_SYMBOL.getValue() + anchorTag;
                     }
					//}
					LOGGER.debug("stringURL = " + stringURL);
					iBean.setLinkURL(stringURL.trim());
				} else {
					iBean.setTitle(jObj.getString("jcr:title"));
					String url = jObj.getString("linkURL");
					url = (url == null)?GenericEnum.EMPTY_STRING.getValue():url.trim();
					String anchorTag  = jObj.getString("anchorTag");
					anchorTag = (anchorTag == null)?GenericEnum.EMPTY_STRING.getValue():anchorTag.trim();
					//stringURL = Utils.getModifyURL(url,"",anchorTag);
					//if(url.isEmpty() == false){
					stringURL = genUtil.getFullPageUrl(url, dotcomService, resourceResolver, slingService);
					if(anchorTag.startsWith(GenericEnum.HASH_SYMBOL.getValue())) {
						stringURL = stringURL + anchorTag;
                     } else {
                    	 stringURL = stringURL + GenericEnum.HASH_SYMBOL.getValue() + anchorTag;
                     }
					//}
					LOGGER.debug("stringURL = " + stringURL);
					iBean.setLinkURL(stringURL.trim());
				}
				lBean.add(iBean);
			}

			mBean.setItems(lBean);

		} catch (Exception e) {
			LOGGER.error("Exception captured: ",e);
		}

	}

	/**
	 * Returns bean list 
	 * @return
	 */
	public List<ComboMultiBean> getMBean() {
		return this.multiList;
	}

}