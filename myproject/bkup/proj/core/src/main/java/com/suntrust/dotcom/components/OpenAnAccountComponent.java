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

import com.suntrust.dotcom.utils.Utils;
import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.OpenAnAccountItemsBean;
import com.suntrust.dotcom.beans.OpenAnAccountMultiBean;

/**
* This OpenAnAccountComponent is used to fetch the product details
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
 */
public class OpenAnAccountComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenAnAccountComponent.class);
	/**	Multifield product items **/
	private static final String PRODUCT_ITEMS = "productitems";
	/**	Multifield navigation items **/
	private static final String NAVIGATION_ITEMS = "navigationitems";
	
	/**	 open an account bean items * */
	private OpenAnAccountMultiBean mBean = null;
	/**	List of OpenAnAccount items * */
	private List<OpenAnAccountMultiBean> multiList = null;
	/**	Count of navigation items * */
	/* default */int navigationItems=0;

	/* (non-Javadoc)
	 * @see com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	@Override
	/**
	 * @method overrides com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	public void activate() {

		LOGGER.info("##### INVOKED ACTIVATE");
		try{
			multiList = new ArrayList<OpenAnAccountMultiBean>();
	
			Node currentNode = getResource().adaptTo(Node.class);
	
			String[] tabs = {PRODUCT_ITEMS,NAVIGATION_ITEMS};
	
			for (int i = 0; i < tabs.length; i++) {
				String currentItem = tabs[i];
				if (currentNode.hasProperty(currentItem)) {
					//LOGGER.info("##### ITEMS ARE BEING SET" + currentItem);					
					setItems(currentNode, currentItem);					
					multiList.add(mBean);
				}
				else
				{
					multiList.add(new OpenAnAccountMultiBean());
				}
			}
		} catch (PathNotFoundException pathNotFoundException) {
			LOGGER.error("OpenAnAccountComponent : activate() :pathNotFoundException, {}",pathNotFoundException.getMessage(),pathNotFoundException);
		} catch (ValueFormatException valueFormatException) {
			LOGGER.error("OpenAnAccountComponent : activate() :valueFormatException, {}",valueFormatException.getMessage(),valueFormatException);
		} catch (RepositoryException repositoryException) {
			LOGGER.error("OpenAnAccountComponent : activate() :repositoryException, {}",repositoryException.getMessage(),repositoryException);
		} catch (IllegalStateException e) {
			LOGGER.error("OpenAnAccountComponent : activate() :illegalStateException, {}",e.getMessage(),e);
		} catch (JSONException e) {			
			LOGGER.error("OpenAnAccountComponent : activate() :JsonException, {}",e.getMessage(),e);
		}
		
	}

	/**
	 * @param currentNode
	 * @param item	
	 * @throws RepositoryException 
	 * @throws PathNotFoundException 
	 * @throws JSONException 
	 * @throws IllegalStateException 
	 */
	private void setItems(Node currentNode, String item) throws PathNotFoundException, RepositoryException, IllegalStateException, JSONException{
		
			Value[] value;

			JSONObject jObj;

			Property currentProperty;

			mBean = new OpenAnAccountMultiBean();
			OpenAnAccountItemsBean itemBean = null;
			List<OpenAnAccountItemsBean> sectionBean=null;

			sectionBean = new ArrayList<OpenAnAccountItemsBean>();

			currentProperty = currentNode.getProperty(item);

			if (currentProperty.isMultiple()) {

				value = currentProperty.getValues();

			} else {

				value = new Value[1];

				value[0] = currentProperty.getValue();

			}
			navigationItems=0;
			for (int i = 0; i < value.length; i++) {

				jObj = new JSONObject(value[i].getString());
				itemBean = new OpenAnAccountItemsBean();
				String stringCtaURL;
				String stringLinkURL;
				String stringNavigationURL;

				if (item.equalsIgnoreCase(PRODUCT_ITEMS)) {
					//LOGGER.info("item =" + item);					
					itemBean.setSplcallout(jObj.getString("splcallout"));
					//LOGGER.info("splcallout =" + jObj.getString("splcallout"));
					itemBean.setBodercolor(jObj.getString("bodercolor"));
					itemBean.setProductlinktext(jObj.getString("productlinktext"));
					itemBean.setProductdescription(jObj.getString("productdescription"));
					itemBean.setAdditionalproductlinktext(jObj.getString("additionalproductlinktext"));
					stringLinkURL = Utils.getModifyURL(jObj.getString("producturl").trim(),jObj.getString("producturlParams").trim(),jObj.getString("productanchorTag").trim());
					//LOGGER.debug("stringLinkURL = " + stringLinkURL);
					itemBean.setProductfullurl(stringLinkURL.trim());
					itemBean.setProducturl(jObj.getString("producturl").trim());
					itemBean.setProducttitletag(jObj.getString("producttitletag"));
					itemBean.setProducttarget(jObj.getString("producttarget"));
					itemBean.setCtatext(jObj.getString("ctatext"));
					stringCtaURL = Utils.getModifyURL(jObj.getString("ctalinkurl").trim(),jObj.getString("ctaurlParams").trim(),jObj.getString("ctaanchorTag").trim());
					//LOGGER.debug("stringctaURL = " + stringCtaURL);
					itemBean.setCtalinkfullurl(stringCtaURL.trim());
					itemBean.setCtalinkurl(jObj.getString("ctalinkurl").trim());
					itemBean.setCtatitletag(jObj.getString("ctatitletag"));
					itemBean.setCtatarget(jObj.getString("ctatarget"));
					itemBean.setCtatheme(jObj.getString("ctatheme"));				

					//LOGGER.info("tab after= " + item);

				}else if(item.equalsIgnoreCase(NAVIGATION_ITEMS)){
					itemBean.setNavigationlinktext(jObj.getString("navigationlinktext"));					
					stringNavigationURL = Utils.getModifyURL(jObj.getString("navigationlinkurl").trim(),jObj.getString("navigationurlParams").trim(),jObj.getString("navigationanchorTag").trim());
					//LOGGER.debug("stringNavigationURL = " + stringNavigationURL);
					itemBean.setNavigationlinkfullurl(stringNavigationURL.trim());
					itemBean.setNavigationlinkurl(jObj.getString("navigationlinkurl").trim());
					itemBean.setNavigationtitletag(jObj.getString("navigationtitletag"));
					itemBean.setNavigationtarget(jObj.getString("navigationtarget"));
					itemBean.setNavigationanchor(jObj.getString("navigationanchorTag"));
					navigationItems++;
				}		
				else{
					//LOGGER.info("item = " + item);					
				}

				sectionBean.add(itemBean);
			}
			mBean.setItems(sectionBean);
		
	}

	/**
	 * @return List<OpenAnAccountMultiBean>
	 */
	public List<OpenAnAccountMultiBean> getMBean() {

		return this.multiList;

	}

	/**
	 * @return itemcount
	 */
	public int getNavigationItemCount() {

		return this.navigationItems;

	}
	
	/**
	 * @return NavigationItemLeftCount
	 */
	public int getNavigationItemLeftCount() {
		int leftCount=0;
		if(this.navigationItems<=8){
			leftCount=4;
		}else{
			if(this.navigationItems % 2 ==0){
				leftCount=this.navigationItems / 2;			
			}else{
				leftCount=this.navigationItems / 2+1;			
			}
		}
		return leftCount;
	}
	
	/**
	 * @return NavigationItemRightCount
	 */
	public int getNavigationItemRightCount() {		
		return this.navigationItems - getNavigationItemLeftCount();
	}
}