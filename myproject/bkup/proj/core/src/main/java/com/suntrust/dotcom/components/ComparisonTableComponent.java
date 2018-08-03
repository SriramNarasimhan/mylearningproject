package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.ComparisonTableItemsBean;
import com.suntrust.dotcom.beans.ComparisonTableMultiBean;
import com.suntrust.dotcom.utils.Utils;

/**
* This ComparisonTableComponent is used to fetch the each product details
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
 */

public class ComparisonTableComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComparisonTableComponent.class);
	/**	Multifield items productItems **/
	private static final String PRODUCT_ITEMS = "productItems";
	/**	Multifield items sectionItems1 **/
	private static final String SECTION1_ITEMS = "sectionItems1";
	/**	Multifield items sectionItems2 **/
	private static final String SECTION2_ITEMS = "sectionItems2";
	/**	Multifield items sectionItems3 **/
	private static final String SECTION3_ITEMS = "sectionItems3";
	/**	Multifield items sectionItems4 **/
	private static final String SECTION4_ITEMS = "sectionItems4";
	/**	Multifield items sectionItems5 **/
	private static final String SECTION5_ITEMS = "sectionItems5";
	/**	Multifield items sectionItems6 **/
	private static final String SECTION6_ITEMS = "sectionItems6";

	/**	 Comparison table bean items * */
	private ComparisonTableMultiBean mBean = null;
	/**	List of Comparison Table items * */
	private List<ComparisonTableMultiBean> multiList = null;
	/**	Count of products in table * */
	/* default */int productItems=0;

	/* (non-Javadoc)
	 * @see com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	@Override
	/**
	 * @method overrides com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	public void activate(){

		LOGGER.info("##### INVOKED ACTIVATE");
		try {

			multiList = new ArrayList<ComparisonTableMultiBean>();
	
			Node currentNode = getResource().adaptTo(Node.class);
	
			String[] tabs = { PRODUCT_ITEMS, SECTION1_ITEMS ,SECTION2_ITEMS, SECTION3_ITEMS, SECTION4_ITEMS,SECTION5_ITEMS,SECTION6_ITEMS};
	
			for (int i = 0; i < tabs.length; i++) {
				String currentItem = tabs[i];
				
					if (currentNode.hasProperty(currentItem)) {
						LOGGER.info("##### ITEMS ARE BEING SET" + currentItem);
						setItems(currentNode, currentItem);
						multiList.add(mBean);
					}
					else
					{
						multiList.add(new ComparisonTableMultiBean());
					}
				
			}
			if (currentNode.hasProperty(PRODUCT_ITEMS)) {
				productItems=getProductCount(currentNode,PRODUCT_ITEMS);
			}
		} catch (PathNotFoundException pathNotFoundException) {
			LOGGER.error("ComparisonTableComponent : activate() :Exception, {}",pathNotFoundException.getMessage(),pathNotFoundException);
		} catch (ValueFormatException valueFormatException) {
			LOGGER.error("ComparisonTableComponent : activate() :Exception, {}",valueFormatException.getMessage(),valueFormatException);
		} catch (RepositoryException repositoryException) {
			LOGGER.error("ComparisonTableComponent : activate() :Exception, {}",repositoryException.getMessage(),repositoryException);
		} 
		 
	}

	/**
	 * @param currentNode
	 * @param item
	*/
	private void setItems(Node currentNode, String item) {
			try{
				Value[] value;
	
				JSONObject jObj;
	
				Property currentProperty;
	
				mBean = new ComparisonTableMultiBean();
				List<ComparisonTableItemsBean> sectionBean=null;
				sectionBean = new ArrayList<ComparisonTableItemsBean>();
				ComparisonTableItemsBean itemBean = null;
	
				currentProperty = currentNode.getProperty(item);
	
				if (currentProperty.isMultiple()) {
	
					value = currentProperty.getValues();
	
				} else {
	
					value = new Value[1];
	
					value[0] = currentProperty.getValue();
	
				}
	
				for (int i = 0; i < value.length; i++) {
	
					jObj = new JSONObject(value[i].getString());
					itemBean = new ComparisonTableItemsBean();
					String stringCtaURL;
					String stringLinkURL;
	
					if (item.equalsIgnoreCase(PRODUCT_ITEMS)) {
						LOGGER.info("item =" + item);
						itemBean.setItemheading(jObj.getString("itemheading"));
						itemBean.setItemdescription(jObj.getString("itemdescription"));
	
						itemBean.setCtatitle(jObj.getString("ctatitle"));
						stringCtaURL = Utils.getModifyURL(jObj.getString("ctalinkurl").trim(),jObj.getString("ctaurlParams").trim(),"");
						LOGGER.debug("stringctaURL = " + stringCtaURL);
						itemBean.setCtalinkurl(stringCtaURL.trim());
						itemBean.setCtatitletag(jObj.getString("ctatitletag"));
						itemBean.setCtatarget(jObj.getString("ctatarget"));
						itemBean.setCtatheme(jObj.getString("ctatheme"));
	
						itemBean.setLinktitle(jObj.getString("linktitle"));
						stringLinkURL = Utils.getModifyURL(jObj.getString("linkurl").trim(),jObj.getString("urlParams").trim(),"");
						LOGGER.debug("stringLinkURL = " + stringLinkURL);
						itemBean.setLinkurl(stringLinkURL.trim());
						itemBean.setTitletag(jObj.getString("titletag"));
						itemBean.setTarget(jObj.getString("target"));
	
						LOGGER.info("tab after= " + item);
	
					} else{
						LOGGER.info("item = " + item);
						itemBean.setRowquestion(jObj.getString("rowquestion"));
						itemBean.setRowanswer1(jObj.getString("rowanswer1"));
						itemBean.setRowanswer2(jObj.getString("rowanswer2"));
						itemBean.setRowanswer3(jObj.getString("rowanswer3"));
						itemBean.setRowanswer4(jObj.getString("rowanswer4"));
						itemBean.setRowanswer5(jObj.getString("rowanswer5"));
						itemBean.setRowanswer6(jObj.getString("rowanswer6"));
						itemBean.setRowanswer7(jObj.getString("rowanswer7"));
						itemBean.setRowanswer8(jObj.getString("rowanswer8"));
					}
	
					sectionBean.add(itemBean);
				}
				mBean.setItems(sectionBean);
			} catch (PathNotFoundException pathNotFoundException) {
				LOGGER.error("ComparisonTableComponent : setItems() :pathNotFoundException, {}",pathNotFoundException.getMessage(),pathNotFoundException);
			} catch (RepositoryException repositoryException) {
				LOGGER.error("ComparisonTableComponent : setItems() :repositoryException, {}",repositoryException.getMessage(),repositoryException);
			} catch (IllegalStateException illegalStateException) {
				LOGGER.error("ComparisonTableComponent : setItems() :illegalstate, {}",illegalStateException.getMessage(),illegalStateException);
			} catch (JSONException jSONException) {
				LOGGER.error("ComparisonTableComponent : setItems() :jsonException, {}",jSONException.getMessage(),jSONException);
			}
	}


	/**
	 * @return List<ComparisonTableMultiBean>
	 */
	public List<ComparisonTableMultiBean> getMBean() {

		return this.multiList;

	}

	/**
	 * @param currentNode
	 * @param item
	 * @return int product items
	 * @throws PathNotFoundException
	 * @throws RepositoryException
	 * @throws ValueFormatException
	 */
	public int getProductCount(Node currentNode, String item) throws PathNotFoundException, RepositoryException, ValueFormatException{

		Value[] value;
		Property currentProperty;
		currentProperty = currentNode.getProperty(item);
		if (currentProperty.isMultiple()) {

			value = currentProperty.getValues();

		} else {

			value = new Value[1];

			value[0] = currentProperty.getValue();

		}
		productItems=0;

		for (int i = 0; i < value.length; i++) {
			if (item.equalsIgnoreCase(PRODUCT_ITEMS)) {
				productItems=productItems + 1;
			}
		}
		LOGGER.info("productItems::" + productItems);
		return productItems;

	}

	/**
	 * @return int product items
	 */
	public int getTableProductCount() {

		return this.productItems;

	}

}