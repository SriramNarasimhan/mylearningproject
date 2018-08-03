package com.suntrust.dotcom.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.MustacheItemsBean;
import com.suntrust.dotcom.beans.MustacheMultiBean;
import com.suntrust.dotcom.utils.GenericEnum;
import com.suntrust.dotcom.utils.Utils;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.sling.commons.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;

import javax.jcr.Value;
import javax.jcr.Property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* This MustacheComponent is used to store
* multifield values and return to component HTML.
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
 */
public class MustacheComponent extends WCMUsePojo {
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(MustacheComponent.class);
	/**	gets and sets the item Value pairs **/
	private MustacheMultiBean mBean = null;
	/**	returns the list of values in multifield **/ 
	private List<MustacheMultiBean> multiList = null;
	/**	counter for counting list index **/
	private int ctr;
	
	/**
	 * Method which initiates storing in JSON objects
	 */
	@Override
	public void activate() throws PathNotFoundException, ValueFormatException, RepositoryException, JSONException {
		LOGGER.info("##### INVOKED ACTIVATE");
		multiList = new ArrayList<MustacheMultiBean>();
		Node currentNode = getResource().adaptTo(Node.class);
		String[] tabs = { "i"};
		for (int i = 0; i < tabs.length; i++) {
			String currentItem = tabs[i] + "Items";
			if (currentNode.hasProperty(currentItem)) {
				LOGGER.info("##### ITEMS ARE BEING SET" + currentItem);
				LOGGER.info("##### mustache");
				setItems(currentNode, currentItem);

				multiList.add(mBean);
			}
		}
	}
	
	/**
	 * @param currentNode
	 * @param tab
	 * sets the multilist values in the arraylist
	 */
	private void setItems(Node currentNode, String tab) {
		/**	gets and sets value for each item **/ 
		MustacheItemsBean iBean = null;
		/**	list that stores iBeans **/
		List<MustacheItemsBean> lBean = null;
		
		try {
			Value[] value;
			JSONObject jObj;
			Property currentProperty;
			mBean = new MustacheMultiBean();
			lBean = new ArrayList<MustacheItemsBean>();
			currentProperty = currentNode.getProperty(tab);
			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();
			}
			else
			{
				value = new Value[1];
				value[0] = currentProperty.getValue();
			}
			for (int i = 0; i < value.length; i++) {
				jObj = new JSONObject(value[i].getString());
				iBean = new MustacheItemsBean();
				String stringURL;
				
					iBean.setCallouttext(jObj.getString("callouttext"));
					iBean.setCalloutlink(jObj.getString("calloutlink"));
					iBean.setTarget(jObj.getString("target"));
					iBean.setIcon(jObj.getString("icon"));
					//iBean.setLinkURL(urlCheck(jObj.getString("linkURL")));
					String url = jObj.getString("linkURL");
					url = (url == null)?GenericEnum.EMPTY_STRING.getValue():url.trim();
					String anchorTag  = jObj.getString("anchorTag");
					anchorTag = (anchorTag == null)?GenericEnum.EMPTY_STRING.getValue():anchorTag.trim();
					String urlParams  = jObj.getString("urlParams");
					urlParams = (urlParams == null)?GenericEnum.EMPTY_STRING.getValue():urlParams.trim();
					stringURL = Utils.getModifyURL(url,urlParams,anchorTag);

					LOGGER.debug("stringURL = " + stringURL);
					iBean.setLinkURL(stringURL.trim());
				
				lBean.add(iBean);
				ctr++;
			}
			mBean.setItems(lBean);
			LOGGER.info("#### MULTIFIELD COUNTER = "+ctr+" ####");

		}
		catch (RepositoryException e) {
			LOGGER.error("RepositoryException " + e.getMessage(), e);
		} catch (IllegalStateException e) {
			LOGGER.error("IllegalStateException " + e.getMessage(), e);
		} catch (org.apache.sling.commons.json.JSONException e) {
			LOGGER.error("JSONException " + e.getMessage(), e);
		}
	}

	
	/**
	 * @return the number of multifield values
	 */
	public int multifieldCounter(){
		LOGGER.info("#### INSIDE FUNCTION MULTIFIELD COUNTER = "+ctr+" ####");
		return ctr;
	}


	
	/**
	 * @return the multilist containing the multifield values
	 */
	public List<MustacheMultiBean> getMBean() {
		return this.multiList;
	}
}