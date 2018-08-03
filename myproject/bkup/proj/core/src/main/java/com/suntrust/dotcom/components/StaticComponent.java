package com.suntrust.dotcom.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.suntrust.dotcom.beans.StaticItemsBean;
import com.suntrust.dotcom.beans.StaticMultiBean;
import com.suntrust.dotcom.utils.Utils;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;







import org.apache.commons.lang.StringUtils;
import org.apache.sling.commons.json.JSONObject;

import java.util.ArrayList;

import javax.jcr.Value;
import javax.jcr.Property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maintains the links in static list
 * @author Cognizant
 * @version 1.0
 *
 */
public class StaticComponent extends WCMUsePojo {
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(StaticComponent.class);
	/** Multifield bean. */
	private StaticMultiBean mBean = null;
	/** StaticMultiBean list object. */
	private List<StaticMultiBean> multiList = null;

	/**
	 * Method which initiates storing in JSON objects
	 * 
	 */
	@Override
	public void activate() {
		LOGGER.info("##### INVOKED ACTIVATE");
		multiList = new ArrayList<StaticMultiBean>();
		Node currentNode = getResource().adaptTo(Node.class);
		String[] tabs = {"i","u","uk"};
		for (int i = 0; i < tabs.length; i++) {
			String currentItem = tabs[i]+"Items";
			try {
				if(currentNode.hasProperty(currentItem)){
					LOGGER.info("##### ITEMS ARE BEING SET" + currentItem );
					setItems(currentNode, currentItem);
					if(currentNode.hasProperty(tabs[i]+"Dashboard")){
						mBean.setDashboard(currentNode.getProperty(tabs[i]+"Dashboard").getString());
					}
					multiList.add(mBean);
				}
			}
			catch (PathNotFoundException e) {
				LOGGER.error("PathNotFoundException " + e.getMessage());
			}
			catch (ValueFormatException e) {
				LOGGER.error("ValueFormatException " + e.getMessage());
			}
			catch (RepositoryException e) {
				LOGGER.error("RepositoryException " + e.getMessage());
			}
		}
	}

	/**
	 * Method to set one list of items from multifield
	 */
	private void setItems(Node currentNode, String tab) {
		/** StaticItemsBean class object. */
		StaticItemsBean iBean = null;
		/** StaticItemsBean list object. */
		List<StaticItemsBean> lBean = null;
		
		try {
			Value[] value;
			JSONObject jObj;
			Property currentProperty;
			mBean = new StaticMultiBean();
			lBean = new ArrayList<StaticItemsBean>();
			currentProperty = currentNode.getProperty(tab);
			if(currentProperty.isMultiple()) {
				value = currentProperty.getValues();
			}
			else {
				value = new Value[1];
				value[0] = currentProperty.getValue();
			}

			for (int i = 0; i < value.length; i++) {
				jObj = new JSONObject(value[i].getString());
				iBean = new StaticItemsBean();
				if(StringUtils.isNotBlank(jObj.getString("page"))) {
					iBean.setPage(jObj.getString("page"));
				}
				
				else {
					Page curPage = getPageManager().getPage(jObj.getString("path"));
					String pageTitle = null;
					if(curPage == null) {
						continue;
					}
					else {
						pageTitle = curPage.getTitle();
					}
					iBean.setPage(pageTitle);
				}
				
				iBean.setPath(Utils.getModifyURL(jObj.getString("path"),jObj.getString("urlParams"),jObj.getString("anchorTag")));
				iBean.setTarget(jObj.getString("target"));
				iBean.setTitletag(jObj.getString("titletag"));
				lBean.add(iBean);
			}
			mBean.setItems(lBean);
		}
		catch (PathNotFoundException e) {
			LOGGER.error("PathNotFoundException " + e.getMessage());
		}
		catch (RepositoryException e) {
			LOGGER.error("RepositoryException " + e.getMessage());
		}
		catch (IllegalStateException e) {
			LOGGER.error("IllegalStateException " + e.getMessage());
		}
		catch (org.apache.sling.commons.json.JSONException e) {
			LOGGER.error("JSONException " + e.getMessage());
		}
	}

	/**
	 * Method to return the set list of items
	 * @return
	 */
	public List<StaticMultiBean> getMBean() {
		return this.multiList;
	}
}