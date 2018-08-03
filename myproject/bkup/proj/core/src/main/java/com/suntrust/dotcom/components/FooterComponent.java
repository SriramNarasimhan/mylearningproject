package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.FooterItemsBean;
import com.suntrust.dotcom.beans.FooterMultiBean;
import com.suntrust.dotcom.utils.GenericEnum;
import com.suntrust.dotcom.utils.Utils;


/**
 * This FooterComponent is used to manage
 * the links provided in the footer
 * @author Cognizant
 * @version 1.0
 * @since 10 July 2017
 *
 */
public class FooterComponent extends WCMUsePojo {
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FooterComponent.class);
	/** Multifield constant item. */
	private static final String I_ITEMS = "iItems";
	/** Multifield bean. */
	private FooterMultiBean mBean = null;
	/** FooterMultiBean list object. */
	private List<FooterMultiBean> multiList = null;
	
	/**
	 * Method which initiates storing in JSON objects
	 * 
	 */
	@Override
	public void activate() {
		multiList = new ArrayList<FooterMultiBean>();
		Node currentNode = getResource().adaptTo(Node.class);
		String[] tabs = { "i", "u" };
		for (int i = 0; i < tabs.length; i++) {
			String currentItem = tabs[i] + "Items";
			try {
				if (currentNode.hasProperty(currentItem)) {
					setItems(currentNode, currentItem);
					multiList.add(mBean);
				}
			} catch (RepositoryException e) {
				LOGGER.error("RepositoryException. Message: {}, Trage: {}", e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Method to set one list of items from multifield
	 * 
	 * @param currentNode
	 * @param tab
	 */
	private void setItems(Node currentNode, String tab) {
		/** FooterItemsBean class object. */
		FooterItemsBean iBean = null;
		/** FooterItemsBean list object. */
		List<FooterItemsBean> lBean = null;
		
		try {
			Value[] value;
			JSONObject jObj;
			Property currentProperty;
			mBean = new FooterMultiBean();
			lBean = new ArrayList<FooterItemsBean>();
			currentProperty = currentNode.getProperty(tab);
			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();
			}
			else {
				value = new Value[1];
				value[0] = currentProperty.getValue();
			}
			for (int i = 0; i < value.length; i++) {
				jObj = new JSONObject(value[i].getString());
				iBean = new FooterItemsBean();
				String stringURL;
				
				iBean.setTitle(jObj.getString("jcr:title"));
				//iBean.setLinkURL(urlCheck(jObj.getString("linkURL")));
				String url = jObj.getString("linkURL");
				url = (url == null)?GenericEnum.EMPTY_STRING.getValue():url.trim();
				String anchorTag  = jObj.getString("anchorTag");
				anchorTag = (anchorTag == null)?GenericEnum.EMPTY_STRING.getValue():anchorTag.trim();
				String urlParams  = jObj.getString("urlParams");
				urlParams = (urlParams == null)?GenericEnum.EMPTY_STRING.getValue():urlParams.trim();
				stringURL = Utils.getModifyURL(url,urlParams,anchorTag);
				iBean.setLinkURL(stringURL.trim());
				iBean.setTarget(jObj.getString("target"));
				
				if (!tab.equalsIgnoreCase(I_ITEMS)) {
					iBean.setIcon(jObj.getString("icon"));
				}
				lBean.add(iBean);
			}
			mBean.setItems(lBean);

		}
		catch (RepositoryException | IllegalStateException | JSONException e) {
			LOGGER.error("PathNotFoundException. Messgae: {}, Trace: {} ", e.getMessage(),e);
		}
	}

	/**
	 * Method to return the set list of items
	 * @return
	 */
	public List<FooterMultiBean> getMBean() {
		return this.multiList;
	}
}