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
import com.suntrust.dotcom.beans.SearchBean;
import com.suntrust.dotcom.beans.SearchFormMultiBean;

/**
 * Class returns the Search form authored multifield values for passing as
 * hidden parameters.
 * 
 * @author Cognizant
 * @version 1.0
 * @since 05 September 2017
 * 
 */

public class SearchFormComponent extends WCMUsePojo {
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SearchFormComponent.class);
	/** Multifield constant item. */
	private static final String I_ITEMS = "iItems";
	/** Multifield bean. */
	private SearchFormMultiBean mBean = null;
	/** Multifield search bean list collection. */
	private List<SearchFormMultiBean> multiList = null;

	/**
	 * Gets called on bundle activate.
	 * 
	 * @throws RepositoryException
	 */
	@Override
	public final void activate() throws RepositoryException {
		multiList = new ArrayList<SearchFormMultiBean>();
		Node currentNode = getResource().adaptTo(Node.class);
		String[] tabs = {"i"};
		for (int i = 0; i < tabs.length; i++) {
			String currentItem = tabs[i] + "Items";
			if (currentNode.hasProperty(currentItem)) {
				setItems(currentNode, currentItem);
				multiList.add(mBean);
			}
		}
	}

	/**
	 * @param currentNode
	 * @param tab
	 */
	@SuppressWarnings("deprecation")
	private void setItems(Node currentNode, String tab) {
		try {
			Value[] value;
			JSONObject jObj;
			Property currentProperty;
			mBean = new SearchFormMultiBean();
			List<SearchBean> lBean = new ArrayList<SearchBean>();
			currentProperty = currentNode.getProperty(tab);
			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();
			} else {
				value = new Value[1];
				value[0] = currentProperty.getValue();
			}
			for (int i = 0; i < value.length; i++) {
				jObj = new JSONObject(value[i].getString());
				SearchBean iBean = new SearchBean();
				if (tab.equalsIgnoreCase(I_ITEMS)) {
					iBean.setHiddenFieldName(jObj.getString("hiddenFieldName"));
					iBean.setHiddenFieldValue(jObj
							.getString("hiddenFieldValue"));
				}

				lBean.add(iBean);
			}
			mBean.setItems(lBean);

		} catch (JSONException | RepositoryException | IllegalStateException e) {
			LOGGER.error("Exception during setItems" + e.getMessage());
		}
	}

	/**
	 * Returns multifields.
	 */
	public final List<SearchFormMultiBean> getMBean() {
		return this.multiList;
	}
}