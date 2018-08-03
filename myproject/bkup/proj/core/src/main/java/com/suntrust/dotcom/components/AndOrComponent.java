package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.AndOrItemsBean;
import com.suntrust.dotcom.beans.AndOrMultiBean;

/**
* This AndOrComponent is used to
* store multifield values and return
* to component HTML.
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
 */
public class AndOrComponent extends WCMUsePojo {
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AndOrComponent.class);
	/**	stores all iItems as string **/  
	private static final String I_ITEMS = "iItems";
	/**	gets and sets the item Value pairs **/  
	private AndOrMultiBean mBean = null;
	/**	returns the list of values in multifield **/ 
	private List<AndOrMultiBean> multiList = null;

	
	/**
	 * Method which initiates storing in JSON objects
	 */
	@Override
	public void activate() throws RepositoryException, JSONException {
		LOGGER.info("##### INVOKED ACTIVATE");
		multiList = new ArrayList<AndOrMultiBean>();
		Node currentNode = getResource().adaptTo(Node.class);
		LOGGER.info("currentNode::" + currentNode.getPath());
		String[] tabs = { "i" };
		for (int i = 0; i < tabs.length; i++) {
			String currentItem = tabs[i] + "Items";
			if (currentNode.hasProperty(currentItem)) {
				LOGGER.info("##### ITEMS ARE BEING SET" + currentItem);
				LOGGER.info("##### AndOr");
				setItems(currentNode, currentItem);
				multiList.add(mBean);
			}
		}
		/*for (AndOrMultiBean andorMbean : multiList) {
			for (AndOrItemsBean andorIbean : andorMbean.getItems()) {
				LOGGER.info("##### andorIbean " + andorIbean.getAndOrHeading());
			}
		}*/
	}

	/**
	 * sets the multilist values in the arraylist
	 */
	private void setItems(Node currentNode, String tab) {
		/**	gets and sets value for each item **/  
		AndOrItemsBean iBean = null;
		/**	list that stores iBeans **/
		List<AndOrItemsBean> lBean = null;
		try {
			Value[] value;
			JSONObject jObj;
			Property currentProperty;
			mBean = new AndOrMultiBean();
			lBean = new ArrayList<AndOrItemsBean>();
			currentProperty = currentNode.getProperty(tab);
			LOGGER.info("currentNode "+currentNode.getPath());
			LOGGER.info("currentProperty "+currentProperty.isMultiple());
			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();
				LOGGER.info("coming insdie if "+ value);
			} else {
				value = new Value[1];
				value[0] = currentProperty.getValue();
				LOGGER.info("coming insdie else "+ value[0]);
			}
			int foralttext = 1;
			LOGGER.info("before for - value length"+ value.length);
			for (int i = 0; i < value.length; i++) {
				LOGGER.info("coming insdie for "+value[i].getString());
				jObj = new JSONObject(value[i].getString());
				iBean = new AndOrItemsBean();
				// String stringURL;
				LOGGER.info("tab value insdie for "+ tab);
				LOGGER.info("I_ITEMS value insdie for "+ I_ITEMS);
				if (tab.equalsIgnoreCase(I_ITEMS)) {
					LOGGER.info("equalsIgnoreCase Passed");
					iBean.setAndOrHeading(jObj.getString("heading"));
					LOGGER.info("And or waiver "+jObj.getString("heading"));
					iBean.setAndOrRequirement(jObj.getString("requirement"));
					iBean.setAndOrSubheading(jObj.getString("subheading"));
					iBean.setAndOrChoice(jObj.getString("choice"));
					/*
					 * stringURL = updateURL(jObj); LOGGER.debug("stringURL = "
					 * + stringURL); iBean.setdescription(stringURL.trim());
					 */
					foralttext = foralttext + 1;
				}

				lBean.add(iBean);

			}
			mBean.setItems(lBean);
			// LOGGER.info("#### MULTIFIELD COUNTER = "+ctr+" ####");

		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException " + e.getMessage(), e);
		} catch (IllegalStateException e) {
			LOGGER.error("IllegalStateException " + e.getMessage(), e);
		} catch (JSONException e) {
			LOGGER.error("JSONException " + e.getMessage(), e);
		}
	}

	

	/**
	 * @return the multilist containing the multifield values
	 */
	public List<AndOrMultiBean> getMBean() {
		return this.multiList;
	}
}