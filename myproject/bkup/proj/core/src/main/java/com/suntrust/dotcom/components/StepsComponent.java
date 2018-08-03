package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.sling.commons.json.JSONObject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.StepsItemsBean;
import com.suntrust.dotcom.beans.StepsMultiBean;

/**
* This Steps Component is used 
* to fetch the multifield values 
* from dialog to component HTML
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
 */

public class StepsComponent extends WCMUsePojo {
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(StepsComponent.class);
	/**	stores all iItems as string **/ 
	private static final String I_ITEMS = "iItems";
	/**	gets and sets the item Value pairs **/
	private StepsMultiBean mBean = null;
	/**	returns the list of values in multifield **/
	private List<StepsMultiBean> multiList = null;

	/**
	 * Method which initiates storing in JSON objects
	 */
	@Override
	public void activate() throws RepositoryException, JSONException {
		LOGGER.info("##### INVOKED ACTIVATE");
		multiList = new ArrayList<StepsMultiBean>();
		Node currentNode = getResource().adaptTo(Node.class);
		LOGGER.info("currentNode::" + currentNode.getPath());
		String[] tabs = { "i" };
		for (int i = 0; i < tabs.length; i++) {
			String currentItem = tabs[i] + "Items";
			if (currentNode.hasProperty(currentItem)) {
				LOGGER.info("##### ITEMS ARE BEING SET" + currentItem);
				LOGGER.info("##### Steps");
				setItems(currentNode, currentItem);
				multiList.add(mBean);
			}
		}
		for (StepsMultiBean stepsMbean : multiList) {
			for (StepsItemsBean stepsIbean : stepsMbean.getItems()) {
				LOGGER.info("##### stepsIbean " + stepsIbean.getStepsHeading());
			}
		}
	}

	/**
	 * sets the multilist values in the arraylist
	 */
	private void setItems(Node currentNode, String tab)
			throws PathNotFoundException, RepositoryException,
			ValueFormatException, org.json.JSONException {
		/**	gets and sets value for each item **/
		StepsItemsBean iBean = null;
		/**	list that stores iBeans **/
		List<StepsItemsBean> lBean = null;
		try {
			Value[] value;
			JSONObject jObj;
			Property currentProperty;
			mBean = new StepsMultiBean();
			lBean = new ArrayList<StepsItemsBean>();
			currentProperty = currentNode.getProperty(tab);
			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();
			} else {
				value = new Value[1];
				value[0] = currentProperty.getValue();
			}
			int foralttext = 1;
			for (int i = 0; i < value.length; i++) {
				jObj = new JSONObject(value[i].getString());
				iBean = new StepsItemsBean();
				// String stringURL;
				if (tab.equalsIgnoreCase(I_ITEMS)) {
					iBean.setStepsAlttext("Step "+ foralttext);
					iBean.setStepsHeading(jObj.getString("heading"));
					iBean.setStepsDescription(jObj.getString("description"));
					iBean.setStepsImage(jObj.getString("image"));
					iBean.setStepsImageAlttext(jObj.getString("alttext"));
					/*
					 * stringURL = updateURL(jObj); LOGGER.debug("stringURL = "
					 * + stringURL); iBean.setdescription(stringURL.trim());
					 */
					foralttext = foralttext + 1;
				}

				lBean.add(iBean);

			}
			mBean.setItems(lBean);
			} catch (RepositoryException e) {
				LOGGER.error("RepositoryException " + e.getMessage(), e);
			} catch (IllegalStateException e) {
				LOGGER.error("IllegalStateException " + e.getMessage(), e);
			} catch (org.apache.sling.commons.json.JSONException e) {
				LOGGER.error("JSONException " + e.getMessage(), e);
			}
	}

	/**
	 * @return the multilist containing the multifield values
	 */
	public List<StepsMultiBean> getMBean() {
		return this.multiList;
	}
}