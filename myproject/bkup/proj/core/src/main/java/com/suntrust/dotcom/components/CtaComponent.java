package com.suntrust.dotcom.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.CTAItemsBean;
import com.suntrust.dotcom.beans.DialogMultiBean;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import com.suntrust.dotcom.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.jcr.Value;
import javax.jcr.Property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Purpose - This code performs business logic in CTA Component.
 * @author UGRK104
 *
 */
public class CtaComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CtaComponent.class);	
	
	/**	Constant variable * */
	private static final String I_ITEMS = "iItems";
	
	/**	instance variable * */
	private DialogMultiBean<CTAItemsBean> mBean = null;
	
	/**	instance variable * */
	private CTAItemsBean iBean = null;
	
	/**	instance variable * */
	private List<CTAItemsBean> lBean = null;
	
	/**	instance variable * */
	private List<DialogMultiBean<CTAItemsBean>> multiList = null;

	/* (non-Javadoc)
	 * @see com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	@Override
	public void activate()
	{

		LOGGER.info("INVOKED CTACOMPONENT ACTIVATE METHOD");
		multiList = new ArrayList<DialogMultiBean<CTAItemsBean>>();
		Node currentNode = getResource().adaptTo(Node.class);
		String[] tabs = { "i"};

		for (int i = 0; i < tabs.length; i++) {

			String currentItem = tabs[i] + "Items";

			try{
				if (currentNode.hasProperty(currentItem)) {
					LOGGER.info("##### ITEMS ARE BEING SET" + currentItem);
					setItems(currentNode, currentItem);
					multiList.add(mBean);
				}
			}
			catch (PathNotFoundException pathNotFoundException) {
				LOGGER.error(pathNotFoundException.getMessage(),pathNotFoundException);
			} catch (RepositoryException repositoryException) {
				LOGGER.error(repositoryException.getMessage(),repositoryException);
			} catch (JSONException jsonException) {			
				LOGGER.error(jsonException.getMessage(),jsonException);
			} catch (Exception exception) {			
				LOGGER.error(exception.getMessage(),exception);
			}		

		}

	}

	/**
	 * @param currentNode {@link Node}
	 * @param tab {@link String}
	 * @throws PathNotFoundException {@link PathNotFoundException}
	 * @throws RepositoryException {@link RepositoryException}
	 * @throws ValueFormatException {@link ValueFormatException}
	 * @throws JSONException {@link JSONException}
	 */
	private void setItems(Node currentNode, String tab)	throws PathNotFoundException, RepositoryException, ValueFormatException, JSONException
	{
		
		Value[] value;
		JSONObject jObj;
		Property currentProperty;

		mBean = new DialogMultiBean<CTAItemsBean>();
		lBean = new ArrayList<CTAItemsBean>();
		currentProperty = currentNode.getProperty(tab);

		if (currentProperty.isMultiple()) {
			value = currentProperty.getValues();
		} else {
			value = new Value[1];
			value[0] = currentProperty.getValue();
		}

		for (int i = 0; i < value.length; i++) {

			jObj = new JSONObject(value[i].getString());
			iBean = new CTAItemsBean();				
			String stringURL;
			
			if (tab.equalsIgnoreCase(I_ITEMS)) {
				iBean.setTitle(jObj.getString("jcr:title"));					
				stringURL = updateURL(jObj);
				
				LOGGER.debug("stringURL = " + stringURL);
				
				iBean.setLinkURL(stringURL.trim());
				iBean.setTarget(jObj.getString("target"));					
				iBean.setAllignment(jObj.getString("allignment"));
				if(jObj.has("anchortitle")) {
					iBean.setAnchorTitle(jObj.getString("anchortitle"));
				}
					
				// Start US19707
				if(jObj.has("loDataPersistCheckbox")) {
					Boolean boolean1 = (jObj.getString("loDataPersistCheckbox").equalsIgnoreCase("true")) ? true : false;
					iBean.setLoDataPersistCheckbox(boolean1);
				}
				// End US19707
					
			}
		lBean.add(iBean);
		}
		
		mBean.setItems(lBean);

	}
	

	
	/**
	 * @param jObj {@link JSONObject}
	 * @return modefied url
	 * @throws JSONException {@link JSONException}
	 */
	private String updateURL(JSONObject jObj) throws JSONException
	{
		String url = jObj.getString("linkURL").trim();
		String urlParams = jObj.getString("urlParams").trim();
		String anchorTag = jObj.getString("anchorTag").trim();	
		
		return Utils.getModifyURL(url , urlParams , anchorTag);
				
	}
	

	/**
	 * @return list of DialogMultiBean objects
	 */
	public List<DialogMultiBean<CTAItemsBean>> getMBean() {
		return this.multiList;
	}

}