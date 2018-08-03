package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONObject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.DialogMultiBean;
import com.suntrust.dotcom.beans.RelatedLinksBean;
import com.suntrust.dotcom.config.AdvisorConfigService;
import com.suntrust.dotcom.utils.Utils;

public class RelatedLinksComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(RelatedLinksComponent.class);

	/** Dialog tab name */
	private static final String I_ITEMS = "adv_iItems";

	/** MultiBean class reference variable*/
	private DialogMultiBean<RelatedLinksBean> mBean = null;

	/** RelatedLinksBean class reference variable for tab*/
	private RelatedLinksBean iBean = null;

	/** RelatedLinksBean class reference variable for tab*/
	private List<RelatedLinksBean> lBean = null;

	/** Bean class reference variable*/
	private List<DialogMultiBean<RelatedLinksBean>> multiList = null;

	@Reference
	AdvisorConfigService configService;

	@Override
	public void activate() throws Exception {
		LOGGER.info("INVOKED RELATED LINKS ACTIVATE METHOD");
		multiList = new ArrayList<DialogMultiBean<RelatedLinksBean>>();
		Node currentNode = getResource().adaptTo(Node.class);
		configService = getSlingScriptHelper().getService(AdvisorConfigService.class);
		String[] tabs = {"adv_i"};
		for (int i = 0; i < tabs.length; i++) {
			String currentItem = tabs[i] + "Items";
			if (currentNode.hasProperty(currentItem)) {
				LOGGER.debug("##### ITEMS ARE BEING SET" + currentItem);
				setItems(currentNode, currentItem);
				multiList.add(mBean);
			}
			//this loop will execute for profiles that dont have common links in db
			else
			{
				Node jcrNode= getCurrentPage().getContentResource().adaptTo(Node.class);
				if(jcrNode != null && jcrNode.hasProperty("adv_specialty"))
				{
					String speciality = jcrNode.getProperty("adv_specialty").getString();
					if(StringUtils.isNotBlank(speciality))
					{
						String specialityStr = speciality.split(":")[1];
						List<String> relatedLinks= configService.getPropertyArray(specialityStr+".relatedlinks");
						if(null != relatedLinks && relatedLinks.size() > 0)
						{
							lBean = new ArrayList<RelatedLinksBean>();
							mBean = new DialogMultiBean<RelatedLinksBean>();
							setRelatedLinks(lBean);
							mBean.setItems(lBean);
							multiList.add(mBean);
						}
					}
				}
			}
		}
	}

	/**
	 * Creates data bean for dialog multifield
	 *
	 * @param currentNode
	 * @param tab
	 * @throws PathNotFoundException
	 * @throws RepositoryException
	 * @throws ValueFormatException
	 * @throws JSONException
	 */
	private void setItems(Node currentNode, String tab) throws PathNotFoundException, RepositoryException, ValueFormatException, JSONException {

		try {
			Value[] value;
			JSONObject jObj;
			Property currentProperty;
			mBean = new DialogMultiBean<RelatedLinksBean>();
			lBean = new ArrayList<RelatedLinksBean>();
			currentProperty = currentNode.getProperty(tab);

			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();
			} else {

				value = new Value[1];
				value[0] = currentProperty.getValue();
			}
			//set the common links
			setRelatedLinks(lBean);
			for (int i = 0; i < value.length; i++) {

				jObj = new JSONObject(value[i].getString());
				iBean = new RelatedLinksBean();
				String stringURL;
				if (tab.equalsIgnoreCase(I_ITEMS)) {
					iBean.setTitle(jObj.getString("page"));
					stringURL = updateURL(jObj);
					LOGGER.debug("stringURL = " + stringURL);
					iBean.setLinkURL(stringURL.trim());
					iBean.setTarget(jObj.getString("target"));
				}

				lBean.add(iBean);

			}

			mBean.setItems(lBean);

		} catch (Exception e) {
			LOGGER.error("Exception in related links component:", e);
		}
	}

	/**
	 * set the related links based on speciality
	 *
	 * @param lBean
	 * @throws org.apache.sling.commons.json.JSONException
	 */
	private void setRelatedLinks(List<RelatedLinksBean> lBean) throws org.apache.sling.commons.json.JSONException {
		//check if related links is present
		String speciality = getCurrentPage().getProperties().get("adv_specialty").toString();
		speciality = speciality.substring(speciality.indexOf(":")+1);
		List<String> relatedlinks = configService.getPropertyArray(speciality + ".relatedlinks");
		if (relatedlinks != null && relatedlinks.size() != 0) {
			Iterator<String> itr = relatedlinks.iterator();
			RelatedLinksBean iBean = null;
			while (itr.hasNext()) {
				String itemStr = itr.next();
				iBean = new RelatedLinksBean();
				String[] itemList = itemStr.split("\\|");
				iBean.setTitle(itemList[1]);
				String linkUrl =itemList[0];
				String legalIdentifier = itemList[2];
				String linkParamName = itemList[3];
				String urlparams = "";
				String appString = getCurrentPage().getProperties().get("adv_appstring").toString();
				if ("legacyidentifier".equalsIgnoreCase(legalIdentifier)) {
					linkUrl = linkUrl+"?" + appString;
					if (!"null".equalsIgnoreCase(linkParamName)) {
						linkUrl = linkUrl+"?" + linkParamName + "=" + appString;
					}
				}
				iBean.setLinkURL(linkUrl);
				iBean.setTarget(itemList[4]);
				lBean.add(iBean);
			}
		}
	}

	/**
	 * Get updated URL combined with param and anchortag
	 *
	 * @param jObj
	 * @return
	 * @throws org.apache.sling.commons.json.JSONException
	 */
	private String updateURL(JSONObject jObj) throws org.apache.sling.commons.json.JSONException
	{
		String url = jObj.getString("linkURL").trim();
		String urlParams = jObj.getString("urlParams").trim();
		String anchorTag = jObj.getString("anchorTag").trim();

		String updatedUrl = Utils.getModifyURL(url , urlParams , anchorTag);

		return updatedUrl;

	}

	/**
	 * Returns bean
	 *
	 * @return
	 */
	public List<DialogMultiBean<RelatedLinksBean>> getMBean() {
		return this.multiList;
	}

}