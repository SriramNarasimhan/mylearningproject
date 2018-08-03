package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.AnimatedHeroItemsBean;
import com.suntrust.dotcom.beans.AnimatedHeroMultiBean;

/**
* This AnimatedHeroComponent is used to fetch the Animated hero images details
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
 */

public class AnimatedHeroComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AnimatedHeroComponent.class);
	
	
	/**	Multifield items slide1 **/
	private static final String SLIDE1_ITEMS = "slide1";
	/**	Multifield items slide2 **/
	private static final String SLIDE2_ITEMS = "slide2";
	/**	Multifield items slide3 **/
	private static final String SLIDE3_ITEMS = "slide3";
	/**	Multifield items slide4 **/
	private static final String SLIDE4_ITEMS = "slide4";
	/**	Multifield items slide5 **/
	private static final String SLIDE5_ITEMS = "slide5";
	/**	Multifield items slide6 **/
	private static final String SLIDE6_ITEMS = "slide6";
	/**	Multifield items slide7 **/
	private static final String SLIDE7_ITEMS = "slide7";
	/**	Multifield items slide8 **/
	private static final String SLIDE8_ITEMS = "slide8";


	/**	 animated hero items * */
	private AnimatedHeroMultiBean mBean = null;			
	/**	List of animated hero items * */
	private List<AnimatedHeroMultiBean> multiList = null;
	/**	Count of images in each slide * */
	/* default */int imageCount=0;
	/**	Index of images in each slide * */
	/* default */int imageIndex=0;
	

	/* (non-Javadoc)
	 * @see com.adobe.cq.sightly.WCMUsePojo#activate()
	 */	
	@Override
	/**
	 * @method overrides com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	public void activate() {

		LOGGER.info("##### INVOKED ACTIVATE");

		multiList = new ArrayList<AnimatedHeroMultiBean>();		

		Node currentNode = getResource().adaptTo(Node.class);

		String[] tabs = { SLIDE1_ITEMS, SLIDE2_ITEMS ,SLIDE3_ITEMS, SLIDE4_ITEMS, SLIDE5_ITEMS,SLIDE6_ITEMS,SLIDE7_ITEMS,SLIDE8_ITEMS};
		imageCount=0;
		imageIndex=0;
		
		for (int i = 0; i < tabs.length; i++) {			
			String currentItem = tabs[i];	
			try {
				if (currentNode.hasProperty(currentItem)) {	
					LOGGER.info("##### ITEMS ARE BEING SET" + currentItem);
					setItems(currentNode, currentItem);
					multiList.add(mBean);
				}
				else
				{	
					multiList.add(new AnimatedHeroMultiBean());
				}
			} catch (RepositoryException e) {
				LOGGER.error("AnimatedHeroComponent : activate() :Exception, {}",e.getMessage(), e);
			}
		}
		
	}

	/**
	 * @param currentNode
	 * @param item
	*/
	private void setItems(Node currentNode, String item) {
		try {
			Value[] value;

			JSONObject jObj;

			Property currentProperty;
			
			AnimatedHeroItemsBean itemBean = null;
			List<AnimatedHeroItemsBean> slidesBean=null;
			
			mBean = new AnimatedHeroMultiBean();

			slidesBean = new ArrayList<AnimatedHeroItemsBean>();

			
				currentProperty = currentNode.getProperty(item);
			

			if (currentProperty.isMultiple()) {

				value = currentProperty.getValues();

			} else {

				value = new Value[1];

				value[0] = currentProperty.getValue();

			}			
			
			for (int i = 0; i < value.length; i++) {

				jObj = new JSONObject(value[i].getString());
				itemBean = new AnimatedHeroItemsBean();
				if(item.equals(SLIDE1_ITEMS)&& i==0){
					imageCount=0;
					imageIndex=0;					
				}
				
				imageCount++;	
				
					LOGGER.info("item =" + item);
					itemBean.setDisplaycta(jObj.getString("displaycta"));
					itemBean.setMobilecarouselimage(jObj.getString("mobilecarouselimage"));
					itemBean.setMobileimagealttext(jObj.getString("mobileimagealttext"));
					itemBean.setMobileslidename(jObj.getString("mobileslidename"));
					itemBean.setImageCount(imageCount);	
					itemBean.setImageIndex(imageIndex);
					imageIndex++;

				slidesBean.add(itemBean);
			}
			mBean.setItems(slidesBean);
			
		} catch (PathNotFoundException pathNotFoundException) {
			LOGGER.error("AnimatedHeroComponent : setItems() :pathNotFoundException, {}",pathNotFoundException.getMessage(),pathNotFoundException);
		} catch (RepositoryException repositoryException) {
			LOGGER.error("AnimatedHeroComponent : setItems() :repositoryException, {}",repositoryException.getMessage(),repositoryException);
		} catch (IllegalStateException illegalStateException) {
			LOGGER.error( "AnimatedHeroComponent : setItems() :illegalStateException, {}",illegalStateException.getMessage(),illegalStateException);
		} catch (JSONException jSONException) {
			LOGGER.error("AnimatedHeroComponent : setItems() :JsonException, {}",jSONException.getMessage(),jSONException);
		}

	}
	
	
	/**
	 * @return AnimatedHeroMultiBean list
	 */
	public List<AnimatedHeroMultiBean> getMBean() {
		return this.multiList;
	}
	
	

}