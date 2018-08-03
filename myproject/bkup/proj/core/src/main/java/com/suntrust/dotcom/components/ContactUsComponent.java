package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.felix.scr.annotations.Reference;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.ContactUsBean;
import com.suntrust.dotcom.beans.ContactUsMultiBean;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.GenericEnum;
import com.suntrust.dotcom.utils.Utils;

/**
 * ContactUsComponent class is to fetch the values for the Contact Us component
 *
 * @author Cognizant
 * @version 1.0
 * @since 26 May 2017
 *
 */
public class ContactUsComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ContactUsComponent.class);
	/**
	 * This is used to retrieve mBean object
	 */
	private ContactUsMultiBean mBean = null;	
	/**
	 * This is used to retrieve contactus each tab values
	 */
	private List<ContactUsMultiBean> multiList = null;

	/**
	 * This is used to retrieve suntrust DotcomService
	 */
	@Reference
	private SuntrustDotcomService dotcomServiceconfig;

	/**
	 * Gets called on bundle activate.
	 * 
	 * @throws RepositoryException
	 */
	@Override
	public void activate() {
		LOGGER.info("##### INVOKED the ContactUsComponent ACTIVATE");
		multiList = new ArrayList<ContactUsMultiBean>();
		Node currentNode = getResource().adaptTo(Node.class);
		String[] tabs = { "i","a"};

		for (int i = 0; i < tabs.length; i++) {
			String currentItem = tabs[i] + "Items";
			try {
				if (currentNode.hasProperty(currentItem)) {
					LOGGER.info("##### ITEMS ARE BEING SET FOR ContactUsComponent: " + currentItem);
					setItems(currentNode, currentItem);
					multiList.add(mBean);
				}else{
					multiList.add(new ContactUsMultiBean());
				}
			} catch (RepositoryException | JSONException e) {
				// TODO Auto-generated catch block
				LOGGER.error("ContactUsComponent : activate() :Exception, {}",e.getMessage(), e);
			}
		}

	}
	
	/**
	 * @param currentNode
	 * @param tab
	 * @throws PathNotFoundException
	 * @throws RepositoryException
	 * @throws ValueFormatException
	 * @throws org.json.JSONException
	 */
	private void setItems(Node currentNode, String tab)
			throws PathNotFoundException, RepositoryException, ValueFormatException,
			org.json.JSONException {
		ContactUsBean iBean = null;
		List<ContactUsBean> lBean = null;

		try {
			Value[] value = null;

			JSONObject jObj = null;
			Property currentProperty = currentNode.getProperty(tab);
			mBean = new ContactUsMultiBean();
			lBean = new ArrayList<ContactUsBean>();

			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();
			} else {
				value = new Value[1];
				value[0] = currentProperty.getValue();
			}

			for (int i = 0; i < value.length; i++) {

				jObj = new JSONObject(value[i].getString());
				iBean = new ContactUsBean();
				String stringURL = "";
				iBean.setIcon(jObj.getString("icon"));
				iBean.setIconAltText(jObj.getString("iconalttext"));
				iBean.setPhoneNumber(jObj.getString("phone"));
				iBean.setMiniDesc(jObj.getString("minidesc"));
				iBean.setSubDesc(jObj.getString("subdesc"));
				String url = jObj.getString("linkURL");
				url = (url == null)?GenericEnum.EMPTY_STRING.getValue():url.trim();
				String anchorTag  = jObj.getString("titleTag");
				anchorTag = (anchorTag == null)?GenericEnum.EMPTY_STRING.getValue():anchorTag.trim();
				String urlParams  = jObj.getString("urlParams");
				urlParams = (urlParams == null)?GenericEnum.EMPTY_STRING.getValue():urlParams.trim();
				stringURL = Utils.getModifyURL(url,urlParams,anchorTag);
				stringURL = stringURL.trim();
				iBean.setLinkURL(stringURL);

				iBean.setTarget(jObj.getString("target"));
				iBean.setUrlParams(jObj.getString("urlParams"));
				iBean.setTitleTag(jObj.getString("titleTag"));
				Boolean boolean1 = false;
				if(jObj.has("loDataPersistCheckbox") && !jObj.isNull("loDataPersistCheckbox")){  
					boolean1 = jObj.getString("loDataPersistCheckbox").equalsIgnoreCase("true") ? true : false;
				}							    												
				iBean.setLoDataPersistCheckbox(boolean1);

				LOGGER.info("linkURL value ="+stringURL);
				lBean.add(iBean);
			}

			mBean.setItems(lBean);

		} catch (Exception e) {
			LOGGER.error("ContactUsComponent : setItems() : Exception, {}",e.getMessage(), e);
		}

	}

	/**
	 * @return multiList
	 */
	public List<ContactUsMultiBean> getMBean() {
		return this.multiList;
	}

}